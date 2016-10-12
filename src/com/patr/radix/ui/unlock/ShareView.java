/**
 * radix
 * ShareView
 * zhoushujie
 * 2016-10-10 上午6:28:45
 */
package com.patr.radix.ui.unlock;

import org.xutils.x;

import com.patr.radix.MyApplication;
import com.patr.radix.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author zhoushujie
 *
 */
public class ShareView extends LinearLayout {
    
    // 分享图类型，0=二维码，1=小区路线图
    private int type = 0;
    
    private ImageView iv;

    /**
     * @param context
     */
    public ShareView(Context context, int type) {
        this(context, type, null);
    }

    /**
     * @param context
     * @param i
     * @param bitmap
     */
    public ShareView(Context context, int type, Bitmap bitmap) {
        super(context);
        this.type = type;
        LayoutInflater.from(context).inflate(R.layout.view_share, this);
        iv = (ImageView) findViewById(R.id.iv);
        if (type == 0) {
            iv.setImageBitmap(bitmap);
        } else {
            x.image().bind(iv, MyApplication.instance.getUserInfo().getAreaPic());
        }
    }

    public ImageView getIv() {
        return iv;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

}
