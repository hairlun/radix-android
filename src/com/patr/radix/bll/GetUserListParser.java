/**
 * radix
 * GetUserListParser
 * zhoushujie
 * 2016-8-23 下午6:17:06
 */
package com.patr.radix.bll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.MyApplication;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetUserListResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bean.UserInfo;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class GetUserListParser extends AbsBaseParser<GetUserListResult> {

    /**
     * 
     */
    public GetUserListParser() {
        super();
    }

    /**
     * @param listener
     */
    public GetUserListParser(IAsyncListener<GetUserListResult> listener) {
        super(listener);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.network.IAsyncListener.ResultParser#parse(java.lang.String)
     */
    @Override
    public GetUserListResult parse(String response) {
        GetUserListResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new GetUserListResult(retcode, retinfo);
                result.setResponse(response);
                if (result.isSuccesses()) {
                    JSONArray arr = json.optJSONArray(ResponseKey.USER_LIST);
                    if (arr != null) {
                        int size = arr.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject obj = arr.optJSONObject(i);
                            if (obj != null) {
                                String id = obj.optString(ResponseKey.ID);
                                String account = obj.optString(ResponseKey.USER_ID);
                                String areaId = obj.optString(ResponseKey.AREA_ID);
                                String areaName = obj.optString(ResponseKey.AREA_NAME);
                                String name = obj.optString(ResponseKey.NAME);
                                String mobile = obj.optString(ResponseKey.MOBILE);
                                String home = obj.optString(ResponseKey.HOME);
                                String areaPic = obj.optString(ResponseKey.AREA_PIC);
                                UserInfo userInfo = new UserInfo();
                                userInfo.setId(id);
                                userInfo.setAccount(account);
                                userInfo.setAreaId(areaId);
                                userInfo.setAreaName(areaName);
                                userInfo.setName(name);
                                userInfo.setMobile(mobile);
                                userInfo.setHome(home);
                                if (!areaPic.startsWith("http")) {
                                    Community community = MyApplication.instance.getSelectedCommunity();
                                    areaPic = String.format("%s:%s%s", community.getHost(), community.getPort(), areaPic);
                                }
                                userInfo.setAreaPic(areaPic);
                                result.getUsers().add(userInfo);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
