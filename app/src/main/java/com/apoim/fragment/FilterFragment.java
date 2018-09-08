package com.apoim.fragment;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apoim.R;
import com.apoim.helper.Constant;
import com.apoim.modal.FilterInfo;
import com.apoim.session.Session;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;


import static android.content.ContentValues.TAG;

/**
 * Created by mindiii on 7/3/18.
 */

public class FilterFragment extends Fragment {

    Context mContext;
    SupportPlaceAutocompleteFragment autocompleteFragment;
    private TextView tv_location,clear;
    private ImageView iv_back;
    private TextView done_button;
    private RadioGroup rg_show_me,rg_filter_by;
    private RadioButton rb_girls,rb_guys,rb_both,rb_all,rb_online,rb_new_;
    private String latitude,longitude,showMe,miniValue,maxiValue,from,address;
    private Session session;
    FilterInfo filterInfo;
    Double current_lat=0.0, current_lng=0.0;
    CrystalRangeSeekbar rangeSeekbar;

    public static FilterFragment newInstance(String from, Double current_lat, Double current_lng) {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        args.putString(Constant.from,from);
        args.putDouble(Constant.current_lat,current_lat);
        args.putDouble(Constant.current_lng,current_lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            from = getArguments().getString(Constant.from);
            current_lat = getArguments().getDouble(Constant.current_lat);
            current_lng = getArguments().getDouble(Constant.current_lng);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_layout, container, false);
        rangeSeekbar = view.findViewById(R.id.rangeSeekbar1);
        rangeSeekbar.setMinValue(18f).apply();
        rangeSeekbar.setMaxValue(100f).apply();
        rangeSeekbar.setMaxStartValue(30f).apply();

        final TextView tvMin = view.findViewById(R.id.tvMin);
        final TextView tvMax = view.findViewById(R.id.tvMax);
        tv_location = view.findViewById(R.id.tv_location);
        iv_back = view.findViewById(R.id.iv_back);
        done_button = view.findViewById(R.id.done_button);
        clear = view.findViewById(R.id.clear);

        rg_show_me = view.findViewById(R.id.rg_show_me);
        rg_filter_by = view.findViewById(R.id.rg_filter_by);

        rb_girls = view.findViewById(R.id.rb_girls);
        rb_guys = view.findViewById(R.id.rb_guys);
        rb_both = view.findViewById(R.id.rb_both);
        rb_all = view.findViewById(R.id.rb_all);
        rb_online = view.findViewById(R.id.rb_online);
        rb_new_ = view.findViewById(R.id.rb_new_);

        filterInfo = new FilterInfo();
        session = new Session(mContext,getActivity());
        filterInfo = session.getFilterInfo();
        if(filterInfo == null){
            filterInfo = new FilterInfo();
        }


        if(filterInfo != null){
            if(filterInfo.address != null){
                tv_location.setText(filterInfo.address);
            }

            latitude = filterInfo.latitude;
            longitude = filterInfo.longitude;

            if(filterInfo.showMe != null){
                if(filterInfo.showMe.equals("1")){
                    rb_guys.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_guys.setChecked(true);
                }else if(filterInfo.showMe.equals("2")){
                    rb_girls.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_girls.setChecked(true);
                }else {
                    rb_both.setChecked(true);
                    rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
                }
            }else {
                rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
            }
            rb_all.setTextColor(getResources().getColor(R.color.colorPrimary));

            if(filterInfo.ageStart != null && filterInfo.ageEnd != null){
               /* String start_Age = "start Age"+(filterInfo.ageStart);
                String end_Age = "start Age"+(filterInfo.ageEnd);

                Toast.makeText(mContext,"start Age"+start_Age+"\n"+"end age"+end_Age,Toast.LENGTH_SHORT).show();*/
                rangeSeekbar.setMinStartValue(Float.parseFloat(filterInfo.ageStart));
                rangeSeekbar.setMaxStartValue(Float.parseFloat(filterInfo.ageEnd)).apply();
            }
        }

        autocompleteFragment  = (SupportPlaceAutocompleteFragment)getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                tv_location.setText(place.getAddress());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                address = String.valueOf(place.getAddress());

                if(filterInfo != null){
                    filterInfo.address = address;
                    filterInfo.latitude = latitude;
                    filterInfo.longitude = longitude;
                }


            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
                miniValue = String.valueOf(minValue);
                maxiValue = String.valueOf(maxValue);
                if(filterInfo != null){
                    filterInfo.ageStart = String.valueOf(minValue);
                    filterInfo.ageEnd = String.valueOf(maxValue);
                }

            }
        });

        // set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));

                miniValue = String.valueOf(minValue);
                maxiValue = String.valueOf(maxValue);
                if(filterInfo != null){
                    filterInfo.ageStart = String.valueOf(minValue);
                    filterInfo.ageEnd = String.valueOf(maxValue);
                }

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterInfo != null){
                    replaceFragment(ListorMapFragment.newInstance(filterInfo,from),false,R.id.fragment_place);
                }else {

                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterInfo filterInfo = new FilterInfo();
                session.setFilterSession(filterInfo);
                replaceFragment(ListorMapFragment.newInstance(filterInfo,from),false,R.id.fragment_place);
            }
        });

        rg_show_me.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_girls) {
                    rb_girls.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    showMe = "2";
                }else if(i == R.id.rb_guys){
                    rb_girls.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    showMe = "1";
                }else  if(i == R.id.rb_both){
                    rb_girls.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
                    showMe = "";
                }
                if(filterInfo != null)
                    filterInfo.showMe = showMe;
            }
        });

        rg_filter_by.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_all) {
                    rb_all.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_online.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_new_.setTextColor(getResources().getColor(R.color.colorBlack));

                }else if(i == R.id.rb_online){
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_online.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_new_.setTextColor(getResources().getColor(R.color.colorBlack));

                }else  if(i == R.id.rb_new_){
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_online.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_new_.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterInfo filterInfo1 = new FilterInfo();
                if(filterInfo != null){

                    if(latitude == null && longitude== null){
                        filterInfo1.latitude = filterInfo.latitude;
                        filterInfo1.longitude = filterInfo.longitude;
                    }

                    if(showMe == null){
                        filterInfo1.showMe = filterInfo.showMe;
                    }

                    if(address == null){
                        filterInfo1.address = filterInfo.address;
                    }

                    filterInfo1.ageStart = miniValue;
                    filterInfo1.ageEnd = maxiValue;




                    session.setFilterSession(filterInfo);

                    replaceFragment(ListorMapFragment.newInstance(filterInfo,from),false,R.id.fragment_place);

                }

            }
        });



        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fm = getFragmentManager();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }
}
