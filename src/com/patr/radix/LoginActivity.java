package com.patr.radix;

import com.patr.radix.utils.NetUtils;
import com.patr.radix.view.LoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                Toast.makeText(context, "用户名不能为空，请重新输入!", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(pwd)) {
                Toast.makeText(context, "密码不能为空，请重新输入!", Toast.LENGTH_LONG).show();
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
        
    }

}
