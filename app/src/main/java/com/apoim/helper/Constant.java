package com.apoim.helper;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Anil on 2/14/2018.
 */

public class Constant {
    public static final int MY_PERMISSIONS_REQUEST_CEMERA=103;
    public static final  int ACCESS_FINE_LOCATION = 104;
    public static final  int MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY = 105;
    public static final  String online = "online";
    public static final  String offline = "offline";
    public static String IsGetNotificationValue = "";
    public static String fcm_password = "12345678";
    public static String Notication_on = "1";
    public static String Notication_off = "0";
    public static String isNotification = "isNotification";

    public static int FB_LOGIN = 1;
    public static int RC_SIGN_IN = 2;

    public static String REGISTER_MALE = "1";
    public static String REGISTER_FEMALE = "2";

    public static String REGISTER_NEW_FRIENDS = "1";
    public static String REGISTER_CHAT = "2";
    public static String REGISTER_DATE = "3";

    public static String REGISTER_GIRL = "1";
    public static String REGISTER_GUY = "2";
    public static String REGISTER_Transgender = "3";
    public static String REGISTER_GIRL_GUY_TransGebder = "4";

    public static String REGISTER_PUBLIC = "1";
    public static String REGISTER_PRIVATE = "2";
    public static String REGISTER_PUBLIC_PRIVATE_BOTH = "3";

    public static String appointment_details = "appId";

    //FireBase....................................................
    public static final String ARG_USERS = "users";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_HISTORY = "chat_history";
    public static final String BlockUsers = "block_users";
    public static final String blockedBy = "blockedBy";

    public final static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public final static int MY_PERMISSIONS_CAMERA = 121;
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    public final static int PROFILE_REQUEST_CODE = 111;
    public final static int AddBankAccRequestCode = 555;
    public final static int EventPayRequestCode = 444;

   public static boolean isSocial = false;

    public static String PROFILE_MALE = "1";
    public static String PROFILE_FEMALE = "2";

    public static String SHOW_ON_MAP_YES = "1";
    public static String SHOW_ON_MAP_NO = "2";

    public static String filterInfo = "filterInfo";
    public static String from = "from";
    public static String current_lat = "current_lat";
    public static String current_lng = "current_lng";

    public static String OtherProfile = "OtherProfile";
    public static String UserPersonalProfile = "UserPersonalProfile";
    public static String editEvent = "editEvent";

    public static final String Confirmed_payment = "1";
    public static final String Joined_Payment_is_pending = "2";
    public static final String Confirmed = "3";
    public static final String Request_rejected = "4";
    public static final String Pending_request = "5";
    public static final String Request_Canceled = "face_verification";


    public static final int PayForMap = 1;
    public static final int PayForToBeOnTop = 2;
    public static final int PayForcompainion = 4;
    public static final int PayForTSubscription = 5;
    public static final int PayForBusinessSubscription =6;
    public static final int PayForCounterAppointment = 7;


    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
