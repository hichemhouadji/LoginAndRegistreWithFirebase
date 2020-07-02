package com.example.cwfbase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private Toolbar mtoolbar;
    ViewPager myviewpafer;
    TabLayout mytablayout;
    private DatabaseReference rootRef;

    private TabAccesAdapter mytabAccesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mtoolbar = findViewById(R.id.start_app_toolbar);
        rootRef = FirebaseDatabase.getInstance().getReference();
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Whatsapp");

        myviewpafer = findViewById(R.id.tabs_pager);
        mytabAccesAdapter = new TabAccesAdapter(getSupportFragmentManager());
        myviewpafer.setAdapter(mytabAccesAdapter);


        mytablayout = findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpafer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        } else virifyuserexsistance();
    }

    private void virifyuserexsistance() {
        String currentuserID = auth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("name").exists())) {
                    Toast.makeText(StartActivity.this, "Welcom", Toast.LENGTH_SHORT).show();

                } else {
                    sendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendUserToSettingActivity() {

        Intent seting_int = new Intent(StartActivity.this, SettingActivity.class);
        seting_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(seting_int);
        finish();
    }

    private void sendUserToLoginActivity() {

        Intent login_int = new Intent(StartActivity.this, LoginActivity.class);
        login_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(login_int);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.find_friends:
                break;
            case R.id.seting:
                Intent setting_int = new Intent(StartActivity.this, SettingActivity.class);
                startActivity(setting_int);
                break;
            case R.id.logout:

                auth.signOut();
                sendUserToLoginActivity();
                break;
            case R.id.create_group:
                RequestNewGroup();
                break;
        }
        return true;
    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(StartActivity.this,R.style.AlertDialog);
        builder.setTitle("Entre group name : ");
        final EditText groupnamefild=new EditText(StartActivity.this);
        groupnamefild.setHint("ex : inptic group");

        builder.setView(groupnamefild);

        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname=groupnamefild.getText().toString();
                if (TextUtils.isEmpty(groupname)){
                    Toast.makeText(StartActivity.this, "please entre the group name", Toast.LENGTH_SHORT).show();
                }
                else {
CreateNewGroup(groupname);
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.cancel();
            }
        });
        builder.show();

    }

    private void CreateNewGroup(final String groupname) {
        rootRef.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(StartActivity.this, groupname+" is created", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}