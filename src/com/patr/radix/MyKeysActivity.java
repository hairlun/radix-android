/**
 * radix
 * MyKeysActivity
 * zhoushujie
 * 2016-6-21 下午3:33:34
 */
package com.patr.radix;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.adapter.KeyListAdapter;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.view.TitleBarView;
import com.patr.radix.view.swipe.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author zhoushujie
 *
 */
public class MyKeysActivity extends Activity implements OnClickListener {
    
	private TitleBarView titleBarView;
	
	private ListView keysLv;
	
	private Button okBtn;
	
	private KeyListAdapter adapter;

	private SwipeRefreshLayout swipe;
	
	private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_my_keys);
        initView();
        // 测试数据
        testData();
        loadData();
    }
    
    private void initView() {
    	titleBarView = (TitleBarView) findViewById(R.id.my_keys_titlebar);
        keysLv = (ListView) findViewById(R.id.my_keys_lv);
        okBtn = (Button) findViewById(R.id.ok_btn); 
        titleBarView.setTitle(R.string.titlebar_my_keys);
        adapter = new KeyListAdapter(this, MyApplication.instance.getLocks());
        keysLv.setAdapter(adapter);
        okBtn.setOnClickListener(this);
    }
    
    private void loadData() {
        if (MyApplication.instance.getLocks().size() == 0) {
            // 从服务器获取门禁钥匙列表
            ServiceManager.getLockList(new RequestListener<GetLockListResult>() {

                @Override
                public void onStart() {
                    swipe.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            swipe.setRefreshing(true);
                        }
                    });
                }

                @Override
                public void onSuccess(int stateCode, GetLockListResult result) {
                    if (result != null) {
                        if (result.isSuccesses()) {
                            MyApplication.instance.setLocks(result.getLocks());
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showShort(context, result.getRetinfo());
                        }
                    } else {
                        ToastUtil.showShort(context, R.string.connect_exception);
                    }
                }

                @Override
                public void onFailure(Exception error, String content) {
                    ToastUtil.showShort(context, R.string.connect_exception);
                }
                
                @Override
                public void onStopped() {
                    swipe.setRefreshing(false);
                }
                
            });
        }
        
    }
    
    private void testData() {
        List<RadixLock> locks = new ArrayList<RadixLock>();
        RadixLock lock;
        for (int i = 0; i < 4; i++) {
            lock = new RadixLock();
            lock.setName("Radix" + i);
            lock.setBleName("Radix" + i);
            lock.setKey("123456");
            lock.setStart("2016-06-24 12:00");
            lock.setEnd("2016-06-30 12:00");
            locks.add(lock);
        }
        MyApplication.instance.setLocks(locks);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyKeysActivity.class);
        context.startActivity(intent);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok_btn:
            break;
        }
    }
}
