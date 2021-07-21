package com.example.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connect.ImageViewingActivity;
import com.example.connect.Model.ModelChat;
import com.example.connect.R;

import java.net.ConnectException;
import java.util.List;

public class ScrollAdapter extends RecyclerView.Adapter<ScrollAdapter.ScrollViewHolder> {
    private Context context;
    private List<ModelChat> mChats;
    public ScrollAdapter(Context context,List<ModelChat>chats){
        this.context=context;
        this.mChats=chats;
    }
    @NonNull
    @Override
    public ScrollAdapter.ScrollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.scroll_list_item,parent,false);
        return new ScrollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollAdapter.ScrollViewHolder holder, int position) {
  final ModelChat chat=mChats.get(position);

  try{
      Glide.with(context).load(chat.getMessage()).placeholder(R.drawable.default_pic).into(holder.imageView);
  }catch (Exception e){
      e.printStackTrace();
  }
  holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Intent i=new Intent(context, ImageViewingActivity.class);
          i.putExtra("image_url",chat.getMessage());
          context.startActivity(i);
      }
  });

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }
    public static class ScrollViewHolder extends RecyclerView.ViewHolder{
  private ImageView imageView;
        public ScrollViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.scroll_imageview);
        }
    }
}
