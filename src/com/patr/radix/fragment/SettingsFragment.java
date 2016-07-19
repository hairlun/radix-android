package com.patr.radix.fragment;

import org.xutils.common.util.LogUtil;

import com.patr.radix.LoginActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.ListSelectDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements OnClickListener, OnItemClickListener {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private LinearLayout userInfoLl;
    
    private TextView usernameTv;
    
    private Button permissionBtn;
    
    private Button feedbackBtn;
    
    private LinearLayout currentCommunityLl;
    
    private TextView currentCommunityTv;
    
    private Button checkUpdateBtn;
    
    private Button logoutBtn;
    
    private CommunityListAdapter adapter;

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
        currentCommunityLl = (LinearLayout) view.findViewById(R.id.settings_current_community_ll);
        currentCommunityTv = (TextView) view.findViewById(R.id.settings_current_community_tv);
        checkUpdateBtn = (Button) view.findViewById(R.id.settings_check_update_btn);
        logoutBtn = (Button) view.findViewById(R.id.settings_logout_btn);
        permissionBtn.setCompoundDrawablePadding(15);
        feedbackBtn.setCompoundDrawablePadding(15);
        checkUpdateBtn.setCompoundDrawablePadding(15);
        userInfoLl.setOnClickListener(this);
        permissionBtn.setOnClickListener(this);
        feedbackBtn.setOnClickListener(this);
        currentCommunityLl.setOnClickListener(this);
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
        Community selectedCommunity = MyApplication.instance.getSelectedCommunity();
        if (selectedCommunity != null) {
            currentCommunityTv.setText("(" + selectedCommunity.getName() + ")");
        } else {
            currentCommunityTv.setText("");
        }
        adapter = new CommunityListAdapter(context, MyApplication.instance.getCommunities());
        int size = adapter.getCount();
        for (int i = 0; i < size; i++) {
            if (selectedCommunity.equals(adapter.getList().get(i))) {
                adapter.select(i);
                break;
            }
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
            // 修改用户信息
            break;
        case R.id.settings_user_permission_btn:
            ToastUtil.showShort(context, "暂无用户授权功能");
            break;
        case R.id.settings_feedback_btn:
            // 意见反馈
            break;
        case R.id.settings_current_community_ll:
            // 选择小区
            getCommunityList();
            break;
        case R.id.settings_check_update_btn:
            // 版本检查及更新
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
    
    private void getCommunityList() {
        switch (NetUtils.getConnectedType(context)) {
        case NONE:
            getCommunityListFromCache();
            break;
        case WIFI:
        case OTHER:
            getCommunityListFromServer();
            break;
        default:
            break;
        }
    }
    
    private void getCommunityListFromCache() {
        CacheManager.getCacheContent(context, CacheManager.getCommunityListUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        MyApplication.instance.setCommunities(result.getCommunities());
                        adapter.notifyDataSetChanged();
                        ListSelectDialog.show(context, "请选择小区", adapter, SettingsFragment.this);
                    }
                    
                }, new GetCommunityListParser());
    }
    
    private void getCommunityListFromServer() {
        // 从服务器获取小区列表
        ServiceManager.getCommunityList(new RequestListener<GetCommunityListResult>() {

            @Override
            public void onSuccess(int stateCode, GetCommunityListResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        MyApplication.instance.setCommunities(result.getCommunities());
                        saveCommunityListToDb(result.getResponse());
                        adapter.notifyDataSetChanged();
                        ListSelectDialog.show(context, "请选择小区", adapter, SettingsFragment.this);
                    } else {
                        ToastUtil.showShort(context, result.getRetinfo());
                        getCommunityListFromCache();
                    }
                } else {
                    getCommunityListFromCache();
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
                getCommunityListFromCache();
            }
            
        });
    }

    /**
     * 保存列表到数据库
     * 
     * @param content
     */
    protected void saveCommunityListToDb(String content) {
        CacheManager.saveCacheContent(context, CacheManager.getCommunityListUrl(), content,
                new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + CacheManager.getCommunityListUrl() + "=" + result);
                    }
                });
    }
    
    private void login() {
        LoginActivity.start(context);
    }
    
    private void logout() {
        
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        MyApplication.instance.setSelectedCommunity(adapter.getItem(position));
        if (!adapter.isSelect(position)) {
            MyApplication.instance.setSelectedLock(null);
        }
        adapter.select(position);
    }

}
