package com.apoim.adapter.apoinment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.profile.OtherProfileDetailsActivity;
import com.apoim.helper.Constant;
import com.apoim.listener.AppointmentReqStatus;
import com.apoim.modal.AppointmentListInfo;
import com.apoim.session.Session;
import com.apoim.util.TimeAgo;
import com.apoim.util.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mindiii on 14/3/18.
 */

public class ApoinmentAdapter extends RecyclerView.Adapter<ApoinmentAdapter.ViewHolder> {
    AppointmentReqStatus reqStatus;
    Context mContext;
    ArrayList<AppointmentListInfo.AppoimDataBean> apomList;
    Session session;
    String myUserId = "";
    int count = -1;
    String currentDate = "";

    public ApoinmentAdapter(Context mContext, ArrayList<AppointmentListInfo.AppoimDataBean> apomList, AppointmentReqStatus reqStatus) {
        this.reqStatus = reqStatus;
        this.mContext = mContext;
        this.apomList = apomList;
        session = new Session(mContext, (Activity) mContext);
        myUserId = session.getUser().userDetail.userId;
    }

    public void agoDate(String currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_oppoim_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ((ViewHolder) holder).bindData(position);

        AppointmentListInfo.AppoimDataBean bean = apomList.get(position);

        holder.tv_location.setText(bean.appointAddress);

        String date_before = bean.appointDate;
        String date_after = formateDateFromstring("yyyy-MM-dd", "dd, MMM yyyy", date_before);

        holder.tv_time_ago.setText(TimeAgo.toRelative(bean.crd, currentDate));


        holder.tv_date.setText(date_after + ", ");

        if (apomList.get(position).offerPrice.equals("")) {
            holder.tv_offer_price.setText("Free");
        } else holder.tv_offer_price.setText("$ " + bean.offerPrice);

        holder.tv_counter_price.setText("$ " + bean.counterPrice);
        holder.tv_counter_price1.setText("$ " + bean.counterPrice);
        holder.tv_coounter_price.setText("$ " + bean.counterPrice);
        holder.ly_counter_price.setVisibility(View.GONE);

        try {
            String timeLong = bean.appointTime;

            SimpleDateFormat formatLong = new SimpleDateFormat("HH:mm:ss", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("hh:mm aa", Locale.US);
            Log.v("timeLong", formatShort.format(formatLong.parse(timeLong)));
            holder.tv_time.setText(formatShort.format(formatLong.parse(timeLong)));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            String timeLong = apomList.get(position).crd;

            SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat formatShort = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.US);
            Log.v("timeLong", formatShort.format(formatLong.parse(timeLong)));

            //holder.tv_time_date.setText(formatShort.format(formatLong.parse(timeLong)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (apomList.get(position).appointById.equals(myUserId)) {

            if (apomList.get(position).isFinish.equals("1")) {

                holder.tv_status.setText("Finished Appointment");
                holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                holder.ly_bottom_view.setVisibility(View.GONE);

                if (bean.counterPrice.equals("") || bean.counterPrice.equals("0")) {// empty mean free
                    holder.ly_counter_price.setVisibility(View.GONE); // if free then gone if paid then visible
                } else
                    holder.ly_counter_price.setVisibility(View.VISIBLE); // if free then gone if paid then visible


            }
            else if (bean.isCounterApply.equals("1")) {

                if (bean.counterStatus.equals("0")) {
                    holder.ly_status_counter.setVisibility(View.GONE);
                    holder.ly_accept_reject.setVisibility(View.VISIBLE);

                    if (bean.counterPrice.equals("")|| bean.counterPrice.equals("0")) {// empty mean free
                        holder.ly_fill_counter_price.setVisibility(View.GONE); // if free then gone if paid then visible
                    } else{
                        holder.ly_fill_counter_price.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                        holder.ly_fill_counter_price.setEnabled(false);
                    }


                } else if (bean.counterStatus.equals("1")) {
                    holder.tv_status.setText("Payment is pending");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));
                    holder.ly_accept_reject.setVisibility(View.GONE);
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                } else if (bean.counterStatus.equals("3")) {
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                    holder.ly_accept_reject.setVisibility(View.GONE);

                    holder.tv_status.setText("Confirmed Appointment");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                }
            } else if (apomList.get(position).appointmentStatus.equals("1")) {

                holder.tv_status.setText("Waiting for approval");
                holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));

                holder.ly_accept_reject.setVisibility(View.GONE);
                holder.ly_status_counter.setVisibility(View.VISIBLE);

                if (bean.counterStatus.equals("0")) {// empty mean free
                    holder.ly_counter.setVisibility(View.GONE); // if free then gone if paid then visible
                } else
                    holder.ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible

            } else if (apomList.get(position).appointmentStatus.equals("2")) {


                if (apomList.get(position).offerPrice.equals("")) {// empty mean free case
                    holder.tv_status.setText("Confirmed Appointment");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                } else {
                    holder.tv_status.setText("Payment is pending");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));// if free then gone if paid then visible
                }

                holder.ly_accept_reject.setVisibility(View.GONE);
                holder.ly_status_counter.setVisibility(View.VISIBLE);

                if (bean.counterStatus.equals("0")) {// empty mean free
                    holder.ly_counter.setVisibility(View.GONE); // if free then gone if paid then visible
                } else
                    holder.ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible

            } else if (apomList.get(position).appointmentStatus.equals("4")) {

                holder.tv_status.setText("Confirmed Appointment");
                holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                holder.ly_accept_reject.setVisibility(View.GONE);
                holder.ly_status_counter.setVisibility(View.VISIBLE);
                holder.ly_counter.setVisibility(View.GONE);
            }
            holder.tv_name.setText(apomList.get(position).ForName);

            if (!apomList.get(position).byImage.equals(""))
                Picasso.with(mContext).load(apomList.get(position).forImage).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_profile);
