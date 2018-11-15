package com.apoim.adapter.apoinment;

import android.app.Dialog;
import android.content.Context;
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
import com.apoim.app.Apoim;
import com.apoim.modal.InvitedListInfo;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 26/4/18.
 **/

public class InvitedEventListAdapter extends RecyclerView.Adapter<InvitedEventListAdapter.ViewHolder> {
    Context mContext;
    ArrayList<InvitedListInfo.ListBean> listBeans;
    InsLoadingView loading_view;
    LinearLayout ly_no_friend_found;
    boolean isExpaireDate;

    public InvitedEventListAdapter(Context mContext, ArrayList<InvitedListInfo.ListBean> listBeans
            , InsLoadingView loading_view, LinearLayout ly_no_friend_found, boolean isExpaireDate) {
        this.mContext = mContext;
        this.listBeans = listBeans;
        this.loading_view = loading_view;
        this.ly_no_friend_found = ly_no_friend_found;
        this.isExpaireDate = isExpaireDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_member_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InvitedListInfo.ListBean bean = listBeans.get(position);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(mContext).load(bean.userImg).apply(options).into(holder.iv_profile);

        holder.tv_name.setText(bean.fullName);
        holder.tv_work.setText(bean.workName);

        if(isExpaireDate){
            holder.iv_remove_friend.setVisibility(View.GONE);
        }else {
            holder.iv_remove_friend.setVisibility(View.VISIBLE);
        }

        holder.iv_remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listBeans.size() == 1){
                    Utils.openAlertDialog(mContext,mContext.getString(R.string.cant_remove_all));
                }else wannaDeleteFriend(bean.fullName,bean,position);
            }
        });

    }

    private void wannaDeleteFriend(String userName, final InvitedListInfo.ListBean bean, final int position){
        final Dialog _dialog = new Dialog(mContext);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);


        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);
        tv_dialog_txt.setText(R.string.sure_remove_friend);

        tv_name.setText(userName);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unFriendUser(position,bean.eventId,"invited",bean.eventMemId);
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

    @Override
    public int getItemCount() {
        return listBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_profile,iv_remove_friend;
        TextView tv_name,tv_work;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_remove_friend = itemView.findViewById(R.id.iv_remove_friend);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_work = itemView.findViewById(R.id.tv_work);
        }
    }

    private void unFriendUser(final int position, String eventId, String memberType, String eventMemId){
        loading_view.setVisibility(View.VISIBLE);

        Map<String,String> param = new HashMap<>();
        param.put("eventId",eventId);
        param.put("memberType",memberType);
        param.put("eventMemId",eventMemId);

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
                        listBeans.remove(position);

                        if(listBeans.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }else {
                        if(listBeans.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                        Utils.openAlertDialog(mContext,message);

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

        service.callSimpleVolley("event/removeMember",param);
    }
}
