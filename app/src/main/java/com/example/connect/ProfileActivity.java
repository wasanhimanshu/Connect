package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connect.Adapter.MessageAdapter;
import com.example.connect.Adapter.ScrollAdapter;
import com.example.connect.Model.ModelChat;
import com.example.connect.Model.ModelUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
private ImageView profilepic;
private TextView nametv,emailtv,phonetv,biotv,media_count,mediatv;
private String name,image_url,email,phone,bio;
private FirebaseAuth mAuth;
private RecyclerView recyclerView;
private LinearLayout block_ll;
private List<ModelChat> mChats;
private ScrollAdapter messageAdapter;
private String hisuid;
@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    Intent i = getIntent();
    hisuid = i.getExtras().getString("hisuid");
    Toolbar toolbar = (Toolbar) findViewById(R.id.profileactiity_toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setTitle("Connect");
    profilepic = (ImageView) findViewById(R.id.profile_activity_imageview);
    nametv = (TextView) findViewById(R.id.profile_activiy_nametv);
    emailtv = (TextView) findViewById(R.id.profile_activity_emailtv);
    phonetv = (TextView) findViewById(R.id.profile_activity_phonetv);
    biotv = (TextView) findViewById(R.id.profile_activity_biotv);
    mAuth = FirebaseAuth.getInstance();
    mediatv = (TextView) findViewById(R.id.mediatv);
    media_count = (TextView) findViewById(R.id.media_count);
    block_ll=(LinearLayout)findViewById(R.id.block_ll);
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(hisuid);
    db.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            name = snapshot.child("name").getValue().toString();
            email = snapshot.child("email").getValue().toString();
            phone = snapshot.child("phone").getValue().toString();
            bio = snapshot.child("bio").getValue().toString();
            image_url = snapshot.child("image_url").getValue().toString();
            nametv.setText(name);
            phonetv.setText(phone);
            emailtv.setText(email);
            biotv.setText(bio);
            if (image_url.equals("noImage")) {
                profilepic.setImageResource(R.drawable.default_pic);
            } else {
                try {
                    Glide.with(ProfileActivity.this).load(image_url).placeholder(R.drawable.default_pic).into(profilepic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("ProfileActivity", error.getMessage());
        }
    });
    recyclerView = (RecyclerView) findViewById(R.id.profile_activity_recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mChats = new ArrayList<>();

    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Chats");
    mref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mChats.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {
                ModelChat chat = ds.getValue(ModelChat.class);
                assert chat != null;
                boolean a = chat.getSender().equals(hisuid) && chat.getReciever().equals(mAuth.getUid()) || chat.getReciever().equals(hisuid) && chat.getSender().equals(mAuth.getUid());
                boolean b = chat.getMessagetype().equals("image");
                if (a && b) {
                    mChats.add(chat);

                }
            }
            if (mChats.size() != 0) {
                messageAdapter = new ScrollAdapter(ProfileActivity.this, mChats);
                recyclerView.setAdapter(messageAdapter);
                media_count.setText(String.valueOf(mChats.size()));
                mediatv.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                media_count.setVisibility(View.VISIBLE);
            } else {
                mediatv.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                media_count.setVisibility(View.GONE);
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("ProfileActivity", error.getMessage());
        }
    });

profilepic.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i=new Intent(ProfileActivity.this, ImageViewingActivity.class);
        if(image_url.equals("noImage")){
            Toast.makeText(ProfileActivity.this, name + " has not Uploaded Profile",Toast.LENGTH_SHORT).show();
        }else {
            i.putExtra("image_url",image_url);
            startActivity(i);
        }
    }
});

block_ll.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});

}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if(item.getItemId()==android.R.id.home){
        finish();
    }
        return super.onOptionsItemSelected(item);
    }
}