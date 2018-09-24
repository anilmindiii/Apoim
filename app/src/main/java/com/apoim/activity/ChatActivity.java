package com.apoim.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.adapter.chat.ChattingAdapter;
import com.apoim.app.Apoim;
import com.apoim.fcm.FcmNotificationBuilder;
import com.apoim.groupchatwebrtc.activities.CallActivity;
import com.apoim.groupchatwebrtc.activities.OpponentsActivity;
import com.apoim.groupchatwebrtc.activities.PermissionsActivity;
import com.apoim.groupchatwebrtc.db.QbUsersDbManager;
import com.apoim.groupchatwebrtc.services.CallService;
import com.apoim.groupchatwebrtc.util.QBResRequestExecutor;
import com.apoim.groupchatwebrtc.utils.CollectionsUtils;
import com.apoim.groupchatwebrtc.utils.Consts;
import com.apoim.groupchatwebrtc.utils.PermissionsChecker;
import com.apoim.groupchatwebrtc.utils.PushNotificationSender;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.groupchatwebrtc.utils.UsersUtils;
import com.apoim.groupchatwebrtc.utils.WebRtcSessionManager;
import com.apoim.helper.Constant;
import com.apoim.listener.GetDateStatus;
import com.apoim.modal.Chat;
import com.apoim.modal.UserInfoFCM;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.apoim.helper.Constant.IsGetNotificationValue;
import static com.apoim.util.Utils.formateDateFromstring;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView header_image;
    private TextView title_name,tv_days_status;
    ImageView send_msg_button;
    private RelativeLayout ly_popup_menu;
    private FirebaseDatabase firebaseDatabase;
    private ChattingAdapter chattingAdapter;
    private ArrayList<Chat> chatList;
    private RecyclerView recycler_view;
    private int startCount = 10;
    private EditText ed_message;

    private Uri image_FirebaseURL;
    private String otherUID = "", myUid = "";
    private UserInfoFCM otherUserInfo;

    private Map<String, Chat> map;

    private String chatNode = "";
    private String otherprofilePic = "";
    private String myName = "";
    private String myProfileImage = "";
    private String blockedId = "";
    private InsLoadingView loading_view;
    private TextView tv_block_msg;

    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private int mPostsPerPage = 20;
    private boolean isNotification;
    private RelativeLayout btn_audio_call,btn_video_call;

    private QbUsersDbManager dbManager;
    protected QBResRequestExecutor requestExecutor;
    private SharedPrefsHelper sharedPrefsHelper;
    private PermissionsChecker checker;
    private String quickBloxId = "";

    private int unreadCount = 0;
    private String readBy = "";
    private String lastDataSnapshotKey = "";
    private boolean isunreadDone;
    private String myAuthToken = "";
    private QBUser userForSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        Session session = new Session(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();

        myAuthToken = session.getUser().userDetail.authToken;
        myUid = session.getUser().userDetail.userId;
        myName = session.getUser().userDetail.fullName;
        if(session.getUser().userDetail.profileImage.size() > 0){
            myProfileImage = session.getUser().userDetail.profileImage.get(0).image;
        }

        loading_view = findViewById(R.id.loading_view);
        // video audio call init
        requestExecutor = Apoim.getInstance().getQbResRequestExecutor();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        checker = new PermissionsChecker(getApplicationContext());

        if (getIntent().getStringExtra("otherUID") != null) {
            otherprofilePic = getIntent().getStringExtra("profilePic");
            String titleName = getIntent().getStringExtra("titleName");
            title_name.setText(titleName);
            otherUID = getIntent().getStringExtra("otherUID");
            Glide.with(this).load(otherprofilePic).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder)).into(header_image);
            gettingDataFromUserTable(otherUID);
            IsGetNotificationValue = otherUID;
        }

        if(getIntent().getStringExtra("quickBloxId") != null){
            quickBloxId = getIntent().getStringExtra("quickBloxId");
        }

        chatNode = gettingNotes();
        chatList = new ArrayList<>();
        chattingAdapter = new ChattingAdapter(this, chatList, myUid, new GetDateStatus() {
            @Override
            public void currentDateStatus(Object timestamp) {

                SimpleDateFormat sd = new  SimpleDateFormat("dd MMMM yyyy");
                try {
                    String date = sd.format(new Date((Long) timestamp));
                    isTodaysDate(date);
                }catch (Exception e){

                }
            }
        });



        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                loadNextDataFromApi();
            }
        };

        //checkIsSessionExpair();
        getChat();

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = linearLayoutManager.getItemCount();
                mLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition + mPostsPerPage)) {
                    //getChat(chattingAdapter.getLastItemId());
                    mIsLoading = true;
                }

            }
        });

        recycler_view.addOnScrollListener(scrollListener);
        recycler_view.setAdapter(chattingAdapter);
        getBlockUserData();
        isNotification();

        readBy = myUid;

        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(otherUID)){
                    firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).child("readBy").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startLoadUsers();

        // Register audio/vidio call
       // startSignUpNewUser(createUserWithEnteredData(session.getUser().userDetail.fullName, session.getUser().userDetail.email));


    }

    private boolean isTodaysDate(String checkDate){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = df.format(c);
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);
        java.sql.Date yesterday = new java.sql.Date(cal.getTimeInMillis());
        String beforeOneDay = formateDateFromstring("yyyy-MM-dd", "dd MMMM yyyy",yesterday.toString());


        Log.d("dateA",checkDate);
        if(currentDate.equals(checkDate)){
            tv_days_status.setText("Today");
        }else if(beforeOneDay.equals(checkDate)){
            tv_days_status.setText("Yesterday");
        }else tv_days_status.setText(checkDate);


       // tv_days_status.setVisibility(View.VISIBLE);
        tv_days_status.postDelayed(new Runnable() {
            public void run() {
                //tv_days_status.setVisibility(View.GONE);
            }
        }, 2000);

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsGetNotificationValue = "";
        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(otherUID)){
                    firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).child("readBy").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void isNotification(){
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(otherUID).child(Constant.isNotification).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null){
                    String isNotificationValue = dataSnapshot.getValue(String.class);
                    if(isNotificationValue.equals("1")){
                        isNotification = true;
                    }else {
                        isNotification = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String pushkey = firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(chatNode).push().getKey();
        String msg = ed_message.getText().toString().trim();
        String firebase_id = "";
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        if (msg.equals("") && image_FirebaseURL != null) {

        } else if (msg.equals("")) {
            return;
        }

        isunreadDone = true;

        Chat otherChat = new Chat();
        otherChat.deleteby = "";
        otherChat.firebaseId = firebase_id;
        otherChat.firebaseToken = firebaseToken;

        if (image_FirebaseURL != null) {
            otherChat.imageUrl = image_FirebaseURL.toString();
            otherChat.message = "";
            otherChat.image = 1;
        } else {
            otherChat.imageUrl = "";
            otherChat.message = msg;
            otherChat.image = 0;
        }

        otherChat.name = title_name.getText().toString();
        otherChat.profilePic = otherprofilePic;
        otherChat.timestamp = ServerValue.TIMESTAMP;
        otherChat.uid = otherUID;
        otherChat.lastMsg = myUid;
        otherChat.readBy = otherUID;


        Chat myChat = new Chat();
        myChat.deleteby = "";
        myChat.firebaseId = firebase_id;
        myChat.firebaseToken = firebaseToken;

        if (image_FirebaseURL != null) {
            myChat.imageUrl = image_FirebaseURL.toString();
            myChat.message = "";
            myChat.image = 1;
        } else {
            myChat.imageUrl = "";
            myChat.message = msg;
            myChat.image = 0;
        }

        myChat.name = myName;
        myChat.profilePic = myProfileImage;
        myChat.timestamp = ServerValue.TIMESTAMP;
        myChat.uid = myUid;
        myChat.lastMsg = myUid;
        myChat.readBy = otherUID;

        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).child(pushkey).setValue(myChat);
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).child(pushkey).setValue(myChat);


        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).setValue(otherChat);
        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).setValue(myChat);


        if(isNotification){
            if (image_FirebaseURL != null) {
                if (firebaseToken != null && otherUserInfo != null) {
                    sendPushNotificationToReceiver(myName, "Image", myName, myUid, firebaseToken);
                }
            } else {
                if (firebaseToken != null && otherUserInfo != null)
                    sendPushNotificationToReceiver(myName, msg, myName, myUid, firebaseToken);
            }
        }

        ed_message.setText("");
        image_FirebaseURL = null;
        loading_view.setVisibility(View.GONE);
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

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Session session = new Session(ChatActivity.this);
                session.logout();
            }
        });

        if(!isFinishing())
        {
            //show alert
            android.support.v7.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    private void getChat() {
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                getChatDataInmap(dataSnapshot.getKey(), chat);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                getChatDataInmap(dataSnapshot.getKey(), chat);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatDataInmap(String key, Chat chat) {
        if (chat != null) {
            if (chat.deleteby.equals(myUid)) {
                return;
            } else {
                map.put(key, chat);
                chatList.clear();
                Collection<Chat> values = map.values();
                chatList.addAll(values);
                recycler_view.scrollToPosition(map.size() - 1);
                chattingAdapter.notifyDataSetChanged();
            }
        }
        shortList();
    }

    private void shortList() {
        Collections.sort(chatList, new Comparator<Chat>() {

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
        chattingAdapter.notifyDataSetChanged();
    }

    private void sendPushNotificationToReceiver(String title, String message, String username, String uid, String firebaseToken) {

        FcmNotificationBuilder.initialize()
                .title(title)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(otherUserInfo.firebaseToken).send();
    }

    private void getBlockUserData() {
        firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    blockedId = dataSnapshot.getValue(String.class);

                    if (blockedId.equals("Both")) {
                        tv_block_msg.setText("Unblock user");
                    } else if (blockedId.equals("")) {
                        tv_block_msg.setText("Block user");
                    } else if (blockedId.equals(otherUID)) {
                        tv_block_msg.setText("Block user");
                    } else if (blockedId.equals(myUid)) {
                        tv_block_msg.setText("Unblock user");
                    }

                } else {
                    blockedId = "";
                    tv_block_msg.setText("Block user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String gettingNotes() {
        //create note for chatroom
        int myUid_ = Integer.parseInt(myUid);
        int otherUID_ = Integer.parseInt(otherUID);

        if (myUid_ < otherUID_) {
            chatNode = myUid + "_" + otherUID;
        } else {
            chatNode = otherUID + "_" + myUid;
        }

        return chatNode;
    }

    private void loadNextDataFromApi() {
        //Api Call here
        startCount = startCount + 10;

    }

    private void init() {
        ImageView iv_popup_menu = findViewById(R.id.iv_popup_menu);
        ly_popup_menu = findViewById(R.id.ly_popup_menu);
        ImageView iv_back = findViewById(R.id.iv_back);
        header_image = findViewById(R.id.header_image);
        title_name = findViewById(R.id.title_name);
        recycler_view = findViewById(R.id.recycler_view);
        ed_message = findViewById(R.id.ed_message);
        send_msg_button = findViewById(R.id.send_msg_button);
        tv_days_status = findViewById(R.id.tv_days_status);
        ImageView camera_btn = findViewById(R.id.camera_btn);
        RelativeLayout btn_block_user = findViewById(R.id.btn_block_user);
        RelativeLayout ly_delete_chat = findViewById(R.id.ly_delete_chat);
        tv_block_msg = findViewById(R.id.tv_block_msg);
        btn_video_call = findViewById(R.id.btn_video_call);
        btn_audio_call = findViewById(R.id.btn_audio_call);
        RelativeLayout main_view = findViewById(R.id.main_view);

        btn_video_call.setOnClickListener(this);
        btn_audio_call.setOnClickListener(this);
        iv_popup_menu.setOnClickListener(this);
        send_msg_button.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_block_user.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        ly_delete_chat.setOnClickListener(this);
        main_view.setOnClickListener(this);


    }

    private void gettingDataFromUserTable(String otherUID) {
        firebaseDatabase.getReference().child(Constant.ARG_USERS).child(otherUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserInfoFCM.class) != null) {
                    otherUserInfo = dataSnapshot.getValue(UserInfoFCM.class);
                    title_name.setText(otherUserInfo.name+"");
                    if (!otherUserInfo.profilePic.equals("")) {
                        otherprofilePic = otherUserInfo.profilePic;
                        if (getApplicationContext() != null)
                            Glide.with(getApplicationContext()).load(otherprofilePic).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder)).into(header_image);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_popup_menu: {
                if (ly_popup_menu.getVisibility() == View.VISIBLE) {
                    ly_popup_menu.setVisibility(View.GONE);
                } else {
                    ly_popup_menu.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.iv_back: {
                Utils.hideSoftKeyboard(this);
                onBackPressed();
                break;
            }
            case R.id.send_msg_button: {
                ly_popup_menu.setVisibility(View.GONE);
                if (blockedId.equals(myUid)) {
                    ed_message.setText("");
                    Utils.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals(otherUID)) {
                    ed_message.setText("");
                    Utils.openAlertDialog(this, "You are blocked by " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals("Both")) {
                    ed_message.setText("");
                    Utils.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else {
                    sendMessage();
                }
                break;
            }
            case R.id.btn_block_user: {

                if (blockedId.equals("")) {
                    blockChatDialog("Are you want to block ");
                } else if (blockedId.equals(myUid)) {
                    blockChatDialog("Are you want to unblock ");
                } else if (blockedId.equals(otherUID)) {
                    blockChatDialog("Are you want to block ");
                } else if (blockedId.equals("Both")) {
                    blockChatDialog("Are you want to unblock ");
                }
                ly_popup_menu.setVisibility(View.GONE);
                break;
            }
            case R.id.camera_btn: {
                if (blockedId.equals(myUid)) {
                    Utils.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals(otherUID)) {
                    Utils.openAlertDialog(this, "You are blocked by " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals("Both")) {
                    Utils.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");

                } else {
                    getPermissionAndPicImage();
                }

                break;
            }
            case R.id.ly_delete_chat: {
                ly_popup_menu.setVisibility(View.GONE);
                deleteChatDialog("Are you sure you want to delete conversation with?");
                break;
            }
            case R.id.main_view: {
                ly_popup_menu.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_audio_call: {
                if (isLoggedInChat() && otherUserInfo != null) {
                    if(!TextUtils.isEmpty(otherUserInfo.quickBloxId)){
                        startCall(false,Integer.parseInt(otherUserInfo.quickBloxId));
                    }
                    else if(!TextUtils.isEmpty(quickBloxId)){
                        startCall(false,Integer.parseInt(quickBloxId));
                    }
                    else {
                        Utils.openAlertDialog(ChatActivity.this,"User is not allowed take audio call");
                    }

                }
                if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                    startPermissionsActivity(false);
                }
                ly_popup_menu.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_video_call: {
                if (isLoggedInChat() && otherUserInfo != null) {
                    if(!TextUtils.isEmpty(otherUserInfo.quickBloxId)){
                        startCall(true,Integer.parseInt(otherUserInfo.quickBloxId));
                    }
                    else if(!TextUtils.isEmpty(quickBloxId)){
                        startCall(true,Integer.parseInt(quickBloxId));
                    }
                    else {
                        Utils.openAlertDialog(ChatActivity.this,"User is not allowed take audio call");
                    }
                }
                if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                    startPermissionsActivity(false);
                }
                ly_popup_menu.setVisibility(View.GONE);
                break;
            }
        }

    }

    private void blockChatDialog(String msg) {
        final Dialog _dialog = new Dialog(ChatActivity.this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_dialog_txt.setText(msg);
        tv_name.setText(otherUserInfo.name + "?");
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockedId.equals("Both")) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(otherUID);
                } else if (blockedId.equals("")) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(myUid);
                } else if (blockedId.equals(otherUID)) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue("Both");
                } else if (blockedId.equals(myUid)) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(null);
                }
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


    private void deleteChatDialog(String msg) {
        final Dialog _dialog = new Dialog(ChatActivity.this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_dialog_txt.setText(msg);
        tv_name.setText(otherUserInfo.name);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).setValue(null);
                firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).setValue(null);

                map.clear();
                chatList.clear();
                chattingAdapter.notifyDataSetChanged();
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


    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(ChatActivity.this);
            }
        } else {
            ImagePicker.pickImage(ChatActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(ChatActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(ChatActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(ChatActivity.this);
                } else {
                    Toast.makeText(ChatActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;

        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(ChatActivity.this, requestCode, resultCode, data);
                creatFirebaseProfilePicUrl(imageUri);
                loading_view.setVisibility(View.VISIBLE);
            }
        }
    }*/

    private void creatFirebaseProfilePicUrl(Uri selectedImageUri) {
        StorageReference storageRef;
        FirebaseStorage storage;
        FirebaseApp app;

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);

        storageRef = storage.getReference("chat_photos_apoim");
        StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image_FirebaseURL = uri;
                        send_msg_button.callOnClick();
                    }
                });

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("sasa",e+"");
            }
        });

    }

