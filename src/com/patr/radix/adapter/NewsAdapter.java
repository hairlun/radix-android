package com.patr.radix.adapter;

import java.util.ArrayList;
import java.util.List;

import com.patr.radix.R;
import com.patr.radix.bean.Message;

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
        }
        View vLayout = hd.obtainView(R.id.v_layout);
        View hLayout = hd.obtainView(R.id.h_layout);
        if (item.isHorizontal()) {
            List<NetImageView> ivs = new ArrayList<NetImageView>();
            hLayout.setVisibility(View.VISIBLE);
            vLayout.setVisibility(View.GONE);
            NetImageView picIv1 = hd.obtainView(R.id.item_news_iv1);
            NetImageView picIv2 = hd.obtainView(R.id.item_news_iv2);
            NetImageView picIv3 = hd.obtainView(R.id.item_news_iv3);
            ivs.add(picIv1);
            ivs.add(picIv2);
            ivs.add(picIv3);
            TextView titleTv = hd.obtainView(R.id.item_news_title_h_tv);
            TextView countTv = hd.obtainView(R.id.item_news_piccount_tv);
            ImageView videoIv = hd.obtainView(R.id.item_video_h_iv);

            int size = item.getImageUrls().size();
            int count = 0;
            for (int i = 0; i < size; i++) {
                String url = item.getImageUrls().get(i);
                if (TextUtils.isEmpty(url) && i < size - 3) {
                    continue;
                }
                NetImageView picIv = ivs.get(count);
                picIv.setErrorGone(false);
                picIv.setDefaultImageResId(R.drawable.image_default);
                picIv.setErrorImageResId(R.drawable.image_default);
                picIv.setImageUrl(url, ImageCacheManager.getInstance()
                        .getImageLoader());
                if (++count == 3) {
                    break;
                }
            }
            titleTv.setText(item.getTitle());
            videoIv.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);
            countTv.setText(size + "图");
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
        ImageView pic;
        TextView title;
        TextView content;
        TextView sentDate;
    }
}
