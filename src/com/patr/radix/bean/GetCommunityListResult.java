/**
 * radix
 * GetCommunityListResult
 * zhoushujie
 * 2016-7-15 下午5:48:02
 */
package com.patr.radix.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoushujie
 * 
 */
public class GetCommunityListResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = 7359233401956300144L;

    private List<Community> communities = new ArrayList<Community>();

    /**
     * @param retcode
     * @param retinfo
     */
    public GetCommunityListResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

}
