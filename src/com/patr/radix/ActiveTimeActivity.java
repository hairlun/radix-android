/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix;

import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author zhoushujie
 *
 */
public class ActiveTimeActivity extends Activity implements OnClickListener {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private RelativeLayout keyStartTimeRl;
    
    private LinearLayout keyActiveTimeLl;
    
    private EditText keyActiveTimeEt;
    
    private Button generateQrcodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_time);
        context = this;
        initView();
    }
    
    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.unlock_active_time_titlebar);
        keyStartTimeRl = (RelativeLayout) findViewById(R.id.unlock_start_rl);
        keyActiveTimeLl = (LinearLayout) findViewById(R.id.unlock_active_time_ll);
        keyActiveTimeEt = (EditText) findViewById(R.id.unlock_active_time_et);
        generateQrcodeBtn = (Button) findViewById(R.id.unlock_generate_qrcode_btn);
        titleBarView.setTitle(R.string.titlebar_key_active_time);
        keyStartTimeRl.setOnClickListener(this);
        keyActiveTimeLl.setOnClickListener(this);
        generateQrcodeBtn.setOnClickListener(this);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unlock_start_rl:
            break;
        case R.id.unlock_active_time_ll:
            break;
        case R.id.unlock_generate_qrcode_btn:
            break;
        }
    }

}
