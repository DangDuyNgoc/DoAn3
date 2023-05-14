package com.example.thucphamxanh2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import com.example.thucphamxanh2.Adapter.CartAdapter;
import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.Model.Cart;
import com.example.thucphamxanh2.Model.ProductTop;
import com.example.thucphamxanh2.Model.Voucher;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;

    private List<Cart> list;
    private List<Bill> listBill;
    private List<ProductTop> listTop = new ArrayList<>();
    private List<Voucher> listVoucher = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private CartAdapter adapter;

    private TextView tvTotalPrice,tv1, tvHide1, tvHide2, tvVoucher;
    private Button btn_senBill, btnEmptyProduct;
    private ImageView ivEmptyProduct;

    private Spinner spinner;
//    private String[] arr = {"Không có ưu đãi","Giảm 50%","Giảm 30%", "Giảm 20%"};
    private String[] arr = {""};
    private NumberFormat numberFormat = new DecimalFormat("#,##0");

    private String voucher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#73DE4E")));
        getSupportActionBar().setTitle("Giỏ hàng");
        unitUi();
        btn_senBill.setOnClickListener(view -> {
        addBill();
        for (int i = 0; i < list.size(); i++) {
            addProductTop(list.get(i).getIdProduct(),list.get(i).getNumberProduct(),list.get(i).getIdCategory());
        }
        deleteCart();
        });
    }
    public void unitUi(){
//        getVoucher();
        getProductTop();
        spinner = findViewById(R.id.spinner_voucher_cart);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,arr);
        spinner.setAdapter(adapter1);
        voucher = spinner.getSelectedItem().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                voucher = spinner.getSelectedItem().toString();
                getCartProduct();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        rvCart = findViewById(R.id.recyclerView_CartActivity_listCart);
        list = getCartProduct();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvCart.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(list);
        rvCart.setAdapter(adapter);
        tvTotalPrice = findViewById(R.id.tv_CartActivity_totalPrice);
        tv1 = findViewById(R.id.tv1_CartActivity_totalPrice);
        btn_senBill = findViewById(R.id.btn_CartActivity_btnPay);
        ivEmptyProduct = findViewById(R.id.tv_CartActivity_emptyProduct);
        btnEmptyProduct = findViewById(R.id.btn_CartActivity_emptyProduct);
        listBill = getAllBill();
        tvHide1 = findViewById(R.id.tvHide1);
        tvHide2 = findViewById(R.id.tvHide2);
        tvVoucher = findViewById(R.id.tvVoucher);
    }

    public void getVoucher(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference("Voucher");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listVoucher.clear();
//                for(DataSnapshot snap : snapshot.getChildren()){
//                    Voucher voucher = snap.getValue(Voucher.class);
//                    listVoucher.add(voucher);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
    public  List<Cart> getCartProduct(){
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Vui lòng đợi ...");
//        progressDialog.setCanceledOnTouchOutside(false);

        SharedPreferences preferences = getSharedPreferences("My_User",MODE_PRIVATE);
        String user = preferences.getString("username","");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Cart");
        List<Cart> list1 = new ArrayList<>();
//        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressDialog.dismiss();
                list1.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Cart cart = snap.getValue(Cart.class);
                    if(cart!=null){
                        if (cart.getUserClient().equals(user)){
                            list1.add(cart);

                        }
                    }


                }
                int sum = 0;
                for (int i = 0; i < list1.size(); i++) {
                    sum += list1.get(i).getTotalPrice();
                }

                if (voucher.equals("Giảm 50%")){
                    sum = (int) (sum-sum *0.5);
                }else if (voucher.equals("Giảm 30%")){
                    sum = (int) (sum-sum*0.3);
                }else if (voucher.equals("Giảm 20%")){
                    sum = (int) (sum-sum*0.2);
                }else sum = sum;

                tvTotalPrice.setText(numberFormat.format(sum));
                tv1.setText(""+sum);
                if(list1.size()==0){
                    tvHide1.setVisibility(View.GONE);
                    tvHide2.setVisibility(View.GONE);
                    btn_senBill.setVisibility(View.GONE);
                    tvTotalPrice.setVisibility(View.GONE);
                    ivEmptyProduct.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    btnEmptyProduct.setVisibility(View.VISIBLE);
                    tvVoucher.setVisibility(View.GONE);
                    btnEmptyProduct.setOnClickListener(view -> {
                        startActivity(new Intent(CartActivity.this, MainActivity.class));
                        finish();
                    });
                    rvCart.setVisibility(View.INVISIBLE);
                }else {
                    tvHide1.setVisibility(View.VISIBLE);
                    tvHide2.setVisibility(View.VISIBLE);
                    tvVoucher.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    btn_senBill.setVisibility(View.VISIBLE);
                    ivEmptyProduct.setVisibility(View.INVISIBLE);
                    btnEmptyProduct.setVisibility(View.INVISIBLE);
                    rvCart.setVisibility(View.VISIBLE);
                }
                
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }


    public void addBill(){
        Bill bill = new Bill();
        SharedPreferences preferences = getSharedPreferences("My_User",MODE_PRIVATE);
        String user = preferences.getString("username","");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("k:mm");
        String time = timeFormat.format(Calendar.getInstance().getTime());
        if (listBill.size()==0){
            bill.setIdBill(1);
            bill.setIdClient(user);
            bill.setDayOut(date);
            bill.setTimeOut(time);
            bill.setIdPartner(list.get(0).getIdPartner());
            bill.setTotal(Integer.parseInt(tv1.getText().toString()));
            if (user.equals("admin")){
                bill.setStatus("Yes");
            }else bill.setStatus("No");
            reference.child(""+1).setValue(bill);
            reference.child(""+1).child("Cart").setValue(list);
        }else {
            int i = listBill.size()-1;
            int id = listBill.get(i).getIdBill()+1;
            bill.setIdBill(id);
            bill.setIdClient(user);
            bill.setDayOut(date);
            bill.setTimeOut(time);
            bill.setIdPartner(list.get(0).getIdPartner());
            bill.setTotal(Integer.parseInt(tv1.getText().toString()));
            if (user.equals("admin")){
                bill.setStatus("Yes");
            }else bill.setStatus("No");
            reference.child(""+id).setValue(bill);
            reference.child(""+id).child("Cart").setValue(list);
        }

    }
    public void deleteCart(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Cart");
        for (int i = 0; i < list.size(); i++) {
            reference.child(""+list.get(i).getIdCart()).removeValue();
        }
        if (list.size()==0){
            btn_senBill.setEnabled(false);
        }else  btn_senBill.setEnabled(true);

    }
    public  List<Bill> getAllBill(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        List<Bill> list1 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Bill bill = snap.getValue(Bill.class);
                    list1.add(bill);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }

    public void addProductTop(int id, int amount,int category){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ProductTop");
        ProductTop top = new ProductTop();
        top.setIdProduct(id);
        top.setIdCategory(category);
        top.setAmountProduct(amount);
        if (listTop.size()==0){
            reference.child(""+id).setValue(top);
        }else {
            for (int i = 0; i < listTop.size(); i++) {
                if (id == listTop.get(i).getIdProduct()) {
                    int amounts = listTop.get(i).getAmountProduct() + amount;
                    reference.child("" + id).child("amountProduct").setValue(amounts);
                    return;
                } else {
                    reference.child("" + id).setValue(top);
                }
            }
        }
    }

    public void getProductTop(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ProductTop");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTop.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ProductTop top = snapshot1.getValue(ProductTop.class);
                    listTop.add(top);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}