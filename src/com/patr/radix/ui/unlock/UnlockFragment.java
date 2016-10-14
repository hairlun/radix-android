package com.patr.radix.ui.unlock;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xutils.common.util.LogUtil;

import com.felipecsl.gifimageview.library.GifImageView;
import com.patr.radix.LockValidateActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.GetWeatherResult;
import com.patr.radix.bean.MDevice;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.ble.BluetoothLeService;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.GetLockListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.WeatherActivity;
import com.patr.radix.ui.view.ListSelectDialog;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.GattAttributes;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.Utils;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECInitParams.LoginMode;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UnlockFragment extends Fragment implements OnClickListener,
        OnItemClickListener, SensorEventListener {

    private Context context;

    private TextView areaTv;

    private ImageView weatherIv;

    private TextView weatherTv;

    private TextView tempTv;

    private ImageButton detailBtn;

    private ImageButton shakeBtn;

    private TextView keyTv;

    private Button sendKeyBtn;

    private LinearLayout keysLl;

    private CommunityListAdapter adapter;

    private LoadingDialog loadingDialog;

    SensorManager sensorManager = null;

    Vibrator vibrator = null;

    private Handler handler;

    private static BluetoothAdapter mBluetoothAdapter;

    private final List<MDevice> list = new ArrayList<MDevice>();

    private BluetoothLeScanner bleScanner;

    private boolean mScanning = false;

    private BluetoothGattCharacteristic notifyCharacteristic;

    private BluetoothGattCharacteristic writeCharacteristic;

    private boolean notifyEnable = false;

    private boolean handShake = false;

    private String currentDevAddress;

    private String currentDevName;

    private boolean isUnlocking = false;

    private boolean isDisconnectForUnlock = false;

    private boolean foundDevice = false;

    private int retryCount = 0;

    private static final int REQUEST_FINE_LOCATION = 0;

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
        areaTv = (TextView) view.findViewById(R.id.area_tv);
        weatherIv = (ImageView) view.findViewById(R.id.weather_iv);
        weatherTv = (TextView) view.findViewById(R.id.weather_tv);
        tempTv = (TextView) view.findViewById(R.id.temp_tv);
        detailBtn = (ImageButton) view.findViewById(R.id.weather_detail_btn);
        shakeBtn = (ImageButton) view.findViewById(R.id.shake_btn);
        keyTv = (TextView) view.findViewById(R.id.key_tv);
        sendKeyBtn = (Button) view.findViewById(R.id.send_key_btn);
        keysLl = (LinearLayout) view.findViewById(R.id.key_ll);

        detailBtn.setOnClickListener(this);
        shakeBtn.setOnClickListener(this);
        sendKeyBtn.setOnClickListener(this);

        loadingDialog = new LoadingDialog(context);
        init();
        loadData();
        return view;
    }

    private void init() {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        adapter = new CommunityListAdapter(context,
                MyApplication.instance.getCommunities());
        checkBleSupportAndInitialize();

        handler = new Handler();
        // 注册广播接收者，接收消息
        context.registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(context.getApplicationContext(),
                BluetoothLeService.class);
        context.startService(gattServiceIntent);
    }

    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            // 连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");
                // statusTv.setText("已连接门禁。");
                LogUtil.d("已连接门禁。");

                // 搜索服务
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        BluetoothLeService.discoverServices();
                    }
                });

            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                System.out.println("--------------------->发现SERVICES");
                // statusTv.setText();
                LogUtil.d("已连接门禁，正在开门…");
                prepareGattServices(BluetoothLeService
                        .getSupportedGattServices());
                doUnlock();
            } else if (action
                    .equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                System.out.println("--------------------->断开连接");
                // connect break (连接断开)
                // statusTv.setText("");
                LogUtil.d("连接已断开。");
                if (isDisconnectForUnlock) {
                    // BluetoothLeService.close();
                    isUnlocking = false;
                    loadingDialog.dismiss();
                }
            }

            // There are four basic operations for moving data in BLE: read,
            // write, notify,
            // and indicate. The BLE protocol specification requires that the
            // maximum data
            // payload size for these operations is 20 bytes, or in the case of
            // read operations,
            // 22 bytes. BLE is built for low power consumption, for infrequent
            // short-burst data transmissions.
            // Sending lots of data is possible, but usually ends up being less
            // efficient than classic Bluetooth
            // when trying to achieve maximum throughput.
            // 从google查找的，解释了为什么android下notify无法解释超过
            // 20个字节的数据
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    final byte[] array = intent
                            .getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                    LogUtil.d("收到数据：" + Utils.ByteArraytoHex(array));
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        // int size = encryptArray.length;
                        // byte[] array = new byte[size];
                        // for (int i = 0; i < size; i++) {
                        // array[i] = (byte) (encryptArray[i] ^
                        // Constants.ENCRYPT);
                        // }
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                handle(array);
                            }
                        });
                    }
                }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras
                            .containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        byte[] array = intent
                                .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);

                        // updateButtonStatus(array);

                    }
                }
            }

            if (action
                    .equals(BluetoothLeService.ACTION_GATT_DESCRIPTORWRITE_RESULT)) {
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_WRITE_RESULT)) {
                    int status = extras
                            .getInt(Constants.EXTRA_DESCRIPTOR_WRITE_RESULT);
                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        Toast.makeText(context, R.string.option_fail,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            if (action
                    .equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR)) {
                if (extras
                        .containsKey(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE)) {
                    String errorMessage = extras
                            .getString(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE);
                    System.out
                            .println("GattDetailActivity---------------------->err:"
                                    + errorMessage);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                            .show();
                }

            }

            // write characteristics succcess
            if (action
                    .equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)) {
                LogUtil.d("发送开门命令成功！");
                // handler.post(new Runnable() {
                // @Override
                // public void run() {
                // try {
                // Thread.sleep(5000);
                // if (isUnlocking) {
                // ToastUtil.showShort(context, "开门失败，断开连接！");
                // disconnectDevice();
                // }
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                // });
            }

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                // final int state =
                // intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                // BluetoothDevice.ERROR);
                // if (state == BluetoothDevice.BOND_BONDING) {}
                // else if (state == BluetoothDevice.BOND_BONDED) {}
                // else if (state == BluetoothDevice.BOND_NONE) {}
            }

        }
    };

    /**
     * Getting the GATT Services 获得服务
     * 
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);
    }

    /**
     * Prepare GATTServices data.
     * 
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE)
                    || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                continue;
            if (uuid.equals(GattAttributes.USR_SERVICE)) {
                initCharacteristics(gattService.getCharacteristics());
                notifyOption();
                break;
            }
        }
    }

    private void handle(byte[] array) {
        int size = array.length;
        if (size < 6 || array[0] != (byte) 0xAA) {
            // invalid msg
            // if (isUnlocking) {
            // retryCount++;
            // if (retryCount <= 3) {
            // //ToastUtil.showShort(context, "开门失败，第" + retryCount + "次重试…");
            // doUnlock();
            // } else {
            // ToastUtil.showShort(context, "开门失败，断开连接！");
            // disconnectDevice();
            // }
            // }
            return;
        }
        byte cmd = array[2];
        int len;
        switch (cmd) {
        case Constants.UNLOCK:
            len = array[3];
            if (size < 6 + len || array[len + 5] != (byte) 0xDD) {
                // invalid msg
                if (isUnlocking) {
                    retryCount++;
                    if (retryCount <= 3) {
                        LogUtil.d("开门失败，第" + retryCount + "次重试…");
                        doUnlock();
                    } else {
                        LogUtil.d("开门失败，断开连接！");
                        ToastUtil.showShort(context, "开门失败，断开连接！");
                        disconnectDevice();
                    }
                }
            } else {
                byte check = array[1];
                check = (byte) (check ^ cmd ^ array[3]);
                for (int i = 0; i < len; i++) {
                    check ^= array[i + 4];
                }
                if (check == array[len + 4]) {
                    // check pass
                    if (array[4] == 0x00) {
                        // 命令执行成功，断开蓝牙连接
                        ToastUtil.showShort(context, "开门成功");
                        disconnectDevice();
                    } else {
                        // 命令执行失败或命令数据错误
                        if (isUnlocking) {
                            retryCount++;
                            if (retryCount <= 3) {
                                LogUtil.d("开门失败，第" + retryCount + "次重试…");
                                doUnlock();
                            } else {
                                LogUtil.d("开门失败，断开连接！");
                                ToastUtil.showShort(context, "开门失败，断开连接！");
                                disconnectDevice();
                            }
                        }
                    }
                } else {
                    // check fail
                    if (isUnlocking) {
                        retryCount++;
                        if (retryCount <= 3) {
                            LogUtil.d("开门失败，第" + retryCount + "次重试…");
                            doUnlock();
                        } else {
                            LogUtil.d("开门失败，断开连接！");
                            ToastUtil.showShort(context, "开门失败，断开连接！");
                            disconnectDevice();
                        }
                    }
                }
            }
            break;

        default:
            // INVALID REQUEST/RESPONSE.
            break;
        // case Constants.HAND_SHAKE:
        // if (size < 5 || array[4] != (byte) 0xDD) {
        // // invalidMsg();
        // } else {
        // if ((cmd ^ array[2]) == array[3]) {
        // if (array[2] == (byte) 0x00) {
        // handShake = true;
        // // messageEt.append("握手成功。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // } else {
        // // messageEt.append("握手失败。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // }
        // } else {
        // // checkFailed();
        // }
        // }
        // break;
        // case Constants.READ_CARD:
        // if (size < 12 || array[11] != (byte) 0xDD) {
        // // invalidMsg();
        // } else {
        // for (int i = 2; i < 10; i++) {
        // if (array[i] != (byte) 0x00) {
        // // checkFailed();
        // writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
        // return;
        // }
        // }
        // byte check = cmd;
        // for (int i = 2; i < 10; i++) {
        // check ^= array[i];
        // }
        // if (check == array[10]) {
        // if (handShake) {
        // // messageEt.append("读卡号。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("90 ", "00 00 00 00 00 " +
        // MyApplication.instance.getCardNum());
        // } else {
        // // messageEt.append("未握手，读卡失败。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
        // }
        // } else {
        // // checkFailed();
        // writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
        // }
        // }
        // break;
        // case Constants.WRITE_CARD:
        // if (size < 12 || array[11] != (byte) 0xDD) {
        // // invalidMsg();
        // } else {
        // for (int i = 2; i < 6; i++) {
        // if (array[i] != (byte) 0x00) {
        // // checkFailed();
        // writeOption("91 ", "FF 00 00 00 00 00 00 00 00 ");
        // return;
        // }
        // }
        // byte check = cmd;
        // for (int i = 2; i < 10; i++) {
        // check ^= array[i];
        // }
        // if (check == array[10]) {
        // if (handShake) {
        // byte[] cn = new byte[4];
        // for (int i = 0; i < 4; i++) {
        // cn[i] = array[i + 6];
        // }
        // MyApplication.instance.setCardNum(Utils.ByteArraytoHex(cn));
        // MyApplication.instance.setCsn(MyApplication.instance.getCardNum());
        // // messageEt.append("写卡号。新卡号：" + cardNum + "\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("91 ", "00 ");
        // } else {
        // // messageEt.append("未握手，写卡失败。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("91 ", "FF ");
        // }
        // } else {
        // // checkFailed();
        // writeOption("91 ", "FF ");
        // }
        // }
        // break;
        // case Constants.DISCONNECT:
        // if (size < 12 || array[11] != (byte) 0xDD) {
        // // invalidMsg();
        // } else {
        // for (int i = 2; i < 10; i++) {
        // if (array[i] != (byte) 0x00) {
        // // checkFailed();
        // writeOption("A0 ", "FF ");
        // return;
        // }
        // }
        // byte check = cmd;
        // for (int i = 2; i < 10; i++) {
        // check ^= array[i];
        // }
        // if (check == array[10]) {
        // if (handShake) {
        // // messageEt.append("断开连接。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("A0 ", "00 ");
        // handShake = false;
        // } else {
        // // messageEt.append("未握手，断开连接失败。\n");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // writeOption("A0 ", "FF ");
        // }
        // } else {
        // // checkFailed();
        // writeOption("A0 ", "FF ");
        // }
        // }
        // break;
        // default:
        // // messageEt.append("INVALID REQUEST/RESPONSE.");
        // // messageEt.setSelection(messageEt.getText().length(),
        // // messageEt.getText().length());
        // break;
        }
    }

    private void doUnlock() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                writeOption("30 ", "06 00 00 "
                        + MyApplication.instance.getUserInfo().getCardNo());
            }
        });
        // handler.post(new Runnable() {
        // @Override
        // public void run() {
        // try {
        // Thread.sleep(5000);
        // if (isUnlocking) {
        // ToastUtil.showShort(context, "开门失败，断开连接！");
        // disconnectDevice();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });
    }

    private void writeOption(String cmd, String data) {
        writeOption(Utils.getCmdData("00 ", cmd, data));
    }

    private void writeOption(String hexStr) {
        writeCharacteristic(writeCharacteristic,
                Utils.getCmdDataByteArray(hexStr));
    }

    private void writeCharacteristic(
            BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            LogUtil.d("write bytes: " + Utils.ByteArraytoHex(bytes));
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initCharacteristics(
            List<BluetoothGattCharacteristic> characteristics) {
        for (BluetoothGattCharacteristic c : characteristics) {
            if (Utils.getPorperties(context, c).equals("Notify")) {
                notifyCharacteristic = c;
                continue;
            }

            if (Utils.getPorperties(context, c).equals("Write")) {
                writeCharacteristic = c;
                continue;
            }
        }
    }

    private void notifyOption() {
        if (notifyEnable) {
            notifyEnable = false;
            stopBroadcastDataNotify(notifyCharacteristic);
        } else {
            notifyEnable = true;
            prepareBroadcastDataNotify(notifyCharacteristic);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     * 
     * @param characteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     * 
     * @param characteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic,
                    false);
        }
    }

    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        LogUtil.d("connectDevice: DevName = " + currentDevName
                + "; DevAddress = " + currentDevAddress);
        // 如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED) {
            isDisconnectForUnlock = false;
            BluetoothLeService.disconnect();
        }

        BluetoothLeService.connect(currentDevAddress, currentDevName, context);
    }

    private void disconnectDevice() {
        notifyOption();
        if (isUnlocking) {
            isDisconnectForUnlock = true;
        } else {
            isDisconnectForUnlock = false;
        }
        handler.post(new Runnable() {

            @Override
            public void run() {
                BluetoothLeService.disconnect();
            }
        });
    }

    private void loadData() {
        // 若没有选小区，则获取小区列表，让用户选小区
        if (MyApplication.instance.getSelectedCommunity() == null) {
            getCommunityList();
            return;
        }
        // 若用户已登录，则初始化和登录云通讯账号
        if (!TextUtils.isEmpty(MyApplication.instance.getUserInfo()
                .getAccount())) {
            String appKey = FileAccessor.getAppKey();
            String token = FileAccessor.getAppToken();
            String myMobile = MyApplication.instance.getUserInfo().getMobile();
            String pass = "";
            ClientUser clientUser = new ClientUser(myMobile);
            clientUser.setAppKey(appKey);
            clientUser.setAppToken(token);
            clientUser.setLoginAuthType(LoginAuthType.NORMAL_AUTH);
            clientUser.setPassword(pass);
            CCPAppManager.setClientUser(clientUser);
            SDKCoreHelper.init(context, LoginMode.FORCE_LOGIN);
        }
    }

    private void getWeather() {
        ServiceManager.getWeather(new RequestListener<GetWeatherResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在加载…");
            }

            @Override
            public void onSuccess(int stateCode, GetWeatherResult result) {
                if (result.getErrorCode().equals("0")) {
                    refreshWeather(result);
                } else {
                    ToastUtil.showShort(context, result.getReason());
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, "获取天气信息失败，请稍后再试。");
                loadingDialog.dismiss();
            }

        });
    }

    private void refreshWeather(GetWeatherResult result) {
        Field f;
        try {
            f = (Field) R.drawable.class.getDeclaredField("ww"
                    + result.getImg());
            int id = f.getInt(R.drawable.class);
            weatherIv.setImageResource(id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        weatherTv.setText(result.getWeather());
        tempTv.setText(result.getTemp() + "℃");
        areaTv.setText(result.getCity());
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
                                setSelectedKey();
                                refreshKey();
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
                        setSelectedKey();
                        refreshKey();
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

    private void refreshKey() {
        RadixLock selectedKey = MyApplication.instance.getSelectedLock();
        if (selectedKey != null) {
            keyTv.setText(selectedKey.getName());
        }
        keysLl.removeAllViews();
        final List<RadixLock> keys = MyApplication.instance.getLocks();
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            KeyView keyView;
            if (keys.get(i).equals(selectedKey)) {
                keyView = new KeyView(context, keys.get(i), i, true);
            } else {
                keyView = new KeyView(context, keys.get(i), i, false);
            }
            keyView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int idx = (int) v.getTag();
                    if (!keys.get(idx).equals(
                            MyApplication.instance.getSelectedLock())) {
                        MyApplication.instance.setSelectedLock(keys.get(idx));
                        refreshKey();
                    }
                }
            });
            keysLl.addView(keyView);
        }
    }

    private void setSelectedKey() {
        String selectedKey = PrefUtil.getString(context,
                Constants.PREF_SELECTED_KEY);
        for (RadixLock lock : MyApplication.instance.getLocks()) {
            if (selectedKey.equals(lock.getId())) {
                MyApplication.instance.setSelectedLock(lock);
                return;
            }
        }
        // 若没有选择钥匙，则默认选第一个
        if (MyApplication.instance.getLocks().size() > 0) {
            MyApplication.instance.setSelectedLock(MyApplication.instance
                    .getLocks().get(0));
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PrefUtil.getBoolean(context, Constants.PREF_SHAKE_SWITCH, true)) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        getWeather();
        getLockList();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mGattUpdateReceiver);
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.weather_detail_btn:
            context.startActivity(new Intent(context, WeatherActivity.class));
            break;

        case R.id.send_key_btn:
            context.startActivity(new Intent(context, MyKeysActivity.class));
            break;

        case R.id.shake_btn:
            preUnlock();
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
                preUnlock();
            }

        }
    }

    private void preUnlock() {
        RadixLock lock = MyApplication.instance.getSelectedLock();
        LogUtil.d("preUnlock: lock = " + lock.getBleName1());
        if (lock != null) {
            String lockPaternStr = PrefUtil.getString(context,
                    Constants.PREF_LOCK_KEY, null);
            if (!TextUtils.isEmpty(lockPaternStr)) {
                // 如果有手势密码，则验证手势密码
                LockValidateActivity.startForResult(this, Constants.LOCK_CHECK);
            } else {
                // 没有手势密码，则直接开锁
                unlock();
            }
        } else {
            // 提示请先选择钥匙
            ToastUtil.showShort(context, "请先选择钥匙！");
        }
    }

    private void unlock() {
        if (mBluetoothAdapter == null) {
            getBluetoothAdapter();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ToastUtil.showLong(context, "蓝牙未开启，请先开启蓝牙！");
            return;
        }
        if (!isUnlocking) {
            isUnlocking = true;
            retryCount = 0;
            RadixLock lock = MyApplication.instance.getSelectedLock();
            LogUtil.d("unlock: inBleName = " + lock.getBleName1()
                    + ", outBleName = " + lock.getBleName2());
            startScan();
            handler.post(new Runnable() {

                @Override
                public void run() {
                    loadingDialog.show("正在开门…");
                }
            });
        }
        // for (MDevice device : list) {
        // LogUtil.d("device list: " + device.getDevice().getName());
        // if (device.getDevice().getName() != null &&
        // device.getDevice().getName().equalsIgnoreCase(bleName)) {
        // if (!isUnlocking) {
        // isUnlocking = true;
        // retryCount = 0;
        // loadingDialog.show("正在开门…");
        // connectDevice(device.getDevice());
        // break;
        // }
        // }
        // }
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
        final BluetoothManager bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
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
                    if (list.contains(mDev)) {
                        return;
                    }
                    list.add(mDev);
                    String name = mDev.getDevice().getName();
                    LogUtil.d("发现蓝牙设备：" + name);
                    if (name == null) {
                        return;
                    }
                    RadixLock lock = MyApplication.instance.getSelectedLock();
                    if (name.equals(lock.getBleName1())
                            || name.equals(lock.getBleName2())) {
                        if (!foundDevice) {
                            foundDevice = true;
                            LogUtil.d("正在连接……");
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    connectDevice(device);
                                }
                            });
                            if (mBluetoothAdapter != null) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            }
                        }
                    }
                }
            });
        }
    };

    private void startScan() {
        list.clear();
        foundDevice = false;
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
        // 2秒后停止扫描
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
                if (!foundDevice) {
                    ToastUtil.showShort(context, "没找到此门禁设备！");
                    loadingDialog.dismiss();
                    isUnlocking = false;
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
                        public void onScanResult(int callbackType,
                                ScanResult result) {
                            super.onScanResult(callbackType, result);
                        }
                    });
                }
                if (!foundDevice) {
                    ToastUtil.showShort(context, "没找到此门禁设备！");
                    loadingDialog.dismiss();
                    isUnlocking = false;
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
                    final MDevice mDev = new MDevice(result.getDevice(), result
                            .getRssi());
                    if (list.contains(mDev)) {
                        return;
                    }
                    list.add(mDev);
                    String name = mDev.getDevice().getName();
                    LogUtil.d("发现蓝牙设备：" + name);
                    if (name == null) {
                        return;
                    }
                    RadixLock lock = MyApplication.instance.getSelectedLock();
                    if (name.equals(lock.getBleName1())
                            || name.equals(lock.getBleName2())) {
                        if (!foundDevice) {
                            foundDevice = true;
                            LogUtil.d("正在连接……");
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    connectDevice(mDev.getDevice());
                                }
                            });
                            if (bleScanner != null) {
                                bleScanner.stopScan(new ScanCallback() {
                                    @Override
                                    public void onScanResult(int callbackType,
                                            ScanResult result) {
                                        super.onScanResult(callbackType, result);
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    // @TargetApi(Build.VERSION_CODES.M)
    // private void mayRequestLocation() {
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    // int checkCallPhonePermission =
    // context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    // if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
    // // 判断是否需要 向用户解释，为什么要申请该权限
    // if
    // (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
    // ToastUtil.showLong(context, "Android 6.0以上系统连接蓝牙需要获取您的位置。");
    //
    // requestPermissions(
    // new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
    // REQUEST_FINE_LOCATION);
    // return;
    // } else {
    //
    // }
    // } else {
    //
    // }
    // }

    // @Override
    // public void onRequestPermissionsResult(int requestCode,
    // @NonNull String[] permissions, @NonNull int[] grantResults) {
    // switch (requestCode) {
    // case REQUEST_FINE_LOCATION:
    // // If request is cancelled, the result arrays are empty.
    // if (grantResults.length > 0
    // && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // // The requested permission is granted.
    // if (mScanning == false) {
    // startScan();
    // }
    // } else {
    // // The user disallowed the requested permission.
    // }
    // break;
    //
    // }
    //
    // }

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
                ToastUtil.showShort(context, "密码不正确！");
            }
        }
    }

}
