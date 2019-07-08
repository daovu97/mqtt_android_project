package com.daovu67.iotapp.views.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daovu67.iotapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPassActivity extends AppCompatActivity {

    EditText inputUsername;
    Button btnReset;
    ProgressBar progressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        Objects.requireNonNull(getSupportActionBar()).hide();
        if (isNetworkAvailable(ForgotPassActivity.this)) {
            mAuth = FirebaseAuth.getInstance();
        } else
            Toast.makeText(this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();

        Init();


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(ForgotPassActivity.this)) {
                    String email = inputUsername.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForgotPassActivity.this, "Failed to send reset email!\n" + "Please check your Email", Toast.LENGTH_SHORT).show();
                                    }

                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                } else
                    Toast.makeText(ForgotPassActivity.this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void Init() {
        inputUsername = findViewById(R.id.loginemail);
        btnReset = findViewById(R.id.btnlogin);
        progressBar = findViewById(R.id.progressBar);
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
