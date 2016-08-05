/**
 * radix
 * PrefUtil
 * zhoushujie
 * 2016-6-27 下午5:09:11
 */
package com.patr.radix.utils;

import com.patr.radix.bean.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
     * @param userInfo
     */
    public static void saveUserInfo(Context context, UserInfo userInfo) {
        save(context, Constants.PREF_ACCOUNT, userInfo.getAccount());
        save(context, Constants.PREF_NAME, userInfo.getName());
        save(context, Constants.PREF_AREA_ID, userInfo.getAreaId());
        save(context, Constants.PREF_AREA_NAME, userInfo.getAreaName());
        save(context, Constants.PREF_MOBILE, userInfo.getMobile());
        save(context, Constants.PREF_HOME, userInfo.getHome());
        save(context, Constants.PREF_TOKEN, userInfo.getToken());
    }
    
    public static UserInfo getUserInfo(Context context) {
        String account = getString(context, Constants.PREF_ACCOUNT);
        String name = getString(context, Constants.PREF_NAME);
        String areaId = getString(context, Constants.PREF_AREA_ID);
        String areaName = getString(context, Constants.PREF_AREA_NAME);
        String mobile = getString(context, Constants.PREF_MOBILE);
        String home = getString(context, Constants.PREF_HOME);
        String token = getString(context, Constants.PREF_TOKEN);
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount(account);
        userInfo.setName(name);
        userInfo.setAreaId(areaId);
        userInfo.setAreaName(areaName);
        userInfo.setMobile(mobile);
        userInfo.setHome(home);
        userInfo.setToken(token);
        return userInfo;
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
