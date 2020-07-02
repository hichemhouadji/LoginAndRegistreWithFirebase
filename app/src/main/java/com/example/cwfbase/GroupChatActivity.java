package com.example.cwfbase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
private ImageButton send_msg_img;
private Toolbar toolbar;
private EditText send_msg_input;
private ScrollView mscrollView;
private TextView display_text_msg;
private DatabaseReference userRef,GroupnameRef,groupmessagekeyRef;
private FirebaseAuth auth;
private String currentgroupname,currentusreid,currentusername,currentdate,currenttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentgroupname=getIntent().getExtras().get("groupname").toString();
        Toast.makeText(GroupChatActivity.this, currentgroupname, Toast.LENGTH_SHORT).show();

        auth=FirebaseAuth.getInstance();
        currentusreid=auth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        GroupnameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgroupname);




        initializeFilds();
        GetUserInfos();
        send_msg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageToDatabase();
                send_msg_input.setText("");
                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    displayMessage(snapshot);


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void saveMessageToDatabase() {

        String message=send_msg_input.getText().toString();
        String messageKey=GroupnameRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this,"please entre message", Toast.LENGTH_SHORT).show();


        }
        else {
            Calendar callForDate=Calendar.getInstance();
            SimpleDateFormat currentdateformat=new SimpleDateFormat("MMM dd, yyyy");
            currentdate=currentdateformat.format(callForDate.getTime());


            Calendar callForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeformat=new SimpleDateFormat("hh:mm a");
            currenttime=currentTimeformat.format(callForTime.getTime());

            HashMap<String,Object> groupMessageKey=new HashMap<>();
            GroupnameRef.updateChildren(groupMessageKey);

            groupmessagekeyRef=GroupnameRef.child(messageKey);


            HashMap<String,Object> messageInfoMAp=new HashMap<>();
            messageInfoMAp.put("name", currentusername);
            messageInfoMAp.put("message", message);
            messageInfoMAp.put("date", currentdate);
            messageInfoMAp.put("time", currenttime);

            groupmessagekeyRef.updateChildren(messageInfoMAp);



        }

    }


    private void initializeFilds() {
        toolbar=findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentgroupname);

        send_msg_img=findViewById(R.id.send_msg_image);
        send_msg_input=findViewById(R.id.inputs);
        mscrollView=findViewById(R.id.my_scrol_view);
        display_text_msg=findViewById(R.id.group_chat_display);

    }
    private void GetUserInfos() {
userRef.child(currentusreid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
if (snapshot.exists()){
    currentusername=snapshot.child("name").getValue().toString();

}
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

    }
    private void displayMessage(DataSnapshot  snapshot) {
        Iterator iterator=snapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String Chatdate= (String) ((DataSnapshot)iterator.next()).getValue();
            String ChatMessage= (String) ((DataSnapshot)iterator.next()).getValue();
            String ChatName= (String) ((DataSnapshot)iterator.next()).getValue();
            String ChatTime= (String) ((DataSnapshot)iterator.next()).getValue();

            display_text_msg.append(ChatName+": \n" + ChatMessage+"\n" + ChatTime+"      " + Chatdate+" \n\n\n");
// for down auto scroling
            mscrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }
}
