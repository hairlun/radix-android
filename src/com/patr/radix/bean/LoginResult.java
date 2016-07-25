package com.patr.radix.bean;

/**
 * 登录结果包装
 */
public class LoginResult extends RequestResult {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4832219653928648661L;

    /** userid */
    private String userid;

    /**
     * 无参构造
     */
    public LoginResult() {

    }

    /**
     * 全参构造
     * 
     * @param resultCode
     * @param resultMsg
     */
    public LoginResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    /**
     * 设置userid
     * 
     * @param userid
     */
    public void setUserid(String userid) {
        if (userid == null) {
            userid = "";
        }
        this.userid = userid;
    }

    /**
     * 获取Token
     * 
     * @return
     */
    public String getUserid() {
        return userid == null ? "" : userid;
    }

    @Override
    public String toString() {
        return "LoginResult [userid=" + userid + ", isSuccesses()="
                + isSuccesses() + ", getRetcode()=" + getRetcode()
                + ", getRetinfo()=" + getRetinfo() + ", getResponse()="
                + getResponse() + "]";
    }

}
