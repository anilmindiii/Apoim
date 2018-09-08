package com.apoim.activity.business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.modal.BussinessInfo;
import com.apoim.modal.NotificationInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessDetailsActivity extends AppCompatActivity {
    private InsLoadingView loading_view;
    private ImageView iv_back,iv_buss_img,iv_map_img;
    private TextView tv_buss_name,tv_buss_address;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        loading_view = findViewById(R.id.loading_view);
        iv_back = findViewById(R.id.iv_back);
        tv_buss_name = findViewById(R.id.tv_buss_name);
        tv_buss_address = findViewById(R.id.tv_buss_address);
        iv_buss_img = findViewById(R.id.iv_buss_img);
        iv_map_img = findViewById(R.id.iv_map_img);

        userId = getIntent().getStringExtra("userId");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getBussinessDetails();
    }


    private void getBussinessDetails() {
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
                        BussinessInfo bussinessInfo = gson.fromJson(response, BussinessInfo.class);

                        setData(bussinessInfo.businessDetail);

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
        if(userId != null){ // for other business info
            service.callGetSimpleVolley("business/getBusinessDetail?userId="+userId+"");
        }else {
            service.callGetSimpleVolley("business/getBusinessDetail");
        }


    }

    private void setData(BussinessInfo.BusinessDetailBean businessDetail) {
        tv_buss_name.setText(businessDetail.businessName);
        tv_buss_address.setText(Utils.convertUTF8ToStringSetText(businessDetail.businessAddress));
        Glide.with(BusinessDetailsActivity.this).load(businessDetail.businessImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image))
                .into(iv_buss_img);

        openGoogleMap(businessDetail);
    }

    private void openGoogleMap(BussinessInfo.BusinessDetailBean businessDetail) {
        // val API_KEY = "AIzaSyBnFGTrGe8dJKMnrcinn1edleHCB_yZI5U"
        String API_KEY = "AIzaSyDI-QUWEEWFiV1W90w4PW2UWpIt04_DsmA";
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + "&zoom=13&size=1280x600&maptype=roadmap" +
                "&markers=color:red%7Clabel:S%7C" + businessDetail.businesslat + "," + businessDetail.businesslong + "&key=" + API_KEY;





        Glide.with(BusinessDetailsActivity.this).load(url).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image))
                .into(iv_map_img);
    }
}
