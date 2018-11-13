package com.apoim.fragment.eventFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.activity.event.CreateNewEventActivity;
import com.apoim.activity.event.FilterEventActivity;
import com.apoim.adapter.apoinment.InviteFrienAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.modal.AllUserForEventInfo;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.EventFilterData;
import com.apoim.multipleFileUpload.MultiPartRequest;
import com.apoim.multipleFileUpload.Template;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.apoim.activity.event.CreateEventActivity.RattingIds;
import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by mindiii on 14/9/18.
 */

public class ThiredFragment extends Fragment {
    private TextView tv_next_thired, tv_select_background_three, tv_three;
    private ImageView iv_right_three, iv_filter;
    private Context mContext;
    private RecyclerView recycler_view;

    private TextView tv_select_background_two, tv_two;
    private ImageView iv_right_two;
    private String eventId = "", imageId = "",updatedIds = "";
    private String eventUserType = "";
    private String privacy = "";
    private ArrayList<AllUserForEventInfo.DataBean.UserBean> friendList;
    private InsLoadingView loadingView;
    private LinearLayout ly_no_friend_found;
    private EditText ed_search_friend;
    private Session session;
    private Bitmap bitmap;
    String latitude = null, longitude = null, name = null;
    private InviteFrienAdapter adapter;
    private RelativeLayout ly_action_bar;


    public static ThiredFragment newInstance(String eventUserType, String privacy) {
        Bundle args = new Bundle();
        ThiredFragment fragment = new ThiredFragment();
        fragment.setArguments(args);
        args.putString("eventUserType", eventUserType);
        args.putString("privacy", privacy);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_thired_screen, container, false);

        if (getArguments() != null) {
            eventUserType = getArguments().getString("eventUserType");
            privacy = getArguments().getString("privacy");
        }

        session = new Session(mContext);
        tv_next_thired = view.findViewById(R.id.tv_next_thired);

        if (CreateNewEventActivity.isForUpdateEvent) {
            friendsIds = session.getcreateEventInfo().Detail.memberIds;
            tv_next_thired.setText(R.string.update_event);
        }

        if (session.getcreateEventInfo().Detail.firstImage != null) {
            bitmap = getBitmap(session.getcreateEventInfo().Detail.firstImage);
        }

        ly_action_bar = view.findViewById(R.id.ly_action_bar);
        ly_action_bar.setVisibility(View.GONE);

        ed_search_friend = view.findViewById(R.id.ed_search_friend);

        iv_filter = getActivity().findViewById(R.id.iv_filter);
        iv_filter.setVisibility(View.VISIBLE);
        tv_select_background_three = getActivity().findViewById(R.id.tv_select_background_three);
        tv_three = getActivity().findViewById(R.id.tv_three);
        iv_right_three = getActivity().findViewById(R.id.iv_right_three);
        tv_select_background_two = getActivity().findViewById(R.id.tv_select_background_two);
        tv_two = getActivity().findViewById(R.id.tv_two);
        iv_right_two = getActivity().findViewById(R.id.iv_right_two);

        recycler_view = view.findViewById(R.id.recycler_view);
        loadingView = view.findViewById(R.id.loadingView);
        ly_no_friend_found = view.findViewById(R.id.ly_no_friend_found);


        tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_three.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        friendList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recycler_view.setLayoutManager(manager);


        iv_filter.setOnClickListener(view1 -> {
            Intent intent = new Intent(mContext, FilterEventActivity.class);
            startActivityForResult(intent, Constant.eventFilter);
        });

