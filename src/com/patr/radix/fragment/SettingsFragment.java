package com.patr.radix.fragment;

import org.xutils.common.util.LogUtil;

import com.patr.radix.LockSetupActivity;
import com.patr.radix.LockValidateActivity;
import com.patr.radix.LoginActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.ListSelectDialog;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.dialog.MsgDialog;
import com.patr.radix.view.dialog.MsgDialog.BtnType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
    
    private LinearLayout permissionLl;
    
    private LinearLayout feedbackLl;
    
    private LinearLayout lockLl;
    
    private TextView lockStatusTv;
    
    private LinearLayout currentCommunityLl;
    
    private TextView currentCommunityTv;
    
    private LinearLayout checkUpdateLl;
    
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
        permissionLl = (LinearLayout) view.findViewById(R.id.settings_user_permission_ll);
        feedbackLl = (LinearLayout) view.findViewById(R.id.settings_feedback_ll);
        lockLl = (LinearLayout) view.findViewById(R.id.settings_lock_ll);
        lockStatusTv = (TextView) view.findViewById(R.id.settings_lock_status_tv);
        currentCommunityLl = (LinearLayout) view.findViewById(R.id.settings_current_community_ll);
        currentCommunityTv = (TextView) view.findViewById(R.id.settings_current_community_tv);
        checkUpdateLl = (LinearLayout) view.findViewById(R.id.settings_check_update_ll);
        logoutBtn = (Button) view.findViewById(R.id.settings_logout_btn);
        userInfoLl.setOnClickListener(this);
        permissionLl.setOnClickListener(this);
        feedbackLl.setOnClickListener(this);
        lockLl.setOnClickListener(this);
        currentCommunityLl.setOnClickListener(this);
        checkUpdateLl.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        adapter = new CommunityListAdapter(context, MyApplication.instance.getCommunities());
        Community selectedCommunity = MyApplication.instance.getSelectedCommunity();
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
    public void onResume() {
        super.onResume();
        refresh();
    }
	
	private void refresh() {
	    if (!TextUtils.isEmpty(PrefUtil.getString(context, Constants.PREF_LOCK_KEY, null))) {
            lockStatusTv.setText("(已开启)");
        } else {
            lockStatusTv.setText("(已关闭)");
        }
        String name = MyApplication.instance.getUserInfo().getName();
        if (TextUtils.isEmpty(name)) {
            // 未登录
            userInfoLl.setVisibility(View.GONE);
            permissionLl.setVisibility(View.GONE);
            logoutBtn.setText(R.string.login);
        } else {
            // 已登录
            userInfoLl.setVisibility(View.VISIBLE);
            permissionLl.setVisibility(View.VISIBLE);
            logoutBtn.setText(R.string.settings_logout);
            usernameTv.setText(name);
            
        }
        Community selectedCommunity = MyApplication.instance.getSelectedCommunity();
        if (selectedCommunity != null) {
            currentCommunityTv.setText("(" + selectedCommunity.getName() + ")");
        } else {
            currentCommunityTv.setText("");
        }
	}

    @Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.settings_user_info_ll:
            // 修改用户信息
            break;
        case R.id.settings_user_permission_ll:
            ToastUtil.showShort(context, "暂无用户授权功能");
            break;
        case R.id.settings_feedback_ll:
            // 意见反馈
            break;
        case R.id.settings_lock_ll:
            // 手势密码
            if (TextUtils.isEmpty(PrefUtil.getString(context, Constants.PREF_LOCK_KEY, null))) {
                // 打开密码锁
                LockSetupActivity.start(context);
            } else {
                // 关闭密码锁
                LockValidateActivity.startForResult(this, Constants.LOCK_CLEAR);
            }
            break;
        case R.id.settings_current_community_ll:
            // 选择小区
            getCommunityList();
            break;
        case R.id.settings_check_update_ll:
            // 版本检查及更新
            break;
        case R.id.settings_logout_btn:
            if (TextUtils.isEmpty(MyApplication.instance.getUserInfo().getAccount())) {
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
                        if (result != null) {
                            MyApplication.instance.setCommunities(result.getCommunities());
                            adapter.notifyDataSetChanged();
                            ListSelectDialog.show(context, "请选择小区", adapter, SettingsFragment.this);
                        }
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
                    ToastUtil.showShort(context, "网络请求失败！");
                    getCommunityListFromCache();
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, content);
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
        MsgDialog.show(context, "确认", "确定要退出当前账号吗？", "确定",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MyApplication.instance.setUserInfo(new UserInfo());
                        PrefUtil.saveUserInfo(context, new UserInfo());
                        refresh();
                    }
                }, BtnType.TWO);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        MyApplication.instance.setSelectedCommunity(adapter.getItem(position));
        if (!adapter.isSelect(position)) {
            MyApplication.instance.setSelectedLock(null);
        }
        adapter.select(position);
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOCK_CLEAR) {
            if (resultCode == Constants.LOCK_CHECK_OK) {
                PrefUtil.save(context, Constants.PREF_LOCK_KEY, "");
            }
        }
    }

}
