package com.patr.radix.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.xutils.common.util.LogUtil;

import com.felipecsl.gifimageview.library.GifImageView;
import com.patr.radix.MyApplication;
import com.patr.radix.MyKeysActivity;
import com.patr.radix.R;
import com.patr.radix.adapter.CommunityListAdapter;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bll.CacheManager;
import com.patr.radix.bll.GetCommunityListParser;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.bll.ServiceManager.Url;
import com.patr.radix.network.RequestListener;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.NetUtils;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.Utils;
import com.patr.radix.view.GifView;
import com.patr.radix.view.ListSelectDialog;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class UnlockFragment extends Fragment implements OnClickListener, OnItemClickListener {
    
    private Context context;
    
    private TitleBarView titleBarView;
    
    private GifImageView gifView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unlock, container, false);
		titleBarView = (TitleBarView) view.findViewById(R.id.unlock_titlebar);
		titleBarView.hideBackBtn().showSelectKeyBtn();
		titleBarView.setOnSelectKeyClickListener(this);
		gifView = (GifImageView) view.findViewById(R.id.unlock_giv);
		byte[] bytes;
        try {
            bytes = Utils.input2byte(getResources().openRawResource(
                    R.raw.shake_shake));
            gifView.setBytes(bytes);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadData();
		return view;
	}
    
    private void loadData() {
        if (MyApplication.instance.getSelectedCommunity() == null) {
            getCommunityList();
            return;
        }
        if (MyApplication.instance.getLocks().size() == 0) {
            // 从服务器获取门禁钥匙列表
            ServiceManager.getLockList(new RequestListener<GetLockListResult>() {

                @Override
                public void onSuccess(int stateCode, GetLockListResult result) {
                    if (result != null) {
                        if (result.isSuccesses()) {
                            MyApplication.instance.setLocks(result.getLocks());
                            setTitle();
                        } else {
                            ToastUtil.showShort(context, result.getRetinfo());
                        }
                    } else {
//                        ToastUtil.showShort(context, R.string.connect_exception);
                    }
                }

                @Override
                public void onFailure(Exception error, String content) {
//                    ToastUtil.showShort(context, R.string.connect_exception);
                }
                
            });
        }
        
    }
    
    private void getCommunityList() {
        switch (NetUtils.getConnectedType(context)) {
        case NONE:
            getCommunityListFromCache();
            break;
        case WIFI:
        case OTHER:
            getCommunityListFromServer();
            break;
        default:
            break;
        }
    }
    
    private void getCommunityListFromCache() {
        CacheManager.getCacheContent(context, getUrl(),
                new RequestListener<GetCommunityListResult>() {

                    @Override
                    public void onSuccess(int stateCode,
                            GetCommunityListResult result) {
                        MyApplication.instance.setCommunities(result.getCommunities());
                        ListSelectDialog.show(context, "请选择门禁", new CommunityListAdapter(context, MyApplication.instance.getCommunities()), UnlockFragment.this);
                    }
                    
                }, new GetCommunityListParser());
    }
    
    private void getCommunityListFromServer() {
        // 从服务器获取小区列表
        ServiceManager.getCommunityList(new RequestListener<GetCommunityListResult>() {

            @Override
            public void onSuccess(int stateCode, GetCommunityListResult result) {
                if (result != null) {
                    if (result.isSuccesses()) {
                        MyApplication.instance.setCommunities(result.getCommunities());
                        saveToDb(result.getResponse());
                        ListSelectDialog.show(context, "请选择门禁", new CommunityListAdapter(context, MyApplication.instance.getCommunities()), UnlockFragment.this);
                    } else {
                        ToastUtil.showShort(context, result.getRetinfo());
                        getCommunityListFromCache();
                    }
                } else {
                    getCommunityListFromCache();
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
                getCommunityListFromCache();
            }
            
        });
    }

    /**
     * 保存列表到数据库
     * 
     * @param content
     */
    protected void saveToDb(String content) {
        CacheManager.saveCacheContent(context, getUrl(), content,
                new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        LogUtil.i("save " + getUrl() + "=" + result);
                    }
                });
    }

    /**
     * 
     * @return
     */
    private String getUrl() {
        return String.format("%s%s&account=%s", MyApplication.instance.getSelectedCommunity().getHost(), Url.LOCK_LIST, MyApplication.instance.getUserId());
    }
    
    private void setTitle() {
        String selectedKey = PrefUtil.getString(context, Constants.PREF_SELECTED_KEY);
        for (RadixLock lock : MyApplication.instance.getLocks()) {
            if (selectedKey.equals(lock.getId())) {
                titleBarView.setTitle(lock.getName());
                MyApplication.instance.setSelectedLock(lock);
                return;
            }
        }
        if (MyApplication.instance.getLocks().size() > 0) {
            titleBarView.setTitle(MyApplication.instance.getLocks().get(0).getName());
            MyApplication.instance.setSelectedLock(MyApplication.instance.getLocks().get(0));
        }
    }

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

    @Override
    public void onStart() {
        super.onStart();
        gifView.startAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_select_key_btn:
            MyKeysActivity.start(context);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        
    }

}
