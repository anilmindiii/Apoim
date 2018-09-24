package com.apoim.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ablanco.zoomy.Zoomy;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.ImageGalleryActivity;
import com.apoim.activity.MatchGalleryActivity;
import com.apoim.activity.MyFevoriteActivity;
import com.apoim.activity.MyFriendsActivity;
import com.apoim.activity.NotificationActivity;
import com.apoim.activity.OtherProfileActivity;
import com.apoim.activity.ProfileActivity;
import com.apoim.activity.SelectPaymentTypeActivity;
import com.apoim.activity.SettingsActivity;
import com.apoim.activity.SignInActivity;
import com.apoim.activity.business.BusinessDetailsActivity;
import com.apoim.adapter.ShowInterestAdapter;
import com.apoim.adapter.newProfile.NewProfileAdapter;
import com.apoim.app.Apoim;
import com.apoim.groupchatwebrtc.db.QbUsersDbManager;
import com.apoim.groupchatwebrtc.services.CallService;
import com.apoim.groupchatwebrtc.util.QBResRequestExecutor;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.groupchatwebrtc.utils.UsersUtils;
import com.apoim.helper.Constant;
import com.apoim.listener.GetInterestValueListener;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.apoim.util.VerticalViewPager;
import com.apoim.util.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.apoim.app.Apoim.TAG;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * Created by mindiii on 10/3/18.
 */

public class MyProfileFragment extends Fragment implements View.OnClickListener {
    private Context mContext;

    /* private LinearLayout ly_logout;
    private LinearLayout ly_details;
    private LinearLayout ly_settings;
    private LinearLayout ly_notifications;
    private LinearLayout ly_subscription;
    private ImageView iv_edit_profile;*/

    private VerticalViewPager viewPager;
    private LinearLayout sliderDotspanel,ly_uper_view;
    private int dotscount;
    private ImageView[] dots;
    private ViewPagerAdapter viewPagerAdapter;

    private ImageView iv_mobile_veri, iv_id_hand_veri, iv_face_detection_veri;
    private ImageView iv_mobile_veri_active, iv_id_hand_veri_active, iv_face_detection_veri_active;
    private TextView tv_mobile_veri, tv_id_hand_veri, tv_face_detection_veri;
    private RelativeLayout ly_mobile_veri, ly_id_hand_veri, ly_face_detection_veri;

    private LinearLayout ly_friends;

    private Session session;
    private GetOtherProfileInfo otherProfileInfo;
    private InsLoadingView loading_view;
    private ArrayList<ProfileInterestInfo> interestArrayList;
    private ImageView iv_profile;
    // private TextView tv_email;
    private TextView tv_friend_count;
    private TextView tv_visits_count;
    private TextView tv_friendstxt;
    private TextView tv_likestxt;
    private TextView tv_visittxt;
    private TextView tv_no_user_image_found;
    private boolean isResponceAppear = false;
    private LinearLayout ly_my_fevorite, ly_photo_view, ly_business;


    NestedScrollView bottom_sheet;

    String userId;

    private TextView tv_basic_info, tv_more_info;
    private LinearLayout ly_basic_info, ly_more_info;
    private TextView tv_fullName, tv_address, tv_about, tv_work, tv_education,
            tv_height, tv_weight, tv_marrige_status, tv_languge, tv_like_count, tv_age,tv_no_interest_found,
            profile_action_bar;
    private RecyclerView profile_horizontal_recycler;
   // private RecyclerView user_selected_interest_list_view;
    private ShowInterestAdapter showInterestAdapter;
    private NewProfileAdapter newProfileImageAdapter;
    TextView tv_appoim_type, tv_event_type,tv_interest;
    private ImageView iv_settings, iv_edit_profile;
    String typeNotification = "";
    RelativeLayout pager_main_layout;

    public static MyProfileFragment newInstance(String type) {

        Bundle args = new Bundle();
        MyProfileFragment fragment = new MyProfileFragment();
        fragment.setArguments(args);
        args.putString("typeNotification", type);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test, container, false);

        if (getArguments() != null) {
            typeNotification = getArguments().get("typeNotification").toString();
        }

       /* ly_logout = view.findViewById(R.id.ly_logout);
        ly_details = view.findViewById(R.id.ly_details);

        ly_settings = view.findViewById(R.id.ly_settings);
        ly_notifications = view.findViewById(R.id.ly_notifications);
        ly_subscription = view.findViewById(R.id.ly_subscription);*/

        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        ly_my_fevorite = view.findViewById(R.id.ly_my_fevorite);
        iv_settings = view.findViewById(R.id.iv_settings);
        iv_edit_profile = view.findViewById(R.id.iv_edit_profile);

