package com.patr.radix;

import java.io.File;
import java.io.InvalidClassException;

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
                App.instance.setBadge(0);
                View view = tabHost.getTabWidget().getChildAt(2);
                ImageView badge = (ImageView) view.findViewById(R.id.badge);
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
        adapter = new KeyListAdapter2(this, App.instance.getLocks());
        updateBadge();
        
//        // 云通讯
//        ECContentObservers.getInstance().initContentObserver();
//        CrashHandler.getInstance().setContext(this);

        // 信鸽注册
        // 开启logcat输出，方便debug，发布时请关闭
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
        // XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        XGPushConfig.enableDebug(this, App.DEBUG);
        final Context context = getApplicationContext();
        XGPushManager.registerPush(this, new XGIOperateCallback() {

            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
                // 保存pushToken到本地
                App.instance.setPushToken((String) data);
                if (!PrefUtil.getBoolean(context, Constants.PREF_PUSH_SWITCH,
                        true)) {
                    XGPushManager.unregisterPush(getApplicationContext(),
                            new XGIOperateCallback() {
                                @Override
                                public void onSuccess(Object data, int flag) {
                                    Log.d("TPush", "反注册成功");
                                }

                                @Override
                                public void onFail(Object data, int errCode,
                                        String msg) {
                                    Log.d("TPush", "反注册失败，错误码：" + errCode
                                            + ",错误信息：" + msg);
                                }
                            });
                }
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
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
        tv.setText(TabDb.getTabsTxt()[idx]);
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

    private void updateBadge() {
        View view = tabHost.getTabWidget().getChildAt(2);
        ImageView badge = (ImageView) view.findViewById(R.id.badge);
        if (App.instance.getBadge() > 0) {
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        App.instance.setSelectedLock(adapter.getItem(position));
    }

}
