package com.patr.radix.network;

/**
 * 异步监听接口
 * 
 * @author huangzhongwen
 * 
 */
public interface IAsyncListener<T> {

    /**
     * 请求成功
     * 
     * @param result
     *            请求结果
     */
    void onSuccess(T result);

    /**
     * 请求成功
     * 
     * @param stateCode
     * @param result
     */
    void onSuccess(int stateCode, T result);

    /**
     * 请求失败
     * 
     * @param error
     * @param content
     */
    void onFailure(Exception error, String content);

    /**
     * 请求开始
     */
    void onStart();

    /**
     * 请求停止
     */
    void onStopped();

    /**
     * 请求停止
     */
    void onStopped(T result);

    /**
     * 加载进度
     * 
     * @param values
     */
    void onLoading(Object... values);

    /**
     * 解析结果
     * 
     * @param response
     * @return
     */
    T parseResult(String response);

    /**
     * 结果解析器
     * 
     * @author huangzhongwen
     * 
     * @param <T>
     */
    public interface ResultParser<T> {
        /**
         * 解析响应字符串
         * 
         * @param response
         * @return
         */
        T parse(String response);
    }
}