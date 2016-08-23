/**
 * radix
 * GetUserListResult
 * zhoushujie
 * 2016-8-23 下午6:01:54
 */
package com.patr.radix.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoushujie
 * 
 */
public class GetUserListResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = -9159031437163245698L;

    private List<UserInfo> users = new ArrayList<UserInfo>();

    /**
     * @param retcode
     * @param retinfo
     */
    public GetUserListResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

}
