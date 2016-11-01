package com.patr.radix.utils;

import org.xutils.common.util.LogUtil;

import android.content.SharedPreferences;
import android.os.Build;

/**
 * com.yuntongxun.ecdemo.common.utils in ECDemo_Android
 * Created by Jorstin on 2015/6/23.
 */
public class SupportSwipeModeUtils {

    private static final String TAG = "SupportSwipeModeUtils";

    private static int mode = 0;

    public static boolean isEnable() {
        if(YtxUtils.nullAsNil(Build.VERSION.INCREMENTAL).toLowerCase().contains("flyme")
                || YtxUtils.nullAsNil(Build.DISPLAY).toLowerCase().contains("flyme")) {
            return false;
        }
        return true;
    }
 }
