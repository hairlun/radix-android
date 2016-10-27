package com.patr.radix.bll;

import java.io.File;

import org.xutils.common.util.LogUtil;

import com.patr.radix.App;
import com.patr.radix.bll.ServiceManager.Url;
import com.patr.radix.dal.CacheDAOImpl;
import com.patr.radix.dal.CacheDAOImpl.Key;
import com.patr.radix.network.IAsyncListener.ResultParser;
import com.patr.radix.network.RequestListener;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;

public class CacheManager {

    public static final File CACHE_DIR = new File(Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/radix/cache");

    public static String getCommunityListUrl() {
        return Url.COMMUNITY_LIST;
    }

    public static String getLockListUrl() {
        return String.format("%s%stoken=%s", App.instance
                .getSelectedCommunity().getUrl(), Url.LOCK_LIST,
                App.instance.getUserInfo().getToken());
    }

    public static String getUserListUrl() {
        return String.format("%s%stoken=%s", App.instance
                .getSelectedCommunity().getUrl(), Url.USER_LIST,
                App.instance.getUserInfo().getToken());
    }

    /**
     * 保存缓存内容
     * 
     * @param context
     * @param url
     * @param content
     * @param callback
     */
    public static void saveCacheContent(Context context, String url,
            String content, RequestListener<Boolean> callback) {
        new SaveCacheContentTask(callback, context, url, content).execute();
    }

    /**
     * 删除缓存的URL记录
     * 
     * @param context
     * @param url
     * @return
     */
    public static boolean deleteCache(Context context, String url) {
        boolean result = false;
        try {
            result = new CacheDAOImpl(context).delete(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取缓存内容
     * 
     * @param context
     * @param url
     * @return
     */
    private static String getCacheContent(Context context, String url) {
        String result = null;
        try {
            LogUtil.i(url);
            result = new CacheDAOImpl(context).getContent(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 异步获取缓存数据
     * 
     * @param context
     * @param url
     * @param callback
     * @param parser
     */
    public static <T> void getCacheContent(Context context, String url,
            RequestListener<T> callback, ResultParser<T> parser) {
        new GetCacheContentTask<T>(callback, url, context, parser).execute();
    }

    /**
     * 清空缓存的URL记录
     * 
     * @param context
     * @return
     */
    public static boolean clearCache(Context context) {
        boolean result = false;
        try {
            result = new CacheDAOImpl(context).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * @param keys
     * @param values
     * @return
     */
    public static ContentValues createContentValues(String[] keys,
            String[] values) {
        ContentValues cv = new ContentValues();
        try {
            if (keys.length > values.length) {
                for (int i = 0; i < keys.length; i++) {
                    if (i < values.length) {
                        cv.put(keys[i], values[i]);
                    } else {
                        cv.put(keys[i], "");
                    }
                }
            } else {
                for (int i = 0; i < keys.length; i++) {
                    cv.put(keys[i], values[i]);
                }
            }
            cv.put(Key.M_TIME, String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            cv = null;
        }
        return cv;
    }

}
