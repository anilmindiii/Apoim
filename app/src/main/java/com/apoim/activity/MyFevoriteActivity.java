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
import com.apoim.adapter.apoinment.MyFevoriteAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.MyFavoriteListInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFevoriteActivity extends AppCompatActivity {
    MyFevoriteAdapter myFevoriteAdapter;
    RecyclerView recycler;
    InsLoadingView loading_view;
    MyFavoriteListInfo favoriteListInfo;
    LinearLayout ly_no_favorite_found;
    ImageView iv_back;
    private ArrayList<MyFavoriteListInfo.FavoriteDataBean> favoriteList;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int startCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fevorite);

        iv_back = findViewById(R.id.iv_back);
        recycler = findViewById(R.id.recycler);
        loading_view = findViewById(R.id.loading_view);
        ly_no_favorite_found = findViewById(R.id.ly_no_favorite_found);

        favoriteListInfo = new MyFavoriteListInfo();
        favoriteList = new ArrayList<>();

        myFevoriteAdapter = new MyFevoriteAdapter(this, favoriteList,ly_no_favorite_found);


        showFavouriteList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recycler.addOnScrollListener(scrollListener);
        recycler.setAdapter(myFevoriteAdapter);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadNextDataFromApi() {
        startCount = startCount+10;
        showFavouriteList();
    }


    private void showFavouriteList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    Log.e("FAVOURITE LIST RESPONSE", response.toString());


                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        favoriteListInfo = gson.fromJson(response, MyFavoriteListInfo.class);
                        favoriteList.addAll(favoriteListInfo.favoriteList);
                        myFevoriteAdapter.notifyDataSetChanged();
                        if (favoriteList.size() == 0) {
                            ly_no_favorite_found.setVisibility(View.VISIBLE);
                        }

                    } else {
                        if (favoriteList.size() == 0) {
                            ly_no_favorite_found.setVisibility(View.VISIBLE);
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
        service.callGetSimpleVolley("user/getMyFavorite?offset="+startCount+"&limit=10");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
