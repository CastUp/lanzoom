package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInformation extends AppCompatActivity {

    private Toolbar UserInformaionBar ;
    private EditText SurName , Status , Email , FullName ,Address, AboutYourSalf , City ;
    private TextView BarthDay;
    private Spinner  Gender , Langudage ,Country ;


    private FloatingActionButton FloatingActionBtn ;
    private DatePickerDialog.OnDateSetListener dateSetListener ;

    private FirebaseAuth mAuth ;
    private DatabaseReference UserRef ;

    private String Gender_Data , Langudage_date;

    private String Chooser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        mAuth   = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();
        Chooser = getIntent().getStringExtra("newup");
        Veiw_Tools();
        Actionbar();
        Update();


    }

    private void Veiw_Tools(){

        UserInformaionBar     = findViewById(R.id.UserInformaionbar);

        SurName               = findViewById(R.id.UserAccount_SurName);
        Status                = findViewById(R.id.UserAccount_status);
        Email                 = findViewById(R.id.UserAccount_Email);
        FullName              = findViewById(R.id.UserProfile_FullName);
        AboutYourSalf         = findViewById(R.id.UserYourSelf_AboutYourSelf);
        Address               = findViewById(R.id.UserLocation_Address);
        BarthDay              = findViewById(R.id.UserProfile_Age);
        City                  = findViewById(R.id.UserLocation_City);

        Gender                = findViewById(R.id.UserProfile_Gender);
        Langudage             = findViewById(R.id.UserProfile_Language);

        FloatingActionBtn     = findViewById(R.id.UserInformaionbar_FloatingActionBtn);

    }

    private void Actionbar(){

        setSupportActionBar(UserInformaionBar);
        getSupportActionBar().setTitle("User Information");

    }

    private void Update(){

        if(Chooser.equals("newupdate")){

            downloadinfo();

            UpLoadDataToFirebase();
            UserBarthDay();
            SpinnerSelected();


        }else{

            UpLoadDataToFirebase();
            UserBarthDay();
            SpinnerSelected();

        }

    }

    private void UserBarthDay(){

        BarthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar  = Calendar.getInstance();
                         int year  = calendar.get(Calendar.YEAR);
                         int month = calendar.get(Calendar.MONTH);
                         int day   = calendar.get(Calendar.DAY_OF_MONTH);

                         DatePickerDialog dialog = new DatePickerDialog(UserInformation.this,
                                                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                                         dateSetListener,year,month,day);

                         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                         dialog.show();
            }
        });


        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

               String Months [] = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

               BarthDay.setText(dayOfMonth+" "+Months[month]+" "+year);
            }
        };
    }

    private void SpinnerSelected(){


        //===================================Choose Gender ============================================================
        ArrayAdapter<String>  adapter ;

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,SpinnerData.ChooseGender);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Gender.setAdapter(adapter);
        Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Choose gender")){


                }else {

                    Gender_Data = parent.getItemAtPosition(position).toString();
                    Gender.setBackgroundColor(Color.WHITE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //===================================Choose Language ============================================================



        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,SpinnerData.ChooseLangudage);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Langudage.setAdapter(adapter);
        Langudage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Choose language")){


                }else {

                    Langudage_date = parent.getItemAtPosition(position).toString();
                    Langudage.setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void UpLoadDataToFirebase(){

        FloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean email = Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",Email.getText().toString().trim());

                if(TextUtils.isEmpty(SurName.getText().toString())){

                   SurName.setError("Don't leave empty");
                   SurName.requestFocus();
                   return;

                }if(SurName.length()> 15){

                    SurName.setError("15 characters only");
                    SurName.requestFocus();
                    return;

                }if(TextUtils.isEmpty(Status.getText().toString())){

                    Status.setError("Don't leave empty");
                    Status.requestFocus();
                    return;

                }if(Status.length()> 50){

                    Status.setError("50 characters only");
                    Status.requestFocus();
                    return;

                }if(TextUtils.isEmpty(Email.getText().toString())){

                    Email.setError("Don't leave empty");
                    Email.requestFocus();
                    return;

                }if(email == false){

                    Email.setError("Please input Email");
                    Email.requestFocus();
                    return;

                }if (TextUtils.isEmpty(FullName.getText().toString())){

                    FullName.setError("Don't leave empty");
                    FullName.requestFocus();
                    return;

                }if(FullName.length()>40){

                    FullName.setError("40 characters only");
                    FullName.requestFocus();
                    return;

                }if(Address.length()>80){

                    Address.setError("80 characters only");
                    Address.requestFocus();
                    return;

                }if(AboutYourSalf.length()>200){

                    AboutYourSalf.setError("200 characters only");
                    AboutYourSalf.requestFocus();
                    return;

                }if(City.length()>15){

                    City.setError("15 characters only");
                    City.requestFocus();
                    return;

                }if(TextUtils.isEmpty(City.getText().toString())){

                    City.setError("Don't leave empty");
                    City.requestFocus();
                    return;

                }if(TextUtils.isEmpty(BarthDay.getText().toString())){

                    BarthDay.setError("Don't leave empty");
                    BarthDay.requestFocus();
                    return;

                }if(TextUtils.isEmpty(Gender_Data)){

                    Snackbar.make(Gender,"Gender, Don't leave empty",Snackbar.LENGTH_SHORT).show();
                    Gender.setBackgroundColor(Color.rgb(249,173,173));
                    return;

                }if(TextUtils.isEmpty(Langudage_date)){

                    Snackbar.make(Gender,"Langudage, Don't leave empty",Snackbar.LENGTH_SHORT).show();
                    Langudage.setBackgroundColor(Color.rgb(249,173,173));
                    return;

                }else{

                    UploadUserInformation();
                }

            }
        });

    }

    private void UploadUserInformation(){

        HashMap<String,Object> hashMap = new HashMap<>();
                               hashMap.put("surname",SurName.getText().toString().trim().toUpperCase());
                               hashMap.put("status",Status.getText().toString().trim());
                               hashMap.put("email",Email.getText().toString().trim());
                               hashMap.put("fullname",FullName.getText().toString().trim());
                               hashMap.put("birthday",BarthDay.getText().toString().trim());
                               hashMap.put("city",City.getText().toString().trim());
                               hashMap.put("gender",Gender_Data.trim());
                               hashMap.put("langudage",Langudage_date.trim());
                               if(TextUtils.isEmpty(Address.getText().toString())){hashMap.put("address","");}else{ hashMap.put("address",Address.getText().toString().trim());}
                               if(TextUtils.isEmpty(AboutYourSalf.getText().toString())){hashMap.put("myself","");}else {hashMap.put("myself",AboutYourSalf.getText().toString().trim());}


        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    if(Chooser.equals("newupdate")){


                    }else {

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("appearbirthday","show");
                        map.put("appearcity","show");
                        map.put("appearemail","show");
                        map.put("appearphone","show");
                        map.put("appearaddress","show");
                        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("appearance").updateChildren(map);

                    }

                     startActivity(new Intent(UserInformation.this,UploadPhoto.class));
                }
            }
        });
    }

    private void downloadinfo(){

        String MyId = mAuth.getCurrentUser().getUid();

        UserRef.child("Users").child(MyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                    SurName.setText(dataSnapshot.child("surname").getValue().toString());
                    Status.setText(dataSnapshot.child("status").getValue().toString());
                    Email.setText(dataSnapshot.child("email").getValue().toString());
                    FullName.setText(dataSnapshot.child("fullname").getValue().toString());
                    BarthDay.setText(dataSnapshot.child("birthday").getValue().toString());
                    City.setText(dataSnapshot.child("city").getValue().toString());

                    if(dataSnapshot.hasChild("address")){

                        Address.setText(dataSnapshot.child("address").getValue().toString());

                    }if(dataSnapshot.hasChild("myself")){

                        AboutYourSalf.setText(dataSnapshot.child("myself").getValue().toString());

                    }else {


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
