/**
 * radix
 * FeedbackActivity
 * zhoushujie
 * 2016-10-10 上午10:50:04
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author zhoushujie
 *
 */
public class FeedbackActivity extends Activity implements OnClickListener {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private EditText titleEt;
    
    private EditText contentEt;
    
    private TextView lenTv;
    
    private Button submitBtn;
    
    private LoadingDialog loadingDialog;
    
    private TextWatcher watcher = new TextWatcher() {
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            lenTv.setText("" + s.length());
            if (s.length() > 200) {
                lenTv.setTextColor(getResources().getColor(R.color.red));
            } else {
                lenTv.setTextColor(getResources().getColor(R.color.gray_text));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        context = this;
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.feedback_titlebar);
        titleEt = (EditText) findViewById(R.id.title_et);
        contentEt = (EditText) findViewById(R.id.content_et);
        lenTv = (TextView) findViewById(R.id.len_tv);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        titleBarView.setTitle("意见反馈");
        contentEt.addTextChangedListener(watcher);
        submitBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(context);
    }
    
    private void submit() {
        String title = titleEt.getText().toString().trim();
        String content = contentEt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.showShort(context, "请输入标题！");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(context, "请输入您宝贵的意见！");
            return;
        }
        ServiceManager.adviceFeedback(title, content, new RequestListener<RequestResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在提交…");
            }

            @Override
            public void onSuccess(int stateCode, RequestResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        ToastUtil.showShort(context, "提交成功");
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
        case R.id.submit_btn:
            submit();
            break;
        }
    }

}
