package com.patr.radix.bean;

import java.io.Serializable;

/***
 * 请求结果
 */
public class RequestResult implements Serializable {

    /**  */
    private static final long serialVersionUID = 5931089691718708406L;

    /** 请求结果码KEY */
    public static final String RET_INFO_KEY = "retinfo";

    /** 请求结果描述KEY */
    public static final String RET_CODE_KEY = "retcode";

    /**
     * 返回状态码
     */
    protected String retcode;
    
    /**
     * 返回信息描述
     */
    protected String retinfo;

    /**
     * 响应json字符串
     */
    private String response;

    /**
     * 无参构造方法
     */
    public RequestResult() {

    }

    /**
     * 全参构造方法
     * 
     * @param resultCode
     *            返回结果代码，成功返回1，失败返回0
     * @param resultMsg
     *            返回结果消息描述
     */
    public RequestResult(String retcode, String retinfo) {
        super();
        this.retcode = retcode;
        this.retinfo = retinfo;
    }

    /**
     * 返回结果是否成功
     * 
     * @return
     */
    public boolean isSuccesses() {
        return retcode.equals("1");
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(String retinfo) {
        this.retinfo = retinfo;
    }

    /**
     * 响应结果 JSON字符串
     * 
     * @return
     */
    public String getResponse() {
        return response;
    }

    /**
     * 响应结果 JSON字符串
     * 
     * @param response
     */
    public void setResponse(String response) {
        this.response = response;
    }

}
