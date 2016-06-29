/**
 * radix
 * KeyListAdapter
 * zhoushujie
 * 2016-6-23 上午10:32:24
 */
package com.patr.radix.adapter;

import java.util.HashSet;
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
    
    private HashSet<RadixLock> selectedSet = new HashSet<RadixLock>();
    
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
        if (MyApplication.DEBUG) {
            holder.name.append("(" + lock.getBleName() + ")");
        }
        if (isEdit) {
            holder.chooseIv.setVisibility(View.VISIBLE);
            if (selectedSet.contains(lock)) {
                holder.chooseIv.setImageDrawable(mContext.getDrawable(R.drawable.checkbox_yes));
            }
        }
        return convertView;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public HashSet<RadixLock> getSelectedSet() {
        return selectedSet;
    }

    public void setSelectedSet(HashSet<RadixLock> selectedSet) {
        this.selectedSet = selectedSet;
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
