/**
 * radix
 * MobileUploadResult
 * zhoushujie
 * 2016-10-6 上午10:39:28
 */
package com.patr.radix.bean;

/**
 * @author zhoushujie
 * 
 */
public class MobileUploadResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = -5400592558006089289L;
    private String userPic;

    /**
     * @param retcode
     * @param retinfo
     */
    public MobileUploadResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
