package com.apoim.adapter;

import android.app.Activity;
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

public class ProfileWorkAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileItemInfo> workList;
    ProfileImageAdapterListener listener;
    Session session;
    String work_name;

    public ProfileWorkAdapter(Context context, ArrayList<ProfileItemInfo> workList, String work_name, ProfileImageAdapterListener listener) {
        this.context = context;
        this.workList = workList;
        this.work_name = work_name;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return workList.size();
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

        session = new Session(context.getApplicationContext(), (Activity) context);

        RelativeLayout rl_education_adapter = view.findViewById(R.id.rl_education_adapter);

        View education_view = view.findViewById(R.id.education_view);
        TextView education_name = view.findViewById(R.id.education_name);
        final ImageView education_check_box = view.findViewById(R.id.education_check_box);

        education_name.setText(workList.get(i).name);

        for (int k = 0; k < workList.size(); k++) {
            if (workList.get(k).name.equals(work_name)) {
                workList.get(k).isChecked = true;
            }
        }

        rl_education_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i);

                for (int x = 0; x < workList.size(); x++) {
                    workList.get(x).isChecked = false;
                }

                workList.get(i).isChecked = true;
                notifyDataSetChanged();

            }
        });

        if (workList.get(i).isChecked) {
            education_check_box.setVisibility(View.VISIBLE);
            workList.get(i).isChecked = false;
        } else if (!workList.get(i).isChecked) {
            education_check_box.setVisibility(View.GONE);
        }


        return view;
    }
}
