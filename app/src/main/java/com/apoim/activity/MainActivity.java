package com.apoim.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.fragment.AppoinmentFragment;
import com.apoim.fragment.ChatFragment;
import com.apoim.fragment.EventFragment;
import com.apoim.fragment.ListorMapFragment;
import com.apoim.fragment.MyProfileFragment;
import com.apoim.groupchatwebrtc.activities.BaseActivity;
import com.apoim.groupchatwebrtc.activities.CallActivity;
import com.apoim.groupchatwebrtc.activities.OpponentsActivity;
import com.apoim.groupchatwebrtc.db.QbUsersDbManager;
import com.apoim.groupchatwebrtc.util.QBResRequestExecutor;
import com.apoim.groupchatwebrtc.utils.Consts;
import com.apoim.groupchatwebrtc.utils.PermissionsChecker;
import com.apoim.groupchatwebrtc.utils.SharedPrefsHelper;
import com.apoim.groupchatwebrtc.utils.WebRtcSessionManager;
import com.apoim.helper.Constant;
import com.apoim.modal.Chat;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.facebook.login.LoginManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout ly_map_tab, ly_event_tab, ly_home_tab, ly_chat_tab, ly_profile_tab;
    ImageView iv_map_tab, iv_event_tab, iv_home_tab, iv_chat_tab, iv_profile_tab;
    String type = "";
    String title = "";
    String current_fragment_Name = "";
    boolean doubleBackToExitPressedOnce = false;
    String createrId = "";
    String reference_id = "";
    private Session session;
    private String myUserId = "";
    //This is our tablayout
    private TabLayout tabLayout;
    TabLayout.Tab tabAt;
    String eventMemId = "";
    String compId = "";
    String opponentChatId = "";
    ImageView iv_unread_msg_tab;
    Map<String, String> isMsgFoundMap;

    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;
    protected QBResRequestExecutor requestExecutor;
    private QbUsersDbManager dbManager;


    public static void start(Context context, boolean isRunForCall) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(MainActivity.this, true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("com.apoim"));
        setContentView(R.layout.activity_main);
        initView();

        //Initializing the tablayout
        tabLayout = findViewById(R.id.tablayout);
        isMsgFoundMap = new HashMap<>();

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabAt = tabLayout.getTabAt(2);
        tabAt.select();


        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        requestExecutor = Apoim.getInstance().getQbResRequestExecutor();

        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(MainActivity.this, true);
        }

        if (getIntent().getExtras() != null) {
            isRunForCall = getIntent().getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(MainActivity.this, true);
            }
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 30, 0);
            tab.requestLayout();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    ly_map_tab.callOnClick();
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    ly_event_tab.callOnClick();
                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    ly_home_tab.callOnClick();
                } else if (tabLayout.getSelectedTabPosition() == 3) {
                    ly_chat_tab.callOnClick();
                } else if (tabLayout.getSelectedTabPosition() == 4) {
                    ly_profile_tab.callOnClick();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ly_map_tab.setOnClickListener(this);
        ly_event_tab.setOnClickListener(this);
        ly_home_tab.setOnClickListener(this);
        ly_chat_tab.setOnClickListener(this);
        ly_profile_tab.setOnClickListener(this);
        session = new Session(this, this);

        if (session.getUser() != null)
            myUserId = session.getUser().userDetail.userId;

        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
            title = getIntent().getStringExtra("title");
            String message = getIntent().getStringExtra("message");
            reference_id = getIntent().getStringExtra("reference_id");
            createrId = getIntent().getStringExtra("createrId");
            String orderId = getIntent().getStringExtra("orderId");

            eventMemId = getIntent().getStringExtra("eventMemId");
            compId = getIntent().getStringExtra("compId");
            opponentChatId = getIntent().getStringExtra("opponentChatId");

        }

        if (getIntent().getStringExtra("type") != null) {

            if (type.equals("friend_request") || type.equals("accept_request") || type.equals("add_like") || type.equals("add_favorite")) {
                Intent intent = new Intent(MainActivity.this, OtherProfileActivity.class);// other user profile
                intent.putExtra("userId", reference_id);
                startActivity(intent);
            }
            else if (type.equals("create_appointment") || type.equals("delete_appointment") ||
                    type.equals("apply_counter") || type.equals("update_counter")) {
                ly_map_tab.callOnClick();// appoinment listing
            }
            else if (type.equals("confirmed_appointment") || type.equals("finish_appointment")) {
                Intent intent = new Intent(this, AppointmentDirectionActivity.class); // appointment details
                intent.putExtra(Constant.appointment_details, reference_id);
                startActivity(intent);
            }
            else if (type.equals("create_event") || type.equals("companion_payment") ||
                    type.equals("join_event") || type.equals("event_payment") ||
                    type.equals("share_event") || type.equals("companion_accept") ||
                    type.equals("companion_reject")) {

                Intent intent = new Intent(this, EventDetailsActivity.class); // event details
                intent.putExtra("eventId", reference_id);
                if (myUserId.equals(createrId)) {
                    intent.putExtra("from", "myEvent");// for my event details
                } else {
                    intent.putExtra("from", "eventRequest"); // event request details
                }

                if (eventMemId.equals("")) {
                    intent.putExtra("id", compId);
                    intent.putExtra("ownerType", "Shared Event");
                } else if (compId.equals("")) {
                    intent.putExtra("id", eventMemId);
                    intent.putExtra("ownerType", "Administrator");
                }

                startActivity(intent);
            }
            else if (type.equals("chat")) {
                Intent intent = new Intent(this, ChatActivity.class); // appointment details
                if (opponentChatId != null) {
                    intent.putExtra("otherUID", opponentChatId);
                    intent.putExtra("titleName", title);
                }
                startActivity(intent);
            } else if (type.equals("id_verification")) {
                profile_tab();
                addFragment(new MyProfileFragment().newInstance(type), false, R.id.fragment_place);
            }

        }
        else if (getIntent().getData() != null) {
            Map<String, String> map = Utils.getQueryString(getIntent().getData().toString());
            if (map.containsKey("id")) {
                Intent intent = new Intent(this, OtherProfileActivity.class);
                try {

                    byte[] data = Base64.decode(map.get("id"), Base64.DEFAULT);
                    String decodedId = new String(data, "UTF-8");
                    intent.putExtra("userId", decodedId);
                    type = "deeplinking";
                    startActivity(intent);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        } else {

            addFragment(new ListorMapFragment(), false, R.id.fragment_place);
        }

        // Intent intent = new Intent(this,VideoCallActivity.class);
        //startActivity(intent);


    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent1) {
            type = intent1.getStringExtra("type");

            if (current_fragment_Name.equals("AppoinmentFragment")) {
                if (type.equals("create_appointment") || type.equals("delete_appointment")) {

                    iv_map_tab.setImageResource(R.drawable.ico_tab_app_active);
                    iv_event_tab.setImageResource(R.drawable.ico_tab_event);
                    iv_home_tab.setImageResource(R.drawable.ico_tab_home);
                    iv_chat_tab.setImageResource(R.drawable.ico_tab_chat);
                    iv_profile_tab.setImageResource(R.drawable.ico_tab_profile);

                    tabAt = tabLayout.getTabAt(0);
                    tabAt.select();

                    addFragment(new AppoinmentFragment(), false, R.id.fragment_place);
                }
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void startLoadUsers() {
        String currentRoomName = SharedPrefsHelper.getInstance().get(Consts.PREF_CURREN_ROOM_NAME);
        requestExecutor.loadUsersByTag("mychatroom", new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                dbManager.saveAllUsers(result, true);
            }

            @Override
            public void onError(QBResponseException responseException) {
                startLoadUsers();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type != null && !type.equals("")) {

            if (type.equals("friend_request") || type.equals("accept_request") || type.equals("add_like") || type.equals("add_favorite")) {
                addFragment(new ListorMapFragment(), false, R.id.fragment_place);
                type = "";
            } else if (type.equals("confirmed_appointment") || type.equals("finish_appointment") ||
                    type.equals("apply_counter") || type.equals("update_counter")) {
                ly_map_tab.callOnClick();
                type = "";

            } else if (type.equals("create_event") || type.equals("companion_payment") ||
                    type.equals("join_event") || type.equals("event_payment") ||
                    type.equals("share_event") || type.equals("companion_accept") || type.equals("companion_reject")) {

                ly_event_tab.callOnClick();
                type = "";

            } else if (type.equals("chat")) {
                ly_chat_tab.callOnClick();
                type = "";
            } else if (type.equals("deeplinking")) {
                ly_home_tab.callOnClick();
                type = "";
            }
        }
        startLoadUsers();
        checkUREADmsg();
    }

    private void initView() {
        ly_map_tab = findViewById(R.id.ly_map_tab);
        ly_event_tab = findViewById(R.id.ly_event_tab);
        ly_home_tab = findViewById(R.id.ly_home_tab);
        ly_chat_tab = findViewById(R.id.ly_chat_tab);
        ly_profile_tab = findViewById(R.id.ly_profile_tab);

        iv_map_tab = findViewById(R.id.iv_map_tab);
        iv_event_tab = findViewById(R.id.iv_event_tab);
        iv_home_tab = findViewById(R.id.iv_home_tab);
        iv_chat_tab = findViewById(R.id.iv_chat_tab);
        iv_profile_tab = findViewById(R.id.iv_profile_tab);
        iv_unread_msg_tab = findViewById(R.id.iv_unread_msg_tab);


        ///facebook_logout = findViewById(R.id.facebook_logout);
    }

    private void checkUREADmsg() {
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    String readBy = dataSnapshot.getValue(Chat.class).readBy;

                    if (readBy != null) {
                        isMsgFoundMap.put(dataSnapshot.getKey(), readBy);

                        if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);


                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    String readBy = dataSnapshot.getValue(Chat.class).readBy;
                    if (readBy != null) {
                        isMsgFoundMap.put(dataSnapshot.getKey(), readBy);
                        if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    String readBy = dataSnapshot.getValue(Chat.class).readBy;
                    if (readBy != null) {
                        isMsgFoundMap.put(dataSnapshot.getKey(), readBy);
                        if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.ly_map_tab:
                iv_map_tab.setImageResource(R.drawable.ico_tab_app_active);
                iv_event_tab.setImageResource(R.drawable.ico_tab_event);
                iv_home_tab.setImageResource(R.drawable.ico_tab_home);
                iv_chat_tab.setImageResource(R.drawable.ico_tab_chat);
                iv_profile_tab.setImageResource(R.drawable.ico_tab_profile);

                ly_map_tab.setEnabled(false);
                ly_event_tab.setEnabled(true);
                ly_home_tab.setEnabled(true);
                ly_chat_tab.setEnabled(true);
                ly_profile_tab.setEnabled(true);
                current_fragment_Name = "AppoinmentFragment";
                String reqType = "";

                if (type != null) {
                    if (type.equals("apply_counter")) {
                        reqType = "sent";
                    } else if (type.equals("update_counter")) {
                        reqType = "received";
                    }
                }


                addFragment(AppoinmentFragment.newInstance(reqType), false, R.id.fragment_place);
                tabAt = tabLayout.getTabAt(0);
                tabAt.select();
                break;

            case R.id.ly_event_tab:
                iv_map_tab.setImageResource(R.drawable.ico_tab_app);
                iv_event_tab.setImageResource(R.drawable.ico_tab_event_active);
                iv_home_tab.setImageResource(R.drawable.ico_tab_home);
                iv_chat_tab.setImageResource(R.drawable.ico_tab_chat);
                iv_profile_tab.setImageResource(R.drawable.ico_tab_profile);

                ly_map_tab.setEnabled(true);
                ly_event_tab.setEnabled(false);
                ly_home_tab.setEnabled(true);
                ly_chat_tab.setEnabled(true);
                ly_profile_tab.setEnabled(true);
                current_fragment_Name = "EventFragment";
                addFragment(new EventFragment(), false, R.id.fragment_place);

                tabAt = tabLayout.getTabAt(1);
                tabAt.select();
                break;

            case R.id.ly_home_tab:
                iv_map_tab.setImageResource(R.drawable.ico_tab_app);
                iv_event_tab.setImageResource(R.drawable.ico_tab_event);
                iv_home_tab.setImageResource(R.drawable.ico_tab_home_active);
                iv_chat_tab.setImageResource(R.drawable.ico_tab_chat);
                iv_profile_tab.setImageResource(R.drawable.ico_tab_profile);

                ly_map_tab.setEnabled(true);
                ly_event_tab.setEnabled(true);
                ly_home_tab.setEnabled(false);
                ly_chat_tab.setEnabled(true);
                ly_profile_tab.setEnabled(true);
                current_fragment_Name = "ListorMapFragment";
                addFragment(new ListorMapFragment(), false, R.id.fragment_place);

                tabAt = tabLayout.getTabAt(2);
                tabAt.select();
                break;

            case R.id.ly_chat_tab:
                iv_map_tab.setImageResource(R.drawable.ico_tab_app);
                iv_event_tab.setImageResource(R.drawable.ico_tab_event);
                iv_home_tab.setImageResource(R.drawable.ico_tab_home);
                iv_chat_tab.setImageResource(R.drawable.ico_tab_chat_active);
                iv_profile_tab.setImageResource(R.drawable.ico_tab_profile);

                ly_map_tab.setEnabled(true);
                ly_event_tab.setEnabled(true);
                ly_home_tab.setEnabled(true);
                ly_chat_tab.setEnabled(false);
                ly_profile_tab.setEnabled(true);
                current_fragment_Name = "ChatFragment";
                addFragment(new ChatFragment(), false, R.id.fragment_place);

                tabAt = tabLayout.getTabAt(3);
                tabAt.select();
                break;

            case R.id.ly_profile_tab:
                profile_tab();
                addFragment(new MyProfileFragment(), false, R.id.fragment_place);
                break;
        }
    }

    private void profile_tab() {
        iv_map_tab.setImageResource(R.drawable.ico_tab_app);
        iv_event_tab.setImageResource(R.drawable.ico_tab_event);
        iv_home_tab.setImageResource(R.drawable.ico_tab_home);
        iv_chat_tab.setImageResource(R.drawable.ico_tab_chat);
        iv_profile_tab.setImageResource(R.drawable.ico_tab_profile_active);

        ly_map_tab.setEnabled(true);
        ly_event_tab.setEnabled(true);
        ly_home_tab.setEnabled(true);
        ly_chat_tab.setEnabled(true);
        ly_profile_tab.setEnabled(false);
        current_fragment_Name = "MyProfileFragment";


        tabAt = tabLayout.getTabAt(4);
        tabAt.select();
    }


    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
