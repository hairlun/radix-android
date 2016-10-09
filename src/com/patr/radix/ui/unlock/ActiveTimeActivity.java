/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix.ui.unlock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.patr.radix.MainActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.R.id;
import com.patr.radix.R.layout;
import com.patr.radix.R.string;
import com.patr.radix.bean.AddVisitorResult;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
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
import android.os.Parcel;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan.Standard;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText nameEt;

    private EditText telEt;

    private EditText remarkEt;

    private Button generateQrcodeBtn;

    private LoadingDialog loadingDialog;

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
        nameEt = (EditText) findViewById(R.id.name_et);
        telEt = (EditText) findViewById(R.id.tel_et);
        remarkEt = (EditText) findViewById(R.id.remark_et);
        generateQrcodeBtn = (Button) findViewById(R.id.unlock_generate_qrcode_btn);

        titleBarView.setTitle("生成二维码");
        titleBarView.showCloseBtn();
        titleBarView.setOnCloseClickListener(this);
        keyStartTimeRl.setOnClickListener(this);
        keyEndTimeRl.setOnClickListener(this);
        generateQrcodeBtn.setOnClickListener(this);
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        loadingDialog = new LoadingDialog(context);
//        Parcel p = Parcel.obtain();
//        p.writeInt(20);
//        p.writeInt(0);
//        p.setDataPosition(0);
//        Standard lms = new Standard(40, 0);
//        Spann
//        remarkEt.setText(text)
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

    private void addVisitor() {
        String visitorName = nameEt.getText().toString().trim();
        String phoneNum = telEt.getText().toString().trim();
        String remark = remarkEt.getText().toString().trim();
        String startTime = keyStartTimeTv.getText().toString();
        String endTime = keyEndTimeTv.getText().toString();
        ServiceManager.mobileAddVisitor(visitorName, startTime, endTime,
                phoneNum, remark, new RequestListener<AddVisitorResult>() {

                    @Override
                    public void onStart() {
                        loadingDialog.show("正在提交数据…");
                    }

                    @Override
                    public void onSuccess(int stateCode, AddVisitorResult result) {
                        if (result != null && result.isSuccesses()) {
                            loadingDialog.show("正在生成二维码…");
                            String visitorId = result.getVisitorId();
                            generateQRCode(visitorId);
                        } else {
                            loadingDialog.dismiss();
                            ToastUtil.showShort(context, "访问预约失败。");
                        }
                    }

                    @Override
                    public void onFailure(Exception error, String content) {
                        loadingDialog.dismiss();
                        ToastUtil.showShort(context, "访问预约失败。");
                    }

                });
    }

    private void generateQRCode(String visitorId) {
        // 生成二维码
        visitorId = visitorId.replaceAll(" ", "");
        visitorId = Utils.ByteArraytoHex(Utils.hexStringToByteArray(visitorId));
        try {
            String cmd = "71 ";
            String data = "10 "
                    + visitorId
                    + Utils.ByteArraytoHex(Utils.dateTime2Bytes(startCal
                            .getTime()))
                    + Utils.ByteArraytoHex(Utils.dateTime2Bytes(endCal
                            .getTime()))
                    + MyApplication.instance.getUserInfo().getCardNo();
            byte len = (byte) MyApplication.instance.getSelectedLocks().size();
            data += Utils.ByteArraytoHex(new byte[] { len });
            for (RadixLock lock : MyApplication.instance.getSelectedLocks()) {
                data += Utils.ByteArraytoHex(new byte[] {
                        (byte) (lock.getCtrId() & 0xFF),
                        (byte) ((lock.getCtrId() >> 8) & 0xFF) });
            }
            String cmdData = Utils.getCmdData("00 ", cmd, data);
            byte[] array = Utils.getCmdDataByteArray(cmdData);
            String text = new String(array, "ISO8859-1");
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(text, 300, 300);
            loadingDialog.dismiss();
            QRCodeActivity.start(context, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        case R.id.unlock_end_rl:
            showDatePickerDialog(1);
            break;
        case R.id.titlebar_close_btn:
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            break;
        case R.id.unlock_generate_qrcode_btn:
            if (TextUtils.isEmpty(keyStartTimeTv.getText().toString())) {
                ToastUtil.showShort(context, "请选择开始时间！");
                break;
            }
            if (TextUtils.isEmpty(keyEndTimeTv.getText().toString())) {
                ToastUtil.showShort(context, "请选择截止时间！");
                break;
            }
            if (TextUtils.isEmpty(nameEt.getText().toString().trim())) {
                ToastUtil.showShort(context, "请输入拜访人姓名！");
                break;
            }
            // 访客预约
            addVisitor();
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
