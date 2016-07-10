package com.patr.radix.bll;

import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

public class GetNoticeDetailsParser extends
        AbsBaseParser<GetNoticeDetailsResult> {

    public GetNoticeDetailsParser(
            IAsyncListener<GetNoticeDetailsResult> listener) {
        super(listener);
    }

    @Override
    public GetNoticeDetailsResult parse(String response) {
        GetNoticeDetailsResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new GetNoticeDetailsResult(retcode, retinfo);
                String url = json.optString(ResponseKey.URL);
                result.setUrl(url);
                result.setResponse(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
