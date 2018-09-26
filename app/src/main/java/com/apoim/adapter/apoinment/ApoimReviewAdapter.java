package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.modal.ApoimReviewInfo;
import com.apoim.util.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Anil on 26/9/18.
 */

public class ApoimReviewAdapter extends RecyclerView.Adapter<ApoimReviewAdapter.ViewHolder> {

    ArrayList<ApoimReviewInfo.ReviewListBean> reviewList;
    Context mContext;
    String currentDate;

    public ApoimReviewAdapter(ArrayList<ApoimReviewInfo.ReviewListBean> reviewList, Context mContext) {
        this.reviewList = reviewList;
        this.mContext = mContext;
    }

    public void agoDate(String currentDate) {
        this.currentDate = currentDate;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apoim_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApoimReviewInfo.ReviewListBean info = reviewList.get(position);

        holder.tv_name.setText(info.fullName);
        holder.tv_comments.setText(info.comment);
        holder.ratingBar.setRating(Float.parseFloat(info.rating));

        holder.tv_date.setText(TimeAgo.toRelative(info.crd, currentDate));

        if (!info.profileImage.equals(""))
            Picasso.with(mContext).load(info.profileImage).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_profile);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_name, tv_comments, tv_date;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_comments = itemView.findViewById(R.id.tv_comments);
            tv_date = itemView.findViewById(R.id.tv_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
