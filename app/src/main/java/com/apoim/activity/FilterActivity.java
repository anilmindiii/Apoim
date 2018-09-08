package com.apoim.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.fragment.ListorMapFragment;
import com.apoim.modal.FilterInfo;
import com.apoim.session.Session;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import static android.content.ContentValues.TAG;

public class FilterActivity extends AppCompatActivity {

    SupportPlaceAutocompleteFragment autocompleteFragment;
    private TextView tv_location, clear;
    private ImageView iv_back;
    private TextView done_button;
    private RadioGroup rg_show_me, rg_filter_by;
    private RadioButton rb_girls, rb_guys, rb_both, rb_all, rb_online, rb_new_,rb_transgender;
    private String latitude, longitude, showMe, miniValue, maxiValue, from, address, filterBy;
    private Session session;
    FilterInfo filterInfo;
    Double current_lat = 0.0, current_lng = 0.0;
    CrystalRangeSeekbar rangeSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (getIntent() != null) {
            from = getIntent().getStringExtra("from");
            current_lat = getIntent().getDoubleExtra("current_lat", 0.0);
            current_lng = getIntent().getDoubleExtra("current_lng", 0.0);
        }

        rangeSeekbar = findViewById(R.id.rangeSeekbar1);
        rangeSeekbar.setMinValue(18f).apply();
        rangeSeekbar.setMaxValue(100f).apply();
        rangeSeekbar.setMaxStartValue(30f).apply();

        final TextView tvMin = findViewById(R.id.tvMin);
        final TextView tvMax = findViewById(R.id.tvMax);
        tv_location = findViewById(R.id.tv_location);
        iv_back = findViewById(R.id.iv_back);
        done_button = findViewById(R.id.done_button);
        clear = findViewById(R.id.clear);

        rg_show_me = findViewById(R.id.rg_show_me);
        rg_filter_by = findViewById(R.id.rg_filter_by);

        rb_girls = findViewById(R.id.rb_girls);
        rb_guys = findViewById(R.id.rb_guys);
        rb_both = findViewById(R.id.rb_both);
        rb_all = findViewById(R.id.rb_all);
        rb_online = findViewById(R.id.rb_online);
        rb_new_ = findViewById(R.id.rb_new_);
        rb_transgender = findViewById(R.id.rb_transgender);

        filterInfo = new FilterInfo();
        session = new Session(this, this);
        filterInfo = session.getFilterInfo();
        if (filterInfo == null) {
            filterInfo = new FilterInfo();
        }


        if (filterInfo != null) {
            if (filterInfo.address != null) {
                tv_location.setText(filterInfo.address);
            }

            latitude = filterInfo.latitude;
            longitude = filterInfo.longitude;

            if (filterInfo.showMe != null) {
                if (filterInfo.showMe.equals("1")) {
                    rb_guys.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_guys.setChecked(true);
                }
                else if (filterInfo.showMe.equals("2")) {
                    rb_girls.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_girls.setChecked(true);
                }
                else if (filterInfo.showMe.equals("3")) {
                    rb_transgender.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_transgender.setChecked(true);
                }
                else {
                    rb_both.setChecked(true);
                    rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
                }
            } else {
                rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
            }


            if (filterInfo.ageStart != null && filterInfo.ageEnd != null) {
                rangeSeekbar.setMinStartValue(Float.parseFloat(filterInfo.ageStart));
                rangeSeekbar.setMaxStartValue(Float.parseFloat(filterInfo.ageEnd)).apply();
            }

            if (filterInfo.filterBy != null)

                if (filterInfo.filterBy.equals("2")) {
                    rb_online.setChecked(true);
                } else if (filterInfo.filterBy.equals("1")) {
                    rb_all.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_all.setChecked(true);
                } else if (filterInfo.filterBy.equals("3")) {
                    rb_new_.setChecked(true);
                } else {
                    rb_all.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
        }

        autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                tv_location.setText(place.getAddress());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                address = String.valueOf(place.getAddress());

                if (filterInfo != null) {
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
                if (filterInfo != null) {
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
                if (filterInfo != null) {
                    filterInfo.ageStart = String.valueOf(minValue);
                    filterInfo.ageEnd = String.valueOf(maxValue);
                }

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterInfo filterInfo = new FilterInfo();
                session.setFilterSession(filterInfo);
                finish();
                // replaceFragment(ListorMapFragment.newInstance(filterInfo,from),false,R.id.fragment_place);
            }
        });

        rg_show_me.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_girls) {
                    rb_girls.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_transgender.setTextColor(getResources().getColor(R.color.colorBlack));
                    showMe = "2";
                } else if (i == R.id.rb_guys) {
                    rb_girls.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_transgender.setTextColor(getResources().getColor(R.color.colorBlack));
                    showMe = "1";
                } else if(i == R.id.rb_transgender){
                    rb_girls.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_transgender.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    showMe = "3";
                }

                else if (i == R.id.rb_both) {
                    rb_girls.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_guys.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_transgender.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorPurple));
                    showMe = "";
                }
                if (filterInfo != null)
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
                    filterBy = "1";

                } else if (i == R.id.rb_online) {
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_online.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_new_.setTextColor(getResources().getColor(R.color.colorBlack));
                    filterBy = "2";

                } else if (i == R.id.rb_new_) {
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_online.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_new_.setTextColor(getResources().getColor(R.color.colorPrimary));
                    filterBy = "3";
                }

                if (filterInfo != null) {
                    filterInfo.filterBy = filterBy;
                }

            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterInfo filterInfo1 = new FilterInfo();
                if (filterInfo != null) {

                    if (latitude == null && longitude == null) {
                        filterInfo1.latitude = filterInfo.latitude;
                        filterInfo1.longitude = filterInfo.longitude;
                    }

                    if (showMe == null) {
                        filterInfo1.showMe = filterInfo.showMe;
                    }

                    if (address == null) {
                        filterInfo1.address = filterInfo.address;
                    }

                    filterInfo1.ageStart = miniValue;
                    filterInfo1.ageEnd = maxiValue;


                    session.setFilterSession(filterInfo);
                    finish();

                    // replaceFragment(ListorMapFragment.newInstance(filterInfo,from),false,R.id.fragment_place);

                }

            }
        });

    }
}
