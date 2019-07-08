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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputCode;
    private Button btnSignup;
    private TextView textViewForgot;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_signup);

        if (isNetworkAvailable(SignupActivity.this)) {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } else
            Toast.makeText(this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();

        Init();

        onClick();


    }

    private void onClick() {

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(SignupActivity.this)) {
                    Signup();
                } else
                    Toast.makeText(SignupActivity.this, "Not connect to Internet, please check Internet", Toast.LENGTH_SHORT).show();
            }
        });

        textViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, ForgotPassActivity.class);
                startActivities(new Intent[]{intent});
            }
        });
    }

    private void Init() {
        inputEmail = findViewById(R.id.signupemail);
        inputPassword = findViewById(R.id.signuppass);
        inputCode = findViewById(R.id.codenum);
        btnSignup = findViewById(R.id.btnsignup);
        textViewForgot = findViewById(R.id.forgotpass);
        progressBar = findViewById(R.id.progressBar);

    }

    private void Signup() {

        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        String code = inputCode.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "Enter User code", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = db.collection("user").document(code);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            sendVerificationEmail();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText(SignupActivity.this, "Creat account Fail!!\n" + "Please check your account and password", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);

                                        }

                                        // ...
                                    }
                                });

                    } else {
                        Toast.makeText(SignupActivity.this, "Usercode not exist, please retype usercode.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Not succsess, please waite to retry!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            Toast.makeText(SignupActivity.this, "Creat account success, please verify your email", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        } else {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }


}
