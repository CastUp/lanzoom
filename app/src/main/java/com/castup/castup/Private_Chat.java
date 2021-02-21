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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

public class Private_Chat extends AppCompatActivity {

    private Toolbar       Private_Toolbar ;
    private RecyclerView  Recycle_Private ;
    private String        Friend_ID       ;
    private String        SaveCurrentTime , SaveCurrentDate ;

    private static final int   camera = 1000 ;
    private static final int   photos = 2000 ;
    private static final int   video  = 3000 ;
    private static final int   audio  = 4000 ;
    private static final int   pdf    = 5000 ;
    private static final int   zip    = 6000 ;
    private static final int   wordshit       = 100 ;
    private static final int   excelshit      = 200 ;
    private static final int   powerpointshit = 300 ;
    private String             Choser       ;
    protected static final int UploadCameraPormation = 10000 ;

    private CardView              Card_ToolsChating , Card_chatingRoom ;
    private EmojiconEditText      TypeChatingPrivate ;
    private ImageView             emotionPrivate , filesPrivate , CameraGroup ;
    private ImageView             Photo , Video , Audio , Pdf , MicroSoft , Zip;
    private CircleImageView       SendMessagesPrivate ;
    private EmojIconActions       emojIcon ;
    private View                  Root ;
    private Resources             resources ;
    private int                   Tools = 0 ;
    private ProgressDialog        loadingbar  ;

