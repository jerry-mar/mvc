package com.jerry_mar.mvc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jerry_mar.mvc.widget.InflaterFactory;
import com.jerry_mar.mvc.content.Carrier;
import com.jerry_mar.mvc.utils.DimenUtils;

public class Component<T extends Scene> extends PermissionComponent
        implements Handler.Callback {
    protected final Handler handler;
    private LayoutInflater inflater;
    private InflaterFactory factory;

    private FrameLayout root;
    protected T scene;

    public Component() {
        handler = new Handler(this);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onPrepare();
    }

    protected void onPrepare() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        this.inflater = build(inflater);
        RuntimeContext context = new RuntimeContext(getActivity(),
                        this.inflater, getResources(), handler);
        root = new FrameLayout(getContext());
        scene = bindScene(context);
        if(scene != null) {
            factory.setScene(scene.toList());
            View view = scene.create(root);
            root.addView(view);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        if (view != null) {
            super.onViewCreated(view, bundle);
        }
        if(scene != null) {
            changeStatusBar();
            scene.initialize();
        }
        initialize();
    }

    private LayoutInflater build(LayoutInflater inflater) {
        LayoutInflater.Factory2 factory = inflater.getFactory2();
        if(factory instanceof InflaterFactory) {
            inflater = inflater.cloneInContext(getContext());
            this.factory = new InflaterFactory(this, getResources());
            this.factory.inject(inflater);
        }
        this.inflater = inflater;
        return inflater;
    }

    protected void rebind() {
        RuntimeContext runtime = new RuntimeContext(getActivity(),
                        inflater, getResources(), handler);
        View view;
        if (scene != null) {
            view = scene.getView();
            scene.hide(-1, R.anim.anim_scale_out);
            root.removeView(view);
        }
        scene = bindScene(runtime);
        if (scene != null) {
            factory.setScene(scene.toList());
            view = scene.create(root);
            root.addView(view);
        }
        changeStatusBar();
        root.requestLayout();
        onViewCreated(null, null);
    }

    private void changeStatusBar() {
        if (scene != null && scene.getFitsSystemWindows()) {
            View statusBar = root.findViewWithTag(toString());
            if (statusBar == null) {
                statusBar = new View(getContext());
                root.addView(statusBar);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DimenUtils.getStatusBarHeight(getContext()));
                params.gravity = Gravity.TOP;
                statusBar.setLayoutParams(params);
                statusBar.setTag(toString());
            }
            ViewCompat.setBackground(statusBar, scene.getStatusBarDrawable());
            ((ViewGroup.MarginLayoutParams) scene.getView().getLayoutParams())
                    .topMargin = DimenUtils.getStatusBarHeight(getContext());
            scene.setFitsSystemWindows(false);
            scene.requestApplyInsets();
        } else {
            View statusBar = root.findViewWithTag(toString());
            if (statusBar != null) {
                root.removeView(statusBar);
            }
        }
    }

    protected T bindScene(RuntimeContext context) {
        return null;
    }

    protected void initialize() {}

    @Override
    public void onResume() {
        super.onResume();
        if (scene != null){
            scene.resume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (scene != null)
            scene.saveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        if (scene != null){
            scene.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if(scene != null) {
            scene.destory();
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Carrier carrier = null;
        if (data != null) {
            carrier = new Carrier(data);
        }
        if(!onResult(requestCode + resultCode, carrier)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected boolean onResult(int code, Carrier carrier) {
        return false;
    }

    @Override
    public LayoutInflater onGetLayoutInflater(Bundle bundle) {
        LayoutInflater inflater;
        if (this.inflater != null)
            inflater = this.inflater;
        else
            inflater = super.onGetLayoutInflater(bundle);
        return inflater;
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
                getActivity().overridePendingTransition(in, out);
            }
            break;
            case Scene.FORWARD_FOR_RESULT : {
                startActivityForResult(intent, msg.what);
                getActivity().overridePendingTransition(in, out);
            }
            break;
            case Scene.FINISH : {
                getActivity().finish();
                getActivity().overridePendingTransition(in, out);
            }
            break;
            case Scene.FINISH_WITH_RESULT : {
                getActivity().setResult(msg.what, intent);
                getActivity().finish();
                getActivity().overridePendingTransition(in, out);
            }
            break;
        }
        return true;
    }

    public T getScene() {
        return scene;
    }
}
