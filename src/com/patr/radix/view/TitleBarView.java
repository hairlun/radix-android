/**
 * radix
 * TitleBarView
 * zhoushujie
 * 2016-6-13 上午11:03:47
 */
package com.patr.radix.view;

import com.patr.radix.MyApplication;
import com.patr.radix.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author zhoushujie
 * 
 */
public class TitleBarView extends LinearLayout implements OnClickListener {

    public static final String TAG = TitleBarView.class.getName();

    private Context context;

    /** 返回按钮 */
    private ImageButton mBackBtn;

    /** 取消按钮 */
    private Button mCancelBtn;

    /** 标题 */
    private TextView mTitleTv;

    /** 全选按钮 */
    private Button mCheckAllBtn;

    /** 发送钥匙按钮 */
    private Button mSendKeyBtn;

    /** 选择钥匙按钮 */
    private ImageButton mSelectKeyBtn;

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
        mCancelBtn = (Button) findViewById(R.id.titlebar_cancel_btn);
        mCheckAllBtn = (Button) findViewById(R.id.titlebar_check_all_btn);
        mSendKeyBtn = (Button) findViewById(R.id.titlebar_send_key_btn);
        mSelectKeyBtn = (ImageButton) findViewById(R.id.titlebar_select_key_btn);

        hideCancelBtn();
        hideCheckAllBtn();
        hideSendKeyBtn();
        hideSelectKeyBtn();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mTitleTv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mCheckAllBtn.setOnClickListener(this);
        mSendKeyBtn.setOnClickListener(this);
        mSelectKeyBtn.setOnClickListener(this);
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
     * 取消按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnCancelClickListener(OnClickListener listener) {
        mCancelBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 全选按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnCheckAllClickListener(OnClickListener listener) {
        mCheckAllBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 发送钥匙按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnSendKeyClickListener(OnClickListener listener) {
        mSendKeyBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 选择钥匙按钮点击监听
     * 
     * @param listener
     * @return
     */
    public TitleBarView setOnSelectKeyClickListener(OnClickListener listener) {
        mSelectKeyBtn.setOnClickListener(listener);
        return this;
    }

    /**
     * 显示返回按钮
     */
    public TitleBarView showBackBtn() {
        mBackBtn.setVisibility(VISIBLE);
        mCancelBtn.setVisibility(GONE);
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
     * 显示取消按钮
     */
    public TitleBarView showCancelBtn() {
        mBackBtn.setVisibility(GONE);
        mCancelBtn.setVisibility(VISIBLE);
        return this;
    }

    /**
     * 不显示取消按钮
     */
    public TitleBarView hideCancelBtn() {
        mCancelBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 显示全选按钮
     */
    public TitleBarView showCheckAllBtn() {
        mCheckAllBtn.setVisibility(VISIBLE);
        mSendKeyBtn.setVisibility(GONE);
        mSelectKeyBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 不显示全选按钮
     */
    public TitleBarView hideCheckAllBtn() {
        mCheckAllBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 显示发送钥匙按钮
     */
    public TitleBarView showSendKeyBtn() {
        mSendKeyBtn.setVisibility(VISIBLE);
        mCheckAllBtn.setVisibility(GONE);
        mSelectKeyBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 不显示发送钥匙按钮
     */
    public TitleBarView hideSendKeyBtn() {
        mSendKeyBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 显示选择钥匙按钮
     */
    public TitleBarView showSelectKeyBtn() {
        mSelectKeyBtn.setVisibility(VISIBLE);
        mCheckAllBtn.setVisibility(GONE);
        mSendKeyBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 不显示选择钥匙按钮
     */
    public TitleBarView hideSelectKeyBtn() {
        mSelectKeyBtn.setVisibility(GONE);
        return this;
    }

    /**
     * 执行返回按钮的点击
     */
    public void backPerformClick() {
        mBackBtn.performClick();
    }

    /**
     * 执行取消按钮的点击
     */
    public void cancelPerformClick() {
        mCancelBtn.performClick();
    }

    /**
     * 执行全选按钮的点击
     */
    public void checkAllPerformClick() {
        mCheckAllBtn.performClick();
    }

    /**
     * 执行发送钥匙按钮的点击
     */
    public void sendKeyPerformClick() {
        mSendKeyBtn.performClick();
    }

    /**
     * 执行选择钥匙按钮的点击
     */
    public void selectKeyPerformClick() {
        mSelectKeyBtn.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_back_btn:
            ((Activity) getContext()).onBackPressed();
            break;
        case R.id.titlebar_cancel_btn:
            break;
        case R.id.titlebar_check_all_btn:
            break;
        case R.id.titlebar_send_key_btn:
            break;
        case R.id.titlebar_select_key_btn:
            break;
        default:
            break;
        }
    }

}
