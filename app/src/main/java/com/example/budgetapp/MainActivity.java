package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    FirebaseFirestore fStore;
    String userid;
    FirebaseAuth fAuth;
    Spinner amountype;
    int totalpriceone =0;
    public RecyclerView recyclerView;
    String toload;
    ProgressBar oneprog;
    RecyclerView.LayoutManager layoutManager;
String globexp,globinc;
    String atype;
    String globamount;
    String cdate,ctime;
    int examount;
    Uri imageuri;
    ImageView settingdp;
    String takentype;
    TextView nametx,balancetx,incometx,exencetx,editdps;
    FirebaseUser using;
    LinearLayout linearLayout;
    EditText name,amount;
    Button ofex,ofinc,ofall,adding;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
settingdp=findViewById(R.id.pprofilepics);
        amountype=findViewById(R.id.setspinner);
        adding=findViewById(R.id.addtypebtn);
        oneprog=findViewById(R.id.bgprog);
        recyclerView=findViewById(R.id.recyclermenucarts);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userid = fAuth.getCurrentUser().getUid();
        using=fAuth.getCurrentUser();
        editdps=findViewById(R.id.changedptxt);
        editdps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askfordp();
            }
        });
        name=findViewById(R.id.listtypename);
        amount=findViewById(R.id.listtypeamounte);
        adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Name required");
                    return;
                }if(TextUtils.isEmpty(amount.getText().toString())){
                    amount.setError("Amount is required");
                    return;
                }

                String typein=atype;
                if(typein.equals("Select")){
                    Toast.makeText(MainActivity.this, "Select Type of Data to add", Toast.LENGTH_SHORT).show();
                    return;
                }if(typein.equals("Expense")){
                    takentype="expense";

                }if(typein.equals("Income")){
takentype="income";
                }

                oneprog.setVisibility(View.VISIBLE);
                Calendar calendar= Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
                cdate =currentdate.format(calendar.getTime());

                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
                ctime =currenttime.format(calendar.getTime());
