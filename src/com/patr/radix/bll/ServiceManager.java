/**
 * radix
 * ServiceManager
 * zhoushujie
 * 2016-6-23 下午2:40:37
 */
package com.patr.radix.bll;

import org.xutils.common.Callback.Cancelable;

import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.LoginResult;
import com.patr.radix.network.RequestListener;
import com.patr.radix.network.WebService;

/**
 * @author zhoushujie
 *
 */
public class ServiceManager {
    
    public static final String TAG = ServiceManager.class.getName();
    
    public static final int LIMIT = 20;

    /**
     * 请求参数Key
     */
    public interface RequestKey {
        String USERNAME = "username";
        
        String ID = "id";
        
        String NAME = "name";
        
        String PWD = "pwd";
        
        String MOBILE = "mobile";
    }

    /**
     * 响应参数Key
     */
    public interface ResponseKey {

        String COMMUNITY_LIST = "communityList";
        
        String NAME = "name";
        
        String PATH = "path";
        
        String USER_LIST = "userList";
        
        String USERNAME = "username";
        
        String USER_ID = "userId";
        
        String MOBILE = "mobile";
        
        String PWD = "pwd";
        
        String HOME = "home";
        
        String LOCK_LIST = "lockList";
        
        String BLE_NAME = "bleName";
        
        String KEY = "key";
        
        String START = "start";
        
        String END = "end";
        
        String HOUSE_NUMBER_LIST = "houseNumberList";
        
        String CHAT_ID = "chatId";
        
        String NOTICE_LIST = "noticeList";
        
        String ID = "id";
        
        String TITLE = "title";
        
        String SENT_DATE = "sentDate";
        
        String URL = "url";

    }

    /**
     * 接口地址
     */
    public interface Url {
        
        /** 用户登录 */
        String LOGIN = "/login.do?";
        
        /** 用户列表 */
        String USER_LIST = "/userList.do?";
        
        /** 已授权门禁钥匙列表 */
        String LOCK_LIST = "/lockList.do?";
        
        /** 门牌号列表 */
        String HOUSE_NUMBER_LIST = "/houseNumberList.do?";
        
        /** 公告列表 */
        String NOTICE_LIST = "/noticeList.do?";
        
        /** 公告详情 */
        String NOTICE_DETAILS = "/noticeDetails.do?";
        
        /** 修改用户信息 */
        String EDIT_USER = "/editUser.do?";
    }

    /**
     * 用户账号登录
     * 
     * @param account
     * @param pwd
     * @param listener
     * @return
     */
    public static Cancelable login(String account, String pwd,
            final RequestListener<LoginResult> listener) {
        String[] keys = { RequestKey.USERNAME, RequestKey.PWD };
        String[] values = { account, pwd };
        return WebService.post(Url.LOGIN, keys, values, listener,
                new LoginParser(listener));
    }
   
    public static Cancelable getLockList(final RequestListener<GetLockListResult> listener) {
        String[] keys = {  };
        String[] values = {};
        return WebService.post(Url.LOCK_LIST, keys, values, listener, new GetLockListParser(listener));
    }
}
