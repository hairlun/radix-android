package com.patr.radix.adapter;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.ui.unlock.ShareView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/***
 * 分享Pager适配器
 */
public class SharePagerAdapter extends PagerAdapter {

    public static final String TAG = SharePagerAdapter.class.getName();

    Context mContext;

    /** View集合 */
    private List<ShareView> views = new ArrayList<ShareView>();

    public SharePagerAdapter(Context context, List<ShareView> mViews) {
        mContext = context;
        this.views = mViews;
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
