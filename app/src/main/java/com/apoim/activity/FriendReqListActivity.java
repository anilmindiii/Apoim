package com.apoim.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.MyFriendsAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendReqListActivity extends AppCompatActivity {
    private InsLoadingView loading_view;
    private Session session;
    private RecyclerView recycler;
    private String myId = "";
    private MyFriendListInfo friendListInfo;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private MyFriendsAdapter myFriendsAdapter;
    private LinearLayout ly_no_friend_request;
    private ImageView iv_back;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int startCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_req_list);

        friendList = new ArrayList<>();
        friendListInfo = new MyFriendListInfo();
        recycler = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        ly_no_friend_request = findViewById(R.id.ly_no_friend_request);
        loading_view = findViewById(R.id.loading_view);
        session = new Session(this,this);
        myId = session.getUser().userDetail.userId;


        myFriendsAdapter = new MyFriendsAdapter(this,friendList,loading_view,ly_no_friend_request,"request");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recycler.addOnScrollListener(scrollListener);
        recycler.setAdapter(myFriendsAdapter);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }



    private void loadNextDataFromApi() {
        startCount = startCount+10;
        showFriendList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        friendList.clear();
        startCount = 0;
        scrollListener.resetState();
        showFriendList();
    }

    private void showFriendList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    Log.e("FRIEND LIST RESPONSE", response.toString());


                    if (status.equals("success")) {
                        Gson gson = new Gson();

                        friendListInfo = gson.fromJson(response, MyFriendListInfo.class);
                        friendList.addAll(friendListInfo.List);

                        if (friendList.size() == 0) {
                            ly_no_friend_request.setVisibility(View.VISIBLE);
                        }
                        myFriendsAdapter.notifyDataSetChanged();
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
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("user/getFriendList?"+"offset="+startCount+"&limit="+10+"&listType=request");
    }

}
