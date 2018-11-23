package com.apoim.activity.sign_signup;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.business.RegisterBusinessActivity;
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
import com.apoim.multipleFileUpload.MultiPartRequest;
import com.apoim.multipleFileUpload.Template;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.LocationRuntimePermission;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by abc on 2/13/2018.
 */

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ImageButton register_back;
    private TextView create_account_button;
    private EditText account_password, account_name, account_email;
    private TextView account_birthday;
    private int mYear, mMonth, mDay;
    private RelativeLayout create_account_layout;
    private PreRegistrationInfo preRegistrationInfo;
    private InsLoadingView loadingView;
    private Session session;
    private ImageView profileImage;
    private Bitmap bitmap;
    private boolean isGPSEnabled, isNetworkEnabled;
    private LocationManager locationManager;
    private Location location;
    private Context context;
    private String address = "";
    private MultiPartRequest mMultiPartRequest;
    private QBUser userForSave;
    private LinearLayout ly_email, ly_account_password;

    // Get Current Location
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initView();
        session = new Session(CreateAccountActivity.this, this);

        // Get Latitude and Longitude
        locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (checkPlayServices()) {
            //Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        displayLocation();

        create_account_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Constant.hideSoftKeyboard(CreateAccountActivity.this);
                return false;
            }
        });

        register_back.setVisibility(View.VISIBLE);
        register_back.setOnClickListener(this);
        create_account_button.setOnClickListener(this);

        if (Utils.IsNetPresent(CreateAccountActivity.this)) {
            preRegistrationInfo = new PreRegistrationInfo();
            preRegistrationInfo = session.getRegistrationInfo();

            if (preRegistrationInfo.socialType != null) {
                if (preRegistrationInfo.socialType.equals("facebook")) {

                    if (preRegistrationInfo.email.equals("")) {
                        ly_email.setVisibility(View.GONE);
                    } else {
                        account_email.setText(preRegistrationInfo.email);
                        ly_email.setVisibility(View.VISIBLE);
                        account_email.setEnabled(false);

                    }

                    ly_account_password.setVisibility(View.GONE);

                    account_name.setText(preRegistrationInfo.fullName);
                    account_name.setSelection(account_name.length());
                    //account_name.setEnabled(false);

                    if (!preRegistrationInfo.profileImage.equals("") && preRegistrationInfo.profileImage != null) {
                        Glide.with(this).load(preRegistrationInfo.profileImage).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(profileImage);
                    }

                } else if (preRegistrationInfo.socialType.equals("gmail")) {

                    if (preRegistrationInfo.email.equals("")) {
                        ly_email.setVisibility(View.GONE);
                    } else {
                        account_email.setText(preRegistrationInfo.email);
                        ly_email.setVisibility(View.VISIBLE);
                        account_email.setEnabled(false);
                    }

                    ly_account_password.setVisibility(View.GONE);

                    account_name.setText(preRegistrationInfo.fullName);
                    account_name.setSelection(account_name.length());

                    Glide.with(this).load(preRegistrationInfo.profileImage).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(profileImage);
                }


            }else {
                ly_email.setVisibility(View.GONE);
            }
        }
        else {
            ly_email.setVisibility(View.GONE);
        }
    }


    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void initView() {
        create_account_layout = findViewById(R.id.create_account_layout);
        register_back = findViewById(R.id.register_back);
        create_account_button = findViewById(R.id.create_account_button);
        account_password = findViewById(R.id.account_password);
        account_name = findViewById(R.id.account_name);
        account_name.setInputType(InputType.TYPE_CLASS_TEXT);
        account_birthday = findViewById(R.id.account_birthday);
        loadingView = findViewById(R.id.loadingView);
        profileImage = findViewById(R.id.profileImage);
        ly_email = findViewById(R.id.ly_email);
        ly_account_password = findViewById(R.id.ly_account_password);
        account_email = findViewById(R.id.account_email);

        account_birthday.setOnClickListener(this);
        profileImage.setOnClickListener(this);
    }

    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(CreateAccountActivity.this);
            }
        } else {
            ImagePicker.pickImage(CreateAccountActivity.this);
        }
    }

    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_birthday:
                Constant.hideSoftKeyboard(CreateAccountActivity.this);
                openDatePicker();
                PreRegistrationInfo r_info = session.getRegistrationInfo();
                r_info.birthday = account_birthday.getText().toString();
                session.createRegistrationInfo(r_info);
                break;

            case R.id.register_back:
                onBackPressed();
                break;

            case R.id.profileImage:
                getPermissionAndPicImage();
                break;


            case R.id.create_account_button:

                if (isValidData()) {
                    if (Utils.IsNetPresent(CreateAccountActivity.this)) {
                        PreRegistrationInfo preRegistrationInfo = session.getRegistrationInfo();

                        //Preventing multiple clicks, using threshold of 5 second
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        if ((preRegistrationInfo.latitude == null) && (preRegistrationInfo.longitude == null)) {
                            displayLocation();
                        } else {
                            if (preRegistrationInfo.socialType != null) {
                                if (preRegistrationInfo.socialType.equals("facebook")) {
                                    preRegistrationInfo = session.getRegistrationInfo();

                                    signupTask(preRegistrationInfo.contactNo, preRegistrationInfo.countryCode,
                                            preRegistrationInfo.fullName, preRegistrationInfo.email, preRegistrationInfo.latitude,
                                            preRegistrationInfo.longitude, preRegistrationInfo.deviceToken, "2", preRegistrationInfo.profileImage,
                                            preRegistrationInfo.socialId, preRegistrationInfo.socialType, preRegistrationInfo.gender, preRegistrationInfo.birthday,
                                            preRegistrationInfo.purpose, preRegistrationInfo.dateWith, preRegistrationInfo.eventInvitation);

                                } else if (preRegistrationInfo.socialType.equals("gmail")) {

                                    preRegistrationInfo = session.getRegistrationInfo();

                                    signupTask(preRegistrationInfo.contactNo, preRegistrationInfo.countryCode,
                                            preRegistrationInfo.fullName, preRegistrationInfo.email, preRegistrationInfo.latitude,
                                            preRegistrationInfo.longitude, preRegistrationInfo.deviceToken, "2", preRegistrationInfo.profileImage,
                                            preRegistrationInfo.socialId, preRegistrationInfo.socialType, preRegistrationInfo.gender, preRegistrationInfo.birthday,
                                            preRegistrationInfo.purpose, preRegistrationInfo.dateWith, preRegistrationInfo.eventInvitation);

                                }
                            } else {
                                /*PreRegistrationInfo reg_info = session.getRegistrationInfo();
                                reg_info.email = account_email.getText().toString();
                                reg_info.fullName = account_name.getText().toString();
                                reg_info.birthday = account_birthday.getText().toString();
                                session.createRegistrationInfo(reg_info);*/

                                preRegistrationInfo = session.getRegistrationInfo();

                                signupTask(preRegistrationInfo.contactNo, preRegistrationInfo.countryCode,
                                        preRegistrationInfo.fullName, preRegistrationInfo.email, preRegistrationInfo.latitude,
                                        preRegistrationInfo.longitude, preRegistrationInfo.deviceToken, "2", "",
                                        "", "", preRegistrationInfo.gender, preRegistrationInfo.birthday,
                                        preRegistrationInfo.purpose, preRegistrationInfo.dateWith, preRegistrationInfo.eventInvitation);

                            }
                        }

                    } else {
                        Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_network_check));
                    }

                }
                break;
        }
    }


    void signupTask(String contactNo, String countryCode, String fullName, String email, String latitude, String longitude, String deviceToken, String deviceType, String profileImage, final String socialId, String socialType, String gender,
                    String birthday, String purpose, String dateWith, String eventInvitation) {

        loadingView.setVisibility(View.VISIBLE);

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        String date = account_birthday.getText().toString();

        Map<String, String> map = new HashMap<>();
        if (countryCode != null)
            map.put("contactNo", countryCode);
        if (contactNo != null)
            map.put("countryCode", contactNo);
        map.put("fullName", account_name.getText().toString().trim());
        map.put("email", email);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("address", address);
        map.put("deviceToken", deviceToken);
        map.put("deviceType", deviceType);
        map.put("socialId", socialId);
        map.put("socialType", socialType);
        map.put("gender", gender);
        map.put("birthday", date);
        map.put("purpose", purpose);
        map.put("dateWith", dateWith);
        map.put("password", account_password.getText().toString().trim());
        if(eventInvitation.equals("")){
            eventInvitation = "3"; // 3 is for both
        }
        map.put("eventInvitation", eventInvitation);


        ArrayList<File> fileList = new ArrayList<>();

        if (preRegistrationInfo.socialType != null) {

            if (!preRegistrationInfo.socialType.equals("")) {
                if (preRegistrationInfo.profileImage != null)
                    map.put("profileImage", preRegistrationInfo.profileImage);
            } else {
                map.put("profileImage", "");
            }
        } else {
            if (bitmap != null) {
                fileList = new ArrayList<>();
                fileList.add(bitmapToFile(bitmap));
            } else {
                map.put("profileImage", "");
            }
        }


        mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingView.setVisibility(View.GONE);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        Gson gson = new Gson();
                        String responce = String.valueOf(response);
                        SignInInfo signInInfo = gson.fromJson(responce, SignInInfo.class);

                        session.createSession(signInInfo);
                        //addUserFirebaseDatabase();
                        //for video call active below line
                        startSignUpNewUser(createUserWithEnteredData(signInInfo.userDetail.fullName, signInInfo.userDetail.email));

                    } else {
                        loadingView.setVisibility(View.GONE);
                        Utils.openAlertDialog(CreateAccountActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            //  }, productImages, productImages.size(), params, ProfileActivity.this);
        }, fileList, fileList.size(), map, CreateAccountActivity.this, "profileImage", "userRegistration");

        //Set tag
        mMultiPartRequest.setTag("MultiRequest");

        //Set retry policy
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Apoim.getInstance().addToRequestQueue(mMultiPartRequest, "UPLOAD");
    }


    public File bitmapToFile(Bitmap bmp) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAccountActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if ((dayOfMonth < 10) && (monthOfYear + 1) < 10) {
                            //account_birthday.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));

                            account_birthday.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                        } else if ((dayOfMonth < 10) && (monthOfYear + 1) >= 10) {
                            //account_birthday.setText("0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));

                            account_birthday.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                        } else if ((dayOfMonth >= 10) && (monthOfYear + 1) <= 10) {
                            //account_birthday.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));

                            account_birthday.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                        } else {
                            //account_birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));

                            account_birthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }
                }, mYear - 18, mMonth, mDay);

        c.set(mYear - 18, mMonth, mDay);
        long value = c.getTimeInMillis();
        datePickerDialog.getDatePicker().setMaxDate(value);
        datePickerDialog.show();

    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(account_name.getText().toString().trim())) {
            Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_name_null));
            return false;
        } else if (!v.isNameValid(account_name.getText().toString().trim())) {
            Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_name_null));
            return false;
        } else if (!v.isNameAlphabets(account_name.getText().toString().trim())) {
            Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_name_alphabets));
            return false;
        } else if (preRegistrationInfo.socialType == null) {
            if (!v.isNullValue(account_password.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_password_null));
                return false;
            } else if (!v.isPasswordValid(account_password.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.password_should_be_atleast));
                return false;
            } else if (!v.isNullValue(account_birthday.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_birthday_null));

                return false;
            }
        } else if (!v.isNullValue(account_birthday.getText().toString().trim())) {
            Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_birthday_null));

            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(0, R.anim.back_slide);
    }


    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        // Displaying the new location on UI
        displayLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void displayLocation() {
        if (LocationRuntimePermission.checkLocationPermission(CreateAccountActivity.this)) {

            LocationManager locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();
                    address = getAddress(latitude, longitude, CreateAccountActivity.this);
                    PreRegistrationInfo reg_info = session.getRegistrationInfo();
                    reg_info.latitude = latitude + "";
                    reg_info.longitude = longitude + "";
                    session.createRegistrationInfo(reg_info);
                    Log.e("LAT", latitude + "");
                    Log.e("LONG", longitude + "");
                }
            } else if (!isGPSEnabled) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Please Enable GPS");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            } else if (!isGPSEnabled && !isNetworkEnabled) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Please Enable Network and GPS");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocation();
                } else {
                    //Utils.openAlertDialog(CreateAccountActivity.this,"Location permission denied");
                    //displayLocation();
                }
                break;
        }
    }

    public String getAddress(double latitude, double longitude, Context mContext) {
        String user_address = "";
        // Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                user_address = address.getAddressLine(0);
            }

        } catch (IOException e) {
            Log.e(Apoim.TAG, "Unable connect to Geocoder", e);
        }

        if (user_address.equals("")) {
            return "NA";
        } else

            return user_address;
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Utils.openAlertDialog(CreateAccountActivity.this, getResources().getString(R.string.alert_play_services_check));

                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates

        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) CreateAccountActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) CreateAccountActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addUserFirebaseDatabase() {
        final Session app_session = new Session(CreateAccountActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM infoFCM = new UserInfoFCM();
        infoFCM.uid = app_session.getUser().userDetail.userId;
        infoFCM.email = app_session.getUser().userDetail.email;
        infoFCM.firebaseId = "";
        infoFCM.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFCM.name = app_session.getUser().userDetail.fullName;
        infoFCM.isNotification = Constant.Notication_on;
        infoFCM.authToken = app_session.getUser().userDetail.authToken;
        if (userForSave != null) {
            infoFCM.quickBloxId = userForSave.getId().toString();
        } else infoFCM.quickBloxId = "";

        if (app_session.getUser().userDetail.profileImage.size() > 0) {
            infoFCM.profilePic = app_session.getUser().userDetail.profileImage.get(0).image;
        } else infoFCM.profilePic = "";

        database.child(Constant.ARG_USERS)
                .child(app_session.getUser().userDetail.userId)
                .setValue(infoFCM)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        loadingView.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Utils.goToOnlineStatus(CreateAccountActivity.this, Constant.online);
                            Intent i = new Intent(CreateAccountActivity.this, RegisterBusinessActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        } else {
                            Toast.makeText(CreateAccountActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(CreateAccountActivity.this, requestCode, resultCode, data);

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(4, 3)
                            .start(this);
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                try {
                    if (result != null)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (bitmap != null) {

                        bitmap = ImagePicker.getImageResized(this, result.getUri());
                        profileImage.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
*/
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
                            addUserFirebaseDatabase();
                            loadingView.setVisibility(View.GONE);
                            // Toast.makeText(CreateAccountActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
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
        addUserFirebaseDatabase();

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
            loadingView.setVisibility(View.GONE);
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);
                signInCreatedUser(userForSave, false);
            } else {
                Toast.makeText(CreateAccountActivity.this, getString(R.string.login_chat_login_error) + errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {

                bitmap = ImagePicker.getImageFromResult(CreateAccountActivity.this, requestCode, resultCode, data);

                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);

                }

            }

    /*        if (resultCode == RESULT_OK) {
                if (requestCode == 234) {
                    Uri imageUri = ImagePicker.getImageURIFromResult(CreateAccountActivity.this, requestCode, resultCode, data);

                    try {
                        if (imageUri != null)
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        if (bitmap != null) {

                            bitmap = ImagePicker.getImageResized(this, imageUri);
                            bitmap = ImageRotator.rotateImageIfRequired(bitmap, imageUri);
                            profileImage.setImageBitmap(bitmap);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }*/

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
                //Toast.makeText(CreateAccountActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeAllUserData(final QBUser user) {
        Session app_session = new Session(CreateAccountActivity.this);
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData(app_session.getUser().userDetail.fullName, app_session.getUser().userDetail.email));
            }

            @Override
            public void onError(QBResponseException e) {
                loadingView.setVisibility(View.GONE);
                //Toast.makeText(CreateAccountActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
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
                        Utils.openAlertDialog(CreateAccountActivity.this, message);
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
