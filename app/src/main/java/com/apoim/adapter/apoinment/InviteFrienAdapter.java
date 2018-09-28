package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.modal.MyFriendListInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by Anil on 4/4/18.
 **/

public class InviteFrienAdapter extends RecyclerView.Adapter<InviteFrienAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private CreateEventActivity.FriedsIdsListner friedsIdsListner;
    private String privacy = ""; // 1 for public , 2 for private

    public InviteFrienAdapter(Context mContext,String privacy
            ,ArrayList<MyFriendListInfo.ListBean> friendList,String friendsIds,
                              CreateEventActivity.FriedsIdsListner friedsIdsListner) {

        this.mContext = mContext;
        this.friendList = friendList;
        this.friedsIdsListner = friedsIdsListner;
        this.privacy = privacy;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friends_layout_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MyFriendListInfo.ListBean bean = friendList.get(position);
        if(bean.isSelected){
            holder.item_check.setImageResource(R.drawable.ico_not_checked);
        }else {
            holder.item_check.setImageResource(R.drawable.ico_not_unchecked);
        }

        holder.main_view.setEnabled(false);

        if(privacy.equals("1")){ // for public case

            if(bean.gender.equals("2")){

                if(bean.eventInvitation.equals("1")){
                    holder.status.setText("Public");
                    holder.item_check.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }
                else if(bean.eventInvitation.equals("2")){
                    holder.status.setText("Private");
                    holder.item_check.setVisibility(View.GONE);
                    holder.status.setVisibility(View.VISIBLE);

                }else if(bean.eventInvitation.equals("3")) {
                    holder.status.setText("Both (Public/Private)");
                    holder.item_check.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }

            }else if(bean.gender.equals("1")){
                holder.item_check.setVisibility(View.VISIBLE);
                holder.main_view.setEnabled(true);
                holder.status.setVisibility(View.GONE);
            }


        }
        else if(privacy.equals("2")){ // for case private

            if(bean.gender.equals("2")){

                if(bean.eventInvitation.equals("1")){
                    holder.status.setText("Public");
                    holder.item_check.setVisibility(View.GONE);
                    holder.status.setVisibility(View.VISIBLE);
                }
                else if(bean.eventInvitation.equals("2")){
                    holder.status.setText("Private");
                    holder.item_check.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);

                }else if(bean.eventInvitation.equals("3")) {
                    holder.status.setText("Both (Public/Private)");
                    holder.item_check.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }

            }else  if(bean.gender.equals("1")){
                holder.item_check.setVisibility(View.VISIBLE);
                holder.main_view.setEnabled(true);
                holder.status.setVisibility(View.GONE);
            }

        }else {
            holder.item_check.setVisibility(View.VISIBLE);
            holder.main_view.setEnabled(true);
            holder.status.setVisibility(View.GONE);
        }

/*....................................................................................*/


        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String friendIdFromList = friendList.get(position).userId; // here we are using userId for understanding given name friendIds only
                if(friendList.get(position).isSelected)
                {
                    friendList.get(position).isSelected = false;
                    if(friendsIds.contains(friendIdFromList + ",")){
                        friendsIds = friendsIds.replace(friendIdFromList + ",","");
                    }
                    else if(friendsIds.contains("," + friendIdFromList)) {
                        friendsIds = friendsIds.replace("," + friendIdFromList,"");
                    }
                    else if(friendsIds.contains(friendIdFromList)){
                        friendsIds = friendsIds.replace(friendIdFromList,"");
                    }
                }else {
                    if(friendsIds.length() == 0){
                        friendsIds = friendIdFromList + friendsIds;
                    }else {
                        friendsIds = friendIdFromList + "," + friendsIds;
                    }

                    friendList.get(position).isSelected = true;
                }

                friedsIdsListner.getIds(friendsIds);
                notifyDataSetChanged();
            }
        });

        holder.tv_name.setText(bean.fullName);
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(mContext).load(bean.profileImage).apply(options).into(holder.profile_image);

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profile_image,item_check;
        TextView tv_name,status;
        LinearLayout main_view;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
            profile_image = itemView.findViewById(R.id.profile_image);
            item_check = itemView.findViewById(R.id.item_check);
            status = itemView.findViewById(R.id.status);
            main_view = itemView.findViewById(R.id.main_view);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
