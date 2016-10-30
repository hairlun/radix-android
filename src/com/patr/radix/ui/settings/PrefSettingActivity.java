/**
 * radix
 * PrefSettingActivity
 * zhoushujie
 * 2016-10-6 下午7:11:26
 */
package com.patr.radix.ui.settings;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.LoginActivity;
import com.patr.radix.App;
import com.patr.radix.R;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.ui.view.dialog.MsgDialog;
import com.patr.radix.ui.view.dialog.MsgDialog.BtnType;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author zhoushujie
 * 
 */
public class PrefSettingActivity extends Activity implements OnClickListener {

    Context context;

    private TitleBarView titleBarView;

    private LinearLayout pushLl;

    private LinearLayout shakeLl;

    private ImageView pushSwitchIv;

    private ImageView shakeSwitchIv;

    private Button logoutBtn;
    
    private LoadingDialog loadingDialog;
    
    private Handler handler;

    private boolean pushEnabled;

    private boolean shakeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_setting);
        context = this;
        initView();
        handler = new Handler();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.pref_setting_titlebar);
        pushLl = (LinearLayout) findViewById(R.id.push_ll);
        shakeLl = (LinearLayout) findViewById(R.id.shake_ll);
        pushSwitchIv = (ImageView) findViewById(R.id.push_iv);
        shakeSwitchIv = (ImageView) findViewById(R.id.shake_iv);
        logoutBtn = (Button) findViewById(R.id.logout_btn);

        pushLl.setOnClickListener(this);
        shakeLl.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        titleBarView.setTitle("偏好设置");
        loadingDialog = new LoadingDialog(context);
        pushEnabled = PrefUtil.getBoolean(context, Constants.PREF_PUSH_SWITCH,
                true);
        shakeEnabled = PrefUtil.getBoolean(context,
                Constants.PREF_SHAKE_SWITCH, true);
        if (pushEnabled) {
            pushSwitchIv.setImageResource(R.drawable.switch_on);
        } else {
            pushSwitchIv.setImageResource(R.drawable.switch_off);
        }
        if (shakeEnabled) {
            shakeSwitchIv.setImageResource(R.drawable.switch_on);
        } else {
            shakeSwitchIv.setImageResource(R.drawable.switch_off);
        }
        refresh();
    }

    private void login() {
        LoginActivity.start(context);
    }

    private void logout() {
        MsgDialog.show(context, "确认", "确定要退出当前账号吗？", "确定",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        loadingDialog.show("正在退出…");
                        UserInfo userInfo = new UserInfo();
                        App.instance.setUserInfo(userInfo);
                        PrefUtil.saveUserInfo(context, userInfo);
                        App.instance.clearCache();
                        App.instance.setMyMobile(App.instance.getVisitorId());
                        refresh();
                        loadingDialog.dismiss();
//                        // 注销云通讯
//                        CCPAppManager.setClientUser(null);
//                        ECDevice.unInitial();
//                        
//                        handler.postDelayed(new Runnable() {
//                            
//                            @Override
//                            public void run() {
//                                // 以访客id重新登录云通讯
//                                String appKey = FileAccessor.getAppKey();
//                                String token = FileAccessor.getAppToken();
//                                String myMobile = App.instance.getMyMobile();
//                                String pass = "";
//                                ClientUser clientUser = new ClientUser(myMobile);
//                                clientUser.setAppKey(appKey);
//                                clientUser.setAppToken(token);
//                                clientUser.setLoginAuthType(LoginAuthType.NORMAL_AUTH);
//                                clientUser.setPassword(pass);
//                                CCPAppManager.setClientUser(clientUser);
//                                SDKCoreHelper.init(getApplicationContext(), LoginMode.FORCE_LOGIN);
//                                refresh();
//                                loadingDialog.dismiss();
//                            }
//                        }, 1500);
                    }
                }, BtnType.TWO);
    }

    private void refresh() {
        if (TextUtils.isEmpty(App.instance.getUserInfo().getToken())) {
            logoutBtn.setText("登录");
        } else {
            logoutBtn.setText("退出登录");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onDestroy() {
        boolean oldPushEnabled = PrefUtil.getBoolean(context, Constants.PREF_PUSH_SWITCH, true);
        if (oldPushEnabled != pushEnabled) {
            if (pushEnabled) {
                XGPushManager.registerPush(context,
                        new XGIOperateCallback() {

                            @Override
                            public void onSuccess(Object data,
                                    int flag) {
                                Log.d("TPush", "注册成功，设备token为："
                                        + data);
                                // 保存pushToken到本地
                                App.instance
                                        .setPushToken((String) data);
                            }

                            @Override
                            public void onFail(Object data,
                                    int errCode, String msg) {
                                Log.d("TPush", "注册失败，错误码："
                                        + errCode + ",错误信息：" + msg);
                            }
                        });
            } else {
                XGPushManager.unregisterPush(getApplicationContext(),
                        new XGIOperateCallback() {
                            @Override
                            public void onSuccess(Object data, int flag) {
                                Log.d("TPush", "反注册成功");
                            }

                            @Override
                            public void onFail(Object data, int errCode, String msg) {
                                Log.d("TPush", "反注册失败，错误码：" + errCode + ",错误信息："
                                        + msg);
                            }
                });
            }
        }
        PrefUtil.save(context, Constants.PREF_PUSH_SWITCH, pushEnabled);
        PrefUtil.save(context, Constants.PREF_SHAKE_SWITCH, shakeEnabled);
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.push_ll:
            pushEnabled = !pushEnabled;
            if (pushEnabled) {
                pushSwitchIv.setImageResource(R.drawable.switch_on);
            } else {
                pushSwitchIv.setImageResource(R.drawable.switch_off);
            }
            break;

        case R.id.shake_ll:
            shakeEnabled = !shakeEnabled;
            if (shakeEnabled) {
                shakeSwitchIv.setImageResource(R.drawable.switch_on);
            } else {
                shakeSwitchIv.setImageResource(R.drawable.switch_off);
            }
            break;

        case R.id.logout_btn:
            if (TextUtils.isEmpty(App.instance.getUserInfo()
                    .getAccount())) {
                login();
            } else {
                logout();
            }
            break;
        }
    }

}
