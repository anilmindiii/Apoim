package com.apoim.adapter.newEvent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.modal.EventFilterInfo;

import java.util.List;

import static com.apoim.activity.event.CreateEventActivity.RattingIds;
import static com.apoim.activity.event.CreateEventActivity.RattingIds;
import static com.apoim.activity.event.CreateEventActivity.RattingIds;

/**
 * Created by mindiii on 17/10/18.
 */

public class PopularityAdapter extends RecyclerView.Adapter<PopularityAdapter.ViewHolder> {

    List<EventFilterInfo> list;
    Context mContext;
    private CreateEventActivity.FriedsIdsListner friedsIdsListner;


    public PopularityAdapter(List<EventFilterInfo> list, Context mContext,CreateEventActivity.FriedsIdsListner friedsIdsListner) {
        this.list = list;
        this.mContext = mContext;
        this.friedsIdsListner = friedsIdsListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popularity_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.ratting_bar.getLayoutParams());


        if(position == 0){
            holder.ratting_bar.setNumStars(5);
            lp.setMargins(0, 0, 0, 0);
            holder.ratting_bar.setLayoutParams(lp);
        }

        if(position == 1){
            holder.ratting_bar.setNumStars(4);
            lp.setMargins(15, 0, 0, 0);
            holder.ratting_bar.setLayoutParams(lp);
        }

        if(position == 2){
            holder.ratting_bar.setNumStars(3);
            lp.setMargins(30, 0, 0, 0);
            holder.ratting_bar.setLayoutParams(lp);
        }

        if(position == 3){
            holder.ratting_bar.setNumStars(2);
            lp.setMargins(50, 0, 0, 0);
            holder.ratting_bar.setLayoutParams(lp);
        }

        if(position == 4){
            holder.ratting_bar.setNumStars(1);
            lp.setMargins(60, 0, 0, 0);
            holder.ratting_bar.setLayoutParams(lp);
        }

        if(list.get(position).isSelected){
            holder.iv_checkbox.setImageResource(R.drawable.ico_not_checked);
        }else {
            holder.iv_checkbox.setImageResource(R.drawable.ico_not_unchecked);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RatingBar ratting_bar;
        ImageView iv_checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            ratting_bar = itemView.findViewById(R.id.ratting_bar);
            iv_checkbox = itemView.findViewById(R.id.iv_checkbox);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            
            int position = getAdapterPosition();
/*

            if(list.get(getAdapterPosition()).isSelected){
                list.get(getAdapterPosition()).isSelected = false;
            }else {
                list.get(getAdapterPosition()).isSelected = true;
                rattingsKey = list.get(getAdapterPosition()).ratting +","+ ratting_bar;
            }
*/

            String friendIdFromList = String.valueOf(list.get(position).ratting); // here we are using userId for understanding given name friendIds only
            if(list.get(position).isSelected)
            {
                list.get(position).isSelected = false;
                if(RattingIds.contains(friendIdFromList + ",")){
                    RattingIds = RattingIds.replace(friendIdFromList + ",","");
                }
                else if(RattingIds.contains("," + friendIdFromList)) {
                    RattingIds = RattingIds.replace("," + friendIdFromList,"");
                }
                else if(RattingIds.contains(friendIdFromList)){
                    RattingIds = RattingIds.replace(friendIdFromList,"");
                }

            }else {
                if(RattingIds.length() == 0){
                    RattingIds = friendIdFromList + RattingIds;
                }else {
                    RattingIds = friendIdFromList + "," + RattingIds;
                }

                list.get(position).isSelected = true;
            }

            friedsIdsListner.getIds(RattingIds);
            notifyDataSetChanged();
            
            
            
            
            
            
            
            



           notifyItemChanged(getAdapterPosition());
        }
    }
}
