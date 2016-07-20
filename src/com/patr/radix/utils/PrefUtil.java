/**
 * radix
 * PrefUtil
 * zhoushujie
 * 2016-6-27 下午5:09:11
 */
package com.patr.radix.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * 
 * @author zhoushujie
 *
 */
public class PrefUtil {

    /**
     * 默认数据记录文件名称
     */
    private static final String PREF_FILE = "RADIX_PERSISTANT";

    /**
     * 存储字符串数据
     * 
     * @param context
     *            上下文
     * @param key
     *            存储数据特定的键
     * @param value
     *            存储的数据 （String类型）
     */
    public static boolean save(Context context, String key, String value) {
        return save(PREF_FILE, context, key, value);
    }

    /**
     * 存储字符串数据
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param value
     *            String值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            String value) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获取字符串数据，默认值""
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @return String
     */
    public static String getString(String prefFile, Context context, String key) {
        return getString(prefFile, context, key, "");
    }

    /**
     * 获取字符串数据，默认值""
     * 
     * @param context
     * @param key
     *            键
     * @return String
     */
    public static String getString(Context context, String key) {
        return getString(PREF_FILE, context, key, "");
    }
    
    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getString(PREF_FILE, context, key, defValue);
    }

    /**
     * 获取字符串数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return String
     */
    public static String getString(String prefFile, Context context,
            String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getString(key, defValue);
    }

}
