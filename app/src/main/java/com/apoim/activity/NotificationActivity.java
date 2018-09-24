package com.apoim.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.adapter.apoinment.NotificationFilterAdapter;
import com.apoim.adapter.apoinment.NotificationListAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.FilterItemInfo;
import com.apoim.modal.NotificationInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_filter;
    private LinearLayout ly_no_notification_found;
    private RecyclerView recycler;
    private NotificationListAdapter notificationListAdapter;
    private NotificationFilterAdapter filterAdapter;
    private InsLoadingView loading_view;
    private ArrayList<NotificationInfo.NotificationListBean> notificationList;
    private Session session;
    //private TextView tv_done,tv_reset;
    private ArrayList<FilterItemInfo> filterItemInfoList;
    private FilterItemInfo filterItemInfo;
    private ArrayList<NotificationInfo.NotificationListBean> tempList;
    private int unCheckCount = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int endCount = 400;
    private int startCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        iv_filter = findViewById(R.id.iv_filter);
        ly_no_notification_found = findViewById(R.id.ly_no_notification_found);

        recycler = findViewById(R.id.recycler);
        ImageView iv_back = findViewById(R.id.iv_back);
        loading_view = findViewById(R.id.loading_view);

        filterItemInfoList = new ArrayList<>();
        tempList = new ArrayList<>();


       /* filterItemInfo = new FilterItemInfo(R.drawable.ico_not_chat,getResources().getString(R.string.chat),false);
        filterItemInfoList.add(filterItemInfo);*/
        filterItemInfo = new FilterItemInfo(R.drawable.ico_not_likes,getResources().getString(R.string.likes),false);
        filterItemInfoList.add(filterItemInfo);
       /* filterItemInfo = new FilterItemInfo(R.drawable.ico_not_visit,getResources().getString(R.string.visits),false);
        filterItemInfoList.add(filterItemInfo);*/
        filterItemInfo = new FilterItemInfo(R.drawable.ico_not_fav,getResources().getString(R.string.fevorites),false);
        filterItemInfoList.add(filterItemInfo);
        filterItemInfo = new FilterItemInfo(R.drawable.ico_not_friends,getResources().getString(R.string.friends),false);
        filterItemInfoList.add(filterItemInfo);
        filterItemInfo = new FilterItemInfo(R.drawable.ico_not_event,getResources().getString(R.string.event),false);
        filterItemInfoList.add(filterItemInfo);
        filterItemInfo = new FilterItemInfo(R.drawable.ico_not_app,getResources().getString(R.string.appointment),false);
        filterItemInfoList.add(filterItemInfo);

        notificationList = new ArrayList<>();
        session = new Session(this,this);

        notificationListAdapter = new NotificationListAdapter(this,this,notificationList);
        recycler.setAdapter(notificationListAdapter);

        if(session.getFilterList() != null && session.getFilterList().size() != 0){
            filterItemInfoList = session.getFilterList();
            iv_filter.setImageResource(R.drawable.ico_filter_active);
        }else {
            iv_filter.setImageResource(R.drawable.ico_filter);
        }


        filterAdapter = new NotificationFilterAdapter(this,filterItemInfoList);

        iv_filter.setOnClickListener(this);
        iv_back.setOnClickListener(this);


        //RecyclerView rvItems = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //loadNextDataFromApi();
            }
        };

        recycler.addOnScrollListener(scrollListener);

    }

    private void loadNextDataFromApi() {

        startCount = endCount;
        endCount = endCount+200;
        getNotificationList();
    }

    private void filterDialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.notification_filter_layout))
                .setGravity(Gravity.TOP)
                .setCancelable(false)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View view = dialog.getHolderView();
        ImageView iv_filter_icon = view.findViewById(R.id.iv_filter_icon);
        TextView tv_done = view.findViewById(R.id.tv_done);
        TextView tv_reset = view.findViewById(R.id.tv_reset);
        RecyclerView recycler_filter = view.findViewById(R.id.recycler_filter);
        recycler_filter.setAdapter(filterAdapter);

        if(session.getFilterList() != null && session.getFilterList().size() != 0){
            iv_filter_icon.setImageResource(R.drawable.ico_filter_active);
        }else {
            iv_filter_icon.setImageResource(R.drawable.ico_filter);
        }

        if(filterItemInfoList.size() == unCheckCount){
            iv_filter_icon.setImageResource(R.drawable.ico_filter);
        }

        filterAdapter.notifyDataSetChanged();

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
                dialog.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void filter(){
        tempList.clear();
        unCheckCount = 0;
        // checking filter apply here
        for (int i = 0; i<filterItemInfoList.size();i++){

            if(filterItemInfoList.get(i).isCheck){

                String filterItetermInfo =  filterItemInfoList.get(i).filterItemName;

             /*   if(filterItetermInfo.equals("Chat")){
                    for(int j =0;j<notificationList.size();j++){
                        if(notificationList.get(j).notificationType.equals("add_chat")){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(j);
                            tempList.add(listBean);
                        }
                    }
                }*/

                if(filterItetermInfo.equals("Likes")){
                    for(int m =0;m<notificationList.size();m++){
                        if(notificationList.get(m).notificationType.equals("add_like")){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(m);
                            tempList.add(listBean);
                        }
                    }
                }

                if(filterItetermInfo.equals("Favorites")){
                    for(int m =0;m<notificationList.size();m++){
                        if(notificationList.get(m).notificationType.equals("add_favorite")){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(m);
                            tempList.add(listBean);
                        }
                    }
                }

                if(filterItetermInfo.equals("Friends")){
                    for(int m =0;m<notificationList.size();m++){
                        if(notificationList.get(m).notificationType.equals("friend_request") || notificationList.get(m).notificationType.equals("accept_request")){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(m);
                            tempList.add(listBean);
                        }
                    }
                }

                if(filterItetermInfo.equals("Appointment")){
                    for(int m =0;m<notificationList.size();m++){
                        if(notificationList.get(m).notificationType.equals("create_appointment") || notificationList.get(m).notificationType.equals("delete_appointment")
                                || notificationList.get(m).notificationType.equals("confirmed_appointment") || notificationList.get(m).notificationType.equals("finish_appointment")
                                ||notificationList.get(m).notificationType.equals("update_counter") || notificationList.get(m).notificationType.equals("appointment_payment")
                                ||notificationList.get(m).notificationType.equals("apply_counter") || notificationList.get(m).notificationType.equals("review_appointment")
                                ||notificationList.get(m).notificationType.equals("update_appointment") ){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(m);
                            tempList.add(listBean);
                        }
                    }
                }

                if(filterItetermInfo.equals("Event")){
                    for(int m =0;m<notificationList.size();m++){
                        if(notificationList.get(m).notificationType.equals("create_event")
                                ||notificationList.get(m).notificationType.equals("companion_payment")
                                ||notificationList.get(m).notificationType.equals("join_event")
                                ||notificationList.get(m).notificationType.equals("share_event")
                                ||notificationList.get(m).notificationType.equals("companion_accept")
                                ||notificationList.get(m).notificationType.equals("companion_reject")
                                ||notificationList.get(m).notificationType.equals("event_payment")){
                            NotificationInfo.NotificationListBean listBean = notificationList.get(m);
                            tempList.add(listBean);
                        }
                    }
                }

                if(tempList.size() == 0){
                    ly_no_notification_found.setVisibility(View.VISIBLE);
                }else {
                    ly_no_notification_found.setVisibility(View.GONE);
                }
                notificationListAdapter = new NotificationListAdapter(this,this,tempList);
                recycler.setAdapter(notificationListAdapter);
               // recycler.scrollToPosition(endCount);
                notificationListAdapter.notifyDataSetChanged();

            }else {
                unCheckCount++;

            }
        }



        session.saveFilterList(filterItemInfoList);

        if(session.getFilterList() != null && session.getFilterList().size() != 0){
            iv_filter.setImageResource(R.drawable.ico_filter_active);
        }else {
            iv_filter.setImageResource(R.drawable.ico_filter);
        }

        // for getting orignal list
        if(filterItemInfoList.size() == unCheckCount){
            notificationListAdapter = new NotificationListAdapter(this,this,notificationList);
            recycler.setAdapter(notificationListAdapter);
            recycler.scrollToPosition(endCount);
            notificationListAdapter.notifyDataSetChanged();
           // filterAdapter.notifyDataSetChanged();
            iv_filter.setImageResource(R.drawable.ico_filter);

            if(notificationList.size() == 0){
                ly_no_notification_found.setVisibility(View.VISIBLE);
            }else {
                ly_no_notification_found.setVisibility(View.GONE);
            }
        }


    }

    private void resetFilter(){
        for (int i = 0; i<filterItemInfoList.size();i++){
            filterItemInfoList.get(i).isCheck = false;
        }

        notificationListAdapter = new NotificationListAdapter(this,this,notificationList);
        recycler.setAdapter(notificationListAdapter);
        notificationListAdapter.notifyDataSetChanged();
        filterAdapter.notifyDataSetChanged();
        session.saveFilterList(new ArrayList<FilterItemInfo>());
        iv_filter.setImageResource(R.drawable.ico_filter);

        if(notificationList.size() == 0){
            ly_no_notification_found.setVisibility(View.VISIBLE);
        }else {
            ly_no_notification_found.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_filter:{
                filterDialog();
                break;
            }
            case R.id.iv_back:{
                onBackPressed();
                break;
            }
            case R.id.tv_reset:{
                resetFilter();
                break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //loadNextDataFromApi();
        getNotificationList();
    }


    private void getNotificationList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    notificationList.clear();

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        NotificationInfo notificationInfo = gson.fromJson(response, NotificationInfo.class);
                        notificationList.addAll(notificationInfo.notificationList);

                        notificationListAdapter.notifyDataSetChanged();
                        filter();

                    }
                    else {
                        if(notificationList.size() == 0){
                            ly_no_notification_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_notification_found.setVisibility(View.GONE);
                        }
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
        service.callGetSimpleVolley("user/getNotificationList?offset="+startCount+"&limit="+endCount+"");

    }


}
