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
    
    private String bleName1;
    
    private String bleName2;
    
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

    public String getBleName1() {
        return bleName1 == null ? "" : bleName1;
    }

    public void setBleName1(String bleName1) {
        this.bleName1 = bleName1;
    }

    public String getBleName2() {
        return bleName2 == null ? "" : bleName2;
    }

    public void setBleName2(String bleName2) {
        this.bleName2 = bleName2;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RadixLock other = (RadixLock) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
