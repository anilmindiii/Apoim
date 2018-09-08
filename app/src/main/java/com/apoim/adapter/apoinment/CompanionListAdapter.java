package com.apoim.adapter.apoinment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.CompanionListActivity;
import com.apoim.helper.Constant;
import com.apoim.listener.GetCompId;
import com.apoim.modal.CompanionInfo;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by mindiii on 17/5/18.
 */

public class CompanionListAdapter extends RecyclerView.Adapter<CompanionListAdapter.ViewHoler> {
    ArrayList<CompanionInfo.ListBean> listBean;
    Context mContext;
    GetCompId compIdListner;
    boolean isLimitOver;


    public CompanionListAdapter(Context context, boolean isLimitOver, ArrayList<CompanionInfo.ListBean> compList,GetCompId compIdListner) {
        this.listBean = compList;
        this.mContext = context;
        this.compIdListner = compIdListner;
        this.isLimitOver = isLimitOver;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.companion_item,parent,false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(final ViewHoler holder, int position) {
        final CompanionInfo.ListBean bean = listBean.get(position);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ico_user_placeholder);
        Glide.with(mContext).load(bean.userImg).apply(options).into(holder.iv_compan);

        holder.tv_comp_name.setText(bean.fullName);

        if(bean.companionMemberStatus != null){
            if(bean.companionMemberStatus.equals(Constant.Pending_request) || bean.companionMemberStatus.equals(Constant.Joined_Payment_is_pending)){
                holder.tv_comp_status.setTextColor(ContextCompat.getColor( mContext,R.color.coloryellow));
            }else if(bean.companionMemberStatus.equals(Constant.Confirmed_payment) || bean.companionMemberStatus.equals(Constant.Confirmed)|| bean.companionMemberStatus.equals("Request accepted")){
                holder.tv_comp_status.setTextColor(ContextCompat.getColor( mContext,R.color.colorgreen));
            } else if(bean.companionMemberStatus.equals(Constant.Request_rejected) || bean.companionMemberStatus.equals(Constant.Request_Canceled)){
                holder.tv_comp_status.setTextColor(ContextCompat.getColor( mContext,R.color.colorPrimary));
            }
        }

        if(bean.companionMemberStatus.equals(Constant.Joined_Payment_is_pending)){
            holder.tv_pay_compan.setVisibility(View.VISIBLE);
        }
        else holder.tv_pay_compan.setVisibility(View.GONE);

        holder.tv_pay_compan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLimitOver){
                    compIdListner.companionId(bean.eventId,bean.compId,bean.eventMem_Id);
                }else {
                    Utils.openAlertDialog(mContext,mContext.getString(R.string.limit_over_to_join_event));
                }

            }
        });

        Utils.setStatusById(holder.tv_comp_status,bean.companionMemberStatus);
    }

    @Override
    public int getItemCount() {
        return listBean.size();
    }

    class ViewHoler extends RecyclerView.ViewHolder {
        ImageView iv_compan;
        TextView tv_comp_name,tv_comp_status,tv_pay_compan;
        public ViewHoler(View itemView) {
            super(itemView);
            iv_compan = itemView.findViewById(R.id.iv_compan);
            tv_comp_name = itemView.findViewById(R.id.tv_comp_name);
            tv_comp_status = itemView.findViewById(R.id.tv_comp_status);
            tv_pay_compan = itemView.findViewById(R.id.tv_pay_compan);
        }
    }


}
