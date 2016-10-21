package com.patr.radix;

import java.io.File;
import java.io.InvalidClassException;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.patr.radix.adapter.KeyListAdapter2;
import com.patr.radix.bean.QueryPersonMessageResult;
import com.patr.radix.ble.BluetoothLeService;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.unlock.MyKeysActivity;
import com.patr.radix.ui.view.ListSelectDialog;
import com.patr.radix.ui.view.dialog.MsgDialog;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.TabDb;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.ECContentObservers;
import com.yuntongxun.ecdemo.common.utils.CrashHandler;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
        OnTabChangeListener, OnItemClickListener {

    private Context mContext;

    private FragmentTabHost tabHost;

    private KeyListAdapter2 adapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("MainActivity", action);
            if ("actionClearPersonMessage".equals(action)) {
                View view = tabHost.getTabWidget().getChildAt(2);
                TextView badge = (TextView) view.findViewById(R.id.badge);
                badge.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Intent intent = getIntent();
        if (intent.getBooleanExtra("visitorCall", false)) {
            MsgDialog.show(mContext, "提示", "您收到开门申请，请选择", "立即开门", "发送钥匙", "取消",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 立即开门
                            Intent intent = new Intent(mContext,
                                    MyKeysActivity.class);
                            intent.putExtra("remoteOpenDoor", true);
                            mContext.startActivity(intent);
                        }
                    }, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            MyKeysActivity.start(MainActivity.this);
                        }
                    }, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                        }
                    });
        }
        tabHost = (FragmentTabHost) super.findViewById(android.R.id.tabhost);
        tabHost.setup(this, super.getSupportFragmentManager(),
                R.id.contentLayout);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(this);
        initTab();
        registerReceiver();
        adapter = new KeyListAdapter2(this, MyApplication.instance.getLocks());

        // 云通讯初始化
        CCPAppManager.setContext(MyApplication.instance);
        FileAccessor.initFileAccess();
        setChattingContactId();
        initImageLoader();
        // CrashHandler.getInstance().init(MyApplication.instance);
        ECContentObservers.getInstance().initContentObserver();
        CrashHandler.getInstance().setContext(this);

        XGPushConfig.enableDebug(this, MyApplication.DEBUG);
        if (PrefUtil.getBoolean(this, Constants.PREF_PUSH_SWITCH, true)) {
            // 信鸽注册
            // 开启logcat输出，方便debug，发布时请关闭
            // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
            // XGIOperateCallback)带callback版本
            // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
            // 具体可参考详细的开发指南
            // 传递的参数为ApplicationContext
            final Context context = getApplicationContext();
            XGPushManager.unregisterPush(getApplicationContext(),
                    new XGIOperateCallback() {
                        @Override
                        public void onSuccess(Object data, int flag) {
                            XGPushManager.registerPush(context,
                                    new XGIOperateCallback() {

                                        @Override
                                        public void onSuccess(Object data,
                                                int flag) {
                                            Log.d("TPush", "注册成功，设备token为："
                                                    + data);
                                            // 保存pushToken到本地
                                            MyApplication.instance
                                                    .setPushToken((String) data);
                                        }

                                        @Override
                                        public void onFail(Object data,
                                                int errCode, String msg) {
                                            Log.d("TPush", "注册失败，错误码："
                                                    + errCode + ",错误信息：" + msg);
                                        }
                                    });
                        }

                        @Override
                        public void onFail(Object data, int errCode, String msg) {
                            Log.d("TPush", "反注册失败，错误码：" + errCode + ",错误信息："
                                    + msg);
                            XGPushManager.registerPush(context,
                                    new XGIOperateCallback() {

                                        @Override
                                        public void onSuccess(Object data,
                                                int flag) {
                                            Log.d("TPush", "注册成功，设备token为："
                                                    + data);
                                            // 保存pushToken到本地
                                            MyApplication.instance
                                                    .setPushToken((String) data);
                                        }

                                        @Override
                                        public void onFail(Object data,
                                                int errCode, String msg) {
                                            Log.d("TPush", "注册失败，错误码："
                                                    + errCode + ",错误信息：" + msg);
                                        }
                                    });
                        }
                    });
        }
    }

    private void initTab() {
        int tabs[] = TabDb.getTabsTxt();
        for (int i = 0; i < tabs.length; i++) {
            TabSpec tabSpec = tabHost.newTabSpec(
                    getResources().getString(tabs[i])).setIndicator(
                    getTabView(i));
            tabHost.addTab(tabSpec, TabDb.getFragments()[i], null);
            tabHost.setTag(i);
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(this).inflate(R.layout.footer_tabs,
                null);
        int defaultTab = 0;
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView badge = (TextView) view.findViewById(R.id.badge);
        tv.setText(TabDb.getTabsTxt()[idx]);
        // TODO 设置badge
        // if (idx == 2) {
        // badge.setText("99");
        // badge.setVisibility(View.VISIBLE);
        // } else {
        // badge.setVisibility(View.GONE);
        // }
        if (idx == defaultTab) {
            iv.setImageResource(TabDb.getTabsImgLight()[idx]);
            tv.setTextColor(getResources().getColor(
                    R.color.buttombar_text_selected));
        } else {
            iv.setImageResource(TabDb.getTabsImg()[idx]);
            tv.setTextColor(getResources().getColor(R.color.buttombar_text));
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        updateTab();
        updateBadge();
    }

    private void updateTab() {
        TabWidget tabw = tabHost.getTabWidget();
        for (int i = 0; i < tabw.getChildCount(); i++) {
            View view = tabw.getChildAt(i);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            if (i == tabHost.getCurrentTab()) {
                iv.setImageResource(TabDb.getTabsImgLight()[i]);
                tv.setTextColor(getResources().getColor(
                        R.color.buttombar_text_selected));
            } else {
                iv.setImageResource(TabDb.getTabsImg()[i]);
                tv.setTextColor(getResources().getColor(R.color.buttombar_text));
            }

        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("actionClearPersonMessage");
        registerReceiver(receiver, filter);
    }

    /**
     * 保存当前的聊天界面所对应的联系人、方便来消息屏蔽通知
     */
    private void setChattingContactId() {
        try {
            ECPreferences.savePreference(
                    ECPreferenceSettings.SETTING_CHATTING_CONTACTID, "", true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(
                getApplicationContext(), "ECSDK_Demo/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(1)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                // .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(CCPAppManager.md5FileNameGenerator)
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(
                        new UnlimitedDiscCache(cacheDir, null,
                                CCPAppManager.md5FileNameGenerator))// 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // .writeDebugLogs() // Remove for release app
                .build();// 开始构建
        ImageLoader.getInstance().init(config);
    }

    private void updateBadge() {
        ServiceManager.queryPersonMessage(1,
                new RequestListener<QueryPersonMessageResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            QueryPersonMessageResult result) {
                        if (result != null && result.isSuccesses()) {
                            View view = tabHost.getTabWidget().getChildAt(2);
                            TextView badge = (TextView) view
                                    .findViewById(R.id.badge);
                            if (result.getTotalCount() > 0) {
                                badge.setText("" + result.getTotalCount());
                                badge.setVisibility(View.VISIBLE);
                            } else {
                                badge.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        XGPushManager.onActivityStoped(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        BluetoothLeService.close();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
        MyApplication.instance.setSelectedLock(adapter.getItem(position));
    }

}
