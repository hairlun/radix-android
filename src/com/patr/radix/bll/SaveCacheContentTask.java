package com.patr.radix.bll;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import org.xutils.common.util.LogUtil;

import com.patr.radix.dal.CacheDAOImpl;
import com.patr.radix.dal.CacheDAOImpl.Key;
import com.patr.radix.network.BaseAsyncTask;
import com.patr.radix.network.RequestListener;

/**
 * 保存缓存数据
 * 
 * @author huangzhongwen 2014-9-3 上午10:52:20
 */
public class SaveCacheContentTask extends BaseAsyncTask<Boolean> {

    Context context;
    String url;
    String content;

    public SaveCacheContentTask(RequestListener<Boolean> callback,
            Context context, String url, String content) {
        super(callback);
        this.context = context;
        this.url = url;
        this.content = content;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        boolean result = false;
        try {
            LogUtil.i("url = " + url);
            LogUtil.i("content = " + content);
            List<ContentValues> values = new ArrayList<ContentValues>();
            String[] keys = { Key.URL, Key.CONTENT };
            String[] value = { url, content };
            values.add(CacheManager.createContentValues(keys, value));
            result = new CacheDAOImpl(context).saveCache(Key.TB, values,
                    Key.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
