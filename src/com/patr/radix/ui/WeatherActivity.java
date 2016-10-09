/**
 * radix
 * WeatherActivity
 * zhoushujie
 * 2016-10-8 上午5:05:51
 */
package com.patr.radix.ui;

import java.lang.reflect.Field;

import com.patr.radix.R;
import com.patr.radix.R.id;
import com.patr.radix.R.layout;
import com.patr.radix.adapter.WeatherListAdapter;
import com.patr.radix.bean.GetWeatherResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author zhoushujie
 * 
 */
public class WeatherActivity extends Activity implements OnClickListener {

    private Context context;

    private ImageView weatherIv;

    private TextView weatherTv;

    private TextView tempTv;

    private TextView wetTv;

    private TextView pmTv;

    private TextView windDirectTv;

    private TextView windPowerTv;

    private TextView areaTv;

    private ImageButton closeBtn;

    private ListView weatherLv;
    
    private WeatherListAdapter adapter;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        context = this;
        initView();
        initData();
    }

    /**
     * 
     */
    private void initView() {
        weatherIv = (ImageView) findViewById(R.id.weather_iv);
        weatherTv = (TextView) findViewById(R.id.weather_tv);
        tempTv = (TextView) findViewById(R.id.temp_tv);
        wetTv = (TextView) findViewById(R.id.wet_tv);
        pmTv = (TextView) findViewById(R.id.pm_tv);
        windDirectTv = (TextView) findViewById(R.id.wind_direct_tv);
        windPowerTv = (TextView) findViewById(R.id.wind_power_tv);
        areaTv = (TextView) findViewById(R.id.area_tv);
        closeBtn = (ImageButton) findViewById(R.id.close_btn);
        weatherLv = (ListView) findViewById(R.id.weather_lv);

        closeBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(context);
        adapter = new WeatherListAdapter(context, null);
        weatherLv.setAdapter(adapter);
    }

    private void initData() {
        ServiceManager.getWeather(new RequestListener<GetWeatherResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在加载…");
            }

            @Override
            public void onSuccess(int stateCode, GetWeatherResult result) {
                if (result.getErrorCode().equals("0")) {
                    refresh(result);
                } else {
                    ToastUtil.showShort(context, result.getReason());
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, "网络异常，请稍后再试。");
                loadingDialog.dismiss();
            }

        });
    }

    private void refresh(GetWeatherResult result) {
        Field f;
        try {
            f = (Field) R.drawable.class
                    .getDeclaredField("ww" + result.getImg());
            int id = f.getInt(R.drawable.class);
            weatherIv.setImageResource(id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        weatherTv.setText(result.getWeather());
        tempTv.setText(result.getTemp() + "℃");
        wetTv.setText(result.getWet() + "%");
        windDirectTv.setText(result.getWindDirect());
        windPowerTv.setText(result.getWindPower());
        areaTv.setText(result.getCity());
        adapter.changeData(result.getList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close_btn:
            finish();
            break;
        }
    }

}
