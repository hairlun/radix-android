/**
 * radix
 * MessegeView
 * zhoushujie
 * 2016-9-22 下午8:24:46
 */
package com.patr.radix.ui.message;

import com.patr.radix.adapter.NoticeListAdapter;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author zhoushujie
 *
 */
public class MessageView extends LinearLayout {

    private ListView lv;

    private NoticeListAdapter adapter;

    private SwipeRefreshLayout swipe;

    private int pageNum;

    private int totalCount;

    private int totalPage;

    /**
     * @param context
     */
    public MessageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

}
