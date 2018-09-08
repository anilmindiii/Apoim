package com.apoim.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.MainActivity;
import com.apoim.activity.OtherProfileActivity;
import com.apoim.activity.ProfileActivity;
import com.apoim.activity.SignInActivity;
import com.apoim.adapter.userListAdapter.UserListAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.CustomClick;
import com.apoim.listener.GetUsetItemClick;
import com.apoim.modal.FilterInfo;
import com.apoim.modal.OnlineInfo;
import com.apoim.modal.UserInfoFCM;
import com.apoim.modal.UserListInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mindiii on 7/3/18.
 **/

public class UserListFragment extends Fragment {
    private Context mContext;
    private UserListAdapter userListAdapter1;
    private UserListAdapter userListAdapter2;
    private UserListAdapter userListAdapter3;
    private ArrayList<UserListInfo.NearByUsersBean> userList1;
    private ArrayList<UserListInfo.NearByUsersBean> userList2;
    private ArrayList<UserListInfo.NearByUsersBean> userList3;
    private LinearLayout tv_no_record;

    private boolean bool1;
    private boolean bool2;
    private boolean bool3;
    private InsLoadingView loadingView;
    FilterInfo filterInfo = new FilterInfo();
    Double current_lat=0.0, current_lng=0.0;
    Session session;
    Map<String,Object> onlineMapList;
    Map<String,Object> onlyOnlineList;


    public static UserListFragment newInstance(FilterInfo filterInfo, Double current_lat, Double current_lng) {
        Bundle args = new Bundle();
        UserListFragment fragment = new UserListFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_fragment, container, false);
        onlineMapList = new HashMap<>();
        onlyOnlineList = new HashMap<>();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Apoim.getInstance().cancelTask();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler_1 = view.findViewById(R.id.recycler_1);
        RecyclerView recycler_2 = view.findViewById(R.id.recycler_2);
        RecyclerView recycler_3 = view.findViewById(R.id.recycler_3);
        loadingView = view.findViewById(R.id.loading_view);
        tv_no_record = view.findViewById(R.id.tv_no_record);

        userList1 = new ArrayList<>();
        userList2 = new ArrayList<>();
        userList3 = new ArrayList<>();
        session = new Session(mContext,getActivity());
        filterInfo = session.getFilterInfo();


        if(filterInfo == null){
            filterInfo = new FilterInfo();
        }else {
            filterInfo = session.getFilterInfo();
        }

