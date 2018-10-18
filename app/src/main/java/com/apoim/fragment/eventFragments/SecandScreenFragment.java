package com.apoim.fragment.eventFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.activity.event.CreateNewEventActivity;
import com.apoim.activity.payment_subscription.PaymentActivity;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.CurrencyInfo;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.EventFilterData;
import com.apoim.modal.SignInInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.apoim.activity.event.CreateEventActivity.RattingIds;
import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by mindiii on 14/9/18.
 */

public class SecandScreenFragment extends Fragment {

    private TextView tv_next_secand,tv_select_background_two,tv_two;
    private ImageView iv_right_two;
    private Context mContext;
    private TextView tv_one;
    private ImageView iv_right_one;

    private RadioGroup rg_join_event;
    private RadioGroup rg_privacy;
    private RadioGroup rg_payment;

    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioButton rb_both;
    private RadioButton rb_all;
    private RadioButton rb_private;
    private RadioButton rb_public;
    private RadioButton rb_paid;
    private RadioButton rb_free;

    private String  eventUserType = "", privacy = "", payment = "", editEvent = "", currencySymbol = "", currencyCode = "";
    private LinearLayout ly_paid_view;
    private Session session;
    private boolean isPaymentDone;
    private EditText ed_user_limite,ed_amount;
    private TextView tv_currency_name;
    private RelativeLayout ly_currency;
    private ArrayList<CurrencyInfo> currencyList;
    private int count = -1;
    private ImageView iv_groupchat_toggle;
    private String groupChat = "1";

    public static SecandScreenFragment newInstance() {
        Bundle args = new Bundle();
        SecandScreenFragment fragment = new SecandScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_secand_screen, container, false);

        session = new Session(mContext);
        init(view);

        tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_two.setTextColor(ContextCompat.getColor(mContext,R.color.white));

        currencyList = new ArrayList<>();
        currencyList = Utils.loadCurrency(mContext);

        if (CreateNewEventActivity.isForUpdateEvent) {
            // for update event
            EventDetailsInfo.DetailBean bean = session.getcreateEventInfo();

            rb_private.setClickable(false);
            rb_public.setClickable(false);
            rb_paid.setClickable(false);
            rb_free.setClickable(false);
            ed_user_limite.setEnabled(false);

            rb_male.setClickable(false);
            rb_female.setClickable(false);
            rb_both.setClickable(false);
            rb_all.setClickable(false);


            if (bean.privacy.equals("Public")) {
                rb_public.setChecked(true);
                privacy = "1";
            } else if (bean.privacy.equals("Private")) {

                rb_private.setChecked(true);
                privacy = "2";
            }

            if (bean.eventUserType.equals("Male")) {
                rb_male.setChecked(true);
                eventUserType = "1";
            } else if (bean.eventUserType.equals("Female")) {
                rb_female.setChecked(true);
                eventUserType = "2";
            } else if (bean.eventUserType.equals("Both")) {
                rb_both.setChecked(true);
                eventUserType = "3";
            } else if(bean.eventUserType.equals("")){
                rb_all.setChecked(true);
                eventUserType = "";
            }

            if (bean.payment.equals("Paid")) {
                payment = "1";
                rb_paid.setChecked(true);
                ly_paid_view.setVisibility(View.VISIBLE);
                ly_currency.setEnabled(false);
                ed_amount.setEnabled(false);
                ed_user_limite.setEnabled(false);

            } else if (bean.payment.equals("Free")) {
                payment = "2";
                rb_free.setChecked(true);
            }


            ed_user_limite.setText(bean.userLimit);

            ed_amount.setText(bean.eventAmount);

            for (CurrencyInfo info : currencyList) {
                if (info.code.equals(bean.currencyCode)) {
                    tv_currency_name.setText(info.name_plural);
                }
            }
        }

        tv_next_secand.setOnClickListener(view1 -> {

            if( isValid()){
                tv_select_background_two.setBackgroundResource(R.drawable.primary_circle_solid);
                iv_right_two.setVisibility(View.VISIBLE);
                tv_two.setVisibility(View.GONE);

                EventDetailsInfo.DetailBean info = session.getcreateEventInfo();
                info.userLimit = ed_user_limite.getText().toString().trim();
                info.privacy = privacy;
                info.eventUserType = eventUserType;
                info.payment = payment;
                info.currencyCode = currencyCode;
                info.currencySymbol = currencySymbol;
                info.eventAmount = ed_amount.getText().toString().trim();
                info.groupChat = groupChat;

                session.createEventInfo(info);
                EventFilterData data = new EventFilterData();
                session.createFilterData(data);
                RattingIds = "";
                friendsIds = "";

                addFragment(ThiredFragment.newInstance(eventUserType,privacy), true, R.id.event_fragment_place);
            }

        });

