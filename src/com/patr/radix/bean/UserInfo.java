/**
 * radix
 * UserInfo
 * zhoushujie
 * 2016-7-26 上午8:45:04
 */
package com.patr.radix.bean;

import java.io.Serializable;

/**
 * @author zhoushujie
 *
 */
public class UserInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5351676968034757995L;
    
    private String id;

    private String account;
    
    private String areaId;
    
    private String areaName;
    
    private String name;
    
    private String home;
    
    private String mobile;
    
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
