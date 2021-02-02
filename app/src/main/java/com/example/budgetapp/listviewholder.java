package com.example.budgetapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class listviewholder extends  RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtlname,txtlamount,txtldate;
    itemclicklistener listener;

    public listviewholder(@NonNull View itemView) {
        super(itemView);
        txtlname=itemView.findViewById(R.id.listname);
        txtlamount = itemView.findViewById(R.id.listamount);
        txtldate = itemView.findViewById(R.id.listdate);



    }

    public void setItemClickListener(itemclicklistener listener){
        this.listener =listener;
    }

    @Override
    public void onClick(View v) {
        listener.onclick(v,getAdapterPosition(),false);

    }

}
