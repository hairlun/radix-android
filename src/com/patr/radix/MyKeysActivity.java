/**
 * radix
 * MyKeysActivity
 * zhoushujie
 * 2016-6-21 下午3:33:34
 */
package com.patr.radix;

import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author zhoushujie
 *
 */
public class MyKeysActivity extends Activity {
    
	private TitleBarView titleBarView;
	
	private ListView keysLv;
	
	private Button okBtn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_keys);
        initView();
    }
    
    private void initView() {
    	titleBarView = (TitleBarView) findViewById(R.id.my_keys_titlebar);
        keysLv = (ListView) findViewById(R.id.my_keys_lv);
        okBtn = (Button) findViewById(R.id.ok_btn);
        titleBarView.setTitle(R.string.titlebar_my_keys);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyKeysActivity.class);
        context.startActivity(intent);
    }
}
