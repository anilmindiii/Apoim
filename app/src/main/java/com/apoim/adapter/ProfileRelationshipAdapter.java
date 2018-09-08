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
import com.apoim.listener.ProfileRelationListener;
import com.apoim.modal.ProfileItemInfo;
import com.apoim.session.Session;

import java.util.ArrayList;

/**
 * Created by mindiii on 28/2/18.
 */

public class ProfileRelationshipAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileItemInfo> relation_list;
    ProfileRelationListener listener;
    Session session;
    String relation_name;

    public ProfileRelationshipAdapter(Context context, ArrayList<ProfileItemInfo> relation_list, String relation_name, ProfileRelationListener listener) {
        this.context = context;
        this.relation_list = relation_list;
        this.listener = listener;
        this.relation_name = relation_name;
    }

    @Override
    public int getCount() {
        return relation_list.size();
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

        education_name.setText(relation_list.get(i).name);

        for (int k = 0; k < relation_list.size(); k++) {
            if (relation_list.get(k).name.equals(relation_name)) {
                relation_list.get(k).isChecked = true;
            }
        }

        rl_education_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i, i+1);

                for (int x = 0; x < relation_list.size(); x++) {
                    relation_list.get(x).isChecked = false;
                }

                relation_list.get(i).isChecked = true;
                notifyDataSetChanged();
            }
        });

        if (relation_list.get(i).isChecked) {
            education_check_box.setVisibility(View.VISIBLE);
            relation_list.get(i).isChecked = false;
        } else if (!relation_list.get(i).isChecked) {
            education_check_box.setVisibility(View.GONE);
        }

        return view;
    }
}
