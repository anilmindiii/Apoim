package com.apoim.fragment.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;

/**
 * Created by mindiii on 11/9/18.
 */

public class FirstScreenFragment extends Fragment {
    private TextView tv_next_first,tv_one;
    private ImageView iv_right_one;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_first_screen, container, false);

        tv_next_first = view.findViewById(R.id.tv_next_first);
        tv_one = getActivity().findViewById(R.id.tv_one);
        iv_right_one = getActivity().findViewById(R.id.iv_right_one);

        tv_one.setVisibility(View.VISIBLE);
        iv_right_one.setVisibility(View.GONE);

        tv_next_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_one.setVisibility(View.GONE);
                iv_right_one.setVisibility(View.VISIBLE);
                addFragment(new SecandScreenFragment(), true, R.id.event_fragment_place);
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
        FirstScreenFragment  youTubePlayerFragment = (FirstScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);

        if (youTubePlayerFragment != null)
        {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }


    }
}
