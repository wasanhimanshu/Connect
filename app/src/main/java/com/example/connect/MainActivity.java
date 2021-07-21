package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.connect.Fragment.ChatsFragment;
import com.example.connect.Fragment.ProfileFragment;
import com.example.connect.Fragment.UserFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
private TabLayout tabLayout;
private FirebaseAuth mAuth;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Connect");
        mAuth=FirebaseAuth.getInstance();
        tabLayout=(TabLayout)findViewById(R.id.main_tab_layout);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,new ChatsFragment()).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            Fragment selected_frag=null;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
             switch (tab.getPosition()){
                 case 0:
                     selected_frag=new ChatsFragment();
                     break;
                 case 1:
                     selected_frag=new UserFragment();
                     break;
                 case 2:
                     selected_frag=new ProfileFragment();
                     break;

             }
             getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,selected_frag).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }





}