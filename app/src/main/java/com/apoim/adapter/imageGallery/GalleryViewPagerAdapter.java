package com.apoim.adapter.imageGallery;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.apoim.R;
import com.apoim.modal.ProfileImageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryViewPagerAdapter extends PagerAdapter {
    private Activity mContext;
    private ArrayList<ProfileImageModel> imageList;
    private LayoutInflater inflater;


    public GalleryViewPagerAdapter(Activity mContext, ArrayList<ProfileImageModel> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.view_match_gallery_view_pager, container, false);

        // create a ImageView programmatically
        ImageView imageView = view.findViewById(R.id.iv_user_image);


        Zoomy.Builder builder = new Zoomy.Builder(mContext).target(imageView);

        builder.tapListener(new TapListener() {
            @Override
            public void onTap(View v) {
                mContext.finish();
            }
        });
        builder.register();

        Glide.with(mContext).load(imageList.get(position).profileUrl).apply(new RequestOptions()
                .placeholder(R.drawable.ico_user_placeholder)).into(imageView);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
