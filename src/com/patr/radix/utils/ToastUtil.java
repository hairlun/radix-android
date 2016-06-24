package com.patr.radix.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 消息提示
 * 
 * @author huangzhongwen
 * 
 */
public class ToastUtil {

    private ToastUtil() {
    }

    /**
     * 
     * @param context
     * @param text
     */
    public static void showShort(Context context, CharSequence text) {
        showShort(context, text, Gravity.CENTER);
    }

    /**
     * 
     * @param context
     * @param text
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showShort(Context context, CharSequence text,
            int gravity, int xOffset, int yOffset) {
        Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        t.setGravity(gravity, xOffset, yOffset);
        t.show();
    }

    /**
     * 
     * @param context
     * @param text
     * @param gravity
     */
    public static void showShort(Context context, CharSequence text, int gravity) {
        showShort(context, text, gravity, 0, 0);
    }

    /**
     * 
     * @param context
     * @param resId
     */
    public static void showShort(Context context, int resId) {
        showShort(context, resId, Gravity.CENTER);
    }

    /**
     * 
     * @param context
     * @param resId
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showShort(Context context, int resId, int gravity,
            int xOffset, int yOffset) {
        showShort(context, context.getString(resId), gravity, xOffset, yOffset);
    }

    /**
     * 
     * @param context
     * @param resId
     * @param gravity
     */
    public static void showShort(Context context, int resId, int gravity) {
        showShort(context, resId, gravity, 0, 0);
    }

    /**
     * 
     * @param context
     * @param text
     */
    public static void showLong(Context context, CharSequence text) {
        showLong(context, text, Gravity.CENTER);
    }

    /**
     * 
     * @param context
     * @param text
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showLong(Context context, CharSequence text,
            int gravity, int xOffset, int yOffset) {
        Toast t = Toast.makeText(context, text, Toast.LENGTH_LONG);
        t.setGravity(gravity, xOffset, yOffset);
        t.show();
    }

    /**
     * 
     * @param context
     * @param text
     * @param gravity
     */
    public static void showLong(Context context, CharSequence text, int gravity) {
        showLong(context, text, gravity, 0, 0);
    }

    /**
     * 
     * @param context
     * @param resId
     */
    public static void showLong(Context context, int resId) {
        showLong(context, resId, Gravity.CENTER);
    }

    /**
     * 
     * @param context
     * @param resId
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showLong(Context context, int resId, int gravity,
            int xOffset, int yOffset) {
        showLong(context, context.getString(resId), gravity, xOffset, yOffset);
    }

    /**
     * 
     * @param context
     * @param resId
     * @param gravity
     */
    public static void showLong(Context context, int resId, int gravity) {
        showLong(context, resId, gravity, 0, 0);
    }

}