        tv_no_user_image_found = view.findViewById(R.id.tv_no_user_image_found);
        ly_friends = view.findViewById(R.id.ly_friends);
        tv_fullName = view.findViewById(R.id.tv_fullName);
        tv_address = view.findViewById(R.id.tv_address);
        tv_about = view.findViewById(R.id.tv_about);
        tv_work = view.findViewById(R.id.tv_work);
        tv_education = view.findViewById(R.id.tv_education);
        tv_height = view.findViewById(R.id.tv_height);
        tv_weight = view.findViewById(R.id.tv_weight);
        tv_marrige_status = view.findViewById(R.id.tv_marrige_status);
        tv_languge = view.findViewById(R.id.tv_languge);
        tv_age = view.findViewById(R.id.tv_age);
        tv_interest = view.findViewById(R.id.tv_interest);
        tv_no_interest_found = view.findViewById(R.id.tv_no_interest_found);


        profile_action_bar = view.findViewById(R.id.profile_action_bar);
    //    user_selected_interest_list_view = view.findViewById(R.id.user_selected_interest_list_view);
        ly_business = view.findViewById(R.id.ly_business);

        iv_mobile_veri = view.findViewById(R.id.iv_mobile_veri);
        iv_id_hand_veri = view.findViewById(R.id.iv_id_hand_veri);
        iv_face_detection_veri = view.findViewById(R.id.iv_face_detection_veri);

        iv_mobile_veri_active = view.findViewById(R.id.iv_mobile_veri_active);
        iv_id_hand_veri_active = view.findViewById(R.id.iv_id_hand_veri_active);
        iv_face_detection_veri_active = view.findViewById(R.id.iv_face_detection_veri_active);

        tv_mobile_veri = view.findViewById(R.id.tv_mobile_veri);
        tv_id_hand_veri = view.findViewById(R.id.tv_id_hand_veri);
        tv_face_detection_veri = view.findViewById(R.id.tv_face_detection_veri);

        ly_mobile_veri = view.findViewById(R.id.ly_mobile_veri);
        ly_id_hand_veri = view.findViewById(R.id.ly_id_hand_veri);
        ly_face_detection_veri = view.findViewById(R.id.ly_face_detection_veri);
        pager_main_layout = view.findViewById(R.id.pager_main_layout);


       // int numberOfColumns = 2;
        //user_selected_interest_list_view.setLayoutManager(new GridLayoutManager(mContext, numberOfColumns));
        profile_horizontal_recycler = view.findViewById(R.id.profile_horizontal_recycler);
        ly_photo_view = view.findViewById(R.id.ly_photo_view);

        tv_event_type = view.findViewById(R.id.tv_event_type);
        tv_appoim_type = view.findViewById(R.id.tv_appoim_type);

        // iv_edit_profile = view.findViewById(R.id.iv_edit_profile);
        loading_view = view.findViewById(R.id.loading_view);
        iv_profile = view.findViewById(R.id.iv_profile);
        //tv_email = view.findViewById(R.id.tv_email);
        tv_friend_count = view.findViewById(R.id.tv_friend_count);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        tv_visits_count = view.findViewById(R.id.tv_visits_count);

        tv_friendstxt = view.findViewById(R.id.tv_friendstxt);
        tv_likestxt = view.findViewById(R.id.tv_likestxt);
        tv_visittxt = view.findViewById(R.id.tv_visittxt);

        tv_basic_info = view.findViewById(R.id.tv_basic_info);
        tv_more_info = view.findViewById(R.id.tv_more_info);
        ly_basic_info = view.findViewById(R.id.ly_basic_info);
        ly_more_info = view.findViewById(R.id.ly_more_info);

        otherProfileInfo = new GetOtherProfileInfo();
        interestArrayList = new ArrayList<>();
        session = new Session(mContext, getActivity());

        userId = session.getUser().userDetail.userId;
        ly_friends.setOnClickListener(this);


