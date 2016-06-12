package com.patr.radix.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ListView适配器抽象类
 * 
 * @author huangzhongwen
 * 
 * @param <T>
 */

public abstract class AbsListAdapter<T> extends BaseAdapter {

    protected List<T> mList; // Arraylist数据源
    protected Context mContext; // 上下文对象
    protected LayoutInflater mInflater;
    /**
     * 数据变化监听器
     */
    protected OnDataChangedListener<T> onDataChangedListener;

    /**
     * 构造方法
     * 
     * @param context
     *            上下文对象
     * @param mList
     *            数据源
     */
    public AbsListAdapter(Context context, List<T> mList) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        setList(mList);
    }

    /**
     * 
     * @param context
     * @param mList
     */
    public AbsListAdapter(Context context, T[] mArray) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        setList(mArray);
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置数据源
     * 
     * @param mList2
     */
    private void setList(List<T> mList2) {
        this.mList = mList2 != null ? mList2 : new ArrayList<T>();
    }

    /**
     * 获取数据集合
     * 
     * @return
     */
    public List<T> getList() {
        return mList;
    }

    /**
     * 
     * @param mArray
     */
    private void setList(T[] mArray) {
        List<T> list = new ArrayList<T>();
        if (mArray != null) {
            for (T t : mArray) {
                list.add(t);
            }
        }
        setList(list);
    }

    /**
     * 更新数据源
     * 
     * @param mList
     *            ArrayList<T>
     */
    public void changeData(List<T> mList) {
        setList(mList);
        notifyDataSetChanged();
    }

    /**
     * 更新数据源
     * 
     * @param mArray
     */
    public void changeData(T[] mArray) {
        setList(mArray);
        this.notifyDataSetChanged();
    }

    /**
     * 增加一条数据
     * 
     * @param t
     * @return
     */
    public boolean add(T t) {
        boolean result = false;
        if (mList.contains(t)) {
            result = mList.set(mList.indexOf(t), t) != null;
        } else {
            result = this.mList.add(t);
        }
        notifyDataSetChanged();
        return result;
    }

    /**
     * 增加一个集合的数据
     * 
     * @param mList
     */
    public void addAll(List<T> mList) {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            T t = mList.get(i);
            if (!this.mList.contains(t)) {
                this.mList.add(t);
            } else {
                this.mList.set(this.mList.indexOf(t), t);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 添加新数据并是否更新
     * 
     * @param mList
     * @param isUpdate
     */
    public void addAll(List<T> mList, boolean isUpdate) {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            T t = mList.get(i);
            if (!this.mList.contains(t)) {
                this.mList.add(t);
            } else {
                this.mList.set(this.mList.indexOf(t), t);
            }
        }
        if (isUpdate) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged(this.mList);
        }
    }

    /**
     * 
     * @param mList
     * @return
     */
    public boolean set(List<T> mList) {
        this.mList.clear();
        boolean result = this.mList.addAll(mList);
        notifyDataSetChanged();
        return result;
    }

    /**
     * 插入一条数据
     * 
     * @param position
     *            插入的位置
     * @param t
     *            数据对象
     */
    public void insert(int position, T t) {
        if (position < 0 || position > getCount() - 1) {
            return;
        }
        this.mList.add(position, t);
        this.notifyDataSetChanged();
    }

    /**
     * 删除一条数据
     * 
     * @param t
     *            数据对象
     * @return 删除是否成功
     */
    public boolean remove(T t) {
        boolean removed = this.mList.remove(t);
        this.notifyDataSetChanged();
        return removed;
    }

    /**
     * 删除一个集合的数据
     * 
     * @param list
     * @return
     */
    public boolean removeAll(Collection<T> list) {
        boolean removed = mList.removeAll(list);
        this.notifyDataSetChanged();
        return removed;
    }

    /**
     * 删除指定位置的数据
     * 
     * @param position
     *            要删除的位置
     * @return 被删除的对象
     */
    public T remove(int position) {
        if (position < 0 || position > getCount() - 1) {
            return null;
        }
        T t = this.mList.remove(position);
        this.notifyDataSetChanged();
        return t;
    }

    /**
     * 清除所有
     */
    public void clear() {
        this.mList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 更新指定位置的对象
     * 
     * @param position
     *            指定位置
     * @param t
     *            数据对象
     */
    public void set(int position, T t) {
        if (position < 0 || position > getCount() - 1) {
            return;
        }
        this.mList.set(position, t);
        this.notifyDataSetChanged();
    }

    /**
     * 数据变化监听器
     * 
     * @param onDataChangedListener
     */
    public void setOnDataChangedListener(
            OnDataChangedListener<T> onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    @Override
    public abstract View getView(int position, View convertView,
            ViewGroup parent);

    /**
     * 数据变化监听器
     * 
     * @author huangzhongwen 2014-10-9 上午10:13:58
     * @param <T>
     */
    public interface OnDataChangedListener<T> {

        void onDataChanged(List<T> list);
    }
}
