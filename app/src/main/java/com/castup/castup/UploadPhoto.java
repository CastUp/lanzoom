package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPhoto extends AppCompatActivity {

    private ImageButton           CoverImageButton , ProfileImageButton ;
    private FloatingActionButton  floatingUploadImage ;
    private TextView              TextAddCoverPhoto , TextIgnore ;
    private ImageView             CoverImage ;
    private CircleImageView       ProfileImage , ProfileImageCamera ;

    private FirebaseAuth mAuth ;
    private DatabaseReference  UserRef ;
    private StorageReference Storageimage ;
    private ProgressDialog loadingbar ;
    private int Chooser ;
    protected static final int UploadImagesPormation = 10 ;
    protected static final int UploadCameraPormation = 20 ;
    protected static final int UploadCover   = 100 ;
    protected static final int UploadProfile = 200 ;
    protected static final int UploadCamera  = 300 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        mAuth        = FirebaseAuth.getInstance();
        UserRef      = FirebaseDatabase.getInstance().getReference();
        Storageimage = FirebaseStorage.getInstance().getReference();
        View_Tools();
        UploadPhotoPormotion();
        UploadCoverImage();
        UploadProfileImage();
        UploadProfileCamera();
        Show_Images();
        FloatingButton();
        Text_Igonre();


    }

    @Override
    protected void onResume() {
        super.onResume();

        UploadCameraPormotion();
    }

    private void View_Tools(){

        CoverImageButton    = findViewById(R.id.image_Button_Cover);
        ProfileImageButton  = findViewById(R.id.image_Button_profile);
        floatingUploadImage = findViewById(R.id.UserUploadPhoto_FloatingActionBtn);
        TextAddCoverPhoto   = findViewById(R.id.Addcoverphoto);
        TextIgnore          = findViewById(R.id.UserUploadPhoto_TEXT);
        CoverImage          = findViewById(R.id.CoverPhoto);
        ProfileImage        = findViewById(R.id.profileImage);
        ProfileImageCamera  = findViewById(R.id.UplaodprofileImage_Camera);

        loadingbar          = new ProgressDialog(this);

    }
    private void UploadPhotoPormotion(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},UploadImagesPormation);

            }
        }
    }
    private void UploadCameraPormotion(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},UploadCameraPormation);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case UploadImagesPormation :

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("coverimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/cover1.jpg?alt=media&token=95fa81a3-27b5-425f-8ee1-309c2c5ddfde");
                hashMap.put("profileimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/profile_image.png?alt=media&token=ca608388-9e00-4f3a-9d44-7862fc34dfba");

                UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            startActivity(new Intent(UploadPhoto.this, MainActivity.class));
                        }
                    }
                });
            }

            case UploadCameraPormation :


                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("coverimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/cover1.jpg?alt=media&token=95fa81a3-27b5-425f-8ee1-309c2c5ddfde");
                hashMap.put("profileimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/profile_image.png?alt=media&token=ca608388-9e00-4f3a-9d44-7862fc34dfba");

                UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            startActivity(new Intent(UploadPhoto.this, MainActivity.class));
                        }
                    }
                });

            }

        }
    }
    private void UploadCoverImage() {

        CoverImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Cover = new Intent();
                Cover.setAction(Intent.ACTION_GET_CONTENT);
                Cover.setType("image/*");
                startActivityForResult(Cover,UploadCover);
            }
        });

    }
    private void UploadProfileImage() {

        ProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Profile = new Intent();
                Profile.setAction(Intent.ACTION_GET_CONTENT);
                Profile.setType("image/*");
                startActivityForResult(Profile,UploadProfile);

            }
        });
    }
    private void UploadProfileCamera(){

        ProfileImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Camera,UploadCamera);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UploadCover && resultCode == RESULT_OK && data != null) {

            Uri ImageCoverUri = data.getData();

            CropImage.activity(ImageCoverUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(4,3).setMaxZoom(0).start(this);
            Chooser = 0 ;

        }if (requestCode == UploadProfile && resultCode == RESULT_OK && data != null) {

            Uri ImageProfileUri = data.getData();

            CropImage.activity(ImageProfileUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setMaxZoom(0).start(this);
            Chooser = 1 ;

        }if(requestCode == UploadCamera && resultCode == RESULT_OK && data != null){

            Uri ImageCameraUri = data.getData();

            CropImage.activity(ImageCameraUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setMaxZoom(0).start(this);
            Chooser = 2 ;

        }if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == 0){

                CropImage.ActivityResult  resultCoverImage =  CropImage.getActivityResult(data);

                 if(resultCode == RESULT_OK ){

                     loadingbar.setTitle("Set Cover Image");
                     loadingbar.setMessage("Please Wait, Your Cover Image Is Updating..");
                     loadingbar.setCanceledOnTouchOutside(false);
                     loadingbar.show();

                     Uri ResultImage = resultCoverImage.getUri();

                     StorageReference filePath = Storageimage.child("CoverImages").child(mAuth.getCurrentUser().getUid()+".jpg");

                     UploadTask uploadTask = filePath.putFile(ResultImage);

                     uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                             taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {

                                     if(uri.isAbsolute()){

                                         String Cover =  uri.toString();

                                         UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("coverimage").setValue(Cover).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {

                                                 if(task.isSuccessful()){

                                                     loadingbar.dismiss();

                                                 }else {

                                                     Toast.makeText(getBaseContext(), "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                     loadingbar.dismiss();
                                                 }
                                             }
                                         });
                                     }
                                 }
                             });
                         }
                     });

                 }
            }

        //============================== ImageProfile ==============================================

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == 1){

            CropImage.ActivityResult  resultProfileImage =  CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please Wait, Your Profile Image Is Updating..");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri ResultImageP = resultProfileImage.getUri();

                StorageReference filePath = Storageimage.child("ProfileImages").child(mAuth.getCurrentUser().getUid()+".jpg");

                UploadTask uploadTask = filePath.putFile(ResultImageP);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(uri.isAbsolute()){

                                    String ProfileIMG =  uri.toString();

                                    UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("profileimage").setValue(ProfileIMG).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){


                                                loadingbar.dismiss();

                                            }else {

                                                Toast.makeText(getBaseContext(), "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            }

        }

        //==============================  CameraProfile ==============================================

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == 2){

            CropImage.ActivityResult  resultProfileCamera =  CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please Wait, Your Profile Image Is Updating..");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri ResultImageP = resultProfileCamera.getUri();

                StorageReference filePath = Storageimage.child("ProfileImages").child(mAuth.getCurrentUser().getUid()+".jpg");

                UploadTask uploadTask = filePath.putFile(ResultImageP);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(uri.isAbsolute()){

                                    String ProfileCamera =  uri.toString();

                                    UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("profileimage").setValue(ProfileCamera).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                loadingbar.dismiss();

                                            }else {

                                                Toast.makeText(getBaseContext(), "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            }

        }

    }
    private void Show_Images(){

        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){



                    if(dataSnapshot.hasChild("coverimage")) {

                        String CoverPhoto = dataSnapshot.child("coverimage").getValue().toString();
                        Picasso.get().load(CoverPhoto).into(CoverImage);
                        TextAddCoverPhoto.setVisibility(View.GONE);
                        TextIgnore.setVisibility(View.GONE);

                    }if (dataSnapshot.hasChild("profileimage")){

                        String ProfilePhoto = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(ProfilePhoto).into(ProfileImage);
                        TextIgnore.setVisibility(View.GONE);

                    }if (dataSnapshot.hasChild("coverimage")&& dataSnapshot.hasChild("profileimage")){
                        if(dataSnapshot.child("coverimage").getValue().toString().equals("https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/cover1.jpg?alt=media&token=95fa81a3-27b5-425f-8ee1-309c2c5ddfde")){

                        }else {
                            Snackbar.make(CoverImage, "Image Save In Database, Successfully..", Snackbar.LENGTH_SHORT).show();
                            TextIgnore.setVisibility(View.GONE);
                            floatingUploadImage.setVisibility(View.VISIBLE);
                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FloatingButton (){

        floatingUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UploadPhoto.this, MainActivity.class));
            }
        });


    }
    private void Text_Igonre(){

        TextIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("coverimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/cover1.jpg?alt=media&token=95fa81a3-27b5-425f-8ee1-309c2c5ddfde");
                hashMap.put("profileimage","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/profile_image.png?alt=media&token=ca608388-9e00-4f3a-9d44-7862fc34dfba");

                UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            startActivity(new Intent(UploadPhoto.this, MainActivity.class));
                        }
                    }
                });
            }
        });

    }


}
