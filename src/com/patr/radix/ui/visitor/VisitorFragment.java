package com.patr.radix.ui.visitor;

import org.xutils.common.util.LogUtil;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.adapter.UserListAdapter;
import com.patr.radix.bean.GetUserListResult;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetUserListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.ListSelectDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.ToastUtil;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECInitParams.LoginMode;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class VisitorFragment extends Fragment implements OnClickListener,
        OnItemClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private EditText mobileEt;

    private ImageButton contactBtn;

    private Button requestBtn;

    private UserListAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor, container,
                false);
        titleBarView = (TitleBarView) view.findViewById(R.id.visitor_titlebar);
        titleBarView.hideBackBtn().setTitle(R.string.titlebar_visitor_request);
        mobileEt = (EditText) view.findViewById(R.id.visitor_user_mobile_et);
        contactBtn = (ImageButton) view.findViewById(R.id.visitor_contact_btn);
        requestBtn = (Button) view.findViewById(R.id.visitor_btn);
        contactBtn.setOnClickListener(this);
        requestBtn.setOnClickListener(this);
        adapter = new UserListAdapter(context, null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 初始化和登录云通讯账号
        if (!TextUtils.isEmpty(MyApplication.instance.getMyMobile())) {
            String appKey = FileAccessor.getAppKey();
            String token = FileAccessor.getAppToken();
            String myMobile = MyApplication.instance.getMyMobile();
            String pass = "";
            ClientUser clientUser = new ClientUser(myMobile);
            clientUser.setAppKey(appKey);
            clientUser.setAppToken(token);
            clientUser.setLoginAuthType(LoginAuthType.NORMAL_AUTH);
            clientUser.setPassword(pass);
            CCPAppManager.setClientUser(clientUser);
            SDKCoreHelper.init(context, LoginMode.FORCE_LOGIN);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    private void getUserList() {
        switch (NetUtils.getConnectedType(context)) {
        case NONE:
            getUserListFromCache();
            break;
        case WIFI:
        case OTHER:
            getUserListFromServer();
            break;
        default:
            break;
        }
    }

    private void getUserListFromCache() {
        CacheManager.getCacheContent(context, CacheManager.getUserListUrl(),
                new RequestListener<GetUserListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetUserListResult result) {
                        if (result != null) {
                            adapter.set(result.getUsers());
                            ListSelectDialog.show(context, "请选择电话号码", adapter,
                                    VisitorFragment.this);
                        }
                    }

                }, new GetUserListParser());
    }

    private void getUserListFromServer() {
        ServiceManager.getUserList(new RequestListener<GetUserListResult>() {

            @Override
            public void onSuccess(int stateCode, GetUserListResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        adapter.set(result.getUsers());
                        ListSelectDialog.show(context, "请选择电话号码", adapter,
                                VisitorFragment.this);
                        saveUserListToDb(result.getResponse());
                    } else {
//                        ToastUtil.showShort(context, result.getRetinfo());
                        getUserListFromCache();
                    }
                } else {
//                    ToastUtil.showShort(context, R.string.connect_exception);
                    getUserListFromCache();
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
//                ToastUtil.showShort(context, R.string.connect_exception);
                getUserListFromCache();
            }

        });
    }

    /**
     * 保存列表到数据库
     * 
     * @param content
     */
    protected void saveUserListToDb(String content) {
        CacheManager.saveCacheContent(context, CacheManager.getUserListUrl(),
                content, new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + CacheManager.getUserListUrl() + "="
                                + result);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        String mobile = null;
        switch (v.getId()) {
        case R.id.visitor_contact_btn:
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI), 0);
            break;
        case R.id.visitor_btn:
            mobile = mobileEt.getText().toString().trim();
            if (TextUtils.isEmpty(mobile)) {
                ToastUtil.showShort(context, "请输入手机号码！");
                return;
            }
            // 申请访问
            CCPAppManager.callVoIPAction(getActivity(), CallType.VIDEO, "",
                    mobile, false);
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        adapter.select(position);
        mobileEt.setText(adapter.getItem(position).getMobile());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // ContentProvider展示数据类似一个单个数据库表
            // ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
            ContentResolver reContentResolverol = context.getContentResolver();
            // URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
            Uri contactData = data.getData();
            // 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
            Cursor cursor = getActivity().managedQuery(contactData, null, null,
                    null, null);
            cursor.moveToFirst();
            // 获得DATA表中的名字
            String username = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // 条件为联系人ID
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
            Cursor phone = reContentResolverol.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);
            while (phone.moveToNext()) {
                String usernumber = phone
                        .getString(phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mobileEt.setText(usernumber);
                break;
            }
        }
    }
}
