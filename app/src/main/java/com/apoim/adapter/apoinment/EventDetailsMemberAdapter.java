package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apoim.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by mindiii on 10/4/18.
 */

public class EventDetailsMemberAdapter extends RecyclerView.Adapter<EventDetailsMemberAdapter.ViewHolder> {
    Context mContext;
    ArrayList<String> invitedMemberList;
    int listType ;

    public EventDetailsMemberAdapter(int listType, Context context, ArrayList<String> invitedMemberList) {
        this.mContext = context;
        this.invitedMemberList = invitedMemberList;
        this.listType = listType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(listType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.companion_member_layout,parent,false);
            return new ViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_member_item,parent,false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(holder.profile_image.getContext()).load(invitedMemberList.get(position)).apply(options).into(holder.profile_image);

    }

    @Override
    public int getItemCount() {
        return invitedMemberList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        public ViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
