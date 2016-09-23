package com.patr.radix;

import org.xutils.x;

import com.patr.radix.ui.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QRCodeActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private ImageView qrcodeIv;

    private Button shareBtn;

    private TextView areaPicTv;

    private ImageView areaPicIv;

    private Button share2Btn;

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
        qrcodeIv = (ImageView) findViewById(R.id.unlock_qrcode_iv);
        shareBtn = (Button) findViewById(R.id.unlock_share_btn);
        areaPicTv = (TextView) findViewById(R.id.unlock_area_pic_tv);
        areaPicIv = (ImageView) findViewById(R.id.unlock_area_pic_iv);
        share2Btn = (Button) findViewById(R.id.unlock_share2_btn);
        titleBarView.setTitle(R.string.titlebar_send_to_friend);
        String areaPic = MyApplication.instance.getUserInfo().getAreaPic();
        if (TextUtils.isEmpty(areaPic)) {
            areaPicTv.setVisibility(View.GONE);
            areaPicIv.setVisibility(View.GONE);
            share2Btn.setVisibility(View.GONE);
        } else {
            x.image().bind(areaPicIv, areaPic);
        }
        try {
            if (bitmap != null) {
                qrcodeIv.setImageBitmap(bitmap);
                qrcodeUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, null, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareBtn.setOnClickListener(this);
        share2Btn.setOnClickListener(this);
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
        case R.id.unlock_share_btn:
            shareMsg("请选择", "", "", qrcodeUri);
            break;

        case R.id.unlock_share2_btn:
            Bitmap bm = ((BitmapDrawable) areaPicIv.getDrawable()).getBitmap();
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                    getContentResolver(), bm, null, null));
            shareMsg("请选择", "", "", uri);
            break;
        }
    }

    public static void start(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

}
