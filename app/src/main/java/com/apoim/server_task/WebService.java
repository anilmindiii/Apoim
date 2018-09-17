package com.apoim.server_task;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apoim.activity.ProfileActivity;
import com.apoim.app.Apoim;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.apoim.volley.AppHelper;
import com.apoim.volley.DataPart;
import com.apoim.volley.VolleyMultipartRequest;
import com.apoim.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 16/2/18.
 **/

public class WebService {
    private Context mContext;
    private String TAG;


    private Session session;
    private ResponseListener mListener;
    private LoginRegistrationListener mLSListener;

    public WebService(Context context, String TAG, LoginRegistrationListener listener) {
        super();
        mLSListener = listener;
        this.mContext = context;
        this.TAG = TAG;
        session = new Session(mContext, (Activity) mContext);
    }


    public void callSimpleVolley(final String url, final Map<String, String> params) {
        final String volley_url = API.BASE_URL + url;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, volley_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mLSListener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
                mLSListener.ErrorListener(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (session.isLoggedIn()) {
                    headers.put("authToken", session.getAuthToken());
                }
                return headers;
            }
        };
        Apoim.getInstance().addToRequestQueue(stringRequest, TAG);


    }

    public void callMultiPartApi(final String url, final Map<String, String> params, final Map<String, Bitmap> bitmapList) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                API.BASE_URL + url, new Response.Listener<NetworkResponse>() {

            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                System.out.println(resultResponse);
                mLSListener.onResponse(resultResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLSListener.ErrorListener(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (session.isLoggedIn()) {
                    headers.put("authToken", session.getAuthToken());
                }
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                if (bitmapList != null) {
                    for (Map.Entry<String, Bitmap> entry : bitmapList.entrySet()) {
                        String key = entry.getKey();
                        Bitmap bitmap = entry.getValue();
                        params.put(key, new DataPart(key.concat(".jpg"), AppHelper.getFileDataFromBitmap(mContext, bitmap), "image/png"));
                    }

                }

                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1f));
        VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(multipartRequest, TAG);
        //ImLink.getInstance().addToRequestQueue(multipartRequest, TAG);
    }

    public void callGetSimpleVolley(final String url) {
        final String volley_url = API.BASE_URL + url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, volley_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mLSListener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
                mLSListener.ErrorListener(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (session.isLoggedIn()) {
                    headers.put("authToken", session.getAuthToken());
                }
                return headers;
            }
        };
        Apoim.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    public interface ResponseListener {
        void onResponse(String response, String url);

        void ErrorListener(VolleyError error);
    }

    public void setListener(ResponseListener listener) {
        mListener = listener;
    }


    public interface LoginRegistrationListener {
        /**
         * Called when a response is received.
         */
        void onResponse(String response);

        void ErrorListener(VolleyError error);
    }

    public void setLoginRegisterListener(LoginRegistrationListener LoginRegistrationListener) {
        mLSListener = LoginRegistrationListener;
    }

    private void handleError(VolleyError error){
        handleError(mContext, error);
    }

    private void handleError(Context context, VolleyError error){

        if(error!=null && error.networkResponse!=null){
            try{
                if(error.networkResponse.statusCode == 400){
                Dialog dialog = new Dialog(context);
                showSessionError("session expired", "Your current session has expired, please login again", "Ok");
                }else {
                    Utils.openAlertDialog(mContext, "Something went wrong. please try again later.");
                }
            }catch (NullPointerException ex){ex.printStackTrace();}

        } else{
            if (!Utils.IsNetPresent(mContext)) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Something went wrong. please try again later.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void showSessionError(String title, String msg, String button) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Session session = new Session(mContext,(Activity) mContext);
                session.logout();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            alertDialog.dismiss();
        }

    }

}
