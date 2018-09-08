package com.apoim.adapter.apoinment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.apoim.R;
import com.apoim.listener.ShareListner;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

public class ShareEventJoinAdapter extends RecyclerView.Adapter<ShareEventJoinAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private ShareListner listner;
    private String eventPrivacy;
    private String userId = "";

    public ShareEventJoinAdapter(Context mContext,String eventPrivacy, ArrayList<MyFriendListInfo.ListBean> friendList,ShareListner listner) {
        this.mContext = mContext;
        this.friendList = friendList;
        this.listner = listner;
        this.eventPrivacy = eventPrivacy;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_event_dialog_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyFriendListInfo.ListBean bean = friendList.get(position);
        holder.tv_name.setText(bean.fullName);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(mContext).load(bean.profileImage).apply(options).into(holder.profile_image);

        if(bean.isSelected){
            holder.iv_ischecked_image.setVisibility(View.VISIBLE);
            holder.tv_name.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
        }else{
            holder.tv_name.setTextColor(ContextCompat.getColor(mContext,R.color.colorBlack));
            holder.iv_ischecked_image.setVisibility(View.GONE);
        }

        holder.main_view.setEnabled(false);



        if(eventPrivacy.equals("1")){ // for public case

            if(bean.gender.equals("2")){

                if(bean.eventInvitation.equals("1")){
                    holder.status.setText("Public");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }
                else if(bean.eventInvitation.equals("2")){
                    holder.status.setText("Private");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(false);

                }else if(bean.eventInvitation.equals("3")) {
                    holder.status.setText("Both(Public/Private)");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }

            }else  if(bean.gender.equals("1")){
                if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
                    holder.already_invited.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.GONE);
                    holder.main_view.setEnabled(false);
                }else {
                    holder.already_invited.setVisibility(View.GONE);
                    holder.status.setVisibility(View.GONE);
                    holder.main_view.setEnabled(true);
                }
            }

        }
        else if(eventPrivacy.equals("2")){ // for case private

            if(bean.gender.equals("2")){

                if(bean.eventInvitation.equals("1")){
                    holder.status.setText("Public");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(false);
                }
                else if(bean.eventInvitation.equals("2")){
                    holder.status.setText("Private");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);

                }else if(bean.eventInvitation.equals("3")) {
                    holder.status.setText("Both(Public/Private)");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.main_view.setEnabled(true);
                }

            }else  if(bean.gender.equals("1")){
                if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
                    holder.already_invited.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.GONE);
                    holder.main_view.setEnabled(false);
                }else {
                    holder.already_invited.setVisibility(View.GONE);
                    holder.status.setVisibility(View.GONE);
                    holder.main_view.setEnabled(true);
                }
            }

        }else {
            holder.main_view.setEnabled(true);
            holder.status.setVisibility(View.GONE);
        }

        if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
            holder.already_invited.setVisibility(View.VISIBLE);
            holder.main_view.setEnabled(false);
        }else {
            holder.already_invited.setVisibility(View.GONE);
            holder.main_view.setEnabled(true);
        }


    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profile_image,iv_ischecked_image;
        TextView tv_name,status,already_invited;
        LinearLayout main_view;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            profile_image = itemView.findViewById(R.id.profile_image);
            iv_ischecked_image = itemView.findViewById(R.id.iv_ischecked_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            status = itemView.findViewById(R.id.status);
            main_view = itemView.findViewById(R.id.main_view);
            already_invited = itemView.findViewById(R.id.already_invited);
        }

        @Override
        public void onClick(View view) {
            MyFriendListInfo.ListBean bean = friendList.get(getAdapterPosition());

            if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
                main_view.setEnabled(false);
            }else {

                if(eventPrivacy.equals("1")){ // for public case

                    if(bean.gender.equals("2")){

                        if(bean.eventInvitation.equals("1")){
                            selectItem();
                        }
                        else if(bean.eventInvitation.equals("2")){
                           //holder.main_view.setEnabled(false);

                        }else if(bean.eventInvitation.equals("3")) {
                            selectItem();
                        }

                    }else  if(bean.gender.equals("1")){
                        if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
                            //holder.main_view.setEnabled(false);
                        }else {
                            selectItem();
                        }
                    }

                }
                else if(eventPrivacy.equals("2")){ // for case private

                    if(bean.gender.equals("2")){

                        if(bean.eventInvitation.equals("1")){
                           // holder.main_view.setEnabled(false);
                        }
                        else if(bean.eventInvitation.equals("2")){
                            selectItem();

                        }else if(bean.eventInvitation.equals("3")) {
                            selectItem();
                        }

                    }else if(bean.gender.equals("1")){

                        if(bean.memberStatus.equals("1") || bean.memberStatus.equals("2")){
                            //holder.main_view.setEnabled(false);
                        }else {
                            selectItem();
                        }
                    }

                }

            }





            /*if(friendList.get(getAdapterPosition()).memberStatus.equals("")){

                selectItem();
            }*/
        }

        private void selectItem() {
            String valueUserId = friendList.get(getAdapterPosition()).userId;

            if(friendList.get(getAdapterPosition()).isSelected){

                friendList.get(getAdapterPosition()).isSelected = false;
                if(userId.contains(valueUserId+",")){
                    userId = userId.replace(valueUserId+",","");
                }
                else if(userId.contains(","+valueUserId)){
                    userId = userId.replace(","+valueUserId,"");
                }
                else if(userId.contains(valueUserId)){
                    userId = userId.replace(valueUserId,"");
                }
            }else {
                friendList.get(getAdapterPosition()).isSelected = true;
                userId = valueUserId + "," + userId;
            }

            listner.getEventMemId(userId);
            notifyDataSetChanged();
        }
    }
}
