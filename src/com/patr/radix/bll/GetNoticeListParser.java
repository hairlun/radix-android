package com.patr.radix.bll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.GetNoticeListResult;
import com.patr.radix.bean.Notice;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager.ResponseKey;
import com.patr.radix.network.IAsyncListener;

public class GetNoticeListParser extends AbsBaseParser<GetNoticeListResult> {

    public GetNoticeListParser(IAsyncListener<GetNoticeListResult> listener) {
        super(listener);
    }

    @Override
    public GetNoticeListResult parse(String response) {
        GetNoticeListResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String retcode = json.optString(RequestResult.RET_CODE_KEY);
                String retinfo = json.optString(RequestResult.RET_INFO_KEY);
                result = new GetNoticeListResult(retcode, retinfo);
                JSONObject pageModel = json.optJSONObject(ResponseKey.PAGE_MODEL);
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
                                String title = obj.optString(ResponseKey.TITLE);
                                String sentDate = obj.optString(ResponseKey.SENT_DATE);
                                String content = obj.optString(ResponseKey.CONTENT);
                                String readTime = obj.optString(ResponseKey.READ_TIME);
                                String imgUrl = obj.optString(ResponseKey.PIC);
                                int video = obj.optInt(ResponseKey.VIDEO);
                                Notice notice = new Notice();
                                notice.setId(id);
                                notice.setTitle(title);
                                notice.setSentDate(sentDate);
                                notice.setContent(content);
                                notice.setReadTime(readTime);
                                notice.setImgUrl(imgUrl);
                                notice.setVideo(video == 1 ? true : false);
                                result.getNotices().add(notice);
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
