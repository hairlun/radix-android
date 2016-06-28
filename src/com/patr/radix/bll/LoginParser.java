package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.LoginResult;
import com.patr.radix.bean.RequestResult;
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
                String userid = json.optString(ResponseKey.USER_ID);
                result.setUserid(userid);
                if (result.isSuccesses()) {
                    result.setResponse(response);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
