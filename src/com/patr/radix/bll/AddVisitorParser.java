package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.AddVisitorResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

public class AddVisitorParser extends AbsBaseParser<AddVisitorResult> {

    public AddVisitorParser(IAsyncListener<AddVisitorResult> listener) {
        super(listener);
    }

    @Override
    public AddVisitorResult parse(String response) {
        AddVisitorResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new AddVisitorResult(retcode, retinfo);
                result.setResponse(response);
                if (result.isSuccesses()) {
                    String visitorId = json.optString(ResponseKey.VISITOR_ID);
                    result.setVisitorId(visitorId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
