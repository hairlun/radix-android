package com.patr.radix.ui.visitor;

import org.xutils.common.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.patr.radix.R;
import com.patr.radix.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VoIPCallUserInfo;

public class VoIPCallActivity extends ECVoIPBaseActivity {

    private static final String TAG = "VoIPCallActivity";
	private boolean isCallBack;
	private Context context;

    @Override
    protected int getLayoutId() {
        return R.layout.ec_call_interface;
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initCall();
        isCreated=true;
        
    }

    private void initCall() {
        if(mIncomingCall) {
            // 来电
            mCallId = getIntent().getStringExtra(ECDevice.CALLID);
            LogUtil.e("mCallId----"+mCallId);
            mCallNumber = getIntent().getStringExtra(ECDevice.CALLER);
        } else {
            // 呼出
            mCallName = getIntent().getStringExtra(EXTRA_CALL_NAME);
            mCallNumber = getIntent().getStringExtra(EXTRA_CALL_NUMBER);

            isCallBack = getIntent().getBooleanExtra(ACTION_CALLBACK_CALL, false);

        }

        initView();
        if (!mIncomingCall) {
            // 处理呼叫逻辑
            if (TextUtils.isEmpty(mCallNumber)) {
                ToastUtil.showShort(context, "呼叫的号码错误");
                finish();
                return;
            }

            if (isCallBack) {
                VoIPCallHelper.makeCallBack(CallType.VOICE, mCallNumber);
            } else {
                //ECDevice.getECVoIPCallManager().setProcessDataEnabled(null , true , true , this);
                mCallId = VoIPCallHelper.makeCall(mCallType, mCallNumber);
                if (TextUtils.isEmpty(mCallId)) {
                    ToastUtil.showShort(context, "无法连接服务器，请稍后再试");
                    LogUtil.d("Call fail, callId " + mCallId);
                    finish();
                    return;
                }
            }
            mCallHeaderView .setCallTextMsg("正在连接服务器…");
        } else {
            mCallHeaderView.setCallTextMsg(" ");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
    	if(!isCreated){
         super.onNewIntent(intent);
         initCall();
    	}
    }
    private boolean isCreated=false;

    private void initView() {
        mCallHeaderView = (ECCallHeadUILayout) findViewById(R.id.call_header_ll);
        mCallControlUIView = (ECCallControlUILayout) findViewById(R.id.call_control_ll);
        mCallControlUIView.setOnCallControlDelegate(this);
        mCallHeaderView.setCallName(mCallName);
        mCallHeaderView.setCallNumber(TextUtils.isEmpty(mPhoneNumber) ? mCallNumber : mPhoneNumber);
        mCallHeaderView.setCalling(false);

        ECCallControlUILayout.CallLayout callLayout = mIncomingCall ? ECCallControlUILayout.CallLayout.INCOMING
                : ECCallControlUILayout.CallLayout.OUTGOING;
        mCallControlUIView.setCallDirect(callLayout);
    }


    @Override
    protected boolean isEnableSwipe() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreated=false;

    }

    /**
     * 连接到服务器
     * @param callId 通话的唯一标识
     */
    @Override
    public void onCallProceeding(String callId) {
        if(mCallHeaderView == null || !needNotify(callId)) {
            return ;
        }
        LogUtil.d("onUICallProceeding:: call id " + callId);
        mCallHeaderView.setCallTextMsg("正在呼叫对方，请稍候…");
    }

    /**
     * 连接到对端用户，播放铃音
     * @param callId 通话的唯一标识
     */
    @Override
    public void onCallAlerting(String callId) {
        if(!needNotify(callId) || mCallHeaderView == null) {
            return ;
        }
        LogUtil.d("onUICallAlerting:: call id " + callId);
        mCallHeaderView.setCallTextMsg("等待对方接听…");
        mCallControlUIView.setCallDirect(ECCallControlUILayout.CallLayout.ALERTING);
    }

    /**
     * 对端应答，通话计时开始
     * @param callId 通话的唯一标识
     */
    @Override
    public void onCallAnswered(final String callId) {
        if(!needNotify(callId)|| mCallHeaderView == null) {
            return ;
        }
        LogUtil.d("onUICallAnswered:: call id " + callId);
        mCallHeaderView.setCalling(true);
        isConnect = true;


//         boolean p =  isVoicePermission();
//        Log.e("aaaaaaaaaaaaa","aa"+p);
        
        
    }
    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();


            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onMakeCallFailed(String callId , int reason) {
        if(mCallHeaderView == null || !needNotify(callId)) {
            return ;
        }
        LogUtil.d("onUIMakeCallFailed:: call id " + callId + " ,reason " + reason);
        mCallHeaderView.setCalling(false);
        isConnect = false;
        mCallHeaderView.setCallTextMsg(CallFailReason.getCallFailReason(reason));
        if(reason != SdkErrorCode.REMOTE_CALL_BUSY && reason != SdkErrorCode.REMOTE_CALL_DECLINED) {
            VoIPCallHelper.releaseCall(mCallId);
            finish();
        }
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }

    /**
     * 通话结束，通话计时结束
     * @param callId 通话的唯一标识
     */
    @Override
    public void onCallReleased(String callId) {
        if(mCallHeaderView == null || !needNotify(callId)) {
            return ;
        }
        LogUtil.d("onUICallReleased:: call id " + callId);
        mCallHeaderView.setCalling(false);
        isConnect = false;
        mCallHeaderView.setCallTextMsg("通话结束");
        mCallControlUIView.setControlEnable(false);
        finish();
    }

	@Override
	public void onMakeCallback(ECError ecError, String caller, String called) {
		if(!TextUtils.isEmpty(mCallId)) {
			return ;
		}
		if(ecError.errorCode != SdkErrorCode.REQUEST_SUCCESS) {
			mCallHeaderView .setCallTextMsg("回拨呼叫失败[" + ecError.errorCode + "]");
		} else {
			mCallHeaderView .setCallTextMsg("回拨呼叫成功，请注意接听系统来电!");
		}
		mCallHeaderView.setCalling(false);
        isConnect = false;
        mCallControlUIView.setControlEnable(false);
		finish();
	}

   /* @Override
    public byte[] onCallProcessData(byte[] inByte, boolean upLink) {

        ECLogger.d("upLink audio %b " , upLink);
        return inByte;
    }

    @Override
    public byte[] onCallProcessVideoData(byte[] inByte, boolean upLink) {
        ECLogger.d("upLink video %b " , upLink);
        return inByte;
    }*/
}
