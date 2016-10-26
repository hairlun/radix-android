package com.patr.radix.ui.view;

import com.patr.radix.R;
import com.patr.radix.ui.view.dialog.AbsCustomDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 正在加载的对话框
 * 
 * @author huangzhongwen
 * 
 */
public class LoadingDialog extends AbsCustomDialog implements
        android.view.View.OnClickListener {

    /**
     * 内容
     */
    private TextView msgTv;

    /**
     * 取消按钮
     */
    private ImageButton cancelBtn;

    private String msg;

    /**
     * 是否显示取消按钮
     */
    private boolean isCancelBtnShown = false;

    private android.view.View.OnClickListener onClickListener;

    private TextView lineTv;

    public LoadingDialog(Context context) {
        this(context, "");
    }

    public LoadingDialog(Context context, String msg) {
        super(context);
        this.msg = msg;
    }

    public LoadingDialog(Context context, int resId) {
        super(context);
        this.msg = context.getString(resId);
    }

    public LoadingDialog(Context context, int resId, Object... formatArgs) {
        super(context);
        this.msg = context.getString(resId, formatArgs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        msgTv = (TextView) findViewById(R.id.loading_msg_tv);
        lineTv = (TextView) findViewById(R.id.loading_line_tv);
        cancelBtn = (ImageButton) findViewById(R.id.loading_btn);
        if (isCancelBtnShown) {
            lineTv.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            lineTv.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        msgTv.setText(msg);
    }

    @Override
    public void initListener() {
        cancelBtn.setOnClickListener(this);
    }

    // @Override
    // public int getType() {
    // return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    // }

    @Override
    public int getWindowAnimationsResId() {
        return android.R.style.Animation_Dialog;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.view_loading_dialog;
    }

    @Override
    public int getWidth() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getHeight() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean getCancelable() {
        return false;
    }

    @Override
    public boolean getCanceledOnTouchOutside() {
        return false;
    }

    @Override
    public boolean getDimEnabled() {
        return false;
    }

    /**
     * 设置显示文本
     * 
     * @param msg
     */
    public void setMessage(String msg) {
        this.msg = msg;
        if (msgTv != null) {
            msgTv.setText(this.msg);
        }
    }

    /**
     * 
     * @param resId
     */
    public void setMessage(int resId) {
        setMessage(getContext().getString(resId));
    }

    /**
     * 
     * @param msg
     */
    public void show(String msg) {
        show(msg, false);
    }

    /**
     * 
     * @param msg
     * @param isCancelBtnShown
     *            是否显示取消按钮
     */
    public void show(String msg, boolean isCancelBtnShown) {
        try {
            if (!isShowing()) {
                super.show();
            }
            this.msg = msg;
            if (msgTv != null) {
                msgTv.setText(this.msg);
            }
            setCancelBtnShown(isCancelBtnShown);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param resId
     */
    public void show(int resId) {
        show(getContext().getString(resId), false);
    }

    /**
     * 
     * @param resId
     * @param isCancelBtnShown
     *            是否显示取消按钮
     */
    public void show(int resId, boolean isCancelBtnShown) {
        show(getContext().getString(resId), isCancelBtnShown);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.loading_btn:
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
            dismiss();
            break;

        default:
            break;
        }
    }

    /**
     * 取消按钮监听器
     * 
     * @return
     */
    public android.view.View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    /**
     * 取消按钮监听器
     * 
     * @param onClickListener
     */
    public void setOnClickListener(
            android.view.View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void setCancelBtnShown(boolean isCancelBtnShown) {
        this.isCancelBtnShown = isCancelBtnShown;
        if (isCancelBtnShown) {
            lineTv.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            lineTv.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }
    }
}
