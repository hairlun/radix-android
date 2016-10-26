/**
 * radix
 * EditPwdActivity
 * zhoushujie
 * 2016-10-13 下午4:15:20
 */
package com.patr.radix.ui.settings;

import com.patr.radix.R;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * @author zhoushujie
 *
 */
public class EditPwdActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private EditText oldPwdEt;
    
    private EditText newPwdEt;
    
    private EditText rePwdEt;
    
    private ImageView oldPwdIv;
    
    private ImageView newPwdIv;
    
    private ImageView rePwdIv;
    
    private Button submitBtn;
    
    private LoadingDialog loadingDialog;
    
    private boolean showOldPwd = false;
    
    private boolean showNewPwd = false;
    
    private boolean showRePwd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);
        context = this;
        initView();
    }
    
    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.edit_pwd_titlebar);
        oldPwdEt = (EditText) findViewById(R.id.old_pwd_et);
        newPwdEt = (EditText) findViewById(R.id.new_pwd_et);
        rePwdEt = (EditText) findViewById(R.id.re_pwd_et);
        oldPwdIv = (ImageView) findViewById(R.id.old_pwd_et_iv);
        newPwdIv = (ImageView) findViewById(R.id.new_pwd_et_iv);
        rePwdIv = (ImageView) findViewById(R.id.re_pwd_et_iv);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        
        titleBarView.setTitle("修改密码");
        oldPwdIv.setOnClickListener(this);
        newPwdIv.setOnClickListener(this);
        rePwdIv.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(context);
    }
    
    private void editPwd() {
        String oldPwd = oldPwdEt.getText().toString();
        String newPwd = newPwdEt.getText().toString();
        String rePwd = rePwdEt.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.showShort(context, "请输入原密码！");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtil.showShort(context, "请输入新密码！");
            return;
        }
        if (TextUtils.isEmpty(rePwd)) {
            ToastUtil.showShort(context, "请再次确认密码！");
            return;
        }
        if (!newPwd.equals(rePwd)) {
            ToastUtil.showShort(context, "两次输入的新密码不相同！");
            return;
        }
        ServiceManager.updateUserPwd(newPwd, oldPwd, new RequestListener<RequestResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在提交…");
            }

            @Override
            public void onSuccess(int stateCode, RequestResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        ToastUtil.showShort(context, "修改成功！");
                        finish();
                    } else {
                        ToastUtil.showShort(context, result.getRetinfo());
                    }
                } else {
                    ToastUtil.showShort(context, R.string.connect_exception);
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, R.string.connect_exception);
                loadingDialog.dismiss();
            }
            
        });
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.old_pwd_et_iv:
            showOldPwd = !showOldPwd;
            if (showOldPwd) {
                oldPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                oldPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            break;

        case R.id.new_pwd_et_iv:
            showNewPwd = !showNewPwd;
            if (showNewPwd) {
                newPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                newPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            break;

        case R.id.re_pwd_et_iv:
            showRePwd = !showRePwd;
            if (showRePwd) {
                rePwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                rePwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            break;

        case R.id.submit_btn:
            editPwd();
            break;
        }
    }
}
