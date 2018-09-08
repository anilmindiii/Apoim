package com.apoim.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.helper.Validation;
import com.apoim.modal.Country;
import com.apoim.modal.PreRegistrationInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by abc on 2/12/2018.
 */

public class EmailVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView enter_contact_button;
    private ImageButton register_back;
    private EditText enter_contact_no;
    private Session session;
    private PreRegistrationInfo reg_info;
    private String con_phone_code;
    private RelativeLayout select_contact_layout;
    private InsLoadingView loadingView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        session = new Session(EmailVerifyActivity.this, this);

        select_contact_layout = findViewById(R.id.select_contact_layout);
        enter_contact_no = findViewById(R.id.enter_contact_no);
        loadingView = findViewById(R.id.loadingView);

        register_back = findViewById(R.id.register_back);
        enter_contact_button = findViewById(R.id.enter_contact_button);

        register_back.setVisibility(View.VISIBLE);

        register_back.setOnClickListener(this);
        enter_contact_button.setOnClickListener(this);

        PreRegistrationInfo preRegistrationInfo = session.getRegistrationInfo();

        if (preRegistrationInfo.socialType != null) {
            if (preRegistrationInfo.socialType.equals("facebook")) {
                enter_contact_no.setText(preRegistrationInfo.email);
                enter_contact_no.setSelection(enter_contact_no.length());

            } else if (preRegistrationInfo.socialType.equals("gmail")) {
                enter_contact_no.setText(preRegistrationInfo.email);
                enter_contact_no.setSelection(enter_contact_no.length());
            }
        }
    }


    public boolean isValidData() {
        Validation v = new Validation();

        if(!v.isNullValue(enter_contact_no.getText().toString().trim())){
            Utils.openAlertDialog(EmailVerifyActivity.this, getResources().getString(R.string.alert_email_null));
            return false;
        }
        else if (!v.isEmailValid(enter_contact_no.getText().toString().trim())) {
            Utils.openAlertDialog(EmailVerifyActivity.this, getResources().getString(R.string.alert_invalid_email));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter_contact_button:
                if (Utils.IsNetPresent(EmailVerifyActivity.this)) {
                    if (isValidData()) {
                        selectContactNoTask(enter_contact_no.getText().toString());
                    }
                }else {
                    Utils.openAlertDialog(EmailVerifyActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.register_back:
                onBackPressed();
                break;
        }
    }

    private void selectContactNoTask(final String email) {
        loadingView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("email", email);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        String email = jsonObject.getString("email");
                        String code = jsonObject.getString("code");

                        PreRegistrationInfo reg_info = session.getRegistrationInfo();
                        reg_info.contactNo = "";
                        reg_info.countryCode = "";
                        reg_info.email =email;
                        session.createRegistrationInfo(reg_info);
                        successDialog(EmailVerifyActivity.this,message,code,email);


                    } else {
                        Utils.openAlertDialog(EmailVerifyActivity.this, message);
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
        service.callSimpleVolley("verifyEmail", map);
    }

    public  void successDialog(Context context, String message, String code, String email) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Intent intent = new Intent(EmailVerifyActivity.this, ConfirmationCodeActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("email", email);

                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }




    /*private boolean isValidemail(String email) {
        Validation validation = new Validation();

        if(!validation.isEmailValid(email)){
            return true;
        }

        return false;
    }



    public void openVerifyContactDialog(String country_code, String contact_no) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EmailVerifyActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Info");
        builder.setMessage("NUMBER CONFIRMATION:\n" + country_code + "  " + contact_no + "\nIs this phone number correct?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isValidemail(enter_contact_no.getText().toString())) {
                    selectContactNoTask(enter_contact_no.getText().toString(), enter_contact_no.getText().toString());
                } else {
                    Utils.openAlertDialog(EmailVerifyActivity.this, getResources().getString(R.string.alert_invalid_contact));
                }

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(0, R.anim.back_slide);
    }
}
