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
import com.apoim.adapter.apoinment.InvitedEventListAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.InvitedListInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvidedEventActivity extends AppCompatActivity {
    private InsLoadingView loading_view;
    private String eventType = "invited";
    private String eventId = "";
    private ArrayList<InvitedListInfo.ListBean> listBeans;
    private RecyclerView recyclerView;
    private InvitedEventListAdapter adapter;
    private ImageView iv_back;
    private LinearLayout ly_no_friend_found;
    private String from = "";
    private  boolean isExpaireDate;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int startCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invided_event);
        loading_view = findViewById(R.id.loading_view);
        recyclerView = findViewById(R.id.recyclerView);
        ly_no_friend_found = findViewById(R.id.ly_no_friend_found);
        iv_back = findViewById(R.id.iv_back);
        listBeans = new ArrayList<>();

        if(getIntent().getExtras() != null){
            eventId = getIntent().getStringExtra("eventId");
            from = getIntent().getStringExtra("from");
            isExpaireDate = getIntent().getBooleanExtra("isExpaireDate",false);

            if(from.equals("myEvent")){
                from = "request";
            }else if(from.equals("eventRequest")){
                from = "myEvent";
            }
        }

        adapter = new InvitedEventListAdapter(this,listBeans,loading_view,ly_no_friend_found,isExpaireDate);

        invidedMemberList(eventId,eventType,from);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(adapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadNextDataFromApi() {
        startCount = startCount+10;
        invidedMemberList(eventId,eventType,from);
    }

    private void invidedMemberList(final String eventId, final String eventType ,final String request) {
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
                        InvitedListInfo  invitedListInfo = gson.fromJson(response,InvitedListInfo.class);
                        listBeans.addAll(invitedListInfo.List);
                        adapter.notifyDataSetChanged();
                    } else {
                        if(listBeans.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else  ly_no_friend_found.setVisibility(View.GONE);

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
        service.callGetSimpleVolley("event/getEventMemberList?offset="+startCount+"&limit=10&memberType="+eventType+"&eventId="+eventId+"&type="+request+"");
    }
}
