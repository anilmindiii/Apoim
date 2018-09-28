package com.apoim.activity.payment_subscription;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.business.BusinessSubscriptionActivity;
import com.apoim.helper.Constant;
import com.apoim.util.Utils;

import static android.graphics.Color.TRANSPARENT;
import static com.apoim.activity.payment_subscription.SubscriptionPayActivity.paymentType;


public class SelectPaymentTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private String mapPayment = "";
    private String showTopPayment = "";
    private String isBusinessAdded = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_type);

        if (getIntent().getExtras() != null) {
            mapPayment = getIntent().getStringExtra("mapPayment");
            showTopPayment = getIntent().getStringExtra("showTopPayment");
            isBusinessAdded = getIntent().getStringExtra("isBusinessAdded");
        }

        CardView cv_pay_for_appointment = findViewById(R.id.cv_pay_for_appointment);
        CardView cv_pay_to_be_top = findViewById(R.id.cv_pay_to_be_top);
        CardView cv_subscription = findViewById(R.id.cv_subscription);
        CardView cv_business = findViewById(R.id.cv_business);
        ImageView iv_back = findViewById(R.id.iv_back);

        cv_business.setOnClickListener(this);
        cv_pay_for_appointment.setOnClickListener(this);
        cv_pay_to_be_top.setOnClickListener(this);
        cv_subscription.setOnClickListener(this);
        iv_back.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.cv_pay_for_appointment:
                moveToPaymentScreen(R.id.cv_pay_for_appointment, getString(R.string.pay_for_appointment), getString(R.string.access_map_to_create_appointments));
                break;
            case R.id.cv_pay_to_be_top:
                moveToPaymentScreen(R.id.cv_pay_to_be_top, getString(R.string.Pay_to_be_on_top), getString(R.string.payment_you_will_be_on_top));
                break;
            case R.id.cv_subscription:
                intent = new Intent(this, SubscriptionActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.cv_business:

                if(isBusinessAdded.equals("0")){
                    Utils.openAlertDialog(this,"First add your business from settings.");
                }else {
                    intent = new Intent(this, BusinessSubscriptionActivity.class);
                    intent.putExtra("from", "settings");
                    startActivity(intent);
                }




                break;

        }
    }


    private void moveToPaymentScreen(final int id, String headerText, String msgText) {
        final Dialog _dialog = new Dialog(this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        _dialog.setContentView(R.layout.move_to_payment_screen_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        final TextView tv_pay = _dialog.findViewById(R.id.tv_pay);
        TextView dialog_header = _dialog.findViewById(R.id.dialog_header);
        TextView tv_msg_text = _dialog.findViewById(R.id.tv_msg_text);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        dialog_header.setText(headerText);
        tv_msg_text.setText(msgText);
        tv_pay.setText("Pay $5");


        if (id == R.id.cv_pay_for_appointment) {
            if (mapPayment.equals("1"))
                tv_pay.setText("Paid");
        } else if (id == R.id.cv_pay_to_be_top) {
            if (showTopPayment.equals("1"))
                tv_pay.setText("Paid");
        }

        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == R.id.cv_pay_for_appointment) {
                    if (mapPayment.equals("1")) {

                    } else {
                        Intent intent = new Intent(SelectPaymentTypeActivity.this, SubscriptionPayActivity.class);
                        intent.putExtra(paymentType, Constant.PayForMap);// 1 for pay for map
                        startActivityForResult(intent, 1);
                    }

                } else if (id == R.id.cv_pay_to_be_top) {
                    if (showTopPayment.equals("1")) {

                    } else {
                        Intent intent = new Intent(SelectPaymentTypeActivity.this, SubscriptionPayActivity.class);
                        intent.putExtra(paymentType, Constant.PayForToBeOnTop); // 2 for go to on top
                        startActivityForResult(intent, 2);
                    }

                }

                _dialog.dismiss();
            }
        });

        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialog.dismiss();
            }
        });
        _dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {// 1 for pay for map
                mapPayment = "1";
            } else if (requestCode == 2) {// 2 for go to on top
                showTopPayment = "1";
            }

            if (requestCode == 90) {
                //sub code
            }

        }
    }
}