        viewPager = view.findViewById(R.id.viewPager);
        sliderDotspanel =  view.findViewById(R.id.SliderDots);
        //getProfileDetails();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        showInterestAdapter = new ShowInterestAdapter(Constant.OtherProfile, mContext, interestArrayList, new GetInterestValueListener() {
            @Override
            public void getInterestValue(String value) {

            }
        });
       // user_selected_interest_list_view.setAdapter(showInterestAdapter);


        ly_my_fevorite.setOnClickListener(this);
        iv_profile.setOnClickListener(this);

        /*ly_logout.setOnClickListener(this);
        ly_details.setOnClickListener(this);
        iv_edit_profile.setOnClickListener(this);
        ly_settings.setOnClickListener(this);
        ly_notifications.setOnClickListener(this);
        ly_subscription.setOnClickListener(this);*/

        //getProfileDetails();

        ly_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BusinessDetailsActivity.class);
                startActivity(intent);

            }
        });

        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);*/

                if (otherProfileInfo.UserDetail.fullName != null) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("otherProfileInfo", otherProfileInfo);
                    startActivityForResult(intent, 178);
                }
            }
        });

        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherProfileInfo.status != null)
                    startActivity(new Intent(mContext, SettingsActivity.class)
                            .putExtra("otherProfileInfo", otherProfileInfo));

            }
        });

        tv_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_basic_info.setVisibility(View.GONE);
                ly_more_info.setVisibility(View.VISIBLE);
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

       /* image_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               *//* if (otherProfileInfo.UserDetail.profileImage.size() != 0) {
                    Intent intent = new Intent(mContext, MatchGalleryActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("image_index", 0);
                    intent.putExtra("otherProfileInfo", otherProfileInfo);
                    startActivity(intent);
                }*//*
            }
        });*/


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void mobileVerificationSelcted() {
        iv_mobile_veri_active.setVisibility(View.VISIBLE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.GONE);
        iv_mobile_veri.setImageResource(R.drawable.active_sms);
        tv_mobile_veri.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_green_border);
    }

    private void idWithHandSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.VISIBLE);
        iv_face_detection_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri.setImageResource(R.drawable.active_id);
        tv_id_hand_veri.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_green_border);
    }

    private void faceDetectionSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.VISIBLE);
        iv_face_detection_veri.setImageResource(R.drawable.active_face);
        tv_face_detection_veri.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_green_border);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ly_my_fevorite: {
                //ly_my_fevorite.setEnabled(false);
                intent = new Intent(mContext, MyFevoriteActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.iv_profile: {
                //ly_my_fevorite.setEnabled(false);
                if (otherProfileInfo.UserDetail.profileImage.size() != 0) {
                    intent = new Intent(mContext, MatchGalleryActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("image_index", 0);
                    intent.putExtra("otherProfileInfo", otherProfileInfo);
                    startActivity(intent);
                }
                break;
            }

/*            case R.id.ly_logout:{
                logOutQuickBlock();
                //logout();
                break;
            }
            case R.id.ly_details:{
                if(isResponceAppear){
                    intent = new Intent(mContext, OtherProfileActivity.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }

                break;
            }


            case R.id.ly_notifications:{
               // ly_notifications.setEnabled(false);
                    intent = new Intent(mContext, NotificationActivity.class);
                    startActivity(intent);
                break;
            }

            case R.id.ly_subscription:{
                if(isResponceAppear){
                    intent = new Intent(mContext, SelectPaymentTypeActivity.class);
                    intent.putExtra("mapPayment",otherProfileInfo.UserDetail.mapPayment);
                    intent.putExtra("showTopPayment",otherProfileInfo.UserDetail.showTopPayment);
                    startActivity(intent);}
                break;
            }
            */
            case R.id.ly_friends: {
                ly_friends.setEnabled(false);
                intent = new Intent(mContext, MyFriendsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
      /*  ly_my_fevorite.setEnabled(true);
        ly_notifications.setEnabled(true);
        ly_settings.setEnabled(true);*/
        ly_friends.setEnabled(true);
        getProfileDetails();
    }

    private void getProfileDetails() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(mContext, TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        isResponceAppear = true;
                        interestArrayList.clear();
                        Gson gson = new Gson();
                        otherProfileInfo = gson.fromJson(response, GetOtherProfileInfo.class);

                        setData(otherProfileInfo.UserDetail);

                        SignInInfo info = session.getUser();
                        info.userDetail.mapPayment = otherProfileInfo.UserDetail.mapPayment;
                        info.userDetail.showTopPayment = otherProfileInfo.UserDetail.showTopPayment;
                        info.userDetail.totalFriends = otherProfileInfo.UserDetail.totalFriends;

                        info.userDetail.bizSubscriptionId = otherProfileInfo.UserDetail.bizSubscriptionId;
                        info.userDetail.bizSubscriptionStatus = otherProfileInfo.UserDetail.bizSubscriptionStatus;
                        session.createSession(info);

                        if (!otherProfileInfo.UserDetail.interest.equals("")) {
                            List<String> interestList = Arrays.asList(otherProfileInfo.UserDetail.interest.split(","));
                            for (int i = 0; i < interestList.size(); i++) {
                                ProfileInterestInfo interestInfo = new ProfileInterestInfo();
                                interestInfo.interest = interestList.get(i);
                                interestArrayList.add(interestInfo);
                            }
                            showInterestAdapter.notifyDataSetChanged();

                            String allInterest = getCommonSeperatedString(interestArrayList);
                            tv_interest.setText(allInterest);

                            if(!allInterest.equals("")){
                                tv_interest.setVisibility(View.VISIBLE);
                                tv_no_interest_found.setVisibility(View.GONE);
                            }else {
                                tv_interest.setVisibility(View.GONE);
                                tv_no_interest_found.setVisibility(View.VISIBLE);
                            }
                        }

                        if (otherProfileInfo.UserDetail.profileImage.size() == 0) {
                            tv_no_user_image_found.setVisibility(View.VISIBLE);
                            profile_horizontal_recycler.setVisibility(View.GONE);
                        }else {
                            tv_no_user_image_found.setVisibility(View.GONE);
                            profile_horizontal_recycler.setVisibility(View.VISIBLE);
                        }

                        newProfileImageAdapter = new NewProfileAdapter(mContext, otherProfileInfo.UserDetail.profileImage, new GetNewImageClick() {
                            @Override
                            public void imageClick(int position) {
                                Intent intent = new Intent(mContext, MatchGalleryActivity.class);
                                intent.putExtra("user_id", userId);
                                intent.putExtra("image_index", position);
                                intent.putExtra("otherProfileInfo", otherProfileInfo);
                                startActivity(intent);
                            }
                        });
                        profile_horizontal_recycler.setAdapter(newProfileImageAdapter);

                        if (typeNotification.equals("id_verification")) {
                            startActivity(new Intent(mContext, SettingsActivity.class)
                                    .putExtra("otherProfileInfo", otherProfileInfo)
                                    .putExtra("typeNotification", "id_verification")
                            );

                            typeNotification = "";
                        }

                    } else {
                        isResponceAppear = false;
                        Utils.openAlertDialog(mContext, message);
                    }

 /*..........................................<<<<< view pager >>>>............................................*/
                    viewPagerAdapter = new ViewPagerAdapter(mContext,otherProfileInfo.UserDetail.profileImage);
                    viewPager.setAdapter(viewPagerAdapter);

                    dotscount = viewPagerAdapter.getCount();
                    dots = new ImageView[dotscount];
                    sliderDotspanel.removeAllViews();

                    for(int i = 0; i < dotscount; i++){
                        dots[i] = new ImageView(mContext);
                        dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.non_active_dot));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0, 8, 0);
                        sliderDotspanel.addView(dots[i], params);

                    }

                    if(dots.length != 0)
                        dots[0].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.active_dot));


                } catch (JSONException e) {
                    e.printStackTrace();
                    isResponceAppear = false;
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                isResponceAppear = false;
            }
        });
        service.callGetSimpleVolley("user/getUserProfile?userId=" + userId + "");

    }

    String getCommonSeperatedString(List<ProfileInterestInfo> actionObjects) {
        StringBuffer sb = new StringBuffer();
        for (ProfileInterestInfo actionObject : actionObjects){
            sb.append(actionObject.interest).append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(", "));
        return sb.toString();
    }

    private void startCountAnimation(final TextView textView, int friend_count) {
        ValueAnimator animator = ValueAnimator.ofInt(0, friend_count);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    void setData(GetOtherProfileInfo.UserDetailBean userDetail) {

        if (userDetail.appointmentType.equals("1")) {
            tv_appoim_type.setText(R.string.paid);
        } else if (userDetail.appointmentType.equals("2")) {
            tv_appoim_type.setText(R.string.free);
        } else {
            tv_appoim_type.setText(R.string.not_available);
        }


        if(userDetail.isBusinessAdded.equals("1")){
            ly_business.setVisibility(View.VISIBLE);
        }else {
            ly_business.setVisibility(View.GONE);
        }



        if (userDetail.eventType.equals("1")) {
            tv_event_type.setText(R.string.paid);
        } else if (userDetail.eventType.equals("2")) {
            tv_event_type.setText(R.string.free);
        } else {
            tv_event_type.setText(R.string.not_available);
        }

        //tv_email.setText(userDetail.email);
        if (userDetail.profileImage.size() != 0) {
            ly_photo_view.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ico_user_placeholder);

            Glide.with(mContext).load(userDetail.profileImage.get(0).image).apply(options).into(iv_profile);
            //Picasso.with(mContext).load(userDetail.images.get(0).image).placeholder(R.drawable.ico_user_placeholder).into(iv_profile);
        } else {
            ly_photo_view.setVisibility(View.GONE);
            iv_profile.setImageResource(R.drawable.ico_user_placeholder);
        }

        if (!userDetail.visit.equals(""))
            if (Integer.parseInt(userDetail.visit) > 1) {
                tv_visittxt.setText("Visits");
            }

        if (!userDetail.totalLikes.equals(""))
            if (Integer.parseInt(userDetail.totalLikes) > 1) {
                tv_likestxt.setText("Likes");
            }

        if (!userDetail.totalFriends.equals(""))
            if (Integer.parseInt(userDetail.totalFriends) > 1) {
                tv_friendstxt.setText("Friends");
            }

        //tv_visits_count.setText(userDetail.visit);
        //tv_like_count.setText(userDetail.totalLikes);
        //tv_friend_count.setText(userDetail.totalFriends);
        int friend_count = Integer.parseInt(userDetail.totalFriends);
        int like_count = Integer.parseInt(userDetail.totalLikes);
        int visits_count = Integer.parseInt(userDetail.visit);

        startCountAnimation(tv_friend_count, friend_count);
        startCountAnimation(tv_like_count, like_count);
        startCountAnimation(tv_visits_count, visits_count);


        if (!userDetail.fullName.equals("")) {
            tv_fullName.setText(userDetail.fullName);
        } else tv_fullName.setText(R.string.not_available);


        if (!userDetail.age.equals("")) {
            tv_age.setText(", " + userDetail.age);
        } else tv_age.setText(R.string.not_available);

        if (!userDetail.totalLikes.equals("")) {
            tv_like_count.setText(userDetail.totalLikes);
        } else tv_like_count.setText(R.string.not_available);

        if (!userDetail.address.equals("")) {
            tv_address.setText(userDetail.address);
        } else tv_address.setText(R.string.not_available);

        if (!userDetail.about.equals("")) {
            tv_about.setText(userDetail.about);
        } else tv_about.setText(R.string.not_available);

        if (!userDetail.work.equals("")) {
            tv_work.setText(userDetail.work);
        } else tv_work.setText(R.string.not_available);

        if (!userDetail.education.equals("")) {
            tv_education.setText(userDetail.education);
        } else tv_education.setText(R.string.not_available);

        if (!userDetail.height.equals("")) {
            tv_height.setText(userDetail.height);
        } else tv_height.setText(R.string.not_available);

        if (!userDetail.weight.equals("")) {
            tv_weight.setText(userDetail.weight);
        } else tv_weight.setText(R.string.not_available);

        if (userDetail.relationship.equals("1")) {
            tv_marrige_status.setText("Single");
        } else if (userDetail.relationship.equals("2")) {
            tv_marrige_status.setText("Married");
        } else if (userDetail.relationship.equals("3")) {
            tv_marrige_status.setText("Divorced");
        } else if (userDetail.relationship.equals("4")) {
            tv_marrige_status.setText("Widowed");
        } else {
            tv_marrige_status.setText(R.string.not_available);
        }

        if (userDetail.isVerifiedId.equals("1")) {
            idWithHandSelcted();
        }
        if (userDetail.otpVerified.equals("1")) {
            mobileVerificationSelcted();
        }

        if (!userDetail.language.equals("")) {
            if (userDetail.language.contains(",")) {
                userDetail.language = userDetail.language.replaceAll("(?!\\s)\\W", "$0 ");
                tv_languge.setText(userDetail.language);
            } else {
                tv_languge.setText(userDetail.language);
            }

        } else {
            tv_languge.setText(R.string.not_available);
        }
    }



   /* private void logout() {
        ly_logout.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(mContext, TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                ly_logout.setEnabled(true);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    Log.e("FRIEND LIST RESPONSE", response.toString());
                    if (status.equals("success")) {
                        String f_id = session.getUser().userDetail.userId;
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("dkjshfs");
                        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(f_id).child("firebaseToken").setValue("");
                        Utils.goToOnlineStatus(mContext, Constant.offline);
                        session.logout();
                    }
                    else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                ly_logout.setEnabled(true);
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("user/logout");
    }*/
}
