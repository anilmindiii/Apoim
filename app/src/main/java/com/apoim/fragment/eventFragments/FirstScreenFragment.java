package com.apoim.fragment.eventFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.event.CreateNewEventActivity;
import com.apoim.activity.event.SelectEventPlaceActivity;
import com.apoim.activity.sign_signup.CreateAccountActivity;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.CurrencyInfo;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.app.Activity.RESULT_OK;
import static com.apoim.util.Utils.formateDateFromstring;

/**
 * Created by mindiii on 11/9/18.
 */

public class FirstScreenFragment extends Fragment implements View.OnClickListener {
    private TextView tv_next_first, tv_one;
    private ImageView iv_right_one;
    private Context mContext;
    private LinearLayout ly_is_buz_added;
    private EditText ed_event_name;
    private TextView tv_start_date_time, tv_end_date_time, tv_location;
    private RelativeLayout ly_event_start_date_time, ly_event_end_date_time;
    private DatePickerDialog fromDate;
    private String latitude = "", longitude = "", businessId = "", payment = "", eventUserType = "", eventPlace = "", eventStartDate = "", eventEndDate = "", editEvent = "", eventId = "";
    private String yearsOfMonth = "", day, years = "", startSeelectedDate = "", nextDayDate = "", currencySymbol = "", currencyCode = "";
    private Calendar now, nowEnd;
    private TimePickerDialog myTime, myEndTime;
    private int hour, min, sec;
    private Session session;
    private InsLoadingView loadingView;
    private Bitmap bitmap;
    ImageView eventImage, camera_icon,event_place_image;
    String base64Image;

    public static FirstScreenFragment newInstance(String eventId) {

        Bundle args = new Bundle();

        FirstScreenFragment fragment = new FirstScreenFragment();
        fragment.setArguments(args);
        args.putString("eventId", eventId);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_first_screen, container, false);
        init(view);

        eventImage = view.findViewById(R.id.eventImage);
        camera_icon = view.findViewById(R.id.camera_icon);
        event_place_image = view.findViewById(R.id.event_place_image);

        if (CreateNewEventActivity.isForUpdateEvent) {
            eventId = getArguments().getString(Constant.eventId);
            if (eventId != null)
                myEventRequestEvent(eventId);
        }


        eventImage.setOnClickListener(view1 -> {
            getPermissionAndPicImage();
        });

        tv_next_first.setOnClickListener(view12 -> {
            if (isValid()) {
                tv_one.setVisibility(View.GONE);
                iv_right_one.setVisibility(View.VISIBLE);

                EventDetailsInfo.DetailBean bean = new EventDetailsInfo.DetailBean();
                bean.eventName = ed_event_name.getText().toString().trim();
                bean.eventStartDate = eventStartDate;
                bean.eventEndDate = eventEndDate;
                bean.eventLatitude = latitude;
                bean.eventLongitude = longitude;
                bean.eventPlace = eventPlace;
                bean.firstImage = base64Image;
                bean.businessId = businessId;

                if (!CreateNewEventActivity.isForUpdateEvent) {
                    session.createEventInfo(bean);
                }
                addFragment(new SecandScreenFragment(), true, R.id.event_fragment_place);
            }

        });

        ly_is_buz_added.setOnClickListener(view13 -> {
            Intent intent = new Intent(mContext, SelectEventPlaceActivity.class);
            startActivityForResult(intent, Constant.EventPlaceRequestCode);
        });

