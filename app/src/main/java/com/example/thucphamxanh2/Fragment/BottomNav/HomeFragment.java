package com.example.thucphamxanh2.Fragment.BottomNav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thucphamxanh2.Fragment.Bill.OrderFragment;
import com.example.thucphamxanh2.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    FrameLayout frame_Home;
    BottomNavigationView bottom_nav_Home;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);

        frame_Home = view.findViewById(R.id.frame_Home);
        bottom_nav_Home = view.findViewById(R.id.bottom_nav_Home);

        replaceFragment(new HomePageFragment());

        bottom_nav_Home.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    replaceFragment(new HomePageFragment());
                    break;
//                case R.id.action_voucher:
//                    replaceFragment(new VoucherFragment());
//                    break;
                case R.id.action_order:
                    replaceFragment(new OrderFragment());
                    break;
                case R.id.action_personal:
                    replaceFragment(new PersonalFragment());
                    break;
            }

            return true;
        });
        return  view;

    }

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_Home,fragment);
        fragmentTransaction.commit();
    }

}