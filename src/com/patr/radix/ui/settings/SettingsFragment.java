package com.patr.radix.ui.settings;

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
import com.patr.radix.ui.view.ListSelectDialog;
import com.patr.radix.ui.view.dialog.MsgDialog;
import com.patr.radix.ui.view.dialog.MsgDialog.BtnType;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements OnClickListener,
        OnItemClickListener {

    private Context context;

    private RelativeLayout currentCommunityRl;

    private CommunityListAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);
        currentCommunityRl = (RelativeLayout) view
                .findViewById(R.id.current_community_ll);
        currentCommunityRl.setOnClickListener(this);
        // logoutBtn = (Button) view.findViewById(R.id.settings_logout_btn);
        // logoutBtn.setOnClickListener(this);
        adapter = new CommunityListAdapter(context,
                MyApplication.instance.getCommunities());
        Community selectedCommunity = MyApplication.instance
                .getSelectedCommunity();
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
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.current_community_ll:
            getCommunityList();
            break;
        // case R.id.settings_logout_btn:
        // if (TextUtils.isEmpty(MyApplication.instance.getUserInfo()
        // .getAccount())) {
        // login();
        // } else {
        // logout();
        // }
        // break;
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
        CacheManager.getCacheContent(context,
                CacheManager.getCommunityListUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            MyApplication.instance.setCommunities(result
                                    .getCommunities());
                            adapter.notifyDataSetChanged();
                            ListSelectDialog.show(context, "请选择小区", adapter,
                                    SettingsFragment.this);
                        }
                    }

                }, new GetCommunityListParser());
    }

    private void getCommunityListFromServer() {
        // 从服务器获取小区列表
        ServiceManager
                .getCommunityList(new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        if (result != null) {
                            if (result.isSuccesses()) {
                                MyApplication.instance.setCommunities(result
                                        .getCommunities());
                                saveCommunityListToDb(result.getResponse());
                                adapter.notifyDataSetChanged();
                                ListSelectDialog.show(context, "请选择小区",
                                        adapter, SettingsFragment.this);
                            } else {
                                ToastUtil.showShort(context,
                                        result.getRetinfo());
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
        CacheManager.saveCacheContent(context,
                CacheManager.getCommunityListUrl(), content,
                new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + CacheManager.getCommunityListUrl()
                                + "=" + result);
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
