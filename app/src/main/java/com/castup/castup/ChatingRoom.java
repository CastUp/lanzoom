package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatingRoom extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String             GroupRefrance  ;
    private String             FriendID       ;
    private String             SaveCurrentTime , SaveCurrentDate ;

    private static final int   camera = 100 ;
    private static final int   photos = 200 ;
    private static final int   video  = 300 ;
    private static final int   audio  = 400 ;
    private static final int   pdf    = 500 ;
    private static final int   zip    = 600 ;
    private static final int   wordshit       = 10 ;
    private static final int   excelshit      = 20 ;
    private static final int   powerpointshit = 30 ;
    private String             Choser       ;
    protected static final int UploadCameraPormation = 1000 ;

    private DrawerLayout          drawerLayout ;
    private ActionBarDrawerToggle barDrawerToggle ;
    private NavigationView        navigationView ;
    private Toolbar               Toolbar_Groups ;
    private CardView              Card_ToolsChating , Card_chatingRoom ;
    private EmojiconEditText      TypeChatingGroup ;
    private ImageView             emotionGroup , filesGroup , CameraGroup ;
    private ImageView             Photo , Video , Audio , Pdf , MicroSoft , Zip;
    private CircleImageView       SendMessagesGroup ;
    private EmojIconActions       emojIcon ;
    private View                  Root ;
    private Resources             resources ;
    private int                   Tools = 0 ;
    private ProgressDialog        loadingbar  ;
    private RecyclerView                    Recycle_Groups  ;
    private ArrayList<Data_Messages_Groups> messagesGroup ;
    private RecycleRView_Message_Groups     Adapter ;

    private TextView              Custom_NameGroup , Custom_Numbers ;
    private CircleImageView       Custom_Image ;


    private FirebaseRecyclerOptions<Data_Firebase> options ;
    private FirebaseRecyclerAdapter<Data_Firebase,HolderGroup> adapter ;
    private DatabaseReference GroupRef ;
    private FirebaseAuth      mAuth ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating_room);

        GroupRefrance     = getIntent().getStringExtra("GroupName");
        FriendID          = getIntent().getStringExtra("Friend");
        resources         = getResources();

        GroupRef          = FirebaseDatabase.getInstance().getReference();
        mAuth             = FirebaseAuth.getInstance();


        View_Tools();
        Custom_ActionBar();
        CreateGroupUsers();
        SendFilesAndPhotos();
        SendMessages();
        ShowMessages();

        Recycle_Groups.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        Adapter  = new RecycleRView_Message_Groups(messagesGroup);
        Recycle_Groups.setAdapter(Adapter);


    }

    private void View_Tools(){

        drawerLayout       = findViewById(R.id.DrawerLayout);
        navigationView     = findViewById(R.id.Navigation_Main);
        Card_ToolsChating  = findViewById(R.id.Card_ToolsChating);
        Card_chatingRoom   = findViewById(R.id.Card_chatingRoom);
        Recycle_Groups     = findViewById(R.id.Recycle_Groups);
        messagesGroup      = new ArrayList<>();

        TypeChatingGroup   = findViewById(R.id.Type_Chating_Group);
        emotionGroup       = findViewById(R.id.emotion_Group);
        filesGroup         = findViewById(R.id.files_Group);
        CameraGroup        = findViewById(R.id.Camera_Group);
        SendMessagesGroup  = findViewById(R.id.Send_Messages_Group);

        Photo              = findViewById(R.id.Photo_Chating_Group);
        Video              = findViewById(R.id.Video_Chating_Group);
        Audio              = findViewById(R.id.Audio_Chating_Group);
        Pdf                = findViewById(R.id.PDF_Chating_Group);
        MicroSoft          = findViewById(R.id.Micro_Chating_Group);
        Zip               = findViewById(R.id.Txt_Chating_Group);

        Root               = findViewById(R.id.Root);
        loadingbar         = new ProgressDialog(this);

    }

    private void Custom_ActionBar(){

        Toolbar_Groups = findViewById(R.id.Toolbar_Groups);

        setSupportActionBar(Toolbar_Groups);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  Custom_Group      = inflater.inflate(R.layout.custom_toolbar_groups,null);
        getSupportActionBar().setCustomView(Custom_Group);

        Custom_Image      = findViewById(R.id.custom_Group_image);
        Custom_NameGroup  = findViewById(R.id.custom_Group_name);
        Custom_Numbers    = findViewById(R.id.custom_Group_Number);

        GroupRef.child("PublicGroups").child(GroupRefrance).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).resize(200,200).into(Custom_Image);
                    Custom_NameGroup.setText(dataSnapshot.child("name").getValue().toString());

                    if(dataSnapshot.hasChild("numbers")){

                       long Number = dataSnapshot.child("numbers").getChildrenCount();

                        Custom_Numbers.setText("Numbers : "+Number);

                    }else{}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // ============================================================================================

        barDrawerToggle    = new ActionBarDrawerToggle(this,drawerLayout,R.string.barDrawerToggle_open,R.string.barDrawerToggle_Close);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void CreateGroupUsers(){

        GroupRef.child("PublicGroups").child(GroupRefrance).child("numbers").child(FriendID).child("Group")
                .setValue(GroupRefrance).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){

                    Toast.makeText(getBaseContext(),"Welcome To " +GroupRefrance+" Room",Toast.LENGTH_SHORT).show();

                }

            }
        });

        NavigationView();
        animationTools();
    }

    private void animationTools(){

        filesGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Tools == 0){

                    Card_ToolsChating.setVisibility(View.VISIBLE);
                    Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.cardtools));
                    Tools = 1 ;

                }else {

                    Card_ToolsChating.setVisibility(View.GONE);
                    Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                    Tools = 0 ;
                }
            }
        });

        //==========================================

        emojIcon = new EmojIconActions(getApplicationContext(),Root,emotionGroup, TypeChatingGroup);
        emojIcon.ShowEmojicon();

        //===========================================



    }

    private void CameraPormotion(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},UploadCameraPormation);

            }

        }
    }

    private void ShowMessages(){

        GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Data_Messages_Groups UsersMessages = dataSnapshot.getValue(Data_Messages_Groups.class);

                        messagesGroup.add(UsersMessages);
                        Adapter.notifyDataSetChanged();
                        Recycle_Groups.smoothScrollToPosition(Recycle_Groups.getAdapter().getItemCount());



                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void SendFilesAndPhotos(){

        CameraGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraPormotion();

                Intent Camira = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Camira,camera);
            }
        });

        //======================================== Photos ==================================================

        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent Gallary = new Intent(Intent.ACTION_GET_CONTENT);
                Gallary.setType("image/*");
                startActivityForResult(Intent.createChooser(Gallary,"Select Photo File"),photos);
            }
        });

        //=============================================PDF ===================================================

        Pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent PDF = new Intent(Intent.ACTION_GET_CONTENT);
                       PDF.setType("application/pdf");
                       startActivityForResult(Intent.createChooser(PDF,"Select PDF File"),pdf);

            }
        });

        //=============================================Zip ===================================================

        Zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent Zep = new Intent(Intent.ACTION_GET_CONTENT);
                Zep.setType("application/zip");
                startActivityForResult(Intent.createChooser(Zep,"Select Zip File"),zip);

            }
        });

        //=============================================Micro ===================================================

         MicroSoft.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 PopupMenu MicroMenu = new PopupMenu(ChatingRoom.this,MicroSoft);
                 MicroMenu.inflate(R.menu.menu_micro);
                 MicroMenu.show();

                 MicroMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {


                            switch (item.getItemId()){


                                case R.id.Word:

                                    Card_ToolsChating.setVisibility(View.GONE);
                                    Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                                    Tools = 0 ;

                                    Intent Word = new Intent(Intent.ACTION_GET_CONTENT);
                                    Word.setType("application/msword");
                                    startActivityForResult(Intent.createChooser(Word,"Select Word sheet"),wordshit);

                                    break;

                                case R.id.Excel:

                                    Card_ToolsChating.setVisibility(View.GONE);
                                    Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                                    Tools = 0 ;

                                    Intent Excel = new Intent(Intent.ACTION_GET_CONTENT);
                                    Excel.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                                    startActivityForResult(Intent.createChooser(Excel,"Select Excel sheet"),excelshit);

                                    break;


                                case R.id.PowerPoint:

                                    Card_ToolsChating.setVisibility(View.GONE);
                                    Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                                    Tools = 0 ;

                                    Intent Power = new Intent(Intent.ACTION_GET_CONTENT);
                                    Power.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                                    startActivityForResult(Intent.createChooser(Power,"Select Power point sheet"),powerpointshit);

                                    break;
                            }

                         return false;
                     }
                 });
             }
         });

       //=============================================Video ===================================================

        Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent FileVideo = new Intent(Intent.ACTION_GET_CONTENT);
                FileVideo.setType("video/*");
                startActivityForResult(Intent.createChooser(FileVideo,"Select Video"),video);

            }
        });

        //=============================================audio ===================================================

        Audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent FileAudio = new Intent(Intent.ACTION_GET_CONTENT);
                FileAudio.setType("audio/*");
                startActivityForResult(Intent.createChooser(FileAudio,"Select Audio"),audio);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case UploadCameraPormation :


                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){


            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photos && resultCode == RESULT_OK && data != null) {

            Uri PhotoGallary = data.getData();

            CropImage.activity(PhotoGallary).setGuidelines(CropImageView.Guidelines.ON).setMaxZoom(0).start(this);
            Choser = "image";

        }if (requestCode == camera && resultCode == RESULT_OK && data != null) {

            Uri PhotoCamera = data.getData();

            CropImage.activity(PhotoCamera).setGuidelines(CropImageView.Guidelines.ON).setMaxZoom(0).start(this);
            Choser = "image";

        }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Choser.equals("image")) {

            loadingbar.setTitle("Sending Photo");
            loadingbar.setMessage("Please Wait, We are Sending that Photo..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            CropImage.ActivityResult resultCoverImage = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri ResultCamera = resultCoverImage.getUri();

                StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

                DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

                final String MessagePushID = MessageKeyRef.getKey();

                final StorageReference filepath = StorageRef.child(MessagePushID + "." + "jpg");

                StorageTask uploadTask = filepath.putFile(ResultCamera).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if (uri.isAbsolute()) {

                                    String UriPhoto = uri.toString();


                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                    SaveCurrentDate = currentdate.format(calendar.getTime());

                                    SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                    SaveCurrentTime = currenttime.format(calendar.getTime());

                                    Map messagePhotoBody = new HashMap();

                                    messagePhotoBody.put("message", UriPhoto);
                                    messagePhotoBody.put("sender", FriendID);
                                    messagePhotoBody.put("messageid", MessagePushID);
                                    messagePhotoBody.put("namegroup",GroupRefrance  );
                                    messagePhotoBody.put("type", "photo");
                                    messagePhotoBody.put("time", SaveCurrentTime);
                                    messagePhotoBody.put("date", SaveCurrentDate);
                                    messagePhotoBody.put("edit", "no");


                                    Map messageBodyDetalis = new HashMap();

                                    messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                    GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                            .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {

                                            if (task.isSuccessful()) {

                                                loadingbar.dismiss();
                                            } else {

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


                        loadingbar.setTitle("Sending Photo");
                        double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadingbar.setMessage((int) P + " % Uploading...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                    }
                });

            } else {
                loadingbar.dismiss();
            }

            //=============================================PDF ==============================================================


        }if (requestCode == pdf && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FilePDF = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "pdf");

            StorageTask uploadTask = filepath.putFile(FilePDF).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriPdf = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", UriPdf);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "pdf");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

            //=============================================Zip ==============================================================

        }if (requestCode == zip && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileZip = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "zip");

            StorageTask uploadTask = filepath.putFile(FileZip).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Urizip = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", Urizip);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "zip");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

            //=============================================word ==============================================================

        }if (requestCode == wordshit && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileWord = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "word");

            StorageTask uploadTask = filepath.putFile(FileWord).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriWord = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", UriWord);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "word");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

            //=============================================Excel ==============================================================

        }if (requestCode == excelshit && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileExcel = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "excel");

            StorageTask uploadTask = filepath.putFile(FileExcel).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriExcel = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", UriExcel);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "excel");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

            //=============================================PowerPoint ==============================================================

        }if (requestCode == powerpointshit && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FilePower = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "powerpoint");

            StorageTask uploadTask = filepath.putFile(FilePower).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriPower = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", UriPower);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "powerpoint");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

            //=============================================Video ==============================================================

        }if (requestCode == video && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending Video");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileVideo = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "mp4");

            StorageTask uploadTask = filepath.putFile(FileVideo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriVideo = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", UriVideo);
                                messagePhotoBody.put("sender", FriendID);
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "video");
                                messagePhotoBody.put("time", SaveCurrentTime);
                                messagePhotoBody.put("date", SaveCurrentDate);
                                messagePhotoBody.put("edit", "no");


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending Video");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

        //=============================================Audio ==============================================================

        }if (requestCode == audio && resultCode == RESULT_OK && data != null) {

            loadingbar.setTitle("Sending audio");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri Fileaudio = data.getData();


            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("FilesGroups").child(GroupRefrance).child(FriendID);

            DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

            final String MessagePushID = MessageKeyRef.getKey();

            final StorageReference filepath = StorageRef.child(MessagePushID + "." + "mp3");

            StorageTask uploadTask = filepath.putFile(Fileaudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Uriaudio = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagePhotoBody = new HashMap();

                                messagePhotoBody.put("message", Uriaudio      );
                                messagePhotoBody.put("sender", FriendID        );
                                messagePhotoBody.put("messageid", MessagePushID);
                                messagePhotoBody.put("namegroup",GroupRefrance  );
                                messagePhotoBody.put("type", "audio"           );
                                messagePhotoBody.put("time", SaveCurrentTime   );
                                messagePhotoBody.put("date", SaveCurrentDate   );
                                messagePhotoBody.put("edit", "no"              );


                                Map messageBodyDetalis = new HashMap();

                                messageBodyDetalis.put(MessagePushID, messagePhotoBody);


                                GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                                        .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            loadingbar.dismiss();
                                        } else {

                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            } else {
                                loadingbar.dismiss();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    loadingbar.setTitle("Sending audio");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

        }
    }

    private void SendMessages(){


        SendMessagesGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Messages = TypeChatingGroup.getText().toString() ;

               if(TextUtils.isEmpty(Messages)){

                   Toast.makeText(getBaseContext(),resources.getString(R.string.frist_write_ChatingRoom),Toast.LENGTH_SHORT).show();

               }else {



                   DatabaseReference MessageKeyRef = GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages").push();

                   Calendar calendar = Calendar.getInstance();
                   SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                   SaveCurrentDate = currentdate.format(calendar.getTime());

                   SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                   SaveCurrentTime = currenttime.format(calendar.getTime());


                   String MessagePushID = MessageKeyRef.getKey();

                   Map messageTextBody = new HashMap();

                   messageTextBody.put("message"  ,"  "+Messages  +"  " );
                   messageTextBody.put("sender"   ,FriendID       );
                   messageTextBody.put("messageid",MessagePushID  );
                   messageTextBody.put("namegroup",GroupRefrance  );
                   messageTextBody.put("type"     ,"text"         );
                   messageTextBody.put("time"     ,SaveCurrentTime);
                   messageTextBody.put("date"     ,SaveCurrentDate);
                   messageTextBody.put("edit"     ,"no"           );


                   Map  messageBodyDetalis = new HashMap();

                   messageBodyDetalis.put(MessagePushID , messageTextBody);

                   GroupRef.child("PublicGroups").child(GroupRefrance).child("Messages")
                           .updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {

                           if ((task.isSuccessful())){


                           }
                       }
                   });
               }

               TypeChatingGroup.setText("");
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        return false;
    }


    private void NavigationView() {

        final View HeadVeiw = navigationView.getHeaderView(0);

        final ImageView    ImageHeader    = HeadVeiw.findViewById(R.id.Image_Header);
        final TextView     GruopHeader    = HeadVeiw.findViewById(R.id.GroupNameHeader);
        RecyclerView RecycleHearder = HeadVeiw.findViewById(R.id.Recycle_Header);


        GroupRef.child("PublicGroups").child(GroupRefrance).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                   Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).resize(200,200).into(ImageHeader);
                   GruopHeader.setText(dataSnapshot.child("name").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //==================================RecycleUsers ====================================================================

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                .setQuery(GroupRef.child("PublicGroups").child(GroupRefrance).child("numbers"),Data_Firebase.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderGroup>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderGroup holderGroup, int position, @NonNull Data_Firebase data_firebase) {

                final String Users = getRef(position).getKey();
                final String MYID  = mAuth.getCurrentUser().getUid();

                GroupRef.child("Users").child(Users).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            holderGroup.Layout.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(200,200).into(holderGroup.image);
                            holderGroup.surname.setText(dataSnapshot.child("surname").getValue().toString());
                            holderGroup.country.setText(dataSnapshot.child("country").getValue().toString());

                            if(!MYID.equals(Users)){

                               // MenuPopupHeader(holderGroup,Users,MYID);

                                holderGroup.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent UserProfile = new Intent(ChatingRoom.this,Users_Profile.class);
                                        UserProfile.putExtra("privacy","public");
                                        UserProfile.putExtra("ID",Users);
                                        UserProfile.putExtra("UserID",MYID);
                                        startActivity(UserProfile);
                                    }
                                });

                            }else {

                                holderGroup.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Snackbar snackbar = Snackbar.make(holderGroup.Layout,"My Profile",Snackbar.LENGTH_LONG);

                                                 snackbar.setAction(R.string.View_Profile_Snakbar, new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                                                         Intent UserProfile = new Intent(ChatingRoom.this,Users_Profile.class);
                                                         UserProfile.putExtra("privacy","public");
                                                         UserProfile.putExtra("ID",Users);
                                                         UserProfile.putExtra("UserID",MYID);
                                                         startActivity(UserProfile);
                                                     }
                                                 });
                                                   snackbar.setActionTextColor(Color.rgb(255,255,0));
                                                   snackbar.show();
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public HolderGroup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view_users_header   = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_users_header,parent,false);
                return new HolderGroup(view_users_header);
            }
        };


        RecycleHearder.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        RecycleHearder.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public static class HolderGroup extends RecyclerView.ViewHolder{

        CircleImageView  image ;
        TextView surname , country ;
        RelativeLayout Layout ;

        public HolderGroup(@NonNull View itemView) {
            super(itemView);

              image    = itemView.findViewById(R.id.ImageUserHeader);
              surname  = itemView.findViewById(R.id.SurNameUserHeader);
              country  = itemView.findViewById(R.id.CountryUsersHeader);
              Layout   = itemView.findViewById(R.id.Layout_Custom_Header);

        }
    }

    private void MenuPopupHeader(final HolderGroup holderGroup , final String UserID , final String MyID){

        holderGroup.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu Header_Menu = new PopupMenu(ChatingRoom.this,holderGroup.surname);
                Header_Menu.inflate(R.menu.menu_header_group);

                GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(MyID).child(UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String Value = dataSnapshot.child("value").getValue().toString() ;

                            if(Value.equals("sentban")){

                                Header_Menu.getMenu().findItem(R.id.Ban_Header).setTitle(resources.getString(R.string.Unbanning_HeaderUsers));

                            }if(Value.equals("receiveban")) {

                                Header_Menu.getMenu().findItem(R.id.Ban_Header).setVisible(false);

                            }

                        }else {

                            Header_Menu.getMenu().findItem(R.id.Ban_Header).setTitle(resources.getString(R.string.Ban_HeaderUsers));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Header_Menu.show();


                Header_Menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.Profile_Header:

                                Intent UserProfile = new Intent(ChatingRoom.this,Users_Profile.class);
                                UserProfile.putExtra("privacy","public");
                                UserProfile.putExtra("ID",UserID);
                                UserProfile.putExtra("UserID",MyID);
                                startActivity(UserProfile);

                                break;

                            case R.id.Ban_Header:

                                if(Header_Menu.getMenu().findItem(R.id.Ban_Header).getTitle().equals(resources.getString(R.string.Ban_HeaderUsers))){

                                    GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(MyID).child(UserID)
                                            .child("value").setValue("sentban").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(UserID).child(MyID)
                                                        .child("value").setValue("receiveban").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                            GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(MyID).child(UserID).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                    if(dataSnapshot.exists()){

                                                                        String Value = dataSnapshot.child("value").getValue().toString() ;

                                                                        if(Value.equals("sentban")){

                                                                            Header_Menu.getMenu().findItem(R.id.Ban_Header).setTitle(resources.getString(R.string.Unbanning_HeaderUsers));
                                                                            Toast.makeText(getBaseContext(),"User messages have been ban",Toast.LENGTH_SHORT).show();

                                                                        }if(Value.equals("receiveban")) {

                                                                            Header_Menu.getMenu().findItem(R.id.Ban_Header).setVisible(false);

                                                                        }

                                                                    }else {

                                                                        Header_Menu.getMenu().findItem(R.id.Ban_Header).setTitle(resources.getString(R.string.Ban_HeaderUsers));
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }

                                                    }
                                                });

                                            }
                                        }
                                    });

                                }else{

                                    GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(MyID).child(UserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                GroupRef.child("PublicGroups").child(GroupRefrance).child("UsersBan").child(UserID).child(MyID)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                            Header_Menu.getMenu().findItem(R.id.Ban_Header).setTitle(resources.getString(R.string.Ban_HeaderUsers));
                                                            Toast.makeText(getBaseContext(),"ban has been canceled",Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                            }
                                        }
                                    });

                                }

                                break;

                        }

                        return false;
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_group, menu);

        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(barDrawerToggle.onOptionsItemSelected(item)){

            return true ;}


          switch (item.getItemId()){

              case R.id.Exit_Group :

                  GroupRef.child("PublicGroups").child(GroupRefrance).child("numbers").child(FriendID)
                          .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {

                          if(task.isSuccessful()){

                              startActivity(new Intent(ChatingRoom.this,MainActivity.class));

                              Toast.makeText(getBaseContext(),"Log Out From "+GroupRefrance,Toast.LENGTH_SHORT).show();
                          }

                      }
                  });

                  break;

          }

        return true ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GroupRef.child("PublicGroups").child(GroupRefrance).child("numbers").child(FriendID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getBaseContext(),"Log Out From "+GroupRefrance,Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}


