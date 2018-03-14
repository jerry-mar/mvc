package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jerry_mar.mvc.R;

public class ImageView extends AppCompatImageView {
    private float scale;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
        scale = a.getFloat(R.styleable.ImageView_scale, 1.0F);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (size == 0 && mode == MeasureSpec.EXACTLY) {
            heightMeasureSpec = (int) (widthMeasureSpec * scale);
        } else {
            size = MeasureSpec.getSize(widthMeasureSpec);
            mode = MeasureSpec.getMode(widthMeasureSpec);

            if (size == 0 && mode == MeasureSpec.EXACTLY) {
                widthMeasureSpec = (int) (heightMeasureSpec * scale);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
