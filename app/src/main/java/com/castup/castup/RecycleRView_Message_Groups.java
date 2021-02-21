package com.castup.castup;

import android.app.DownloadManager;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecycleRView_Message_Groups extends RecyclerView.Adapter<RecycleRView_Message_Groups.HolderMessagesGroups> {

    ArrayList<Data_Messages_Groups>  messagesGroup ;
    Resources                        resources ;
    DatabaseReference                RootRef ;
    StorageReference                 StorageRef ;
    FirebaseAuth                     mAuth ;



    public RecycleRView_Message_Groups(ArrayList<Data_Messages_Groups> messagesGroup) {
        this.messagesGroup = messagesGroup;


    }

    @NonNull
    @Override
    public HolderMessagesGroups onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View View_Message = LayoutInflater.from(parent.getContext()).inflate(R.layout.custome_messages_groups,parent,false);


        RootRef           = FirebaseDatabase.getInstance().getReference();
        StorageRef        = FirebaseStorage.getInstance().getReference();
        mAuth             = FirebaseAuth.getInstance();
        resources         = parent.getResources();

        return new HolderMessagesGroups(View_Message);
    }


    @Override
    public void onBindViewHolder(@NonNull final HolderMessagesGroups holder, int position) {

        final Data_Messages_Groups  Messages = messagesGroup.get(position);

        final String User        = mAuth.getCurrentUser().getUid();
        final String Sender      = Messages.getSender() ;
        final String MessageID   = Messages.getMessageid();
        final String Type        = Messages.getType();
        String Edit              = Messages.getEdit();


        RootRef.child("Users").child(Sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(150,150).into(holder.ImageUser);
                    holder.SurName.setText(" "+dataSnapshot.child("surname").getValue().toString()+" ");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ShowMessage(holder,Messages,Type);

        Downloading(Type,holder,Messages);


        if(User.equals(Messages.getSender())){

            deletedmessages(holder, Messages);
        }



    }

    @Override
    public int getItemCount() {
        return messagesGroup.size();
    }


    public class HolderMessagesGroups extends RecyclerView.ViewHolder{

        RelativeLayout   Layout , Layout2, RelativePdf ;
        CardView         CardMessagesGroup ;
        CircleImageView  ImageUser ,Downloadingfiles;
        ReadMoreTextView TextMessages ;
        TextView         SurName , Time , Date , NameFiles , text ,Downloadphoto, ViewPhoto;
        ImageView        Photos , Files;


       public HolderMessagesGroups(@NonNull View itemView) {
           super(itemView);

           Layout             = itemView.findViewById(R.id.Layout_Messages_Groups);
           Layout2            = itemView.findViewById(R.id.R_Layout);
           CardMessagesGroup  = itemView.findViewById(R.id.Card_Messages_Group);
           ImageUser          = itemView.findViewById(R.id.ImageMessageGroup);
           TextMessages       = itemView.findViewById(R.id.Text_message_Group);
           SurName            = itemView.findViewById(R.id.Surname_Message_Group);
           Time               = itemView.findViewById(R.id.Time_Massage_Group);
           Date               = itemView.findViewById(R.id.Date_messages_groups);
           Photos             = itemView.findViewById(R.id.photo_messages_group);
           RelativePdf        = itemView.findViewById(R.id.RelativePdf);
           Downloadphoto      = itemView.findViewById(R.id.Download_photo);
           ViewPhoto          = itemView.findViewById(R.id.View_photo);
           Files              = itemView.findViewById(R.id.photo_Pdf);
           text               = itemView.findViewById(R.id.text);
           NameFiles          = itemView.findViewById(R.id.textfilepdf);
           Downloadingfiles   = itemView.findViewById(R.id.Dwonloading_Pdf);



       }


   }

    public void file_download(Context context ,String uRl,String type,String title) {

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

    public void ShowMessage(final HolderMessagesGroups holder, final Data_Messages_Groups Messages, String Type){


        holder.Layout.setVisibility(View.GONE);
        holder.Layout2.setVisibility(View.GONE);
        holder.ImageUser.setVisibility(View.GONE);
        holder.TextMessages.setVisibility(View.GONE);
        holder.CardMessagesGroup.setVisibility(View.GONE);
        holder.SurName.setVisibility(View.GONE);
        holder.Time.setVisibility(View.GONE);
        holder.Date.setVisibility(View.GONE);
        holder.Photos.setVisibility(View.GONE);
        holder.RelativePdf.setVisibility(View.GONE);
        holder.Files.setVisibility(View.GONE);
        holder.text.setVisibility(View.GONE);
        holder.NameFiles.setVisibility(View.GONE);
        holder.Downloadingfiles.setVisibility(View.GONE);
        holder.ViewPhoto.setVisibility(View.GONE);
        holder.Downloadphoto.setVisibility(View.GONE);



        if(Type.equals("text")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.TextMessages.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.TextMessages.setTextColor(Color.BLACK);
            holder.TextMessages.setText(Messages.getMessage());
            holder.Date.setText(Messages.getDate());


        }if (Type.equals("photo")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.Downloadphoto.setVisibility(View.VISIBLE);
            holder.ViewPhoto.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Photos.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);



            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            Picasso.get().load(Messages.getMessage()).resize(400,400).into(holder.Photos);

            holder.Downloadphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    file_download(v.getContext(),Messages.getMessage(),"jpg","Photo");

                }
            });

            holder.ViewPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {


                    Intent Photos = new Intent(v.getContext(), ViewPhotos.class);

                    Photos.putExtra("photonew",Messages.getMessage());
                    Photos.putExtra("Date",    Messages.getDate());
                    Photos.putExtra("Time",    Messages.getTime());
                    Photos.putExtra("Sender",  Messages.getSender());
                    v.getContext().startActivity(Photos);
                }
            });

        }if(Type.equals("pdf")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_chat_pdf);
            holder.NameFiles.setText("File Pdf");

        }if(Type.equals("zip")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_zip);
            holder.NameFiles.setText("File Zip");

        }if(Type.equals("word")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_word);
            holder.NameFiles.setText("Word sheet");

        }if(Type.equals("excel")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_excel);
            holder.NameFiles.setText("Excel sheet");

        }if(Type.equals("powerpoint")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_powerpoint);
            holder.NameFiles.setText("power point sheet");

        }if(Type.equals("video")){

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_viduo);
            holder.NameFiles.setText(" Video");
            holder.NameFiles.setTextSize(16f);

        }if(Type.equals("audio")) {

            holder.Layout.setVisibility(View.VISIBLE);
            holder.Layout2.setVisibility(View.VISIBLE);
            holder.CardMessagesGroup.setVisibility(View.VISIBLE);
            holder.ImageUser.setVisibility(View.VISIBLE);
            holder.SurName.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.VISIBLE);
            holder.Date.setVisibility(View.VISIBLE);
            holder.RelativePdf.setVisibility(View.VISIBLE);
            holder.Files.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.NameFiles.setVisibility(View.VISIBLE);
            holder.Downloadingfiles.setVisibility(View.VISIBLE);


            holder.Time.setText(Messages.getTime());
            holder.Date.setText(Messages.getDate());
            holder.Files.setImageResource(R.drawable.ic_audio);
            holder.NameFiles.setText(" Audio");
            holder.NameFiles.setTextSize(16f);

        }


    }

    public void deletedmessages(final HolderMessagesGroups messagesGroups , final Data_Messages_Groups Idmessage ){

        messagesGroups.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu deletedMenu = new PopupMenu(v.getContext(),messagesGroups.CardMessagesGroup);
                deletedMenu.inflate(R.menu.messagesmenu);
                deletedMenu.show();
                deletedMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.deletedMessages :

                                RootRef.child("PublicGroups").child(Idmessage.getNamegroup()).child("Messages").child(Idmessage.getMessageid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(v.getContext(),resources.getString(R.string.deletedToast_ChatingRoom),Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });

                                break;

                        }
                        return false;
                    }
                });
            }
        });


    }

    public void Downloading(final String Type , HolderMessagesGroups holder , final Data_Messages_Groups Messages){

        holder.Downloadingfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Type.equals("pdf")){

                    file_download(v.getContext(),Messages.getMessage(),"pdf","file Pdf");

                }if(Type.equals("word")){

                    file_download(v.getContext(),Messages.getMessage(),"docx","Word sheet");

                }if(Type.equals("excel")){

                    file_download(v.getContext(),Messages.getMessage(),"xlsx","Excel sheet");

                }if(Type.equals("powerpoint")){

                    file_download(v.getContext(),Messages.getMessage(),"pptx","power point sheet");

                }if(Type.equals("video")){

                    file_download(v.getContext(),Messages.getMessage(),"mp4","file Video");

                }if(Type.equals("audio")){

                    file_download(v.getContext(),Messages.getMessage(),"mp3","file Audio");

                }if(Type.equals("zip")){

                    file_download(v.getContext(),Messages.getMessage(),"zip","file Zip");
                }
            }
        });


    }


}


