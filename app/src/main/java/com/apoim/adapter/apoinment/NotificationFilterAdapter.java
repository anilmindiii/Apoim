package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.modal.FilterItemInfo;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 10/4/18.
 */

public class NotificationFilterAdapter extends RecyclerView.Adapter<NotificationFilterAdapter.ViewHolder> {
    Context mContext;
    List<FilterItemInfo> filterItemInfoList;

    public NotificationFilterAdapter(Context mContext, List<FilterItemInfo> filterItemInfoList) {
        this.mContext = mContext;
        this.filterItemInfoList = filterItemInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_filter_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FilterItemInfo filterItemInfo = filterItemInfoList.get(position);

        holder.filter_type.setText(filterItemInfo.filterItemName);
        Glide.with(mContext).load(filterItemInfo.image).into(holder.image);

        if(filterItemInfo.isCheck){
            holder.checkbox_img.setImageResource(R.drawable.ico_not_checked);

        }else {
            holder.checkbox_img.setImageResource(R.drawable.ico_not_unchecked);
        }

    }

    @Override
    public int getItemCount() {
        return filterItemInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image,checkbox_img;
        TextView filter_type;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            image = itemView.findViewById(R.id.image);
            filter_type = itemView.findViewById(R.id.filter_type);
            checkbox_img = itemView.findViewById(R.id.checkbox_img);
        }

        @Override
        public void onClick(View view) {
            if(filterItemInfoList.get(getAdapterPosition()).isCheck){

                filterItemInfoList.get(getAdapterPosition()).isCheck = false;
            }else {
                filterItemInfoList.get(getAdapterPosition()).isCheck = true;
            }

            notifyDataSetChanged();
        }
    }
}
