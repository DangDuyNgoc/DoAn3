package com.example.thucphamxanh2.Fragment.Profile;

import static com.example.thucphamxanh2.Activity.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.thucphamxanh2.Activity.MainActivity;
import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.Model.User;
import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment implements LocationListener {
    private static final String TAG = "ProfileFragment";
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mStorage.getReference();
    private User user;
    private Partner partner;

    private TextInputLayout mLayoutName, mLayoutEmail, mLayoutAddress, mLayoutPhoneNumber;
    private Button btnUpdateInfoUser;
    private ImageView ivAvatar, backBtn, gpsBtn;

    private LocationManager locationManager;

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] locationPermission;
    private String[] cameraPermission;
    private String[] storagePermission;

    private double latitude, longitude;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: start");
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        user = profileViewModel.getUser().getValue();
        partner = profileViewModel.getPartner().getValue();
        Log.d(TAG, "onCreate: " + user + "\n" + partner);
        Log.d(TAG, "onCreate: end");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initUI();
//        setUserInfoToView();
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        Log.d(TAG, "onCreateView: end");
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initListener();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnUpdateInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null) updateUserInfo();
                else if (partner != null) updatePartnerInfo();
                else Log.d(TAG, "onClick: không có đối tượng để update");
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

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        if (user != null) showUserInformation();
        else if (partner != null) showPartnerInformation();
//        ivAvatar.setImageBitmap(user.getBitmapAvatar());
    }

    private void showImagePickedDialog() {
        String[] options = {"Camera", "Gallery"};

//        dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image ")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
//                            camera click
                            if (checkCameraPermission()) {
                                pickFromCamera();
                            } else {
//                                requestCameraPermission();
                            }
                        }
//                        else {
////                            gallery click
//                            if (checkStoragePermission()) {
//                                pickFromGallery();
//                            } else {
//                                requestStoragePermission();
//                            }
//
//                        }
                    }
                }).show();
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp _Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp _Image Description");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), locationPermission, LOCATION_REQUEST_CODE);
    }

    private void detectLocation() {
        try {
            Toast.makeText(getContext(), "Please wait....", Toast.LENGTH_LONG).show();
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void showPartnerInformation() {
        Log.d(TAG, "showPartnerInformation: start");
        profileViewModel.getBitmapLiveData().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                Glide.with(requireActivity())
                        .load(profileViewModel.getBitmapLiveData().getValue())
                        .error(R.drawable.profile)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .into(ivAvatar);
            }
        });
        profileViewModel.getPartner().observe(getViewLifecycleOwner(), new Observer<Partner>() {
            @Override
            public void onChanged(Partner partner) {
                try {
                    mLayoutName.getEditText().setText(partner.getNamePartner());
//                mLayoutEmail.getEditText().setText(String.valueOf(partner.getIdPartner()));
                    mLayoutAddress.getEditText().setText(partner.getAddressPartner());
                    mLayoutPhoneNumber.getEditText().setText(partner.getUserPartner());
                    byte[] decodeString = Base64.decode(partner.getImgPartner(), Base64.DEFAULT);
                    Glide.with(requireActivity()).load(decodeString)
                            .error(R.drawable.profile)
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .into(ivAvatar);
                    Log.d(TAG, "onChanged() returned: " + "ảnh được lấy từ storage về");
//                Glide.with(getActivity()).load(user.getBitmapAvatar()).error(R.drawable.ic_avatar_default).into(ivAvatar);
                    Log.d(TAG, "onChanged: ");
                    Log.d(TAG, "onChanged: " + partner.toString());
                } catch (Exception e) {
                    Log.e(TAG, "onChanged: ", e);
                }

            }
        });
        Log.d(TAG, "showPartnerInformation: end");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void initUI() {
        ivAvatar = binding.getRoot().findViewById(R.id.iv_profile_fragment_avatar);
        btnUpdateInfoUser = binding.getRoot().findViewById(R.id.btn_profile_fragment_update);
        backBtn = binding.getRoot().findViewById(R.id.backBtn);
        gpsBtn = binding.getRoot().findViewById(R.id.gpsBtn);

//        mLayoutEmail = binding.getRoot().findViewById(R.id.text_input_layout_profile_fragment_email);
        mLayoutName = binding.getRoot().findViewById(R.id.text_input_layout_profile_fragment_full_name);
        mLayoutAddress = binding.getRoot().findViewById(R.id.text_input_layout_profile_fragment_address);
        mLayoutPhoneNumber = binding.getRoot().findViewById(R.id.text_input_layout_profile_fragment_phone_number);
    }
