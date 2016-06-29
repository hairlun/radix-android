package com.patr.radix;

import com.patr.radix.bean.LoginResult;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.LoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
    
    private Context context;
    
    private EditText accountEt;
    
    private EditText pwdEt;
    
    private Button loginBtn;
    
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
        accountEt = (EditText) findViewById(R.id.login_user_et);
        pwdEt = (EditText) findViewById(R.id.login_pwd_et);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
    }

    /* (non-Javadoc)
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
                login();
            }
            break;
        }
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
                        MyApplication.instance.setUserId(result.getUserid());
                        PrefUtil.save(context, "userId", result.getUserid());
                        MainActivity.startAfterLogin(context);
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