final String storemode=ctime+cdate;
final String takename =name.getText().toString();
final String takeamount=amount.getText().toString();
globamount=amount.getText().toString();

                DocumentReference documentReference = fStore.collection("Budgetlist").document(takentype).collection(userid)
                        .document(storemode);

                Map<String, Object> user = new HashMap<>();
                user.put("name", takename);
                user.put("amount",takeamount);
                user.put("date",storemode);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {


                        DocumentReference documentReference = fStore.collection("Budgetlist").document("all").collection(userid)
                                .document(storemode);

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", takename);
                        user.put("amount",takeamount);
                        user.put("date",storemode);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {


                                budgetCart();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                oneprog.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Failled to add to all", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        oneprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Failled", Toast.LENGTH_SHORT).show();
                    }
                });



              //  Toast.makeText(MainActivity.this, takentype, Toast.LENGTH_SHORT).show();


            }
        });
        ArrayAdapter<CharSequence> arrayAdapter =ArrayAdapter.createFromResource(this,R.array.locations,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amountype.setAdapter(arrayAdapter);
        amountype.setOnItemSelectedListener(this);

        nametx=findViewById(R.id.mainnametxt);

balancetx=findViewById(R.id.mainbalancetxt);
ofall=findViewById(R.id.viewallbtn);
        ofinc=findViewById(R.id.viewincomebtn);
        ofex=findViewById(R.id.viewexbtn);
linearLayout=findViewById(R.id.inputtextlayout);



incometx=findViewById(R.id.mainincometext);
exencetx=findViewById(R.id.mainexpensetext);



        autochangeprofile2();
        autochangeprofiles();


        ofall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  linearLayout.setVisibility(View.INVISIBLE);
                ofall.setBackgroundColor(getResources().getColor(R.color.white));
                ofinc.setBackgroundColor(getResources().getColor(R.color.red));
                ofex.setBackgroundColor(getResources().getColor(R.color.red));
                String towhere ="all";
                Intent intent=new Intent(MainActivity.this,mainlist.class);
                intent.putExtra("location",towhere);
                startActivity(intent);


            }
        });
        ofinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                ofall.setBackgroundColor(getResources().getColor(R.color.red));
                ofinc.setBackgroundColor(getResources().getColor(R.color.white));
                ofex.setBackgroundColor(getResources().getColor(R.color.red));
                String towhere ="income";
                Intent intent=new Intent(MainActivity.this,mainlist.class);
                intent.putExtra("location",towhere);
                startActivity(intent);


            }
        });
        ofex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                ofall.setBackgroundColor(getResources().getColor(R.color.red));
                ofinc.setBackgroundColor(getResources().getColor(R.color.red));
                ofex.setBackgroundColor(getResources().getColor(R.color.white));
                String towhere ="expense";
                Intent intent=new Intent(MainActivity.this,mainlist.class);
                intent.putExtra("location",towhere);
                startActivity(intent);

            }
        });



    }



    private void budgetCart() {


        if(takentype.equals("expense")){
          //  Toast.makeText(this, "BUDGET IS EXPENSE"+takentype, Toast.LENGTH_LONG).show();

            int gexp= Integer.parseInt(globexp);
            int cexp= Integer.parseInt(globamount);
            int addes= gexp+cexp;

            String uploadex =String.valueOf(addes);
            //Toast.makeText(this, uploadex, Toast.LENGTH_SHORT).show();

            Map<String,Object> userbank2 =new HashMap<>();
            userbank2.put("expense",uploadex);

            final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");

            documentReference.set(userbank2,SetOptions.merge());


            oneprog.setVisibility(View.INVISIBLE);

        }else if (takentype.equals("income")){
            int ginc= Integer.parseInt(globinc);
            int cinc= Integer.parseInt(globamount);
            int addin= ginc+cinc;

            String uploadex =String.valueOf(addin);

            Map<String,Object> userbank3 =new HashMap<>();
            userbank3.put("income",uploadex);

            final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");

            documentReference.set(userbank3,SetOptions.merge());




           // Toast.makeText(this, "BUDGET IS INCOME"+takentype, Toast.LENGTH_LONG).show();
            oneprog.setVisibility(View.INVISIBLE);
        }
        else {
            Toast.makeText(this, "BUDGET NOT RECOGNISED", Toast.LENGTH_LONG).show();
            oneprog.setVisibility(View.INVISIBLE);
        }

//        String in=takentype;
//        int ttl= examount;
//        int ttl2=Integer.parseInt(globamount);
//        int inputtt =ttl+ttl2;
//        String valueonfint = String.valueOf(inputtt);
//
//        Map<String,Object> userbank =new HashMap<>();
//        userbank.put(takentype,valueonfint);
//        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
//
//        documentReference.set(userbank,SetOptions.merge());
//
//     ttl=0;
//    ttl2=0;
//   inputtt=0;
//   examount=0;



    }

    private void checkdata() {

        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String amounttake=documentSnapshot.getString("expense");
                 examount= Integer.parseInt(amounttake);
              //  Toast.makeText(MainActivity.this, amounttake, Toast.LENGTH_SHORT).show();


            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Toast.makeText(this, "Data received", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();

                imageuri = data.getData();
                settingdp.setImageURI(imageuri);
//                hasimageset=true;

                uploadimage(imageuri);
            }catch (Exception e){

                Toast.makeText(this, "Run time exception gained", Toast.LENGTH_SHORT).show();
            }


        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void askfordp() {
        android.app.AlertDialog dialog = new AlertDialog.Builder(this,R.style.AlertDialogStyle)
                .setTitle("Profile Image")
                .setMessage("update an image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeindp();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private void autochangeprofiles() {

        StorageReference profileref = storageReference.child("Userprofile/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //  Toast.makeText(settings.this, "Loading profile", Toast.LENGTH_SHORT).show();
                Picasso.get().load(uri).into(settingdp);
                // Picasso.get().load(uri).into(nav_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "errorr "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void uploadimage(Uri imageuri) {

        final StorageReference fileref = storageReference.child("Userprofile/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(settingdp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed in getting url", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(MainActivity.this, "IMAGE UPLOADED", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "UPLOAD ERROOR", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void takeindp() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        atype=parent.getItemAtPosition(position).toString();

        if(atype.equals("Expense")){
            name.setHint("Expense name");
            amount.setHint("Expense amount");

        }if(atype.equals("Income")){
            name.setHint("Income name");
            amount.setHint("Income amount");
        }if(atype.equals("Select")){
            name.setHint("Select a type");
            amount.setHint(" ");

        }
        else {

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void autochangeprofile2() {
        final DocumentReference documentReference = fStore.collection("users").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String nametake=documentSnapshot.getString("name");
                String mailtake =documentSnapshot.getString("mail");


                nametx.setText(nametake);





            }
        });
        autobanktxt();

    }

    private void autobanktxt() {
        final DocumentReference documentReference = fStore.collection("BudgetCart").document("Foruser").collection(userid).document("bank");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String amounttake=documentSnapshot.getString("expense");
                String amountake2=documentSnapshot.getString("income");


                int inc= Integer.parseInt(amountake2);
                int exp= Integer.parseInt(amounttake);

                globexp=amounttake;
                globinc=amountake2;

              //  Toast.makeText(MainActivity.this, "globexp"+globexp, Toast.LENGTH_SHORT).show();
                int bl=inc-exp;
                String balancing =String.valueOf(bl);

                balancetx.setText(balancing);
                incometx.setText(amountake2);
                exencetx.setText(amounttake);

                //  Toast.makeText(MainActivity.this, amounttake, Toast.LENGTH_SHORT).show();


            }
        });



    }

}