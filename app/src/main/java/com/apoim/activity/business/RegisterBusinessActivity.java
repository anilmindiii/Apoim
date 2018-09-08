package com.apoim.activity.business;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.ProfileActivity;
import com.apoim.app.Apoim;
import com.apoim.cropper.CropImage;
import com.apoim.cropper.CropImageView;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.BussinessInfo;
import com.apoim.multipleFileUpload.MultiPartRequest;
import com.apoim.multipleFileUpload.Template;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterBusinessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView businessImage,iv_back;
    public  Bitmap bitmap;
    private EditText business_name;
    private TextView skip,text_register_buss,text_register_discrip;
    private TextView add_location, create_business_button;
    private InsLoadingView loadingView;
    PlaceAutocompleteFragment autocompleteFragment;
    private String latitude;
    private String longitude;
    String type = "";
    String imageUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);
        init();

        if(getIntent().getStringExtra("type") != null){
            type = getIntent().getStringExtra("type");

            if(type.equals("addBussiness")){
                iv_back.setVisibility(View.VISIBLE);
                skip.setVisibility(View.GONE);
                text_register_buss.setText(R.string.register_buisness);
                text_register_discrip.setText(R.string.register_your_business_to_get_maximum_no_of_customer);
            }
            else if(type.equals("updateBussiness")){
                iv_back.setVisibility(View.VISIBLE);
                skip.setVisibility(View.GONE);
                text_register_buss.setText(R.string.update_buisness);
                text_register_discrip.setText(R.string.update_your_business_to_get_maximum_no_of_customer);
                create_business_button.setText("Update");
                getBussinessDetails();
            }

        }

        autocompleteFragment  = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());//get place details here
                add_location.setText(place.getAddress());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }

        });

    }


    private void init() {
        businessImage = findViewById(R.id.businessImage);
        create_business_button = findViewById(R.id.create_business_button);
        add_location = findViewById(R.id.add_location);
        business_name = findViewById(R.id.business_name);
        skip = findViewById(R.id.skip);
        loadingView = findViewById(R.id.loadingView);
        iv_back = findViewById(R.id.iv_back);
        text_register_buss = findViewById(R.id.text_register_buss);
        text_register_discrip = findViewById(R.id.text_register_discrip);

        iv_back.setOnClickListener(this);
        skip.setOnClickListener(this);
        add_location.setOnClickListener(this);
        create_business_button.setOnClickListener(this);
        businessImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {

            case R.id.businessImage:
                getPermissionAndPicImage();
                break;

                case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.skip:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;


            case R.id.create_business_button:
                if (isValidData()) registerBusiness();
                break;
        }
    }

    public  Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public boolean isValidData() {
        Validation v = new Validation();

        if(type.equals("updateBussiness")){
            if (!v.isNullValue(business_name.getText().toString().trim())) {
                Utils.openAlertDialog(RegisterBusinessActivity.this, getResources().getString(R.string.alert_business_name_null));
                return false;
            } else if (!v.isNameValid(add_location.getText().toString().trim())) {
                Utils.openAlertDialog(RegisterBusinessActivity.this, getResources().getString(R.string.location_select_text));
                return false;
            }
        }else {
            if (bitmap == null) {
                Utils.openAlertDialog(RegisterBusinessActivity.this,"Please upload your business image.");
                return false;
            } else if (!v.isNullValue(business_name.getText().toString().trim())) {
                Utils.openAlertDialog(RegisterBusinessActivity.this, getResources().getString(R.string.alert_business_name_null));
                return false;
            } else if (!v.isNameValid(add_location.getText().toString().trim())) {
                Utils.openAlertDialog(RegisterBusinessActivity.this, getResources().getString(R.string.location_select_text));
                return false;
            }
        }



        return true;
    }

    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(RegisterBusinessActivity.this);
            }
        } else {
            ImagePicker.pickImage(RegisterBusinessActivity.this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(RegisterBusinessActivity.this, requestCode, resultCode, data);

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(4, 3)
                            .start(this);
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                try {
                    if (result != null)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (bitmap != null) {

                        bitmap = ImagePicker.getImageResized(this, result.getUri());
                        businessImage.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerBusiness() {
        loadingView.setVisibility(View.VISIBLE);
        create_business_button.setEnabled(false);

        Map<String, String> map = new HashMap<>();
        map.put("businessName", Utils.convertStringToUTF8SendToserver(business_name.getText().toString().trim()));
        map.put("businessAddress", Utils.convertStringToUTF8SendToserver(add_location.getText().toString().trim()));
        map.put("businesslat", latitude);
        map.put("businesslong", longitude);


        ArrayList<File> fileList = new ArrayList<>();
        if (bitmap != null) {
            fileList = new ArrayList<>();
            fileList.add(bitmapToFile(bitmap));
        }else {
            map.put("businessImage", imageUrl);
        }

        MultiPartRequest mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingView.setVisibility(View.GONE);
                create_business_button.setEnabled(true);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    loadingView.setVisibility(View.GONE);

                    if (status.equals("success")) {
                        if(type.equals("updateBussiness") || type.equals("addBussiness")){

                            successAlertDialog(RegisterBusinessActivity.this,message);

                        }else {
                            Intent intent = new Intent(RegisterBusinessActivity.this, BusinessSubscriptionActivity.class);
                            startActivity(intent);
                        }

                    } else {
                        Utils.openAlertDialog(RegisterBusinessActivity.this, message);
                    }

                    create_business_button.setEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                    create_business_button.setEnabled(true);
                    loadingView.setVisibility(View.GONE);
                }
            }

        }, fileList, fileList.size(), map, RegisterBusinessActivity.this, "businessImage", "business/addBusiness");

        //Set tag
        mMultiPartRequest.setTag("MultiRequest");

        //Set retry policy
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Apoim.getInstance().addToRequestQueue(mMultiPartRequest, "UPLOAD");
    }

    public File bitmapToFile(Bitmap bmp) {
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

    public void successAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getBussinessDetails() {
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
                        BussinessInfo bussinessInfo = gson.fromJson(response, BussinessInfo.class);

                        setData(bussinessInfo.businessDetail);

                    }
                    else {

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
        service.callGetSimpleVolley("business/getBusinessDetail");

    }

    private void setData(BussinessInfo.BusinessDetailBean businessDetail) {
        Glide.with(RegisterBusinessActivity.this).load(businessDetail
                .businessImage).apply(new RequestOptions().placeholder(R.drawable.upload)).into(businessImage);

        business_name.setText(businessDetail.businessName);
        business_name.setSelection(businessDetail.businessName.length());

        add_location.setText(businessDetail.businessAddress);
        latitude = businessDetail.businesslat;
        longitude = businessDetail.businesslong;
        imageUrl =  businessDetail.businessImage;
    }
}
