package com.apoim.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.CreateAppointMentActivity;
import com.apoim.activity.MatchGalleryActivity;
import com.apoim.activity.OtherProfileActivity;
import com.apoim.activity.SelectPaymentTypeActivity;
import com.apoim.activity.SettingsActivity;
import com.apoim.activity.SubscriptionPayActivity;
import com.apoim.adapter.newProfile.NewProfileAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetNewImageClick;
import com.apoim.listener.OnInfoWindowElemTouchListener;
import com.apoim.modal.FilterInfo;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.OnlineInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.modal.UserListInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cv.MapWrapperLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.TRANSPARENT;
import static com.apoim.activity.SubscriptionPayActivity.paymentType;
import static com.apoim.app.Apoim.TAG;

/**
 * Created by mindiii on 7/3/18.
 */

public class MapListFragment extends Fragment  implements OnMapReadyCallback {
    private Context mContext;
    private SupportMapFragment mapFragment;
    protected GoogleMap mGoogleMap;
    //  private BalloonAdapter balloonAdapter;
    ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList;
    FilterInfo filterInfo = new FilterInfo();
    Double current_lat=0.0, current_lng=0.0;
    private Session session;
    private InsLoadingView loadingView;

    private MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView imageView21, iv_info_button;
    private OnInfoWindowElemTouchListener infoButtonListener;
    Map<String,Object> onlineMapList;

    //LinearLayout ll_dummyInfowindo;
    //TextView tvTest;

    public static MapListFragment newInstance(FilterInfo filterInfo, Double current_lat, Double current_lng) {
        Bundle args = new Bundle();
        MapListFragment fragment = new MapListFragment();
        args.putSerializable(Constant.filterInfo,filterInfo);
        args.putDouble(Constant.current_lat,current_lat);
        args.putDouble(Constant.current_lng,current_lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            filterInfo = (FilterInfo) getArguments().getSerializable(Constant.filterInfo);
            current_lat = getArguments().getDouble(Constant.current_lat);
            current_lng = getArguments().getDouble(Constant.current_lng);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        init(view);
        loadingView = view.findViewById(R.id.loading_view);
        onlineMapList = new HashMap<>();
        session = new Session(mContext,getActivity());
        filterInfo = session.getFilterInfo();
        if(filterInfo == null){
            filterInfo = new FilterInfo();
        }else {
            filterInfo = session.getFilterInfo();
        }
        mapBeanArrayList = new ArrayList<>();



        if(session.getUser().userDetail.mapPayment != null){
            if(session.getUser().userDetail.mapPayment.equals("1")){
                addUserFirebaseDatabase();
                getUsersList();
            }else {
                moveToPaymentScreen(getString(R.string.pay_for_appointment),getString(R.string.access_map_to_create_appointments));

            }
        }else {
            moveToPaymentScreen(getString(R.string.pay_for_appointment),getString(R.string.access_map_to_create_appointments));

        }

        return view;
    }


    private void moveToPaymentScreen(String headerText, String msgText){
        final Dialog _dialog = new Dialog(mContext);
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


        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SubscriptionPayActivity.class);
                intent.putExtra(paymentType,Constant.PayForMap);// 1 for pay for map
                startActivity(intent);
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

    private void init(View view) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapWrapperLayout = view.findViewById(R.id.parentView);
        mapFragment.getMapAsync(this);
    }

