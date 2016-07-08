package com.patr.radix.fragment;

import com.patr.radix.R;
import com.patr.radix.adapter.NoticeAdapter;
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_message, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.message_titlebar);
        lv = (ListView) view.findViewById(R.id.message_lv);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_latest_msg);
        adapter = new NoticeAdapter(context, null);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        swipe.setOnRefreshListener(this);
        return view;
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
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener#onRefresh(com.patr.radix.view.swipe.SwipeRefreshLayoutDirection)
     */
    @Override
    public void onRefresh(SwipeRefreshLayoutDirection direction) {
        // TODO Auto-generated method stub
        
    }

}
