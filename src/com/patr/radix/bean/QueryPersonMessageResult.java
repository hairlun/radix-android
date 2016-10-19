package com.patr.radix.bean;

import java.util.ArrayList;
import java.util.List;

public class QueryPersonMessageResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = -2802166331101655269L;

    private List<PersonMessage> messages = new ArrayList<PersonMessage>();

    private int curPage;

    private int totalCount;

    private int totalPage;

    public QueryPersonMessageResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public List<PersonMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PersonMessage> messages) {
        this.messages = messages;
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
