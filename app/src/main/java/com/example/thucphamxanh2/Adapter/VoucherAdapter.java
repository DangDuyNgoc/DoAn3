package com.example.thucphamxanh2.Adapter;

import android.content.Context;
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

import com.example.thucphamxanh2.Model.Voucher;
import com.example.thucphamxanh2.R;

import java.util.Base64;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private List<Voucher> list;
    private Context context;
    private ItemClickListener itemClickListener;

    public VoucherAdapter(List<Voucher> list,ItemClickListener listener, Context context) {
        this.list = list;
        this.itemClickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher,parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SharedPreferences preferences = context.getSharedPreferences("My_User",Context.MODE_PRIVATE);
        String role = preferences.getString("role","");

        Voucher voucher = list.get(position);
        byte[] imgByte = Base64.getDecoder().decode(voucher.getImgVoucher());
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
        holder.item_imgVoucher.setImageBitmap(bitmap);

        holder.item_codeVoucher.setText(voucher.getCodeVoucher());

        holder.item_cardView_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role.equals("admin")){
                    if (holder.btn_delete_voucher.getVisibility() == View.GONE){
                        holder.btn_delete_voucher.setVisibility(View.VISIBLE);
                    }else {
                        holder.btn_delete_voucher.setVisibility(View.GONE);

                    }
                }


            }
        });

        holder.btn_delete_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClickDeleteVoucher(voucher);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (list != null){
            return  list.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView item_codeVoucher;
        private ImageView item_imgVoucher;
        private CardView item_cardView_voucher;
        Button btn_delete_voucher;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_codeVoucher = itemView.findViewById(R.id.item_codeVoucher);
            item_imgVoucher = itemView.findViewById(R.id.item_imgVoucher);
            item_cardView_voucher = itemView.findViewById(R.id.item_cardView_voucher);
            btn_delete_voucher = itemView.findViewById(R.id.btn_delete_voucher);

        }
    }

    public interface ItemClickListener{
        void onClickDeleteVoucher(Voucher voucher);
    }
}
