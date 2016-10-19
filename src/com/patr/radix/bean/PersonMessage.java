/**
 * radix
 * PersonMessage
 * zhoushujie
 * 2016-10-18 下午4:07:42
 */
package com.patr.radix.bean;

import java.io.Serializable;

/**
 * @author zhoushujie
 *
 */
public class PersonMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2235756162853764654L;

    private String id;
    
    private String name;
    
    private String ctrName;
    
    private String dateTime;
    
    private int outOrIn;
    
    private int type;

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

    public String getCtrName() {
        return ctrName;
    }

    public void setCtrName(String ctrName) {
        this.ctrName = ctrName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getOutOrIn() {
        return outOrIn;
    }

    public void setOutOrIn(int outOrIn) {
        this.outOrIn = outOrIn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
