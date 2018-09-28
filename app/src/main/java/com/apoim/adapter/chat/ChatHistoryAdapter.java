package com.apoim.adapter.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.chat.ChatActivity;
import com.apoim.activity.profile.OtherProfileDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.modal.Chat;
import com.apoim.session.Session;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


/**
 * Created by Anil on 14/6/18.
 **/

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    private ArrayList<Chat> historyList;
    private Context mContext;
    String myUid;

    public ChatHistoryAdapter(ArrayList<Chat> historyList, Context mContext) {
        this.historyList = historyList;
        this.mContext = mContext;
        Session session = new Session(mContext);
        myUid = session.getUser().userDetail.userId;
    }

    @Override
    public ChatHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_fevorite_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHistoryAdapter.ViewHolder holder, int position) {
        Chat chat = historyList.get(position);

        holder.iv_favorite.setVisibility(View.GONE);

        if (chat.imageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
            holder.tv_favorite_work.setText("Image");
        } else {
            holder.tv_favorite_work.setText(chat.message);
        }

        holder.tv_favorite_name.setText(chat.name);
        Glide.with(mContext).load(chat.profilePic).apply(new RequestOptions()
                .placeholder(R.drawable.ico_user_placeholder)).into(holder.iv_favorite_image);

        if (chat.readBy != null)
            if (chat.readBy.equals(myUid)) {
                holder.iv_unread_msg.setVisibility(View.VISIBLE);
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                holder.tv_favorite_work.setTypeface(boldTypeface);
            } else {
                holder.iv_unread_msg.setVisibility(View.GONE);
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
                holder.tv_favorite_work.setTypeface(boldTypeface);
            }

        holder.iv_favorite_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);
                intent.putExtra(Constant.userId,historyList.get(position).uid);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_favorite_name;
        TextView tv_favorite_work;
        ImageView iv_favorite_image, iv_favorite, iv_unread_msg;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_favorite_name = itemView.findViewById(R.id.tv_favorite_name);
            tv_favorite_work = itemView.findViewById(R.id.tv_favorite_work);
            iv_favorite_image = itemView.findViewById(R.id.iv_favorite_image);
            iv_favorite = itemView.findViewById(R.id.iv_favorite);
            iv_unread_msg = itemView.findViewById(R.id.iv_unread_msg);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("otherUID", historyList.get(getAdapterPosition()).uid);
            intent.putExtra("titleName", historyList.get(getAdapterPosition()).name);
            intent.putExtra("profilePic", historyList.get(getAdapterPosition()).profilePic);

            mContext.startActivity(intent);
        }
    }
}
