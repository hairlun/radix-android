package com.patr.radix.view;

import com.patr.radix.R;
import com.patr.radix.view.dialog.AbsCustomDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 列表选择的对话框
 * 
 * @author zhoushujie 2014-8-13 下午6:59:17
 */
public class ListSelectDialog extends AbsCustomDialog {

    private Button cancelBtn;
    private ListView selectLv;
    private TextView titleTv;

    private CharSequence title = "";
    private ListAdapter adapter;

    private OnItemClickListener onItemClickListener;

    private ListSelectDialog(Context context) {
        super(context);
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
        selectLv = (ListView) findViewById(R.id.select_lv);
        cancelBtn = (Button) findViewById(R.id.dialog_cancel);
    }

    /**
     * 显示数据
     */
    @Override
    public void initData() {
        titleTv.setText(title);
        selectLv.setAdapter(adapter);
    }

    // @Override
    // public int getType() {
    // return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    // }

    @Override
    public int getLayoutResID() {
        return R.layout.view_dialog_selectable;
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
        return Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
    }

    @Override
    public void initListener() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        selectLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }
                dismiss();
            }
        });
    }

    /**
     * 设置标题
     * 
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        this.title = title == null ? "" : title;
    }

    /**
     * 设置适配器
     * 
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        if (adapter == null) {
            return;
        }
        this.adapter = adapter;
    }

    /**
     * 
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 
     * @param context
     * @param title
     * @param adapter
     * @param l
     */
    public static void show(Context context, CharSequence title,
            ListAdapter adapter, OnItemClickListener l) {
        ListSelectDialog dialog = new ListSelectDialog(context);
        dialog.setTitle(title);
        dialog.setAdapter(adapter);
        dialog.setOnItemClickListener(l);
        dialog.show();
    }
}
