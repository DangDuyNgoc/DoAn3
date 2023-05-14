package com.example.thucphamxanh2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.Model.Cart;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.ViewHolder> {
    private List<Bill> list;
    private Context context;
    List<Cart> listCart ;
    AdapterItemBill adapterItemBill;
    LinearLayoutManager linearLayoutManager;

    public StatisticalAdapter(List<Bill> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StatisticalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical,parent,false);
        return new StatisticalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = list.get(position);
        listCart = getAllCart(position);
        adapterItemBill = new AdapterItemBill(listCart);
        linearLayoutManager = new LinearLayoutManager(context);
        holder.rvItemOrder.setLayoutManager(linearLayoutManager);
        holder.rvItemOrder.setAdapter(adapterItemBill);
        holder.tvidBill.setText("Mã HD :"+ bill.getIdBill());
        holder.tvTimeOut.setText("Thời gian: "+bill.getTimeOut());
        holder.tvDayOut.setText(String.valueOf(bill.getDayOut()));
        NumberFormat numberFormat = new DecimalFormat("#,##0");
        holder.tvTotal.setText(numberFormat.format(bill.getTotal()));
        holder.linearLayout_itemProducts.setOnClickListener(view -> {
            if (holder.rvItemOrder.getVisibility() == View.GONE){
                holder.rvItemOrder.setVisibility(View.VISIBLE);
                holder.img_dropDown.setImageResource(R.drawable.ic_arrow_drop_up);
            }else {
                holder.rvItemOrder.setVisibility(View.GONE);
                holder.img_dropDown.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvidBill, tvTotal, tvDayOut, tvTimeOut;
        private LinearLayout linearLayout_itemProducts;
        private ImageView img_dropDown;
        private RecyclerView rvItemOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvidBill = itemView.findViewById(R.id.tv_ItemStatistical_idBill);
            tvTotal = itemView.findViewById(R.id.tv_ItemStatistical_totalOrder);
            tvDayOut = itemView.findViewById(R.id.tv_ItemStatistical_dayOut);
            tvTimeOut = itemView.findViewById(R.id.tv_ItemStatistical_timeOut);
            linearLayout_itemProducts = itemView.findViewById(R.id.linearLayout_ItemStatistical_itemProducts);
            img_dropDown = itemView.findViewById(R.id.img_ItemStatistical_dropDown);
            rvItemOrder = itemView.findViewById(R.id.recyclerView_ItemStatistical_products);
        }
    }
    private List<Cart> getAllCart(int i) {
        List<Cart> listCart = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        reference.child(""+list.get(i).getIdBill()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCart.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Cart cart = snap.getValue(Cart.class);
                    listCart.add(cart);
                }
                adapterItemBill.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listCart;
    }
}
