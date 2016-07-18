/**
 * radix
 * GetLockListParser
 * zhoushujie
 * 2016-6-23 下午4:20:19
 */
package com.patr.radix.bll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.GetLockListResult;
import com.patr.radix.bean.RadixLock;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class GetLockListParser extends AbsBaseParser<GetLockListResult> {

    /**
     * 
     */
    public GetLockListParser() {
        super();
    }

    /**
     * @param listener
     */
    public GetLockListParser(IAsyncListener<GetLockListResult> listener) {
        super(listener);
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
                JSONArray array = json.optJSONArray(ResponseKey.LOCK_LIST);
                if (array != null) {
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            String id = obj.optString(ResponseKey.ID);
                            String name = obj.optString(ResponseKey.NAME);
                            String bleName = obj.optString(ResponseKey.BLE_NAME);
                            String key = obj.optString(ResponseKey.KEY);
                            String start = obj.optString(ResponseKey.START);
                            String end = obj.optString(ResponseKey.END);
                            RadixLock lock = new RadixLock();
                            lock.setId(id);
                            lock.setName(name);
                            lock.setBleName(bleName);
                            lock.setKey(key);
                            lock.setStart(start);
                            lock.setEnd(end);
                            result.getLocks().add(lock);
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
