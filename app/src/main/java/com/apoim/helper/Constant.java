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
    public static String Appointment_type_review = "1";

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
    public final static int EventPlaceRequestCode = 666;

   public static boolean isSocial = false;

    public static String PROFILE_MALE = "1";
    public static String PROFILE_FEMALE = "2";
    public static String PROFILE_TRANSGENDER = "3";

    public static String SHOW_ON_MAP_YES = "1";
    public static String SHOW_ON_MAP_NO = "2";

    public static String filterInfo = "filterInfo";
    public static String from = "from";
    public static String current_lat = "current_lat";
    public static String current_lng = "current_lng";

    public static String OtherProfile = "OtherProfile";
    public static String UserPersonalProfile = "UserPersonalProfile";
    public static String editEvent = "editEvent";
    public static int eventFilter = 87;
    public static String userId = "userId";
    public static String eventId = "eventId";

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


    // Method for uploading multipart for multiple images upload

   /* void updateProfileTask() {
        profile_button.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        String date = profile_birthday.getText().toString();
        String name = profile_name.getText().toString().replaceAll("( )+", " ");
        String aboutYou = user_about_you.replaceAll("( )+", " ");

        if (aboutYou.equals("")) {
            aboutYou = "NA";
        }
        params = new HashMap<>();
        params.put("birthday", date);
        params.put("fullName", name);
        params.put("gender", gender);
        params.put("height", user_height);
        params.put("weight", user_weight);
        params.put("relationship", user_relationship);
        params.put("about", aboutYou);
        params.put("showOnMap", showOnMap);
        params.put("language", user_I_speak);
        params.put("eduId", educationId);
        params.put("workId", workId);
        params.put("interestId", user_interest);

        params.put("address", profile_select_location.getText().toString().trim());
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        params.put("eventType", eventType);
        params.put("appointmentType", appointmentType);

        ArrayList<File> fileList = new ArrayList<>();
        for (ImageBean tmp : imageBeans) {
            if (tmp != null && tmp.bitmap != null) {
                fileList.add(bitmapToFile(tmp.bitmap));
            }
        }

        mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                setResponse(null, error);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                loading_view.setVisibility(View.GONE);
                try {
                    Log.e("RESPONSE", response.toString());

                    JSONObject result = null;

                    Log.d("profileResponce", response + "");

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        Gson gson = new Gson();
                        SignInInfo signInInfo = gson.fromJson(String.valueOf(response), SignInInfo.class);
                        otherProfileInfo = gson.fromJson(String.valueOf(response), GetOtherProfileInfo.class);


                        ArrayList<ProfileInterestInfo> interestInfoList = new ArrayList<>();
                        session.saveUserInterestList(interestInfoList);

                        //signInInfo = session.getUser();
                       *//* if(signInInfo != null){
                            signInInfo.userDetail.isProfileUpdate = "1";
                            session.createSession(signInInfo);
                        }*//*

                        session.createSession(signInInfo);
                        addUserFirebaseDatabase();


                        Intent intent = new Intent(EditProfileActivity.this, SettingsActivity.class);
                        intent.putExtra("otherProfileInfo", otherProfileInfo);
                        setResult(RESULT_OK, intent);

                        if (EditProfileActivity.this != null) {
                            profileUpdated(EditProfileActivity.this, getString(R.string.profile_update));
                        }

                    } else if (status.equalsIgnoreCase("authFail")) {
                        //  Constant.showLogOutDialog(AddVehicleInfoActivity.this);
                    } else {
                        Utils.openAlertDialog(EditProfileActivity.this, message);
                    }
                    profile_button.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    profile_button.setEnabled(true);
                    loading_view.setVisibility(View.GONE);
                }

            }
            //  }, productImages, productImages.size(), params, EditProfileActivity.this);
        }, null, 0, params, EditProfileActivity.this, Template.Query.KEY_IMAGE, "user/updateProfile");

        //Set tag
        mMultiPartRequest.setTag("MultiRequest");

        //Set retry policy
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Apoim.getInstance().addToRequestQueue(mMultiPartRequest, "UPLOAD");
    }*/


  /*  void setResponse(Object response, VolleyError error) {
        profile_button.setEnabled(true);
        if (response == null) {

        } else {
            if (StringParser.getCode(response.toString()).equals(Template.Query.VALUE_CODE_SUCCESS)) {
                //  Constant.snackbar(mainLayout, StringParser.getMessage(response.toString()));
                Utils.openAlertDialog(EditProfileActivity.this, StringParser.getMessage(response.toString()));
            } else {
                //  Constant.snackbar(mainLayout, "Error\n" + StringParser.getMessage(response.toString()));
                Utils.openAlertDialog(EditProfileActivity.this, StringParser.getMessage(response.toString()));
            }
        }
    }*/


  /*    public File bitmapToFile(Bitmap bmp) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getCompressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.toByteArray().length);

        return compressedBitmap;
    }*/

}