        return view;
    }

    private void init(View view) {
        tv_next_first = view.findViewById(R.id.tv_next_first);
        tv_one = getActivity().findViewById(R.id.tv_one);
        iv_right_one = getActivity().findViewById(R.id.iv_right_one);

        ly_is_buz_added = view.findViewById(R.id.ly_is_buz_added);
        ed_event_name = view.findViewById(R.id.ed_event_name);
        tv_end_date_time = view.findViewById(R.id.tv_end_date_time);
        tv_start_date_time = view.findViewById(R.id.tv_start_date_time);
        tv_location = view.findViewById(R.id.tv_location);
        ly_event_start_date_time = view.findViewById(R.id.ly_event_start_date_time);
        ly_event_end_date_time = view.findViewById(R.id.ly_event_end_date_time);
        loadingView = view.findViewById(R.id.loadingView);

        ly_event_start_date_time.setOnClickListener(this);
        ly_event_end_date_time.setOnClickListener(this);

        session = new Session(mContext);

        tv_one.setVisibility(View.VISIBLE);
        iv_right_one.setVisibility(View.GONE);

        now = Calendar.getInstance();
        nowEnd = Calendar.getInstance();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.EventPlaceRequestCode) {
                String eventAddress = data.getStringExtra("eventAddress");
                String eventlatitude = data.getStringExtra("eventlatitude");
                String eventlogitude = data.getStringExtra("eventlogitude");
                String eventplaceImage = data.getStringExtra("eventplaceImage");
                businessId = data.getStringExtra("businessId");

                tv_location.setText(eventAddress);
                eventPlace = eventAddress;
                latitude = eventlatitude;
                longitude = eventlogitude;


                if(eventplaceImage != null){
                    Glide.with(mContext).load(eventplaceImage).apply(new RequestOptions().placeholder(R.drawable.map_event)).into(event_place_image);

                }else {
                    event_place_image.setImageResource(R.drawable.map_event);
                }
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                camera_icon.setVisibility(View.GONE);
                bitmap = ImagePicker.getImageFromResult(mContext, requestCode, resultCode, data);

                if (bitmap != null) {
                    eventImage.setImageBitmap(bitmap);
                    base64Image = setBitmap(bitmap);
                }
            }
        }
    }


    private String setBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }



    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_event_start_date_time:
                setStartEventDate(tv_start_date_time);
                break;

            case R.id.ly_event_end_date_time:
                if (!TextUtils.isEmpty(tv_start_date_time.getText().toString())) {
                    setEndEventDate(tv_end_date_time);
                } else {
                    Utils.openAlertDialog(mContext, "Please select event start time first");
                }
                break;
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


        fromDate.show(getActivity().getFragmentManager(), "");
        fromDate.setAccentColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        fromDate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                fromDate.dismiss();
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
        fromDate.show(getActivity().getFragmentManager(), "");
        fromDate.setAccentColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        fromDate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                fromDate.dismiss();
            }
        });
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
        myTime.show(getActivity().getFragmentManager(), "");
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

        myEndTime.show(getActivity().getFragmentManager(), "");

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirstScreenFragment youTubePlayerFragment = (FirstScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);

        if (youTubePlayerFragment != null) {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }
    }

    private boolean isValid() {
        Validation v = new Validation();

        if (bitmap == null) {
            Utils.openAlertDialog(mContext, "Please select event image");
            return false;
        } else if (!v.isNull(ed_event_name)) {
            Utils.openAlertDialog(mContext, getString(R.string.enter_event_name));
            return false;
        } else if (!v.isNull(tv_start_date_time)) {
            Utils.openAlertDialog(mContext, getString(R.string.start_event_date_time));
            return false;
        } else if (!v.isNull(tv_end_date_time)) {
            Utils.openAlertDialog(mContext, getString(R.string.end_event_date_time));
            return false;
        } else if (!v.isNull(tv_location)) {
            Utils.openAlertDialog(mContext, getString(R.string.event_location));
            return false;
        }
        return true;
    }

    private void myEventRequestEvent(final String eventId) {
        loadingView.setVisibility(View.VISIBLE);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        Gson gson = new Gson();
                        EventDetailsInfo detailsInfo = gson.fromJson(response, EventDetailsInfo.class);
                        session.createEventInfo(detailsInfo.Detail);

                        EventDetailsInfo.DetailBean bean = session.getcreateEventInfo();

                        eventStartDate = formateDateFromstring("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a", bean.eventStartDate);
                        eventEndDate = formateDateFromstring("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a", bean.eventEndDate);
                        tv_start_date_time.setText(eventStartDate);
                        tv_end_date_time.setText(eventEndDate);

                        years = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyy", bean.eventStartDate);
                        yearsOfMonth = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "MM", bean.eventStartDate);
                        day = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "dd", bean.eventStartDate);

                        hour = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "HH", bean.eventStartDate));
                        min = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "mm", bean.eventStartDate));
                        sec = Integer.parseInt(formateDateFromstring("yyyy-MM-dd hh:mm:ss", "ss", bean.eventStartDate));

                        startSeelectedDate = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", bean.eventStartDate);

                        latitude = bean.eventLatitude;
                        longitude = bean.eventLongitude;
                        eventPlace = bean.eventPlace;

                        tv_location.setText(eventPlace);
                        ed_event_name.setText(bean.eventName);
                        ed_event_name.setSelection(bean.eventName.length());

                        /*
                        }*/

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
}
