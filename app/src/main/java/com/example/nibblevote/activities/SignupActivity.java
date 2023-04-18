package com.example.nibblevote.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nibblevote.R;
import com.example.nibblevote.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private CircleImageView userProfile;
    private EditText userName, userPassword, userEmail, userNationalID;
    private Button signUpBtn;
    private Uri mainUri = null;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    public static final String PREFERENCES = "prefKey";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Password = "passwordKey";
    public static final String NationalId = "nationalIdKey";
    public static final String Image = "imageKey";

    SharedPreferences sharedPreferences;
    FirebaseFirestore firebaseFirestore;
    StorageReference reference;
//    FirebaseStorage firebaseStorage;

    String name, password, email, nationalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        reference = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.have_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        userProfile = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_Name);
        userPassword = findViewById(R.id.user_Password);
        userEmail = findViewById(R.id.user_Email);
        userNationalID = findViewById(R.id.user_National_Id);
        signUpBtn = findViewById(R.id.signUp_Btn);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SignupActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(SignupActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else {
                        cropImage();
                    }

                }else{
                    cropImage();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String password = userPassword.getText().toString();
                String nationalId = userNationalID.getText().toString();
                String email = userEmail.getText().toString().trim();
                String image = mainUri.toString();


                String uid = mAuth.getUid();
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        
                        verifyEmail();
                        progressDialog.cancel();
                        assert uid != null;
                        firebaseFirestore.collection("User")
                                .document(mAuth.getUid())
                                .set(new UserModel(name, nationalId, email, password, image, uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                StorageReference imagePath = reference.child("image_profile").child(mAuth.getCurrentUser().getUid() + "jpg");
                                imagePath.putFile(Uri.parse(image));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(SignupActivity.this, "Image not stored!", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });


            }
        });


    }

    private void verifyEmail() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        Toast.makeText(SignupActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();

                    }else {
                        finish();
                    }

                }
            });

        }

    }

    private void cropImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainUri = result.getUri();
                userProfile.setImageURI(mainUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}