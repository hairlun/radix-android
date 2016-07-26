package com.patr.radix.view.dialog;

import com.patr.radix.R;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 自定义消息提示的对话框
 * 
 * @author huangzhongwen
 * 
 */
public class MsgDialog extends AbsCustomDialog {

    private TextView titleTv;
    private TextView messageTv;
    private Button btn1;
    private Button btn2;
    private Button btn3;

    private String title;
    private String message;
    private String btn1Text;
    private String btn2Text;
    private String btn3Text;
    private View.OnClickListener btn1Listener;
    private View.OnClickListener btn2Listener;
    private View.OnClickListener btn3Listener;
    /**
     * 按钮类型
     */
    private BtnType type;

    /**
     * 按钮类型
     * 
     * @author huangzhongwen 2014-8-20 下午4:04:12
     */
    public enum BtnType {
        /**
         * 一个按钮
         */
        ONE,
        /**
         * 两个按钮
         */
        TWO,
        /**
         * 三个按钮
         */
        THREE;
    }

    public MsgDialog(Context context, BtnType type) {
        super(context);
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化View
     */
    @Override
    public void initView() {
        titleTv = (TextView) findViewById(R.id.dialog_title_tv);
        messageTv = (TextView) findViewById(R.id.dialog_msg_tv);
        btn1 = (Button) findViewById(R.id.dialog_btn1);
        btn2 = (Button) findViewById(R.id.dialog_btn2);
        btn3 = (Button) findViewById(R.id.dialog_btn3);

        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        switch (type) {
        case THREE:
            btn3.setVisibility(View.VISIBLE);
        case TWO:
            btn2.setVisibility(View.VISIBLE);
        case ONE:
            btn1.setVisibility(View.VISIBLE);
            break;

        default:
            break;
        }
    }

    /**
     * 显示数据
     */
    @Override
    public void initData() {
        titleTv.setText(title == null ? "Title" : title);
        messageTv.setText(message == null ? "Message" : message);
    }

    /**
     * 设置标题
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置内容
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 设置按钮1
     * 
     * @param text
     * @param listener
     */
    public void setButton1(String text, View.OnClickListener listener) {
        this.btn1Text = text;
        this.btn1Listener = listener;
    }

    public void setButton1(String text) {
        this.btn1Text = text;
    }

    /**
     * 设置按钮2
     * 
     * @param type
     */
    public void setButton2(String text, View.OnClickListener listener) {
        this.btn2Text = text;
        this.btn2Listener = listener;
    }

    public void setButton2(String text) {
        this.btn2Text = text;
    }

    /**
     * 设置按钮3
     * 
     * @param type
     */
    public void setButton3(String text, View.OnClickListener listener) {
        this.btn3Text = text;
        this.btn3Listener = listener;
    }

    public void setButton3(String text) {
        this.btn3Text = text;
    }

    // @Override
    // public int getType() {
    // return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    // }

    @Override
    public int getLayoutResID() {
        return R.layout.view_dialog;
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
        return Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
    }

    @Override
    public void initListener() {
        btn1.setText(btn1Text == null ? "Button1" : btn1Text);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (btn1Listener != null) {
                    btn1Listener.onClick(v);
                }
            }
        });
        btn2.setText(btn2Text == null ? "Button2" : btn2Text);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (btn2Listener != null) {
                    btn2Listener.onClick(v);
                }
            }
        });
        btn3.setText(btn3Text == null ? "Button3" : btn3Text);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (btn3Listener != null) {
                    btn3Listener.onClick(v);
                }
            }
        });
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param btn1
     * @param listener1
     * @param btn2
     * @param listener2
     */
    public static void show(Context context, String title, String content,
            String btn1, View.OnClickListener listener1, String btn2,
            View.OnClickListener listener2) {
        MsgDialog dialog = new MsgDialog(context, BtnType.TWO);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1(btn1, listener1);
        dialog.setButton2(btn2, listener2);
        dialog.show();
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param btn1
     * @param listener1
     * @param type
     */
    public static MsgDialog show(Context context, String title, String content,
            String btn1, View.OnClickListener listener1, BtnType type) {
        MsgDialog dialog = new MsgDialog(context, type);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1(btn1, listener1);
        dialog.setButton2("取消", null);
        dialog.show();
        return dialog;
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param listener1
     * @param listener2
     */
    public static MsgDialog show(Context context, String title, String content,
            View.OnClickListener listener1, View.OnClickListener listener2) {
        MsgDialog dialog = new MsgDialog(context, BtnType.TWO);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1("确定", listener1);
        dialog.setButton2("取消", listener2);
        dialog.show();
        return dialog;
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param button1
     * @param button2
     * @param button3
     * @param listener1
     * @param listener2
     * @param listener3
     * @return
     */
    public static MsgDialog show(Context context, String title, String content,
            String button1, String button2, String button3,
            View.OnClickListener listener1, View.OnClickListener listener2,
            View.OnClickListener listener3) {
        MsgDialog dialog = new MsgDialog(context, BtnType.THREE);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1(button1, listener1);
        dialog.setButton2(button2, listener2);
        dialog.setButton3(button3, listener3);
        dialog.show();
        return dialog;
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param listener
     * @param type
     */
    public static MsgDialog show(Context context, String title, String content,
            BtnType type, View.OnClickListener... listener) {
        MsgDialog dialog = new MsgDialog(context, type);
        dialog.setTitle(title);
        dialog.setMessage(content);
        if (listener == null || listener.length == 0) {
            dialog.setButton1("确定", null);
            dialog.setButton2("取消", null);
            dialog.setButton3("忽略", null);
        } else {
            switch (listener.length) {
            case 3:
                dialog.setButton3("忽略", listener[2]);
            case 2:
                dialog.setButton2("取消", listener[1]);
            case 1:
                dialog.setButton1("确定", listener[0]);
                break;

            default:
                break;
            }
        }
        dialog.show();
        return dialog;
    }

    /**
     * 
     * @param context
     * @param title
     * @param content
     * @param type
     * @param button
     * @return
     */
    public static MsgDialog show(Context context, String title, String content,
            BtnType type, String... button) {
        MsgDialog dialog = new MsgDialog(context, type);
        dialog.setTitle(title);
        dialog.setMessage(content);
        if (button == null || button.length == 0) {
            dialog.setButton1("确定", null);
            dialog.setButton2("取消", null);
            dialog.setButton3("忽略", null);
        } else {
            switch (button.length) {
            case 3:
                dialog.setButton3(button[2], null);
            case 2:
                dialog.setButton2(button[1], null);
            case 1:
                dialog.setButton1(button[0], null);
                break;

            default:
                break;
            }
        }
        dialog.show();
        return dialog;
    }

    public static MsgDialog show(Context context, String title, String content,
            View.OnClickListener onClickListener, BtnType type) {
        MsgDialog dialog = new MsgDialog(context, type);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1("确定", onClickListener);
        dialog.setButton2("取消", null);
        dialog.setButton3("忽略", null);
        dialog.show();
        return dialog;
    }

    public static MsgDialog show(Context context, String title, String content,
            BtnType type) {
        MsgDialog dialog = new MsgDialog(context, type);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton1("确定", null);
        dialog.setButton2("取消", null);
        dialog.setButton3("忽略", null);
        dialog.show();
        return dialog;
    }
}
