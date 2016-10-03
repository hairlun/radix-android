package com.patr.radix;

import org.xutils.common.util.MD5;

import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditUserInfoActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private LinearLayout nameLl;

    private EditText nameEt;

    private LinearLayout pwdLl;

    private EditText pwdEt;

    private LinearLayout mobileLl;

    private EditText mobileEt;

    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        context = this;
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.user_info_titlebar);
        nameLl = (LinearLayout) findViewById(R.id.user_info_name_ll);
        nameEt = (EditText) findViewById(R.id.user_info_name_et);
        pwdLl = (LinearLayout) findViewById(R.id.user_info_pwd_ll);
        pwdEt = (EditText) findViewById(R.id.user_info_pwd_et);
        mobileLl = (LinearLayout) findViewById(R.id.user_info_mobile_ll);
        mobileEt = (EditText) findViewById(R.id.user_info_mobile_et);
        okBtn = (Button) findViewById(R.id.user_info_edit_btn);
        titleBarView.setTitle(R.string.titlebar_edit_user_info);
        nameLl.setOnClickListener(this);
        pwdLl.setOnClickListener(this);
        mobileLl.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    private void editUserInfo() {
        if (TextUtils.isEmpty(nameEt.getText())
                && TextUtils.isEmpty(pwdEt.getText())
                && TextUtils.isEmpty(mobileEt.getText())) {
            ToastUtil.showShort(context, "请至少修改一项！");
            return;
        }
        String name = nameEt.getText().toString().trim();
        String pwd = pwdEt.getText().toString().trim();
        if (!TextUtils.isEmpty(pwd)) {
            pwd = MD5.md5(pwd);
        }
        String mobile = mobileEt.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.user_info_name_ll:
            nameEt.setFocusable(true);
            nameEt.setFocusableInTouchMode(true);
            nameEt.requestFocus();
            break;
        case R.id.user_info_pwd_ll:
            pwdEt.setFocusable(true);
            pwdEt.setFocusableInTouchMode(true);
            pwdEt.requestFocus();
            break;
        case R.id.user_info_mobile_ll:
            mobileEt.setFocusable(true);
            mobileEt.setFocusableInTouchMode(true);
            mobileEt.requestFocus();
            break;
        case R.id.user_info_edit_btn:
            // 确定修改
            editUserInfo();
            break;
        }
    }

}
