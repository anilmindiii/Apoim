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
import com.apoim.modal.ProfileItemInfo;
import com.apoim.session.Session;

import java.util.ArrayList;

/**
 * Created by mindiii on 27/2/18.
 */

public class ProfileEducationAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileItemInfo> educationList;
    ProfileImageAdapterListener listener;
    Session session;
    String education;

    public ProfileEducationAdapter(Context context, ArrayList<ProfileItemInfo> educationList, String education, ProfileImageAdapterListener listener) {
        this.context = context;
        this.educationList = educationList;
        this.listener = listener;
        this.education = education;
    }

    @Override
    public int getCount() {
        return educationList.size();
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

        RelativeLayout rl_education_adapter = view.findViewById(R.id.rl_education_adapter);

        View education_view = view.findViewById(R.id.education_view);
        TextView education_name = view.findViewById(R.id.education_name);
        final ImageView education_check_box = view.findViewById(R.id.education_check_box);

        education_name.setText(educationList.get(i).name);

        for (int k = 0; k < educationList.size(); k++) {
            if (educationList.get(k).name.equals(education)) {
                educationList.get(k).isChecked = true;
            }
        }


        rl_education_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i);

                for (int x = 0; x < educationList.size(); x++) {
                    educationList.get(x).isChecked = false;
                }

                educationList.get(i).isChecked = true;
                notifyDataSetChanged();

            }
        });

        if (educationList.get(i).isChecked) {
            education_check_box.setVisibility(View.VISIBLE);
            educationList.get(i).isChecked = false;
        } else if (!educationList.get(i).isChecked) {
            education_check_box.setVisibility(View.GONE);
        }


        return view;
    }
}
