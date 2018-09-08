package com.apoim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.listener.ProfileImageAdapterListener;
import com.apoim.modal.ProfileInterestInfo;

import java.util.ArrayList;

/**
 * Created by mindiii on 26/2/18.
 */

public class AddInterestAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileInterestInfo> interestList;
    ProfileImageAdapterListener listener;
    ArrayList<ProfileInterestInfo> interest;

    public AddInterestAdapter(Context context, ArrayList<ProfileInterestInfo> interestList, ArrayList<ProfileInterestInfo> interest_name, ProfileImageAdapterListener listener) {
        this.context = context;
        this.interestList = interestList;
        this.listener = listener;
        this.interest = interest_name;
    }

    @Override
    public int getCount() {
        return interestList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_education_list_adapter, viewGroup, false);

        RelativeLayout rl_select_interest = view.findViewById(R.id.rl_education_adapter);

       // View interest_view = view.findViewById(R.id.interest_view);
        TextView interest_name = view.findViewById(R.id.education_name);
        final ImageView interest_check_box = view.findViewById(R.id.education_check_box);

        interest_name.setText(interestList.get(i).interest.toString().trim());

        for (int k = 0; k < interestList.size(); k++) {
            for (int j = 0; j < interest.size(); j++) {
                if (interestList.get(k).interest.equals(interest.get(j).interest)) {
                    interestList.get(k).isChecked = true;
                }
            }
        }

        rl_select_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i);

                for (int x = 0; x < interestList.size(); x++) {
                    interestList.get(x).isChecked = false;
                }

                interestList.get(i).isChecked = true;
                notifyDataSetChanged();
            }
        });

        if (interestList.get(i).isChecked) {
            interest_check_box.setVisibility(View.VISIBLE);
            rl_select_interest.setEnabled(false);
            interestList.get(i).isChecked = false;
        } else if (!interestList.get(i).isChecked) {
            interest_check_box.setVisibility(View.GONE);
        }

        return view;
    }
}
