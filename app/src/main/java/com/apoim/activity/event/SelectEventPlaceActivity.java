package com.apoim.activity.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.appointment.CreateAppointMentActivity;
import com.apoim.adapter.apoinment.BusinessHorizontalListAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.GoogleDirection;
import com.apoim.listener.PositionListner;
import com.apoim.modal.BusinessListInfo;
import com.apoim.server_task.WebService;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SelectEventPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private InsLoadingView loading_view;
    private ImageView iv_back;
    private RecyclerView profile_horizontal_recycler;
    private BusinessHorizontalListAdapter horizontalListAdapter;
    private ArrayList<BusinessListInfo.BusinessListBean> businessList;

    //map work
    private MapFragment mapFragment;
    protected GoogleMap mGoogleMap;

    // for getting current location
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

    private PlaceAutocompleteFragment autocompleteFragment;
    private String appointLatitude, appointLongitude, eventAddress, appointTime, appointDate, appointForId,appointmentId;

    private LinearLayout ly_location, ly_is_buz_added;
    private ImageView iv_buz_image;
    private TextView tv_buz_name, tv_buz_location, tv_buz_distance,tv_distance;

    private String businessId = "";
    private TextView tv_location,tv_selectAddr;
    private String eventplaceImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_place);

        init();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

                eventplaceImage = bean.businessImage;

                Picasso.with(SelectEventPlaceActivity.this).load(bean
                        .businessImage).into(iv_buz_image);

                eventAddress = bean.businessAddress;
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
                eventAddress = String.valueOf(place.getAddress());

                if (current_latitude != 0 && current_longitude != 0) {
                    LatLng start = new LatLng(current_latitude, current_longitude);
                    LatLng end = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    drowRouteAnimated(GoogleDirection.MODE_DRIVING, start, end);
                }

                ly_is_buz_added.setVisibility(View.GONE);
                ly_location.setVisibility(View.VISIBLE);
                for(BusinessListInfo.BusinessListBean bean:businessList){
                    bean.isSelected = false;
                }
                horizontalListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        tv_selectAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectEventPlaceActivity.this,CreateNewEventActivity.class);
                intent.putExtra("eventAddress",eventAddress);
                intent.putExtra("eventlatitude",appointLatitude);
                intent.putExtra("eventlogitude",appointLongitude);
                intent.putExtra("eventplaceImage",eventplaceImage);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        displayCurrentLocation();
        profile_horizontal_recycler.setAdapter(horizontalListAdapter);
    }

    private void init() {
        loading_view = findViewById(R.id.loading_view);
        iv_back = findViewById(R.id.iv_back);
        ly_location = findViewById(R.id.ly_location);
        ly_is_buz_added = findViewById(R.id.ly_is_buz_added);
        iv_buz_image = findViewById(R.id.iv_buz_image);
        tv_buz_name = findViewById(R.id.tv_buz_name);
        tv_buz_location = findViewById(R.id.tv_buz_location);
        tv_buz_distance = findViewById(R.id.tv_buz_distance);
        tv_location = findViewById(R.id.tv_location);
        profile_horizontal_recycler = findViewById(R.id.profile_horizontal_recycler);
        tv_distance = findViewById(R.id.tv_distance);
        tv_selectAddr = findViewById(R.id.tv_selectAddr);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void drowRouteAnimated(String MODE, LatLng start, LatLng end) {
        GoogleDirection gd = new GoogleDirection(SelectEventPlaceActivity.this);
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

                    } else {

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
        //wattingForImage(mapBeanArrayList);
        //setUpMap(ProfileInfo.latitude, ProfileInfo.longitude);
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
                Utils.openAlertDialog(SelectEventPlaceActivity.this, getResources().getString(R.string.alert_play_services_check));

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
            Utils.showGPSDisabledAlertToUser(SelectEventPlaceActivity.this);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) SelectEventPlaceActivity.this);
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
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) SelectEventPlaceActivity.this);
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
        if (LocationRuntimePermission.checkLocationPermission(SelectEventPlaceActivity.this)) {

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

}
