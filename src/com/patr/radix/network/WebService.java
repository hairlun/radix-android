package com.patr.radix.network;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import org.xutils.HttpManager;
import org.xutils.x;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import com.patr.radix.network.IAsyncListener.ResultParser;

/**
 * Web服务
 * 
 * @author huangzhongwen
 * 
 */
public class WebService {

    /**
     * 
     */
    public static final String TAG = WebService.class.getName();

    /**
     * 服务器地址
     */
    public static String URL;

    /**
     * 异步HTTP请求工具，30S超时
     */
    public static final HttpManager HTTP = x.http();

    /** 连接超时时间 */
    public static final int TIMEOUT = 30000;

    /**
     * 私有构造
     */
    private WebService() {
    }

    /**
     * POST请求
     * 
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param listener
     *            请求监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable post(String url, String[] keys,
            String[] values, final RequestListener<T> listener,
            final ResultParser<T> parser) {
        String requestUrl;
        if (!url.startsWith("http")) {
            requestUrl = URL + url;
        } else {
            requestUrl = url;
        }
        RequestParams params = createParams(keys, values, requestUrl);
        return request(HttpMethod.POST, params, listener, parser);
    }

    /**
     * POST请求
     * 
     * @param url
     *            请求地址
     * @param listener
     *            请求监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable post(String url,
            final RequestListener<T> listener, final ResultParser<T> parser) {
        return post(url, null, null, listener, parser);
    }

    /**
     * GET请求
     * 
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param listener
     *            请求监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable get(String url, String[] keys,
            String[] values, final RequestListener<T> listener,
            final ResultParser<T> parser) {
        String requestUrl = URL + url;
        RequestParams params = createParams(keys, values, requestUrl);
        return request(HttpMethod.GET, params, listener, parser);
    }

    /**
     * Get请求
     * 
     * @param url
     *            请求地址
     * @param listener
     *            请求监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable get(String url,
            final RequestListener<T> listener, final ResultParser<T> parser) {
        return get(url, null, null, listener, parser);
    }

    /**
     * Http请求
     * 
     * @param method
     *            请求方式
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param listener
     *            监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable request(HttpMethod method,
            final RequestParams params, final RequestListener<T> listener,
            final ResultParser<T> parser) {
        if (params != null) {
            params.setConnectTimeout(TIMEOUT);
            params.setHeader("User-Agent", "android");
        }
        return HTTP.request(method, params, new ProgressCallback<String>() {

            @Override
            public void onCancelled(CancelledException e) {
                if (listener != null) {
                    listener.onStopped();
                }
            }

            @Override
            public void onError(Throwable t, boolean isOnCallback) {
                if (listener != null) {
                    listener.onFailure((Exception) t, "解析结果异常");
                }
                StringBuilder urlStr = new StringBuilder(params.getUri());
                try {
                    List<KeyValue> keyValues = params.getBodyParams();
                    for (KeyValue kv : keyValues) {
                        urlStr.append("&");
                        urlStr.append(kv.key + "=" + kv.getValueStr());
                    }
                    LogUtil.i(URLDecoder.decode(urlStr.toString(), "utf-8")
                            + "->onFailure():Exception=" + t.getMessage()
                            + ",content=" + "解析结果异常");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onStopped();
                }
            }

            @Override
            public void onSuccess(String response) {
                StringBuilder urlStr = new StringBuilder(params.getUri());
                try {
                    List<KeyValue> keyValues = params.getBodyParams();
                    for (KeyValue kv : keyValues) {
                        urlStr.append("&");
                        urlStr.append(kv.key + "=" + kv.getValueStr());
                    }
                    LogUtil.i(URLDecoder.decode(urlStr.toString(), "utf-8")
                            + "->onSuccess():content=" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (listener != null) {
                    if (parser == null) {
                        T t = listener.parseResult(response);
                        listener.onSuccess(200, t);
                        listener.onSuccess(t);
                    } else {
                        new ParseTask<T>(new RequestListener<T>() {
                            @Override
                            public void onSuccess(T t) {
                                listener.onSuccess(200, t);
                                listener.onSuccess(t);
                                listener.onStopped();
                            }
                        }, parser, response).execute();
                    }
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (listener != null) {
                    listener.onLoading(total, current, isUploading);
                }
            }

            @Override
            public void onStarted() {
                if (listener != null) {
                    listener.onStart();
                }
                StringBuilder urlStr = new StringBuilder(params.getUri());
                try {
                    List<KeyValue> keyValues = params.getBodyParams();
                    for (KeyValue kv : keyValues) {
                        urlStr.append("&");
                        urlStr.append(kv.key + "=" + kv.getValueStr());
                    }
                    LogUtil.i(URLDecoder.decode(urlStr.toString(), "utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWaiting() {

            }

            @SuppressWarnings("hiding")
            class ParseTask<T> extends BaseAsyncTask<T> {
                ResultParser<T> parser;
                String response;

                public ParseTask(RequestListener<T> callback,
                        ResultParser<T> parser, String response) {
                    super(callback);
                    this.parser = parser;
                    this.response = response;
                }

                @Override
                protected T doInBackground(Object... params) {
                    T t = null;
                    try {
                        t = parser.parse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return t;
                }

            }
        });
    }

    /**
     * Http请求
     * 
     * @param params
     *            请求参数
     * @param listener
     *            监听器
     * @param parser
     *            结果解析器
     * @return
     */
    public static <T> Cancelable upload(final RequestParams params,
            final RequestListener<T> listener, final ResultParser<T> parser) {
        return request(HttpMethod.POST, params, listener, parser);
    }

