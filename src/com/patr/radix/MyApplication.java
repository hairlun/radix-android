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
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.MService;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.GetLockListParser;
import com.patr.radix.bll.ServiceManager.Url;
import com.patr.radix.fragment.UnlockFragment;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.view.ListSelectDialog;

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
    
    private final List<Community> communities = new ArrayList<Community>();
    
    private final List<RadixLock> locks = new ArrayList<RadixLock>();
    
    private RadixLock selectedLock;
    
    private Community selectedCommunity;
    
    private String selectedCommunityId;
    
    private String selectedLockId;
    
    private String mUserId;
    
    private String mName;
    
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(DEBUG);
        instance = this;
        if (DEBUG) {
            mUserId = "admin";
        }
        // 从缓存读取小区列表和当前选择小区
        getCommunityListFromCache();
        // 从缓存读取门禁钥匙列表和当前选择门禁钥匙
        getLockListFromCache();
    }
    
    private void getCommunityListFromCache() {
        CacheManager.getCacheContent(instance, CacheManager.getCommunityListUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            setCommunities(result.getCommunities());
                            if (getSelectedCommunityId() != null) {
                                for (Community community : getCommunities()) {
                                    if (selectedCommunityId.equals(community.getId())) {
                                        setSelectedCommunity(community);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                }, new GetCommunityListParser());
    }
    
    private void getLockListFromCache() {
        if (getSelectedCommunity() != null) {
            CacheManager.getCacheContent(instance, CacheManager.getLockListUrl(), 
                    new RequestListener<GetLockListResult>() {
    
                        @Override
                        public void onSuccess(int stateCode,
                                GetLockListResult result) {
                            if (result != null) {
                                setLocks(result.getLocks());
                                if (getSelectedLockId() != null) {
                                    for (RadixLock lock : getLocks()) {
                                        if (selectedLockId.equals(lock.getId())) {
                                            setSelectedLock(lock);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        
                    }, new GetLockListParser());
        }
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
            mUserId = PrefUtil.getString(instance, Constants.PREF_USER_ID);
        }
        return mUserId;
    }
    
    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getName() {
        if (TextUtils.isEmpty(mName)) {
            mUserId = PrefUtil.getString(instance, Constants.PREF_NAME);
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
        if (selectedLock != null) {
            PrefUtil.save(instance, Constants.PREF_SELECTED_KEY, selectedLock.getId());
        } else {
            PrefUtil.save(instance, Constants.PREF_SELECTED_KEY, null);
        }
    }
    
    public String getSelectedLockId() {
        if (TextUtils.isEmpty(selectedLockId)) {
            selectedLockId = PrefUtil.getString(instance, Constants.PREF_SELECTED_KEY);
        }
        return selectedLockId;
    }

    public Community getSelectedCommunity() {
        return selectedCommunity;
    }

    public void setSelectedCommunity(Community selectedCommunity) {
        this.selectedCommunity = selectedCommunity;
        if (selectedCommunity != null) {
            PrefUtil.save(instance, Constants.PREF_SELECTED_COMMUNITY, selectedCommunity.getId());
        } else {
            PrefUtil.save(instance, Constants.PREF_SELECTED_COMMUNITY, null);
        }
    }
    
    public String getSelectedCommunityId() {
        if (TextUtils.isEmpty(selectedCommunityId)) {
            selectedCommunityId = PrefUtil.getString(instance, Constants.PREF_SELECTED_COMMUNITY);
        }
        return selectedCommunityId;
    }

    public List<Community> getCommunities() {
        return communities;
    }
    
    public void setCommunities(List<Community> communities) {
        this.communities.clear();
        this.communities.addAll(communities);
    }

}
