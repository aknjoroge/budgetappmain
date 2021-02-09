package com.example.budgetapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;

import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity {
    FirebaseFirestore fStore;
    int inc;
    int data;
    ProgressBar progressBar;
    int gdele;
    double exp;
    String savedamount;

    TextView forname,foramount,dd;
    Button dels;
    String detailamount,detailname;
    FirebaseUser using;
    String globkey;
    String userid;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String delamount,globexp,globinc;

    public RecyclerView recyclerView;
    String toload;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toload=getIntent().getStringExtra("cat");
        progressBar=findViewById(R.id.dtprogm);
        globkey=getIntent().getStringExtra("key");
        dels=findViewById(R.id.detaildel);
        dd=findViewById(R.id.datatxt);
        forname=findViewById(R.id.detailname);
        foramount=findViewById(R.id.detamount);
        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userid = fAuth.getCurrentUser().getUid();
        using=fAuth.getCurrentUser();

        getdata();






        dels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(toload.equals("income")){
                    String in = String.valueOf(data);
                    Map<String,Object> userbank3 =new HashMap<>();
                    userbank3.put("income",in);

                    final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
                    documentReference.set(userbank3,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dd.setText("Entry deleted");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fStore.collection("Budgetlist").document(toload).collection(userid).document(globkey).delete();
                                }
                            },1000);

                        }
                    });

                    progressBar.setVisibility(View.INVISIBLE);
                }  if(toload.equals("expense")){
                    String in = String.valueOf(data);
                    Map<String,Object> userbank3 =new HashMap<>();
                    userbank3.put("expense",in);
                    final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
                    documentReference.set(userbank3,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dd.setText("Entry deleted");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fStore.collection("Budgetlist").document(toload).collection(userid).document(globkey).delete();
                                }
                            },500);

                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    public void autobanktxt() {
        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String amounttake=documentSnapshot.getString("expense");
                String amountake2=documentSnapshot.getString("income");

                if(amountake2==null && amounttake ==null){
                    globexp="0";
                    globinc="0";
                }else {
                    globexp=amounttake;
                    globinc=amountake2;

                    if(toload.equals("expense")){

                        int documentamount=Integer.parseInt(detailamount);
                        int globamount=Integer.parseInt(globexp);
                         data= globamount-documentamount;

                     //  dd.setText("balance "+data);


                    }else if(toload.equals("income")){

                        int documentamount=Integer.parseInt(detailamount);
                        int globamount=Integer.parseInt(globinc);
                         data= globamount-documentamount;


                     //   dd.setText("balance "+data);



                    }else {
                        dd.setText("HISTORY CANNOT BE DELETED");

                    }



                }



                }


        });



    }


    private void getdata() {

        final DocumentReference documentReference =  fStore.collection("Budgetlist").document(toload).collection(userid).document(globkey);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                detailamount =documentSnapshot.getString("amount");
                detailname =documentSnapshot.getString("name");
                forname.setText("NAME: "+detailname);
                foramount.setText("AMOUNT: "+detailamount);



            }
        });
        autobanktxt();
    }


}