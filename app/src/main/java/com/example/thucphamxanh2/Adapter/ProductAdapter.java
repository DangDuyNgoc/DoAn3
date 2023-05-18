package com.example.thucphamxanh2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thucphamxanh2.Activity.SignInActivity;
import com.example.thucphamxanh2.Fragment.ProductFragments.ProductFragment;
import com.example.thucphamxanh2.Model.Cart;
import com.example.thucphamxanh2.Model.Product;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.util.Base64;
import android.widget.Toast;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {
    private List<Product> list;
    private List<Cart> listCart;
    private ProductFragment fragment;
    private Context context;

    public ProductAdapter(List<Product> list, ProductFragment fragment, Context context) {
        this.list = list;
        this.fragment = fragment;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new viewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SharedPreferences preferences = context.getSharedPreferences("My_User", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "");
        String role = preferences.getString("role", "");

        Product product = list.get(position);
        NumberFormat numberFormat = new DecimalFormat("#,##0");
        listCart = getAllCart();
        byte[] imgByte = Base64.decode(product.getImgProduct(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        holder.imgProduct.setImageBitmap(bitmap);
        holder.tvNameProduct.setText(String.valueOf(product.getNameProduct()));
        holder.tvProductDiscount.setText(String.valueOf(product.getProductDiscount()));
        holder.tvPriceProduct.setText(numberFormat.format(product.getPriceProduct()) + " đ");
        holder.cardProduct.setOnClickListener(view -> {
            if (role.equals("admin") || role.equals("partner")) {
                holder.btn_addCart.setVisibility(View.GONE);
                if (holder.btnUpdateProduct.getVisibility() == View.VISIBLE || holder.btnDeleteProduct.getVisibility() == View.VISIBLE) {
                    holder.btnUpdateProduct.setVisibility(View.GONE);
                    holder.btnDeleteProduct.setVisibility(View.GONE);
                } else {
                    holder.btn_addCart.setVisibility(View.GONE);
                    holder.btnUpdateProduct.setVisibility(View.VISIBLE);
                    holder.btnDeleteProduct.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.btnUpdateProduct.setOnClickListener(view -> {
            fragment.dialogProduct(product, 1, context);
        });
        holder.btnDeleteProduct.setOnClickListener(view -> {
            showDialogDelete(product);
        });

        holder.btn_addCart.setOnClickListener(view -> {
            if (!user.equals("")) {
                StringBuilder str = new StringBuilder();
                Cart cart = new Cart();
                cart.setUserClient(user);
                cart.setIdCategory(product.getCodeCategory());
                cart.setIdProduct(product.getCodeProduct());
                cart.setImgProduct(product.getImgProduct());
                cart.setNameProduct(product.getNameProduct());
                cart.setPriceProduct(product.getPriceProduct());
                cart.setProductDiscount(product.getProductDiscount());
                cart.setNumberProduct(1);
                cart.setIdPartner(product.getUserPartner());
                cart.setTotalPrice(cart.getPriceProduct() * cart.getNumberProduct());
                for (int i = 0; i < listCart.size(); i++) {
                    if (!listCart.get(i).getIdPartner().equals(cart.getIdPartner())) {
                        str.append("1");
                    }
                }
                if (str.length() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Nếu bạn thêm sản phẩm ở cửa hàng này, sản phẩm ở cửa hàng khác sẽ bị xóa");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int j = 0; j < listCart.size(); j++) {
                                if (user.equals(listCart.get(j).getUserClient())) {
                                    deleteCart(listCart.get(j));
                                }
                            }
                            addProductCart(cart);
                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    addProductCart(cart);
                }
            } else {
                showDialogLogin();
            }
        });
    }

    private void deleteCart(Cart cart) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Cart");
        reference.child("" + cart.getIdCart()).removeValue();
    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView tvNameProduct, tvPriceProduct, tvProductDiscount;
        private ImageView imgProduct;
        private CardView cardProduct;
        private Button btnUpdateProduct, btnDeleteProduct;
        private Button btn_addCart;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct_item);
            tvPriceProduct = itemView.findViewById(R.id.tvPriceProduct_item);
            imgProduct = itemView.findViewById(R.id.imgProduct_item);
            tvProductDiscount = itemView.findViewById(R.id.product_discount);
            cardProduct = itemView.findViewById(R.id.cardProduct);
            btnUpdateProduct = itemView.findViewById(R.id.btn_updateProduct_item);
            btnDeleteProduct = itemView.findViewById(R.id.btn_deleteProduct_item);
            btn_addCart = itemView.findViewById(R.id.btn_addCart_item);
        }

    }

    public void addProductCart(Cart cart) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Cart");
        if (listCart.size() == 0) {
            cart.setIdCart(1);
            reference.child("1").setValue(cart);
            Toast.makeText(context, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();

        } else {
            int i = listCart.size() - 1;
            int id = listCart.get(i).getIdCart() + 1;
            cart.setIdCart(id);
            reference.child("" + id).setValue(cart);
            Toast.makeText(context, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }

    }

    public List<Cart> getAllCart() {
        SharedPreferences preferences = context.getSharedPreferences("My_User", context.MODE_PRIVATE);
        String user = preferences.getString("username", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Cart");
        List<Cart> list1 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Cart cart = snap.getValue(Cart.class);
                    if (cart != null) {
                        if (cart.getUserClient().equals(user)) {
                            list1.add(cart);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }

    private void showDialogDelete(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có chắc muốn xóa sản phẩm");
        builder.setNegativeButton("Xoá", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragment.deleteProduct(product);
            }
        });
        builder.setPositiveButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn phải đăng nhập");
        builder.setNegativeButton("Đăng Nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, SignInActivity.class);
                context.startActivity(intent);
            }
        });
        builder.setPositiveButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