        tv_next_thired.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(friendsIds)) {
                loadingView.setVisibility(View.VISIBLE);
                tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
                iv_right_three.setVisibility(View.VISIBLE);
                tv_three.setVisibility(View.GONE);

                if (CreateNewEventActivity.isForUpdateEvent){
                    updateEvent(tv_next_thired);
                }else  registerBusiness(tv_next_thired);



            } else {
                Utils.openAlertDialog(mContext, getString(R.string.event_invitaion));
            }


        });

         adapter = new InviteFrienAdapter(mContext, privacy, friendList, friendsIds, new CreateEventActivity.FriedsIdsListner() {
            @Override
            public void getIds(String ids) {
                friendsIds = ids;
            }
        },"");


        recycler_view.setAdapter(adapter);
        searchFriend();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, name);

        EventFilterData filterData = session.getFilterData();
        if(filterData.isApply){
            iv_filter.setImageResource(R.drawable.ico_filter_active);
        }else {
            iv_filter.setImageResource(R.drawable.ico_filter);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == Constant.eventFilter){
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                //rating = data.getStringExtra("ratting");

            }

        }
    }

    private Bitmap getBitmap(String encoded) {
        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


    private void searchFriend() {

        ed_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textName = editable.toString();
                if(textName.equals("")){
                    showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, null);
                }else {
                    showFriendList(loadingView, adapter, ly_no_friend_found, recycler_view, textName);
                }

                //InviteFrienAdapter adapter = new InviteFrienAdapter(mContext, privacy, temp_friendList, friendsIds, ids -> friendsIds = ids);
                //recycler_view.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iv_filter.setVisibility(View.GONE);
        tv_select_background_three.setBackgroundResource(R.drawable.holo_circle_border);
        tv_three.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_two.setVisibility(View.VISIBLE);
        tv_two.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        iv_right_two.setVisibility(View.GONE);

        ThiredFragment youTubePlayerFragment = (ThiredFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);
        if (youTubePlayerFragment != null)
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();

    }

    private void showFriendList(final InsLoadingView loading_view, final InviteFrienAdapter adapter,
                                final LinearLayout ly_no_friend_found, final RecyclerView recycler_view,String name) {
        loading_view.setVisibility(View.VISIBLE);

        EventFilterData filterData = session.getFilterData();
        latitude =  filterData.latitude;
        longitude = filterData.longitude;
        String RattingIds = filterData.rating;

        HashMap<String, String> map = new HashMap<>();
        map.put("userGender", eventUserType);
        map.put("privacy", privacy);

        map.put("offset", "0");
        map.put("limit", "100");

        if (name != null)
            map.put("name", name);

        if (RattingIds != null ){
            if(!RattingIds.equals("")){
                map.put("rating", RattingIds);
            }
        }


        if (latitude != null)
            map.put("latitude", latitude);

        if (longitude != null)
            map.put("longitude", longitude);


        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    friendList.clear();

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        AllUserForEventInfo allUserList = gson.fromJson(response, AllUserForEventInfo.class);
                        friendList.addAll(allUserList.data.user);

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            ly_no_friend_found.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                        }


                        if(friendsIds != null){
                            List<String> tempList = new ArrayList<>(Arrays.asList(friendsIds.split(",")));
                            for (int i = 0; i < friendList.size(); i++) {
                                for (int j = 0; j < tempList.size(); j++) {
                                    if (tempList.get(j).equals(friendList.get(i).userId + "")) {
                                        friendList.get(i).isSelected = true;
                                        updatedIds = friendList.get(i).userId +","+updatedIds;

                                    }
                                }
                            }
                            if (CreateNewEventActivity.isForUpdateEvent) {
                                friendsIds = updatedIds;
                                friendsIds = friendsIds.replaceAll(",$", "");
                            }

                        }

                        adapter.notifyDataSetChanged();
                    } else {

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
        service.callSimpleVolley("event/getInvitationUserList", map);


    }

    private void registerBusiness(TextView button) {
        button.setEnabled(false);
        EventDetailsInfo bean = session.getcreateEventInfo();

        Map<String, String> map = new HashMap<>();
        map.put("eventName", bean.Detail.eventName);
        map.put("eventStartDate", bean.Detail.eventStartDate);
        map.put("eventEndDate", bean.Detail.eventEndDate);
        map.put("eventPlace", bean.Detail.eventPlace);
        map.put("eventLatitude", bean.Detail.eventLatitude);
        map.put("eventLongitude", bean.Detail.eventLongitude);
        map.put("privacy", bean.Detail.privacy);
        map.put("payment", bean.Detail.payment);
        map.put("userLimit", bean.Detail.userLimit);
        map.put("businessId", bean.Detail.businessId);

        if(bean.Detail.eventUserType.equals("")){
            map.put("eventUserType", "4");
        }else {
            map.put("eventUserType", bean.Detail.eventUserType);
        }

        map.put("inviteFriendId", friendsIds);
        map.put("eventAmount", bean.Detail.eventAmount);
        map.put("currencySymbol", bean.Detail.currencySymbol);
        map.put("currencyCode", bean.Detail.currencyCode);
        map.put("groupChat", bean.Detail.groupChat);

        Log.d("eventDats", map+"");
        ArrayList<File> fileList = new ArrayList<>();
        if (bitmap != null) {
            fileList = new ArrayList<>();
            fileList.add(bitmapToFile(bitmap));
        }


        MultiPartRequest mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingView.setVisibility(View.GONE);
                button.setEnabled(true);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    loadingView.setVisibility(View.GONE);

                    if (status.equals("success")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        Log.d("eventResponse", response+"");
                        eventId = object.getString("eventId");

                        JSONObject imageObj = object.getJSONObject("event");
                        imageId = imageObj.getString("eventImgId");

                        iv_filter.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);

                        EventFilterData data = new EventFilterData();
                        session.createFilterData(data);
                        RattingIds = "";
                        successDialog(mContext,"Event created successfully");
                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }

                    button.setEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                    button.setEnabled(true);
                    loadingView.setVisibility(View.GONE);
                }
            }

        }, fileList, fileList.size(), map, (Activity) mContext, "eventImage[]", "event/createEvent");

        //Set tag
        mMultiPartRequest.setTag("MultiRequest");

        //Set retry policy
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Apoim.getInstance().addToRequestQueue(mMultiPartRequest, "UPLOAD");
    }

    private void updateEvent(TextView button) {
        button.setEnabled(false);
        loadingView.setVisibility(View.VISIBLE);
        EventDetailsInfo bean = session.getcreateEventInfo();

        Map<String, String> map = new HashMap<>();
        map.put("eventId", bean.Detail.eventId);
        map.put("eventName", bean.Detail.eventName);
        map.put("eventStartDate", bean.Detail.eventStartDate);
        map.put("eventEndDate", bean.Detail.eventEndDate);
        map.put("eventPlace", bean.Detail.eventPlace);
        map.put("eventLatitude", bean.Detail.eventLatitude);
        map.put("eventLongitude", bean.Detail.eventLongitude);
        map.put("privacy", bean.Detail.privacy);
        map.put("payment", bean.Detail.payment);
        map.put("userLimit", bean.Detail.userLimit);
        map.put("businessId", bean.Detail.businessId);

        if(bean.Detail.eventUserType.equals("")){
            map.put("eventUserType", "4");
        }else {
            map.put("eventUserType", bean.Detail.eventUserType);
        }

        map.put("inviteFriendId", friendsIds);
        map.put("eventAmount", bean.Detail.eventAmount);
        map.put("currencySymbol", bean.Detail.currencySymbol);
        map.put("currencyCode", bean.Detail.currencyCode);
        map.put("groupChat", bean.Detail.groupChat);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                button.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        successDialog(mContext,message);
                        EventFilterData data = new EventFilterData();
                        session.createFilterData(data);
                        RattingIds = "";
                        friendsIds = "";

                    } else if (status.equals("fail")) {
                        Utils.openAlertDialog(mContext, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    button.setEnabled(true);
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                button.setEnabled(true);
                loadingView.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("event/updateEvent", map);



    }

    public File bitmapToFile(Bitmap bmp) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(mContext.getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  void successDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (CreateNewEventActivity.isForUpdateEvent) {
                    addFragment(FourthScreenFragment.newInstance(session.getcreateEventInfo().Detail.eventId, session.getcreateEventInfo().Detail.eventImage.get(0).eventImgId), true, R.id.event_fragment_place);
                }else addFragment(FourthScreenFragment.newInstance(eventId, imageId), true, R.id.event_fragment_place);
            }
        });
        AlertDialog alert = builder.create();

        if(!((Activity) context).isFinishing())
        {
            alert.show();
        }

    }


}
