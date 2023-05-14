
            package com.example.thucphamxanh2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.Model.Cart;
import com.example.thucphamxanh2.Model.User;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

    public class AdapterBill extends RecyclerView.Adapter<AdapterBill.viewHolder> {
        private List<Bill> list;
        private Context context;
        AdapterItemBill adapterItemBill;
        LinearLayoutManager linearLayoutManager;
        public AdapterBill(List<Bill> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill,parent,false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            SharedPreferences preferences = context.getSharedPreferences("My_User",Context.MODE_PRIVATE);
            String role = preferences.getString("role","");
            Bill bill = list.get(position);
            List<Cart> listCart = getAllCart(position);
            getUser(bill.getIdClient(),holder.tvNameClient);
            adapterItemBill = new AdapterItemBill(listCart);
            linearLayoutManager = new LinearLayoutManager(context);
            holder.rvItemOrder.setLayoutManager(linearLayoutManager);
            holder.rvItemOrder.setAdapter(adapterItemBill);
            holder.tvidBill.setText("Mã HD :"+ bill.getIdBill());
            holder.tvPhone.setText("Số điện thoại : "+bill.getIdClient());
            holder.tvTime.setText("Thời gian: "+bill.getTimeOut());
            holder.tvDay.setText(String.valueOf(bill.getDayOut()));
            holder.tvTotal.setText(String.valueOf(bill.getTotal()));
            holder.linearLayout_item_product.setOnClickListener(view -> {
                if (holder.rvItemOrder.getVisibility() == View.GONE){
                    holder.rvItemOrder.setVisibility(View.VISIBLE);
                    holder.img_drop_up.setImageResource(R.drawable.ic_arrow_drop_down);
                }else {
                    holder.rvItemOrder.setVisibility(View.GONE);
                    holder.img_drop_up.setImageResource(R.drawable.ic_arrow_drop_up);
                }
            });
            holder.card_bill.setOnClickListener(view -> {
                if (!role.equals("user")) {
                    if (holder.btn_updateStatusBill.getVisibility() == View.GONE) {
                        holder.btn_updateStatusBill.setVisibility(View.VISIBLE);
                    } else {
                        holder.btn_updateStatusBill.setVisibility(View.GONE);
                    }
                }
                if (bill.getStatus().equals("Yes")){
                    holder.btn_updateStatusBill.setVisibility(View.GONE);
                }
            });
            holder.btn_updateStatusBill.setOnClickListener(view -> {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Bill");
                reference.child(bill.getIdBill()+"/status").setValue("Yes");
            });
        }



        @Override
        public int getItemCount() {
            if (list!=null){
                return list.size();
            }
            return 0;
        }

        public class viewHolder extends RecyclerView.ViewHolder{
            private TextView tvidBill,tvNameClient,tvTotal, tvTime, tvDay,tvPhone;
            private LinearLayout linearLayout_item_product;
            private ImageView img_drop_up;
            private Button btn_updateStatusBill;
            private RecyclerView rvItemOrder;
            private CardView card_bill;

            public viewHolder(@NonNull View itemView) {
                super(itemView);
                tvidBill = itemView.findViewById(R.id.tv_idBill_item);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                tvTotal = itemView.findViewById(R.id.tv_totalOrder_item);
                linearLayout_item_product = itemView.findViewById(R.id.linear_layout_item_product);
                img_drop_up = itemView.findViewById(R.id.img_drop_up);
                btn_updateStatusBill = itemView.findViewById(R.id.btn_updateStatusBill_item);
                rvItemOrder = itemView.findViewById(R.id.rv_order);
                card_bill = itemView.findViewById(R.id.card_bill);
                tvTime = itemView.findViewById(R.id.tv_time_item);
                tvDay = itemView.findViewById(R.id.tv_day_item);
                tvNameClient = itemView.findViewById(R.id.tv_name_client_item);
            }
        }
        private List<Cart> getAllCart(int i) {
            List<Cart> list1 = new ArrayList<>();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Bill");
            reference.child(""+list.get(i).getIdBill()).child("Cart").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list1.clear();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Cart cart = snap.getValue(Cart.class);
                        list1.add(cart);
                    }
                    adapterItemBill.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return list1;
        }
        private void getUser(String userId,TextView tvName) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("User");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        if (userId.equals(user.getId())){
                            tvName.setText("Tên khách hàng:"+ user.getName());
                        }

                    }
                    adapterItemBill.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