    /**
     * 下载
     * 
     * @param url
     *            路径
     * @param params
     *            参数
     * @param target
     *            保存路径
     * @param autoRename
     *            自动重命名
     * @param listener
     *            回调
     * @return
     */
    public static Cancelable download(final RequestParams params,
            String target, boolean autoRename,
            final RequestListener<File> listener) {
        params.setSaveFilePath(target);
        params.setAutoRename(autoRename);
        params.setAutoResume(true);
        return HTTP.post(params, new ProgressCallback<File>() {

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (listener != null) {
                    listener.onLoading(total, current, isUploading);
                }
            }

            @Override
            public void onCancelled(CancelledException arg0) {

                if (listener != null) {
                    listener.onStopped();
                }
            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                if (ex instanceof HttpException) {
                    HttpException e = (HttpException) ex;
                    // 获取响应状态码
                    int exceptionCode = e.getCode();
                    switch (exceptionCode) {
                    case 403:
                        // 状态码是403（token失效、设备未验证、长时间未使用）
                        if (listener != null) {
                            listener.onFailure(e, e.getResult());
                        }
                        break;
                    case 404:
                        // 状态码是404（附件不存在）
                        if (listener != null) {
                            listener.onFailure(e, "附件不存在");
                        }
                        break;

                    default:
                        // 其他状态码
                        if (listener != null) {
                            listener.onFailure(e, e.getResult());
                        }
                        break;
                    }
                    LogUtil.e("exceptionCode=" + exceptionCode);
                    LogUtil.e(e.getResult(), e);
                } else {
                    if (listener != null) {
                        listener.onFailure((Exception) ex, ex.getMessage());
                    }
                }
                if (listener != null) {
                    listener.onStopped();
                }
            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onStopped();
                }
            }

            @Override
            public void onSuccess(File result) {
                LogUtil.i("contentLength=" + result.length());
                LogUtil.i("result=" + result.getAbsolutePath());
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onStarted() {
                if (listener != null) {
                    listener.onStart();
                }
                StringBuilder urlStr = new StringBuilder(params.getUri());
                try {
                    List<KeyValue> keyValues = params.getBodyParams();
                    for (KeyValue kv : keyValues) {
                        urlStr.append("&");
                        urlStr.append(kv.key + "=" + kv.getValueStr());
                    }
                    LogUtil.i(URLDecoder.decode(urlStr.toString(), "utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWaiting() {

            }
        });
    }

    public static RequestParams createParams(String[] keys, String[] values,
            String url) {
        RequestParams params = new RequestParams(url);
        if (keys != null) {
            int len = keys.length;
            for (int i = 0; i < len; i++) {
                params.addBodyParameter(keys[i], values[i]);
            }
        }
        return params;
    }

}
