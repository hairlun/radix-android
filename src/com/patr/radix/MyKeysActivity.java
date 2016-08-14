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
import com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener;
import com.patr.radix.view.swipe.SwipeRefreshLayoutDirection;
import com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author zhoushujie
 *
 */
public class MyKeysActivity extends Activity implements OnClickListener, OnItemClickListener, OnRefreshListener {
    
	private TitleBarView titleBarView;
	
	private ListView keysLv;
	
	private Button okBtn;
	
	private KeyListAdapter adapter;

	private SwipeRefreshLayout swipe;
	
	private Context context;
	
	private boolean isAfterIM;
	
	private String callNumber;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        isAfterIM = getIntent().getBooleanExtra("IM", false);
        callNumber = getIntent().getStringExtra("callNumber");
        setContentView(R.layout.activity_my_keys);
        initView();
        // 测试数据
        if (MyApplication.TEST) {
            testData();
        }
        if (MyApplication.instance.getLocks().size() == 0) {
            loadData();
        }
    }
    
    private void initView() {
    	titleBarView = (TitleBarView) findViewById(R.id.my_keys_titlebar);
        keysLv = (ListView) findViewById(R.id.my_keys_lv);
        okBtn = (Button) findViewById(R.id.ok_btn);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        titleBarView.setTitle(R.string.titlebar_my_keys).showSendKeyBtn().setOnSendKeyClickListener(this).setOnCancelClickListener(this).setOnCheckAllClickListener(this);
        adapter = new KeyListAdapter(this, MyApplication.instance.getLocks());
        keysLv.setAdapter(adapter);
        if (isAfterIM) {
            okBtn.setText(R.string.unlock_send);
        }
        okBtn.setOnClickListener(this);
        keysLv.setOnItemClickListener(this);
        swipe.setOnRefreshListener(this);
        swipe.setDirection(SwipeRefreshLayoutDirection.TOP);
    }
    
    private void loadData() {
        
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
    
    private void testData() {
        List<RadixLock> locks = new ArrayList<RadixLock>();
        RadixLock lock;
        lock = new RadixLock();
        lock.setId("99");
        lock.setName("RADIX");
        lock.setBleName("RADIX");
        lock.setKey("123456");
        lock.setStart("2016-06-24 12:00");
        lock.setEnd("2016-06-30 12:00");
        locks.add(lock);
        for (int i = 0; i < 4; i++) {
            lock = new RadixLock();
            lock.setId("" + i);
            lock.setName("Radix" + i);
            lock.setBleName("Radix" + i);
            lock.setKey("123456");
            lock.setStart("2016-06-24 12:00");
            lock.setEnd("2016-06-30 12:00");
            locks.add(lock);
        }
        MyApplication.instance.setLocks(locks);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_send_key_ll:
            adapter.setEdit(true);
            adapter.notifyDataSetChanged();
            titleBarView.showCancelBtn().showCheckAllBtn();
            okBtn.setVisibility(View.VISIBLE);
            break;
        case R.id.titlebar_cancel_ll:
            adapter.deselectAll();
            adapter.setEdit(false);
            adapter.notifyDataSetChanged();
            titleBarView.showBackBtn().showSendKeyBtn();
            okBtn.setVisibility(View.GONE);
            break;
        case R.id.titlebar_check_all_ll:
            if (!adapter.isSelectAll()) {
                adapter.selectAll();
            } else {
                adapter.deselectAll();
            }
            adapter.notifyDataSetChanged();
            break;
        case R.id.ok_btn:
            if (adapter.selectedSet.isEmpty()) {
                ToastUtil.showShort(context, "请至少选择一个钥匙！");
            } else {
                MyApplication.instance.setSelectedLocks(new ArrayList<RadixLock>(adapter.selectedSet));
                if (isAfterIM) {
                    ActiveTimeActivity.startAfterIM(context, callNumber);
                } else {
                    // 设置有效时间，生成二维码
                    ActiveTimeActivity.start(context);
                }
            }
            break;
        }
    }

    /* (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        RadixLock lock = adapter.getItem(position);
        if (adapter.isEdit()) {
            if (!adapter.selectedSet.contains(lock)) {
                adapter.selectedSet.add(lock);
            } else {
                adapter.selectedSet.remove(lock);
            }
            adapter.notifyDataSetChanged();
        } else {
            MyApplication.instance.setSelectedLock(lock);
            adapter.notifyDataSetChanged();
            finish();
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyKeysActivity.class);
        intent.putExtra("IM", false);
        context.startActivity(intent);
    }
    
    public static void startAfterIM(Context context, String callNumber) {
        Intent intent = new Intent(context, MyKeysActivity.class);
        intent.putExtra("IM", true);
        intent.putExtra("callNumber", callNumber);
        context.startActivity(intent);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.view.swipe.SwipeRefreshLayout.OnRefreshListener#onRefresh(com.patr.radix.view.swipe.SwipeRefreshLayoutDirection)
     */
    @Override
    public void onRefresh(SwipeRefreshLayoutDirection direction) {
        if (SwipeRefreshLayoutDirection.TOP == direction) {
            loadData();
        }
    }

}
