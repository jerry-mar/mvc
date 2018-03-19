package com.jerry_mar.mvc.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.List;

public class ConfigUtils {
    /**
     * @since 1.0
     * @param context 运行环境
     * @return 版本号
     */
    public static int getVersion(Context context) {
        int version;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(),0);
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            version = 0;
        }
        return version;
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        String versionName;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packInfo = manager.getPackageInfo(
                    context.getPackageName(),0);
            versionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = StringUtils.NULL;
        }
        return versionName;
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @param key Manifest配置信息主键
     * @return Manifest配置信息
     */

    public static String getMetaData(Context context, String key) {
        String name = context.getPackageName();
        PackageManager manager = context.getPackageManager();
        String msg;
        try {
            ApplicationInfo info = manager.getApplicationInfo(name,
                    PackageManager.GET_META_DATA);
            msg = info.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            msg = StringUtils.NULL;
        }
        return msg;
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @return 是否为debug运行
     */
    public static boolean debug(Context context) {
        int mode = 0;
        ApplicationInfo info = context.getApplicationInfo();
        if (info != null) {
            mode = info.flags & ApplicationInfo.FLAG_DEBUGGABLE;
        }
        return mode != 0;
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @param apk 安装文件
     */
    public static void install(Context context, File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @param packageName 应用包名
     */
    public static void uninstall(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * @since 1.0
     * @param context 运行环境
     * @param packageName 包名
     * @return 对应的apk是否安装
     */
    public static boolean avilible(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> list = manager.getInstalledPackages(0);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i).packageName;
                if (name.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
