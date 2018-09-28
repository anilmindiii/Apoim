package com.apoim.activity.business;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.profile.EditProfileActivity;
import com.apoim.activity.payment_subscription.SubscriptionPayActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Plan;
import com.stripe.model.Subscription;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BusinessSubscriptionActivity extends AppCompatActivity {
    InsLoadingView loadingView;
    TextView tv_subscription;
    TextView skip;
    String from = "";
    ImageView iv_back;
    private TextView text;
    private String bizSubscriptionId = "";
    private String bizSubscriptionStatus = "";
    String planId = "plan_DK92aozpdTVwkC";
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_subscription);

        session = new Session(this);

        skip = findViewById(R.id.skip);
        tv_subscription = findViewById(R.id.tv_subscription);
        loadingView = findViewById(R.id.loadingView);
        iv_back = findViewById(R.id.iv_back);
        text = findViewById(R.id.text);

        if(getIntent().getStringExtra("from") != null){
            from = getIntent().getStringExtra("from");

            if(from.equals("settings")){
                skip.setVisibility(View.GONE);
                iv_back.setVisibility(View.VISIBLE);

            }
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusinessSubscriptionActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_subscription.getText().toString().equals("Cancel subscription")){
                    cancelSubscrition();
                }else {
                    Intent intent = new Intent(BusinessSubscriptionActivity.this, SubscriptionPayActivity.class);
                    intent.putExtra("paymentType", Constant.PayForBusinessSubscription);
                    intent.putExtra("from","settings");
                    startActivity(intent);
                }

            }
        });

    }


    private void cancelSubscrition() {
        loadingView.setVisibility(View.VISIBLE);
        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);

                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {

                        JSONObject jsonObject = object.getJSONObject("subscriptionDetail");
                        String bizSubscriptionId = jsonObject.getString("bizSubscriptionId");
                        String bizSubscriptionStatus = jsonObject.getString("bizSubscriptionStatus");

                        SignInInfo userDetail = session.getUser();
                        userDetail.userDetail.bizSubscriptionId = bizSubscriptionId;
                        userDetail.userDetail.bizSubscriptionStatus = bizSubscriptionStatus;
                        session.createSession(userDetail);

                        successAlertDialog(BusinessSubscriptionActivity.this,message);
                    }
                    else {
                        Utils.openAlertDialog(BusinessSubscriptionActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loadingView.setVisibility(View.GONE);
            }
        });
            service.callSimpleVolley("business/cancelSubscription", null);
    }

    public void successAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        bizSubscriptionId = session.getUser().userDetail.bizSubscriptionId;
        bizSubscriptionStatus = session.getUser().userDetail.bizSubscriptionStatus;

        if(bizSubscriptionId == null){
            bizSubscriptionId  = "";
        }

        if(bizSubscriptionStatus == null){
            bizSubscriptionStatus = "0";
        }

        BusinessSubscriptionActivity.GetPlan getPlan = new BusinessSubscriptionActivity.GetPlan();
        getPlan.execute();
    }

    class GetPlan extends AsyncTask<String, String, String> {
        String msg;
        int isVisibleButton;

        @Override
        protected String doInBackground(String... strings) {
            Stripe.apiKey = "sk_test_jVM872jPfk462GPwYDH7mr84";
            try {
                Plan plan = Plan.retrieve(planId);
                Log.d("plan", plan + "");

                if (!plan.equals("") && plan != null) {
                    String interval = plan.getInterval();
                    String plan_name = plan.getNickname();
                    String currency = plan.getCurrency();
                    long amount = plan.getAmount();

                    if (bizSubscriptionId.equals("") && bizSubscriptionStatus.equals("0")) {

                        isVisibleButton = 1;
                        msg = "Promote your business and users will get suggestion of your business when they create appointments and event";

                    } else if (!bizSubscriptionId.equals("") && bizSubscriptionStatus.equals("0")) {

                        isVisibleButton = 0;

                        if (Subscription.retrieve(bizSubscriptionId) != null) {
                            Subscription subscription = Subscription.retrieve(bizSubscriptionId);
                            long end_time = subscription.getCurrentPeriodEnd();

                            SimpleDateFormat sd1 = new SimpleDateFormat("dd MMMM yyyy");
                            try {
                                String date1 = sd1.format(new Date((Long) end_time*1000));

                                msg = "You are on a " + plan_name + " plan ending on " + date1 + " Your current plan is " + "$" + amount + " per " + interval + "You have canceled your current subscription plan and you will be downgraded to Free plan at the end of current billing cycle";

                            } catch (Exception e) {

                            }

                        }
                    } else {
                        if (Subscription.retrieve(bizSubscriptionId) != null) {
                            Subscription subscription = Subscription.retrieve(bizSubscriptionId);
                            long end_time = subscription.getCurrentPeriodEnd();

                            SimpleDateFormat sd1 = new SimpleDateFormat("dd MMMM yyyy");
                            try {
                                String date1 = sd1.format(new Date((Long) end_time*1000));

                                msg = "You are on a " + plan_name + " plan ending on " + date1 + ". Your Current plan is " + amount + " per " + interval + "";

                            } catch (Exception e) {

                            }

                            isVisibleButton = 2;
                        }

                    }
                }


            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            text.setText(msg);
            loadingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadingView.setVisibility(View.GONE);

            text.setText(msg);
            if (isVisibleButton == 0) {
                tv_subscription.setVisibility(View.GONE);
            } else if (isVisibleButton == 1) {
                tv_subscription.setVisibility(View.VISIBLE);
            } else if (isVisibleButton == 2) {
                tv_subscription.setVisibility(View.VISIBLE);
                tv_subscription.setText("Cancel subscription");
            }
        }
    }



}
