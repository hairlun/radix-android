package com.patr.radix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.patr.radix.adapter.AbsListAdapter;
import com.patr.radix.bean.MDevice;
import com.patr.radix.ble.BluetoothLeService;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.GattAttributes;
import com.patr.radix.utils.Utils;
import com.patr.radix.view.ListSelectDialog;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UnlockActivity extends Activity implements OnItemClickListener,
        OnClickListener {

    private TextView statusTv;
    private Button unlockBtn;

    private static BluetoothAdapter mBluetoothAdapter;
    private Handler handler;
    private final List<MDevice> list = new ArrayList<MDevice>();
    private final List<String> nameList = new ArrayList<String>();
    private BluetoothLeScanner bleScanner;
    private String currentDevAddress;
    private String currentDevName;
    MyApplication myApplication;

    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private boolean nofityEnable = false;
    private boolean handShake = false;
    private String cardNum = "FF FF FF FF ";
    private String csn;
    private static final int REQUEST_FINE_LOCATION = 0;
    private boolean mScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        statusTv = (TextView) findViewById(R.id.status_tv);
        unlockBtn = (Button) findViewById(R.id.unlock_btn);
        statusTv.setText("未连接门禁。");
        unlockBtn.setOnClickListener(this);
        handler = new Handler();
        myApplication = (MyApplication) getApplication();
        checkBleSupportAndInitialize();
        // 注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());

        Intent gattServiceIntent = new Intent(getApplicationContext(),
                BluetoothLeService.class);
        startService(gattServiceIntent);

        if (!mScanning) {
            startScan();
        }
        csn = Utils.getCsn(this);
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

                // 搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                System.out.println("--------------------->发现SERVICES");
                statusTv.setText("已连接门禁，正在开门…");
                prepareGattServices(BluetoothLeService
                        .getSupportedGattServices());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlock();
                    }
                }, 50);
            } else if (action
                    .equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                System.out.println("--------------------->断开连接");
                // connect break (连接断开)
                statusTv.setText("连接已断开。");
                BluetoothLeService.close();
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
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            byte[] encryptArray = intent
                                    .getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                            int size = encryptArray.length;
                            byte[] array = new byte[size];
                            for (int i = 0; i < size; i++) {
                                array[i] = (byte) (encryptArray[i] ^ Constants.ENCRYPT);
                            }
                            // messageEt.append("Received: " +
                            // formatMsgContent(array) + " (encrypt:" +
                            // Utils.ByteArraytoHex(encryptArray) + ")\n");
                            // messageEt.setSelection(messageEt.getText().length(),
                            // messageEt.getText().length());
                            handle(array);
                        }
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
                // Toast.makeText(context, "开门成功", Toast.LENGTH_SHORT).show();
                // notifyOption();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        disconnectDevice();
                    }
                }, 100);
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
                // notifyOption();
                break;
            }
        }
    }

    private void handle(byte[] array) {
        int size = array.length;
        if (size < 5 || array[0] != (byte) 0xAA) {
            // invalidMsg();
            return;
        }
        byte cmd = array[1];
        switch (cmd) {
        case Constants.HAND_SHAKE:
            if (size < 5 || array[4] != (byte) 0xDD) {
                // invalidMsg();
            } else {
                if ((cmd ^ array[2]) == array[3]) {
                    if (array[2] == (byte) 0x00) {
                        handShake = true;
                        // messageEt.append("握手成功。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                    } else {
                        // messageEt.append("握手失败。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                    }
                } else {
                    // checkFailed();
                }
            }
            break;
        case Constants.READ_CARD:
            if (size < 12 || array[11] != (byte) 0xDD) {
                // invalidMsg();
            } else {
                for (int i = 2; i < 10; i++) {
                    if (array[i] != (byte) 0x00) {
                        // checkFailed();
                        writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
                        return;
                    }
                }
                byte check = cmd;
                for (int i = 2; i < 10; i++) {
                    check ^= array[i];
                }
                if (check == array[10]) {
                    if (handShake) {
                        // messageEt.append("读卡号。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("90 ", "00 00 00 00 00 " + cardNum);
                    } else {
                        // messageEt.append("未握手，读卡失败。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
                    }
                } else {
                    // checkFailed();
                    writeOption("90 ", "FF 00 00 00 00 00 00 00 00 ");
                }
            }
            break;
        case Constants.WRITE_CARD:
            if (size < 12 || array[11] != (byte) 0xDD) {
                // invalidMsg();
            } else {
                for (int i = 2; i < 6; i++) {
                    if (array[i] != (byte) 0x00) {
                        // checkFailed();
                        writeOption("91 ", "FF 00 00 00 00 00 00 00 00 ");
                        return;
                    }
                }
                byte check = cmd;
                for (int i = 2; i < 10; i++) {
                    check ^= array[i];
                }
                if (check == array[10]) {
                    if (handShake) {
                        byte[] cn = new byte[4];
                        for (int i = 0; i < 4; i++) {
                            cn[i] = array[i + 6];
                        }
                        cardNum = Utils.ByteArraytoHex(cn);
                        csn = cardNum;
                        // messageEt.append("写卡号。新卡号：" + cardNum + "\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("91 ", "00 ");
                    } else {
                        // messageEt.append("未握手，写卡失败。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("91 ", "FF ");
                    }
                } else {
                    // checkFailed();
                    writeOption("91 ", "FF ");
                }
            }
            break;
        case Constants.DISCONNECT:
            if (size < 12 || array[11] != (byte) 0xDD) {
                // invalidMsg();
            } else {
                for (int i = 2; i < 10; i++) {
                    if (array[i] != (byte) 0x00) {
                        // checkFailed();
                        writeOption("A0 ", "FF ");
                        return;
                    }
                }
                byte check = cmd;
                for (int i = 2; i < 10; i++) {
                    check ^= array[i];
                }
                if (check == array[10]) {
                    if (handShake) {
                        // messageEt.append("断开连接。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("A0 ", "00 ");
                        handShake = false;
                    } else {
                        // messageEt.append("未握手，断开连接失败。\n");
                        // messageEt.setSelection(messageEt.getText().length(),
                        // messageEt.getText().length());
                        writeOption("A0 ", "FF ");
                    }
                } else {
                    // checkFailed();
                    writeOption("A0 ", "FF ");
                }
            }
            break;
        default:
            // messageEt.append("INVALID REQUEST/RESPONSE.");
            // messageEt.setSelection(messageEt.getText().length(),
            // messageEt.getText().length());
            break;
        }
    }

    private void writeOption(String cmd, String data) {
        String dataText = data.replace(" ", "");
        String cmdText = cmd.replace(" ", "");
        byte[] dataArray = Utils.hexStringToByteArray(dataText);
        byte[] cmdArray = Utils.hexStringToByteArray(cmdText);
        byte[] check = { cmdArray[0] };
        for (byte b : dataArray) {
            check[0] ^= b;
        }
        writeOption("AA " + cmd + data + Utils.ByteArraytoHex(check) + "DD");
    }

    private void writeOption(String hexStr) {
        String text = hexStr.replace(" ", "");
        byte[] array = Utils.hexStringToByteArray(text);
        int size = array.length;
        for (int i = 0; i < size; i++) {
            array[i] ^= Constants.ENCRYPT;
        }
        writeCharacteristic(writeCharacteristic, array);
        // messageEt.append("Sent: HEX:" + hexStr + "(encrypt: " +
        // Utils.ByteArraytoHex(array) + ")\n");
        // messageEt.setSelection(messageEt.getText().length(),
        // messageEt.getText().length());
    }

    private void writeCharacteristic(
            BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void unlock() {
        writeOption("31 ", "00 00 00 00 " + csn);
    }

    private void initCharacteristics(
            List<BluetoothGattCharacteristic> characteristics) {
        for (BluetoothGattCharacteristic c : characteristics) {
            if (Utils.getPorperties(this, c).equals("Notify")) {
                notifyCharacteristic = c;
                continue;
            }

            if (Utils.getPorperties(this, c).equals("Write")) {
                writeCharacteristic = c;
                continue;
            }
        }
    }

    private void notifyOption() {
        if (nofityEnable) {
            nofityEnable = false;
            stopBroadcastDataNotify(notifyCharacteristic);
            // messageEt.append("STOP NOTIFY\n");
            // messageEt.setSelection(messageEt.getText().length(),
            // messageEt.getText().length());
        } else {
            nofityEnable = true;
            prepareBroadcastDataNotify(notifyCharacteristic);
            // messageEt.append("NOTIFY\n");
            // messageEt.setSelection(messageEt.getText().length(),
            // messageEt.getText().length());
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

    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        getBluetoothAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            return;
        }

        // 打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    private void getBluetoothAdapter() {
    	final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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

            runOnUiThread(new Runnable() {
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
                    nameList.add(name);
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
                if (statusTv.getText().equals("正在扫描门禁设备…")) {
                    if (list.size() <= 0) {
                        Toast.makeText(UnlockActivity.this, "没找到门禁设备",
                                Toast.LENGTH_SHORT).show();
                        statusTv.setText("未连接门禁。");
                    } else {
                        statusTv.setText("正在连接门禁…");
                        unlockBtn.performClick();
                    }
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
                if (statusTv.getText().equals("正在扫描门禁设备…")) {
                    if (list.size() <= 0) {
                        Toast.makeText(UnlockActivity.this, "没找到门禁设备",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        statusTv.setText("正在连接门禁…");
                        unlockBtn.performClick();
                    }
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
	                nameList.add(name);
	            }
	        });
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                // 判断是否需要 向用户解释，为什么要申请该权限
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "Android 6.0以上系统连接蓝牙需要获取您的位置。", Toast.LENGTH_LONG).show();

                requestPermissions(
                        new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                        REQUEST_FINE_LOCATION);
                return;
            } else {

            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
        case REQUEST_FINE_LOCATION:
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The requested permission is granted.
                if (mScanning == false) {
                    startScan();
                }
            } else {
                // The user disallowed the requested permission.
            }
            break;

        }

    }

    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        // 如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED)
            BluetoothLeService.disconnect();

        statusTv.setText("正在连接门禁…");
        BluetoothLeService.connect(currentDevAddress, currentDevName, this);
    }

    private void disconnectDevice() {
        BluetoothLeService.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unlock_btn:
            if (list.size() > 1) {
                ListSelectDialog.show(this, "请选择门禁", new SelectListAdapter(
                        this, nameList), this);
            } else if (list.size() == 1) {
                connectDevice(list.get(0).getDevice());
            } else {
                // Toast.makeText(MainActivity.this, "未找到门禁设备",
                // Toast.LENGTH_SHORT).show();
                statusTv.setText("正在扫描门禁设备…");
                startScan();
            }
            break;
        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        connectDevice(list.get(position).getDevice());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    class SelectListAdapter extends AbsListAdapter<String> {

        /** 已选择的集合 */
        public HashSet<String> selectedSet = new HashSet<String>();

        /**
         * 构造方法
         * 
         * @param context
         *            上下文对象
         * @param mList
         *            数据源
         */
        public SelectListAdapter(Context context, List<String> mList) {
            super(context, mList);
            notifyDataSetChanged();
        }

        /**
         * 选择
         * 
         * @param position
         */
        public void select(int position) {
            selectedSet.clear();
            selectedSet.add(getItem(position));
            notifyDataSetChanged();
        }

        /**
         * 判断是否已选择
         * 
         * @param position
         * @return
         */
        public boolean isSelect(int position) {
            return selectedSet.contains(getItem(position));
        }

        /**
         * 取消选择
         * 
         * @param position
         */
        public void deselect(int position) {
            selectedSet.remove(getItem(position));
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_server_addr, null);
                holder.tv = (TextView) convertView.findViewById(R.id.choose_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String text = getItem(position);
            holder.tv.setText(text);
            holder.tv.setSelected(selectedSet.contains(text));
            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }

    }
}
