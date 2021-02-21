package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar              MainToolbar ;
    private ViewPager            MainViewPager ;
    private TabLayout            MainTablayout ;
    private FloatingActionButton MainFloatingButton ;
    private TabsViewFragments    fragments ;

    private DrawerLayout drawerLayout ;
    private ActionBarDrawerToggle barDrawerToggle ;
    private NavigationView navigationView ;

    private FirebaseAuth         mAuth ;
    private FirebaseUser         firebaseUser ;
    private DatabaseReference    UserRef ;
    private StorageReference   Storageimage ;

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor ;
    private String StateNow , Chooser ;
    private ProgressDialog loadingbar ;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth           = FirebaseAuth.getInstance();
        firebaseUser    = mAuth.getCurrentUser();
        UserRef         = FirebaseDatabase.getInstance().getReference();
        Storageimage    = FirebaseStorage.getInstance().getReference();

        View_Tools();
        ActionBar();
        View_Fragments();
        VerifyUserExistance();
        FindFriends();
        NavigationView();


    }
    private void View_Tools(){


        MainToolbar        = findViewById(R.id.Main_Action_Bar);
        MainViewPager      = findViewById(R.id.Main_ViewPager);
        MainTablayout      = findViewById(R.id.Main_Tabs);
        MainFloatingButton = findViewById(R.id.Main_FloatingButton);
        drawerLayout       = findViewById(R.id.DrawerLayout);
        navigationView     = findViewById(R.id.Navigation_Main);
        loadingbar         = new ProgressDialog(this);



    }

    private void ActionBar(){
        setSupportActionBar(MainToolbar);
        getSupportActionBar().setTitle("Lanzoom");

        barDrawerToggle    = new ActionBarDrawerToggle(this,drawerLayout,R.string.barDrawerToggle_open,R.string.barDrawerToggle_Close);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void View_Fragments(){

        fragments = new TabsViewFragments(getSupportFragmentManager());
        MainViewPager.setAdapter(fragments);
        MainTablayout.setupWithViewPager(MainViewPager);
    }

    private void VerifyUserExistance() {


        if(firebaseUser == null){

               finish();
              startActivity(new Intent(MainActivity.this,LoginPhone.class));


        }else {

           preferences   = getSharedPreferences("Stats",MODE_PRIVATE);
           StateNow      = preferences.getString("UserNow","online");

          if(StateNow.equals("online")){

              UpdateUserState("Online");

          }else if(StateNow.equals("busy")){

              UpdateUserState("Busy");

          }else if(StateNow.equals("meeting")){

              UpdateUserState("In a meeting");

          }else if(StateNow.equals("work")){

              UpdateUserState("At work");

          }else if(StateNow.equals("school")){

              UpdateUserState("At School");

          }else if(StateNow.equals("Offline")){

              UpdateUserState("Offline");

          }


        }

    }

    private void FindFriends(){

       mInterstitialAd = new InterstitialAd(this);
       mInterstitialAd.setAdUnitId("ca-app-pub-8175375039939984/1756322656");
       mInterstitialAd.loadAd(new AdRequest.Builder().build());


        MainFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent findfriend = new Intent(MainActivity.this,FindFriends.class);
                startActivity(findfriend);

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserInfo();


    }

    private void UserInfo(){


        UserRef.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild("fullname")){

                         Intent Info  = new Intent(MainActivity.this,UserInformation.class);
                                Info.putExtra("newup", "Yes");
                                startActivity(Info);

                         return;

                }if(!dataSnapshot.hasChild("coverimage") || !dataSnapshot.hasChild("profileimage")){

                         startActivity(new Intent(MainActivity.this,UploadPhoto.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateUserState(String State){

        String SaveCurrentTime ;

        Calendar calendar = Calendar.getInstance();

        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        String Months [] = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = currenttime.format(calendar.getTime());

        HashMap<String,Object> UpdateState = new HashMap<>();
                               UpdateState.put("date",Months[month]+" "+day+" "+year);
                               UpdateState.put("time",SaveCurrentTime);
                               UpdateState.put("state",State);


        UserRef.child("StatsUsers").child(firebaseUser.getUid()).updateChildren(UpdateState);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         if(barDrawerToggle.onOptionsItemSelected(item)){return true ; }

         return true ;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.Appearance:

                StateOfAppearance();

                break;

            case R.id.Profile:

                Intent  VeiwProfile = new Intent(MainActivity.this,Users_Profile.class);
                        VeiwProfile.putExtra("privacy", "Contants");
                        VeiwProfile.putExtra("ID",firebaseUser.getUid());
                        VeiwProfile.putExtra("UserID",firebaseUser.getUid());
                        startActivity(VeiwProfile);

                break;

            case R.id.Request:

                startActivity(new Intent(MainActivity.this,Requests.class));

                break;

            case R.id.SentRequst:

                startActivity(new Intent(MainActivity.this,SentRequests.class));

                break;

            case R.id.Settings:

                  startActivity(new Intent(MainActivity.this,SettingsActivity.class));

                break;

            case R.id.Signout:

                if(firebaseUser != null){

                    UpdateUserState("Offline");
                }
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginPhone.class));

                break;

        }
        return false;
    }

    private void NavigationView(){



        View HeadVeiw = navigationView.getHeaderView(0);

        final TextView        UserName        = HeadVeiw.findViewById(R.id.navigation_UserName);
        final TextView        UserStatus      = HeadVeiw.findViewById(R.id.navigation_UserState);
        final ImageView       UserCover       = HeadVeiw.findViewById(R.id.navigation_cover);
        final ImageView       ChangeCover     = HeadVeiw.findViewById(R.id.changecover);
        final CircleImageView ImageCamera     = HeadVeiw.findViewById(R.id.navigation_camera);
        final CircleImageView UserPhoto       = HeadVeiw.findViewById(R.id.navigation_Photo);

        try {

            UserRef.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if(dataSnapshot.hasChild("coverimage")){

                            Picasso.get().load(dataSnapshot.child("coverimage").getValue().toString()).resize(900,500).into(UserCover);

                        }if(dataSnapshot.hasChild("profileimage")){

                            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(500,500).into(UserPhoto);

                        }if(dataSnapshot.hasChild("surname")){

                            UserName.setText(dataSnapshot.child("surname").getValue().toString());

                        }if(dataSnapshot.hasChild("phone")){

                            UserStatus.setText(dataSnapshot.child("phone").getValue().toString());


                        }else {


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        UserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Gallary = new Intent(Intent.ACTION_GET_CONTENT);
                       Gallary.setType("image/*");
                       startActivityForResult(Gallary,100);
            }
        });

        ChangeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Cover = new Intent(Intent.ACTION_GET_CONTENT);
                Cover.setType("image/*");
                startActivityForResult(Cover,200);
            }
        });

        ImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Camira = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(Camira,300);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri ImageCoverUri = data.getData();

            CropImage.activity(ImageCoverUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setMaxZoom(0).start(this);
            Chooser = "image" ;

        }if (requestCode == 200 && resultCode == RESULT_OK && data != null) {

            Uri ImageProfileUri = data.getData();

            CropImage.activity(ImageProfileUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(4, 3).setMaxZoom(0).start(this);
            Chooser = "cover";

        }if(requestCode == 300 && resultCode == RESULT_OK && data != null){

            Uri ImageCameraUri = data.getData();

            CropImage.activity(ImageCameraUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setMaxZoom(0).start(this);
            Chooser = "camera" ;

        }if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == "cover"){

            CropImage.ActivityResult  resultCoverImage =  CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK ){

                loadingbar.setTitle("Set Cover Image");
                loadingbar.setMessage("Please Wait, Your Cover Image Is Updating..");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri ResultImage = resultCoverImage.getUri();

                StorageReference filePath = Storageimage.child("CoverImages").child(firebaseUser.getUid()+".jpg");

                UploadTask uploadTask = filePath.putFile(ResultImage);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(uri.isAbsolute()){

                                    String Cover =  uri.toString();

                                    UserRef.child("Users").child(firebaseUser.getUid()).child("coverimage").setValue(Cover).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        loadingbar.setTitle("Uploading Photo");
                        double P = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                        loadingbar.setMessage((int) P + " % Uploading...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();

                    }
                });

            }
        }

        //============================== ImageProfile ==============================================

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == "image"){

            CropImage.ActivityResult  resultProfileImage =  CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please Wait, Your Profile Image Is Updating..");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri ResultImageP = resultProfileImage.getUri();

                StorageReference filePath = Storageimage.child("ProfileImages").child(firebaseUser.getUid()+".jpg");

                UploadTask uploadTask = filePath.putFile(ResultImageP);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(uri.isAbsolute()){

                                    String ProfileIMG =  uri.toString();

                                    UserRef.child("Users").child(firebaseUser.getUid()).child("profileimage").setValue(ProfileIMG).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        loadingbar.setTitle("Uploading Photo");
                        double P = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                        loadingbar.setMessage((int) P + " % Uploading...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                    }
                });


            }
        }

        //==============================  CameraProfile ==============================================

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Chooser == "camera"){

            CropImage.ActivityResult  resultProfileCamera =  CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please Wait, Your Profile Image Is Updating..");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri ResultImageP = resultProfileCamera.getUri();

                StorageReference filePath = Storageimage.child("ProfileImages").child(firebaseUser.getUid()+".jpg");

                UploadTask uploadTask = filePath.putFile(ResultImageP);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(uri.isAbsolute()){

                                    String ProfileCamera =  uri.toString();

                                    UserRef.child("Users").child(firebaseUser.getUid()).child("profileimage").setValue(ProfileCamera).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        loadingbar.setTitle("Uploading Photo");
                        double P = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                        loadingbar.setMessage((int) P + " % Uploading...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                    }
                });


            }

        }
    }

    private void StateOfAppearance(){

        PopupMenu  popupMenu = new PopupMenu(this,MainFloatingButton);
        popupMenu.inflate(R.menu.stateofapperance);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.online:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","online");
                        editor.apply();
                        recreate();

                        break;

                    case R.id.busy:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","busy");
                        editor.apply();
                        recreate();

                        break;

                    case R.id.meeting:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","meeting");
                        editor.apply();
                        recreate();

                        break;

                    case R.id.work:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","work");
                        editor.apply();
                        recreate();

                        break;

                    case R.id.school:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","school");
                        editor.apply();
                        recreate();

                        break;

                    case R.id.appearOffline:

                        preferences = getSharedPreferences("Stats",MODE_PRIVATE);
                        editor      = preferences.edit();
                        editor.putString("UserNow","Offline");
                        editor.apply();
                        recreate();

                        break;

                }
                return false;
            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(firebaseUser != null){

            UpdateUserState("Offline");
        }
    }

}
