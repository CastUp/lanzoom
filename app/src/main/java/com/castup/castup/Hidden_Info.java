package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Hidden_Info extends AppCompatActivity {

    private Toolbar    H_Toolbar ;
    private AdView     mAdView;
    private TextView   Phone , Email , City , Brithday , Address;
    private Switch     swithphone , swithEmail , swithCity, swithBrithday , swithAddress;

    private DatabaseReference  RootRef ;
    private FirebaseAuth       mAuth   ;
    private String             MYID    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden__info);

        RootRef    = FirebaseDatabase.getInstance().getReference();
        mAuth      = FirebaseAuth.getInstance();
        MYID       = mAuth.getCurrentUser().getUid();

        ViewTools();
        BarAction();
        ADMOB();
        HidePhone();
        ShowToolsData();


    }

    private void ViewTools(){

        Phone          = findViewById(R.id.textphone);
        Email          = findViewById(R.id.textemail);
        City           = findViewById(R.id.textCity);
        Brithday       = findViewById(R.id.textbirthday);
        Address        = findViewById(R.id.textaddress);

        swithphone     = findViewById(R.id.swithphone);
        swithEmail     = findViewById(R.id.swithemail);
        swithCity      = findViewById(R.id.swithCity);
        swithBrithday  = findViewById(R.id.swithbirthday);
        swithAddress   = findViewById(R.id.swithaddress);

    }

    private void BarAction(){

        H_Toolbar  = findViewById(R.id.HiddenTool);
        setSupportActionBar(H_Toolbar);
        getSupportActionBar().setTitle("Hidden Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void ADMOB (){

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = findViewById(R.id.adViewH);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

    }

    private void ShowToolsData(){

        RootRef.child("Users").child(MYID).child("appearance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                    String PhoneToolsData    = dataSnapshot.child("appearphone").getValue().toString();
                    String EmailToolsData    = dataSnapshot.child("appearemail").getValue().toString();
                    String CityToolsData     = dataSnapshot.child("appearcity").getValue().toString();
                    String BrithdayToolsData = dataSnapshot.child("appearbirthday").getValue().toString();
                    String AddressToolsData  = dataSnapshot.child("appearaddress").getValue().toString();

                    if(PhoneToolsData.equals("show")){

                        Phone.setText("Show mobile number");
                        swithphone.setChecked(false);

                    }if (PhoneToolsData.equals("hiden")){

                        Phone.setText("Hide mobile number");
                        swithphone.setChecked(true);

                    }if (EmailToolsData.equals("show")){

                        Email.setText("Show Email");
                        swithEmail.setChecked(false);

                    }if (EmailToolsData.equals("hiden")){

                        Email.setText("Hide Email");
                        swithEmail.setChecked(true);

                    }if (CityToolsData.equals("show")){

                        City.setText("Show City");
                        swithCity.setChecked(false);

                    }if (CityToolsData.equals("hiden")){

                        City.setText("Hide City");
                        swithCity.setChecked(true);

                    }if (BrithdayToolsData.equals("show")){

                        Brithday.setText("Show Brithday");
                        swithBrithday.setChecked(false);

                    }if (BrithdayToolsData.equals("hiden")){

                        Brithday.setText("Hide Brithday");
                        swithBrithday.setChecked(true);

                    }if (AddressToolsData.equals("show")){

                        Address.setText("Show Address");
                        swithAddress.setChecked(false);

                    }if (AddressToolsData.equals("hiden")){

                        Address.setText("Hide Address");
                        swithAddress.setChecked(true);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void HidePhone(){


        swithphone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    RootRef.child("Users").child(MYID).child("appearance").child("appearphone").setValue("hiden").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Phone.setText("Hide mobile number");

                            }

                        }
                    });


                }else {

                    RootRef.child("Users").child(MYID).child("appearance").child("appearphone").setValue("show").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Phone.setText("Show mobile number");

                            }

                        }
                    });

                }
            }
        });

        // ==============================================================================================================

        swithEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    RootRef.child("Users").child(MYID).child("appearance").child("appearemail").setValue("hiden").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Email.setText("Hide Email");

                            }

                        }
                    });


                }else {

                    RootRef.child("Users").child(MYID).child("appearance").child("appearemail").setValue("show").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Email.setText("Show Email");

                            }

                        }
                    });

                }
            }
        });

        //=============================================================================================================================

        swithCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    RootRef.child("Users").child(MYID).child("appearance").child("appearcity").setValue("hiden").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                City.setText("Hide City");

                            }

                        }
                    });


                }else {

                    RootRef.child("Users").child(MYID).child("appearance").child("appearcity").setValue("show").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                City.setText("Show City");

                            }

                        }
                    });

                }
            }
        });

        //=============================================================================================================================

        swithBrithday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    RootRef.child("Users").child(MYID).child("appearance").child("appearbirthday").setValue("hiden").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Brithday.setText("Hide Brithday");

                            }

                        }
                    });


                }else {

                    RootRef.child("Users").child(MYID).child("appearance").child("appearbirthday").setValue("show").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Brithday.setText("Show Brithday");

                            }

                        }
                    });

                }
            }
        });

        //=============================================================================================================================

        swithAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    RootRef.child("Users").child(MYID).child("appearance").child("appearaddress").setValue("hiden").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Address.setText("Hide Address");

                            }

                        }
                    });


                }else {

                    RootRef.child("Users").child(MYID).child("appearance").child("appearaddress").setValue("show").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Address.setText("Show Address");

                            }

                        }
                    });

                }
            }
        });

    }

}
