package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Toast implements Runnable  {
    private static android.widget.Toast toast;
    private static Handler handler;
    private final static long duration;
    private final static Toast task;
    private final static int resid;
    private final static int messageID;

    static {
        handler = new Handler(Looper.getMainLooper());
        task = new Toast();
        duration = 2000;
        Resources resources = Resources.getSystem();
        resid = resources.getIdentifier("transient_notification", "layout", "android");
        messageID = resources.getIdentifier("message", "id", "android");
    }

    public static void show(Context context, String message) {
        show(context, message, duration);
    }

    public static void show(Context context, String message, long duration) {
        if (toast != null) {
            handler.removeCallbacks(task);
            hide();
        }
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(resid, null);
        toast = new android.widget.Toast(context);
        toast.setView(view);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);

        TextView tv = (TextView) view.findViewById(messageID);
        tv.setText(message);
        toast.show();
        handler.postDelayed(task, duration);
    }

    public static void hide() {
        if(toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    @Override
    public void run() {
        hide();
    }
}
