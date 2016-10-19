package com.patr.radix.adapter;

import java.util.List;

import org.xutils.x;

import com.patr.radix.R;
import com.patr.radix.bean.PersonMessage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends AbsListAdapter<PersonMessage> {

    public MessageListAdapter(Context context, List<PersonMessage> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_person_message, null);
            holder.pic = (ImageView) convertView.findViewById(R.id.avatar);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.sentDate = (TextView) convertView.findViewById(R.id.time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PersonMessage message = getItem(position);
        if (message.getType() == 0) {
            // 家人消息
            holder.pic.setImageResource(R.drawable.personal_news);
            holder.title.setText("家人消息");
        } else {
            // 访客消息
            holder.pic.setImageResource(R.drawable.vistor_news);
            holder.title.setText("访客消息");
        }

        holder.content.setText(String.format("%s %s：%s", message.getName(),
                message.getOutOrIn() == 0 ? "进入" : "离开", message.getCtrName()));
        holder.sentDate.setText(message.getDateTime());
        return convertView;

    }

    class ViewHolder {
        ImageView pic;
        TextView title;
        TextView content;
        TextView sentDate;
    }
}
