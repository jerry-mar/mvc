package com.jerry_mar.mvc;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jerry_mar.mvc.view.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.AnimationUtils.currentAnimationTimeMillis;

public abstract class Scene {
    public final static String TYPE = "scene_change_type";
    public final static int FORWARD = 0;
    public final static int FORWARD_FOR_RESULT = -1;
    public final static int FINISH = -2;
    public final static int FINISH_WITH_RESULT = -4;

    protected RuntimeContext context;
    private ViewHolder holder;
    private SparseArray<Object> resource;

    protected Scene(RuntimeContext context) {
        this.context = context;
        resource = new SparseArray<>();
    }

    protected void initialize(Bundle bundle) {
        initialize();
    }

    protected void initialize() {}

    protected View create(ViewGroup parent) {
        View view = createView(getId(), parent);
        holder = new ViewHolder(view);
        return view;
    }

    public abstract int getId();

    protected void resume() {}

    protected void pause() {}

    protected void saveInstanceState(Bundle bundle) {}

    protected void destory() {}

    protected boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    protected boolean onBackDown() {
        return false;
    }

    protected boolean onBackUp() {
        return false;
    }

    //-----------------------------------------------------------------

    protected Drawable getStatusBarDrawable() {
        View view = holder.getView(0);
        view = view == null ? holder.getView() : view;
        Drawable drawable = view.getBackground();
        return drawable.getConstantState().newDrawable();
    }

    protected void requestApplyInsets() {
        ViewCompat.requestApplyInsets(holder.getView());
    }

    protected void setFitsSystemWindows(boolean fitsSystemWindows) {
        holder.getView().setFitsSystemWindows(fitsSystemWindows);
    }

    protected boolean getFitsSystemWindows() {
        return ViewCompat.getFitsSystemWindows(holder.getView());
    }

    protected Drawable getBackground() {
        return new ColorDrawable(Color.WHITE);
    }

    protected List<Object> toList() {
        List<Object> list = new ArrayList<>();
        list.add(this);
        return list;
    }

    //-----------------------------------------------------------------

    protected void setTag(int resId, Object tag) {
        holder.setTag(resId, tag);
    }

    protected <T> T getTag(int resId) {
        return holder.getTag(resId);
    }

    protected void setText(int id, CharSequence value) {
        holder.setText(id, value);
    }

    protected CharSequence getText(int id) {
        return holder.getText(id).toString();
    }

    protected void setTextColor(int id, int color) {
        holder.setTextColor(id, color);
    }

    protected void getTextColor(int id) {
        holder.getTextColor(id);
    }

    protected void setImage(int id, int resId) {
        holder.setImage(id, resId);
    }

    protected void setImage(int id, Bitmap bitmap) {
        holder.setImage(id, bitmap);
    }

    protected void setImageDrawable(int id, Drawable drawable) {
        holder.setImageDrawable(id, drawable);
    }

    protected Drawable getImageDrawable(int id) {
        return holder.getImageDrawable(id);
    }

    protected void setBackgroundColor(int id, int color) {
        holder.setBackgroundColor(id, color);
    }

    protected void setBackground(int id, Drawable drawable) {
        holder.setBackground(id, drawable);
    }

    protected void setBackground(int id, int color) {
        holder.setBackground(id, color);
    }

    protected Drawable getBackground(int id) {
        return holder.getBackground(id);
    }

    protected void show(int id, int resId) {
        View view;
        if (id != -1) {
            view = holder.findView(id);
        } else {
            view = holder.getView();
        }
        view.setVisibility(View.VISIBLE);
        if (resId != 0) {
            Animation anim = AnimationUtils.loadAnimation(context
                    .getApplicationContext(), resId);
            anim.setStartTime(currentAnimationTimeMillis());
            view.setAnimation(anim);
        }
    }

    protected void hide(int id, int resId) {
        View view;
        if (id != -1) {
            view = holder.findView(id);
        } else {
            view = holder.getView();
        }
        view.setVisibility(View.GONE);
        if (resId != 0) {
            Animation anim = AnimationUtils.loadAnimation(context
                    .getApplicationContext(), resId);
            anim.setStartTime(currentAnimationTimeMillis());
            view.setAnimation(anim);
        }
    }

