package com.patr.radix.adapter;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.adapter.KeyListAdapter.ViewHolder;
import com.patr.radix.bean.Message;
import com.patr.radix.view.NetImageView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends AbsListAdapter<Message> {

    public NewsAdapter(Context context, List<Message> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_message, null);
            holder.pic = (NetImageView) convertView.findViewById(R.id.item_message_niv);
            holder.title = (TextView) convertView.findViewById(R.id.item_message_title_tv);
            holder.content = (TextView) convertView.findViewById(R.id.item_message_content_tv);
            holder.from = (TextView) convertView.findViewById(R.id.item_message_from_tv);
            holder.sentDate = (TextView) convertView.findViewById(R.id.item_message_datetime_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Message message = getItem(position);
        holder.pic.setErrorGone(false);
        String imgUrl = message.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
            holder.pic.setVisibility(View.VISIBLE);
            holder.pic.setDefaultImageResId(R.drawable.image_default);
            holder.pic.setErrorImageResId(R.drawable.image_default);
            holder.pic.setImageUrl(imgUrl, ImageCacheManager.getInstance()
                    .getImageLoader());
        }
        return convertView;
        View vLayout = hd.obtainView(R.id.v_layout);
        View hLayout = hd.obtainView(R.id.h_layout);
        if (item.isHorizontal()) {
        } else {
            hLayout.setVisibility(View.GONE);
            vLayout.setVisibility(View.VISIBLE);
            NetImageView picIv = hd.obtainView(R.id.item_news_iv);
            picIv.setErrorGone(false);
            ImageView videoIv = hd.obtainView(R.id.item_video_iv);
            TextView titleTv = hd.obtainView(R.id.item_news_title_tv);
            TextView datetimeTv = hd.obtainView(R.id.item_news_datetime_tv);

            String imageUrl = item.getThumbImageUrl();
            if (TextUtils.isEmpty(imageUrl)) {
                picIv.setVisibility(View.GONE);
            } else {
                picIv.setDefaultImageResId(R.drawable.image_default);
                picIv.setErrorImageResId(R.drawable.image_default);
                picIv.setImageUrl(imageUrl, ImageCacheManager.getInstance()
                        .getImageLoader());
            }
            titleTv.setText(item.getTitle());
            datetimeTv.setText(item.getDatetime());
            videoIv.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);
        }
        
    }
    
    class ViewHolder {
        NetImageView pic;
        TextView title;
        TextView content;
        TextView from;
        TextView sentDate;
    }
}
