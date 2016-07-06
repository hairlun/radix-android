package com.patr.radix;

import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class QRCodeActivity extends Activity implements OnClickListener {
   
    private Context context;
    
    private TitleBarView titleBarView;
    
    private ImageView qrcodeIv;
    
    private Button shareBtn;
    
    private Bitmap bitmap;
    
    private Uri uri;

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
        titleBarView.setTitle(R.string.titlebar_send_to_friend);
        try {
            if (bitmap != null) {
                qrcodeIv.setImageBitmap(bitmap);
                uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareBtn.setOnClickListener(this);
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
    public void shareMsg(String activityTitle, String msgTitle, String msgText) {
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
        shareMsg("请选择", "", "");
    }
    
    public static void start(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

}
