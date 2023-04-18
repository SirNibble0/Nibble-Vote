package com.example.nibblevote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nibblevote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn = "islogin";

    private CircleImageView circleImg;
    private TextView nameTxt, nationalIdTxt;
    private Button voteBtn, createBtn, startBtn;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseFirestore = FirebaseFirestore.getInstance();

        circleImg = findViewById(R.id.circle_image);
        nameTxt = findViewById(R.id.name);
        nationalIdTxt = findViewById(R.id.nationalId);
        createBtn = findViewById(R.id.adminBtn);
        startBtn = findViewById(R.id.start_voting);
        voteBtn = findViewById(R.id.give_vote);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(IsLogIn, true);
        pref.commit();

//        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                pref.putBoolean(IsLogIn, false);
//                pref.commit();
//                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                finish();
//            }
//        });

        firebaseFirestore.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            String name = task.getResult().getString("name");
                            String nationalId = task.getResult().getString("nationalId");
                            String image = task.getResult().getString("image");

                            assert name != null;
                            if (name.equals("admin")) {

                                startBtn.setVisibility(View.VISIBLE);
                                createBtn.setVisibility(View.VISIBLE);
                                voteBtn.setVisibility(View.GONE);

                            } else {
                                startBtn.setVisibility(View.GONE);
                                createBtn.setVisibility(View.GONE);
                                voteBtn.setVisibility(View.VISIBLE);
                            }

                            nameTxt.setText(name);
                            nationalIdTxt.setText(nationalId);
                            Glide.with(HomeActivity.this).load(image).into(circleImg);

                        } else {
                            Toast.makeText(HomeActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, Create_CandidateActivity.class));

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, AllCandidatesActivity.class));

            }
        });

        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, AllCandidatesActivity.class));

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SharedPreferences.Editor pref = sharedPreferences.edit();
        switch (id) {
            case R.id.show_result:
                startActivity(new Intent(HomeActivity.this, ResultActivity.class));

                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                pref.putBoolean(IsLogIn, false);
                pref.commit();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                return true;

            case R.id.exit:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
