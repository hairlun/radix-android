/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.patr.radix.ui.visitor;

import java.util.ArrayList;
import java.util.List;

import org.xutils.common.util.LogUtil;

import com.patr.radix.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 界面处理
 * Created by Jorstin on 2015/3/17.
 */
public abstract class CCPActivityBase {

    private FragmentActivity mActionBarActivity;

    private AudioManager mAudioManager ;

    /**
     * CCPActivity root view
     */
    private View mContentView;

    private LayoutInflater mLayoutInflater;

    /**
     * CCPActivity root View container
     */
    private FrameLayout mContentFrameLayout;

    /**
     * Manager dialog.
     */
    private List<Dialog> mAppDialogCache ;

    /**
     * The volume of music
     */
    private int mMusicMaxVolume;
    public View mBaseLayoutView;
    private View mTransLayerView;

    public void init(Context context , FragmentActivity activity)  {
        mActionBarActivity = activity;
        onInit();

        mAudioManager = AudioManagerTools.getInstance().getAudioManager();
        mMusicMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int layoutId = getLayoutId();
        mLayoutInflater = LayoutInflater.from(mActionBarActivity);
        mBaseLayoutView = mLayoutInflater.inflate(R.layout.ccp_activity, null);
        mTransLayerView = mBaseLayoutView.findViewById(R.id.ccp_trans_layer);
        LinearLayout mRootView = (LinearLayout) mBaseLayoutView.findViewById(R.id.ccp_root_view);
        mContentFrameLayout = (FrameLayout) mBaseLayoutView.findViewById(R.id.ccp_content_fl);

        if (layoutId != -1) {

            mContentView = getContentLayoutView();
            if(mContentView == null) {
                mContentView = mLayoutInflater.inflate(getLayoutId(), null);
            }
            mRootView.addView(mContentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }

        dealContentView(mBaseLayoutView);

        CCPLayoutListenerView listenerView = (CCPLayoutListenerView) mActionBarActivity.findViewById(R.id.ccp_content_fl);
        if (listenerView != null && mActionBarActivity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
            listenerView.setOnSizeChangedListener(new CCPLayoutListenerView.OnCCPViewSizeChangedListener() {

                @Override
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    LogUtil.d("oldh - h = " + (oldh - h));
                }
            });

        }
    }

    /**
     *
     * @return
     */
    public View getActivityLayoutView() {
        return mContentView;
    }

    public View getContentView() {
        return mBaseLayoutView;
    }

    /**
     *
     * @return
     */
    public FragmentActivity getFragmentActivity() {
        return mActionBarActivity;
    }


    /**
     *
     * @param contentDescription
     */
    public final void setActionContentDescription(CharSequence contentDescription) {
        if(TextUtils.isEmpty(contentDescription)) {
            return;
        }
        String description = "当前所在页面," + contentDescription;
        mActionBarActivity.getWindow().getDecorView().setContentDescription(description);
    }


    /**
     *
     */
    public void toggleSoftInput() {
        final FragmentActivity activity = mActionBarActivity;
        // Display the soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = activity.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                inputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * hide input method.
     */
    public void hideSoftKeyboard(View view) {
        if (view == null) {
            return;
        }


        InputMethodManager inputMethodManager = (InputMethodManager) mActionBarActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            IBinder localIBinder = view.getWindowToken();
            if (localIBinder != null)
                inputMethodManager.hideSoftInputFromWindow(localIBinder, 0);
        }
    }

    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mActionBarActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = mActionBarActivity.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    public final void setRootConsumeWatcher(CCPLayoutListenerView layoutListenerView) {
        if (!(this.mContentFrameLayout instanceof CCPLayoutListenerView)) {
            return;
        }
        ((CCPLayoutListenerView) this.mContentFrameLayout).setRootConsumeWatcher();
    }

    /**
     *
     * @return
     */
    public int getStreamMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     *
     * @return
     */
    public int getStreamVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)
                && mAudioManager != null) {
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            if(streamVolume >= mMusicMaxVolume) {
                LogUtil.d("has set the max volume");
                return true;
            }

            int mean = mMusicMaxVolume / 7;
            if(mean == 0) {
                mean = 1;
            }

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    streamVolume + mean, AudioManager.FLAG_PLAY_SOUND
                            | AudioManager.FLAG_SHOW_UI);
        }
        if((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                && mAudioManager != null) {
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int mean = mMusicMaxVolume / 7;
            if(mean == 0) {
                mean = 1;
            }

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    streamVolume - mean, AudioManager.FLAG_PLAY_SOUND
                            | AudioManager.FLAG_SHOW_UI);
            return true;
        }
        return false;
    }

    /**
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
			/*if(mOverFlowAction != null && mOverFlowAction.isEnabled()) {
				callMenuCallback(mOverFlowMenuItem, mOverFlowAction);
				return true;
			}*/
        }
        return false;
    }

    public void onResume(){

    }

    public void onPause(){

    }

    /**
     *
     */
    public void onDestroy() {
        releaseDialogList();
        mAudioManager = null;
    }

    /**
     *
     */
    private void releaseDialogList() {
        if(mAppDialogCache == null) {
            return;
        }

        for(Dialog dialog : mAppDialogCache) {
            if(dialog == null || !dialog.isShowing()) {
                continue;
            }
            dialog.dismiss();
        }
        mAppDialogCache.clear();
        mAppDialogCache = null;
    }

    protected abstract void onInit();

    /**
     * The sub Activity implement, set the Ui Layout
     * @return
     */
    protected abstract int getLayoutId();
    protected abstract View getContentLayoutView();
    protected abstract String getClassName();

    /**
     *
     */
    protected abstract void dealContentView(View contentView);

    public void addDialog(Dialog dialog) {
        if(dialog == null) {
            return;
        }

        if(mAppDialogCache == null) {
            mAppDialogCache = new ArrayList<Dialog>();
        }
        mAppDialogCache.add(dialog);
    }

    /**
     *
     * @param padding
     * @param iconRes
     * @return
     */
    private VerticalImageSpan getTitleIconTips(int padding , int iconRes) {
        Drawable drawable = mActionBarActivity.getResources().getDrawable(iconRes);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
        imageSpan.setPadding((drawable.getIntrinsicHeight() - padding) / 2);
        return imageSpan;
    }

    /**
     *
     */
    public final void invalidateActionMenu() {
        mActionBarActivity.supportInvalidateOptionsMenu();
    }

    public void setScreenEnable(boolean screenEnable) {

    }

    public void onStart() {

    }
}
