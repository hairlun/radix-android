/**
 * radix
 * KeyListAdapter
 * zhoushujie
 * 2016-6-23 上午10:32:24
 */
package com.patr.radix.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.patr.radix.adapter.AbsListAdapter#getView(int,
     * android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_key, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.key_iv);
            holder.name = (TextView) convertView.findViewById(R.id.key_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RadixLock lock = getItem(position);
        holder.name.setText(lock.getName());
        if (MyApplication.DEBUG) {
            holder.name.append("(" + lock.getBleName() + ")");
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView name;
    }

}
