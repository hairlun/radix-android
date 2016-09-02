package com.patr.radix.adapter;

import java.util.HashSet;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.bean.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommunityListAdapter extends AbsListAdapter<Community> {

    /** 已选择的集合 */
    public HashSet<Community> selectedSet = new HashSet<Community>();

    public CommunityListAdapter(Context context, List<Community> mList) {
        super(context, mList);
        notifyDataSetChanged();
    }

    /**
     * 选择
     * 
     * @param position
     */
    public void select(int position) {
        selectedSet.clear();
        selectedSet.add(getItem(position));
        notifyDataSetChanged();
    }

    /**
     * 判断是否已选择
     * 
     * @param position
     * @return
     */
    public boolean isSelect(int position) {
        return selectedSet.contains(getItem(position));
    }

    /**
     * 取消选择
     * 
     * @param position
     */
    public void deselect(int position) {
        selectedSet.remove(getItem(position));
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_server_addr, null);
            holder.tv = (TextView) convertView.findViewById(R.id.choose_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Community community = getItem(position);
        String text = community.getName();
        holder.tv.setText(text);
        holder.tv.setSelected(selectedSet.contains(text));
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }

}
