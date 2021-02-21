package com.castup.castup;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View ChatingFragment ;

    private TextView Mystate ;
    private EditText  MyStatus ;
    private CircleImageView ProfileImage , StateImage ;
    private CardView  cardView ;
    private RecyclerView Recycler_Chating ;

    private FirebaseAuth mAuth ;
    private DatabaseReference UserRef ;
    private FirebaseRecyclerOptions<Data_Messages_Groups> options ;
    private FirebaseRecyclerAdapter<Data_Messages_Groups,HolderFriends> adapter ;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ChatingFragment = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth        = FirebaseAuth.getInstance();
        UserRef      = FirebaseDatabase.getInstance().getReference();


        Veiw_Tools();
        ShowUserInfo_ChatingFragment();
        ChangeStatus();
        ListFriends();

        return ChatingFragment ;
    }

    private void Veiw_Tools(){

          MyStatus         = ChatingFragment.findViewById(R.id.Status_Chating_Fragment);
          Mystate          = ChatingFragment.findViewById(R.id.State_Chating_Fragment);
          ProfileImage     = ChatingFragment.findViewById(R.id.ImageProfile_ChatingFragment);
          StateImage       = ChatingFragment.findViewById(R.id.Image_State_Chating_Fragment);
          cardView         = ChatingFragment.findViewById(R.id.Card_Veiw_Chating);
          Recycler_Chating = ChatingFragment.findViewById(R.id.Recycle_Chating_Fragment);

          MyStatus.setTextIsSelectable(true);
          MyStatus.setInputType(0);
    }

    private void CloseKeyboard(){

        View view = getActivity().getCurrentFocus();
        if(view != null){

            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);

        }

    } // no work now
    private void OpenKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {

            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        }

    }

    private void ShowUserInfo_ChatingFragment() {

        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("profileimage")){


                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(300,300).into(ProfileImage);
                    ProfileImage.setBorderColor(Color.rgb(0,184,212));
                    MyStatus.setText(dataSnapshot.child("status").getValue().toString());


                }else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserRef.child("StatsUsers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {


                    if (dataSnapshot.child("state").getValue().toString().equals("Online")) {

                        StateImage.setBorderColor(Color.GREEN);
                        StateImage.setColorFilter(Color.GREEN);
                        Mystate.setText(dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.rgb(98, 199, 34));

                    }
                    if (dataSnapshot.child("state").getValue().toString().equals("Busy")) {

                        StateImage.setBorderColor(Color.RED);
                        StateImage.setColorFilter(Color.RED);
                        Mystate.setText(dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.RED);

                    }
                    if (dataSnapshot.child("state").getValue().toString().equals("In a meeting")) {

                        StateImage.setBorderColor(Color.RED);
                        StateImage.setColorFilter(Color.RED);
                        Mystate.setText(dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.RED);

                    }
                    if (dataSnapshot.child("state").getValue().toString().equals("At work")) {

                        StateImage.setBorderColor(Color.BLUE);
                        StateImage.setColorFilter(Color.BLUE);
                        Mystate.setText(dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.BLUE);

                    }
                    if (dataSnapshot.child("state").getValue().toString().equals("At School")) {

                        StateImage.setBorderColor(Color.BLUE);
                        StateImage.setColorFilter(Color.BLUE);
                        Mystate.setText(dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.BLUE);

                    }
                    if (dataSnapshot.child("state").getValue().toString().equals("Offline")) {

                        StateImage.setBorderColor(Color.GRAY);
                        StateImage.setColorFilter(Color.GRAY);
                        Mystate.setText("Appear " + dataSnapshot.child("state").getValue().toString());
                        Mystate.setTextColor(Color.GRAY);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ChangeStatus(){

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MyStatus.isEnabled()){

                    MyStatus.setEnabled(true);
                    MyStatus.setInputType(1);
                    MyStatus.setText("");
                    MyStatus.setHint("Change Status");
                    MyStatus.setTextColor(Color.RED);
                    OpenKeyboard();

                }else {

                    if(MyStatus.getText().toString().length()> 50) {

                        MyStatus.setError("50 characters only");
                        MyStatus.requestFocus();
                        return;

                    }if (TextUtils.isEmpty(MyStatus.getText().toString())){

                        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild("status")){

                                    MyStatus.setText(dataSnapshot.child("status").getValue().toString());
                                    MyStatus.setTextColor(Color.rgb(100,98,98));
                                    MyStatus.setEnabled(false);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else {

                        UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("status")
                               .setValue(MyStatus.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    UserRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild("status")){

                                                MyStatus.setText(dataSnapshot.child("status").getValue().toString());
                                                MyStatus.setTextColor(Color.rgb(100,98,98));
                                                MyStatus.setEnabled(false);


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
            }
        });
    }

    private void ListFriends(){

        options = new FirebaseRecyclerOptions.Builder<Data_Messages_Groups>()
                  .setQuery(UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()),Data_Messages_Groups.class)
                  .build();

        adapter = new FirebaseRecyclerAdapter<Data_Messages_Groups, HolderFriends>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderFriends holderFriends, int position, @NonNull Data_Messages_Groups messagesGroups) {

                      holderFriends.ContantsMenu.setVisibility(View.GONE);
                final String  IDRef = getRef(position).getKey();

                UserRef.child("Users").child(IDRef).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            showDataforfriends(dataSnapshot,holderFriends,IDRef);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holderFriends.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent Chating = new Intent(v.getContext(), Private_Chat.class);
                               Chating.putExtra("IDFRIEND",IDRef);
                               startActivity(Chating);
                    }
                });

                holderFriends.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                            builder.setIcon(R.drawable.ic_deleted);
                                            builder.setTitle("Delete a friend");
                                            builder.setMessage("Delete a friend from the list");
                                            builder.setPositiveButton("Deleted", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(IDRef).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){

                                                                        Snackbar.make(Recycler_Chating,"Deleted",Snackbar.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });

                                                }
                                            });

                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                            builder.show();


                        return false;
                    }
                });


            }

            @NonNull
            @Override
            public HolderFriends onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View friends = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_contants_fragment,parent,false);

                return new HolderFriends(friends) ;
            }
        };

        Recycler_Chating.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        adapter.startListening();
        Recycler_Chating.setAdapter(adapter);

    }

    public static class HolderFriends extends RecyclerView.ViewHolder{

        TextView FullName , Status , State ;
        CircleImageView ProfileImage , StateImage ;
        ImageView ContantsMenu ;

        public HolderFriends(@NonNull View itemView) {
            super(itemView);

            FullName        = itemView.findViewById(R.id.FullName_ContantsFragment);
            Status          = itemView.findViewById(R.id.Status_ContantsFragment);
            State           = itemView.findViewById(R.id.State_ContantsFragment);
            ProfileImage    = itemView.findViewById(R.id.ProfileImage_Contants_Fragment);
            StateImage      = itemView.findViewById(R.id.Image_State_ContantsFragment);
            ContantsMenu    = itemView.findViewById(R.id.ContantsMenu);

        }
    }

    private void showDataforfriends(DataSnapshot dataSnapshot , final HolderFriends holderContants, String Friend){

        if(dataSnapshot.exists() && dataSnapshot.hasChild("fullname")){

            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(300,300).into(holderContants.ProfileImage);
            holderContants.FullName.setText(dataSnapshot.child("fullname").getValue().toString());
            holderContants.Status.setText(dataSnapshot.child("status").getValue().toString());

            UserRef.child("StatsUsers").child(Friend).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        if(dataSnapshot.child("state").getValue().toString().equals("Online")){

                            holderContants.StateImage.setBorderColor(Color.GREEN);
                            holderContants.StateImage.setColorFilter(Color.GREEN);
                            holderContants.State.setText(dataSnapshot.child("state").getValue().toString());
                            holderContants.State.setTextColor(Color.rgb(98,199,34));

                        }if(dataSnapshot.child("state").getValue().toString().equals("Busy")){

                            holderContants.StateImage.setBorderColor(Color.RED);
                            holderContants.StateImage.setColorFilter(Color.RED);
                            holderContants.State.setText(dataSnapshot.child("state").getValue().toString());
                            holderContants.State.setTextColor(Color.RED);

                        }if(dataSnapshot.child("state").getValue().toString().equals("In a meeting")){

                            holderContants.StateImage.setBorderColor(Color.RED);
                            holderContants.StateImage.setColorFilter(Color.RED);
                            holderContants.State.setText(dataSnapshot.child("state").getValue().toString());
                            holderContants.State.setTextColor(Color.RED);

                        }if(dataSnapshot.child("state").getValue().toString().equals("At work")){

                            holderContants.StateImage.setBorderColor(Color.BLUE);
                            holderContants.StateImage.setColorFilter(Color.BLUE);
                            holderContants.State.setText(dataSnapshot.child("state").getValue().toString());
                            holderContants.State.setTextColor(Color.BLUE);

                        }if(dataSnapshot.child("state").getValue().toString().equals("At School")){

                            holderContants.StateImage.setBorderColor(Color.BLUE);
                            holderContants.StateImage.setColorFilter(Color.BLUE);
                            holderContants.State.setText(dataSnapshot.child("state").getValue().toString());
                            holderContants.State.setTextColor(Color.BLUE);

                        }if(dataSnapshot.child("state").getValue().toString().equals("Offline")){

                            holderContants.StateImage.setBorderColor(Color.GRAY);
                            holderContants.StateImage.setColorFilter(Color.GRAY);
                            holderContants.State.setText("last seen " + dataSnapshot.child("date").getValue().toString()+ " at "+ dataSnapshot.child("time").getValue().toString());
                            holderContants.State.setTextColor(Color.GRAY);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }





}
