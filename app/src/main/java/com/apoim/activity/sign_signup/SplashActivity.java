package com.apoim.activity.sign_signup;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.apoim.R;
import com.apoim.activity.MainActivity;
import com.apoim.activity.profile.EditProfileActivity;
import com.apoim.app.Apoim;
import com.apoim.groupchatwebrtc.activities.BaseActivity;
import com.apoim.groupchatwebrtc.services.CallService;
import com.apoim.groupchatwebrtc.utils.ErrorUtils;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.helper.Constant;
import com.apoim.modal.SignInInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.quickblox.users.model.QBUser;

import java.security.MessageDigest;


public class SplashActivity extends BaseActivity {
    Session session;


    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private SharedPrefsHelper sharedPrefsHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        session = new Session(this, this);

        //for quick blocks
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sharedPrefsHelper = SharedPrefsHelper.getInstance();

                if (sharedPrefsHelper.hasQbUser()) {
                    startLoginService(sharedPrefsHelper.getQbUser());
                    // from here to go main Activity
                    apoim_session();
                    return;
                } else {
                    // normal login
                    SignInInfo signInInfo = session.getUser();
                    if (signInInfo == null) {
                        signInInfo = new SignInInfo();
                    }

                    if (signInInfo.userDetail != null) {
                        Utils.goToOnlineStatus(SplashActivity.this, Constant.online);
                        if (signInInfo.userDetail.isProfileUpdate.equals("0")) {
                            startActivity(new Intent(SplashActivity.this, EditProfileActivity.class));
                            // startActivity(new Intent(SplashActivity.this, LogOutActivity.class));
                            // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAfterTransition();
                            }
                        } else if (signInInfo.userDetail.isProfileUpdate.equals("1")) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            // startActivity(new Intent(SplashActivity.this, LogOutActivity.class));
                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAfterTransition();
                            }
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        }
                    }

                }


                if (checkConfigsWithSnackebarError()) {
                   // proceedToTheNextActivityWithDelay();
                }
            }
        }, secondsDelayed * 3000);

        //for Normal
     /*   session = new Session(this, this);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SignInInfo signInInfo = session.getUser();
                if (signInInfo == null) {
                    signInInfo = new SignInInfo();
                }

                if (signInInfo.userDetail != null) {
                    Utils.goToOnlineStatus(SplashActivity.this, Constant.online);
                    if (signInInfo.userDetail.isProfileUpdate.equals("0")) {
                        startActivity(new Intent(SplashActivity.this, EditProfileActivity.class));
                        // startActivity(new Intent(SplashActivity.this, LogOutActivity.class));
                       // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        }
                    } else if (signInInfo.userDetail.isProfileUpdate.equals("1")) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        // startActivity(new Intent(SplashActivity.this, LogOutActivity.class));
                        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        }
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    }
                }
            }
        }, secondsDelayed * 3000);
*/

    }


    /**
     * Changes the System Bar Theme.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    protected void proceedToTheNextActivity() {
        SignInActivity.start(this);
        finish();
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }

    private void startOpponentsActivity() {
        MainActivity.start(SplashActivity.this, false);
        finish();
    }

    protected boolean sampleConfigIsCorrect() {
        return Apoim.getInstance().getQbConfigs() != null;
    }

    protected void proceedToTheNextActivityWithDelay() {
        mainThreadHandler.postDelayed(this::proceedToTheNextActivity, 1500);
    }

    protected boolean checkConfigsWithSnackebarError() {
        if (!sampleConfigIsCorrect()) {
            showSnackbarErrorParsingConfigs();
            return false;
        }

        return true;
    }

    protected void showSnackbarErrorParsingConfigs() {
        ErrorUtils.showSnackbar(findViewById(R.id.layout_root), R.string.error_parsing_configs, R.string.dlg_ok, null);
    }

    private void getKeyHashFacebook() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
        }
    }

    private void apoim_session() {
        SignInInfo signInInfo = session.getUser();
        if (signInInfo == null) {
            signInInfo = new SignInInfo();
        }

        if (signInInfo.userDetail != null) {
            Utils.goToOnlineStatus(SplashActivity.this, Constant.online);
            if (signInInfo.userDetail.isProfileUpdate.equals("0")) {
                startActivity(new Intent(SplashActivity.this, EditProfileActivity.class));
                // startActivity(new Intent(SplashActivity.this, LogOutActivity.class));
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }
            } else if (signInInfo.userDetail.isProfileUpdate.equals("1")) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
               /* overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }*/
                startOpponentsActivity();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, SelectMaleFemaleActivity.class));
            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
        }
    }
}
