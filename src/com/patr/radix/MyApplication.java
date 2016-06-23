package com.patr.radix;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import com.patr.radix.bean.MService;
import com.patr.radix.bean.RadixLock;

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
    
    private final List<RadixLock> locks = new ArrayList<>();
    
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(DEBUG);
        instance = this;
    }

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

    public List<RadixLock> getLocks() {
        return locks;
    }
    
    public void setLocks(List<RadixLock> locks) {
        this.locks.clear();
        this.locks.addAll(locks);
    }

}
