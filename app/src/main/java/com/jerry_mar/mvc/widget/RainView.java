package com.jerry_mar.mvc.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jerry_mar.mvc.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RainView extends View {
    private int count, width;
    private float speed, max, min;
    private List<Drawable> drawables;
    private Set<Meteor> meteors;
    private Paint paint;
    private ValueAnimator animator;
    private long prevTime;

    public RainView(Context context) {
        this(context, null);
    }

    public RainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RainView);
        count = typedArray.getInt(R.styleable.RainView_count, 25);
        speed = typedArray.getFloat(R.styleable.RainView_speed, 1);
        max = typedArray.getFloat(R.styleable.RainView_max, 1.2f);
        min = typedArray.getFloat(R.styleable.RainView_min, 0.5f);
        Drawable drawable = typedArray.getDrawable(R.styleable.RainView_meteor);
        drawables = new ArrayList<>();
        meteors = new HashSet<>();
        if (drawable != null) {
            drawables.add(drawable);
        }
        typedArray.recycle();
        newPaint();
        newAnimator();
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void newAnimator() {
        animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long nowTime = System.currentTimeMillis();
                float secs = (float) (nowTime - prevTime) / 1000f;
                prevTime = nowTime;
                Iterator<Meteor> iterator = meteors.iterator();
                while (iterator.hasNext()) {
                    Meteor meteor = iterator.next();
                    meteor.y += (meteor.speed * secs);
                    if (meteor.y > getHeight()) {
                        meteor.y = 0 - meteor.height;
                    }
                    meteor.rotation = meteor.rotation
                            + (meteor.rotationSpeed * secs);
                }
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(100);
    }

    private void newPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Iterator<Meteor> iterator = meteors.iterator();
        while (iterator.hasNext()) {
            Meteor meteor = iterator.next();
            Matrix m = new Matrix();
            m.setTranslate(-meteor.width / 2, -meteor.height / 2);
            m.postRotate(meteor.rotation);
            m.postTranslate(meteor.width / 2 + meteor.x, meteor.height / 2 + meteor.y);
            canvas.drawBitmap(meteor.bitmap, m, paint);
        }
    }

    private void prepareMeteor() {
        int count = drawables.size();
        Random random = new Random();
        for (int i = 0; i < this.count; i++) {
            Drawable drawable = drawables.get(random.nextInt(count));
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Meteor meteor = new Meteor(getContext(), bitmap, speed, max, min, width);
            meteors.add(meteor);
        }
    }

    public void start() {
        clear();
        prepareMeteor();
        prevTime = System.currentTimeMillis();
        animator.start();
    }

    public void stop() {
        clear();
        invalidate();
        animator.cancel();
        setLayerType(View.LAYER_TYPE_NONE, null);
    }

    private void clear() {
        Iterator<Meteor> iterator = meteors.iterator();
        while (iterator.hasNext()) {
            iterator.next().recycle();
        }
        meteors.clear();
    }

    static class Meteor {
        public float x, y;
        public float rotation;
        public float speed;
        public float rotationSpeed;
        public int width, height;
        public Bitmap bitmap;

        public Meteor(Context context, Bitmap bitmap, float speed,
                      float max, float min, int maxWidth) {
            double scale = min + Math.random() * (max - min);
            width = (int) (bitmap.getWidth() * scale);
            height = width * bitmap.getHeight() / bitmap.getWidth();
            maxWidth = (maxWidth == 0) ? context.getResources().getDisplayMetrics().widthPixels : maxWidth;
            this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            Random random = new Random();
            x = Math.abs(random.nextInt(maxWidth) - width);
            y = -random.nextInt(height * 5) * 0.1F / 5;
            this.speed = speed * (float) (1 + Math.random() * 9) * height;
            rotation = (float) Math.random() * 180 - 90;
            rotationSpeed = (float) Math.random() * 90 - 45;
        }

        public void recycle() {
            if (bitmap!= null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
    }
}
