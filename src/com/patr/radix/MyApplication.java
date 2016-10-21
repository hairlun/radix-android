package com.patr.radix;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import cn.smssdk.SMSSDK;

import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.MService;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.GetLockListParser;
import com.patr.radix.network.RequestListener;
import com.patr.radix.network.WebService;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

public class MyApplication extends Application {

    /**
     * 打包发布要改为false
     */
    public static final boolean DEBUG = true;

    public static final boolean TEST = false;

    private final List<MService> services = new ArrayList<MService>();
    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>();

    private final List<Community> communities = new ArrayList<Community>();

    private final List<RadixLock> locks = new ArrayList<RadixLock>();

    private final List<RadixLock> selectedLocks = new ArrayList<RadixLock>();

    private RadixLock selectedLock;

    private Community selectedCommunity;

    private String selectedCommunityId;

    private String selectedLockId;

    private UserInfo userInfo = new UserInfo();

    private String visitorId;

    private int badge = 0;

    // 信鸽推送token
    private String pushToken;

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(DEBUG);
        instance = this;
        // 从缓存读取用户信息
        userInfo = PrefUtil.getUserInfo(instance);
        // 从缓存读取小区列表和当前选择小区
        getCommunityListFromCache();
        // 从缓存读取门禁钥匙列表和当前选择门禁钥匙
        getLockListFromCache();
        // 初始化验证码模块
        SMSSDK.initSDK(this, "17805a217c862",
                "4489d28f7383f6b9eb6b697b3998a42d");
        setVisitorId(String.format("%s", System.currentTimeMillis()));

        // 在主进程设置信鸽相关的内容
        if (isMainProcess()) {
            // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
            // 收到通知时，会调用本回调函数。
            // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
            // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
            XGPushManager
                    .setNotifactionCallback(new XGPushNotifactionCallback() {

                        @Override
                        public void handleNotify(XGNotifaction xGNotifaction) {
                            Log.i("test", "处理信鸽通知：" + xGNotifaction);
                            // 获取标签、内容、自定义内容
                            String title = xGNotifaction.getTitle();
                            int start = title.indexOf("(") + 1;
                            int end = title.indexOf(")");
                            if (start < end) {
                                try {
                                    setBadge(Integer.parseInt(title.substring(start, end)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // 其它的处理
                            // 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
                            xGNotifaction.doNotify();
                        }
                    });
        }
    }

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void getCommunityListFromCache() {
        CacheManager.getCacheContent(instance,
                CacheManager.getCommunityListUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            setCommunities(result.getCommunities());
                            if (getSelectedCommunityId() != null) {
                                for (Community community : getCommunities()) {
                                    if (selectedCommunityId.equals(community
                                            .getId())) {
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
            CacheManager.getCacheContent(instance,
                    CacheManager.getLockListUrl(),
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

    public void setCharacteristics(
            List<BluetoothGattCharacteristic> characteristics) {
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RadixLock getSelectedLock() {
        return selectedLock;
    }

    public void setSelectedLock(RadixLock selectedLock) {
        this.selectedLock = selectedLock;
        if (selectedLock != null) {
            PrefUtil.save(instance, Constants.PREF_SELECTED_KEY,
                    selectedLock.getId());
        } else {
            PrefUtil.save(instance, Constants.PREF_SELECTED_KEY, null);
        }
    }

    public String getSelectedLockId() {
        if (TextUtils.isEmpty(selectedLockId)) {
            selectedLockId = PrefUtil.getString(instance,
                    Constants.PREF_SELECTED_KEY);
        }
        return selectedLockId;
    }

    public Community getSelectedCommunity() {
        return selectedCommunity;
    }

    public void setSelectedCommunity(Community selectedCommunity) {
        this.selectedCommunity = selectedCommunity;
        if (selectedCommunity != null) {
            WebService.URL = selectedCommunity.getUrl();
            PrefUtil.save(instance, Constants.PREF_SELECTED_COMMUNITY,
                    selectedCommunity.getId());
        } else {
            PrefUtil.save(instance, Constants.PREF_SELECTED_COMMUNITY, null);
        }
    }

    public String getSelectedCommunityId() {
        if (TextUtils.isEmpty(selectedCommunityId)) {
            selectedCommunityId = PrefUtil.getString(instance,
                    Constants.PREF_SELECTED_COMMUNITY);
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

    public List<RadixLock> getSelectedLocks() {
        return selectedLocks;
    }

    public void setSelectedLocks(List<RadixLock> list) {
        this.selectedLocks.clear();
        this.selectedLocks.addAll(list);
    }

    public String getPushToken() {
        return pushToken == null ? "" : pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public void clearCache() {
        locks.clear();
        selectedLocks.clear();
        selectedLock = null;
        selectedLockId = null;
        PrefUtil.save(this, Constants.PREF_SELECTED_KEY, null);
    }

}
