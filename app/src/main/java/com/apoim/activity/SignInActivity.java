package com.apoim.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.apoim.helper.Validation;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anil on 2/13/2018.
 **/

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private TextView create_signIn, forgot_password;
    private EditText sign_in_email, sign_in_password, forgot_email;
    private TextView sign_in_button, forgot_submit_button;
    private RelativeLayout signin_facebook, signin_gmail;
    private InsLoadingView loadingView;
    private Session session;
    private PreRegistrationInfo reg_info;
    private ImageButton forgot_back;
    // Facebook Login
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    static boolean SignInFBloggedIn;
    private QBUser userForSave;
    // Gmail Integration
    GoogleSignInClient signInClient;
    private String isProfileUpdate = "";


    public static void start(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();

        session = new Session(SignInActivity.this, this);
        reg_info = new PreRegistrationInfo();
        // Facebook Integration
        SignInFBloggedIn = false;
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        facebookLogin();

        // Gmail Integration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        signin_facebook.setOnClickListener(this);
        signin_gmail.setOnClickListener(this);

        create_signIn.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        forgot_password.setOnClickListener(this);

    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void initView() {
        signin_facebook = findViewById(R.id.signin_facebook);
        signin_gmail = findViewById(R.id.signin_gmail);

        create_signIn = findViewById(R.id.create_signIn);
        sign_in_email = findViewById(R.id.sign_in_email);
        sign_in_password = findViewById(R.id.sign_in_password);
        sign_in_button = findViewById(R.id.sign_in_button);
        forgot_password = findViewById(R.id.forgot_password);
        loadingView = findViewById(R.id.loadingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_facebook:
                if (Utils.IsNetPresent(SignInActivity.this)) {
                    performLogin();
                } else {
                    Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.signin_gmail:
                if (Utils.IsNetPresent(SignInActivity.this)) {
                    Intent signInIntent = signInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
                } else {
                    Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.create_signIn:
                startActivity(new Intent(SignInActivity.this, SelectMaleFemaleActivity.class));
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                break;

            case R.id.sign_in_button:
                Utils.hideSoftKeyboard(this);
                if (Utils.IsNetPresent(SignInActivity.this)) {
                    if (isValidData()) {
                        loginTask(sign_in_email.getText().toString(), sign_in_password.getText().toString());
                    }
                } else {
                    Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.forgot_password:
                forgotPassDialog();
                break;

        }
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
                        }else {
                            loadingView.setVisibility(View.GONE);
                            addUserFirebaseDatabase(isProfileUpdate);
                        }


                        if(e.getHttpStatusCode() == Consts.SERVERIN_MAINTINANCE){
                            loadingView.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Quick blox Server is on maintenance", Toast.LENGTH_LONG).show();
                        }
                        else {
//                            addUserFirebaseDatabase(isProfileUpdate);
                           // loadingView.setVisibility(View.GONE);
                            //Toast.makeText(SignInActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
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


    private QBUser createUserWithEnteredData(String userName, String email) {
        return createQBUserWithCurrentData(userName,
                "mychatroom", email);
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
            //loadingView.setVisibility(View.GONE);
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);
                signInCreatedUser(userForSave, false);
            } else {
                addUserFirebaseDatabase(isProfileUpdate);
              //  Toast.makeText(SignInActivity.this, getString(R.string.login_chat_login_error) + errorMessage, Toast.LENGTH_LONG).show();
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

                //Toast.makeText(SignInActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeAllUserData(final QBUser user) {
        Session app_session = new Session(SignInActivity.this);
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData(app_session.getUser().userDetail.fullName, app_session.getUser().userDetail.email));

            }

            @Override
            public void onError(QBResponseException e) {
                loadingView.setVisibility(View.GONE);
                //Toast.makeText(SignInActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
/*..................................................................................................................*/

    private void forgotPassDialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.forgot_password_layout))
                .setGravity(Gravity.TOP)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View view = dialog.getHolderView();
        forgot_back = view.findViewById(R.id.forgot_back);
        forgot_email = view.findViewById(R.id.forgot_email);
        forgot_submit_button = view.findViewById(R.id.forgot_submit_button);
        final InsLoadingView loadingView_dialog = view.findViewById(R.id.loadingView_dialog);

        forgot_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        forgot_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(SignInActivity.this);
                if (Utils.IsNetPresent(SignInActivity.this)) {
                    if (isForgotDataValid()) {
                        forgotPasswordTask(dialog, forgot_email.getText().toString().trim(), loadingView_dialog);
                    }
                } else {
                    Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_network_check));
                }
            }
        });
        dialog.show();
    }

    private void forgotPasswordTask(final DialogPlus dialogPlus, String email, final InsLoadingView loadingView_dialog) {
        loadingView_dialog.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("email", email);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView_dialog.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        if (dialogPlus != null) {
                            dialogPlus.dismiss();
                        }
                        Utils.openAlertDialog(SignInActivity.this, message);
                    } else {
                        Utils.openAlertDialog(SignInActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView_dialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("forgotPassword", map);

    }

    private void checkSocialLogin(final String socialId, final String socialType) {
        loadingView.setVisibility(View.VISIBLE);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> map = new HashMap<>();
        map.put("socialId", socialId);
        map.put("socialType", socialType);
        map.put("deviceToken", deviceToken);


        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        SignInInfo signInInfo = gson.fromJson(response, SignInInfo.class);
                        session.createSession(signInInfo);
                        isProfileUpdate = signInInfo.userDetail.isProfileUpdate;

                        //for video call active this line
                        startSignUpNewUser(createUserWithEnteredData(signInInfo.userDetail.fullName, signInInfo.userDetail.email));


                       // addUserFirebaseDatabase(isProfileUpdate);


                    } else if (message.equals("Currently you are not authorized to login")) {
                        loadingView.setVisibility(View.GONE);
                        Utils.openAlertDialog(SignInActivity.this, message);
                    } else if (status.equals("fail")) {
                        loadingView.setVisibility(View.GONE);
                        Constant.isSocial = true;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setCancelable(false);
                        if (socialType.equals("facebook")) {
                            builder.setMessage(getResources().getString(R.string.alert_registration_questioniare_facebook));

                        } else
                            builder.setMessage(getResources().getString(R.string.alert_registration_questioniare_gmail));

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                startActivity(new Intent(SignInActivity.this, SelectMaleFemaleActivity.class));
                                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finish();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
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
        service.callSimpleVolley("checkSocialRegistor", map);

    }

    private void loginTask(String sign_in_email, final String sign_in_password) {
        loadingView.setVisibility(View.VISIBLE);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Map<String, String> map = new HashMap<>();
        map.put("email", sign_in_email);
        map.put("password", sign_in_password);
        map.put("deviceToken", deviceToken);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                //loadingView.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        SignInInfo signInInfo = gson.fromJson(String.valueOf(jsonObject), SignInInfo.class);
                        session.createSession(signInInfo);
                        session.savePassword(sign_in_password);

                        //for video calling active below line
                        startSignUpNewUser(createUserWithEnteredData(signInInfo.userDetail.fullName, signInInfo.userDetail.email));
                        //addUserFirebaseDatabase(signInInfo.userDetail.isProfileUpdate);

                        isProfileUpdate = signInInfo.userDetail.isProfileUpdate;
                    } else {
                        loadingView.setVisibility(View.GONE);
                        Utils.openAlertDialog(SignInActivity.this, message);
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
        service.callSimpleVolley("userLogin", map);

    }

    private void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadingView.setVisibility(View.VISIBLE);

                String accessToken = loginResult.getAccessToken().getToken();
                final String sSocialId = loginResult.getAccessToken().getUserId();
                final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError() != null) {
                            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_api_fail));
                        } else {
                            Log.e("SIGN UP RESPONSE", response.toString());
                            Log.e("SIGN UP OBJECT", object + "");

                            SignInFBloggedIn = true;

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

                                session.createRegistrationInfo(reg_info);

                                checkSocialLogin(reg_info.socialId, reg_info.socialType);

                                //loadingView.setVisibility(View.GONE);

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
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void performLogin() {
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }


/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.e("SIGNIN GMAIL ACCOUNT", account.getEmail() + "");

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
            Log.e("signInResult:failed =", e.getStatusCode() + "");

        }
    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(sign_in_email.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_email_null));

            return false;
        } else if (!v.isEmailValid(sign_in_email.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_invalid_email));

            return false;
        } else if (!v.isNullValue(sign_in_password.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_password_null));

            return false;
        } else if (!v.isPasswordValid(sign_in_password.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_invalid_password));
            return false;
        }

        return true;
    }

    public boolean isForgotDataValid() {
        Validation val = new Validation();

        if (!val.isNullValue(forgot_email.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_email_null));

            return false;
        } else if (!val.isEmailValid(forgot_email.getText().toString().trim())) {
            Utils.openAlertDialog(SignInActivity.this, getResources().getString(R.string.alert_invalid_email));

            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInActivity.this, SelectMaleFemaleActivity.class));
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    private void addUserFirebaseDatabase(final String isProfileUpdate) {
        final Session app_session = new Session(SignInActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM infoFCM = new UserInfoFCM();
        infoFCM.uid = app_session.getUser().userDetail.userId;
        infoFCM.email = app_session.getUser().userDetail.email;
        infoFCM.firebaseId = "";
        infoFCM.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFCM.name = app_session.getUser().userDetail.fullName;
        infoFCM.isNotification = Constant.Notication_on;
        infoFCM.authToken = app_session.getUser().userDetail.authToken;
        if(userForSave != null){
            infoFCM.quickBloxId = userForSave.getId().toString();
        }else  infoFCM.quickBloxId = "";


        if (app_session.getUser().userDetail.profileImage.size() != 0 && app_session.getUser().userDetail.profileImage != null) {
            infoFCM.profilePic = app_session.getUser().userDetail.profileImage.get(0).image;
        } else infoFCM.profilePic = "";

        database.child(Constant.ARG_USERS)
                .child(app_session.getUser().userDetail.userId)
                .setValue(infoFCM)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.goToOnlineStatus(SignInActivity.this, Constant.online);

                            if (isProfileUpdate.equals("1")) {
                                MainActivity.start(SignInActivity.this, false);
                                finish();
                            } else {
                                Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();

                              /*  MainActivity.start(SignInActivity.this, false);
                                finish();*/
                            }
                        } else {
                            Toast.makeText(SignInActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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
                        Utils.openAlertDialog(SignInActivity.this, message);
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
