package com.castup.castup;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecycleReView_Message_Private extends RecyclerView.Adapter<RecycleReView_Message_Private.HolderPrivate>{

    private ArrayList<Data_Messages_Groups> PrivateChating ;
    private Resources resources ;
    private DatabaseReference UsersRef ;
    private StorageReference MediaRef ;
    private FirebaseAuth mAuth ;

    public RecycleReView_Message_Private(ArrayList<Data_Messages_Groups> privateChating) {
        PrivateChating = privateChating;

    }

    @NonNull
    @Override
    public HolderPrivate onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View P_Chating = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_private_messages,parent,false);

        resources = parent.getResources();
        UsersRef  = FirebaseDatabase.getInstance().getReference();
        MediaRef  = FirebaseStorage.getInstance().getReference();
        mAuth     = FirebaseAuth.getInstance();

        return new HolderPrivate(P_Chating);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderPrivate holder, int position) {

        final String MYID  = mAuth.getCurrentUser().getUid();

        final Data_Messages_Groups ChatingMessagesPrivate = PrivateChating.get(position);

        UsersRef.child("Users").child(ChatingMessagesPrivate.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(180,180).into(holder.ImageSender);
                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(180,180).into(holder.ImageReceiver);

                    holder.SurNameSender.setText(dataSnapshot.child("surname").getValue().toString());
                    holder.SurNameReceiver.setText(dataSnapshot.child("surname").getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Messages(holder,ChatingMessagesPrivate,MYID);

        holder.CardSender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Deleted_EditSender(holder,ChatingMessagesPrivate,MYID,v.getContext());

                return false;
            }
        });

        holder.CardReceiver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Deleted_EditReceiver(holder,ChatingMessagesPrivate,MYID,v.getContext());

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return PrivateChating.size();
    }

    public class HolderPrivate extends RecyclerView.ViewHolder{

        CardView         CardSender , CardReceiver ;
        RelativeLayout   RLayoutSender , RLayoutReceiver, RfilsSender , RfilsReceiver ;
        CircleImageView  ImageSender , ImageReceiver , DownloadingSender , DownloadingReceiver ;
        ImageView        PhotoMessageSender  , PhotoMessageReceiver ,PhotofilesSender , PhotofilesReceiver ;
        TextView         DateSender  , DateReceiver , TimeSender ,TimeReceiver ,SurNameSender , SurNameReceiver ,
                         DownloadPhotoSender , ViewPhotoSender, DownloadPhotoReceiver , ViewPhotoReceiver , textSender , textReceiver ,
                         FileNameSender , FileNameReceiver , EditSender , EditReceiver ;
        ReadMoreTextView MessageSender , MessageReceiver ;

        public HolderPrivate(@NonNull View itemView) {
            super(itemView);

            CardSender               = itemView.findViewById(R.id.Card_Sender_Private);
            CardReceiver             = itemView.findViewById(R.id.Card_Receiver_Private);
            RLayoutSender            = itemView.findViewById(R.id.Sender_Layout);
            RLayoutReceiver          = itemView.findViewById(R.id.Receiver_Layout);
            ImageSender              = itemView.findViewById(R.id.ImageSender);
            ImageReceiver            = itemView.findViewById(R.id.ImageReceiver);
            SurNameSender            = itemView.findViewById(R.id.Surname_Sender_private);
            SurNameReceiver          = itemView.findViewById(R.id.Surname_Receiver_private);
            DateSender               = itemView.findViewById(R.id.Date_Sender_private);
            DateReceiver             = itemView.findViewById(R.id.Date_Receiver_private);
            TimeSender               = itemView.findViewById(R.id.Time_Sender_private);
            TimeReceiver             = itemView.findViewById(R.id.Time_Receiver_private);
            MessageSender            = itemView.findViewById(R.id.Text_Sender_private);
            MessageReceiver          = itemView.findViewById(R.id.Text_Receiver_private);
            PhotoMessageSender       = itemView.findViewById(R.id.photo_Sender_private);
            PhotoMessageReceiver     = itemView.findViewById(R.id.photo_Receiver_private);
            DownloadPhotoSender      = itemView.findViewById(R.id.Download_photo_Sender);
            DownloadPhotoReceiver    = itemView.findViewById(R.id.Download_photo_Receiver);
            ViewPhotoSender          = itemView.findViewById(R.id.View_photo_Sender);
            ViewPhotoReceiver        = itemView.findViewById(R.id.View_photo_Receiver);
            RfilsSender              = itemView.findViewById(R.id.RelativefilsSender);
            RfilsReceiver            = itemView.findViewById(R.id.RelativefilsReceiver);
            PhotofilesSender         = itemView.findViewById(R.id.photo_filsSender);
            PhotofilesReceiver       = itemView.findViewById(R.id.photo_filsReceiver);
            DownloadingSender        = itemView.findViewById(R.id.Dwonloading_filsSender);
            DownloadingReceiver      = itemView.findViewById(R.id.Dwonloading_filsReceiver);
            FileNameSender           = itemView.findViewById(R.id.textfilsSender);
            FileNameReceiver         = itemView.findViewById(R.id.textfilsReceiver);
            textSender               = itemView.findViewById(R.id.textS);
            textReceiver             = itemView.findViewById(R.id.textR);
            EditSender               = itemView.findViewById(R.id.EditedSender);
            EditReceiver             = itemView.findViewById(R.id.EditedReceiver);



        }
    }


    private void Messages(final HolderPrivate holder , final Data_Messages_Groups ChatingMessagesPrivate , String MYID ){


        holder.CardSender.setVisibility(View.GONE);
        holder.CardReceiver.setVisibility(View.GONE);
        holder.RLayoutSender.setVisibility(View.GONE);
        holder.RLayoutReceiver.setVisibility(View.GONE);
        holder.ImageSender.setVisibility(View.GONE);
        holder.ImageReceiver.setVisibility(View.GONE);
        holder.SurNameSender.setVisibility(View.GONE);
        holder.SurNameReceiver.setVisibility(View.GONE);
        holder.DateSender.setVisibility(View.GONE);
        holder.DateReceiver.setVisibility(View.GONE);
        holder.TimeSender.setVisibility(View.GONE);
        holder.TimeReceiver.setVisibility(View.GONE);
        holder.MessageSender.setVisibility(View.GONE);
        holder.MessageReceiver.setVisibility(View.GONE);
        holder.PhotoMessageSender.setVisibility(View.GONE);
        holder.PhotoMessageReceiver.setVisibility(View.GONE);
        holder.DownloadPhotoSender.setVisibility(View.GONE);
        holder.DownloadPhotoReceiver.setVisibility(View.GONE);
        holder.ViewPhotoSender.setVisibility(View.GONE);
        holder.ViewPhotoReceiver.setVisibility(View.GONE);
        holder.RfilsSender.setVisibility(View.GONE);
        holder.RfilsReceiver.setVisibility(View.GONE);
        holder.PhotofilesSender.setVisibility(View.GONE);
        holder.PhotofilesReceiver.setVisibility(View.GONE);
        holder.DownloadingSender.setVisibility(View.GONE);
        holder.DownloadingReceiver.setVisibility(View.GONE);
        holder.FileNameSender.setVisibility(View.GONE);
        holder.FileNameReceiver.setVisibility(View.GONE);
        holder.textSender.setVisibility(View.GONE);
        holder.textReceiver.setVisibility(View.GONE);
        holder.EditSender.setVisibility(View.GONE);
        holder.EditReceiver.setVisibility(View.GONE);



        if(ChatingMessagesPrivate.getType().equals("text")){

            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.MessageSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.MessageSender.setText(ChatingMessagesPrivate.getMessage());
                if(ChatingMessagesPrivate.getEdit().equals("yes")){holder.EditSender.setVisibility(View.VISIBLE);}


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.MessageReceiver.setVisibility(View.VISIBLE);


                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.MessageReceiver.setText(ChatingMessagesPrivate.getMessage());
                if(ChatingMessagesPrivate.getEdit().equals("yes")){holder.EditReceiver.setVisibility(View.VISIBLE);}

            }

        }else if(ChatingMessagesPrivate.getType().equals("photo")){


            if (ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.PhotoMessageSender.setVisibility(View.VISIBLE);
                holder.ViewPhotoSender.setVisibility(View.VISIBLE);
                holder.DownloadPhotoSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                Picasso.get().load(ChatingMessagesPrivate.getMessage()).resize(400,400).into(holder.PhotoMessageSender);

                holder.ViewPhotoSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent PhotoSender = new Intent(v.getContext(), ViewPhotos.class);

                        PhotoSender.putExtra("photonew", ChatingMessagesPrivate.getMessage());
                        PhotoSender.putExtra("Date"    , ChatingMessagesPrivate.getDate()   );
                        PhotoSender.putExtra("Time"    , ChatingMessagesPrivate.getTime()   );
                        PhotoSender.putExtra("Sender"  , ChatingMessagesPrivate.getSender() );

                        v.getContext().startActivity(PhotoSender);
                    }
                });

                holder.DownloadPhotoSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        file_download(v.getContext(),ChatingMessagesPrivate.getMessage(),"jpg","Photo");

                    }
                });

            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.PhotoMessageReceiver.setVisibility(View.VISIBLE);
                holder.ViewPhotoReceiver.setVisibility(View.VISIBLE);
                holder.DownloadPhotoReceiver.setVisibility(View.VISIBLE);


                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                Picasso.get().load(ChatingMessagesPrivate.getMessage()).resize(400,400).into(holder.PhotoMessageReceiver);

                holder.ViewPhotoReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent PhotoReceiver = new Intent(v.getContext(), ViewPhotos.class);

                        PhotoReceiver.putExtra("photonew", ChatingMessagesPrivate.getMessage());
                        PhotoReceiver.putExtra("Date"    , ChatingMessagesPrivate.getDate()   );
                        PhotoReceiver.putExtra("Time"    , ChatingMessagesPrivate.getTime()   );
                        PhotoReceiver.putExtra("Sender"  , ChatingMessagesPrivate.getSender() );

                        v.getContext().startActivity(PhotoReceiver);

                    }
                });

                holder.DownloadPhotoReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        file_download(v.getContext(),ChatingMessagesPrivate.getMessage(),"jpg","Photo");
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("pdf")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_chat_pdf);
                holder.FileNameSender.setText("  File Pdf");

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_chat_pdf);
                holder.FileNameReceiver.setText("  File Pdf");

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("zip")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_zip);
                holder.FileNameSender.setText("  File zip");

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_zip);
                holder.FileNameReceiver.setText("  File zip");

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("word")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_word);
                holder.FileNameSender.setText("  Word sheet");

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_word);
                holder.FileNameReceiver.setText("  Word sheet");

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("excel")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_excel);
                holder.FileNameSender.setText("  Excel sheet");

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_excel);
                holder.FileNameReceiver.setText("  Excel sheet");

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("powerpoint")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_powerpoint);
                holder.FileNameSender.setText("  power point sheet");

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });


            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_powerpoint);
                holder.FileNameReceiver.setText("  power point sheet");

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("video")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_viduo);
                holder.FileNameSender.setText(" Video");
                holder.FileNameSender.setTextSize(16f);

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_viduo);
                holder.FileNameReceiver.setText(" Video");
                holder.FileNameReceiver.setTextSize(16f);

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }else if(ChatingMessagesPrivate.getType().equals("audio")){


            if(ChatingMessagesPrivate.getSender().equals(MYID)){

                holder.CardSender.setVisibility(View.VISIBLE);
                holder.RLayoutSender.setVisibility(View.VISIBLE);
                holder.ImageSender.setVisibility(View.VISIBLE);
                holder.SurNameSender.setVisibility(View.VISIBLE);
                holder.DateSender.setVisibility(View.VISIBLE);
                holder.TimeSender.setVisibility(View.VISIBLE);
                holder.RfilsSender.setVisibility(View.VISIBLE);
                holder.PhotofilesSender.setVisibility(View.VISIBLE);
                holder.DownloadingSender.setVisibility(View.VISIBLE);
                holder.FileNameSender.setVisibility(View.VISIBLE);
                holder.textSender.setVisibility(View.VISIBLE);


                holder.TimeSender.setText(ChatingMessagesPrivate.getTime());
                holder.DateSender.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesSender.setImageResource(R.drawable.ic_audio);
                holder.FileNameSender.setText(" Audio");
                holder.FileNameSender.setTextSize(16f);

                holder.DownloadingSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }else {

                holder.CardReceiver.setVisibility(View.VISIBLE);
                holder.RLayoutReceiver.setVisibility(View.VISIBLE);
                holder.ImageReceiver.setVisibility(View.VISIBLE);
                holder.SurNameReceiver.setVisibility(View.VISIBLE);
                holder.DateReceiver.setVisibility(View.VISIBLE);
                holder.TimeReceiver.setVisibility(View.VISIBLE);
                holder.RfilsReceiver.setVisibility(View.VISIBLE);
                holder.PhotofilesReceiver.setVisibility(View.VISIBLE);
                holder.DownloadingReceiver.setVisibility(View.VISIBLE);
                holder.FileNameReceiver.setVisibility(View.VISIBLE);
                holder.textReceiver.setVisibility(View.VISIBLE);

                holder.TimeReceiver.setText(ChatingMessagesPrivate.getTime());
                holder.DateReceiver.setText(ChatingMessagesPrivate.getDate());
                holder.PhotofilesReceiver.setImageResource(R.drawable.ic_audio);
                holder.FileNameReceiver.setText(" Audio");
                holder.FileNameReceiver.setTextSize(16f);

                holder.DownloadingReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Downloading(v.getContext(),ChatingMessagesPrivate);
                    }
                });

            }

        }



    }

    public void file_download(Context context , String uRl, String type, String title) {

        String timeStamp = new SimpleDateFormat("YYYYMMDD_HHMMSS", Locale.getDefault()).format(System.currentTimeMillis());

        File path    = Environment.getExternalStorageDirectory();
        File direct  = new File(path + "/CustUp/");

        if (!direct.exists()) {

            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                .setAllowedOverRoaming(false).setTitle(title)
                .setDescription("")
                .setDestinationInExternalPublicDir("/CustUp/", timeStamp+"." + type);

        mgr.enqueue(request);

    }

    public void Downloading(Context context , final Data_Messages_Groups MessagesPrivate){


        if(MessagesPrivate.getType().equals("pdf")){

            file_download(context,MessagesPrivate.getMessage(),"pdf","file Pdf");

        }if(MessagesPrivate.getType().equals("word")){

            file_download(context,MessagesPrivate.getMessage(),"docx","Word sheet");

        }if(MessagesPrivate.getType().equals("excel")){

            file_download(context,MessagesPrivate.getMessage(),"xlsx","Excel sheet");

        }if(MessagesPrivate.getType().equals("powerpoint")){

            file_download(context,MessagesPrivate.getMessage(),"pptx","power point sheet");

        }if(MessagesPrivate.getType().equals("video")){

            file_download(context,MessagesPrivate.getMessage(),"mp4","file Video");

        }if(MessagesPrivate.getType().equals("audio")){

            file_download(context,MessagesPrivate.getMessage(),"mp3","file Audio");

        }if(MessagesPrivate.getType().equals("zip")){

            file_download(context,MessagesPrivate.getMessage(),"zip","file Zip");
        }
    }

    private void EditMessages(final Context context, final Data_Messages_Groups EditInfo, EditText  Message){

        if (Message.getText().toString().equals("")){

            Toast.makeText(Message.getContext(),R.string.frist_write_ChatingRoom,Toast.LENGTH_SHORT).show();

        }else {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
            String SaveCurrentDate = currentdate.format(calendar.getTime());

            SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
            String SaveCurrentTime = currenttime.format(calendar.getTime());

            Map messagesTextBody = new HashMap();

            messagesTextBody.put("message"  ,"  "+ Message.getText().toString()+"  " );
            messagesTextBody.put("sender"   ,      EditInfo.getSender()              );
            messagesTextBody.put("receiver" ,      EditInfo.getReceiver()            );
            messagesTextBody.put("messageid",      EditInfo.getMessageid()           );
            messagesTextBody.put("type"     ,      "text"                            );
            messagesTextBody.put("time"     ,      SaveCurrentTime                   );
            messagesTextBody.put("date"     ,      SaveCurrentDate                   );
            messagesTextBody.put("edit"     ,      "yes"                             );

            final Map  messageBody = new HashMap();

            messageBody.put(EditInfo.getMessageid() , messagesTextBody);


            UsersRef.child("PrivateMessages").child("Messages").child(EditInfo.getSender()).child(EditInfo.getReceiver()).updateChildren(messageBody)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                                UsersRef.child("PrivateMessages").child("Messages").child(EditInfo.getReceiver()).child(EditInfo.getSender()).updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(context, "Edited successfully", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                            }

                        }
                    });

        }


    }

    private void Deleted_EditSender (final HolderPrivate holderPrivate , final Data_Messages_Groups messagesPrivate , final String MYID , final Context context){

        PopupMenu ChooseMenu = new PopupMenu(context,holderPrivate.DateSender);

                  ChooseMenu.inflate(R.menu.menu_message_private);
                  if(!messagesPrivate.getType().equals("text")){ChooseMenu.getMenu().findItem(R.id.Editprivate).setVisible(false);}
                  ChooseMenu.show();



                  ChooseMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem item) {

                          switch (item.getItemId()){

                              case R.id.Editprivate:

                                  AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                  builder.setTitle("Edit Message:");

                                  final EditText EditMess = new EditText(context);
                                  EditMess .setHint("Edit ");
                                  EditMess.setText(messagesPrivate.getMessage());

                                  builder.setView(EditMess );

                                  builder.setPositiveButton("Done ", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {

                                          String MessageE = EditMess.getText().toString();

                                          if (TextUtils.isEmpty(MessageE)) {

                                              Toast.makeText(context, R.string.frist_write_ChatingRoom, Toast.LENGTH_SHORT).show();

                                          } else {

                                             EditMessages(context, messagesPrivate,EditMess );

                                          }

                                      }
                                  });

                                  builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {

                                          dialog.cancel();
                                      }
                                  });

                                  builder.show();


                                  break;


                              case R.id.DeletedprivateMe:

                                  UsersRef.child("PrivateMessages").child("Messages").child(MYID).child(messagesPrivate.getReceiver())
                                          .child(messagesPrivate.getMessageid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {

                                          if(task.isSuccessful()){

                                              Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });

                                  break;


                              case R.id.DeletedprivateAll:

                                  UsersRef.child("PrivateMessages").child("Messages").child(MYID).child(messagesPrivate.getReceiver())
                                          .child(messagesPrivate.getMessageid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {

                                          if(task.isSuccessful()){

                                              UsersRef.child("PrivateMessages").child("Messages").child(messagesPrivate.getReceiver()).child(MYID)
                                                      .child(messagesPrivate.getMessageid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {

                                                      Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                                  }
                                              });

                                          }
                                      }
                                  });


                                  break;
                          }



                          return false;
                      }
                  });


    }

    private void Deleted_EditReceiver (final HolderPrivate holderPrivate , final Data_Messages_Groups messagesPrivate , final String MYID , final Context context){

        PopupMenu ChooseMenu = new PopupMenu(context,holderPrivate.SurNameReceiver);

                  ChooseMenu.inflate(R.menu.menu_message_private);

                 if(!messagesPrivate.getSender().equals(MYID)) {

                     ChooseMenu.getMenu().findItem(R.id.Editprivate).setVisible(false);
                     ChooseMenu.getMenu().findItem(R.id.DeletedprivateAll).setVisible(false);
                 }

                ChooseMenu.show();



        ChooseMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.DeletedprivateMe:

                        UsersRef.child("PrivateMessages").child("Messages").child(MYID).child(messagesPrivate.getSender())
                                .child(messagesPrivate.getMessageid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                }



                return false;
            }
        });


    }



}
