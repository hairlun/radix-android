/**
 * radix
 * KeyListAdapter
 * zhoushujie
 * 2016-6-23 上午10:32:24
 */
package com.patr.radix.adapter;

import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patr.radix.App;
import com.patr.radix.R;
import com.patr.radix.bean.RadixLock;

/**
 * @author zhoushujie
 * 
 */
public class KeyListAdapter extends AbsListAdapter<RadixLock> {

    public HashSet<RadixLock> selectedSet = new HashSet<RadixLock>();

    private boolean isEdit = false;

    /**
     * @param context
     * @param mList
     */
    public KeyListAdapter(Context context, List<RadixLock> mList, boolean isEdit) {
        super(context, mList);
        this.isEdit = isEdit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.patr.radix.adapter.AbsListAdapter#getView(int,
     * android.view.View, android.view.ViewGroup)
     */
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_key, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.key_iv);
            holder.name = (TextView) convertView.findViewById(R.id.key_name);
            holder.desc = (TextView) convertView.findViewById(R.id.key_desc);
            holder.chooseIv = (ImageView) convertView
                    .findViewById(R.id.key_arrow_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RadixLock lock = getItem(position);
        String idx = String.format("%02d", position);
        holder.name.setText("Gate" + idx);
        holder.desc.setText(lock.getName());
        if (isEdit) {
            if (selectedSet.contains(lock)) {
                // 选中
                holder.chooseIv.setImageResource(R.drawable.btn_check_s);
            } else {
                // 没选中
                holder.chooseIv.setImageResource(R.drawable.btn_check);
            }
        } else {
            holder.chooseIv.setImageResource(R.drawable.icon_go_to); 
        }
        return convertView;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void selectAll() {
        selectedSet.addAll(mList);
    }

    public void deselectAll() {
        selectedSet.clear();
    }

    public boolean isSelectAll() {
        return selectedSet.containsAll(mList);
    }

    class ViewHolder {
        ImageView iv;
        TextView name;
        TextView desc;
        ImageView chooseIv;
    }

}
