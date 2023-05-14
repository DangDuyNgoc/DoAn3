package com.example.thucphamxanh2.Fragment.Bill;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thucphamxanh2.Adapter.AdapterBill;
import com.example.thucphamxanh2.Model.Bill;
import com.example.thucphamxanh2.R;
import com.example.thucphamxanh2.databinding.FragmentBillBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CurrentOrderFragment extends Fragment {

    private RecyclerView rvBill;
    private LinearLayoutManager linearLayoutManager;
    private AdapterBill adapterBill;
    private List<Bill> listBill = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_current_order, container, false);
        initUi(view);
        return view;
    }
    public void initUi(View view ){
        getBill();
        rvBill = view.findViewById(R.id.rv_billCurrent);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvBill.setLayoutManager(linearLayoutManager);
        adapterBill = new AdapterBill(listBill,getContext());
        rvBill.setAdapter(adapterBill);
    }
    public void getBill(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Bill");
        SharedPreferences preferences = getContext().getSharedPreferences("My_User", Context.MODE_PRIVATE);
        String user = preferences.getString("username","");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBill.clear();

                for (DataSnapshot snap : snapshot.getChildren()){
                    Bill bill = snap.getValue(Bill.class);
                    if (user.equals(bill.getIdPartner()) && bill.getStatus().equals("No")) {
                        listBill.add(bill);
                    }else if (user.equals(bill.getIdClient()) && bill.getStatus().equals("No")){
                        listBill.add(bill);
                    }
                }
                adapterBill.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public  void notification(){
        String CHANNEL_ID="1234";

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getContext().getPackageName() + "/" + R.raw.sound);
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //For API 26+ you need to put some additional code like below:
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Thông báo", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription("Chuông thông báo");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);
            mChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

        //General code:
        NotificationCompat.Builder status = new NotificationCompat.Builder(getContext(),CHANNEL_ID);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setOnlyAlertOnce(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Bạn có đơn hàng mới")
                .setDefaults(Notification.DEFAULT_LIGHTS )
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getContext().getPackageName()+"/"+R.raw.sound))
                .build();


        mNotificationManager.notify((int)System.currentTimeMillis(), status.build());

    }
}