package com.apoim.activity.profile;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.chat.ChatActivity;
import com.apoim.activity.MatchGalleryActivity;
import com.apoim.activity.appointment.CreateAppointMentActivity;
import com.apoim.activity.business.BusinessDetailsActivity;
import com.apoim.adapter.ShowInterestAdapter;
import com.apoim.adapter.newProfile.NewProfileAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetInterestValueListener;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.apoim.util.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherProfileDetailsActivity extends AppCompatActivity {
   // private ViewPager viewPager;
    //private LinearLayout sliderDotspanel/*ly_uper_view*/;
    private int dotscount;
    private ImageView[] dots;
    private ArrayList<GetOtherProfileInfo.UserDetailBean> otherProfileList;
    private ArrayList<ProfileInterestInfo> interestArrayList;
    private ShowInterestAdapter showInterestAdapter;
    private RecyclerView profile_horizontal_recycler;
   // private RecyclerView user_selected_interest_list_view;
    private TextView tv_basic_info,tv_more_info;
    private LinearLayout ly_basic_info,ly_more_info,ly_request_accept_reject,ly_photo_view;

    private InsLoadingView loading_view;
    private String userId = "";
    private GetOtherProfileInfo otherProfileInfo;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView tv_fullName,tv_address,tv_about,tv_work,tv_education,
            tv_height,tv_weight,tv_marrige_status,tv_languge,tv_like_count,tv_age,tv_no_user_image_found
            ,tv_accept_friend,tv_remove_friend;
    //private RelativeLayout pager_view;
    //private NestedScrollView bottom_sheet;
    private RelativeLayout like_chat_direction_view;
    private ImageView iv_chat,iv_share,iv_back,iv_fevorate,iv_direction,iv_add_friend,iv_like,iv_profile_image,iv_remove_friend_request;
    private Session mSession;
    private TextView tv_about_me,tv_friend_status;
    private LinearLayout scr_shot_view;
    private NewProfileAdapter newProfileImageAdapter;
    LinearLayout ly_my_fevorite,ly_business;
    TextView tv_appoim_type,tv_event_type;

    private ImageView iv_mobile_veri, iv_id_hand_veri, iv_face_detection_veri;
    private ImageView iv_mobile_veri_active, iv_id_hand_veri_active, iv_face_detection_veri_active;
    private TextView tv_mobile_veri, tv_id_hand_veri, tv_face_detection_veri,tv_interest,tv_no_interest_found;
    private RelativeLayout ly_mobile_veri, ly_id_hand_veri, ly_face_detection_veri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("com.apoim"));

        setContentView(R.layout.new_other_profile);
        init();

        otherProfileInfo = new GetOtherProfileInfo();
        mSession = new Session(this,this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_fevorate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otherProfileInfo.UserDetail.userId != null && !otherProfileInfo.UserDetail.userId.equals("")) {

                    if(otherProfileInfo.UserDetail.isFavorite.equals("1")){
                        otherProfileInfo.UserDetail.isFavorite  = "0";
                    }else {
                        otherProfileInfo.UserDetail.isFavorite  = "1";
                    }
                    add_remove_Favourate(otherProfileInfo.UserDetail.userId,otherProfileInfo.UserDetail.isFavorite);
                }

            }
        });

        iv_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otherProfileInfo.UserDetail.isAppointment != null){
                    if(otherProfileInfo.UserDetail.isAppointment.equals("0")){
                        Intent intent = new Intent(OtherProfileDetailsActivity.this,CreateAppointMentActivity.class);
                        intent.putExtra("profileDetails",otherProfileInfo.UserDetail);
                        intent.putExtra("userId",userId);
                        startActivityForResult(intent,190);
                    }
                    else if(otherProfileInfo.UserDetail.isAppointment.equals("1")){
                        Utils.openAlertDialog(OtherProfileDetailsActivity.this, "You already sent appointment request to "+otherProfileInfo.UserDetail.fullName);
                    }
                    else  if(otherProfileInfo.UserDetail.isAppointment.equals("2")){
                        Utils.openAlertDialog(OtherProfileDetailsActivity.this, "You already have an appointment with "+otherProfileInfo.UserDetail.fullName);
                    }
                }
            }
        });


        if(getIntent().getStringExtra("userId") != null){
            userId = getIntent().getStringExtra("userId");
        }

        tv_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ly_more_info.setVisibility(View.VISIBLE);
                ly_basic_info.setVisibility(View.GONE);
                tv_more_info.setTextColor(getResources().getColor(R.color.colorBlack));
                tv_basic_info.setTextColor(getResources().getColor(R.color.colorGray));
            }
        });

        tv_basic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ly_more_info.setVisibility(View.GONE);
                ly_basic_info.setVisibility(View.VISIBLE);
                tv_basic_info.setTextColor(getResources().getColor(R.color.colorBlack));
                tv_more_info.setTextColor(getResources().getColor(R.color.colorGray));
            }
        });


       // viewPager = findViewById(R.id.viewPager);
        otherProfileList = new ArrayList<>();
        getProfileDetails();

