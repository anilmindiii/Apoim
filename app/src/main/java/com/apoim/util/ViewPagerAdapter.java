package com.apoim.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.apoim.R;
import com.apoim.activity.SliderImageActivity;
import com.apoim.modal.GetOtherProfileInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 13/3/18.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> images ;


    public ViewPagerAdapter(Context context, List<GetOtherProfileInfo.UserDetailBean.ImagesBean> images1) {
        this.context = context;
        this.images = new ArrayList<>();

        for(int i=0;i<images1.size();i++){
            String img = images1.get(i).image;
            this.images.add(img);
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_pager_item_layout, null);
        ImageView imageView =  view.findViewById(R.id.imageView);
        //imageView.setImageResource(images.get(position));


        if(!images.get(position).equals("")){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ico_user_placeholder);
            Glide.with(imageView.getContext()).load(images.get(position)).apply(requestOptions).into(imageView);
           /* Picasso.with(context).load(images.get(position)).placeholder(R.drawable.ico_user_placeholder)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });*/
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!images.get(position).equals("")){
                    Intent  intent = new Intent(context, SliderImageActivity.class);
                    intent.putStringArrayListExtra("imagelist",images);
                    context.startActivity(intent);
                }

            }
        });
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}