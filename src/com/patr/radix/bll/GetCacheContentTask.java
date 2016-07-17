package com.patr.radix.bll;

import android.content.Context;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import com.patr.radix.dal.CacheDAOImpl;
import com.patr.radix.network.BaseAsyncTask;
import com.patr.radix.network.IAsyncListener.ResultParser;
import com.patr.radix.network.RequestListener;

/**
 * 获取缓存数据
 * 
 * @author huangzhongwen 2014-9-3 上午10:52:31
 * @param <T>
 */
public class GetCacheContentTask<T> extends BaseAsyncTask<T> {

    String url;
    Context context;
    ResultParser<T> parser;

    public GetCacheContentTask(RequestListener<T> callback, String url,
            Context context, ResultParser<T> parser) {
        super(callback);
        this.url = url;
        this.context = context;
        this.parser = parser;
    }

    @Override
    protected T doInBackground(Object... params) {
        String content = null;
        try {
            LogUtil.i(url);
            content = new CacheDAOImpl(context).getContent(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(content) && !"null".equals(content)) {
            LogUtil.i(content);
            return parser.parse(content);
        }
        return null;
    }

}