    protected void sneak(int id) {
        holder.sneak(id);
    }

    protected View createView(int resId, View parent) {
        View view = context.getLayoutInflater().inflate(resId,
                (ViewGroup) parent, false);
        return view;
    }

    protected void addView(int id, View child) {
        addView(id, child, -1);
    }

    protected void addView(int id, View child, int index) {
        ViewGroup group = holder.findView(id);
        group.addView(child, index);
    }

    protected View getView(int id, int index) {
        ViewGroup group = holder.findView(id);
        return group.getChildAt(index);
    }

    protected View getView() {
        return holder.getView();
    }

    protected <V extends View> V getView(int id) {
        return (V) holder.findView(id);
    }

    protected void removeView(int id, int index) {
        ViewGroup group = holder.findView(id);
        group.removeViewAt(index);
    }

    protected void removeView(int id, View view) {
        ViewGroup group = holder.findView(id);
        group.removeView(view);
    }

    protected void removeView(int id) {
        ViewGroup group = holder.findView(id);
        group.removeAllViews();
    }

    protected void requestLayout(int id) {
        holder.findView(id).requestLayout();
    }

    protected void requestLayout(View view) {
        view.requestLayout();
    }

    protected int getColor(int resid, Resources.Theme theme) {
        Integer color = (Integer) resource.get(resid);
        if(color == null) {
            color = context.getColor(resid, theme);
            resource.put(resid, color);
        }
        return color;
    }

    protected int getColor(int resid) {
        Integer color = (Integer) resource.get(resid);
        if(color == null) {
            color = context.getColor(resid, null);
            resource.put(resid, color);
        }
        return color;
    }

    protected int getDimension(int resid) {
        Integer dimen = (Integer) resource.get(resid);
        if(dimen == null) {
            dimen = context.getDimension(resid);
            resource.put(resid, dimen);
        }
        return dimen;
    }

    protected Drawable getDrawable(int resid) {
        Drawable drawable = (Drawable) resource.get(resid);
        if(drawable == null) {
            drawable = context.getDrawable(resid);
            resource.put(resid, drawable);
        }
        return drawable;
    }

    protected String getString(int resid) {
        String value = (String) resource.get(resid);
        if(value == null) {
            value = context.getString(resid);
            resource.put(resid, value);
        }
        return value;
    }

    public void forward(Intent intent) {
        forward(intent, R.anim.anim_right_in, R.anim.anim_right_out);
    }

    public void forward(Intent intent, int in, int out) {
        Message msg = context.getHandler().obtainMessage();
        intent.putExtra(TYPE, FORWARD);
        msg.obj = intent;
        msg.arg1 = in;
        msg.arg2 = out;
        context.getHandler().sendMessage(msg);
    }

    public void forward(Intent intent, int code) {
        forward(intent, code, R.anim.anim_right_in, R.anim.anim_right_out);
    }

    public void forward(Intent intent, int code, int in, int out) {
        Message msg = context.getHandler().obtainMessage();
        intent.putExtra(TYPE, FORWARD_FOR_RESULT);
        msg.obj = intent;
        msg.what = code;
        msg.arg1 = in;
        msg.arg2 = out;
        context.getHandler().sendMessage(msg);
    }

    public void finish() {
        finish(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    public void finish(int in, int out) {
        Message msg = context.getHandler().obtainMessage();
        Intent intent = new Intent();
        intent.putExtra(TYPE, FINISH);
        msg.obj = intent;
        msg.arg1 = in;
        msg.arg2 = out;
        context.getHandler().sendMessage(msg);
    }

    public void finish(Intent intent, int code) {
        finish(intent, code, R.anim.anim_right_in, R.anim.anim_right_out);
    }

    public void finish(Intent intent, int code, int in, int out) {
        Message msg = context.getHandler().obtainMessage();
        intent.putExtra(TYPE, FINISH_WITH_RESULT);
        msg.obj = intent;
        msg.what = code;
        msg.arg1 = in;
        msg.arg2 = out;
        context.getHandler().sendMessage(msg);
    }
}
