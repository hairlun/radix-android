/**
 * radix
 * YtxUtils
 * zhoushujie
 * 2016-11-1 下午2:43:56
 */
package com.patr.radix.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.xutils.common.util.LogUtil;

import com.patr.radix.ui.visitor.MethodInvoke;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author zhoushujie
 * 
 */
public class YtxUtils {

    public static String getLastwords(String srcText, String p) {
        try {
            String[] array = TextUtils.split(srcText, p);
            int index = (array.length - 1 < 0) ? 0 : array.length - 1;
            return array[index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity,
            MethodInvoke.SwipeInvocationHandler handler) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains(
                        "TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }

            Object proxy = Proxy
                    .newProxyInstance(
                            translucentConversionListenerClazz.getClassLoader(),
                            new Class[] { translucentConversionListenerClazz },
                            handler);
            LogUtil.d("proxy " + proxy);
            if (Build.VERSION.SDK_INT < 21) {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent",
                        new Class[] { translucentConversionListenerClazz });
                method.setAccessible(true);
                method.invoke(activity, new Object[] { proxy });
            } else {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent", new Class[] {
                                translucentConversionListenerClazz,
                                ActivityOptions.class });
                method.setAccessible(true);
                method.invoke(activity, new Object[] { proxy, null });
            }

        } catch (Throwable t) {
            LogUtil.e(Log.getStackTraceString(t));
        }
    }

    /**
     * 过滤字符串为空
     * 
     * @param str
     * @return
     */
    public static String nullAsNil(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

}
