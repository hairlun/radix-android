package com.patr.radix.fragment;

import com.patr.radix.R;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    
    private TitleBarView titleBarView;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_settings, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.settings_titlebar);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_my_settings);
        return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

}
