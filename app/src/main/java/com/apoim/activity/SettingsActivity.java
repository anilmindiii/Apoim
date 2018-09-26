package com.apoim.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.business.BusinessDetailsActivity;
import com.apoim.activity.business.BusinessSubscriptionActivity;
import com.apoim.activity.business.RegisterBusinessActivity;
import com.apoim.app.Apoim;
import com.apoim.groupchatwebrtc.db.QbUsersDbManager;
import com.apoim.groupchatwebrtc.services.CallService;
import com.apoim.groupchatwebrtc.util.QBResRequestExecutor;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.groupchatwebrtc.utils.UsersUtils;
import com.apoim.helper.Constant;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.apoim.app.Apoim.TAG;
import static org.webrtc.ContextUtils.getApplicationContext;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_notification_toggle;
    private ImageView iv_back;
    private Session session;
    private boolean isPaymentDone;
    String userId = "";
    private InsLoadingView loading_view;
    RelativeLayout ly_edit_profile, ly_logout, ly_change_password, ly_verification, ly_business_page, ly_subscription;
    private GetOtherProfileInfo otherProfileInfo;
    String typeNotification = "";
    private QBUser currentUser;
    SharedPrefsHelper sharedPrefsHelper;
    protected QBResRequestExecutor requestExecutor;
    private QbUsersDbManager dbManager;
    private ImageView iv_event_toggle,iv_apoim_toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout ly_payment = findViewById(R.id.ly_payment);
        RelativeLayout ly_about_us = findViewById(R.id.ly_about_us);
        RelativeLayout ly_privacy_policy = findViewById(R.id.ly_privacy_policy);
        RelativeLayout ly_tnc = findViewById(R.id.ly_tnc);
        iv_notification_toggle = findViewById(R.id.iv_notification_toggle);
        iv_back = findViewById(R.id.iv_back);
        loading_view = findViewById(R.id.loading_view);
        ly_edit_profile = findViewById(R.id.ly_edit_profile);
        ly_logout = findViewById(R.id.ly_logout);
        ly_change_password = findViewById(R.id.ly_change_password);
        ly_verification = findViewById(R.id.ly_verification);
        ly_business_page = findViewById(R.id.ly_business_page);
        ly_subscription = findViewById(R.id.ly_subscription);

        iv_apoim_toggle = findViewById(R.id.iv_apoim_toggle);
        iv_event_toggle = findViewById(R.id.iv_event_toggle);

        ly_subscription.setOnClickListener(this);
        ly_business_page.setOnClickListener(this);
        ly_verification.setOnClickListener(this);
        ly_privacy_policy.setOnClickListener(this);
        ly_about_us.setOnClickListener(this);
        ly_tnc.setOnClickListener(this);
        ly_payment.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ly_change_password.setOnClickListener(this);
        iv_notification_toggle.setOnClickListener(this);
        ly_edit_profile.setOnClickListener(this);
        ly_logout.setOnClickListener(this);
        iv_event_toggle.setOnClickListener(this);
        iv_apoim_toggle.setOnClickListener(this);

        session = new Session(this);
        otherProfileInfo = new GetOtherProfileInfo();

        if (getIntent().getSerializableExtra("otherProfileInfo") != null) {
            otherProfileInfo = (GetOtherProfileInfo) getIntent().getSerializableExtra("otherProfileInfo");
            typeNotification = getIntent().getStringExtra("typeNotification");

            if (typeNotification != null) {
                if (typeNotification.equals("id_verification")) {
                    Intent intent = new Intent(SettingsActivity.this, VerificationActivity.class);
                    intent.putExtra("typeNotification", "id_verification");
                    startActivity(intent);
                }

            }

            if (otherProfileInfo.UserDetail.appointmentType.equals("1")) {
                iv_apoim_toggle.setImageResource(R.drawable.ico_set_toggle_on);
            }
            else iv_apoim_toggle.setImageResource(R.drawable.ico_set_toggle_off);


            if (otherProfileInfo.UserDetail.eventType.equals("1")) {
                iv_event_toggle.setImageResource(R.drawable.ico_set_toggle_on);
            }
            else iv_event_toggle.setImageResource(R.drawable.ico_set_toggle_off);

        }


        userId = session.getUser().userDetail.userId;

        String isNotificationstatus = session.getUser().userDetail.isNotification;
        if (isNotificationstatus.equals("1")) {
            iv_notification_toggle.setImageResource(R.drawable.ico_set_toggle_on);
        } else {
            iv_notification_toggle.setImageResource(R.drawable.ico_set_toggle_off);
        }



        String socialId = session.getUser().userDetail.socialId;
        if (socialId.equals("")) {
            ly_change_password.setVisibility(View.VISIBLE);
        } else ly_change_password.setVisibility(View.GONE);

        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        requestExecutor = Apoim.getInstance().getQbResRequestExecutor();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        currentUser = sharedPrefsHelper.getQbUser();
    }

    @Override
    public void onClick(View view) {
        SignInInfo info = session.getUser();
        switch (view.getId()) {
            case R.id.ly_payment:
                if (info.userDetail.bankAccountStatus.equals("1")) {
                    Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
                    intent.putExtra("updateBankInfo", "updateBankInfo");
                    startActivity(intent);

                } else if (info.userDetail.bankAccountStatus.equals("0")) {
                    Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
                    intent.putExtra("isPaymentDone", isPaymentDone);
                    startActivityForResult(intent, Constant.AddBankAccRequestCode);
                }

                break;
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.ly_logout:
                logOutQuickBlock();
                logout();
                break;

            case R.id.ly_tnc: {
                Intent intent = new Intent(SettingsActivity.this, TnCAndPrivacyActivity.class);
                intent.putExtra("screenType", "term_condition");
                startActivity(intent);
            }
            break;

            case R.id.ly_privacy_policy: {
                Intent intent = new Intent(SettingsActivity.this, TnCAndPrivacyActivity.class);
                intent.putExtra("screenType", "privacy_policy");
                startActivity(intent);
            }
            break;

            case R.id.ly_about_us: {
                Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.ly_change_password:
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.ly_verification:
                intent = new Intent(SettingsActivity.this, VerificationActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_notification_toggle:
                String isNotificationstatus = session.getUser().userDetail.isNotification;

                if (isNotificationstatus.equals("1")) {
                    changeNotificationStatus("0");
                } else {
                    changeNotificationStatus("1");
                }
                break;

            case R.id.ly_edit_profile: {
                intent = new Intent(SettingsActivity.this, NotificationActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ly_business_page: {
                intent = new Intent(SettingsActivity.this, RegisterBusinessActivity.class);
                if (otherProfileInfo.UserDetail.isBusinessAdded.equals("0")) {
                    intent.putExtra("type", "addBussiness");
                } else {
                    intent.putExtra("type", "updateBussiness");
                }

                startActivityForResult(intent,122);
                break;
            }
            case R.id.ly_subscription: {
                intent = new Intent(SettingsActivity.this, SelectPaymentTypeActivity.class);
                intent.putExtra("mapPayment", otherProfileInfo.UserDetail.mapPayment);
                intent.putExtra("showTopPayment", otherProfileInfo.UserDetail.showTopPayment);
                intent.putExtra("isBusinessAdded", otherProfileInfo.UserDetail.isBusinessAdded);
                startActivity(intent);
                break;
            }
            case R.id.iv_apoim_toggle:{

                if(otherProfileInfo.UserDetail.appointmentType.equals("1")){ // 1 is the case of paid
                    updateApoimOrEventStatus("appointmentType","2");
                }else {

                    if (info.userDetail.bankAccountStatus.equals("1")) {
                        updateBankInfoDialog(SettingsActivity.this, getString(R.string.do_you_wanna_update_bank_info),"appointmentType");
                    } else if (info.userDetail.bankAccountStatus.equals("0")) {
                        AddBankAccDialog(SettingsActivity.this, getString(R.string.need_add_bank),"appointmentType");
                    }
                }

                break;
            }

            case R.id.iv_event_toggle:{

                if(otherProfileInfo.UserDetail.eventType.equals("1")){ // 1 is the case of paid
                    updateApoimOrEventStatus("eventType","2");
                }else {
                    if (info.userDetail.bankAccountStatus.equals("1")) {
                        updateBankInfoDialog(SettingsActivity.this, getString(R.string.do_you_wanna_update_bank_info),"eventType");
                    } else if (info.userDetail.bankAccountStatus.equals("0")) {
                        AddBankAccDialog(SettingsActivity.this, getString(R.string.need_add_bank),"eventType");
                    }

                }

            }
        }
    }

    public void AddBankAccDialog(Context context, String message,String type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
                intent.putExtra("isPaymentDone", isPaymentDone);
                if(type.equals("appointmentType")){
                    startActivityForResult(intent, Constant.AddBankAccRequestCode);
                }else {
                    startActivityForResult(intent, Constant.EventPayRequestCode);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateBankInfoDialog(Context context, String message,String type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Bank info");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if(type.equals("appointmentType")){
                    updateApoimOrEventStatus("appointmentType","1");
                }else {
                    updateApoimOrEventStatus("eventType","1");
                }

                Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
                intent.putExtra("updateBankInfo", "updateBankInfo");
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(type.equals("appointmentType")){
                    updateApoimOrEventStatus("appointmentType","1");
                }else {
                    updateApoimOrEventStatus("eventType","1");
                }


                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(data != null){
                isPaymentDone = data.getBooleanExtra("isPaymentDone", false);
                if (isPaymentDone) {
                    SignInInfo info = session.getUser();
                    info.userDetail.bankAccountStatus = "1";
                    session.createSession(info);

                    if(requestCode == Constant.AddBankAccRequestCode){
                        updateApoimOrEventStatus("appointmentType","1");
                    }else if(requestCode == Constant.EventPayRequestCode){
                        updateApoimOrEventStatus("eventType","1");
                    }
                }
            }


            /*if (requestCode == 178) {
                otherProfileInfo = (GetOtherProfileInfo) data.getSerializableExtra("otherProfileInfo");
                final GetOtherProfileInfo.UserDetailBean userDetail = otherProfileInfo.userDetail;
                otherProfileInfo.UserDetail = userDetail;
            }*/

            if(requestCode == 122){
                otherProfileInfo.UserDetail.isBusinessAdded = "1";
            }


        }
    }

    private void changeNotificationStatus(final String notification_status) {
        iv_notification_toggle.setEnabled(false);
        Map<String, String> param = new HashMap<>();
        param.put("status", notification_status);

        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    SignInInfo inInfo = session.getUser();

                    if (status.equals("success")) {

                        if (notification_status.equals("1")) {
                            inInfo.userDetail.isNotification = "1";
                            iv_notification_toggle.setImageResource(R.drawable.ico_set_toggle_on);
                            FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(userId).child(Constant.isNotification).setValue("1");

                        } else if (notification_status.equals("0")) {
                            inInfo.userDetail.isNotification = "0";
                            iv_notification_toggle.setImageResource(R.drawable.ico_set_toggle_off);
                            FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(userId).child(Constant.isNotification).setValue("0");

                        }


                    } else {
                        if (notification_status.equals("1")) {
                            inInfo.userDetail.isNotification = "1";
                        } else {
                            inInfo.userDetail.isNotification = "0";
                        }

                        Utils.openAlertDialog(SettingsActivity.this, message);
                    }
                    session.createSession(inInfo);
                    iv_notification_toggle.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                    iv_notification_toggle.setEnabled(true);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                iv_notification_toggle.setEnabled(true);
            }
        });
        service.callSimpleVolley("user/notificationStatus", param);
    }


    private void logout() {
        ly_logout.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(SettingsActivity.this, "LOGOUT", new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                ly_logout.setEnabled(true);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    Log.e("FRIEND LIST RESPONSE", response.toString());
                    if (status.equals("success")) {
                        String f_id = session.getUser().userDetail.userId;
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("dkjshfs");
                        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(f_id).child("firebaseToken").setValue("");
                        Utils.goToOnlineStatus(SettingsActivity.this, Constant.offline);
                        session.logout();
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
                ly_logout.setEnabled(true);
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("user/logout");
    }

    private void logOutQuickBlock() {
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();

    }

    private void startLogoutCommand() {
        CallService.logout(SettingsActivity.this);
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(SettingsActivity.this);
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        if (currentUser != null)
            requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    Log.d(TAG, "Current user was deleted from QB");
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "Current user wasn't deleted from QB " + e);
                }
            });
    }

    void updateApoimOrEventStatus(String key,String value) {
        loading_view.setVisibility(View.VISIBLE);
        HashMap params = new HashMap<>();
        params.put(key, value);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                try {

                    JSONObject result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        Gson gson = new Gson();
                        SignInInfo signInInfo = gson.fromJson(String.valueOf(response), SignInInfo.class);
                        GetOtherProfileInfo otherProfileInfoLocal = gson.fromJson(String.valueOf(response), GetOtherProfileInfo.class);

                        if(key.equals("appointmentType")){
                            otherProfileInfo.UserDetail.appointmentType = otherProfileInfoLocal.userDetail.appointmentType;

                            if (otherProfileInfo.UserDetail.appointmentType.equals("1")) {
                                iv_apoim_toggle.setImageResource(R.drawable.ico_set_toggle_on);

                            }
                            else {
                                iv_apoim_toggle.setImageResource(R.drawable.ico_set_toggle_off);
                            }

                        }else {
                            otherProfileInfo.UserDetail.eventType = otherProfileInfoLocal.userDetail.eventType;

                            if (otherProfileInfo.UserDetail.eventType.equals("1")) {
                                iv_event_toggle.setImageResource(R.drawable.ico_set_toggle_on);
                            }
                            else iv_event_toggle.setImageResource(R.drawable.ico_set_toggle_off);
                        }







                    }
                    else {
                        Utils.openAlertDialog(SettingsActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callMultiPartApi("user/updateProfile", params, null);
    }
}
