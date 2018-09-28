package com.apoim.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.event.CreateNewEventActivity;
import com.apoim.adapter.apoinment.EventRequestAdapter;
import com.apoim.adapter.apoinment.MyEventAdapter;
import com.apoim.app.Apoim;
import com.apoim.listener.DeleteListner;
import com.apoim.modal.EventRequestInfo;
import com.apoim.modal.MyEventInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 2/4/18.
 **/

public class EventFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    private Button btn_my_even,btn_even_request;
    private EventRequestAdapter eventRequestAdapter;
    private RecyclerView recycler_view;
    private MyEventAdapter myEventAdapter;
    InsLoadingView loading_view;
    ArrayList<EventRequestInfo.ListBean> eventList;
    ArrayList<MyEventInfo.ListBean> myEventList;
    RelativeLayout ly_no_record_found;
    int fromBack = 0;
    Session session;
    private EndlessRecyclerViewScrollListener scrollListener;
    String currentDate = "";
    private int startCount = 0;
    int eventType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment_layout,container,false);

        ImageView create_event = view.findViewById(R.id.create_event);
        btn_my_even = view.findViewById(R.id.btn_my_even);
        btn_even_request = view.findViewById(R.id.btn_even_request);
        recycler_view = view.findViewById(R.id.recycler_view);
        loading_view = view.findViewById(R.id.loading_view);
        ly_no_record_found = view.findViewById(R.id.ly_no_record_found);
        session = new Session(mContext);

        eventList = new ArrayList<>();
        myEventList = new ArrayList<>();

        eventRequestAdapter = new EventRequestAdapter(mContext,eventList);

        myEventAdapter = new MyEventAdapter(mContext,currentDate, myEventList, new DeleteListner() {
            @Override
            public void deleteItem(String eventId,int position) {
                deleteEventTask(eventId,position);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        eventType = 2;
        recycler_view.addOnScrollListener(scrollListener);
        recycler_view.setAdapter(eventRequestAdapter);

        create_event.setOnClickListener(this);
        btn_my_even.setOnClickListener(this);
        btn_even_request.setOnClickListener(this);
        return view;
    }

    private void loadNextDataFromApi() {
        startCount = startCount+10;

        if(eventType == 1){ // 1 for myevent
            myEventRequestEvent("myEvent");
        }else if(eventType == 2) {// 2 for eventRequest
            myEventRequestEvent("eventRequest");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.create_event:{
                if(session.getUser().userDetail.totalFriends != null) {
                   /* if (!session.getUser().userDetail.totalFriends.equals("")) {
                        int totalFriends = Integer.parseInt(session.getUser().userDetail.totalFriends);
                        if (totalFriends == 0) {
                            Utils.openAlertDialog(mContext, getString(R.string.no_friend_to_create_event));
                        } else {
                            Intent intent = new Intent(mContext, CreateEventActivity.class);
                            startActivity(intent);
                        }
                    }*/

                    Intent intent = new Intent(mContext, CreateNewEventActivity.class);
                    startActivity(intent);

                }

                break;
            }

            case R.id.btn_my_even:{

                btn_my_even.setEnabled(false);
                btn_even_request.setEnabled(false);


                btn_my_even.setBackground(ContextCompat.getDrawable(mContext,R.drawable.primary_right_side_round));
                btn_my_even.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));

                btn_even_request.setBackground(ContextCompat.getDrawable(mContext,R.drawable.white_left_round));
                btn_even_request.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));

                recycler_view.setAdapter(myEventAdapter);

                eventType = 1;// 1 for my event
                fromBack = R.id.btn_my_even;
                //recycler_view.setVisibility(View.GONE);
                startCount = 0;
                myEventList.clear();
                scrollListener.resetState();
                myEventRequestEvent("myEvent");

                break;
            }

            case R.id.btn_even_request:{

                btn_even_request.setEnabled(false);
                btn_my_even.setEnabled(false);

                btn_my_even.setBackground(ContextCompat.getDrawable(mContext,R.drawable.white_right_round));
                btn_my_even.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));

                btn_even_request.setBackground(ContextCompat.getDrawable(mContext,R.drawable.primary_left_side_round));
                btn_even_request.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));

                recycler_view.setAdapter(eventRequestAdapter);


                eventType = 2;// 1 for eventRequest
                fromBack = R.id.btn_even_request;
                //recycler_view.setVisibility(View.GONE);

                startCount = 0;

                eventList.clear();
                scrollListener.resetState();
                myEventRequestEvent("eventRequest");
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    private void deleteEventTask(String eventId, final int position) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        myEventList.remove(position);
                        myEventAdapter.notifyDataSetChanged();

                        if(myEventList.size() == 0){
                            ly_no_record_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_record_found.setVisibility(View.GONE);
                        }
                        // startActivity(new Intent(SignInActivity.this, LogOutActivity.class));

                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("event/deleteEvent", map);

    }


    @Override
    public void onResume() {
        super.onResume();
        // myEventRequestEvent("myEvent");
        if(fromBack == R.id.btn_even_request){
            btn_even_request.callOnClick();
        }else if(fromBack == R.id.btn_my_even){
            btn_my_even.callOnClick();
        }else {
            //myEventRequestEvent("eventRequest");
            btn_even_request.callOnClick();
        }

    }

    private void myEventRequestEvent(final String eventType) {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    try {
                        String currentDateServer = jsonObject.getString("currentDate");
                        currentDate = currentDateServer;
                    }catch (Exception e){

                    }

                    myEventAdapter.getData(currentDate);
                    recycler_view.setVisibility(View.VISIBLE);
                    if (status.equals("success")) {
                        Gson gson = new Gson();

                        if(eventType.equals("eventRequest")){
                            EventRequestInfo eventInfo =  gson.fromJson(response,EventRequestInfo.class);

                            eventList.addAll(eventInfo.List);

                           eventRequestAdapter.setCurrentDate(eventInfo.currentDate);


                            eventRequestAdapter.notifyDataSetChanged();

                            if(eventList.size() != 0){
                                ly_no_record_found.setVisibility(View.GONE);
                            }
                            btn_my_even.setEnabled(true);
                        }else {
                            btn_even_request.setEnabled(true);
                            MyEventInfo  myEventInfo = gson.fromJson(response,MyEventInfo.class);
                            myEventList.addAll(myEventInfo.List);


                            myEventAdapter.notifyDataSetChanged();
                            if(myEventList.size() != 0){
                                ly_no_record_found.setVisibility(View.GONE);
                            }
                        }

                    } else {

                        if(eventType.equals("eventRequest")){
                            btn_my_even.setEnabled(true);
                            if(eventList.size() == 0){
                                ly_no_record_found.setVisibility(View.VISIBLE);
                            }else {
                                ly_no_record_found.setVisibility(View.GONE);
                            }
                        }else {
                            btn_even_request.setEnabled(true);
                            if(myEventList.size() == 0){
                                ly_no_record_found.setVisibility(View.VISIBLE);
                            }else {
                                ly_no_record_found.setVisibility(View.GONE);
                            }
                        }



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    btn_even_request.setEnabled(true);
                    btn_my_even.setEnabled(true);
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                btn_even_request.setEnabled(true);
                btn_my_even.setEnabled(true);
            }
        });
        service.callGetSimpleVolley("event/getEventList?offset="+startCount+"&limit=10&listType="+eventType+"");
    }
}
