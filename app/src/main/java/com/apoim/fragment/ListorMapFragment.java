package com.apoim.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.apoim.R;
import com.apoim.activity.FilterActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.CustomClick;
import com.apoim.modal.FilterInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mindiii on 7/3/18.
 */

public class ListorMapFragment extends Fragment implements View.OnClickListener ,LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Context mContext;
    private ImageView list_map_togle;
    private boolean isMapOpen;
    private ImageView iv_filter;
    FilterInfo filterInfo = new FilterInfo();
    private String from = "";

    private static final String TAG = "Apoim";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Double current_lat = 0.0, current_lng = 0.0;
    Session session;

    private FrameLayout fragment_place_fragment;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public static ListorMapFragment newInstance(FilterInfo filterInfo,String from) {
        Bundle args = new Bundle();
        ListorMapFragment fragment = new ListorMapFragment();
        args.putSerializable(Constant.filterInfo,filterInfo);
        args.putString(Constant.from,from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            filterInfo = (FilterInfo) getArguments().getSerializable(Constant.filterInfo);
            if(getArguments().getString(Constant.from) != null)
                from = getArguments().getString(Constant.from);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_or_map_layout, container, false);
        fragment_place_fragment = view.findViewById(R.id.fragment_place_fragment);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new Session(mContext,getActivity());
        FilterInfo filterInfo = session.getFilterInfo();

        list_map_togle = view.findViewById(R.id.list_map_togle);
        iv_filter = view.findViewById(R.id.iv_filter);

        iv_filter.setOnClickListener(this);
        list_map_togle.setOnClickListener(this);
        list_map_togle.setImageResource(R.drawable.ico_map);


        if(filterInfo != null){
            if(filterInfo.latitude != null || filterInfo.ageEnd != null || filterInfo.address != null){
                iv_filter.setImageResource(R.drawable.ico_filter_active);
            }
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(session.getLatlng() != null){
            String str = session.getLatlng();
            List<String> latlngList = Arrays.asList(str.split(","));
            current_lat = Double.valueOf(latlngList.get(0));
            current_lng = Double.valueOf(latlngList.get(1));

            switch (from) {
                case "UserListFragment":
                    replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
                case "MapListFragment":
                    isMapOpen = true;
                    list_map_togle.setImageResource(R.drawable.ico_list);
                    replaceFragment(MapListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
                default:
                    replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
            }
        }


    }



   /* public boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }*/
    private boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }
        // otherwise return false
        return false;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }



    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fm = getFragmentManager();
        // boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);

        if(getChildFragmentManager() != null && getView()!=null){
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.list_map_togle :{
                if(isMapOpen){
                    isMapOpen = false;
                    list_map_togle.setImageResource(R.drawable.ico_map);
                    from = "UserListFragment";
                    replaceFragment(NewUserListFragment.newInstance(filterInfo,current_lat,current_lng),false,R.id.fragment_place_fragment);
                }else {
                    isMapOpen = true;
                    list_map_togle.setImageResource(R.drawable.ico_list);
                    from = "MapListFragment";
                    replaceFragment(MapListFragment.newInstance(filterInfo,current_lat,current_lng),false,R.id.fragment_place_fragment);

                }
                return;
            }

            case R.id.iv_filter:{
                Intent intent = new Intent(mContext, FilterActivity.class);
                intent.putExtra("from",from);
                intent.putExtra("current_lat",current_lat);
                intent.putExtra("current_lng",current_lng);
                startActivity(intent);
                // replaceFragment(FilterFragment.newInstance(from,current_lat,current_lng),false,R.id.fragment_place);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        current_lat = location.getLatitude();
        current_lng = location.getLongitude();
        if (current_lat != null && current_lng!=null) {
            if( mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            if(session.getLatlng() == null){
                switch (from) {
                    case "UserListFragment":
                        replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                        break;
                    case "MapListFragment":
                        isMapOpen = true;
                        list_map_togle.setImageResource(R.drawable.ico_list);
                        replaceFragment(MapListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                        break;
                    default:
                        replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                        break;
                }
            }

            session.saveLatLng(current_lat,current_lng);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        FilterInfo filterInfo = session.getFilterInfo();
        if(filterInfo != null){
            if(filterInfo.latitude != null || filterInfo.ageEnd != null || filterInfo.address != null){
                iv_filter.setImageResource(R.drawable.ico_filter_active);
            }else {
                iv_filter.setImageResource(R.drawable.ico_filter);
            }
        }

        if(session.getLatlng() != null){
            String str = session.getLatlng();
            List<String> latlngList = Arrays.asList(str.split(","));
            current_lat = Double.valueOf(latlngList.get(0));
            current_lng = Double.valueOf(latlngList.get(1));

            switch (from) {
                case "UserListFragment":
                    //replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
                case "MapListFragment":
                    isMapOpen = true;
                    list_map_togle.setImageResource(R.drawable.ico_list);
                    replaceFragment(MapListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
                default:
                    //replaceFragment(NewUserListFragment.newInstance(filterInfo, current_lat, current_lng), false, R.id.fragment_place_fragment);
                    break;
            }
        }

        if(!isGPSEnabled()){
            Utils.showGPSDisabledAlertToUser(mContext);
        }
    }

    protected void startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,},
                        Constant.ACCESS_FINE_LOCATION);
            } else {
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.
                        requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        Log.d(TAG, "Location update started ..............: ");
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Apoim.getInstance().getRequestQueue().cancelAll(Apoim.TAG);
        if(fragment_place_fragment!=null)
            fragment_place_fragment.removeAllViews();
        fragment_place_fragment = null;
    }
}
