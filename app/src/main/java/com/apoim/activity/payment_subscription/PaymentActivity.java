package com.apoim.activity.payment_subscription;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Validation;
import com.apoim.modal.BankInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView iv_bank_icon,iv_stripe_icon,bank_Added_sign;
    private LinearLayout ly_stripe,ly_bank;
    CardView cv_bank,cv_stripe;
    private TextView tv_stripe_text,tv_bank_acc_text,tv_add_bank_acc_txt;
    private EditText first_name,last_name,acc_number,routing_num,postal_code,ssn_last;
    private TextView tv_dob,add_bank_acc_btn;
    private InsLoadingView loading_view;
    private DatePickerDialog fromDate;
    private Calendar now;

    private boolean isValid() {
        Validation v = new Validation();
        if (!v.isNull(first_name)) {
            Utils.openAlertDialog(this, "First name can't be empty");
            return false;
        }
        else if (!v.isNull(last_name)) {
            Utils.openAlertDialog(this, "Last name can't be empty");
            return false;
        }
        else if (!v.isNull(acc_number)) {
            Utils.openAlertDialog(this, "Account number can't be empty");
            return false;
        }
        else if (!v.isNull(routing_num)) {
            Utils.openAlertDialog(this, "Routing number can't be empty");
            return false;
        }
        else if (!v.isNull(tv_dob)) {
            Utils.openAlertDialog(this, "Date of birth can't be empty");
            return false;
        }
        else if (!v.isNull(postal_code)) {
            Utils.openAlertDialog(this, "Postal code can't be empty");
            return false;
        }
        else if (!v.isNull(ssn_last)) {
            Utils.openAlertDialog(this, "SSN_Last can't be empty");
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ImageView iv_back = findViewById(R.id.iv_back);
        cv_bank = findViewById(R.id.cv_bank);
        cv_stripe = findViewById(R.id.cv_stripe);
        iv_bank_icon = findViewById(R.id.iv_bank_icon);
        iv_stripe_icon = findViewById(R.id.iv_stripe_icon);
        ly_stripe = findViewById(R.id.ly_stripe);
        ly_bank = findViewById(R.id.ly_bank);
        tv_stripe_text = findViewById(R.id.tv_stripe_text);
        tv_bank_acc_text = findViewById(R.id.tv_bank_acc_text);
        bank_Added_sign = findViewById(R.id.bank_Added_sign);
        tv_add_bank_acc_txt = findViewById(R.id.tv_add_bank_acc_txt);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        acc_number = findViewById(R.id.acc_number);
        routing_num = findViewById(R.id.routing_num);
        postal_code = findViewById(R.id.postal_code);
        ssn_last = findViewById(R.id.ssn_last);
        tv_dob = findViewById(R.id.tv_dob);
        add_bank_acc_btn = findViewById(R.id.add_bank_acc_btn);
        loading_view = findViewById(R.id.loading_view);

        iv_back.setOnClickListener(this);
        cv_bank.setOnClickListener(this);
        cv_stripe.setOnClickListener(this);
        ly_bank.setOnClickListener(this);
        ly_stripe.setOnClickListener(this);
        add_bank_acc_btn.setOnClickListener(this);
        tv_dob.setOnClickListener(this);

        now = Calendar.getInstance();

        if(getIntent().getStringExtra("updateBankInfo") != null){
            getBankInfo();
            add_bank_acc_btn.setText(R.string.update_acc);
            bank_Added_sign.setVisibility(View.VISIBLE);
            tv_add_bank_acc_txt.setVisibility(View.GONE);
        }


        Intent intent = new Intent(PaymentActivity.this,CreateEventActivity.class);
        intent.putExtra("isPaymentDone",false);
        setResult(RESULT_OK,intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:{
                onBackPressed();
                break;
            }
            case R.id.cv_bank:{
                ly_stripe.setVisibility(View.GONE);
                ly_bank.setVisibility(View.VISIBLE);
                cv_bank.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
                cv_stripe.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite));
                iv_bank_icon.setImageResource(R.drawable.ico_bank_acc_active);
                iv_stripe_icon.setImageResource(R.drawable.ico_stripe);
                tv_bank_acc_text.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                tv_stripe_text.setTextColor(ContextCompat.getColor(this,R.color.colorBlack));

                break;
            }
            case R.id.cv_stripe:{
                ly_bank.setVisibility(View.GONE);
                ly_stripe.setVisibility(View.VISIBLE);
                cv_bank.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite));
                cv_stripe.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
                iv_bank_icon.setImageResource(R.drawable.ico_bank_acc);
                iv_stripe_icon.setImageResource(R.drawable.ico_stripe_active);
                tv_bank_acc_text.setTextColor(ContextCompat.getColor(this,R.color.colorBlack));
                tv_stripe_text.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                break;
            }
            case R.id.add_bank_acc_btn:{
                if(isValid()){
                    addBankAccount();
                    add_bank_acc_btn.setEnabled(false);
                }
                break;
            }
            case R.id.tv_dob:{
                advanceDatePicker(tv_dob,13,true);
                break;
            }
        }
    }

    private void advanceDatePicker(final TextView setDate, int yearBackToOpen, boolean openFromGetDate) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        if(openFromGetDate){
            if(!setDate.getText().toString().trim().equals("")){

                SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat day = new SimpleDateFormat("dd", Locale.US);
                SimpleDateFormat month = new SimpleDateFormat("MM", Locale.US);
                SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.US);

                try {
                    mDay = Integer.parseInt(day.format(formatLong.parse(setDate.getText().toString())));
                    mMonth = Integer.parseInt(month.format(formatLong.parse(setDate.getText().toString()))) - 1;
                    mYear = Integer.parseInt(year.format(formatLong.parse(setDate.getText().toString())));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        DatePickerDialog fromDate = DatePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                if ((dayOfMonth < 10) && (monthOfYear + 1) < 10) {
                    //profile_birthday.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));
                    setDate.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                } else if ((dayOfMonth < 10) && (monthOfYear + 1) >= 10) {
                    //profile_birthday.setText("0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));
                    setDate.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                } else if ((dayOfMonth >= 10) && (monthOfYear + 1) <= 10) {
                    //profile_birthday.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));
                    setDate.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                } else {
                    //profile_birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));
                    setDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
            }
        }, mYear, mMonth, mDay);

        c.set(c.get(Calendar.YEAR )- yearBackToOpen , c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        fromDate.setMaxDate(c);
        fromDate.show(getFragmentManager(), "");

    }

    private void addBankAccount() {
        add_bank_acc_btn.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        Map<String,String> map = new HashMap<>();
        map.put("firstName",first_name.getText().toString().trim());
        map.put("lastName",last_name.getText().toString().trim());
        map.put("routingNumber",routing_num.getText().toString().trim());
        map.put("accountNumber",acc_number.getText().toString().trim());
        map.put("postalCode",postal_code.getText().toString().trim());
        map.put("ssnLast",ssn_last.getText().toString().trim());
        map.put("dob",tv_dob.getText().toString().trim());

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                add_bank_acc_btn.setEnabled(true);
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        successPaymentPopup(PaymentActivity.this,message);
                        bank_Added_sign.setVisibility(View.VISIBLE);

                    } else {

                        Utils.openAlertDialog(PaymentActivity.this,message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                add_bank_acc_btn.setEnabled(true);
            }
        });
        service.callSimpleVolley("payment/addBankAccount",map);
    }

    public void successPaymentPopup(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PaymentActivity.this,CreateEventActivity.class);
                intent.putExtra("isPaymentDone",true);
                setResult(RESULT_OK,intent);
                finish();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getBankInfo() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        BankInfo bankInfo = gson.fromJson(response,BankInfo.class);
                        setData(bankInfo.accountDetail);
                    }
                    else {
                        Utils.openAlertDialog(PaymentActivity.this,message);
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
        service.callGetSimpleVolley("event/getBankAccDetail");

    }

    private void setData(BankInfo.AccountDetailBean accountDetail) {
        first_name.setText(accountDetail.firstName);
        last_name.setText(accountDetail.lastName);
        acc_number.setText(accountDetail.accountNumber);
        routing_num.setText(accountDetail.routingNumber);
        tv_dob.setText(accountDetail.dob);
        postal_code.setText(accountDetail.postalCode);

        if(accountDetail.ssnLast.equals("0")){
            ssn_last.setText("0000");
        }else {
            ssn_last.setText(accountDetail.ssnLast);
        }

    }
}
