package com.mwongela.randomizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PickActivity extends AppCompatActivity {

    private TextView RandomNumber;
    private ImageView wheel;
    private Random random = new Random();
    private int lastDir;
    private boolean spinning;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        RandomNumber = findViewById(R.id.genText);
        wheel = findViewById(R.id.spin);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("PickedNumbers");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        wheel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!spinning) {
                    final int newDir = random.nextInt(10000);
                    int pivotX = wheel.getWidth() / 2;
                    int pivotY = wheel.getHeight() / 2;
                    Animation rotate = new RotateAnimation(lastDir, newDir, pivotX, pivotY);
                    rotate.setDuration(2500);
                    rotate.setFillAfter(true);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            spinning = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            int a = 0;
                            Integer[] colm = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                                    20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 40, 50, 60, 70, 80, 90,
                                    100, 31, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 44, 45, 46, 47,
                                    48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 59, 61, 62, 63, 64, 65, 66, 67,
                                    68, 69, 71, 72, 73, 74, 75, 76, 77, 78, 79, 81, 82, 83, 84, 85, 86, 87, 88,
                                    89, 91, 92, 93, 94, 95, 96, 97, 98, 99
                            };
                            // final List<Integer> ints = new ArrayList<Integer>(Arrays.asList(colm));
                            ArrayList<Integer> ints = new ArrayList<Integer>(Arrays.asList(colm));
                            Random rand = new Random();

                            for (int i = 0; (i < 30) && (ints.size() > 0); i++) {
                                final int randomIndex = rand.nextInt(ints.size());
                                int temp = ints.get(randomIndex);
                                ints.remove(randomIndex);

                                a = temp;

                            }





                              /*
                            HashSet hs = new HashSet();
                            while(hs.size()<10){

                                int num=(int)(Math.random()*100);
                                hs.add(num);

                            }

                            Iterator it=hs.iterator();


                            while(it.hasNext()) {
                                 a =(int) it.next()/10;
                               // truncate = a/10;
                                RandomNumber.setText(String.valueOf(a));
                                     */

                            final int pickedNumber = a;
                            java.util.Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                            final String saveCurrentDate = currentDate.format(calendar.getTime());

                            java.util.Calendar calendar1 = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                            final String saveCurrentTime = currentTime.format(calendar1.getTime());
                            final String myNumber = String.valueOf(a);
                            if ((pickedNumber != 0)) {
                                final DatabaseReference newNumber = databaseRef.push();
                                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        newNumber.child("uid").setValue(mCurrentUser.getUid());
                                        newNumber.child("time").setValue(saveCurrentTime);
                                        newNumber.child("date").setValue(saveCurrentDate);
                                        newNumber.child("profilePhoto").setValue(dataSnapshot.child("profilePhoto").getValue());
                                        newNumber.child("displayName").setValue(dataSnapshot.child("displayName").getValue());
                                        newNumber.child("randomNumber").setValue(pickedNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //show a toast to indicate the profile was updated
                                                    Toast.makeText(PickActivity.this, "Your Number is" + myNumber, Toast.LENGTH_SHORT).show();
                                                    //launch the Main activity
                                                    Intent post = new Intent(PickActivity.this, MainActivity.class);
                                                    startActivity(post);
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }


                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            spinning = false;

                        }
                    });
                    lastDir = newDir;
                    wheel.startAnimation(rotate);
                }
            }
        });
    }

}





