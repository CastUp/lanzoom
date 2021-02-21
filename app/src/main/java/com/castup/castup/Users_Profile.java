package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Users_Profile extends AppCompatActivity {

    private Toolbar toolbar_UserProlile ;
    private AdView  mAdView;

    private ImageView       CoverImage ;
    private CircleImageView ImageProfile ;
    private Button          CancelRequest , RequesetButton ;
    private TextView        SurName , Status , CountFriends , Fullname , BirthDay , Gender , Country , City , Address , Langudage , Email , Phone , AboutMySelf;
    private ImageView       IconFullName ,IconAddress , IconBirthday , IconCity , IconEmail, IconPhone;
    private LinearLayout    linearMySelf , linearComunications;

    private String            ID ,UserID, Privacy ;
    private DatabaseReference UserRef , ChatRequestRef , ContactRef , BlockRef ;
    private FirebaseAuth      mAuth ;
    private String            Current_state = "new" ;
    private Resources         resources ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users__profile);

        Privacy          = getIntent().getStringExtra("privacy");
        resources        = getResources();

        ID               = getIntent().getStringExtra("ID");
        UserID           = getIntent().getStringExtra("UserID");
        UserRef          = FirebaseDatabase.getInstance().getReference();
        ChatRequestRef   = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        ContactRef       = FirebaseDatabase.getInstance().getReference().child("Contacts");
        BlockRef         = FirebaseDatabase.getInstance().getReference().child("BlockUsers");
        mAuth            = FirebaseAuth.getInstance();


        Veiw_Tools();
        ActionUserProfile();
        ADMOB();
        Show_UserInfo();
        ManageChatRequestsButtons();
        CountFriends();



    }

    private void Veiw_Tools(){


        CoverImage          = findViewById(R.id.Cover_UserProfile);
        ImageProfile        = findViewById(R.id.Image_UserProfile);
        SurName             = findViewById(R.id.SurName_UserProfile);
        Status              = findViewById(R.id.Status_UserProfile);
        CountFriends        = findViewById(R.id.CountFriends_UserProfile);


        CancelRequest       = findViewById(R.id.CanceltRequest_UserProfile);
        RequesetButton      = findViewById(R.id.RequestButton_UserProfile);

        Fullname            = findViewById(R.id.UserName_UserProfile);
        BirthDay            = findViewById(R.id.Birthday_UserProfile);
        Gender              = findViewById(R.id.Gender_UserProfile);
        Country             = findViewById(R.id.Country_UserProfile);
        City                = findViewById(R.id.City_UserProfile);
        Address             = findViewById(R.id.Address_UserProfile);
        Langudage           = findViewById(R.id.langudage_UserProfile);
        Email               = findViewById(R.id.Email_UserProfile);
        Phone               = findViewById(R.id.Phone_UserProfile);
        AboutMySelf         = findViewById(R.id.AboutMe_UserProfile);

        IconFullName        = findViewById(R.id.Icon_UserName_UserProfile);
        linearMySelf        = findViewById(R.id.linearAboutme);
        IconAddress         = findViewById(R.id.Icon_Address_Userprofile);
        IconBirthday        = findViewById(R.id.icon_Birthday_Userprofile);
        IconCity            = findViewById(R.id.Icon_City_Userprofile);
        IconEmail           = findViewById(R.id.Icon_Email_UsreProfile);
        IconPhone           = findViewById(R.id.Icon_Phone_UserProfile);
        linearComunications = findViewById(R.id.linearComunications);

    }

    private void ActionUserProfile(){

        toolbar_UserProlile = findViewById(R.id.Actionbar_UserProfile);
        setSupportActionBar(toolbar_UserProlile);
        getSupportActionBar().setTitle(R.string.Profile_personly_Userprofile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void ADMOB (){

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

    }

    private void Show_UserInfo(){

        UserRef.child("Users").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataUser(dataSnapshot,ID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DataUser(DataSnapshot dataSnapshot , String ID){

        if(dataSnapshot.exists()){

            Picasso.get().load(dataSnapshot.child("coverimage").getValue().toString()).into(CoverImage);
            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(ImageProfile);
            SurName.setText(dataSnapshot.child("surname").getValue().toString());
            Status.setText(dataSnapshot.child("status").getValue().toString());

            if(Privacy.equals("Contants") || UserID.equals(ID)){

                Gender.setText(dataSnapshot.child("gender").getValue().toString());
                Country.setText(dataSnapshot.child("country").getValue().toString());
                Langudage.setText(dataSnapshot.child("langudage").getValue().toString());

                Fullname.setVisibility(View.VISIBLE);
                IconFullName.setVisibility(View.VISIBLE);
                Fullname.setText(dataSnapshot.child("fullname").getValue().toString());


                if(TextUtils.isEmpty(dataSnapshot.child("myself").getValue().toString())){

                }if (!TextUtils.isEmpty(dataSnapshot.child("myself").getValue().toString())){

                    linearMySelf.setVisibility(View.VISIBLE);
                    AboutMySelf.setText(dataSnapshot.child("myself").getValue().toString());



                }if((TextUtils.isEmpty(dataSnapshot.child("address").getValue().toString())
                        && dataSnapshot.child("appearance").child("appearaddress").getValue().toString().equals("show"))
                        || (TextUtils.isEmpty(dataSnapshot.child("address").getValue().toString())
                        && dataSnapshot.child("appearance").child("appearaddress").getValue().equals("hiden"))
                        || (!TextUtils.isEmpty(dataSnapshot.child("address").getValue().toString())
                        && dataSnapshot.child("appearance").child("appearaddress").getValue().equals("hiden"))){


                }if(!TextUtils.isEmpty(dataSnapshot.child("address").getValue().toString())
                        && dataSnapshot.child("appearance").child("appearaddress").getValue().toString().equals("show")){

                    Address.setVisibility(View.VISIBLE);
                    IconAddress.setVisibility(View.VISIBLE);
                    Address.setText(dataSnapshot.child("address").getValue().toString());

                }if(dataSnapshot.child("appearance").child("appearbirthday").getValue().toString().equals("hiden")){



                }if(dataSnapshot.child("appearance").child("appearbirthday").getValue().toString().equals("show")){

                    BirthDay.setVisibility(View.VISIBLE);
                    IconBirthday.setVisibility(View.VISIBLE);
                    BirthDay.setText(dataSnapshot.child("birthday").getValue().toString());

                }if(dataSnapshot.child("appearance").child("appearcity").getValue().toString().equals("hiden")){



                }if(dataSnapshot.child("appearance").child("appearcity").getValue().toString().equals("show")){

                    City.setVisibility(View.VISIBLE);
                    IconCity.setVisibility(View.VISIBLE);
                    City.setText(dataSnapshot.child("city").getValue().toString());

                }if(dataSnapshot.child("appearance").child("appearemail").getValue().toString().equals("hiden")){



                }if(dataSnapshot.child("appearance").child("appearemail").getValue().toString().equals("show")){

                    Email.setVisibility(View.VISIBLE);
                    IconEmail.setVisibility(View.VISIBLE);
                    linearComunications.setVisibility(View.VISIBLE);
                    Email.setText(dataSnapshot.child("email").getValue().toString());

                }if(dataSnapshot.child("appearance").child("appearphone").getValue().toString().equals("hiden")){



                }if(dataSnapshot.child("appearance").child("appearphone").getValue().toString().equals("show")){

                    Phone.setVisibility(View.VISIBLE);
                    IconPhone.setVisibility(View.VISIBLE);
                    linearComunications.setVisibility(View.VISIBLE);
                    Phone.setText(dataSnapshot.child("phone").getValue().toString());

                }

            }if(Privacy.equals("public")){

                Gender.setText(dataSnapshot.child("gender").getValue().toString());
                Country.setText(dataSnapshot.child("country").getValue().toString());
                Langudage.setText(dataSnapshot.child("langudage").getValue().toString());

            }

        }
    }

    private void ManageChatRequestsButtons(){

        ChatRequestRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(ID)){

                    String Request_type = dataSnapshot.child(ID).child("request_type").getValue().toString();

                    if(Request_type.equals("sent")){

                        Current_state = "request_sent" ;
                        RequesetButton.setEnabled(true);
                        RequesetButton.setText(resources.getString(R.string.CancelRequest_userprofile));
                        RequesetButton.setBackground(resources.getDrawable(R.drawable.button_request_userprofile));


                    }else if(Request_type.equals("received")){

                        Current_state = "request_received" ;
                        RequesetButton.setText(resources.getString(R.string.AcceptRequest_userprofile));
                        RequesetButton.setEnabled(true);
                        RequesetButton.setBackground(resources.getDrawable(R.drawable.button_sendmessage_userprofile));

                        CancelRequest.setVisibility(View.VISIBLE);
                        CancelRequest.setText(resources.getString(R.string.CancelRequest_userprofile));
                        CancelRequest.setBackground(resources.getDrawable(R.drawable.button_request_userprofile));
                        CancelRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CancelChatRequest();
                            }
                        });

                    }

                }else{

                  ContactRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                          if (dataSnapshot.hasChild(ID)) {


                              Current_state = "friends";
                              RequesetButton.setText(resources.getString(R.string.Removethecontact_UserProfile));
                              RequesetButton.setBackground(resources.getDrawable(R.drawable.button_request_userprofile));
                          }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });

                  //==============================
                    BlockRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(ID)) {


                                Current_state = "Block";
                                RequesetButton.setVisibility(View.GONE);
                               CancelRequest.setVisibility(View.GONE);

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

        //===========================

        if(!mAuth.getCurrentUser().getUid().equals(ID)){

           RequesetButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   RequesetButton.setEnabled(false);

                   if(Current_state.equals("new")){

                       SendChatRequest();

                   }if (Current_state.equals("request_sent")){

                       CancelChatRequest();

                   }if(Current_state.equals("request_received")){

                       AcceptChatRequest();

                   }if (Current_state.equals("friends")){

                       RemoveContacts();

                   }if(Current_state.equals("Block")){

                       BlockUser();
                   }
               }
           });


        }else {

            RequesetButton.setVisibility(View.GONE);
        }

    }

    private void  SendChatRequest(){

        HashMap<String , Object> Map1 = new HashMap<>();
                                 Map1.put("request_type","sent");
                                 Map1.put("date", DateEvent());

        ChatRequestRef.child(mAuth.getCurrentUser().getUid()).child(ID).updateChildren(Map1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                HashMap<String , Object> Map2 = new HashMap<>();
                                         Map2.put("request_type","received");
                                         Map2.put("date", DateEvent());

                ChatRequestRef.child(ID).child(mAuth.getCurrentUser().getUid()).updateChildren(Map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Current_state = "request_sent" ;
                        RequesetButton.setEnabled(true);
                        RequesetButton.setText(resources.getString(R.string.CancelRequest_userprofile));
                        RequesetButton.setBackground(resources.getDrawable(R.drawable.button_request_userprofile));

                    }
                });
            }
        });

    }

    private void CancelChatRequest() {

        ChatRequestRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    ChatRequestRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Current_state = "new" ;
                                RequesetButton.setEnabled(true);
                                RequesetButton.setText(resources.getString(R.string.FriendRequest_userprofile));
                                RequesetButton.setBackground(resources.getDrawable(R.drawable.button_sendmessage_userprofile));
                                CancelRequest.setVisibility(View.GONE);
                                Snackbar.make(CoverImage,resources.getString(R.string.Successfullycanceled_Requests),Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private void AcceptChatRequest() {

        ContactRef.child(mAuth.getCurrentUser().getUid()).child(ID).child("contact").setValue("save").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    ContactRef.child(ID).child(mAuth.getCurrentUser().getUid()).child("contact").setValue("save").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                ChatRequestRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            ChatRequestRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        RequesetButton.setEnabled(true);
                                                        Current_state = "friends";
                                                        RequesetButton.setText(resources.getString(R.string.Removethecontact_UserProfile));
                                                        RequesetButton.setBackground(resources.getDrawable(R.drawable.button_request_userprofile));
                                                        CancelRequest.setVisibility(View.GONE);
                                                        CancelRequest.setEnabled(false);

                                                        Snackbar.make(CoverImage,resources.getString(R.string.Added_successfully_Requests),Snackbar.LENGTH_SHORT).show();

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
            }
        });
    }

    private void RemoveContacts() {

       ContactRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if(task.isSuccessful()){

                   ContactRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                               if (task.isSuccessful()){

                                   Current_state = "new" ;
                                   RequesetButton.setEnabled(true);
                                   RequesetButton.setText(resources.getString(R.string.FriendRequest_userprofile));
                                   RequesetButton.setBackground(resources.getDrawable(R.drawable.button_sendmessage_userprofile));
                                   CancelRequest.setVisibility(View.GONE);
                                   CancelRequest.setEnabled(false);
                                   Snackbar.make(CoverImage,resources.getString(R.string.Delete_successfully_Requests),Snackbar.LENGTH_SHORT).show();

                                   UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if(task.isSuccessful()){

                                               UserRef.child("ChatingPage").child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {

                                                       if (task.isSuccessful()){


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
           }
       });

    }

    private void CountFriends(){

        ContactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(mAuth.getCurrentUser().getUid().equals(ID)) {

                        long MYNumber = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildrenCount();

                        CountFriends.setText(resources.getString(R.string.Friends_UserProfile) + " " + MYNumber);

                    }if(!mAuth.getCurrentUser().getUid().equals(ID)){

                        long FriendNumber = dataSnapshot.child(ID).getChildrenCount();

                        CountFriends.setText(resources.getString(R.string.Friends_UserProfile) + " " + FriendNumber);

                    }

                }else {

                    CountFriends.setText(resources.getString(R.string.Friends_UserProfile) + " " + "0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void BlockUser(){

        BlockRef.child(mAuth.getCurrentUser().getUid()).child(ID).child("block").setValue("sentblock").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    BlockRef.child(ID).child(mAuth.getCurrentUser().getUid()).child("block").setValue("receivedblock").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                CancelRequest.setVisibility(View.GONE);
                                RequesetButton.setVisibility(View.GONE);
                                Current_state = "Block";

                                Snackbar.make(CoverImage,resources.getString(R.string.Userhasbeenbanned_menuReport),Snackbar.LENGTH_SHORT).show();

                                ContactRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            ContactRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        ChatRequestRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){

                                                                    ChatRequestRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){

                                                                                UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()){

                                                                                            UserRef.child("ChatingPage").child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if (task.isSuccessful()){


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
                                                            }
                                                        });

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

            }
        });
    }

    private void Dialog_Blocked(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(resources.getString(R.string.BlockUsers_UserProfile));
                            builder.setIcon(R.drawable.ic_blockuser);
                            builder.setMessage(resources.getString(R.string.Incase_UserProfile));
                            builder.setPositiveButton(resources.getString(R.string.block_UserProfile), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BlockUser();
                                }
                            });
                            builder.setNegativeButton(resources.getString(R.string.Cancel_UserProfile), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.show();
    }

    private void MenuChangeText(final Menu menu){

        BlockRef.child(mAuth.getCurrentUser().getUid()).child(ID).child("block").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if (dataSnapshot.getValue().toString().equals("sentblock")){

                        menu.findItem(R.id.UserBlock).setTitle(resources.getString(R.string.unBlock_menuReport));


                    }else if(dataSnapshot.getValue().toString().equals("receivedblock")) {

                        menu.findItem(R.id.UserBlock).setVisible(false);
                        menu.findItem(R.id.UserReport).setVisible(false);

                    }

                }else {

                    menu.findItem(R.id.UserBlock).setTitle(resources.getString(R.string.Block_menuReport));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UnblockUser(){

        BlockRef.child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    BlockRef.child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Current_state = "new" ;
                                CancelRequest.setVisibility(View.GONE);
                                CancelRequest.setEnabled(false);
                                RequesetButton.setVisibility(View.VISIBLE);
                                RequesetButton.setEnabled(true);
                                RequesetButton.setText(resources.getString(R.string.FriendRequest_userprofile));
                                RequesetButton.setBackground(resources.getDrawable(R.drawable.button_sendmessage_userprofile));
                                Snackbar.make(CoverImage,resources.getString(R.string.Dangerhasbeenremoved_menuReport),Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private String DateEvent(){

        Calendar calendar = Calendar.getInstance();

        int year  = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        String Months [] = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        return Months[month]+" "+day+"-"+year ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(!UserID.equals(ID)){

            getMenuInflater().inflate(R.menu.userreport,menu);
            menu.findItem(R.id.UserReport).setVisible(false);
            MenuChangeText(menu);
        }

        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

         switch (item.getItemId()){

            case  R.id.UserBlock :

                if(item.getTitle().equals(resources.getString(R.string.Block_menuReport))){
                    Dialog_Blocked();

                }else {

                    UnblockUser();
                }

                 break;

             case R.id.UserReport:

                  //Reports

                 break;
         }

         return false ;
    }

}
