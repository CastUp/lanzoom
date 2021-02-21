package com.castup.castup;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View GroupFragment ;
    private RecyclerView recyclerGroups ;
    private Resources resources ;
    private AdView mAdView;

    private DatabaseReference DataRef ;
    private FirebaseAuth mAuth ;
    private String FriendID ;
    private FirebaseRecyclerOptions<Data_Firebase> options ;
    private FirebaseRecyclerAdapter<Data_Firebase,HolderGroups> adapter ;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            GroupFragment = inflater.inflate(R.layout.fragment_groups, container, false);

            DataRef        = FirebaseDatabase.getInstance().getReference();
            mAuth          = FirebaseAuth.getInstance();
            FriendID       = mAuth.getCurrentUser().getUid();

            View_Tools();
            ADMOB();
            Group();
            Recycle_Groups();




        return GroupFragment ;

    }

    private void View_Tools(){

        recyclerGroups  = GroupFragment.findViewById(R.id.Recycle_Groups);
        resources       = getResources();

    }

    public void ADMOB (){

        AdView adView = new AdView(GroupFragment.getContext());
        adView.setAdSize(AdSize.BANNER);adView.setAdUnitId("ca-app-pub-8175375039939984/7499603921");

        mAdView = GroupFragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adView = new AdView(GroupFragment.getContext());
        adView.setAdSize(AdSize.BANNER);

    }

    private void Recycle_Groups(){

         options  = new FirebaseRecyclerOptions.Builder<Data_Firebase>()
                    .setQuery(DataRef.child("PublicGroups"),Data_Firebase.class)
                    .build();

         adapter = new FirebaseRecyclerAdapter<Data_Firebase, HolderGroups>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderGroups holderGroups, int position, @NonNull Data_Firebase data_firebase) {

                final String Groups = getRef(position).getKey();

                DataRef.child("PublicGroups").child(Groups).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).resize(200,200).into(holderGroups.ImagepublicGroup);
                            holderGroups.NamePublicGroup.setText(dataSnapshot.child("name").getValue().toString());
                            holderGroups.DescriptionPublicGroup.setText(dataSnapshot.child("description").getValue().toString());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holderGroups.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent OpenChatingGroup = new Intent(getContext(),ChatingRoom.class);
                               OpenChatingGroup.putExtra("GroupName",Groups);
                               OpenChatingGroup.putExtra("Friend",FriendID);
                               startActivity(OpenChatingGroup);
                    }
                });

            }

            @NonNull
            @Override
            public HolderGroups onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View  Veiw_Groups = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_groups,parent,false);

                return new HolderGroups(Veiw_Groups);
            }
        };

         recyclerGroups.setLayoutManager(new LinearLayoutManager(getContext()));
         recyclerGroups.setAdapter(adapter);
         adapter.startListening();
         adapter.notifyDataSetChanged();

    }

    public static class HolderGroups extends RecyclerView.ViewHolder{

        CircleImageView ImagepublicGroup ;
        TextView        NamePublicGroup  , DescriptionPublicGroup ;

        public HolderGroups(@NonNull View itemView) {
            super(itemView);


            ImagepublicGroup       = itemView.findViewById(R.id.ImagePublicGroup);
            NamePublicGroup        = itemView.findViewById(R.id.NamePublicGroup);
            DescriptionPublicGroup = itemView.findViewById(R.id.DescriptionPublicGroup);

        }
    }

    private void Group(){

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name","High Tech");
        hashMap.put("photo","https://firebasestorage.googleapis.com/v0/b/castup-6f01e.appspot.com/o/GroupsPhoto%2Fhigtech.png?alt=media&token=4700750d-e30c-4152-9f8e-97a51edc223b");
        hashMap.put("description","New in technology");

        DataRef.child("PublicGroups").child("High Tech").updateChildren(hashMap);
    }


}
