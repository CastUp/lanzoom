package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import de.hdodenhof.circleimageview.CircleImageView;

public class Requests extends AppCompatActivity {

    private Toolbar toolbarRequest;
    private RecyclerView recyclerRequests;
    private Resources resources;
    private AdView mAdView;
    private FirebaseRecyclerOptions<Data_Firebase> options;
    private FirebaseRecyclerAdapter<Data_Firebase, HolderRequest> adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private String MYID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth   = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();
        MYID    = mAuth.getCurrentUser().getUid();
        Veiw_Tools();
        ActionBar();
        ADMOB();
        RecycleRequests();
    }

    private void Veiw_Tools() {

        toolbarRequest = findViewById(R.id.Main_Action_Bar_Requests);
        recyclerRequests = findViewById(R.id.Recycle_Requests);
        resources = getResources();
    }

    private void ActionBar() {

        setSupportActionBar(toolbarRequest);
        getSupportActionBar().setTitle(resources.getString(R.string.Request_Text_RequstesActivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void ADMOB() {

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

    }

    private void RecycleRequests() {

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                .setQuery(UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()), Data_Firebase.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderRequest>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderRequest holderRequest, int position, @NonNull final Data_Firebase data_firebase) {

                holderRequest.Rel_Requests.setVisibility(View.GONE);

                final String UsersID = getRef(position).getKey();
                final DatabaseReference RequestType = getRef(position).child("request_type").getRef();

                RequestType.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String Type = dataSnapshot.getValue().toString();

                            if (Type.equals("received")) {

                                UserRef.child("Users").child(UsersID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            holderRequest.Rel_Requests.setVisibility(View.VISIBLE);
                                            holderRequest.AcceptButton.setText(resources.getString(R.string.AcceptRequest_userprofile));

                                            holderRequest.SurName.setText(dataSnapshot.child("surname").getValue().toString());
                                            holderRequest.Country.setText(dataSnapshot.child("country").getValue().toString());
                                            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(holderRequest.ProfileImage);

                                            UserRef.child("ChatRequests").child(MYID).child(UsersID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.exists()){

                                                        holderRequest.DateRequstes.setText(dataSnapshot.child("date").getValue().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            holderRequest.itemView.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_find_friends2));
                                            Buttons(holderRequest, UsersID);
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
            public HolderRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View View_Request = LayoutInflater.from(parent.getContext()).inflate(R.layout.costomrequstes, parent, false);

                return new HolderRequest(View_Request);
            }
        };

        recyclerRequests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerRequests.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


    }

    private void Search_FriendRequest(String SurName) {

        final Query SearchFriends = UserRef.child("Users").orderByChild("surname").startAt(SurName.trim().toUpperCase()).endAt(SurName.trim().toUpperCase()+"\uf0ff");

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                .setQuery(SearchFriends, Data_Firebase.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderRequest>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderRequest holderRequest, int position, @NonNull final Data_Firebase data_firebase) {

                  holderRequest.Rel_Requests.setVisibility(View.GONE);

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

                                        if(Type.equals("received")){

                                            holderRequest.DateRequstes.setText(dataSnapshot.child("date").getValue().toString());
                                            UserRef.child("Users").child(ID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.exists()){

                                                        holderRequest.Rel_Requests.setVisibility(View.VISIBLE);
                                                        holderRequest.CancelButton.setVisibility(View.VISIBLE);
                                                        holderRequest.AcceptButton.setVisibility(View.VISIBLE);
                                                        holderRequest.AcceptButton.setText(resources.getString(R.string.AcceptRequest_userprofile));

                                                        holderRequest.SurName.setText(dataSnapshot.child("surname").getValue().toString());
                                                        holderRequest.Country.setText(dataSnapshot.child("country").getValue().toString());
                                                        Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(holderRequest.ProfileImage);


                                                        holderRequest.itemView.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_find_friends2));
                                                        Buttons(holderRequest,ID);

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
            public HolderRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View View_Request = LayoutInflater.from(parent.getContext()).inflate(R.layout.costomrequstes, parent, false);

                return new HolderRequest(View_Request);
            }
        };

        recyclerRequests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerRequests.setAdapter(adapter);
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
                RecycleRequests();
                return false;
            }
        });

        return true;
    }

    public static class HolderRequest extends RecyclerView.ViewHolder{

        CircleImageView ProfileImage ;
        TextView   SurName , Country , DateRequstes;
        Button     AcceptButton , CancelButton ;
        RelativeLayout Rel_Requests ;

        public HolderRequest(@NonNull View itemView) {
            super(itemView);

             ProfileImage    = itemView.findViewById(R.id.ProfileImage_Requstes);
             SurName         = itemView.findViewById(R.id.SurName_Requstes);
             Country         = itemView.findViewById(R.id.Country_Request);
             DateRequstes    = itemView.findViewById(R.id.Date_Requstes);
             AcceptButton    = itemView.findViewById(R.id.RequestButton_Requstes);
             CancelButton    = itemView.findViewById(R.id.CanceltRequest_Requstes);
             Rel_Requests    = itemView.findViewById(R.id.Rel_Requests);
        }
    }

    private void Buttons (final HolderRequest holderRequest , final String Users ){

        holderRequest.AcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRef.child("Contacts").child(mAuth.getCurrentUser().getUid()).child(Users).child("contact").setValue("save").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            UserRef.child("Contacts").child(Users).child(mAuth.getCurrentUser().getUid()).child("contact").setValue("save").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()).child(Users).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    UserRef.child("ChatRequests").child(Users).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){

                                                                Toast.makeText(getBaseContext(),resources.getString(R.string.Added_successfully_Requests),Toast.LENGTH_SHORT).show();
                                                                holderRequest.Rel_Requests.setVisibility(View.GONE);
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
        });

        //======================================================================================================================================

        holderRequest.CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRef.child("ChatRequests").child(mAuth.getCurrentUser().getUid()).child(Users).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            UserRef.child("ChatRequests").child(Users).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(getBaseContext(),resources.getString(R.string.Successfullycanceled_Requests),Toast.LENGTH_SHORT).show();
                                        holderRequest.Rel_Requests.setVisibility(View.GONE);
                                    }

                                }
                            });
                        }
                    }
                });
            }
        });

        //=======================================================================================================================================

        holderRequest.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent RequestsINT = new Intent(Requests.this,Users_Profile.class);
                       RequestsINT.putExtra("ID",Users);
                       RequestsINT.putExtra("UserID",mAuth.getCurrentUser().getUid());
                       RequestsINT.putExtra("privacy","public");
                       startActivity(RequestsINT);
            }
        });
    }


}
