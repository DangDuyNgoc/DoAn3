package com.example.thucphamxanh2.Fragment.Bill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.databinding.FragmentBillBinding;

public class BillFragment extends Fragment {

    private FragmentBillBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBillBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Fragment newFragment = new OrderFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_bill, newFragment);
        transaction.commit();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}