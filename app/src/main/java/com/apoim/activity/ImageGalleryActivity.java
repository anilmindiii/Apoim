package com.apoim.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apoim.R;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.util.VerticalViewPager;
import com.apoim.util.ViewPagerAdapter;

import java.util.ArrayList;

public class ImageGalleryActivity extends AppCompatActivity {
    private VerticalViewPager viewPager;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private ViewPagerAdapter viewPagerAdapter;
    private GetOtherProfileInfo profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        viewPager = findViewById(R.id.viewPager);
        sliderDotspanel =  findViewById(R.id.SliderDots);

        profileImage = new GetOtherProfileInfo();

        profileImage.UserDetail.profileImage = (ArrayList<GetOtherProfileInfo.UserDetailBean.ImagesBean>) getIntent().getSerializableExtra("profileImage");

        viewPagerAdapter = new ViewPagerAdapter(this,profileImage.UserDetail.profileImage);
        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];
        sliderDotspanel.removeAllViews();

        for(int i = 0; i < dotscount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 6, 0, 6);
            sliderDotspanel.addView(dots[i], params);

        }

        if(dots.length != 0)
            dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this, R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(ImageGalleryActivity.this, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
