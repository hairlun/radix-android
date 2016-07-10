package com.patr.radix;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.patr.radix.bean.MService;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;

public class MyApplication extends Application {

    /**
     * 打包发布要改为false
     */
    public static final boolean DEBUG = true;

    /**
     * 服务器地址
     */
    public static final String DEFAULT_URL = "http://112.92.206.187/surpass/mobile";

    private final List<MService> services = new ArrayList<MService>();
    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>();
    
    private final List<RadixLock> locks = new ArrayList<RadixLock>();
    
    private RadixLock selectedLock;
    
    private String mUserId = "admin";
    
    private String mName;
    
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
    
    public String getUserId() {
        if (TextUtils.isEmpty(mUserId)) {
            mUserId = PrefUtil.getString(instance, Constants.PREF_USER_ID_KEY);
        }
        return mUserId;
    }
    
    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getName() {
        if (TextUtils.isEmpty(mName)) {
            mUserId = PrefUtil.getString(instance, Constants.PREF_NAME_KEY);
        }
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public RadixLock getSelectedLock() {
        return selectedLock;
    }

    public void setSelectedLock(RadixLock selectedLock) {
        this.selectedLock = selectedLock;
        PrefUtil.save(instance, Constants.PREF_SELECTED_KEY, selectedLock.getId());
    }

}
