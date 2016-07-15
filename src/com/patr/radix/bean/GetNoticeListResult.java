package com.patr.radix.bean;

import java.util.ArrayList;
import java.util.List;


public class GetNoticeListResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = -2802166331101655269L;
    
    private List<Notice> notices = new ArrayList<Notice>();
    
    private int curPage;
    
    private int totalCount;
    
    private int totalPage;

    public GetNoticeListResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

}
