package com.example.thucphamxanh2.Fragment.BottomNav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.thucphamxanh2.Activity.SignInActivity;
import com.example.thucphamxanh2.Fragment.Profile.ProfileFragment;
import com.example.thucphamxanh2.Fragment.Profile.ProfileViewModel;
import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.Model.User;
import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.databinding.FragmentPersonalBinding;

import java.util.ArrayList;
import java.util.List;

public class PersonalFragment extends Fragment {
    private static final String TAG = "PersonalFragment";
    FragmentPersonalBinding binding;
    Button btn_logout_personal, btn_changepassword_personal,btn_login;
    TextView tvName, tvPhone;
    ImageView imgUser;
    List<User> listUser = new ArrayList<>();
    ProfileViewModel profileViewModel;
    CardView itemPerson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        itemPerson = binding.itemPerson;
        btn_logout_personal = binding.btnPersonalFragmentLogoutPersonal;
        btn_login = binding.btnPersonalFragmentLogin;
        btn_changepassword_personal = binding.btnPersonalFragmentChangePasswordPersonal;
        tvName = binding.tvPersonalFragmentNameUser;
        tvPhone = binding.tvPersonalFragmentPhoneUser;
        imgUser = binding.imgPersonalFragmentImgUser;
        imgUser.setImageResource(R.drawable.profile);

        if (user.equals("")){
            btn_logout_personal.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
            itemPerson.setEnabled(false);
            imgUser.setImageResource(R.drawable.profile);
            btn_changepassword_personal.setVisibility(View.GONE);
        }else{
            btn_login.setVisibility(View.GONE);
            btn_logout_personal.setVisibility(View.VISIBLE);
            itemPerson.setEnabled(true);
        }
        Log.d(TAG, "onCreateView: ");
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        btn_login.setOnClickListener(view ->{
            startActivity(new Intent(getContext(),SignInActivity.class));
        });

        btn_logout_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                remember();
                getActivity().finishAffinity();
            }

        });

        btn_changepassword_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new ChangePasswordFragment()).addToBackStack(null).commit();
            }
        });

        itemPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new ProfileFragment()).addToBackStack(null).commit();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = profileViewModel.getUser().getValue();
        Partner partner = profileViewModel.getPartner().getValue();
        if (user != null) {
            profileViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    Log.d("TAG", "onChanged: ");
                    tvName.setText(user.getName());
                    tvPhone.setText(user.getId());
                    Glide.with(requireActivity()).load(user.getStrUriAvatar()).error(R.drawable.profile).into(imgUser);
                }
            });
        } else if (partner != null) {
            profileViewModel.getPartner().observe(getViewLifecycleOwner(), new Observer<Partner>() {
                @Override
                public void onChanged(Partner partner) {
                    Log.d("TAG", "onChanged: ");
                    try {
                        byte[] decodeString = Base64.decode(partner.getImgPartner(), Base64.DEFAULT);
                        tvPhone.setText(partner.getUserPartner() + "\n" + partner.getNamePartner());
                        Glide.with(requireActivity())
                                .load(decodeString)
                                .signature(new ObjectKey(Long.toString(System.currentTimeMillis())))
                                .error(R.drawable.profile)
                                .into(imgUser);
                    } catch (Exception e) {
                        Log.e(TAG, "onChanged: ", e);
                    }
                }
            });
        }
        Log.d(TAG, "onViewCreated: " + user);
        profileViewModel.getUser().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: ");
            }
        });
    }
    public void remember(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("My_User",getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("role", "");
        editor.putString("id", "");
        editor.putBoolean("remember", false);
        editor.apply();
    }

}