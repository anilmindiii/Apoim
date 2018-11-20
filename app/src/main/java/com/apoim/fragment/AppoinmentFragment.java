package com.apoim.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.appointment.AppointmentDirectionActivity;
import com.apoim.adapter.apoinment.ApoinmentAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.AppointmentReqStatus;
import com.apoim.modal.AppointmentListInfo;
import com.apoim.pagination.EndlessRecyclerViewScrollListener;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mindiii on 14/3/18.
 **/

public class AppoinmentFragment extends Fragment {
    private Context mContext;
    private RecyclerView recyclerView;
    private ApoinmentAdapter apoinmentAdapter;
    private InsLoadingView loading_view;
    private ArrayList<AppointmentListInfo.AppoimDataBean> apomList;
    private Session session;
    private LinearLayout ly_no_appointment;
    AppointmentListInfo listInfo;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int startCount = 0;
    String type = "all";
    ImageView iv_filter;
    int id;
    private TextView tv_filter_title;


    public static AppoinmentFragment newInstance(String type) {

        Bundle args = new Bundle();

        AppoinmentFragment fragment = new AppoinmentFragment();
        fragment.setArguments(args);

        args.putString("type",type);

        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appoinment_frgament_layout, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        loading_view = view.findViewById(R.id.loading_view);
        ly_no_appointment = view.findViewById(R.id.ly_no_appointment);
        iv_filter = view.findViewById(R.id.iv_filter);
        tv_filter_title = view.findViewById(R.id.tv_filter_title);

        apomList = new ArrayList<>();
        session = new Session(mContext, getActivity());
        listInfo = new AppointmentListInfo();

        if(getArguments() != null){
            if(getArguments().getString("type") != null && !getArguments().getString("type").equals("")){
                type = getArguments().getString("type");

                if(type.equals("sent")){
                    tv_filter_title.setText("Sent Appointment");
                }
                else if(type.equals("received")){
                    tv_filter_title.setText("Recevied Appointment");
                }
            }
        }

        //getAppointmentList();

        apoinmentAdapter = new ApoinmentAdapter(mContext, apomList, new AppointmentReqStatus() {

            @Override
            public void acceptRequest(String a1ppointmentId, int position, String isCounterApply, String appointForId) {
                if (isCounterApply.equals("1")) {
                    upDateCounter(a1ppointmentId,appointForId,"1"); // 1 for accept
                } else acceptAppoinmentRequest(a1ppointmentId, position);
            }

            @Override
            public void rejectRequest(String appointmentId, int position, String isCounterApply, String appointForId) {
                if (isCounterApply.equals("1")) {
                    upDateCounter(appointmentId,appointForId,"2"); // 2 for reject
                } else rejectAppoinmentRequest(appointmentId, position);
            }

            @Override
            public void directionRequest(int position) {
                Intent intent = new Intent(mContext, AppointmentDirectionActivity.class);
                intent.putExtra(Constant.appointment_details, listInfo.appoimData.get(position).appId);
                startActivity(intent);
            }

            @Override
            public void applyCounterTask(String appointId, String counterPrice, String appointById, int position) {
                counterApplyRequest(appointId, counterPrice, appointById);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(apoinmentAdapter);

        iv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });



        return view;
    }

    private void loadNextDataFromApi() {
        startCount = startCount + 10;
        getAppointmentList();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        startCount = 0;
        apomList.clear();
        getAppointmentList();
    }

    private void filterDialog() {

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.filter_apoim_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        ImageView iv_received_apoim = dialog.findViewById(R.id.iv_received_apoim);
        ImageView iv_sent_apoim = dialog.findViewById(R.id.iv_sent_apoim);
        ImageView iv_finished_apoim = dialog.findViewById(R.id.iv_finished_apoim);
        ImageView iv_all_apoim = dialog.findViewById(R.id.iv_all_apoim);

        ImageView iv_close_button = dialog.findViewById(R.id.iv_close_button);
        TextView tv_apply_button = dialog.findViewById(R.id.tv_apply_button);

        RelativeLayout ly_receive_apoim = dialog.findViewById(R.id.ly_receive_apoim);
        RelativeLayout ly_sent_apoim = dialog.findViewById(R.id.ly_sent_apoim);
        RelativeLayout ly_finished_apoim = dialog.findViewById(R.id.ly_finished_apoim);
        RelativeLayout ly_all_apoim = dialog.findViewById(R.id.ly_all_apoim);

        if (type.equals("received")) {
            receivedSeleced(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
        } else if (type.equals("sent")) {
            sentSelected(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
        } else if (type.equals("finished")) {
            finishedSelected(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
        }else if(type.equals("all")){
            allSeleced(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
        }

        ly_all_apoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = R.id.ly_all_apoim;
                allSeleced(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
            }
        });

        ly_receive_apoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = R.id.ly_receive_apoim;
                receivedSeleced(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
            }
        });

        ly_sent_apoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = R.id.ly_sent_apoim;
                sentSelected(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
            }
        });