/*..............................video/Audio calling...............................................*/

    private void startLoadUsers() {
        loading_view.setVisibility(View.VISIBLE);
        String currentRoomName = SharedPrefsHelper.getInstance().get(Consts.PREF_CURREN_ROOM_NAME);
        requestExecutor.loadUsersByTag("mychatroom", new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                loading_view.setVisibility(View.GONE);
                dbManager.saveAllUsers(result, true);
                //initUsersList();

            }

            @Override
            public void onError(QBResponseException responseException) {
                loading_view.setVisibility(View.GONE);
                //startLoadUsers();
            }
        });
    }

    private void startCall(boolean isVideoCall,int oppUserId) {

        //Log.d(TAG, "startCall()");
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(oppUserId);

        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, "currentUser.getFullName()");// sender name

        CallActivity.start(this, false);
        //Log.d(TAG, "conferenceType = " + conferenceType);
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toast.makeText(ChatActivity.this,R.string.dlg_signal_error,Toast.LENGTH_SHORT).show();
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }


    /*................................................video call start ...........................................................*/

    private void startSignUpNewUser(final QBUser newUser) {
        //loading_view.setVisibility(View.VISIBLE);
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, true);
                        } else {
                            addUserFirebaseDatabase();
                            loading_view.setVisibility(View.GONE);
                            //Toast.makeText(SelectMaleFemaleActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);

        userForSave = qbUser;
        startLoginService(qbUser);
    }

    private void startOpponentsActivity() {
        sendQuickBlockIdToServer(userForSave.getId().toString());
        addUserFirebaseDatabase();

    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }


    private QBUser createUserWithEnteredData(String userName,String email) {
        return createQBUserWithCurrentData(userName,
                "mychatroom",email);
    }

    private QBUser createQBUserWithCurrentData(String userName, String chatRoomName, String email) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(chatRoomName)) {
            StringifyArrayList<String> userTags = new StringifyArrayList<>();
            userTags.add(chatRoomName);

            qbUser = new QBUser();
            qbUser.setFullName(userName);
            qbUser.setLogin(email);
            qbUser.setEmail(email);
            qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
            qbUser.setTags(userTags);
        }

        return qbUser;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            loading_view.setVisibility(View.GONE);
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);
                signInCreatedUser(userForSave, false);
            } else {
                Toast.makeText(ChatActivity.this, getString(R.string.login_chat_login_error) + errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(ChatActivity.this, requestCode, resultCode, data);
                creatFirebaseProfilePicUrl(imageUri);
                loading_view.setVisibility(View.VISIBLE);
            }
        }
    }

    private void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new com.apoim.groupchatwebrtc.utils.QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {

                if (deleteCurrentUser) {
                    removeAllUserData(result);
                } else {
                    startOpponentsActivity();
                }
            }

            @Override
            public void onError(QBResponseException responseException) {
                loading_view.setVisibility(View.GONE);
                //  Toast.makeText(ChatActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeAllUserData(final QBUser user) {
        Session app_session = new Session(ChatActivity.this);
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData(app_session.getUser().userDetail.fullName,app_session.getUser().userDetail.email));
            }

            @Override
            public void onError(QBResponseException e) {
                loading_view.setVisibility(View.GONE);
                // Toast.makeText(ChatActivity.this, R.string.sign_up_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
/*................................end video call ............................................................*/

    private void addUserFirebaseDatabase() {
        final Session app_session = new Session(ChatActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM infoFCM = new UserInfoFCM();
        infoFCM.uid = app_session.getUser().userDetail.userId;
        infoFCM.email = app_session.getUser().userDetail.email;
        infoFCM.firebaseId = "";
        infoFCM.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFCM.name = app_session.getUser().userDetail.fullName;
        infoFCM.isNotification = Constant.Notication_on;
        infoFCM.authToken = app_session.getUser().userDetail.authToken;
        if(userForSave != null){
            infoFCM.quickBloxId = userForSave.getId().toString();
        }else  infoFCM.quickBloxId = "";


        if (app_session.getUser().userDetail.profileImage.size() != 0 && app_session.getUser().userDetail.profileImage != null) {
            infoFCM.profilePic = app_session.getUser().userDetail.profileImage.get(0).image;
        } else infoFCM.profilePic = "";

        database.child(Constant.ARG_USERS)
                .child(app_session.getUser().userDetail.userId)
                .setValue(infoFCM);

    }

    private void sendQuickBlockIdToServer(String quickBloxId) {
       // loading_view.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("quickBloxId", quickBloxId);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                    } else {
                        Utils.openAlertDialog(ChatActivity.this, message);
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
        service.callSimpleVolley("user/saveCallingUserId", map);

    }

}
