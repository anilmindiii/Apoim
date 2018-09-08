package com.apoim.adapter.imageGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apoim.R;
import com.apoim.listener.GetNewImageClick;
import com.apoim.modal.ProfileImageModel;
import com.github.siyamed.shapeimageview.RoundedImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {
    private ArrayList<ProfileImageModel> imagesList;
    private Context mContext;
    private GetNewImageClick listener;

    public GalleryRecyclerAdapter(ArrayList<ProfileImageModel> imagesList, Context mContext, GetNewImageClick listener) {
        this.imagesList = imagesList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gallery_images_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ProfileImageModel imageModel = imagesList.get(position);

        if (imageModel.isSelected) {
            holder.rl_select_image.setVisibility(View.VISIBLE);
            holder.iv_user_image.setVisibility(View.GONE);
            Picasso.with(mContext).load(imageModel.profileUrl).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_selected_user_image);
        } else {
            holder.rl_select_image.setVisibility(View.GONE);
            holder.iv_user_image.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(imageModel.profileUrl).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_user_image);
        }

        holder.rl_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProfileImageModel model : imagesList) {
                    model.isSelected = false;
                }
                imagesList.get(position).isSelected = true;

                notifyDataSetChanged();

                listener.imageClick(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView iv_user_image, iv_selected_user_image;
        private RelativeLayout rl_select_image, rl_images;

        ViewHolder(View itemView) {
            super(itemView);
            iv_user_image = itemView.findViewById(R.id.iv_user_image);
            iv_selected_user_image = itemView.findViewById(R.id.iv_selected_user_image);
            rl_select_image = itemView.findViewById(R.id.rl_select_image);
            rl_images = itemView.findViewById(R.id.rl_images);

        }

    }
}
