package com.jerry_mar.mvc.callback;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import java.lang.reflect.Method;

public class OnLoadCallback extends RecyclerView.OnScrollListener {
    private RecyclerEvent event;
    private Method method;
    private Object controller;
    private int type;
    private int index;
    private int[] values;

    public OnLoadCallback(RecyclerEvent event, Method method, Object controller) {
        this.event = event;
        this.method = method;
        this.controller = controller;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (!event.load && manager != null) {
            if (type == 0) {
                type = manager instanceof LinearLayoutManager ? 1 :
                        manager instanceof StaggeredGridLayoutManager ? 2 : -1;
                index = recyclerView.getAdapter().hashCode();
            }
            boolean can = canLoad(recyclerView.getLayoutManager());
            if (can && (dx > 0 || dy > 0)) {
                try {
                    event.load();
                    method.invoke(controller, event);
                } catch (Exception e) {
                    event.finish();
                    Log.d(controller.getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private boolean canLoad(RecyclerView.LayoutManager layoutManager) {
        boolean result = false;
        switch (type) {
            case 1 : {
                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                int last = manager.findLastVisibleItemPosition();
                int count = manager.getItemCount();
                result = last >= count - index - 1;
            }
            break;
            case 2 : {
                StaggeredGridLayoutManager manager =
                        (StaggeredGridLayoutManager) layoutManager;
                if (values == null || values.length != manager.getSpanCount()) {
                    values = new int[manager.getSpanCount()];
                }
                int last = 0;
                int count = manager.getItemCount();
                for (int value : values) {
                    last = Math.max(value, last);
                }
                result = last >= count - index - 1;
            }
            break;
        }
        return result;
    }
}
