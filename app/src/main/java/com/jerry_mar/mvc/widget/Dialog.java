package com.jerry_mar.mvc.widget;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.jerry_mar.mvc.Component;
import com.jerry_mar.mvc.Controller;
import com.jerry_mar.mvc.RuntimeContext;
import com.jerry_mar.mvc.Scene;
import com.jerry_mar.mvc.view.ViewHolder;

public class Dialog extends DialogFragment {
    protected String tag;
    protected String parentName;
    protected int resid;
    protected int gravity;
    protected int animation;
    protected boolean cancelable;
    protected boolean outside;

    protected FragmentManager manager;

    private android.app.Dialog dialog;
    private ViewHolder holder;
    private ArrayMap<String, Object> message;
    private Scene scene;

    public static Dialog create(int resid, RuntimeContext context) {
        String tag = context.getResource().getResourceEntryName(resid);
        return create(resid, tag, null, context);
    }

    public static Dialog create(int resid, String parentName, RuntimeContext context) {
        String tag = context.getResource().getResourceEntryName(resid);
        return create(resid, tag, parentName, context);
    }

    public static Dialog create(int resid, String tag,
                                String parentName, RuntimeContext context) {
        FragmentManager fm = context.getComponentManager().getFragmentManager();
        Dialog dialog = (Dialog) fm.findFragmentByTag(tag);
        if(dialog == null) {
            dialog = new Dialog();
            dialog.tag = tag;
            dialog.resid = resid;
            dialog.parentName = parentName;
            dialog.manager = fm;
        }
        return dialog;
    }

    public Dialog() {
        cancelable = true;
        outside = true;
        gravity = Gravity.CENTER;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setOutside(boolean outside) {
        this.outside = outside;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public void addMessage(String key, Object value) {
        if (message == null) {
            message = new ArrayMap<>();
        }
        message.put(key, value);
    }

    public Object getMessage(String key) {
        return message.get(key);
    }

    public <T extends View> T getView(int resid) {
        return holder.findView(resid);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle != null) {
            parentName = bundle.getString("parentName", null);
            tag = bundle.getString("tag", null);
            resid = bundle.getInt("resid", -1);
            gravity = bundle.getInt("gravity", -1);
            animation = bundle.getInt("animation", -1);
            cancelable = bundle.getBoolean("cancelable", true);
            outside = bundle.getBoolean("outside", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        dialog.setCanceledOnTouchOutside(outside);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();

        if (parentName != null) {
            Component component = (Component) manager.findFragmentByTag(parentName);
            inflater = component.onGetLayoutInflater(bundle);
        } else {
            inflater = getActivity().getLayoutInflater();
        }

        if(container == null) {
            container = (ViewGroup) window.findViewById(android.R.id.content);
        }
        View view = inflater.inflate(resid, container, false);
        holder = new ViewHolder(view);
        ViewGroup.LayoutParams params = view.getLayoutParams();

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(params.width, params.height);
        window.setGravity(gravity);
        window.setWindowAnimations(animation);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (scene == null) {
            if(parentName == null) {
                Controller controller = (Controller) getActivity();
                scene = controller.getScene();
            } else {
                Component component = (Component) manager.findFragmentByTag(parentName);
                scene = component.getScene();
            }
        }

        if(scene instanceof Designer) {
            ((Designer) scene).design(holder, tag);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("parentName", parentName);
        outState.putString("tag", tag);
        outState.putInt("resid", resid);
        outState.putInt("gravity", gravity);
        outState.putInt("animation", animation);
        outState.putBoolean("cancelable", cancelable);
        outState.putBoolean("outside", outside);
        super.onSaveInstanceState(outState);
    }

    public void show() {
        if(isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.show(this);
            ft.commitAllowingStateLoss();
        } else {
            super.show(manager, tag);
        }
    }

    @Override
    public void dismiss() {
        if (dialog != null) {
            super.dismissAllowingStateLoss();
            dialog = null;
        }
    }

    @Override
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle bundle) {
        dialog = new android.app.Dialog(getActivity(), getTheme()){
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if(!cancelable) {
                    return getActivity().dispatchKeyEvent(event);
                }
                return super.dispatchKeyEvent(event);
            }
        };
        return dialog;
    }

    public interface Designer {
        void design(ViewHolder holder, String tag);
    }

    //--------------------------------------------------------------------------------------------------------------------
    public void setTag(int resId, Object tag) {
        holder.setTag(resId, tag);
    }

    public <T> T getTag(int resId) {
        return holder.getTag(resId);
    }

    public void setText(int id, CharSequence value) {
        holder.setText(id, value);
    }

    public CharSequence getValue(int id) {
        return holder.getText(id).toString();
    }

    public void password(int id, boolean show) {
        EditText view = holder.findView(id);
        TransformationMethod method;
        if (show) {
            method = HideReturnsTransformationMethod.getInstance();
        } else {
            method = PasswordTransformationMethod.getInstance();
        }
        view.setTransformationMethod(method);
    }

    public boolean password(int id) {
        EditText view = holder.findView(id);
        return view.getTransformationMethod() == PasswordTransformationMethod.getInstance();
    }

    public void setTextColor(int id, int color) {
        holder.setTextColor(id, color);
    }

    public void getTextColor(int id) {
        holder.getTextColor(id);
    }

    public void setImage(int id, int resId) {
        holder.setImage(id, resId);
    }

    public void setImage(int id, Bitmap bitmap) {
        holder.setImage(id, bitmap);
    }

    public void setImageDrawable(int id, Drawable drawable) {
        holder.setImageDrawable(id, drawable);
    }

    public Drawable getImageDrawable(int id) {
        return holder.getImageDrawable(id);
    }

    public void setBackgroundColor(int id, int color) {
        holder.setBackgroundColor(id, color);
    }

    public void setBackground(int id, Drawable drawable) {
        holder.setBackground(id, drawable);
    }

    public void setBackground(int id, int color) {
        holder.setBackground(id, color);
    }

    public Drawable getBackground(int id) {
        return holder.getBackground(id);
    }

    public void show(int id) {
        holder.show(id);
    }

    public boolean isShow(int id) {
        return holder.isShow(id);
    }

    public void hide(int id, int resId) {
        holder.hide(id);
    }

    public boolean isHidden(int id) {
        return holder.isHidden(id);
    }

    public void sneak(int id) {
        holder.sneak(id);
    }

    public boolean isSneak(int id) {
        return holder.isSneak(id);
    }

    public void addView(int id, View child) {
        addView(id, child, -1);
    }

    public void addView(int id, View child, int index) {
        ViewGroup group = holder.findView(id);
        group.addView(child, index);
    }

    public View getView(int id, int index) {
        ViewGroup group = holder.findView(id);
        return group.getChildAt(index);
    }

    public void removeView(int id, int index) {
        ViewGroup group = holder.findView(id);
        group.removeViewAt(index);
    }

    public void removeView(int id, View view) {
        ViewGroup group = holder.findView(id);
        group.removeView(view);
    }

    public void removeView(int id) {
        ViewGroup group = holder.findView(id);
        group.removeAllViews();
    }

    public void requestLayout(int id) {
        holder.findView(id).requestLayout();
    }

    public void requestLayout(View view) {
        view.requestLayout();
    }
}
