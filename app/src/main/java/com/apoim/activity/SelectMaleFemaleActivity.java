package com.apoim.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.groupchatwebrtc.activities.BaseActivity;
import com.apoim.groupchatwebrtc.services.CallService;
import com.apoim.groupchatwebrtc.utils.Consts;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.groupchatwebrtc.utils.UsersUtils;
import com.apoim.helper.Constant;
import com.apoim.modal.PreRegistrationInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.modal.UserInfoFCM;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 2/12/2018.
 */

public class SelectMaleFemaleActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout lending_layout;
    LinearLayout select_male_female_layout, select_chat_date_layout, select_girl_guys_layout, select_public_private_layout;
    TextView select_male_button,select_transgender_button, select_txgender_for_date_button , select_female_button, select_new_friends_button, select_chat_button, select_date_button,
            select_girl_button, select_guys_button, select_girl_guy_both_button, select_public_button,
            select_private_button, select_public_private_both_button;
    ImageButton register_back;
    LinearLayout register_signIn;
    Session session;
    public PreRegistrationInfo reg_info, r_info;
    boolean isFemale = false;
    private InsLoadingView loadingView;

    // Facebook Login
    CallbackManager callbackManager;
    LoginManager loginManager;

    static boolean SignUpFBloggedIn;
    // Gmail Integration
    GoogleSignInClient signInClient;
    //quickblock
    private QBUser userForSave;
    private String isProfileUpdate = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_male_female);

        initView();

        session = new Session(SelectMaleFemaleActivity.this, this);
        reg_info = new PreRegistrationInfo();

        if (Utils.IsNetPresent(SelectMaleFemaleActivity.this)) {
            reg_info.deviceToken = FirebaseInstanceId.getInstance().getToken();
        }

        // Facebook Integration
        SignUpFBloggedIn = false;
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        facebookLogin();

        // Gmail Integration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (select_male_female_layout.getVisibility() == View.VISIBLE) {
            register_back.setVisibility(View.GONE);
        }

        select_male_button.setOnClickListener(this);
        select_txgender_for_date_button.setOnClickListener(this);
        select_transgender_button.setOnClickListener(this);
        select_female_button.setOnClickListener(this);
        select_new_friends_button.setOnClickListener(this);
        select_chat_button.setOnClickListener(this);
        select_date_button.setOnClickListener(this);
        select_girl_button.setOnClickListener(this);
        select_guys_button.setOnClickListener(this);
        select_girl_guy_both_button.setOnClickListener(this);
        select_public_button.setOnClickListener(this);
        select_private_button.setOnClickListener(this);
        select_public_private_both_button.setOnClickListener(this);
        register_back.setOnClickListener(this);
        register_signIn.setOnClickListener(this);

    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void initView() {

        select_male_female_layout = findViewById(R.id.select_male_female_layout);
        select_chat_date_layout = findViewById(R.id.select_chat_date_layout);
        select_girl_guys_layout = findViewById(R.id.select_girl_guys_layout);
        select_public_private_layout = findViewById(R.id.select_public_private_layout);

        select_male_button = findViewById(R.id.select_male_button);
        select_female_button = findViewById(R.id.select_female_button);
        select_transgender_button = findViewById(R.id.select_transgender_button);
        select_txgender_for_date_button = findViewById(R.id.select_txgender_for_date_button);

        select_new_friends_button = findViewById(R.id.select_new_friends_button);
        select_chat_button = findViewById(R.id.select_chat_button);
        select_date_button = findViewById(R.id.select_date_button);

        select_girl_button = findViewById(R.id.select_girl_button);
        select_guys_button = findViewById(R.id.select_guys_button);
        select_girl_guy_both_button = findViewById(R.id.select_girl_guy_both_button);

        select_public_button = findViewById(R.id.select_public_button);
        select_private_button = findViewById(R.id.select_private_button);
        select_public_private_both_button = findViewById(R.id.select_public_private_both_button);

        register_back = findViewById(R.id.register_back);
        register_signIn = findViewById(R.id.register_signIn);

        lending_layout = findViewById(R.id.lending_layout);
        loadingView = findViewById(R.id.loadingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.select_male_button:
                if (select_male_female_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_male_female_layout.clearAnimation();
                    select_male_female_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_male_female_layout.setVisibility(View.GONE);

                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_chat_date_layout.setVisibility(View.VISIBLE);

                    isFemale = false;

                    reg_info.gender = Constant.REGISTER_MALE;
                    if (Constant.isSocial) {
                        reg_info = session.getRegistrationInfo();
                        reg_info.gender = Constant.REGISTER_MALE;
                        Constant.isSocial = false;
                    }

                    session.createRegistrationInfo(reg_info);

                    select_male_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_male_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_female_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_female_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_transgender_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_transgender_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    // startActivity(new Intent(this, CreateAccountActivity.class));
                }
                break;


            case R.id.select_female_button:
                if (select_male_female_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_male_female_layout.clearAnimation();
                    select_male_female_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_male_female_layout.setVisibility(View.GONE);

                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_chat_date_layout.setVisibility(View.VISIBLE);

                    isFemale = true;

                    reg_info.gender = Constant.REGISTER_FEMALE;

                    if (Constant.isSocial) {
                        reg_info = session.getRegistrationInfo();
                        reg_info.gender = Constant.REGISTER_FEMALE;
                        Constant.isSocial = false;
                    }

                    session.createRegistrationInfo(reg_info);

                    select_male_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_male_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_transgender_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_transgender_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_female_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_female_button.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                break;

            case R.id.select_transgender_button:
                if (select_male_female_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_male_female_layout.clearAnimation();
                    select_male_female_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_male_female_layout.setVisibility(View.GONE);

                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_chat_date_layout.setVisibility(View.VISIBLE);

                    isFemale = false;

                    reg_info.gender = Constant.REGISTER_Transgender;
                    if (Constant.isSocial) {
                        reg_info = session.getRegistrationInfo();
                        reg_info.gender = Constant.REGISTER_Transgender;
                        Constant.isSocial = false;
                    }

                    session.createRegistrationInfo(reg_info);

                    select_male_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_male_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_female_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_female_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_transgender_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_transgender_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    //  startActivity(new Intent(this, CreateAccountActivity.class));
                }
                break;

            case R.id.select_new_friends_button:
                if (select_chat_date_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_chat_date_layout.setVisibility(View.GONE);

                    select_girl_guys_layout.clearAnimation();
                    select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_girl_guys_layout.setVisibility(View.VISIBLE);

                    reg_info.purpose = Constant.REGISTER_NEW_FRIENDS;
                    session.createRegistrationInfo(reg_info);

                    select_new_friends_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_new_friends_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_chat_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_chat_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_date_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_date_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_chat_button:
                if (select_chat_date_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_chat_date_layout.setVisibility(View.GONE);

                    select_girl_guys_layout.clearAnimation();
                    select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_girl_guys_layout.setVisibility(View.VISIBLE);

                    reg_info.purpose = Constant.REGISTER_CHAT;
                    session.createRegistrationInfo(reg_info);

                    select_new_friends_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_new_friends_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_chat_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_chat_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_date_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_date_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_date_button:
                if (select_chat_date_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);
                    select_chat_date_layout.clearAnimation();
                    select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                    select_chat_date_layout.setVisibility(View.GONE);

                    select_girl_guys_layout.clearAnimation();
                    select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                    select_girl_guys_layout.setVisibility(View.VISIBLE);

                    reg_info.purpose = Constant.REGISTER_DATE;
                    session.createRegistrationInfo(reg_info);

                    select_new_friends_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_new_friends_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_chat_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_chat_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_date_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_date_button.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                break;


                /*................*/

            case R.id.select_girl_button:
                if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);

                    if (isFemale) {
                        select_girl_guys_layout.clearAnimation();
                        select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                        select_girl_guys_layout.setVisibility(View.GONE);

                        select_public_private_layout.clearAnimation();
                        select_public_private_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                        select_public_private_layout.setVisibility(View.VISIBLE);

                        reg_info.dateWith = Constant.REGISTER_GIRL;
                        session.createRegistrationInfo(reg_info);
                    } else {
                        reg_info.dateWith = Constant.REGISTER_GIRL;
                        reg_info.eventInvitation = "";
                        session.createRegistrationInfo(reg_info);

                        if(reg_info.socialType != null){
                            startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }else {
                            startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }


                    }

                    select_girl_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_girl_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_guys_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_guys_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_girl_guy_both_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_guy_both_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_txgender_for_date_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_txgender_for_date_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_guys_button:
                if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);


                    if (isFemale) {
                        select_girl_guys_layout.clearAnimation();
                        select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                        select_girl_guys_layout.setVisibility(View.GONE);

                        select_public_private_layout.clearAnimation();
                        select_public_private_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                        select_public_private_layout.setVisibility(View.VISIBLE);

                        reg_info.dateWith = Constant.REGISTER_GUY;
                        session.createRegistrationInfo(reg_info);
                    } else {
                        reg_info.dateWith = Constant.REGISTER_GUY;
                        reg_info.eventInvitation = "";
                        session.createRegistrationInfo(reg_info);

                        if(reg_info.socialType != null){
                            startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }else {
                            startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }

                    }

                    select_girl_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_guys_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_guys_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_girl_guy_both_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_guy_both_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_txgender_for_date_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_txgender_for_date_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_txgender_for_date_button:
                if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);


                    if (isFemale) {
                        select_girl_guys_layout.clearAnimation();
                        select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                        select_girl_guys_layout.setVisibility(View.GONE);

                        select_public_private_layout.clearAnimation();
                        select_public_private_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                        select_public_private_layout.setVisibility(View.VISIBLE);

                        reg_info.dateWith = Constant.REGISTER_Transgender;
                        session.createRegistrationInfo(reg_info);
                    } else {
                        reg_info.dateWith = Constant.REGISTER_Transgender;
                        reg_info.eventInvitation = "";
                        session.createRegistrationInfo(reg_info);

                        if(reg_info.socialType != null){
                            startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }else {
                            startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }

                    select_girl_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_guys_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_guys_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_txgender_for_date_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_txgender_for_date_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_girl_guy_both_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_guy_both_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_girl_guy_both_button:
                if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
                    register_back.setVisibility(View.VISIBLE);


                    if (isFemale) {
                        select_girl_guys_layout.clearAnimation();
                        select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_out));
                        select_girl_guys_layout.setVisibility(View.GONE);

                        select_public_private_layout.clearAnimation();
                        select_public_private_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.slide_in));
                        select_public_private_layout.setVisibility(View.VISIBLE);

                        reg_info.dateWith = Constant.REGISTER_GIRL_GUY_TransGebder;
                        session.createRegistrationInfo(reg_info);
                    } else {
                        reg_info.dateWith = Constant.REGISTER_GIRL_GUY_TransGebder;
                        reg_info.eventInvitation = "";
                        session.createRegistrationInfo(reg_info);

                        if(reg_info.socialType != null){
                            startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }else {
                            startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }

                    select_girl_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_girl_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_guys_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_guys_button.setTextColor(getResources().getColor(R.color.colorBlack));

                    select_girl_guy_both_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                    select_girl_guy_both_button.setTextColor(getResources().getColor(R.color.colorWhite));

                    select_txgender_for_date_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                    select_txgender_for_date_button.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;

            case R.id.select_public_button:
                register_back.setVisibility(View.VISIBLE);

                reg_info.eventInvitation = Constant.REGISTER_PUBLIC;
                session.createRegistrationInfo(reg_info);

                if(reg_info.socialType != null){
                    startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else {
                    startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

                select_public_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                select_public_button.setTextColor(getResources().getColor(R.color.colorWhite));

                select_private_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_private_button.setTextColor(getResources().getColor(R.color.colorBlack));

                select_public_private_both_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_public_private_both_button.setTextColor(getResources().getColor(R.color.colorBlack));
                break;

            case R.id.select_private_button:
                register_back.setVisibility(View.VISIBLE);

                reg_info.eventInvitation = Constant.REGISTER_PRIVATE;
                session.createRegistrationInfo(reg_info);

                if(reg_info.socialType != null){
                    startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else {
                    startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                   // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

                select_public_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_public_button.setTextColor(getResources().getColor(R.color.colorBlack));

                select_private_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                select_private_button.setTextColor(getResources().getColor(R.color.colorWhite));

                select_public_private_both_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_public_private_both_button.setTextColor(getResources().getColor(R.color.colorBlack));
                break;

            case R.id.select_public_private_both_button:
                register_back.setVisibility(View.VISIBLE);

                reg_info.eventInvitation = Constant.REGISTER_PUBLIC_PRIVATE_BOTH;
                session.createRegistrationInfo(reg_info);

                if(reg_info.socialType != null){
                    startActivity(new Intent(SelectMaleFemaleActivity.this, CreateAccountActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else {
                    startActivity(new Intent(SelectMaleFemaleActivity.this, EmailVerifyActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

                select_public_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_public_button.setTextColor(getResources().getColor(R.color.colorBlack));

                select_private_button.setBackgroundResource(R.drawable.register_btn_inactive_bg);
                select_private_button.setTextColor(getResources().getColor(R.color.colorBlack));

                select_public_private_both_button.setBackgroundResource(R.drawable.register_btn_active_bg);
                select_public_private_both_button.setTextColor(getResources().getColor(R.color.colorWhite));
                break;

            case R.id.register_back:
                backPressMethod();
                break;

            case R.id.register_signIn:
                startActivity(new Intent(SelectMaleFemaleActivity.this, SignInActivity.class));
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                break;
        }

    }

    private void backPressMethod() {
        PreRegistrationInfo reg = session.getRegistrationInfo();

        if (select_chat_date_layout.getVisibility() == View.VISIBLE) {
            select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide));
            select_chat_date_layout.clearAnimation();
            select_chat_date_layout.setVisibility(View.GONE);

            if (select_chat_date_layout.getVisibility() == View.GONE) {
                select_male_female_layout.clearAnimation();
                select_male_female_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide_out));
                select_male_female_layout.setVisibility(View.VISIBLE);

            }

            if (select_male_female_layout.getVisibility() == View.VISIBLE) {
                register_back.setVisibility(View.GONE);
            }
        } else if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
            select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide));
            select_girl_guys_layout.clearAnimation();
            select_girl_guys_layout.setVisibility(View.GONE);

            if (select_girl_guys_layout.getVisibility() == View.GONE) {
                select_chat_date_layout.clearAnimation();
                select_chat_date_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide_out));
                select_chat_date_layout.setVisibility(View.VISIBLE);
            }

        } else if (select_public_private_layout.getVisibility() == View.VISIBLE) {
            select_public_private_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide));
            select_public_private_layout.clearAnimation();
            select_public_private_layout.setVisibility(View.GONE);

            if (select_public_private_layout.getVisibility() == View.GONE) {
                select_girl_guys_layout.clearAnimation();
                select_girl_guys_layout.startAnimation(AnimationUtils.loadAnimation(SelectMaleFemaleActivity.this, R.anim.back_slide_out));
                select_girl_guys_layout.setVisibility(View.VISIBLE);
            }
        }

        if (select_male_female_layout.getVisibility() == View.VISIBLE) {
            select_chat_date_layout.setVisibility(View.GONE);
            select_girl_guys_layout.setVisibility(View.GONE);
            select_public_private_layout.setVisibility(View.GONE);
        } else if (select_chat_date_layout.getVisibility() == View.VISIBLE) {
            select_male_female_layout.setVisibility(View.GONE);
            select_girl_guys_layout.setVisibility(View.GONE);
            select_public_private_layout.setVisibility(View.GONE);
        } else if (select_girl_guys_layout.getVisibility() == View.VISIBLE) {
            select_male_female_layout.setVisibility(View.GONE);
            select_chat_date_layout.setVisibility(View.GONE);
            select_public_private_layout.setVisibility(View.GONE);
        } else if (select_public_private_layout.getVisibility() == View.VISIBLE) {
            select_male_female_layout.setVisibility(View.GONE);
            select_chat_date_layout.setVisibility(View.GONE);
            select_girl_guys_layout.setVisibility(View.GONE);
        }
    }

    private void facebookLogin() {

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                final String sSocialId = loginResult.getAccessToken().getUserId();
                final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError() != null) {
                            Utils.openAlertDialog(SelectMaleFemaleActivity.this, getResources().getString(R.string.alert_api_fail));
                        } else {
                            Log.e("SIGN UP RESPONSE", response.toString());
                            Log.e("SIGN UP OBJECT", object + "");

                            SignUpFBloggedIn = true;

                            try {
                                String email = "";

                                if (object.has("email")) {
                                    email = object.getString("email");
                                }

                                final String socialId = object.getString("id");
                                final String firstname = object.getString("first_name");
                                final String lastname = object.getString("last_name");
                                final String fullname = firstname + " " + lastname;
                                final String profileImage = "https://graph.facebook.com/" + sSocialId + "/picture?type=large";
                                final String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                Log.e("FB", socialId + "===" + email + "===" + fullname + "===" + profileImage + "===" + deviceToken);

                                if (object.has("email")) {
                                    email = object.getString("email");
                                    reg_info.email = email;
                                } else {
                                    reg_info.email = "";
                                }
                                reg_info.socialId = socialId;
                                reg_info.socialType = "facebook";
                                reg_info.fullName = fullname;
                                reg_info.profileImage = profileImage;

                               // session.createRegistrationInfo(reg_info);

                                checkSocialLogin(reg_info.socialId, reg_info.socialType);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingView.setVisibility(View.GONE);
                            }

                        }

                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, picture");
                request.setParameters(parameters);
                request.executeAsync();

               /* Utils.openAlertDialog(SelectMaleFemaleActivity.this, getResources().getString(R.string.alert_registration_questioniare));*/
            }

            @Override
            public void onCancel() {
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                loadingView.setVisibility(View.GONE);
            }
        });
    }


 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.e("SIGNUP GMAIL ACCOUNT", account.getEmail() + "");
            Log.e("Name", account.getDisplayName());
            Log.e("Social Id", account.getId());

            reg_info.email = account.getEmail();
            reg_info.socialId = account.getId();
            reg_info.socialType = "gmail";
            reg_info.fullName = account.getDisplayName();
            if (account.getPhotoUrl() != null) {
                reg_info.profileImage = account.getPhotoUrl().toString();
            } else {
                reg_info.profileImage = "";
            }

            session.createRegistrationInfo(reg_info);

            checkSocialLogin(reg_info.socialId, reg_info.socialType);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("signInResult:failed =", e.getStatusCode() + "");

        }
    }

    private void checkSocialLogin(String socialId, String socialType) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("socialId", socialId);
        map.put("socialType", socialType);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        SignInInfo signInInfo= gson.fromJson(response, SignInInfo.class);

                        session.createSession(signInInfo);
                        isProfileUpdate = signInInfo.userDetail.isProfileUpdate;

                        //addUserFirebaseDatabase(isProfileUpdate);
                        // for video call active below line
                        startSignUpNewUser(createUserWithEnteredData(signInInfo.userDetail.fullName,signInInfo.userDetail.email));

                    } else {
                        if(message.equals("Currently you are not authorized to login")){
                            Utils.openAlertDialog(SelectMaleFemaleActivity.this, message);
                        }else Utils.openAlertDialog(SelectMaleFemaleActivity.this, getResources().getString(R.string.alert_registration_questioniare));
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
        service.callSimpleVolley("checkSocialRegistor", map);

    }


    @Override
    public void onBackPressed() {
        if (select_male_female_layout.getVisibility() == View.VISIBLE) {
            finish();
        } else backPressMethod();
    }

    private void addUserFirebaseDatabase(final String isProfileUpdate) {
        final Session app_session = new Session(SelectMaleFemaleActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM infoFCM = new UserInfoFCM();
        infoFCM.uid = app_session.getUser().userDetail.userId;
        infoFCM.email = app_session.getUser().userDetail.email;
        infoFCM.firebaseId = "";
        infoFCM.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFCM.name = app_session.getUser().userDetail.fullName;
        infoFCM.isNotification = Constant.Notication_on;
        if(userForSave != null){
            infoFCM.quickBloxId = userForSave.getId().toString();
        }else  infoFCM.quickBloxId = "";


        if( app_session.getUser().userDetail.profileImage.size() > 0){
            infoFCM.profilePic = app_session.getUser().userDetail.profileImage.get(0).image;
        }else infoFCM.profilePic = "";

        database.child(Constant.ARG_USERS)
                .child(app_session.getUser().userDetail.userId)
                .setValue(infoFCM)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        loadingView.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Utils.goToOnlineStatus(SelectMaleFemaleActivity.this,Constant.online);
                            if(isProfileUpdate.equals("1")){
                                MainActivity.start(SelectMaleFemaleActivity.this, false);
                                finish();
                            }else {
                                Intent intent =new Intent(SelectMaleFemaleActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(SelectMaleFemaleActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*................................................video call start ...........................................................*/

    private void startSignUpNewUser(final QBUser newUser) {
        loadingView.setVisibility(View.VISIBLE);
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, true);
                        } else {
                            addUserFirebaseDatabase(isProfileUpdate);
                            loadingView.setVisibility(View.GONE);
                            //Toast.makeText(SelectMaleFemaleActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);

        userForSave = qbUser;
        startLoginService(qbUser);
    }

    private void startOpponentsActivity() {
        sendQuickBlockIdToServer(userForSave.getId().toString());
        addUserFirebaseDatabase(isProfileUpdate);

    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }


    private QBUser createUserWithEnteredData(String userName,String email) {
        return createQBUserWithCurrentData(userName,
                "mychatroom",email);
    }

    private QBUser createQBUserWithCurrentData(String userName, String chatRoomName, String email) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(chatRoomName)) {
            StringifyArrayList<String> userTags = new StringifyArrayList<>();
            userTags.add(chatRoomName);

            qbUser = new QBUser();
            qbUser.setFullName(userName);
            qbUser.setLogin(email);
            qbUser.setEmail(email);
            qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
            qbUser.setTags(userTags);
        }

        return qbUser;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            loadingView.setVisibility(View.GONE);
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);
                signInCreatedUser(userForSave, false);
            } else {
                Toast.makeText(SelectMaleFemaleActivity.this, getString(R.string.login_chat_login_error) + errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new com.apoim.groupchatwebrtc.utils.QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {

                if (deleteCurrentUser) {
                    removeAllUserData(result);
                } else {
                    startOpponentsActivity();
                }
            }

            @Override
            public void onError(QBResponseException responseException) {
                loadingView.setVisibility(View.GONE);
              //  Toast.makeText(SelectMaleFemaleActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeAllUserData(final QBUser user) {
        Session app_session = new Session(SelectMaleFemaleActivity.this);
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData(app_session.getUser().userDetail.fullName,app_session.getUser().userDetail.email));
            }

            @Override
            public void onError(QBResponseException e) {
                loadingView.setVisibility(View.GONE);
               // Toast.makeText(SelectMaleFemaleActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
/*................................end video call ............................................................*/

    private void sendQuickBlockIdToServer(String quickBloxId) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("quickBloxId", quickBloxId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {


                    } else {
                        Utils.openAlertDialog(SelectMaleFemaleActivity.this, message);
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
        service.callSimpleVolley("user/saveCallingUserId", map);

    }

}
