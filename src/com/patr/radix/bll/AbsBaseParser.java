package com.patr.radix.bll;

import com.patr.radix.network.IAsyncListener;
import com.patr.radix.network.IAsyncListener.ResultParser;

import android.os.Handler;

/**
 * 解析器抽象类
 * 
 * @author huangzhongwen
 * 
 * @param <T>
 */
public abstract class AbsBaseParser<T> implements ResultParser<T> {

    protected Handler handler = new Handler();

    protected IAsyncListener<T> listener = new IAsyncListener<T>() {

        @Override
        public void onSuccess(T result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSuccess(int stateCode, T result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFailure(Exception error, String content) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStart() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopped() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopped(T result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLoading(Object... values) {
            // TODO Auto-generated method stub

        }

        @Override
        public T parseResult(String response) {
            // TODO Auto-generated method stub
            return null;
        }

    };

    public AbsBaseParser() {
    }

    public AbsBaseParser(IAsyncListener<T> listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public void onFailure(final Exception e) {
        if (listener != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.onFailure(e, "解析异常");
                }
            });
        }
    }
}
