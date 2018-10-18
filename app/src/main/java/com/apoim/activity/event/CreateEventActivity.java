package com.apoim.activity.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.payment_subscription.PaymentActivity;
import com.apoim.adapter.apoinment.InviteFrienAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.CurrencyInfo;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.MyFriendListInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_invite_n_create_friend;
    private TextView tv_currency_name;
    private TextView profile_action_bar;
    private TextView tv_update_event;
    private ImageView iv_back;
    private RadioGroup rg_join_event;
    private RadioGroup rg_privacy;
    private RadioGroup rg_payment;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioButton rb_both;
    private RadioButton rb_private;
    private RadioButton rb_public;
    private RadioButton rb_paid;
    private RadioButton rb_free;
    private LinearLayout ly_event_start_date_time;
    private LinearLayout ly_event_end_date_time;
    private LinearLayout ly_paid_view;
    private RelativeLayout ly_currency;
    private DatePickerDialog fromDate;
    private TimePickerDialog myTime, myEndTime;
    private Calendar now, nowEnd;
    private TextView tv_start_date_time, tv_end_date_time, tv_location;
    private PlaceAutocompleteFragment autocompleteFragment;
    private ArrayList<MyFriendListInfo.ListBean> friendList;
    private ArrayList<CurrencyInfo> currencyList;
    public static String friendsIds = "";
    public static String RattingIds = "";
    private boolean isCheckedAll;
    private InsLoadingView loadingView;
    private EditText ed_event_name, ed_user_limite, ed_amount;
    private String latitude = "", longitude = "", privacy = "", payment = "", eventUserType = "", eventPlace = "", eventStartDate = "", eventEndDate = "", editEvent = "", eventId = "";
    private String yearsOfMonth = "", day, years = "", startSeelectedDate = "", nextDayDate = "", currencySymbol = "", currencyCode = "";
    private int hour, min, sec;
    private int count = -1;
    private boolean isPaymentDone;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        init();
        session = new Session(this);
        if (getIntent().getStringExtra(Constant.editEvent) != null) {
            editEvent = getIntent().getStringExtra(Constant.editEvent);
            eventId = getIntent().getStringExtra("eventId");

            if (editEvent.equals(Constant.editEvent)) {
                profile_action_bar.setText("Edit Event");
                tv_invite_n_create_friend.setVisibility(View.GONE);
                tv_update_event.setVisibility(View.VISIBLE);
                myEventRequestEvent(eventId);
            }
        }

        friendList = new ArrayList<>();
        currencyList = new ArrayList<>();

        currencyList = Utils.loadCurrency(this);
        now = Calendar.getInstance();
        nowEnd = Calendar.getInstance();

        rg_join_event.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_male: {
                        rb_female.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_male.setTextColor(getResources().getColor(R.color.colorPrimary));
                        rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                        eventUserType = "1";
                        break;
                    }
                    case R.id.rb_female: {
                        rb_female.setTextColor(getResources().getColor(R.color.colorPurple));
                        rb_male.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                        eventUserType = "2";
                        break;
                    }
                    case R.id.rb_both: {
                        rb_female.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_male.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_both.setTextColor(getResources().getColor(R.color.colorPrimary));
                        eventUserType = "3";
                        break;
                    }
                }
            }
        });

        rg_privacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_private: {
                        rb_public.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_private.setTextColor(getResources().getColor(R.color.colorPrimary));
                        privacy = "2";
                        break;
                    }
                    case R.id.rb_public: {
                        rb_private.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_public.setTextColor(getResources().getColor(R.color.colorPrimary));
                        privacy = "1";
                        break;
                    }
                }
            }
        });

        rg_payment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_paid: {
                        rb_free.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                        payment = "1";
                        ly_paid_view.setVisibility(View.VISIBLE);

                        SignInInfo info = session.getUser();

                        if (!editEvent.equals(Constant.editEvent))
                            if(info.userDetail.bankAccountStatus.equals("1")){

                                updateBankInfoDialog(CreateEventActivity.this, getString(R.string.do_you_wanna_update_bank_info));

                            }else  if(info.userDetail.bankAccountStatus.equals("0")){

                                AddBankAccDialog(CreateEventActivity.this, getString(R.string.need_to_add_bank));
                            }


                        break;
                    }
                    case R.id.rb_free: {
                        rb_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                        payment = "2";
                        ly_paid_view.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        });

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                tv_location.setText(place.getAddress());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                eventPlace = String.valueOf(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        tv_invite_n_create_friend.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ly_event_end_date_time.setOnClickListener(this);
        ly_event_start_date_time.setOnClickListener(this);
        ly_currency.setOnClickListener(this);
        tv_update_event.setOnClickListener(this);
    }


    public void AddBankAccDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(CreateEventActivity.this, PaymentActivity.class);
                intent.putExtra("isPaymentDone",isPaymentDone);
                startActivityForResult(intent, Constant.AddBankAccRequestCode);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rb_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                payment = "2";
                ly_paid_view.setVisibility(View.GONE);
                rb_free.setChecked(true);
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateBankInfoDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Bank info");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(CreateEventActivity.this, PaymentActivity.class);
                intent.putExtra("updateBankInfo","updateBankInfo");
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isPaymentDone = data.getBooleanExtra("isPaymentDone",false);

            if(isPaymentDone){
                SignInInfo info = session.getUser();
                info.userDetail.bankAccountStatus = "1";
                session.createSession(info);
            }else {
                rb_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                payment = "2";
                ly_paid_view.setVisibility(View.GONE);
                rb_free.setChecked(true);
                //AddBankAccDialog(CreateEventActivity.this, getString(R.string.need_to_add_bank));
            }

        }
    }

    private void myEventRequestEvent(final String eventId) {
        loadingView.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        EventDetailsInfo detailsInfo = gson.fromJson(response, EventDetailsInfo.class);

                        rb_private.setClickable(false);
                        rb_public.setClickable(false);
                        rb_paid.setClickable(false);
                        rb_free.setClickable(false);
                        ed_user_limite.setEnabled(false);

                        rb_male.setClickable(false);
                        rb_female.setClickable(false);
                        rb_both.setClickable(false);


                        if (detailsInfo.Detail.privacy.equals("Public")) {
                            rb_public.setChecked(true);
                            privacy = "1";
                        } else if (detailsInfo.Detail.privacy.equals("Private")) {

                            rb_private.setChecked(true);
                            privacy = "2";
                        }

                        if (detailsInfo.Detail.eventUserType.equals("Male")) {
                            rb_male.setChecked(true);
                        } else if (detailsInfo.Detail.eventUserType.equals("Female")) {
                            rb_female.setChecked(true);
                        } else if (detailsInfo.Detail.eventUserType.equals("Both")) {
                            rb_both.setChecked(true);
                        }

                        if (detailsInfo.Detail.payment.equals("Paid")) {
                            payment = "1";
                            rb_paid.setChecked(true);
                            ly_paid_view.setVisibility(View.VISIBLE);
                            ly_currency.setEnabled(false);
                            ed_amount.setEnabled(false);
                            ed_user_limite.setEnabled(false);

                        } else if (detailsInfo.Detail.payment.equals("Free")) {
                            payment = "2";
                            rb_free.setChecked(true);
                        }
                        eventUserType = detailsInfo.Detail.eventUserType;

                        ed_event_name.setText(detailsInfo.Detail.eventName);
                        tv_location.setText(detailsInfo.Detail.eventPlace);

                        ed_user_limite.setText(detailsInfo.Detail.userLimit);

                        ed_amount.setText(detailsInfo.Detail.eventAmount);

                        eventStartDate = formateDateFromstring("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a", detailsInfo.Detail.eventStartDate);
                        eventEndDate = formateDateFromstring("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a", detailsInfo.Detail.eventEndDate);
                        tv_start_date_time.setText(eventStartDate);
                        tv_end_date_time.setText(eventEndDate);

                        years = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyy", detailsInfo.Detail.eventStartDate);
                        yearsOfMonth = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "MM", detailsInfo.Detail.eventStartDate);
                        day = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "dd", detailsInfo.Detail.eventStartDate);

                        hour = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "HH", detailsInfo.Detail.eventStartDate));
                        min = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "mm", detailsInfo.Detail.eventStartDate));
                        sec = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "ss", detailsInfo.Detail.eventStartDate));

                        startSeelectedDate = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", detailsInfo.Detail.eventStartDate);

                        for (CurrencyInfo info : currencyList) {
                            if (info.code.equals(detailsInfo.Detail.currencyCode)) {
                                tv_currency_name.setText(info.name_plural);
                            }
                        }

                        latitude = detailsInfo.Detail.eventLatitude;
                        longitude = detailsInfo.Detail.eventLongitude;
                        eventPlace = detailsInfo.Detail.eventPlace;

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loadingView.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("event/getEventDetail?eventId=" + eventId + "&detailType=" + "myEvent" + "");
    }

    private void updateEvent() {
        tv_update_event.setEnabled(false);
        loadingView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("eventName", ed_event_name.getText().toString().trim());
        map.put("eventStartDate", eventStartDate);
        map.put("eventPlace", eventPlace);
        map.put("eventEndDate", eventEndDate);
        map.put("eventLatitude", latitude);
        map.put("eventLongitude", longitude);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                tv_update_event.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        updateEventDialog(CreateEventActivity.this, message);

                    } else if (status.equals("fail")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_update_event.setEnabled(true);
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                tv_update_event.setEnabled(true);
            }
        });
        service.callSimpleVolley("event/updateEvent", map);

    }


    public void updateEventDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    private void init() {
        tv_invite_n_create_friend = findViewById(R.id.tv_invite_n_create_friend);
        iv_back = findViewById(R.id.iv_back);
        ly_event_start_date_time = findViewById(R.id.ly_event_start_date_time);
        ly_event_end_date_time = findViewById(R.id.ly_event_end_date_time);
        ly_paid_view = findViewById(R.id.ly_paid_view);
        ly_currency = findViewById(R.id.ly_currency);
        tv_start_date_time = findViewById(R.id.tv_start_date_time);
        tv_end_date_time = findViewById(R.id.tv_end_date_time);
        tv_location = findViewById(R.id.tv_location);
        tv_currency_name = findViewById(R.id.tv_currency_name);
        ed_event_name = findViewById(R.id.ed_event_name);
        ed_user_limite = findViewById(R.id.ed_user_limite);
        loadingView = findViewById(R.id.loadingView);
        ed_amount = findViewById(R.id.ed_amount);

        rg_join_event = findViewById(R.id.rg_join_event);
        rg_privacy = findViewById(R.id.rg_privacy);
        rg_payment = findViewById(R.id.rg_payment);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        rb_both = findViewById(R.id.rb_both);
        rb_private = findViewById(R.id.rb_private);
        rb_public = findViewById(R.id.rb_public);
        rb_paid = findViewById(R.id.rb_paid);
        rb_free = findViewById(R.id.rb_free);
        profile_action_bar = findViewById(R.id.profile_action_bar);
        tv_update_event = findViewById(R.id.tv_update_event);

        // by default parameter...
        privacy = "1";
        payment = "2";
        eventUserType = "3";

        inputFilter(ed_amount);
    }

    private void selectCurrency() {

        final ArrayList<String> alist = new ArrayList<>();
        for (int i = 0; i < currencyList.size(); i++)
            alist.add(currencyList.get(i).name_plural);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateEventActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, alist);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        String cur_name = alist.get(position);
                        tv_currency_name.setText(cur_name);

                        for (int j = 0; j < currencyList.size(); j++) {
                            if (cur_name.equals(currencyList.get(j).name_plural)) {
                                CurrencyInfo currencyInfo = currencyList.get(j);
                                currencySymbol = currencyInfo.symbol;
                                currencyCode = currencyInfo.code;
                            }
                        }

                        dialog.dismiss();
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setHeader(R.layout.select_currency_layout)

                .create();

        dialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_invite_n_create_friend: {
                if (isValid()) inviteFriendsDialog();
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.ly_event_start_date_time: {
                setStartEventDate(tv_start_date_time);
                break;
            }
            case R.id.ly_event_end_date_time: {
                if (!TextUtils.isEmpty(tv_start_date_time.getText().toString())) {
                    setEndEventDate(tv_end_date_time);
                } else {
                    Utils.openAlertDialog(this, "Please select event start time first");
                }

                break;
            }
            case R.id.ly_currency: {
                Utils.hideSoftKeyboard(this);
                selectCurrency();
                break;
            }
            case R.id.tv_update_event: {
                updateEvent();
                break;
            }
        }
    }

    private void setStartEventDate(final TextView view) {
        Calendar calendar = Calendar.getInstance();

        Date tDate = new Date();
        calendar.setTime(tDate);

        int currentHour = tDate.getHours(); //calendar.get(Calendar.HOUR);
        int currentMinutes = tDate.getMinutes(); //calendar.get(Calendar.MINUTE);
        int currentSeconds = tDate.getSeconds(); //calendar.get(Calendar.SECOND);

        fromDate = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                years = String.valueOf(year);
                if (monthOfYear + 1 < 10) {
                    yearsOfMonth = "0" + (monthOfYear + 1);
                } else {
                    yearsOfMonth = String.valueOf((monthOfYear + 1));
                }

                if (dayOfMonth < 10) {
                    //day = "0"+(dayOfMonth + 1);
                    day = "0" + (dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                startSeelectedDate = year + "-" + (yearsOfMonth) + "-" + day;
                //String newStartSelectedDate = day + "/" + (yearsOfMonth) + "/" + year;
                setStartEventTime(view, startSeelectedDate, year, monthOfYear, dayOfMonth);
                nextDayDate = getNextDay(startSeelectedDate);

            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        // checking time is in 8pm or out of 8pm
        if (currentHour >= 20 && currentMinutes >= 00 && currentSeconds >= 00) {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
            fromDate.setMinDate(calendarDate);
        } else {
            fromDate.setMinDate(Calendar.getInstance());
        }


        fromDate.show(getFragmentManager(), "");
        fromDate.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fromDate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                fromDate.dismiss();
            }
        });
    }

    private String getNextDay(String anyDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            //Setting the date to the given date
            c.setTime(sdf.parse(anyDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Number of Days to add
        c.add(Calendar.DAY_OF_MONTH, 1);
        //Date after adding the days to the given date
        String nextDay = sdf.format(c.getTime());

        return nextDay;
    }

    private void setStartEventTime(final TextView textView,
                                   final String date, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        Date tDate = new Date();
        SimpleDateFormat simpDate;
        simpDate = new SimpleDateFormat("hh:mm:ss");
        System.out.println(simpDate.format(tDate));
        calendar.setTime(tDate);

        int currentHour = tDate.getHours(); //calendar.get(Calendar.HOUR);
        int currentMinutes = tDate.getMinutes(); //calendar.get(Calendar.MINUTE);
        int currentSeconds = tDate.getSeconds(); //calendar.get(Calendar.SECOND);

        myTime = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                hour = hourOfDay;
                min = minute;
                sec = second;
                String format;
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                } else if (hourOfDay == 12) {
                    format = "PM";
                } else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                String hour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String min = minute < 10 ? "0" + minute : "" + minute;
                String time = hour + ":" + min;

                try {
                    if (time != null) {
                        eventStartDate = date + " " + time + format;

                        textView.setText(date + " " + time + " " + format);
                        tv_end_date_time.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        myTime.show(getFragmentManager(), "");
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        boolean bool = DateUtils.isToday(cal.getTimeInMillis());
        if (bool) {

        }

        myTime.setMinTime(20, 00, 00);
        myTime.setMaxTime(20, 00, 00);
        myTime.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                myTime.dismiss();
            }
        });
    }

    private void setEndEventDate(final TextView view) {
        fromDate = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String yearsOfMonth;
                String day;
                int years = year;
                if (monthOfYear + 1 < 10) {
                    yearsOfMonth = "0" + (monthOfYear + 1);
                } else {
                    yearsOfMonth = String.valueOf((monthOfYear + 1));
                }

                if (dayOfMonth < 10) {
                    // day = "0"+(dayOfMonth + 1);
                    day = "0" + (dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                String date = year + "-" + (yearsOfMonth) + "-" + day;
                //String newformatDate =  day + "/" + (yearsOfMonth) + "/" + year;
                setEndEventTime(view, date, hour, min);
            }
        }, nowEnd.get(Calendar.YEAR), nowEnd.get(Calendar.MONTH), nowEnd.get(Calendar.DAY_OF_MONTH));
        Calendar calendar = Calendar.getInstance();
        if (hour == 23 && min >= 30) {
            calendar.set(Integer.parseInt(years), Integer.parseInt(yearsOfMonth) - 1, Integer.parseInt(day) + 1);
        } else {
            calendar.set(Integer.parseInt(years), Integer.parseInt(yearsOfMonth) - 1, Integer.parseInt(day));
        }

        fromDate.setMinDate(calendar);
        fromDate.show(getFragmentManager(), "");
        fromDate.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fromDate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                fromDate.dismiss();
            }
        });
    }

    private void setEndEventTime(final TextView textView, final String date, int hour, int min) {
        int tempHour = 0;
        myEndTime = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String format;
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                } else if (hourOfDay == 12) {
                    format = "PM";
                } else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                String hour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String min = minute < 10 ? "0" + minute : "" + minute;
                String sec = String.valueOf(second);
                String time = hour + ":" + min;

                try {
                    if (time != null) {
                        // eventStartDate = date +" "+ time;
                        eventEndDate = date + " " + time + format;
                        textView.setText(date + " " + time + " " + format);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, nowEnd.get(Calendar.HOUR_OF_DAY), nowEnd.get(Calendar.MINUTE), false);

        myEndTime.show(getFragmentManager(), "");

        if (startSeelectedDate.equals(date)) {
            if (this.min + 30 >= 60) {
                tempHour = this.hour + 1;
                myEndTime.setMinTime(tempHour, this.min + 30, sec);
            } else {
                myEndTime.setMinTime(this.hour, this.min + 30, sec);
            }

        } else if (nextDayDate.equals(date)) {
            if (this.hour == 23 && this.min >= 30) {
                if (this.min + 30 >= 60) {
                    tempHour = this.hour + 1;
                    myEndTime.setMinTime(tempHour, this.min + 30, sec);
                } else {
                    myEndTime.setMinTime(this.hour, this.min + 30, sec);
                }
            }
        }

        myEndTime.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                myEndTime.dismiss();
            }
        });
    }

    private void inviteFriendsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.invite_friend_layout);
        dialog.setCancelable(false);

        LinearLayout ly_no_friend_found = dialog.findViewById(R.id.ly_no_friend_found);
        TextView create_event = dialog.findViewById(R.id.create_event);
        RecyclerView recycler_view = dialog.findViewById(R.id.recycler_view);
        RelativeLayout rl_invite_all = dialog.findViewById(R.id.rl_invite_all);
        ImageView cancel_popup = dialog.findViewById(R.id.cancel_popup);
        final ImageView iv_is_checked_all = dialog.findViewById(R.id.iv_is_checked_all);
        InsLoadingView loading_view = dialog.findViewById(R.id.loading_view);

      /*  if(isCheckedAll){
            iv_is_checked_all.setImageResource(R.drawable.ico_not_checked);
        }else {
            iv_is_checked_all.setImageResource(R.drawable.ico_not_unchecked);
        }
*/

       /* final InviteFrienAdapter adapter = new InviteFrienAdapter(this, privacy, friendList, friendsIds, new FriedsIdsListner() {
            @Override
            public void getIds(String ids) {
                friendsIds = ids;
            }
        });*/


        rl_invite_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCheckedAll) {
                    for (int i = 0; i < friendList.size(); i++) {

                        if (privacy.equals("1")) { // for public case

                            if (friendList.get(i).gender.equals("2")) { // FOR GIRL

                                if (friendList.get(i).eventInvitation.equals("2")) {

                                    removeNotAllowedIds(i);
                                } else {
                                    friendList.get(i).isSelected = true;
                                    friendsIds = friendList.get(i).userId + "," + friendsIds;
                                }
                            } else {
                                friendList.get(i).isSelected = true;
                                friendsIds = friendList.get(i).userId + "," + friendsIds;
                            }
                        } else if (privacy.equals("2")) {

                            if (friendList.get(i).gender.equals("2")) {

                                if (friendList.get(i).eventInvitation.equals("1")) {

                                    removeNotAllowedIds(i);

                                } else {
                                    friendList.get(i).isSelected = true;
                                    friendsIds = friendList.get(i).userId + "," + friendsIds;
                                }

                            } else {
                                friendList.get(i).isSelected = true;
                                friendsIds = friendList.get(i).userId + "," + friendsIds;
                            }

                        }

                    }
                    iv_is_checked_all.setImageResource(R.drawable.ico_not_checked);
                    isCheckedAll = true;
                } else {
                    for (int i = 0; i < friendList.size(); i++) {
                        friendList.get(i).isSelected = false;
                        friendsIds = "";
                    }
                    iv_is_checked_all.setImageResource(R.drawable.ico_not_unchecked);
                    isCheckedAll = false;
                }
                if (friendsIds.length() != 0) friendsIds = removeLastChar(friendsIds);
                //adapter.notifyDataSetChanged();
            }

            private void removeNotAllowedIds(int i) {
                if (friendsIds.contains(friendList.get(i).userId)) {
                    friendsIds = friendsIds.replace(friendList.get(i).userId, "");
                } else if (friendsIds.contains("," + friendsIds.contains(friendList.get(i).userId))) {
                    friendsIds = friendsIds.replace(("," + friendsIds.contains(friendList.get(i).userId)), "");
                } else if (friendsIds.contains(friendList.get(i).userId + ",")) {
                    friendsIds = friendsIds.replace((friendList.get(i).userId + ","), "");
                }
            }
        });

        friendList.clear();
        //showFriendList(loading_view, adapter, ly_no_friend_found, recycler_view);

        cancel_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                friendsIds = "";
            }
        });

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create event api calling here...
                if (!TextUtils.isEmpty(friendsIds)) {
                    createEvent();
                    dialog.dismiss();
                } else {
                    Utils.openAlertDialog(CreateEventActivity.this, getString(R.string.event_invitaion));
                }

            }
        });
       // recycler_view.setAdapter(adapter);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }


    public interface FriedsIdsListner {
        void getIds(String ids);
    }

    private String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private void showFriendList(final InsLoadingView loading_view, final InviteFrienAdapter adapter, final LinearLayout ly_no_friend_found, final RecyclerView recycler_view) {
        loading_view.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    friendList.clear();

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        MyFriendListInfo friendListInfo = gson.fromJson(response, MyFriendListInfo.class);

                        switch (eventUserType) {
                            case "1": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("1")) {
                                        friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "2": {
                                for (int i = 0; i < friendListInfo.List.size(); i++) {
                                    if (friendListInfo.List.get(i).gender.equals("2")) {
                                        friendList.add(friendListInfo.List.get(i));
                                    }
                                }
                                break;
                            }
                            case "3": {
                                friendList.addAll(friendListInfo.List);
                                break;
                            }
                        }

                        if (friendList.size() == 0) {
                            ly_no_friend_found.setVisibility(View.VISIBLE);
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            ly_no_friend_found.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {

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
        service.callGetSimpleVolley("user/getFriendList?offset=0&limit=20&listType=friend&eventId=" + eventId + "");
    }

    private boolean isValid() {
        Validation v = new Validation();
        if (!v.isNull(ed_event_name)) {
            Utils.openAlertDialog(this, getString(R.string.enter_event_name));
            return false;
        } else if (!v.isNull(tv_location)) {
            Utils.openAlertDialog(this, getString(R.string.event_location));
            return false;
        } else if (!v.isNull(tv_start_date_time)) {
            Utils.openAlertDialog(this, getString(R.string.start_event_date_time));
            return false;
        } else if (!v.isNull(tv_end_date_time)) {
            Utils.openAlertDialog(this, getString(R.string.end_event_date_time));
            return false;
        } else if (payment.equals("1")) {
            if (!v.isNull(tv_currency_name)) {
                Utils.openAlertDialog(this, "Please select currency");
                return false;
            } else if (!v.isNull(ed_amount)) {
                Utils.openAlertDialog(this, "Please enter amount");
                return false;
            } else if (!v.isDoublezero(ed_amount)) {
                Utils.openAlertDialog(this, "Amount can't be zero");
                return false;
            } else if (!v.isNull(ed_user_limite)) {
                Utils.openAlertDialog(this, getString(R.string.max_user_limite));
                return false;
            } else if (!v.isDoublezero(ed_user_limite)) {
                Utils.openAlertDialog(this, "User limit can not be zero");
                return false;
            }
        } else if (!v.isNull(ed_user_limite)) {
            Utils.openAlertDialog(this, getString(R.string.max_user_limite));
            return false;
        } else if (!v.isDoublezero(ed_user_limite)) {
            Utils.openAlertDialog(this, "User limit can not be zero");
            return false;
        }
        return true;
    }

    private void createEvent() {
        loadingView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("eventName", ed_event_name.getText().toString().trim());
        map.put("eventStartDate", eventStartDate);
        map.put("eventEndDate", eventEndDate);
        map.put("eventPlace", eventPlace);
        map.put("eventLatitude", latitude);
        map.put("eventLongitude", longitude);
        map.put("privacy", privacy);
        map.put("payment", payment);
        map.put("userLimit", ed_user_limite.getText().toString().trim());
        map.put("eventUserType", eventUserType);
        map.put("inviteFriendId", friendsIds);
        map.put("eventAmount", ed_amount.getText().toString().trim());
        map.put("currencySymbol", currencySymbol);
        map.put("currencyCode", currencyCode);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        eventCreatedDialog(CreateEventActivity.this);

                    } else if (status.equals("fail")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("event/createEvent", map);

    }

    public void eventCreatedDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(R.string.event_created);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void inputFilter(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 0) {
                    String str = et.getText().toString();
                    et.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                count--;
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(100);
                                et.setFilters(fArray);
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
