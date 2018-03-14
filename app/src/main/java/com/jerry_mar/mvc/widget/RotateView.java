package com.jerry_mar.mvc.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class RotateView {
    private View view;
    private Animation rotate;
    public int REPEATCOUNT = -1;
    public int DURATION = 2000;

    public RotateView(View view) {
        this.view = view;
    }

    public void setRepeatCount(int RepeatCount){
        this.REPEATCOUNT = RepeatCount;
    }

    public void setDuration(int duration){
        this.DURATION = duration;
    }

    public void start(){
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(REPEATCOUNT);
        rotate.setFillAfter(true);
        rotate.setDuration(DURATION);
        view.setAnimation(rotate);
    }

    public void stop(){
        view.clearAnimation();
    }
}