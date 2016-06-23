/**
 * radix
 * KeyListAdapter
 * zhoushujie
 * 2016-6-23 上午10:32:24
 */
package com.patr.radix.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.patr.radix.bean.RadixLock;

/**
 * @author zhoushujie
 *
 */
public class KeyListAdapter extends AbsListAdapter<RadixLock> {

    /**
     * @param context
     * @param mList
     */
    public KeyListAdapter(Context context, List<RadixLock> mList) {
        super(context, mList);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.patr.radix.adapter.AbsListAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

}
