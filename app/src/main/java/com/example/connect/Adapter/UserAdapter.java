package com.example.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connect.MessageActivity;
import com.example.connect.Model.ModelUser;
import com.example.connect.R;

import java.lang.reflect.GenericArrayType;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<ModelUser> mUser;
   private Typeface MRR;
   private Typeface MR;
    public UserAdapter(Context context,List<ModelUser>m){
        this.context=context;
        this.mUser=m;


    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_list_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        MRR=Typeface.createFromAsset(context.getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(context.getAssets(),"Lato-Bold.ttf");
   final ModelUser m=mUser.get(position);
   String name=m.getName();
   String bio=m.getBio();
   String image_url=m.getImage_url();
   String uid=m.getUid();
   holder.usernametv.setText(name);
   holder.userbiotv.setText(bio);
   holder.usernametv.setTypeface(MR);
   holder.userbiotv.setTypeface(MRR);

   if(image_url.equals("noImage")){
       holder.user_image.setImageResource(R.drawable.default_pic);
   }else{
       try{
           Glide.with(context).load(image_url).placeholder(R.drawable.default_pic).into(holder.user_image);
       }catch (Exception e){
           e.printStackTrace();
       }
   }

   holder.itemView.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Intent i=new Intent(context, MessageActivity.class);
           i.putExtra("hisuid",uid);
           context.startActivity(i);
       }
   });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{
  private TextView usernametv,userbiotv;
  private CircleImageView user_image;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image=(CircleImageView)itemView.findViewById(R.id.userli_imageview);
            usernametv=(TextView)itemView.findViewById(R.id.userli_nametv);
            userbiotv=(TextView)itemView.findViewById(R.id.userli_biotv);
        }
    }
}
