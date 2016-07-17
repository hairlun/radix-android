/**
 * radix
 * GetCommunityListParser
 * zhoushujie
 * 2016-7-15 下午5:55:32
 */
package com.patr.radix.bll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.Community;
import com.patr.radix.bean.GetCommunityListResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class GetCommunityListParser extends
        AbsBaseParser<GetCommunityListResult> {

    public GetCommunityListParser() {
        super();
    }

    /**
     * @param listener
     */
    public GetCommunityListParser(
            IAsyncListener<GetCommunityListResult> listener) {
        super(listener);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.network.IAsyncListener.ResultParser#parse(java.lang.String)
     */
    @Override
    public GetCommunityListResult parse(String response) {
        GetCommunityListResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new GetCommunityListResult(retcode, retinfo);
                JSONArray array = json.optJSONArray(ResponseKey.COMMUNITY_LIST);
                if (array != null) {
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            String id = obj.optString(ResponseKey.AREA_ID);
                            String name = obj.optString(ResponseKey.AREA_NAME);
                            String host = obj.optString(ResponseKey.HOST);
                            String port = obj.optString(ResponseKey.PORT);
                            Community community = new Community();
                            community.setId(id);
                            community.setName(name);
                            community.setHost(host);
                            community.setPort(port);
                            result.getCommunities().add(community);
                        }
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
