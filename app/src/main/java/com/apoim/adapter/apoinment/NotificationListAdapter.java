package com.apoim.adapter.apoinment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.appointment.AppointmentDirectionActivity;
import com.apoim.activity.event.EventDetailsActivity;
import com.apoim.activity.MainActivity;
import com.apoim.activity.profile.OtherProfileDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.modal.NotificationInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mindiii on 27/3/18.
 **/

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    Context mContext;
    Activity activity;
    ArrayList<NotificationInfo.NotificationListBean> notificationList;
    ArrayList<String> alist;
    private Session session;
    private String myUserId = "";


    public NotificationListAdapter(Activity activity,Context mContext, ArrayList<NotificationInfo.NotificationListBean> notificationList) {
        this.mContext = mContext;
        this.activity = activity;
        this.notificationList  = notificationList;
        alist = new ArrayList<>();
        session = new Session(activity,activity);
        myUserId = session.getUser().userDetail.userId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationInfo.NotificationListBean notificationInfo = notificationList.get(position);

        if( notificationInfo.fullName != null){
            int lengthOfName = notificationInfo.fullName.length();

            if(notificationInfo.message.body.contains(notificationInfo.fullName)){
                notificationInfo.message.body = notificationInfo.message.body.replace(notificationInfo.fullName,"");
            }

            String sourceString = "<b>" +  notificationInfo.fullName + "</b> " + notificationInfo.message.body;
            //holder.tv_status.setText();
            Spannable WordtoSpan = new SpannableString(Html.fromHtml(sourceString));
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, lengthOfName, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_status.setText(WordtoSpan);
        }else {
            holder.tv_status.setText(notificationInfo.message.body);
        }




        String date_before =notificationInfo.crd ;
        String date_after = formateDateFromstring("yyyy-MM-dd hh:mm:ss",  "yyyy-MM-dd", date_before);
        try {

            holder.time_ago.setText(date_after);

        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(holder.profile_image.getContext()).load(notificationInfo.image).apply(requestOptions).into(holder.profile_image);

        if(notificationInfo.isRead.equals("1")){
            holder.iv_isRead.setVisibility(View.GONE);

        }else if(notificationInfo.isRead.equals("0")){
            holder.iv_isRead.setVisibility(View.VISIBLE);
        }

        if(notificationInfo.message.type.equals("delete_appointment")){
            alist.add(notificationInfo.message.referenceId);
        }

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);
                intent.putExtra(Constant.userId,notificationList.get(position).notificationBy);
                mContext.startActivity(intent);
            }
        });
    }

    public String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //Log(TAG, "ParseException - dateFormat");
        }
        return outputDate;

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_status,time_ago;
        ImageView profile_image,iv_isRead;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_status = itemView.findViewById(R.id.tv_status);
            time_ago = itemView.findViewById(R.id.time_ago);
            profile_image = itemView.findViewById(R.id.profile_image);
            iv_isRead = itemView.findViewById(R.id.iv_isRead);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String eventMemId = notificationList.get(getAdapterPosition()).message.eventMemId;
            String compId = notificationList.get(getAdapterPosition()).message.compId;
            String reference_id = notificationList.get(getAdapterPosition()).message.referenceId;
            String type = notificationList.get(getAdapterPosition()).message.type;

            if(type.equals("finish_appointment") || type.equals("confirmed_appointment")
                    || type.equals("review_appointment")  || type.equals("apply_counter")
                    || type.equals("appointment_payment") || type.equals("update_counter")){

                if(alist.contains(reference_id)){
                    Utils.openAlertDialog(mContext,"This appointment does not exists");
                }else {
                    Intent intent = new Intent(mContext, AppointmentDirectionActivity.class);
                    intent.putExtra(Constant.appointment_details,reference_id);
                    mContext.startActivity(intent);
                }

            }else {
                if(type.equals("friend_request") || type.equals("accept_request") || type.equals("add_like") || type.equals("add_favorite")){
                    Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);
                    intent.putExtra("userId",reference_id);
                    mContext.startActivity(intent);
                }
                else if(type.equals("create_appointment") || type.equals("delete_appointment")){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    intent.putExtra("type",type);
                    intent.putExtra("reference_id",reference_id);
                    mContext.startActivity(intent);
                    activity.finish();

                }
                else if(type.equals("confirmed_appointment") || type.equals("finish_appointment")){
                    Intent intent = new Intent(mContext, AppointmentDirectionActivity.class);
                    intent.putExtra(Constant.appointment_details,reference_id);
                    mContext.startActivity(intent);
                }
                else if(type.equals("create_event") || type.equals("companion_payment")||
                        type.equals("join_event")|| type.equals("event_payment")||
                        type.equals("share_event")|| type.equals("companion_accept")||
                        type.equals("companion_reject")){

                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("eventId",reference_id);
                    if(myUserId.equals(notificationList.get(getAdapterPosition()).message.createrId)){
                        intent.putExtra("from","myEvent");
                    }else {
                        intent.putExtra("from","eventRequest");
                    }

                    if(eventMemId.equals("")){
                        intent.putExtra("id",compId);
                        intent.putExtra("ownerType","Shared Event");
                    }else if(compId.equals("")){
                        intent.putExtra("id",eventMemId);
                        intent.putExtra("ownerType","Administrator");
                    }

                    mContext.startActivity(intent);
                }
            }
        }

    }
}
