/**
 * radix
 * WeatherActivity
 * zhoushujie
 * 2016-10-8 上午5:05:51
 */
package com.patr.radix;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * @author zhoushujie
 *
 */
public class WeatherActivity extends Activity {
    
    private Context context;
    
    private ImageView weatherIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
    }

    /**
     * 
     */
    private void initView() {
        weatherIv = (ImageView) findViewById(R.id.weather_iv);
    }

}
