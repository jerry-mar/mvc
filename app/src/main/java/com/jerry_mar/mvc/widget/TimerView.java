package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TimerView extends AppCompatTextView implements Runnable {
    private long time;
    private long unit;
    private long delay;
    private String value;
    private Handler handler = new Handler();
    private boolean started;
    private String prefix;
    private String suffix;
    private Callback callback;

    public TimerView(Context context) {
        this(context, null);
    }
    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void start(long time, long unit, long delay) {
        start(time, unit, delay, "", "秒");
    }

    public void start(long time, long unit, long delay, String p, String s) {
        if(!started) {
            callback = callback == null ? new Callback() {
                @Override
                public String value(long time) {
                    StringBuffer buff = new StringBuffer(prefix);
                    buff.append(Long.toString(time))
                            .append(suffix);
                    return buff.toString();
                }

                @Override
                public String value() {
                    return value;
                }
            } : callback;
            started = true;
            this.prefix = p;
            this.suffix = s;
            this.time = time + unit;
            this.unit = unit;
            this.delay = delay;
            value = getText().toString();
            handler.post(this);
        }
    }

    @Override
    public void run() {
        time -= unit;
        if (time > 0) {
            setText(callback.value(time));
            handler.postDelayed(this, delay);
        } else {
            setText(callback.value());
            started = false;
        }
    }


    public boolean isRun() {
        return started;
    }

    public static interface Callback {
        String value(long time);
        String value();
    }
}
