package com.patr.radix.ui.settings;

import org.xutils.x;
import org.xutils.common.util.LogUtil;

import com.patr.radix.LoginActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.LoginResult;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.unlock.MyKeysActivity;
import com.patr.radix.ui.view.AvatarView;
import com.patr.radix.ui.view.ListSelectDialog;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.dialog.MsgDialog;
import com.patr.radix.ui.view.dialog.MsgDialog.BtnType;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements OnClickListener,
        OnItemClickListener {

    private Context context;

    private RelativeLayout currentCommunityRl;

    private TextView communityName;

    private RelativeLayout remoteOpenDoorRl;

    private RelativeLayout modifyUserinfoRl;

    private RelativeLayout shareRl;

    private RelativeLayout feedbackRl;

    private TextView clearValue;

    private Button clearBtn;

    private ImageButton settingBtn;

    private AvatarView avatarIv;

    private TextView nameTv;

    private TextView phoneTv;

    private CommunityListAdapter adapter;

    private LoadingDialog loadingDialog;

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
        communityName = (TextView) view.findViewById(R.id.community_name);
        remoteOpenDoorRl = (RelativeLayout) view.findViewById(R.id.my_key_ll);
        modifyUserinfoRl = (RelativeLayout) view
                .findViewById(R.id.modify_userinfo_ll);
        shareRl = (RelativeLayout) view.findViewById(R.id.share_ll);
        feedbackRl = (RelativeLayout) view.findViewById(R.id.feedback_ll);
        clearValue = (TextView) view.findViewById(R.id.clear_value);
        clearBtn = (Button) view.findViewById(R.id.clear_btn);
        settingBtn = (ImageButton) view.findViewById(R.id.setting_btn);
        avatarIv = (AvatarView) view.findViewById(R.id.avatar_iv);
        nameTv = (TextView) view.findViewById(R.id.name_tv);
        phoneTv = (TextView) view.findViewById(R.id.phone_tv);

        currentCommunityRl.setOnClickListener(this);
        remoteOpenDoorRl.setOnClickListener(this);
        modifyUserinfoRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        adapter = new CommunityListAdapter(context,
                MyApplication.instance.getCommunities());
        Community selectedCommunity = MyApplication.instance
                .getSelectedCommunity();
        if (selectedCommunity != null) {
            communityName.setText("(" + selectedCommunity.getName() + ")");
            int size = adapter.getCount();
            for (int i = 0; i < size; i++) {
                if (selectedCommunity.equals(adapter.getList().get(i))) {
                    adapter.select(i);
                    break;
                }
            }
        }
        loadingDialog = new LoadingDialog(context);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Community selectedCommunity = MyApplication.instance
                .getSelectedCommunity();
        if (selectedCommunity != null) {
            communityName.setText("(" + selectedCommunity.getName() + ")");
        }
        UserInfo userInfo = MyApplication.instance.getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getToken())) {
            if (!TextUtils.isEmpty(userInfo.getUserPic())) {
                avatarIv.setVisibility(View.VISIBLE);
                x.image().bind(avatarIv, userInfo.getUserPic());
            }
            nameTv.setVisibility(View.VISIBLE);
            phoneTv.setVisibility(View.VISIBLE);
            nameTv.setText(userInfo.getName());
            phoneTv.setText(userInfo.getMobile());
        } else {
            avatarIv.setVisibility(View.GONE);
            nameTv.setVisibility(View.INVISIBLE);
            phoneTv.setVisibility(View.INVISIBLE);
        }

        // TODO 刷新缓存大小

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.setting_btn:
            context.startActivity(new Intent(context, PrefSettingActivity.class));
            break;

        case R.id.current_community_ll:
            getCommunityList();
            break;

        case R.id.my_key_ll:
            intent = new Intent(context, MyKeysActivity.class);
            intent.putExtra("remoteOpenDoor", true);
            context.startActivity(intent);
            break;

        case R.id.modify_userinfo_ll:
            if (!TextUtils.isEmpty(MyApplication.instance.getUserInfo().getAccount())) {
                intent = new Intent(context, EditUserInfoActivity.class);
                context.startActivity(intent);
            } else {
                ToastUtil.showShort(context, "请先登录！");
                intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
            break;

        case R.id.share_ll:
            shareMsg("请选择", "", "http://zsyuxindianzi.b2b.c-ps.net/", null);
            break;

        case R.id.feedback_ll:
            intent = new Intent(context, FeedbackActivity.class);
            context.startActivity(intent);
            break;

        case R.id.clear_btn:
            MsgDialog.show(context, "确认", "确定要清除缓存吗？", "确定",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            loadingDialog.show("正在清除缓存…");
                            MyApplication.instance.clearCache();
                            loadingDialog.dismiss();
                            getUserInfo();
                        }
                    }, BtnType.TWO);

            break;
        }
    }

    private void getUserInfo() {
        ServiceManager.queryMobileUserById(new RequestListener<LoginResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在加载…");
            }

            @Override
            public void onSuccess(int stateCode, LoginResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        MyApplication.instance.setUserInfo(result.getUserInfo());
                        PrefUtil.saveUserInfo(context, result.getUserInfo());
                        refresh();
                    } else {
//                        ToastUtil.showShort(context, result.getRetinfo());
                    }
                } else {
//                    ToastUtil.showShort(context, R.string.connect_exception);
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, R.string.connect_exception);
                loadingDialog.dismiss();
            }

        });
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
            Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); // 纯文本
        // intent.setType("image/*");
        // if (uri != null) {
        // intent.putExtra(Intent.EXTRA_STREAM, uri);
        // }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
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
//                                ToastUtil.showShort(context,
//                                        result.getRetinfo());
                                getCommunityListFromCache();
                            }
                        } else {
//                            ToastUtil.showShort(context,
//                                    R.string.connect_exception);
                            getCommunityListFromCache();
                        }
                    }

                    @Override
                    public void onFailure(Exception error, String content) {
//                        ToastUtil
//                                .showShort(context, R.string.connect_exception);
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
            UserInfo userInfo = new UserInfo();
            MyApplication.instance.setUserInfo(userInfo);
            PrefUtil.saveUserInfo(context, userInfo);
            MyApplication.instance.clearCache();
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
