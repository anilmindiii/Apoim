package com.apoim.activity.event;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.CompanionListActivity;
import com.apoim.activity.MatchGalleryActivity;
import com.apoim.activity.chat.GroupChatHistortActivity;
import com.apoim.activity.payment_subscription.SubscriptionPayActivity;
import com.apoim.adapter.apoinment.EventDetailsMemberAdapter;
import com.apoim.adapter.apoinment.ShareEventJoinAdapter;
import com.apoim.adapter.newProfile.NewProfileAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.EventFilterData;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.apoim.activity.event.CreateEventActivity.RattingIds;
import static com.apoim.activity.event.CreateEventActivity.friendsIds;
import static com.bumptech.glide.util.Preconditions.checkArgument;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private EventDetailsMemberAdapter invitedMemberAdapter, joinedMemberAdapter, companionMemAdapter;
    private RecyclerView rcv_invite_member, rcv_joined_member, rcv_companion, rcv_chat_member;
    private ImageView iv_back, iv_businessImg;
    private String from;
    private TextView tv_invite_member_txt;
    private TextView tv_address;
    private TextView tv_day;
    private TextView tv_start_date_time;
    private TextView tv_max_user;
    private TextView tv_paid_amount;
    private TextView tv_share_event, tv_comp_name;
    private TextView tv_pay;
    private TextView tv_end_day;
    private TextView tv_end_date_time;
    private TextView tv_gender;
    private TextView tv_event_name;
    private TextView tv_event_creater_name;
    private TextView tv_type_of_user;
    private TextView tv_privacy;
    private TextView tv_joined_count, tv_chat_count;
    private TextView tv_invite_count, tv_business_name;
    private LinearLayout ly_accept, ly_reject;
    private TextView tv_joined_member_txt;
    private TextView tv_payment_status;
    private TextView tv_time_To, tv_time_From;
    private CardView cv_accept_companion;
    private CardView cv_reject_companion;
    private TextView tv_comp_count;
    private ImageView iv_profile, map_image, iv_event_img, compainion_img,iv_social_share;
    private LinearLayout  ly_all_bottom_view, ly_companion_view;
    private RelativeLayout ly_edit_delete,ly_companion, ly_accept_reject, ly_join_accept_reject;
    private InsLoadingView loading_view;
    private String eventId = "";
    private String id = "";
    private ArrayList<String> invitedMemberList;
    private ArrayList<String> joinedMemberList;
    private ArrayList<String> companionMemberList;
    private RelativeLayout ly_invited_member, ly_joined, ly_join_member;
    private TextView tv_start_th, tv_end_th;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private String userGenderType = "";
    private ShareEventJoinAdapter adapter;
    private ImageView iv_chat_group_img;
    private String userId = "", memberId = "", currencyCode = "", eventAmount = "", eventPrivacy = "", currentDate = "", eventMemId = "", eventOrgnizarId = "", ownerType;
    private LinearLayout ly_edit_event,ly_delete_myevent;
    private EventDetailsInfo detailsInfo;
    private LinearLayout cv_companion_view;
    private boolean isExpaireDate;
    private boolean isLimitOver = false;
    private boolean isEventPaymentDone = false;
    private NewProfileAdapter eventImageAdapter;
    private RecyclerView rcv_event_images;
    private RelativeLayout bottom_sheet, ly_business_way, ly_address_image, ly_comp_count, ly_joined_count, ly_invite_count, ly_chat_count;
    private Session session;
    private LinearLayout ly_main_invited_mem, ly_shared_event_btn, ly_shared_event_call_btn,ly_chat_view;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_details_layout);
        init();

        friendList = new ArrayList<>();
        invitedMemberList = new ArrayList<>();
        joinedMemberList = new ArrayList<>();
        companionMemberList = new ArrayList<>();
        session = new Session(this);

        invitedMemberAdapter = new EventDetailsMemberAdapter(0, this, invitedMemberList);
        joinedMemberAdapter = new EventDetailsMemberAdapter(0, this, joinedMemberList);
        companionMemAdapter = new EventDetailsMemberAdapter(1, this, companionMemberList);

        rcv_invite_member.setAdapter(invitedMemberAdapter);
        rcv_joined_member.setAdapter(joinedMemberAdapter);
        rcv_chat_member.setAdapter(joinedMemberAdapter);
        rcv_companion.setAdapter(companionMemAdapter);

        if (getIntent().getStringExtra("from") != null) {
            from = getIntent().getStringExtra("from");
            eventId = getIntent().getStringExtra("eventId");

            id = getIntent().getStringExtra("id");
            ownerType = getIntent().getStringExtra("ownerType");

            if (from.equals("myEvent")) {
                tv_pay.setVisibility(View.GONE);
                tv_share_event.setVisibility(View.GONE);
                ly_join_accept_reject.setVisibility(View.GONE);

                ly_edit_delete.setVisibility(View.VISIBLE);
                tv_invite_member_txt.setVisibility(View.VISIBLE);
                rcv_invite_member.setVisibility(View.VISIBLE);
                ly_invite_count.setVisibility(View.VISIBLE);
                joined_view_visible();

            } else if (from.equals("eventRequest")) {
                ly_main_invited_mem.setVisibility(View.GONE);
                ly_invited_member.setVisibility(View.GONE);
                ly_edit_delete.setVisibility(View.GONE);
            }
        }

        ly_reject.setOnClickListener(this);
        ly_accept.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_share_event.setOnClickListener(this);
        ly_join_member.setOnClickListener(this);
        ly_invited_member.setOnClickListener(this);
        ly_delete_myevent.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        cv_accept_companion.setOnClickListener(this);
        cv_reject_companion.setOnClickListener(this);
        ly_edit_event.setOnClickListener(this);
        ly_companion.setOnClickListener(this);
        iv_event_img.setOnClickListener(this);
        iv_social_share.setOnClickListener(this);
        iv_chat_group_img.setOnClickListener(this);

    }

    private void joined_view_visible() {
        rcv_chat_member.setVisibility(View.VISIBLE);
        rcv_joined_member.setVisibility(View.VISIBLE);
        tv_joined_member_txt.setVisibility(View.VISIBLE);

        if (detailsInfo != null) {
            if (detailsInfo.Detail.joinedMemberCount > 3) {
                detailsInfo.Detail.joinedMemberCount = detailsInfo.Detail.joinedMemberCount - 3;
                tv_joined_count.setText(detailsInfo.Detail.joinedMemberCount + "+");
                ly_joined_count.setVisibility(View.VISIBLE);

                iv_chat_group_img.setVisibility(View.VISIBLE);

            } else if (detailsInfo.Detail.joinedMemberCount == 0) {
                rcv_chat_member.setVisibility(View.GONE);
                rcv_joined_member.setVisibility(View.GONE);
                tv_joined_count.setText(detailsInfo.Detail.joinedMemberCount + "");
                ly_joined_count.setVisibility(View.VISIBLE);

                tv_chat_count.setText(detailsInfo.Detail.joinedMemberCount + "");
                ly_chat_count.setVisibility(View.VISIBLE);
            } else {
                ly_joined_count.setVisibility(View.GONE);
                ly_chat_count.setVisibility(View.GONE);
                iv_chat_group_img.setVisibility(View.VISIBLE);
            }

        }
    }

    private void init() {
        tv_invite_member_txt = findViewById(R.id.tv_invite_member_txt);
        ly_edit_delete = findViewById(R.id.ly_edit_delete);
        loading_view = findViewById(R.id.loading_view);
        tv_share_event = findViewById(R.id.tv_share_event);
        tv_invite_count = findViewById(R.id.tv_invite_count);
        ly_accept = findViewById(R.id.ly_accept);
        ly_reject = findViewById(R.id.ly_reject);
        tv_joined_member_txt = findViewById(R.id.tv_joined_member_txt);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        cv_reject_companion = findViewById(R.id.cv_reject_companion);
        cv_accept_companion = findViewById(R.id.cv_accept_companion);
        tv_comp_count = findViewById(R.id.tv_comp_count);
        map_image = findViewById(R.id.map_image);

        cv_companion_view = findViewById(R.id.cv_companion_view);
        ly_companion = findViewById(R.id.ly_companion);
        ly_accept_reject = findViewById(R.id.ly_accept_reject);
        ly_invited_member = findViewById(R.id.ly_invited_member);
        ly_join_member = findViewById(R.id.ly_join_member);
        tv_start_th = findViewById(R.id.tv_start_th);
        tv_end_th = findViewById(R.id.tv_end_th);
        ly_joined = findViewById(R.id.ly_joined);
        ly_join_accept_reject = findViewById(R.id.ly_join_accept_reject);
        ly_business_way = findViewById(R.id.ly_business_way);
        ly_address_image = findViewById(R.id.ly_address_image);
        ly_all_bottom_view = findViewById(R.id.ly_all_bottom_view);
        ly_companion_view = findViewById(R.id.ly_companion_view);
        ly_main_invited_mem = findViewById(R.id.ly_main_invited_mem);
        ly_shared_event_btn = findViewById(R.id.ly_shared_event_btn);
        ly_shared_event_call_btn = findViewById(R.id.ly_shared_event_call_btn);
        ly_comp_count = findViewById(R.id.ly_comp_count);
        ly_joined_count = findViewById(R.id.ly_joined_count);
        ly_invite_count = findViewById(R.id.ly_invite_count);
        ly_chat_count = findViewById(R.id.ly_chat_count);
        ly_chat_view = findViewById(R.id.ly_chat_view);
        bottom_sheet = findViewById(R.id.bottom_sheet);

        ly_delete_myevent = findViewById(R.id.ly_delete_myevent);
        compainion_img = findViewById(R.id.compainion_img);

        ly_edit_event = findViewById(R.id.ly_edit_event);
        iv_chat_group_img = findViewById(R.id.iv_chat_group_img);

        tv_address = findViewById(R.id.tv_address);
        tv_day = findViewById(R.id.tv_day);
        tv_start_date_time = findViewById(R.id.tv_start_date_time);
        tv_max_user = findViewById(R.id.tv_max_user);
        tv_paid_amount = findViewById(R.id.tv_paid_amount);
        tv_pay = findViewById(R.id.tv_pay);
        tv_end_day = findViewById(R.id.tv_end_day);
        tv_end_date_time = findViewById(R.id.tv_end_date_time);
        tv_gender = findViewById(R.id.tv_gender);
        tv_event_name = findViewById(R.id.tv_event_name);
        tv_event_creater_name = findViewById(R.id.tv_event_creater_name);
        tv_type_of_user = findViewById(R.id.tv_type_of_user);
        iv_profile = findViewById(R.id.iv_profile);
        tv_privacy = findViewById(R.id.tv_privacy);
        tv_comp_name = findViewById(R.id.tv_comp_name);

        rcv_invite_member = findViewById(R.id.rcv_invite_member);
        rcv_joined_member = findViewById(R.id.rcv_joined_member);
        rcv_companion = findViewById(R.id.rcv_companion);
        rcv_chat_member = findViewById(R.id.rcv_chat_member);
        iv_back = findViewById(R.id.iv_back);
        tv_joined_count = findViewById(R.id.tv_joined_count);
        tv_chat_count = findViewById(R.id.tv_chat_count);
        tv_time_From = findViewById(R.id.tv_time_From);
        tv_time_To = findViewById(R.id.tv_time_To);
        tv_business_name = findViewById(R.id.tv_business_name);
        iv_businessImg = findViewById(R.id.iv_businessImg);
        iv_event_img = findViewById(R.id.iv_event_img);
        iv_social_share = findViewById(R.id.iv_social_share);

        rcv_event_images = findViewById(R.id.rcv_event_images);
    }

    public void setData(EventDetailsInfo.DetailBean detail) {
        tv_address.setText(detail.eventPlace);
        openGoogleMap(detail, this, map_image);
        day_time(tv_start_th, tv_day, tv_start_date_time, detail.eventStartDate, tv_time_From);

        tv_paid_amount.setText(detail.currencySymbol + detail.eventAmount);
        day_time(tv_end_th, tv_end_day, tv_end_date_time, detail.eventEndDate, tv_time_To);

        if (detail.eventUserType.equals("Male")) {
            userGenderType = "1";
        } else if (detail.eventUserType.equals("Female")) {
            userGenderType = "2";
        } else if (detail.eventUserType.equals("Both")) {
            userGenderType = "3";
        }

        if(detail.groupChat.equals("1")){
            ly_chat_view.setVisibility(View.VISIBLE);
        }else  ly_chat_view.setVisibility(View.GONE);

        currencyCode = detail.currencyCode;
        eventAmount = detail.eventAmount;
        memberId = detail.memberId;
        eventMemId = detail.eventMemId;
        eventOrgnizarId = detail.eventOrganizer;

        tv_gender.setText(detail.eventUserType);
        tv_event_name.setText(detail.eventName);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);

        tv_max_user.setText(detail.userLimit);
        tv_privacy.setText(detail.privacy);
        tv_payment_status.setText(detail.payment);

        if (detail.joinedMemberCount > 3) {
            ly_joined_count.setVisibility(View.VISIBLE);
            detail.joinedMemberCount = detail.joinedMemberCount - 3;
            tv_joined_count.setText(detail.joinedMemberCount + "+");
        } else if (detail.joinedMemberCount == 0) {
            tv_joined_count.setText(detail.joinedMemberCount + "");
        } else {
            ly_joined_count.setVisibility(View.GONE);
        }


        if (detail.businessId.equals("")) {
            tv_business_name.setVisibility(View.GONE);
            ly_business_way.setVisibility(View.GONE);
            ly_address_image.setVisibility(View.VISIBLE);
        } else {
            tv_business_name.setVisibility(View.VISIBLE);
            ly_business_way.setVisibility(View.VISIBLE);
            ly_address_image.setVisibility(View.GONE);

            tv_business_name.setText(detail.businessName);
            Glide.with(getApplicationContext()).load(detail.businessImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(iv_businessImg);

            ly_business_way.setOnClickListener(view -> {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(detail.businesslat), Double.parseDouble(detail.businesslong));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            });
        }

        Glide.with(getApplicationContext()).load(detail.eventImage.get(0).eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(iv_event_img);


        if (from.equals("myEvent")) { // this is myevent if part

            if (detail.invitedMemberCount != null && !TextUtils.isEmpty(detail.invitedMemberCount)) {
                if (Integer.parseInt(detail.invitedMemberCount) > 3) {
                    ly_invite_count.setVisibility(View.VISIBLE);
                    int count = Integer.parseInt(detail.invitedMemberCount) - 3;
                    detail.invitedMemberCount = count + "";
                    tv_invite_count.setText(detail.invitedMemberCount + "+");
                } else if (Integer.parseInt(detail.invitedMemberCount) == 0) {
                    ly_invite_count.setVisibility(View.VISIBLE);
                    tv_invite_count.setText(detail.invitedMemberCount + "");
                } else {
                    ly_invite_count.setVisibility(View.GONE);
                }
            }


            if (isExpaireDate(currentDate, detail.eventEndDate) || detail.joinedMemberCount != 0) {
                ly_delete_myevent.setVisibility(View.VISIBLE);
                ly_edit_event.setVisibility(View.GONE);
                isExpaireDate = true;
            } else {
                ly_delete_myevent.setVisibility(View.VISIBLE);
                ly_edit_event.setVisibility(View.VISIBLE);
                isExpaireDate = false;
            }

            ly_companion_view.setVisibility(View.GONE);
            tv_event_creater_name.setText(detail.fullName);
            if (getApplicationContext() != null) {
                Glide.with(getApplicationContext()).load(detail.profileImage).apply(options).into(iv_profile);
            }
            tv_type_of_user.setText("Administrator");
            joined_view_visible();

        } else if (from.equals("eventRequest")) { // this is 1st tab ie request event part

            if (!detailsInfo.Detail.confirmedCount.equals("") && !detailsInfo.Detail.userLimit.equals("")) {
                if (Integer.parseInt(detailsInfo.Detail.confirmedCount) < Integer.parseInt(detailsInfo.Detail.userLimit)) {
                    isLimitOver = true;
                } else {
                    isLimitOver = false;
                }
            }


            switch (detail.memberStatus) {
                case Constant.Confirmed:
                    tv_pay.setVisibility(View.GONE);
                    tv_share_event.setVisibility(View.VISIBLE);
                    ly_join_accept_reject.setVisibility(View.GONE);
                    ly_all_bottom_view.setVisibility(View.VISIBLE);
                    ly_companion_view.setVisibility(View.VISIBLE);
                    joined_view_visible();
                    break;
                case Constant.Pending_request:
                    ly_join_accept_reject.setVisibility(View.VISIBLE);
                    tv_share_event.setVisibility(View.GONE);
                    tv_pay.setVisibility(View.GONE);
                    tv_joined_count.setVisibility(View.GONE);
                    ly_chat_count.setVisibility(View.GONE);
                    // All Bottom view is hide here below the code for hiding view
                    ly_all_bottom_view.setVisibility(View.GONE);
                    ly_companion_view.setVisibility(View.GONE);

                    break;
                case Constant.Confirmed_payment:
                    tv_pay.setVisibility(View.GONE);
                    tv_share_event.setVisibility(View.VISIBLE);
                    ly_join_accept_reject.setVisibility(View.GONE);
                    ly_all_bottom_view.setVisibility(View.VISIBLE);
                    ly_companion_view.setVisibility(View.VISIBLE);

                    joined_view_visible();
                    break;
                case Constant.Joined_Payment_is_pending:
                    tv_pay.setVisibility(View.VISIBLE);
                    tv_share_event.setVisibility(View.GONE);
                    ly_join_accept_reject.setVisibility(View.GONE);
                    ly_all_bottom_view.setVisibility(View.GONE);
                    ly_companion_view.setVisibility(View.VISIBLE);
                    joined_view_visible();
                    break;
            }

            if (detail.payment.equals("Free")) {
                tv_pay.setVisibility(View.GONE);
            }


            if (detail.companionMemberCount != null && !TextUtils.isEmpty(detail.companionMemberCount)) {

                if (Integer.parseInt(detail.companionMemberCount) > 3) {
                    ly_comp_count.setVisibility(View.VISIBLE);
                    int count = Integer.parseInt(detail.companionMemberCount) - 3;
                    detail.companionMemberCount = count + "";
                    tv_comp_count.setText(detail.companionMemberCount + "+");
                    rcv_companion.setVisibility(View.VISIBLE);
                } else if (Integer.parseInt(detail.companionMemberCount) == 0) {
                    tv_comp_count.setText(detail.companionMemberCount + "");
                    ly_companion_view.setVisibility(View.GONE);
                } else {
                    ly_comp_count.setVisibility(View.GONE);
                    rcv_companion.setVisibility(View.VISIBLE);
                }

                ly_accept_reject.setVisibility(View.GONE);
            }


            tv_event_creater_name.setText(detail.fullName);
            if (getApplicationContext() != null) {
                Glide.with(getApplicationContext()).load(detail.profileImage).apply(options).into(iv_profile);
            }
            tv_type_of_user.setText(detail.ownerType);


/*...................................................................................................................*/
            if (detail.ownerType.equals("Shared Event")) {

                if (detail.memberStatus.equals(Constant.Joined_Payment_is_pending)) {
                    all_view_hide();
                    ly_shared_event_btn.setVisibility(View.GONE);
                    ly_shared_event_call_btn.setVisibility(View.VISIBLE);
                } else if (detail.memberStatus.equals(Constant.Confirmed)) {
                    all_view_hide();
                    ly_shared_event_btn.setVisibility(View.GONE);
                    ly_shared_event_call_btn.setVisibility(View.VISIBLE);

                } else if (detail.memberStatus.equals(Constant.Confirmed_payment)) {
                    all_view_hide();
                    ly_shared_event_btn.setVisibility(View.GONE);
                    ly_shared_event_call_btn.setVisibility(View.VISIBLE);
                } else {
                    ly_accept_reject.setVisibility(View.VISIBLE);
                    ly_companion_view.setVisibility(View.VISIBLE);

                    ly_join_accept_reject.setVisibility(View.GONE);
                    tv_pay.setVisibility(View.GONE);
                    tv_share_event.setVisibility(View.GONE);

                }
                cv_companion_view.setVisibility(View.GONE);


                tv_event_creater_name.setText(detail.ownerName);
                if (getApplicationContext() != null) {
                    Glide.with(getApplicationContext()).load(detail.ownerImage).apply(options).into(iv_profile);
                }
                tv_type_of_user.setText("Administrator");


                tv_comp_name.setText(detail.fullName);
                if (getApplicationContext() != null) {
                    Glide.with(getApplicationContext()).load(detail.profileImage).apply(options).into(compainion_img);
                }
                ly_all_bottom_view.setVisibility(View.VISIBLE);


            } else if (detail.ownerType.equals("Administrator") && Integer.parseInt(detail.companionMemberCount) > 0) {
                tv_share_event.setVisibility(View.GONE);
                cv_companion_view.setVisibility(View.VISIBLE);
            }

            if (isExpaireDate(currentDate, detail.eventEndDate)) {
                //All view should be gone
                tv_share_event.setVisibility(View.GONE);
                tv_pay.setVisibility(View.GONE);
                ly_accept_reject.setVisibility(View.GONE);
                ly_join_accept_reject.setVisibility(View.GONE);
            }

            /*if(detail.joinedMemberCount == Integer.parseInt(detail.userLimit)){
                //All view should be gone
                tv_share_event.setVisibility(View.GONE);
                tv_pay.setVisibility(View.GONE);
                ly_accept_reject.setVisibility(View.GONE);
                ly_accept.setVisibility(View.GONE);
            }*/
        }


    }

    private void openGoogleMap(EventDetailsInfo.DetailBean detail, Context context, ImageView iv_map_img) {
        // val API_KEY = "AIzaSyBnFGTrGe8dJKMnrcinn1edleHCB_yZI5U"
        String API_KEY = "AIzaSyDI-QUWEEWFiV1W90w4PW2UWpIt04_DsmA";
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + "&zoom=16&size=800x800&maptype=roadmap" +
                "&markers=color:red%7Clabel:S%7C" + detail.eventLatitude + "," + detail.eventLongitude + "&key=" + API_KEY;
        Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image))
                .into(iv_map_img);
    }

    private void all_view_hide() {
        //ly_accept_reject.setVisibility(View.GONE);
        tv_share_event.setVisibility(View.GONE);
        tv_pay.setVisibility(View.GONE);
        cv_companion_view.setVisibility(View.GONE);
    }

    private void eventShareDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.event_share_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        ImageView cancel_button = dialog.findViewById(R.id.cancel_button);
        ImageView event_image = dialog.findViewById(R.id.event_image);
        TextView event_name = dialog.findViewById(R.id.event_name);
        TextView tv_address = dialog.findViewById(R.id.tv_address);

        TextView tv_day = dialog.findViewById(R.id.tv_day);
        TextView tv_start_th = dialog.findViewById(R.id.tv_start_th);
        TextView tv_start_date_time = dialog.findViewById(R.id.tv_start_date_time);
        TextView tv_time_From = dialog.findViewById(R.id.tv_time_From);

        TextView tv_end_day = dialog.findViewById(R.id.tv_end_day);
        TextView tv_end_th = dialog.findViewById(R.id.tv_end_th);
        TextView tv_end_date_time = dialog.findViewById(R.id.tv_end_date_time);
        TextView tv_time_To = dialog.findViewById(R.id.tv_time_To);
        LinearLayout ly_sharing_details = dialog.findViewById(R.id.ly_sharing_details);

        Button btn_share = dialog.findViewById(R.id.btn_share);

        Glide.with(getApplicationContext()).load(detailsInfo.Detail.eventImage.get(0).eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(event_image);
        event_name.setText(detailsInfo.Detail.eventName);
        tv_address.setText(detailsInfo.Detail.eventPlace);

        day_time(tv_start_th, tv_day, tv_start_date_time, detailsInfo.Detail.eventStartDate, tv_time_From);
        day_time(tv_end_th, tv_end_day, tv_end_date_time, detailsInfo.Detail.eventEndDate, tv_time_To);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionForTakepicture(ly_sharing_details,detailsInfo.Detail.eventPlace);
                dialog.dismiss();
            }
        });


        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
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
        sharIntent.putExtra(Intent.EXTRA_TEXT, text+"\n"+"This URl will get soon");
        startActivity(Intent.createChooser(sharIntent, "Share:"));
    }

    private void day_time(TextView th, TextView day, TextView date_time, String eventdate, TextView time) {
        try {
            String timeLong = eventdate;

            SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("MMM yyyy,", Locale.US);
            SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa", Locale.US);
            SimpleDateFormat formatShort1 = new SimpleDateFormat("dd", Locale.US);
            Log.v("timeLong", formatShort1.format(formatLong.parse(timeLong)));

            time.setText(formatTime.format(formatLong.parse(timeLong)));
            day.setText(formatShort1.format(formatLong.parse(timeLong)));
            date_time.setText(formatShort.format(formatLong.parse(timeLong)));
            th.setText(getDayOfMonthSuffix(Integer.parseInt(formatShort1.format(formatLong.parse(timeLong)))));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }



    private String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private void shareEvent(String userId) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("memberId", userId);
        map.put("eventMemId", eventMemId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        myEventRequestEvent(eventId, from);
                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("event/shareMember", map);

    }

    private void myEventRequestEvent(final String eventId, final String eventType) {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                invitedMemberList.clear();
                joinedMemberList.clear();
                companionMemberList.clear();
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        bottom_sheet.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        detailsInfo = gson.fromJson(response, EventDetailsInfo.class);

                        if (detailsInfo.Detail.privacy.equals("Private")) {
                            eventPrivacy = "2";
                        } else {
                            eventPrivacy = "1";
                        }

                        if (detailsInfo.Detail.ownerType != null) {
                            if (detailsInfo.Detail.ownerType.contains("_")) {
                                detailsInfo.Detail.ownerType = detailsInfo.Detail.ownerType.replace("_", " ");
                            }
                        }

                        for (EventDetailsInfo.InvitedMember list : detailsInfo.invitedMember) {
                            invitedMemberList.add(list.userImg);
                        }

                        for (EventDetailsInfo.JoinedMemberBean list : detailsInfo.joinedMember) {
                            joinedMemberList.add(list.userImg);
                        }

                        for (EventDetailsInfo.CompanionMember list : detailsInfo.companionMember) {
                            companionMemberList.add(list.userImg);
                        }

                        invitedMemberAdapter.notifyDataSetChanged();
                        joinedMemberAdapter.notifyDataSetChanged();
                        companionMemAdapter.notifyDataSetChanged();

                        currentDate = detailsInfo.currentDate;
                        setData(detailsInfo.Detail);

                        eventImageAdapter = new NewProfileAdapter(detailsInfo.Detail.eventImage, EventDetailsActivity.this, new GetNewImageClick() {
                            @Override
                            public void imageClick(int position) {
                                Intent intent = new Intent(EventDetailsActivity.this, MatchGalleryActivity.class);
                                intent.putExtra("user_id", userId);
                                intent.putExtra("image_index", position);
                                intent.putExtra("eventImages", detailsInfo.Detail);
                                startActivity(intent);
                            }
                        });
                        rcv_event_images.setAdapter(eventImageAdapter);
                        eventImageAdapter.notifyDataSetChanged();

                    } else {

                        noEventDialog(EventDetailsActivity.this);
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
        if (ownerType != null) {
            if (ownerType.equals("Administrator")) {
                service.callGetSimpleVolley("event/getEventDetail?eventId=" + eventId + "&detailType=" + eventType + "" + "&compId=" + "" + "&eventMemId=" + id + "");
            } else {
                service.callGetSimpleVolley("event/getEventDetail?eventId=" + eventId + "&detailType=" + eventType + "" + "&compId=" + id + "&eventMemId=" + "" + "");
            }
        } else {
            service.callGetSimpleVolley("event/getEventDetail?eventId=" + eventId + "&detailType=" + eventType);
        }

        //https://dev.apoim.com/service/event/getEventDetail?eventId=57&detailType=eventRequest&compId=56&eventMemId
    }

    public void noEventDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage("Sorry this event is no longer exists");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ly_accept:
                if (isLimitOver) {
                    //if(invitedMemberList.size() !=0)
                    joinEvent(eventId, "1"); // status 1 mean accept case
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }

                break;

            case R.id.ly_reject:
                joinEvent(eventId, "2"); // status 2 mean reject case
                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_share_event:
                if (isLimitOver) {
                    //eventJoinMenberDialog();
                    intent = new Intent(EventDetailsActivity.this, SelectCompanionActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("eventUserType", detailsInfo.Detail.eventUserType);
                    intent.putExtra("privacy", detailsInfo.Detail.privacy);
                    intent.putExtra("eventOrganizer", detailsInfo.Detail.eventOrganizer);

                    EventFilterData data = new EventFilterData();
                    session.createFilterData(data);
                    RattingIds = "";

                    startActivityForResult(intent, Constant.EventCompanionCode);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.ly_join_member:
                if (joinedMemberList.size() != 0) {
                    intent = new Intent(EventDetailsActivity.this, JoinedEventActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("from", from);
                    intent.putExtra("isExpaireDate", isExpaireDate);
                    startActivity(intent);
                }
                break;

            case R.id.ly_invited_member:
                if (invitedMemberList.size() != 0) {
                    intent = new Intent(EventDetailsActivity.this, InvidedEventActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("from", from);
                    intent.putExtra("isExpaireDate", isExpaireDate);
                    startActivity(intent);
                }
                break;

            case R.id.ly_delete_myevent:
                deleteAlertDialog(eventId);
                break;

            case R.id.tv_pay:
                if (isLimitOver) {
                    payDialog(eventAmount);

                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.cv_accept_companion:
                if (isLimitOver) {
                    if (!TextUtils.isEmpty(eventId))
                        accptRejectReq(eventId, "1", eventMemId);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.cv_reject_companion:
                if (isLimitOver) {
                    if (!TextUtils.isEmpty(eventId))
                        accptRejectReq(eventId, "2", eventMemId);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.ly_edit_event:
                ly_edit_event.setEnabled(false);
                intent = new Intent(this,CreateNewEventActivity.class);
                intent.putExtra(Constant.editEvent, Constant.editEvent);
                intent.putExtra("eventId", eventId);
                startActivity(intent);

                break;

            case R.id.ly_companion:
                ly_companion.setEnabled(false);
                intent = new Intent(EventDetailsActivity.this, CompanionListActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("from", from);
                intent.putExtra("isLimitOver", isLimitOver);
                startActivity(intent);
                break;

            case R.id.iv_event_img:
                intent = new Intent(EventDetailsActivity.this, MatchGalleryActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("image_index", 0);
                intent.putExtra("eventImages", detailsInfo.Detail);
                startActivity(intent);
                break;

            case R.id.iv_social_share:
                eventShareDialog();
                break;

            case R.id.iv_chat_group_img:
                intent = new Intent(EventDetailsActivity.this, GroupChatHistortActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("from", from);
                intent.putExtra("eventName", detailsInfo.Detail.eventName);
                intent.putExtra("eventImage", detailsInfo.Detail.eventImage.get(0).eventImage);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myEventRequestEvent(eventId, from);
        ly_edit_event.setEnabled(true);
        ly_companion.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.EventPayRequestCode) {
                isEventPaymentDone = data.getBooleanExtra("isEventPaymentDone", false);
                if (isEventPaymentDone) {
                    myEventRequestEvent(eventId, from);

                    if (dialog != null)
                        dialog.dismiss();
                }

            }


            if (requestCode == Constant.EventCompanionCode) {
                shareEvent(friendsIds);
                friendsIds = "";

            }
        }
    }

    private void payDialog(String amount) {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.event_pay_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tv_paid_amount = dialog.findViewById(R.id.tv_paid_amount);
        tv_paid_amount.setText("$" + amount);

        ImageView cancel_button = dialog.findViewById(R.id.cancel_button);
        Button btn_pay = dialog.findViewById(R.id.btn_pay);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(memberId)) {
                    if (isEventPaymentDone == false) {
                        Intent intent = new Intent(EventDetailsActivity.this, SubscriptionPayActivity.class);
                        intent.putExtra("eventId", eventId);
                        intent.putExtra("memberId", memberId);
                        intent.putExtra("eventAmount", eventAmount);
                        intent.putExtra("currencyCode", currencyCode);
                        intent.putExtra("paymentType", 3);// 3 for event pay
                        startActivityForResult(intent, Constant.EventPayRequestCode);
                    }
                    //paymentTask(eventId, memberId, "event/eventPayment");
                }
            }
        });


        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void accptRejectReq(String eventId, String status, String eventMemId) {
        cv_accept_companion.setEnabled(false);
        cv_reject_companion.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        Map<String, String> param = new HashMap<>();
        param.put("eventId", eventId);
        param.put("status", status);
        param.put("eventMemId", eventMemId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                cv_accept_companion.setEnabled(true);
                cv_reject_companion.setEnabled(true);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        myEventRequestEvent(eventId, from);
                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }

                    //Utils.openAlertDialog(EventDetailsActivity.this,message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    cv_accept_companion.setEnabled(true);
                    cv_reject_companion.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
                cv_accept_companion.setEnabled(true);
                cv_reject_companion.setEnabled(true);
            }
        });

        service.callSimpleVolley("event/companionMemberStatus", param);
    }


    public void deleteAlertDialog(final String eventId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.delete_event_msg));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteEventTask(eventId);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteEventTask(String eventId) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        finish();
                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("event/deleteEvent", map);

    }

    private void showFriendList(final String eventUserType, final InsLoadingView loading_view, final ShareEventJoinAdapter adapter, final LinearLayout ly_no_friend_found, final RecyclerView recycler_view) {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    Log.e("FRIEND LIST RESPONSE", response);

                    friendList.clear();

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        MyFriendListInfo friendListInfo = gson.fromJson(response, MyFriendListInfo.class);

                        switch (eventUserType) {
                            case "1": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("1")) {
                                        if (!friendListInfo.List.get(i).userId.equals(eventOrgnizarId))
                                            friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "2": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("2")) {
                                        if (!friendListInfo.List.get(i).userId.equals(eventOrgnizarId))
                                            friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "3": {
                                for (MyFriendListInfo.ListBean info : friendListInfo.List) {
                                    if (!info.userId.equals(eventOrgnizarId))
                                        friendList.add(info);
                                }
                                break;
                            }
                        }

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            ly_no_friend_found.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
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
        service.callGetSimpleVolley("user/getFriendList?offset=0&limit=20&listType=friend&eventId=" + eventId + "");
    }

    private void joinEvent(final String eventId, String reqStatus) {
        ly_accept.setEnabled(false);
        Session session = new Session(this, this);
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("status", reqStatus);
        map.put("eventId", eventId);
        map.put("memberId", session.getUser().userDetail.userId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);
                ly_accept.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        ly_join_accept_reject.setVisibility(View.GONE);

                        if (reqStatus.equals("1")) {
                            joined_view_visible();
                            myEventRequestEvent(eventId, from);

                        } else {
                            finish();
                        }

                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ly_accept.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                ly_accept.setEnabled(true);
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("event/joinMember", map);
    }

    private Date getDateFromString(String startDateString) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        try {
            startDate = df.parse(startDateString);
            String newDateString = df.format(startDate);
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    private boolean isExpaireDate(String currentDate, String endDate) {
        Date date2 = getDateFromString(currentDate);
        Date date1 = getDateFromString(endDate);

        if (date1.after(date2)) {
            //Do Something else
            return false;
        } else {
            return true;

        }
    }

}
