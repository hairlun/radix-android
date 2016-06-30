/**
 * radix
 * ActiveTimeActivity
 * zhoushujie
 * 2016-6-30 下午3:24:59
 */
package com.patr.radix;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * @author zhoushujie
 *
 */
public class ActiveTimeActivity extends Activity {
    
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_time);
        context = this;
    }

}
