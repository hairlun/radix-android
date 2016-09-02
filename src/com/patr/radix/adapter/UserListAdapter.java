/**
 * radix
 * UserListAdapter
 * zhoushujie
 * 2016-8-23 下午7:23:17
 */
package com.patr.radix.adapter;

import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.patr.radix.R;
import com.patr.radix.bean.UserInfo;

/**
 * @author zhoushujie
 * 
 */
public class UserListAdapter extends AbsListAdapter<UserInfo> {

    /** 已选择的集合 */
    public HashSet<UserInfo> selectedSet = new HashSet<UserInfo>();

    /**
     * @param context
     * @param mList
     */
    public UserListAdapter(Context context, List<UserInfo> mList) {
        super(context, mList);
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
                    R.layout.item_server_addr, null);
            holder.tv = (TextView) convertView.findViewById(R.id.choose_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfo userInfo = getItem(position);
        holder.tv
                .setText(userInfo.getName() + "(" + userInfo.getMobile() + ")");
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }

}
