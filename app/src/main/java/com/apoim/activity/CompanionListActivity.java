package com.apoim.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.payment_subscription.SubscriptionPayActivity;
import com.apoim.adapter.apoinment.CompanionListAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetCompId;
import com.apoim.modal.CompanionInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompanionListActivity extends AppCompatActivity {
    private InsLoadingView loading_view;
    private String eventType = "companion";
    private String eventId = "";
    private ArrayList<CompanionInfo.ListBean> compList;
    private RecyclerView recyclerView;
    private ImageView iv_back;
    private LinearLayout ly_no_friend_found;
    private String from = "";
    private CompanionListAdapter adapter;
    private boolean isLimitOver;
    private  boolean isComPaymentDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_list);

        compList = new ArrayList<>();
        loading_view = findViewById(R.id.loading_view);
        recyclerView = findViewById(R.id.recyclerView);
        iv_back = findViewById(R.id.iv_back);
        ly_no_friend_found = findViewById(R.id.ly_no_friend_found);

        if(getIntent().getExtras() != null){
            eventId = getIntent().getStringExtra("eventId");
            from = getIntent().getStringExtra("from");
            isLimitOver = getIntent().getBooleanExtra("isLimitOver",false);

            if(from.equals("myEvent")){
                from = "request";
            }else if(from.equals("eventRequest")){
                //from = "myEvent";
                from = "request";
            }
        }

        adapter = new CompanionListAdapter(this,isLimitOver, compList, new GetCompId() {
            @Override
            public void companionId(String eventId,String compId,String eventMem_Id) {

                Intent intent = new Intent(CompanionListActivity.this, SubscriptionPayActivity.class);
                intent.putExtra("eventId",eventId);
                intent.putExtra("compId",compId);
                intent.putExtra("eventMem_Id",eventMem_Id);
                intent.putExtra("paymentType",Constant.PayForcompainion);// 4 for compainion pay
                startActivity(intent);

                //paymentTask(eventId,compId,eventMem_Id);
            }
        });
        recyclerView.setAdapter(adapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        companionMemberList(eventId,eventType);
    }

    private void companionMemberList(final String eventId, final String eventType) {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                compList.clear();

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        CompanionInfo companionInfo = gson.fromJson(response,CompanionInfo.class);
                        compList.addAll(companionInfo.List);
                        adapter.notifyDataSetChanged();
                    } else {
                        if(compList.size() == 0){
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
        service.callGetSimpleVolley("event/getEventMemberList?offset=0&limit=10&memberType="+eventType+"&eventId="+eventId);
    }

}
