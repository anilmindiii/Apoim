package com.apoim.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.cropper.CropImage;
import com.apoim.cropper.CropImageView;
import com.apoim.helper.Constant;
import com.apoim.helper.Validation;
import com.apoim.modal.PreRegistrationInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IDwithHandVerifyActivity extends AppCompatActivity {
    ImageView profileImage,register_back,iv_status_icon;
    Bitmap bitmap;
    TextView tv_submit_button,tv_statust_txt;
    InsLoadingView loadingView;
    Session session;
    String idWithHand = "";
    String isVerifiedId = "";
    LinearLayout ly_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idwith_hand_verify);
        profileImage = findViewById(R.id.profileImage);
        tv_submit_button = findViewById(R.id.tv_submit_button);
        loadingView = findViewById(R.id.loadingView);
        register_back = findViewById(R.id.register_back);
        iv_status_icon = findViewById(R.id.iv_status_icon);
        ly_status = findViewById(R.id.ly_status);
        tv_statust_txt = findViewById(R.id.tv_statust_txt);

        session = new Session(this);
        register_back.setVisibility(View.VISIBLE);

        if(getIntent().getStringExtra("isVerifiedId").equals("0") && getIntent().getStringExtra("idWithHand").equals("")){

        }else {
            getIntentMethod(1);
        }




        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVerifiedId.equals("2")){
                    iv_status_icon.setImageResource(R.drawable.ico_camera);
                    tv_submit_button.setVisibility(View.VISIBLE);
                    ly_status.setVisibility(View.GONE);
                    isVerifiedId = "";
                    profileImage.setImageResource(R.drawable.ico_user_placeholder);
                }
                else {
                    getPermissionAndPicImage();
                }
            }
        });

        tv_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = session.getUser().userDetail.userId;
                if(!TextUtils.isEmpty(userId)){
                    if(bitmap != null){
                        selectContactNoTask(userId);
                    }else {
                        Utils.openAlertDialog(IDwithHandVerifyActivity.this,"Please select image for verification");
                    }
                }
            }
        });
    }

    private void getIntentMethod(int img) {
        if(getIntent().getStringExtra("isVerifiedId") != null){
            isVerifiedId = getIntent().getStringExtra("isVerifiedId"); // 0 for review , 1 for approve, 2 for reject ,
            idWithHand = getIntent().getStringExtra("idWithHand");

            if(img==1){
                Glide.with(this).load(idWithHand).apply(new RequestOptions().placeholder(R.drawable.ico_user_placeholder)).into(profileImage);

            }else {
                isVerifiedId = "0";
                tv_statust_txt.setText("In Reviews");
            }

            profileImage.setEnabled(false);

            tv_submit_button.setVisibility(View.GONE);
            ly_status.setVisibility(View.VISIBLE);

            if(isVerifiedId.equals("0")){ //0 for review
                iv_status_icon.setImageResource(R.drawable.veri_star);
                tv_statust_txt.setText("In Reviews");
                tv_statust_txt.setTextColor(ContextCompat.getColor(this,R.color.coloryellow));

            }
            else if(isVerifiedId.equals("1")){ //1 for approve
                iv_status_icon.setImageResource(R.drawable.veri_check);
                tv_statust_txt.setText("Approved");
                tv_statust_txt.setTextColor(ContextCompat.getColor(this,R.color.colorgreen));
            }
            else if(isVerifiedId.equals("2")){ //2 for reject
                iv_status_icon.setImageResource(R.drawable.veri_reload);
                tv_statust_txt.setText("Rejected");
                profileImage.setEnabled(true);
                tv_statust_txt.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            }

        }
    }


    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(IDwithHandVerifyActivity.this);
            }
        } else {
            ImagePicker.pickImage(IDwithHandVerifyActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(IDwithHandVerifyActivity.this, requestCode, resultCode, data);

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
                        iv_status_icon.setVisibility(View.GONE);
                        bitmap = ImagePicker.getImageResized(this, result.getUri());
                        profileImage.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void selectContactNoTask(final String userId) {
        loadingView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);

        Map<String , Bitmap> bitmapMap = new HashMap<>();
        bitmapMap.put("idWithHand",bitmap);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        isVerifiedId = "0";
                        getIntentMethod(0);


                    } else {
                        Utils.openAlertDialog(IDwithHandVerifyActivity.this, message);
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
        service.callMultiPartApi("user/verifyIdWithHand", map,bitmapMap);
    }
}
