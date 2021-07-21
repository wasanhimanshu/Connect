package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;

import java.util.logging.LogRecord;

public class StartActivity extends AppCompatActivity {
private TextView tv,by;
private Typeface MRR,MR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tv=(TextView)findViewById(R.id.tv);
        by=(TextView)findViewById(R.id.by);
        MRR=Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(getAssets(),"Lato-Bold.ttf");
        tv.setTypeface(MRR);
        by.setTypeface(MR);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1000);
    }

}