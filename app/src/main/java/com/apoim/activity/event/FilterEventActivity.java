package com.apoim.activity.event;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apoim.R;
import com.apoim.adapter.newEvent.PopularityAdapter;
import com.apoim.helper.Constant;
import com.apoim.modal.EventFilterData;
import com.apoim.modal.EventFilterInfo;
import com.apoim.session.Session;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.apoim.activity.event.CreateEventActivity.RattingIds;

public class FilterEventActivity extends AppCompatActivity {
    private PopularityAdapter adapter;
    private RecyclerView recycler_view;
    private ArrayList<EventFilterInfo> list;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private String latitude, longitude, address, ratting;
    private TextView tv_location, tv_apply;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_event);

        tv_location = findViewById(R.id.tv_location);

        list = new ArrayList<>();
        session = new Session(this);

        for (int i = 0; i < 5; i++) {
            EventFilterInfo info = new EventFilterInfo();
            info.isSelected = false;
            info.ratting = 6 - (i + 1);
            list.add(info);
        }

        tv_apply = findViewById(R.id.tv_apply);
        recycler_view = findViewById(R.id.recycler_view);
        adapter = new PopularityAdapter(list, this, new CreateEventActivity.FriedsIdsListner() {
            @Override
            public void getIds(String ids) {
                ratting = ids;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(manager);
        recycler_view.setAdapter(adapter);

        autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                tv_location.setText(place.getAddress());

                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                address = String.valueOf(place.getAddress());

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(view -> {
            onBackPressed();
        });

        findViewById(R.id.iv_refresh).setOnClickListener(view -> {
            tv_location.setText("");

            for (int i = 0; i < 5; i++) {
                EventFilterInfo info = new EventFilterInfo();
                info.isSelected = false;
                info.ratting = i;
                list.set(i, info);
            }

            RattingIds = "";

            EventFilterData data = new EventFilterData();
            session.createFilterData(data);

            adapter.notifyDataSetChanged();
        });

        tv_apply.setOnClickListener(view -> {
            Intent intent = new Intent(FilterEventActivity.this, CreateNewEventActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("ratting", ratting);
            setResult(RESULT_OK, intent);

            EventFilterData data = new EventFilterData();
            data.rating = ratting;
            data.latitude = latitude;
            data.longitude = longitude;
            data.address = address;
            data.isApply = true;

            if (tv_location.getText().toString().trim().equals("") && ratting == null) {
                if (ratting != null) {

                    if (ratting.equals(""))
                        data.isApply = false;
                } else {
                    data.isApply = false;
                }


            }

            session.createFilterData(data);

            finish();
        });

        EventFilterData filterData = session.getFilterData();
        if (filterData != null) {
            if (filterData.address != null) {
                tv_location.setText(filterData.address + "");
            }

            latitude = filterData.latitude;
            longitude = filterData.longitude;
            ratting = filterData.rating;

            if (ratting != null) {
                List<String> tempList = new ArrayList<String>(Arrays.asList(ratting.split(",")));
                for (int i = 0; i < list.size(); i++) {

                    for (int j = 0; j < tempList.size(); j++) {
                        if (tempList.get(j).equals(list.get(i).ratting + "")) {
                            list.get(i).isSelected = true;
                        }

                    }

                }

                adapter.notifyDataSetChanged();
            }

        }


    }
}
