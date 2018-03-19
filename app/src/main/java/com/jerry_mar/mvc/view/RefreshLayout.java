package com.jerry_mar.mvc.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.TypedValue;

public class RefreshLayout extends SwipeRefreshLayout {
    private OnRefreshListener listener;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSize(DEFAULT);
        setProgressViewOffset(false, -getProgressCircleDiameter(), (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()
        ));
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        this.listener = listener;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        boolean refresh = isRefreshing();
        super.setRefreshing(refreshing);
        if (refreshing && !refresh) {
            if (listener != null) {
                listener.onRefresh();
            }
        }
    }
}
