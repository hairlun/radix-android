package com.patr.radix;

import com.patr.radix.bll.ServiceManager;
import com.patr.radix.bll.ServiceManager.Url;
import com.patr.radix.network.RequestListener;
import com.patr.radix.network.WebService;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.NewsShowUrlWebView;
import com.patr.radix.view.NewsShowUrlWebView.NewsWebViewClient;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.swipe.SwipeRefreshLayout;
import com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener;
import com.patr.radix.view.swipe.SwipeRefreshLayoutDirection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public class NoticeDetailsActivity extends Activity implements OnRefreshListener, NewsWebViewClient {
    
    private TitleBarView titleBarView;

    private NewsShowUrlWebView webView;
    
    private SwipeRefreshLayout swipe;
    
    private Context context;
    
    private String noticeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);
        context = this;
        noticeId = getIntent().getStringExtra("noticeId");
        initView();
    }
    
    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.notice_details_titlebar);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        titleBarView.setTitle(R.string.titlebar_msg_details);
        swipe.setOnRefreshListener(this);
        loadData();
    }
    
    private void loadData() {
        String url = WebService.URL + Url.NOTICE_DETAILS + "token=" + MyApplication.instance.getUserInfo().getToken() + "&id=" + noticeId;
        initWebView(url);
//        // 从服务器获取公告详情
//        ServiceManager.getNoticeDetails(noticeId, new RequestListener<GetNoticeDetailsResult>() {
//
//            @Override
//            public void onStart() {
//                swipe.post(new Runnable() {
//                    
//                    @Override
//                    public void run() {
//                        swipe.setRefreshing(true);
//                    }
//                });
//            }
//
//            @Override
//            public void onSuccess(int stateCode, GetNoticeDetailsResult result) {
//                if (result != null) {
//                    if (result.isSuccesses()) {
//                        String url = result.getUrl();
//                        initWebView(url);
//                    } else {
//                        ToastUtil.showShort(context, result.getRetinfo());
//                    }
//                } else {
//                    ToastUtil.showShort(context, R.string.connect_exception);
//                }
//            }
//
//            @Override
//            public void onFailure(Exception error, String content) {
//                ToastUtil.showShort(context, R.string.connect_exception);
//                swipe.setRefreshing(false);
//            }
//            
//        });
    }
    
    private void initWebView(String url) {
        if (webView != null) {
            swipe.removeView(webView);
            webView = null;
        }
        webView = new NewsShowUrlWebView(context, url, this, null);
        swipe.addView(webView, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        swipe.post(new Runnable() {
            
            @Override
            public void run() {
                swipe.setRefreshing(true);
            }
        });
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        swipe.setRefreshing(false);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
            String description, String failingUrl) {
        swipe.setRefreshing(false);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        if (progress == 100) {
            swipe.setRefreshing(false);
        }
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        
    }

    @Override
    public void onDownloadStart(String url, String userAgent,
            String contentDisposition, String mimetype, long contentLength) {
        
    }

    @Override
    public void onRefresh(SwipeRefreshLayoutDirection direction) {
        webView.reload();
    }

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, NoticeDetailsActivity.class);
        intent.putExtra("noticeId", id);
        context.startActivity(intent);
    }
}
