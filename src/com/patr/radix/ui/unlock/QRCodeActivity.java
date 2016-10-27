package com.patr.radix.ui.unlock;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import com.patr.radix.MainActivity;
import com.patr.radix.App;
import com.patr.radix.R;
import com.patr.radix.R.id;
import com.patr.radix.R.layout;
import com.patr.radix.adapter.SharePagerAdapter;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.BitmapUtil;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.yuntongxun.ecdemo.ui.voip.VideoActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class QRCodeActivity extends Activity implements OnClickListener,
        OnPageChangeListener {

    private Context context;

    private TitleBarView titleBarView;

    private ViewPager pager;

    private TextView pg1;

    private TextView pg2;

    private Button sendBtn;

    private ImageView gohomeIv;

    private boolean gohomeAfterShare;

    private SharePagerAdapter pagerAdapter;

    /** View集合 */
    private List<ShareView> views = new ArrayList<>();

    private Bitmap bitmap;

    private Uri qrcodeUri;

    private ShareView areaPicView;
    
    private boolean isAfterIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        context = this;
        isAfterIM = getIntent().getBooleanExtra("IM", false);
        bitmap = getIntent().getParcelableExtra("bitmap");
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.unlock_qrcode_titlebar);
        pager = (ViewPager) findViewById(R.id.img_pager);
        pg1 = (TextView) findViewById(R.id.pg1);
        pg2 = (TextView) findViewById(R.id.pg2);
        sendBtn = (Button) findViewById(R.id.send_btn);
        gohomeIv = (ImageView) findViewById(R.id.gohome_iv);

        titleBarView.setTitle("分享给好友");
        titleBarView.showCloseBtn();
        titleBarView.setOnCloseClickListener(this);
        sendBtn.setOnClickListener(this);
        gohomeIv.setOnClickListener(this);
        views.add(new ShareView(context, 0, bitmap));
        areaPicView = new ShareView(context, 1);
        views.add(areaPicView);
        pagerAdapter = new SharePagerAdapter(context, views);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(this);
        gohomeAfterShare = PrefUtil.getBoolean(context,
                Constants.PREF_GOHOME_AFTER_SHARE, false);
        if (gohomeAfterShare) {
            gohomeIv.setImageResource(R.drawable.send_key_btn_return_check);
        } else {
            gohomeIv.setImageResource(R.drawable.send_key_btn_return);
        }

        try {
            if (bitmap != null) {
                qrcodeUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, null, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Intent intent;
            if (isAfterIM) {
                intent = new Intent(context, VideoActivity.class);
            } else {
                intent = new Intent(context, MainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            break;

        case R.id.gohome_iv:
            gohomeAfterShare = !gohomeAfterShare;
            PrefUtil.save(context, Constants.PREF_GOHOME_AFTER_SHARE,
                    gohomeAfterShare);
            if (gohomeAfterShare) {
                gohomeIv.setImageResource(R.drawable.send_key_btn_return_check);
            } else {
                gohomeIv.setImageResource(R.drawable.send_key_btn_return);
            }
            break;

        case R.id.send_btn:
            if (pager.getCurrentItem() == 0) {
                // 分享二维码
                shareMsg("请选择", "", "", qrcodeUri);
                break;
            } else {
                // 分享路线路
                Bitmap bm = ((BitmapDrawable) areaPicView.getIv().getDrawable())
                        .getBitmap();
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bm, null, null));
                shareMsg("请选择", "", "", uri);
            }
            break;
        }
    }

    public static void start(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

    public static void startAfterIM(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra("IM", true);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#
     * onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled
     * (int, float, int)
     */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected
     * (int)
     */
    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            pg1.setBackgroundResource(R.drawable.page_index_bg_selected_selector);
            pg2.setBackgroundResource(R.drawable.page_index_bg_selector);
            sendBtn.setText("发送二维码");
        } else {
            pg1.setBackgroundResource(R.drawable.page_index_bg_selector);
            pg2.setBackgroundResource(R.drawable.page_index_bg_selected_selector);
            sendBtn.setText("发送小区引导图");
        }
    }

}