/*............................................................................................................................................*/


        } else if (apomList.get(position).appointForId.equals(myUserId)) {

            if (apomList.get(position).isFinish.equals("1")) {
                holder.tv_status.setText("Finished Appointment");
                holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                holder.ly_bottom_view.setVisibility(View.GONE);

                if (bean.counterPrice.equals("")|| bean.counterPrice.equals("0")) {// empty mean free
                    holder.ly_counter_price.setVisibility(View.GONE); // if free then gone if paid then visible
                } else{
                    holder.ly_counter_price.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }


            } else if (bean.isCounterApply.equals("1")) {

                if (bean.counterStatus.equals("0")) {
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                    holder.ly_accept_reject.setVisibility(View.GONE);
                    holder.tv_status.setText("Waiting for approval");

                    holder.ly_counter.setVisibility(View.VISIBLE);
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));
                } else if (bean.counterStatus.equals("1")) {
                    holder.tv_status.setText("Payment is pending");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));
                    holder.ly_accept_reject.setVisibility(View.GONE);
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                } else if (bean.counterStatus.equals("3")) {
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                    holder.ly_accept_reject.setVisibility(View.GONE);

                    holder.tv_status.setText("Confirmed Appointment");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                }
            } else if (apomList.get(position).appointmentStatus.equals("1")) {

                if (!bean.counterStatus.equals("0")) {
                    holder.ly_counter.setVisibility(View.VISIBLE);
                    holder.tv_counter_price1.setText(bean.counterPrice);
                    holder.tv_status.setText("Waiting for approval");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));

                    holder.ly_accept_reject.setVisibility(View.GONE);
                    holder.ly_status_counter.setVisibility(View.VISIBLE);
                } else {
                    holder.ly_counter.setVisibility(View.GONE);
                    holder.ly_accept_reject.setVisibility(View.VISIBLE);
                    holder.ly_status_counter.setVisibility(View.GONE);
                    holder.tv_counter_price.setText("Counter");
                }


                if (bean.offerPrice.equals("")) {// empty mean free
                    holder.ly_fill_counter_price.setVisibility(View.GONE); // if free then gone if paid then visible
                } else
                    holder.ly_fill_counter_price.setVisibility(View.VISIBLE); // if free then gone if paid then visible


                holder.ly_fill_counter_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext, "dbfjdbfjud", Toast.LENGTH_SHORT).show();
                        enterCounterPriceDialog(position, bean.offerPrice);
                    }
                });

            } else if (apomList.get(position).appointmentStatus.equals("2")) {

                if (apomList.get(position).offerPrice.equals("")) {// empty mean free case
                    holder.tv_status.setText("Confirmed Appointment");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                } else {
                    holder.tv_status.setText("Payment is pending");
                    holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.coloryellow));// if free then gone if paid then visible
                }

                holder.ly_accept_reject.setVisibility(View.GONE);
                holder.ly_status_counter.setVisibility(View.VISIBLE);

                if (!bean.counterStatus.equals("0")) {
                    holder.ly_counter.setVisibility(View.VISIBLE);
                    holder.tv_counter_price1.setText(bean.counterPrice);
                } else {
                    holder.ly_counter.setVisibility(View.GONE);
                }


            } else if (apomList.get(position).appointmentStatus.equals("4")) {
                holder.tv_status.setText("Confirmed Appointment");
                holder.tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                holder.ly_accept_reject.setVisibility(View.GONE);


                if (bean.counterPrice.equals("")|| bean.counterPrice.equals("0")) {// empty mean free
                    holder.ly_counter.setVisibility(View.GONE); // if free then gone if paid then visible
                } else{
                    holder.ly_counter.setVisibility(View.VISIBLE); // if free then gone if paid then visible
                }

                holder.ly_status_counter.setVisibility(View.VISIBLE);
            }
            holder.tv_name.setText(apomList.get(position).ByName);

            if (!apomList.get(position).forImage.equals("")) {
                Picasso.with(mContext).load(apomList.get(position).byImage).placeholder(R.drawable.ico_user_placeholder).into(holder.iv_profile);
            }
        }

    }

    public String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //Log(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    @Override
    public int getItemCount() {
        return apomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_profile;
        TextView tv_time, tv_location, tv_time_date, tv_status, tv_name, tv_date,
                tv_offer_price, tv_counter_price, tv_counter_price1, tv_time_ago,tv_coounter_price;
        LinearLayout ly_main_view, ly_accept, ly_reject, ly_counter, ly_fill_counter_price,ly_counter_price;
        RelativeLayout ly_accept_reject, ly_status_counter, ly_bottom_view;

        public ViewHolder(View itemView) {
            super(itemView);

            ly_reject = itemView.findViewById(R.id.ly_reject);
            ly_accept = itemView.findViewById(R.id.ly_accept);

            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_time_date = itemView.findViewById(R.id.tv_time_date);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_coounter_price = itemView.findViewById(R.id.tv_coounter_price);

            ly_main_view = itemView.findViewById(R.id.ly_main_view);
            ly_fill_counter_price = itemView.findViewById(R.id.ly_fill_counter_price);

            tv_counter_price1 = itemView.findViewById(R.id.tv_counter_price1);
            tv_counter_price = itemView.findViewById(R.id.tv_counter_price);
            tv_offer_price = itemView.findViewById(R.id.tv_offer_price);

            ly_accept_reject = itemView.findViewById(R.id.ly_accept_reject);
            ly_status_counter = itemView.findViewById(R.id.ly_status_counter);
            ly_counter = itemView.findViewById(R.id.ly_counter);
            ly_bottom_view = itemView.findViewById(R.id.ly_bottom_view);
            ly_counter_price = itemView.findViewById(R.id.ly_counter_price);

            ly_reject.setOnClickListener(this);
            ly_accept.setOnClickListener(this);
            iv_profile.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        void bindData(final int position) {
            ly_main_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reqStatus.directionRequest(position);
                }
            });
        }


        @Override
        public void onClick(View view) {
            //reqStatus.directionRequest(getAdapterPosition());
            switch (view.getId()) {
                case R.id.ly_reject: {
                   /* ly_accept.setVisibility(View.GONE);
                    ly_reject.setVisibility(View.GONE);*/
                    reqStatus.rejectRequest(apomList.get(getAdapterPosition()).appId, getAdapterPosition(), apomList.get(getAdapterPosition()).isCounterApply, apomList.get(getAdapterPosition()).appointForId);
                    break;
                }
                case R.id.ly_accept: {
                   /* ly_accept.setVisibility(View.GONE);
                    ly_reject.setVisibility(View.GONE);*/
                    reqStatus.acceptRequest(apomList.get(getAdapterPosition()).appId, getAdapterPosition(), apomList.get(getAdapterPosition()).isCounterApply, apomList.get(getAdapterPosition()).appointForId);
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen));
                    // tv_status.setText("Confirmed Appointment");

                    break;
                }

                case R.id.iv_profile:{

                    Intent intent = new Intent(mContext, OtherProfileDetailsActivity.class);

                    if (apomList.get(getAdapterPosition()).appointById.equals(myUserId)) {
                        intent.putExtra(Constant.userId,apomList.get(getAdapterPosition()).appointForId);
                    }
                    else if (apomList.get(getAdapterPosition()).appointForId.equals(myUserId)){
                        intent.putExtra(Constant.userId,apomList.get(getAdapterPosition()).appointById);
                    }
                    mContext.startActivity(intent);
                    break;
                }

            }

        }
    }

    private void enterCounterPriceDialog(int position, String offerPrice) {

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.enter_counter_price_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        EditText ed_counter_price = dialog.findViewById(R.id.ed_counter_price);

        inputFilter(ed_counter_price);
        TextView tv_apply_button = dialog.findViewById(R.id.tv_apply_button);
        TextView tv_offer_price = dialog.findViewById(R.id.tv_offer_price);
        ImageView iv_close_button = dialog.findViewById(R.id.iv_close_button);

        tv_offer_price.setText("$" + offerPrice);

        tv_apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String counter_price = ed_counter_price.getText().toString().trim();
                if (!counter_price.equals("")) {

                    if((!ed_counter_price.getText().toString().trim().equals(".") && !ed_counter_price.getText().toString().trim().equals(".0"))){
                        reqStatus.applyCounterTask(apomList.get(position).appId, counter_price, apomList.get(position).appointById, position);
                        dialog.dismiss();
                    }else {
                        Utils.openAlertDialog(mContext, "Please Enter Valid Counter Price");
                    }


                } else {
                    Utils.openAlertDialog(mContext, "Please Enter Counter Price");
                }


            }
        });

        iv_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    // Input filter used to restrict amount input to be round off to 2 decimal places
    private void inputFilter(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String temp = et.getText().toString();
                if (temp.contains(".")) {
                    if (et.getText().toString().substring(et.getText().toString().indexOf(".") + 1, et.length()).length() == 2) {
                        InputFilter[] fArray = new InputFilter[1];
                        fArray[0] = new InputFilter.LengthFilter(arg0.length());
                        et.setFilters(fArray);
                    }
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
                if (arg0.toString().length() == 1 && arg0.toString().startsWith("0") || arg0.toString().startsWith(".00")) {
                    arg0.clear();
                }

                if (arg0.length() > 0) {
                    String str = et.getText().toString();
                    et.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                count--;
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(100);
                                et.setFilters(fArray);
                                //change the edittext's maximum length to 100.
                                //If we didn't change this the edittext's maximum length will
                                //be number of digits we previously entered.
                            }
                            return false;
                        }
                    });
                    char t = str.charAt(arg0.length() - 1);
                    if (t == '.') {
                        count = 0;
                    }
                    if (count >= 0) {
                        if (count == 2) {
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(arg0.length());
                            et.setFilters(fArray);
                            //prevent the edittext from accessing digits
                            //by setting maximum length as total number of digits we typed till now.
                        }
                        count++;
                    }
                }
            }
        });
    }
}
