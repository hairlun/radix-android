package com.patr.radix.bll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.patr.radix.App;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.QueryPersonMessageResult;
import com.patr.radix.bean.PersonMessage;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

public class QueryPersonMessageParser extends AbsBaseParser<QueryPersonMessageResult> {

    public QueryPersonMessageParser(IAsyncListener<QueryPersonMessageResult> listener) {
        super(listener);
    }

    @Override
    public QueryPersonMessageResult parse(String response) {
        QueryPersonMessageResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new QueryPersonMessageResult(retcode, retinfo);
                JSONObject pageModel = json
                        .optJSONObject(ResponseKey.PAGE_MODEL);
                if (pageModel != null) {
                    int curPage = pageModel.optInt(ResponseKey.CURRENT_PAGE);
                    int totalCount = pageModel.optInt(ResponseKey.TOTAL_COUNT);
                    int totalPage = pageModel.optInt(ResponseKey.TOTAL_PAGE);
                    result.setCurPage(curPage);
                    result.setTotalCount(totalCount);
                    result.setTotalPage(totalPage);
                    JSONArray data = pageModel.optJSONArray(ResponseKey.DATA);
                    if (data != null) {
                        int size = data.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject obj = data.optJSONObject(i);
                            if (obj != null) {
                                String id = obj.optString(ResponseKey.ID);
                                String name = obj.optString(ResponseKey.NAME);
                                String dateTime = obj
                                        .optString(ResponseKey.DATE_TIME);
                                String ctrName = obj
                                        .optString(ResponseKey.CTR_NAME);
                                int outOrIn = obj
                                        .optInt(ResponseKey.OUT_OR_IN);
                                int type = obj.optInt(ResponseKey.TYPE);
                                PersonMessage message = new PersonMessage();
                                message.setId(id);
                                message.setName(name);
                                message.setDateTime(dateTime);
                                message.setCtrName(ctrName);
                                message.setOutOrIn(outOrIn);
                                message.setType(type);
                                result.getMessages().add(message);
                            }
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
