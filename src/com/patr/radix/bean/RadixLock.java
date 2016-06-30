/**
 * radix
 * UnlockKey
 * zhoushujie
 * 2016-6-23 上午10:33:12
 */
package com.patr.radix.bean;

import java.io.Serializable;

/**
 * @author zhoushujie
 *
 */
public class RadixLock implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4531879147539004767L;
    
    private String id;

    private String name;
    
    private String bleName;
    
    private String key;
    
    private String start;
    
    private String end;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
