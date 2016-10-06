/**
 * radix
 * MobileUploadParser
 * zhoushujie
 * 2016-10-6 上午10:44:13
 */
package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.MyApplication;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.MobileUploadResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class MobileUploadParser extends AbsBaseParser<MobileUploadResult> {

    /**
     * @param listener
     */
    public MobileUploadParser(IAsyncListener<MobileUploadResult> listener) {
        super(listener);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.network.IAsyncListener.ResultParser#parse(java.lang.String)
     */
    @Override
    public MobileUploadResult parse(String response) {
        MobileUploadResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new MobileUploadResult(retcode, retinfo);
                if (result.isSuccesses()) {
                    String userPic = json.optString(ResponseKey.FILE_PATH);
//                    if (!userPic.startsWith("http")) {
//                        Community community = MyApplication.instance
//                                .getSelectedCommunity();
//                        userPic = String.format("%s:%s/%s",
//                                community.getHost(), community.getPort(),
//                                userPic);
//                    }
                    result.setUserPic(userPic);
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
