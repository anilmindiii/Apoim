package com.apoim.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.business.BusinessSubscriptionActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.CurrencyInfo;
import com.apoim.modal.ProfileInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class SubscriptionPayActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_card_icon, iv_stripe_icon;
    private CardView cv_card, cv_stripe;
    private TextView tv_credit_card;
    private TextView tv_stripe;
    private TextView tv_year;
    private TextView tv_month;
    private EditText editTxtCardNumber, ed_cvv, ed_card_holder_name;
    private InsLoadingView loading_view;
    private int monthType = 1;
    public static final String paymentType = "paymentType";
    private LinearLayout ly_stripe;
    private LinearLayout ly_card;
    private int payfor;
    private String from = "";
    TextView tv_pay;
    String memberId = "";
    String eventId = "";
    String compId = "";
    String eventMem_Id = "";
    String eventAmount = "";
    String currencyCode = "";
    private ArrayList<CurrencyInfo> currencyList;
    Session session ;

    // for appointment...
    String amount = "";
    String appointmentId = "";
    String appointForId = "";

    public static final int MAX_LENGTH_CARD_NUMBER_VISA_MASTERCARD = 19;
    public static final int MAX_LENGTH_CARD_NUMBER_AMEX = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_pay);

        ImageView iv_back = findViewById(R.id.iv_back);
        cv_card = findViewById(R.id.cv_card);
        cv_stripe = findViewById(R.id.cv_stripe);
        iv_card_icon = findViewById(R.id.iv_card_icon);
        iv_stripe_icon = findViewById(R.id.iv_stripe_icon);
        tv_credit_card = findViewById(R.id.tv_credit_card);
        tv_stripe = findViewById(R.id.tv_stripe);
        loading_view = findViewById(R.id.loading_view);
        tv_pay = findViewById(R.id.tv_pay);

        editTxtCardNumber = findViewById(R.id.ed_card_num);
        ed_cvv = findViewById(R.id.ed_cvv);
        ed_card_holder_name = findViewById(R.id.ed_card_holder_name);
        tv_year = findViewById(R.id.tv_year);
        tv_month = findViewById(R.id.tv_month);
        ly_stripe = findViewById(R.id.ly_stripe);
        ly_card = findViewById(R.id.ly_card);

        currencyList = new ArrayList<>();
        currencyList = Utils.loadCurrency(this);

        session = new Session(this);
        editTxtCardNumber.addTextChangedListener(new TextWatcher() {

            private boolean spaceDeleted;

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                CharSequence charDeleted = s.subSequence(start, start + count);
                spaceDeleted = " ".equals(charDeleted.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editTxtCardNumber.getText().length() > 0 && editTxtCardNumber.getText().charAt(0) == '3') {
                    editTxtCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH_CARD_NUMBER_AMEX)});

                    editTxtCardNumber.removeTextChangedListener(this);
                    int cursorPosition = editTxtCardNumber.getSelectionStart();
                    String withSpaces = formatTextAmEx(editable);
                    editTxtCardNumber.setText(withSpaces);
                    editTxtCardNumber.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

                    if (spaceDeleted) {
                        editTxtCardNumber.setSelection(editTxtCardNumber.getSelectionStart() - 1);
                        spaceDeleted = false;
                    }

                    editTxtCardNumber.addTextChangedListener(this);
                } else if (editTxtCardNumber.getText().length() > 0
                        && (editTxtCardNumber.getText().charAt(0) == '4' || editTxtCardNumber.getText().charAt(0) == '5')) {
                    editTxtCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH_CARD_NUMBER_VISA_MASTERCARD)});

                    editTxtCardNumber.removeTextChangedListener(this);
                    int cursorPosition = editTxtCardNumber.getSelectionStart();
                    String withSpaces = formatTextVisaMasterCard(editable);
                    editTxtCardNumber.setText(withSpaces);
                    editTxtCardNumber.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

                    if (spaceDeleted) {
                        editTxtCardNumber.setSelection(editTxtCardNumber.getSelectionStart() - 1);
                        spaceDeleted = false;
                    }

                    editTxtCardNumber.addTextChangedListener(this);
                } else {
                    editTxtCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH_CARD_NUMBER_VISA_MASTERCARD)});

                    editTxtCardNumber.removeTextChangedListener(this);
                    int cursorPosition = editTxtCardNumber.getSelectionStart();
                    String withSpaces = formatTextVisaMasterCard(editable);
                    editTxtCardNumber.setText(withSpaces);
                    editTxtCardNumber.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

                    if (spaceDeleted) {
                        editTxtCardNumber.setSelection(editTxtCardNumber.getSelectionStart() - 1);
                        spaceDeleted = false;
                    }

                    editTxtCardNumber.addTextChangedListener(this);
                }
            }
        });

        if (getIntent().getExtras() != null) {
            payfor = getIntent().getIntExtra(paymentType, 0);// 1 for payment for map // 2 for payment for to be on top // 3 for event pay
            memberId = getIntent().getStringExtra("memberId");
            eventId = getIntent().getStringExtra("eventId");
            compId = getIntent().getStringExtra("compId");
            eventMem_Id = getIntent().getStringExtra("eventMem_Id");
            eventAmount = getIntent().getStringExtra("eventAmount");
            currencyCode = getIntent().getStringExtra("currencyCode");
            from = getIntent().getStringExtra("from");


            if (eventAmount != null && currencyCode != null) {
                for (CurrencyInfo info : currencyList) {
                    if (info.code.equals(currencyCode)) {
                        tv_pay.setText("Pay" + " " + info.symbol_native + eventAmount);
                    }
                }
            }

            if(payfor == Constant.PayForCounterAppointment){
                // for appointment...
                amount = getIntent().getStringExtra("amount");
                appointmentId = getIntent().getStringExtra("appointmentId");;
                appointForId = getIntent().getStringExtra("appointForId");;
            }
        }

        iv_back.setOnClickListener(this);
        cv_card.setOnClickListener(this);
        cv_stripe.setOnClickListener(this);
        tv_year.setOnClickListener(this);
        tv_month.setOnClickListener(this);
        tv_pay.setOnClickListener(this);

    }

    public void gettingStripeToken() {
        loading_view.setVisibility(View.VISIBLE);
        String cardNumber = editTxtCardNumber.getText().toString().replace("-", "");
        int cardExpMonth = Integer.parseInt(tv_month.getText().toString().trim());
        int cardExpYear = Integer.parseInt(tv_year.getText().toString().trim());
        String cardCVC = ed_cvv.getText().toString().trim();

        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);

        card.validateNumber();
        card.validateCVC();

        Stripe stripe = new Stripe(this, "pk_test_QCwLHJXgVrZfIuGYFYRNY2eJ");

        stripe.createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) { // Send token to your server
                        Log.v("stripeTokenID", token.getId() + "");
                        if (payfor == 3) {
                            EventpaymentTask(eventId, memberId, "event/eventPayment",token.getId());
                        } else if (payfor == Constant.PayForcompainion) {
                            CompanionpaymentTask(eventId, compId, eventMem_Id,token.getId());
                        } else {
                            paymentTask(payfor,token.getId());

                        }
                    }

                    public void onError(Exception error) {
                        // Show localized error message
                        Utils.openAlertDialog(SubscriptionPayActivity.this,error.getLocalizedMessage()+"");
                    }
                }
        );
    }

    private String formatTextVisaMasterCard(CharSequence text) {
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (Character.isDigit(text.charAt(i))) {
                if (count % 4 == 0 && count > 0)
                    formatted.append("-");
                formatted.append(text.charAt(i));
                ++count;
            }
        }


        return formatted.toString();
    }

    private String formatTextAmEx(CharSequence text) {
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (Character.isDigit(text.charAt(i))) {
                if (count > 0 && ((count == 4) || (count == 10))) {
                    formatted.append("-");
                }
                formatted.append(text.charAt(i));
                ++count;
            }
        }

        return formatted.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.cv_card: {
                ly_stripe.setVisibility(View.GONE);
                ly_card.setVisibility(View.VISIBLE);
                cv_card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                cv_stripe.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                iv_card_icon.setImageResource(R.drawable.ico_c_card_active);
                iv_stripe_icon.setImageResource(R.drawable.ico_stripe);

                tv_credit_card.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tv_stripe.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));

                break;
            }
            case R.id.cv_stripe: {
                ly_card.setVisibility(View.GONE);
                ly_stripe.setVisibility(View.VISIBLE);
                cv_card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                cv_stripe.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                iv_card_icon.setImageResource(R.drawable.ico_c_card);
                iv_stripe_icon.setImageResource(R.drawable.ico_stripe_active);

                tv_credit_card.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                tv_stripe.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                break;
            }
            case R.id.tv_month: {
                showMonnthYearDialog(monthType);
                break;
            }
            case R.id.tv_year: {
                int yearType = 2;
                showMonnthYearDialog(yearType);
                break;
            }
            case R.id.tv_pay: {
                if (isValid())
                    gettingStripeToken();
                break;
            }
        }
    }

    private void CompanionpaymentTask(final String eventId, String compId, String eventMem_Id, String token) {
        tv_pay.setEnabled(false);
        //loading_view.setVisibility(View.VISIBLE);
        String cardNum = editTxtCardNumber.getText().toString().replace("-", "");
        Map<String, String> param = new HashMap<>();
        param.put("eventId", eventId);
        param.put("compId", compId);
        param.put("eventMemId", eventMem_Id);
        param.put("token", token);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        Utils.openAlertDialog(SubscriptionPayActivity.this, message);
                    } else {
                        if (message.equals("Payment is already done")) {
                            successWork("Payment done successfully");
                        } else {
                            Utils.openAlertDialog(SubscriptionPayActivity.this, message);
                        }
                    }
                    tv_pay.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                    tv_pay.setEnabled(true);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
                tv_pay.setEnabled(true);
            }
        });

        service.callMultiPartApi("event/companionPayment", param, null);

        //service.callSimpleVolley("event/companionPayment",param);
    }

    private void EventpaymentTask(final String eventId, String memberId, final String paymentApi, String token) {
        tv_pay.setEnabled(false);
        //String cardNum = editTxtCardNumber.getText().toString().replace("-", "");

        Map<String, String> param = new HashMap<>();
        param.put("eventId", eventId);
        param.put("memberId", memberId);
        param.put("fullName", ed_card_holder_name.getText().toString().trim());
        param.put("token", token);


        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        successWork(message);
                    } else {
                        Utils.openAlertDialog(SubscriptionPayActivity.this, message);
                    }

                    tv_pay.setEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                    tv_pay.setEnabled(true);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
                tv_pay.setEnabled(true);
            }
        });

        service.callMultiPartApi(paymentApi, param, null);
        //service.callSimpleVolley(paymentApi, param);
    }

    private void paymentTask(final int payfor,String token) {
        tv_pay.setEnabled(false);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        if(payfor == Constant.PayForCounterAppointment){
            params.put("amount", amount);
            params.put("appointmentId", appointmentId);
            params.put("appointForId", appointForId);
        }

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {

                        if(payfor == Constant.PayForTSubscription){
                            JSONObject jsonObject = object.getJSONObject("subscriptionDetail");
                            String subscriptionId = jsonObject.getString("subscriptionId");
                            String subscriptionStatus = jsonObject.getString("subscriptionStatus");
                            String customerId = jsonObject.getString("customerId");

                            SignInInfo userDetail = session.getUser();
                            userDetail.userDetail.subscriptionId = subscriptionId;
                            userDetail.userDetail.subscriptionStatus = subscriptionStatus;
                            userDetail.userDetail.stripeCustomerId = customerId;
                            session.createSession(userDetail);

                            subscriptionPaymentDialod(SubscriptionPayActivity.this,message);
                        }
                        else if(payfor == Constant.PayForBusinessSubscription) {
                            JSONObject jsonObject = object.getJSONObject("subscriptionDetail");
                            String bizSubscriptionId = jsonObject.getString("bizSubscriptionId");
                            String bizSubscriptionStatus = jsonObject.getString("bizSubscriptionStatus");
                            String customerId = jsonObject.getString("customerId");

                            SignInInfo userDetail = session.getUser();
                            userDetail.userDetail.bizSubscriptionId = bizSubscriptionId;
                            userDetail.userDetail.bizSubscriptionStatus = bizSubscriptionStatus;
                            session.createSession(userDetail);

                            subscriptionPaymentDialod(SubscriptionPayActivity.this,message);
                        }else {
                            successWork("Payment done successfully");
                        }
                    } else {
                        Utils.openAlertDialog(SubscriptionPayActivity.this, message);
                        tv_pay.setEnabled(true);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_pay.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
                tv_pay.setEnabled(true);
            }
        });

        if (payfor == Constant.PayForMap) { // 1 for payment for map
            service.callMultiPartApi("payment/viewOnMapPayment", params, null);
            //service.callSimpleVolley("payment/viewOnMapPayment",params);
        }
        else if (payfor == Constant.PayForToBeOnTop) { // 2 for payment for to be on top
            service.callMultiPartApi("payment/paymentForShowTopList", params, null);
            //service.callSimpleVolley("payment/paymentForShowTopList",params);
        }
        else if(payfor == Constant.PayForTSubscription){
            service.callMultiPartApi("payment/subsPaymentProcess", params, null);
        }
        else if(payfor == Constant.PayForBusinessSubscription){
            service.callMultiPartApi("business/businessSubscription", params, null);
        }
        else if(payfor == Constant.PayForCounterAppointment){
            service.callMultiPartApi("appointment/appointmentPayment", params, null);
        }

    }

    private void successWork(String message) {
        Session session = new Session(SubscriptionPayActivity.this, SubscriptionPayActivity.this);
        SignInInfo info = session.getUser();

        if (payfor == Constant.PayForMap) { // 1 for payment for map
            info.userDetail.mapPayment = "1";
            session.createSession(info);
            setResult(RESULT_OK);

        } else if (payfor == Constant.PayForToBeOnTop) {
            info.userDetail.showTopPayment = "1";
            session.createSession(info);
            setResult(RESULT_OK);
        } else if (payfor == 3) {
            Intent intent = new Intent(SubscriptionPayActivity.this, EventDetailsActivity.class);
            intent.putExtra("isEventPaymentDone", true);
            setResult(RESULT_OK, intent);
        }

        completePaymentDialod(SubscriptionPayActivity.this, message);
    }

    public void completePaymentDialod(Context context, String message) {
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

    public void subscriptionPaymentDialod(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(from.equals("settings")){
                    finish();
                }else {
                    Intent intent = new Intent(SubscriptionPayActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isValid() {
        Validation v = new Validation();
        if (!v.isNull(editTxtCardNumber)) {
            Utils.openAlertDialog(this, "Please enter card number");
            return false;
        } else if (editTxtCardNumber.getText().toString().trim().length() < 19) {
            Utils.openAlertDialog(this, "Card Number must be 16 characters long");
            return false;
        } else if (!v.isNull(tv_month)) {
            Utils.openAlertDialog(this, "Please select expiry month");
            return false;
        } else if (!v.isNull(tv_year)) {
            Utils.openAlertDialog(this, "Please select expiry year");
            return false;
        } else if (!v.isNull(ed_cvv)) {
            Utils.openAlertDialog(this, "Please enter CVV Number");
            return false;
        } else if (ed_cvv.getText().toString().length() < 3) {
            Utils.openAlertDialog(this, "CVV Number must be atleast 3 characters long");
            return false;
        } else if (!v.isNull(ed_card_holder_name)) {
            Utils.openAlertDialog(this, "Please enter cardholder name");
            return false;
        }

        return true;
    }

    private void showMonnthYearDialog(final int pickerType) {
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.year_month_dialog);
        d.getWindow().setLayout((getWindowManager().getDefaultDisplay().getWidth() * 9) / 10, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button set = d.findViewById(R.id.button1);
        Button cancel = d.findViewById(R.id.button2);
        TextView dialog_header = d.findViewById(R.id.dialog_header);
        final NumberPicker yearPicker = d.findViewById(R.id.yearPicker);
        final NumberPicker monthPicker = d.findViewById(R.id.monthPicker);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month + 1);

        yearPicker.setMaxValue(year + 20);
        yearPicker.setMinValue(year);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        if (pickerType == monthType) {
            monthPicker.setVisibility(View.VISIBLE);
            yearPicker.setVisibility(View.GONE);
            dialog_header.setText("Expiry Month");

        } else {
            monthPicker.setVisibility(View.GONE);
            yearPicker.setVisibility(View.VISIBLE);
            dialog_header.setText("Expiry Year");
        }

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expireMnth = String.valueOf(monthPicker.getValue());
                String expireYear = String.valueOf(yearPicker.getValue());
                //String  expireYear = String.valueOf(yearPicker.getValue()).substring(2, 4);

                if (pickerType == monthType) {
                    if (Integer.parseInt(expireMnth) < 10) {
                        expireMnth = "0" + expireMnth;
                    }
                    tv_month.setText(expireMnth);
                } else {
                    tv_year.setText(expireYear);
                }

                d.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }
}
