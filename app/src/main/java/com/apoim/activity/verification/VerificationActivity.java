package com.apoim.activity.verification;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.medialablk.easygifview.EasyGifView;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {
    private ImageView iv_mobile_veri, iv_id_hand_veri, iv_face_detection_veri;
    private ImageView iv_mobile_veri_active, iv_id_hand_veri_active, iv_face_detection_veri_active, iv_back;
    private TextView tv_mobile_veri, tv_id_hand_veri, tv_face_detection_veri;
    private RelativeLayout ly_mobile_veri, ly_id_hand_veri, ly_face_detection_veri;
    private TextView btn_proceed, tv_msg;
    private int id;
    private TextView verification_text;
    private InsLoadingView loading_view;
    String idWithHand = "";
    String isVerifiedId = "";
    String typeNotification = "";
    // variable to track event time
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        iv_mobile_veri = findViewById(R.id.iv_mobile_veri);
        iv_id_hand_veri = findViewById(R.id.iv_id_hand_veri);
        iv_face_detection_veri = findViewById(R.id.iv_face_detection_veri);

        iv_mobile_veri_active = findViewById(R.id.iv_mobile_veri_active);
        iv_id_hand_veri_active = findViewById(R.id.iv_id_hand_veri_active);
        iv_face_detection_veri_active = findViewById(R.id.iv_face_detection_veri_active);

        tv_mobile_veri = findViewById(R.id.tv_mobile_veri);
        tv_id_hand_veri = findViewById(R.id.tv_id_hand_veri);
        tv_face_detection_veri = findViewById(R.id.tv_face_detection_veri);

        ly_mobile_veri = findViewById(R.id.ly_mobile_veri);
        ly_id_hand_veri = findViewById(R.id.ly_id_hand_veri);
        ly_face_detection_veri = findViewById(R.id.ly_face_detection_veri);

        verification_text = findViewById(R.id.verification_text);
        iv_back = findViewById(R.id.iv_back);
        loading_view = findViewById(R.id.loading_view);
        tv_msg = findViewById(R.id.tv_msg);

        btn_proceed = findViewById(R.id.btn_proceed);

        EasyGifView easyGifView = findViewById(R.id.easyGifView);
        easyGifView.setGifFromResource(R.drawable.mobile_verify);

        if (getIntent().getStringExtra("typeNotification") != null) {
            typeNotification = getIntent().getStringExtra("id_verification");
        }



        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        verification_text.setText("Mobile Number");
        tv_msg.setText("A user initiates SMS verification in your app. Your app might prompt");

        iv_mobile_veri.setImageResource(R.drawable.active_sms);
        iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
        iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

        tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));
        tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));


        ly_mobile_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                id = view.getId();
                easyGifView.setGifFromResource(R.drawable.mobile_verify);
                verification_text.setText("Mobile Number");
                tv_msg.setText("A user initiates SMS verification in your app. Your app might prompt");

                iv_mobile_veri.setImageResource(R.drawable.active_sms);
                iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
                iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

                tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));
                tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
                tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));

            }
        });

        ly_id_hand_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                id = view.getId();
                easyGifView.setGifFromResource(R.drawable.face_verify);
                verification_text.setText("ID With Hand");
                tv_msg.setText("Please select image for verification");

                iv_mobile_veri.setImageResource(R.drawable.inactive_sms);
                iv_id_hand_veri.setImageResource(R.drawable.active_id);
                iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

                tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
                tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));
                tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));

            }
        });

        ly_face_detection_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* id = view.getId();
                verification_text.setText("Face Detection");

                iv_mobile_veri.setImageResource(R.drawable.inactive_sms);
                iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
                iv_face_detection_veri.setImageResource(R.drawable.active_face);

                tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.colorGray));
                tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.colorGray));
                tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.colorBlack));
*/
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (id) {
                    case R.id.ly_mobile_veri:
                        Utils.openAlertDialog(VerificationActivity.this,"Mobile Verification Under Development");
                        //startActivity(new Intent(VerificationActivity.this, SelectContactNoActivity.class));
                        break;

                    case R.id.ly_id_hand_veri:
                        startActivity(new Intent(VerificationActivity.this, IDwithHandVerifyActivity.class)
                                .putExtra("idWithHand", idWithHand)
                                .putExtra("isVerifiedId", isVerifiedId));
                        break;

                    case R.id.ly_face_detection_veri:
                        break;

                    default:
                        Utils.openAlertDialog(VerificationActivity.this,"Mobile Verification Under Development");

                        //startActivity(new Intent(VerificationActivity.this, SelectContactNoActivity.class));

                }
            }
        });



    }

    private void mobileVerificationSelcted() {
        iv_mobile_veri_active.setVisibility(View.VISIBLE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.GONE);

        iv_mobile_veri.setImageResource(R.drawable.active_sms);
        //iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
        //iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

        tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));
       // tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        //tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));

        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_green_border);
        //ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        //ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
    }



    private void idWithHandSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.VISIBLE);
        iv_face_detection_veri_active.setVisibility(View.GONE);

        //iv_mobile_veri.setImageResource(R.drawable.inactive_sms);
        iv_id_hand_veri.setImageResource(R.drawable.active_id);
        //iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

        //tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));
        //tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));

       // ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_green_border);
        //ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);

    }

    private void idWithHandDeSelcted() {
        iv_id_hand_veri_active.setVisibility(View.GONE);

        //iv_mobile_veri.setImageResource(R.drawable.inactive_sms);
        iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
        //iv_face_detection_veri.setImageResource(R.drawable.inactive_face);

        //tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        //tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));

        // ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        //ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);

    }

    private void faceDetectionSelcted() {
        iv_mobile_veri_active.setVisibility(View.GONE);
        iv_id_hand_veri_active.setVisibility(View.GONE);
        iv_face_detection_veri_active.setVisibility(View.VISIBLE);

       // iv_mobile_veri.setImageResource(R.drawable.inactive_sms);
       // iv_id_hand_veri.setImageResource(R.drawable.inactive_id);
        iv_face_detection_veri.setImageResource(R.drawable.active_face);

        //tv_mobile_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        //tv_id_hand_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorGray));
        tv_face_detection_veri.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.colorBlack));

        ly_mobile_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_id_hand_veri.setBackgroundResource(R.drawable.rectangular_black_border_white_bg);
        ly_face_detection_veri.setBackgroundResource(R.drawable.rectangular_green_border);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getStatus();
    }

    private void getStatus() {
        loading_view.setVisibility(View.VISIBLE);
        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    loading_view.setVisibility(View.GONE);
                    if (status.equals("success")) {
                        JSONObject object = jsonObject.getJSONObject("verificationStatus");
                        String otpVerified = object.getString("otpVerified");
                        String emailVerified = object.getString("emailVerified");
                        isVerifiedId = object.getString("isVerifiedId");
                        idWithHand = object.getString("idWithHand");

                        if (otpVerified.equals("1")) {
                            mobileVerificationSelcted();
                            ly_mobile_veri.setEnabled(false);
                        }
                        else if (otpVerified.equals("0")) {
                            idWithHandDeSelcted();
                        }

                        if (isVerifiedId.equals("1")) {
                            idWithHandSelcted();
                        }

                        if(typeNotification != null)
                        if(typeNotification.equals("id_verification")){
                            startActivity(new Intent(VerificationActivity.this,IDwithHandVerifyActivity.class)
                                    .putExtra("typeNotification","id_verification"));

                            typeNotification = "";
                        }


                    } else {

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
        service.callGetSimpleVolley("user/getUserVerificationStatus");

    }
}
