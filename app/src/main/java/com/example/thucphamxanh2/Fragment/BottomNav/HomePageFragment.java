package com.example.thucphamxanh2.Fragment.BottomNav;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.thucphamxanh2.Adapter.HomeSliderAdapter;
import com.example.thucphamxanh2.Adapter.ProductAdapter;
import com.example.thucphamxanh2.Fragment.ProductFragments.FoodFragment;
import com.example.thucphamxanh2.Fragment.ProductFragments.FruitFragment;
import com.example.thucphamxanh2.Fragment.ProductFragments.MeatFragment;
import com.example.thucphamxanh2.Fragment.ProductFragments.ProductFragment;
import com.example.thucphamxanh2.Fragment.ProductFragments.VegetableFragment;
import com.example.thucphamxanh2.Model.Product;
import com.example.thucphamxanh2.Model.ProductTop;
import com.example.thucphamxanh2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomePageFragment extends Fragment {

    private List<ProductTop> productTopListVegetable = new ArrayList<>();
    private List<ProductTop> productTopListMeat= new ArrayList<>();
    private List<ProductTop> productTopListFruit= new ArrayList<>();
    private List<ProductTop> productTopListFood= new ArrayList<>();
    private List<Product> listVegetable = new ArrayList<>();
    private List<Product> listMeat= new ArrayList<>();
    private List<Product> listFruit= new ArrayList<>();
    private List<Product> listFood= new ArrayList<>();
    private List<Product> listProduct = new ArrayList<>();

    ViewPager viewPager;
    Timer timer;
    LinearLayout sliderDotsPanel;

    private int dotsCount;
    private ImageView[] dots;

    int page_position = 0;

    private Integer[] images = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4, R.drawable.slider5};

    ProductAdapter adapter;

    CardView card_vegetable_home,card_fruit_home,card_meat_home,card_food_home;

    CardView card_Top_Vegetable,card_Top_Fruit,card_Top_Meat,card_Top_Food;

    RecyclerView rv_VegetableTop_Home,rv_FruitTop_Home,rv_MeatTop_Home,rv_FoodTop_Home;

    ImageView arrow1,arrow2,arrow3,arrow4;
    private ProductFragment fragment = new ProductFragment();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        adapter = new ProductAdapter(listVegetable,fragment,getContext());
        getTopProduct();
        getProduct();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        timer = new Timer();
        viewPager = view.findViewById(R.id.viewPager);
        sliderDotsPanel = view.findViewById(R.id.SliderDots);

        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(getContext(), images);

        viewPager.setAdapter(viewPagerAdapter);

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        arrow1 = view.findViewById(R.id.arrow1);
        arrow2 = view.findViewById(R.id.arrow2);
        arrow3 = view.findViewById(R.id.arrow3);
        arrow4 = view.findViewById(R.id.arrow4);

        card_vegetable_home = view.findViewById(R.id.card_vegetable_home);
        card_fruit_home = view.findViewById(R.id.card_fruit_home);
        card_meat_home = view.findViewById(R.id.card_meat_home);
        card_food_home = view.findViewById(R.id.card_food_home);

        card_Top_Vegetable = view.findViewById(R.id.card_Top_Vegetable);
        card_Top_Fruit = view.findViewById(R.id.card_Top_Fruit);
        card_Top_Meat = view.findViewById(R.id.card_Top_Meat);
        card_Top_Food = view.findViewById(R.id.card_Top_Food);

        rv_VegetableTop_Home = view.findViewById(R.id.rv_VegetableTop_Home);
        rv_FruitTop_Home = view.findViewById(R.id.rv_FruitTop_Home);
        rv_MeatTop_Home = view.findViewById(R.id.rv_MeatTop_Home);
        rv_FoodTop_Home = view.findViewById(R.id.rv_FoodTop_Home);

        card_vegetable_home.setOnClickListener(view1 -> {
            fragmentManager.beginTransaction().replace(R.id.frame_Home, new VegetableFragment(),null).addToBackStack(null).commit();

        });
        card_fruit_home.setOnClickListener(view1 -> {
            fragmentManager.beginTransaction().replace(R.id.frame_Home, new FruitFragment(),null).addToBackStack(null).commit();
        });
        card_meat_home.setOnClickListener(view1 -> {

            fragmentManager.beginTransaction().replace(R.id.frame_Home, new MeatFragment(),null).addToBackStack(null).commit();
        });
        card_food_home.setOnClickListener(view1 -> {

            fragmentManager.beginTransaction().replace(R.id.frame_Home, new FoodFragment(),null).addToBackStack(null).commit();
        });


        card_Top_Vegetable.setOnClickListener(view1 -> {
            onClickItemCart(listVegetable,rv_VegetableTop_Home);
        });
        card_Top_Fruit.setOnClickListener(view1 -> {
            onClickItemCart(listFruit,rv_FruitTop_Home);
        });
        card_Top_Meat.setOnClickListener(view1 -> {
            onClickItemCart(listMeat,rv_MeatTop_Home);
        });
        card_Top_Food.setOnClickListener(view1 -> {

            onClickItemCart(listFood,rv_FoodTop_Home);

        });

        for (int i = 0; i < dotsCount; i++) {

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotsPanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                for (int i = 0; i < dotsCount; i++) {
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
//                }
//
//                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        scheduleSlider();

        return view;
    }

    private void scheduleSlider() {
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotsCount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 300, 4000);
    }

    public void getTopProduct(){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("ProductTop");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   productTopListVegetable.clear();
                    productTopListFood.clear();
                    productTopListFruit.clear();
                    productTopListMeat.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        ProductTop top = snapshot1.getValue(ProductTop.class);
                        if (top.getIdCategory()==1){
                            productTopListVegetable.add(top);
                        }else  if (top.getIdCategory()==2){
                            productTopListFruit.add(top);
                        }else  if (top.getIdCategory()==3){
                            productTopListMeat.add(top);
                        }else {productTopListFood.add(top);
                        }
                    }
                    getProduct();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    public void getProduct(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMeat.clear();
                listFruit.clear();
                listVegetable.clear();
                listFood.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Product top = snapshot1.getValue(Product.class);
                        listProduct.add(top);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(productTopListVegetable,Comparator.comparing(ProductTop::getAmountProduct).reversed());
                    Collections.sort(productTopListFruit,Comparator.comparing(ProductTop::getAmountProduct).reversed());
                    Collections.sort(productTopListFood,Comparator.comparing(ProductTop::getAmountProduct).reversed());
                    Collections.sort(productTopListMeat,Comparator.comparing(ProductTop::getAmountProduct).reversed());
                }
                add(productTopListVegetable,listProduct,listVegetable);
                add(productTopListFruit,listProduct,listFruit);
                add(productTopListFood,listProduct,listFood);
                add(productTopListMeat,listProduct,listMeat);
                collections(listVegetable);
                collections(listFruit);
                collections(listFood);
                collections(listMeat);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
