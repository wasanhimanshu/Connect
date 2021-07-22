package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connect.Adapter.MessageAdapter;
import com.example.connect.Model.ModelChat;
import com.example.connect.Model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
 private CircleImageView userimage;
 private TextView nameTv,Statustv;
 private FirebaseAuth mAuth;
 private ImageButton attachment_btn,send_btn;
 private TextInputEditText messageEt;
 private DatabaseReference mRef,userRefForSeen;
 private  String uid,myuid;
 private List<ModelChat>mchats;
 private MessageAdapter mAdapter;
 private RecyclerView mRecyclerview;
 private String name,status,image_url;
 private LinearLayout empylayout , gallery_ll,pdfll,camera_ll;
    public static final int RESULT_GALLERY = 0;
    public static final int RESULT_PDF = 1;
    public static final int RESULT_CAMERA = 2;
    private ValueEventListener seenListener;
    private Uri imageUri;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String[] cameraPermissions;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar=(Toolbar)findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();
         uid=i.getExtras().getString("hisuid");
        userimage=(CircleImageView)findViewById(R.id.message_userimage);
        nameTv=(TextView)findViewById(R.id.message_nametv);
        Statustv=(TextView)findViewById(R.id.message_statustv);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        myuid= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mRef=FirebaseDatabase.getInstance().getReference().child("Users");
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelUser user=ds.getValue(ModelUser.class);
                    assert user != null;
                    if(user.getUid().equals(uid)) {
                        name = "" + user.getName();
                        status = "" + user.getStatus();
                        image_url ="" + user.getImage_url();
                    }
                }
                nameTv.setText(name);
                Statustv.setText(status);
                try {
                        Glide.with(MessageActivity.this).load(image_url).placeholder(R.drawable.default_pic).into(userimage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 Log.e("MessageActivity",error.getMessage());
            }
        });
        attachment_btn=(ImageButton)findViewById(R.id.attachment_btn);
        send_btn=(ImageButton)findViewById(R.id.send_btn);
        messageEt=(TextInputEditText)findViewById(R.id.message_text);

        empylayout=(LinearLayout)findViewById(R.id.empty_chat);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= Objects.requireNonNull(messageEt.getText()).toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MessageActivity.this,"You cannot send Empty Message!!",Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
                messageEt.setText("");
            }
        });



        attachment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  BottomSheetDialog b=new BottomSheetDialog(MessageActivity.this);
                b.setContentView(R.layout.bottomsheetchat);

                 gallery_ll=b.findViewById(R.id.galley_ll);
                 pdfll=b.findViewById(R.id.pdf_ll);
                 camera_ll=b.findViewById(R.id.camera_ll);
                 assert gallery_ll != null;
                gallery_ll.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent galleryIntent = new Intent(
                                 Intent.ACTION_PICK,
                                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                         startActivityForResult(galleryIntent, RESULT_GALLERY);
                         b.cancel();
                     }
                 });

                pdfll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                        // We will be redirected to choose pdf
                        galleryIntent.setType("application/pdf");
                        startActivityForResult(galleryIntent, RESULT_PDF);
                        b.cancel();


                    }
                });

                camera_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkCameraPermission()) {
                            requestCameraPermission();
                        } else pickFromCamera();
                    b.cancel();
                    }
                });


                b.show();
            }

        });

        mRecyclerview=(RecyclerView)findViewById(R.id.message_recyclerview);
         LinearLayoutManager ll=new LinearLayoutManager(this);
         ll.setStackFromEnd(true);
         mRecyclerview.setLayoutManager(ll);
        mchats=new ArrayList<>();
        readMessages();
        setOnline();
        seenMessage();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MessageActivity.this,ProfileActivity.class);
                i.putExtra("hisuid",uid);
                startActivity(i);

            }
        });

    }





    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    assert chat != null;
                    if (chat.getReciever().equals(myuid) && chat.getSender().equals(uid)){
                        HashMap<String, Object> hasSeenHasNap = new HashMap<>();
                        hasSeenHasNap.put("seen", true);
                        ds.getRef().updateChildren(hasSeenHasNap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String message) {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Chats");
        String timeStamp= String.valueOf(System.currentTimeMillis());
        String randomid=ref.push().getKey();
        ModelChat chat=new ModelChat(message,myuid,uid,timeStamp,"text",false);

        assert randomid != null;
        ref.child(randomid).setValue(chat);
        DatabaseReference db1=FirebaseDatabase.getInstance().getReference().child("ChatList").child(uid).child(myuid);
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db1.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            Log.e("MessageActivity",error.getMessage());
            }
        });



        DatabaseReference db2=FirebaseDatabase.getInstance().getReference().child("ChatList").child(myuid).child(uid);
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity",error.getMessage());
            }
        });

    }




    private void readMessages() {
        DatabaseReference messageref=FirebaseDatabase.getInstance().getReference().child("Chats");
        messageref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchats.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chats=ds.getValue(ModelChat.class);
                    assert chats != null;
                    if(chats.getReciever().equals(myuid)&&chats.getSender().equals(uid) ||  chats.getReciever().equals(uid)&&chats.getSender().equals(myuid)){
                        mchats.add(chats);
                    }
                }
                if(mchats.size()!=0 ) {
                    mAdapter = new MessageAdapter(MessageActivity.this, mchats);
                    mRecyclerview.setAdapter(mAdapter);
                    empylayout.setVisibility(View.GONE);
                    mRecyclerview.setVisibility(View.VISIBLE);
                }else{
                     empylayout.setVisibility(View.VISIBLE);
                     mRecyclerview.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
             Log.e("MessageActivity",error.getMessage());
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

    private void clearchat() {

readMessages();
    }

    private void sendImageMesssage() throws IOException {
        ProgressDialog p= new ProgressDialog(this);
        p.setTitle("Sending Image");
        p.setMessage("Please Wait");

     String timeStamp= String.valueOf(System.currentTimeMillis());
     String filePathName="ImageMessages/"+"ImageMessages_"+timeStamp;
     StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathName);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloaduri= Objects.requireNonNull(uriTask.getResult()).toString();
                if(uriTask.isSuccessful()){
                    DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Chats");
                    String random=db.push().getKey();
                    ModelChat chat=new ModelChat(downloaduri,myuid,uid,timeStamp,"image",false);
                    assert random != null;
                    db.child(random).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                p.dismiss();
                                Toast.makeText(getApplicationContext(),"Image Sent!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            p.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                p.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                p.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference db1=FirebaseDatabase.getInstance().getReference().child("ChatList").child(uid).child(myuid);
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db1.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity",error.getMessage());
            }
        });



        DatabaseReference db2=FirebaseDatabase.getInstance().getReference().child("ChatList").child(myuid).child(uid);
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity",error.getMessage());
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_GALLERY:
                if (null != data) {
                    imageUri = data.getData();
                    try {
                        sendImageMesssage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RESULT_PDF:
                if(data!=null) {
                    imageUri = data.getData();
                    try{
                    sendPdf();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                break;
            case IMAGE_PICK_CAMERA_CODE:
                if(data!=null) {
                    imageUri = data.getData();
                    try{
                        sendImageMesssage();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
                default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendPdf() {
        ProgressDialog p= new ProgressDialog(this);
        p.setTitle("Sending Pdf");
        p.setMessage("Please Wait");

        String fileName=String.valueOf(System.currentTimeMillis());
        String filePathName="PDF/"+"PDF_"+fileName;
        StorageReference storageReference=FirebaseStorage.getInstance().getReference().child(filePathName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloadUri= Objects.requireNonNull(uriTask.getResult()).toString();
                if(uriTask.isSuccessful()){
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Chats");
                    String random=ref.push().getKey();
                    ModelChat chat=new ModelChat(downloadUri,myuid,uid,fileName,"pdf",false);
                    ref.child(random).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            p.dismiss();
                            Toast.makeText(getApplicationContext(),"Pdf sent",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            p.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                p.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                p.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference db1=FirebaseDatabase.getInstance().getReference().child("ChatList").child(uid).child(myuid);
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db1.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity",error.getMessage());
            }
        });



        DatabaseReference db2=FirebaseDatabase.getInstance().getReference().child("ChatList").child(myuid).child(uid);
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    db2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity",error.getMessage());
            }
        });


    }


    private void setLastSeen() {
      String timestamp= String.valueOf(System.currentTimeMillis());
      Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
        db.child("status").setValue("Last Seen at :"+ dateTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setLastSeen();
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnline();
    }

    private void setOnline() {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
        db.child("status").setValue("Online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLastSeen();

    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, RESULT_CAMERA);
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
               boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted&& writeStorageAccepted) {
                    pickFromCamera();
                } else {
                    Toast.makeText(this, "Please enable camera and storage permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    }



