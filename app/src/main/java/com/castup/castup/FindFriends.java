package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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

public class FindFriends extends AppCompatActivity {

    private Toolbar ActionBarFindFriends ;
    private ProgressDialog loadContacts ;
    private RecyclerView  Recycle_Users ;
    private AdView mAdView;

    private DatabaseReference UserRef ;
    private FirebaseAuth mAuth ;
    private FirebaseRecyclerAdapter  adapter ;
    private FirebaseRecyclerOptions<Data_Firebase> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UserRef = FirebaseDatabase.getInstance().getReference();
        mAuth   = FirebaseAuth.getInstance();

        Veiw_Tools();
        Actionbar_Frinds();
        ADMOB();
        RecycleReview_Users();

    }

    private void Veiw_Tools(){

        ActionBarFindFriends = findViewById(R.id.Actionbar_findfriends);
        Recycle_Users        = findViewById(R.id.Recycle_Users);
        loadContacts = new ProgressDialog(this);

    }
    private void Actionbar_Frinds(){

        setSupportActionBar(ActionBarFindFriends);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    private void RecycleReview_Users(){

        loadContacts.setTitle("Find Friends");
        loadContacts.setMessage("Please wait");
        loadContacts.setCanceledOnTouchOutside(false);
        loadContacts.show();

         options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                                               .setQuery(UserRef.child("Users"),Data_Firebase.class)
                                               .build();


          adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderUsers>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderUsers holderUsers, int position, @NonNull final Data_Firebase data_firebase) {

                String UsersID = getRef(position).getKey();

                UserRef.child("Users").child(UsersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() && dataSnapshot.hasChild("fullname") && dataSnapshot.hasChild("coverimage") && dataSnapshot.hasChild("profileimage")){


                            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(500,500).into(holderUsers.ProfileImage);
                            holderUsers.UserName.setText(dataSnapshot.child("surname").getValue().toString());
                            holderUsers.UserStatus.setText(dataSnapshot.child("status").getValue().toString());
                            holderUsers.State.setText(dataSnapshot.child("country").getValue().toString());

                            holderUsers.ProfileImage.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends));
                            holderUsers.CardView_CustomlistUsers.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends2));
                            loadContacts.dismiss();

                        }else {

                            holderUsers.RelativeAll.setVisibility(View.GONE);
                            holderUsers.CardView_CustomlistUsers.setVisibility(View.GONE);
                            holderUsers.ProfileImage.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {



                    }
                });

                OnCleckFriend(holderUsers,UsersID);
            }

            @NonNull
            @Override
            public HolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View ListUsers = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_users,parent,false);

                return new HolderUsers(ListUsers);
            }
        };

        Recycle_Users.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        Recycle_Users.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    private void Search_Friend(String Phone){

        final Query SearchFriends = UserRef.child("Users").orderByChild("phone").equalTo(Phone.trim());

        options = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                  .setQuery(SearchFriends,Data_Firebase.class)
                  .build();

        adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderUsers>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final HolderUsers holderUsers, int position , @NonNull final Data_Firebase data_firebase) {

                String UserID = getRef(position).getKey();

                UserRef.child("Users").child(UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()&& dataSnapshot.hasChild("fullname")&& dataSnapshot.hasChild("coverimage") && dataSnapshot.hasChild("profileimage")){


                            Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).resize(500,500).into(holderUsers.ProfileImage);
                            holderUsers.UserName.setText(dataSnapshot.child("surname").getValue().toString());
                            holderUsers.UserStatus.setText(dataSnapshot.child("status").getValue().toString());
                            holderUsers.State.setText(dataSnapshot.child("country").getValue().toString());

                            holderUsers.ProfileImage.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends));
                            holderUsers.CardView_CustomlistUsers.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animation_find_friends2));
                            loadContacts.dismiss();

                        }else {

                            holderUsers.RelativeAll.setVisibility(View.GONE);
                            holderUsers.CardView_CustomlistUsers.setVisibility(View.GONE);
                            holderUsers.ProfileImage.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                OnCleckFriend(holderUsers,UserID);

            }

            @NonNull
            @Override
            public HolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View list_frinds = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_users,parent,false);

                return new HolderUsers(list_frinds);
            }
        };

        Recycle_Users.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        Recycle_Users.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public static class HolderUsers extends RecyclerView.ViewHolder {

        CircleImageView  ProfileImage , StateImage ;
        TextView         UserName , UserStatus , State ;
        RelativeLayout   CardView_CustomlistUsers , RelativeAll ;

        public HolderUsers(@NonNull View itemView) {
            super(itemView);

            ProfileImage              = itemView.findViewById(R.id.ProfileImage_Custom_List_Users);
            StateImage                = itemView.findViewById(R.id.Image_State_list_users);
            State                     = itemView.findViewById(R.id.State_Custom_list_Users);
            UserName                  = itemView.findViewById(R.id.UserName_Custom_list_Users);
            UserStatus                = itemView.findViewById(R.id.Status_Custom_list_Users);
            CardView_CustomlistUsers  = itemView.findViewById(R.id.Relative_Layout_list_Users);
            RelativeAll               = itemView.findViewById(R.id.REL_F);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.search_friends,menu);

        SearchView  view_friends = (SearchView) menu.findItem(R.id.Search_Friends).getActionView();

                    view_friends.setSubmitButtonEnabled(true);
                    view_friends.setQueryHint("phone number");

        view_friends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search_Friend(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        view_friends.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                RecycleReview_Users();
                return false;
            }
        });

        return true ;
    }

    public void AppearStateUser (HolderUsers holderUsers , DataSnapshot dataSnapshot){

        if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("Online")){

            holderUsers.StateImage.setVisibility(View.VISIBLE);
            holderUsers.StateImage.setBorderColor(Color.WHITE);
            holderUsers.StateImage.setColorFilter(Color.GREEN);
            holderUsers.State.setText(dataSnapshot.child("userstate").child("state").getValue().toString());
            holderUsers.State.setTextColor(Color.rgb(98,199,34));

        }if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("Busy")){

            holderUsers.StateImage.setVisibility(View.VISIBLE);
            holderUsers.StateImage.setBorderColor(Color.WHITE);
            holderUsers.StateImage.setColorFilter(Color.GREEN);
            holderUsers.State.setText("Online");
            holderUsers.State.setTextColor(Color.rgb(98,199,34));

        }if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("In a meeting")){

            holderUsers.StateImage.setVisibility(View.VISIBLE);
            holderUsers.StateImage.setBorderColor(Color.WHITE);
            holderUsers.StateImage.setColorFilter(Color.GREEN);
            holderUsers.State.setText("Online");
            holderUsers.State.setTextColor(Color.rgb(98,199,34));

        }if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("At work")){

            holderUsers.StateImage.setVisibility(View.VISIBLE);
            holderUsers.StateImage.setBorderColor(Color.WHITE);
            holderUsers.StateImage.setColorFilter(Color.GREEN);
            holderUsers.State.setText("Online");
            holderUsers.State.setTextColor(Color.rgb(98,199,34));

        }if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("At School")){

            holderUsers.StateImage.setVisibility(View.VISIBLE);
            holderUsers.StateImage.setBorderColor(Color.WHITE);
            holderUsers.StateImage.setColorFilter(Color.GREEN);
            holderUsers.State.setText("Online");
            holderUsers.State.setTextColor(Color.rgb(98,199,34));

        }if(dataSnapshot.child("userstate").child("state").getValue().toString().equals("Offline")){

            holderUsers.StateImage.setVisibility(View.GONE);
            holderUsers.State.setText("last seen " + dataSnapshot.child("userstate").child("date").getValue().toString()+ " at "+ dataSnapshot.child("userstate").child("time").getValue().toString());
            holderUsers.State.setTextColor(Color.rgb(103,104,104));
        }

    }

    private void OnCleckFriend(HolderUsers holderUsers , final String ID){

        holderUsers.CardView_CustomlistUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent PublicProfileUser = new Intent(FindFriends.this,Users_Profile.class);
                       PublicProfileUser.putExtra("ID",ID);
                       PublicProfileUser.putExtra("UserID",mAuth.getCurrentUser().getUid());
                       PublicProfileUser.putExtra("privacy","public");
                       startActivity(PublicProfileUser);
            }
        });
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
}
