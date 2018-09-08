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

import java.util.ArrayList;

/**
 * Created by mindiii on 28/2/18.
 */

public class ProfileISpeakAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProfileItemInfo> I_speak_list;
    ProfileRelationListener listener;
    String I_speak_name;

    public ProfileISpeakAdapter(Context context, ArrayList<ProfileItemInfo> i_speak_list, String I_speak_name, ProfileRelationListener listener) {
        this.context = context;
        I_speak_list = i_speak_list;
        this.listener = listener;
        this.I_speak_name = I_speak_name;
    }

    @Override
    public int getCount() {
        return I_speak_list.size();
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

        education_name.setText(I_speak_list.get(i).name);

       /* for (int k = 0; k < I_speak_list.size(); k++) {
            if (I_speak_list.get(k).name.equals(I_speak_name)) {
                I_speak_list.get(k).isChecked = true;
            }
        }*/


        if (I_speak_list.get(i).isChecked) {
            education_check_box.setVisibility(View.VISIBLE);
        } else if (!I_speak_list.get(i).isChecked) {
            education_check_box.setVisibility(View.GONE);
        }

        rl_education_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getPosition(i,i+1);

               /* for (int x = 0; x < I_speak_list.size(); x++) {
                    I_speak_list.get(x).isChecked = false;
                }*/

                if (I_speak_list.get(i).isChecked) {
                    I_speak_list.get(i).isChecked = false;
                } else if (!I_speak_list.get(i).isChecked) {
                    I_speak_list.get(i).isChecked = true;
                }

               // I_speak_list.get(i).isChecked = true;
                notifyDataSetChanged();
            }
        });



        return view;
    }
}
