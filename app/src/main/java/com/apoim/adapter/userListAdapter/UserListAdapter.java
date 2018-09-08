package com.apoim.adapter.userListAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.helper.Constant;
import com.apoim.listener.GetUsetItemClick;
import com.apoim.modal.UserListInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mindiii on 7/3/18.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context mContext;
    ArrayList<UserListInfo.NearByUsersBean> userList;
    GetUsetItemClick usetItemClick;
    boolean isOnlineFilterApplyed;


    public UserListAdapter(Context context,ArrayList<UserListInfo.NearByUsersBean> userList1, GetUsetItemClick usetItemClick) {
        this.userList = userList1;
        this.mContext = context;
        this.usetItemClick = usetItemClick;
    }

    public void onlineFilter(boolean isonlineFilter){
        this.isOnlineFilterApplyed = isonlineFilter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(userList.get(position).fullName.contains(" ")){
            String name = userList.get(position).fullName.substring(0, userList.get(position).fullName.indexOf(" "));
            String capitalFirstLatter = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.tv_user_name.setText(capitalFirstLatter);
        }else {
            String name = userList.get(position).fullName;
            String capitalFirstLatter = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.tv_user_name.setText(capitalFirstLatter);
        }

        if(userList.get(position).status.equals(Constant.online)){
            holder.status.setVisibility(View.VISIBLE);
        }else  holder.status.setVisibility(View.GONE);



        //if(!userList.get(position).profileImage.equals(""))
        String str = userList.get(position).profileImage;
        Picasso.with(mContext).load(str).placeholder(R.drawable.ico_user_placeholder).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        ImageView status;
        TextView tv_user_name;
        RelativeLayout main_view;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.user_image);
            status =itemView.findViewById(R.id.status);
            tv_user_name =itemView.findViewById(R.id.tv_user_name);
            main_view =itemView.findViewById(R.id.main_view);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            usetItemClick.userItemClick(userList.get(getAdapterPosition()).userId);
        }
    }
}
