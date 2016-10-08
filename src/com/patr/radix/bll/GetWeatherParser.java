/**
 * radix
 * GetWeatherParser
 * zhoushujie
 * 2016-10-8 下午2:21:24
 */
package com.patr.radix.bll;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.patr.radix.bean.GetWeatherResult;
import com.patr.radix.network.IAsyncListener;

/**
 * @author zhoushujie
 *
 */
public class GetWeatherParser extends AbsBaseParser<GetWeatherResult> {

    /**
     * @param listener
     */
    public GetWeatherParser(IAsyncListener<GetWeatherResult> listener) {
        super(listener);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.network.IAsyncListener.ResultParser#parse(java.lang.String)
     */
    @Override
    public GetWeatherResult parse(String response) {
        GetWeatherResult result = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                String errorCode = json.optString("error_code");
                String reason = json.optString("reason");
                result = new GetWeatherResult(errorCode, reason);
                result.setResponse(response);
                if (errorCode.equals("0")) {
                    JSONObject data = json.optJSONObject("result").optJSONObject("data");
                    if (data != null) {
                        JSONObject robj = data.optJSONObject("realtime");
                        if (robj != null) {
                            String city = robj.optString("city_name");
                            result.setCity(city);
                            JSONObject rwobj = robj.optJSONObject("weather");
                            if (rwobj != null) {
                                String temp = rwobj.optString("temperature");
                                String wet = rwobj.optString("humidity");
                                String weather = rwobj.optString("info");
                                String img = rwobj.optString("img");
                                result.setTemp(temp);
                                result.setWet(wet);
                                result.setWeather(weather);
                                result.setImg(img);
                            }
                            JSONObject wobj = robj.optJSONObject("wind");
                            if (wobj != null) {
                                String windDirect = wobj.optString("direct");
                                String windPower = wobj.optString("power");
                                result.setWindDirect(windDirect);
                                result.setWindPower(windPower);
                            }
                        }
                        JSONObject pm25obj = data.optJSONObject("pm25").optJSONObject("pm25");
                        if (pm25obj != null) {
                            String pm25 = pm25obj.optString("pm25");
                            result.setPm25(pm25);
                        }
                        JSONArray arr = data.optJSONArray("weather");
                        if (arr != null) {
                            int size = arr.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                HashMap<String, String> wmap = new HashMap<>();
                                String date = obj.optString("date");
                                String week = obj.optString("week");
                                String nongli = obj.optString("nongli");
                                wmap.put("date", date);
                                wmap.put("week", week);
                                wmap.put("nongli", nongli);
                                JSONArray day = obj.optJSONObject("info").optJSONArray("day");
                                if (day != null) {
                                    String dayImg = day.getString(0);
                                    String dayWeather = day.getString(1);
                                    String dayTemp = day.getString(2);
                                    String dayWindDirect = day.getString(3);
                                    String dayWindPower = day.getString(4);
                                    wmap.put("dayImg", dayImg);
                                    wmap.put("dayWeather", dayWeather);
                                    wmap.put("dayTemp", dayTemp);
                                    wmap.put("dayWindDirect", dayWindDirect);
                                    wmap.put("dayWindPower", dayWindPower);
                                }
                                JSONArray night = obj.optJSONObject("info").optJSONArray("night");
                                if (night != null) {
                                    String nightImg = night.getString(0);
                                    String nightWeather = night.getString(1);
                                    String nightTemp = night.getString(2);
                                    String nightWindDirect = night.getString(3);
                                    String nightWindPower = night.getString(4);
                                    wmap.put("nightImg", nightImg);
                                    wmap.put("nightWeather", nightWeather);
                                    wmap.put("nightTemp", nightTemp);
                                    wmap.put("nightWindDirect", nightWindDirect);
                                    wmap.put("nightWindPower", nightWindPower);
                                }
                                result.getList().add(wmap);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e);
        }
        return result;
    }

}