/*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
*/


        iv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(otherProfileInfo.UserDetail.isRequest.equals("1")){
                    Utils.openAlertDialog(OtherProfileDetailsActivity.this,"You already sent friend request to "+otherProfileInfo.UserDetail.fullName);
                }else {
                    if(tv_friend_status.getText().toString().equals("Respond")){
                        acceptRejectDialog();
                    }else {
                        iv_add_friend.setEnabled(false);
                        add_remove_accept_Request(userId,"add","1","Friend request has been send successfully","1");

                    }

                }

            }
        });


        tv_accept_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_accept_friend.setEnabled(false);
                add_remove_accept_Request(userId,"edit","2","Friend request accepted","0");
            }
        });


        tv_remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_remove_friend.setEnabled(false);
                add_remove_accept_Request(userId,"edit","3","Friend request removed successfully","0");
            }
        });

        iv_remove_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_remove_friend_request.setEnabled(false);
                add_remove_accept_Request(userId,"edit","3","Friend request removed successfully","0");
            }
        });


        showInterestAdapter = new ShowInterestAdapter(Constant.OtherProfile,this, interestArrayList, new GetInterestValueListener() {
            @Override
            public void getInterestValue(String value) {

            }
        });
      //  user_selected_interest_list_view.setAdapter(showInterestAdapter);

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otherProfileInfo.UserDetail.userId != null && !otherProfileInfo.UserDetail.userId.equals("")) {

                    if( otherProfileInfo.UserDetail.isLike.equals("1")){
                        otherProfileInfo.UserDetail.isLike  = "0";
                    }else {
                        otherProfileInfo.UserDetail.isLike  = "1";
                    }
                    like_unlike_api(otherProfileInfo.UserDetail.userId,otherProfileInfo.UserDetail.isLike);
                }
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( otherProfileInfo.UserDetail.profileImage.size() > 0){
                    shareProfileToFriendDialog(tv_about.getText().toString().trim(),otherProfileInfo.UserDetail.profileImage.get(0).image);
                }

            }
        });

        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherProfileDetailsActivity.this, ChatActivity.class);
                intent.putExtra("otherUID",userId);
                intent.putExtra("titleName",otherProfileInfo.UserDetail.fullName);
                intent.putExtra("quickBloxId",otherProfileInfo.UserDetail.quickBloxId);

                if(otherProfileInfo.UserDetail.profileImage.size() != 0){
                    intent.putExtra("profilePic",otherProfileInfo.UserDetail.profileImage.get(0).image);
                }else {
                    intent.putExtra("profilePic","");
                }
                startActivity(intent);
            }
        });


        ly_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherProfileDetailsActivity.this, BusinessDetailsActivity.class);
                intent.putExtra("userId",otherProfileInfo.UserDetail.userId);
                startActivity(intent);
            }
        });

        iv_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otherProfileInfo.UserDetail.profileImage.size()> 0){
                    Intent intent = new Intent(OtherProfileDetailsActivity.this, MatchGalleryActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("image_index", 0);
                    intent.putExtra("otherProfileInfo", otherProfileInfo);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void acceptRejectDialog() {
        final Dialog accept_reject_dialog = new Dialog(this);
        accept_reject_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        accept_reject_dialog.setContentView(R.layout.view_dialog_accept_reject);
        accept_reject_dialog.setCancelable(false);
        accept_reject_dialog.setCanceledOnTouchOutside(false);

        final ImageView work_decline_button = accept_reject_dialog.findViewById(R.id.interest_decline_button);
        final RelativeLayout ly_accept = accept_reject_dialog.findViewById(R.id.ly_accept);
        final RelativeLayout ly_reject = accept_reject_dialog.findViewById(R.id.ly_reject);
        final TextView tv_name = accept_reject_dialog.findViewById(R.id.tv_name);

        tv_name.setText(otherProfileInfo.UserDetail.fullName);

        ly_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept_reject_dialog.dismiss();
                add_remove_accept_Request(userId,"edit","2","Friend request accepted","0");

            }
        });

        ly_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept_reject_dialog.dismiss();
                add_remove_accept_Request(userId,"edit","3","Friend request removed successfully","0");

            }
        });

        work_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept_reject_dialog.dismiss();
            }
        });

        accept_reject_dialog.getWindow().setGravity(Gravity.CENTER);
        accept_reject_dialog.show();
    }

    private void mobileVerificationSelcted() {
        iv_mobile_veri_active.setVisibility(View.VISIBLE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.GONE);
        iv_mobile_veri.setImageResource(R.drawable.active_sms);
        tv_mobile_veri.setTextColor(ContextCompat.getColor(OtherProfileDetailsActivity.this, R.color.colorBlack));
        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_green_border);
    }

    private void idWithHandSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.VISIBLE);
        iv_face_detection_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri.setImageResource(R.drawable.active_id);
        tv_id_hand_veri.setTextColor(ContextCompat.getColor(OtherProfileDetailsActivity.this, R.color.colorBlack));
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_green_border);
    }

    private void faceDetectionSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.VISIBLE);
        iv_face_detection_veri.setImageResource(R.drawable.active_face);
        tv_face_detection_veri.setTextColor(ContextCompat.getColor(OtherProfileDetailsActivity.this, R.color.colorBlack));
        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_green_border);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent1) {
            String type = intent1.getStringExtra("type");
            if(type.equals("add_like")){
                getProfileDetails();
            }
            else if(type.equals("accept_request")){
                tv_remove_friend.setVisibility(View.GONE);
                getProfileDetails();
            }
            else if(type.equals("friend_request")){
                tv_remove_friend.setVisibility(View.VISIBLE);
                tv_accept_friend.setVisibility(View.VISIBLE);
                ly_request_accept_reject.setVisibility(View.VISIBLE);
               // iv_add_friend.setVisibility(View.GONE);
               // iv_add_friend.setEnabled(false);
                getProfileDetails();
            }else {
                getProfileDetails();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 190){
                if(data.getStringExtra("status").equals("AppointmentRequestSent")){
                    otherProfileInfo.UserDetail.isAppointment = "1";
                }

            }
        }
    }



    private void init() {
        loading_view = findViewById(R.id.loading_view);

        tv_fullName = findViewById(R.id.tv_fullName);
        tv_address = findViewById(R.id.tv_address);
        tv_about = findViewById(R.id.tv_about);
        tv_work = findViewById(R.id.tv_work);
        tv_education = findViewById(R.id.tv_education);
        tv_height = findViewById(R.id.tv_height);
        tv_weight = findViewById(R.id.tv_weight);
        tv_marrige_status = findViewById(R.id.tv_marrige_status);
        tv_languge = findViewById(R.id.tv_languge);
        tv_like_count = findViewById(R.id.tv_like_count);
        tv_event_type = findViewById(R.id.tv_event_type);
        tv_appoim_type = findViewById(R.id.tv_appoim_type);
        tv_interest = findViewById(R.id.tv_interest);
        tv_friend_status = findViewById(R.id.tv_friend_status);
        tv_no_interest_found = findViewById(R.id.tv_no_interest_found);

        iv_direction = findViewById(R.id.iv_direction);
        tv_accept_friend = findViewById(R.id.tv_accept_friend);
        tv_remove_friend = findViewById(R.id.tv_remove_friend);
        iv_add_friend = findViewById(R.id.iv_add_friend);
        tv_no_user_image_found = findViewById(R.id.tv_no_user_image_found);
        iv_like = findViewById(R.id.iv_like);
        tv_age = findViewById(R.id.tv_age);
        tv_basic_info = findViewById(R.id.tv_basic_info);
        tv_more_info = findViewById(R.id.tv_more_info);
        ly_basic_info = findViewById(R.id.ly_basic_info);
        ly_more_info = findViewById(R.id.ly_more_info);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        profile_horizontal_recycler = findViewById(R.id.profile_horizontal_recycler);
        ly_request_accept_reject = findViewById(R.id.ly_request_accept_reject);
        ly_photo_view = findViewById(R.id.ly_photo_view);

        ly_my_fevorite = findViewById(R.id.ly_my_fevorite);
        ly_business = findViewById(R.id.ly_business);
        iv_remove_friend_request = findViewById(R.id.iv_remove_friend_request);

        iv_mobile_veri = findViewById(R.id.iv_mobile_veri);
        iv_id_hand_veri = findViewById(R.id.iv_id_hand_veri);
        iv_face_detection_veri = findViewById(R.id.iv_face_detection_veri);

        iv_mobile_veri_active = findViewById(R.id.iv_mobile_veri_active);
        iv_id_hand_veri_active = findViewById(R.id.iv_id_hand_veri_active);
        iv_face_detection_veri_active = findViewById(R.id.iv_face_detection_veri_active);

        tv_mobile_veri = findViewById(R.id.tv_mobile_veri);
        tv_id_hand_veri = findViewById(R.id.tv_id_hand_veri);
        tv_face_detection_veri = findViewById(R.id.tv_face_detection_veri);

        ly_mobile_veri = findViewById(R.id.ly_mobile_veri);
        ly_id_hand_veri = findViewById(R.id.ly_id_hand_veri);
        ly_face_detection_veri = findViewById(R.id.ly_face_detection_veri);


        ly_my_fevorite.setVisibility(View.GONE);

        //place_holder_img = findViewById(R.id.place_holder_img);
        //ly_uper_view =  findViewById(R.id.ly_uper_view);
        iv_chat =  findViewById(R.id.iv_chat);

       // pager_view = findViewById(R.id.pager_view);
        //bottom_sheet = findViewById(R.id.bottom_sheet);
        like_chat_direction_view = findViewById(R.id.like_chat_direction_view);
        iv_back = findViewById(R.id.iv_back);
        iv_share = findViewById(R.id.iv_share);
        iv_fevorate = findViewById(R.id.iv_fevorate);
       // user_selected_interest_list_view = findViewById(R.id.user_selected_interest_list_view);
        //int numberOfColumns = 2;
        //user_selected_interest_list_view.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        interestArrayList = new ArrayList<>();
    }

    private void getProfileDetails() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                like_chat_direction_view.setVisibility(View.VISIBLE);
               // bottom_sheet.setVisibility(View.VISIBLE);
                //pager_view.setVisibility(View.VISIBLE);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    otherProfileInfo.UserDetail.profileImage.clear();
                    if(dots != null){
                        dots = null;
                    }
                    if (status.equals("success")) {
                        interestArrayList.clear();
                        Gson gson = new Gson();
                        otherProfileInfo = gson.fromJson(response, GetOtherProfileInfo.class);
                        setData(otherProfileInfo.UserDetail);
                        //ly_uper_view.setVisibility(View.VISIBLE);
                        if(!otherProfileInfo.UserDetail.interest.equals("")){

                            List<String> interestList =  Arrays.asList(otherProfileInfo.UserDetail.interest.split(","));
                            for(int i = 0;i<interestList.size();i++){
                                ProfileInterestInfo interestInfo = new ProfileInterestInfo();
                                interestInfo.interest = interestList.get(i);
                                interestArrayList.add(interestInfo);
                            }

                            String allInterest = getCommonSeperatedString(interestArrayList);
                            tv_interest.setText(allInterest);

                            if(!allInterest.equals("")){
                                tv_interest.setVisibility(View.VISIBLE);
                                tv_no_interest_found.setVisibility(View.GONE);
                            }else {
                                tv_interest.setVisibility(View.GONE);
                                tv_no_interest_found.setVisibility(View.VISIBLE);
                            }

                              showInterestAdapter.notifyDataSetChanged();

                        }

                      /*  if(otherProfileInfo.UserDetail.images.size() == 0){
                            place_holder_img.setVisibility(View.VISIBLE);
                        }else {
                            place_holder_img.setVisibility(View.GONE);
                        }*/
                        newProfileImageAdapter = new NewProfileAdapter(OtherProfileDetailsActivity.this, otherProfileInfo.UserDetail.profileImage, new GetNewImageClick() {
                            @Override
                            public void imageClick(int position) {
                                Intent intent = new Intent(OtherProfileDetailsActivity.this, MatchGalleryActivity.class);
                                intent.putExtra("user_id", userId);
                                intent.putExtra("image_index", position);
                                intent.putExtra("otherProfileInfo", otherProfileInfo);
                                startActivity(intent);
                            }
                        });
                        profile_horizontal_recycler.setAdapter(newProfileImageAdapter);

                        viewPagerAdapter = new ViewPagerAdapter(OtherProfileDetailsActivity.this,otherProfileInfo.UserDetail.profileImage);
                        //viewPager.setAdapter(viewPagerAdapter);

                        dotscount = viewPagerAdapter.getCount();
                        dots = new ImageView[dotscount];

                        for(int i = 0; i < dotscount; i++){

                            dots[i] = new ImageView(OtherProfileDetailsActivity.this);
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(8, 0, 8, 0);


                        }

                        if(dots.length != 0)
                            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("user/getUserProfile?userId="+userId+"");

    }

    String getCommonSeperatedString(List<ProfileInterestInfo> actionObjects) {
        StringBuffer sb = new StringBuffer();
        for (ProfileInterestInfo actionObject : actionObjects){
            sb.append(actionObject.interest).append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(", "));
        return sb.toString();
    }
    private void shareProfileToFriendDialog(String aboutme,String profileImage){
        final Dialog _dialog = new Dialog(this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.share_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);


        ImageView profile_image = _dialog.findViewById(R.id.profile_image);
        tv_about_me = _dialog.findViewById(R.id.tv_about_me);
        TextView tv_share = _dialog.findViewById(R.id.tv_share);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.image_decline_button);
        scr_shot_view = _dialog.findViewById(R.id.scr_shot_view);

        if(!TextUtils.isEmpty(aboutme) && !aboutme.equals("NA")){
            tv_about_me.setText(aboutme);
        }

        Glide.with(this).load(profileImage).into(profile_image);

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionForTakepicture(scr_shot_view,tv_about_me.getText().toString().trim());
                _dialog.dismiss();
            }
        });

        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialog.dismiss();
            }
        });
        _dialog.show();
    }


    public void getPermissionForTakepicture(LinearLayout scr_shot_view, String about_string){

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            }
            else {
                screenShot(scr_shot_view,about_string);
            }
        }else {
            screenShot(scr_shot_view,about_string);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    screenShot(scr_shot_view,tv_about_me.getText().toString().trim());
                } else {
                    Toast.makeText(OtherProfileDetailsActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    screenShot(scr_shot_view,tv_about_me.getText().toString().trim());
                } else {
                    Toast.makeText(OtherProfileDetailsActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case  Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    screenShot(scr_shot_view,tv_about_me.getText().toString().trim());
                } else {
                    Toast.makeText(OtherProfileDetailsActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;

        }
    }

    /*.................................screenShot()...................................*/
    private void screenShot(LinearLayout scr_shot_view, String text) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";
            scr_shot_view.setDrawingCacheEnabled(true);
            scr_shot_view.buildDrawingCache(true);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = Bitmap.createBitmap(scr_shot_view.getDrawingCache());
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
            scr_shot_view.destroyDrawingCache();
            sharOnsocial(imageFile,text);
            //onShareClick(imageFile,text);
            //doShareLink(text,otherProfileInfo.UserDetail.profileUrl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void sharOnsocial(File imageFile, String text) {
        Uri uri;
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",imageFile);
            sharIntent.setDataAndType(uri, type);
        } else {
            uri = Uri.fromFile(imageFile);
            sharIntent.setDataAndType(uri, type);
        }

        sharIntent.setType("image/png");
        sharIntent.setType("text/plain");
        sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharIntent.putExtra(Intent.EXTRA_SUBJECT, "Apoim");
        sharIntent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
        startActivity(Intent.createChooser(sharIntent, "Share:"));

    }

    private void doShareLink(String text, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, otherProfileInfo.UserDetail.profileUrl);
        intent.setType("text/plain");

        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().contains("facebook")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }

        startActivity(intent);
    }


    public void onShareClick(File imageFile,String text) {
        Uri uri;
        Intent sharIntent = new Intent();
        sharIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            sharIntent.setDataAndType(uri, type);
        } else {
            uri = Uri.fromFile(imageFile);
            sharIntent.setDataAndType(uri, type);
        }

        sharIntent.setType("image/png");
        sharIntent.putExtra(Intent.EXTRA_STREAM, uri);


        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(sharIntent, "Share:");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sharIntent, 0);
        List<LabeledIntent> intentList = new ArrayList();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntentcom.instagram
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                sharIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm") || packageName.contains("com.instagram.android") || packageName.contains("messaging") || packageName.contains("messaging") || packageName.contains("com.whatsapp")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    intent.setType("image/png");
                    sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                    intent.putExtra(Intent.EXTRA_TEXT, "Today I'm grateful for...#gratitudemorning");
                } else if (packageName.contains("facebook")) {

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_STREAM, otherProfileInfo.UserDetail.profileUrl);
                    intent.putExtra(Intent.EXTRA_TEXT,otherProfileInfo.UserDetail.profileUrl);

                } else if (packageName.contains("mms")) {
                    sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Apoim");
                    sharIntent.setType("message/rfc822");
                } else if (packageName.contains("instagram")) {

                    sharIntent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);

                } else if (packageName.contains("messaging")) {
                    sharIntent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
                } else if (packageName.contains("com.whatsapp")) {
                    sharIntent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
                } else {
                    sharIntent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+otherProfileInfo.UserDetail.profileUrl);
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }


    private void setData(GetOtherProfileInfo.UserDetailBean userDetail){


        if(userDetail.appointmentType.equals("1")){
            tv_appoim_type.setText(R.string.paid) ;
        }else if(userDetail.appointmentType.equals("2")){
            tv_appoim_type.setText(R.string.free) ;
        }else {
            tv_appoim_type.setText(R.string.not_available);
        }


        if(userDetail.eventType.equals("1")){
            tv_event_type.setText(R.string.paid) ;
        }else if(userDetail.eventType.equals("2")){
            tv_event_type.setText(R.string.free) ;
        }else {
            tv_event_type.setText(R.string.not_available);
        }

        if(userDetail.isBusinessAdded.equals("1")){
            ly_business.setVisibility(View.VISIBLE);
        }else {
            ly_business.setVisibility(View.GONE);
        }

        if(userDetail.isVerifiedId.equals("1")){
            idWithHandSelcted();
        }
        if(userDetail.otpVerified.equals("1")){
            mobileVerificationSelcted();
        }


  if(!userDetail.fullName.equals("")){
            tv_fullName.setText(userDetail.fullName) ;
        }else   tv_fullName.setText(R.string.not_available);

        if(!userDetail.age.equals("")){
            tv_age.setText(", "+ userDetail.age) ;
        }else   tv_age.setText(R.string.not_available);

        if(!userDetail.totalLikes.equals("")){
            tv_like_count.setText(userDetail.totalLikes) ;
        }else   tv_like_count.setText(R.string.not_available);

        if(!userDetail.address.equals("")){
            tv_address.setText(userDetail.address);
        }else   tv_address.setText(R.string.not_available);

        if(!userDetail.about.equals("")){
            tv_about.setText(userDetail.about);
        }else   tv_about.setText(R.string.not_available);

        if(!userDetail.work.equals("")){
            tv_work.setText(userDetail.work);
        }else   tv_work.setText(R.string.not_available);

        if(!userDetail.education.equals("")){
            tv_education.setText(userDetail.education);
        }else   tv_education.setText(R.string.not_available);

        if(!userDetail.height.equals("")){
            tv_height.setText(userDetail.height);
        }else   tv_height.setText(R.string.not_available);

        if(!userDetail.weight.equals("")){
            tv_weight.setText(userDetail.weight);
        }else   tv_weight.setText(R.string.not_available);

        if(userDetail.relationship.equals("1")){
            tv_marrige_status.setText("Single");
        }else if(userDetail.relationship.equals("2")){
            tv_marrige_status.setText( "Married");
        }else if(userDetail.relationship.equals("3")){
            tv_marrige_status.setText("Divorced");
        }else if(userDetail.relationship.equals("4")){
            tv_marrige_status.setText("Widowed");
        }else {
            tv_marrige_status.setText(R.string.not_available);
        }

        

        if(userDetail.profileImage.size() > 0){

            Glide.with(getApplicationContext())
                    .load(userDetail.profileImage.get(0).image)
                    .apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder))
                    .into(iv_profile_image);
        }else {
            tv_no_user_image_found.setVisibility(View.VISIBLE);
            profile_horizontal_recycler.setVisibility(View.GONE);
            ly_photo_view.setVisibility(View.GONE);
        }

        if(!userDetail.language.equals("")){
            if(userDetail.language.contains(",")){
                userDetail.language = userDetail.language.replaceAll("(?!\\s)\\W", "$0 ");
                tv_languge.setText(userDetail.language);
            }else {
                tv_languge.setText(userDetail.language);
            }

        }else {
            tv_languge.setText(R.string.not_available);
        }

        if(userDetail.isFavorite.equals("1")){
            iv_fevorate.setImageResource(R.drawable.ico_favourite_active);
        }

        if(userDetail.isLike.equals("1")){
            iv_like.setImageResource(R.drawable.ico_pr_unlike);
        }

        if(userDetail.isRequest.equals("0") && userDetail.isFriend.equals("0")){
            iv_add_friend.setVisibility(View.VISIBLE);
            iv_add_friend.setEnabled(true);

            tv_remove_friend.setVisibility(View.GONE);
            iv_remove_friend_request.setVisibility(View.GONE);
            tv_accept_friend.setVisibility(View.GONE);
            ly_request_accept_reject.setVisibility(View.GONE);
            tv_friend_status.setText("Add");
        }
        else if(userDetail.isRequest.equals("1")){
            tv_remove_friend.setVisibility(View.VISIBLE);
            iv_remove_friend_request.setVisibility(View.VISIBLE);
            tv_accept_friend.setVisibility(View.GONE);
            ly_request_accept_reject.setVisibility(View.GONE);
            iv_add_friend.setVisibility(View.GONE);
            //iv_add_friend.setEnabled(false);
            tv_friend_status.setText("Remove");
        }

        /*.......................*/
       else if(userDetail.isRequest.equals("2") && userDetail.isFriend.equals("0")){
            iv_remove_friend_request.setVisibility(View.GONE);
            ly_request_accept_reject.setVisibility(View.VISIBLE);

            iv_add_friend.setVisibility(View.VISIBLE);
           // iv_add_friend.setEnabled(false);

            iv_add_friend.setImageResource(R.drawable.respond);

            tv_friend_status.setText("Respond");
        }
        /*..........................*/

        else if(userDetail.isRequest.equals("2")){
            iv_remove_friend_request.setVisibility(View.VISIBLE);
            tv_accept_friend.setVisibility(View.VISIBLE);
            ly_request_accept_reject.setVisibility(View.VISIBLE);
            iv_add_friend.setVisibility(View.GONE);
            //iv_add_friend.setEnabled(false);
        }
        else if(userDetail.isFriend.equals("1")){
            iv_remove_friend_request.setVisibility(View.GONE);
            tv_accept_friend.setVisibility(View.GONE);
            ly_request_accept_reject.setVisibility(View.GONE);
            iv_add_friend.setEnabled(false);
            tv_friend_status.setText("Friends");
            iv_add_friend.setVisibility(View.VISIBLE);
            iv_add_friend.setImageResource(R.drawable.ico_accept_friend);
            iv_add_friend.setEnabled(false);
        }


        String myUid = mSession.getUser().userDetail.userId;

        if(userId.equals(myUid)){
            tv_accept_friend.setVisibility(View.GONE);
            ly_request_accept_reject.setVisibility(View.GONE);
            iv_remove_friend_request.setVisibility(View.GONE);
             iv_add_friend.setVisibility(View.GONE);
            //iv_add_friend.setEnabled(false);
            iv_fevorate.setVisibility(View.GONE);
            like_chat_direction_view.setVisibility(View.GONE);
        }

    }

    private void add_remove_Favourate(String favUserId,String isFavorite){
        //loading_view.setVisibility(View.VISIBLE);

        Map<String,String> param = new HashMap<>();
        param.put("favUserId",favUserId);
        param.put("isFavorite",isFavorite);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("success")){

                        if( otherProfileInfo.UserDetail.isFavorite.equals("1")){
                            otherProfileInfo.UserDetail.isFavorite  = "1";
                            iv_fevorate.setImageResource(R.drawable.ico_favourite_active);

                            // Utils.openAlertDialog(OtherProfileDetailsActivity.this,otherProfileInfo.UserDetail.fullName+" added to your favorite");
                        }else {
                            otherProfileInfo.UserDetail.isFavorite  = "0";
                            iv_fevorate.setImageResource(R.drawable.un_fav);
                            //Utils.openAlertDialog(OtherProfileDetailsActivity.this,otherProfileInfo.UserDetail.fullName+" remove from your favorite");
                        }

                    }else {

                        if( otherProfileInfo.UserDetail.isFavorite.equals("0")){
                            iv_fevorate.setImageResource(R.drawable.un_fav);
                            otherProfileInfo.UserDetail.isFavorite  = "0";
                        }else {
                            otherProfileInfo.UserDetail.isFavorite  = "1";
                            iv_fevorate.setImageResource(R.drawable.ico_favourite_active);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("user/myFavorite",param);
    }

    private void add_remove_accept_Request(String requestForId, String type, String status, final String msg, final String noPopup){
        loading_view.setVisibility(View.VISIBLE);
        Map<String,String> param = new HashMap<>();
        param.put("requestFor",requestForId);
        param.put("type",type);
        param.put("status",status);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("success")){
                        otherProfileInfo.UserDetail.isRequest = "1";
                        if(noPopup.equals("1")){
                            Utils.openAlertDialog(OtherProfileDetailsActivity.this,msg);
                        }
                        getProfileDetails();

                        iv_add_friend.setEnabled(true);
                        tv_accept_friend.setEnabled(true);
                        tv_remove_friend.setEnabled(true);
                        iv_remove_friend_request.setEnabled(true);

                    }else {
                        Utils.openAlertDialog(OtherProfileDetailsActivity.this,message);
                        iv_add_friend.setEnabled(true);
                        tv_accept_friend.setEnabled(true);
                        tv_remove_friend.setEnabled(true);
                        iv_remove_friend_request.setEnabled(true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                    iv_add_friend.setEnabled(true);
                    tv_accept_friend.setEnabled(true);
                    tv_remove_friend.setEnabled(true);
                    iv_remove_friend_request.setEnabled(true);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                loading_view.setVisibility(View.GONE);
                iv_add_friend.setEnabled(true);
                tv_accept_friend.setEnabled(true);
                tv_remove_friend.setEnabled(true);
                iv_remove_friend_request.setEnabled(true);
            }
        });

        service.callSimpleVolley("user/friendRequest",param);
    }

    private void like_unlike_api(String likeUserId,String isLike){
        //loading_view.setVisibility(View.VISIBLE);
        iv_like.setEnabled(false);
        Map<String,String> param = new HashMap<>();
        param.put("likeUserId",likeUserId);
        param.put("isLike",isLike);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("success")){

                        if(otherProfileInfo.UserDetail.isLike.equals("1")){
                            otherProfileInfo.UserDetail.isLike  = "1";
                            iv_like.setImageResource(R.drawable.ico_pr_unlike);
                            // Utils.openAlertDialog(OtherProfileDetailsActivity.this,otherProfileInfo.UserDetail.fullName+" added to your favorite");

                            if(!TextUtils.isEmpty(otherProfileInfo.UserDetail.totalLikes)){
                                tv_like_count.setText((Integer.parseInt(otherProfileInfo.UserDetail.totalLikes) + 1)+"");
                                otherProfileInfo.UserDetail.totalLikes = String.valueOf((Integer.parseInt(otherProfileInfo.UserDetail.totalLikes) + 1));
                            }
                        }else if(otherProfileInfo.UserDetail.isLike.equals("0")){
                            otherProfileInfo.UserDetail.isLike  = "0";
                            iv_like.setImageResource(R.drawable.ico_pr_like);
                            //Utils.openAlertDialog(OtherProfileDetailsActivity.this,otherProfileInfo.UserDetail.fullName+" remove from your favorite");

                            if(!TextUtils.isEmpty(otherProfileInfo.UserDetail.totalLikes)){
                                tv_like_count.setText((Integer.parseInt(otherProfileInfo.UserDetail.totalLikes) - 1)+"");
                                otherProfileInfo.UserDetail.totalLikes = String.valueOf((Integer.parseInt(otherProfileInfo.UserDetail.totalLikes) - 1));

                            }
                        }

                        iv_like.setEnabled(true);

                    }else {
                        iv_like.setEnabled(true);
                     /*   if( otherProfileInfo.UserDetail.isLike.equals("0")){
                            iv_like.setImageResource(R.drawable.ico_pr_like);
                            otherProfileInfo.UserDetail.isLike  = "0";
                        }else {
                            otherProfileInfo.UserDetail.isLike  = "1";
                            iv_like.setImageResource(R.drawable.ico_pr_unlike);
                        }
*/

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    iv_like.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                iv_like.setEnabled(true);
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("user/myLike",param);
    }

}


