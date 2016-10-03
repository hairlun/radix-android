/**
 * radix
 * ServiceManager
 * zhoushujie
 * 2016-6-23 下午2:40:37
 */
package com.patr.radix.bll;

import org.xutils.common.Callback.Cancelable;

import com.patr.radix.MyApplication;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.GetNoticeListResult;
import com.patr.radix.bean.GetUserListResult;
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

        String TOKEN = "token";

        String PAGE_NUM = "pageNum";

        String PAGE_SIZE = "pageSize";

        String ID = "id";

        String NAME = "name";

        String PWD = "pwd";

        String MOBILE = "mobile";

        String DOOR_ID = "doorId";

        String VISITOR_NAME = "visitorName";

        String START_TIME = "startTime";

        String END_TIME = "endTime";

        String PHONE_NUM = "phoneNum";

        String REMARK = "remark";

        String FILENAME = "filename";

        String USER_PIC = "userPic";
    }

    /**
     * 响应参数Key
     */
    public interface ResponseKey {

        String COMMUNITY_LIST = "communityList";

        String NAME = "name";

        String AREA_NAME = "name";

        String HOST = "path";

        String PORT = "port";

        String USER_LIST = "userList";

        String USERNAME = "username";

        String USER_ID = "account";

        String AREA_ID = "areaId";

        String MOBILE = "mobile";

        String AREA_PIC = "areaPic";

        String PWD = "pwd";

        String HOME = "home";

        String CARD_NO = "cardNo";

        String USER_PIC = "userPic";

        String LOCK_LIST = "lockList";

        String BLE_NAME1 = "inBleName";

        String BLE_NAME2 = "outBleName";

        String KEY = "key";

        String START = "start";

        String END = "end";

        String CTR_NO = "ctrNo";

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

        String PIC = "pic";

        String VIDEO = "video";

        String URL = "url";

        String MODEL = "model";

        String TOKEN = "token";

        String FILE_PATH = "filePath";

        String VISITOR_ID = "visitorId";

    }

    /**
     * 接口地址
     */
    public interface Url {

        String COMMUNITY_LIST = "http://119.29.33.197:8080/surpass/mobile/getCommunityList";

        /** 用户登录 */
        String LOGIN = "/mobileUserLogin?";

        /** 用户列表 */
        String USER_LIST = "/mobileUserList?";

        /** 已授权门禁钥匙列表 */
        String LOCK_LIST = "/lockList?";

        /** 门牌号列表 */
        String HOUSE_NUMBER_LIST = "/houseNumberList?";

        /** 公告列表 */
        String NOTICE_LIST = "/noticeList?";

        /** 公告详情 */
        String NOTICE_DETAILS = "/notice.html?";

        /** 手机远程开门 */
        String MOBILE_OPEN_DOOR = "/mobileOpenDoor?";

        /** 访客预约 */
        String MOBILE_ADD_VISITOR = "/mobileAddVisitor";

        /** 文件上传 */
        String MOBILE_UPLOAD = "/mobileUpload";

        /** 修改用户头像 */
        String UPDATE_USER_PORTRAIT = "/updateUserPortrait";

        /** 修改用户手机号 */
        String UPDATE_USER_PHONE = "/updateUserPhone";

        /** 修改用户登录密码 */
        String UPDATE_USER_PWD = "/updateUserPWD";
    }

    /**
     * 获取小区列表
     * 
     * @param listener
     * @return
     */
    public static Cancelable getCommunityList(
            final RequestListener<GetCommunityListResult> listener) {
        return WebService.post(Url.COMMUNITY_LIST, null, null, listener,
                new GetCommunityListParser(listener));
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
     * 获取用户列表
     * 
     * @param listener
     * @return
     */
    public static Cancelable getUserList(
            final RequestListener<GetUserListResult> listener) {
        String[] keys = { RequestKey.TOKEN };
        String[] values = { MyApplication.instance.getUserInfo().getToken() };
        return WebService.post(Url.USER_LIST, keys, values, listener,
                new GetUserListParser(listener));
    }

    /**
     * 获取门禁钥匙列表
     * 
     * @param listener
     * @return
     */
    public static Cancelable getLockList(
            final RequestListener<GetLockListResult> listener) {
        String[] keys = { RequestKey.TOKEN };
        String[] values = { MyApplication.instance.getUserInfo().getToken() };
        return WebService.post(Url.LOCK_LIST, keys, values, listener,
                new GetLockListParser(listener));
    }

    /**
     * 获取公告列表
     * 
     * @param pageNum
     * @param listener
     * @return
     */
    public static Cancelable getNoticeList(int pageNum,
            final RequestListener<GetNoticeListResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.PAGE_NUM,
                RequestKey.PAGE_SIZE };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                String.valueOf(pageNum), String.valueOf(LIMIT) };
        return WebService.post(Url.NOTICE_LIST, keys, values, listener,
                new GetNoticeListParser(listener));
    }

    /**
     * 手机远程开门
     * 
     * @param doorId
     * @param listener
     * @return
     */
    public static Cancelable mobileOpenDoor(String doorId,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.DOOR_ID };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                doorId };
        return WebService.post(Url.MOBILE_OPEN_DOOR, keys, values, listener,
                new RequestResultParser(listener));
    }

    /**
     * 访客预约
     * 
     * @param visitorName
     * @param startTime
     * @param endTime
     * @param phoneNum
     * @param remark
     * @param listener
     * @return
     */
    public static Cancelable mobileAddVisitor(String visitorName,
            String startTime, String endTime, String phoneNum, String remark,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.VISITOR_NAME,
                RequestKey.START_TIME, RequestKey.END_TIME,
                RequestKey.PHONE_NUM, RequestKey.REMARK };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                visitorName, startTime, endTime, phoneNum, remark };
        return WebService.post(Url.MOBILE_ADD_VISITOR, keys, values, listener,
                new RequestResultParser(listener));
    }

    /**
     * 修改用户头像
     * 
     * @param userPic
     * @param listener
     * @return
     */
    public static Cancelable mobileUpload(String filename,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.FILENAME,
                RequestKey.NAME };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                filename, "file" };
        return WebService.post(Url.MOBILE_UPLOAD, keys, values, listener,
                new RequestResultParser(listener));
    }

    /**
     * 修改用户头像
     * 
     * @param userPic
     * @param listener
     * @return
     */
    public static Cancelable updateUserPortrait(String userPic,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.USER_PIC };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                userPic };
        return WebService.post(Url.UPDATE_USER_PORTRAIT, keys, values,
                listener, new RequestResultParser(listener));
    }

    /**
     * 修改用户手机号
     * 
     * @param mobile
     * @param listener
     * @return
     */
    public static Cancelable updateUserPhone(String mobile,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.MOBILE };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                mobile };
        return WebService.post(Url.UPDATE_USER_PHONE, keys, values, listener,
                new RequestResultParser(listener));
    }

    /**
     * 修改用户登录密码
     * 
     * @param pwd
     * @param listener
     * @return
     */
    public static Cancelable updateUserPwd(String pwd,
            final RequestListener<RequestResult> listener) {
        String[] keys = { RequestKey.TOKEN, RequestKey.PWD };
        String[] values = { MyApplication.instance.getUserInfo().getToken(),
                pwd };
        return WebService.post(Url.UPDATE_USER_PWD, keys, values, listener,
                new RequestResultParser(listener));
    }

}
