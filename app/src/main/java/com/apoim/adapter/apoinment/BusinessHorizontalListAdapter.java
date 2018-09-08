package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.CreateAppointMentActivity;
import com.apoim.listener.PositionListner;
import com.apoim.modal.BusinessListInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mindiii on 22/8/18.
 */

public class BusinessHorizontalListAdapter extends RecyclerView.Adapter<BusinessHorizontalListAdapter.ViewHolder> {
    Context mContext;
    ArrayList<BusinessListInfo.BusinessListBean> businessListBeans;
    PositionListner positionListner;

    public BusinessHorizontalListAdapter(Context mContext,
                                         ArrayList<BusinessListInfo.BusinessListBean> businessList,
                                         PositionListner positionListner) {
        this.mContext = mContext;
        this.businessListBeans = businessList;
        this.positionListner = positionListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_busines_list_apoim_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessListInfo.BusinessListBean bean = businessListBeans.get(position);
        holder.tv_buz_name.setText(bean.businessName+"");
        holder.tv_distance.setText(bean.distance+"");
        Picasso.with(mContext).load(bean.businessImage).into(holder.buz_image);
    }

    @Override
    public int getItemCount() {
        return businessListBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_buz_name,tv_distance;
        ImageView buz_image;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_buz_name = itemView.findViewById(R.id.tv_buz_name);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            buz_image = itemView.findViewById(R.id.buz_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            positionListner.getPosition(getAdapterPosition());
        }
    }
}
