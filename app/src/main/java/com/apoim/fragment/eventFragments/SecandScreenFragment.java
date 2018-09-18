package com.apoim.fragment.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;

/**
 * Created by mindiii on 14/9/18.
 */

public class SecandScreenFragment extends Fragment {

    private TextView tv_next_secand,tv_select_background_two,tv_two;
    private ImageView iv_right_two;
    private Context mContext;
    private TextView tv_one;
    private ImageView iv_right_one;

    public static SecandScreenFragment newInstance() {
        Bundle args = new Bundle();

        SecandScreenFragment fragment = new SecandScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_secand_screen, container, false);
        tv_next_secand = view.findViewById(R.id.tv_next_secand);

        tv_select_background_two = getActivity().findViewById(R.id.tv_select_background_two);
        tv_two = getActivity().findViewById(R.id.tv_two);
        iv_right_two = getActivity().findViewById(R.id.iv_right_two);
        tv_one = getActivity().findViewById(R.id.tv_one);
        iv_right_one = getActivity().findViewById(R.id.iv_right_one);

        tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_two.setTextColor(ContextCompat.getColor(mContext,R.color.white));

        tv_next_secand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
                iv_right_two.setVisibility(View.VISIBLE);
                tv_two.setVisibility(View.GONE);

                addFragment(new ThiredFragment(), true, R.id.event_fragment_place);
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
        tv_select_background_two.setBackgroundResource(R.drawable.holo_circle_border);
        tv_two.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        iv_right_one.setVisibility(View.GONE);
        tv_one.setVisibility(View.VISIBLE);

        SecandScreenFragment  youTubePlayerFragment = (SecandScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);

        if (youTubePlayerFragment != null)
        {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }


    }
}
