package com.apoim.adapter.apoinment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.activity.event.EventDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.listener.DeleteListner;
import com.apoim.modal.MyEventInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.util.Preconditions.checkArgument;

/**
 * Created by mindiii on 5/4/18.
 **/

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MyEventInfo.ListBean> myEventList;
    private DeleteListner listner;
    private String currentDate;

    public MyEventAdapter(Context mContext, String currentDate, ArrayList<MyEventInfo.ListBean> myEventList,
                          DeleteListner listner) {
        this.mContext = mContext;
        this.myEventList = myEventList;
        this.listner = listner;
        this.currentDate = currentDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_event_request_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyEventInfo.ListBean bean = myEventList.get(position);
        if (bean.payment.equals("Paid")) {
            holder.tv_paid_amount.setVisibility(View.VISIBLE);
            holder.tv_paid_amount.setText((bean.currencySymbol + bean.eventAmount));
            holder.tv_payment_status.setText(bean.payment);
        } else {
            holder.tv_paid_amount.setVisibility(View.GONE);
            holder.tv_payment_status.setText(bean.payment);
        }

        holder.tv_privacy.setText(bean.privacy);
        holder.tv_event_name.setText(bean.eventName);
        holder.tv_address.setText(bean.eventPlace);

        day_time(position, holder, bean);

        if (!bean.eventImage.equals(""))
            Picasso.with(mContext).load(bean.eventImage).placeholder(R.drawable.placeholder_chat_image).fit().into(holder.event_img);

        if (!TextUtils.isEmpty(currentDate) && !TextUtils.isEmpty(bean.eventEndDate)) {
            Date date1 = getDateFromString(currentDate);
            Date date2 = getDateFromString(bean.eventEndDate);

            if (date1.after(date2)) {
                //Do Something else
                holder.ly_edit_event.setVisibility(View.GONE);
                //holder.event_status.setVisibility(View.VISIBLE);
            } else {
                holder.ly_edit_event.setVisibility(View.VISIBLE);
                //holder.event_status.setVisibility(View.GONE);
            }
        }


    }

    public void getData(String currentDate) {
        this.currentDate = currentDate;
    }

    private void day_time(final int position, MyEventAdapter.ViewHolder holder, final MyEventInfo.ListBean bean) {
        try {
            String timeLong = bean.eventStartDate;

            SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("MMM yyyy", Locale.US);
            SimpleDateFormat formatShortTime = new SimpleDateFormat("hh:mm aa", Locale.US);
            SimpleDateFormat formatShort1 = new SimpleDateFormat("dd", Locale.US);
            Log.v("timeLong", formatShort1.format(formatLong.parse(timeLong)));

            holder.tv_day.setText(formatShort1.format(formatLong.parse(timeLong)));
            holder.tv_start_date_time.setText(formatShort.format(formatLong.parse(timeLong)));
            holder.tv_time.setText(formatShortTime.format(formatLong.parse(timeLong)));
            holder.tv_th.setText(getDayOfMonthSuffix(Integer.parseInt(formatShort1.format(formatLong.parse(timeLong)))));


            holder.ly_delete_myevent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlertDialog(position, bean.eventId, mContext, mContext.getString(R.string.delete_event_msg));
                }
            });

            holder.ly_edit_event.setOnClickListener(view -> {
                /*Intent intent = new Intent(mContext,CreateEventActivity.class);
                intent.putExtra(Constant.editEvent, Constant.editEvent);
                intent.putExtra("eventId", bean.eventId);
                mContext.startActivity(intent);*/
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void openAlertDialog(final int position, final String eventId, Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listner.deleteItem(eventId, position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return myEventList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_event_name, tv_address, tv_payment_status, tv_paid_amount,
                tv_privacy, tv_start_date_time, tv_day, tv_th, event_status, tv_time;

        LinearLayout ly_edit_event, ly_delete_myevent;
        ImageView event_img;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_payment_status = itemView.findViewById(R.id.tv_payment_status);
            tv_paid_amount = itemView.findViewById(R.id.tv_paid_amount);
            tv_privacy = itemView.findViewById(R.id.tv_privacy);
            tv_start_date_time = itemView.findViewById(R.id.tv_start_date_time);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_th = itemView.findViewById(R.id.tv_th);
            ly_delete_myevent = itemView.findViewById(R.id.ly_delete_myevent);
            ly_edit_event = itemView.findViewById(R.id.ly_edit_event);
            event_status = itemView.findViewById(R.id.event_status);
            event_img = itemView.findViewById(R.id.event_img);
        }

        @Override
        public void onClick(View view) {
           /* Intent intent = new Intent(mContext, EventDetailsActivity.class);
            intent.putExtra("from","myEvent");
            intent.putExtra("eventId",myEventList.get(getAdapterPosition()).eventId);
            mContext.startActivity(intent);*/
        }
    }

    private String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private Date getDateFromString(String startDateString) {
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
