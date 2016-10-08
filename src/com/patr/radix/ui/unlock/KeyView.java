/**
 * radix
 * KeyView
 * zhoushujie
 * 2016-10-7 下午7:54:25
 */
package com.patr.radix.ui.unlock;

import com.patr.radix.R;
import com.patr.radix.bean.RadixLock;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author zhoushujie
 *
 */
public class KeyView extends LinearLayout {
    
    private LinearLayout keyLl;
    
    private ImageView keyIv;
    
    private TextView keyTv;
    
    private RadixLock key;
    
    private int idx;

    /**
     * @param context
     */
    public KeyView(Context context, RadixLock key, int idx, boolean selected) {
        super(context);
        this.key = key;
        this.idx = idx;
        initView(context, selected);
    }
    
    private void initView(Context context, boolean selected) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_key, this);
        keyLl = (LinearLayout) findViewById(R.id.key_ll);
        keyIv = (ImageView) findViewById(R.id.key_iv);
        keyTv = (TextView) findViewById(R.id.key_tv);
        keyLl.setTag(idx);
        String name = String.format("Gate%02d", idx + 1);
        keyTv.setText(name);
        if (!selected) {
            keyTv.setTextColor(getResources().getColor(R.color.black));
            keyIv.setImageResource(R.drawable.homepage_key);
        } else {
            keyTv.setTextColor(getResources().getColor(R.color.buttombar_text_selected));
            keyIv.setImageResource(R.drawable.homepage_key_pre);
        }
    }
    
    public void setOnClickListener(OnClickListener onClickListener) {
        keyLl.setOnClickListener(onClickListener);
    }

}
