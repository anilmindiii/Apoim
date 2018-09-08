package com.apoim.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.apoim.modal.MyFavoriteListInfo;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyFriendsActivity extends AppCompatActivity {
    private MyFriendsAdapter myFriendsAdapter;
    private RecyclerView recycler;
    private InsLoadingView loading_view;
    private TextView tv_request_count;
    private MyFriendListInfo friendListInfo;
    private LinearLayout ly_no_friend_found;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private ImageView iv_back,iv_friend_request;
    private int startCount = 0;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        tv_request_count = findViewById(R.id.tv_request_count);
        iv_friend_request = findViewById(R.id.iv_friend_request);
        recycler = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        loading_view = findViewById(R.id.loading_view);
        ly_no_friend_found = findViewById(R.id.ly_no_friend_found);

        friendList = new ArrayList<>();
        friendListInfo = new MyFriendListInfo();

        myFriendsAdapter = new MyFriendsAdapter(this,friendList,loading_view,ly_no_friend_found,"friend");

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

        //showFriendList();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MyFriendsActivity.this,FriendReqListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadNextDataFromApi() {
        startCount = startCount+10;
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
                        tv_request_count.setText(friendListInfo.requestCount+"");


                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
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
        service.callGetSimpleVolley("user/getFriendList?offset="+startCount+"&limit=10&listType=friend");
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCount = 0;
        friendList.clear();
        showFriendList();
    }
}
