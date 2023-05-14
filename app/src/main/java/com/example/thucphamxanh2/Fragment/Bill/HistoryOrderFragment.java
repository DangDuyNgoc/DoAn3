package com.example.thucphamxanh2.Fragment.Bill;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thucphamxanh2.Adapter.AdapterBill;
import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HistoryOrderFragment extends Fragment {

    private RecyclerView rvBill;
    private LinearLayoutManager linearLayoutManager;
    private AdapterBill adapterBill;
    private List<Bill> listBill = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_order, container, false);
        initUi(view);
        return view;
    }
    public void initUi(View view ){
        getBill();
        rvBill = view.findViewById(R.id.rv_billHistory);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvBill.setLayoutManager(linearLayoutManager);
        adapterBill = new AdapterBill(listBill,getContext());
        rvBill.setAdapter(adapterBill);
    }
    public void getBill(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        SharedPreferences preferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        String user = preferences.getString("username","");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBill.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Bill bill = snap.getValue(Bill.class);
                    if(user.equals(bill.getIdPartner()) && bill.getStatus().equals("Yes")){
                        listBill.add(bill);
                    }else if (user.equals(bill.getIdClient()) && bill.getStatus().equals("Yes")){
                        listBill.add(bill);
                    }

                }
                adapterBill.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}