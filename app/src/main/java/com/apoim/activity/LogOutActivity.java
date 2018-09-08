package com.apoim.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.apoim.R;
import com.apoim.session.Session;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by mindiii on 23/2/18.
 */

public class LogOutActivity extends AppCompatActivity {
    Button logout_button;
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_layout);

        session = new Session(LogOutActivity.this, this);
        logout_button = findViewById(R.id.logout_button);

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  final AlertDialog.Builder builder = new AlertDialog.Builder(LogOutActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Do you really want to logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        session.logout();
                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();*/

                session.logout();
                FirebaseAuth.getInstance().signOut();


            }
        });
    }
}
