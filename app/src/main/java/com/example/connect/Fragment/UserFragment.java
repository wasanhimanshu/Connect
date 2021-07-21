package com.example.connect.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connect.Adapter.UserAdapter;
import com.example.connect.LoginActivity;
import com.example.connect.Model.ModelUser;
import com.example.connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFragment  extends Fragment {
    public UserFragment(){}
    private RecyclerView mRecyclerView;
    private List<ModelUser>mUser;
    private UserAdapter mAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ProgressBar pb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.user_fragment,container,false);
       mRecyclerView=(RecyclerView)view.findViewById(R.id.user_recyclerview);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       mRecyclerView.setHasFixedSize(true);
       mUser=new ArrayList<>();
       mAuth=FirebaseAuth.getInstance();
       pb=(ProgressBar)view.findViewById(R.id.user_pb);
       loadUser();
       return view;
    }

    private void loadUser() {
        mRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelUser m=ds.getValue(ModelUser.class);
                    if(!m.getUid().equals(mAuth.getCurrentUser().getUid())){
                        mUser.add(m);
                    }
                }
                mAdapter=new UserAdapter(getContext(),mUser);
                mRecyclerView.setAdapter(mAdapter);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserFragment", error.getMessage());
            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem menuItem=(MenuItem)menu.findItem(R.id.home_Search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    loadoneuser(query);
                }else{
                loadUser();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(!TextUtils.isEmpty(query)){
                  loadoneuser(query);
                }else{
                    loadUser();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadoneuser(String query) {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelUser m=ds.getValue(ModelUser.class);
                    if(m.getEmail().toLowerCase().equals(query.toLowerCase()) || m.getName().toLowerCase().equals(query.toLowerCase()) ){
                        if (mAuth.getUid().equals(m.getUid())) {
                            continue;
                        }
                        mUser.add(m);
                    }
                }
                mAdapter=new UserAdapter(getContext(),mUser);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.signout){
            mAuth.signOut();
            Intent i=new Intent(getContext(), LoginActivity.class);
            startActivity(i);
            requireActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
