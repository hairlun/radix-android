package com.patr.radix;

import com.google.zxing.common.BitMatrix;
import com.patr.radix.utils.qrcode.QRCodeUtil;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class QRCodeActivity extends Activity implements OnClickListener {
    
    private TitleBarView titleBarView;
    
    private ImageView qrcodeIv;
    
    private ImageView shareIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }
    
    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.unlock_qrcode_titlebar);
        qrcodeIv = (ImageView) findViewById(R.id.unlock_qrcode_iv);
        shareIv = (ImageView) findViewById(R.id.unlock_share_iv);
        titleBarView.setTitle(R.string.titlebar_send_to_friend);
        try {
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap("1234", 300, 300);
            if (bitmap != null) {
                qrcodeIv.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO 分享
        
    }
    
    public static void start(Context context) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        context.startActivity(intent);
    }

}
