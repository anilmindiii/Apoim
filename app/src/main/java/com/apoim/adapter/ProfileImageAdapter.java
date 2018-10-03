package com.apoim.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.listener.ProfileImageAdapterListener;
import com.apoim.modal.ImageBean;
import com.apoim.server_task.WebService;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 21/2/18.
 */

public class ProfileImageAdapter extends RecyclerView.Adapter<ProfileImageAdapter.MyViewHolder> {
    //private ArrayList<Bitmap> recycler_list;
    // private ArrayList<File> productImages;
    private Context context;
    ProfileImageAdapterListener listener;

    List<ImageBean> list;

    public ProfileImageAdapter(List<ImageBean> imageBeans, ProfileImageAdapterListener listener) {
        this.list = imageBeans;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
     ImageView circular_profile_image;
        ImageView  cancel_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            circular_profile_image = itemView.findViewById(R.id.circular_profile_image);
            cancel_icon = itemView.findViewById(R.id.cancel_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public ProfileImageAdapter(ArrayList<Bitmap> recycler_list, ArrayList<File> productImages, ProfileImageAdapterListener listener) {
        //this.recycler_list = recycler_list;
        //this.productImages = productImages;
        this.listener = listener;
    }

    @Override
    public ProfileImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile_horizontal_recycler, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileImageAdapter.MyViewHolder holder, final int position) {
        if (position == 0) {
            holder.cancel_icon.setVisibility(View.GONE);
            holder.circular_profile_image.setImageResource(R.drawable.photo_camera);
        } else if (position > 0) {

            ImageBean imageBean = list.get(position);
            holder.cancel_icon.setVisibility(View.VISIBLE);


            if (imageBean != null)
                if (!TextUtils.isEmpty(imageBean.url)) {
                    /*Glide.with(holder.circular_profile_image.getContext())
                            .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ico_user_placeholder)).load(imageBean.url)
                            .into(holder.circular_profile_image);*/

                Picasso.with(holder.circular_profile_image.getContext())
                        .load(imageBean.url)
                        .placeholder(R.drawable.ico_user_placeholder)
                        .into(holder.circular_profile_image);
                } else if (imageBean.bitmap != null) {
                    holder.circular_profile_image.setImageBitmap(imageBean.bitmap);
                }


            holder.cancel_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = holder.getAdapterPosition();

                    if (!list.get(pos).imageId.equals("")) {
                        deleteImages(list.get(pos).imageId);
                    }

                    if (list.size() > pos) {
                        list.remove(pos);
                        notifyItemRemoved(pos);
                    }


                }
            });

        }


        holder.circular_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.size() >= 6) {
                    if (holder.getAdapterPosition() == 0)
                        Utils.openAlertDialog(context, context.getString(R.string.image_selection_limite_msg));
                } else listener.getPosition(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void deleteImages(final String userImgId) {
        //loadingView.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("userImgId", userImgId);

        WebService service = new WebService(context, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                //loadingView.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {


                    } else if (status.equals("fail")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("user/deleteUserImage", map);

    }

}
