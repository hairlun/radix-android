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
 */
package com.patr.radix.ui.visitor;

import java.io.IOException;

import org.xutils.common.util.LogUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Looper;

import com.patr.radix.App;
import com.patr.radix.R;
import com.patr.radix.utils.NotificationUtil;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

/**
 * 状态栏通知
 * 
 * @author Jorstin Chan@容联•云通讯
 * @date 2015-1-4
 * @version 4.0
 */
public class ECNotificationManager {

    public static final int CCP_NOTIFICATOIN_ID_CALLING = 0x1;

    public static final int NOTIFY_ID_PUSHCONTENT = 35;

    private Context mContext;

    private static NotificationManager mNotificationManager;

    public static ECNotificationManager mInstance;

    public static ECNotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new ECNotificationManager(
                    App.instance.getApplicationContext());
        }

        return mInstance;
    }

    MediaPlayer mediaPlayer = null;

    public void playNotificationMusic(String voicePath) throws IOException {
        // paly music ...
        AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(
                voicePath);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mediaPlayer.prepare();
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    private ECNotificationManager(Context context) {
        mContext = context;
    }

    /**
     * 
     * @param contex
     * @param fromUserName
     * @param msgType
     * @return
     */
    public final String getTickerText(Context contex, String fromUserName,
            int msgType) {
        if (msgType == ECMessage.Type.TXT.ordinal()) {
            return fromUserName + "发来1条消息";
        } else if (msgType == ECMessage.Type.IMAGE.ordinal()) {
            return fromUserName + "发来1张图片";
        } else if (msgType == ECMessage.Type.VOICE.ordinal()) {
            return fromUserName + "发来1段语音";
        } else if (msgType == ECMessage.Type.FILE.ordinal()) {
            return fromUserName + "发来1个文件";
        } else {
            return contex.getPackageManager()
                    .getApplicationLabel(contex.getApplicationInfo())
                    .toString();
        }

    }

    public final String getContentTitle(Context context,
            int sessionUnreadCount, String fromUserName) {
        if (sessionUnreadCount > 1) {
            return context.getString(R.string.app_name);
        }

        return fromUserName;
    }

    private void cancel() {
        NotificationManager notificationManager = (NotificationManager) App.instance
                .getApplicationContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        notificationManager.cancel(0);
    }

    /**
     * 取消所有的状态栏通知
     */
    public final void forceCancelNotification() {
        cancel();
        NotificationManager notificationManager = (NotificationManager) App.instance
                .getApplicationContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        notificationManager.cancel(NOTIFY_ID_PUSHCONTENT);

    }

    public final Looper getLooper() {
        return Looper.getMainLooper();
    }

    /**
     * 后台呈现音视频呼叫Notification
     * 
     * @param callType
     *            呼叫类型
     */
    public static void showCallingNotification(
            ECVoIPCallManager.CallType callType) {
        try {
            Intent intent;
            // if(callType == ECVoIPCallManager.CallType.VIDEO) {
            intent = new Intent(ECVoIPBaseActivity.ACTION_VIDEO_CALL);
            // } else {
            // intent = new Intent(ECVoIPBaseActivity.ACTION_VOICE_CALL);
            // }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    getInstance().mContext, R.string.app_name, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            getInstance().checkNotification();
            String topic = "正在通话中, 轻击以继续";
            Notification.Builder builder = new Notification.Builder(
                    App.instance.getApplicationContext());
            builder.setLights(-16711936, 300, 1000)
                    .setSmallIcon(R.drawable.title_bar_logo).setTicker(topic)
                    .setContentTitle("[通话]").setContentText(topic)
                    .setContentIntent(contentIntent);

            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(CCP_NOTIFICATOIN_ID_CALLING,
                    notification);
            mNotificationManager.cancel(NOTIFY_ID_PUSHCONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

    }

    public static void cancelCCPNotification(int id) {
        getInstance().checkNotification();
        mNotificationManager.cancel(id);
    }
}
