package com.example.thucphamxanh2.Service;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.widget.AppCompatButton;

import com.example.thucphamxanh2.R;

public class ConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_disconnect);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            AppCompatButton btnRetry = dialog.findViewById(R.id.btnRetry_dialogDisconnect_Retry);
            btnRetry.setOnClickListener(view -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}
