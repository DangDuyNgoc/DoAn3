package com.example.thucphamxanh2.Fragment.BottomNav;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thucphamxanh2.Adapter.StatisticalAdapter;
import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.databinding.FragmentStatisticalBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticalFragment extends Fragment {
    private final String TAG = "StatisticalFragment";
    private FragmentStatisticalBinding binding;
    private TextInputLayout fromDate, toDate;
    private Button btnSearch;
    private TextView totalRevenue, tvHide;
    final Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    NumberFormat numberFormat = new DecimalFormat("#,##0");
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StatisticalAdapter adapterStatistical;
    private List<Bill> listBill = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticalBinding.inflate(inflater, container, false);
        getBill();
        initUi();
        getDate();

        return binding.getRoot();
    }

    public void initUi() {
        fromDate = binding.textStatisticalFragmentFromDate;
        toDate = binding.textStatisticalFragmentToDate;
        btnSearch = binding.btnStatisticalFragmentSearch;
        totalRevenue = binding.tvStatisticalFragmentTotalRevenue;
        recyclerView = binding.recyclerViewStatisticalFragmentItemBill;
        adapterStatistical = new StatisticalAdapter(listBill, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterStatistical);
        tvHide = binding.tvStatisticalFragmentHide;
    }

    public void getBill() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        SharedPreferences preferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBill.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Bill bill = snap.getValue(Bill.class);
                    if (user.equals(bill.getIdPartner()) && bill.getStatus().equals("Yes")) {
                        listBill.add(bill);
                    }
                    int sum = 0;
                    for (int i = 0; i < listBill.size(); i++) {
                        sum += listBill.get(i).getTotal();
                    }
                    totalRevenue.setText(numberFormat.format(sum));
                }
                adapterStatistical.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDate() {
        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                fromDate.getEditText().setText(sdf.format(myCalendar.getTime()));
            }
        };

        fromDate.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), startDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                toDate.getEditText().setText(sdf.format(myCalendar.getTime()));
            }
        };

        toDate.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), endDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSearch.setOnClickListener(view -> {
            SharedPreferences preferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
            String user = preferences.getString("username", "");
            recyclerView.setVisibility(View.GONE);
            tvHide.setVisibility(View.GONE);
            String startdate = fromDate.getEditText().getText().toString();
            String todate = toDate.getEditText().getText().toString();

            if (startdate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            } else if (todate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ngày kết thúc", Toast.LENGTH_SHORT).show();
            } else {
                final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference("Bill");
                rootReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int total = 0;
                        List<Bill> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Bill bill = dataSnapshot.getValue(Bill.class);
                            if (user.equals(bill.getIdPartner()) && bill.getStatus().equals("Yes")) {
                                try {
                                    Date dayOut = sdf.parse(bill.getDayOut());
                                    Date startDate = sdf.parse(startdate);
                                    Date toDate = sdf.parse(todate);
                                    if (dayOut.compareTo(startDate) >= 0 && dayOut.compareTo(toDate) <= 0) {
                                        total += bill.getTotal();
                                        list.add(bill);
                                        adapterStatistical = new StatisticalAdapter(list, getContext());
                                        recyclerView.setAdapter(adapterStatistical);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvHide.setVisibility(View.VISIBLE);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        totalRevenue.setText(numberFormat.format(total));
                        adapterStatistical.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: ", error.toException());
                    }
                });
            }
        });
    }
}