    private ArrayList<Data_Messages_Groups> PrivateChating ;
    private RecycleReView_Message_Private   adapterPrivate ;
    private FirebaseAuth      mAuth ;
    private DatabaseReference UserRef  ;
    private StorageReference  MediaRef ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private__chat);

        Friend_ID             = getIntent().getStringExtra("IDFRIEND");
        mAuth                 = FirebaseAuth.getInstance();
        UserRef               = FirebaseDatabase.getInstance().getReference();
        MediaRef              = FirebaseStorage.getInstance().getReference();

        View_Tools();
        ActionbarLayout();
        animationTools();
        SendMessages();
        Sendfiles();
        ShowMessagesPrivate();

        Recycle_Private.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapterPrivate  = new RecycleReView_Message_Private(PrivateChating);
        Recycle_Private.setAdapter(adapterPrivate);

    }

    private void View_Tools(){


       Private_Toolbar       = findViewById(R.id.tool_bar_private);
       Recycle_Private       = findViewById(R.id.Recycle_Private);

        Card_ToolsChating    = findViewById(R.id.Card_ToolsPrivate);
        Card_chatingRoom     = findViewById(R.id.Card_chatingPrivate);

        TypeChatingPrivate   = findViewById(R.id.Type_Chating_Private);
        emotionPrivate       = findViewById(R.id.emotion_Private);
        filesPrivate         = findViewById(R.id.files_Private);
        CameraGroup          = findViewById(R.id.Camera_Private);
        SendMessagesPrivate  = findViewById(R.id.Send_Messages_Private);

        Photo                = findViewById(R.id.Photo_Chating_Private);
        Video                = findViewById(R.id.Video_Chating_Private);
        Audio                = findViewById(R.id.Audio_Chating_Private);
        Pdf                  = findViewById(R.id.PDF_Chating_Private);
        MicroSoft            = findViewById(R.id.Micro_Chating_Private);
        Zip                  = findViewById(R.id.Txt_Chating_Private);

        Root                 = findViewById(R.id.RootR);
        loadingbar           = new ProgressDialog(this);
        PrivateChating       = new ArrayList<>();


    }

    private void CameraPormotion(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},UploadCameraPormation);

            }

        }
    }

    private void ActionbarLayout(){

        setSupportActionBar(Private_Toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);



        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Custom_Group      = inflater.inflate(R.layout.custom_toolbar_groups,null);
        getSupportActionBar().setCustomView(Custom_Group);

        final CircleImageView Custom_Image       = findViewById(R.id.custom_Group_image);
        final TextView        Custom_NameFriend  = findViewById(R.id.custom_Group_name);
        final TextView        Custom_state       = findViewById(R.id.custom_Group_Number);

        UserRef.child("Users").child(Friend_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(300,300).into(Custom_Image);
                    Custom_NameFriend.setText(dataSnapshot.child("surname").getValue().toString());

                    UserRef.child("StatsUsers").child(Friend_ID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotstate) {

                            if(dataSnapshotstate.exists()){

                                showDataforfriends(dataSnapshotstate,Custom_state);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showDataforfriends(DataSnapshot dataSnapshot , final TextView custom_state){


        if(dataSnapshot.child("state").getValue().toString().equals("Online")){

            custom_state.setText(dataSnapshot.child("state").getValue().toString());
            custom_state.setTextColor(Color.rgb(255,214,0));

        }if(dataSnapshot.child("state").getValue().toString().equals("Busy")){

            custom_state.setText(dataSnapshot.child("state").getValue().toString());
            custom_state.setTextColor(Color.RED);

        }if(dataSnapshot.child("state").getValue().toString().equals("In a meeting")){


            custom_state.setText(dataSnapshot.child("state").getValue().toString());
            custom_state.setTextColor(Color.RED);

        }if(dataSnapshot.child("state").getValue().toString().equals("At work")){


            custom_state.setText(dataSnapshot.child("state").getValue().toString());
            custom_state.setTextColor(Color.rgb(255,214,0));

        }if(dataSnapshot.child("state").getValue().toString().equals("At School")){

            custom_state.setText(dataSnapshot.child("state").getValue().toString());
            custom_state.setTextColor(Color.rgb(255,214,0));

        }if(dataSnapshot.child("state").getValue().toString().equals("Offline")){


            custom_state.setText("last seen " + " at "+ dataSnapshot.child("time").getValue().toString());
            custom_state.setTextColor(Color.WHITE);
        }


    }

    private void animationTools(){

        filesPrivate.setOnClickListener(new View.OnClickListener() {
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

        emojIcon = new EmojIconActions(getApplicationContext(),Root,emotionPrivate, TypeChatingPrivate);
        emojIcon.ShowEmojicon();


    }

    private void SendMessages(){

        SendMessagesPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String textmessages = TypeChatingPrivate.getText().toString();
                final String Sender       = mAuth.getCurrentUser().getUid();

                if(TextUtils.isEmpty(textmessages)){

                    Toast.makeText(getBaseContext(),R.string.frist_write_ChatingRoom,Toast.LENGTH_SHORT).show();

                }else {

                    DatabaseReference MessagesKeyRef = UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).push();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                    SaveCurrentDate = currentdate.format(calendar.getTime());

                    SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                    SaveCurrentTime = currenttime.format(calendar.getTime());

                    String PuchID = MessagesKeyRef.getKey();

                    Map messagesTextBody = new HashMap();

                    messagesTextBody.put("message"  ,"  "+ textmessages+"  " );
                    messagesTextBody.put("sender"   ,      Sender            );
                    messagesTextBody.put("receiver" ,      Friend_ID         );
                    messagesTextBody.put("messageid",      PuchID            );
                    messagesTextBody.put("type"     ,      "text"            );
                    messagesTextBody.put("time"     ,      SaveCurrentTime   );
                    messagesTextBody.put("date"     ,      SaveCurrentDate   );
                    messagesTextBody.put("edit"     ,      "no"              );

                    final Map  messageBody = new HashMap();

                    messageBody.put(PuchID , messagesTextBody);


                    UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if(task.isSuccessful()){

                                                    UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){


                                                            }
                                                        }
                                                    });

                                                }

                                            }
                                        });

                                    }
                                }
                            });

                }

                TypeChatingPrivate.setText("");
            }
        });


    }

    private void Sendfiles(){


        CameraGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraPormotion();

                Intent CamiraPrivate = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(CamiraPrivate,camera);
            }
        });

        //======================================== Photos ==================================================

        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent GallaryPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                GallaryPrivate.setType("image/*");
                startActivityForResult(Intent.createChooser(GallaryPrivate,"Select Photo File"),photos);

            }
        });

        //=============================================PDF ===================================================

        Pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent PDFPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                PDFPrivate.setType("application/pdf");
                startActivityForResult(Intent.createChooser(PDFPrivate,"Select PDF File"),pdf);

            }
        });

        //=============================================Zip ===================================================

        Zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent ZepPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                ZepPrivate.setType("application/zip");
                startActivityForResult(Intent.createChooser(ZepPrivate,"Select Zip File"),zip);

            }
        });

        //=============================================Micro ===================================================

        MicroSoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu MicroMenu = new PopupMenu(Private_Chat.this,MicroSoft);
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

                                Intent WordPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                                WordPrivate.setType("application/msword");
                                startActivityForResult(Intent.createChooser(WordPrivate,"Select Word sheet"),wordshit);

                                break;

                            case R.id.Excel:

                                Card_ToolsChating.setVisibility(View.GONE);
                                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                                Tools = 0 ;

                                Intent ExcelPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                                ExcelPrivate.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                                startActivityForResult(Intent.createChooser(ExcelPrivate,"Select Excel sheet"),excelshit);

                                break;


                            case R.id.PowerPoint:

                                Card_ToolsChating.setVisibility(View.GONE);
                                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                                Tools = 0 ;

                                Intent PowerPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                                PowerPrivate.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                                startActivityForResult(Intent.createChooser(PowerPrivate,"Select Power point sheet"),powerpointshit);

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

                Intent FileVideoPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                FileVideoPrivate.setType("video/*");
                startActivityForResult(Intent.createChooser(FileVideoPrivate,"Select Video"),video);

            }
        });

        //=============================================audio ===================================================

        Audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Card_ToolsChating.setVisibility(View.GONE);
                Card_ToolsChating.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_lift_to_right));
                Tools = 0 ;

                Intent FileAudioPrivate = new Intent(Intent.ACTION_GET_CONTENT);
                FileAudioPrivate.setType("audio/*");
                startActivityForResult(Intent.createChooser(FileAudioPrivate,"Select Audio"),audio);

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

        }
        if (requestCode == camera && resultCode == RESULT_OK && data != null) {

            Uri PhotoCamera = data.getData();

            CropImage.activity(PhotoCamera).setGuidelines(CropImageView.Guidelines.ON).setMaxZoom(0).start(this);
            Choser = "image";

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && Choser.equals("image")) {

            loadingbar.setTitle("Sending Photo");
            loadingbar.setMessage("Please Wait, We are Sending that Photo..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            CropImage.ActivityResult resultCoverImage = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri ResultCamera = resultCoverImage.getUri();
                final String Sender = mAuth.getCurrentUser().getUid();

                StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

                final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

                final String MessagePushID = MessageKeyRefPrivate.getKey();

                final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "jpg");

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

                                    Map messagesTextBody = new HashMap();

                                    messagesTextBody.put("message", UriPhoto);
                                    messagesTextBody.put("sender", Sender);
                                    messagesTextBody.put("receiver", Friend_ID);
                                    messagesTextBody.put("messageid", MessagePushID);
                                    messagesTextBody.put("type", "photo");
                                    messagesTextBody.put("time", SaveCurrentTime);
                                    messagesTextBody.put("date", SaveCurrentDate);
                                    messagesTextBody.put("edit", "no");

                                    final Map messageBody = new HashMap();

                                    messageBody.put(MessagePushID, messagesTextBody);


                                    UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                            .addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {

                                                    if (task.isSuccessful()) {

                                                        UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {

                                                                if (task.isSuccessful()) {

                                                                    UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                loadingbar.dismiss();
                                                                            }
                                                                        }
                                                                    });

                                                                }

                                                            }
                                                        });


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

        }else if (requestCode == pdf && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FilePDF = data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "pdf");

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

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message", UriPdf);
                                messagesTextBody.put("sender", Sender);
                                messagesTextBody.put("receiver", Friend_ID);
                                messagesTextBody.put("messageid", MessagePushID);
                                messagesTextBody.put("type", "pdf");
                                messagesTextBody.put("time", SaveCurrentTime);
                                messagesTextBody.put("date", SaveCurrentDate);
                                messagesTextBody.put("edit", "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


        //=============================================Zip ==============================================================

        }else if (requestCode == zip && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileZep = data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "zip");

            StorageTask uploadTask = filepath.putFile(FileZep).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriZep = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , UriZep            );
                                messagesTextBody.put("sender"    , Sender            );
                                messagesTextBody.put("receiver"  , Friend_ID         );
                                messagesTextBody.put("messageid" , MessagePushID     );
                                messagesTextBody.put("type"      , "zip"             );
                                messagesTextBody.put("time"      , SaveCurrentTime   );
                                messagesTextBody.put("date"      , SaveCurrentDate   );
                                messagesTextBody.put("edit"      , "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


            //=============================================Word ==============================================================

        }else if (requestCode == wordshit && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri Fileword = data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "word");

            StorageTask uploadTask = filepath.putFile(Fileword).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Uriword = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , Uriword           );
                                messagesTextBody.put("sender"    , Sender            );
                                messagesTextBody.put("receiver"  , Friend_ID         );
                                messagesTextBody.put("messageid" , MessagePushID     );
                                messagesTextBody.put("type"      , "word"             );
                                messagesTextBody.put("time"      , SaveCurrentTime   );
                                messagesTextBody.put("date"      , SaveCurrentDate   );
                                messagesTextBody.put("edit"      , "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


            //=============================================Excel ==============================================================

        }else if (requestCode == excelshit && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri FileExcel= data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "excel");

            StorageTask uploadTask = filepath.putFile(FileExcel).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Uriword = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , Uriword           );
                                messagesTextBody.put("sender"    , Sender            );
                                messagesTextBody.put("receiver"  , Friend_ID         );
                                messagesTextBody.put("messageid" , MessagePushID     );
                                messagesTextBody.put("type"      , "excel"           );
                                messagesTextBody.put("time"      , SaveCurrentTime   );
                                messagesTextBody.put("date"      , SaveCurrentDate   );
                                messagesTextBody.put("edit"      , "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


            //=============================================powerpoint ==============================================================

        }else if (requestCode == powerpointshit && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending File");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Uri Filepowerpoint= data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "powerpoint");

            StorageTask uploadTask = filepath.putFile(Filepowerpoint).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Uripowerpoint = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , Uripowerpoint     );
                                messagesTextBody.put("sender"    , Sender            );
                                messagesTextBody.put("receiver"  , Friend_ID         );
                                messagesTextBody.put("messageid" , MessagePushID     );
                                messagesTextBody.put("type"      , "powerpoint"      );
                                messagesTextBody.put("time"      , SaveCurrentTime   );
                                messagesTextBody.put("date"      , SaveCurrentDate   );
                                messagesTextBody.put("edit"      , "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending File");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


            //=============================================Audio ==============================================================

        }else if (requestCode == audio && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending Audio");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

                  Uri FileAudio = data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "mp3");

            StorageTask uploadTask = filepath.putFile(FileAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String UriAudio = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , UriAudio          );
                                messagesTextBody.put("sender"    , Sender            );
                                messagesTextBody.put("receiver"  , Friend_ID         );
                                messagesTextBody.put("messageid" , MessagePushID     );
                                messagesTextBody.put("type"      , "audio"           );
                                messagesTextBody.put("time"      , SaveCurrentTime   );
                                messagesTextBody.put("date"      , SaveCurrentDate   );
                                messagesTextBody.put("edit"      , "no");

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending Audio");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });


            //=============================================Video ==============================================================

        }else if (requestCode == video && resultCode == RESULT_OK && data != null) {


            loadingbar.setTitle("Sending Video");
            loadingbar.setMessage("Please Wait, We are Sending that file..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

                  Uri FileVideo = data.getData();
            final String Sender = mAuth.getCurrentUser().getUid();

            StorageReference StorageRefPrivate = FirebaseStorage.getInstance().getReference().child("FilesMessagesPrivate").child(Sender);

            final DatabaseReference MessageKeyRefPrivate = UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).push();

            final String MessagePushID = MessageKeyRefPrivate.getKey();

            final StorageReference filepath = StorageRefPrivate.child(MessagePushID + "." + "mp4");

            StorageTask uploadTask = filepath.putFile(FileVideo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if (uri.isAbsolute()) {

                                String Urivideo = uri.toString();


                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
                                SaveCurrentDate = currentdate.format(calendar.getTime());

                                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
                                SaveCurrentTime = currenttime.format(calendar.getTime());

                                Map messagesTextBody = new HashMap();

                                messagesTextBody.put("message"   , Urivideo       );
                                messagesTextBody.put("sender"    , Sender         );
                                messagesTextBody.put("receiver"  , Friend_ID      );
                                messagesTextBody.put("messageid" , MessagePushID  );
                                messagesTextBody.put("type"      , "video"        );
                                messagesTextBody.put("time"      , SaveCurrentTime);
                                messagesTextBody.put("date"      , SaveCurrentDate);
                                messagesTextBody.put("edit"     , "no"            );

                                final Map messageBody = new HashMap();

                                messageBody.put(MessagePushID, messagesTextBody);


                                UserRef.child("PrivateMessages").child("Messages").child(Sender).child(Friend_ID).updateChildren(messageBody)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {

                                                    UserRef.child("PrivateMessages").child("Messages").child(Friend_ID).child(Sender).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {

                                                                UserRef.child("ChatingPage").child(Friend_ID).child(Sender).child("Attendees").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            loadingbar.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }

                                                        }
                                                    });


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


                    loadingbar.setTitle("Sending Video");
                    double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    loadingbar.setMessage((int) P + " % Uploading...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            });

        }else {

            loadingbar.dismiss();
        }

    }

    private void ShowMessagesPrivate(){

        String MYid = mAuth.getCurrentUser().getUid();

        UserRef.child("PrivateMessages").child("Messages").child(MYid).child(Friend_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Data_Messages_Groups UsersMessagesPrivate = dataSnapshot.getValue(Data_Messages_Groups.class);

                PrivateChating.add(UsersMessagesPrivate);
                adapterPrivate.notifyDataSetChanged();
                Recycle_Private.smoothScrollToPosition(Recycle_Private.getAdapter().getItemCount());


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

}
