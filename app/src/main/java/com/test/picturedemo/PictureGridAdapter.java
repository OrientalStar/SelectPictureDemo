package com.test.picturedemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by ponos
 * 2017/8/7.
 * Description:图片适配器
 */

public class PictureGridAdapter extends BaseAdapter{
    private Activity activity;
    private ArrayList<PictureBean> mList;

    public PictureGridAdapter(Activity activity, ArrayList<PictureBean> list) {
        super();
        this.activity = activity;
        this.mList = list;

    }
    @Override
    public int getCount() {

        return mList == null ? 1 : mList.size() + 1;
    }
    @Override
    public Object getItem(int position) {
        if (mList == null) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (activity).inflate(R.layout.grid_picture_item, null);
            holder.cookImg = (ImageView)convertView.findViewById(R.id.img_picture);
            holder.statusImg = (ImageView)convertView.findViewById(R.id.img_delete);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList != null && position < mList.size()) {
            PictureBean mBean = (PictureBean)getItem(position);
            String imgUrl = mBean.getImgPath();
            Picasso.with(activity)
                    .load("file://"+imgUrl)
                    .placeholder(R.mipmap.bg_photo_normal)
                    .error(R.mipmap.bg_photo_normal)
                    .into(holder.cookImg);
            holder.statusImg.setVisibility(View.VISIBLE);

            holder.statusImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mList.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            // 手动增加的这个Item的显示和功能实现
            holder.cookImg.setImageResource(R.mipmap.bg_photo_normal);
            holder.statusImg.setVisibility(View.GONE);
            if (mList.size() != 4){
                holder.cookImg.setImageResource(R.mipmap.bg_photo_normal);
                holder.cookImg.setVisibility(View.VISIBLE);
            }else {
                holder.cookImg.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView cookImg,statusImg;
    }
}
