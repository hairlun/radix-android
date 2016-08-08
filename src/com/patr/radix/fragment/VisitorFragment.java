package com.patr.radix.fragment;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.TitleBarView;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECInitParams.LoginMode;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
        String mobile = null;
        switch (v.getId()) {
        case R.id.visitor_request_btn:
            mobile = mobileEt.getText().toString().trim();
            if (TextUtils.isEmpty(mobile)) {
                ToastUtil.showShort(context, "请输入手机号码！");
                return;
            }
            // 申请访问
            // 检查有没有登录
            if (TextUtils.isEmpty(MyApplication.instance.getUserInfo().getAccount())) {
//                // 如果没有登录，使用自己的手机号码自动登录云通讯
//                String appKey = FileAccessor.getAppKey();
//                String token = FileAccessor.getAppToken();
//                String myMobile = Utils.getNativePhoneNumber(context);
//                String pass = "";
//                ClientUser clientUser = new ClientUser(myMobile);
//                clientUser.setAppKey(appKey);
//                clientUser.setAppToken(token);
//                clientUser.setLoginAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
//                clientUser.setPassword(pass);
//                CCPAppManager.setClientUser(clientUser);
//                SDKCoreHelper.init(context, ECInitParams.LoginMode.FORCE_LOGIN);

                // 如果没有登录，提示没有登录
                ToastUtil.showLong(context, "未登录！");
            } else {
                // 如果登录了，直接呼叫
                String appKey = FileAccessor.getAppKey();
                String token = FileAccessor.getAppToken();
                String myMobile = MyApplication.instance.getUserInfo().getMobile();
                String pass = "";
                ClientUser clientUser = new ClientUser(myMobile);
                clientUser.setAppKey(appKey);
                clientUser.setAppToken(token);
                clientUser.setLoginAuthType(LoginAuthType.NORMAL_AUTH);
                clientUser.setPassword(pass);
                CCPAppManager.setClientUser(clientUser);
                SDKCoreHelper.init(context, LoginMode.FORCE_LOGIN);
                CCPAppManager.callVoIPAction(getActivity(), CallType.VIDEO,
                        "", mobile, false);
            }
            break;
        }
    }

}
