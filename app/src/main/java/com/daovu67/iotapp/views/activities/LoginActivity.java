package com.daovu67.iotapp.views.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daovu67.iotapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView textViewCreate, textViewForgot;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        Init();

        if (isNetworkAvailable(LoginActivity.this)) {
            mAuth = FirebaseAuth.getInstance();

        } else
            Toast.makeText(this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();

        onClick();

    }

    private void Init() {
        inputEmail = findViewById(R.id.loginemail);
        inputPassword = findViewById(R.id.loginpass);
        btnLogin = findViewById(R.id.btnlogin);
        textViewCreate = findViewById(R.id.creatacc);
        textViewForgot = findViewById(R.id.forgotpass);
        progressBar = findViewById(R.id.progressBar);

    }

    private void onClick() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(LoginActivity.this)) {
                    Login();
                } else
                    Toast.makeText(LoginActivity.this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();
            }
        });

        textViewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivities(new Intent[]{intent});
            }
        });

        textViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivities(new Intent[]{intent});
            }
        });
    }

    private void Login() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            checkIfEmailVerified();


                        } else {
                            Toast.makeText(LoginActivity.this, "Login Fail!!\n" + "Please check your account and password", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }


                });

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Verify your account!", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

        }
    }

}
