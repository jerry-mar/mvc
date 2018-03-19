package com.jerry_mar.mvc;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class SkinResources extends Resources {
    private String packageName;

    public SkinResources(AssetManager assets, DisplayMetrics metrics, Configuration config, String packageName) {
        super(assets, metrics, config);
        this.packageName = packageName;
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        return super.getIdentifier(name, defType, packageName);
    }
}
