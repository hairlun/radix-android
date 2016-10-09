/**
 * radix
 * TitleBarView
 * zhoushujie
 * 2016-6-13 上午11:03:47
 */
package com.patr.radix.ui.view;

import com.patr.radix.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author zhoushujie
 * 
 */
public class TitleBarView extends LinearLayout implements OnClickListener {

    public static final String TAG = TitleBarView.class.getName();

    private Context context;

    /** 返回按钮 */
    private ImageButton mBackBtn;

    /** 关闭按钮 */
    private Button mCloseBtn;

    /** 标题 */
    private TextView mTitleTv;

    private String title;

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        initListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_titlebar, this);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mTitleTv.setClickable(true);
        mTitleTv.setText(title);
        mBackBtn = (ImageButton) findViewById(R.id.titlebar_back_btn);
        mCloseBtn = (Button) findViewById(R.id.titlebar_close_btn);

        hideCloseBtn();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mTitleTv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    /**
     * 设置显示标题
     * 
     * @param title
     */
    public TitleBarView setTitle(String title) {
        if (title == null) {
            title = "";
        }
        this.title = title;
        // mTitleTv.setText(Html.fromHtml("<b>" + title + "</b>"));
        mTitleTv.setText(title);
        return this;
    }

    /**
     * 设置标题
     * 
     * @param resId
     *            string资源ID
     */
    public TitleBarView setTitle(int resId) {
        title = getContext().getString(resId);
        // mTitleTv.setText(Html.fromHtml("<b>" + title + "</b>"));
        mTitleTv.setText(title);
        return this;
    }

    /**
     * 获取标题TextView对象
     * 
     * @return
     */
    public TextView getTitleTv() {
        return mTitleTv;
    }

    /**
     * 返回按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnBackClickListener(OnClickListener listener) {
        mBackBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 关闭按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnCloseClickListener(OnClickListener listener) {
        mCloseBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 显示返回按钮
     */
    public TitleBarView showBackBtn() {
        mBackBtn.setVisibility(VISIBLE);
        return this;
    }

    /**
     * 不显示返回按钮
     */
    public TitleBarView hideBackBtn() {
        mBackBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 显示关闭按钮
     */
    public TitleBarView showCloseBtn() {
        mCloseBtn.setVisibility(VISIBLE);
        return this;
    }

    /**
     * 不显示关闭按钮
     */
    public TitleBarView hideCloseBtn() {
        mCloseBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 执行返回按钮的点击
     */
    public void backPerformClick() {
        mBackBtn.performClick();
    }

    /**
     * 执行关闭按钮的点击
     */
    public void closePerformClick() {
        mCloseBtn.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_back_btn:
            ((Activity) getContext()).onBackPressed();
            break;
        case R.id.titlebar_close_btn:
            ((Activity) getContext()).onBackPressed();
            break;
        default:
            break;
        }
    }

}
