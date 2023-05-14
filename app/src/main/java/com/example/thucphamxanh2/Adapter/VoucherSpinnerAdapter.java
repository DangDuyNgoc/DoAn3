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

public class VoucherSpinnerAdapter extends ArrayAdapter<Voucher> {
    Context context;
    List<Voucher> arrayList;
    TextView tvCodeVoucher,idVoucher;


    public VoucherSpinnerAdapter(@NonNull Context context, List<Voucher> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_voucher,null);
            tvCodeVoucher = convertView.findViewById(R.id.tvCodeVoucher);
            idVoucher = convertView.findViewById(R.id.idVoucher);
        }
        tvCodeVoucher.setText(String.valueOf(arrayList.get(position).getCodeVoucher()));
        idVoucher.setText(String.valueOf(arrayList.get(position).getIdVoucher()));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_voucher,null);
            tvCodeVoucher = convertView.findViewById(R.id.tvCodeVoucher);
            idVoucher = convertView.findViewById(R.id.idVoucher);
        }
        tvCodeVoucher.setText(String.valueOf(arrayList.get(position).getCodeVoucher()));
        idVoucher.setText(String.valueOf(arrayList.get(position).getIdVoucher()));
        return convertView;

    }
}
