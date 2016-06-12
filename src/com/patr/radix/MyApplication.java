package com.patr.radix;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.bean.MService;

public class MyApplication extends Application {

    /**
     * 打包发布要改为false
     */
    public static final boolean DEBUG = true;

    /**
     * 服务器地址
     */
    public static final String DEFAULT_URL = "http://117.34.71.28/web/mobile";

    private final List<MService> services = new ArrayList<>();
    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

    public List<MService> getServices() {
        return services;
    }

    public void setServices(List<MService> services) {
        this.services.clear();
        this.services.addAll(services);
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<BluetoothGattCharacteristic> characteristics) {
        this.characteristics.clear();
        this.characteristics.addAll(characteristics);
    }

}
