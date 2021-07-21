package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connect.Model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterAcitivity extends AppCompatActivity {
private TextInputEditText mNameEt,mEmailet,mPhoneEt,mPassEt;
private Button mRegisterbtn;
private TextView already_user,registerwelcome,registersignin;
private FirebaseAuth mAuth;
private DatabaseReference ref;
    private Typeface MRR;
    private Typeface MR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitivity);
        Toolbar toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Connect");

        mNameEt=(TextInputEditText)findViewById(R.id.register_name);
        mEmailet=(TextInputEditText)findViewById(R.id.register_email);
        mPhoneEt=(TextInputEditText)findViewById(R.id.register_phone);
        mPassEt=(TextInputEditText)findViewById(R.id.register_pass);
        mRegisterbtn=(Button)findViewById(R.id.register_btn);
        already_user=(TextView)findViewById(R.id.already_user);
        registerwelcome=(TextView)findViewById(R.id.register_welcome);
        registersignin=(TextView)findViewById(R.id.register_signin);
        mAuth=FirebaseAuth.getInstance();
        MRR=Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(getAssets(),"Lato-Bold.ttf");

        registerwelcome.setTypeface(MR);
        registersignin.setTypeface(MRR);
        mEmailet.setTypeface(MR);
        mPassEt.setTypeface(MR);
        mNameEt.setTypeface(MR);
        mPhoneEt.setTypeface(MR);
        already_user.setTypeface(MRR);


        already_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterAcitivity.this,LoginActivity.class));
            }
        });
    mRegisterbtn.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         String name= Objects.requireNonNull(mNameEt.getText()).toString().trim();
         String email= Objects.requireNonNull(mEmailet.getText()).toString().trim();
         String phone= Objects.requireNonNull(mPhoneEt.getText()).toString().trim();
         String pass= Objects.requireNonNull(mPassEt.getText()).toString().trim();

         if(TextUtils.isEmpty(name)){
             mNameEt.setError("Please Enter a valid Name");
             mNameEt.setFocusable(true);
         }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
             mEmailet.setError("Please Enter a valid Email");
             mEmailet.setFocusable(true);
         }else if(phone.length()!=10){
             mPhoneEt.setError("Please Enter a valid phone Number");
             mPhoneEt.setFocusable(true);
         }else if(pass.length()<6){
             mPassEt.setError("Password must have 6 characters");
             mPassEt.setFocusable(true);
         }else{
             registeruser(name,email,phone,pass);
         }

     }
 });

    }

    private void registeruser(String name, String email, String phone, String pass) {
        ProgressDialog p=new ProgressDialog(this);
        p.setMessage("Please wait for a moment");
        p.setTitle("Creeating your account");
        p.show();

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser mUser=mAuth.getCurrentUser();
                    ref= FirebaseDatabase.getInstance().getReference().child("Users");
                    assert mUser != null;
                    ModelUser m=new ModelUser(name,email,phone,"noImage",mUser.getUid(),"Hey there I am Using Connect App","Online");
                    ref.child(Objects.requireNonNull(mAuth.getUid())).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              p.dismiss();
                              Intent i=new Intent(RegisterAcitivity.this,MainActivity.class);
                              startActivity(i);
                              finish();
                          }
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          p.dismiss();
                          Toast.makeText(RegisterAcitivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                      }
                  });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                p.dismiss();
                Toast.makeText(RegisterAcitivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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