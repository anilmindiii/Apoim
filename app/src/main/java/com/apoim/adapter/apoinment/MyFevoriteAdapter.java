package com.apoim.adapter.apoinment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.apoim.modal.MyFavoriteListInfo;
import com.apoim.server_task.WebService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 27/3/18.
 */

public class MyFevoriteAdapter extends RecyclerView.Adapter<MyFevoriteAdapter.ViewHolder> {
    Context mContext;
    ArrayList<MyFavoriteListInfo.FavoriteDataBean> favoriteList;
    LinearLayout ly_no_friend_found;

    public MyFevoriteAdapter(Context mContext, ArrayList<MyFavoriteListInfo.FavoriteDataBean> favoriteList, LinearLayout ly_no_friend_found) {
        this.mContext = mContext;
        this.favoriteList = favoriteList;
        this.ly_no_friend_found = ly_no_friend_found;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_fevorite_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MyFavoriteListInfo.FavoriteDataBean bean = favoriteList.get(position);

        if (!bean.profileImage.equals("")) {
            Picasso.with(mContext).load(bean.profileImage).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_favorite_image);
        }

        holder.tv_favorite_name.setText(bean.fullName);

        if(bean.workName.equals("")){
            holder.tv_favorite_work.setText("NA");
        }else {
            holder.tv_favorite_work.setText(bean.workName);
        }


        holder.iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(bean.favUserId)){
                    add_remove_Favourate(bean.favUserId,position);
                }
            }
        });

        holder.iv_favorite_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OtherProfileActivity.class);
                intent.putExtra(Constant.userId,favoriteList.get(position).favUserId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_favorite_image,iv_favorite;
        TextView tv_favorite_name, tv_favorite_work;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_favorite_image = itemView.findViewById(R.id.iv_favorite_image);
            tv_favorite_name = itemView.findViewById(R.id.tv_favorite_name);
            tv_favorite_work = itemView.findViewById(R.id.tv_favorite_work);
            iv_favorite = itemView.findViewById(R.id.iv_favorite);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private void add_remove_Favourate(String favUserId, final int position){
        Map<String,String> param = new HashMap<>();
        param.put("favUserId",favUserId);
        param.put("isFavorite","0");

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener(){

            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("success")){
                        favoriteList.remove(position);
                        if(favoriteList.size() == 0){
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                        }

                        notifyDataSetChanged();
                    }else {


                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response",error.toString());

            }
        });

        service.callSimpleVolley("user/myFavorite",param);
    }
}
