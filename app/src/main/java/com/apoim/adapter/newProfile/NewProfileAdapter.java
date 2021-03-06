package com.apoim.adapter.newProfile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.apoim.R;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.GetOtherProfileInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 8/8/18.
 */

public class NewProfileAdapter extends RecyclerView.Adapter<NewProfileAdapter.ViewHolder> {

    Context mContext;
    ArrayList<GetOtherProfileInfo.UserDetailBean.ImagesBean> imagesBeans;
    List<EventDetailsInfo.EventImage> imagesList;
    GetNewImageClick imageClick;

    public NewProfileAdapter(Context mContext, ArrayList<GetOtherProfileInfo.UserDetailBean.ImagesBean> imagesBeans,
                             GetNewImageClick imageClick) {
        this.mContext = mContext;
        this.imagesBeans = imagesBeans;
        this.imageClick = imageClick;
    }

 public NewProfileAdapter(List<EventDetailsInfo.EventImage> eventImage,Context mContext,GetNewImageClick imageClick) {
        this.mContext = mContext;
        this.imagesList = eventImage;
        this.imageClick = imageClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_profile_image_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       if(imagesBeans == null){

           if(imagesList.get(position).eventImage != null)
               Picasso.with(mContext)
                       .load(imagesList.get(position).eventImage).placeholder(R.drawable.placeholder_chat_image)
                       .into(holder.circular_profile_image);

       }else {
           if(imagesBeans.get(position).image != null)
               Picasso.with(mContext)
                       .load(imagesBeans.get(position).image).placeholder(R.drawable.ico_user_placeholder)
                       .into(holder.circular_profile_image);
       }





    }

    @Override
    public int getItemCount() {
        if(imagesBeans == null){
           return imagesList.size();
        }

        return imagesBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView circular_profile_image;
        public ViewHolder(View itemView) {
            super(itemView);
            circular_profile_image = itemView.findViewById(R.id.circular_profile_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            imageClick.imageClick(getAdapterPosition());
        }
    }
}