public void add(List<ProductTop> listTop, List<Product> listProduct ,List<Product> listProductTop ){
    for (int i = 0; i < listTop.size(); i++) {
        for (int j = 0; j < listProduct.size(); j++) {
            if (listTop.get(i).getIdProduct() == listProduct.get(j).getCodeProduct() ){
                listProductTop.add(listProduct.get(j));
            }
        }

    }
}
    public void collections(List<Product> listProductTop ){
//        for (int i = 0; i < listTop.size(); i++) {
//            for (int j = 0; j < listProduct.size(); j++) {
//                if (listTop.get(i).getIdProduct() == listProduct.get(j).getCodeProduct() ){
//                    listProductTop.add(listProduct.get(j));
//                }
//            }
//
//        }
        try {
            for (int i = 0; i < listProductTop.size(); i++) {
                for (int j = 1; j < listProductTop.size(); j++) {
                    if (listProductTop.get(i).getCodeProduct() == listProductTop.get(j).getCodeProduct() ){
                        listProductTop.remove(listProductTop.get(i));
                    }
                }
            }
        }catch (Exception e){

        }

    }

    public void onClickItemCart(List<Product> list,RecyclerView recyclerView){
        if (recyclerView.getVisibility() == View.GONE){
            arrow1.setImageResource(R.drawable.ic_arrow_drop_down);
            arrow2.setImageResource(R.drawable.ic_arrow_drop_down);
            arrow3.setImageResource(R.drawable.ic_arrow_drop_down);
            arrow4.setImageResource(R.drawable.ic_arrow_drop_down);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ProductAdapter(list,fragment,getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        else {
            recyclerView.setVisibility(View.GONE);
            arrow1.setImageResource(R.drawable.ic_arrow_drop_up);
            arrow2.setImageResource(R.drawable.ic_arrow_drop_up);
            arrow3.setImageResource(R.drawable.ic_arrow_drop_up);
            arrow4.setImageResource(R.drawable.ic_arrow_drop_up);
        }
    }







}