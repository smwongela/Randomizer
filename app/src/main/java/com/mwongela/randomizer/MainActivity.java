package com.mwongela.randomizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference databaseReference;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setHasFixedSize(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent spin = new Intent(MainActivity.this, PickActivity.class);
                startActivity(spin);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("PickedNumbers");
    }

    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is logged in populate the Ui With card views
            updateUI(currentUser);
            adapter.startListening();

        }


    }

    private void updateUI(FirebaseUser currentUser) {
        //create and initialize an instance of Query that retrieves all posts uploaded

        Query query = databaseReference.orderByChild("randomNumber");
        // Create and initialize and instance of Recycler Options passing in your model class and
        FirebaseRecyclerOptions<Merry> options = new FirebaseRecyclerOptions.Builder<Merry>().
                setQuery(query, new SnapshotParser<Merry>() {


                    @NonNull
                    @Override
                    //Create a snap shot of your model
                    public Merry parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Merry(snapshot.child("randomNumber").getValue().toString(),
                                snapshot.child("profilePhoto").getValue().toString(),
                                snapshot.child("displayName").getValue().toString(),
                                snapshot.child("time").getValue().toString(),
                                snapshot.child("date").getValue().toString());

                    }
                })
                .build();
        adapter = new FirebaseRecyclerAdapter<Merry, MainActivity.randomViewHolder>(options) {
            @NonNull
            @Override
            public randomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items, parent, false);
                return new MainActivity.randomViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull randomViewHolder holder, int position, @NonNull Merry model) {
                final String post_key = getRef(position).getKey();
                holder.setRandomNumber(model.getRandomNumber());
                holder.setUserName(model.getDisplayName());
                holder.setProfilePhoto(getApplicationContext(), model.getProfilePhoto());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setMerryNumber();
                //holder.numberTextView.text = position.plus(1).toString();

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            adapter.stopListening();
        }
    }

    public static class randomViewHolder extends RecyclerView.ViewHolder {
        //Declare the view objects in the card view

        public TextView commenterName;
        public ImageView commenterimage;
        public TextView commentTime;
        public TextView commentDate;
        public TextView the_RandomNumber;
        public TextView merryNumber;
        private ArrayList<Merry> merryData;
        private FirebaseRecyclerAdapter adapter;
        private int size;
        private DatabaseReference countPeople;

        public randomViewHolder(@NonNull View itemView) {
            super(itemView);
            //Initialize the card view item objects

            commenterName = itemView.findViewById(R.id.random_user);
            commenterimage = itemView.findViewById(R.id.userImage);
            commentTime = itemView.findViewById(R.id.time);
            commentDate = itemView.findViewById(R.id.date);
            the_RandomNumber = itemView.findViewById(R.id.number);
            merryNumber = itemView.findViewById(R.id.merry_number);
            merryNumber.setVisibility(View.INVISIBLE);
            countPeople = FirebaseDatabase.getInstance().getReference().child("PickedNumbers");
        }

        public void setRandomNumber(String randomNumber) {
            the_RandomNumber.setText(randomNumber);
        }

        public void setUserName(String displayName) {
            commenterName.setText(displayName);
        }

        public void setProfilePhoto(Context context, String profilePhoto) {
            Picasso.with(context).load(profilePhoto).into(commenterimage);
        }

        public void setTime(String time) {
            commentTime.setText(time);
        }

        public void setDate(String date) {
            commentDate.setText(date);
        }

        public void setMerryNumber() {
            //  size = adapter.getItemCount();
            final int position = getAdapterPosition();
            countPeople.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    size = (int) snapshot.getChildrenCount();

                    if (size > 6) {
                        merryNumber.setVisibility(View.VISIBLE);
                        if (position == 0) {
                            merryNumber.setText(R.string.one);
                        }
                        if (position == 1) {
                            merryNumber.setText(R.string.two);
                        }
                        if (position == 2) {
                            merryNumber.setText(R.string.three);
                        }
                        if (position == 3) {
                            merryNumber.setText(R.string.four);
                        }
                        if (position == 4) {
                            merryNumber.setText(R.string.five);
                        }
                        if (position == 5) {
                            merryNumber.setText(R.string.six);
                        }
                        if (position == 6) {
                            merryNumber.setText(R.string.seven);
                        }
                        if (position == 7) {
                            merryNumber.setText(R.string.eight);
                        }
                        if (position == 8) {
                            merryNumber.setText(R.string.nine);
                        }
                        if (position == 9) {
                            merryNumber.setText(R.string.ten);
                        }
                        if (position == 10) {
                            merryNumber.setText(R.string.eleven);
                        }
                        if (position == 11) {
                            merryNumber.setText(R.string.twelve);
                        }
                        if (position == 12) {
                            merryNumber.setText(R.string.thirteen);
                        }
                        if (position == 13) {
                            merryNumber.setText(R.string.fourteen);
                        }
                        if (position == 14) {
                            merryNumber.setText(R.string.fifteen);
                        }
                        if (position == 15) {
                            merryNumber.setText(R.string.sixteen);
                        }
                        if (position == 16) {
                            merryNumber.setText(R.string.seventeen);
                        }
                        if (position == 17) {
                            merryNumber.setText(R.string.eighteen);
                        }
                        if (position == 18) {
                            merryNumber.setText(R.string.nineteen);
                        }
                        if (position == 19) {
                            merryNumber.setText(R.string.twenty);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent instruct = new Intent(MainActivity.this, Instructions.class);
             startActivity(instruct);
        }
        //implement the functionality of the add icon, so that the user on clicking it launches the post activity
        else if (id == R.id.action_add) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {

                Intent postIntent = new Intent(MainActivity.this, Comment.class);
                startActivity(postIntent);
            }
            // on clicking log out, log the user out
        } else if (id == R.id.logout){
            mAuth.signOut();
            Intent logouIntent = new Intent(MainActivity.this, RegisterActivity.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);
        }
        return super.onOptionsItemSelected(item);
    }


}