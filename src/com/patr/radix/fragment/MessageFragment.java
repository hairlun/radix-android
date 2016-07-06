package com.patr.radix.fragment;

import com.patr.radix.R;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.swipe.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class MessageFragment extends Fragment {
    
    TitleBarView titleBarView;
    private ListView lv;
    private SwipeRefreshLayout swipe;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_message, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.message_titlebar);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_latest_msg);
        return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

}
