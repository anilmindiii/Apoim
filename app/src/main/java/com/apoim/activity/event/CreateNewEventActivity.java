package com.apoim.activity.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.fragment.eventFragments.FirstScreenFragment;
import com.apoim.helper.Constant;
import com.apoim.modal.CurrencyInfo;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateNewEventActivity extends AppCompatActivity {
    ImageView iv_back;
    public static boolean isForUpdateEvent;
    private InsLoadingView loadingView;
    private Session session;
    private String eventId = null;
    private Bitmap bitmap;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        iv_back = findViewById(R.id.iv_back);
        TextView profile_action_bar = findViewById(R.id.profile_action_bar);
        loadingView = findViewById(R.id.loadingView);

        session = new Session(this);

        if (getIntent().getStringExtra(Constant.editEvent) != null) {
            String editEvent = getIntent().getStringExtra(Constant.editEvent);
            eventId = getIntent().getStringExtra("eventId");

            if (editEvent.equals(Constant.editEvent)) {
                profile_action_bar.setText("Edit Event");
               // tv_invite_n_create_friend.setVisibility(View.GONE);
                //tv_update_event.setVisibility(View.VISIBLE);
                myEventRequestEvent(eventId);
            }
            isForUpdateEvent = true;
        }else {
            isForUpdateEvent = false;
            session.createEventInfo(new EventDetailsInfo());
        }


        addFragment(FirstScreenFragment.newInstance(eventId), false, R.id.event_fragment_place);


        iv_back.setOnClickListener(view -> onBackPressed());
    }

    private void myEventRequestEvent(final String eventId) {
        loadingView.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        EventDetailsInfo detailsInfo = gson.fromJson(response, EventDetailsInfo.class);

                        session.createEventInfo(detailsInfo);

                        /*rb_private.setClickable(false);
                        rb_public.setClickable(false);
                        rb_paid.setClickable(false);
                        rb_free.setClickable(false);
                        ed_user_limite.setEnabled(false);

                        rb_male.setClickable(false);
                        rb_female.setClickable(false);
                        rb_both.setClickable(false);


                        if (detailsInfo.Detail.privacy.equals("Public")) {
                            rb_public.setChecked(true);
                            privacy = "1";
                        } else if (detailsInfo.Detail.privacy.equals("Private")) {

                            rb_private.setChecked(true);
                            privacy = "2";
                        }

                        if (detailsInfo.Detail.eventUserType.equals("Male")) {
                            rb_male.setChecked(true);
                        } else if (detailsInfo.Detail.eventUserType.equals("Female")) {
                            rb_female.setChecked(true);
                        } else if (detailsInfo.Detail.eventUserType.equals("Both")) {
                            rb_both.setChecked(true);
                        }

                        if (detailsInfo.Detail.payment.equals("Paid")) {
                            payment = "1";
                            rb_paid.setChecked(true);
                            ly_paid_view.setVisibility(View.VISIBLE);
                            ly_currency.setEnabled(false);
                            ed_amount.setEnabled(false);
                            ed_user_limite.setEnabled(false);

                        } else if (detailsInfo.Detail.payment.equals("Free")) {
                            payment = "2";
                            rb_free.setChecked(true);
                        }
                        eventUserType = detailsInfo.Detail.eventUserType;

                        ed_event_name.setText(detailsInfo.Detail.eventName);
                        tv_location.setText(detailsInfo.Detail.eventPlace);

                        ed_user_limite.setText(detailsInfo.Detail.userLimit);

                        ed_amount.setText(detailsInfo.Detail.eventAmount);

                        for (CurrencyInfo info : currencyList) {
                            if (info.code.equals(detailsInfo.Detail.currencyCode)) {
                                tv_currency_name.setText(info.name_plural);
                            }
                        }*/

                    } else {

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
        service.callGetSimpleVolley("event/getEventDetail?eventId=" + eventId + "&detailType=" + "myEvent" + "");
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                bitmap = ImagePicker.getImageFromResult(CreateNewEventActivity.this, requestCode, resultCode, data);
            }
        }
    }




    @Override
    public void onBackPressed() {

        Fragment fr = fragmentManager.findFragmentById(R.id.event_fragment_place);
        if(fr!=null){
            Log.e("fragment=", fr.getClass().getSimpleName());

            if( fr.getClass().getSimpleName().equals("FourthScreenFragment")){
                finish();
            }
        }

        super.onBackPressed();
    }

}
