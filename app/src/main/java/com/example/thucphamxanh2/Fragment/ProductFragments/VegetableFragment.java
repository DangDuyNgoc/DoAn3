package com.example.thucphamxanh2.Fragment.ProductFragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thucphamxanh2.Adapter.ProductAdapter;

import com.example.thucphamxanh2.Model.Product;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class VegetableFragment extends Fragment {

    private List<Product> listVegetable;
    private RecyclerView rvVegetable;
    private LinearLayoutManager linearLayoutManager;
    private ProductAdapter adapter;
    private View view;
    private ProductFragment fragment= new ProductFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_vegetable, container, false);
         unitUI();
        return view;
    }
    public void unitUI(){
        listVegetable = getVegetableProduct();
        rvVegetable = view.findViewById(R.id.rvVegetable);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvVegetable.setLayoutManager(linearLayoutManager);
        adapter = new ProductAdapter(listVegetable,fragment,getContext());
        rvVegetable.setAdapter(adapter);
        rvVegetable.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    public  List<Product> getVegetableProduct(){
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Vui lòng đợi ...");
        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");
        List<Product> list1 = new ArrayList<>();
        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                list1.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Product product = snap.getValue(Product.class);
                    if (product.getCodeCategory()==1){
                        list1.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }


}