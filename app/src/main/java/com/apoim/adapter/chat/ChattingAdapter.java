package com.apoim.adapter.chat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.listener.GetDateStatus;
import com.apoim.modal.Chat;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.apoim.util.Utils.formateDateFromstring;

/**
 * Created by Anil on 13/6/18.
 **/

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int VIEW_TYPE_ME = 1;
    private int VIEW_TYPE_OTHER = 2;

    Context context;
    ArrayList<Chat> chatList;
    String myUid;
    GetDateStatus getDateStatus;
    String isDateChange = "";

    public ChattingAdapter(Context context, ArrayList<Chat> chatList, String myId, GetDateStatus getDateStatus) {
        this.context = context;
        this.chatList = chatList;
        this.myUid = myId;
        this.getDateStatus = getDateStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == VIEW_TYPE_ME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_side_view, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_OTHER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left_side_view, parent, false);
            return new OtherViewHolder(view);
        }

        return new OtherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        int pos = position - 1;
        int tempPos = (pos == -1) ? pos + 1 : pos;

        if (TextUtils.equals(chat.uid, myUid)) {
            ((MyViewHolder) holder).myBindData(chat, tempPos);
        } else {
            ((OtherViewHolder) holder).otherBindData(chat, tempPos);
        }


    }


    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(chatList.get(position).uid, myUid)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView my_message, my_date_time_;
        RelativeLayout ly_my_image_view;
        ImageView iv_my_side_img, iv_msg_tick;
        TextView tv_days_status;
        ProgressBar my_progress;

        public MyViewHolder(View itemView) {
            super(itemView);
            my_message = itemView.findViewById(R.id.my_message);
            ly_my_image_view = itemView.findViewById(R.id.ly_my_image_view);
            iv_my_side_img = itemView.findViewById(R.id.iv_my_side_img);
            my_date_time_ = itemView.findViewById(R.id.my_date_time_);
            tv_days_status = itemView.findViewById(R.id.tv_days_status);
            iv_msg_tick = itemView.findViewById(R.id.iv_msg_tick);
            my_progress = itemView.findViewById(R.id.my_progress);

        }

        void myBindData(final Chat chat, int tempPos) {
            if (chat.image == 1) {

                ly_my_image_view.setVisibility(View.VISIBLE);
                my_message.setVisibility(View.GONE);

                my_progress.setVisibility(View.VISIBLE);

                Glide.with(context).load(chat.imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        my_progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        my_progress.setVisibility(View.GONE);
                        return false;
                    }
                }).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(iv_my_side_img);


            } else {
                ly_my_image_view.setVisibility(View.GONE);
                my_message.setVisibility(View.VISIBLE);
                my_message.setText(chat.message);
            }


            if (chat.isMsgReadTick == 1) {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_received);
            } else if (chat.isMsgReadTick == 2) {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_read);
            } else {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_sent);
            }


            SimpleDateFormat sd = new SimpleDateFormat("hh:mm a");
            try {
                String date = sd.format(new Date((Long) chat.timestamp));
                my_date_time_.setText(date);

            } catch (Exception e) {

            }

            iv_my_side_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    full_screen_photo_dialog(chat.imageUrl);
                }
            });
            getDateStatus.currentDateStatus(chat.timestamp);

            /* SimpleDateFormat sd1 = new  SimpleDateFormat("dd MMMM yyyy");
             try {
                 String date1 = sd1.format(new Date((Long) chat.timestamp));
                 isTodaysDate(date1,tv_days_status);
             }catch (Exception e){

             }
*/

            if (!chat.banner_date.equals(chatList.get(tempPos).banner_date)) {
                tv_days_status.setText(chat.banner_date);
                tv_days_status.setVisibility(View.VISIBLE);
            } else {
                tv_days_status.setVisibility(View.GONE);
            }
        }

        public void full_screen_photo_dialog(String image_url) {
            final Dialog openDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            openDialog.setContentView(R.layout.full_image_view_dialog);
            ImageView iv_back = openDialog.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog.dismiss();
                }
            });

            PhotoView photoView = openDialog.findViewById(R.id.photo_view);
            if (!image_url.equals("")) {
                Glide.with(context).load(image_url).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(photoView);
            }
            openDialog.show();

        }
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView other_message, other_date_time_;
        RelativeLayout ly_other_image_view;
        ImageView iv_other_side_img;
        TextView tv_days_status;
        ProgressBar other_progress;

        public OtherViewHolder(View itemView) {
            super(itemView);
            other_message = itemView.findViewById(R.id.other_message);
            ly_other_image_view = itemView.findViewById(R.id.ly_other_image_view);
            iv_other_side_img = itemView.findViewById(R.id.iv_other_side_img);
            other_date_time_ = itemView.findViewById(R.id.other_date_time_);
            tv_days_status = itemView.findViewById(R.id.tv_days_status);
            other_progress = itemView.findViewById(R.id.other_progress);
        }

        public void otherBindData(final Chat chat, int tempPos) {

            if (chat.image == 1) {

                ly_other_image_view.setVisibility(View.VISIBLE);
                other_message.setVisibility(View.GONE);

                other_progress.setVisibility(View.VISIBLE);
                Glide.with(context).load(chat.imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        other_progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        other_progress.setVisibility(View.GONE);
                        return false;
                    }
                }).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(iv_other_side_img);
            } else {
                ly_other_image_view.setVisibility(View.GONE);
                other_message.setVisibility(View.VISIBLE);
                other_message.setText(chat.message);
            }

            SimpleDateFormat sd = new SimpleDateFormat("hh:mm a");
            try {
                String date = sd.format(new Date((Long) chat.timestamp));
                other_date_time_.setText(date);

            } catch (Exception ignored) {

            }

            iv_other_side_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    full_screen_photo_dialog(chat.imageUrl);
                }
            });

            getDateStatus.currentDateStatus(chat.timestamp);

            /*SimpleDateFormat sd1 = new  SimpleDateFormat("dd MMMM yyyy");
            try {
                String date1 = sd1.format(new Date((Long) chat.timestamp));
                isTodaysDate(date1,tv_days_status);

            }catch (Exception e){

            }*/


            if (!chat.banner_date.equals(chatList.get(tempPos).banner_date)) {
                tv_days_status.setText(chat.banner_date);
                tv_days_status.setVisibility(View.VISIBLE);
            } else {
                tv_days_status.setVisibility(View.GONE);
            }
        }

        public void full_screen_photo_dialog(String image_url) {
            final Dialog openDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            openDialog.setContentView(R.layout.full_image_view_dialog);
            ImageView iv_back = openDialog.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog.dismiss();
                }
            });

            PhotoView photoView = openDialog.findViewById(R.id.photo_view);
            if (!image_url.equals("")) {
                Glide.with(context).load(image_url).apply(new RequestOptions().placeholder(R.drawable.placeholder_chat_image)).into(photoView);

            }
            openDialog.show();

        }
    }

}
