package com.patr.radix;


import com.patr.radix.utils.TabDb;
import com.yuntongxun.ecdemo.common.ECContentObservers;
import com.yuntongxun.ecdemo.common.utils.CrashHandler;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class MainActivity extends FragmentActivity implements
        OnTabChangeListener {

    private FragmentTabHost tabHost;

    private boolean isAfterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAfterLogin = getIntent().getBooleanExtra("login", false);
        tabHost = (FragmentTabHost) super.findViewById(android.R.id.tabhost);
        tabHost.setup(this, super.getSupportFragmentManager(),
                R.id.contentLayout);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(this);
        initTab();

        // 云通讯初始化
        ECContentObservers.getInstance().initContentObserver();
        CrashHandler.getInstance().setContext(this);
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
        if (isAfterLogin) {
            defaultTab = 3;
        }
        if (idx == defaultTab) {
            ((ImageView) view.findViewById(R.id.ivImg)).setImageResource(TabDb
                    .getTabsImgLight()[idx]);
        } else {
            ((ImageView) view.findViewById(R.id.ivImg)).setImageResource(TabDb
                    .getTabsImg()[idx]);
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
            ImageView iv = (ImageView) view.findViewById(R.id.ivImg);
            if (i == tabHost.getCurrentTab()) {
                iv.setImageResource(TabDb.getTabsImgLight()[i]);
            } else {
                iv.setImageResource(TabDb.getTabsImg()[i]);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void startAfterLogin(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("login", true);
        context.startActivity(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("login", false);
        context.startActivity(intent);
    }

}
