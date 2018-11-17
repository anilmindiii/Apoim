package com.apoim.activity.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.chat.ChattingAdapter;
import com.apoim.adapter.newEvent.JoinedMemberChatAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetDateStatus;
import com.apoim.modal.JoinedEventInfo;
import com.apoim.modal.OnlineInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupMemberInfoActivity extends AppCompatActivity {
    private InsLoadingView loading_view;
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    Map<String, Object> onlineMapList;
    private RecyclerView recycler_view;
    private JoinedMemberChatAdapter adapter;
    private Session session;
    String from = "", eventId = "", myEmail = "", myUid = "", myName = "", myProfileImage = "", eventType = "", eventName = "";
    String eventOrganizerId = "", eventOrganizerName = "", eventOrganizerProfileImage = "";
    TextView title_name,tv_mem_count;
    ImageView header_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_info);

        loading_view = findViewById(R.id.loading_view);
        recycler_view = findViewById(R.id.recycler_view);
        title_name = findViewById(R.id.title_name);
        header_image = findViewById(R.id.header_image);
        tv_mem_count = findViewById(R.id.tv_mem_count);


        if (getIntent().getExtras() != null) {
            eventId = getIntent().getStringExtra("eventId");
            from = getIntent().getStringExtra("from");
            eventName = getIntent().getStringExtra("eventName");
            String eventImage = getIntent().getStringExtra("eventImage");

            eventOrganizerId = getIntent().getStringExtra("eventOrganizerId");
            eventOrganizerName = getIntent().getStringExtra("eventOrganizerName");
            eventOrganizerProfileImage = getIntent().getStringExtra("eventOrganizerProfileImage");

            eventType = getIntent().getStringExtra("eventType");
            title_name.setText(eventName);
            Glide.with(this).load(eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(header_image);
        }

        session = new Session(this);
        myUid = session.getUser().userDetail.userId;
        myName = session.getUser().userDetail.fullName;
        myEmail = session.getUser().userDetail.email;

        if (session.getUser().userDetail.profileImage.size() > 0) {
            myProfileImage = session.getUser().userDetail.profileImage.get(0).image;
        }


        adapter = new JoinedMemberChatAdapter(joinedList, this, myUid);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(adapter);


        joinedList = new ArrayList<>();
        onlineMapList = new HashMap<>();

        joinedEventList();
    }


    private void joinedEventList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        JoinedEventInfo joinedEventInfo = gson.fromJson(response, JoinedEventInfo.class);


                        for (int i = 0; i < joinedEventInfo.List.size(); i++) {
                            if(joinedEventInfo.List.get(i).memberUserId != null){
                                joinedEventInfo.List.get(i).commanUserIdForProfile = joinedEventInfo.List.get(i).memberUserId;

                            }

                            if (joinedEventInfo.List.get(i).companionMemberStatus != null) {
                                if (joinedEventInfo.List.get(i).companionMemberStatus.equals("3") ||
                                        joinedEventInfo.List.get(i).companionMemberStatus.equals("1")) {
                                    JoinedEventInfo.ListBean infoComp = new JoinedEventInfo.ListBean();

                                    infoComp.companionUserId = joinedEventInfo.List.get(i).companionUserId;
                                    infoComp.memberName = joinedEventInfo.List.get(i).companionName;
                                    infoComp.memberImage = joinedEventInfo.List.get(i).companionImage;
                                    infoComp.commanUserIdForProfile = joinedEventInfo.List.get(i).companionUserId;

                                    joinedEventInfo.List.add(infoComp);
                                }
                            }
                        }


                        if (eventType.equals("eventRequest")) {
                            JoinedEventInfo.ListBean info = new JoinedEventInfo.ListBean();
                            info.commanUserIdForProfile = eventOrganizerId;
                            info.memberId = eventOrganizerId;
                            info.memberUserId = eventOrganizerId;
                            info.memberName = eventOrganizerName + " " + "(Admin)";
                            info.memberImage = eventOrganizerProfileImage;

                            //joinedEventInfo.List.get(0). = session.getUser().userDetail.r;
                            joinedEventInfo.List.add(0, info);
                        } else {
                            // my data added here
                            JoinedEventInfo.ListBean info = new JoinedEventInfo.ListBean();
                            //info.commanUserIdForProfile = myUid;
                            info.memberUserId = myUid;
                            info.memberName = myName + " " + "(You)";
                            info.memberImage = session.getUser().userDetail.profileImage.get(session.getUser().userDetail.profileImage.size() - 1).image;
                            //joinedEventInfo.List.get(0). = session.getUser().userDetail.r;
                            joinedEventInfo.List.add(0, info);
                        }

                        joinedList.addAll(joinedEventInfo.List);
                        adapter.notifyDataSetChanged();
                        tv_mem_count.setText(joinedList.size() + " " + "Members");

                        addOnlineStatus();
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
        service.callGetSimpleVolley("event/getEventMemberList?offset=" + 0 + "&limit=1000&memberType=" + "joined" + "&eventId=" + eventId + "");
    }

    private void addOnlineStatus() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constant.online).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                onlineMapList.put(dataSnapshot.getKey(), onlineStatus);
                runTimeStatus(dataSnapshot, onlineStatus);

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                onlineMapList.put(dataSnapshot.getKey(), onlineStatus);
                runTimeStatus(dataSnapshot, onlineStatus);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                onlineMapList.put(dataSnapshot.getKey(), onlineStatus);
                runTimeStatus(dataSnapshot, onlineStatus);


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }


            private void runTimeStatus(@NonNull DataSnapshot dataSnapshot, OnlineInfo onlineStatus) {

                for (int k = 0; k < joinedList.size(); k++) {
                    if (dataSnapshot.getKey().equals(joinedList.get(k).commanUserIdForProfile)) {
                        joinedList.get(k).status = onlineStatus.lastOnline;
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });


    }
}
