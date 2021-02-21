package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SentRequests extends AppCompatActivity {

    private RecyclerView RecycleSent_Requests ;
    private Toolbar      ToolBarSent_Requests ;
    private Resources    resources ;
    private AdView       mAdView;

    private FirebaseAuth       mAuth  ;
    private DatabaseReference  UserRef ;
    private FirebaseRecyclerOptions<Data_Firebase> options ;
    private FirebaseRecyclerAdapter<Data_Firebase, HolderSentRequests> adapter ;
    private String MYID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_requests);

        mAuth       = FirebaseAuth.getInstance();
        UserRef     = FirebaseDatabase.getInstance().getReference();
        MYID        = mAuth.getCurrentUser().getUid();

        View_Tools();
        ActionBarSent_Requests();
        ADMOB();
        RecycleReViewSentRequests();

    }

    private void View_Tools(){

        RecycleSent_Requests   = findViewById(R.id.Recycle_SentRequests);
        ToolBarSent_Requests   = findViewById(R.id.Main_Action_Bar_SentRequests);
        resources              = getResources();
    }

    private void ActionBarSent_Requests(){

        setSupportActionBar(ToolBarSent_Requests);
        getSupportActionBar().setTitle(resources.getString(R.string.Request_Text_RequstesActivity));
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

    private void RecycleReViewSentRequests(){

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                .setQuery(UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()),Data_Firebase.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderSentRequests>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderSentRequests holderSentRequests, int position, @NonNull final Data_Firebase data_firebase) {

                holderSentRequests.Rel_Layout.setVisibility(View.GONE);

                    final String               UserId = getRef(position).getKey();
                    final DatabaseReference reference = getRef(position).child("request_type").getRef();

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){

                                String Type = dataSnapshot.getValue().toString();

                                if(Type.equals("sent")){

                                    UserRef.child("Users").child(UserId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()){

                                                holderSentRequests.Rel_Layout.setVisibility(View.VISIBLE);
                                                Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(holderSentRequests.ProfileImage);
                                                holderSentRequests.Surname.setText(dataSnapshot.child("surname").getValue().toString());
                                                holderSentRequests.Country.setText(dataSnapshot.child("country").getValue().toString());

                                                UserRef.child("ChatRequests").child(MYID).child(UserId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if(dataSnapshot.exists()){


                                                            holderSentRequests.DateSentRequste.setText(dataSnapshot.child("date").getValue().toString());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                holderSentRequests.itemView.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends2));
                                                Cancel_Button(holderSentRequests,UserId);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



            }

            @NonNull
            @Override
            public HolderSentRequests onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View  View_SentRequests = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_sentrequests,parent,false);

                return new HolderSentRequests(View_SentRequests);
            }
        };

        RecycleSent_Requests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        RecycleSent_Requests.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    private void Search_FriendRequest(String SurName) {

        final Query SearchFriends = UserRef.child("Users").orderByChild("surname").startAt(SurName.trim().toUpperCase()).endAt(SurName.trim().toUpperCase()+"\uf0ff");

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                .setQuery(SearchFriends, Data_Firebase.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderSentRequests>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderSentRequests holderSentRequests, int position, @NonNull final Data_Firebase data_firebase) {

                holderSentRequests.Rel_Layout.setVisibility(View.GONE);

                final String UsersID = getRef(position).getKey();

                UserRef.child("Users").child(UsersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            final String ID        = dataSnapshot.getRef().getKey();

                            UserRef.child("ChatRequests").child(MYID).child(ID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){

                                        final String Type = dataSnapshot.child("request_type").getValue().toString();

                                        if(Type.equals("sent")){

                                            holderSentRequests.DateSentRequste.setText(dataSnapshot.child("date").getValue().toString());

                                            UserRef.child("Users").child(ID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.exists()){

                                                        holderSentRequests.Rel_Layout.setVisibility(View.VISIBLE);

                                                        Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(holderSentRequests.ProfileImage);
                                                        holderSentRequests.Surname.setText(dataSnapshot.child("surname").getValue().toString());
                                                        holderSentRequests.Country.setText(dataSnapshot.child("country").getValue().toString());

                                                        holderSentRequests.itemView.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends2));
                                                        Cancel_Button(holderSentRequests,ID);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
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


            }

            @NonNull
            @Override
            public HolderSentRequests onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View  View_SentRequests = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_sentrequests,parent,false);

                return new HolderSentRequests(View_SentRequests);
            }
        };

        RecycleSent_Requests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        RecycleSent_Requests.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.search_friends, menu);

        SearchView view_friends = (SearchView) menu.findItem(R.id.Search_Friends).getActionView();

        view_friends.setSubmitButtonEnabled(true);
        view_friends.setQueryHint("Surname");

        view_friends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Search_FriendRequest(newText);
                return false;
            }
        });

        view_friends.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                RecycleReViewSentRequests();
                return false;
            }
        });

        return true;
    }

    private static class HolderSentRequests extends RecyclerView.ViewHolder{

            CircleImageView ProfileImage ;
            TextView        Surname , Country , DateSentRequste ;
            Button          CancelRequests ;
            RelativeLayout  Rel_Layout ;

        public HolderSentRequests(@NonNull View itemView) {
            super(itemView);

            ProfileImage    = itemView.findViewById(R.id.ProfileImage_SentRequstes);
            Surname         = itemView.findViewById(R.id.SurName_SentRequstes);
            Country         = itemView.findViewById(R.id.Country_SentRequest);
            DateSentRequste = itemView.findViewById(R.id.Date_SentRequstes);
            CancelRequests  = itemView.findViewById(R.id.CanceltRequest_SentRequstes);
            Rel_Layout      = itemView.findViewById(R.id.Rel_SentRequests);

        }
    }

    private void Cancel_Button(final HolderSentRequests holderSentRequests , final String UserID){

        holderSentRequests.CancelRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()).child(UserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            UserRef.child("ChatRequests").child(UserID).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        holderSentRequests.Rel_Layout.setVisibility(View.GONE);
                                        Snackbar.make(RecycleSent_Requests,resources.getString(R.string.Successfullycanceled_Requests),Snackbar.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });
            }
        });


        //=================================

        holderSentRequests.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent UserProfilePublic = new Intent(SentRequests.this,Users_Profile.class);
                       UserProfilePublic.putExtra("ID",UserID);
                       UserProfilePublic.putExtra("UserID",mAuth.getCurrentUser().getUid());
                       UserProfilePublic.putExtra("privacy","public");
                       startActivity(UserProfilePublic);
            }
        });

    }

}
