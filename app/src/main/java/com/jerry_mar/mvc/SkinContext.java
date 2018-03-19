package com.jerry_mar.mvc;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;

import com.jerry_mar.mvc.content.Storage;
import com.jerry_mar.mvc.utils.ControllerUtils;
import com.jerry_mar.mvc.utils.SkinLoader;

import java.util.List;

public class SkinContext extends Application implements SkinLoader.Callback {
    public static final String SKIN_NAME = "skin_name";

    private Storage storage;
    private String skinName;
    private Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        storage = Storage.getStorage(this, getClass().getSimpleName());
        loadSkin();
        skinName = getSkin();
    }

    public void loadSkin() {
        String skin = getSkin();
        if (skin != null && !skin.equals(skinName)) {
            new SkinLoader(this).execute(skin, this);
        }
    }

    public void putSkin(String path) {
        storage.putString(SKIN_NAME, path).apply();
    }

    public void resetSkin() {
        storage.remove(SKIN_NAME);
        storage.apply();
    }

    public String getSkin() {
        return storage.getString(SKIN_NAME);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onFinish(SkinResources resources) {
        if (resources == null) {
            this.resources = super.getResources();
        } else {
            this.resources = resources;
        }

        List<Activity> stack = ControllerUtils.getStack();
        for (int i = stack.size() - 1; i >= 0; i--) {
            Activity activity = stack.get(i);
            if (activity instanceof SkinInterface) {
                ((SkinInterface) activity).onThemeUpdate();
            }
        }
    }
}
