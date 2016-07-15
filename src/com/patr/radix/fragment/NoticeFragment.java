package com.patr.radix.fragment;

import java.util.List;

import com.patr.radix.MyApplication;
import com.patr.radix.NoticeDetailsActivity;
import com.patr.radix.R;
import com.patr.radix.adapter.NoticeAdapter;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.GetNoticeListResult;
import com.patr.radix.bean.Notice;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.swipe.SwipeRefreshLayout;
import com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener;
import com.patr.radix.view.swipe.SwipeRefreshLayoutDirection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    
    Context context;
    
    TitleBarView titleBarView;
    
    private ListView lv;
    
    private NoticeAdapter adapter;
    
    private SwipeRefreshLayout swipe;
    
    private int pageNum;
    
    private int totalCount;
    
    private int totalPage;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_notice, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.notice_titlebar);
        lv = (ListView) view.findViewById(R.id.message_lv);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_latest_msg);
        adapter = new NoticeAdapter(context, null);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        swipe.setOnRefreshListener(this);
        pageNum = 1;
        totalCount = 0;
        totalPage = 0;
        loadData();
        return view;
	}
	
	private void loadData() {
	    // 从服务器获取公告列表
        ServiceManager.getNoticeList(pageNum, new RequestListener<GetNoticeListResult>() {

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
            public void onSuccess(int stateCode, GetNoticeListResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        List<Notice> notices = result.getNotices();
                        totalCount = result.getTotalCount();
                        totalPage = result.getTotalPage();
                        adapter.set(notices);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShort(context, result.getRetinfo());
                    }
                } else {
//                    ToastUtil.showShort(context, R.string.connect_exception);
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
//                ToastUtil.showShort(context, R.string.connect_exception);
            }
            
            @Override
            public void onStopped() {
                swipe.setRefreshing(false);
            }
            
        });
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

    /* (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Notice notice = adapter.getItem(position);
        NoticeDetailsActivity.start(context, notice.getId());
    }

    /* (non-Javadoc)
     * @see com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener#onRefresh(com.patr.radix.view.swipe.SwipeRefreshLayoutDirection)
     */
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
                ToastUtil.showShort(context, "已显示全部数据");
            }
        }
    }

}
