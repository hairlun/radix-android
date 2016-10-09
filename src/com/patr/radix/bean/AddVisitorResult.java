/**
 * radix
 * AddVisitorResult
 * zhoushujie
 * 2016-10-9 上午8:28:26
 */
package com.patr.radix.bean;

/**
 * @author zhoushujie
 * 
 */
public class AddVisitorResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = 4179638600476874617L;
    private String visitorId;

    /**
     * @param retcode
     * @param retinfo
     */
    public AddVisitorResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

}
