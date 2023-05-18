package com.example.thucphamxanh2.Activity;


import static com.example.thucphamxanh2.constant.Profile.FIELDS_EMPTY;
import static com.example.thucphamxanh2.constant.Profile.NUMBER_PHONE_INVALID;
import static com.example.thucphamxanh2.constant.Profile.PASSWORD_INVALID;
import static com.example.thucphamxanh2.constant.Profile.PASSWORD_NOT_MATCH;
import static com.example.thucphamxanh2.constant.Profile.REGEX_PHONE_NUMBER;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.thucphamxanh2.Model.User;
import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements LocationListener {
    public static final String TAG = "SignUpActivity";
    private TextInputLayout mFormPhoneNumber,
            mFormUserName,
            mFormPassword,
            mFormConfirmPassword,
            mFormAddress;
    private Button mBtnSignUp;
    private ImageView gpsBtn;
    private TextView loginTv;

    private LocationManager locationManager;

    private static final int LOCATION_REQUEST_CODE = 100;

    private String[] locationPermission;

    private double latitude, longitude;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUI();
        firebaseAuth = FirebaseAuth.getInstance();
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        getSupportActionBar().hide(); // Ẩn actionbar
    }

    private void initUI() {
        mFormPhoneNumber = findViewById(R.id.form_SignUpActivity_phone_number);
        mFormUserName = findViewById(R.id.form_SignUpActivity_user_name);
        mFormPassword = findViewById(R.id.form_SignUpActivity_password);
        mFormConfirmPassword = findViewById(R.id.form_SignUpActivity_confirmPassword);
        mFormAddress = findViewById(R.id.form_SignUpActivity_address);
        mBtnSignUp = findViewById(R.id.btn_SignUpActivity_signUp);
        gpsBtn = findViewById(R.id.gpsBtn);
        loginTv = findViewById(R.id.already_user);
        setOnclickListener();
    }

    private void setOnclickListener() {
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermission, LOCATION_REQUEST_CODE);
    }

    private void detectLocation() {
        try {
            Toast.makeText(this, "Please wait....", Toast.LENGTH_LONG).show();
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_SignUpActivity_signUp:
//                signUp();
//                break;
//        }
//    }

    private void signUp() {
        final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        mFormPhoneNumber.setError(null);
        mFormPassword.setError(null);
        mFormConfirmPassword.setError(null);
        mFormUserName.setError(null);
        mFormAddress.setError(null);
        String strPhoneNumber = Objects.requireNonNull(mFormPhoneNumber.getEditText()).getText().toString().trim();
        String strUserName = mFormUserName.getEditText().getText().toString().trim();
        String strPassword = mFormPassword.getEditText().getText().toString().trim();
        String strConfirmPassword = mFormConfirmPassword.getEditText().getText().toString().trim();
        String strAddress = mFormAddress.getEditText().getText().toString().trim();
        try {
            validate(strPhoneNumber,
                    strUserName,
                    strPassword,
                    strConfirmPassword,
                    strAddress);
            User user = new User();
            user.setPhoneNumber(strPhoneNumber);
            user.setName(strUserName);
            user.setPassword(strPassword);
            user.setAddress(strAddress);
            user.setStrUriAvatar("");
            user.setId(strPhoneNumber);
            ProgressDialog progressDialog = Utils.createProgressDiaglog(SignUpActivity.this);
            progressDialog.show();
            rootReference.child("User").child(strPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Map<String, Object> userDataMap = user.toMap();
                        rootReference.child("User")
                                .child(strPhoneNumber)
                                .setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this
                                                        , "Tạo tài khoản thành công"
                                                        , Toast.LENGTH_SHORT)
                                                .show();
                                        remember(strPhoneNumber,strPassword,"user",strPhoneNumber);
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finishAffinity();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this
                                                        , "Tạo tài khoản thất bại"
                                                        , Toast.LENGTH_LONG)
                                                .show();
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        //TODO thông báo số điện thoại đã tồn tại tới view
                        progressDialog.dismiss();
                        mFormPhoneNumber.setError("Số điện thoại đã tồn tại");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignUpActivity.this
                                    , "Có lỗi khi tạo tài khoản, vui lòng thử lại sau"
                                    , Toast.LENGTH_LONG)
                            .show();
                    Log.e(TAG, "onCancelled: ",error.toException() );
                }
            });
        } catch (NullPointerException e) {
            if (e.getMessage().equals(FIELDS_EMPTY)) {
                setErrorEmpty();
                //TODO thông báo lỗi khi empty
            } else {
                Log.e(TAG, "signUp: ", e);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(NUMBER_PHONE_INVALID)) {
                mFormPhoneNumber.setError("Số điện thoại không hợp lệ");
            } else if (e.getMessage().equals(PASSWORD_INVALID)) {
                mFormPassword.setError("Mật khẩu không hợp lệ");
            } else if (e.getMessage().equals(PASSWORD_NOT_MATCH)) {
                mFormConfirmPassword.setError("Mật khẩu không trùng nhau");
            } else {
                Log.e(TAG, "signUp: ", e);
            }
        } catch (Exception e) {
            //TODO ngoại lệ gì đó chưa bắt được
            Log.e(TAG, "signUp: ", e);
        }

        /*try {
            validate(strPhoneNumber,
                    strUserName,
                    strPassword,
                    strConfirmPassword,
                    strAddress);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            mProgressDialog.show();
            auth.createUserWithEmailAndPassword(strPhoneNumber, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser userAuth = auth.getCurrentUser();
                                User user = new User();
                                user.setEmail(mEtPhoneNumber.getText().toString());
                                user.setPassword(mEtPassword.getText().toString());
                                user.setId(userAuth.getUid());
                                writeNewUser(user);
                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                //
                                startActivity(intent);
                                finishAffinity();
//                            updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }
                        }

                        private void writeNewUser(User user) {
                            DatabaseReference databaseReference;
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference
                                    .child("users")
                                    .child(user.getId())
                                    .child("id")
                                    .setValue(user.getId());
                            databaseReference
                                    .child("users")
                                    .child(user.getId())
                                    .child("email")
                                    .setValue(user.getEmail());
                            databaseReference
                                    .child("users")
                                    .child(user.getId())
                                    .child("role")
                                    .setValue("customer");
                        }
                    });

        } catch (NullPointerException e) {
            if (e.getMessage().equals(FIELDS_EMPTY)) {
                //TODO thông báo lỗi khi empty
            } else {
                Log.e(TAG, "signUp: ", e);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(NUMBER_PHONE_INVALID)) {
                //TODO thông báo lỗi khi số điện thoại không đúng định dạng
            } else if (e.getMessage().equals(PASSWORD_INVALID)) {
                //TODO thông báo lỗi khi mật khẩu không hợp lệ
            } else if (e.getMessage().equals(PASSWORD_NOT_MATCH)) {
                //TODO thông báo lỗi số điện thoại không trùng nhau
            } else {
                Log.e(TAG, "signUp: ", e);
            }
        } catch (Exception e) {
            //TODO ngoại lệ gì đó chưa bắt được
            Log.e(TAG, "signUp: ", e);
        }*/

    }

    private void setErrorEmpty() {
        if (mFormPhoneNumber.getEditText().getText().toString().isEmpty()) {
            mFormPhoneNumber.setError("Số điện thoại không được để trống");
        }
        if (mFormUserName.getEditText().getText().toString().isEmpty()) {
            mFormUserName.setError("Họ tên không được để trống");
        }
        if (mFormPassword.getEditText().getText().toString().isEmpty()) {
            mFormPassword.setError("Mật khẩu không được để trống");
        }
        if (mFormConfirmPassword.getEditText().getText().toString().isEmpty()) {
            mFormConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
        }
        if (mFormAddress.getEditText().getText().toString().isEmpty()) {
            mFormAddress.setError("Địa chỉ không được để trống");
        }

    }

    private void validate(String strPhoneNumber,
                          String strUserName,
                          String strPassword,
                          String strConfirmPassword,
                          String strAddress) {
        if (strPhoneNumber.isEmpty()
                || strPassword.isEmpty()
                || strConfirmPassword.isEmpty()
                || strUserName.isEmpty()
                || strAddress.isEmpty())
            throw new NullPointerException(FIELDS_EMPTY);
        if (strPhoneNumber.matches(REGEX_PHONE_NUMBER))
            throw  new IllegalArgumentException(NUMBER_PHONE_INVALID);
        if (strPassword.length() < 6)
            throw  new IllegalArgumentException(PASSWORD_INVALID);
        if (!strConfirmPassword.equals(strPassword))
            throw new IllegalArgumentException(PASSWORD_NOT_MATCH);
    }
    public void remember(String user,String password,String role, String id){
        SharedPreferences sharedPreferences = getSharedPreferences("My_User",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user);
        editor.putString("password", password);
        editor.putString("role", role);
        editor.putString("id", id);
        editor.putBoolean("logged",true);
        editor.putBoolean("remember", true);
        editor.apply();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);

//            set address
            mFormAddress.getEditText().setText(address);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Please turn on location....", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccept) {
                        detectLocation();
                    } else {
                        Toast.makeText(this, "Location Permission is necessary.... ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}