package com.apoim.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.helper.Validation;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 28/3/18.
 */

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText ed_old_password;
    private EditText ed_new_password;
    private EditText ed_confirm_password;
    private TextView submit_button;
    private Session session;
    private InsLoadingView loading_view;
    private ImageView register_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_layout);

        register_back = findViewById(R.id.register_back);
        loading_view = findViewById(R.id.loading_view);
        ed_old_password = findViewById(R.id.ed_old_password);
        ed_new_password = findViewById(R.id.ed_new_password);
        ed_confirm_password = findViewById(R.id.ed_confirm_password);
        submit_button = findViewById(R.id.submit_button);
        session = new Session(this);

        register_back.setVisibility(View.VISIBLE);

        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    changePassword();
                }
            }
        });
    }

    private boolean isValid() {
        String oldPassword = ed_old_password.getText().toString().trim();
        String newPassword = ed_new_password.getText().toString().trim();
        String confirmPassword = ed_confirm_password.getText().toString().trim();
        String savedPassword = session.getPassword();

        Validation v = new Validation();
        if (!v.isNull(ed_old_password)) {
            Utils.openAlertDialog(this, "Please enter old password");
            return false;
        }
        else if (!v.isPasswordValid(oldPassword)) {
            Utils.openAlertDialog(this, "Password should be 8 characters long.");
            return false;
        }
        else if(!oldPassword.equals(savedPassword)) {
            Utils.openAlertDialog(this, "Please enter correct old password.");
            return false;
        }
        else if (!v.isNull(ed_new_password)) {
            Utils.openAlertDialog(this,"Please enter new password");
            return false;
        }
        else if (!v.isPasswordValid(newPassword)) {
            Utils.openAlertDialog(this, "Password should be 8 characters long.");
            return false;
        }
        else if (!v.isNull(ed_confirm_password)) {
            Utils.openAlertDialog(this, "Please enter confirm password");
            return false;
        }
        else if (!v.isPasswordValid(confirmPassword)) {
            Utils.openAlertDialog(this,"Password should be 8 characters long.");
            return false;
        }
        else if(!newPassword.equals(confirmPassword)) {
            Utils.openAlertDialog(this, "New password and confirm password does not match.");
            return false;
        }
        return true;
    }

    private void changePassword() {
        submit_button.setEnabled(false);
        loading_view.setVisibility(View.VISIBLE);
        Map<String,String> map = new HashMap<>();
        map.put("oldPassword",ed_old_password.getText().toString().trim());
        map.put("newPassword",ed_new_password.getText().toString().trim());


        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                submit_button.setEnabled(true);
                loading_view.setVisibility(View.GONE);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        session.savePassword(ed_new_password.getText().toString().trim());
                        successDialog(ChangePasswordActivity.this,message);
                    } else {

                        Utils.openAlertDialog(ChangePasswordActivity.this,message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    submit_button.setEnabled(true);
                    loading_view.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loading_view.setVisibility(View.GONE);
                submit_button.setEnabled(true);
            }
        });
        service.callSimpleVolley("user/changePassword",map);
    }

    public void successDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Apoim");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
