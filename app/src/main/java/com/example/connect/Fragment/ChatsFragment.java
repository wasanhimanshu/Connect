package com.example.connect.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connect.Adapter.ChatListAdapter;
import com.example.connect.LoginActivity;
import com.example.connect.Model.ModelChat;
import com.example.connect.Model.ModelChatList;
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
import java.util.Objects;

public class ChatsFragment extends Fragment  {
   public ChatsFragment(){}
private RecyclerView mRecyclerview;
   private ChatListAdapter mAdpter;
   private List<ModelChatList> mchatlist;
   private List<ModelUser> mUser;
   private FirebaseAuth mAuth;
   private DatabaseReference mRef;
   private ProgressBar pb;
   private TextView emptytv;
   private Typeface MRR,MR;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.chats_fragment,container,false);
       mRecyclerview=(RecyclerView)view.findViewById(R.id.chatfrag_recyclerview);
       mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
       emptytv=(TextView)view.findViewById(R.id.empty_textview);
       mRecyclerview.setHasFixedSize(true);
       mAuth=FirebaseAuth.getInstance();
        MRR=Typeface.createFromAsset(requireContext().getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(requireContext().getAssets(),"Lato-Bold.ttf");
       mRef= FirebaseDatabase.getInstance().getReference().child("ChatList").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
       mchatlist=new ArrayList<>();


       pb=(ProgressBar)view.findViewById(R.id.cahtsfrag_pb);
       mRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchatlist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                      ModelChatList modelChatList=ds.getValue(ModelChatList.class);
                      mchatlist.add(modelChatList);

                }
                loadAllChats();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
            Log.e("ChatsFragment",error.getMessage());
           }
       });
       return view;
    }

    private void loadAllChats() {
        mUser=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelUser users = ds.getValue(ModelUser.class);
                    for(ModelChatList chatli:mchatlist){
                        assert users != null;
                        if(users.getUid().equals(chatli.getId())){
                            mUser.add(users);
                            break;
                        }
                    }
                    mAdpter=new ChatListAdapter(getContext(),mUser);
                    mRecyclerview.setAdapter(mAdpter);
                    pb.setVisibility(View.GONE);
                    if(mUser.size()==0){
                        emptytv.setText("No Chats Yet");
                        emptytv.setTypeface(MR);
                        emptytv.setVisibility(View.VISIBLE);
                        mRecyclerview.setVisibility(View.GONE);
                    }else {
                        emptytv.setVisibility(View.GONE);
                        mRecyclerview.setVisibility(View.VISIBLE);
                        for (int i = 0; i < mUser.size(); i++) {
                            getlastmsg(mUser.get(i).getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatsFragment",error.getMessage());
            }
        });
    }

    private void getlastmsg(String uid) {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Chats");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastmsg="deafult";
                String lastmgstime="default";
                boolean lastmsgseenornot=false;
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelChat c=ds.getValue(ModelChat.class);
                    if(c==null){
                        continue;
                    }else if(c.getSender()==null|| c.getReciever()==null){
                        continue;
                    }else if(c.getReciever().equals(mAuth.getUid())&& c.getSender().equals(uid) ||  c.getSender().equals(mAuth.getUid())&& c.getReciever().equals(uid)){
                        if(c.getMessagetype().equals("image")){
                            lastmsg="sent an Image";
                        }else if(c.getMessagetype().equals("text")){
                            lastmsg=c.getMessage();
                        }else if(c.getMessagetype().equals("pdf")){
                            lastmsg="sent a pdf";
                        }
                        lastmgstime=c.getTimeStamp();
                        lastmsgseenornot=c.isSeen();

                    }
                }
                mAdpter.setLastMsgMap(uid,lastmsg);
                mAdpter.setLastMsgTime(uid,lastmgstime);
                mAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatsFragment",error.getMessage());
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
        MenuItem item=menu.findItem(R.id.home_Search);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
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
