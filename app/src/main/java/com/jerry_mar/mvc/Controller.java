package com.jerry_mar.mvc;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jerry_mar.mvc.utils.ControllerUtils;
import com.jerry_mar.mvc.utils.DimenUtils;
import com.jerry_mar.mvc.widget.InflaterFactory;

public abstract class Controller<T extends Scene> extends PermissionController
        implements Handler.Callback, Runnable {
    protected final Handler handler;
    private LayoutInflater inflater;
    private InflaterFactory factory;

    protected T scene;

    public Controller() {
        handler = new Handler(this);
    }

    void setFactory(InflaterFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ControllerUtils.add(this);
        inflater = getLayoutInflater();
        try{
            factory = new InflaterFactory(this, getResources());
            inflater.setFactory2(factory);
        } catch (IllegalStateException e) {
            factory.inject(inflater);
        }
        onPrepare(bundle);
    }

    public void onPrepare(Bundle bundle) {
        action(bundle);
    }

    protected void action(Bundle bundle) {
        RuntimeContext context = new RuntimeContext(this,
                getLayoutInflater(), getResources(), handler);
        if (scene != null) {
            scene.hide(-1, R.anim.anim_scale_out);
        }
        scene = bindScene(context);
        factory.setScene(scene.toList());
        Window window = getWindow();
        View root = window.getDecorView();
        ViewGroup parent = root.findViewById(Window.ID_ANDROID_CONTENT);
        View view = scene.create(parent);
        adjustWindowAndView(window, (ViewGroup) root, parent);
        if (scene != null) {
            scene.initialize(bundle);
        }
        super.setContentView(view, view.getLayoutParams());
    }

    private void adjustWindowAndView(Window window, ViewGroup root, ViewGroup parent) {
        if (root.getBackground() == null) {
            ViewCompat.setBackground(root, scene.getBackground());
        }
        if (scene.getFitsSystemWindows()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                createStatusBar(root, parent);
                scene.setFitsSystemWindows(false);
                scene.requestApplyInsets();
            }
        } else {
            View statusBar = root.findViewWithTag(toString());
            if (statusBar != null) {
                root.removeView(statusBar);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    private void createStatusBar(ViewGroup root, ViewGroup parent) {
        View statusBar = root.findViewWithTag(toString());
        if (statusBar == null) {
            statusBar = new View(this);
            root.addView(statusBar);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.getStatusBarHeight(this));
            params.gravity = Gravity.TOP;
            statusBar.setLayoutParams(params);
            statusBar.setTag(toString());
            ((ViewGroup.MarginLayoutParams) parent.getLayoutParams())
                    .topMargin = DimenUtils.getStatusBarHeight(this);
        }
        ViewCompat.setBackground(statusBar, scene.getStatusBarDrawable());
    }

    protected T bindScene(RuntimeContext context) {
        return null;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        initialize();
    }

    protected void initialize() {}

    @Override
    protected void onResume() {
        super.onResume();
        if (scene != null) {
            scene.resume();
        }
    }

    @Override
    protected void onPause() {
        if (scene != null) {
            scene.pause();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        if (scene != null) {
            scene.saveInstanceState(bundle);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy() {
        if (scene != null) {
            scene.destory();
        }
        ControllerUtils.remove(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!onResult(requestCode + resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected boolean onResult(int code, Intent intent) {
        return false;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (inflater == null) {
            inflater = super.getLayoutInflater();
        }
        return inflater;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (scene != null) {
            result = scene.onTouchEvent(event);
        }
        if (!result) {
            result = super.onTouchEvent(event);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                scene != null && scene.onBackDown()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                scene != null && scene.onBackUp()) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Intent intent = (Intent) msg.obj;
        int type = intent.getIntExtra(Scene.TYPE, 1);
        int in = msg.arg1;
        int out = msg.arg2;

        switch (type) {
            case Scene.FORWARD : {
                startActivity(intent);
                overridePendingTransition(in, out);
            }
            break;
            case Scene.FORWARD_FOR_RESULT : {
                startActivityForResult(intent, msg.what);
                overridePendingTransition(in, out);
            }
            break;
            case Scene.FINISH : {
                finish();
                overridePendingTransition(in, out);
            }
            break;
            case Scene.FINISH_WITH_RESULT : {
                setResult(msg.what, intent);
                finish();
                overridePendingTransition(in, out);
            }
            break;
        }
        return true;
    }

    @Override
    public void run() {
        Instrumentation instrumentation = new Instrumentation();
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    public T getScene() {
        return scene;
    }
}
