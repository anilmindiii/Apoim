package com.apoim.activity.chat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.adapter.apoinment.JoinedEventAdapter;
import com.apoim.adapter.chat.ChattingAdapter;
import com.apoim.adapter.newEvent.JoinedMemberChatAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.GetDateStatus;
import com.apoim.modal.Chat;
import com.apoim.modal.JoinedEventInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupChatHistortActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_chat, btn_group_member;
    private CardView bottom_box;
    private ImageView iv_back, iv_popup_menu, send_msg_button, camera_btn, header_image;
    private RecyclerView recycler_view;
    private InsLoadingView loading_view;
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    private JoinedMemberChatAdapter adapter;
    private ChattingAdapter chattingAdapter;
    String from = "", eventId = "", myUid = "", myName = "", myProfileImage = "";
    private ArrayList<Chat> chatList;
    private Session session;
    private FirebaseDatabase firebaseDatabase;
    private Map<String, Chat> map;
    private ArrayList<String> keyList;
    private String holdKeyForImage = "";
    private Uri image_FirebaseURL;
    private EditText ed_message;
    private TextView tv_days_status, tv_mem_count, title_name;
    private boolean isChatTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_histort);

        iv_back = findViewById(R.id.iv_back);
        bottom_box = findViewById(R.id.bottom_box);
        iv_popup_menu = findViewById(R.id.iv_popup_menu);
        recycler_view = findViewById(R.id.recycler_view);
        loading_view = findViewById(R.id.loading_view);
        btn_chat = findViewById(R.id.btn_chat);
        btn_group_member = findViewById(R.id.btn_group_member);
        send_msg_button = findViewById(R.id.send_msg_button);
        ed_message = findViewById(R.id.ed_message);
        camera_btn = findViewById(R.id.camera_btn);
        tv_days_status = findViewById(R.id.tv_days_status);
        tv_mem_count = findViewById(R.id.tv_mem_count);
        title_name = findViewById(R.id.title_name);
        header_image = findViewById(R.id.header_image);


        joinedList = new ArrayList<>();
        adapter = new JoinedMemberChatAdapter(joinedList, this);
        session = new Session(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();
        keyList = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            eventId = getIntent().getStringExtra("eventId");
            from = getIntent().getStringExtra("from");
            String eventName = getIntent().getStringExtra("eventName");
            String eventImage = getIntent().getStringExtra("eventImage");
            title_name.setText(eventName);
            Glide.with(this).load(eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(header_image);
        }

        myUid = session.getUser().userDetail.userId;
        myName = session.getUser().userDetail.fullName;
        if (session.getUser().userDetail.profileImage.size() > 0) {
            myProfileImage = session.getUser().userDetail.profileImage.get(0).image;
        }
        chatList = new ArrayList<>();
        chattingAdapter = new ChattingAdapter(this, chatList, myUid, new GetDateStatus() {
            @Override
            public void currentDateStatus(Object timestamp) {

            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(adapter);

        recycler_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //ly_popup_menu.setVisibility(View.GONE);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isChatTab)
                        tv_days_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                    if (chatList.size() > 0)
                        tv_days_status.setText(chatList.get(linearLayoutManager.findFirstVisibleItemPosition()).banner_date + "");
                    if (isChatTab)
                        tv_days_status.setVisibility(View.VISIBLE);
                }
            }
        });


        btn_chat.setOnClickListener(this);
        btn_group_member.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        send_msg_button.setOnClickListener(this);
        camera_btn.setOnClickListener(this);

        joinedEventList();
        getChat();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_group_member: {
                isChatTab = false;
                recycler_view.setAdapter(adapter);
                bottom_box.setVisibility(View.GONE);
                btn_chat.setBackground(ContextCompat.getDrawable(this, R.color.white));
                btn_chat.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));

                btn_group_member.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_pink));
                btn_group_member.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                break;
            }

            case R.id.btn_chat: {
                isChatTab = true;
                recycler_view.setAdapter(chattingAdapter);
                bottom_box.setVisibility(View.VISIBLE);
                btn_chat.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_pink));
                btn_chat.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                btn_group_member.setBackground(ContextCompat.getDrawable(this, R.color.white));
                btn_group_member.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                break;
            }

            case R.id.iv_back: {
                onBackPressed();
                break;
            }

            case R.id.iv_popup_menu: {

                break;
            }

            case R.id.camera_btn: {
                getPermissionAndPicImage();
                break;
            }

            case R.id.send_msg_button: {
                /*ly_popup_menu.setVisibility(View.GONE);
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
                }*/

                sendMessage();
                break;
            }
        }
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
                        joinedList.addAll(joinedEventInfo.List);
                        adapter.notifyDataSetChanged();

                        if (joinedList.size() == 1) {
                            tv_mem_count.setText(joinedList.size() + " " + "Member");
                        } else tv_mem_count.setText(joinedList.size() + " " + "Members");
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
                    chat.banner_date = getDateBanner(chat.timestamp);

                    map.put(key, chat);
                    chatList.clear();
                    Collection<Chat> values = map.values();
                    chatList.addAll(values);

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

        /*Chat otherChat = new Chat();
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
        otherChat.unreadCount = 0;*/


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
        /*myChat.unreadCount = 0;

        if (isOtherUserOnline) {
            myChat.isMsgReadTick = 1;
        } else {
            myChat.isMsgReadTick = 0;
        }*/


        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(pushkey).setValue(myChat);
        firebaseDatabase.getReference().child(Constant.ARG_GROUP_CHAT_ROOMS).child(eventId).child(pushkey).setValue(myChat);

        /*if (isNotification) {
            if (image_FirebaseURL != null) {
                if (firebaseToken != null && otherUserInfo != null) {
                    sendPushNotificationToReceiver(myName, "Image", myName, myUid, firebaseToken);
                }
            } else {
                if (firebaseToken != null && otherUserInfo != null)
                    sendPushNotificationToReceiver(myName, msg, myName, myUid, firebaseToken);
            }
        }*/

        ed_message.setText("");
        image_FirebaseURL = null;
        loading_view.setVisibility(View.GONE);
    }


}
