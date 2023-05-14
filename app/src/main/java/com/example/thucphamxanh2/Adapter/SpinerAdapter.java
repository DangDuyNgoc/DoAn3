package com.example.thucphamxanh2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thucphamxanh2.Model.Voucher;
import com.example.thucphamxanh2.R;

import java.util.List;

public class SpinerAdapter extends ArrayAdapter<Voucher> {
    private Context context;
    private List<Voucher> list;
    TextView name, id;
    public SpinerAdapter(@NonNull Context context, List<Voucher> list) {
        super(context, 0,list);
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_voucher,null);
            name = convertView.findViewById(R.id.tvCodeVoucher);
            id = convertView.findViewById(R.id.idVoucher);
        }
        name.setText(String.valueOf(list.get(position).getCodeVoucher()));
        id.setText(String.valueOf(list.get(position).getIdVoucher()));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_voucher,null);
            name = convertView.findViewById(R.id.tvCodeVoucher);
            id = convertView.findViewById(R.id.idVoucher);
        }
        name.setText(String.valueOf(list.get(position).getCodeVoucher()));
        id.setText(String.valueOf(list.get(position).getIdVoucher()));

        return convertView;
    }
}