    private void setMapWrapperView(GoogleMap mGoogleMap) {
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mGoogleMap, getPixelsFromDp(mContext, 39 + 20));
        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window_layout, null);
        this.infoTitle = infoWindow.findViewById(R.id.tv_title);
        this.infoSnippet = infoWindow.findViewById(R.id.tv_address);
        this.imageView21 = infoWindow.findViewById(R.id.imageView21);
        this.iv_info_button = infoWindow.findViewById(R.id.iv_info_button);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(iv_info_button/*,
                getResources().getDrawable(R.drawable.ico_pr_app),
                getResources().getDrawable(R.drawable.ico_pr_app)*/) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                int position = Integer.parseInt(marker.getTitle());

                GetOtherProfileInfo otherProfileInfo = new GetOtherProfileInfo();
                otherProfileInfo.UserDetail.isAppointment = mapBeanArrayList.get(position).isAppointment;
                otherProfileInfo.UserDetail.userId = mapBeanArrayList.get(position).userId;
                otherProfileInfo.UserDetail.fullName = mapBeanArrayList.get(position).fullName;
                otherProfileInfo.UserDetail.address = mapBeanArrayList.get(position).address;
                otherProfileInfo.UserDetail.latitude = mapBeanArrayList.get(position).latitude;
                otherProfileInfo.UserDetail.longitude = mapBeanArrayList.get(position).longitude;
                otherProfileInfo.UserDetail.gender = mapBeanArrayList.get(position).gender;
                otherProfileInfo.UserDetail.age = mapBeanArrayList.get(position).age;
                otherProfileInfo.UserDetail.showOnMap = mapBeanArrayList.get(position).showOnMap;




                GetOtherProfileInfo.UserDetailBean.ImagesBean imagesBean =new GetOtherProfileInfo.UserDetailBean.ImagesBean();
                imagesBean.image = mapBeanArrayList.get(position).profileImage;
                imagesBean.userImgId = "1";
                otherProfileInfo.UserDetail.profileImage.add(imagesBean);
                // Here we can perform some action triggered after clicking the button

                if(mapBeanArrayList.get(position).isAppointment.equals("0")){
                    getProfileDetails(otherProfileInfo);

                    /*Intent intent = new Intent(mContext,CreateAppointMentActivity.class);
                    intent.putExtra("profileDetails",otherProfileInfo.UserDetail);
                    intent.putExtra("userId",mapBeanArrayList.get(position).userId);
                    startActivityForResult(intent,190);*/
                }
                else if(mapBeanArrayList.get(position).isAppointment.equals("1")){
                    Utils.openAlertDialog(mContext, "You already sent appointment request to "+mapBeanArrayList.get(position).fullName);
                }
                else  if(mapBeanArrayList.get(position).isAppointment.equals("2")){
                    Utils.openAlertDialog(mContext, "You already have an appointment with "+mapBeanArrayList.get(position).fullName);
                }

            }
        };


        this.iv_info_button.setOnTouchListener(infoButtonListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMapWrapperView(googleMap);
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
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
                    Projection projection = mGoogleMap.getProjection();
                    LatLng markerLocation = marker.getPosition();
                    Point screenPosition = projection.toScreenLocation(markerLocation);

                    int position = Integer.parseInt(marker.getTitle());
                    Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                    intent.putExtra("userId",mapBeanArrayList.get(position).userId);
                    startActivity(intent);
                }
            }
        });


        setUpMap(googleMap);
    }

    private void setUpMap(GoogleMap googleMap) {

        if(filterInfo.latitude != null && filterInfo.longitude != null){
            LatLng start = new LatLng(Double.parseDouble(filterInfo.latitude ), Double.parseDouble(filterInfo.longitude));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 12.0f));
        }else {
            LatLngBounds bounds = new LatLngBounds(new LatLng(current_lat, current_lng), new LatLng(current_lat, current_lng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
        }


        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }


    private void getProfileDetails(GetOtherProfileInfo otherProfileInfo) {

        WebService service = new WebService(mContext, TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        Gson gson = new Gson();
                        GetOtherProfileInfo otherProfileInfo = gson.fromJson(response, GetOtherProfileInfo.class);

                        Intent intent = new Intent(mContext,CreateAppointMentActivity.class);
                        intent.putExtra("profileDetails",otherProfileInfo.UserDetail);
                        intent.putExtra("userId",otherProfileInfo.UserDetail.userId);
                        startActivityForResult(intent,190);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callGetSimpleVolley("user/getUserProfile?userId=" + otherProfileInfo.UserDetail.userId + "");

    }

    private void getUsersList(){
        final String userId = session.getUser().userDetail.userId;
        loadingView.setVisibility(View.VISIBLE);
        Map<String,String> param = new HashMap<>();

        if(filterInfo.ageStart != null){
            if(filterInfo.latitude == null &&  filterInfo.longitude == null){
                filterInfo.latitude = String.valueOf(current_lat);
                filterInfo.longitude = String.valueOf(current_lng);
            }

            if(filterInfo.showMe == null ){
                filterInfo.showMe = "";
            }

            param.put("latitude",filterInfo.latitude);
            param.put("longitude",filterInfo.longitude);
            param.put("showMe",filterInfo.showMe);
            param.put("ageStart",filterInfo.ageStart);
            param.put("ageEnd",filterInfo.ageEnd);

            if(filterInfo.filterBy == null){
                filterInfo.filterBy = "1";
            }
            param.put("newUsers",filterInfo.filterBy);
        }

        else {
            param.put("latitude", String.valueOf(current_lat));
            param.put("longitude", String.valueOf(current_lng));
            param.put("showMe","");
            param.put("ageStart","");
            param.put("ageEnd","");
        }
        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loadingView.setVisibility(View.GONE);

                UserListInfo.NearByUsersBean userListInfo = null;

                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String status = object.getString("status");

                    if(status.equals("success")){
                        mapBeanArrayList.clear();
                        JSONArray array = object.getJSONArray("nearByUsers");
                        for(int i =0;i<array.length();i++) {
                            userListInfo = new UserListInfo.NearByUsersBean();
                            JSONObject jsonObject = array.getJSONObject(i);
                            userListInfo.showOnMap = jsonObject.getString("showOnMap");
                            if(userListInfo.showOnMap.equals("1")){

                                userListInfo.isAppointment = jsonObject.getString("isAppointment");
                                userListInfo.showOnMap = jsonObject.getString("showOnMap");
                                userListInfo.userId = jsonObject.getString("userId");
                                userListInfo.fullName = jsonObject.getString("fullName");
                                userListInfo.address = jsonObject.getString("address");
                                userListInfo.age = jsonObject.getString("age");
                                userListInfo.gender = jsonObject.getString("gender");
                                userListInfo.latitude = jsonObject.getString("latitude");
                                userListInfo.longitude = jsonObject.getString("longitude");
                                userListInfo.distance = jsonObject.getDouble("distance");
                                userListInfo.profileImage = jsonObject.getString("profileImage");
                                userListInfo.showTopPayment = jsonObject.getString("showTopPayment");
                                userListInfo.mapPayment = jsonObject.getString("mapPayment");

                                //Create iterator on Set
                                Iterator iterator = onlineMapList.entrySet().iterator();

                                while (iterator.hasNext()) {
                                    Map.Entry mapEntry = (Map.Entry) iterator.next();
                                    // Get Key
                                    String key = (String) mapEntry.getKey();
                                    //Get Value
                                    OnlineInfo onlineInfo = (OnlineInfo) mapEntry.getValue();
                                    String value = onlineInfo.lastOnline;

                                    if(userListInfo.userId.equals(key)){
                                        userListInfo.status = value;

                                    }
                                }

                                if(filterInfo.filterBy != null){
                                    if(filterInfo.filterBy.equals("2")){
                                        if(userListInfo.status.equals("online"))
                                            mapBeanArrayList.add(userListInfo);
                                    }else mapBeanArrayList.add(userListInfo);
                                }else mapBeanArrayList.add(userListInfo);
                            }

                        }

                        if (mapBeanArrayList.size() > 0) {
                            wattingForImage(mapBeanArrayList);
                        }

                        if(mapBeanArrayList.size() == 0){
                            Utils.openAlertDialog(mContext,getString(R.string.ohh_sorry_no_record_found));
                        }

                    }else {
                        if(mContext != null)
                            Utils.openAlertDialog(mContext,getString(R.string.ohh_sorry_no_record_found));
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                loadingView.setVisibility(View.GONE);

            }
        });

        service.callSimpleVolley("user/nearByUsers",param);
    }


    private void moveToNext(final ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList, final int i){

        if(i<mapBeanArrayList.size()) {

            final View markerView = ((LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custommarkerlayout, null);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            if(getActivity() != null){
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            }
            markerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.buildDrawingCache();
            CircleImageView imageuser = markerView.findViewById(R.id.marker_image);
            ImageView iv_outer = markerView.findViewById(R.id.iv_outer);
            /*.............info window Adapter...........................*/
            // BalloonAdapter balloonAdapter = new BalloonAdapter(getActivity().getLayoutInflater());
            if(mGoogleMap != null){
                //mGoogleMap.setInfoWindowAdapter(balloonAdapter);
                setAdapter(mGoogleMap);
            }

            /*..............................................................................*/
            final UserListInfo.NearByUsersBean mapBean = mapBeanArrayList.get(i);

            if(mapBean.gender.equals(Constant.PROFILE_MALE)){
                iv_outer.setImageResource(R.drawable.ico_map_pin_m);
            }else if(mapBean.gender.equals(Constant.PROFILE_FEMALE)){
                iv_outer.setImageResource(R.drawable.ico_map_pin_f);
            }

            Picasso.with(getContext())
                    .load(mapBean.profileImage).placeholder(R.drawable.logo)
                    .into(imageuser, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                            Bitmap finalBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(finalBitmap);
                            markerView.draw(canvas);

                            if (MapListFragment.this.getView() != null) {
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

                                if(mGoogleMap != null){
                                    dropPinEffect(mGoogleMap.addMarker(markerOptions));
                                }


                                if (i < mapBeanArrayList.size()) {
                                    moveToNext(mapBeanArrayList, i + 1);
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            Log.d("ERROR::", "In error");
                        }
                    });

        }
    }


    private void setInfoData(int pos, Marker marker){
        UserListInfo.NearByUsersBean bean = mapBeanArrayList.get(pos);
        infoTitle.setText(bean.fullName);
        infoSnippet.setText(bean.address);
        infoButtonListener.setMarker(marker);
        Picasso.with(getContext()).load(bean.profileImage).into(imageView21);

        /*infoWindow.findViewById(R.id.iv_info_button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        Log.d("", "a view in info window clicked");
                        break;
                }
                return true;
            }
        });*/
    }


    private void setAdapter(GoogleMap map){
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                int pos = Integer.parseInt(marker.getTitle());
                setInfoData(pos, marker);
                // infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }


    private void wattingForImage(final ArrayList<UserListInfo.NearByUsersBean> mapBeanArrayList){
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

    private void addUserFirebaseDatabase( ) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constant.online).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                onlineMapList.put(dataSnapshot.getKey(),onlineStatus);


            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                    onlineMapList.put(dataSnapshot.getKey(),onlineStatus);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                    onlineMapList.put(dataSnapshot.getKey(),onlineStatus);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}



        });


    }
}
