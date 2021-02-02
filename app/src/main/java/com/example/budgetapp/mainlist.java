package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class mainlist extends AppCompatActivity {
    FirebaseFirestore fStore;


TextView menu;
    FirebaseUser using;
    String userid;
    FirebaseAuth fAuth;
    StorageReference storageReference;

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



                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        oncartclick();
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
    public void oncartclick(){

        android.app.AlertDialog dialog = new AlertDialog.Builder(this,R.style.AlertDialogStyle)
                .setTitle("Cart Opptions")
                .setMessage("Remove Product?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mainlist.this, "Conatctadmin", Toast.LENGTH_SHORT).show();


                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


}