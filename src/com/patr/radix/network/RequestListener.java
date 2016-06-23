package com.patr.radix.network;

/**
 * 登录回调对象
 * 
 * @author huangzhongwen
 * 
 */
public abstract class RequestListener<T> implements IAsyncListener<T> {

    @Override
    public void onSuccess(T result) {

    }
    
    @Override
    public void onSuccess(int stateCode, T result) {

    }

    @Override
    public void onFailure(Exception error, String content) {

    };

    @Override
    public void onStart() {
    }

    @Override
    public void onStopped() {
    }

    @Override
    public void onLoading(Object... values) {
    }

    @Override
    public T parseResult(String response) {
        return null;
    }

    @Override
    public final void onStopped(T result) {

    }
}