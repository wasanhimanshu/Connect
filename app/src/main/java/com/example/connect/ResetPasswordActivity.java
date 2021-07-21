package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordActivity extends AppCompatActivity {
private TextInputEditText email_et;
private Button reset_btn;
private TextView hinttv;
private FirebaseAuth mAuth;
    private Typeface MRR;
    private Typeface MR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar tb=(Toolbar)findViewById(R.id.reset_activity_toolbar);
        setSupportActionBar(tb);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Reset password");
        email_et=(TextInputEditText)findViewById(R.id.reset_send_email);
        reset_btn=(Button)findViewById(R.id.btn_reset);
        hinttv=(TextView)findViewById(R.id.hint_tv);
        MRR=Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(getAssets(),"Lato-Bold.ttf");
        email_et.setTypeface(MR);
        hinttv.setTypeface(MRR);
        mAuth= FirebaseAuth.getInstance();
         reset_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String email=email_et.getText().toString().trim();
                 if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                     email_et.setError("Please Enter a valid Email");
                     email_et.setFocusable(true);
                 }else{
                     sendEmail(email);
                 }
             }
         });

    }

    private void sendEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this,"Please Check your email",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResetPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

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