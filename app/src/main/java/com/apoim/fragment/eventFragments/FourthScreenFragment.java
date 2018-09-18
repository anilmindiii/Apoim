package com.apoim.fragment.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class FourthScreenFragment extends Fragment {
    private TextView tv_select_background_four,tv_four;
    private Context mContext;

    private TextView tv_select_background_three,tv_three;
    private ImageView iv_right_three;

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

        tv_select_background_four = getActivity().findViewById(R.id.tv_select_background_four);
        tv_four = getActivity().findViewById(R.id.tv_four);
        tv_select_background_three = getActivity().findViewById(R.id.tv_select_background_three);
        tv_three = getActivity().findViewById(R.id.tv_three);
        iv_right_three = getActivity().findViewById(R.id.iv_right_three);



        tv_select_background_four.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_four.setTextColor(ContextCompat.getColor(mContext,R.color.white));


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
}
