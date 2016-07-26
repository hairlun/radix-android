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
    private UserInfo userInfo;

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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}
