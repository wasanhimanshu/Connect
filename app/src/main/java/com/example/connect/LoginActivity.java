package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.MissingFormatArgumentException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
private TextInputEditText mEmailet,mPasset;
private Button mLoginBtn;
private TextView forgot_pass,new_user,welcometext,signintv;
private FirebaseAuth mAuth;
private FirebaseUser mUser;
private Typeface MRR;
private Typeface MR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Connect");

        mEmailet=(TextInputEditText)findViewById(R.id.login_email);
        mPasset=(TextInputEditText)findViewById(R.id.login_pass);
        mLoginBtn=(Button)findViewById(R.id.login_btn);
        forgot_pass=(TextView)findViewById(R.id.forgot_pass);
        new_user=(TextView)findViewById(R.id.new_user);
        welcometext=(TextView)findViewById(R.id.loginwelcome_text);
        signintv=(TextView)findViewById(R.id.pleasesignin);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        MRR=Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(getAssets(),"Lato-Bold.ttf");
        welcometext.setTypeface(MR);
        signintv.setTypeface(MRR);
        forgot_pass.setTypeface(MRR);
        new_user.setTypeface(MRR);
        mEmailet.setTypeface(MR);
        mPasset.setTypeface(MR);


forgot_pass.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
        finish();
    }
});


        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterAcitivity.class));
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Objects.requireNonNull(mEmailet.getText()).toString().trim();
                String pass= Objects.requireNonNull(mPasset.getText()).toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmailet.setError("Please ENter a valid Email");
                    mEmailet.setFocusable(true);
                }else if(pass.length()<6){
                    mPasset.setError("Please Enter a valid password");
                    mPasset.setFocusable(true);
                }else{

                    ProgressDialog p=new ProgressDialog(LoginActivity.this);
                    p.setMessage("Please wait for a moment");
                    p.setTitle("Logging In");
                    p.show();

                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                p.dismiss();
                                Intent i=new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            p.dismiss();
                            Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser!=null){
            Intent i=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}