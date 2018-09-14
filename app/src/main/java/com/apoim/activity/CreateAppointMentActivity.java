package com.apoim.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.BusinessHorizontalListAdapter;
import com.apoim.adapter.autocompleterAdapter.Place_API_Adapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.GoogleDirection;
import com.apoim.helper.Validation;
import com.apoim.listener.PositionListner;
import com.apoim.modal.BusinessListInfo;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.modal.SingleAppointmentInfo;
import com.apoim.modal.UserListInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.LocationRuntimePermission;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class CreateAppointMentActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MapFragment mapFragment;
    protected GoogleMap mGoogleMap;
    private BalloonAdapter balloonAdapter;
    private UserListInfo.NearByUsersBean nearByUsersBean;
    private ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList;
    private GetOtherProfileInfo.UserDetailBean ProfileInfo;
    private TextView tv_location, tv_date_time, tv_distance;
    private TextView sendRequestBtn;
    private String appointLatitude, appointLongitude, appointAddress, appointTime, appointDate, appointForId,appointmentId;
    private DatePickerDialog fromDate;
    private TimePickerDialog myTime;
    private Calendar now;
    private InsLoadingView loading_view;
    private ImageView iv_back;
    private TextView edt_location;
    private Double latitude;
    private Double longitude;
    private String full_address = "";
    private RecyclerView profile_horizontal_recycler;
    private BusinessHorizontalListAdapter horizontalListAdapter;
    private RelativeLayout ly_offer_price;
    private String offerType = "2";
    private EditText ed_offer_price;

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
    private boolean isGPSEnabled, isNetworkEnabled;
    private LocationManager locationManager;
    private double current_latitude;
    private double current_longitude;
    private ImageView iv_user_image;
    private TextView tv_user_name;
    private int count = -1;
    private PlaceAutocompleteFragment autocompleteFragment;
    private GoogleDirection gd;
    private ArrayList<BusinessListInfo.BusinessListBean> businessList;
    private String isOtherbusinessAdded = "";
    private String isMybusinessAdded = "";
    private String myUserId = "";
    private String otherUserId = "";
    private String businessId = "";
    private String MybizSubscriptionId = "";
    private Session session;
    String forEditApoim = "";

    private LinearLayout ly_location, ly_is_buz_added;
    private ImageView iv_buz_image;
    private TextView tv_buz_name, tv_buz_location, tv_buz_distance,tv_heading;


    private void init() {
        tv_location = findViewById(R.id.tv_location);
        edt_location = findViewById(R.id.edt_location);
        tv_date_time = findViewById(R.id.tv_date_time);
        sendRequestBtn = findViewById(R.id.sendRequestBtn);
        loading_view = findViewById(R.id.loading_view);
        iv_back = findViewById(R.id.iv_back);
        profile_horizontal_recycler = findViewById(R.id.profile_horizontal_recycler);
        ly_offer_price = findViewById(R.id.ly_offer_price);
        ed_offer_price = findViewById(R.id.ed_offer_price);
        tv_distance = findViewById(R.id.tv_distance);

        iv_user_image = findViewById(R.id.iv_user_image);
        tv_user_name = findViewById(R.id.tv_user_name);

        ly_location = findViewById(R.id.ly_location);
        ly_is_buz_added = findViewById(R.id.ly_is_buz_added);
        iv_buz_image = findViewById(R.id.iv_buz_image);
        tv_buz_name = findViewById(R.id.tv_buz_name);
        tv_buz_location = findViewById(R.id.tv_buz_location);
        tv_buz_distance = findViewById(R.id.tv_buz_distance);
        tv_heading = findViewById(R.id.tv_heading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appoint_ment);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().getStringExtra("userId") != null) {
            appointForId = getIntent().getStringExtra("userId");
        }

        session = new Session(this);
        SignInInfo signInInfo = session.getUser();
        isMybusinessAdded = signInInfo.userDetail.isBusinessAdded;
        myUserId = signInInfo.userDetail.userId;
        MybizSubscriptionId = signInInfo.userDetail.bizSubscriptionId;
        if(MybizSubscriptionId == null){
            MybizSubscriptionId = "";
        }

        gd = new GoogleDirection(this);
        now = Calendar.getInstance();
        ProfileInfo = new GetOtherProfileInfo.UserDetailBean();
        init();

        inputFilter(ed_offer_price);

        businessList = new ArrayList<>();
        horizontalListAdapter = new BusinessHorizontalListAdapter(this, businessList, new PositionListner() {
            @Override
            public void getPosition(int position) {
                BusinessListInfo.BusinessListBean bean = businessList.get(position);
                tv_buz_name.setText(bean.businessName);
                tv_buz_location.setText(bean.businessAddress);
                tv_buz_distance.setText(bean.distance);

                if(bean.distance != null &&!bean.distance.equals("")){
                    Double d = Double.valueOf(bean.distance);
                    tv_buz_distance.setText(String.format("%.2f", d)+" Km");
                }

                Picasso.with(CreateAppointMentActivity.this).load(bean
                        .businessImage).into(iv_buz_image);

                appointAddress = bean.businessAddress;
                appointLatitude = bean.businesslat;
                appointLongitude = bean.businesslong;
                businessId = bean.businessId;
                horizontalListAdapter.notifyDataSetChanged();

                ly_is_buz_added.setVisibility(View.VISIBLE);
                ly_location.setVisibility(View.GONE);

            }
        });

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                tv_location.setText(place.getAddress());
                appointLatitude = String.valueOf(place.getLatLng().latitude);
                appointLongitude = String.valueOf(place.getLatLng().longitude);
                appointAddress = String.valueOf(place.getAddress());

                if (current_latitude != 0 && current_longitude != 0) {
                    LatLng start = new LatLng(current_latitude, current_longitude);
                    LatLng end = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    drowRouteAnimated(GoogleDirection.MODE_DRIVING, start, end);
                }

                ly_is_buz_added.setVisibility(View.GONE);
                ly_location.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        profile_horizontal_recycler.setAdapter(horizontalListAdapter);

        mapBeanArrayList = new ArrayList<>();
        nearByUsersBean = new UserListInfo.NearByUsersBean();

        /*.........................<<<<<<<<<<<<<<UPDATE CASE>>>>>>>>>>>.......................................*/

        // for update apointment...
        if (getIntent().getStringExtra("forEditApoim") != null) {
            forEditApoim = getIntent().getStringExtra("forEditApoim");
            SingleAppointmentInfo.AppoimDataBean listInfo = (SingleAppointmentInfo.AppoimDataBean) getIntent().getSerializableExtra("SingleAppointmentInfo");

            if (listInfo.forImage != null) {
                if (listInfo.forImage != null && !listInfo.forImage.equals("")) {
                    Glide.with(this).load(listInfo.forImage).apply(new RequestOptions().
                            placeholder(R.drawable.ico_user_placeholder)).into(iv_user_image);
                    tv_user_name.setText(listInfo.ForName);
                }
            }

            tv_location.setText(listInfo.appointAddress+"");
            ed_offer_price.setText(listInfo.offerPrice+"");
            sendRequestBtn.setText("Update Request");
            tv_heading.setText("Update Appointment");

            String time = "";
            try {
                String timeLong = listInfo.appointTime;
                SimpleDateFormat formatLong = new SimpleDateFormat("HH:mm:ss", Locale.US);
                SimpleDateFormat formatShort = new SimpleDateFormat("hh:mm aa", Locale.US);
                time = formatShort.format(formatLong.parse(timeLong));
                tv_date_time.setText(listInfo.appointDate + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nearByUsersBean.fullName = listInfo.ForName;
            nearByUsersBean.address = listInfo.ForAddress;
            nearByUsersBean.latitude = listInfo.ForLatitude;
            nearByUsersBean.longitude = listInfo.ForLongitude;
            nearByUsersBean.gender = listInfo.ForGender;
            ProfileInfo.latitude = listInfo.ForLatitude;
            ProfileInfo.longitude = listInfo.ForLongitude;

            if (listInfo.forImage != null) {
                if (listInfo.forImage != null && !listInfo.forImage.equals("")) {
                    nearByUsersBean.profileImage = listInfo.forImage;
                } else {
                    nearByUsersBean.profileImage = "demoImage";
                }
            } else nearByUsersBean.profileImage = "demoImage";


            if (!listInfo.offerPrice.equals("")) { // paid apominment
                ly_offer_price.setVisibility(View.VISIBLE);
                offerType = "1";// paid
            } else {
                offerType = "2";//free
                ly_offer_price.setVisibility(View.GONE);
            }

            //setData for update apoim request
            appointmentId = listInfo.appId;
            appointForId = listInfo.appointForId;
            appointDate = listInfo.appointDate;
            appointTime = time;
            appointAddress = listInfo.appointAddress;
            appointLatitude = listInfo.appointLatitude;
            appointLongitude = listInfo.appointLongitude;
            businessId = listInfo.businessId;

            businessId = listInfo.business_id;
            // for business module set data
            if (!listInfo.business_id.equals("0")) {
                ly_is_buz_added.setVisibility(View.VISIBLE);
                ly_location.setVisibility(View.GONE);

                tv_buz_name.setText(listInfo.businessName);
                tv_buz_location.setText(listInfo.businessAddress);

                Glide.with(CreateAppointMentActivity.this).load(listInfo
                        .businessImage).apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_chat_image)).into(iv_buz_image);

                appointAddress = listInfo.businessAddress;
                appointLatitude = listInfo.businesslat;
                appointLongitude = listInfo.businesslong;
                businessId = listInfo.businessId;

            } else {
                ly_is_buz_added.setVisibility(View.GONE);
                ly_location.setVisibility(View.VISIBLE);
            }

            mapBeanArrayList.add(nearByUsersBean);
            balloonAdapter = new BalloonAdapter(getLayoutInflater());
        }
