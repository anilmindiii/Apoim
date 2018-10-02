package com.apoim.activity.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.MainActivity;
import com.apoim.activity.SettingsActivity;
import com.apoim.adapter.AddInterestAdapter;
import com.apoim.adapter.ProfileEducationAdapter;
import com.apoim.adapter.ProfileHeightAdapter;
import com.apoim.adapter.ProfileISpeakAdapter;
import com.apoim.adapter.ProfileImageAdapter;
import com.apoim.adapter.ProfileRelationshipAdapter;
import com.apoim.adapter.ProfileWorkAdapter;
import com.apoim.adapter.ShowInterestAdapter;
import com.apoim.app.Apoim;
import com.apoim.cropper.CropImage;
import com.apoim.cropper.CropImageView;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.listener.GetInterestValueListener;
import com.apoim.listener.ProfileImageAdapterListener;
import com.apoim.listener.ProfileRelationListener;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.ImageBean;
import com.apoim.modal.ProfileInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.ProfileItemInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.modal.UserInfoFCM;
import com.apoim.multipleFileUpload.MultiPartRequest;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.LocationRuntimePermission;
import com.apoim.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static android.content.ContentValues.TAG;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private RecyclerView profile_horizontal_recycler;
    private ArrayList<Uri> myUriList;
    private ProfileImageAdapter imageAdapter;
    private RelativeLayout profile_layout;
    private int mYear, mMonth, mDay;
    private RelativeLayout rl_select_work, rl_select_education;
    private Session session;
    private SignInInfo signInInfo;

    private TextView profile_birthday, profile_select_location, profile_select_work,
            profile_select_education, profile_skip, profile_action_bar, setup_profile_text;
    private EditText profile_name;
    private RadioGroup rg_profile_gender, rg_profile_show_map;
    private RadioButton profile_male_radio, profile_female_radio, profile_transgender_radio, profile_map_yes_radio, profile_map_no_radio;

    private boolean isGPSEnabled, isNetworkEnabled;
    private LocationManager locationManager;
    private Bitmap bitmap;
    //ArrayList<File> productImages;
    private ImageView iv_back;
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

    private ArrayList<ProfileItemInfo> workList;
    private ArrayList<ProfileItemInfo> educationList;
    private DatePickerDialog fromDate;

    private MultiPartRequest mMultiPartRequest;
    private Map<String, String> params;

    private TextView profile_button;
    private String user_about_you, user_height, user_weight, user_relationship, user_I_speak, user_interest, allDetailsFilled;
    private String gender, showOnMap, educationId, workId;

    private InsLoadingView loading_view;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private String latitude, longitude, address;
    private GetOtherProfileInfo otherProfileInfo;

    private List<ImageBean> imageBeans;

    private String username = "";
    private int checker = 0;

    private LinearLayout ly_basic_info, ly_more_info;
    private TextView tv_basic_info, tv_more_info;

    /*...........user personal profile ........*/
    private RelativeLayout rl_select_height, rl_select_weight, rl_select_relationship, rl_select_I_speak;
    private TextView tv_user_height, tv_user_weight, tv_display_selected_unit, tv_weight_unit, tv_user_relationship, tv_user_I_speak;
    private ArrayList<ProfileItemInfo> heightInfoList;
    private ArrayList<String> weightInfoList;
    private ArrayList<ProfileItemInfo> relationshipInfoList;
    private ArrayList<ProfileItemInfo> IspeakInfoList;
    private String[] arrHeight = {"121.92 (4 feet 0 inches)", "127.00 (4 feet 1 inches)", "129.54 (4 feet 2 inches)", "132.08 (4 feet 3 inches)", "134.62 (4 feet 4 inches)", "137.16 (4 feet 5 inches)", "139.70 (4 feet 6 inches)", "142.24 (4 feet 7 inches)", "144.78 (4 feet 8 inches)", "147.32 (4 feet 9 inches)", "149.86 (4 feet 10 inches)", "149.86 (4 feet 11 inches)", "152.40 (5 feet 0 inches)", "154.94 (5 feet 1 inches)", "157.48 (5 feet 2 inches)", "160.02 (5 feet 3 inches)", "162.56 (5 feet 4 inches)", "165.10 (5 feet 5 inches)", "167.64 (5 feet 6 inches)", "170.18 (5 feet 7 inches)", "172.72 (5 feet 8 inches)", "175.26 (5 feet 9 inches)", "177.80 (5 feet 10 inches)", "180.34 (5 feet 11 inches)", "182.88 (6 feet 0 inches)", "185.42 (6 feet 1 inches)", "185.42 (6 feet 2 inches)", "190.50 (6 feet 3 inches)", "193.04 (6 feet 4 inches)", "195.58 (6 feet 5 inches)", "198.12 (6 feet 6 inches)", "200.66 (6 feet 7 inches)", "203.20 (6 feet 8 inches)", "208.28 (6 feet 9 inches)", "214.36 (6 feet 10 inches)", "220.44 (6 feet 11 inches)", "221.52 (7 feet 0 inches)", "232.60 (7 feet 1 inches)", "237.68 (7 feet 2 inches)", "244.76 (7 feet 3 inches)", "250.84 (7 feet 4 inches)", "256.92 (7 feet 5 inches)", "263.00 (7 feet 6 inches)", "268.08 (7 feet 7 inches)", "273.16 (7 feet 8 inches)", "278.24 (7 feet 9 inches)", "283.32 (7 feet 10 inches)", "288.40 (7 feet 11 inches)", "293.48 (8 feet 0 inches)", "298.56 (8 feet 1 inches)", "303.64 (8 feet 2 inches)", "308.72 (8 feet 3 inches)", "313.80 (8 feet 4 inches)", "318.88 (8 feet 5 inches)", "323.96 (8 feet 6 inches)", "329.04 (8 feet 7 inches)", "334.12 (8 feet 8 inches)", "339.20 (8 feet 9 inches)", "344.28 (8 feet 10 inches)", "349.28 (8 feet 11 inches)", "354.36 (9 feet 0 inches)", "359.44 (9 feet 1 inches)", "364.52 (9 feet 2 inches)", "369.60 (9 feet 3 inches)", "374.68 (9 feet 4 inches)", "379.76 (9 feet 5 inches)", "384.84 (9 feet 6 inches)", "389.92 (9 feet 7 inches)", "395.00 (9 feet 8 inches)", "400.08 (9 feet 9 inches)", "405.16 (9 feet 10 inches)", "410.24 (9 feet 11 inches)", "415.32 (10 feet 0 inches)"};
    private String[] arrRelatioship = {"Single", "Married", "Divorced", "Widowed"};
    private String[] arrISpeak = {"English", "Spanish", "French"};
    private String language = "", relationId = "", unit = "";
    private EditText ed_about_you;
    private ImageView user_add_interest;
    private ListView selector_interest_list_view;
    private ArrayList<ProfileInterestInfo> interestList;
    private ArrayList<ProfileInterestInfo> interestInfoList;
    private String selected_interest;
    private String user_interests = "";
    private ShowInterestAdapter showInterestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_edit_profile_layout);


        myUriList = new ArrayList<>();
        educationList = new ArrayList<>();
        workList = new ArrayList<>();
        imageBeans = new ArrayList<>();
        interestList = new ArrayList<>();
        interestInfoList = new ArrayList<>();

        initView();
        gender = rg_profile_gender.getCheckedRadioButtonId() == R.id.profile_male_radio ? Constant.PROFILE_MALE : Constant.PROFILE_FEMALE;
        showOnMap = rg_profile_show_map.getCheckedRadioButtonId() == R.id.profile_map_yes_radio ? Constant.SHOW_ON_MAP_YES : Constant.SHOW_ON_MAP_NO;

        session = new Session(EditProfileActivity.this, this);
        signInInfo = new SignInInfo();

        profile_horizontal_recycler.setHasFixedSize(true);


        imageBeans.add(0, null);

        imageAdapter = new ProfileImageAdapter(imageBeans, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(int position) {
                if (position == 0) {
                    getPermissionAndPicImage();
                }
            }
        });

        Utils.hideSoftKeyboard(this);

        autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                profile_select_location.setText(place.getAddress());

                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                address = String.valueOf(place.getAddress());

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        // Setting adapter for recycler view
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        profile_horizontal_recycler.setLayoutManager(mLayoutManager);
        profile_horizontal_recycler.setItemAnimator(new DefaultItemAnimator());
        profile_horizontal_recycler.setAdapter(imageAdapter);


        if (getIntent().getSerializableExtra("otherProfileInfo") != null) {

            otherProfileInfo = (GetOtherProfileInfo) getIntent().getSerializableExtra("otherProfileInfo");


            ed_about_you.setText(otherProfileInfo.UserDetail.about);
            tv_user_height.setText(otherProfileInfo.UserDetail.height);
            tv_user_weight.setText(otherProfileInfo.UserDetail.weight);

            user_interests = otherProfileInfo.UserDetail.interest;

            if (otherProfileInfo.UserDetail.relationship != null) {
                switch (otherProfileInfo.UserDetail.relationship) {
                    case "1": {
                        tv_user_relationship.setText("Single");
                        break;
                    }
                    case "2": {
                        tv_user_relationship.setText("Married");
                        break;
                    }
                    case "3": {
                        tv_user_relationship.setText("Divorced");
                        break;
                    }
                    case "4": {
                        tv_user_relationship.setText("Widowed");
                        break;
                    }
                }
            }


            if (otherProfileInfo.UserDetail.language != null) {

                if (otherProfileInfo.UserDetail.language.contains(", ") || otherProfileInfo.UserDetail.language.contains(",")) {
                    List<String> result = Arrays.asList(otherProfileInfo.UserDetail.language.split("\\s*,\\s*"));

                    for (int i = 0; i < IspeakInfoList.size(); i++) {
                        for (int j = 0; j < result.size(); j++) {
                            if (IspeakInfoList.get(i).name.equals(result.get(j))) {
                                language = IspeakInfoList.get(j).name + ", " + language;
                                IspeakInfoList.get(j).isChecked = true;
                            }
                        }
                    }

                    if (language.endsWith(", ")) {
                        language = language.substring(0, language.length() - 2);
                    }

                    tv_user_I_speak.setText(language);


                } else {
                    switch (otherProfileInfo.UserDetail.language) {
                        case "English": {
                            tv_user_I_speak.setText("English");
                            language = "English";
                            //languageId = "1";
                            IspeakInfoList.get(0).isChecked = true;
                            break;
                        }
                        case "Spanish": {
                            tv_user_I_speak.setText("Spanish");
                            language = "Spanish";
                            // languageId = "2";
                            IspeakInfoList.get(1).isChecked = true;
                            break;
                        }
                        case "French": {
                            tv_user_I_speak.setText("French");
                            language = "French";
                            // languageId = "3";
                            IspeakInfoList.get(2).isChecked = true;
                            break;
                        }
                    }
                }


            }


            if (!otherProfileInfo.UserDetail.interest.equals("")) {
                interestInfoList.clear();
                List<String> interestList = Arrays.asList(otherProfileInfo.UserDetail.interest.split(","));
                for (int i = 0; i < interestList.size(); i++) {
                    ProfileInterestInfo interestInfo = new ProfileInterestInfo();
                    interestInfo.interest = interestList.get(i);
                    interestInfoList.add(interestInfo);
                }
                showInterestAdapter.notifyDataSetChanged();

            }


            profile_name.setText(otherProfileInfo.UserDetail.fullName);
            profile_select_work.setText(otherProfileInfo.UserDetail.work);
            profile_select_education.setText(otherProfileInfo.UserDetail.education);
            profile_select_location.setText(otherProfileInfo.UserDetail.address);
            profile_birthday.setText(otherProfileInfo.UserDetail.birthday);
            educationId = otherProfileInfo.UserDetail.eduId;
            workId = otherProfileInfo.UserDetail.workId;

            user_about_you = otherProfileInfo.UserDetail.about;
            user_height = otherProfileInfo.UserDetail.height;
            user_weight = otherProfileInfo.UserDetail.weight;
            user_relationship = otherProfileInfo.UserDetail.relationship;
            user_I_speak = otherProfileInfo.UserDetail.language;
            user_interest = otherProfileInfo.UserDetail.interest;

            address = otherProfileInfo.UserDetail.address;
            latitude = otherProfileInfo.UserDetail.latitude;
            longitude = otherProfileInfo.UserDetail.longitude;

            profile_action_bar.setText(R.string.edit_profile);
            profile_skip.setVisibility(View.GONE);
            iv_back.setVisibility(View.VISIBLE);
            setup_profile_text.setVisibility(View.VISIBLE);

            // Set gender
            if (otherProfileInfo.UserDetail.gender.equals(Constant.REGISTER_MALE)) {
                profile_male_radio.setChecked(true);
                profile_female_radio.setChecked(false);
                profile_transgender_radio.setChecked(false);
                gender = Constant.PROFILE_MALE;

            } else if (otherProfileInfo.UserDetail.gender.equals(Constant.PROFILE_FEMALE)) {
                profile_transgender_radio.setChecked(false);
                profile_male_radio.setChecked(false);
                profile_female_radio.setChecked(true);
                gender = Constant.PROFILE_FEMALE;
            } else if (otherProfileInfo.UserDetail.gender.equals(Constant.REGISTER_Transgender)) {
                profile_transgender_radio.setChecked(true);
                profile_male_radio.setChecked(false);
                profile_female_radio.setChecked(false);
                gender = Constant.REGISTER_Transgender;
            }

            if (otherProfileInfo.UserDetail.showOnMap.equals(Constant.SHOW_ON_MAP_YES)) {
                profile_map_yes_radio.setChecked(true);
                profile_map_no_radio.setChecked(false);


            } else if (otherProfileInfo.UserDetail.showOnMap.equals(Constant.SHOW_ON_MAP_NO)) {
                profile_map_yes_radio.setChecked(false);
                profile_map_no_radio.setChecked(true);
            }

            loadImages();


        } else {
            signInInfo = session.getUser();
        }


        if (signInInfo.userDetail != null) {
            getProfileDetails(signInInfo.userDetail.userId);

            profile_name.setText(signInInfo.userDetail.fullName);
            profile_birthday.setText(signInInfo.userDetail.birthday);

            // Set gender
            if (signInInfo.userDetail.gender.equals(Constant.REGISTER_MALE)) {
                profile_male_radio.setChecked(true);
                profile_female_radio.setChecked(false);
                gender = Constant.PROFILE_MALE;

            } else if (signInInfo.userDetail.gender.equals(Constant.PROFILE_FEMALE)) {
                profile_male_radio.setChecked(false);
                profile_female_radio.setChecked(true);
                gender = Constant.PROFILE_FEMALE;
            } else if (signInInfo.userDetail.gender.equals(Constant.REGISTER_Transgender)) {
                profile_transgender_radio.setChecked(true);
                profile_male_radio.setChecked(false);
                profile_female_radio.setChecked(false);
                gender = Constant.REGISTER_Transgender;
            }

            if (signInInfo.userDetail.showOnMap != null) {
                if (signInInfo.userDetail.showOnMap.equals(Constant.SHOW_ON_MAP_YES)) {
                    profile_map_yes_radio.setChecked(true);
                    profile_map_no_radio.setChecked(false);


                } else if (signInInfo.userDetail.showOnMap.equals(Constant.SHOW_ON_MAP_NO)) {
                    profile_map_yes_radio.setChecked(false);
                    profile_map_no_radio.setChecked(true);
                }
            }

        }

        rg_profile_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.profile_male_radio) {
                    gender = Constant.PROFILE_MALE;
                } else if (i == R.id.profile_female_radio) {
                    gender = Constant.PROFILE_FEMALE;
                } else if (i == R.id.profile_transgender_radio) {
                    gender = Constant.REGISTER_Transgender;
                }
            }
        });

        // Show me on map function
        if (profile_map_yes_radio.isChecked()) {
            showOnMap = Constant.SHOW_ON_MAP_YES;
            profile_map_no_radio.setChecked(false);
        } else if (profile_map_no_radio.isChecked()) {
            profile_map_yes_radio.setChecked(false);
            showOnMap = Constant.SHOW_ON_MAP_NO;
        }

        rg_profile_show_map.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.profile_map_yes_radio) {
                    showOnMap = Constant.SHOW_ON_MAP_YES;
                    profile_map_yes_radio.setTextColor(getResources().getColor(R.color.colorPrimary));
                    profile_map_no_radio.setTextColor(getResources().getColor(R.color.colorBlack));
                } else if (i == R.id.profile_map_no_radio) {
                    showOnMap = Constant.SHOW_ON_MAP_NO;
                    profile_map_yes_radio.setTextColor(getResources().getColor(R.color.colorBlack));
                    profile_map_no_radio.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        // Get Latitude and Longitude
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        displayCurrentLocation();

        profile_birthday.setOnClickListener(this);
        rl_select_work.setOnClickListener(this);
        rl_select_education.setOnClickListener(this);
        profile_button.setOnClickListener(this);
        profile_skip.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        tv_basic_info.setOnClickListener(this);
        tv_more_info.setOnClickListener(this);

        /*....other profile......*/
        rl_select_height.setOnClickListener(this);
        rl_select_weight.setOnClickListener(this);
        rl_select_relationship.setOnClickListener(this);
        rl_select_I_speak.setOnClickListener(this);
        user_add_interest.setOnClickListener(this);

        interest_service(null, null, null, null, null,"");
    }

    private void getProfileDetails(String userId) {
        WebService service = new WebService(EditProfileActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        otherProfileInfo = gson.fromJson(response, GetOtherProfileInfo.class);

                        if (otherProfileInfo.UserDetail.profileImage.size() != 0 && otherProfileInfo.UserDetail.profileImage != null) {

                            for (int i = 0; i < otherProfileInfo.UserDetail.profileImage.size(); i++) {
                                imageBeans.add((i + 1), new ImageBean(otherProfileInfo.UserDetail.profileImage.get(i).image, null,
                                        otherProfileInfo.UserDetail.profileImage.get(i).userImgId));
                            }
                            imageAdapter.notifyDataSetChanged();

                           /* if (imageBeans.size() < 6) {
                                imageBeans.addAll(otherProfileInfo.userDetail.profileImage);
                                imageAdapter.notifyDataSetChanged();

                                imageBeans.add(1, new ImageBean(signInInfo.userDetail.profileImage.get(0).image, null, ""));
                            }*/

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
            }
        });
        service.callGetSimpleVolley("user/getUserProfile?userId=" + userId + "");

    }

    /*Rj*/
    private void loadImages() {
        for (int i = 0; i < otherProfileInfo.UserDetail.profileImage.size(); i++) {
            String imageURL = otherProfileInfo.UserDetail.profileImage.get(i).image;
            String imageId = otherProfileInfo.UserDetail.profileImage.get(i).userImgId;
            if (!TextUtils.isEmpty(imageURL)) {

                imageBeans.add(1, new ImageBean(imageURL, null, imageId));
                imageAdapter.notifyItemInserted(1);

            }

        }
    }

    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(EditProfileActivity.this);
            }
        } else {
            ImagePicker.pickImage(EditProfileActivity.this);
        }
    }

    private void initView() {
        profile_horizontal_recycler = findViewById(R.id.profile_horizontal_recycler);
        profile_name = findViewById(R.id.profile_name);
        profile_birthday = findViewById(R.id.profile_birthday);
        loading_view = findViewById(R.id.loading_view);

        rg_profile_gender = findViewById(R.id.rg_profile_gender);
        profile_male_radio = findViewById(R.id.profile_male_radio);
        profile_female_radio = findViewById(R.id.profile_female_radio);
        profile_transgender_radio = findViewById(R.id.profile_transgender_radio);

        rl_select_work = findViewById(R.id.rl_select_work);
        profile_select_work = findViewById(R.id.profile_select_work);

        rl_select_education = findViewById(R.id.rl_select_education);
        profile_select_education = findViewById(R.id.profile_select_education);

        rg_profile_show_map = findViewById(R.id.rg_profile_show_map);
        profile_map_yes_radio = findViewById(R.id.profile_map_yes_radio);
        profile_map_no_radio = findViewById(R.id.profile_map_no_radio);

        profile_select_location = findViewById(R.id.profile_select_location);
        profile_layout = findViewById(R.id.profile_layout);

        profile_button = findViewById(R.id.profile_button);
        profile_skip = findViewById(R.id.profile_skip);
        profile_action_bar = findViewById(R.id.profile_action_bar);
        iv_back = findViewById(R.id.iv_back);
        setup_profile_text = findViewById(R.id.setup_profile_text);

        ly_more_info = findViewById(R.id.ly_more_info);
        ly_basic_info = findViewById(R.id.ly_basic_info);
        tv_basic_info = findViewById(R.id.tv_basic_info);
        tv_more_info = findViewById(R.id.tv_more_info);

        /*................ another profile...........*/
        ed_about_you = findViewById(R.id.ed_about_you);
        user_add_interest = findViewById(R.id.user_add_interest);

        rl_select_height = findViewById(R.id.rl_select_height);
        tv_user_height = findViewById(R.id.tv_user_height);

        rl_select_weight = findViewById(R.id.rl_select_weight);
        tv_user_weight = findViewById(R.id.tv_user_weight);

        rl_select_relationship = findViewById(R.id.rl_select_relationship);
        tv_user_relationship = findViewById(R.id.tv_user_relationship);

        rl_select_I_speak = findViewById(R.id.rl_select_I_speak);
        tv_user_I_speak = findViewById(R.id.tv_user_I_speak);

        RecyclerView user_selected_interest_list_view = findViewById(R.id.user_selected_interest_list_view);
        int numberOfColumns = 2;
        user_selected_interest_list_view.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        relationshipInfoList = new ArrayList<>();
        IspeakInfoList = new ArrayList<>();
        heightInfoList = new ArrayList<>();
        weightInfoList = new ArrayList<>();


        weightInfoList.add("Weight");
        weightInfoList.add("Kg");
        weightInfoList.add("Lbs");
        weightInfoList.add("Pounds");

        // Height List from arrHeight String array
        ArrayList<String> heightList = new ArrayList<>();
        for (int i = 0; i < arrHeight.length; i++) {
            ProfileItemInfo profileHeightInfo = new ProfileItemInfo();
            arrHeight[i].split(",");
            heightList = new ArrayList<String>((Arrays.asList(arrHeight)));
            profileHeightInfo.name = heightList.get(i);
            profileHeightInfo.isChecked = false;
            heightInfoList.add(profileHeightInfo);
        }

        // Relation List from arrRelatioship String array
        ArrayList<String> relationList = new ArrayList<>();
        for (int i = 0; i < arrRelatioship.length; i++) {
            ProfileItemInfo profileRelationshipInfo = new ProfileItemInfo();
            arrRelatioship[i].split(",");
            relationList = new ArrayList<String>((Arrays.asList(arrRelatioship)));
            profileRelationshipInfo.name = relationList.get(i);
            profileRelationshipInfo.isChecked = false;
            relationshipInfoList.add(profileRelationshipInfo);
        }

        // I Speak List from arrISpeak String array
        ArrayList<String> ISpeakList = new ArrayList<>();
        for (int i = 0; i < arrISpeak.length; i++) {
            ProfileItemInfo profileISpeakInfo = new ProfileItemInfo();
            arrISpeak[i].split(",");
            ISpeakList = new ArrayList<String>((Arrays.asList(arrISpeak)));
            profileISpeakInfo.name = ISpeakList.get(i);
            profileISpeakInfo.Id = i + 1 + "";
            profileISpeakInfo.isChecked = false;
            IspeakInfoList.add(profileISpeakInfo);
        }

        for (int i = 0; i < relationshipInfoList.size(); i++) {
            if (relationshipInfoList.get(i).name.equals(tv_user_relationship.getText().toString())) {
                relationId = i + 1 + "";
            }
        }

        showInterestAdapter = new ShowInterestAdapter(Constant.UserPersonalProfile, EditProfileActivity.this, interestInfoList, new GetInterestValueListener() {

            @Override
            public void getInterestValue(String value) {
                if (user_interests.contains(value + ",")) {
                    user_interests = user_interests.replace(value + ",", "");
                } else if (user_interests.contains("," + value)) {
                    user_interests = user_interests.replace("," + value, "");
                }


                if (user_interests.endsWith(",")) {
                    user_interests = user_interests.substring(0, user_interests.length() - 1);
                    user_interest = user_interests;
                }
            }
        });
        user_selected_interest_list_view.setAdapter(showInterestAdapter);

        profile_name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checker = 0;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checker != 1) {
                    checker = 1;
                    String str = replaceWithPattern(s.toString(), " ");
                    if (str.startsWith(" ")) {
                        profile_name.setText("");
                    } else {
                        profile_name.setText(str);
                        profile_name.setSelection(str.length());
                    }

                }
            }
        });

    }

    public String replaceWithPattern(String str, String replace) {

        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        return mtch.replaceAll(replace);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_birthday:
                Constant.hideSoftKeyboard(EditProfileActivity.this);
                //openDatePicker();
                advanceDatePicker(profile_birthday, 18, true);
                break;

            case R.id.rl_select_work:
                if (Utils.IsNetPresent(EditProfileActivity.this)) {
                    if (workList.size() > 0) {
                        openProfileSelectWorkDialog(workList);
                    } else {
                        selectWorkTask();
                    }
                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.rl_select_education:
                if (Utils.IsNetPresent(EditProfileActivity.this)) {
                    if (educationList.size() > 0) {
                        openProfileSelectEducationDialog(educationList);
                    } else {
                        selectEducationTask();
                    }
                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

         /*   case R.id.rl_about_you:
                startActivityForResult(new Intent(EditProfileActivity.this, UserPersonalProfileActivity.class).putExtra("otherProfileInfo", otherProfileInfo),
                        Constant.PROFILE_REQUEST_CODE);
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
*/
            case R.id.profile_button:
                if (Utils.IsNetPresent(EditProfileActivity.this)) {
                    if (isValidData()) {
                        if (!profile_select_location.getText().toString().equals("")) {
                            updateProfileTask();
                        }
                        mGoogleApiClient.disconnect();
                        //stopLocationUpdates();
                    }
                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;
            case R.id.profile_skip:
                if (Utils.IsNetPresent(EditProfileActivity.this)) {
                    MainActivity.start(EditProfileActivity.this, false);
                    finish();
                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_basic_info:
                ly_more_info.setVisibility(View.GONE);
                ly_basic_info.setVisibility(View.VISIBLE);
                tv_basic_info.setTextColor(ContextCompat.getColor(this,R.color.black));
                tv_more_info.setTextColor(ContextCompat.getColor(this,R.color.colorGray));
                break;

            case R.id.tv_more_info:
                ly_basic_info.setVisibility(View.GONE);
                ly_more_info.setVisibility(View.VISIBLE);
                tv_more_info.setTextColor(ContextCompat.getColor(this,R.color.black));
                tv_basic_info.setTextColor(ContextCompat.getColor(this,R.color.colorGray));
                break;
                /*..........other profile......*/
            case R.id.rl_select_height:
                openSelectHeightDialog(heightInfoList);
                break;

            case R.id.rl_select_weight:
                openEnterWeightDialog(weightInfoList);
                break;

            case R.id.rl_select_relationship:
                openSelectRelationshipDialog(relationshipInfoList);
                break;

            case R.id.rl_select_I_speak:
                openSelectISpeakDialog(IspeakInfoList);
                break;

            case R.id.user_add_interest:
                if (Utils.IsNetPresent(EditProfileActivity.this)) {
                    openAddInterestDialog();
                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;
        }
    }

    public boolean isValidData() {
        Validation v = new Validation();

       /* if(productImages.size() < 1){
            Utils.openAlertDialog(EditProfileActivity.this, "Atleast one image should be select");
            return false;
        }*/
        if (!v.isNullValue(profile_name.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_profile_name_null));
            return false;
        } else if (!v.isLength3Minimum(profile_name.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, "Name should contain at least 3 characters");
            return false;
        } else if (!v.isLength25Max(profile_name.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, "Name can not more than 25 characters");
            return false;
        } else if (!v.isNullValue(profile_birthday.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_profile_birthday_null));
            return false;
        } else if (!v.isNullValue(tv_user_relationship.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_relation_null));
            return false;
        }else if (!v.isNullValue(tv_user_height.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_height_null));
            return false;
        } else if (!v.isNullValue(tv_user_weight.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_weight_null));
            return false;
        } else if (!v.isNullValue(profile_select_education.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_profile_education_null));
            return false;
        }
        else if (!v.isNullValue(profile_select_work.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_profile_work_null));
            return false;
        }else if (!v.isNullValue(tv_user_I_speak.getText().toString().trim())) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_I_speak_null));
            return false;
        }else if (interestInfoList.size() == 0) {
            Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_interest_null));
            return false;
        }

        else
            return true;
    }


    private void advanceDatePicker(final TextView setDate, int yearBackToOpen, boolean openFromGetDate) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        if (openFromGetDate) {
            if (!setDate.getText().toString().trim().equals("")) {

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

        c.set(c.get(Calendar.YEAR) - yearBackToOpen, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        fromDate.setMaxDate(c);
        fromDate.show(getFragmentManager(), "");

    }

    private void selectEducationTask() {
        rl_select_education.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        educationList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("educationList");
                        String education = null;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ProfileItemInfo profileItemInfo = new ProfileItemInfo();
                            JSONObject object = jsonArray.getJSONObject(i);

                            profileItemInfo.Id = object.getString("eduId");
                            profileItemInfo.name = object.getString("education");

                            educationList.add(profileItemInfo);
                        }

                        openProfileSelectEducationDialog(educationList);
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
        service.callGetSimpleVolley("getEducationList");

    }

    private void openProfileSelectEducationDialog(final ArrayList<ProfileItemInfo> educationList) {
        rl_select_education.setEnabled(false);
        final Dialog education_dialog = new Dialog(this);
        education_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        education_dialog.setContentView(R.layout.view_custom_dialog_profile);
        education_dialog.setCancelable(false);
        education_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = education_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_education_dialog));

        ImageView iv_refrence = education_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.education);

        ListView profile_dialog_listView = education_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = education_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                education_dialog.dismiss();
            }
        });

        String education_name = profile_select_education.getText().toString();

        ProfileEducationAdapter profileEducationAdapter = new ProfileEducationAdapter(EditProfileActivity.this, educationList, education_name, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(final int position) {
                profile_select_education.setText(educationList.get(position).name);
                educationId = educationList.get(position).Id;
                education_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileEducationAdapter);
        profileEducationAdapter.notifyDataSetChanged();

        education_dialog.getWindow().setGravity(Gravity.CENTER);
        education_dialog.show();

        education_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_education.setEnabled(true);
            }
        });
    }

    private void selectWorkTask() {
        rl_select_work.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        workList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("workList");
                        String name = null;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ProfileItemInfo workInfo = new ProfileItemInfo();
                            JSONObject object = jsonArray.getJSONObject(i);

                            workInfo.Id = object.getString("workId");
                            workInfo.name = object.getString("name");
                            workList.add(workInfo);
                        }

                        openProfileSelectWorkDialog(workList);
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
        service.callGetSimpleVolley("getWorkList");
    }

    private void openProfileSelectWorkDialog(final ArrayList<ProfileItemInfo> workList) {
        rl_select_work.setEnabled(false);
        final Dialog work_dialog = new Dialog(this);
        work_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        work_dialog.setContentView(R.layout.view_custom_dialog_profile);
        work_dialog.setCancelable(false);
        work_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = work_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_work_dialog));

        ImageView iv_refrence = work_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.work);

        ListView profile_dialog_listView = work_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView work_decline_button = work_dialog.findViewById(R.id.interest_decline_button);

        work_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                work_dialog.dismiss();
            }
        });

        String work_name = profile_select_work.getText().toString();

        ProfileWorkAdapter profileWorkAdapter = new ProfileWorkAdapter(EditProfileActivity.this, workList, work_name, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(final int position) {
                profile_select_work.setText(workList.get(position).name);
                workId = workList.get(position).Id;
                work_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileWorkAdapter);
        profileWorkAdapter.notifyDataSetChanged();

        work_dialog.getWindow().setGravity(Gravity.CENTER);
        work_dialog.show();

        work_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_work.setEnabled(true);
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        // Displaying the new location on UI
        displayCurrentLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayCurrentLocation();

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

    private void displayCurrentLocation() {
        if (LocationRuntimePermission.checkLocationPermission(EditProfileActivity.this)) {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
               /* mGoogleApiClient.disconnect();
                stopLocationUpdates();*/

                if ((mGoogleApiClient) != null) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (mLastLocation != null) {
                        double latitude = mLastLocation.getLatitude();
                        double longitude = mLastLocation.getLongitude();


                        Geocoder geocoder = new Geocoder(EditProfileActivity.this, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(
                                    latitude, longitude, 1);
                            if (addressList != null && addressList.size() > 0) {
                                Address address = addressList.get(0);
                                String user_address = address.getAddressLine(0);
                                if (profile_select_location.getText().toString().trim().equals("")) {
                                    profile_select_location.setText(user_address);
                                    this.latitude = String.valueOf(latitude);
                                    this.longitude = String.valueOf(longitude);
                                }

                                Log.e("Address", user_address);

                            }


                        } catch (IOException e) {
                            Log.e(Apoim.TAG, "Unable connect to Geocoder", e);
                        }
                    }
                }

            }
        }
    }

    private boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        // otherwise return false
        return false;
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
                Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_play_services_check));

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
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if (!isGPSEnabled()) {
            Utils.showGPSDisabledAlertToUser(EditProfileActivity.this);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) EditProfileActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        loading_view.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) EditProfileActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProfileInfo profileInfo = new ProfileInfo();
        session.createProfileInfo(profileInfo);
        ArrayList<ProfileInterestInfo> interestInfoList = new ArrayList<>();
        session.saveUserInterestList(interestInfoList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(EditProfileActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(EditProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(EditProfileActivity.this);
                } else {
                    Toast.makeText(EditProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;


            case Constant.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayCurrentLocation();
                } else {
                    Toast.makeText(EditProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }


    // onActivityResult for get Intent from Camera or Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {

                bitmap = ImagePicker.getImageFromResult(EditProfileActivity.this, requestCode, resultCode, data);

                if (bitmap != null) {
                    if (imageBeans.size() < 6) {
                        String userId = session.getUser().userDetail.userId;
                        imageUploadTask(userId, bitmap);
                    }
                }
            }
            if (resultCode != RESULT_CANCELED) {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == Constant.SELECT_FILE) {
                        // onSelectFromGalleryResult(data);
                    } else if (requestCode == Constant.REQUEST_CAMERA) {
                        //onCaptureImageResult(data);
                    } else if (requestCode == Constant.PROFILE_REQUEST_CODE) {
                        user_about_you = data.getStringExtra("user_about_you");
                        user_height = data.getStringExtra("user_height");
                        user_weight = data.getStringExtra("user_weight");
                        user_relationship = data.getStringExtra("user_relationship");
                        user_I_speak = data.getStringExtra("user_I_speak");
                        if (user_I_speak.contains(" ")) {
                            user_I_speak = user_I_speak.replace(" ", "");
                        }
                        user_interest = data.getStringExtra("user_interest");
                        allDetailsFilled = data.getStringExtra("allDetailsFilled");
                        user_interest = data.getStringExtra("interest_key");


                        if (otherProfileInfo != null) {
                            otherProfileInfo.UserDetail.about = user_about_you;
                            otherProfileInfo.UserDetail.height = user_height;
                            otherProfileInfo.UserDetail.weight = user_weight;
                            otherProfileInfo.UserDetail.relationship = user_relationship;
                            otherProfileInfo.UserDetail.language = user_I_speak;
                            otherProfileInfo.UserDetail.interest = user_interest;
                        }

                    }
                }

            }
        }
    }


    // Set Captured image from Camera
    private void onCaptureImageResult(int requestCode, int resultCode, Intent data) {
        //   bitmap = (Bitmap) data.getExtras().get("data");
        Uri imageUri = ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);

        if (imageUri != null) {
            CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(4, 4)
                    .start(this);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


        bitmap = ImagePicker.getImageFromResult(this, 234, -1, data);
        if (bitmap != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageBeans.size() < 6) {
                imageBeans.add(1, new ImageBean(null, bitmap, ""));
                imageAdapter.notifyDataSetChanged();
            }
        }
    }


    void updateProfileTask() {
        profile_button.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        String date = profile_birthday.getText().toString();
        String name = profile_name.getText().toString().replaceAll("( )+", " ");

        user_about_you = Utils.convertStringToUTF8SendToserver(ed_about_you.getText().toString().trim());
        user_height = tv_user_height.getText().toString().trim();
        user_weight = tv_user_weight.getText().toString().trim();
        user_I_speak = language;
        if (user_I_speak.contains(" ")) {
            user_I_speak = user_I_speak.replace(" ", "");
        }


        String aboutYou = user_about_you.replaceAll("( )+", " ");

        if (aboutYou.equals("")) {
            aboutYou = "NA";
        }
        params = new HashMap<>();
        params.put("birthday", date);
        params.put("fullName", name);
        params.put("gender", gender);
        params.put("height", user_height);
        params.put("weight", user_weight);
        params.put("relationship", user_relationship);
        params.put("about", aboutYou);
        params.put("showOnMap", showOnMap);
        params.put("language", user_I_speak);
        params.put("eduId", educationId);
        params.put("workId", workId);

        if (user_interest.endsWith(",")) {
            user_interest = user_interest.substring(0, user_interest.length() - 1);
        }
        params.put("interestId", user_interest);

        params.put("address", profile_select_location.getText().toString().trim());
        params.put("latitude", latitude);
        params.put("longitude", longitude);


        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                try {
                    Log.e("RESPONSE", response.toString());

                    JSONObject result = null;

                    Log.d("profileResponce", response + "");

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        Gson gson = new Gson();
                        SignInInfo signInInfo = gson.fromJson(String.valueOf(response), SignInInfo.class);
                        otherProfileInfo = gson.fromJson(String.valueOf(response), GetOtherProfileInfo.class);


                        ArrayList<ProfileInterestInfo> interestInfoList = new ArrayList<>();
                        session.saveUserInterestList(interestInfoList);

                        //signInInfo = session.getUser();
                       /* if(signInInfo != null){
                            signInInfo.userDetail.isProfileUpdate = "1";
                            session.createSession(signInInfo);
                        }*/

                        session.createSession(signInInfo);
                        addUserFirebaseDatabase();


                        Intent intent = new Intent(EditProfileActivity.this, SettingsActivity.class);
                        intent.putExtra("otherProfileInfo", otherProfileInfo);
                        setResult(RESULT_OK, intent);

                        if (EditProfileActivity.this != null) {
                            profileUpdated(EditProfileActivity.this, getString(R.string.profile_update));
                        }

                    } else if (status.equalsIgnoreCase("authFail")) {
                        //  Constant.showLogOutDialog(AddVehicleInfoActivity.this);
                    } else {
                        Utils.openAlertDialog(EditProfileActivity.this, message);
                    }
                    profile_button.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    profile_button.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callMultiPartApi("user/updateProfile", params, null);
    }

    private void addUserFirebaseDatabase() {
        final Session app_session = new Session(EditProfileActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM infoFCM = new UserInfoFCM();
        infoFCM.uid = app_session.getUser().userDetail.userId;
        infoFCM.email = app_session.getUser().userDetail.email;
        infoFCM.firebaseId = "";
        infoFCM.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFCM.name = app_session.getUser().userDetail.fullName;
        infoFCM.isNotification = Constant.Notication_on;
        infoFCM.authToken = app_session.getUser().userDetail.authToken;

        if (app_session.getUser().userDetail.profileImage.size() > 0) {
            infoFCM.profilePic = app_session.getUser().userDetail.profileImage.get(0).image;
        } else infoFCM.profilePic = "";


        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS)
                .child(app_session.getUser().userDetail.userId)
                .setValue(infoFCM)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {


                        } else {
                            Toast.makeText(EditProfileActivity.this, "Not Store At server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void profileUpdated(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getIntent().getSerializableExtra("otherProfileInfo") != null) {
                    finish();
                } else {
                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void imageUploadTask(final String userId, final Bitmap bitmap) {
        loading_view.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);

        Map<String, Bitmap> bitmapMap = new HashMap<>();
        bitmapMap.put("image", bitmap);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        JSONObject object = jsonObject.getJSONObject("imageData");
                        String image = object.getString("image");
                        String userImgId = object.getString("userImgId");

                        imageBeans.add(1, new ImageBean(null, bitmap, userImgId));
                        imageAdapter.notifyDataSetChanged();

                        SignInInfo signInInfo = session.getUser();
                        signInInfo.userDetail.profileImage.get(0).image = image;
                        session.createSession(signInInfo);

                        addUserFirebaseDatabase();

                    } else {
                        Utils.openAlertDialog(EditProfileActivity.this, message);
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
        service.callMultiPartApi("user/uploadUserImage", map, bitmapMap);
    }

    /*................user personal profile .....................*/
    private void openSelectHeightDialog(final ArrayList<ProfileItemInfo> heightInfoList) {
        rl_select_height.setEnabled(false);
        final Dialog height_dialog = new Dialog(this);
        height_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        height_dialog.setContentView(R.layout.view_custom_dialog_profile);
        height_dialog.setCancelable(false);
        height_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = height_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_height_dialog));

        ImageView iv_refrence = height_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.height);

        ListView profile_dialog_listView = height_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = height_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                height_dialog.dismiss();
            }
        });

        String height = tv_user_height.getText().toString();


        ProfileHeightAdapter profileHeightAdapter = new ProfileHeightAdapter(EditProfileActivity.this, heightInfoList, height, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(final int position) {
                tv_user_height.setText(heightInfoList.get(position).name);
                height_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileHeightAdapter);
        profileHeightAdapter.notifyDataSetChanged();

        height_dialog.getWindow().setGravity(Gravity.CENTER);
        height_dialog.show();

        height_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_height.setEnabled(true);
            }
        });

    }

    private void openEnterWeightDialog(final ArrayList<String> weightInfoList) {
        rl_select_weight.setEnabled(false);

        final Dialog weight_dialog = new Dialog(this);
        weight_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        weight_dialog.setContentView(R.layout.view_profile_weight_dialog);
        weight_dialog.setCancelable(false);
        weight_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = weight_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_weight_dialog));

        final EditText enter_weight = weight_dialog.findViewById(R.id.enter_weight);
        Utils utils = new Utils();
        utils.inputFilter(enter_weight);
        tv_display_selected_unit = weight_dialog.findViewById(R.id.display_selected_unit);
        final TextView weight_unit = weight_dialog.findViewById(R.id.weight_unit);

        final Spinner weight_unit_spinner = weight_dialog.findViewById(R.id.weight_unit_spinner);
        ImageView weight_decline_button = weight_dialog.findViewById(R.id.weight_decline_button);
        Button enter_weight_button = weight_dialog.findViewById(R.id.enter_weight_button);

        weight_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight_dialog.dismiss();
            }
        });

        weight_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_weight.setEnabled(true);
            }
        });

        enter_weight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = enter_weight.getText().toString().trim();


                if (!weight.equals("")) {
                    Double d = Double.valueOf(weight);
                    if (d != 0.0) {


                        if (weight_unit.getText().toString().equals("weight")) {
                            Utils.openAlertDialog(EditProfileActivity.this, "Select units");
                        } else {

                            if (!weight_unit.getText().toString().equals("Units")) {
                                weight_dialog.dismiss();
                                tv_user_weight.setText(weight + " " + weight_unit.getText().toString());
                            } else Utils.openAlertDialog(EditProfileActivity.this, "Select units");

                        }

                    } else {
                        Utils.openAlertDialog(EditProfileActivity.this, "Weight can't zero");

                    }

                } else {
                    Utils.openAlertDialog(EditProfileActivity.this, getResources().getString(R.string.alert_weight_null));
                }


                Utils.hideSoftKeyboard(EditProfileActivity.this);

            }
        });


        String temp_weight[] = tv_user_weight.getText().toString().split(" ");
        String weight = temp_weight[0];

        if (temp_weight.length == 2) {
            unit = temp_weight[1];
        }

        enter_weight.setText(weight);
        weight_unit.setText(unit);


        weight_unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //display_selected_unit.setText(adapterView.getItemAtPosition(0).toString());
                tv_display_selected_unit.setText("Weight");


                String select = adapterView.getItemAtPosition(i).toString();

                if (select.equals("Weight")) {
                    if (!unit.equals("") && unit != null) {
                        weight_unit.setText(unit);
                    } else {
                        if (select.equals("Weight")) {
                            weight_unit.setText("Units");
                        } else {
                            weight_unit.setText(adapterView.getItemAtPosition(i).toString());
                        }

                    }

                } else {
                    weight_unit.setText(adapterView.getItemAtPosition(i).toString());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(EditProfileActivity.this, android.R.layout.simple_spinner_item, weightInfoList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight_unit_spinner.setAdapter(arrayAdapter);

        weight_dialog.getWindow().setGravity(Gravity.CENTER);
        weight_dialog.show();
    }

    private void openSelectRelationshipDialog(final ArrayList<ProfileItemInfo> relationshipInfoList) {
        rl_select_relationship.setEnabled(false);
        final Dialog relation_dialog = new Dialog(this);
        relation_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        relation_dialog.setContentView(R.layout.view_custom_dialog_profile);
        relation_dialog.setCancelable(false);
        relation_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = relation_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_relationship_dialog));

        ImageView iv_refrence = relation_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.give_heart);

        ListView profile_dialog_listView = relation_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = relation_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relation_dialog.dismiss();
            }
        });

        String relation_name = tv_user_relationship.getText().toString();

        ProfileRelationshipAdapter profileRelationshipAdapter = new ProfileRelationshipAdapter(EditProfileActivity.this, relationshipInfoList, relation_name, new ProfileRelationListener() {
            @Override
            public void getPosition(int position, int relId) {
                tv_user_relationship.setText(relationshipInfoList.get(position).name);
                relationId = relId + "";
                user_relationship = relationId;
                relation_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileRelationshipAdapter);
        profileRelationshipAdapter.notifyDataSetChanged();

        relation_dialog.getWindow().setGravity(Gravity.CENTER);
        relation_dialog.show();

        relation_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_relationship.setEnabled(true);
            }
        });
    }

    private void openSelectISpeakDialog(final ArrayList<ProfileItemInfo> ispeakInfoList) {
        rl_select_I_speak.setEnabled(false);
        final Dialog I_speak_dialog = new Dialog(this);
        I_speak_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        I_speak_dialog.setContentView(R.layout.view_custom_dialog_profile);
        I_speak_dialog.setCancelable(false);
        I_speak_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = I_speak_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_I_speak_dialog));

        ImageView iv_refrence = I_speak_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.speak);

        ListView profile_dialog_listView = I_speak_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = I_speak_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                I_speak_dialog.dismiss();
            }
        });

        String I_speak_name = tv_user_I_speak.getText().toString();

        ProfileISpeakAdapter profileISpeakAdapter = new ProfileISpeakAdapter(EditProfileActivity.this, ispeakInfoList, I_speak_name, new ProfileRelationListener() {
            @Override
            public void getPosition(int position, int langId) {

                if (language.contains(ispeakInfoList.get(position).name + ", ")) {
                    language = language.replace(ispeakInfoList.get(position).name + ", ", "");
                } else if (language.contains(", " + ispeakInfoList.get(position).name)) {
                    language = language.replace(", " + ispeakInfoList.get(position).name, "");
                } else if (language.contains(ispeakInfoList.get(position).name)) {
                    language = language.replace(ispeakInfoList.get(position).name, "");
                } else {
                    language = ispeakInfoList.get(position).name + ", " + language;
                }


                if (language.endsWith(", ")) {
                    language = language.substring(0, language.length() - 2);
                }


                tv_user_I_speak.setText(language);

                I_speak_dialog.dismiss();

            }

        });

        profile_dialog_listView.setAdapter(profileISpeakAdapter);
        profileISpeakAdapter.notifyDataSetChanged();

        I_speak_dialog.getWindow().setGravity(Gravity.CENTER);
        I_speak_dialog.show();

        I_speak_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_I_speak.setEnabled(true);
            }
        });
    }

    private void openAddInterestDialog() {
        final Dialog add_interest_dialog = new Dialog(this);
        add_interest_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        add_interest_dialog.setContentView(R.layout.view_add_interest_dialog);
        add_interest_dialog.setCancelable(false);
        add_interest_dialog.setCanceledOnTouchOutside(false);

        final EditText interest_search = add_interest_dialog.findViewById(R.id.interest_search);
        final RelativeLayout rl_add_searched_interest = add_interest_dialog.findViewById(R.id.rl_add_searched_interest);
        final TextView searched_interest_list_view = add_interest_dialog.findViewById(R.id.searched_interest_list_view);
        final ImageView add_searched_interest_icon = add_interest_dialog.findViewById(R.id.add_searched_interest_icon);

        selector_interest_list_view = add_interest_dialog.findViewById(R.id.selector_interest_list_view);

        if (interestList.size() != 0) {
            interest_method(add_interest_dialog, interest_search, searched_interest_list_view, rl_add_searched_interest, add_searched_interest_icon);
        } else {
            interest_service(add_interest_dialog, interest_search, rl_add_searched_interest, searched_interest_list_view, add_searched_interest_icon,"dialog");
        }

        ImageView decline_button = add_interest_dialog.findViewById(R.id.interest_decline_button);
        decline_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                add_interest_dialog.dismiss();
            }
        });

        add_interest_dialog.getWindow().setGravity(Gravity.CENTER);
        add_interest_dialog.show();


    }

    private void interest_service(Dialog add_interest_dialog,
                                  EditText interest_search,
                                  RelativeLayout rl_add_searched_interest,
                                  TextView searched_interest_list_view,
                                  ImageView add_searched_interest_icon,
                                  String type) {

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("interestList");
                        String interest = null;
                        interestList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String interestId = object.getString("interestId");
                            interest = object.getString("interest");
                            interestList.add(new ProfileInterestInfo(interestId, interest));

                            if (type.equals("dialog"))
                                interest_method(add_interest_dialog, interest_search, searched_interest_list_view, rl_add_searched_interest, add_searched_interest_icon);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callGetSimpleVolley("getInterestList");
    }

    private void interest_method(Dialog add_interest_dialog, EditText interest_search, TextView searched_interest_list_view, RelativeLayout rl_add_searched_interest, ImageView add_searched_interest_icon) {
        final AddInterestAdapter adapter = new AddInterestAdapter(EditProfileActivity.this,
                interestList, interestInfoList, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(int position) {
                selected_interest = interestList.get(position).interest;
                user_interests = selected_interest + "," + user_interests;
                user_interest = user_interests;
                interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                showInterestAdapter.notifyDataSetChanged();

                add_interest_dialog.dismiss();
            }
        });
        selector_interest_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Calling of method to search recycler view locally
        interest_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                                          int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i,
                                      int i1, int i2) {
                final String getValue = interest_search.getText().toString().replaceAll("( +)", " ").trim();

                if (i2 > 0) {

                    String s = String.valueOf(charSequence.charAt(0)).toUpperCase() + (i2 >= 2 ? charSequence.subSequence(1, i2).toString().toLowerCase() : "");
                    searched_interest_list_view.setText(s.trim());

                    final ArrayList<ProfileInterestInfo> filtered_list = new ArrayList<>();

                    for (ProfileInterestInfo wp : interestList) {
                        if (wp.interest.toLowerCase().contains(charSequence)) {
                            filtered_list.add(wp);
                        } else {
                            if (!getValue.equals("")) {
                                rl_add_searched_interest.setVisibility(View.VISIBLE);
                            }

                            final String sel_int = searched_interest_list_view.getText().toString();

                            add_searched_interest_icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!TextUtils.isEmpty(getValue)) {
                                        user_interests = getValue + "," + user_interests;
                                        user_interest = user_interests;
                                        interestInfoList.add(new ProfileInterestInfo("", getValue));
                                        showInterestAdapter.notifyDataSetChanged();
                                    }
                                    add_interest_dialog.dismiss();

                                }
                            });
                        }

                    }

                    AddInterestAdapter demoAdapter = new AddInterestAdapter(EditProfileActivity.this,
                            filtered_list, interestInfoList, new ProfileImageAdapterListener() {
                        @Override
                        public void getPosition(int position) {
                            selected_interest = filtered_list.get(position).interest;
                            user_interests = selected_interest + "," + user_interests;

                            interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                            showInterestAdapter.notifyDataSetChanged();

                            add_interest_dialog.dismiss();

                        }
                    });
                    selector_interest_list_view.setAdapter(demoAdapter);
                    demoAdapter.notifyDataSetChanged();
                } else {
                    //hide
                    rl_add_searched_interest.setVisibility(View.GONE);
                    searched_interest_list_view.setText("");
                    selector_interest_list_view.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    showInterestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
