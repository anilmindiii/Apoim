package com.apoim.activity.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.activity.payment_subscription.PaymentActivity;
import com.apoim.adapter.AddInterestAdapter;
import com.apoim.adapter.ProfileHeightAdapter;
import com.apoim.adapter.ProfileISpeakAdapter;
import com.apoim.adapter.ProfileRelationshipAdapter;
import com.apoim.adapter.ShowInterestAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.listener.GetInterestValueListener;
import com.apoim.listener.ProfileImageAdapterListener;
import com.apoim.listener.ProfileRelationListener;
import com.apoim.modal.GetOtherProfileInfo;
import com.apoim.modal.ProfileInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.ProfileItemInfo;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mindiii on 20/2/18.
 */

public class UserPersonalProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView user_add_interest, decline_button;
    private ImageButton user_profile_back;
    private ListView selector_interest_list_view;
    private RecyclerView user_selected_interest_list_view;
    private ArrayList<ProfileInterestInfo> interestList;
    private RelativeLayout rl_select_height, rl_select_weight, rl_select_relationship, rl_select_I_speak;
    private TextView user_height, user_weight, display_selected_unit, weight_unit, tv_user_relationship, user_I_speak;
    private Session session;
    private ArrayList<ProfileItemInfo> heightInfoList;
    private ArrayList<String> weightInfoList;
    private ArrayList<ProfileItemInfo> relationshipInfoList;
    private ArrayList<ProfileItemInfo> IspeakInfoList;
    private String selected_interest;
    private ArrayList<ProfileInterestInfo> interestInfoList;
    private TextView user_profile_button, profile_header_text;
    private EditText user_select_about_you;
    private String user_interests = "";
    private String unit = "";
    private ProfileInfo profileInfo;

    private String[] arrHeight = {"121.92 (4 feet 0 inches)", "127.00 (4 feet 1 inches)", "129.54 (4 feet 2 inches)", "132.08 (4 feet 3 inches)", "134.62 (4 feet 4 inches)", "137.16 (4 feet 5 inches)", "139.70 (4 feet 6 inches)", "142.24 (4 feet 7 inches)", "144.78 (4 feet 8 inches)", "147.32 (4 feet 9 inches)", "149.86 (4 feet 10 inches)", "149.86 (4 feet 11 inches)", "152.40 (5 feet 0 inches)", "154.94 (5 feet 1 inches)", "157.48 (5 feet 2 inches)", "160.02 (5 feet 3 inches)", "162.56 (5 feet 4 inches)", "165.10 (5 feet 5 inches)", "167.64 (5 feet 6 inches)", "170.18 (5 feet 7 inches)", "172.72 (5 feet 8 inches)", "175.26 (5 feet 9 inches)", "177.80 (5 feet 10 inches)", "180.34 (5 feet 11 inches)", "182.88 (6 feet 0 inches)", "185.42 (6 feet 1 inches)", "185.42 (6 feet 2 inches)", "190.50 (6 feet 3 inches)", "193.04 (6 feet 4 inches)", "195.58 (6 feet 5 inches)", "198.12 (6 feet 6 inches)", "200.66 (6 feet 7 inches)", "203.20 (6 feet 8 inches)", "208.28 (6 feet 9 inches)", "214.36 (6 feet 10 inches)", "220.44 (6 feet 11 inches)", "221.52 (7 feet 0 inches)", "232.60 (7 feet 1 inches)", "237.68 (7 feet 2 inches)", "244.76 (7 feet 3 inches)", "250.84 (7 feet 4 inches)", "256.92 (7 feet 5 inches)", "263.00 (7 feet 6 inches)", "268.08 (7 feet 7 inches)", "273.16 (7 feet 8 inches)", "278.24 (7 feet 9 inches)", "283.32 (7 feet 10 inches)", "288.40 (7 feet 11 inches)", "293.48 (8 feet 0 inches)", "298.56 (8 feet 1 inches)", "303.64 (8 feet 2 inches)", "308.72 (8 feet 3 inches)", "313.80 (8 feet 4 inches)", "318.88 (8 feet 5 inches)", "323.96 (8 feet 6 inches)", "329.04 (8 feet 7 inches)", "334.12 (8 feet 8 inches)", "339.20 (8 feet 9 inches)", "344.28 (8 feet 10 inches)", "349.28 (8 feet 11 inches)", "354.36 (9 feet 0 inches)", "359.44 (9 feet 1 inches)", "364.52 (9 feet 2 inches)", "369.60 (9 feet 3 inches)", "374.68 (9 feet 4 inches)", "379.76 (9 feet 5 inches)", "384.84 (9 feet 6 inches)", "389.92 (9 feet 7 inches)", "395.00 (9 feet 8 inches)", "400.08 (9 feet 9 inches)", "405.16 (9 feet 10 inches)", "410.24 (9 feet 11 inches)", "415.32 (10 feet 0 inches)"};
    private String[] arrRelatioship = {"Single", "Married", "Divorced", "Widowed"};
    private String[] arrISpeak = {"English", "Spanish", "French"};

    private String relationId, languageId = "", language = "";
    private ShowInterestAdapter showInterestAdapter;
    private GetOtherProfileInfo otherProfileInfo;
    private RadioGroup rg_apoim_type, rg_event_type;
    private RadioButton rb_event_type_paid, rb_event_type_free, rb_apoim_type_paid, rb_apoim_type_free;
    int checker = 0;
    String Apoim_payment = "2";
    String Event_payment = "2";
    private boolean isPaymentDone;
    private String fromToPayment = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_personal_profile);
        initView();

        profileInfo = new ProfileInfo();
        session = new Session(UserPersonalProfileActivity.this, this);
        interestInfoList = new ArrayList<>();
        heightInfoList = new ArrayList<>();

        // Weight Units List for spinner
        weightInfoList = new ArrayList<>();
        weightInfoList.add("Weight");
        weightInfoList.add("Kg");
        weightInfoList.add("Lbs");
        weightInfoList.add("Pounds");

        relationshipInfoList = new ArrayList<>();
        IspeakInfoList = new ArrayList<>();
        interestList = new ArrayList<>();

        // Height List from arrHeight String array
        ArrayList<String> heightList = new ArrayList<>();
        for (int i = 0; i < arrHeight.length; i++) {
            ProfileItemInfo profileHeightInfo = new ProfileItemInfo();
            arrHeight[i].split(",");
            heightList = new ArrayList<String>((Arrays.asList(arrHeight)));
            profileHeightInfo.name = heightList.get(i);
            profileHeightInfo.isChecked = false;
            heightInfoList.add(profileHeightInfo);
        }

        // Relation List from arrRelatioship String array
        ArrayList<String> relationList = new ArrayList<>();
        for (int i = 0; i < arrRelatioship.length; i++) {
            ProfileItemInfo profileRelationshipInfo = new ProfileItemInfo();
            arrRelatioship[i].split(",");
            relationList = new ArrayList<String>((Arrays.asList(arrRelatioship)));
            profileRelationshipInfo.name = relationList.get(i);
            profileRelationshipInfo.isChecked = false;
            relationshipInfoList.add(profileRelationshipInfo);
        }

        // I Speak List from arrISpeak String array
        ArrayList<String> ISpeakList = new ArrayList<>();
        for (int i = 0; i < arrISpeak.length; i++) {
            ProfileItemInfo profileISpeakInfo = new ProfileItemInfo();
            arrISpeak[i].split(",");
            ISpeakList = new ArrayList<String>((Arrays.asList(arrISpeak)));
            profileISpeakInfo.name = ISpeakList.get(i);
            profileISpeakInfo.Id = i + 1 + "";
            profileISpeakInfo.isChecked = false;
            IspeakInfoList.add(profileISpeakInfo);
        }

        user_add_interest.setOnClickListener(this);
        user_profile_back.setOnClickListener(this);

        rl_select_height.setOnClickListener(this);
        rl_select_weight.setOnClickListener(this);
        rl_select_relationship.setOnClickListener(this);
        rl_select_I_speak.setOnClickListener(this);
        user_profile_button.setOnClickListener(this);

        showInterestAdapter = new ShowInterestAdapter(Constant.UserPersonalProfile, UserPersonalProfileActivity.this, interestInfoList, new GetInterestValueListener() {

            @Override
            public void getInterestValue(String value) {
                if (user_interests.contains(value + ",")) {
                    user_interests = user_interests.replace(value + ",", "");
                } else if (user_interests.contains("," + value)) {
                    user_interests = user_interests.replace("," + value, "");
                }
            }
        });
        user_selected_interest_list_view.setAdapter(showInterestAdapter);


        if (getIntent().getSerializableExtra("otherProfileInfo") != null) {
            otherProfileInfo = (GetOtherProfileInfo) getIntent().getSerializableExtra("otherProfileInfo");
            user_select_about_you.setText(otherProfileInfo.UserDetail.about);
            user_height.setText(otherProfileInfo.UserDetail.height);
            user_weight.setText(otherProfileInfo.UserDetail.weight);

            user_interests = otherProfileInfo.UserDetail.interest;

            if (otherProfileInfo.UserDetail.relationship != null) {
                switch (otherProfileInfo.UserDetail.relationship) {
                    case "1": {
                        tv_user_relationship.setText("Single");
                        break;
                    }
                    case "2": {
                        tv_user_relationship.setText("Married");
                        break;
                    }
                    case "3": {
                        tv_user_relationship.setText("Divorced");
                        break;
                    }
                    case "4": {
                        tv_user_relationship.setText("Widowed");
                        break;
                    }
                }
            }

            if (otherProfileInfo.UserDetail.appointmentType.equals("1")) {
                rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                rb_apoim_type_paid.setChecked(true);
                Apoim_payment = "1";
            } else if (otherProfileInfo.UserDetail.appointmentType.equals("2")) {
                rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                rb_apoim_type_free.setChecked(true);
                Apoim_payment = "2";
            }

            if (otherProfileInfo.UserDetail.eventType.equals("1")) {
                rb_event_type_free.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                Event_payment = "1";
                rb_event_type_paid.setChecked(true);
            } else if (otherProfileInfo.UserDetail.eventType.equals("2")) {
                rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_event_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                Event_payment = "2";
                rb_event_type_free.setChecked(true);
            }

            profile_header_text.setText(getString(R.string.edit_profile));

            if (otherProfileInfo.UserDetail.language != null) {

                if (otherProfileInfo.UserDetail.language.contains(", ") || otherProfileInfo.UserDetail.language.contains(",")) {
                    List<String> result = Arrays.asList(otherProfileInfo.UserDetail.language.split("\\s*,\\s*"));

                    for (int i = 0; i < IspeakInfoList.size(); i++) {
                        for (int j = 0; j < result.size(); j++) {
                            if (IspeakInfoList.get(i).name.equals(result.get(j))) {
                                language = IspeakInfoList.get(j).name + ", " + language;
                                IspeakInfoList.get(j).isChecked = true;
                            }
                        }
                    }

                    if (language.endsWith(", ")) {
                        language = language.substring(0, language.length() - 2);
                    }

                    user_I_speak.setText(language);


                } else {
                    switch (otherProfileInfo.UserDetail.language) {
                        case "English": {
                            user_I_speak.setText("English");
                            language = "English";
                            languageId = "1";
                            IspeakInfoList.get(0).isChecked = true;
                            break;
                        }
                        case "Spanish": {
                            user_I_speak.setText("Spanish");
                            language = "Spanish";
                            languageId = "2";
                            IspeakInfoList.get(1).isChecked = true;
                            break;
                        }
                        case "French": {
                            user_I_speak.setText("French");
                            language = "French";
                            languageId = "3";
                            IspeakInfoList.get(2).isChecked = true;
                            break;
                        }
                    }
                }


            }


            if (!otherProfileInfo.UserDetail.interest.equals("")) {
                interestInfoList.clear();
                List<String> interestList = Arrays.asList(otherProfileInfo.UserDetail.interest.split(","));
                for (int i = 0; i < interestList.size(); i++) {
                    ProfileInterestInfo interestInfo = new ProfileInterestInfo();
                    interestInfo.interest = interestList.get(i);
                    interestInfoList.add(interestInfo);
                }
                showInterestAdapter.notifyDataSetChanged();
            }
        } else {
            if (session.getProfileInfo() != null) {
                ProfileInfo profileInfo = session.getProfileInfo();

                if (profileInfo.userData.height != null) {
                    user_select_about_you.setText(Utils.convertUTF8ToStringSetText(profileInfo.userData.about));
                    user_height.setText(profileInfo.userData.height);
                    user_weight.setText(profileInfo.userData.weight);
                    tv_user_relationship.setText(profileInfo.userData.relationship);
                    user_I_speak.setText(profileInfo.userData.language);
                }
            }

            if (session.getuserInterestList() != null) {
                interestInfoList.addAll(session.getuserInterestList());
                showInterestAdapter.notifyDataSetChanged();
            }
        }

        for (int i = 0; i < relationshipInfoList.size(); i++) {
            if (relationshipInfoList.get(i).name.equals(tv_user_relationship.getText().toString())) {
                relationId = i + 1 + "";
            }
        }

        rg_apoim_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_apoim_type_paid: {
                        fromToPayment = "apoim";
                        rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                        Apoim_payment = "1";

                        SignInInfo info = session.getUser();

                        if (info.userDetail.bankAccountStatus.equals("1")) {

                            updateBankInfoDialog(UserPersonalProfileActivity.this, getString(R.string.do_you_wanna_update_bank_info));

                        } else if (info.userDetail.bankAccountStatus.equals("0")) {

                            AddBankAccDialog(UserPersonalProfileActivity.this, getString(R.string.need_add_bank));
                        }

                        break;
                    }
                    case R.id.rb_apoim_type_free: {
                        fromToPayment = "apoim";
                        rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                        Apoim_payment = "2";
                        break;
                    }
                }
            }
        });

        rg_event_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_event_type_paid: {
                        fromToPayment = "event";
                        rb_event_type_free.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorPrimary));
                        Event_payment = "1";

                        SignInInfo info = session.getUser();

                        if (info.userDetail.bankAccountStatus.equals("1")) {

                            updateBankInfoDialog(UserPersonalProfileActivity.this, getString(R.string.do_you_wanna_update_bank_info));

                        } else if (info.userDetail.bankAccountStatus.equals("0")) {

                            AddBankAccDialog(UserPersonalProfileActivity.this, getString(R.string.need_add_bank));
                        }

                        break;
                    }
                    case R.id.rb_event_type_free: {
                        fromToPayment = "event";
                        rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                        rb_event_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                        Event_payment = "2";
                        break;
                    }
                }
            }
        });

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
                Intent intent = new Intent(UserPersonalProfileActivity.this, PaymentActivity.class);
                intent.putExtra("isPaymentDone", isPaymentDone);
                startActivityForResult(intent, Constant.AddBankAccRequestCode);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (fromToPayment.equals("apoim")) {
                    rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_apoim_type_free.setChecked(true);
                    Apoim_payment = "2";
                } else if (fromToPayment.equals("event")) {
                    rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_event_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_event_type_free.setChecked(true);
                    Event_payment = "2";
                }

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
                Intent intent = new Intent(UserPersonalProfileActivity.this, PaymentActivity.class);
                intent.putExtra("updateBankInfo", "updateBankInfo");
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
            isPaymentDone = data.getBooleanExtra("isPaymentDone", false);

            if (isPaymentDone) {
                SignInInfo info = session.getUser();
                info.userDetail.bankAccountStatus = "1";
                session.createSession(info);
            } else {
                if (fromToPayment.equals("apoim")) {
                    rb_apoim_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_apoim_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_apoim_type_free.setChecked(true);
                } else if (fromToPayment.equals("event")) {
                    rb_event_type_paid.setTextColor(getResources().getColor(R.color.colorBlack));
                    rb_event_type_free.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_event_type_free.setChecked(true);
                }
                //AddBankAccDialog(CreateEventActivity.this, getString(R.string.need_to_add_bank));
            }

        }
    }

    private void initView() {
        rg_apoim_type = findViewById(R.id.rg_apoim_type);
        rg_event_type = findViewById(R.id.rg_event_type);

        rb_event_type_paid = findViewById(R.id.rb_event_type_paid);
        rb_event_type_free = findViewById(R.id.rb_event_type_free);
        rb_apoim_type_paid = findViewById(R.id.rb_apoim_type_paid);
        rb_apoim_type_free = findViewById(R.id.rb_apoim_type_free);

        user_add_interest = findViewById(R.id.user_add_interest);
        user_profile_back = findViewById(R.id.user_profile_back);

        rl_select_height = findViewById(R.id.rl_select_height);
        user_height = findViewById(R.id.user_height);

        rl_select_weight = findViewById(R.id.rl_select_weight);
        user_weight = findViewById(R.id.user_weight);

        rl_select_relationship = findViewById(R.id.rl_select_relationship);
        tv_user_relationship = findViewById(R.id.tv_user_relationship);

        rl_select_I_speak = findViewById(R.id.rl_select_I_speak);
        user_I_speak = findViewById(R.id.user_I_speak);

        profile_header_text = findViewById(R.id.profile_header_text);

        user_selected_interest_list_view = findViewById(R.id.user_selected_interest_list_view);
        int numberOfColumns = 2;
        user_selected_interest_list_view.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        user_select_about_you = findViewById(R.id.user_select_about_you);
        user_profile_button = findViewById(R.id.user_profile_button);

        user_select_about_you.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checker = 0;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checker != 1) {
                    checker = 1;
                    String str = replaceWithPattern(s.toString(), " ");
                    if (str.startsWith(" ")) {
                        user_select_about_you.setText("");
                    } else {
                        user_select_about_you.setText(str);
                        user_select_about_you.setSelection(str.length());
                    }

                }
            }
        });
    }

    public String replaceWithPattern(String str, String replace) {

        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        return mtch.replaceAll(replace);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_height:
                openSelectHeightDialog(heightInfoList);
                break;

            case R.id.rl_select_weight:
                openEnterWeightDialog(weightInfoList);
                break;

            case R.id.rl_select_relationship:
                openSelectRelationshipDialog(relationshipInfoList);
                break;

            case R.id.rl_select_I_speak:
                openSelectISpeakDialog(IspeakInfoList);
                break;

            case R.id.user_add_interest:
                if (Utils.IsNetPresent(UserPersonalProfileActivity.this)) {
                    openAddInterestDialog();
                } else {
                    Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;

            case R.id.user_profile_back:
                onBackPressed();
                break;

            case R.id.user_profile_button:
                if (Utils.IsNetPresent(UserPersonalProfileActivity.this)) {
                    if (isValidData()) {
                        if (user_interests.endsWith(",")) {
                            user_interests = user_interests.substring(0, user_interests.length() - 1);
                        }

                        Intent intent = new Intent(UserPersonalProfileActivity.this, EditProfileActivity.class);
                        intent.putExtra("user_about_you", Utils.convertStringToUTF8SendToserver(user_select_about_you.getText().toString()));
                        intent.putExtra("user_height", user_height.getText().toString());
                        intent.putExtra("user_weight", user_weight.getText().toString());
                        intent.putExtra("tv_user_relationship", relationId);
                        intent.putExtra("user_I_speak", language);
                        intent.putExtra("user_interest", user_interests);
                        intent.putExtra("allDetailsFilled", "true");
                        intent.putExtra("interest_key", user_interests);

                        intent.putExtra("eventType", Event_payment);
                        intent.putExtra("appointmentType", Apoim_payment);

                        profileInfo.userData.about = Utils.convertStringToUTF8SendToserver(user_select_about_you.getText().toString());
                        profileInfo.userData.height = user_height.getText().toString();
                        profileInfo.userData.weight = user_weight.getText().toString();
                        profileInfo.userData.relationship = tv_user_relationship.getText().toString().trim();
                        profileInfo.userData.language = user_I_speak.getText().toString().trim();
                        profileInfo.userData.interest = user_interests;

                        profileInfo.userData.eventType = Event_payment;
                        profileInfo.userData.appointmentType = Apoim_payment;

                        session.saveUserInterestList(interestInfoList);

                        session.createProfileInfo(profileInfo);


                        Log.d("profile", profileInfo + "");

                        setResult(RESULT_OK, intent);
                        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


                        finish();
                    }
                } else {
                    Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_network_check));
                }
                break;
        }
    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (!v.isNullValue(user_height.getText().toString().trim())) {
            Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_height_null));
            return false;
        } else if (!v.isNullValue(user_weight.getText().toString().trim())) {
            Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_weight_null));
            return false;
        } else if (!v.isNullValue(tv_user_relationship.getText().toString().trim())) {
            Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_relation_null));
            return false;
        } else if (!v.isNullValue(user_I_speak.getText().toString().trim())) {
            Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_I_speak_null));
            return false;
        } else if (interestInfoList.size() == 0) {
            Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_interest_null));
            return false;
        }
        return true;
    }

    private void openSelectHeightDialog(final ArrayList<ProfileItemInfo> heightInfoList) {
        rl_select_height.setEnabled(false);
        final Dialog height_dialog = new Dialog(this);
        height_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        height_dialog.setContentView(R.layout.view_custom_dialog_profile);
        height_dialog.setCancelable(false);
        height_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = height_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_height_dialog));

        ImageView iv_refrence = height_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.height);

        ListView profile_dialog_listView = height_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = height_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                height_dialog.dismiss();
            }
        });

        String height = user_height.getText().toString();


        ProfileHeightAdapter profileHeightAdapter = new ProfileHeightAdapter(UserPersonalProfileActivity.this, heightInfoList, height, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(final int position) {
                user_height.setText(heightInfoList.get(position).name);
                height_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileHeightAdapter);
        profileHeightAdapter.notifyDataSetChanged();

        height_dialog.getWindow().setGravity(Gravity.CENTER);
        height_dialog.show();

        height_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_height.setEnabled(true);
            }
        });

    }

    private void openEnterWeightDialog(final ArrayList<String> weightInfoList) {
        rl_select_weight.setEnabled(false);

        final Dialog weight_dialog = new Dialog(this);
        weight_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        weight_dialog.setContentView(R.layout.view_profile_weight_dialog);
        weight_dialog.setCancelable(false);
        weight_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = weight_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_weight_dialog));

        final EditText enter_weight = weight_dialog.findViewById(R.id.enter_weight);
        Utils utils = new Utils();
        utils.inputFilter(enter_weight);
        display_selected_unit = weight_dialog.findViewById(R.id.display_selected_unit);
        final TextView weight_unit = weight_dialog.findViewById(R.id.weight_unit);

        final Spinner weight_unit_spinner = weight_dialog.findViewById(R.id.weight_unit_spinner);
        ImageView weight_decline_button = weight_dialog.findViewById(R.id.weight_decline_button);
        Button enter_weight_button = weight_dialog.findViewById(R.id.enter_weight_button);

        weight_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight_dialog.dismiss();
            }
        });

        weight_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_weight.setEnabled(true);
            }
        });

        enter_weight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = enter_weight.getText().toString().trim();


                if (!weight.equals("")) {
                    Double d = Double.valueOf(weight);
                    if (d != 0.0) {


                        if (weight_unit.getText().toString().equals("weight")) {
                            Utils.openAlertDialog(UserPersonalProfileActivity.this, "Select units");
                        } else {

                            if(!weight_unit.getText().toString().equals("Units")){
                                weight_dialog.dismiss();
                                user_weight.setText(weight + " " + weight_unit.getText().toString());
                            }else   Utils.openAlertDialog(UserPersonalProfileActivity.this, "Select units");

                        }

                    } else {
                        Utils.openAlertDialog(UserPersonalProfileActivity.this, "Weight can't zero");

                    }

                } else {
                    Utils.openAlertDialog(UserPersonalProfileActivity.this, getResources().getString(R.string.alert_weight_null));
                }


                Utils.hideSoftKeyboard(UserPersonalProfileActivity.this);

            }
        });


        String temp_weight[] = user_weight.getText().toString().split(" ");
        String weight = temp_weight[0];

        if (temp_weight.length == 2) {
            unit = temp_weight[1];
        }

        enter_weight.setText(weight);
        weight_unit.setText(unit);


        weight_unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //display_selected_unit.setText(adapterView.getItemAtPosition(0).toString());
                display_selected_unit.setText("Weight");


                String select = adapterView.getItemAtPosition(i).toString();

                if (select.equals("Weight")) {
                    if (!unit.equals("") && unit != null) {
                        weight_unit.setText(unit);
                    } else {
                        if (select.equals("Weight")) {
                            weight_unit.setText("Units");
                        } else {
                            weight_unit.setText(adapterView.getItemAtPosition(i).toString());
                        }

                    }

                } else {
                    weight_unit.setText(adapterView.getItemAtPosition(i).toString());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(UserPersonalProfileActivity.this, android.R.layout.simple_spinner_item, weightInfoList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight_unit_spinner.setAdapter(arrayAdapter);

        weight_dialog.getWindow().setGravity(Gravity.CENTER);
        weight_dialog.show();
    }

    private void openSelectRelationshipDialog(final ArrayList<ProfileItemInfo> relationshipInfoList) {
        rl_select_relationship.setEnabled(false);
        final Dialog relation_dialog = new Dialog(this);
        relation_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        relation_dialog.setContentView(R.layout.view_custom_dialog_profile);
        relation_dialog.setCancelable(false);
        relation_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = relation_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_relationship_dialog));

        ImageView iv_refrence = relation_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.give_heart);

        ListView profile_dialog_listView = relation_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = relation_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relation_dialog.dismiss();
            }
        });

        String relation_name = tv_user_relationship.getText().toString();

        ProfileRelationshipAdapter profileRelationshipAdapter = new ProfileRelationshipAdapter(UserPersonalProfileActivity.this, relationshipInfoList, relation_name, new ProfileRelationListener() {
            @Override
            public void getPosition(int position, int relId) {
                tv_user_relationship.setText(relationshipInfoList.get(position).name);
                relationId = relId + "";
                relation_dialog.dismiss();
            }
        });

        profile_dialog_listView.setAdapter(profileRelationshipAdapter);
        profileRelationshipAdapter.notifyDataSetChanged();

        relation_dialog.getWindow().setGravity(Gravity.CENTER);
        relation_dialog.show();

        relation_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_relationship.setEnabled(true);
            }
        });
    }

    private void openSelectISpeakDialog(final ArrayList<ProfileItemInfo> ispeakInfoList) {
        rl_select_I_speak.setEnabled(false);
        final Dialog I_speak_dialog = new Dialog(this);
        I_speak_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        I_speak_dialog.setContentView(R.layout.view_custom_dialog_profile);
        I_speak_dialog.setCancelable(false);
        I_speak_dialog.setCanceledOnTouchOutside(false);

        TextView dialog_header = I_speak_dialog.findViewById(R.id.dialog_header);
        dialog_header.setText(getResources().getString(R.string.heading_select_I_speak_dialog));

        ImageView iv_refrence = I_speak_dialog.findViewById(R.id.iv_refrence);
        iv_refrence.setImageResource(R.drawable.speak);

        ListView profile_dialog_listView = I_speak_dialog.findViewById(R.id.profile_dialog_listView);
        final ImageView education_decline_button = I_speak_dialog.findViewById(R.id.interest_decline_button);

        education_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                I_speak_dialog.dismiss();
            }
        });

        String I_speak_name = user_I_speak.getText().toString();

        ProfileISpeakAdapter profileISpeakAdapter = new ProfileISpeakAdapter(UserPersonalProfileActivity.this, ispeakInfoList, I_speak_name, new ProfileRelationListener() {
            @Override
            public void getPosition(int position, int langId) {

                if (language.contains(ispeakInfoList.get(position).name + ", ")) {
                    language = language.replace(ispeakInfoList.get(position).name + ", ", "");
                } else if (language.contains(", " + ispeakInfoList.get(position).name)) {
                    language = language.replace(", " + ispeakInfoList.get(position).name, "");
                } else if (language.contains(ispeakInfoList.get(position).name)) {
                    language = language.replace(ispeakInfoList.get(position).name, "");
                } else {
                    language = ispeakInfoList.get(position).name + ", " + language;
                }


                if (language.endsWith(", ")) {
                    language = language.substring(0, language.length() - 2);
                }


                user_I_speak.setText(language);

                I_speak_dialog.dismiss();

            }

        });

        profile_dialog_listView.setAdapter(profileISpeakAdapter);
        profileISpeakAdapter.notifyDataSetChanged();

        I_speak_dialog.getWindow().setGravity(Gravity.CENTER);
        I_speak_dialog.show();

        I_speak_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rl_select_I_speak.setEnabled(true);
            }
        });
    }

    private void openAddInterestDialog() {
        final Dialog add_interest_dialog = new Dialog(this);
        add_interest_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        add_interest_dialog.setContentView(R.layout.view_add_interest_dialog);
        add_interest_dialog.setCancelable(false);
        add_interest_dialog.setCanceledOnTouchOutside(false);

        final EditText interest_search = add_interest_dialog.findViewById(R.id.interest_search);
        final RelativeLayout rl_add_searched_interest = add_interest_dialog.findViewById(R.id.rl_add_searched_interest);
        final TextView searched_interest_list_view = add_interest_dialog.findViewById(R.id.searched_interest_list_view);
        final ImageView add_searched_interest_icon = add_interest_dialog.findViewById(R.id.add_searched_interest_icon);

        selector_interest_list_view = add_interest_dialog.findViewById(R.id.selector_interest_list_view);

       if(interestList.size() != 0){
           interest_method(add_interest_dialog, interest_search, searched_interest_list_view, rl_add_searched_interest, add_searched_interest_icon);
       }else {
           interest_service(add_interest_dialog, interest_search, rl_add_searched_interest, searched_interest_list_view, add_searched_interest_icon);
       }

        decline_button = add_interest_dialog.findViewById(R.id.interest_decline_button);
        decline_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                add_interest_dialog.dismiss();
            }
        });

        add_interest_dialog.getWindow().setGravity(Gravity.CENTER);
        add_interest_dialog.show();


    }

    private void interest_service(Dialog add_interest_dialog, EditText interest_search, RelativeLayout rl_add_searched_interest, TextView searched_interest_list_view, ImageView add_searched_interest_icon) {
        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("interestList");
                        String interest = null;
                        interestList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String interestId = object.getString("interestId");
                            interest = object.getString("interest");
                            interestList.add(new ProfileInterestInfo(interestId, interest));

                            interest_method(add_interest_dialog, interest_search, searched_interest_list_view, rl_add_searched_interest, add_searched_interest_icon);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callGetSimpleVolley("getInterestList");
    }

    private void interest_method(Dialog add_interest_dialog, EditText interest_search, TextView searched_interest_list_view, RelativeLayout rl_add_searched_interest, ImageView add_searched_interest_icon) {
        final AddInterestAdapter adapter = new AddInterestAdapter(UserPersonalProfileActivity.this,
                interestList, interestInfoList, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(int position) {
                selected_interest = interestList.get(position).interest;
                user_interests = selected_interest + "," + user_interests;
                interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                showInterestAdapter.notifyDataSetChanged();

                add_interest_dialog.dismiss();
            }
        });
        selector_interest_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Calling of method to search recycler view locally
        interest_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                                          int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i,
                                      int i1, int i2) {
                final String getValue = interest_search.getText().toString().replaceAll("( +)", " ").trim();

                if (i2 > 0) {

                    String s = String.valueOf(charSequence.charAt(0)).toUpperCase() + (i2 >= 2 ? charSequence.subSequence(1, i2).toString().toLowerCase() : "");
                    searched_interest_list_view.setText(s.trim());

                    final ArrayList<ProfileInterestInfo> filtered_list = new ArrayList<>();

                    for (ProfileInterestInfo wp : interestList) {
                        if (wp.interest.toLowerCase().contains(charSequence)) {
                            filtered_list.add(wp);
                        } else {
                            if (!getValue.equals("")) {
                                rl_add_searched_interest.setVisibility(View.VISIBLE);
                            }

                            final String sel_int = searched_interest_list_view.getText().toString();

                            add_searched_interest_icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!TextUtils.isEmpty(getValue)) {
                                        user_interests = getValue + "," + user_interests;
                                        interestInfoList.add(new ProfileInterestInfo("", getValue));
                                        showInterestAdapter.notifyDataSetChanged();
                                    }
                                    add_interest_dialog.dismiss();

                                }
                            });
                        }

                    }

                    AddInterestAdapter demoAdapter = new AddInterestAdapter(UserPersonalProfileActivity.this,
                            filtered_list, interestInfoList, new ProfileImageAdapterListener() {
                        @Override
                        public void getPosition(int position) {
                            selected_interest = filtered_list.get(position).interest;
                            user_interests = selected_interest + "," + user_interests;

                            interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                            showInterestAdapter.notifyDataSetChanged();

                            add_interest_dialog.dismiss();

                        }
                    });
                    selector_interest_list_view.setAdapter(demoAdapter);
                    demoAdapter.notifyDataSetChanged();
                } else {
                    //hide
                    rl_add_searched_interest.setVisibility(View.GONE);
                    searched_interest_list_view.setText("");
                    selector_interest_list_view.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    showInterestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /*private void openAddInterestDialog() {
        final Dialog add_interest_dialog = new Dialog(this);
        add_interest_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        add_interest_dialog.setContentView(R.layout.view_add_interest_dialog);
        add_interest_dialog.setCancelable(false);
        add_interest_dialog.setCanceledOnTouchOutside(false);

        final EditText interest_search = add_interest_dialog.findViewById(R.id.interest_search);
        final RelativeLayout rl_add_searched_interest = add_interest_dialog.findViewById(R.id.rl_add_searched_interest);
        final TextView searched_interest_list_view = add_interest_dialog.findViewById(R.id.searched_interest_list_view);
        final ImageView add_searched_interest_icon = add_interest_dialog.findViewById(R.id.add_searched_interest_icon);

        selector_interest_list_view = add_interest_dialog.findViewById(R.id.selector_interest_list_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("interestList");
                        String interest = null;
                        interestList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String interestId = object.getString("interestId");
                            interest = object.getString("interest");
                            interestList.add(new ProfileInterestInfo(interestId, interest));

                            final AddInterestAdapter adapter = new AddInterestAdapter(UserPersonalProfileActivity.this,
                                    interestList, interestInfoList, new ProfileImageAdapterListener() {
                                @Override
                                public void getPosition(int position) {
                                    selected_interest = interestList.get(position).interest;
                                    user_interests = selected_interest + "," + user_interests;
                                    interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                                    showInterestAdapter.notifyDataSetChanged();

                                    add_interest_dialog.dismiss();
                                }
                            });
                            selector_interest_list_view.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            // Calling of method to search recycler view locally
                            interest_search.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i,
                                                              int i1, int i2) {}
                                @Override
                                public void onTextChanged(CharSequence charSequence, int i,
                                                          int i1, int i2) {
                                    final String getValue = interest_search.getText().toString().replaceAll("( +)", " ").trim();

                                    if (i2 > 0) {

                                        String s = String.valueOf(charSequence.charAt(0)).toUpperCase() + (i2 >= 2 ? charSequence.subSequence(1, i2).toString().toLowerCase() : "");
                                        searched_interest_list_view.setText(s.trim());

                                        final ArrayList<ProfileInterestInfo> filtered_list = new ArrayList<>();

                                        for (ProfileInterestInfo wp : interestList) {
                                            if (wp.interest.toLowerCase().contains(charSequence)) {
                                                filtered_list.add(wp);
                                            } else {
                                                if (!getValue.equals("")) {
                                                    rl_add_searched_interest.setVisibility(View.VISIBLE);
                                                }

                                                final String sel_int = searched_interest_list_view.getText().toString();

                                                add_searched_interest_icon.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (!TextUtils.isEmpty(getValue)) {
                                                            user_interests = getValue + "," + user_interests;
                                                            interestInfoList.add(new ProfileInterestInfo("", getValue));
                                                            showInterestAdapter.notifyDataSetChanged();
                                                        }
                                                        add_interest_dialog.dismiss();

                                                    }
                                                });
                                            }

                                        }

                                        AddInterestAdapter demoAdapter = new AddInterestAdapter(UserPersonalProfileActivity.this,
                                                filtered_list, interestInfoList, new ProfileImageAdapterListener() {
                                            @Override
                                            public void getPosition(int position) {
                                                selected_interest = filtered_list.get(position).interest;
                                                user_interests = selected_interest + "," + user_interests;

                                                interestInfoList.add(new ProfileInterestInfo(interestList.get(position).interestId, selected_interest));
                                                showInterestAdapter.notifyDataSetChanged();

                                                add_interest_dialog.dismiss();

                                            }
                                        });
                                        selector_interest_list_view.setAdapter(demoAdapter);
                                        demoAdapter.notifyDataSetChanged();
                                    } else {
                                        //hide
                                        rl_add_searched_interest.setVisibility(View.GONE);
                                        searched_interest_list_view.setText("");
                                        selector_interest_list_view.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        showInterestAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callGetSimpleVolley("getInterestList");

        decline_button = add_interest_dialog.findViewById(R.id.interest_decline_button);
        decline_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                add_interest_dialog.dismiss();
            }
        });

        add_interest_dialog.getWindow().setGravity(Gravity.CENTER);
        add_interest_dialog.show();


    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(0, R.anim.back_slide);
        finish();
    }
}
