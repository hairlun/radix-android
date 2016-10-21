package com.patr.radix.ui.message;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.adapter.MessagePagerAdapter;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayoutDirection;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MessageFragment extends Fragment implements OnPageChangeListener,
        OnCheckedChangeListener, OnClickListener {

    Context context;

    /** Tab栏 */
    RadioGroup mRadioGroup;
    
    Button clearBtn;

    /** 界面切换 */
    ViewPager mPager;

    /** 界面切换适配器 */
    MessagePagerAdapter mPagerAdapter;
    
    LoadingDialog loadingDialog;

    /** View集合 */
    List<View> mViews = new ArrayList<View>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_messge, container, false);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.message_rg);
        mPager = (ViewPager) view.findViewById(R.id.message_pager);
        clearBtn = (Button) view.findViewById(R.id.clear_btn);
        mViews.add(new NoticeView(context));
        mViews.add(new MessageView(context));
        mPagerAdapter = new MessagePagerAdapter(context, mViews);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        clearBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(context);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NoticeView)mViews.get(0)).refresh();
        ((MessageView)mViews.get(1)).refresh();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
        if (position == 0) {
            clearBtn.setVisibility(View.GONE);
        } else {
            clearBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
        case R.id.public_message_rb:
            mPager.setCurrentItem(0);
            clearBtn.setVisibility(View.GONE);
            break;
        case R.id.private_message_rb:
            mPager.setCurrentItem(1);
            clearBtn.setVisibility(View.VISIBLE);
            context.sendBroadcast(new Intent("actionClearPersonMessage"));
            break;
        default:
            break;
        }
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.clear_btn:
            clearPersonMessage();
            break;
        }
    }
    
    private void clearPersonMessage() {
        ServiceManager.deletePersonMessage(new RequestListener<RequestResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在删除…");
            }

            @Override
            public void onSuccess(int stateCode, RequestResult result) {
                if (result != null) {
                    ToastUtil.showShort(context, result.getRetinfo());
                    ((MessageView)mViews.get(1)).refresh();
                } else {
                    ToastUtil.showShort(context, R.string.connect_exception);
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, R.string.connect_exception);
                loadingDialog.dismiss();
            }
        });
    }

}
