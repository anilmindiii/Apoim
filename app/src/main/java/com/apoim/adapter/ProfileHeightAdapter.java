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

public class ProfileHeightAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileItemInfo> height_list;
    ProfileImageAdapterListener listener;
    Session session;
    String height;

    public ProfileHeightAdapter(Context context, ArrayList<ProfileItemInfo> height_list, String height, ProfileImageAdapterListener listener) {
        this.context = context;
        this.height_list = height_list;
        this.listener = listener;
        this.height = height;
    }

    @Override
    public int getCount() {
        return height_list.size();
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

        View height_view = view.findViewById(R.id.education_view);
        TextView height_name = view.findViewById(R.id.education_name);
        final ImageView height_check_box = view.findViewById(R.id.education_check_box);

        height_name.setText(height_list.get(i).name);

        for (int k = 0; k < height_list.size(); k++) {
            if (height_list.get(k).name.equals(height)) {
                height_list.get(k).isChecked = true;
            }
        }

        rl_education_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i);

                for (int x = 0; x < height_list.size(); x++) {
                    height_list.get(x).isChecked = false;
                }

                height_list.get(i).isChecked = true;
                notifyDataSetChanged();

            }
        });

        if (height_list.get(i).isChecked) {
            height_check_box.setVisibility(View.VISIBLE);
            height_list.get(i).isChecked = false;
        } else if (!height_list.get(i).isChecked) {
            height_check_box.setVisibility(View.GONE);
        }


        return view;
    }
}
