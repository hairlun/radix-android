package com.patr.radix.bean;


public class GetNoticeDetailsResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = -4412300473031306876L;

    private String url;

    public GetNoticeDetailsResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
