package com.apoim.adapter.apoinment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.event.EventDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.modal.EventRequestInfo;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.util.Preconditions.checkArgument;

/**cc
 * Created by Anil on 5/4/18.
 */

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.ViewHolder> {
    Context mContext;
    ArrayList<EventRequestInfo.ListBean> eventList;
    String currentDate;

    public EventRequestAdapter(Context mContext, ArrayList<EventRequestInfo.ListBean> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    public void setCurrentDate(String date){
        this.currentDate = date;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_request_layout_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventRequestInfo.ListBean bean = eventList.get(position);

        if(bean.payment.equals("Paid")){
            holder.tv_paid_amount.setVisibility(View.VISIBLE);
            holder.tv_paid_amount.setText((bean.currencySymbol + bean.eventAmount));
            holder.tv_payment_status.setText(bean.payment);
            holder.space.setVisibility(View.VISIBLE);
        }else {
            holder.tv_paid_amount.setVisibility(View.GONE);
            holder.space.setVisibility(View.GONE);
            holder.tv_payment_status.setText(bean.payment);
        }

        Glide.with(mContext).load(bean.eventImage).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(holder.event_img);


        Date date1 = getDateFromString(currentDate);
        Date date2 = getDateFromString(bean.eventEndDate);

        if(date1 != null)
            if(date1.after(date2)){
                holder.event_status.setTextColor(ContextCompat.getColor( holder.event_status.getContext(),R.color.colorPrimary));
            }else if(bean.memberStatus.equals(Constant.Pending_request) || bean.memberStatus.equals(Constant.Joined_Payment_is_pending)){
                holder.event_status.setTextColor(ContextCompat.getColor( holder.event_status.getContext(),R.color.coloryellow));
            }else if(bean.memberStatus.equals(Constant.Confirmed_payment) || bean.memberStatus.equals(Constant.Confirmed)){
                holder.event_status.setTextColor(ContextCompat.getColor( holder.event_status.getContext(),R.color.colorgreen));
            }


        holder.tv_privacy.setText(bean.privacy);
        holder.tv_event_name.setText(bean.eventName);
        holder.tv_address.setText(bean.eventPlace);


        if(!TextUtils.isEmpty(bean.fullName)){
            String[] splited = bean.fullName.split("\\s+");
            String split_one=splited[0];
            holder.tv_event_creater_name.setText(split_one);
        }

        holder.user_status.setText(bean.ownerType);
        if(date1 != null)
            if(date1.after(date2)){
                holder.event_status.setText("Event passed");
            }else {
                Utils.setStatusById(holder.event_status,bean.memberStatus);
            }



        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(holder.profile_image.getContext()).load(bean.profileImage).apply(options).into(holder.profile_image);

        day_time(holder, bean);
    }


    private void day_time(ViewHolder holder, EventRequestInfo.ListBean bean) {
        try {
            String timeLong = bean.eventStartDate;

            SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat formatShortTime = new SimpleDateFormat("hh:mm aa", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("MMM yyyy", Locale.US);
            SimpleDateFormat formatShort1 = new SimpleDateFormat("dd", Locale.US);
            Log.v("timeLong", formatShort1.format(formatLong.parse(timeLong)));

            holder.tv_day.setText(formatShort1.format(formatLong.parse(timeLong)));
            holder.tv_start_date_time.setText(formatShort.format(formatLong.parse(timeLong)));
            holder.tv_th.setText(getDayOfMonthSuffix(Integer.parseInt(formatShort1.format(formatLong.parse(timeLong)))));
            holder.tv_time.setText(formatShortTime.format(formatLong.parse(timeLong)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_event_name,tv_address,tv_payment_status,tv_paid_amount,
                tv_privacy,tv_event_creater_name,tv_start_date_time,tv_day,user_status,event_status,tv_th,tv_time;
        ImageView profile_image,event_img;
        Space space;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_payment_status = itemView.findViewById(R.id.tv_payment_status);
            tv_paid_amount = itemView.findViewById(R.id.tv_paid_amount);
            tv_privacy = itemView.findViewById(R.id.tv_privacy);
            tv_event_creater_name = itemView.findViewById(R.id.tv_event_creater_name);
            profile_image = itemView.findViewById(R.id.profile_image);
            tv_start_date_time = itemView.findViewById(R.id.tv_start_date_time);
            tv_day = itemView.findViewById(R.id.tv_day);
            user_status = itemView.findViewById(R.id.user_status);
            event_status = itemView.findViewById(R.id.event_status);
            tv_th = itemView.findViewById(R.id.tv_th);
            tv_time = itemView.findViewById(R.id.tv_time);
            space = itemView.findViewById(R.id.space);
            event_img = itemView.findViewById(R.id.event_img);

        }

        @Override
        public void onClick(View view) {
          /*  Intent intent = new Intent(mContext, EventDetailsActivity.class);
            intent.putExtra("from","eventRequest");
            intent.putExtra("eventId",eventList.get(getAdapterPosition()).eventId);
            intent.putExtra("ownerType",eventList.get(getAdapterPosition()).ownerType);
            if(eventList.get(getAdapterPosition()).ownerType.equals("Administrator")){
                intent.putExtra("id",eventList.get(getAdapterPosition()).eventMemId);
            }else {
                intent.putExtra("id",eventList.get(getAdapterPosition()).compId);
            }

            mContext.startActivity(intent);*/
        }
    }

    String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    private Date getDateFromString(String startDateString){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        try {
            startDate = df.parse(startDateString);
            String newDateString = df.format(startDate);
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startDate;
    }
}
