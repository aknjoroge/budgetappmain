package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    private static final String TAG = null ;
    TextView sendtologin;
ProgressBar forsign;
Button signups;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userid;
String takemail,takepass,takename;
EditText signname,signpas,signemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sendtologin=findViewById(R.id.tologintxt);
        forsign=findViewById(R.id.progressBarsign);
        signups=findViewById(R.id.signupbtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        signname=findViewById(R.id.regname);
        signpas=findViewById(R.id.regpass);
        signemail=findViewById(R.id.regmail);

        signups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(takemail=signemail.getText().toString())){
                    signemail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(takename=signname.getText().toString())){
                    signname.setError("Name is required");
                    return;
                }
                if(TextUtils.isEmpty(takepass=signpas.getText().toString())){
                    signpas.setError("Pass is required");
                    return;
                }

forsign.setVisibility(View.VISIBLE);

takemail=signemail.getText().toString();
takepass=signpas.getText().toString();
                fAuth.createUserWithEmailAndPassword(takemail,takepass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser fuser =fAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(signup.this, "Verification Email Sent ", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(signup.this, "Error sending verification , Contact Admin", Toast.LENGTH_SHORT).show();
                            }
                        });


                        Toast.makeText(signup.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        userid= fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userid);
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", takename);
                        user.put("pass",takepass);
                        user.put("mail", takemail);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Log.d(TAG, "user profile created " + userid);
                                createbank();
                                forsign.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG ,"on failure: "+e.toString());
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(signup.this, "Failed sign-up check internet", Toast.LENGTH_SHORT).show();
                        forsign.setVisibility(View.INVISIBLE);
                    }
                });


            }
        });



        sendtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });

    }
    private void createbank() {

        Map<String,Object> userbank =new HashMap<>();
        userbank.put("income","0");
        userbank.put("expense","0");
        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");

        documentReference.set(userbank, SetOptions.merge());



    }


}