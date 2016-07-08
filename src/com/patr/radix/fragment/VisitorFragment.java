package com.patr.radix.fragment;

import com.patr.radix.R;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VisitorFragment extends Fragment implements OnClickListener {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private EditText mobileEt;
    
    private Button requestBtn;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_visitor, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.visitor_titlebar);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_visitor_request);
        mobileEt = (EditText) view.findViewById(R.id.visitor_user_mobile_et);
        requestBtn = (Button) view.findViewById(R.id.visitor_request_btn);
        requestBtn.setOnClickListener(this);
        return view;
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.visitor_request_btn:
            break;
        }
    }

}
