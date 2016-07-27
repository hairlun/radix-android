package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.LoginResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * 登录结果解析器
 */
public class LoginParser extends AbsBaseParser<LoginResult> {

    public LoginParser(IAsyncListener<LoginResult> listener) {
        super(listener);
    }

    @Override
    public LoginResult parse(String response) {
        LoginResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new LoginResult(retcode, retinfo);
                if (result.isSuccesses()) {
                    JSONObject obj = json.optJSONObject(ResponseKey.MODEL);
                    if (obj != null) {
                        String id = obj.optString(ResponseKey.ID);
                        String account = obj.optString(ResponseKey.USER_ID);
                        String areaId = obj.optString(ResponseKey.AREA_ID);
                        String areaName = obj.optString(ResponseKey.AREA_NAME);
                        String name = obj.optString(ResponseKey.NAME);
                        String mobile = obj.optString(ResponseKey.MOBILE);
                        String home = obj.optString(ResponseKey.HOME);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setId(id);
                        userInfo.setAccount(account);
                        userInfo.setAreaId(areaId);
                        userInfo.setAreaName(areaName);
                        userInfo.setName(name);
                        userInfo.setMobile(mobile);
                        userInfo.setHome(home);
                        result.setUserInfo(userInfo);
                    }
                }
                result.setResponse(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
