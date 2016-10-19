/**
 * radix
 * MessegeView
 * zhoushujie
 * 2016-9-22 下午8:24:46
 */
package com.patr.radix.ui.message;

import java.util.List;

import com.patr.radix.R;
import com.patr.radix.adapter.NoticeListAdapter;
import com.patr.radix.bean.GetNoticeListResult;
import com.patr.radix.bean.Message;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayout;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayout.OnRefreshListener;
import com.patr.radix.ui.view.swipe.SwipeRefreshLayoutDirection;
import com.patr.radix.utils.ToastUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author zhoushujie
 * 
 */
public class NoticeView extends LinearLayout implements OnItemClickListener,
        OnRefreshListener {

    private ListView lv;

    private NoticeListAdapter adapter;

    private SwipeRefreshLayout swipe;

    private int pageNum;

    private int totalCount;

    private int totalPage;

    /**
     * @param context
     */
    public NoticeView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_message, this);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lv = (ListView) findViewById(R.id.message_lv);
        adapter = new NoticeListAdapter(context, null);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        swipe.setOnRefreshListener(this);
        pageNum = 1;
        totalCount = 0;
        totalPage = 0;
        loadData();
    }
    
    public void refresh() {
        pageNum = 1;
        totalCount = 0;
        totalPage = 0;
        loadData();
    }

    private void loadData() {
        // 从服务器获取公告列表
        ServiceManager.getNoticeList(pageNum,
                new RequestListener<GetNoticeListResult>() {

                    @Override
                    public void onStart() {
                        swipe.post(new Runnable() {

                            @Override
                            public void run() {
                                swipe.setRefreshing(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(int stateCode,
                            GetNoticeListResult result) {
                        if (result != null) {
                            if (result.isSuccesses()) {
                                List<Message> notices = result.getNotices();
                                totalCount = result.getTotalCount();
                                totalPage = result.getTotalPage();
                                if (pageNum > 1) {
                                    adapter.addAll(notices);
                                } else {
                                    adapter.set(notices);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.showShort(getContext(),
                                        result.getRetinfo());
                            }
                        } else {
                            // ToastUtil.showShort(context,
                            // R.string.connect_exception);
                        }
                    }

                    @Override
                    public void onFailure(Exception error, String content) {
                        // ToastUtil.showShort(context,
                        // R.string.connect_exception);
                    }

                    @Override
                    public void onStopped() {
                        swipe.setRefreshing(false);
                    }

                });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Message message = adapter.getItem(position);
        MessageDetailsActivity.start(getContext(), message.getId());
    }

    @Override
    public void onRefresh(SwipeRefreshLayoutDirection direction) {
        if (SwipeRefreshLayoutDirection.TOP == direction) {
            pageNum = 1;
            totalCount = 0;
            totalPage = 0;
            loadData();
        } else {
            if (pageNum < totalPage) {
                pageNum++;
                loadData();
            } else {
                ToastUtil.showShort(getContext(), "已显示全部数据");
                swipe.setRefreshing(false);
            }
        }
    }
}
