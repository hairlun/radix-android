package com.patr.radix.fragment;

import com.patr.radix.R;
import com.patr.radix.view.GifView;
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

public class UnlockFragment extends Fragment {
    
    private TitleBarView titleBarView;
    
    private GifView gifView;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unlock, container, false);
		titleBarView = (TitleBarView) view.findViewById(R.id.unlock_titlebar);
		titleBarView.hideBackBtn();
		gifView = (GifView) view.findViewById(R.id.unlock_gv);
		gifView.setMovieResource(R.raw.shake_shake);
		return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

}
