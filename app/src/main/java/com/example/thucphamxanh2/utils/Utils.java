package com.example.thucphamxanh2.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.WindowManager;

import com.example.thucphamxanh2.R;

public class Utils {
    public static ProgressDialog createProgressDiaglog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e("", "createProgressDiaglog: ", e);
        }
        dialog.setCancelable(false);
        dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading);
        return dialog;
    }
}
