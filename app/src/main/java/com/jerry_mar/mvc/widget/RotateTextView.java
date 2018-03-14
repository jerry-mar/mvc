package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.jerry_mar.mvc.R;

public class RotateTextView extends AppCompatTextView {
    private int degree;
    private int transX;
    private int transY;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView);
        degree = ta.getInteger(R.styleable.RotateTextView_degree, 0);
        transX = ta.getDimensionPixelSize(R.styleable.RotateTextView_transX, 0);
        transY = ta.getDimensionPixelSize(R.styleable.RotateTextView_transY, 0);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(degree, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.translate(transX, transY);
        super.onDraw(canvas);
        canvas.restore();
    }
}
