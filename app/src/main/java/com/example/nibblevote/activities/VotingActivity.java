package com.example.nibblevote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nibblevote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VotingActivity extends AppCompatActivity {

    private CircleImageView image;
    private TextView name, party, position;
    private Button vote_Btn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        image = findViewById(R.id.img);
        name = findViewById(R.id.name);
        position = findViewById(R.id.post);
        party = findViewById(R.id.party);
        vote_Btn = findViewById(R.id.voteBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String url = getIntent().getStringExtra("image");
        String na = getIntent().getStringExtra("name");
        String pos = getIntent().getStringExtra("position");
        String part = getIntent().getStringExtra("party");
        String id = getIntent().getStringExtra("id");

        Glide.with(this).load(url).into(image);
        name.setText(na);
        position.setText(pos);
        party.setText(part);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        vote_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String finish = "voted";
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("finish",finish);
                userMap.put("deviceIp",getDeviceIP());
//                userMap.put(pos, id);

                firebaseFirestore.collection("User")
                        .document(uid)
                        .update(userMap);

                Map<String, Object> candidateMap = new HashMap<>();
                candidateMap.put("deviceIp", getDeviceIP());
                candidateMap.put("candidatePost", pos);
                candidateMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Candidate/"+id+"/Vote")
                        .document(uid)
                        .set(candidateMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    startActivity(new Intent(VotingActivity.this, ResultActivity.class));
                                    finish();
                                }else{

                                    Toast.makeText(VotingActivity.this, "Voted Successfully!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });

    }

    private String getDeviceIP() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface inf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(VotingActivity.this, ""+e, Toast.LENGTH_SHORT).show();
        }return null;
    }
}