package com.apoim.adapter.newEvent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.profile.OtherProfileDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.modal.JoinedEventInfo;
import com.apoim.session.Session;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by mindiii on 15/11/18.
 */

public class JoinedMemberChatAdapter extends RecyclerView.Adapter<JoinedMemberChatAdapter.ViewHolder> {
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    private Context mContext;
    String myId;

    public JoinedMemberChatAdapter(ArrayList<JoinedEventInfo.ListBean> joinedList, Context mContext, String myId) {
        this.joinedList = joinedList;
        this.mContext = mContext;
        this.myId = myId;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joined_chat_member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoinedEventInfo.ListBean bean = joinedList.get(position);
        Glide.with(mContext).load(bean.memberImage).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder)).into(holder.iv_profile);
        holder.tv_name.setText(bean.memberName);

        if (joinedList.get(position).status != null)
            if (joinedList.get(position).status.equals(Constant.online)) {
                holder.iv_online.setVisibility(View.VISIBLE);
            } else holder.iv_online.setVisibility(View.GONE);


        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (bean.memberUserId == null) {
                    if (bean.companionUserId.equals(myId)) {
                        return;
                    }

                    Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);
                    intent.putExtra(Constant.userId, bean.companionUserId);
                    mContext.startActivity(intent);
                } else {

                    if (bean.memberUserId.equals(myId)) {
                        return;
                    }

                    Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);
                    intent.putExtra(Constant.userId, joinedList.get(position).memberUserId);
                    mContext.startActivity(intent);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return joinedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile, iv_online;
        TextView tv_name;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_name = itemView.findViewById(R.id.tv_name);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            iv_online = itemView.findViewById(R.id.iv_online);
        }
    }
}
