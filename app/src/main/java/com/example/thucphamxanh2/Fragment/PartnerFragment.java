package com.example.thucphamxanh2.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thucphamxanh2.Adapter.PartnerAdapter;
import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.Model.User;
import com.example.thucphamxanh2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class PartnerFragment extends Fragment {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private RecyclerView rvPartner;
    private LinearLayoutManager linearLayoutManager;
    private List<Partner> listPartner;
    private List<User> listUser;
    private PartnerAdapter adapter;
    private TextView tvErrorImg;
    private TextInputLayout til_namePartner,til_addressPartner,til_UserPartner,til_PasswordPartner,til_rePasswordPartner;
    private Button btnAddPartner,btnCancelPartner;
    private String namePartner,addressPartner,userPartner,passwordPartner,rePasswordPartner,imgPartner;
    private FloatingActionButton btn_addPartner;
    private ImageView img_Partner,imgCamera,imgDevice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner, container, false);
        rvPartner = view.findViewById(R.id.rvDoiTac_fragment);
        listPartner =  getAllPartner();
        listUser=getAllUser();
        linearLayoutManager=new LinearLayoutManager(getContext());
        rvPartner.setLayoutManager(linearLayoutManager);
        adapter = new PartnerAdapter(listPartner,this);
        rvPartner.setAdapter(adapter);
        btn_addPartner = view.findViewById(R.id.btn_AddPartner_fragment);
        btn_addPartner.setOnClickListener(view1 -> {
            openDialog();
        });
        return view;

    }
    public void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm đối tác");
        View view1 = getLayoutInflater().inflate(R.layout.dialog_partner,null);
        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
        unitUi(view1);
        imgCamera.setOnClickListener(view -> {
            requestPermissionCamera();
        });
        imgDevice.setOnClickListener(view -> {
            requestPermissionDevice();
        });
        btnAddPartner.setOnClickListener(view -> {
            getData();
            validate();
        });
        btnCancelPartner.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }
    public boolean isEmptys(String str,TextInputLayout til){
        if (str.isEmpty()){
            til.setError("Không được để trống");
            return false;
        }else {
            til.setError("");
            return true;
        }
    }
    public boolean checkLengthNumberPhone(){
        if(userPartner.length()!=10){
            til_UserPartner.setError("Số điện thoại phải đủ 10 số.");
            return false;
        }else {
            til_UserPartner.setError("");
            return true;
        }
    }
    public boolean checkNumberPhone(){
        for (int i = 0; i < listPartner.size(); i++) {
            if (listPartner.get(i).getUserPartner().equals(userPartner)){
                til_UserPartner.setError("Số điện thoại đã được sử dụng");
                return false;
            }else til_UserPartner.setError("");
        }
        for (int i = 0; i < listUser.size(); i++) {
            if (listUser.get(i).getPhoneNumber().equals(userPartner)){
                til_UserPartner.setError("Số điện thoại đã được sử dụng");
                return false;
            }else til_UserPartner.setError("");
        }
        return true;
    }
    public boolean checkPass(){
        if (!passwordPartner.equals(rePasswordPartner)){
            til_PasswordPartner.setError("Pass nhập không khớp");
            til_rePasswordPartner.setError("Pass nhập không khớp");
            return false;
        }else{
            til_PasswordPartner.setError("");
            til_rePasswordPartner.setError("");
            return true;
        }
    }
    public boolean errorImg(String str, TextView tv){
        if (str != null){
            tv.setText("");
            return true;
        }else {
            tv.setText("Ảnh không được để trống");
            return false;
        }
    }

    public void validate(){
        if(isEmptys(namePartner,til_namePartner) && isEmptys(addressPartner,til_addressPartner)
        && isEmptys(userPartner,til_UserPartner) && isEmptys(passwordPartner,til_PasswordPartner)
                && checkLengthNumberPhone() && checkPass() && checkNumberPhone() && errorImg(imgPartner,tvErrorImg) ){
            setDataPartner();
            removeAll();
        }
    }

    public void unitUi(View view){
        tvErrorImg = view.findViewById(R.id.error_img);
        img_Partner = view.findViewById(R.id.imgPartner_dialog);
        imgCamera = view.findViewById(R.id.img_addImageCamera_dialog);
        imgDevice = view.findViewById(R.id.img_addImageDevice_dialog);
        btnAddPartner = view.findViewById(R.id.btn_addPartner_dialog);
        btnCancelPartner = view.findViewById(R.id.btn_cancelPartner_dialog);
        til_namePartner =view.findViewById(R.id.til_namePartner_dialog);
        til_addressPartner =view.findViewById(R.id.til_addressPartner_dialog);
        til_UserPartner =view.findViewById(R.id.til_userPartner_dialog);
        til_PasswordPartner =view.findViewById(R.id.til_passwordPartner_dialog);
        til_rePasswordPartner =view.findViewById(R.id.til_rePasswordPartner_dialog);
    }
    public void getData(){
        try {
            Bitmap bitmap = ((BitmapDrawable)img_Partner.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            byte[] imgByte = outputStream.toByteArray();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imgPartner = Base64.getEncoder().encodeToString(imgByte);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        namePartner = til_namePartner.getEditText().getText().toString();
        addressPartner = til_addressPartner.getEditText().getText().toString();
        userPartner  = til_UserPartner.getEditText().getText().toString();
        passwordPartner = til_PasswordPartner.getEditText().getText().toString();
        rePasswordPartner = til_rePasswordPartner.getEditText().getText().toString();
    }
    public void setDataPartner(){
        Partner partner = new Partner();
        partner.setNamePartner(namePartner);
        partner.setAddressPartner(addressPartner);
        partner.setUserPartner(userPartner);
        partner.setPasswordPartner(passwordPartner);
        partner.setImgPartner(imgPartner);
        addPartner(partner);
    }
    public void removeAll(){
        til_namePartner.getEditText().setText("");
        til_addressPartner.getEditText().setText("");
        til_UserPartner.getEditText().setText("");
        til_PasswordPartner.getEditText().setText("");
        til_PasswordPartner.getEditText().setText("");
        til_rePasswordPartner.getEditText().setText("");
        img_Partner.setImageResource(R.drawable.ic_menu_camera1);
    }
    public List<Partner> getAllPartner(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Partner");
        List<Partner> list1 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Partner partner = snap.getValue(Partner.class);
                    list1.add(partner);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }
    public List<User> getAllUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("User");
        List<User> list1 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    list1.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }
    public void addPartner(Partner partner){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Partner");
        if (listPartner.size()==0){
            partner.setIdPartner(1);
            reference.child("1").setValue(partner);
        }else if (listPartner.size()!=0){
            int i = listPartner.size()-1;
            int id = listPartner.get(i).getIdPartner() + 1;
            partner.setIdPartner(id);
            reference.child(""+id).setValue(partner);
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void requestPermissionCamera(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                captureImage();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                requestPermissionCamera();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn không cấp quyền,bạn sẽ không thể tải ảnh lên\n\nVui lòng vào [Cài đặt] > [Quyền] và cấp quyền để sử dụng")
                .setPermissions(Manifest.permission.CAMERA)
                .check();
    }
    public void requestPermissionDevice(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                requestPermissionDevice();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn không cấp quyền,bạn sẽ không thể tải ảnh lên\n\nVui lòng vào [Cài đặt] > [Quyền] và cấp quyền để sử dụng" )
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                this.img_Partner.setImageBitmap(bp);

                Uri imageUri = data.getData();
                img_Partner.setImageURI(imageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Bạn chưa thêm ảnh", Toast.LENGTH_LONG).show();
            } else if (data!=null){
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_LONG).show();

            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK ) {
                Uri imageUri = data.getData();
                this.img_Partner.setImageURI(imageUri);
            }
        }

    }
}
