package com.patr.radix.adapter;

import java.util.List;

import org.xutils.x;

import com.patr.radix.R;
import com.patr.radix.bean.Message;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeListAdapter extends AbsListAdapter<Message> {

    public NoticeListAdapter(Context context, List<Message> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_message, null);
            holder.pic = (ImageView) convertView
                    .findViewById(R.id.iv);
            holder.title = (TextView) convertView
                    .findViewById(R.id.title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.content);
            holder.sentDate = (TextView) convertView
                    .findViewById(R.id.time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Message notice = getItem(position);
        String imgUrl = notice.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
            holder.pic.setVisibility(View.VISIBLE);
            x.image().bind(holder.pic, imgUrl);
        } else {
            holder.pic.setVisibility(View.GONE);
        }
        holder.title.setText(notice.getTitle());
        holder.content.setText(notice.getPlainText());
        holder.sentDate.setText(notice.getSentDate());
        return convertView;

    }

    class ViewHolder {
        ImageView pic;
        TextView title;
        TextView content;
        TextView sentDate;
    }
}
