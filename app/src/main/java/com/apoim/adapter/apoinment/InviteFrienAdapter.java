package com.apoim.adapter.apoinment;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.modal.AllUserForEventInfo;
import com.apoim.modal.MyFriendListInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by Anil on 4/4/18.
 **/

public class InviteFrienAdapter extends RecyclerView.Adapter<InviteFrienAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AllUserForEventInfo.DataBean.UserBean> friendList;
    private CreateEventActivity.FriedsIdsListner friedsIdsListner;
    private String privacy = ""; // 1 for public , 2 for private
    private String eventOrganizer = ""; // 1 for public , 2 for private

    public InviteFrienAdapter(Context mContext,String privacy
            ,ArrayList<AllUserForEventInfo.DataBean.UserBean> friendList,String friendsIds,
                              CreateEventActivity.FriedsIdsListner friedsIdsListner,String eventOrganizer) {

        this.mContext = mContext;
        this.friendList = friendList;
        this.friedsIdsListner = friedsIdsListner;
        this.privacy = privacy;
        this.eventOrganizer = eventOrganizer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friends_layout_item,parent,false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AllUserForEventInfo.DataBean.UserBean bean = friendList.get(position);

  /*      if(bean.userId.equals(eventOrganizer)){
            holder.itemView.setVisibility(View.GONE);
        }else */

        holder.itemView.setVisibility(View.VISIBLE);

            if(bean.isSelected){
                holder.item_check.setImageResource(R.drawable.check_item);
            }else {
                holder.item_check.setImageResource(R.drawable.uncheck_item);
            }

            holder.ratingBar.setRating(Float.parseFloat(bean.total_rating));

            holder.main_view.setOnClickListener(view -> {

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
            });

            holder.tv_name.setText(bean.fullName);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ico_user_placeholder);
            Glide.with(mContext).load(bean.profileImage).apply(options).into(holder.profile_image);


            if(bean.memberStatus.equals("1") || bean.userId.equals(eventOrganizer)){
                holder.already_invited.setVisibility(View.VISIBLE);
                holder.main_view.setEnabled(false);
            }else {
                holder.already_invited.setVisibility(View.GONE);
                holder.main_view.setEnabled(true);
            }
        //}





    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profile_image,item_check;
        TextView tv_name,already_invited;
        LinearLayout main_view;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
            profile_image = itemView.findViewById(R.id.profile_image);
            item_check = itemView.findViewById(R.id.item_check);
            main_view = itemView.findViewById(R.id.main_view);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            already_invited = itemView.findViewById(R.id.already_invited);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
