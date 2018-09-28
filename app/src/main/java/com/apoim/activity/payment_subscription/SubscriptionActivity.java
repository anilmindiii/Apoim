package com.apoim.activity.payment_subscription;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.apoim.R;
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


import static com.apoim.activity.payment_subscription.SubscriptionPayActivity.paymentType;

public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_subscription;
    private Session session;
    private String stripeCustomerId = "";
    private String subscriptionId = "";
    private String subscriptionStatus = "";
    private TextView text;
    private InsLoadingView loadingView;
    private String planId = "plan_D78Fso1yxuQq2b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        session = new Session(this);

        stripeCustomerId = session.getUser().userDetail.stripeCustomerId;

        iv_back = findViewById(R.id.iv_back);

        loadingView = findViewById(R.id.loadingView);
        tv_subscription = findViewById(R.id.tv_subscription);
        text = findViewById(R.id.text);

        iv_back.setOnClickListener(this);
        tv_subscription.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        subscriptionId = session.getUser().userDetail.subscriptionId;
        subscriptionStatus = session.getUser().userDetail.subscriptionStatus;

        GetPlan getPlan = new GetPlan();
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

                    if (subscriptionId.equals("") && subscriptionStatus.equals("0")) {

                        isVisibleButton = 1;
                        msg = "You are on a free plan. Please upgrade plan to get full access";

                    } else if (!subscriptionId.equals("") && subscriptionStatus.equals("0")) {

                        isVisibleButton = 0;

                        if (Subscription.retrieve(subscriptionId) != null) {
                            Subscription subscription = Subscription.retrieve(subscriptionId);
                            long end_time = subscription.getCurrentPeriodEnd();

                            SimpleDateFormat sd1 = new SimpleDateFormat("dd MMMM yyyy");
                            try {
                                String date1 = sd1.format(new Date((Long) end_time*1000));

                                msg = "You are on a " + plan_name + " plan ending on " + date1 + " Your current plan is " + "$" + amount + " per " + interval + "You have canceled your current subscription plan and you will be downgraded to Free plan at the end of current billing cycle";

                            } catch (Exception e) {

                            }

                            //text.setText(msg);
                        }
                    } else {
                        if (Subscription.retrieve(subscriptionId) != null) {
                            Subscription subscription = Subscription.retrieve(subscriptionId);
                            long end_time = subscription.getCurrentPeriodEnd();

                            SimpleDateFormat sd1 = new SimpleDateFormat("dd MMMM yyyy");
                            try {
                                String date1 = sd1.format(new Date((Long) end_time*1000));

                                msg = "You are on a " + plan_name + " plan ending on " + date1 + ". Your Current plan is " + amount + " per " + interval + "";

                            } catch (Exception e) {

                            }
                            //text.setText(msg);
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
                        String subscriptionId = jsonObject.getString("subscriptionId");
                        String subscriptionStatus = jsonObject.getString("subscriptionStatus");
                        String customerId = jsonObject.getString("customerId");

                        SignInInfo userDetail = session.getUser();
                        userDetail.userDetail.subscriptionId = subscriptionId;
                        userDetail.userDetail.subscriptionStatus = subscriptionStatus;
                        userDetail.userDetail.stripeCustomerId = customerId;
                        session.createSession(userDetail);

                        successAlertDialog(SubscriptionActivity.this,message);
                    }
                    else {
                        Utils.openAlertDialog(SubscriptionActivity.this, message);
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
        service.callSimpleVolley("payment/cancelSubscription", null);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.tv_subscription: {

                if(tv_subscription.getText().toString().equals("Cancel subscription")){
                    cancelSubscrition();
                }else {
                    Intent intent = new Intent(this, SubscriptionPayActivity.class);
                    intent.putExtra(paymentType, Constant.PayForTSubscription);// 3 for subscription for map
                    startActivity(intent);
                }
                break;
            }
        }
    }
}
