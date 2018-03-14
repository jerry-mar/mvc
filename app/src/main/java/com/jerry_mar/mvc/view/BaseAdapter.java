package com.jerry_mar.mvc.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter extends RecyclerView.Adapter<ViewHolder> {
    private LayoutInflater inflater;
    private Object[] params;

    public BaseAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public BaseAdapter(LayoutInflater inflater, Object... params) {
        this.inflater = inflater;
        this.params = params;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public <T> T get(int index) {
        T result = null;
        if (params != null && params.length > index) {
            result = (T) params[index];
        }

        return result;
    }
}
