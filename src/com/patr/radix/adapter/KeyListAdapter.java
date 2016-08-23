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

import com.patr.radix.MyApplication;
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
    public KeyListAdapter(Context context, List<RadixLock> mList) {
        super(context, mList);
        isEdit = false;
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
            holder.name = (TextView) convertView.findViewById(R.id.key_tv);
            holder.chooseIv = (ImageView) convertView.findViewById(R.id.key_arrow_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RadixLock lock = getItem(position);
        holder.name.setText(lock.getName());
//        if (MyApplication.DEBUG) {
//            holder.name.append("(" + lock.getBleName1() + "," + lock.getBleName2() + ")");
//        }
        if (lock.equals(MyApplication.instance.getSelectedLock())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.name.setTextColor(mContext.getColor(R.color.blue));
            } else {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.blue));
            }
            holder.name.setText(holder.name.getText() + "-默认");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.name.setTextColor(mContext.getColor(R.color.black));
            } else {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }
        if (isEdit) {
            holder.chooseIv.setVisibility(View.VISIBLE);
            if (selectedSet.contains(lock)) {
                // 选中
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.chooseIv.setImageDrawable(mContext.getDrawable(R.drawable.checkbox_yes));
                } else {
                    holder.chooseIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checkbox_yes));
                }
            } else {
                // 没选中
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.chooseIv.setImageDrawable(mContext.getDrawable(R.drawable.checkbox_no));
                } else {
                    holder.chooseIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checkbox_no));
                }
            }
        } else {
            holder.chooseIv.setVisibility(View.GONE);
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
        ImageView chooseIv;
    }

}
