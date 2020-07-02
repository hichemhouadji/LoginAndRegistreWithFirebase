package com.example.cwfbase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextView forget_pass, btnSignup;

    private ProgressDialog loadingBar;
    private Button btnLogin;
    private FirebaseAuth auth;
    private FirebaseAuth mphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        InitializeFields();


    }

    private void InitializeFields() {

        inputEmail = findViewById(R.id.email);
        loadingBar = new ProgressDialog(this);

        inputPassword = findViewById(R.id.password);
        forget_pass = findViewById(R.id.reset_password);
        btnLogin = findViewById(R.id.sign_in_button);
        auth = FirebaseAuth.getInstance();
        btnSignup = findViewById(R.id.sign_up);


        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset_pass_intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(reset_pass_intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signup_intent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();


            }
        });

    }

    private void allowUserToLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please entre email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please entre password...", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("creating new account");
            loadingBar.setMessage("please wait , while we are creating new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        sendUserToStartActivity();
                        Toast.makeText(LoginActivity.this, "Login succesfull...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }


    private void sendUserToStartActivity() {
        Intent start_intent = new Intent(LoginActivity.this, StartActivity.class);
        start_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(start_intent);
        finish();

    }
}