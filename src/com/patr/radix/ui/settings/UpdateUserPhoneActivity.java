/**
 * radix
 * UpdataUserPhoneActivity
 * zhoushujie
 * 2016-10-6 上午11:17:35
 */
package com.patr.radix.ui.settings;

import java.util.List;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author zhoushujie
 * 
 */
public class UpdateUserPhoneActivity extends Activity implements
        OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private EditText verifyEt;

    private EditText phoneEt;

    private Button verifyBtn;

    private Button submitBtn;

    private LoadingDialog loadingDialog;

    private Handler handler;

    private int countdown;

    private static final int RETRY_TIME = 60;

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            countdown--;
            if (countdown > 0) {
                verifyBtn.setText("获取验证码(" + countdown + ")");
                if (handler != null) {
                    handler.postDelayed(this, 1000);
                }
            } else {
                verifyBtn.setText("获取验证码");
                verifyBtn.setEnabled(true);
            }
        }
    };

    EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                // 回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    // 提交验证码成功
                    LogUtil.d("提交验证码成功。" + data.toString());
                    handler.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            updateUserPhone();
                        }
                    });
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    // 获取验证码成功
                    LogUtil.d("获取验证码成功。" + data.toString());
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    // 返回支持发送验证码的国家列表
                    LogUtil.d(data.toString());
                }
            } else {
                try {
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");// 错误描述
                    int status = object.optInt("status");// 错误代码
                    if (status > 0 && !TextUtils.isEmpty(des)) {
                        LogUtil.d(des);
                        return;
                    }
                } catch (Exception e) {
                    // do something
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_phone);
        context = this;
        initView();
        SMSSDK.registerEventHandler(eh); // 注册短信回调
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterEventHandler(eh);
        super.onDestroy();
    }

    /**
     * 
     */
    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.update_phone_titlebar);
        verifyEt = (EditText) findViewById(R.id.verification_et);
        phoneEt = (EditText) findViewById(R.id.phone_et);
        verifyBtn = (Button) findViewById(R.id.verify_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);

        verifyBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        titleBarView.setTitle("修改手机号");
        handler = new Handler();
        loadingDialog = new LoadingDialog(this);
    }

    private void verify() {
        String code = verifyEt.getText().toString().trim();
        if (!TextUtils.isEmpty(code)) {
            SMSSDK.submitVerificationCode("86", MyApplication.instance
                    .getUserInfo().getMobile(), code);
        }
    }

    private void updateUserPhone() {
        final String mobile = phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.showShort(this, "请输入新号码");
            return;
        }
        ServiceManager.updateUserPhone(mobile,
                new RequestListener<RequestResult>() {

                    @Override
                    public void onStart() {
                        loadingDialog.show("正在提交…");
                    }

                    @Override
                    public void onSuccess(int stateCode, RequestResult result) {
                        ToastUtil.showShort(context, "成功！");
                        loadingDialog.dismiss();
                        PrefUtil.save(context, Constants.PREF_MOBILE, mobile);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception error, String content) {
                        ToastUtil
                                .showShort(context, R.string.connect_exception);
                        loadingDialog.dismiss();
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.verify_btn:
            SMSSDK.getVerificationCode("86", MyApplication.instance
                    .getUserInfo().getMobile());
            countdown = RETRY_TIME;
            verifyBtn.setText("获取验证码(" + countdown + ")");
            verifyBtn.setEnabled(false);
            if (handler != null) {
                handler.postDelayed(runnable, 1000);
            }
            break;

        case R.id.submit_btn:
            verify();
            break;
        }
    }

}
