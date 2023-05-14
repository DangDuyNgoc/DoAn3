package com.example.thucphamxanh2.Fragment.ProductFragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.example.thucphamxanh2.Adapter.ProductAdapter;
import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.Model.Product;
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


public class FoodFragment extends Fragment {
    private List<Product> listFood;
    private RecyclerView rvFood;
    private LinearLayoutManager linearLayoutManager;
    private ProductAdapter adapter;
    private View view;
    private SharedPreferences sharedPreferences;
    private String user;

    private Partner partner = new Partner();
    private FloatingActionButton fab_addProduct;
    private List<Product> listProduct;
    private TextInputLayout til_nameProduct,til_priceProduct;
    private ImageView img_Product,img_addImageCamera,img_addImageDevice;
    private String nameProduct,imgProduct,userPartner,priceProduct;
    private int codeCategory;
    private Button btn_addVegetable,btn_cancleVegetable;
    private static final int REQUEST_ID_IMAGE_CAPTURE =10;
    private static final int PICK_IMAGE =100;
    private ProductFragment fragment = new ProductFragment();
    private TextView tvErrorImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_food, container, false);
        initUI();
        rvFood.setLayoutManager(new GridLayoutManager(getContext(), 2));

        sharedPreferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("username","");
        if(user.equals("admin")){
           view.findViewById(R.id.fab_addFood_fragment).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.fab_addFood_fragment).setVisibility(View.VISIBLE);
        }
        fab_addProduct = view.findViewById(R.id.fab_addFood_fragment);
        fab_addProduct.setOnClickListener(view1 -> {
            dialogProduct();
        });

        return view;
    }public void initUI(){
        listProduct = getAllProduct();
        listFood = getProductPartner();
        rvFood = view.findViewById(R.id.rvFood);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvFood.setLayoutManager(linearLayoutManager);
        adapter = new ProductAdapter(listFood,fragment,getContext());
        rvFood.setAdapter(adapter);

    }
    public  List<Product> getAllProduct(){
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Vui lòng đợi ...");
        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");
        List<Product> list1 = new ArrayList<>();
        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                list1.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Product product = snap.getValue(Product.class);
                    list1.add(product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }
    public  List<Product> getProductPartner(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");
        List<Product> list1 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Product product = snap.getValue(Product.class);
                    if (user.equals("admin") && product.getCodeCategory()==4){
                        list1.add(product);
                    }else if (product.getUserPartner().equals(user) && product.getCodeCategory()==4 ){
                        list1.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list1;
    }

    private void dialogProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm sản phẩm");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_product,null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        initUiDialog(view);
        view.findViewById(R.id.sp_nameCategory).setVisibility(View.GONE);
        img_addImageCamera.setOnClickListener(view1 -> {
            requestPermissionCamera();
        });
        img_addImageDevice.setOnClickListener(view1 -> {
            requestPermissionDevice();
        });
        btn_addVegetable.setOnClickListener(view1 -> {
            getData();
            validate();

        });
        btn_cancleVegetable.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });
    }
    public void initUiDialog(View view){
        tvErrorImg = view.findViewById(R.id.error_img);
        img_Product = view.findViewById(R.id.imgProduct_dialog);
        img_addImageCamera = view.findViewById(R.id.img_addImageCamera_dialog);
        img_addImageDevice = view.findViewById(R.id.img_addImageDevice_dialog);
        til_nameProduct =  view.findViewById(R.id.til_NameProduct_dialog);
        til_priceProduct =  view.findViewById(R.id.til_PriceProduct_dialog);
        btn_addVegetable =  view.findViewById(R.id.btn_addVegetable_dialog);
        btn_cancleVegetable =  view.findViewById(R.id.btn_cancleVegetable_dialog);
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
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                this.img_Product.setImageBitmap(bp);

                Uri imageUri = data.getData();
                img_Product.setImageURI(imageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Bạn chưa thêm ảnh", Toast.LENGTH_LONG).show();
            } else if (data!=null){
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_LONG).show();

            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK ) {
                Uri imageUri = data.getData();
                this.img_Product.setImageURI(imageUri);
            }
        }

    }
    public void getData(){
        nameProduct = til_nameProduct.getEditText().getText().toString();
        try {
            Bitmap bitmap = ((BitmapDrawable)img_Product.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            byte[] imgByte = outputStream.toByteArray();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imgProduct = Base64.getEncoder().encodeToString(imgByte);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        userPartner = sharedPreferences.getString("username","");
        codeCategory = 4;
        priceProduct = til_priceProduct.getEditText().getText().toString();

    }
    public boolean isEmptys(String str,TextInputLayout til){
        if (str.isEmpty()){
            til.setError("Không được để trống");
            return false;
        }else{
            til.setError("");
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
        if (isEmptys(nameProduct, til_nameProduct) && isEmptys(priceProduct, til_priceProduct) && errorImg(imgProduct, tvErrorImg)) {
            setDataProduct();
            removeAll();
        }
    }
    public void removeAll(){
        til_nameProduct.getEditText().setText("");
        til_priceProduct.getEditText().setText("");
        img_Product.setImageResource(R.drawable.ic_baseline_image_24);
    }
    public void setDataProduct(){
        Product product = new Product();
        product.setUserPartner(userPartner);
        product.setCodeCategory(codeCategory);
        product.setNameProduct(nameProduct);
        product.setPriceProduct(Integer.parseInt(priceProduct));
        product.setImgProduct(imgProduct);
        addProduct(product);

    }
    public void addProduct(Product product){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");
        if (listProduct.size()==0){
            product.setCodeProduct(1);
            reference.child("1").setValue(product);

        }else {
            int i = listProduct.size()-1;
            int id = listProduct.get(i).getCodeProduct()+1;
            product.setCodeProduct(id);

            reference.child(""+id).setValue(product);
        }
    }
}