        rg_join_event.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_male: {
                    rb_female.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_male.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    eventUserType = "1";
                    break;
                }
                case R.id.rb_female: {
                    rb_female.setTextColor(getResources().getColor(R.color.colorPurple));
                    rb_male.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    eventUserType = "2";
                    break;
                }
                case R.id.rb_both: {
                    rb_female.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_male.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_all.setTextColor(getResources().getColor(R.color.colorBlack));
                    eventUserType = "3";
                    break;
                }
                case R.id.rb_all: {
                    rb_female.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_male.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_both.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_all.setTextColor(getResources().getColor(R.color.colorPrimary));
                    eventUserType = "";
                    break;
                }
            }
        });

        rg_privacy.setOnCheckedChangeListener((radioGroup, i) -> {
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
        });

        rg_payment.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_paid: {
                    rb_free.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                    payment = "1";
                    ly_paid_view.setVisibility(View.VISIBLE);

                    SignInInfo info = session.getUser();

                    if (!editEvent.equals(Constant.editEvent))
                        if(info.userDetail.bankAccountStatus.equals("1")){

                            updateBankInfoDialog(mContext, getString(R.string.do_you_wanna_update_bank_info));

                        }else  if(info.userDetail.bankAccountStatus.equals("0")){

                            AddBankAccDialog(mContext, getString(R.string.need_to_add_bank));
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
        });

        ly_currency.setOnClickListener(view12 -> {
            Utils.hideSoftKeyboard(getActivity());
            selectCurrency();
        });

        iv_groupchat_toggle.setOnClickListener(view1 -> {

            if(groupChat.equals("1")){
                groupChat = "0";
                iv_groupchat_toggle.setImageResource(R.drawable.ico_set_toggle_off);
            }
            else {
                groupChat = "1";
                iv_groupchat_toggle.setImageResource(R.drawable.ico_set_toggle_on);
            }
        });

        return view;
    }

    private void init(View view) {
        tv_next_secand = view.findViewById(R.id.tv_next_secand);
        tv_select_background_two = getActivity().findViewById(R.id.tv_select_background_two);
        tv_two = getActivity().findViewById(R.id.tv_two);
        iv_right_two = getActivity().findViewById(R.id.iv_right_two);
        tv_one = getActivity().findViewById(R.id.tv_one);
        iv_right_one = getActivity().findViewById(R.id.iv_right_one);

        rg_join_event = view.findViewById(R.id.rg_join_event);
        rg_privacy = view.findViewById(R.id.rg_privacy);
        rg_payment = view.findViewById(R.id.rg_payment);
        ly_paid_view = view.findViewById(R.id.ly_paid_view);
        ed_user_limite = view.findViewById(R.id.ed_user_limite);
        tv_currency_name = view.findViewById(R.id.tv_currency_name);
        ly_currency = view.findViewById(R.id.ly_currency);
        ed_amount = view.findViewById(R.id.ed_amount);
        iv_groupchat_toggle = view.findViewById(R.id.iv_groupchat_toggle);

        rb_male = view.findViewById(R.id.rb_male);
        rb_female = view.findViewById(R.id.rb_female);
        rb_both = view.findViewById(R.id.rb_both);
        rb_private = view.findViewById(R.id.rb_private);
        rb_public = view.findViewById(R.id.rb_public);
        rb_paid = view.findViewById(R.id.rb_paid);
        rb_free = view.findViewById(R.id.rb_free);
        rb_all = view.findViewById(R.id.rb_all);

        // by default parameter...
        privacy = "1";
        payment = "2";
        eventUserType = "";
        inputFilter(ed_amount);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void selectCurrency() {

        final ArrayList<String> alist = new ArrayList<>();
        for (int i = 0; i < currencyList.size(); i++)
            alist.add(currencyList.get(i).name_plural);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, alist);

        DialogPlus dialog = DialogPlus.newDialog(mContext)
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

    private boolean isValid() {
        Validation v = new Validation();
        if (payment.equals("1")) {
            if (!v.isNull(tv_currency_name)) {
                Utils.openAlertDialog(mContext, "Please select currency");
                return false;
            } else if (!v.isNull(ed_amount)) {
                Utils.openAlertDialog(mContext, "Please enter amount");
                return false;
            } else if (!v.isDoublezero(ed_amount)) {
                Utils.openAlertDialog(mContext, "Amount can't be zero");
                return false;
            } else if (!v.isNull(ed_user_limite)) {
                Utils.openAlertDialog(mContext, getString(R.string.max_user_limite));
                return false;
            } else if (!v.isDoublezero(ed_user_limite)) {
                Utils.openAlertDialog(mContext, "User limit can not be zero");
                return false;
            }
        } else if (!v.isNull(ed_user_limite)) {
            Utils.openAlertDialog(mContext, getString(R.string.max_user_limite));
            return false;
        } else if (!v.isDoublezero(ed_user_limite)) {
            Utils.openAlertDialog(mContext, "User limit can not be zero");
            return false;
        }
        return true;
    }

    public void AddBankAccDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Intent intent = new Intent(mContext, PaymentActivity.class);
            intent.putExtra("isPaymentDone",isPaymentDone);
            startActivityForResult(intent, Constant.AddBankAccRequestCode);
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            rb_paid.setTextColor(getResources().getColor(R.color.colorBlack));
            rb_free.setTextColor(getResources().getColor(R.color.colorPrimary));
            payment = "2";
            ly_paid_view.setVisibility(View.GONE);
            rb_free.setChecked(true);
            dialogInterface.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public void updateBankInfoDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Bank info");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Intent intent = new Intent(mContext, PaymentActivity.class);
            intent.putExtra("updateBankInfo","updateBankInfo");
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
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
        tv_select_background_two.setBackgroundResource(R.drawable.holo_circle_border);
        tv_two.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        iv_right_one.setVisibility(View.GONE);
        tv_one.setVisibility(View.VISIBLE);

        SecandScreenFragment  youTubePlayerFragment = (SecandScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);
        if (youTubePlayerFragment != null)
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();

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
