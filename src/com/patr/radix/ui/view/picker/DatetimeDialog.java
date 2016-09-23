package com.patr.radix.ui.view.picker;

import java.util.Calendar;
import java.util.Date;

import com.patr.radix.R;
import com.patr.radix.ui.view.dialog.AbsCustomDialog;
import com.patr.radix.ui.view.picker.DatetimePickerView.Type;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

/**
 * 正在加载的对话框
 * 
 * @author huangzhongwen
 * 
 */
public class DatetimeDialog extends AbsCustomDialog implements
        View.OnClickListener {

    private DatetimePickerView picker;

    private Button okBtn;

    private Button todayBtn;

    private Button cancelBtn;

    private OnConfirmListener onConfirmListener;

    private OnCancelListener onCancelListener;

    private Calendar cal = Calendar.getInstance();

    private Type type = Type.DATETIME;

    private String todayBtnText;
    private String okBtnText;
    private String cancelBtnText;

    public DatetimeDialog(Context context, Date date, Type type) {
        super(context);
        cal.setTime(date);
        this.type = type;
    }

    public DatetimeDialog(Context context, Date date) {
        this(context, date, Type.DATETIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        picker = (DatetimePickerView) findViewById(R.id.picker);
        picker.setType(type);
        okBtn = (Button) findViewById(R.id.ok_btn);
        todayBtn = (Button) findViewById(R.id.today_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        if (type == Type.TIME) {
            todayBtn.setText("现在");
        } else if (type == Type.YEAR_MONTH) {
            todayBtn.setText("本月");
        }
        if (!TextUtils.isEmpty(todayBtnText)) {
            todayBtn.setText(todayBtnText);
        }
        if (!TextUtils.isEmpty(okBtnText)) {
            okBtn.setText(okBtnText);
        }
        if (!TextUtils.isEmpty(cancelBtnText)) {
            cancelBtn.setText(cancelBtnText);
        }
    }

    @Override
    public void initData() {
        picker.set(cal.getTime());
    }

    @Override
    public void initListener() {
        okBtn.setOnClickListener(this);
        todayBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    // @Override
    // public int getType() {
    // return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    // }

    @Override
    public int getWindowAnimationsResId() {
        // return android.R.style.Animation_Dialog;
        return R.style.BottomDialogAnim;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.view_picker_dialog;
    }

    @Override
    public int getWidth() {
        return android.view.ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    public int getHeight() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getBackgroundDrawableResourceId() {
        // return R.drawable.list_item_bg_gray_first_normal;
        return super.getBackgroundDrawableResourceId();
    }

    @Override
    public boolean getCancelable() {
        return true;
    }

    @Override
    public boolean getCanceledOnTouchOutside() {
        return true;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok_btn:
            if (onConfirmListener != null) {
                onConfirmListener.OnConfirm(picker.getDate(),
                        picker.getDateStr());
            }
            dismiss();
            break;
        case R.id.today_btn:
            picker.set(System.currentTimeMillis());
            break;
        case R.id.cancel_btn:
            dismiss();
            if (onCancelListener != null) {
                onCancelListener.onCancel();
            }
            break;

        default:
            break;
        }
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public String getTodayBtnText() {
        return todayBtnText;
    }

    public String getOkBtnText() {
        return okBtnText;
    }

    public String getCancelBtnText() {
        return cancelBtnText;
    }

    public void setTodayBtnText(String todayBtnText) {
        this.todayBtnText = todayBtnText;
    }

    public void setOkBtnText(String okBtnText) {
        this.okBtnText = okBtnText;
    }

    public void setCancelBtnText(String cancelBtnText) {
        this.cancelBtnText = cancelBtnText;
    }

    public interface OnConfirmListener {

        void OnConfirm(Date date, String dateStr);
    }

    public interface OnCancelListener {

        void onCancel();
    }
}