//    private void initListener() {
//        ivAvatar.setOnClickListener(this::onClick);
//        btnUpdateInfoUser.setOnClickListener(this::onClick);
//    }
    public void showUserInformation() {
        Log.d(TAG, "showUserInformation: start");
        profileViewModel.getBitmapLiveData().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                Glide.with(requireActivity())
                        .load(profileViewModel.getBitmapLiveData().getValue())
                        .error(R.drawable.profile)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .into(ivAvatar);
            }
        });
        profileViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mLayoutName.getEditText().setText(user.getName());
//                mLayoutEmail.getEditText().setText(user.getEmail());
                mLayoutAddress.getEditText().setText(user.getAddress());
                mLayoutPhoneNumber.getEditText().setText(user.getId());
                if (user.getBitmapAvatar() != null) {
                    Glide.with(requireActivity())
                            .load(user.getBitmapAvatar())
                            .error(R.drawable.profile)
                            .into(ivAvatar);
                    user.setBitmapAvatar(null);
                    Log.d(TAG, "onChanged() returned: " + "ảnh được chọn từ thư mục ảnh");
                } else {
                    Glide.with(requireActivity())
                            .load(user.getStrUriAvatar())
                            .error(R.drawable.profile)
                            .signature(new ObjectKey(Long.toString(System.currentTimeMillis())))
                            .into(ivAvatar);
                    Log.d(TAG, "onChanged() returned: " + "ảnh được lấy từ storage về");
                }
//                Glide.with(getActivity()).load(user.getBitmapAvatar()).error(R.drawable.ic_avatar_default).into(ivAvatar);
                Log.d(TAG, "onChanged: ");
                Log.d(TAG, "onChanged: " + user.toString());
            }
        });
        Log.d(TAG, "showUserInformation: end");
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_profile_fragment_avatar:
//                Log.d(TAG, "onClick: click imageview");
//                onClickRequestPermission();
//                break;
//            case R.id.btn_profile_fragment_update:
//                Log.d(TAG, "onClick: click btn update");
//                if(user != null) updateUserInfo();
//                else if (partner != null) updatePartnerInfo();
//                else Log.d(TAG, "onClick: không có đối tượng để update");
//                break;
//        }
//    }

    private void updatePartnerInfo() {
        Log.d(TAG, "updatePartnerInfo: start");
        partner.setNamePartner(mLayoutName.getEditText().getText().toString());
        partner.setAddressPartner(mLayoutAddress.getEditText().getText().toString());
        partner.setUserPartner(mLayoutPhoneNumber.getEditText().getText().toString());
        Bitmap bitmap = ((BitmapDrawable)ivAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imgByte = outputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imgPartner = java.util.Base64.getEncoder().encodeToString(imgByte);
            partner.setImgPartner(imgPartner);
        }
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> userValue = partner.toMap();
        Map<String, Object> userUpdateValue = new HashMap<>();
        userUpdateValue.put("/Partner/" + partner.getIdPartner(), userValue);
        mDatabase.updateChildren(userUpdateValue).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: ");
                profileViewModel.setPartner(partner);
            }
        });
    }

    private void onClickRequestPermission() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            Log.d(TAG, "onClickRequestPermission: android 6.0");
            return;
        }
        //check version < android 6.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mainActivity.openGallery();
            Log.d(TAG, "onClickRequestPermission: android > 6.0");
            return;
        }
        //check user permission when version >= android 6.0
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mainActivity.openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }
    private void updateUserInfo() {
        Log.d(TAG, "updateUserInfo: start");

        user.setName(mLayoutName.getEditText().getText().toString());
        user.setAddress(mLayoutAddress.getEditText().getText().toString());
        user.setId(mLayoutPhoneNumber.getEditText().getText().toString());
        Bitmap bitmap = ((BitmapDrawable) ivAvatar.getDrawable()).getBitmap();

        StorageReference spaceRef = mStorageReference.child("image/" + user.getId() + "_avatar.jpg");
        ivAvatar.setDrawingCacheEnabled(true);
        ivAvatar.buildDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        user.setStrUriAvatar(uri.toString());
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> userValue = user.toMap();
                        Map<String, Object> userUpdateValue = new HashMap<>();
                        userUpdateValue.put("/User/" + user.getId(), userValue);
                        mDatabase.updateChildren(userUpdateValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();
                                profileViewModel.setUser(user);
                            }
                        });
                    }
                });
                //user.setUriAvatar(spaceRef.getDownloadUrl().getResult());

            }
        });

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
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);

//            set address
            mLayoutAddress.getEditText().setText(address);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "Please turn on location....", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Location Permission is necessary.... ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}