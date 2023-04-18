package com.example.nibblevote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.nibblevote.R;
import com.example.nibblevote.adapter.CandidateAdapter;
import com.example.nibblevote.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllCandidatesActivity extends AppCompatActivity {

    private RecyclerView candidateRv;
//    private Button startBtn;
    private List<Candidate> list;
    private CandidateAdapter adapter;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_candidates);

        candidateRv = findViewById(R.id.candidate_rv);
//        startBtn = findViewById(R.id.start);
        firebaseFirestore = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new CandidateAdapter(this, list);
        candidateRv.setLayoutManager(new LinearLayoutManager(this));
        candidateRv.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            firebaseFirestore.collection("Candidate")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()){
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                                    list.add(new Candidate(
                                            snapshot.getString("name"),
                                            snapshot.getString("party"),
                                            snapshot.getString("position"),
                                            snapshot.getString("image"),
                                            snapshot.getId()//to get document id
                                    ));
                                }

                                adapter.notifyDataSetChanged();

                            }else{
                                Toast.makeText(AllCandidatesActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("User")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String finish = task.getResult().getString("finish");

                        if (finish != null) {
                            if (finish.equals("voted")) {

                                Toast.makeText(AllCandidatesActivity.this, "Your vote is counted already!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AllCandidatesActivity.this, ResultActivity.class));
                                finish();

                            }
                        }

                    }
                });
    }
}