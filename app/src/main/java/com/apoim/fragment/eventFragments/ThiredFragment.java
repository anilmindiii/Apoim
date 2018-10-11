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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.adapter.newEvent.InviteMemberEventAdapter;

/**
 * Created by mindiii on 14/9/18.
 */

public class ThiredFragment extends Fragment {
    private TextView tv_next_thired,tv_select_background_three,tv_three;
    private ImageView iv_right_three;
    private Context mContext;
    private InviteMemberEventAdapter adapter;
    private RecyclerView recycler_view;

    private TextView tv_select_background_two,tv_two;
    private ImageView iv_right_two;

    public static ThiredFragment newInstance() {

        Bundle args = new Bundle();

        ThiredFragment fragment = new ThiredFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_thired_screen, container, false);
        tv_next_thired = view.findViewById(R.id.tv_next_thired);

        tv_select_background_three = getActivity().findViewById(R.id.tv_select_background_three);
        tv_three = getActivity().findViewById(R.id.tv_three);
        iv_right_three = getActivity().findViewById(R.id.iv_right_three);
        tv_select_background_two = getActivity().findViewById(R.id.tv_select_background_two);
        tv_two = getActivity().findViewById(R.id.tv_two);
        iv_right_two = getActivity().findViewById(R.id.iv_right_two);

        recycler_view = view.findViewById(R.id.recycler_view);

        tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_three.setTextColor(ContextCompat.getColor(mContext,R.color.white));

        adapter = new InviteMemberEventAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recycler_view.setLayoutManager(manager);
        recycler_view.setAdapter(adapter);


        tv_next_thired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
                iv_right_three.setVisibility(View.VISIBLE);
                tv_three.setVisibility(View.GONE);
                addFragment(new FourthScreenFragment(), true, R.id.event_fragment_place);
            }
        });

        return view;
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
        {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }


    }
}
