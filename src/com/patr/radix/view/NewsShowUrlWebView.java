package com.patr.radix.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xutils.common.util.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 新闻详情页面
 * 
 * @author Administrator
 * 
 */
public class NewsShowUrlWebView extends WebView {

    /**
     * 事项详情页面
     * 
     * @param context
     *            上下文对象
     * @param news
     *            要显示的新闻
     * @param workWebViewClient
     *            WebView客户端
     * @param jsInterface
     *            Javascript接口
     */
    public NewsShowUrlWebView(Context context, String url,
            NewsWebViewClient workWebViewClient, JavascriptInterface jsInterface) {
        super(context);
        setVerticalScrollBarEnabled(true);
        setHorizontalScrollBarEnabled(false);
        setInitialScale(100);
        addJavascriptInterface(jsInterface);

        setSettings();
        setWorkWebViewClient(workWebViewClient);

        // 加载需要显示的网页
        show(url);
        // String html = getFromAssets("sx/sx.html");
        // loadData(html, "text/html;charset=UTF-8", "UTF-8");
    }

    private void show(String url) {
        if (url == null) {
            LogUtil.i("url is null");
            return;
        }
        LogUtil.i(url);
        loadUrl(url);
    }

    @SuppressWarnings("deprecation")
    private void setSettings() {
        WebSettings webSettings = getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);

    }

    private void setWorkWebViewClient(final NewsWebViewClient workWebViewClient) {
        setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (workWebViewClient != null) {
                    workWebViewClient.onProgressChanged(view, progress);
                } else {
                    super.onProgressChanged(view, progress);
                }
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                if (workWebViewClient != null) {
                    workWebViewClient.openFileChooser(uploadMsg);
                }
            }

        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (workWebViewClient != null) {
                    workWebViewClient.onPageStarted(view, url, favicon);
                } else {
                    super.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean result = false;
                LogUtil.i(url);
                if (workWebViewClient != null) {
                    result = workWebViewClient.shouldOverrideUrlLoading(view,
                            url);
                } else {
                    result = super.shouldOverrideUrlLoading(view, url);
                }
                return result;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (workWebViewClient != null) {
                    workWebViewClient.onPageFinished(view, url);
                } else {
                    super.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                if (workWebViewClient != null) {
                    workWebViewClient.onReceivedError(view, errorCode,
                            description, failingUrl);
                } else {
                    super.onReceivedError(view, errorCode, description,
                            failingUrl);
                }
            }
        });

        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                    String contentDisposition, String mimetype,
                    long contentLength) {
                if (workWebViewClient != null) {
                    workWebViewClient.onDownloadStart(url, userAgent,
                            contentDisposition, mimetype, contentLength);
                }
            }

        });
    }

    @Override
    public void goBack() {
        super.goBack();
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(JavascriptInterface javascriptInterface,
            String name) {
        if (javascriptInterface != null) {
            super.addJavascriptInterface(javascriptInterface, name);
        }
    }

    public void addJavascriptInterface(JavascriptInterface javascriptInterface) {
        addJavascriptInterface(javascriptInterface, "android");
    }

    public String getFromAssets(String fileName) {
        String result = "";
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(getResources().getAssets()
                    .open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Javascript交互接口
     * 
     * @author Administrator
     * 
     */
    public interface JavascriptInterface {

        void addFav();

        void playVideo();
    }

    /**
     * 客户端接口
     * 
     * @author Administrator
     * 
     */
    public interface NewsWebViewClient {

        /**
         * 页面开始加载
         * 
         * @param view
         * @param url
         * @param favicon
         */
        void onPageStarted(WebView view, String url, Bitmap favicon);

        /**
         * 页面重载
         * 
         * @param view
         * @param url
         * @return
         */
        boolean shouldOverrideUrlLoading(WebView view, String url);

        /**
         * 页面加载完成
         * 
         * @param view
         * @param url
         */
        void onPageFinished(WebView view, String url);

        /**
         * 加载错误
         * 
         * @param view
         * @param errorCode
         * @param description
         * @param failingUrl
         */
        void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl);

        /**
         * 页面加载进度
         * 
         * @param view
         * @param progress
         */
        void onProgressChanged(WebView view, int progress);

        /**
         * 打开文件选择器
         * 
         * @param uploadMsg
         */
        void openFileChooser(ValueCallback<Uri> uploadMsg);

        /**
         * 页面下载链接
         * 
         * @param url
         * @param userAgent
         * @param contentDisposition
         * @param mimetype
         * @param contentLength
         */
        void onDownloadStart(String url, String userAgent,
                String contentDisposition, String mimetype, long contentLength);
    }

    public void setUrl(String url) {
        show(url);
    }

}
