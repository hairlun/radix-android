/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix;

import java.util.Calendar;
import java.util.Date;

import com.patr.radix.bean.RadixLock;
import com.patr.radix.utils.TimeUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.Utils;
import com.patr.radix.utils.qrcode.QRCodeUtil;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.picker.DatetimeDialog;
import com.patr.radix.view.picker.DatetimeDialog.OnConfirmListener;
import com.patr.radix.view.picker.DatetimePickerView.Type;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    
    private RelativeLayout keyEndTimeRl;
    
    private TextView keyEndTimeTv;
    
//    private LinearLayout keyActiveTimeLl;
//    
//    private EditText keyActiveTimeEt;
    
    private Button generateQrcodeBtn;
    
    private Calendar startCal;
    
    private Calendar endCal;
    
    private int timeType;

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
        keyEndTimeRl = (RelativeLayout) findViewById(R.id.unlock_end_rl);
        keyEndTimeTv = (TextView) findViewById(R.id.unlock_end_tv);
//        keyActiveTimeLl = (LinearLayout) findViewById(R.id.unlock_active_time_ll);
//        keyActiveTimeEt = (EditText) findViewById(R.id.unlock_active_time_et);
        generateQrcodeBtn = (Button) findViewById(R.id.unlock_generate_qrcode_btn);
        titleBarView.setTitle(R.string.titlebar_key_active_time);
        keyStartTimeRl.setOnClickListener(this);
        keyEndTimeRl.setOnClickListener(this);
//        keyActiveTimeLl.setOnClickListener(this);
        generateQrcodeBtn.setOnClickListener(this);
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
    }

    /**
     * 显示日期选择框
     */
    private void showDatePickerDialog(int timeType) {
        Type type = Type.DATETIME;
        DatetimeDialog picker;
        if (timeType == 0) {
            if (TextUtils.isEmpty(keyStartTimeTv.getText().toString().trim())) {
                startCal.setTimeInMillis(System.currentTimeMillis());
            }
            picker = new DatetimeDialog(context, startCal.getTime(),
                    type);
            picker.setOnConfirmListener(this);
            picker.show();
        } else {
            if (TextUtils.isEmpty(keyEndTimeTv.getText().toString().trim())) {
                endCal.setTimeInMillis(System.currentTimeMillis());
            }
            picker = new DatetimeDialog(context, endCal.getTime(),
                    type);
            picker.setOnConfirmListener(this);
            picker.show();
        }
        this.timeType = timeType;
    }

    /**
     * 设置日期对象
     * 
     * @param date
     */
    public void setDateTime(Date date) {
        if (date == null) {
            if (timeType == 0) {
                keyStartTimeTv.setText("");
                startCal.setTimeInMillis(System.currentTimeMillis());
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);
            } else {
                keyEndTimeTv.setText("");
                endCal.setTimeInMillis(System.currentTimeMillis());
                endCal.set(Calendar.SECOND, 0);
                endCal.set(Calendar.MILLISECOND, 0);
            }
            return;
        }
        if (timeType == 0) {
            startCal.setTime(date);
            keyStartTimeTv.setText(TimeUtil.getDateStr(startCal.getTime(), DATE_FORMAT));
        } else {
            endCal.setTime(date);
            keyEndTimeTv.setText(TimeUtil.getDateStr(endCal.getTime(), DATE_FORMAT));
        }
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unlock_start_rl:
            showDatePickerDialog(0);
            break;
//        case R.id.unlock_active_time_ll:
//            keyActiveTimeEt.setFocusable(true);
//            keyActiveTimeEt.setFocusableInTouchMode(true);
//            keyActiveTimeEt.requestFocus();
//            break;
        case R.id.unlock_end_rl:
            showDatePickerDialog(1);
            break;
        case R.id.unlock_generate_qrcode_btn:
            if (TextUtils.isEmpty(keyStartTimeTv.getText())) {
                ToastUtil.showShort(context, "请选择开始时间！");
                break;
            }
            if (TextUtils.isEmpty(keyEndTimeTv.getText())) {
                ToastUtil.showShort(context, "请选择截止时间！");
                break;
            }
//            if (TextUtils.isEmpty(keyActiveTimeEt.getText())) {
//                ToastUtil.showShort(context, "请选择有效时间！");
//                break;
//            }
            // 生成二维码
            try {
                String cmd = "31 ";
                String data = "00 00 00 00 " + MyApplication.instance.getCsn()
                        + Utils.ByteArraytoHex(Utils.dateTime2Bytes(startCal.getTime()))
                        + Utils.ByteArraytoHex(Utils.dateTime2Bytes(endCal.getTime()));
                for (RadixLock lock : MyApplication.instance.getSelectedLocks()) {
                    data += Utils.ByteArraytoHex(new byte[]{ (byte) Integer.parseInt(lock.getId()) });
                }
                String cmdData = Utils.getCmdData(cmd, data);
                byte[] array = Utils.hexStringToByteArray(cmdData.replace(" ", ""));
                String text = new String(data);
                Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(text, 300, 300);
                QRCodeActivity.start(context, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    @Override
    public void OnConfirm(Date date, String dateStr) {
        setDateTime(date);
    }
    
    public static void start(Context context) {
        Intent intent = new Intent(context, ActiveTimeActivity.class);
        context.startActivity(intent);
    }

}
