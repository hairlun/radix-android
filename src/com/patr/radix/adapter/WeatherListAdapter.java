/**
 * radix
 * WeatherListAdapter
 * zhoushujie
 * 2016-10-8 下午3:53:37
 */
package com.patr.radix.adapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.adapter.KeyListAdapter.ViewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author zhoushujie
 *
 */
public class WeatherListAdapter extends AbsListAdapter<HashMap<String, String>> {

    /**
     * @param context
     * @param mList
     */
    public WeatherListAdapter(Context context, List<HashMap<String, String>> mList) {
        super(context, mList);
    }

    /* (non-Javadoc)
     * @see com.patr.radix.adapter.AbsListAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_weather, null);
            holder.date = (TextView) convertView.findViewById(R.id.date_tv);
            holder.week = (TextView) convertView.findViewById(R.id.week_tv);
            holder.nongli = (TextView) convertView.findViewById(R.id.nongli_tv);
            holder.weatherDayIv = (ImageView) convertView
                    .findViewById(R.id.weather_day_iv);
            holder.weatherDayTv = (TextView) convertView
                    .findViewById(R.id.weather_day_tv);
            holder.windDay = (TextView) convertView.findViewById(R.id.wind_day_tv);
            holder.weatherNightIv = (ImageView) convertView
                    .findViewById(R.id.weather_night_iv);
            holder.weatherNightTv = (TextView) convertView
                    .findViewById(R.id.weather_night_tv);
            holder.windNight = (TextView) convertView.findViewById(R.id.wind_night_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> wmap = getItem(position);
        holder.date.setText(wmap.get("date"));
        holder.week.setText("星期" + wmap.get("week"));
        holder.nongli.setText(wmap.get("nongli"));
        Field f;
        try {
            f = (Field) R.drawable.class
                    .getDeclaredField("w" + wmap.get("dayImg"));
            int id = f.getInt(R.drawable.class);
            holder.weatherDayIv.setImageResource(id);
            f = (Field) R.drawable.class
                    .getDeclaredField("w" + wmap.get("nightImg"));
            id = f.getInt(R.drawable.class);
            holder.weatherNightIv.setImageResource(id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        holder.weatherDayTv.setText(wmap.get("dayWeather") + ":" + wmap.get("dayTemp") + "℃");
        String dayWindDirect = wmap.get("dayWindDirect");
        if (!TextUtils.isEmpty(dayWindDirect)) {
            holder.windDay.setText(dayWindDirect + "-" + wmap.get("dayWindPower"));
        } else {
            holder.windDay.setText(wmap.get("dayWindPower"));
        }
        holder.weatherNightTv.setText(wmap.get("nightWeather") + ":" + wmap.get("nightTemp"));
        String nightWindDirect = wmap.get("nightWindDirect");
        if (!TextUtils.isEmpty(nightWindDirect)) {
            holder.windNight.setText(nightWindDirect + "-" + wmap.get("nightWindPower"));
        } else {
            holder.windNight.setText(wmap.get("nightWindPower"));
        }
        
        return convertView;
    }
    
    class ViewHolder {
        TextView date;
        TextView week;
        TextView nongli;
        ImageView weatherDayIv;
        TextView weatherDayTv;
        TextView windDay;
        ImageView weatherNightIv;
        TextView weatherNightTv;
        TextView windNight;
    }

}
