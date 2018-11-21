package com.apoim.activity.chat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.event.GroupMemberInfoActivity;
import com.apoim.adapter.chat.ChattingAdapter;
import com.apoim.adapter.newEvent.JoinedMemberChatAdapter;
import com.apoim.app.Apoim;
import com.apoim.fcm.FcmNotificationBuilder;
import com.apoim.helper.Constant;
import com.apoim.listener.GetDateStatus;
import com.apoim.modal.Chat;
import com.apoim.modal.GroupChatDeleteMuteInfo;
import com.apoim.modal.JoinedEventInfo;
import com.apoim.modal.OnlineInfo;
import com.apoim.modal.PayLoadEvent;
import com.apoim.modal.UserInfoFCM;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GroupChatHistortActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back, iv_popup_menu, send_msg_button, camera_btn, header_image;
    private RecyclerView recycler_view;
    private InsLoadingView loading_view;
    private ChattingAdapter chattingAdapter;
    String  eventId = "", myEmail = "", myUid = "", myName = "", myProfileImage = "", eventType = "", eventName = "";
    String eventOrganizerId = "", eventOrganizerName = "", eventOrganizerProfileImage = "";
    private ArrayList<Chat> chatList;
    private Session session;
    private FirebaseDatabase firebaseDatabase;
    private Map<String, Chat> map;
    private ArrayList<String> keyList;
    private String holdKeyForImage = "";
    private Uri image_FirebaseURL;
    private EditText ed_message;
    private TextView tv_days_status, title_name, tv_mute;
    private boolean isNotification = true;
    private RelativeLayout ly_popup_menu, ly_delete_chat, ly_btn_mute, ly_btn_info;
    private Long deleteTimestamp;
    private String mute = "", eventImage = "", eventMemId = "", compId = "", from = "";
    private LinearLayout ly_info_btn;
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    private Map<String, String> tokenList;
    private ArrayList<String> tokenArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_histort);
        init();
        session = new Session(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        joinedList = new ArrayList<>();
        map = new HashMap<>();
        keyList = new ArrayList<>();
        tokenArrayList = new ArrayList<>();
        tokenList = new HashMap<>();

        if (getIntent().getExtras() != null) {
            eventId = getIntent().getStringExtra("eventId");
            eventName = getIntent().getStringExtra("eventName");
            eventImage = getIntent().getStringExtra("eventImage");
            eventOrganizerId = getIntent().getStringExtra("eventOrganizerId");
            eventOrganizerName = getIntent().getStringExtra("eventOrganizerName");
            eventOrganizerProfileImage = getIntent().getStringExtra("eventOrganizerProfileImage");
            eventMemId = getIntent().getStringExtra("eventMemId");
            compId = getIntent().getStringExtra("compId");
            from = getIntent().getStringExtra("from");

            eventType = getIntent().getStringExtra("eventType");
            title_name.setText(eventName);

            Glide.with(this).load(eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(header_image);
        }

        myUid = session.getUser().userDetail.userId;
        myName = session.getUser().userDetail.fullName;
        myEmail = session.getUser().userDetail.email;
        if (session.getUser().userDetail.profileImage.size() > 0) {
            myProfileImage = session.getUser().userDetail.profileImage.get(0).image;
        }
        chatList = new ArrayList<>();
        chattingAdapter = new ChattingAdapter(this, chatList, myUid, new GetDateStatus() {
            @Override
            public void currentDateStatus(Object timestamp) {

            }
        }, false);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(chattingAdapter);

        recycler_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ly_popup_menu.setVisibility(View.GONE);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    tv_days_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                    if (chatList.size() > 0)
                        tv_days_status.setText(chatList.get(linearLayoutManager.findFirstVisibleItemPosition()).banner_date + "");
                    tv_days_status.setVisibility(View.VISIBLE);
                }
            }
        });

        getDeleteTimeStamp();
        getChat();
        joinedEventList();
    }

    private void init() {
        iv_back = findViewById(R.id.iv_back);
        iv_popup_menu = findViewById(R.id.iv_popup_menu);
        recycler_view = findViewById(R.id.recycler_view);
        loading_view = findViewById(R.id.loading_view);
        send_msg_button = findViewById(R.id.send_msg_button);
        ed_message = findViewById(R.id.ed_message);
        camera_btn = findViewById(R.id.camera_btn);
        tv_days_status = findViewById(R.id.tv_days_status);
        title_name = findViewById(R.id.title_name);
        header_image = findViewById(R.id.header_image);
        ly_popup_menu = findViewById(R.id.ly_popup_menu);
        ly_delete_chat = findViewById(R.id.ly_delete_chat);
        ly_btn_mute = findViewById(R.id.ly_btn_mute);
        ly_btn_info = findViewById(R.id.ly_btn_info);
        tv_mute = findViewById(R.id.tv_mute);
        ly_info_btn = findViewById(R.id.ly_info_btn);


        iv_back.setOnClickListener(this);
        send_msg_button.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        iv_popup_menu.setOnClickListener(this);
        ly_delete_chat.setOnClickListener(this);
        ly_btn_info.setOnClickListener(this);
        ly_btn_mute.setOnClickListener(this);
        ly_info_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_back: {
                onBackPressed();
                break;
            }

            case R.id.ly_delete_chat: {
                ly_popup_menu.setVisibility(View.GONE);
                deleteChatDialog();
                break;
            }

            case R.id.iv_popup_menu: {
                if (ly_popup_menu.getVisibility() == View.VISIBLE) {
                    ly_popup_menu.setVisibility(View.GONE);
                } else {
                    ly_popup_menu.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.camera_btn: {
                getPermissionAndPicImage();
                break;
            }

            case R.id.ly_btn_mute: {
                muteChat();
                break;
            }

            case R.id.send_msg_button: {
                ly_popup_menu.setVisibility(View.GONE);
                sendMessage();
                break;
            }

            case R.id.ly_btn_info: {
                ly_info_btn.callOnClick();
                ly_popup_menu.setVisibility(View.GONE);
                break;
            }

            case R.id.ly_info_btn: {
                Intent intent = new Intent(GroupChatHistortActivity.this, GroupMemberInfoActivity.class);

                intent.putExtra("eventId", eventId);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventType", eventType);
                intent.putExtra("eventImage", eventImage);

                intent.putExtra("eventOrganizerId", eventOrganizerId);
                intent.putExtra("eventOrganizerName", eventOrganizerName);
                intent.putExtra("eventOrganizerProfileImage", eventOrganizerProfileImage);

                intent.putExtra("eventOrganizerProfileImage", eventOrganizerProfileImage);
                intent.putExtra("eventOrganizerProfileImage", eventOrganizerProfileImage);

                startActivity(intent);
                break;
            }
        }
    }

    private void deleteChatDialog() {
        final Dialog _dialog = new Dialog(GroupChatHistortActivity.this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_dialog_txt.setText("Are you sure want to delete\n conversation");
        tv_name.setText(eventName + "?");
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupChatDeleteMuteInfo info = new GroupChatDeleteMuteInfo();
                info.lastDeleted = ServerValue.TIMESTAMP;
                info.email = myEmail;
                info.mute = mute;
                firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS_DELETE).child(eventId).child(myUid).setValue(info);

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

    private void muteChat() {
        GroupChatDeleteMuteInfo info = new GroupChatDeleteMuteInfo();
        info.lastDeleted = deleteTimestamp;
        info.email = myEmail;

        if (mute.equals("1")) {
            info.mute = "0";
        } else info.mute = "1";


        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS_DELETE).child(eventId).child(myUid).setValue(info);

    }

    private void getDeleteTimeStamp() {
        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS_DELETE)
                .child(eventId).child(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Object.class) != null) {
                            GroupChatDeleteMuteInfo info = dataSnapshot.getValue(GroupChatDeleteMuteInfo.class);
                            deleteTimestamp = (Long) info.lastDeleted;
                            if (info.mute != null) {
                                mute = info.mute;
                                if (info.mute.equals("1")) {
                                    tv_mute.setText("Unmute");
                                } else {
                                    tv_mute.setText("Mute");
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getChat() {
        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).orderByKey()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        getChatDataInmap(dataSnapshot.getKey(), chat);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        if (!keyList.contains(dataSnapshot.getKey())) {
                            getChatDataInmap(dataSnapshot.getKey(), chat);
                        } else {
                            updategetChatDataInmap(dataSnapshot.getKey(), chat);
                        }
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
            if (chat.deleteby != null) {

                if (chat.deleteby.equals(myUid)) {
                    return;
                } else {

                    if (deleteTimestamp == null) {
                        chat.banner_date = getDateBanner(chat.timestamp);
                        map.put(key, chat);
                        chatList.clear();
                        Collection<Chat> values = map.values();
                        chatList.addAll(values);

                    } else if (Long.parseLong(String.valueOf(chat.timestamp)) > deleteTimestamp) {
                        chat.banner_date = getDateBanner(chat.timestamp);
                        map.put(key, chat);
                        chatList.clear();
                        Collection<Chat> values = map.values();
                        chatList.addAll(values);
                    }


                    // chattingAdapter.notifyDataSetChanged();
                }
            }

        }
        shortList();
    }

    private void updategetChatDataInmap(String key, Chat chat) {
        if (chat != null) {
            if (chat.deleteby.equals(myUid)) {
                return;
            } else {

                if (deleteTimestamp == null) {
                    chat.banner_date = getDateBanner(chat.timestamp);
                    chat.imageUrl = map.get(key).imageUrl;

                    map.put(key, chat);
                    chatList.clear();
                    Collection<Chat> values = map.values();
                    chatList.addAll(values);

                } else if (Long.parseLong(String.valueOf(chat.timestamp)) > deleteTimestamp) {
                    chat.banner_date = getDateBanner(chat.timestamp);
                    chat.imageUrl = map.get(key).imageUrl;

                    map.put(key, chat);
                    chatList.clear();
                    Collection<Chat> values = map.values();
                    chatList.addAll(values);
                    //recycler_view.scrollToPosition(map.size() - 1);
                    // chattingAdapter.notifyDataSetChanged();
                }
            }
        }
        shortList();
    }

    private String getDateBanner(Object timeStamp) {
        String banner_date = "";
        SimpleDateFormat sim = new SimpleDateFormat(" d MMMM yyyy", Locale.US);
        try {
            String date_str = sim.format(new Date((Long) timeStamp)).trim();
            String currentDate = sim.format(Calendar.getInstance().getTime()).trim();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String yesterdayDate = sim.format(calendar.getTime()).trim();

            if (date_str.equals(currentDate)) {
                banner_date = "Today";
            } else if (date_str.equals(yesterdayDate)) {
                banner_date = "Yesterday";
            } else {
                banner_date = date_str.trim();
            }

            return banner_date;
        } catch (Exception e) {
            e.printStackTrace();
            return banner_date;
        }
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
        recycler_view.scrollToPosition(chatList.size() - 1);
        chattingAdapter.notifyDataSetChanged();
    }

    private void creatFirebaseProfilePicUrl(Uri selectedImageUri) {
        StorageReference storageRef;
        FirebaseStorage storage;
        FirebaseApp app;

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);

        storageRef = storage.getReference("group_chat_photos");
        StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        /*image_FirebaseURL = uri;
                        send_msg_button.callOnClick();*/
                        //holdKeyForImage

                        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(holdKeyForImage).child("imageUrl").setValue(uri.toString());
                        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(holdKeyForImage).child("imageUrl").setValue(uri.toString());

                        keyList.add(holdKeyForImage);
                        holdKeyForImage = "";
                    }
                });

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("sasa", e + "");
            }
        });

    }

    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(GroupChatHistortActivity.this);
            }
        } else {
            ImagePicker.pickImage(GroupChatHistortActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(GroupChatHistortActivity.this, requestCode, resultCode, data);
                image_FirebaseURL = imageUri;

                if (image_FirebaseURL.toString().startsWith("file:///storage/emulated")) {
                    Bitmap bitmap = ImagePicker.getImageFromResult(GroupChatHistortActivity.this, requestCode, resultCode, data);
                    image_FirebaseURL = getImageUri(GroupChatHistortActivity.this, bitmap);
                } else {

                }


                send_msg_button.callOnClick();
                creatFirebaseProfilePicUrl(imageUri);

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void sendMessage() {

        String pushkey = firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).push().getKey();
        String msg = ed_message.getText().toString().trim();
        String firebase_id = "";
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        if (msg.equals("") && image_FirebaseURL != null) {

        } else if (msg.equals("")) {
            return;
        }


        Chat myChat = new Chat();
        myChat.deleteby = "";
        myChat.firebaseId = firebase_id;
        myChat.firebaseToken = firebaseToken;

        if (image_FirebaseURL != null) {
            myChat.imageUrl = image_FirebaseURL.toString();
            myChat.message = "";
            myChat.image = 1;
            holdKeyForImage = pushkey;
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

        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(pushkey).setValue(myChat);
        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(pushkey).setValue(myChat);

        if (isNotification) {
            if (image_FirebaseURL != null) {
                if (firebaseToken != null && myChat != null) {
                    sendPushNotificationToReceiver( "Image", myUid, firebaseToken);
                }
            } else {
                if (firebaseToken != null && myChat != null)
                    sendPushNotificationToReceiver( msg, myUid, firebaseToken);
            }
        }

        ed_message.setText("");
        image_FirebaseURL = null;
        loading_view.setVisibility(View.GONE);
    }

    private void sendPushNotificationToReceiver(String message, String uid, String firebaseToken) {
        tokenArrayList.clear();
        for(Map.Entry entry:tokenList.entrySet()){
            tokenArrayList.add(entry.getValue().toString());
        }

        PayLoadEvent payLoadEvent = new PayLoadEvent();
        payLoadEvent.eventId = eventId;
        payLoadEvent.eventName = eventName;
        payLoadEvent.eventImage = eventImage;
        payLoadEvent.eventOrganizerId = eventOrganizerId;
        payLoadEvent.eventOrganizerName = eventOrganizerName;
        payLoadEvent.eventOrganizerProfileImage = eventOrganizerProfileImage;
        payLoadEvent.eventType = eventType;
        payLoadEvent.ownerType = from;

        if(eventMemId == null){
            eventMemId = "";
        }

        payLoadEvent.eventMemId = eventMemId;
        payLoadEvent.compId = compId;

        Gson gson = new Gson();
        String payLoad = gson.toJson(payLoadEvent,PayLoadEvent.class);

        FcmNotificationBuilder.initialize()
                .title(eventName)
                .message(message)
                .username(myName)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .eventPayLoad(payLoad)
                .isGroupChatModule(true)
                .receiverFirebaseTokenGroup(new JSONArray(tokenArrayList)).send();
    }



    private void joinedEventList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        JoinedEventInfo joinedEventInfo = gson.fromJson(response, JoinedEventInfo.class);


                        for (int i = 0; i < joinedEventInfo.List.size(); i++) {
                            if (joinedEventInfo.List.get(i).memberUserId != null) {
                                joinedEventInfo.List.get(i).commanUserIdForProfile = joinedEventInfo.List.get(i).memberUserId;
                            }

                            if (joinedEventInfo.List.get(i).companionMemberStatus != null) {
                                if (joinedEventInfo.List.get(i).companionMemberStatus.equals("3") ||
                                        joinedEventInfo.List.get(i).companionMemberStatus.equals("1")) {
                                    JoinedEventInfo.ListBean infoComp = new JoinedEventInfo.ListBean();

                                    infoComp.companionUserId = joinedEventInfo.List.get(i).companionUserId;
                                    infoComp.memberName = joinedEventInfo.List.get(i).companionName;
                                    infoComp.memberImage = joinedEventInfo.List.get(i).companionImage;
                                    infoComp.commanUserIdForProfile = joinedEventInfo.List.get(i).companionUserId;

                                    joinedEventInfo.List.add(infoComp);
                                }
                            }
                        }


                        if (eventType.equals("eventRequest")) {
                            JoinedEventInfo.ListBean info = new JoinedEventInfo.ListBean();
                            info.commanUserIdForProfile = eventOrganizerId;
                            info.memberId = eventOrganizerId;
                            info.memberUserId = eventOrganizerId;
                            info.memberName = eventOrganizerName + " " + "(Admin)";
                            info.memberImage = eventOrganizerProfileImage;

                            //joinedEventInfo.List.get(0). = session.getUser().userDetail.r;
                            joinedEventInfo.List.add(0, info);
                        } else {
                            // my data added here
                            JoinedEventInfo.ListBean info = new JoinedEventInfo.ListBean();
                            info.commanUserIdForProfile = myUid;
                            info.memberUserId = myUid;
                            info.memberName = myName + " " + "(You)";
                            info.memberImage = session.getUser().userDetail.profileImage.get(session.getUser().userDetail.profileImage.size() - 1).image;
                            //joinedEventInfo.List.get(0). = session.getUser().userDetail.r;
                            joinedEventInfo.List.add(0, info);
                        }

                        joinedList.addAll(joinedEventInfo.List);
                        getFirebaseToken();
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
        service.callGetSimpleVolley("event/getEventMemberList?offset=" + 0 + "&limit=1000&memberType=" + "joined" + "&eventId=" + eventId + "");
    }

    private void getFirebaseToken() {
        for (JoinedEventInfo.ListBean bean : joinedList) {

            firebaseDatabase.getReference().child(Constant.ARG_USERS).child(bean.commanUserIdForProfile).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfoFCM infoFCM = dataSnapshot.getValue(UserInfoFCM.class);
                    tokenList.put(dataSnapshot.getKey(), infoFCM.firebaseToken);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }


}
