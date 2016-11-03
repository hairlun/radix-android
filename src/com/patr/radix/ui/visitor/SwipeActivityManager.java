package com.patr.radix.ui.visitor;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import org.xutils.common.util.LogUtil;

public class SwipeActivityManager {

    private static final String TAG = "SwipeActivityManager";

    private static LinkedList<WeakReference<SwipeListener>> mLinkedList = new LinkedList<WeakReference<SwipeListener>>();

    public static void notifySwipe(float scrollParent) {
        if(mLinkedList.size() <= 0) {
            LogUtil.w("notifySwipe callback stack empty!, scrollParent:" + scrollParent);
            return ;
        }
        SwipeListener swipeListener = mLinkedList.get(0).get();
        if(swipeListener == null) {
            LogUtil.w("notifySwipe null, scrollParent " + scrollParent) ;
            return ;
        }
        swipeListener.onScrollParent(scrollParent);
        LogUtil.v("notifySwipe scrollParent: " + scrollParent + ", callback: " + swipeListener);
    }

    public static void pushCallback(SwipeListener listener) {
        LogUtil.d("pushCallback size " + mLinkedList.size() + " , " + listener);
        WeakReference<SwipeListener> swipeListenerWeakReference = new WeakReference<SwipeListener>(listener);
        mLinkedList.add(0, swipeListenerWeakReference);
    }

    public static boolean popCallback(SwipeListener listener) {
        int size = mLinkedList.size();
        LogUtil.d("popCallback size " + size + " , " + listener);
        if(listener == null) {
            return true;
        }
        LinkedList<Integer> list = new LinkedList<Integer>();
        for(int i = 0 ; i < mLinkedList.size() ; i ++) {
            if(listener != mLinkedList.get(i).get()) {
                list.add(0 , Integer.valueOf(i));
            } else {
                mLinkedList.remove(i);
                LogUtil.d("popCallback directly, index " + i);
            }
            if(!listener.isEnableGesture() || list.size() == i) {
                LogUtil.d("popCallback Fail! Maybe Top Activity");
                return  false;
            }
        }

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            WeakReference<SwipeListener> remove = mLinkedList.remove(next.intValue());
            LogUtil.d("popCallback, popup " + (remove != null ? remove : "NULL-CALLBACK"));
        }
        return list.isEmpty();
    }


    public static void notifySettle(boolean open , int speed) {
        if(mLinkedList.size() <= 0) {
            LogUtil.w("notifySettle callback stack empty!, open: " + open + ", speed:" + speed);
            return ;
        }
        SwipeListener swipeListener = mLinkedList.get(0).get();
        if(swipeListener == null) {
            LogUtil.w("notifySettle null, open: " + open + ", speed:" + speed);
            return ;
        }
        swipeListener.notifySettle(open , speed);
        LogUtil.v("notifySettle, open:" + open + " speed: " + speed + " callback:" + swipeListener);
    }




    public interface SwipeListener {
        /**
         * Invoke when state change
         * @param scrollPercent scroll percent of this view
         */
        void onScrollParent(float scrollPercent);

        void notifySettle(boolean open ,int speed);

        /**
         * 是否可以滑动
         * @return
         */
        boolean isEnableGesture();
    }
}
