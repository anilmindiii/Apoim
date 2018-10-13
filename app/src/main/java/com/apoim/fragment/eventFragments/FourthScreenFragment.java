package com.apoim.fragment.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.app.Apoim;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.ImageBean;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by mindiii on 14/9/18.
 */

public class FourthScreenFragment extends Fragment {
    private TextView tv_select_background_four,tv_four;
    private Context mContext;

    private TextView tv_select_background_three,tv_three;
    private ImageView iv_right_three;
    private InsLoadingView loadingView;
    private Session session;
    RecyclerView event_horizontal_recycler;
    private List<ImageBean> imageBeans;

    public static FourthScreenFragment newInstance() {

        Bundle args = new Bundle();

        FourthScreenFragment fragment = new FourthScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_fourth_screen, container, false);

        session = new Session(mContext);
        tv_select_background_four = getActivity().findViewById(R.id.tv_select_background_four);
        tv_four = getActivity().findViewById(R.id.tv_four);
        tv_select_background_three = getActivity().findViewById(R.id.tv_select_background_three);
        tv_three = getActivity().findViewById(R.id.tv_three);
        iv_right_three = getActivity().findViewById(R.id.iv_right_three);
        loadingView = view.findViewById(R.id.loadingView);

        tv_select_background_four.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_four.setTextColor(ContextCompat.getColor(mContext,R.color.white));

        imageBeans = new ArrayList<>();
        imageBeans.add(0, null);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tv_select_background_four.setBackgroundResource(R.drawable.holo_circle_border);
        tv_four.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_three.setVisibility(View.VISIBLE);
        tv_three.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        iv_right_three.setVisibility(View.GONE);

        FourthScreenFragment  youTubePlayerFragment = (FourthScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);
        if (youTubePlayerFragment != null)
        {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }
    }

    private void createEvent() {
        loadingView.setVisibility(View.VISIBLE);
        EventDetailsInfo.DetailBean bean = session.getcreateEventInfo();


        Map<String, String> map = new HashMap<>();
        map.put("eventName", bean.eventName);
        map.put("eventStartDate", bean.eventStartDate);
        map.put("eventEndDate", bean.eventEndDate);
        map.put("eventPlace", bean.eventPlace);
        map.put("eventLatitude", bean.eventLatitude);
        map.put("eventLongitude", bean.eventLongitude);
        map.put("privacy", bean.privacy);
        map.put("payment", bean.payment);
        map.put("userLimit", bean.userLimit);
        map.put("eventUserType", bean.eventUserType);
        map.put("inviteFriendId", friendsIds);
        map.put("eventAmount", bean.eventAmount);
        map.put("currencySymbol", bean.currencySymbol);
        map.put("currencyCode", bean.currencyCode);
        map.put("groupChat", bean.groupChat);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        //eventCreatedDialog(mContext);

                    } else if (status.equals("fail")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("event/createEvent", map);

    }
}
