package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class login extends AppCompatActivity {
TextView tosign;
String takelogmail,takelogpass;
Button tologin;
EditText logingpass,loginemail;
    private FirebaseAuth fAuth;
ProgressBar forloging;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tosign=findViewById(R.id.tosigntxt);
        tologin=findViewById(R.id.loginbtn);
        forloging=findViewById(R.id.logprogressBar);
        loginemail=findViewById(R.id.logmail);
        logingpass=findViewById(R.id.logpass);
        fAuth = FirebaseAuth.getInstance();

        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(takelogmail=loginemail.getText().toString())){
                    loginemail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty( takelogpass=logingpass.getText().toString())){
                    logingpass.setError("password required");
                    return;
                }
                takelogpass=logingpass.getText().toString();
                if(takelogpass.length()< 6 ){
                    logingpass.setError("password need to be above 6 characters");
                }

                forloging.setVisibility(View.VISIBLE);

                takelogmail=loginemail.getText().toString();
                takelogpass=logingpass.getText().toString();

                fAuth.signInWithEmailAndPassword(takelogmail,takelogpass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(login.this, "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(login.this, "Login failed, try again ", Toast.LENGTH_SHORT).show();
                        forloging.setVisibility(View.INVISIBLE);
                    }
                });


//                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//
//
//                        }else {
//
//
//
//                        }
//
//                    }
//                });




            }
        });

        tosign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signup.class));
            }
        });


    }
}