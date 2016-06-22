/**
 * radix
 * MyKeysActivity
 * zhoushujie
 * 2016-6-21 下午3:33:34
 */
package com.patr.radix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author zhoushujie
 *
 */
public class MyKeysActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_keys);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyKeysActivity.class);
        context.startActivity(intent);
    }
}
