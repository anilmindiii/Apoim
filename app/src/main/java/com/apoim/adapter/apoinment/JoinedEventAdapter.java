package com.apoim.adapter.apoinment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
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
import com.apoim.helper.Constant;
import com.apoim.modal.JoinedEventInfo;
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
 * Created by Anil on 26/4/18.
 **/

public class JoinedEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int viewTypeYou = 1;
    private int viewTypeCompanion = 2;

    private Context mContext;
    private ArrayList<JoinedEventInfo.ListBean> joinedList;
    private  InsLoadingView loading_view;
    private LinearLayout ly_no_friend_found;
    private String from ;
    private boolean isExpaireDate;

    public JoinedEventAdapter(Context mContext,
                              ArrayList<JoinedEventInfo.ListBean> joinedList,
                              InsLoadingView loading_view,
                              LinearLayout ly_no_friend_found,
                              String from, boolean isExpaireDate) {
        this.mContext = mContext;
        this.joinedList = joinedList;
        this.from = from;
        this.loading_view = loading_view;
        this.ly_no_friend_found = ly_no_friend_found;
        this.isExpaireDate = isExpaireDate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == viewTypeYou){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_member_item, parent, false);

        }else if(viewType == viewTypeCompanion){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_member_companion_item,parent,false);
            return new ViewHolderCompanion(view);
        }
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JoinedEventInfo.ListBean bean = joinedList.get(position);
        final int itemType = getItemViewType(position);

        if(itemType == viewTypeYou){
            ((ViewHolder)holder).bindData(bean,position);
        }else if(itemType == viewTypeCompanion){
            ((ViewHolderCompanion)holder).bindData(bean,position);
        }

    }


    @Override
    public int getItemViewType(int position) {
        if(!joinedList.get(position).companionId.equals("") || !joinedList.get(position).companionMemberStatus.equals("")){
            return viewTypeCompanion;
        }else {
            return viewTypeYou;
        }
    }

    @Override
    public int getItemCount() {
        return joinedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_work;
        ImageView iv_profile,iv_remove_friend;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_work = itemView.findViewById(R.id.tv_work);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_remove_friend = itemView.findViewById(R.id.iv_remove_friend);
        }

        void bindData(final JoinedEventInfo.ListBean bean, final int position) {

            // tv_work.setText(bean.memberStatus);

            Utils.setStatusById(tv_work,bean.memberStatus);
            tv_work.setTextColor(ContextCompat.getColor(tv_work.getContext(),R.color.coloryellow));

            tv_name.setText(bean.memberName);

            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ico_user_placeholder);
            Glide.with(mContext).load(bean.memberImage).apply(options).into(iv_profile);

            if(isExpaireDate){
                iv_remove_friend.setVisibility(View.GONE);
            }
            else if(from.equals("eventRequest")){
                iv_remove_friend.setVisibility(View.GONE);
            }else if(bean.memberStatus.equals(Constant.Joined_Payment_is_pending)){
                iv_remove_friend.setVisibility(View.VISIBLE);
            }else {
                iv_remove_friend.setVisibility(View.GONE);
            }



            if(bean.memberStatus.equals(Constant.Pending_request)){
                tv_work.setTextColor(ContextCompat.getColor(tv_work.getContext(),R.color.coloryellow));
            }

            if(bean.memberStatus.equals(Constant.Pending_request) || bean.memberStatus.equals(Constant.Joined_Payment_is_pending)){
                tv_work.setTextColor(ContextCompat.getColor(tv_work.getContext(),R.color.coloryellow));
            }else if(bean.memberStatus.equals(Constant.Confirmed_payment) || bean.memberStatus.equals(Constant.Confirmed)){
                tv_work.setTextColor(ContextCompat.getColor(tv_work.getContext(),R.color.colorgreen));
            }

            iv_remove_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(joinedList.size() == 1){
                        Utils.openAlertDialog(mContext,"Atlest one member should be invited");
                    }else wannaDeleteFriend(bean.memberName,bean,position);
                }
            });
        }
    }

    class ViewHolderCompanion extends RecyclerView.ViewHolder{
        TextView member_name,member_status,compan_name,compan_status;
        ImageView member_image,compan_image,iv_remove;
        public ViewHolderCompanion(View itemView) {
            super(itemView);
            member_name = itemView.findViewById(R.id.member_name);
            member_status = itemView.findViewById(R.id.member_status);
            compan_name = itemView.findViewById(R.id.compan_name);
            compan_status = itemView.findViewById(R.id.compan_status);
            member_image = itemView.findViewById(R.id.member_image);
            compan_image = itemView.findViewById(R.id.compan_image);
            iv_remove = itemView.findViewById(R.id.iv_remove);

        }

        public void bindData(final JoinedEventInfo.ListBean bean, final int position) {

            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ico_user_placeholder);
            Glide.with(mContext).load(bean.memberImage).apply(options).into(member_image);
            Glide.with(mContext).load(bean.companionImage).apply(options).into(compan_image);

            if(isExpaireDate){
                iv_remove.setVisibility(View.GONE);
            }
            else if(bean.memberStatus.equals(Constant.Joined_Payment_is_pending)){
                iv_remove.setVisibility(View.VISIBLE);
            }else{
                iv_remove.setVisibility(View.GONE);
            }

            if(bean.memberStatus.equals(Constant.Pending_request) || bean.memberStatus.equals(Constant.Joined_Payment_is_pending)){
                member_status.setTextColor(ContextCompat.getColor(member_status.getContext(),R.color.coloryellow));
            }else if(bean.memberStatus.equals(Constant.Confirmed_payment) || bean.memberStatus.equals(Constant.Confirmed)){
                member_status.setTextColor(ContextCompat.getColor(member_status.getContext(),R.color.colorgreen));
            }


            if(bean.companionMemberStatus.equals(Constant.Pending_request) || bean.companionMemberStatus.equals(Constant.Joined_Payment_is_pending)){
                compan_status.setTextColor(ContextCompat.getColor(compan_status.getContext(),R.color.coloryellow));
            }else if(bean.companionMemberStatus.equals(Constant.Confirmed_payment) || bean.companionMemberStatus.equals(Constant.Confirmed)|| bean.companionMemberStatus.equals("Request accepted")){
                compan_status.setTextColor(ContextCompat.getColor(compan_status.getContext(),R.color.colorgreen));
            }else if(bean.companionMemberStatus.equals(Constant.Request_rejected)){
                compan_status.setTextColor(ContextCompat.getColor(compan_status.getContext(),R.color.colorPrimary));
            }

            member_name.setText(bean.memberName);
            //member_status.setText(bean.memberStatus);
            Utils.setStatusById(member_status,bean.memberStatus);
            compan_name.setText(bean.companionName);
            //compan_status.setText(bean.companionMemberStatus);
            Utils.setStatusById(compan_status,bean.companionMemberStatus);

            iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(joinedList.size() == 1){
                        Utils.openAlertDialog(mContext,mContext.getString(R.string.cant_remove_all));
                    }else wannaDeleteFriend(bean.memberName,bean,position);
                }
            });
        }
    }

    private void wannaDeleteFriend(String userName, final JoinedEventInfo.ListBean bean, final int position){
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
                unFriendUser(position,bean.eventId,"joined",bean.eventMemId);
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
                        joinedList.remove(position);

                        if(joinedList.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }else {
                            ly_no_friend_found.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }else {
                        if(joinedList.size() == 0){
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
