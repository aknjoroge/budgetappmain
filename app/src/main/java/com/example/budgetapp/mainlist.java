package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Map;

public class mainlist extends AppCompatActivity {
    FirebaseFirestore fStore;
    int inc;
    String globexp,globinc;
    int gdele;
    double exp;
     String savedamount;

TextView menu;
    FirebaseUser using;
    String globkey;
    String userid;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String delamount;

    public RecyclerView recyclerView;
    String toload;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlist);
        toload=getIntent().getStringExtra("location");
menu=findViewById(R.id.menutypetxt);
        recyclerView=findViewById(R.id.recyclermenucart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userid = fAuth.getCurrentUser().getUid();
        using=fAuth.getCurrentUser();
        menu.setText("Your "+toload+" Page.");
       // Toast.makeText(this, toload, Toast.LENGTH_SHORT).show();
        autobanktxt();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirestoreRecyclerOptions<forlist> options = new FirestoreRecyclerOptions.Builder<forlist>()
                .setQuery(fStore.collection("Budgetlist").document(toload).collection(userid),forlist.class).build();
        FirestoreRecyclerAdapter<forlist,listviewholder> adapter= new FirestoreRecyclerAdapter<forlist, listviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull listviewholder holder, int position, @NonNull final forlist model) {

                holder.txtlname.setText("Name :"+model.getName()+".");
                holder.txtlamount.setText("Amount: "+model.getAmount()+"/=");
                holder.txtldate.setText(model.getDate());
globkey=model.getDate();


                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(mainlist.this,details.class);
                        intent.putExtra("key",model.getDate());
                        intent.putExtra("cat",toload);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public listviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list,parent,false);
                listviewholder holder =new listviewholder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }







    private void autobanktxt() {
        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String amounttake=documentSnapshot.getString("expense");
                String amountake2=documentSnapshot.getString("income");
                //  Toast.makeText(MainActivity.this, "oneis"+amountake2, Toast.LENGTH_SHORT).show();

                if(amountake2==null && amounttake ==null){

                }else {
                    globexp=amounttake;
                    globinc=amountake2;


                }



            }
        });



    }

}