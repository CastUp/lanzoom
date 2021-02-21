package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginPhone extends AppCompatActivity {


    Spinner CodeCuntury ;
    EditText PhoneNumber , VerifyNumber ;
    Button   SentPhoneNumber ,SignIn ;
    TextView TextNumber , ChangeNumber ;
    TextView Welcom , lineander , entry , VerifyText ,TN ;

    private String NumberPhone ;
    private String Number ;
    private String Code  ;
    private String VerificationId ;
    private FirebaseAuth mAuth ;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks Callback ;
    private PhoneAuthProvider.ForceResendingToken ResendToken ;
    private DatabaseReference databaseReference ;
    private ProgressDialog loadingbar ;
    protected static final int PhonePormation = 10 ;
    protected static final int UploadImagesPormation = 20 ;
    protected static final int UploadCameraPormation = 30 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadingbar = new ProgressDialog(this);
        View_Tools();
        UploadPhotoPormotion();
        CounturyCods();



    }
    private void View_Tools(){

        CodeCuntury     = findViewById(R.id.CodePhone);
        PhoneNumber     = findViewById(R.id.PhoneNumber);
        SentPhoneNumber = findViewById(R.id.SentNumber);
        SignIn          = findViewById(R.id.SignIn);
        VerifyNumber    = findViewById(R.id.VerifyNumber);
        TextNumber      = findViewById(R.id.TextNumber);
        ChangeNumber    = findViewById(R.id.ChangeNumber);

        Welcom          = findViewById(R.id.welcome);
        lineander       = findViewById(R.id.lineander);
        entry           = findViewById(R.id.enter);
        VerifyText      = findViewById(R.id.VerifyText);
        TN              = findViewById(R.id.TN);

    }


    private void UploadPhotoPormotion(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},UploadImagesPormation);

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
    protected void onResume() {
        super.onResume();


        UploadCameraPormotion();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case PhonePormation :

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                    finish();
            }


            case UploadImagesPormation :

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                    finish();
                }

            case UploadCameraPormation :


                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                }if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                    finish();
            }

        }

    }

    private void CounturyCods(){

        CodeCuntury.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, SpinnerData.countryNames));

        SentPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Code = SpinnerData.countryAreaCodes[CodeCuntury.getSelectedItemPosition()];
                Number  = PhoneNumber.getText().toString().trim();

                if(TextUtils.isEmpty(Number)|| Number.length() > 15 || TextUtils.isEmpty(Code)){

                    PhoneNumber.setError("Valid number is required");
                    PhoneNumber.requestFocus();
                    return;

                }else {

                    loadingbar.setTitle("Sing In");
                    loadingbar.setMessage("Please Wait...");
                    loadingbar.setCanceledOnTouchOutside(true);
                    loadingbar.show();

                    NumberPhone = "+" + Code + Number ;

                    PhoneAuthProvider.getInstance()
                            .verifyPhoneNumber(NumberPhone,60, TimeUnit.SECONDS,LoginPhone.this,Callback);

                }
            }
        });

        //========================

        Callback =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(getBaseContext(),"Invalid PhoneNumber : " + e.getMessage(),Toast.LENGTH_SHORT).show();

                CodeCuntury.setVisibility(View.VISIBLE);
                PhoneNumber.setVisibility(View.VISIBLE);
                SentPhoneNumber.setVisibility(View.VISIBLE);
                Welcom.setVisibility(View.VISIBLE);
                lineander.setVisibility(View.VISIBLE);
                entry.setVisibility(View.VISIBLE);
                loadingbar.dismiss();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                VerificationId = s ;
                ResendToken = forceResendingToken ;

                Toast.makeText(getBaseContext(),"Code has been sent, Please Check and verify..",Toast.LENGTH_SHORT).show();

                VerifyText.setVisibility(View.VISIBLE);
                TN.setVisibility(View.VISIBLE);
                VerifyNumber.setVisibility(View.VISIBLE);
                SignIn.setVisibility(View.VISIBLE);
                TextNumber.setVisibility(View.VISIBLE);
                TextNumber.setText(NumberPhone);
                ChangeNumber.setVisibility(View.VISIBLE);
                ChangeNumber.setTextColor(Color.BLUE);

                CodeCuntury.setVisibility(View.GONE);
                PhoneNumber.setVisibility(View.GONE);
                SentPhoneNumber.setVisibility(View.GONE);
                Welcom.setVisibility(View.GONE);
                lineander.setVisibility(View.GONE);
                entry.setVisibility(View.GONE);
                loadingbar.dismiss();


            }
        };


        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String VerifitcationCode = VerifyNumber.getText().toString().trim();

                if (TextUtils.isEmpty(VerifitcationCode)){

                    VerifyNumber.setError("Enter Code..");
                    VerifyNumber.requestFocus();
                    return;

                }else{

                    loadingbar.setTitle("Sing In");
                    loadingbar.setMessage("Please Wait...");
                    loadingbar.setCanceledOnTouchOutside(true);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId,VerifitcationCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });



        ChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VerifyText.setVisibility(View.GONE);
                TN.setVisibility(View.GONE);
                VerifyNumber.setVisibility(View.GONE);
                SignIn.setVisibility(View.GONE);
                TextNumber.setVisibility(View.GONE);
                ChangeNumber.setVisibility(View.GONE);


                CodeCuntury.setVisibility(View.VISIBLE);
                PhoneNumber.setVisibility(View.VISIBLE);
                SentPhoneNumber.setVisibility(View.VISIBLE);
                Welcom.setVisibility(View.VISIBLE);
                lineander.setVisibility(View.VISIBLE);
                entry.setVisibility(View.VISIBLE);
                loadingbar.dismiss();

            }
        });


    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String UserCurrentID = mAuth.getCurrentUser().getUid();
                    String deviceToken   = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,Object> UsersInformation = new HashMap<>();
                    UsersInformation.put("phone",Number);
                    UsersInformation.put("country",SpinnerData.countryNames[CodeCuntury.getSelectedItemPosition()]);
                    UsersInformation.put("token",deviceToken);

                    databaseReference.child("Users").child(UserCurrentID).updateChildren(UsersInformation).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                startActivity(new Intent(LoginPhone.this,MainActivity.class));
                            }

                        }
                    });

                    loadingbar.dismiss();
                }else {

                    Toast.makeText(getBaseContext(),"Error : "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });


    }

}
