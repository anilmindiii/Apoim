package com.apoim.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.EventDetailsMemberAdapter;
import com.apoim.adapter.apoinment.ShareEventJoinAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.ShareListner;
import com.apoim.modal.EventDetailsInfo;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.bumptech.glide.util.Preconditions.checkArgument;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private EventDetailsMemberAdapter invitedMemberAdapter, joinedMemberAdapter, companionMemAdapter;
    private RecyclerView rcv_invite_member, rcv_joined_member, rcv_companion;
    private ImageView iv_back;
    private String from;
    private TextView tv_invite_member_txt;
    private TextView tv_address;
    private TextView tv_day;
    private TextView tv_start_date_time;
    private TextView tv_max_user;
    private TextView tv_paid_amount;
    private TextView tv_share;
    private TextView tv_pay;
    private TextView tv_end_day;
    private TextView tv_end_date_time;
    private TextView tv_gender;
    private TextView tv_event_name;
    private TextView tv_event_creater_name;
    private TextView tv_type_of_user;
    private TextView tv_privacy;
    private TextView tv_joined_count;
    private TextView tv_invite_count;
    private TextView tv_join;
    private TextView tv_joined_member_txt;
    private TextView tv_payment_status;
    private TextView tv_accept;
    private TextView tv_reject;
    private TextView tv_comp_count;
    private ImageView iv_profile;
    private LinearLayout ly_edit_delete, ly_accept_reject;
    private RelativeLayout ly_companion;
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
    private ImageView iv_delete_myevent;
    private String userId = "", memberId = "", currencyCode = "", eventAmount = "", eventPrivacy = "", currentDate = "", eventMemId = "", eventOrgnizarId = "", ownerType;
    private ImageView iv_edit_profile;
    private EventDetailsInfo detailsInfo;
    private CardView cv_companion_view;
    private boolean isExpaireDate;
    private boolean isLimitOver = false;
    private boolean isEventPaymentDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();

        friendList = new ArrayList<>();
        invitedMemberList = new ArrayList<>();
        joinedMemberList = new ArrayList<>();
        companionMemberList = new ArrayList<>();

        invitedMemberAdapter = new EventDetailsMemberAdapter(0, this, invitedMemberList);
        joinedMemberAdapter = new EventDetailsMemberAdapter(0, this, joinedMemberList);
        companionMemAdapter = new EventDetailsMemberAdapter(1, this, companionMemberList);

        rcv_invite_member.setAdapter(invitedMemberAdapter);
        rcv_joined_member.setAdapter(joinedMemberAdapter);
        rcv_companion.setAdapter(companionMemAdapter);

        if (getIntent().getStringExtra("from") != null) {
            from = getIntent().getStringExtra("from");
            eventId = getIntent().getStringExtra("eventId");

            id = getIntent().getStringExtra("id");
            ownerType = getIntent().getStringExtra("ownerType");

            if (from.equals("myEvent")) {
                tv_pay.setVisibility(View.GONE);
                tv_share.setVisibility(View.GONE);
                tv_join.setVisibility(View.GONE);

                ly_edit_delete.setVisibility(View.VISIBLE);
                tv_invite_member_txt.setVisibility(View.VISIBLE);
                rcv_invite_member.setVisibility(View.VISIBLE);
                tv_invite_count.setVisibility(View.VISIBLE);
                joined_view_visible();

            } else if (from.equals("eventRequest")) {
                ly_invited_member.setVisibility(View.GONE);
                ly_edit_delete.setVisibility(View.GONE);
            }
        }

        tv_join.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        ly_join_member.setOnClickListener(this);
        ly_invited_member.setOnClickListener(this);
        iv_delete_myevent.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        tv_accept.setOnClickListener(this);
        tv_reject.setOnClickListener(this);
        iv_edit_profile.setOnClickListener(this);
        ly_companion.setOnClickListener(this);

    }

    private void joined_view_visible() {
        rcv_joined_member.setVisibility(View.VISIBLE);
        tv_joined_member_txt.setVisibility(View.VISIBLE);

        if (detailsInfo != null) {
            if (detailsInfo.Detail.joinedMemberCount > 3) {
                detailsInfo.Detail.joinedMemberCount = detailsInfo.Detail.joinedMemberCount - 3;
                tv_joined_count.setText(detailsInfo.Detail.joinedMemberCount + "+");
                tv_joined_count.setVisibility(View.VISIBLE);
            } else if (detailsInfo.Detail.joinedMemberCount == 0) {
                tv_joined_count.setText(detailsInfo.Detail.joinedMemberCount + "");
                tv_joined_count.setVisibility(View.VISIBLE);
            } else {
                tv_joined_count.setVisibility(View.GONE);
            }

        }
    }

    private void init() {
        tv_invite_member_txt = findViewById(R.id.tv_invite_member_txt);
        ly_edit_delete = findViewById(R.id.ly_edit_delete);
        loading_view = findViewById(R.id.loading_view);
        tv_share = findViewById(R.id.tv_share);
        tv_invite_count = findViewById(R.id.tv_invite_count);
        tv_join = findViewById(R.id.tv_join);
        tv_joined_member_txt = findViewById(R.id.tv_joined_member_txt);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_reject = findViewById(R.id.tv_reject);
        tv_accept = findViewById(R.id.tv_accept);
        tv_comp_count = findViewById(R.id.tv_comp_count);

        cv_companion_view = findViewById(R.id.cv_companion_view);
        ly_companion = findViewById(R.id.ly_companion);
        ly_accept_reject = findViewById(R.id.ly_accept_reject);
        ly_invited_member = findViewById(R.id.ly_invited_member);
        ly_join_member = findViewById(R.id.ly_join_member);
        tv_start_th = findViewById(R.id.tv_start_th);
        tv_end_th = findViewById(R.id.tv_end_th);
        ly_joined = findViewById(R.id.ly_joined);
        iv_delete_myevent = findViewById(R.id.iv_delete_myevent);

        iv_edit_profile = findViewById(R.id.iv_edit_profile);

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
        rcv_invite_member = findViewById(R.id.rcv_invite_member);
        rcv_joined_member = findViewById(R.id.rcv_joined_member);
        rcv_companion = findViewById(R.id.rcv_companion);
        iv_back = findViewById(R.id.iv_back);
        tv_joined_count = findViewById(R.id.tv_joined_count);
    }

    public void setData(EventDetailsInfo.DetailBean detail) {
        tv_address.setText(detail.eventPlace);

        day_time(tv_start_th, tv_day, tv_start_date_time, detail.eventStartDate);

        tv_paid_amount.setText(detail.currencySymbol + detail.eventAmount);
        day_time(tv_end_th, tv_end_day, tv_end_date_time, detail.eventEndDate);

        if (detail.eventUserType.equals("Male")) {
            userGenderType = "1";
        } else if (detail.eventUserType.equals("Female")) {
            userGenderType = "2";
        } else if (detail.eventUserType.equals("Both")) {
            userGenderType = "3";
        }

        currencyCode = detail.currencyCode;
        eventAmount = detail.eventAmount;
        memberId = detail.memberId;
        eventMemId = detail.eventMemId;
        eventOrgnizarId = detail.eventOrganizer;

        tv_gender.setText(detail.eventUserType);
        tv_event_name.setText(detail.eventName);
        tv_event_creater_name.setText(detail.fullName);
        tv_max_user.setText(detail.userLimit);
        tv_privacy.setText(detail.privacy);
        tv_payment_status.setText(detail.payment);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);

        if (detail.joinedMemberCount > 3) {
            tv_joined_count.setVisibility(View.VISIBLE);
            detail.joinedMemberCount = detail.joinedMemberCount - 3;
            tv_joined_count.setText(detail.joinedMemberCount + "+");
        } else if (detail.joinedMemberCount == 0) {
            tv_joined_count.setText(detail.joinedMemberCount + "");
        } else {
            tv_joined_count.setVisibility(View.GONE);
        }

        if (from.equals("myEvent")) { // this is myevent if part

            if (detail.invitedMemberCount != null && !TextUtils.isEmpty(detail.invitedMemberCount)) {
                if (Integer.parseInt(detail.invitedMemberCount) > 3) {
                    tv_invite_count.setVisibility(View.VISIBLE);
                    int count = Integer.parseInt(detail.invitedMemberCount) - 3;
                    detail.invitedMemberCount = count + "";
                    tv_invite_count.setText(detail.invitedMemberCount + "+");
                } else if (Integer.parseInt(detail.invitedMemberCount) == 0) {
                    tv_invite_count.setVisibility(View.VISIBLE);
                    tv_invite_count.setText(detail.invitedMemberCount + "");
                } else {
                    tv_invite_count.setVisibility(View.GONE);
                }
            }


            if (isExpaireDate(currentDate, detail.eventEndDate)) {
                iv_delete_myevent.setVisibility(View.VISIBLE);
                iv_edit_profile.setVisibility(View.GONE);
                isExpaireDate = true;
            } else {
                iv_delete_myevent.setVisibility(View.VISIBLE);
                iv_edit_profile.setVisibility(View.VISIBLE);
                isExpaireDate = false;
            }


            joined_view_visible();

        } else if (from.equals("eventRequest")) { // this is 1st tab ie request event part

            if (!detailsInfo.Detail.confirmedCount.equals("") && !detailsInfo.Detail.userLimit.equals("")) {
                if (Integer.parseInt(detailsInfo.Detail.confirmedCount) < Integer.parseInt(detailsInfo.Detail.userLimit)) {
                    isLimitOver = true;
                } else {
                    isLimitOver = false;
                }
            }

            tv_type_of_user.setText(detail.ownerType);
            switch (detail.memberStatus) {
                case Constant.Confirmed:
                    tv_pay.setVisibility(View.GONE);
                    tv_share.setVisibility(View.VISIBLE);
                    tv_join.setVisibility(View.GONE);
                    joined_view_visible();
                    break;
                case Constant.Pending_request:
                    tv_join.setVisibility(View.VISIBLE);
                    tv_share.setVisibility(View.GONE);
                    tv_pay.setVisibility(View.GONE);

                    tv_joined_count.setVisibility(View.GONE);
                    break;
                case Constant.Confirmed_payment:
                    tv_pay.setVisibility(View.GONE);
                    tv_share.setVisibility(View.VISIBLE);
                    tv_join.setVisibility(View.GONE);

                    joined_view_visible();
                    break;
                case Constant.Joined_Payment_is_pending:
                    tv_pay.setVisibility(View.VISIBLE);
                    tv_share.setVisibility(View.GONE);
                    tv_join.setVisibility(View.GONE);
                    joined_view_visible();
                    break;
            }

            if (detail.payment.equals("Free")) {
                tv_pay.setVisibility(View.GONE);
            }


            if (detail.companionMemberCount != null && !TextUtils.isEmpty(detail.companionMemberCount)) {

                if (Integer.parseInt(detail.companionMemberCount) > 3) {
                    tv_comp_count.setVisibility(View.VISIBLE);
                    int count = Integer.parseInt(detail.companionMemberCount) - 3;
                    detail.companionMemberCount = count + "";
                    tv_comp_count.setText(detail.companionMemberCount + "+");
                } else if (Integer.parseInt(detail.companionMemberCount) == 0) {
                    tv_comp_count.setText(detail.companionMemberCount + "");
                    tv_comp_count.setVisibility(View.GONE);
                } else {
                    tv_comp_count.setVisibility(View.GONE);
                }
            }


/*...................................................................................................................*/
            if (detail.ownerType.equals("Shared Event")) {

                if (detail.memberStatus.equals(Constant.Joined_Payment_is_pending)) {
                    all_view_hide();
                } else if (detail.memberStatus.equals(Constant.Confirmed)) {
                    all_view_hide();
                } else if (detail.memberStatus.equals(Constant.Confirmed_payment)) {
                    all_view_hide();
                } else {
                    ly_accept_reject.setVisibility(View.VISIBLE);
                    tv_join.setVisibility(View.GONE);
                    tv_pay.setVisibility(View.GONE);
                    tv_share.setVisibility(View.GONE);

                }
                cv_companion_view.setVisibility(View.GONE);
            } else if (detail.ownerType.equals("Administrator")
                    && Integer.parseInt(detail.companionMemberCount) > 0) {
                tv_share.setVisibility(View.GONE);
                cv_companion_view.setVisibility(View.VISIBLE);
            }

            if (isExpaireDate(currentDate, detail.eventEndDate)) {
                //All view should be gone
                tv_share.setVisibility(View.GONE);
                tv_pay.setVisibility(View.GONE);
                ly_accept_reject.setVisibility(View.GONE);
                tv_join.setVisibility(View.GONE);
            }

            /*if(detail.joinedMemberCount == Integer.parseInt(detail.userLimit)){
                //All view should be gone
                tv_share.setVisibility(View.GONE);
                tv_pay.setVisibility(View.GONE);
                ly_accept_reject.setVisibility(View.GONE);
                tv_join.setVisibility(View.GONE);
            }*/
        }

        if (getApplicationContext() != null) {
            Glide.with(getApplicationContext()).load(detail.profileImage).apply(options).into(iv_profile);
        }

    }

    private void all_view_hide() {
        ly_accept_reject.setVisibility(View.GONE);
        tv_share.setVisibility(View.GONE);
        tv_pay.setVisibility(View.GONE);
    }

    private void day_time(TextView th, TextView day, TextView date_time, String eventdate) {
        try {
            String timeLong = eventdate;

            SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("MMM yyyy, hh:mm aa", Locale.US);
            SimpleDateFormat formatShort1 = new SimpleDateFormat("dd", Locale.US);
            Log.v("timeLong", formatShort1.format(formatLong.parse(timeLong)));

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



    private void eventJoinMenberDialog() {
        Session session = new Session(this, EventDetailsActivity.this);
        String myUserId = session.getUser().userDetail.userId;
        final ArrayList<MyFriendListInfo.ListBean> listBeans = new ArrayList<>();

        final Dialog add_interest_dialog = new Dialog(this);
        add_interest_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        add_interest_dialog.setContentView(R.layout.event_join_member_dialog_layout);
        add_interest_dialog.setCancelable(false);
        add_interest_dialog.setCanceledOnTouchOutside(false);

        final RecyclerView recyclerView = add_interest_dialog.findViewById(R.id.recycler_view);
        TextView tv_share_dialog_click = add_interest_dialog.findViewById(R.id.tv_share_dialog_click);
        final TextView add_interest_title = add_interest_dialog.findViewById(R.id.add_interest_title);
        final ImageView iv_cancel_dialog = add_interest_dialog.findViewById(R.id.iv_cancel_dialog);
        InsLoadingView loading_view = add_interest_dialog.findViewById(R.id.loading_view);
        final LinearLayout ly_no_friend_found = add_interest_dialog.findViewById(R.id.ly_no_friend_found);
        EditText friend_search = add_interest_dialog.findViewById(R.id.friend_search);

        friend_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                listBeans.clear();
                String str = editable.toString();
                for (MyFriendListInfo.ListBean bean : friendList) {
                    if (bean.fullName.toLowerCase().contains(str.toLowerCase())) {
                        listBeans.add(bean);

                        ly_no_friend_found.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        ly_no_friend_found.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                if (str.equalsIgnoreCase("")) {
                    adapter = new ShareEventJoinAdapter(EventDetailsActivity.this, eventPrivacy, friendList, new ShareListner() {
                        @Override
                        public void getEventMemId(String memberId) {
                            userId = memberId;
                        }
                    });
                    listBeans.clear();
                } else
                    adapter = new ShareEventJoinAdapter(EventDetailsActivity.this, eventPrivacy, listBeans, new ShareListner() {
                        @Override
                        public void getEventMemId(String memberId) {
                            userId = memberId;

                        }
                    });


                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

        adapter = new ShareEventJoinAdapter(EventDetailsActivity.this, eventPrivacy, friendList, new ShareListner() {
            @Override
            public void getEventMemId(String memberId) {
                userId = memberId;
            }
        });

        tv_share_dialog_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(userId)) {
                    if (userId.contains(",")) {
                        userId = removeLastChar(userId);
                    }

                    shareEvent(userId);
                    add_interest_dialog.dismiss();
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.share_event_dined));
                }

            }
        });


        recyclerView.setAdapter(adapter);

        showFriendList(userGenderType, loading_view, adapter, ly_no_friend_found, recyclerView);
        iv_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_interest_dialog.dismiss();
            }
        });

        add_interest_title.setText(R.string.share_event);
        add_interest_dialog.getWindow().setGravity(Gravity.CENTER);
        add_interest_dialog.show();
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
            case R.id.tv_join:
                if (isLimitOver) {
                    //if(invitedMemberList.size() !=0)
                    joinEvent(eventId);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }

                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_share:
                if (isLimitOver) {
                    //if(invitedMemberList.size() !=0)
                    eventJoinMenberDialog();
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

            case R.id.iv_delete_myevent:
                deleteAlertDialog(eventId);
                break;

            case R.id.tv_pay:
                if (isLimitOver) {
                    if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(memberId)){
                        if(isEventPaymentDone == false){
                            intent = new Intent(EventDetailsActivity.this, SubscriptionPayActivity.class);
                            intent.putExtra("eventId",eventId);
                            intent.putExtra("memberId",memberId);
                            intent.putExtra("eventAmount",eventAmount);
                            intent.putExtra("currencyCode",currencyCode);
                            intent.putExtra("paymentType",3);// 3 for event pay
                            startActivityForResult(intent, Constant.EventPayRequestCode);
                        }
                        //paymentTask(eventId, memberId, "event/eventPayment");
                    }

                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.tv_accept:
                if (isLimitOver) {
                    if (!TextUtils.isEmpty(eventId))
                        accptRejectReq(eventId, "1", eventMemId);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.tv_reject:
                if (isLimitOver) {
                    if (!TextUtils.isEmpty(eventId))
                        accptRejectReq(eventId, "2", eventMemId);
                } else {
                    Utils.openAlertDialog(EventDetailsActivity.this, getString(R.string.limit_over_to_join_event));
                }
                break;

            case R.id.iv_edit_profile:
                iv_edit_profile.setEnabled(false);
                intent = new Intent(this, CreateEventActivity.class);
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myEventRequestEvent(eventId, from);
        iv_edit_profile.setEnabled(true);
        ly_companion.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Constant.EventPayRequestCode){
                isEventPaymentDone = data.getBooleanExtra("isEventPaymentDone",false);
                if(isEventPaymentDone){
                    myEventRequestEvent(eventId, from);
                }
                else {

                }

            }
        }
    }

    private void accptRejectReq(String eventId, String status, String eventMemId) {
        tv_accept.setEnabled(false);
        tv_reject.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        Map<String, String> param = new HashMap<>();
        param.put("eventId", eventId);
        param.put("status", status);
        param.put("eventMemId", eventMemId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                tv_accept.setEnabled(true);
                tv_reject.setEnabled(true);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        ly_accept_reject.setVisibility(View.GONE);
                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }

                    //Utils.openAlertDialog(EventDetailsActivity.this,message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_accept.setEnabled(true);
                    tv_reject.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
                tv_accept.setEnabled(true);
                tv_reject.setEnabled(true);
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

    private void joinEvent(final String eventId) {
        tv_join.setEnabled(false);
        Session session = new Session(this, this);
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("memberId", session.getUser().userDetail.userId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);
                tv_join.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        tv_join.setVisibility(View.GONE);
                        joined_view_visible();
                        myEventRequestEvent(eventId, from);

                    } else {
                        Utils.openAlertDialog(EventDetailsActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_join.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                tv_join.setEnabled(true);
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
