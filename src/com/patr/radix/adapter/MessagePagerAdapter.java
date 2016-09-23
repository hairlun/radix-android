package com.patr.radix.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/***
 * 最新消息Pager适配器
 */
public class MessagePagerAdapter extends PagerAdapter {

    public static final String TAG = MessagePagerAdapter.class.getName();

    Context mContext;

    /** View集合 */
    private List<LinearLayout> views = new ArrayList<LinearLayout>();

    public MessagePagerAdapter(Context context, List<LinearLayout> views) {
        mContext = context;
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = (View) views.get(position);
        ViewGroup group = (ViewGroup) v.getParent();
        if (group != null) {
            group.removeAllViewsInLayout();
        }
        container.addView(v, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
