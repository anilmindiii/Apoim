package com.apoim.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apoim.R;
import com.apoim.adapter.chat.ChatHistoryAdapter;
import com.apoim.helper.Constant;
import com.apoim.modal.Chat;
import com.apoim.modal.UserInfoFCM;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anil on 2/4/18.
 **/

public class ChatFragment extends Fragment {
    private ChatHistoryAdapter adapter;
    private ArrayList<Chat> historyList;
    private ArrayList<UserInfoFCM> userList;
    private Map<String, Chat> mapList;
    private Context mContext;
    private String myUid = "";
    private InsLoadingView loading_view;
    private LinearLayout ly_no_data_found;
    private String myAuthToken = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment_layout, container, false);

        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);

        historyList = new ArrayList<>();
        userList = new ArrayList<>();
        mapList = new HashMap<>();

        loading_view = view.findViewById(R.id.loading_view);

        Session session = new Session(mContext);
        myAuthToken = session.getUser().userDetail.authToken;
        myUid = session.getUser().userDetail.userId;
        adapter = new ChatHistoryAdapter(historyList, mContext);
        recycler_view.setAdapter(adapter);
        ly_no_data_found = view.findViewById(R.id.ly_no_data_found);

        if(Utils.IsNetPresent(mContext)){
            getHistoryList();
        } else Toast.makeText(mContext, getString(R.string.alert_network_check), Toast.LENGTH_SHORT).show();


        //checkIsSessionExpair();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void getHistoryList() {
        loading_view.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.getValue() == null){
                   ly_no_data_found.setVisibility(View.VISIBLE);
                   loading_view.setVisibility(View.GONE);
               }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(Chat.class) != null){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    gettingDataFromUserTable( dataSnapshot.getKey(), chat);

                    ly_no_data_found.setVisibility(View.GONE);
                    loading_view.setVisibility(View.GONE);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(Chat.class) != null){

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    gettingDataFromUserTable(dataSnapshot.getKey(), chat);

                    ly_no_data_found.setVisibility(View.GONE);
                    loading_view.setVisibility(View.GONE);
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for(int i = 0 ; i<historyList.size();i++){
                    if(historyList.get(i).uid.equals(dataSnapshot.getKey())){
                        historyList.remove(i);
                    }
                }


               /* for(Chat chat:historyList){
                    if(chat.uid.equals(dataSnapshot.getKey())){
                        historyList.remove(chat);
                    }
                }*/

                if(historyList.size() == 0){
                    ly_no_data_found.setVisibility(View.VISIBLE);
                }else{
                    ly_no_data_found.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gettingDataFromUserTable(final String key, final Chat chat) {
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(key).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(UserInfoFCM.class) != null){
                    UserInfoFCM infoFCM = dataSnapshot.getValue(UserInfoFCM.class);
                    userList.add(infoFCM);

                    for (UserInfoFCM userInfoFCM : userList) {
                        if (userInfoFCM.uid.equals(key)) {

                            chat.profilePic = userInfoFCM.profilePic;
                            chat.name = userInfoFCM.name;
                            chat.firebaseToken = userInfoFCM.firebaseToken;
                            chat.uid = key;
                        }

                    }

                    mapList.put(chat.uid, chat);
                    historyList.clear();
                    Collection<Chat> values = mapList.values();
                    historyList.addAll(values);
                    shortList();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void shortList() {
        Collections.sort(historyList,new Comparator<Chat>(){

            @Override
            public int compare(Chat a1, Chat a2) {
                if (a1.timestamp == null || a2.timestamp == null)
                    return -1;
                else {
                    Long long1 = Long.valueOf(String.valueOf(a1.timestamp));
                    Long long2 = Long.valueOf(String.valueOf(a2.timestamp));
                    return long1.compareTo(long2);
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void checkIsSessionExpair(){
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(myUid).child("authToken").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null) {
                    String authToken = dataSnapshot.getValue(String.class);
                    if (!authToken.equals(myAuthToken)) {
                        showSessionError("session expired", "Your current session has expired, please login again", "Ok");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showSessionError(String title, String msg, String button) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Session session = new Session(mContext);
                session.logout();
            }
        });

        if(!((Activity) mContext).isFinishing())
        {
            //show alert
            android.support.v7.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }
}
