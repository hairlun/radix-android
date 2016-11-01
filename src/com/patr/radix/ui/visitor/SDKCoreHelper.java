package com.patr.radix.ui.visitor;

import org.xutils.common.util.LogUtil;

import com.patr.radix.App;
import com.patr.radix.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.ecsdk.ECDevice.InitListener;
import com.yuntongxun.ecsdk.ECDevice.OnECDeviceConnectListener;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.ECVoIPCallManager.OnVoIPListener;
import com.yuntongxun.ecsdk.ECVoIPCallManager.VoIPCall;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.VoipMediaChangedInfo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class SDKCoreHelper
        implements InitListener, OnECDeviceConnectListener {

    public static final String TAG = "SDKCoreHelper";
    private static SDKCoreHelper sInstance;
    private Context mContext;
    private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
    private ECInitParams mInitParams;
    private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;

    private static final String APP_KEY = "8a216da857511049015774ed4f891606";
    private static final String TOKEN = "ffd37ae409314220af2bdf679efc3b36";

    private SDKCoreHelper() {

    }

    public static SDKCoreHelper getInstance() {
        if (sInstance == null) {
            sInstance = new SDKCoreHelper();
        }
        return sInstance;
    }

    public static void init(Context ctx, ECInitParams.LoginMode mode) {
        LogUtil.d("[init] start regist..");
        getInstance().mMode = mode;
        getInstance().mContext = ctx;
        // 判断SDK是否已经初始化，没有初始化则先初始化SDK
        if (!ECDevice.isInitialized()) {
            getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
            ECDevice.initial(ctx, getInstance());
            return;
        }
        LogUtil.d(" SDK has inited , then regist..");
        // 已经初始化成功，直接进行注册
        getInstance().onInitialized();
    }

    @Override
    public void onError(Exception arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitialized() {
        LogUtil.d("ECSDK is ready");

        // 设置登录参数
        ECInitParams params = ECInitParams.createParams();
        params.setUserid(App.instance.getMyMobile());
        params.setAppKey(APP_KEY);
        params.setToken(TOKEN);
        // 设置登陆验证模式：自定义登录方式
        params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        // LoginMode（强制上线：FORCE_LOGIN 默认登录：AUTO。使用方式详见注意事项）
        params.setMode(mMode);

        // 设置登录回调监听
        ECDevice.setOnDeviceConnectListener(this);

        // 设置VOIP 自定义铃声路径
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        if (setupManager != null) {
            // 目前支持下面三种路径查找方式
            // 1、如果是assets目录则设置为前缀[assets://]
            setupManager.setInComingRingUrl(true, "raw://phonering.mp3");
            setupManager.setOutGoingRingUrl(true, "raw://phonering.mp3");
            setupManager.setBusyRingTone(true, "raw://playend.mp3");
            // 2、如果是raw目录则设置为前缀[raw://]
            // 3、如果是SDCard目录则设置为前缀[file://]
        }

        // 接收来电时，需要设置接收来电事件通知Intent。用于SDK回调对应的activity。
        // 呼入activity在sdk初始化的回 调onInitialized中设置。
        // 呼入界面activity、开发者需创建或修改VoIPCallActivity类,可参考demo中的
        // VoIPCallActivity.java
        Intent intent = new Intent(getInstance().mContext,
                VoIPCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getInstance().mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ECDevice.setPendingIntent(pendingIntent);

        // 登录云通讯SDK
        mInitParams.setAuthType(LoginAuthType.NORMAL_AUTH);
        if (!mInitParams.validate()) {
            ToastUtil.showShort(mContext, "注册云通讯参数错误，请检查");
            return;
        }

        ECDevice.login(mInitParams);
    }

    @Override
    public void onConnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(ECConnectState state, ECError error) {
        if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
            if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                Log.i("", "==帐号异地登陆");
            } else {
                Log.i("", "==其他登录失败,错误码：" + error.errorCode);
            }
            return;
        } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            Log.i("", "==登陆成功");
        }
        getInstance().mConnect = state;
    }

    @Override
    public void onDisconnect(ECError arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * VoIP呼叫接口
     * @return
     */
    public static ECVoIPCallManager getVoIPCallManager() {
        return ECDevice.getECVoIPCallManager();
    }

    public static ECVoIPSetupManager getVoIPSetManager() {
        return ECDevice.getECVoIPSetupManager();
    }
    
    /**
     * 
     * 是否支持voip及会议功能
     * true 表示支持 false表示不支持
     * 请在sdk初始化完成之后调用
     */
    public boolean isSupportMedia(){
        
        return ECDevice.isSupportMedia();
    }
    
    /**
     * 判断服务是否自动重启
     * @return 是否自动重启
     */
    public static boolean isUIShowing() {
        return ECDevice.isInitialized();
    }
//
//    @Override
//    public void onSwitchCallMediaTypeRequest(String arg0, CallType arg1) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onSwitchCallMediaTypeResponse(String arg0, CallType arg1) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onVideoRatioChanged(VideoRatio arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onCallEvents(VoIPCall voipCall) {
//        // 处理呼叫事件回调
//        if (voipCall == null) {
//            Log.e("SDKCoreHelper", "handle call event error , voipCall null");
//            return;
//        }
//        // 根据不同的事件通知类型来处理不同的业务
//        ECVoIPCallManager.ECCallState callState = voipCall.callState;
//        switch (callState) {
//        case ECCALL_PROCEEDING:
//            Log.i("", "正在连接服务器处理呼叫请求，callid：" + voipCall.callId);
//            break;
//        case ECCALL_ALERTING:
//            Log.i("", "呼叫到达对方，正在振铃，callid：" + voipCall.callId);
//            break;
//        case ECCALL_ANSWERED:
//            Log.i("", "对方接听本次呼叫,callid：" + voipCall.callId);
//            break;
//        case ECCALL_FAILED:
//            // 本次呼叫失败，根据失败原因进行业务处理或跳转
//            Log.i("",
//                    "called:" + voipCall.callId + ",reason:" + voipCall.reason);
//            break;
//        case ECCALL_RELEASED:
//            // 通话释放[完成一次呼叫]
//            break;
//        default:
//            Log.e("SDKCoreHelper",
//                    "handle call event error , callState " + callState);
//            break;
//        }
//    }
//
//    @Override
//    public void onDtmfReceived(String arg0, char arg1) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onMediaDestinationChanged(VoipMediaChangedInfo arg0) {
//        // TODO Auto-generated method stub
//
//    }
}