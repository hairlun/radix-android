package com.patr.radix;

import com.patr.radix.bean.LoginResult;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECInitParams.LoginMode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private ImageView logoIv;

    private EditText accountEt;

    private EditText pwdEt;

    private Button loginBtn;
    
    private Button forgetPwdBtn;

    private String account;

    private String pwd;

    /** 等待框 */
    public LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.login_titlebar);
        logoIv = (ImageView) findViewById(R.id.login_logo_iv);
        accountEt = (EditText) findViewById(R.id.login_user_et);
        pwdEt = (EditText) findViewById(R.id.login_pwd_et);
        loginBtn = (Button) findViewById(R.id.login_btn);
        forgetPwdBtn = (Button) findViewById(R.id.forget_pwd_btn);
        titleBarView.setTitle(R.string.titlebar_login);
        loginBtn.setOnClickListener(this);
        forgetPwdBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_btn:
            account = accountEt.getText().toString().trim();
            pwd = pwdEt.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                ToastUtil.showLong(context, "用户名不能为空，请重新输入!");
            } else if (TextUtils.isEmpty(pwd)) {
                ToastUtil.showLong(context, "密码不能为空，请重新输入!");
            } else {
                // if (MyApplication.DEBUG) {
                // testLogin();
                // } else {
                login();
                // }
            }
            break;
        }
    }

    private void testLogin() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount("admin");
        userInfo.setName("admin");
        userInfo.setId("1");
        userInfo.setAreaId("5");
        userInfo.setAreaName("物业部");
        userInfo.setHome("0508");
        userInfo.setMobile("88888888");
        MyApplication.instance.setUserInfo(userInfo);
        PrefUtil.saveUserInfo(context, userInfo);
        // MainActivity.startAfterLogin(context);
        finish();
    }

    private void login() {
        // 判断是否有网络
        if (NetUtils.isNetConnected(context)) {
            accountLogin();
        } else {
            NetUtils.showDisconnectMsg(context);
        }
    }

    private void accountLogin() {
        // 创建回调对象
        RequestListener<LoginResult> callback = new RequestListener<LoginResult>() {

            @Override
            public void onStart() {
                loadingDialogShow();
            }

            @Override
            public void onSuccess(int statusCode, LoginResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        MyApplication.instance
                                .setUserInfo(result.getUserInfo());
                        PrefUtil.saveUserInfo(context, result.getUserInfo());
                        // 初始化和登录云通讯账号
                        String appKey = FileAccessor.getAppKey();
                        String token = FileAccessor.getAppToken();
                        String myMobile = MyApplication.instance.getUserInfo()
                                .getMobile();
                        String pass = "";
                        ClientUser clientUser = new ClientUser(myMobile);
                        clientUser.setAppKey(appKey);
                        clientUser.setAppToken(token);
                        clientUser.setLoginAuthType(LoginAuthType.NORMAL_AUTH);
                        clientUser.setPassword(pass);
                        CCPAppManager.setClientUser(clientUser);
                        SDKCoreHelper.init(context, LoginMode.FORCE_LOGIN);

                        finish();
                    } else {
                        ToastUtil.showLong(context, result.getRetinfo());
                    }
                } else {
                    ToastUtil.showShort(context, R.string.connect_exception);
                }
            }

            @Override
            public void onFailure(Exception e, String error) {
                ToastUtil.showShort(context, R.string.connect_exception);
            }

            @Override
            public void onStopped() {
                loadingDialogDismiss();
            }
        };
        ServiceManager.login(account, pwd, callback);
    }

    /**
     * 释放登录等待框
     */
    private void loadingDialogDismiss() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 显示登录等待框
     */
    private void loadingDialogShow() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show("正在核实…");
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}
