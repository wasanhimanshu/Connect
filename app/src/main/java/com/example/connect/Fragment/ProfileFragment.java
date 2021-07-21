package com.example.connect.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.connect.Model.ModelUser;
import com.example.connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthCredential;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private Typeface MRR,MR;
private TextView nametv,emailtv,phonetv,biotv;
private LinearLayout namell,phonell,bioll;
private String name,email,image_url,bio,phone;
private FirebaseAuth mAuth;
private DatabaseReference mRef;
private FloatingActionButton fab;
private CircleImageView circleImageView;
private static final int SELECT_IMAGE=101;
private Uri image_uri;
private ProgressBar pb;
private TextInputEditText changeEt;
private Button okbtn;
private TextView titletv;
private StorageReference storageReference;
    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.profile_fragment,container,false);
        MRR=Typeface.createFromAsset(getActivity().getAssets(),"Raleway-Medium.ttf");
        MR= Typeface.createFromAsset(getActivity().getAssets(),"Lato-Bold.ttf");
        namell=(LinearLayout)view.findViewById(R.id.name_edit_ll);
        phonell=(LinearLayout)view.findViewById(R.id.profile_phone_editll);
        bioll=(LinearLayout)view.findViewById(R.id.profile_bio_editll);
        nametv=(TextView)view.findViewById(R.id.profile_frag_nametv);
        emailtv=(TextView)view.findViewById(R.id.profile_frag_emailtv);
        phonetv=(TextView)view.findViewById(R.id.profile_frag_phonetv);
        biotv=(TextView)view.findViewById(R.id.profile_frag_biotv);
        fab=(FloatingActionButton)view.findViewById(R.id.selectaphoto);
        pb=(ProgressBar)view.findViewById(R.id.select_imagepb);
        circleImageView=(CircleImageView)view.findViewById(R.id.profile_frag_imageview);
        mAuth=FirebaseAuth.getInstance();
        mRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelUser user= ds.getValue(ModelUser.class);
                    if(user.getUid().equals(mAuth.getUid())) {
                        name = user.getName();
                        email = user.getEmail();
                        bio = user.getBio();
                        phone = user.getPhone();
                        image_url=user.getImage_url();
                        break;
                    }
                }
                nametv.setText(name);
                emailtv.setText(email);
                biotv.setText(bio);
                phonetv.setText(phone);
                nametv.setTypeface(MRR);
                emailtv.setTypeface(MRR);
                phonetv.setTypeface(MRR);
                biotv.setTypeface(MRR);
                if(image_url.equals("noImage")){
                    circleImageView.setImageResource(R.drawable.default_pic);
                }else{
                    try {
                        Glide.with(getContext()).load(image_url).placeholder(R.drawable.default_pic).into(circleImageView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
           Log.e("ProfileFragment",error.getMessage());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });
  DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_sheet_layout);
        changeEt=bottomSheetDialog.findViewById(R.id.bottom_sheet_et);
        titletv=bottomSheetDialog.findViewById(R.id.title_bottom_dialog);
        okbtn=bottomSheetDialog.findViewById(R.id.okbtn);
        namell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titletv.setText("Update Name");
                changeEt.setHint("Enter name");
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newname= Objects.requireNonNull(changeEt.getText()).toString().trim();
                        if(TextUtils.isEmpty(newname)){
                            bottomSheetDialog.cancel();
                        }else{
                            db.child("name").setValue(newname).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),"Name Updated Successfully!",Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            });
                        }
                    }

                });
                bottomSheetDialog.show();
            }
        });

        emailtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"you cannot change your email address!",Toast.LENGTH_SHORT).show();
            }
        });

        phonell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titletv.setText("Update Phone Number");
                changeEt.setHint("Enter Number");
                changeEt.setInputType(InputType.TYPE_CLASS_PHONE);
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newname= Objects.requireNonNull(changeEt.getText()).toString().trim();
                        if(TextUtils.isEmpty(newname)){
                            bottomSheetDialog.cancel();
                        }else{
                            db.child("phone").setValue(newname).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),"Number Updated Successfully!",Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            });
                        }
                    }
                });
                bottomSheetDialog.show();
            }

        });


        bioll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titletv.setText("Update Bio");
                changeEt.setHint("Enter Bio");
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newname= Objects.requireNonNull(changeEt.getText()).toString().trim();
                        if(TextUtils.isEmpty(newname)){
                            bottomSheetDialog.cancel();
                        }else{
                            db.child("bio").setValue(newname).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),"Bio Updated Successfully!",Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bottomSheetDialog.getDismissWithAnimation();
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                            });
                        }
                    }
                });
                bottomSheetDialog.show();
            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    image_uri=data.getData();
                    circleImageView.setImageURI(image_uri);
                    if(image_url.equals("noImage")){
                        uploadnewImage();
                    }else{
                        upadateImage();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upadateImage() {
        StorageReference reference=FirebaseStorage.getInstance().getReferenceFromUrl(image_url);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String filename=String.valueOf(System.currentTimeMillis());
                String filepathName="DisplayPic/"+"DisplayPic_"+filename;
                storageReference=FirebaseStorage.getInstance().getReference().child(filepathName);
                Bitmap bitmap = ((BitmapDrawable)circleImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String downloaduri=uriTask.getResult().toString();
                        if(uriTask.isSuccessful()){
                            mRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                            mRef.child("image_url").setValue(downloaduri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pb.setVisibility(View.GONE);
                                    Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb.setVisibility(View.GONE);
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        pb.setVisibility(View.VISIBLE);

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadnewImage() {
        String filename=String.valueOf(System.currentTimeMillis());
        String filepathName="DisplayPic/"+"DisplayPic_"+filename;
        storageReference=FirebaseStorage.getInstance().getReference().child(filepathName);
        Bitmap bitmap = ((BitmapDrawable)circleImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloaduri=uriTask.getResult().toString();
                if(uriTask.isSuccessful()){
                    mRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                    mRef.child("image_url").setValue(downloaduri).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pb.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }
}