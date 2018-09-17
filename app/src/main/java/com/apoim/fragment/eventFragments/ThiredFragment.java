package com.apoim.fragment.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apoim.R;

/**
 * Created by mindiii on 14/9/18.
 */

public class ThiredFragment extends Fragment {
    private TextView tv_next_thired;
    private Context mContext;

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

        tv_next_thired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }
}
