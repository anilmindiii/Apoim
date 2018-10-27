package com.apoim.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.imageGallery.GalleryRecyclerAdapter;
import com.apoim.adapter.imageGallery.GalleryViewPagerAdapter;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.ProfileImageModel;
import com.apoim.volley.AppHelper;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchGalleryActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private String userId;
    private int image_index;

    private ArrayList<ProfileImageModel> imagesList;
    private RelativeLayout rl_match_gallery;
    private ViewPager gallery_view_pager;
    private GalleryViewPagerAdapter pagerAdapter;
    //private RecyclerView galleryRecyclerView;
    private GalleryRecyclerAdapter recyclerAdapter;
    private GetOtherProfileInfo otherProfileInfo;
    private EventDetailsInfo.DetailBean detailBean;

    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_gallery);

        imagesList = new ArrayList<>();

        // Get User Id whose gallery Images has to display
        // Image Index on which end user clicked
        if (getIntent() != null) {
            if (getIntent().getStringExtra("user_id") != null) {
                userId = getIntent().getStringExtra("user_id");
            }

            if (getIntent().getIntExtra("image_index", 0) != 0) {
                image_index = getIntent().getIntExtra("image_index", 0);
            }

            otherProfileInfo = (GetOtherProfileInfo) getIntent().getSerializableExtra("otherProfileInfo");
            detailBean = (EventDetailsInfo.DetailBean) getIntent().getSerializableExtra("eventImages");
        }

        init();

        // Setting Gallery Adapter
        galleryAdapters();
        if(otherProfileInfo != null){
            setUserData(otherProfileInfo);
        }else {
            if(detailBean != null)
            setUserDataEvent(detailBean);
        }


        // Click Listeners
    }

    // Setting Gallery Adapter
    private void galleryAdapters() {
        // View pager adapter to display profile image
        pagerAdapter = new GalleryViewPagerAdapter(MatchGalleryActivity.this, imagesList);
        gallery_view_pager.setAdapter(pagerAdapter);
        gallery_view_pager.setOnPageChangeListener(this);

        // Adapter to set recycler view to display images in bottom as gallery
        recyclerAdapter = new GalleryRecyclerAdapter(imagesList, this, new GetNewImageClick() {
            @Override
            public void imageClick(int position) {
                gallery_view_pager.setCurrentItem(position, true);

            }
        });
       /* LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryRecyclerView.setLayoutManager(layoutManager);
        galleryRecyclerView.setAdapter(recyclerAdapter);*/

    }

    private void init() {
        rl_match_gallery = findViewById(R.id.rl_match_gallery);
        gallery_view_pager = findViewById(R.id.gallery_view_pager);
        rl_match_gallery.setVisibility(View.GONE);
        /*galleryRecyclerView = findViewById(R.id.gallery_recycler_view);*/
    }


    // Getting User Images and adding in list to display
    private void setUserData(GetOtherProfileInfo getUser) {
        if (getUser.UserDetail.profileImage.size() > 0) {
            for (int i = 0; i < getUser.UserDetail.profileImage.size(); i++) {
                ProfileImageModel imageModal = new ProfileImageModel();
                imageModal.imageId = getUser.UserDetail.profileImage.get(i).userImgId;
                imageModal.profileUrl = getUser.UserDetail.profileImage.get(i).image;
                /*imageModal.mediumUrl = getUser.userDetail.images.get(i).imageOriginal;*/
                imagesList.add(imageModal);
            }

            imagesList.get(image_index).isSelected = true;

            rl_match_gallery.setVisibility(View.VISIBLE);
            pagerAdapter.notifyDataSetChanged();
            gallery_view_pager.setCurrentItem(image_index, true);
          /*  galleryRecyclerView.scrollToPosition(image_index);*/
        }
       
    }


    // Getting User Images and adding in list to display
    private void setUserDataEvent(EventDetailsInfo.DetailBean getUser) {
        if (getUser.eventImage.size() > 0) {
            for (int i = 0; i <getUser.eventImage.size() ; i++) {
                ProfileImageModel imageModal = new ProfileImageModel();
                imageModal.imageId = getUser.eventImage.get(i).eventImgId;
                imageModal.profileUrl = getUser.eventImage.get(i).eventImage;
                /*imageModal.mediumUrl = getUser.userDetail.images.get(i).imageOriginal;*/
                imagesList.add(imageModal);
            }

            imagesList.get(image_index).isSelected = true;

            rl_match_gallery.setVisibility(View.VISIBLE);
            pagerAdapter.notifyDataSetChanged();
            gallery_view_pager.setCurrentItem(image_index, true);
          /*  galleryRecyclerView.scrollToPosition(image_index);*/
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        // Preventing multiple clicks, using threshold of 1/2 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {     // Setting view pager item
        for (ProfileImageModel model : imagesList) {
            model.isSelected = false;
        }
        imagesList.get(position).isSelected = true;
       /* galleryRecyclerView.smoothScrollToPosition(position);*/
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
