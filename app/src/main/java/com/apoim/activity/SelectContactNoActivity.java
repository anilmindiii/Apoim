package com.apoim.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
 * Created by anil on 2/12/2018.
 */

public class SelectContactNoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView enter_contact_button;
    private ImageButton register_back;
    private List<Country> countries;
    private TextView country_code_text;
    private LinearLayout spinner_country_code;
    private EditText enter_contact_no;
    private String country_code;
    private Session session;
    private PreRegistrationInfo reg_info;
    private String con_phone_code;
    private RelativeLayout select_contact_layout;
    private InsLoadingView loadingView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact_no);

        session = new Session(SelectContactNoActivity.this, this);

        select_contact_layout = findViewById(R.id.select_contact_layout);
        country_code_text = findViewById(R.id.country_code_text);
        enter_contact_no = findViewById(R.id.enter_contact_no);
        spinner_country_code = findViewById(R.id.spinner_country_code);
        loadingView = findViewById(R.id.loadingView);

        spinner_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountryCodeDialog();
            }
        });

        countries = Utils.loadCountries(this);

        TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        String con_code = tm.getNetworkCountryIso();
        if (!con_code.equals("")) {
            for (Country country : countries) {
                if (country.code.equals(con_code)) {
                    con_phone_code = country.phone_code;
                }
            }

            country_code_text.setText("+" + con_phone_code);
        } else {
            country_code_text.setText("+" + countries.get(0).phone_code);
        }

        register_back = findViewById(R.id.register_back);
        enter_contact_button = findViewById(R.id.enter_contact_button);

        register_back.setVisibility(View.VISIBLE);

        register_back.setOnClickListener(this);
        enter_contact_button.setOnClickListener(this);
    }

    private void openCountryCodeDialog() {
        ArrayList<String> list = new ArrayList<>();
        for (Country country : countries) {
            list.add(country.country_name + "(+" + country.phone_code + ")");
        }
        final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your country");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                country_code_text.setText("+" + countries.get(item).phone_code);
                country_code = countries.get(item).code;
                // country_code_text.setText(cs[item]);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(enter_contact_no.getText().toString().trim())) {
            Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_mobile_null));
            return false;
        } else if (enter_contact_no.getText().toString().trim().length() <= 5) {
            Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_invalid_contact));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter_contact_button:
                if (Utils.IsNetPresent(SelectContactNoActivity.this)) {
                    if (isValidData()) {
                        openVerifyContactDialog(country_code_text.getText().toString(), enter_contact_no.getText().toString());
                    }
                }else {
                    Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.register_back:
                onBackPressed();
                break;
        }
    }

    private void selectContactNoTask(final String country_code, final String contact_no) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("countryCode", country_code);
        map.put("contactNo", contact_no);

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
                        PreRegistrationInfo reg_info = session.getRegistrationInfo();
                        reg_info.contactNo = contact_no;
                        reg_info.countryCode = country_code;
                        session.createRegistrationInfo(reg_info);

                        Intent intent = new Intent(SelectContactNoActivity.this, ConfirmationCodeActivity.class);
                        intent.putExtra("contact_no", contact_no);
                        intent.putExtra("country_code", country_code);
                        try {
                            intent.putExtra("code", jsonObject.getString("otp"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        startActivityForResult(intent,674);


                    } else {
                        if (message.equals("This mobile number is already registered.")) {
                            Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_contact_already_registred));
                        } else {
                            Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_contact_not_verified));
                        }
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
        service.callSimpleVolley("user/verifyNo", map);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 674){
                finish();
            }

        }
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(SelectContactNoActivity.this);
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
            Log.e("ISO Code", isoCode);
            Log.e("Con Code", countryCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            return true;
        } else {
            return false;
        }
    }

    public void openVerifyContactDialog(String country_code, String contact_no) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SelectContactNoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Info");
        builder.setMessage("NUMBER CONFIRMATION:\n" + country_code + "  " + contact_no + "\nIs this phone number correct?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isValidPhoneNumber(enter_contact_no.getText().toString())) {
                    boolean status = validateUsing_libphonenumber(country_code_text.getText().toString(), enter_contact_no.getText().toString());
                    if (status) {
                        selectContactNoTask(country_code_text.getText().toString(), enter_contact_no.getText().toString());
                    } else {
                        Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_invalid_contact));
                    }
                } else {
                    Utils.openAlertDialog(SelectContactNoActivity.this, getResources().getString(R.string.alert_invalid_contact));
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
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(0, R.anim.back_slide);
    }
}
