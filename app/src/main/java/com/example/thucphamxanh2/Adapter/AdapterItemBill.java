
        package com.example.thucphamxanh2.Adapter;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.thucphamxanh2.Model.Cart;
        import com.example.thucphamxanh2.R;

        import java.util.List;

public class AdapterItemBill extends RecyclerView.Adapter<AdapterItemBill.viewHolder> {
    private List<Cart> list;

    public AdapterItemBill(List<Cart> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Cart cart = list.get(position);
        holder.tvName.setText(cart.getNameProduct());
        holder.tvAmount.setText(String.valueOf(cart.getNumberProduct()));
        holder.tvTotal.setText(String.valueOf(cart.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView tvName,tvAmount,tvTotal;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name_product_itemOrder);
            tvAmount = itemView.findViewById(R.id.amount_product_itemOrder);
            tvTotal = itemView.findViewById(R.id.total_product_itemOrder);
        }
    }
}