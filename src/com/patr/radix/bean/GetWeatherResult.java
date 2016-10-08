/**
 * radix
 * GetWeatherResult
 * zhoushujie
 * 2016-10-8 下午1:19:05
 */
package com.patr.radix.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhoushujie
 *
 */
public class GetWeatherResult implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 6157463050887255362L;

    private String response;
    
    private String errorCode;
    
    private String reason;
    
    private String city;
    
    private String temp;
    
    private String wet;
    
    private String weather;
    
    private String img;
    
    private String windDirect;
    
    private String windPower;
    
    private String pm25;
    
    private List<HashMap<String, String>> list = new ArrayList<>();

    /**
     * @param errorCode
     * @param reason
     */
    public GetWeatherResult(String errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWet() {
        return wet;
    }

    public void setWet(String wet) {
        this.wet = wet;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<HashMap<String, String>> getList() {
        return list;
    }

    public void setList(List<HashMap<String, String>> list) {
        this.list = list;
    }

    public String getWindDirect() {
        return windDirect;
    }

    public void setWindDirect(String windDirect) {
        this.windDirect = windDirect;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

}
