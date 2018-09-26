package com.apoim.activity.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.ApoimReviewAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.ApoimReviewInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventApoimReviewActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private InsLoadingView loading_view;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int endCount = 10;
    private int startCount = 0;
    private ArrayList<ApoimReviewInfo.ReviewListBean> reviewList;
    private ApoimReviewAdapter reviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_apoim_review);

        recycler = findViewById(R.id.recycler);
        ImageView iv_back = findViewById(R.id.iv_back);
        loading_view = findViewById(R.id.loading_view);

        reviewList  = new ArrayList<>();

        reviewAdapter = new ApoimReviewAdapter(reviewList,this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //loadNextDataFromApi();
            }
        };

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recycler.addOnScrollListener(scrollListener);
        recycler.setAdapter(reviewAdapter);

        getReviewList();
    }


    private void getReviewList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                   // notificationList.clear();

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        ApoimReviewInfo apoimReviewList = gson.fromJson(response, ApoimReviewInfo.class);
                        reviewList.addAll(apoimReviewList.reviewList);
                        reviewAdapter.agoDate(apoimReviewList.date);

                        reviewAdapter.notifyDataSetChanged();


                    }
                    else {
                        /*if(notificationList.size() == 0){
                            ly_no_notification_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_notification_found.setVisibility(View.GONE);
                        }*/
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
        service.callGetSimpleVolley("appointment/getAppointmentReview?offset="+startCount+"&limit="+endCount+"");
    }
}
