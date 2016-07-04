/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix;

import java.util.Calendar;
import java.util.Date;

import com.patr.radix.utils.TimeUtil;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.picker.DatetimeDialog;
import com.patr.radix.view.picker.DatetimeDialog.OnConfirmListener;
import com.patr.radix.view.picker.DatetimePickerView.Type;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author zhoushujie
 *
 */
public class ActiveTimeActivity extends Activity implements OnClickListener, OnConfirmListener {
    
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private RelativeLayout keyStartTimeRl;
    
    private TextView keyStartTimeTv;
    
    private LinearLayout keyActiveTimeLl;
    
    private EditText keyActiveTimeEt;
    
    private Button generateQrcodeBtn;
    
    private Calendar cal;

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
        keyStartTimeTv = (TextView) findViewById(R.id.unlock_start_tv);
        keyActiveTimeLl = (LinearLayout) findViewById(R.id.unlock_active_time_ll);
        keyActiveTimeEt = (EditText) findViewById(R.id.unlock_active_time_et);
        generateQrcodeBtn = (Button) findViewById(R.id.unlock_generate_qrcode_btn);
        titleBarView.setTitle(R.string.titlebar_key_active_time);
        keyStartTimeRl.setOnClickListener(this);
        keyActiveTimeLl.setOnClickListener(this);
        generateQrcodeBtn.setOnClickListener(this);
    }

    /**
     * 显示日期选择框
     */
    private void showDatePickerDialog() {
        Type type = Type.DATETIME;
        if (TextUtils.isEmpty(keyStartTimeTv.getText().toString().trim())) {
            cal.setTimeInMillis(System.currentTimeMillis());
        }
        DatetimeDialog picker = new DatetimeDialog(context, cal.getTime(),
                type);
        picker.setOnConfirmListener(this);
        picker.show();
    }

    /**
     * 设置日期对象
     * 
     * @param date
     */
    public void setDateTime(Date date) {
        if (date == null) {
            keyStartTimeTv.setText("");
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return;
        }
        cal.setTime(date);
        keyStartTimeTv.setText(TimeUtil.getDateStr(cal.getTime(), DATE_FORMAT));
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unlock_start_rl:
            showDatePickerDialog();
            break;
        case R.id.unlock_active_time_ll:
            keyActiveTimeEt.setFocusable(true);
            keyActiveTimeEt.setFocusableInTouchMode(true);
            keyActiveTimeEt.requestFocus();
            break;
        case R.id.unlock_generate_qrcode_btn:
            break;
        }
    }

    @Override
    public void OnConfirm(Date date, String dateStr) {
        setDateTime(date);
    }

}
