package com.patr.radix.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

/**
 * 异步任务抽象类
 * 
 * @author huangzhongwen
 * 
 * @param <Result>
 */
// public abstract class BaseAsyncTask<Result> extends
// AsyncTask<Object, Object, Result> {
public abstract class BaseAsyncTask<Result> implements Runnable {

    public RequestListener<Result> callback;
    private Object[] params;
    private static ExecutorService mExecutorService = Executors
            .newSingleThreadExecutor();
    private static Handler handler = new Handler();

    public BaseAsyncTask(RequestListener<Result> callback, Object... params) {
        this.callback = callback != null ? callback
                : new RequestListener<Result>() {
                };
        this.params = params;
    }

    @Override
    public void run() {
        final Result result = doInBackground(params);
        handler.post(new Runnable() {

            @Override
            public void run() {
                onPostExecute(result);
            }
        });
    }

    protected abstract Result doInBackground(Object... params);

    // @Override
    // protected void onPostExecute(Result result) {
    // callback.onSuccess(200, result);
    // callback.onSuccess(result);
    // callback.onStopped();
    // callback.onStopped(result);
    // }
    protected void onPostExecute(Result result) {
        callback.onSuccess(200, result);
        callback.onSuccess(result);
        callback.onStopped();
        callback.onStopped(result);
    }

    // @Override
    // protected void onPreExecute() {
    // callback.onStart();
    // }
    protected void onPreExecute() {
        callback.onStart();
    }

    // @Override
    // protected void onProgressUpdate(Object... values) {
    // callback.onLoading(values);
    // }
    protected void onProgressUpdate(Object... values) {
        callback.onLoading(values);
    }

    protected void publishProgress(Object... values) {
        onProgressUpdate(values);
    }

    public void execute() {
        // execute(params);
        onPreExecute();
        mExecutorService.execute(this);
    }
}
