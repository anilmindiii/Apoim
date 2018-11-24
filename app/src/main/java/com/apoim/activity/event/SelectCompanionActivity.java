package com.apoim.activity.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.InviteFrienAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.modal.AllUserForEventInfo;
import com.apoim.modal.EventFilterData;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.apoim.activity.event.CreateEventActivity.friendsIds;

public class SelectCompanionActivity extends AppCompatActivity {
    private TextView tv_invite;
    private ImageView iv_filter,iv_back;
    private RecyclerView recycler_view;

    private String eventOrganizer = "";
    private String eventId = "";
    private String eventUserType = "";
    private String privacy = "";
    private ArrayList<AllUserForEventInfo.DataBean.UserBean> friendList;
    private InsLoadingView loadingView;
    private LinearLayout ly_no_friend_found;
    private EditText ed_search_friend;
    private Session session;
    String latitude = null, longitude = null, name = null;
    private InviteFrienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event_thired_screen);

        if (getIntent() != null) {
            eventUserType = getIntent().getStringExtra("eventUserType");
            privacy = getIntent().getStringExtra("privacy");
            eventId = getIntent().getStringExtra("eventId");
            eventOrganizer = getIntent().getStringExtra("eventOrganizer");

            if (privacy.equals("Private")) {
                privacy = "2";
            } else {
                privacy = "1";
            }


            if (eventUserType.equals("Male")) {
                eventUserType = "1";
            } else if (eventUserType.equals("Female")) {
                eventUserType = "2";
            } else if (eventUserType.equals("Both")) {
                eventUserType = "";
            }
        }

        TextView profile_action_bar = findViewById(R.id.profile_action_bar);
        profile_action_bar.setText("Invite Companion Member");





        session = new Session(this);
        tv_invite = findViewById(R.id.tv_next_thired);
        tv_invite.setText(R.string.invite);
        ed_search_friend = findViewById(R.id.ed_search_friend);

        iv_back = findViewById(R.id.iv_back);
        iv_filter = findViewById(R.id.iv_filter);
        recycler_view = findViewById(R.id.recycler_view);
        loadingView = findViewById(R.id.loadingView);
        ly_no_friend_found = findViewById(R.id.ly_no_friend_found);


        friendList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(manager);

        iv_back.setOnClickListener(view -> {
            onBackPressed();
        });

        iv_filter.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, FilterEventActivity.class);
            startActivityForResult(intent, Constant.eventFilter);
        });

        tv_invite.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(friendsIds)) {

              setResult(RESULT_OK);
              finish();

            } else {
                Utils.openAlertDialog(this, getString(R.string.event_invitaion));
            }


        });

        adapter = new InviteFrienAdapter(this, privacy, friendList, friendsIds, ids -> friendsIds = ids,eventOrganizer);


        recycler_view.setAdapter(adapter);
        searchFriend();
    }

    private void searchFriend() {

        ed_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textName = editable.toString();
                if (textName.equals("")) {
                    showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, null);
                } else {
                    showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, textName);
                }

                //InviteFrienAdapter adapter = new InviteFrienAdapter(mContext, privacy, temp_friendList, friendsIds, ids -> friendsIds = ids);
                //recycler_view.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, name);

        EventFilterData filterData = session.getFilterData();

        if (filterData != null)
            if (filterData.isApply) {
                iv_filter.setImageResource(R.drawable.ico_filter_active);
            } else {
                iv_filter.setImageResource(R.drawable.ico_filter);
            }


    }

    private void showFriendList(final InsLoadingView loading_view, final InviteFrienAdapter adapter,
                                final LinearLayout ly_no_friend_found, final RecyclerView recycler_view, String name) {
        loading_view.setVisibility(View.VISIBLE);
        String RattingIds = null;

        EventFilterData filterData = session.getFilterData();
        if (filterData != null) {
            latitude = filterData.latitude;
            longitude = filterData.longitude;
            RattingIds = filterData.rating;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("userGender", eventUserType);
        map.put("privacy", privacy);
        map.put("offset", "0");
        map.put("limit", "100");

        if (name != null)
            map.put("name", name);

        if (RattingIds != null) {
            if (!RattingIds.equals("")) {
                map.put("rating", RattingIds);
            }
        }

        if (latitude != null)
            map.put("latitude", latitude);

        if (longitude != null)
            map.put("longitude", longitude);

        map.put("eventId", eventId);

        friendList.clear();






        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");


                    if (status.equals("success")) {
                        friendList.clear();
                        Gson gson = new Gson();
                        AllUserForEventInfo allUserList = gson.fromJson(response, AllUserForEventInfo.class);
                        friendList.addAll(allUserList.data.user);

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            ly_no_friend_found.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                        }


                        if(friendsIds != null){

                            List<String> tempList = new ArrayList<String>(Arrays.asList(friendsIds.split(",")));
                            for (int i = 0; i < friendList.size(); i++) {

                                for (int j = 0; j < tempList.size(); j++) {
                                    if (tempList.get(j).equals(friendList.get(i).userId + "")) {
                                        friendList.get(i).isSelected = true;
                                    }

                                }

                            }
                        }



                        adapter.notifyDataSetChanged();
                    } else {

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
        service.callSimpleVolley("event/getInvitationUserList", map);


    }

}
