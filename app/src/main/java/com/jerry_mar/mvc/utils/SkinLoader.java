package com.jerry_mar.mvc.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.jerry_mar.mvc.SkinResources;

import java.io.File;
import java.lang.reflect.Method;

public class SkinLoader extends AsyncTask<Object, Void, SkinResources> {
    private Callback callback;

    public SkinLoader(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPreExecute();
    }

    @Override
    protected SkinResources doInBackground(Object... params) {
        SkinResources resources = null;
        try {
            File skinFile = new File((String) params[0]);
            Context context = (Context) params[1];
            if (skinFile != null && !skinFile.exists()) {
                PackageManager manager = context.getPackageManager();
                PackageInfo mInfo = manager.getPackageArchiveInfo(skinFile.getAbsolutePath(),
                        PackageManager.GET_ACTIVITIES);
                String packageName = mInfo.packageName;

                AssetManager assetManager = AssetManager.class.newInstance();
                Method am = assetManager.getClass().getMethod("addAssetPath", String.class);
                am.invoke(assetManager, skinFile.getAbsolutePath());

                Resources superRes = context.getResources();
                resources = new SkinResources(assetManager, superRes.getDisplayMetrics(),
                        superRes.getConfiguration(), packageName);
            }
            return resources;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SkinResources resources) {
        callback.onFinish(resources);
    }

    public interface Callback {
        void onPreExecute();
        void onFinish(SkinResources resources);
    }
}