/*.............................................<<<< END >>>>.............................................................................*/

        if (getIntent().getSerializableExtra("profileDetails") != null) {

            ProfileInfo = (GetOtherProfileInfo.UserDetailBean) getIntent().getSerializableExtra("profileDetails");

            if (ProfileInfo.profileImage.size() != 0) {
                if (ProfileInfo.profileImage.get(0).image != null && !ProfileInfo.profileImage.get(0).image.equals("")) {

                    Glide.with(this).load(ProfileInfo.profileImage.get(0).image).apply(new RequestOptions().
                            placeholder(R.drawable.ico_user_placeholder)).into(iv_user_image);

                }
            }
            tv_user_name.setText(ProfileInfo.fullName);
            isOtherbusinessAdded = ProfileInfo.isBusinessAdded;
            otherUserId = ProfileInfo.userId;

            nearByUsersBean.userId = ProfileInfo.userId;
            nearByUsersBean.fullName = ProfileInfo.fullName;
            nearByUsersBean.address = ProfileInfo.address;
            nearByUsersBean.latitude = ProfileInfo.latitude;
            nearByUsersBean.longitude = ProfileInfo.longitude;
            nearByUsersBean.gender = ProfileInfo.gender;
            nearByUsersBean.age = ProfileInfo.age;
            nearByUsersBean.showOnMap = ProfileInfo.showOnMap;


            if (ProfileInfo.profileImage.size() != 0) {
                if (ProfileInfo.profileImage.get(0).image != null && !ProfileInfo.profileImage.get(0).image.equals("")) {
                    nearByUsersBean.profileImage = ProfileInfo.profileImage.get(0).image;

                } else {
                    nearByUsersBean.profileImage = "demoImage";
                }
            } else nearByUsersBean.profileImage = "demoImage";

            if (ProfileInfo.appointmentType.equals("1")) {
                offerType = ProfileInfo.appointmentType;
            } else {
                offerType = "2";
            }


            if (ProfileInfo.appointmentType.equals("1")) { // paid apominment
                ly_offer_price.setVisibility(View.VISIBLE);
            } else { //free
                ly_offer_price.setVisibility(View.GONE);
            }

            mapBeanArrayList.add(nearByUsersBean);
            balloonAdapter = new BalloonAdapter(getLayoutInflater());

        }


        tv_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateField(tv_date_time);
            }
        });

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()) {
                    createAppointment();
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

    }


    private void drowRouteAnimated(String MODE, LatLng start, LatLng end) {
        if (gd != null) {

            gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
                public void onResponse(String status, Document doc, GoogleDirection gd) {
                    gd.animateDirection(null, gd.getDirection(doc), 0
                            , false, false, false, false, null, false, false, new PolylineOptions().width(6).color(getResources().getColor(R.color.colorPurple)));
                    if (doc != null) {
                        String distance = gd.getTotalDistanceText(doc).toString();
                        tv_distance.setText(distance);
                    }
                }
            });

            gd.request(start, end, MODE);
        }

    }

    // Input filter used to restrict amount input to be round off to 2 decimal places
    private void inputFilter(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                   if (et.getText().toString().contains(".")) {
                       if (et.getText().toString().substring(et.getText().toString().indexOf(".") + 1, et.length()).length() == 2) {
                           InputFilter[] fArray = new InputFilter[1];
                           fArray[0] = new InputFilter.LengthFilter(arg0.length());
                           et.setFilters(fArray);
                       }
                   }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void afterTextChanged(Editable arg0) {

                if (arg0.toString().length() == 1 && arg0.toString().startsWith("0") || arg0.toString().startsWith(".00")) {
                        arg0.clear();
                    }

                    if (arg0.length() > 0) {
                        String str = et.getText().toString();
                        et.setOnKeyListener(new View.OnKeyListener() {
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_DEL) {
                                    count--;
                                    InputFilter[] fArray = new InputFilter[1];
                                    fArray[0] = new InputFilter.LengthFilter(100);
                                    et.setFilters(fArray);
                                    //change the edittext's maximum length to 100.
                                    //If we didn't change this the edittext's maximum length will
                                    //be number of digits we previously entered.
                                }
                                return false;
                            }
                        });
                        char t = str.charAt(arg0.length() - 1);
                        if (t == '.') {
                            count = 0;
                        }
                        if (count >= 0) {
                            if (count == 2) {
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(arg0.length());

                                et.setFilters(fArray);
                                //prevent the edittext from accessing digits
                                //by setting maximum length as total number of digits we typed till now.
                            }
                            count++;
                        }else {
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(6);

                            et.setFilters(fArray);
                        }
                    }



            }
        });
    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(tv_date_time.getText().toString().trim())) {
            Utils.openAlertDialog(CreateAppointMentActivity.this, "Please select meeting date and time");
            return false;
        } else if (offerType.equals("1")) { // paid
            if (!v.isNullValue(ed_offer_price.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAppointMentActivity.this, "Please enter offer price");
                return false;

            }else if(ed_offer_price.getText().toString().trim().equals(".") || ed_offer_price.getText().toString().trim().equals(".0")){
                Utils.openAlertDialog(CreateAppointMentActivity.this, "Please enter valid offer price");
                return false;
            }
            else if (ly_is_buz_added.getVisibility() == View.VISIBLE) {
                return true;
            } else if (!v.isNullValue(tv_location.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAppointMentActivity.this, "Please select meeting location");
                return false;
            }

        } else if (ly_is_buz_added.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            if (!v.isNullValue(tv_location.getText().toString().trim())) {
                Utils.openAlertDialog(CreateAppointMentActivity.this, "Please select meeting location");
                return false;
            }
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(false);
            }
        } else {
            mGoogleMap.setMyLocationEnabled(false);
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker != null) {
                    int position = Integer.parseInt(marker.getTitle());

                }
            }
        });
        wattingForImage(mapBeanArrayList);
        setUpMap(ProfileInfo.latitude, ProfileInfo.longitude);
    }

    private void setUpMap(String appointLatitude, String appointLongitude) {
        LatLngBounds bounds = new LatLngBounds(new LatLng(Double.parseDouble(appointLatitude),
                Double.parseDouble(appointLongitude)), new LatLng(Double.parseDouble(appointLatitude),
                Double.parseDouble(appointLongitude)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private void moveToNext(final ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList, final int i) {

        if (i < mapBeanArrayList.size()) {

            final View markerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custommarkerlayout, null);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            markerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.buildDrawingCache();
            CircleImageView imageuser = markerView.findViewById(R.id.marker_image);
            ImageView iv_outer = markerView.findViewById(R.id.iv_outer);

            /*.............info window Adapter...........................*/
            mGoogleMap.setInfoWindowAdapter(balloonAdapter);
            final UserListInfo.NearByUsersBean mapBean = mapBeanArrayList.get(i);
            if (mapBean.gender.equals(Constant.PROFILE_MALE)) {
                iv_outer.setImageResource(R.drawable.ico_map_pin_m);
            } else if (mapBean.gender.equals(Constant.PROFILE_FEMALE)) {
                iv_outer.setImageResource(R.drawable.ico_map_pin_f);
            }

            if (!mapBean.profileImage.equals("")) {
                Picasso.with(CreateAppointMentActivity.this)
                        .load(mapBean.profileImage).placeholder(R.drawable.ico_user_placeholder)
                        .into(imageuser, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                getMatkerDrop(markerView, mapBean, i);
                            }

                            @Override
                            public void onError() {
                                getMatkerDrop(markerView, mapBean, i);
                            }
                        });

            }

        }
    }

    private void getMatkerDrop(View markerView, UserListInfo.NearByUsersBean mapBean, int i) {
        Bitmap finalBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBitmap);
        markerView.draw(canvas);
        // update views
        LatLng point;
        double newLat = Double.parseDouble(mapBean.latitude) + (Math.random() - .5) / 1500;// * (Math.random() * (max - min) + min);
        double newLng = Double.parseDouble(mapBean.longitude) + (Math.random() - .5) / 1500;// * (Math.random() * (max - min) + min);
        point = new LatLng(newLat, newLng);
        // point = new LatLng(Double.parseDouble(mapBean.latitude), Double.parseDouble(mapBean.longitude));

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("" + i);
        markerOptions.snippet(mapBean.address);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(finalBitmap));
        dropPinEffect(mGoogleMap.addMarker(markerOptions));
    }

    private class BalloonAdapter implements GoogleMap.InfoWindowAdapter {

        LayoutInflater inflater = null;

        public BalloonAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            View v = inflater.inflate(R.layout.info_window_layout, null);
            if (marker != null) {
                LatLng latLng = marker.getPosition();
                String pos = marker.getTitle();
                int position = Integer.parseInt(pos);
                TextView titleTextview = (TextView) v.findViewById(R.id.tv_title);
                ImageView imageView = (ImageView) v.findViewById(R.id.imageView21);
                TextView addTextview = (TextView) v.findViewById(R.id.tv_address);
                ImageView iv_info_details = v.findViewById(R.id.iv_info_button);
                titleTextview.setText(mapBeanArrayList.get(position).fullName);
                addTextview.setText(mapBeanArrayList.get(position).address);
                Picasso.with(CreateAppointMentActivity.this).load(mapBeanArrayList.get(position).profileImage).into(imageView);

                iv_info_details.setVisibility(View.GONE);
            }
            return (v);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return (null);
        }

    }

    private void wattingForImage(final ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList) {
        moveToNext(mapBeanArrayList, 0);
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);
                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                }
            }
        });
    }

    private void setDateField(final TextView view) {
        fromDate = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {

                if ((dayOfMonth < 10) && (monthOfYear + 1) < 10) {
                    //profile_birthday.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));
                    appointDate = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                } else if ((dayOfMonth < 10) && (monthOfYear + 1) >= 10) {
                    //profile_birthday.setText("0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));
                    appointDate = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                } else if ((dayOfMonth >= 10) && (monthOfYear + 1) <= 10) {
                    //profile_birthday.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + (year));
                    appointDate = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                } else {
                    //profile_birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));
                    appointDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                }


                setTimeField(view, appointDate, year, monthOfYear, dayOfMonth);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        fromDate.setMinDate(Calendar.getInstance());
        fromDate.show(getFragmentManager(), "");
        fromDate.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fromDate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                fromDate.dismiss();
            }
        });
    }

    private void setTimeField(final TextView textView, final String date, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        Date tDate = new Date();
        SimpleDateFormat simpDate;
        simpDate = new SimpleDateFormat("hh:mm:ss");
        System.out.println(simpDate.format(tDate));
        calendar.setTime(tDate);

        int currentHour = tDate.getHours(); //calendar.get(Calendar.HOUR);
        int currentMinutes = tDate.getMinutes(); //calendar.get(Calendar.MINUTE);
        int currentSeconds = tDate.getSeconds(); //calendar.get(Calendar.SECOND);


        myTime = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String format;
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                } else if (hourOfDay == 12) {
                    format = "PM";
                } else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                String hour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String min = minute < 10 ? "0" + minute : "" + minute;
                String time = hour + ":" + min;

                try {
                    if (time != null) {
                        textView.setText(date + " " + time + " " + format);
                        appointTime = time + ":" + "00" + format;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        myTime.show(getFragmentManager(), "");
        //Date currentTime = Calendar.getInstance().getTime();


        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        boolean bool = DateUtils.isToday(cal.getTimeInMillis());

        if (bool)
            myTime.setMinTime(currentHour + 1, currentMinutes, currentSeconds);

        myTime.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                myTime.dismiss();
            }
        });

    }


    private void createAppointment() {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointForId", appointForId);
        param.put("appointDate", appointDate);
        param.put("appointTime", appointTime);
        param.put("appointAddress", appointAddress);
        param.put("appointLatitude", appointLatitude);
        param.put("appointLongitude", appointLongitude);

        param.put("offerPrice", ed_offer_price.getText().toString().trim());
        param.put("offerType", offerType);

        if (ly_is_buz_added.getVisibility() == View.VISIBLE) {
            param.put("businessId", businessId);
        } else {
            param.put("businessId", "");
        }
        if (sendRequestBtn.getText().equals("Update Request")) { // case of update appointment request
            param.put("appointmentId", appointmentId);
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
                        Intent intent = new Intent();
                        intent.putExtra("status", "AppointmentRequestSent");
                        setResult(RESULT_OK, intent);
                        appointmentDoneDialog(CreateAppointMentActivity.this, message);
                    } else {
                        Utils.openAlertDialog(CreateAppointMentActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        if (sendRequestBtn.getText().equals("Update Request")) { // case of update appointment request
            service.callSimpleVolley("appointment/updateAppointment", param);
        } else {
            service.callSimpleVolley("appointment/makeAppointment", param);
        }


    }

    public void appointmentDoneDialog(final Activity context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.finish();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

/*.....................................................Current location......................................................................*/

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
                Utils.openAlertDialog(CreateAppointMentActivity.this, getResources().getString(R.string.alert_play_services_check));

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
            Utils.showGPSDisabledAlertToUser(CreateAppointMentActivity.this);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) CreateAppointMentActivity.this);
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
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) CreateAppointMentActivity.this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        // Displaying the new location on UI
        displayCurrentLocation();
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
        if (LocationRuntimePermission.checkLocationPermission(CreateAppointMentActivity.this)) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    current_latitude = mLastLocation.getLatitude();
                    current_longitude = mLastLocation.getLongitude();

                    if (businessList.size() == 0) {
                        getBusinessList(String.valueOf(current_latitude), String.valueOf(current_longitude));

                    }

                    stopLocationUpdates();

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
    /*........................location......................................................................*/


    private void getBusinessList(String lat, String lng) {
        loading_view.setVisibility(View.VISIBLE);

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
                        Gson gson = new Gson();
                        BusinessListInfo businessListInfo = gson.fromJson(response, BusinessListInfo.class);
                        businessList.addAll(businessListInfo.businessList);


                        if(!forEditApoim.equals("")){
                            for (int i = 0; i < businessList.size(); i++) {
                                if (businessList.get(i).businessId.equals(businessId)) {
                                    profile_horizontal_recycler.scrollToPosition(i);

                                    tv_buz_name.setText(businessList.get(i).businessName);
                                    tv_buz_location.setText(businessList.get(i).businessAddress);

                                    if(businessList.get(i).distance != null &&!businessList.get(i).distance.equals("")){
                                        Double d = Double.valueOf(businessList.get(i).distance);
                                        tv_buz_distance.setText(String.format("%.2f", d)+" Km");
                                    }

                                    Glide.with(CreateAppointMentActivity.this).load(businessList.get(i)
                                            .businessImage).apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder_chat_image)).into(iv_buz_image);

                                    appointAddress = businessList.get(i).businessAddress;
                                    appointLatitude = businessList.get(i).businesslat;
                                    appointLongitude = businessList.get(i).businesslong;
                                    businessId = businessList.get(i).businessId;
                                    businessList.get(i).isSelected = true;

                                    if(forEditApoim.equals("")){
                                        ly_is_buz_added.setVisibility(View.VISIBLE);
                                        ly_location.setVisibility(View.GONE);
                                    }

                                }
                            }
                        }
                        else

                        if (!MybizSubscriptionId.equals("")) {
                                for (int i = 0; i < businessList.size(); i++) {
                                    if (businessList.get(i).userId.equals(myUserId)) {
                                        profile_horizontal_recycler.scrollToPosition(i);

                                        tv_buz_name.setText(businessList.get(i).businessName);
                                        tv_buz_location.setText(businessList.get(i).businessAddress);

                                        if(businessList.get(i).distance != null &&!businessList.get(i).distance.equals("")){
                                            Double d = Double.valueOf(businessList.get(i).distance);
                                            tv_buz_distance.setText(String.format("%.2f", d)+" Km");
                                        }

                                        Glide.with(CreateAppointMentActivity.this).load(businessList.get(i)
                                                .businessImage).apply(new RequestOptions()
                                                .placeholder(R.drawable.placeholder_chat_image)).into(iv_buz_image);

                                        appointAddress = businessList.get(i).businessAddress;
                                        appointLatitude = businessList.get(i).businesslat;
                                        appointLongitude = businessList.get(i).businesslong;
                                        businessId = businessList.get(i).businessId;
                                        businessList.get(i).isSelected = true;

                                        if(forEditApoim.equals("")){
                                            ly_is_buz_added.setVisibility(View.VISIBLE);
                                            ly_location.setVisibility(View.GONE);
                                        }

                                    }
                                }


                            }

                        else if (isOtherbusinessAdded.equals("1")) {
                            for (int i = 0; i < businessList.size(); i++) {
                                if (businessList.get(i).userId.equals(otherUserId)) {
                                    profile_horizontal_recycler.scrollToPosition(i);

                                    tv_buz_name.setText(businessList.get(i).businessName);
                                    tv_buz_location.setText(businessList.get(i).businessAddress);

                                    if(businessList.get(i).distance != null &&!businessList.get(i).distance.equals("")){
                                        Double d = Double.valueOf(businessList.get(i).distance);
                                        tv_buz_distance.setText(String.format("%.2f", d)+" Km");
                                    }



                                    Glide.with(CreateAppointMentActivity.this).load(businessList.get(i)
                                            .businessImage).apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder_chat_image)).into(iv_buz_image);

                                    appointAddress = businessList.get(i).businessAddress;
                                    appointLatitude = businessList.get(i).businesslat;
                                    appointLongitude = businessList.get(i).businesslong;
                                    businessId = businessList.get(i).businessId;
                                    businessList.get(i).isSelected = true;

                                    if(forEditApoim.equals("")){
                                        ly_is_buz_added.setVisibility(View.VISIBLE);
                                        ly_location.setVisibility(View.GONE);
                                    }
                                }
                            }


                        } else {
                            if(forEditApoim.equals("")){
                                ly_is_buz_added.setVisibility(View.GONE);
                                ly_location.setVisibility(View.VISIBLE);
                            }

                        }


                    } else {
                        Utils.openAlertDialog(CreateAppointMentActivity.this, message);
                    }

                    horizontalListAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callGetSimpleVolley("business/getBusinessList?latitude=" + lat + "&longitude=" + lng + "");
    }
}
