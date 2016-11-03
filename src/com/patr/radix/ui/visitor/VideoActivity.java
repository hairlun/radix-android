package com.patr.radix.ui.visitor;

import org.xutils.common.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patr.radix.R;
import com.patr.radix.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.voip.video.ECCaptureView;
import com.yuntongxun.ecsdk.voip.video.OnCameraInitListener;

public class VideoActivity extends ECVoIPBaseActivity implements View.OnClickListener{

    private Context context;
    private Button mVideoStop;
    private Button mVideoBegin;
    private Button mVideoCancle;
    private ImageView mVideoIcon;
    private RelativeLayout mVideoTipsLy;
    public LinearLayout daiLayout;

    private TextView mVideoTopTips;
    private TextView mVideoCallTips;
    private TextView mCallStatus;
    private SurfaceView mVideoView;
    private ECCaptureView mCaptureView;
    // Remote Video
    private FrameLayout mVideoLayout;
    private Chronometer mChronometer;

    private View mCameraSwitch;
    private View video_switch;

    @Override
    protected int getLayoutId() {
        return R.layout.ec_video_call;
    }

    @Override
    protected boolean isEnableSwipe() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initVideoLayout();
        isCreated=true;
    }

    private void initVideoLayout() {
        if(mIncomingCall) {
            // 来电
            mCallId = getIntent().getStringExtra(ECDevice.CALLID);
            mCallNumber = getIntent().getStringExtra(ECDevice.CALLER);
        } else {
            // 呼出
            mCallName = getIntent().getStringExtra(EXTRA_CALL_NAME);
            mCallNumber = getIntent().getStringExtra(EXTRA_CALL_NUMBER);
        }

        initResourceRefs();

        ECDevice.getECVoIPSetupManager().setVideoView(mVideoView, mCaptureView);

        if(!mIncomingCall) {
            mVideoTopTips.setText(R.string.ec_voip_call_connecting_server);
            mCallId = VoIPCallHelper.makeCall(mCallType, mCallNumber);
        } else {
            mVideoCancle.setVisibility(View.GONE);
            mVideoTipsLy.setVisibility(View.VISIBLE);
            mVideoBegin.setVisibility(View.VISIBLE);
            mVideoTopTips.setText((mCallName == null ? mCallNumber : mCallName) + getString(R.string.ec_voip_invited_video_tip));
            mVideoTopTips.setVisibility(View.VISIBLE);
        }

        if(mIncomingCall){
        	mVideoStop.setEnabled(true);
        }
    }

    private void initResourceRefs() {

        mVideoTipsLy = (RelativeLayout) findViewById(R.id.video_call_in_ly);
        mVideoIcon = (ImageView) findViewById(R.id.video_icon);

        mVideoTopTips = (TextView) findViewById(R.id.notice_tips);
        mVideoCallTips = (TextView) findViewById(R.id.video_call_tips);
        mVideoCancle = (Button) findViewById(R.id.video_botton_cancle);
        mVideoBegin = (Button) findViewById(R.id.video_botton_begin);
        mVideoStop = (Button) findViewById(R.id.video_stop);
        
        mVideoStop.setEnabled(false);

        mVideoCancle.setOnClickListener(this);
        mVideoBegin.setOnClickListener(this);
        mVideoStop.setOnClickListener(this);

        mVideoView = (SurfaceView) findViewById(R.id.video_view);
        mVideoView.setVisibility(View.INVISIBLE);
        mCaptureView = (ECCaptureView) findViewById(R.id.localvideo_view);
        mCaptureView.setOnCameraInitListener(new OnCameraInitListener() {
            @Override
            public void onCameraInit(boolean result) {
                if(!result)ToastUtil.showShort(context, "摄像头被占用");
            }
        });
        mCaptureView.setZOrderMediaOverlay(true);
        // mVideoView.setVisibility(View.INVISIBLE);
        mVideoLayout = (FrameLayout) findViewById(R.id.Video_layout);
        mCameraSwitch = findViewById(R.id.camera_switch);
        mCameraSwitch.setOnClickListener(this);
        video_switch = findViewById(R.id.video_switch);
        video_switch.setOnClickListener(this);

        mCallStatus = (TextView) findViewById(R.id.call_status);
        mCallStatus.setVisibility(View.GONE);
        // mVideoView.getHolder().setFixedSize(width, height);

        // SurfaceView localView = ViERenderer.CreateLocalRenderer(this);
        // mLoaclVideoView.addView(localView);
    }

    private void initResVideoSuccess() {
        isConnect = true;
        mVideoLayout.setVisibility(View.VISIBLE);
        mVideoIcon.setVisibility(View.GONE);
        mVideoTopTips.setVisibility(View.GONE);
        mCameraSwitch.setVisibility(View.VISIBLE);
        mVideoTipsLy.setVisibility(View.VISIBLE);
        mVideoBegin.setVisibility(View.GONE);
        // bottom ...
        mVideoCancle.setVisibility(View.GONE);
        mVideoCallTips.setVisibility(View.VISIBLE);
        mVideoCallTips.setText(getString(R.string.str_video_bottom_time, mCallNumber));
        mVideoStop.setVisibility(View.VISIBLE);
        mVideoStop.setEnabled(true);

        mCaptureView.setVisibility(View.VISIBLE);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.start();

    }
    

    /**
     * 根据状态,修改按钮属性及关闭操作
     */
    private void finishCalling() {
        try {
            // mChronometer.setVisibility(View.GONE);

            mVideoTopTips.setVisibility(View.VISIBLE);
            mCameraSwitch.setVisibility(View.GONE);
            mVideoTopTips.setText(R.string.ec_voip_calling_finish);

            if (isConnect) {
                // set Chronometer view gone..
                mChronometer.stop();
                mVideoLayout.setVisibility(View.GONE);
                mVideoIcon.setVisibility(View.VISIBLE);

                mCaptureView.setVisibility(View.GONE);

                // bottom can't click ...
                mVideoStop.setEnabled(false);
            } else {
                mVideoCancle.setEnabled(false);
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isConnect = false;
        }
    }

    private void finishCalling(int reason) {
        try {
            mVideoTopTips.setVisibility(View.VISIBLE);
            mCameraSwitch.setVisibility(View.GONE);
            mCaptureView.setVisibility(View.GONE);
            if (isConnect) {
                mChronometer.stop();
                mVideoLayout.setVisibility(View.GONE);
                mVideoIcon.setVisibility(View.VISIBLE);
                isConnect = false;
                // bottom can't click ...
                mVideoStop.setEnabled(false);
            } else {
                mVideoCancle.setEnabled(false);
            }
            isConnect = false;
            mVideoTopTips.setText(CallFailReason.getCallFailReason(reason));
            VoIPCallHelper.releaseCall(mCallId);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCallType == ECVoIPCallManager.CallType.VIDEO) {
            if(mCaptureView != null) {
                mCaptureView.onResume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoIPCallHelper.mHandlerVideoCall = false;
        isCreated=false;
    }

    @Override
    public void onCallProceeding(String callId) {
        if (callId != null && callId.equals(mCallId)) {
            mVideoTopTips.setText(getString(R.string.ec_voip_call_connect));
        }
    }

    @Override
    public void onCallAlerting(String callId) {
        if (callId != null && callId.equals(mCallId)) {// 等待对方接受邀请...
            mVideoTopTips.setText(getString(R.string.str_tips_wait_invited));
        }
    }

    @Override
    public void onCallAnswered(String callId) {
        if (callId != null && callId.equals(mCallId) && !isConnect) {
            initResVideoSuccess();
            if(ECDevice.getECVoIPSetupManager() != null) {
                ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
            }
        }
    }

    @Override
    public void onMakeCallFailed(String callId, int reason) {
        if (callId != null && callId.equals(mCallId)) {
            finishCalling(reason);

        }
    }

    @Override
    public void onCallReleased(String callId) {
        if (callId != null && callId.equals(mCallId)) {
        	VoIPCallHelper.releaseMuteAndHandFree();
            finishCalling();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_botton_begin:
                VoIPCallHelper.acceptCall(mCallId);
                break;

            case R.id.video_stop:
            case R.id.video_botton_cancle:

                doHandUpReleaseCall();
                break;
            case R.id.camera_switch:
                mCameraSwitch.setEnabled(false);
                if(mCaptureView != null) {
                    mCaptureView.switchCamera();
                }
                mCameraSwitch.setEnabled(true);

//                ECDevice.getECVoIPSetupManager().selectCamera(0,3,15, ECVoIPSetupManager.Rotate.ROTATE_90,true);
                break;
        }
    }
	
    protected void doHandUpReleaseCall() {

        // Hang up the video call...
        LogUtil.d("[VideoActivity] onClick: Voip talk hand up, CurrentCallId " + mCallId);
        try {
            if (mCallId != null) {
            	
            	if(mIncomingCall&&!isConnect){
            		VoIPCallHelper.rejectCall(mCallId);
            	}else {
                	VoIPCallHelper.releaseCall(mCallId);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isConnect) {
            finish();
        }
    }
    
	@Override
	public void onMakeCallback(ECError arg0, String arg1, String arg2) {

	}
	
	public boolean isCreated=false;

    @Override
	protected void onNewIntent(Intent intent) {
    	
    	if(!isCreated){
        setIntent(intent);
        super.onNewIntent(intent);
        initVideoLayout();
    	}
    }

    /**
     * 远端视频分辨率到达，标识收到视频图像
     * @param videoRatio 视频分辨率信息
     */
    @Override
    public void onVideoRatioChanged(VideoRatio videoRatio) {
        super.onVideoRatioChanged(videoRatio);
        /*if(mVideoView != null && videoRatio != null) {
            mVideoView.getHolder().setFixedSize(videoRatio.getWidth() , videoRatio.getHeight());
        }*/
        if(videoRatio == null) {
            return ;
        }
        int width = videoRatio.getWidth();
        int height = videoRatio.getHeight();
        if(width == 0 || height == 0) {
            LogUtil.e("invalid video width(" + width + ") or height(" + height + ")");
            return ;
        }
        mVideoView.setVisibility(View.VISIBLE);
        if(width > height) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            int w = mSurfaceViewWidth * height / width;
            int margin = (mSurfaceViewHeight - mVideoTipsLy.getHeight() - w) / 2;
            LogUtil.d("margin:" + margin);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, margin, 0, margin);
            mVideoView.setLayoutParams(lp);
        }
    }
}