        userListAdapter1 = new UserListAdapter(mContext, userList1, new GetUsetItemClick() {
            @Override
            public void userItemClick(String userId) {
                Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        userListAdapter2 = new UserListAdapter(mContext, userList2, new GetUsetItemClick() {
            @Override
            public void userItemClick(String userId) {
                Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        userListAdapter3 = new UserListAdapter(mContext, userList3, new GetUsetItemClick() {
            @Override
            public void userItemClick(String userId) {
                Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        recycler_1.setAdapter(userListAdapter1);
        recycler_2.setAdapter(userListAdapter2);
        recycler_3.setAdapter(userListAdapter3);

        loadingView.setVisibility(View.VISIBLE);
        addUserFirebaseDatabase();
        getUsersList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Apoim.getInstance().getRequestQueue().cancelAll(Apoim.TAG);
    }

    private void getUsersList(){
        final String userId;
        if(session.getUser() != null){
            userId = session.getUser().userDetail.userId;
        }else {
            userId = "";
        }

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
            if(filterInfo.filterBy == null ){
                filterInfo.filterBy = "";
            }


            param.put("latitude",filterInfo.latitude);
            param.put("longitude",filterInfo.longitude);
            param.put("showMe",filterInfo.showMe);
            param.put("ageStart",filterInfo.ageStart);
            param.put("ageEnd",filterInfo.ageEnd);
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
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    //String message = object.getString("message");

                    if(status.equals("success")){
                        tv_no_record.setVisibility(View.GONE);
                        UserListInfo.NearByUsersBean userListInfo = null;
                        JSONArray array = object.getJSONArray("nearByUsers");
                        bool1 = true;
                        bool2 = false;
                        bool3 = false;
                        for(int i =0;i<array.length();i++){
                            userListInfo = new UserListInfo.NearByUsersBean();
                            JSONObject jsonObject  = array.getJSONObject(i);
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

                            if(!userListInfo.userId.equals(userId)){
                                if(bool1){
                                    bool1 = false;
                                    bool2 = true;
                                    bool3 = false;

                                    if(filterInfo.filterBy != null){
                                        if(filterInfo.filterBy.equals("2")){
                                            if(userListInfo.status.equals("online"))
                                                userList1.add(userListInfo);
                                        }else userList1.add(userListInfo);
                                    }else {
                                        userList1.add(userListInfo);
                                    }

                                }
                                else if(bool2){
                                    bool1 = false;
                                    bool2 = false;
                                    bool3 = true;

                                    if(filterInfo.filterBy != null){
                                        if(filterInfo.filterBy.equals("2")){
                                            if(userListInfo.status.equals("online"))
                                                userList2.add(userListInfo);
                                        }else userList2.add(userListInfo);
                                    }else {
                                        userList2.add(userListInfo);
                                    }

                                }
                                else if(bool3){
                                    bool1 = true;
                                    bool2 = false;
                                    bool3 = false;

                                    if(filterInfo.filterBy != null){
                                        if(filterInfo.filterBy.equals("2")){
                                            if(userListInfo.status.equals("online"))
                                                userList3.add(userListInfo);
                                        }else userList3.add(userListInfo);
                                    }else {
                                        userList3.add(userListInfo);
                                    }
                                }
                            }


                        }

                        if(userList1.size() == 0 && userList2.size() == 0 && userList3.size() == 0 ){
                            tv_no_record.setVisibility(View.VISIBLE);
                        }

                        userListAdapter1.notifyDataSetChanged();
                        userListAdapter2.notifyDataSetChanged();
                        userListAdapter3.notifyDataSetChanged();

                    }else {
                        tv_no_record.setVisibility(View.VISIBLE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
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

    private void addUserFirebaseDatabase( ) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constant.online).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.getKey().equals("3")){
                    OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                    onlineMapList.put(dataSnapshot.getKey(),onlineStatus);
                    runTimeStatus(dataSnapshot, onlineStatus);
                }

            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(!dataSnapshot.getKey().equals("3")){
                    OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                    onlineMapList.put(dataSnapshot.getKey(),onlineStatus);
                    runTimeStatus(dataSnapshot, onlineStatus);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getKey().equals("3")){
                    OnlineInfo onlineStatus = dataSnapshot.getValue(OnlineInfo.class);
                    onlineMapList.put(dataSnapshot.getKey(),onlineStatus);
                    runTimeStatus(dataSnapshot, onlineStatus);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}


            private void runTimeStatus(@NonNull DataSnapshot dataSnapshot, OnlineInfo onlineStatus) {
                userListAdapter1.onlineFilter(false);
                userListAdapter2.onlineFilter(false);
                userListAdapter3.onlineFilter(false);

                for(int k=0;k<userList1.size();k++){
                    if(dataSnapshot.getKey().equals(userList1.get(k).userId)){
                        userList1.get(k).status = onlineStatus.lastOnline;
                    }
                }


                for(int k=0;k<userList2.size();k++){
                    if(dataSnapshot.getKey().equals(userList2.get(k).userId)){
                        userList2.get(k).status = onlineStatus.lastOnline;
                    }
                }


                for(int k=0;k<userList3.size();k++){
                    if(dataSnapshot.getKey().equals(userList3.get(k).userId)){
                        userList3.get(k).status = onlineStatus.lastOnline;
                    }
                }

                userListAdapter1.notifyDataSetChanged();
                userListAdapter2.notifyDataSetChanged();
                userListAdapter3.notifyDataSetChanged();
            }
        });


    }
}
