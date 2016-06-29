package com.patr.radix.fragment;

import com.patr.radix.LoginActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements OnClickListener {
    
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
        userInfoLl.setOnClickListener(this);
        permissionBtn.setOnClickListener(this);
        feedbackBtn.setOnClickListener(this);
        checkUpdateBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        String name = MyApplication.instance.getName();
        if (TextUtils.isEmpty(name)) {
            // 未登录
            userInfoLl.setVisibility(View.GONE);
            permissionBtn.setVisibility(View.GONE);
            logoutBtn.setText(R.string.login);
        } else {
            usernameTv.setText(name);
        }
        return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.settings_user_info_ll:
            break;
        case R.id.settings_user_permission_btn:
            ToastUtil.showShort(context, "暂无用户授权功能");
            break;
        case R.id.settings_feedback_btn:
            break;
        case R.id.settings_check_update_btn:
            break;
        case R.id.settings_logout_btn:
            if (TextUtils.isEmpty(MyApplication.instance.getUserId())) {
                login();
            } else {
                logout();
            }
            break;
        }
    }
    
    private void login() {
        LoginActivity.start(context);
    }
    
    private void logout() {
        
    }

}
