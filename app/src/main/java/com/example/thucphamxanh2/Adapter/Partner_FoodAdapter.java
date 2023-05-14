package com.example.thucphamxanh2.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.R;

import java.util.Base64;
import java.util.List;

public class Partner_FoodAdapter extends RecyclerView.Adapter<Partner_FoodAdapter.ViewHolder> {
    private ItemClickListener itemClickListener;
    List<Partner> list;
    public Partner_FoodAdapter(List<Partner> list,ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_food,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Partner partner = list.get(position);
        holder.namePartner_food.setText(partner.getNamePartner());
        holder.addressPartner_food.setText(partner.getAddressPartner());

        byte[] imgByte = Base64.getDecoder().decode(partner.getImgPartner());
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
        holder.imgPartner_food.setImageBitmap(bitmap);

//        holder.cardView_partner_food.setOnClickListener(view -> {
////            Intent intent = new Intent(context, Food_Of_Partner_Activity.class);
////            context.startActivity(intent);
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(list.get(position));
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
        CardView cardView_partner_food;
        TextView namePartner_food, addressPartner_food;
        ImageView imgPartner_food;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView_partner_food = itemView.findViewById(R.id.cardView_partner_food);
            namePartner_food = itemView.findViewById(R.id.namePartner_food);
            addressPartner_food = itemView.findViewById(R.id.addressPartner_food);
            imgPartner_food = itemView.findViewById(R.id.imgPartner_food);



        }
    }
    public interface ItemClickListener{
        public void onItemClick(Partner partner);
    }

}
