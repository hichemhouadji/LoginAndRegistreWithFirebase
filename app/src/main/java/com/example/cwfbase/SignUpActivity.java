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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnCreateAcount;
    private TextView btn_have_account;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private FirebaseAuth currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnCreateAcount = (Button) findViewById(R.id.create_acount);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        loadingBar = new ProgressDialog(this);
        btn_have_account = (TextView) findViewById(R.id.already_have_account);


        btn_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreateAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                CretaNewAccount();


            }
        });
    }

    private void CretaNewAccount() {
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

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String getcurrentuserid = auth.getCurrentUser().getUid();
                        rootRef.child("Users").child(getcurrentuserid).setValue("");


                        Intent start_intent = new Intent(SignUpActivity.this, StartActivity.class);
                        start_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(start_intent);
                        finish();
                        Toast.makeText(SignUpActivity.this, "account created succesfull", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SignUpActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUserToStartActivity();
        }
    }

    private void sendUserToStartActivity() {

        Intent login_int = new Intent(SignUpActivity.this, StartActivity.class);
        startActivity(login_int);
    }

}
