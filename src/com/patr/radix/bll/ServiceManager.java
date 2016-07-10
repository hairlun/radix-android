/**
 * radix
 * ServiceManager
 * zhoushujie
 * 2016-6-23 下午2:40:37
 */
package com.patr.radix.bll;

import org.xutils.common.Callback.Cancelable;

import com.patr.radix.MyApplication;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.LoginResult;
import com.patr.radix.bean.RequestResult;
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
        String ACCOUNT = "account";
        
        String PAGE_NUM = "pageNum";
        
        String PAGE_SIZE = "pageSize";
        
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
        
        String USER_ID = "account";
        
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
        
        String PAGE_MODEL = "pageModel";
        
        String CURRENT_PAGE = "currectPage";
        
        String PAGE_SIZE = "pageSize";
        
        String TOTAL_COUNT = "totalCount";
        
        String TOTAL_PAGE = "totalPage";
        
        String DATA = "data";
        
        String ID = "id";
        
        String TITLE = "title";
        
        String SENT_DATE = "sentDate";
        
        String CONTENT = "content";
        
        String READ_TIME = "readTime";
        
        String URL = "url";

    }

    /**
     * 接口地址
     */
    public interface Url {
        
        /** 用户登录 */
        String LOGIN = "/login.do?";
        
        /** 用户列表 */
        String USER_LIST = "/mobileUserList?";
        
        /** 已授权门禁钥匙列表 */
        String LOCK_LIST = "/lockList?";
        
        /** 门牌号列表 */
        String HOUSE_NUMBER_LIST = "/houseNumberList?";
        
        /** 公告列表 */
        String NOTICE_LIST = "/noticeList?";
        
        /** 公告详情 */
        String NOTICE_DETAILS = "/mobileViewNotice?";
        
        /** 修改用户信息 */
        String EDIT_USER_INFO = "/updateMobileUserInfo?";
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
        String[] keys = { RequestKey.ACCOUNT, RequestKey.PWD };
        String[] values = { account, pwd };
        return WebService.post(Url.LOGIN, keys, values, listener,
                new LoginParser(listener));
    }
   
    /**
     * 获取门禁钥匙列表
     * 
     * @param listener
     * @return
     */
    public static Cancelable getLockList(final RequestListener<GetLockListResult> listener) {
        String[] keys = { RequestKey.ACCOUNT };
        String[] values = { MyApplication.instance.getUserId() };
        return WebService.post(Url.LOCK_LIST, keys, values, listener, new GetLockListParser(listener));
    }
    
    /**
     * 获取公告列表
     * 
     * @param pageNum
     * @param listener
     * @return
     */
    public static Cancelable getNoticeList(int pageNum, final RequestListener<GetNoticeListResult> listener) {
        String[] keys = { RequestKey.ACCOUNT, RequestKey.PAGE_NUM, RequestKey.PAGE_SIZE };
        String[] values = { MyApplication.instance.getUserId(), String.valueOf(pageNum), String.valueOf(LIMIT)};
        return WebService.post(Url.NOTICE_LIST, keys, values, listener, new GetNoticeListParser(listener));
    }
    
    /**
     * 获取公告详情
     * 
     * @param id
     * @param listener
     * @return
     */
    public static Cancelable getNoticeDetails(String id, final RequestListener<GetNoticeDetailsResult> listener) {
        String[] keys = { RequestKey.ACCOUNT, RequestKey.ID };
        String[] values = { MyApplication.instance.getUserId(), id };
        return WebService.post(Url.NOTICE_DETAILS, keys, values, listener, new GetNoticeDetailsParser(listener));
    }
    
    /**
     * 修改用户信息
     * 
     * @param name
     * @param mobile
     * @param pwd
     * @param listener
     * @return
     */
    public static Cancelable updateUserInfo(String name, String mobile, String pwd, final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.ACCOUNT, RequestKey.NAME, RequestKey.MOBILE, RequestKey.PWD };
        String[] values = { MyApplication.instance.getUserId(), name, mobile, pwd };
        return WebService.post(Url.EDIT_USER_INFO, keys, values, listener, new RequestResultParser(listener));
    }
}
