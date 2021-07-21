package com.example.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connect.ImageViewingActivity;
import com.example.connect.MessageActivity;
import com.example.connect.Model.ModelChat;
import com.example.connect.Model.ModelUser;
import com.example.connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatlistViewHolder> {
private Context context;
private List<ModelUser> mUser;
    private HashMap<String, String> lastMsgMap;
    private HashMap<String, String> lastMsgTimeMap;
    private Typeface MRR;
    private Typeface MR;
    private DatabaseReference mRef;
    private int count=0;
    private FirebaseAuth mAuth;
public ChatListAdapter(Context context,List<ModelUser>user) {
    this.context = context;
    this.mUser = user;
    lastMsgMap = new HashMap<>();
    lastMsgTimeMap = new HashMap<>();
    mRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    mAuth = FirebaseAuth.getInstance();

}

private void unreadmsg(){
    DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Chats");

}

@NonNull
    @Override
    public ChatListAdapter.ChatlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.chat_list_view_item,parent,false);
       return new ChatlistViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatlistViewHolder holder, int position) {
        MRR= Typeface.createFromAsset(context.getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(context.getAssets(),"Lato-Bold.ttf");
      final ModelUser user=mUser.get(position);
      String name=user.getName();
      String image_url=user.getImage_url();
      String hisuid=user.getUid();
      String lastmsg=lastMsgMap.get(hisuid);
        if (lastmsg == null || lastmsg.equals("deafult")){
            holder.lastmsgtv.setVisibility(View.GONE);
        }else{
            holder.lastmsgtv.setVisibility(View.VISIBLE);
            holder.lastmsgtv.setText(lastmsg);
        }
        holder.nametv.setText(name);
        holder.nametv.setTypeface(MR);
        holder.lastmsgtv.setTypeface(MRR);
        try {
            Glide.with(context).load(image_url).placeholder(R.drawable.default_pic).into(holder.userimageviw);
        }catch (Exception e) {
            e.printStackTrace();
        }

        String lastmsgtime=lastMsgTimeMap.get(hisuid);
        if(lastmsgtime==null||lastmsgtime.equals("default")){
            holder.lastmsgtimetv.setVisibility(View.GONE);
        }else{
            String dateTime = DateFormat.format("hh:mm", Long.parseLong(lastmsgtime)).toString();
            holder.lastmsgtimetv.setText(dateTime);
            holder.lastmsgtimetv.setVisibility(View.VISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, MessageActivity.class);
                i.putExtra("hisuid",hisuid);
                context.startActivity(i);
            }
        });

        holder.userimageviw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ImageViewingActivity.class);
                if(user.getImage_url().equals("noImage")){
                    Toast.makeText(context, user.getName() + " has not Uploaded Profile Picture",Toast.LENGTH_SHORT).show();
                }else {
                    i.putExtra("image_url", user.getImage_url());
                    context.startActivity(i);
                }
            }
        });

    }
    public void setLastMsgMap(String userId, String lastMsg) {
        lastMsgMap.put(userId, lastMsg);
    }

    public void setLastMsgTime(String uid,String timestamp){
    lastMsgTimeMap.put(uid,timestamp);
    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ChatlistViewHolder extends RecyclerView.ViewHolder{
private CircleImageView userimageviw;
private TextView nametv,lastmsgtv,lastmsgtimetv;
        public ChatlistViewHolder(@NonNull View itemView) {
            super(itemView);
            userimageviw=(CircleImageView)itemView.findViewById(R.id.chatli_imageview);
            nametv=(TextView)itemView.findViewById(R.id.chatli_nametv);
            lastmsgtv=(TextView)itemView.findViewById(R.id.chatli_lastmgtv);
            lastmsgtimetv=(TextView)itemView.findViewById(R.id.chat_list_timetv);

        }
    }
}
