package com.apoim.activity.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.JoinedEventAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.JoinedEventInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JoinedEventActivity extends AppCompatActivity {
    InsLoadingView loading_view;
    private String eventType = "joined";
    private String eventId = "";
    private JoinedEventAdapter adapter;
    private RecyclerView recycler;
    private ImageView iv_back;
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    private LinearLayout ly_no_friend_found;
    String from = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isExpaireDate;
    private int startCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_event);
        init();

        if(getIntent().getExtras() != null){
            eventId = getIntent().getStringExtra("eventId");
            from = getIntent().getStringExtra("from");
            isExpaireDate = getIntent().getBooleanExtra("isExpaireDate",false);

            if(from.equals("myEvent")){

            }else if(from.equals("eventRequest")){

            }
        }

        joinedList = new ArrayList<>();
        adapter = new JoinedEventAdapter(this,joinedList,loading_view,ly_no_friend_found,from,isExpaireDate);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recycler.addOnScrollListener(scrollListener);
        recycler.setAdapter(adapter);

        joinedEventList(eventId,eventType);
    }


    private void loadNextDataFromApi() {
        startCount = startCount+10;
        joinedEventList(eventId,eventType);
    }


    private void init() {
        loading_view = findViewById(R.id.loading_view);
        recycler = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        ly_no_friend_found = findViewById(R.id.ly_no_friend_found);
    }

    private void joinedEventList(final String eventId,final String eventType) {
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
                        JoinedEventInfo joinedEventInfo = gson.fromJson(response,JoinedEventInfo.class);
                        joinedList.addAll(joinedEventInfo.List);
                        adapter.notifyDataSetChanged();
                    } else {
                        if(joinedList.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
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
        service.callGetSimpleVolley("event/getEventMemberList?offset="+startCount+"&limit=10&memberType="+eventType+"&eventId="+eventId+"");
    }
}
