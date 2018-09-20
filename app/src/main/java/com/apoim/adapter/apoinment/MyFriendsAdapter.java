package com.apoim.adapter.apoinment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.OtherProfileActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 27/3/18.
 */

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder> {

    Context mContext;
    ArrayList<MyFriendListInfo.ListBean> friendList;
    InsLoadingView loading_view;
    LinearLayout ly_no_friend_found;
    String from = "";

    public MyFriendsAdapter(Context mContext, ArrayList<MyFriendListInfo.ListBean> friendList,
                            InsLoadingView loading_view, LinearLayout ly_no_friend_found, String from) {
        this.mContext = mContext;
        this.friendList = friendList;
        this.loading_view = loading_view;
        this.ly_no_friend_found = ly_no_friend_found;
        this.from = from;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_friends_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MyFriendListInfo.ListBean bean = friendList.get(position);

        if(bean.work.equals("")){
            holder.tv_work.setText("NA");
        }else {
            holder.tv_work.setText(bean.work);
        }

        holder.tv_name.setText(bean.fullName);

        if(bean.profileImage != null && !bean.profileImage.equals("")){
            Picasso.with(mContext).load(bean.profileImage).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_profile);
        }
        holder.iv_remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wannaDeleteFriend(bean.fullName,bean,position);

            }
        });

        if(from.equals("request")){
            holder.ly_aceept_reject_layout.setVisibility(View.VISIBLE);
            holder.iv_remove_friend.setVisibility(View.GONE);

        }else if(from.equals("friend")){
            holder.ly_aceept_reject_layout.setVisibility(View.GONE);
            holder.iv_remove_friend.setVisibility(View.VISIBLE);
        }

        holder.iv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove_accept_Request(bean.userId,"edit","2","Friend request accepted",position);
            }
        });

        holder.iv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               remove_accept_Request(bean.userId,"edit","3","Friend request deleted successfully", position);
            }
        });

        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OtherProfileActivity.class);
                intent.putExtra(Constant.userId,friendList.get(position).userId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_profile,iv_remove_friend,iv_accept,iv_reject;
        TextView tv_name,tv_work;
        LinearLayout ly_aceept_reject_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_remove_friend = itemView.findViewById(R.id.iv_remove_friend);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_work = itemView.findViewById(R.id.tv_work);
            ly_aceept_reject_layout = itemView.findViewById(R.id.ly_aceept_reject_layout);
            iv_accept = itemView.findViewById(R.id.iv_accept);
            iv_reject = itemView.findViewById(R.id.iv_reject);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, OtherProfileActivity.class);
            intent.putExtra("userId",friendList.get(getAdapterPosition()).userId);
            mContext.startActivity(intent);
        }
    }

    private void unFriendUser(final int position, String friendId, String type, String status, final String msg){
        loading_view.setVisibility(View.VISIBLE);

        Map<String,String> param = new HashMap<>();
        param.put("friendId",friendId);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");

                    if(status.equals("success")){
                        friendList.remove(position);

                        if(friendList.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }else {
                        if(friendList.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("user/unfriend",param);
    }


    private void wannaDeleteFriend(String userName, final MyFriendListInfo.ListBean bean, final int position){
        final Dialog _dialog = new Dialog(mContext);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);


        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_name.setText(userName);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unFriendUser(position,bean.friendId,"edit","3","Friend request removed successfully");
                _dialog.dismiss();
            }
        });

        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialog.dismiss();
            }
        });
        _dialog.show();
    }

    private void remove_accept_Request(String requestForId, String type, String status, final String msg, final int position){
        loading_view.setVisibility(View.VISIBLE);

        Map<String,String> param = new HashMap<>();
        param.put("requestFor",requestForId);
        param.put("type",type);
        param.put("status",status);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("success")){
                        friendList.remove(position);

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }
                        notifyDataSetChanged();
                    }else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("user/friendRequest",param);
    }
}
