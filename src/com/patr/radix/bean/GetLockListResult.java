/**
 * radix
 * GetLockListResult
 * zhoushujie
 * 2016-6-23 下午3:29:46
 */
package com.patr.radix.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoushujie
 * 
 */
public class GetLockListResult extends RequestResult {

    /**
     * 
     */
    private static final long serialVersionUID = 6946254133401773568L;

    private List<RadixLock> locks = new ArrayList<RadixLock>();

    /**
     * @param retcode
     * @param retinfo
     */
    public GetLockListResult(String retcode, String retinfo) {
        super(retcode, retinfo);
    }

    public List<RadixLock> getLocks() {
        return locks;
    }

    public void setLocks(List<RadixLock> locks) {
        this.locks = locks;
    }

}
