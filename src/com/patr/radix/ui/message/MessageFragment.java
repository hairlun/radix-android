package com.patr.radix.ui.message;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.adapter.MessagePagerAdapter;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayoutDirection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MessageFragment extends Fragment implements OnPageChangeListener,
        OnCheckedChangeListener {

    Context context;

    /** Tab栏 */
    RadioGroup mRadioGroup;

    /** 界面切换 */
    ViewPager mPager;

    /** 界面切换适配器 */
    MessagePagerAdapter mPagerAdapter;

    /** View集合 */
    List<MessageView> mViews = new ArrayList<MessageView>();

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
        mViews.add(new MessageView(context));
        mViews.add(new MessageView(context));
        mPagerAdapter = new MessagePagerAdapter(context, mViews);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        return view;
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
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
        case R.id.public_message_rb:
            mPager.setCurrentItem(0);
            break;
        case R.id.private_message_rb:
            mPager.setCurrentItem(1);
            break;
        default:
            break;
        }
    }

}
