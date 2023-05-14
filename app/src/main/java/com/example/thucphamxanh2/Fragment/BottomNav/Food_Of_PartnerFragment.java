package com.example.thucphamxanh2.Fragment.BottomNav;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thucphamxanh2.Adapter.ProductAdapter;
import com.example.thucphamxanh2.Fragment.ProductFragments.ProductFragment;
import com.example.thucphamxanh2.Model.Product;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Food_Of_PartnerFragment extends Fragment {
    RecyclerView food_of_partner_recyclerView;
    LinearLayoutManager linearLayoutManager;
    private List<Product> listProduct = new ArrayList<>();
    private ProductAdapter adapter;
    private ProductFragment fragment= new ProductFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food__of__partner, container, false);
        food_of_partner_recyclerView = view.findViewById(R.id.food_of_partner_recyclerView);

        listProduct = loadListFood();
        linearLayoutManager = new LinearLayoutManager(getContext());
        food_of_partner_recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ProductAdapter(listProduct,fragment,getContext());
        food_of_partner_recyclerView.setAdapter(adapter);

        food_of_partner_recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }

    private List<Product> loadListFood(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Partner", Context.MODE_PRIVATE);
        String partner = sharedPreferences.getString("partner","");
        Log.d("aaaaaaaaaaa",partner);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = database.getReference("Product");
//        DatabaseReference reference2 = database.getReference("Partner");


        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Product product = snap.getValue(Product.class);
                    if ( product.getCodeCategory()==4 && partner.equals(product.getUserPartner())){
                        listProduct.add(product);
                    }

                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return listProduct;
    }
}