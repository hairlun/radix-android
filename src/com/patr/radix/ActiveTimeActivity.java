/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.patr.radix.bean.RadixLock;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.ui.view.picker.DatetimeDialog;
import com.patr.radix.ui.view.picker.DatetimeDialog.OnConfirmListener;
import com.patr.radix.ui.view.picker.DatetimePickerView.Type;
import com.patr.radix.utils.TimeUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.Utils;
import com.patr.radix.utils.qrcode.QRCodeUtil;
import com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

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
public class ActiveTimeActivity extends Activity implements OnClickListener,
        OnConfirmListener {

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private Context context;

    private TitleBarView titleBarView;

    private RelativeLayout keyStartTimeRl;

    private TextView keyStartTimeTv;

    private RelativeLayout keyEndTimeRl;

    private TextView keyEndTimeTv;

    // private LinearLayout keyActiveTimeLl;
    //
    // private EditText keyActiveTimeEt;

    private Button generateQrcodeBtn;

    private Calendar startCal;

    private Calendar endCal;

    private int timeType;

    private boolean isAfterIM;

    private String callNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_time);
        context = this;
        isAfterIM = getIntent().getBooleanExtra("IM", false);
        callNumber = getIntent().getStringExtra("callNumber");
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.unlock_active_time_titlebar);
        keyStartTimeRl = (RelativeLayout) findViewById(R.id.unlock_start_rl);
        keyStartTimeTv = (TextView) findViewById(R.id.unlock_start_tv);
        keyEndTimeRl = (RelativeLayout) findViewById(R.id.unlock_end_rl);
        keyEndTimeTv = (TextView) findViewById(R.id.unlock_end_tv);
        // keyActiveTimeLl = (LinearLayout)
        // findViewById(R.id.unlock_active_time_ll);
        // keyActiveTimeEt = (EditText)
        // findViewById(R.id.unlock_active_time_et);
        generateQrcodeBtn = (Button) findViewById(R.id.unlock_generate_qrcode_btn);
        titleBarView.setTitle(R.string.titlebar_key_active_time);
        keyStartTimeRl.setOnClickListener(this);
        keyEndTimeRl.setOnClickListener(this);
        // keyActiveTimeLl.setOnClickListener(this);
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
            picker = new DatetimeDialog(context, startCal.getTime(), type);
            picker.setOnConfirmListener(this);
            picker.show();
        } else {
            if (TextUtils.isEmpty(keyEndTimeTv.getText().toString().trim())) {
                endCal.setTimeInMillis(System.currentTimeMillis());
            }
            picker = new DatetimeDialog(context, endCal.getTime(), type);
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
            keyStartTimeTv.setText(TimeUtil.getDateStr(startCal.getTime(),
                    DATE_FORMAT));
        } else {
            endCal.setTime(date);
            keyEndTimeTv.setText(TimeUtil.getDateStr(endCal.getTime(),
                    DATE_FORMAT));
        }
    }

    /**
     * 处理文本发送方法事件通知
     * 
     * @param text
     */
    private void handleSendTextMessage(CharSequence text) {
        if (text == null || text.toString().trim().length() <= 0) {
            return;
        }
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        // 设置消息接收者
        msg.setTo(callNumber);
        // 创建一个文本消息体，并添加到消息对象中
        ECTextMessageBody msgBody = new ECTextMessageBody(text.toString());
        msg.setBody(msgBody);
        try {
            // 发送消息，该函数见上
            long rowId = -1;
            // if(mCustomerService) {
            // rowId = CustomerServiceHelper.sendMCMessage(msg);
            // } else {
            rowId = IMChattingHelper.sendECMessage(msg);
            // }
            // 通知列表刷新
            msg.setId(rowId);
            // notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendKey() {
        List<RadixLock> list = MyApplication.instance.getSelectedLocks();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unlock_start_rl:
            showDatePickerDialog(0);
            break;
        // case R.id.unlock_active_time_ll:
        // keyActiveTimeEt.setFocusable(true);
        // keyActiveTimeEt.setFocusableInTouchMode(true);
        // keyActiveTimeEt.requestFocus();
        // break;
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
            // if (TextUtils.isEmpty(keyActiveTimeEt.getText())) {
            // ToastUtil.showShort(context, "请选择有效时间！");
            // break;
            // }
            // 生成二维码
            try {
                String cmd = "71 ";
                String data = "00 00 00 00 "
                        + MyApplication.instance.getUserInfo().getCardNo()
                        + Utils.ByteArraytoHex(Utils.dateTime2Bytes(startCal
                                .getTime()))
                        + Utils.ByteArraytoHex(Utils.dateTime2Bytes(endCal
                                .getTime()));
                byte len = (byte) MyApplication.instance.getSelectedLocks()
                        .size();
                data += Utils.ByteArraytoHex(new byte[] { len });
                for (RadixLock lock : MyApplication.instance.getSelectedLocks()) {
                    data += Utils.ByteArraytoHex(new byte[] {
                            (byte) (lock.getCtrId() & 0xFF),
                            (byte) ((lock.getCtrId() >> 8) & 0xFF) });
                }
                String cmdData = Utils.getCmdData("00 ", cmd, data);
                byte[] array = Utils.getCmdDataByteArray(cmdData);
                String text = new String(array);
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
        intent.putExtra("IM", false);
        context.startActivity(intent);
    }

    public static void startAfterIM(Context context, String callNumber) {
        Intent intent = new Intent(context, ActiveTimeActivity.class);
        context.startActivity(intent);
        intent.putExtra("IM", true);
        intent.putExtra("callNumber", callNumber);
        context.startActivity(intent);
    }

}
