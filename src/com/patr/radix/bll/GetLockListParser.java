/**
 * radix
 * GetLockListParser
 * zhoushujie
 * 2016-6-23 下午4:20:19
 */
package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class GetLockListParser extends AbsBaseParser<GetLockListResult> {

    /**
     * @param listener
     */
    public GetLockListParser(IAsyncListener<GetLockListResult> listener) {
        super(listener);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.patr.radix.network.IAsyncListener.ResultParser#parse(java.lang.String)
     */
    @Override
    public GetLockListResult parse(String response) {
        GetLockListResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new GetLockListResult(retcode, retinfo);
                if (!json.isNull(ResponseKey.LOCK_LIST)) {
                    
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
