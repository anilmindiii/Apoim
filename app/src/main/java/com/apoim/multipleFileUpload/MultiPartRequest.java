package com.apoim.multipleFileUpload;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.apoim.server_task.API;
import com.apoim.session.Session;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;


/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public class MultiPartRequest extends Request<String> {

    private Response.Listener<String> mListener;
    private HttpEntity mHttpEntity;
    private Map<String, String> mParams;
    private Session session;
    Activity profileActivity;



    public MultiPartRequest(Response.ErrorListener errorListener,
                            Response.Listener listener, ArrayList<File> file,
                            int numberOfFiles, Map<String, String> params,
                            Activity profileActivity,String key,String url) {

        super(Method.POST, API.BASE_URL+url, errorListener);
        mListener = listener;
        mParams=params;
        mHttpEntity = buildMultipartEntity(file, numberOfFiles,key);
        session = new Session(profileActivity.getBaseContext(), profileActivity);
        this.profileActivity=profileActivity;
    }

    private HttpEntity buildMultipartEntity(ArrayList<File> file, int numberOffiles,String Imagekey) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for(int i=0; i < file.size();i++){
            FileBody fileBody = new FileBody(file.get(i));
            builder.addPart(Imagekey, fileBody);
        }

      //  builder.addTextBody(Template.Query.KEY_DIRECTORY, Template.Query.VALUE_DIRECTORY);
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addTextBody(key, value);
        }

        return builder.build();
    }




    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = new HashMap<>();
        header.put("authToken", session.getAuthToken());
        return header;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            mHttpEntity.writeTo(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            VolleyLog.e("" + e);
            return null;
        } catch (OutOfMemoryError e){
            VolleyLog.e("" + e);
            return null;
        }

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.success(new String(response.data),
                    getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}