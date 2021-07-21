package com.example.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connect.ImageViewingActivity;
import com.example.connect.Model.ModelChat;
import com.example.connect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final Context context;
    List<ModelChat> chatList;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
private final FirebaseAuth mAuth;
    public MessageAdapter(Context context,List<ModelChat>chats){
        this.context=context;
        this.chatList=chats;
        mAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.message_right_list_item,parent,false);
            return new MessageViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.message_left_list_item,parent,false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
       final ModelChat chat=chatList.get(position);
       String message=chat.getMessage();
       String time=chat.getTimeStamp();
       String type=chat.getMessagetype();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if(type.equals("text")){
            holder.messageIv.setVisibility(View.GONE);
            holder.messagetv.setText(message);
            holder.messagetv.setVisibility(View.VISIBLE);
            holder.pdfImageView.setVisibility(View.GONE);
        }else if(type.equals("image")){
            holder.messagetv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.pdfImageView.setVisibility(View.GONE);
            Glide.with(context).load(message).into(holder.messageIv);
        }else if(type.equals("pdf")){
            holder.messagetv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.GONE);
            holder.pdfImageView.setVisibility(View.VISIBLE);
        }
        holder.timetv.setText(dateTime);

        if(position==chatList.size()-1){
            if(chat.isSeen()){
                holder.isSeentv.setText("Seen");
            }else{
                holder.isSeentv.setText("Delivered");
            }
        }else{
            holder.isSeentv.setVisibility(View.GONE);
        }


        holder.pdfImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat.getMessagetype().equals("pdf")&& chat.getMessagetype()!=null){
                    String filePath = chat.getMessage();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filePath), "application/pdf");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(Intent.createChooser(intent, "select an app to open this file"));
                }
            }
        });

        holder.messagetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenu(holder.messagetv,chat,position);
            }
        });




      holder.messageIv.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
           Intent i=new Intent(context, ImageViewingActivity.class);
           i.putExtra("image_url",message);
           context.startActivity(i);
          }
      });



    }

    private void createMenu(TextView messagetv, ModelChat chat,int position) {
        PopupMenu popupMenu=new PopupMenu(context,messagetv, Gravity.END);
        popupMenu.getMenu().add(0,0,0,"Delete for everyone");
        popupMenu.getMenu().add(0,1,0,"Star Message");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case 0:
                        delete(position);
                        break;
                    case 1:
                        break;

                }
                return true;
            }
        });
        popupMenu.show();

    }



    private void delete(int position) {
        final String myuid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String msgTimeStamp = chatList.get(position).getTimeStamp();
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Chats");
        Query query = db.orderByChild("timeStamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("sender").getValue().equals(myuid)) {
                        //remove the msg from chat
                        ds.getRef().removeValue();
                        //replace message with This message was deleted...
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted...");
//                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You can delete only your message...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              Log.e("MessageAdapter",databaseError.getMessage());
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
     private TextView messagetv,timetv,isSeentv;
     private ImageView messageIv,pdfImageView;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messagetv=(TextView)itemView.findViewById(R.id.messageTv);
            messageIv=(ImageView) itemView.findViewById(R.id.messageIv);
            isSeentv=(TextView)itemView.findViewById(R.id.isSeenTv);
            timetv=(TextView)itemView.findViewById(R.id.timeTv);
            pdfImageView=(ImageView)itemView.findViewById(R.id.pdfIv);

        }
    }

}
