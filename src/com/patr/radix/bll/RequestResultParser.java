package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.RequestResult;
import com.patr.radix.network.IAsyncListener;

public class RequestResultParser extends AbsBaseParser<RequestResult> {

    public RequestResultParser(IAsyncListener<RequestResult> listener) {
        super(listener);
    }

    @Override
    public RequestResult parse(String response) {
        RequestResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new RequestResult(retcode, retinfo);
                result.setResponse(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