        ly_finished_apoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = R.id.ly_finished_apoim;
                finishedSelected(iv_received_apoim, iv_sent_apoim, iv_finished_apoim,iv_all_apoim);
            }
        });

        iv_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == R.id.ly_receive_apoim) {
                    type = "received";

                    apomList.clear();
                    apoinmentAdapter.notifyDataSetChanged();
                    startCount = 0;
                    tv_filter_title.setText(R.string.recevied_appointment);
                    getAppointmentList();
                } else if (id == R.id.ly_sent_apoim) {
                    type = "sent";

                    apomList.clear();
                    apoinmentAdapter.notifyDataSetChanged();
                    startCount = 0;
                    tv_filter_title.setText(R.string.sent_appointment);
                    getAppointmentList();
                } else if (id == R.id.ly_finished_apoim) {
                    type = "finished";
                    apomList.clear();
                    apoinmentAdapter.notifyDataSetChanged();
                    startCount = 0;
                    tv_filter_title.setText(R.string.finished_appointment);
                    getAppointmentList();
                }else if(id == R.id.ly_all_apoim){
                    type = "all";
                    apomList.clear();
                    apoinmentAdapter.notifyDataSetChanged();
                    startCount = 0;
                    tv_filter_title.setText(R.string.all_apoiment);
                    getAppointmentList();
                }
                dialog.dismiss();
            }
        });

    }


    private void allSeleced(ImageView iv_received_apoim, ImageView iv_sent_apoim, ImageView iv_finished_apoim,ImageView iv_all_apoim) {
        iv_all_apoim.setImageResource(R.drawable.check_btn);
        iv_received_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_sent_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_finished_apoim.setImageResource(R.drawable.uncheck_btn);

    }



    private void receivedSeleced(ImageView iv_received_apoim, ImageView iv_sent_apoim, ImageView iv_finished_apoim,ImageView iv_all_apoim) {
        iv_sent_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_received_apoim.setImageResource(R.drawable.check_btn);
        iv_finished_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_all_apoim.setImageResource(R.drawable.uncheck_btn);
    }

    private void sentSelected(ImageView iv_received_apoim, ImageView iv_sent_apoim, ImageView iv_finished_apoim,ImageView iv_all_apoim) {
        iv_received_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_finished_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_all_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_sent_apoim.setImageResource(R.drawable.check_btn);
    }

    private void finishedSelected(ImageView iv_received_apoim, ImageView iv_sent_apoim, ImageView iv_finished_apoim,ImageView iv_all_apoim) {
        iv_received_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_sent_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_all_apoim.setImageResource(R.drawable.uncheck_btn);
        iv_finished_apoim.setImageResource(R.drawable.check_btn);
    }



    private void getAppointmentList() {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        String current_date = jsonObject.getString("date");

                        Gson gson = new Gson();
                        listInfo = gson.fromJson(response, AppointmentListInfo.class);
                        apomList.addAll(listInfo.appoimData);
                        apoinmentAdapter.agoDate(current_date);
                        apoinmentAdapter.notifyDataSetChanged();

                        ly_no_appointment.setVisibility(View.GONE);

                    } else {
                        if (apomList.size() == 0) {
                            ly_no_appointment.setVisibility(View.VISIBLE);
                        } else {
                            ly_no_appointment.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("appointment/getAppointment?offset=" + startCount + "&limit=10&listType=" + type + "");

    }

    private void upDateCounter(String appointId, String appointForId, String counterStatus) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("appointForId", appointForId);
        param.put("counterStatus", counterStatus);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {

                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }

                    startCount = 0;
                    apomList.clear();
                    getAppointmentList();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/updateCounter", param);
    }

    private void counterApplyRequest(String appointId, String counterPrice, String appointById) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("counterPrice", counterPrice);
        param.put("appointById", appointById);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {

                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }

                    startCount = 0;
                    apomList.clear();
                    getAppointmentList();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/applyCounter", param);
    }

    private void rejectAppoinmentRequest(String appointId, final int position) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("type", "rejected");

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");


                    if (status.equals("success")) {
                        apomList.remove(position);
                        apoinmentAdapter.notifyDataSetChanged();
                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }

                    startCount = 0;
                    apomList.clear();
                    getAppointmentList();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/deleteAppointment", param);
    }

    private void acceptAppoinmentRequest(String appointId, final int position) {
        loading_view.setVisibility(View.VISIBLE);

        Map<String, String> param = new HashMap<>();
        param.put("appointId", appointId);
        param.put("appointeStatus", "2");

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                loading_view.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");
                    startCount = 0;
                    apomList.clear();

                    if (status.equals("success")) {
                      /*  apomList.get(position).appointForStatus = "1";
                        apomList.get(position).appointByStatus = "1";
                        apoinmentAdapter.notifyDataSetChanged();*/


                        getAppointmentList();
                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("response", error.toString());
                loading_view.setVisibility(View.GONE);
            }
        });

        service.callSimpleVolley("appointment/changeAppointmentStatus", param);
    }

}
