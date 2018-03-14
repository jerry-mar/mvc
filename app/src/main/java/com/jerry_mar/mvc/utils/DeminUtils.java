package com.jerry_mar.mvc.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeminUtils {
    private static int STATUS_HEIGHT;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    public static int getStatusBarHeight(Context context) {
        if (STATUS_HEIGHT == 0) {
            try {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    STATUS_HEIGHT = context.getResources().getDimensionPixelSize(resourceId);
                }
            } catch (Resources.NotFoundException e) {
                STATUS_HEIGHT = 0;
            }
        }
        return STATUS_HEIGHT;
    }

    public static int getScreenWidth(Context context) {
        if (SCREEN_WIDTH == 0) {
            initScreenSize(context);
        }
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight(Context context) {
        if (SCREEN_HEIGHT == 0) {
            initScreenSize(context);
        }
        return SCREEN_HEIGHT;
    }

    private static void initScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        try {
            Point point = new Point();
            Display.class.getMethod("getRealSize", Point.class)
                    .invoke(display, point);
            SCREEN_WIDTH = point.x;
            SCREEN_HEIGHT = point.y;
        } catch (Exception ignored) {
            SCREEN_WIDTH = metrics.widthPixels;
            SCREEN_HEIGHT = metrics.heightPixels;
        }
    }

    public static int getContentWidth(Context context, int type) {
        int result;
        if(Configuration.ORIENTATION_PORTRAIT == type) {
            result = getScreenWidth(context);
        } else {
            result = getScreenHeight(context);
        }
        return result;
    }

    public static int getContentHeight(Context context, int type) {
        int result;
        if(Configuration.ORIENTATION_PORTRAIT == type) {
            result = getScreenHeight(context) - getStatusBarHeight(context);
        } else {
            result = getScreenWidth(context) - getStatusBarHeight(context);
        }
        return result;
    }

    public static int getContentWidth(Context context) {
        return getContentWidth(context, Configuration.ORIENTATION_PORTRAIT);
    }

    public static int getContentHeight(Context context) {
        return getContentHeight(context, Configuration.ORIENTATION_PORTRAIT);
    }
}
