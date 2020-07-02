package com.example.cwfbase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button updatusersetting;
    private String cuurentuserid;
    private FirebaseAuth auth;
    private EditText username, userstatus;
    private DatabaseReference rootRef;
    private CircleImageView userprofilimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth = FirebaseAuth.getInstance();
        cuurentuserid = auth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();


        updatusersetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetting();
            }
        });
        retrivedUserInfos();


    }



    private void updateSetting() {

        String usernam = username.getText().toString();
        String userstatuss = userstatus.getText().toString();

        if (TextUtils.isEmpty(usernam)) {
            Toast.makeText(SettingActivity.this, "pleas entre your user name", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(userstatuss)) {
            Toast.makeText(SettingActivity.this, "pleas entre your status", Toast.LENGTH_SHORT).show();

        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", cuurentuserid);
            profileMap.put("name", usernam);
            profileMap.put("status", userstatuss);
            rootRef.child("Users").child(cuurentuserid).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToStartActivity();
                        Toast.makeText(SettingActivity.this, "profil updated", Toast.LENGTH_SHORT).show();

                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }



    private void InitializeFields() {
        updatusersetting = findViewById(R.id.update);
        username = findViewById(R.id.set_user_name);
        userstatus = findViewById(R.id.set_profil_status);
        userprofilimage = findViewById(R.id.profile_image);
    }

    private void sendUserToStartActivity() {
        Intent start_intent = new Intent(SettingActivity.this, StartActivity.class);
        start_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(start_intent);
        finish();

    }

    private void retrivedUserInfos() {
        rootRef.child("Users").child(cuurentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))){

                    String retriveusername=snapshot.child("name").getValue().toString();
                    String retrivestatus=snapshot.child("status").getValue().toString();
                    String retriveprofilimage=snapshot.child("image").getValue().toString();

                    username.setText(retriveusername);
                    userstatus.setText(retrivestatus);


                }
                else if((snapshot.exists()) &&  (snapshot.hasChild("name")))
                {


                    String retriveusername=snapshot.child("name").getValue().toString();
                    String retrivestatus=snapshot.child("status").getValue().toString();

                    //status edite text


                    username.setText(retriveusername);
                    userstatus.setText(retrivestatus);
                }
                else {
                    Toast.makeText(SettingActivity.this, "please set & update your profil informations", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
