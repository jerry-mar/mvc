package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jerry_mar.mvc.R;

public class ImageView extends AppCompatImageView {
    private float scale;
    private int shape;
    private Thread thread;

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
        shape = a.getInt(R.styleable.ImageView_image_shape, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (size == 0 && mode == MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)
                    (MeasureSpec.getSize(widthMeasureSpec) * scale), MeasureSpec.EXACTLY);
        } else {
            size = MeasureSpec.getSize(widthMeasureSpec);
            mode = MeasureSpec.getMode(widthMeasureSpec);

            if (size == 0 && mode == MeasureSpec.EXACTLY) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int)
                        (MeasureSpec.getSize(heightMeasureSpec) * scale), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            setImageBitmap(((BitmapDrawable) drawable).getBitmap());
        } else {
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageBitmap(final Bitmap source) {
        switch (shape) {
            case 1 : {
                if (thread != null) {
                    thread.interrupt();
                }
                thread = new Thread() {
                    @Override
                    public void run() {
                        int minEdge = Math.min(source.getWidth(), source.getHeight());
                        int dx = (source.getWidth() - minEdge) / 2;
                        int dy = (source.getHeight() - minEdge) / 2;

                        Shader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        Matrix matrix = new Matrix();
                        matrix.setTranslate(-dx, -dy);
                        shader.setLocalMatrix(matrix);

                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setShader(shader);

                        final Bitmap output = Bitmap.createBitmap(minEdge, minEdge, source.getConfig());
                        Canvas canvas = new Canvas(output);
                        canvas.drawOval(new RectF(0, 0, minEdge, minEdge), paint);
                        final Thread cur = Thread.currentThread();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (thread != null && !thread.isInterrupted() && cur == thread) {
                                    ImageView.super.setImageDrawable(
                                            new BitmapDrawable(getResources(), output));
                                } else {
                                    output.recycle();
                                }
                                thread = null;
                            }
                        });
                    }
                };
                thread.start();
            }
            break;
            default:
                super.setImageDrawable(new BitmapDrawable(getResources(), source));
        }
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }
}
