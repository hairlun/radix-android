package com.patr.radix.ui.unlock;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import com.patr.radix.MainActivity;
import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.R.id;
import com.patr.radix.R.layout;
import com.patr.radix.adapter.SharePagerAdapter;
import com.patr.radix.ui.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class QRCodeActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private ViewPager pager;
    
    private TextView pg1;
    
    private TextView pg2;
    
    private Button sendBtn;
    
    private CheckBox gohomeCb;
    
    private SharePagerAdapter pagerAdapter;

    /** View集合 */
    private List<ShareView> views = new ArrayList<>();

    private Bitmap bitmap;

    private Uri qrcodeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        context = this;
        bitmap = getIntent().getParcelableExtra("bitmap");
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.unlock_qrcode_titlebar);
        pager = (ViewPager) findViewById(R.id.img_pager);
        pg1 = (TextView) findViewById(R.id.pg1);
        pg2 = (TextView) findViewById(R.id.pg2);
        sendBtn = (Button) findViewById(R.id.send_btn);
        gohomeCb = (CheckBox) findViewById(R.id.gohome_cb);
        
        titleBarView.setTitle("分享给好友");
        titleBarView.showCloseBtn();
        titleBarView.setOnCloseClickListener(this);
        views.add(new ShareView(context, 0, bitmap));
        views.add(new ShareView(context, 1));
        pagerAdapter = new SharePagerAdapter(context, views);
        pager.setAdapter(pagerAdapter);

        try {
            if (bitmap != null) {
                qrcodeUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, null, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//        String areaPic = MyApplication.instance.getUserInfo().getAreaPic();
//        if (TextUtils.isEmpty(areaPic)) {
//            areaPicTv.setVisibility(View.GONE);
//            areaPicIv.setVisibility(View.GONE);
//            share2Btn.setVisibility(View.GONE);
//        } else {
//            x.image().bind(areaPicIv, areaPic);
//        }
//        try {
//            if (bitmap != null) {
//                qrcodeIv.setImageBitmap(bitmap);
//                qrcodeUri = Uri.parse(MediaStore.Images.Media.insertImage(
//                        getContentResolver(), bitmap, null, null));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        shareBtn.setOnClickListener(this);
//        share2Btn.setOnClickListener(this);
    }

    /**
     * 分享功能
     * 
     * @param context
     *            上下文
     * @param activityTitle
     *            Activity的名字
     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
            Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (bitmap == null) {
            intent.setType("text/plain"); // 纯文本
        } else {
            intent.setType("image/*");
            if (uri != null) {
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_close_btn:
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            break;
            
        case R.id.send_btn:
            if (pager.getCurrentItem() == 0) {
                // 分享二维码
                shareMsg("请选择", "", "", qrcodeUri);
                break;
            } else {
                // 分享路线路
                shareMsg("请选择", "", "", Uri.parse(MyApplication.instance.getUserInfo().getAreaPic()));
            }
            break;
        }
    }

    public static void start(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

}
