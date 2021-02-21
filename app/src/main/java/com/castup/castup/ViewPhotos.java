package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPhotos extends AppCompatActivity {

    DatabaseReference RefGetInfo ;
    CircleImageView  Sender ;
    TextView         Name , Date , Time , DownloadPhoto;
    ImageView        MessageImage ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_view_photo);


        ViewTools();
        RefGetInfo = FirebaseDatabase.getInstance().getReference();



    }

    @Override
    protected void onStart() {
        super.onStart();

        Info();
        Picasso.get().load(getIntent().getStringExtra("photonew")).into(MessageImage);
        Date.setText("Date: "+getIntent().getStringExtra("Date"));
        Time.setText("Time: "+getIntent().getStringExtra("Time"));

        DownloadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                file_download();
            }
        });
    }

    private void ViewTools(){

        Sender         = findViewById(R.id.SenderPhoto);
        Name           = findViewById(R.id.SenderName);
        Date           = findViewById(R.id.Datephoto);
        Time           = findViewById(R.id.Timephoto);
        MessageImage   = findViewById(R.id.MessageImage);
        DownloadPhoto  = findViewById(R.id.DLPhoto);
    }

    private void Info(){

        String ID = getIntent().getStringExtra("Sender");

        RefGetInfo.child("Users").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(Sender);
                    Name.setText("Sender: "+dataSnapshot.child("surname").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void file_download() {

        String timeStamp = new SimpleDateFormat("YYYYMMDD_HHMMSS", Locale.getDefault()).format(System.currentTimeMillis());

        File path    = Environment.getExternalStorageDirectory();
        File direct  = new File(path + "/CustUp/");

        if (!direct.exists()) {

            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(getIntent().getStringExtra("photonew"));

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                .setAllowedOverRoaming(false).setTitle("Photo")
                .setDescription("")
                .setDestinationInExternalPublicDir("/CustUp/", timeStamp+".jpg");

        mgr.enqueue(request);

    }

}
