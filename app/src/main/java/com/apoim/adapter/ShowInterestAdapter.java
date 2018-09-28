package com.apoim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.helper.Constant;
import com.apoim.listener.GetInterestValueListener;
import com.apoim.modal.ProfileInterestInfo;

import java.util.ArrayList;

/**
 * Created by mindiii on 28/2/18.
 */

public class ShowInterestAdapter extends RecyclerView.Adapter<ShowInterestAdapter.MyViewHolder> {
    private Context context;
    private GetInterestValueListener listener;
    private ArrayList<ProfileInterestInfo> interest_list;
    private String from = "";

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item_interest;
        ImageView cancel_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.item_interest = itemView.findViewById(R.id.item_interest);
            this.cancel_icon = itemView.findViewById(R.id.cancel_icon);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public ShowInterestAdapter(String from,Context context, ArrayList<ProfileInterestInfo> interest_list, GetInterestValueListener listener) {
        this.context = context;
        this.listener = listener;
        this.interest_list = interest_list;
        this.from = from;
    }

    @Override
    public ShowInterestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(from.equals(Constant.OtherProfile)){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_interest_list_item, parent, false);
            return new MyViewHolder(itemView);
        }
        else if(from.equals(Constant.UserPersonalProfile)){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_selected_interest_list, parent, false);
            return new MyViewHolder(itemView);
        }
        return new MyViewHolder(null);
    }

    @Override
    public void onBindViewHolder(ShowInterestAdapter.MyViewHolder holder, final int position) {
        holder.item_interest.setText(interest_list.get(position).interest);

        holder.cancel_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getInterestValue(interest_list.get(position).interest);
                interest_list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return interest_list.size();
    }


}
