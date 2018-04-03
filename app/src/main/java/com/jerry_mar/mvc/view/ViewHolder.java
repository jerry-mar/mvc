package com.jerry_mar.mvc.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> list;
    private SparseArray<View> queue;
    private ArrayMap<String, Object> message;

    public ViewHolder(View convertView) {
        super(convertView);
        list = new SparseArray<>();
        queue = new SparseArray<>();
        message = new ArrayMap<>();
        convertView.setTag(this);
    }

    public <T extends View> T findView(int id) {
        View view = list.get(id);

        if(view == null){
            view = itemView.findViewById(id);
            list.put(id, view);
        }

        return (T) view;
    }

    public <T extends View> T getView(int index) {
        View view = queue.get(index);
        if(view == null){
            view = ((ViewGroup) itemView).getChildAt(index);
            queue.put(index, view);
        }
        return (T) view;
    }

    public <T> T message(String key) {
        return (T) message.get(key);
    }

    public <T> void message(String key, T obj) {
        message.put(key, obj);
    }

    public View getView() {
        return itemView;
    }

    //---------------------------------   API   ---------------------------------

    public <T> T getTag(int id) {
        return (T) findView(id).getTag();
    }

    public void setTag(int id, Object tag) {
        findView(id).setTag(tag);
    }

    public void setTag(int id) {
        findView(id).setTag(this);
    }

    public void setText(int id, CharSequence value) {
        TextView view = findView(id);
        view.setText(value);
    }

    public String getText(int id) {
        TextView view = findView(id);
        return view.getText().toString();
    }

    public void setTextColor(int id, int color) {
        TextView view = findView(id);
        view.setTextColor(color);
    }

    public int getTextColor(int id) {
        TextView view = findView(id);
        return view.getCurrentTextColor();
    }

    public void setImage(int id, int resId) {
        ImageView view = findView(id);
        view.setImageResource(resId);
    }

    public void setImage(int id, Bitmap bitmap) {
        ImageView view = findView(id);
        view.setImageBitmap(bitmap);
    }

    public void setImageDrawable(int id, Drawable drawable) {
        ImageView view = findView(id);
        view.setImageDrawable(drawable);
    }

    public Drawable getImageDrawable(int id) {
        ImageView view = findView(id);
        return view.getDrawable();
    }

    public void setBackgroundColor(int id, int color) {
        View view = findView(id);
        view.setBackgroundColor(color);
    }

    public void setBackground(int resId, Drawable drawable) {
        View view = findView(resId);
        ViewCompat.setBackground(view, drawable);
    }

    public void setBackground(int id, int color) {
        View view = findView(id);
        view.setBackgroundResource(color);
    }

    public Drawable getBackground(int id) {
        View view = findView(id);
        return view.getBackground();
    }

    public void show(int resId) {
        View view = findView(resId);
        view.setVisibility(View.VISIBLE);
    }

    public boolean isShow(int resId) {
        View view = findView(resId);
        return view.getVisibility() == View.VISIBLE;
    }

    public void hide(int resId) {
        View view = findView(resId);
        view.setVisibility(View.GONE);
    }

    public boolean isHidden(int resId) {
        View view = findView(resId);
        return view.getVisibility() == View.GONE;
    }

    public void sneak(int resId) {
        View view = findView(resId);
        view.setVisibility(View.INVISIBLE);
    }

    public boolean isSneak(int resId) {
        View view = findView(resId);
        return view.getVisibility() == View.INVISIBLE;
    }
}
