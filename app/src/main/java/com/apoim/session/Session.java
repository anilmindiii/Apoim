package com.apoim.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.apoim.activity.SignInActivity;
import com.apoim.modal.FilterInfo;
import com.apoim.modal.FilterItemInfo;
import com.apoim.modal.PreRegistrationInfo;
import com.apoim.modal.ProfileInfo;
import com.apoim.modal.ProfileInterestInfo;
import com.apoim.modal.SignInInfo;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anil on 16/2/18.
 */

public class Session {
    private Context context;
    private Activity activity;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String IS_FIrebaseLogin = "isFirebaseLogin";
    private static final String PROFILEINFO = "profileInfo";
    private static final String FILTERINFO = "filter_info";
    private static final String USER_INTEREST_LIST = "user_interst_list";
    private static final String FILTER_LIST = "filter_list";
    private static final String SAVELATLNG = "savelatlng";
    private static final String PASSWORD = "password";


    public Session(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public Session(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createSession(SignInInfo signInInfo) {
        createSession(signInInfo, false);
    }

    public void createSession(SignInInfo signInInfo, boolean isFirebaseLogin) {
        Gson gson = new Gson();
        String json = gson.toJson(signInInfo); // myObject - instance of MyObject
        editor.putString("signInInfo", json);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putBoolean(IS_FIrebaseLogin, isFirebaseLogin);
        editor.putString("authToken", signInInfo.userDetail.authToken);
        editor.commit();
    }

    public SignInInfo getUser() {
        Gson gson = new Gson();
        String string = sharedPreferences.getString("signInInfo", "");
        return gson.fromJson(string, SignInInfo.class);
    }


    public void createRegistrationInfo(PreRegistrationInfo preRegistrationInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(preRegistrationInfo);
        editor.putString("preRegistrationInfo", json);
        editor.commit();
    }

    public void saveUserInterestList(ArrayList<ProfileInterestInfo> interestInfoList) {
        Gson gson = new Gson();
        String json = gson.toJson(interestInfoList);
        editor.putString(USER_INTEREST_LIST, json);
        editor.commit();
    }

    public List<ProfileInterestInfo> getuserInterestList(){
        Gson gson = new Gson();
        String str = sharedPreferences.getString(USER_INTEREST_LIST,"");
        if (!str.isEmpty()){

            ProfileInterestInfo[] interestInfo = gson.fromJson(str, ProfileInterestInfo[].class);
            List<ProfileInterestInfo> interestInfos = Arrays.asList(interestInfo);
            return  interestInfos;
        }
        else return null;
    }

    public void saveFilterList(ArrayList<FilterItemInfo> filterItemInfoList) {
        Gson gson = new Gson();
        String json = gson.toJson(filterItemInfoList);
        editor.putString(FILTER_LIST, json);
        editor.commit();
    }

    public void savePassword(String password) {
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public String getPassword(){
        return sharedPreferences.getString(PASSWORD,"");
    }

    public ArrayList<FilterItemInfo> getFilterList(){
        Gson gson = new Gson();
        String str = sharedPreferences.getString(FILTER_LIST,"");
        if (!str.isEmpty()){

            FilterItemInfo[] filterInfo = gson.fromJson(str, FilterItemInfo[].class);
            List<FilterItemInfo> filterList = Arrays.asList(filterInfo);

            ArrayList<FilterItemInfo>  infos= new ArrayList<>();

            for (int i =0;i<filterList.size();i++){
                infos.add(filterList.get(i));
            }

            // infos.add(infos)

            return  infos;
        }
        else return null;
    }

    public void createProfileInfo(ProfileInfo profileInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(profileInfo);
        editor.putString(PROFILEINFO, json);
        editor.commit();
    }

    public ProfileInfo getProfileInfo(){
        Gson gson = new Gson();
        String str = sharedPreferences.getString(PROFILEINFO,"");
        if (!str.isEmpty())
            return gson.fromJson(str, ProfileInfo.class);
        else return null;
    }

    public PreRegistrationInfo getRegistrationInfo() {
        Gson gson = new Gson();
        String string = sharedPreferences.getString("preRegistrationInfo", "");
        if (!string.isEmpty())
            return gson.fromJson(string, PreRegistrationInfo.class);
        else return null;
    }

    public void setFilterSession(FilterInfo filterInfo){
        Gson gson = new Gson();
        String json = gson.toJson(filterInfo);
        editor.putString(FILTERINFO, json);
        editor.commit();
    }

    public FilterInfo getFilterInfo(){
        Gson gson = new Gson();
        String string = sharedPreferences.getString(FILTERINFO, "");
        if (!string.isEmpty())
            return gson.fromJson(string, FilterInfo.class);
        else return null;
    }

    public String getAuthToken() {
        return sharedPreferences.getString("authToken", "");
    }



    public void logout() {
        editor.clear();
        editor.apply();

        Intent showLogin = new Intent(context, SignInActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(showLogin);

    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGEDIN, false);
    }

    public void saveLatLng(Double lat,Double lng){
        editor.putString(SAVELATLNG,lat+","+lng);
        editor.commit();
    }

    public String getLatlng(){
        String string = sharedPreferences.getString(SAVELATLNG, "");

        if (!string.isEmpty())
            return string;
        else return null;

    }

}
