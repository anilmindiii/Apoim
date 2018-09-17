package com.apoim.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.GoogleDirection;
import com.apoim.modal.SingleAppointmentInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.TimeAgo;
import com.apoim.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.apoim.activity.SubscriptionPayActivity.paymentType;

public class AppointmentDirectionActivity extends AppCompatActivity implements OnMapReadyCallback
        , View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map = null;
    private GoogleDirection gd;
    private GoogleDirection gd1;
    private GoogleDirection gd2;
    private SingleAppointmentInfo listInfo;
    private TextView tv_crd, tv_time, tv_address, tv_by_name, tv_for_name;
    private ImageView iv_by_profile, iv_for_profile, iv_back;
    private TextView finish_meeting_button;
    private InsLoadingView loadingView;
    private LatLng start;
    private LatLng end;
    private LatLng end1;
    private String myUserId = "";
    private RelativeLayout ly_delete;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    boolean GpsStatus;
    // Location updates intervals in sec
    private static final String TAG = "AppointmentDirectionActivity";
    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 10;
    public Double current_lat;
    public Double current_lng;
    boolean isById = false;
    boolean isForId = false;
    String appId = "";
    Marker marker_start, marker_end;
    Marker marker_end1;
    private LinearLayout ly_accept, ly_reject, ly_fill_counter_price, ly_counter;
    private TextView tv_counter_price, tv_offer_price, tv_counter_price_bottom;
    private int count = -1;
    RelativeLayout ly_accept_reject, ly_popup_menu, ly_update_apoim;
    private ImageView iv_popup_menu;
    private LinearLayout ly_is_buz_added;
    private ImageView iv_buz_image;
    private TextView tv_buz_name, tv_buz_location, pay_button, review_button,tv_walking_time,tv_driving_time;

    /*.........for review and ratting.................*/
    private CardView ly_main_review;
    private LinearLayout ly_top_review,ly_bottom_review;
    private TextView tv_top_review_by,tv_top_comments,tv_div_line;
    private TextView tv_bottom_review_by,tv_bottom_comments,tv_time_ago_top,tv_time_ago_bottom;
    private RatingBar ratingBar_bottom,ratingBar_top;
    private String currentDateTime;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_botton_sheet_layout);
        init();

        Session session = new Session(this, this);
        myUserId = session.getUser().userDetail.userId;
        loadingView = findViewById(R.id.loadingView___);
        if (getIntent().getStringExtra(Constant.appointment_details) != null) {
            appId = getIntent().getStringExtra(Constant.appointment_details);
            appointmentDetails(appId);
        }

        gd = new GoogleDirection(this);
        gd1 = new GoogleDirection(this);
        gd2 = new GoogleDirection(this);

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void init() {
        tv_crd = findViewById(R.id.tv_crd);
        tv_time = findViewById(R.id.tv_time);
        tv_address = findViewById(R.id.tv_address);
        tv_by_name = findViewById(R.id.tv_by_name);
        iv_by_profile = findViewById(R.id.iv_by_profile);
        iv_for_profile = findViewById(R.id.iv_for_profile);
        tv_for_name = findViewById(R.id.tv_for_name);
        finish_meeting_button = findViewById(R.id.finish_meeting_button);
        ly_delete = findViewById(R.id.ly_delete);
        iv_back = findViewById(R.id.iv_back);
        tv_walking_time = findViewById(R.id.tv_walking_time);
        tv_driving_time = findViewById(R.id.tv_driving_time);
        tv_time_ago_top = findViewById(R.id.tv_time_ago_top);
        tv_time_ago_bottom = findViewById(R.id.tv_time_ago_bottom);

        ly_accept = findViewById(R.id.ly_accept);
        ly_reject = findViewById(R.id.ly_reject);
        ly_fill_counter_price = findViewById(R.id.ly_fill_counter_price);

        tv_counter_price = findViewById(R.id.tv_counter_price);
        tv_counter_price_bottom = findViewById(R.id.tv_counter_price_bottom);
        tv_offer_price = findViewById(R.id.tv_offer_price);

        ly_accept_reject = findViewById(R.id.ly_accept_reject);
        ly_counter = findViewById(R.id.ly_counter);

        iv_popup_menu = findViewById(R.id.iv_popup_menu);
        ly_popup_menu = findViewById(R.id.ly_popup_menu);
        ly_update_apoim = findViewById(R.id.ly_update_apoim);

        ly_is_buz_added = findViewById(R.id.ly_is_buz_added);
        iv_buz_image = findViewById(R.id.iv_buz_image);
        tv_buz_name = findViewById(R.id.tv_buz_name);
        tv_buz_location = findViewById(R.id.tv_buz_location);
        pay_button = findViewById(R.id.pay_button);
        review_button = findViewById(R.id.review_button);

        ly_main_review = findViewById(R.id.ly_main_review);
        ly_top_review = findViewById(R.id.ly_top_review);
        ly_bottom_review = findViewById(R.id.ly_bottom_review);
        tv_top_review_by = findViewById(R.id.tv_top_review_by);
        ratingBar_top = findViewById(R.id.ratingBar_top);
        tv_top_comments = findViewById(R.id.tv_top_comments);
        tv_div_line = findViewById(R.id.tv_div_line);

        tv_bottom_review_by = findViewById(R.id.tv_bottom_review_by);
        ratingBar_bottom = findViewById(R.id.ratingBar_bottom);
        tv_bottom_comments = findViewById(R.id.tv_bottom_comments);

        finish_meeting_button.setOnClickListener(this);
        ly_delete.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        ly_accept.setOnClickListener(this);
        ly_reject.setOnClickListener(this);
        ly_fill_counter_price.setOnClickListener(this);
        iv_popup_menu.setOnClickListener(this);
        ly_update_apoim.setOnClickListener(this);
        pay_button.setOnClickListener(this);
        review_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish_meeting_button: {
                if (listInfo.appoimData.appId != null && !listInfo.appoimData.appId.equals(""))
                    finish_meeting(listInfo.appoimData.appId);
                break;
            }
            case R.id.ly_delete: {
                ly_popup_menu.setVisibility(View.GONE);
                conform_delete_meeting(this, getString(R.string.delete_appointment_sure));
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.ly_accept: {
                if (listInfo.appoimData.isCounterApply.equals("1")) {
                    upDateCounter(appId, listInfo.appoimData.appointForId, "1"); // 1 for accept
                } else acceptAppoinmentRequest(appId);


                break;
            }
            case R.id.ly_reject: {
                if (listInfo.appoimData.isCounterApply.equals("1")) {
                    upDateCounter(appId, listInfo.appoimData.appointForId, "2"); // 2 for reject
                } else rejectAppoinmentRequest(appId);


                break;
            }
            case R.id.ly_fill_counter_price: {
                enterCounterPriceDialog(listInfo.appoimData.offerPrice);  // dialog
                break;
            }
            case R.id.iv_popup_menu: {
                if (ly_popup_menu.getVisibility() == View.VISIBLE) {
                    ly_popup_menu.setVisibility(View.GONE);
                } else ly_popup_menu.setVisibility(View.VISIBLE);

                break;
            }
            case R.id.ly_update_apoim: {
                ly_popup_menu.setVisibility(View.GONE);
                if (listInfo != null)

                    if (listInfo.appoimData.appointById.equals(myUserId)) {
                        if (listInfo.appoimData.appointmentStatus.equals("2") || listInfo.appoimData.appointmentStatus.equals("4")) {
                            Utils.openAlertDialog(AppointmentDirectionActivity.this, "You can not edit apointment");
                        } else {
                            askEditApoimDialog(this, getString(R.string.sure_edit_apoim));
                        }
                    }

                break;
            }
            case R.id.pay_button: {
                Intent intent = new Intent(AppointmentDirectionActivity.this, SubscriptionPayActivity.class);
                intent.putExtra(paymentType, Constant.PayForCounterAppointment); // 7 for counter for map
                intent.putExtra("appointmentId", listInfo.appoimData.appId);
                intent.putExtra("appointForId", listInfo.appoimData.appointForId);

                if (listInfo.appoimData.isCounterApply.equals("1")) {
                    intent.putExtra("amount", listInfo.appoimData.counterPrice); // send counter price
                } else {
                    intent.putExtra("amount", listInfo.appoimData.offerPrice);   // send offer price
                }
                startActivityForResult(intent, 1);


                break;
            }
            case R.id.review_button: {
                reviewDialog();
            }
        }
    }

    public void askEditApoimDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(AppointmentDirectionActivity.this, CreateAppointMentActivity.class);
                intent.putExtra("SingleAppointmentInfo", listInfo.appoimData);
                intent.putExtra("forEditApoim", "forEditApoim");
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void enterCounterPriceDialog(String offerPrice) {
        final Dialog dialog = new Dialog(AppointmentDirectionActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.enter_counter_price_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        EditText ed_counter_price = dialog.findViewById(R.id.ed_counter_price);

        inputFilter(ed_counter_price);
        TextView tv_apply_button = dialog.findViewById(R.id.tv_apply_button);
        TextView tv_offer_price = dialog.findViewById(R.id.tv_offer_price);
        ImageView iv_close_button = dialog.findViewById(R.id.iv_close_button);

        tv_offer_price.setText("$" + offerPrice);

        tv_apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String counter_price = ed_counter_price.getText().toString().trim();
                if (!counter_price.equals("")) {
                    counterApplyRequest(appId, counter_price, listInfo.appoimData.appointById);
                    dialog.dismiss();
                } else {
                    Utils.openAlertDialog(AppointmentDirectionActivity.this, "Please Enter Counter Price");
                }


            }
        });

        iv_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 12.0f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(false);
            }
        } else {
            map.setMyLocationEnabled(false);
        }

        getDuration(end);
    }


     void drowRouteAnimated(String MODE, final int ico_current_red, final int colorPrimary) {
        if (gd != null) {

            gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
                public void onResponse(String status, Document doc, GoogleDirection gd) {

                    gd.animateDirection(map, gd.getDirection(doc), 0
                            , false, false, false, false, null, false, false, new PolylineOptions().width(6).color(getResources().getColor(R.color.colorPrimary)));


                    if (marker_start == null && map != null) {
                        marker_start = map.addMarker(new MarkerOptions().position(start)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_route_pin)));
                    }


                    if (marker_end == null && map != null) {
                        map.addMarker(new MarkerOptions().position(end)
                                .icon(BitmapDescriptorFactory.fromResource(ico_current_red)));
                    }

                    String durationValue = gd.getTotalDurationText(doc);
                    Log.d("durationValue", durationValue + "");


                    PolylineOptions options = new PolylineOptions().width(8).color(getResources().getColor(colorPrimary)).geodesic(true);
                    ArrayList<LatLng> arr_pos = gd.getDirection(doc);
                    for (int z = 0; z < arr_pos.size(); z++) {
                        LatLng point = arr_pos.get(z);
                        options.add(point);
                    }
                    map.addPolyline(options);

                    if(isById){
                        String duration =   gd.getTotalDurationText(doc);
                        tv_walking_time.setText(duration);
                    }


                }
            });
            if (start != null && end != null)
                gd.request(start, end, MODE);
        }

    }

    void drowRouteAnimated1(String MODE, final int ico_current_purple, final int colorPurple) {
        if (gd1 != null) {
            gd1.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
                public void onResponse(String status, Document doc, GoogleDirection gd) {

                    gd1.animateDirection(map, gd1.getDirection(doc), 0
                            , false, false, false, false, null, false, false, new PolylineOptions().width(6).color(getResources().getColor(R.color.colorPurple)));

                    if (marker_end1 == null) {
                        marker_end1 = map.addMarker(new MarkerOptions().position(end1)
                                .icon(BitmapDescriptorFactory.fromResource(ico_current_purple)));
                    }


                    PolylineOptions options = new PolylineOptions().width(8).color(getResources().getColor(colorPurple)).geodesic(true);
                    ArrayList<LatLng> arr_pos = gd.getDirection(doc);
                    for (int z = 0; z < arr_pos.size(); z++) {
                        LatLng point = arr_pos.get(z);
                        options.add(point);
                    }
                    map.addPolyline(options);

                    if(isForId){
                        String duration =   gd.getTotalDurationText(doc);
                        tv_walking_time.setText(duration);
                    }


                }
            });
            if (start != null && end1 != null)
                gd1.request(start, end1, MODE);
        }

    }

     void getDuration(LatLng end) {
        if (gd2 != null) {

            gd2.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
                public void onResponse(String status, Document doc, GoogleDirection gd) {

                    String duration =   gd.getTotalDurationText(doc);
                    tv_driving_time.setText(duration);
                }
            });
            if (start != null && end != null)
                gd2.request(start, end, gd2.MODE_DRIVING);
        }

    }


    private void finish_meeting(String appointId) {
        loadingView.setVisibility(View.VISIBLE);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Firebase Token", token);

        Map<String, String> map = new HashMap<>();
        map.put("appointId", appointId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        finish_meeting_button.setVisibility(View.GONE);
                        review_button.setVisibility(View.VISIBLE);
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
        service.callSimpleVolley("appointment/finishAppointment", map);
    }

    private void delete_meeting(String appointId) {
        loadingView.setVisibility(View.VISIBLE);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Firebase Token", token);

        Map<String, String> map = new HashMap<>();
        map.put("appointId", appointId);
        map.put("type", "deleted");

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        finish();
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
        service.callSimpleVolley("appointment/deleteAppointment", map);
    }

    public void conform_delete_meeting(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listInfo.appoimData.appId != null && !listInfo.appoimData.appId.equals("")) {
                    delete_meeting(listInfo.appoimData.appId);
                }
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void appointmentDetails(String appId) {
        loadingView.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");
                    currentDateTime = object.getString("date");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        listInfo = gson.fromJson(response, SingleAppointmentInfo.class);
                        setData(listInfo.appoimData);

                    } else noRecordDialog(AppointmentDirectionActivity.this, message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callGetSimpleVolley("appointment/getAppointmentDetail?appointId=" + appId + "");
    }

    public void noRecordDialog(Context context, String message) {
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

    void setData(SingleAppointmentInfo.AppoimDataBean listInfo) {
        if (listInfo != null) {

            String meetingAddress = "";
            String meetingtime = "";

            String date_before = listInfo.appointDate;
            String date_after = formateDateFromstring(date_before);

            try {
                String timeLong = listInfo.appointTime;

                SimpleDateFormat formatLong = new SimpleDateFormat("HH:mm:ss", Locale.US);
                SimpleDateFormat formatShort = new SimpleDateFormat("hh:mm aa", Locale.US);
                Log.v("timeLong", formatShort.format(formatLong.parse(timeLong)));

                meetingAddress = date_after + ", ";
                meetingtime = formatShort.format(formatLong.parse(timeLong));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_crd.setText(meetingAddress);
            tv_time.setText(meetingtime);

            tv_address.setText(listInfo.appointAddress);
            tv_by_name.setText(listInfo.ByName);
            if (!listInfo.byImage.equals("")) {
                Picasso.with(this).load(listInfo.byImage).placeholder(R.drawable.ico_user_placeholder).into(iv_by_profile);
            }

            if (listInfo.forImage != null) {
                if (!listInfo.forImage.equals(""))
                    Picasso.with(this).load(listInfo.forImage).placeholder(R.drawable.ico_user_placeholder).into(iv_for_profile);
            }
            tv_for_name.setText(listInfo.ForName);

            if (listInfo.appointById.equals(myUserId)) {

                if (listInfo.appointByStatus.equals("1")) {
                    finish_meeting_button.setVisibility(View.GONE);
                } else if (listInfo.appointByStatus.equals("2")) {
                    finish_meeting_button.setVisibility(View.VISIBLE);
                }
                isById = true;
                isForId = false;
                ly_update_apoim.setVisibility(View.VISIBLE);

            } else if (listInfo.appointForId.equals(myUserId)) {
                if (listInfo.appointForStatus.equals("1")) {
                    // tv_status.setText("New Appointment request");
                    finish_meeting_button.setVisibility(View.GONE);
                } else if (listInfo.appointForStatus.equals("2")) {
                    //tv_status.setText("Confirmed appointment");
                    finish_meeting_button.setVisibility(View.VISIBLE);
                }
                isById = false;
                isForId = true;
                ly_update_apoim.setVisibility(View.GONE);
            }
        }

        assert listInfo != null;
        if (listInfo.isFinish.equals("1")) {
            finish_meeting_button.setVisibility(View.GONE);
        }
        start = new LatLng(Double.parseDouble(listInfo.appointLatitude), Double.parseDouble(listInfo.appointLongitude));


        pollyLineMethod(listInfo);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        if (!listInfo.businessId.equals("")) {
            ly_is_buz_added.setVisibility(View.VISIBLE);
            tv_address.setVisibility(View.GONE);
            tv_buz_name.setText(listInfo.businessName);
            tv_buz_location.setText(listInfo.businessAddress);

            Picasso.with(AppointmentDirectionActivity.this).load(listInfo.businessImage).into(iv_buz_image);

        } else {
            ly_is_buz_added.setVisibility(View.GONE);
            tv_address.setVisibility(View.VISIBLE);
        }


        if (listInfo.offerPrice.equals("")) {
            tv_offer_price.setText("Free");
        } else tv_offer_price.setText("$ " + listInfo.offerPrice);

        tv_counter_price.setText("$ " + listInfo.counterPrice);

        /*....................................................manage data................................................*/

        if (listInfo.appointById.equals(myUserId)) {

            if (!listInfo.reviewByUserId.equals("") || !listInfo.reviewForUserId.equals("")) {
                ly_main_review.setVisibility(View.VISIBLE);
                if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                    ly_counter.setVisibility(View.GONE);
                } else {
                    ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }

                if(!listInfo.reviewByUserId.equals("")){
                    // review by wala data set at bottom
                    ly_bottom_review.setVisibility(View.VISIBLE);
                    tv_bottom_review_by.setText("Review by You");
                    ratingBar_bottom.setRating(Float.parseFloat(listInfo.reviewByRating));
                   // tv_bottom_comments.setText(Utils.convertUTF8ToStringSetText(listInfo.reviewByComment));
                    tv_bottom_comments.setText((listInfo.reviewByComment));
                    tv_time_ago_bottom.setText(TimeAgo.toRelative(listInfo.reviewByCreatedDate, currentDateTime));
                }else {
                    review_button.setVisibility(View.VISIBLE);
                    ly_bottom_review.setVisibility(View.GONE);
                }

                if(!listInfo.reviewForUserId.equals("")){
                    // review for wala data set at top
                    tv_top_review_by.setText("Review by "+listInfo.ForName);
                    ratingBar_top.setRating(Float.parseFloat(listInfo.reviewForRating));
                    //tv_top_comments.setText(Utils.convertUTF8ToStringSetText(listInfo.reviewForComment));
                    tv_top_comments.setText((listInfo.reviewForComment));
                    tv_time_ago_top.setText(TimeAgo.toRelative(listInfo.reviewForCreatedDate, currentDateTime));
                }else {
                    ly_top_review.setVisibility(View.GONE);
                }

                if(ly_top_review.getVisibility() == View.VISIBLE && ly_bottom_review.getVisibility() == View.VISIBLE ){
                    tv_div_line.setVisibility(View.VISIBLE);
                }else tv_div_line.setVisibility(View.GONE);

            }
            else if (listInfo.isFinish.equals("1")) {
                ly_accept.setVisibility(View.GONE);
                ly_reject.setVisibility(View.GONE);
                review_button.setVisibility(View.VISIBLE);

                if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                    ly_counter.setVisibility(View.GONE);
                } else {
                    ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }

            }
            else if (listInfo.isCounterApply.equals("1")) {

                if (listInfo.counterStatus.equals("0")) {
                    ly_accept_reject.setVisibility(View.VISIBLE);

                    ly_fill_counter_price.setEnabled(false);
                    tv_counter_price_bottom.setText(listInfo.counterPrice);

                } else if (listInfo.counterStatus.equals("1")) {
                    // pay button visible
                    ly_counter.setVisibility(View.VISIBLE);
                    pay_button.setVisibility(View.VISIBLE);
                    ly_accept_reject.setVisibility(View.GONE);
                } else if (listInfo.counterStatus.equals("3")) {
                    // finish button visible else all gone
                    finish_meeting_button.setVisibility(View.VISIBLE);
                    ly_counter.setVisibility(View.VISIBLE);
                    pay_button.setVisibility(View.GONE);
                }

            }
            else if (listInfo.appointmentStatus.equals("1")) {

                ly_accept_reject.setVisibility(View.GONE);

                if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                    ly_counter.setVisibility(View.GONE);
                } else {
                    ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }


            }
            else if (listInfo.appointmentStatus.equals("2")) {
                ly_accept_reject.setVisibility(View.GONE);

                if (listInfo.offerPrice.equals("")) {// empty mean free case
                    ly_counter.setVisibility(View.GONE);
                    finish_meeting_button.setVisibility(View.VISIBLE);
                } else {
                    pay_button.setVisibility(View.VISIBLE);// if free then gone if paid then visible
                }

            }
            else if (listInfo.appointmentStatus.equals("4")) { // confirm for paid apointment
                finish_meeting_button.setVisibility(View.VISIBLE);
                pay_button.setVisibility(View.GONE);

            }
        }

//*............................................................................................................................................*//*
        else if (listInfo.appointForId.equals(myUserId)) {

            if (!listInfo.reviewByUserId.equals("") || !listInfo.reviewForUserId.equals("")) {
                ly_main_review.setVisibility(View.VISIBLE);
                if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                    ly_counter.setVisibility(View.GONE);
                } else {
                    ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }

                if(!listInfo.reviewByUserId.equals("")){
                    // set ReviewBy data to Top view
                    tv_top_review_by.setText("Review by "+listInfo.ByName);
                    ratingBar_top.setRating(Float.parseFloat(listInfo.reviewByRating));
                   // tv_top_comments.setText(Utils.convertUTF8ToStringSetText(listInfo.reviewByComment));
                    tv_top_comments.setText(listInfo.reviewByComment);
                    tv_time_ago_top.setText(TimeAgo.toRelative(listInfo.reviewByCreatedDate, currentDateTime));
                }
                else {
                    ly_top_review.setVisibility(View.GONE);
                }

                if(!listInfo.reviewForUserId.equals("")){
                    // set ReviewBottom data to Bottom  view
                    ly_bottom_review.setVisibility(View.VISIBLE);
                    tv_bottom_review_by.setText("Review by You");
                    ratingBar_bottom.setRating(Float.parseFloat(listInfo.reviewForRating));
                    //tv_bottom_comments.setText(Utils.convertUTF8ToStringSetText(listInfo.reviewForComment));
                    tv_bottom_comments.setText(listInfo.reviewForComment);
                    tv_time_ago_bottom.setText(TimeAgo.toRelative(listInfo.reviewForCreatedDate, currentDateTime));
                }
                else {
                    review_button.setVisibility(View.VISIBLE);
                    ly_bottom_review.setVisibility(View.GONE);
                }

                if(ly_top_review.getVisibility() == View.VISIBLE && ly_bottom_review.getVisibility() == View.VISIBLE ){
                    tv_div_line.setVisibility(View.VISIBLE);
                }else tv_div_line.setVisibility(View.GONE);

            }
            else if (listInfo.isFinish.equals("1")) {
                review_button.setVisibility(View.VISIBLE);

                if (listInfo.offerPrice.equals("")) { // free wala case
                    tv_offer_price.setText("Free");
                } else {
                    if (listInfo.counterStatus.equals("3")) {
                        if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                            ly_counter.setVisibility(View.GONE); // if free then gone if paid then visible
                        } else
                            ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                    } else {
                        ly_counter.setVisibility(View.GONE);
                    }
                }
            } else if (listInfo.isCounterApply.equals("1")) {
                if (listInfo.counterStatus.equals("0")) {
                    ly_counter.setVisibility(View.VISIBLE);
                    ly_accept_reject.setVisibility(View.GONE);
                }
                else if (listInfo.counterStatus.equals("1")) {
                    // pay button visible
                    ly_counter.setVisibility(View.VISIBLE);
                    pay_button.setVisibility(View.GONE);
                    ly_accept_reject.setVisibility(View.GONE);
                }//my code new w
                else if (listInfo.counterStatus.equals("3")) {
                    finish_meeting_button.setVisibility(View.VISIBLE);
                    ly_counter.setVisibility(View.VISIBLE);
                    pay_button.setVisibility(View.GONE);
                }
            } else if (listInfo.appointmentStatus.equals("1")) {

                if (!listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {
                    ly_counter.setVisibility(View.VISIBLE);
                    ly_accept_reject.setVisibility(View.GONE);
                } else {
                    ly_counter.setVisibility(View.GONE);
                    ly_accept_reject.setVisibility(View.VISIBLE);
                    tv_counter_price.setText("Counter");
                }


                if (listInfo.offerPrice.equals("")) {// empty mean free
                    ly_fill_counter_price.setVisibility(View.GONE); // if free then gone if paid then visible
                } else
                    ly_fill_counter_price.setVisibility(View.VISIBLE); // if free then gone if paid then visible

                ly_fill_counter_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(this, "dbfjdbfjud", Toast.LENGTH_SHORT).show();
                        enterCounterPriceDialog(listInfo.offerPrice);
                    }
                });

            } else if (listInfo.appointmentStatus.equals("2")) {
                ly_accept_reject.setVisibility(View.GONE);

                if (listInfo.offerPrice.equals("")) {// empty mean free case
                    ly_counter.setVisibility(View.GONE);
                    finish_meeting_button.setVisibility(View.VISIBLE);
                }


            } else if (listInfo.appointmentStatus.equals("4")) {
                finish_meeting_button.setVisibility(View.VISIBLE);
                pay_button.setVisibility(View.GONE);

                if (listInfo.counterPrice.equals("")|| listInfo.counterPrice.equals("0")) {// empty mean free
                    ly_counter.setVisibility(View.GONE); // if free then gone if paid then visible
                } else {
                    ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }
            }

        }
    }

    private void pollyLineMethod(SingleAppointmentInfo.AppoimDataBean listInfo) {
        if (listInfo.ByLatitude != null && listInfo.ByLongitude != null) {
            if (isById) {
                if (current_lat != null && current_lng != null)
                    end = new LatLng(current_lat, current_lng);
            } else {
                if (!listInfo.ByLatitude.equals("") && !listInfo.ByLongitude.equals(""))
                    end = new LatLng(Double.parseDouble(listInfo.ByLatitude), Double.parseDouble(listInfo.ByLongitude));
            }

        }

        if (listInfo.ForLatitude != null && listInfo.ForLongitude != null) {

            if (isForId) {
                if (current_lat != null && current_lng != null)
                    end1 = new LatLng(current_lat, current_lng);
            } else {
                if (!listInfo.ForLatitude.equals("") && !listInfo.ForLongitude.equals(""))
                    end1 = new LatLng(Double.parseDouble(listInfo.ForLatitude), Double.parseDouble(listInfo.ForLongitude));
            }

        }

        if (listInfo.ByGender != null && listInfo.ForGender != null) {

            if (listInfo.ByGender.equals(Constant.PROFILE_TRANSGENDER) || listInfo.ByGender.equals(Constant.PROFILE_MALE) && listInfo.ForGender.equals(Constant.PROFILE_FEMALE)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
            }
            else if (listInfo.ByGender.equals(Constant.PROFILE_FEMALE) && listInfo.ForGender.equals(Constant.PROFILE_MALE) || listInfo.ByGender.equals(Constant.PROFILE_TRANSGENDER)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
            }
            else if (listInfo.ByGender.equals(Constant.PROFILE_MALE) && listInfo.ByGender.equals(Constant.PROFILE_MALE) || listInfo.ByGender.equals(Constant.PROFILE_TRANSGENDER)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
            }
            else if (listInfo.ByGender.equals(Constant.PROFILE_FEMALE) && listInfo.ByGender.equals(Constant.PROFILE_FEMALE)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
            }
            else if (listInfo.ForGender.equals(Constant.PROFILE_MALE) && listInfo.ForGender.equals(Constant.PROFILE_MALE) || listInfo.ByGender.equals(Constant.PROFILE_TRANSGENDER)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_red, R.color.colorPrimary);
            }
            else if (listInfo.ForGender.equals(Constant.PROFILE_FEMALE) && listInfo.ForGender.equals(Constant.PROFILE_FEMALE)) {
                drowRouteAnimated(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
                drowRouteAnimated1(GoogleDirection.MODE_WALKING, R.drawable.ico_current_purple, R.color.colorPurple);
            }


        }
    }

    public void CheckGpsStatus() {
        GpsStatus = checkIfLocationOpened();
    }

    private boolean checkIfLocationOpened() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        // otherwise return false
        return false;
    }

    public String formateDateFromstring(String inputDate) {
        Date parsed;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //Log(TAG, "ParseException - dateFormat");
        }
        return outputDate;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Location mCurrentLocation = location;

        if (null != mCurrentLocation) {
            current_lat = mCurrentLocation.getLatitude();
            current_lng = mCurrentLocation.getLongitude();


            /*appointmentDetails(appId);*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        CheckGpsStatus();
        if (!GpsStatus) {
            showGPSDisabledAlertToUser();
        }
        appointmentDetails(appId);
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

/*...............operation accept reject applycounter.....................................*/

    private void upDateCounter(String appointId, String appointForId, String counterStatus) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("appointForId", appointForId);
        param.put("counterStatus", counterStatus);

        WebService service = new WebService(AppointmentDirectionActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        appointmentDetails(appId);
                    } else {
                        Utils.openAlertDialog(AppointmentDirectionActivity.this, message);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/updateCounter", param);
    }

    private void counterApplyRequest(String appointId, String counterPrice, String appointById) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("counterPrice", counterPrice);
        param.put("appointById", appointById);

        WebService service = new WebService(AppointmentDirectionActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {
                        appointmentDetails(appId);
                    } else {
                        Utils.openAlertDialog(AppointmentDirectionActivity.this, message);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/applyCounter", param);
    }

    private void rejectAppoinmentRequest(String appointId) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("type", "rejected");

        WebService service = new WebService(AppointmentDirectionActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {
                        finish();
                    } else {
                        Utils.openAlertDialog(AppointmentDirectionActivity.this, message);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/deleteAppointment", param);
    }

    private void acceptAppoinmentRequest(String appointId) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("appointeStatus", "2");

        WebService service = new WebService(AppointmentDirectionActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                      /*  apomList.get(position).appointForStatus = "1";
                        apomList.get(position).appointByStatus = "1";
                        apoinmentAdapter.notifyDataSetChanged();*/

                        appointmentDetails(appId);
                    } else {
                        Utils.openAlertDialog(AppointmentDirectionActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/changeAppointmentStatus", param);
    }

    private void reviewAppoinment(String rating, String comment, String receiverId, String referenceId, String reviewType) {
        loadingView.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("rating", rating);
        //param.put("comment",  Utils.convertStringToUTF8SendToserver(comment));
        param.put("comment", comment);
        param.put("receiverId", receiverId);
        param.put("referenceId", referenceId);
        param.put("reviewType", reviewType);

        WebService service = new WebService(AppointmentDirectionActivity.this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        appointmentDetails(appId);
                        review_button.setVisibility(View.GONE);
                    } else {
                        Utils.openAlertDialog(AppointmentDirectionActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loadingView.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/giveReview", param);
    }

    // Input filter used to restrict amount input to be round off to 2 decimal places
    private void inputFilter(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String temp = et.getText().toString();
                if (temp.contains(".")) {
                    if (et.getText().toString().substring(et.getText().toString().indexOf(".") + 1, et.length()).length() == 2) {
                        InputFilter[] fArray = new InputFilter[1];
                        fArray[0] = new InputFilter.LengthFilter(arg0.length());
                        et.setFilters(fArray);
                    }
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

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
                    }
                }
            }
        });
    }

    private void reviewDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.apoim_review_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        EditText ed_review = dialog.findViewById(R.id.ed_review);
        TextView tv_submit_button = dialog.findViewById(R.id.tv_submit_button);
        RatingBar ratting_bar = dialog.findViewById(R.id.ratting_bar);
        ImageView iv_close_button = dialog.findViewById(R.id.iv_close_button);

        iv_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = ed_review.getText().toString().trim();
                float rating = ratting_bar.getRating();
                String receiversId = "";

                  if (rating == 0.0) {
                    Utils.openAlertDialog(AppointmentDirectionActivity.this, "Please give rating");
                    return;
                } else if (comments.equals("")) {
                    Utils.openAlertDialog(AppointmentDirectionActivity.this, "Please give review");
                    return;
                }

                if (listInfo.appoimData.appointForId.equals(myUserId)) {
                    receiversId = listInfo.appoimData.appointById;

                } else if (listInfo.appoimData.appointById.equals(myUserId)) {
                    receiversId = listInfo.appoimData.appointForId;
                }

                reviewAppoinment(String.valueOf(rating), comments, receiversId, listInfo.appoimData.appId, Constant.Appointment_type_review);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}









































