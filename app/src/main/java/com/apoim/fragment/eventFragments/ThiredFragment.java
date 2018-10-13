package com.apoim.fragment.eventFragments;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.adapter.apoinment.InviteFrienAdapter;
import com.apoim.app.Apoim;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by mindiii on 14/9/18.
 */

public class ThiredFragment extends Fragment {
    private TextView tv_next_thired,tv_select_background_three,tv_three;
    private ImageView iv_right_three;
    private Context mContext;
    private RecyclerView recycler_view;

    private TextView tv_select_background_two,tv_two;
    private ImageView iv_right_two;
    private String eventId = "";
    private String eventUserType = "";
    private String privacy = "";
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private InsLoadingView loadingView;
    private LinearLayout ly_no_friend_found;
    private EditText ed_search_friend;

    public static ThiredFragment newInstance(String eventUserType,String privacy) {
        Bundle args = new Bundle();
        ThiredFragment fragment = new ThiredFragment();
        fragment.setArguments(args);
        args.putString("eventUserType",eventUserType);
        args.putString("privacy",privacy);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_thired_screen, container, false);

        if(getArguments() != null){
            eventUserType = getArguments().getString("eventUserType");
        }


        tv_next_thired = view.findViewById(R.id.tv_next_thired);
        ed_search_friend = view.findViewById(R.id.ed_search_friend);

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
        tv_three.setTextColor(ContextCompat.getColor(mContext,R.color.white));

        friendList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recycler_view.setLayoutManager(manager);


        tv_next_thired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(friendsIds)) {
                    tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
                    iv_right_three.setVisibility(View.VISIBLE);
                    tv_three.setVisibility(View.GONE);
                    addFragment(new FourthScreenFragment(), true, R.id.event_fragment_place);
                } else {
                    Utils.openAlertDialog(mContext, getString(R.string.event_invitaion));
                }

            }
        });

        final InviteFrienAdapter adapter = new InviteFrienAdapter(mContext, privacy, friendList, friendsIds, new CreateEventActivity.FriedsIdsListner() {
            @Override
            public void getIds(String ids) {
                friendsIds = ids;
            }
        });


        showFriendList(loadingView,adapter,ly_no_friend_found,recycler_view);
        recycler_view.setAdapter(adapter);
        searchFriend();
        return view;
    }


    private void searchFriend(){

        ed_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<MyFriendListInfo.ListBean> temp_friendList = new ArrayList<>();

                for(MyFriendListInfo.ListBean bean:friendList){
                    if(bean.fullName.toLowerCase().contains(editable.toString().toLowerCase())){
                        temp_friendList.add(bean);
                    }
                }

                InviteFrienAdapter adapter = new InviteFrienAdapter(mContext, privacy, temp_friendList, friendsIds, ids -> friendsIds = ids);
                recycler_view.setAdapter(adapter);
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
        tv_select_background_three.setBackgroundResource(R.drawable.holo_circle_border);
        tv_three.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_two.setVisibility(View.VISIBLE);
        tv_two.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        iv_right_two.setVisibility(View.GONE);

        ThiredFragment  youTubePlayerFragment = (ThiredFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);
        if (youTubePlayerFragment != null)
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();

    }

    private void showFriendList(final InsLoadingView loading_view, final InviteFrienAdapter adapter,
                                final LinearLayout ly_no_friend_found, final RecyclerView recycler_view) {
        loading_view.setVisibility(View.VISIBLE);
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
                        MyFriendListInfo friendListInfo = gson.fromJson(response, MyFriendListInfo.class);

                        switch (eventUserType) {
                            case "1": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("1")) {
                                        friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "2": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("2")) {
                                        friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "3": {
                                friendList.addAll(friendListInfo.List);
                                break;
                            }
                        }

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            ly_no_friend_found.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
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
        service.callGetSimpleVolley("user/getFriendList?offset=0&limit=20&listType=friend&eventId=" + eventId + "");
    }

}
