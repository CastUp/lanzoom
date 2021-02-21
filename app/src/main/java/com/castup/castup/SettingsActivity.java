package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar  S_Toolbar ;
    private CardView  UpdateInfo , HiddenInfo , DeletedUser;
    private AdView mAdView;
    private ProgressDialog progressDialog ;

    private FirebaseAuth       mAuth ;
    private FirebaseUser       accountUser ;
    private DatabaseReference  RootRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth        = FirebaseAuth.getInstance();
        accountUser  = mAuth.getCurrentUser();
        RootRef      = FirebaseDatabase.getInstance().getReference();

        ViewTools();
        BarAction();
        ADMOB();
        Up_Info();

    }

    private void ViewTools(){

        UpdateInfo     = findViewById(R.id.UptateInfo);
        HiddenInfo     = findViewById(R.id.HiddenInfo);
        DeletedUser    = findViewById(R.id.DeletedUser);
        progressDialog = new ProgressDialog(this);

    }

    private void BarAction(){

        S_Toolbar  = findViewById(R.id.SettingTool);
        setSupportActionBar(S_Toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void ADMOB (){

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = findViewById(R.id.adViewN);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

    }

    private void Up_Info(){

        UpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Up = new Intent(SettingsActivity.this, UserInformation.class);
                       Up.putExtra("newup","newupdate");
                       startActivity(Up);
            }
        });

        //===========================

        HiddenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 startActivity(new Intent(SettingsActivity.this,Hidden_Info.class));
            }
        });

        //===========================

        DeletedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder  dialog  = new AlertDialog.Builder(SettingsActivity.this);
                                     dialog.setTitle("Delete Account");
                                     dialog.setMessage("Upon deletion, all account information, friend and friend requests and messages related to this account will be lost\n"+"\n" +
                                                        "Are you sure you want to delete?");
                                     dialog.setIcon(R.drawable.ic_deletedaccount);

                                     dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {

                                             DeleteAccountUser();

                                         }
                                     });

                                     dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {


                                         }
                                     });

                                     dialog.show();
            }
        });

    }

    private void DeleteAccountUser() {

        progressDialog.setTitle("Delete Account");
        progressDialog.setMessage("Account being deleted");
        progressDialog.show();

        accountUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    RootRef.child("Users").child(accountUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                RootRef.child("StatsUsers").child(accountUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){


                                            startActivity(new Intent(SettingsActivity.this, LoginPhone.class));
                                            Toast.makeText(getBaseContext(),"Account has been deleted", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }else {

                    Toast.makeText(getBaseContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }


}
