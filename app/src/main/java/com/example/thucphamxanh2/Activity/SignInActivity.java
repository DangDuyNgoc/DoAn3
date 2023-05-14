package com.example.thucphamxanh2.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.constant.Profile;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "SignInActivity";
    private LinearLayout layoutSignUp;
    private TextInputLayout formEmail, formPassword;
    private Button btnSignIn;
    private ProgressBar progressBar;
    private List<Partner> list;
    private CheckBox mChkRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUI();
        getDataSpf();

        list = getAllPartner();
        Log.d(TAG, "onCreate: " + list.toString());
        getSupportActionBar().hide();
    }

    private void setOnclickListener() {
        layoutSignUp.setOnClickListener(this::onClick);
        btnSignIn.setOnClickListener(this::onClick);
    }


    private void initUI() {
        layoutSignUp = findViewById(R.id.layout_SignInActivity_signIn);
        btnSignIn = findViewById(R.id.btn_SignInActivity_signIn);
        progressBar = findViewById(R.id.progressBar_SignInActivity_loadingLogin);
        progressBar.setVisibility(View.INVISIBLE);
        formEmail = findViewById(R.id.form_SignInActivity_email);
        formPassword = findViewById(R.id.form_SignInActivity_password);
        formPassword.setErrorEnabled(true);
        formEmail.setErrorEnabled(true);
        setOnclickListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_SignInActivity_signIn:
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_SignInActivity_signIn:
                if (!logins()){
                    userLogin();
                }
                logins();
                break;
            default:
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void userLogin() {
        formEmail.setError(null);
        formPassword.setError(null);
        String phoneNumber = formEmail.getEditText().getText().toString().trim();
        String passwordUser = formPassword.getEditText().getText().toString().trim();
        if (!validate(phoneNumber, passwordUser)) return;
        progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("User").child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (snapshot.exists()) {
                            String password = snapshot.child("password").getValue(String.class);
                            if (password.equals(passwordUser)) {
                                //TODO ĐĂNG NHẬP VÀO APP
                                remember("user", phoneNumber);
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                return;
                            }
                            //TODO THÔNG BÁO MẬT KHẨU KHÔNG ĐÚNG
                            formPassword.setError("Mật khẩu không đúng");
                            return;
                        }
                        //TODO THÔNG BÁO TÀI KHOẢN CHƯA TỒN TẠI
                        formEmail.setError("Tài khoản không tồn tại");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //TODO THÔNG BÁO LỖI KHI ĐĂNG NHẬP
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "onCancelled: ", error.toException());
                    }
                });
    }


    public boolean logins(){
        //TODO validate partner login
        String email = formEmail.getEditText().getText().toString().trim();
        String password = formPassword.getEditText().getText().toString().trim();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserPartner().equals(email) && list.get(i).getPasswordPartner().equals(password)){
//                remember("partner", String.valueOf(list.get(i).getIdPartner()));
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            }
        }
        if (email.equals("admin") && password.equals("admin") ){
            remember("admin", "admin");
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
            return true;
        }
        return false;
    }

    public void remember(String role, String id){
        String email = formEmail.getEditText().getText().toString().trim();
        String password = formPassword.getEditText().getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("My_User",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", email);
        editor.putString("password", password);
        editor.putString("role", role);
        editor.putString("id", id);
//        editor.putBoolean("remember", mChkRemember.isChecked());
        editor.apply();
    }

    public void getDataSpf(){
        SharedPreferences sharedPreferences = getSharedPreferences("My_User",MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("remember",false);
        if (isRemember) {
            formEmail.getEditText().setText(sharedPreferences.getString("username",""));
            formPassword.getEditText().setText(sharedPreferences.getString("password",""));
        }
    }

    private boolean validate(String email, String password) {
        try {
            if (email.isEmpty() && password.isEmpty()) throw new IllegalArgumentException("email and password is empty");
            else if (email.isEmpty()) throw new IllegalArgumentException("email is empty");
            else if (password.isEmpty()) throw new IllegalArgumentException("password is empty");
            else if (password.length() < 6) throw new IllegalArgumentException(Profile.PASSWORD_INVALID);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("email and password is empty")) {
                formEmail.setError("Không được bỏ trống số điện thoại");
                formPassword.setError("Không được bỏ trống mật khẩu");
            } else if (e.getMessage().equals("email is empty")) {
                formEmail.setError("Không được bỏ trống số điện thoại");
            } else if (e.getMessage().equals("password is empty")) {
                formPassword.setError("Không được bỏ trống mật khẩu");
            } else if (e.getMessage().equals(Profile.PASSWORD_INVALID)) {
                formPassword.setError("Mật khẩu phải từ 6 kí tự trở lên");
            }
            Log.e(TAG, "validate: ", e);
            return false;
        }
        return true;

    }
    public List<Partner> getAllPartner(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Partner");
        List<Partner> list1 = new ArrayList<>();
        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Partner partner = snap.getValue(Partner.class);
                    list1.add(partner);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
        return list1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: đang stop");
    }
}