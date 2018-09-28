package com.apoim.activity.sign_signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 2/12/2018.
 */

public class ConfirmationCodeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton register_back;
    TextView confirm_code_button;
    RelativeLayout confirm_code_layout;
    EditText enter_confirm_code;
    String code, email, resend_otp_code;
    TextView resend_code;
    InsLoadingView loadingView;
    String contact_no,country_code;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);
        loadingView = findViewById(R.id.loadingView__);

        if (getIntent().getStringExtra("code") != null) {
            code = getIntent().getStringExtra("code");
            email = getIntent().getStringExtra("email");

            country_code = getIntent().getStringExtra("country_code");
            contact_no = getIntent().getStringExtra("contact_no");
        }

        initView();
        register_back.setVisibility(View.VISIBLE);
        confirm_code_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Constant.hideSoftKeyboard(ConfirmationCodeActivity.this);
                return false;
            }
        });
        register_back.setOnClickListener(this);
        confirm_code_button.setOnClickListener(this);
        resend_code.setOnClickListener(this);
    }

    private void initView() {
        confirm_code_layout = findViewById(R.id.confirm_code_layout);
        register_back = findViewById(R.id.register_back);
        confirm_code_button = findViewById(R.id.confirm_code_button);
        enter_confirm_code = findViewById(R.id.enter_confirm_code);
        resend_code = findViewById(R.id.resend_code);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back:
                onBackPressed();
                break;

            case R.id.confirm_code_button:
                if (isValidData()) {
                    if (code != null) {
                        if (code.equals(enter_confirm_code.getText().toString())) {

                            if(country_code != null && contact_no != null){
                                otpVerifiedTask();
                            }else {
                                startActivity(new Intent(ConfirmationCodeActivity.this, CreateAccountActivity.class));
                                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            }

                        } else {
                            Utils.openAlertDialog(ConfirmationCodeActivity.this, getResources().getString(R.string.alert_invalid_confirm_code));
                        }
                    } else {
                        Utils.openAlertDialog(ConfirmationCodeActivity.this, getResources().getString(R.string.alert_invalid_confirm_code));
                    }
                }

                break;
            case R.id.resend_code:
                if (Utils.IsNetPresent(ConfirmationCodeActivity.this)) {
                    resendOtpTask();
                } else {
                    Utils.openAlertDialog(ConfirmationCodeActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

        }
    }

    private void resendOtpTask() {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        if(country_code != null && contact_no != null){
            map.put("countryCode", country_code);
            map.put("contactNo", contact_no);
        }
        else map.put("email", email);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        Utils.openAlertDialog(ConfirmationCodeActivity.this, message);
                        code = jsonObject.getString("code");
                        if(country_code != null && contact_no != null){
                            finish();
                        }
                    } else {
                        Utils.openAlertDialog(ConfirmationCodeActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });

        if(country_code != null && contact_no != null){
            service.callSimpleVolley("user/verifyNo", map);
        }
        else service.callSimpleVolley("verifyEmail", map);
    }

    private void otpVerifiedTask() {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("otpVerified", "1");

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
                        setResult(RESULT_OK);
                        finish();

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
        service.callSimpleVolley("user/updateProfile", map);

    }

    private boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(enter_confirm_code.getText().toString().trim())) {
            Utils.openAlertDialog(ConfirmationCodeActivity.this, getResources().getString(R.string.alert_confirm_code_null));
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(0, R.anim.back_slide);
    }

}
