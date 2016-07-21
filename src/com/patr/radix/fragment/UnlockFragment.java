package com.patr.radix.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.xutils.common.util.LogUtil;

import com.felipecsl.gifimageview.library.GifImageView;
import com.patr.radix.LockValidateActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.MyKeysActivity;
import com.patr.radix.R;
import com.patr.radix.UnlockActivity;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.MDevice;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.GetLockListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.bll.ServiceManager.Url;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.Utils;
import com.patr.radix.view.GifView;
import com.patr.radix.view.ListSelectDialog;
import com.patr.radix.view.TitleBarView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class UnlockFragment extends Fragment implements OnClickListener,
        OnItemClickListener, SensorEventListener {

    private Context context;

    private TitleBarView titleBarView;

    private GifImageView gifView;

    private CommunityListAdapter adapter;

    SensorManager sensorManager = null;

    Vibrator vibrator = null;

    private Handler handler;

    private static BluetoothAdapter mBluetoothAdapter;

    private final List<MDevice> list = new ArrayList<MDevice>();

    private BluetoothLeScanner bleScanner;

    private boolean mScanning = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_unlock, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.unlock_titlebar);
        titleBarView.hideBackBtn().showSelectKeyBtn();
        titleBarView.setOnSelectKeyClickListener(this);
        gifView = (GifImageView) view.findViewById(R.id.unlock_giv);
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        byte[] bytes;
        try {
            bytes = Utils.input2byte(getResources().openRawResource(
                    R.raw.shake_shake));
            gifView.setBytes(bytes);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new CommunityListAdapter(context,
                MyApplication.instance.getCommunities());
        loadData();
        return view;
    }

    private void loadData() {
        // 若没有选小区，则获取小区列表，让用户选小区
        if (MyApplication.instance.getSelectedCommunity() == null) {
            getCommunityList();
            return;
        }
        // 若没有选钥匙，则获取钥匙列表
        if (MyApplication.instance.getSelectedLock() == null) {
            getLockList();
        }
    }

    private void getCommunityList() {
        switch (NetUtils.getConnectedType(context)) {
        case NONE:
            getCommunityListFromCache();
            break;
        case WIFI:
        case OTHER:
            getCommunityListFromServer();
            break;
        default:
            break;
        }
    }

    private void getCommunityListFromCache() {
        CacheManager.getCacheContent(context,
                CacheManager.getCommunityListUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            MyApplication.instance.setCommunities(result
                                    .getCommunities());
                            adapter.notifyDataSetChanged();
                            ListSelectDialog.show(context, "请选择小区", adapter,
                                    UnlockFragment.this);
                        }
                    }

                }, new GetCommunityListParser());
    }

    private void getCommunityListFromServer() {
        // 从服务器获取小区列表
        ServiceManager
                .getCommunityList(new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            if (result.isSuccesses()) {
                                MyApplication.instance.setCommunities(result
                                        .getCommunities());
                                saveCommunityListToDb(result.getResponse());
                                adapter.notifyDataSetChanged();
                                ListSelectDialog.show(context, "请选择小区",
                                        adapter, UnlockFragment.this);
                            } else {
                                ToastUtil.showShort(context,
                                        result.getRetinfo());
                                getCommunityListFromCache();
                            }
                        } else {
                            getCommunityListFromCache();
                        }
                    }

                    @Override
                    public void onFailure(Exception error, String content) {
                        getCommunityListFromCache();
                    }

                });
    }

    /**
     * 保存列表到数据库
     * 
     * @param content
     */
    protected void saveCommunityListToDb(String content) {
        CacheManager.saveCacheContent(context,
                CacheManager.getCommunityListUrl(), content,
                new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + CacheManager.getCommunityListUrl()
                                + "=" + result);
                    }
                });
    }

    private void getLockList() {
        if (MyApplication.instance.getLocks().size() == 0) {
            switch (NetUtils.getConnectedType(context)) {
            case NONE:
                getLockListFromCache();
                break;
            case WIFI:
            case OTHER:
                getLockListFromServer();
                break;
            default:
                break;
            }
        } else {
            titleBarView.setTitle(MyApplication.instance.getLocks().get(0)
                    .getName());
            MyApplication.instance.setSelectedLock(MyApplication.instance
                    .getLocks().get(0));
        }

    }

    private void getLockListFromCache() {
        if (MyApplication.instance.getSelectedCommunity() != null) {
            CacheManager.getCacheContent(context,
                    CacheManager.getLockListUrl(),
                    new RequestListener<GetLockListResult>() {

                        @Override
                        public void onSuccess(int stateCode,
                                GetLockListResult result) {
                            if (result != null) {
                                MyApplication.instance.setLocks(result
                                        .getLocks());
                                setTitle();
                            }
                        }

                    }, new GetLockListParser());
        }
    }

    private void getLockListFromServer() {
        // 从服务器获取门禁钥匙列表
        ServiceManager.getLockList(new RequestListener<GetLockListResult>() {

            @Override
            public void onSuccess(int stateCode, GetLockListResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        MyApplication.instance.setLocks(result.getLocks());
                        saveLockListToDb(result.getResponse());
                        setTitle();
                    } else {
                        ToastUtil.showShort(context, result.getRetinfo());
                        getLockListFromCache();
                    }
                } else {
                    getLockListFromCache();
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
                getLockListFromCache();
            }

        });
    }

    /**
     * 保存列表到数据库
     * 
     * @param content
     */
    protected void saveLockListToDb(String content) {
        CacheManager.saveCacheContent(context, CacheManager.getLockListUrl(),
                content, new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + CacheManager.getLockListUrl() + "="
                                + result);
                    }
                });
    }

    private void setTitle() {
        String selectedKey = PrefUtil.getString(context,
                Constants.PREF_SELECTED_KEY);
        for (RadixLock lock : MyApplication.instance.getLocks()) {
            if (selectedKey.equals(lock.getId())) {
                titleBarView.setTitle(lock.getName());
                MyApplication.instance.setSelectedLock(lock);
                return;
            }
        }
        // 若没有选择钥匙，则默认选第一个
        if (MyApplication.instance.getLocks().size() > 0) {
            titleBarView.setTitle(MyApplication.instance.getLocks().get(0)
                    .getName());
            MyApplication.instance.setSelectedLock(MyApplication.instance
                    .getLocks().get(0));
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onStart() {
        super.onStart();
        gifView.startAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        setTitle();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_select_key_btn:
            MyKeysActivity.start(context);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        MyApplication.instance.setSelectedCommunity(adapter.getItem(position));
        if (!adapter.isSelect(position)) {
            MyApplication.instance.setSelectedLock(null);
            getLockList();
        }
        adapter.select(position);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                    .abs(values[2]) > 17)) {
                LogUtil.d("============ values[0] = " + values[0]);
                LogUtil.d("============ values[1] = " + values[1]);
                LogUtil.d("============ values[2] = " + values[2]);
                // 摇动手机后，再伴随震动提示~~
                vibrator.vibrate(500);
                RadixLock lock = MyApplication.instance.getSelectedLock();
                if (lock != null) {
                    String lockPaternStr = PrefUtil.getString(context, Constants.PREF_LOCK_KEY, null);
                    if (!TextUtils.isEmpty(lockPaternStr)) {
                        // 如果有手势密码，则验证手势密码
                        LockValidateActivity.startForResult(this, Constants.LOCK_CHECK);
                    } else {
                        // 没有手势密码，则直接开锁
                        unlock();
                    }
                } else {
                    // 提示请先选择钥匙
                    ToastUtil.showLong(context, "请先选择钥匙！");
                }
            }

        }
    }
    
    private void unlock() {
        
    }

    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "不支持BLE", Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        getBluetoothAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(context, "不支持BLE", Toast.LENGTH_SHORT).show();
            return;
        }

        // 打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    private void getBluetoothAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * Call back for BLE Scan This call back is called when a BLE device is
     * found near by. 发现设备时回调
     */
    private LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                byte[] scanRecord) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    String name = device.getName();
                    if (name.length() < 5
                            || !device.getName().substring(0, 5)
                                    .equalsIgnoreCase("radix")) {
                        return;
                    }
                    if (list.contains(mDev))
                        return;
                    list.add(mDev);
                }
            });
        }
    };

    private void startScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            scanPrevious21Version();
        } else {
            scanAfter21Version();
        }
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        // 2.8秒后停止扫描
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
                mScanning = false;
            }
        }, 2800);

        if (mBluetoothAdapter == null) {
            getBluetoothAdapter();
        }
        if (mBluetoothAdapter != null) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    /**
     * 版本号21及之后的调用该方法扫描
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void scanAfter21Version() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleScanner != null) {
                    bleScanner.stopScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            super.onScanResult(callbackType, result);
    
                        }
                    });
                }
                mScanning = false;
            }
        }, 2800);

        if (bleScanner == null) {
            if (mBluetoothAdapter == null) {
                getBluetoothAdapter();
            }
            bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        if (bleScanner != null) {
            mScanning = true;
            bleScanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    MDevice mDev = new MDevice(result.getDevice(), result.getRssi());
                    String name = result.getDevice().getName();
                    if (name.length() < 5
                            || !name.substring(0, 5).equalsIgnoreCase("radix")) {
                        return;
                    }
                    if (list.contains(mDev))
                        return;
                    list.add(mDev);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOCK_CHECK) {
            if (resultCode == Constants.LOCK_CHECK_OK) {
                // 开门
                unlock();
            } else {
                // 验证不通过，不开门
                ToastUtil.showLong(context, "密码不正确！");
            }
        }
    }

}
