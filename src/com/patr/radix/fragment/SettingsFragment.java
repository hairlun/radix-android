package com.patr.radix.fragment;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private LinearLayout userInfoLl;
    
    private TextView usernameTv;
    
    private Button permissionBtn;
    
    private Button feedbackBtn;
    
    private Button checkUpdateBtn;
    
    private Button logoutBtn;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_settings, container, false);
        titleBarView = (TitleBarView) view.findViewById(R.id.settings_titlebar);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_my_settings);
        userInfoLl = (LinearLayout) view.findViewById(R.id.settings_user_info_ll);
        usernameTv = (TextView) view.findViewById(R.id.settings_username_tv);
        permissionBtn = (Button) view.findViewById(R.id.settings_user_permission_btn);
        feedbackBtn = (Button) view.findViewById(R.id.settings_feedback_btn);
        checkUpdateBtn = (Button) view.findViewById(R.id.settings_check_update_btn);
        logoutBtn = (Button) view.findViewById(R.id.settings_logout_btn);
        permissionBtn.setCompoundDrawablePadding(15);
        feedbackBtn.setCompoundDrawablePadding(15);
        checkUpdateBtn.setCompoundDrawablePadding(15);
        String username = MyApplication.instance.getUsername();
        if (TextUtils.isEmpty(username)) {
            // 未登录
            userInfoLl.setVisibility(View.GONE);
            permissionBtn.setVisibility(View.GONE);
            logoutBtn.setText(R.string.login);
        } else {
            usernameTv.setText(username);
        }
        return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

}
