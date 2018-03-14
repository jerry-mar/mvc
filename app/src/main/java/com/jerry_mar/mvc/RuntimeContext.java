package com.jerry_mar.mvc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;

public class RuntimeContext {
    private LayoutInflater inflater;
    private Resources resources;
    private Context context;
    private Handler handler;
    private ComponentManager manager;

    public RuntimeContext(Activity context, LayoutInflater inflater,
                  Resources resources, Handler handler) {
        this.context = context.getApplicationContext();
        this.inflater = inflater;
        this.resources = resources;
        this.handler = handler;
        manager = new ComponentManager(context.getFragmentManager());
        if (context instanceof ComponentManager.Factory) {
            manager.setFactory((ComponentManager.Factory) context);
        }
    }

    public LayoutInflater getLayoutInflater() {
        return inflater;
    }

    public int getColor(int id, Resources.Theme theme) {
        return ResourcesCompat.getColor(resources, id, theme);
    }

    public Integer getDimension(int id) {
        return resources.getDimensionPixelOffset(id);
    }

    public Drawable getDrawable(int id) {
        return ResourcesCompat.getDrawable(resources, id, null);
    }

    public String getString(int id) {
        return resources.getString(id);
    }

    public Resources getResource() {
        return resources;
    }

    public int getResource(String name, String type) {
        return resources.getIdentifier(name, type, context.getPackageName());
    }

    public String getResource(int resId) {
        return resources.getResourceEntryName(resId);
    }

    public Context getApplicationContext() {
        return context;
    }

    public Handler getHandler() {
        return handler;
    }

    public ComponentManager getComponentManager() {
        return manager;
    }

    public ComponentManager getComponentManager(int id, ComponentManager.Factory factory) {
        manager.setContentId(id);
        manager.setFactory(factory);
        return manager;
    }

    public ComponentManager getComponentManager(int id) {
        manager.setContentId(id);
        return manager;
    }
}
