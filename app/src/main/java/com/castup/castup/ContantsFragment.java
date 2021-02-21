package com.castup.castup;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContantsFragment extends Fragment {

   private View ContantsFragment ;
   private Resources resources ;
   private AdView mAdView;
   private FirebaseRecyclerOptions<Data_Firebase> options ;
   private FirebaseRecyclerAdapter<Data_Firebase,HolderContants> adapter ;
   private RecyclerView  RecycleContants ;

   private FirebaseAuth mAuth ;
   private DatabaseReference UserRef ;

    public ContantsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContantsFragment =  inflater.inflate(R.layout.fragment_contants, container, false);

        mAuth        = FirebaseAuth.getInstance();
        UserRef      = FirebaseDatabase.getInstance().getReference();
        resources    = getResources();

        View_Tools();
        ADMOB();
        RecycleFirebace();


        return ContantsFragment ;
    }

    private void View_Tools(){

       RecycleContants = ContantsFragment.findViewById(R.id.Recycle_Contants);

    }

    public void ADMOB (){

        AdView adView = new AdView(ContantsFragment.getContext());
        adView.setAdSize(AdSize.BANNER);adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = ContantsFragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(ContantsFragment.getContext());
        adView.setAdSize(AdSize.BANNER);

    }

    private void RecycleFirebace(){

         options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                   .setQuery(UserRef.child("Contacts").child(mAuth.getCurrentUser().getUid()),Data_Firebase.class)
                   .build();

        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderContants>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderContants holderContants, int position, @NonNull final Data_Firebase data_firebase) {

                final String FriendID = getRef(position).getKey();

                UserRef.child("Users").child(FriendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        showDataforfriends(dataSnapshot,holderContants,FriendID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holderContants.ContantsMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MenuContants(holderContants,FriendID);
                    }
                });

                holderContants.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(FriendID).child("Attendees")
                                .setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Intent Chating  = new Intent("android.intent.action.SEND");
                                    Chating.putExtra("IDFRIEND", FriendID);
                                    startActivity(Chating);
                                }

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public HolderContants onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View Contants = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_contants_fragment,parent,false);

                return new HolderContants(Contants);
            }
        };

        RecycleContants.setLayoutManager(new LinearLayoutManager(getContext()));
        RecycleContants.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public static class HolderContants extends RecyclerView.ViewHolder{

         TextView FullName , Status , State ;
         CircleImageView ProfileImage , StateImage ;
         ImageView ContantsMenu ;

        public HolderContants(@NonNull View itemView) {
            super(itemView);

            FullName        = itemView.findViewById(R.id.FullName_ContantsFragment);
            Status          = itemView.findViewById(R.id.Status_ContantsFragment);
            State           = itemView.findViewById(R.id.State_ContantsFragment);
            ProfileImage    = itemView.findViewById(R.id.ProfileImage_Contants_Fragment);
            StateImage      = itemView.findViewById(R.id.Image_State_ContantsFragment);
            ContantsMenu    = itemView.findViewById(R.id.ContantsMenu);
        }
    }

    private void BlockUser(final String ID){

        UserRef.child("BlockUsers").child(mAuth.getCurrentUser().getUid()).child(ID).child("block").setValue("sentblock").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    UserRef.child("BlockUsers").child(ID).child(mAuth.getCurrentUser().getUid()).child("block").setValue("receivedblock").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Snackbar.make(ContantsFragment,resources.getString(R.string.Userhasbeenbanned_menuReport),Snackbar.LENGTH_SHORT).show();

                                UserRef.child("Contacts").child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            UserRef.child("Contacts").child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()).child(ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){

                                                                    UserRef.child("ChatRequests").child(ID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void Dialog_Blocked(final String ID){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(resources.getString(R.string.BlockUsers_UserProfile));
        builder.setIcon(R.drawable.ic_blockuser);
        builder.setMessage(resources.getString(R.string.Incase_UserProfile));
        builder.setPositiveButton(resources.getString(R.string.block_UserProfile), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BlockUser(ID);
            }
        });
        builder.setNegativeButton(resources.getString(R.string.Cancel_UserProfile), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void MenuContants(HolderContants holderContants, final String Friend){

        final PopupMenu popupmenu = new PopupMenu(getContext(),holderContants.ContantsMenu);
        popupmenu.inflate(R.menu.menu_contants);
        popupmenu.show();

        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){


                    case R.id.Chating :

                        UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(Friend).child("Attendees")
                               .setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Intent Chating  = new Intent("android.intent.action.SEND");
                                           Chating.putExtra("IDFRIEND", Friend);
                                           startActivity(Chating);
                                }

                            }
                        });


                        break;

                    case R.id.Profile:

                        Intent ProfileMenu  = new Intent(getContext(),Users_Profile.class);
                               ProfileMenu.putExtra("privacy","Contants");
                               ProfileMenu.putExtra("ID",Friend);
                               ProfileMenu.putExtra("UserID",mAuth.getCurrentUser().getUid());
                               startActivity(ProfileMenu);

                        break;

                    case R.id.Remove:

                        UserRef.child("Contacts").child(mAuth.getCurrentUser().getUid()).child(Friend)
                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    UserRef.child("Contacts").child(Friend).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                UserRef.child("ChatingPage").child(mAuth.getCurrentUser().getUid()).child(Friend).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                            Snackbar.make(ContantsFragment,R.string.Delete_successfully_Requests,Snackbar.LENGTH_SHORT).show();

                                                            UserRef.child("ChatingPage").child(Friend).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                        break;

                    case R.id.Block:

                        Dialog_Blocked(Friend);
                        break;

                }

                return false;
            }
        });


    }

    private void showDataforfriends(DataSnapshot dataSnapshot , final HolderContants holderContants, String Friend){

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
