package com.jerry_mar.mvc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jerry_mar.mvc.R;
import com.jerry_mar.mvc.Scene;
import com.jerry_mar.mvc.callback.CallbackInfo;
import com.jerry_mar.mvc.callback.DefaultOnTextChangedListener;
import com.jerry_mar.mvc.callback.DefinedOnClickListener;
import com.jerry_mar.mvc.callback.DefinedOnLongClickListener;
import com.jerry_mar.mvc.callback.Filter;
import com.jerry_mar.mvc.callback.OnClickFilter;
import com.jerry_mar.mvc.callback.OnLoad;
import com.jerry_mar.mvc.callback.OnLoadCallback;
import com.jerry_mar.mvc.callback.OnLongClickFilter;
import com.jerry_mar.mvc.callback.OnRefresh;
import com.jerry_mar.mvc.callback.OnRefreshCallback;
import com.jerry_mar.mvc.callback.OnTextWatcherFilter;
import com.jerry_mar.mvc.callback.RecyclerEvent;
import com.jerry_mar.mvc.widget.ImageView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InflaterFactory implements LayoutInflater.Factory2 {
    private Object controller;
    private Resources res;
    private Map<Integer, CallbackInfo> target;
    private List<Method> methods;

    private Object[] constructorArgs;
    private static final Class<?>[] signature = new Class[]{Context.class, AttributeSet.class};
    private static final String[] preparedClass = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    private static final Map<String, Constructor<? extends View>> constructors = new ArrayMap<>();
    private static final List<Filter> filters;

    static {
        filters = new ArrayList<>();
        filters.add(new OnClickFilter());
        filters.add(new OnLongClickFilter());
        filters.add(new OnTextWatcherFilter());
    }

    public InflaterFactory(Object controller, Resources res) {
        this.res = res;
        this.controller = controller;
        methods = new LinkedList<>();
        methods.addAll(Arrays.asList(controller.getClass().getMethods()));
        constructorArgs = new Object[2];
        target = new HashMap<>();
    }

    public void setScene(List<Object> scenes) {
        target.clear();
        int count = scenes.size();
        for (int i = 0; i < count; i++) {
            Object scene = scenes.get(i);
            Class<?> cls = scene.getClass();
            List<Method> methods = new LinkedList<>();
            methods.addAll(Arrays.asList(cls.getMethods()));
            execute(scene, methods);
        }
    }

    public void inject(LayoutInflater inflater) {
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactory2");
            field.setAccessible(true);
            field.set(inflater, this);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public View onCreateView(View view, String name, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InflaterFactory);
        int id = a.getResourceId(R.styleable.InflaterFactory_android_id, View.NO_ID);
        a.recycle();
        ViewGroup parent = (ViewGroup) view;
        view = onCreateView(name, context, attrs);

        CallbackInfo info;
        if (id != View.NO_ID) {
            if ((info = target.get(id)) != null) {
                bind(view, info);
            } else {
                if (view instanceof SwipeRefreshLayout) {
                    setOnRefreshListener((SwipeRefreshLayout) view, id);
                } else if (view instanceof RecyclerView) {
                    setOnLoadListener((RecyclerView) view, id);
                }
            }
        }

        if (parent != null) {
            view.setLayoutParams(parent.generateLayoutParams(attrs));
        }
        return view;
    }

    private void setOnLoadListener(RecyclerView view, int id) {
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (method.getAnnotations().length == 0) {
                methods.remove(i);
                i--;
                continue;
            }
            if (method.isAnnotationPresent(OnLoad.class)) {
                OnLoad annotation = method.getAnnotation(OnLoad.class);
                String value = annotation.value();
                if (res.getResourceEntryName(id).equals(value)) {
                    view.addOnScrollListener(new OnLoadCallback(new RecyclerEvent(),
                            method, controller));
                    break;
                }
            }
        }
    }

    private void setOnRefreshListener(SwipeRefreshLayout view, int id) {
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (method.getAnnotations().length == 0) {
                methods.remove(i);
                i--;
                continue;
            }
            if (method.isAnnotationPresent(OnRefresh.class)) {
                OnRefresh annotation = method.getAnnotation(OnRefresh.class);
                String value = annotation.value();
                if (res.getResourceEntryName(id).equals(value)) {
                    view.setOnRefreshListener(new OnRefreshCallback(method, controller));
                    break;
                }
            }
        }
    }

    private void bind(View view, CallbackInfo info) {
        if (info.onClick != null) {
            view.setOnClickListener(new DefinedOnClickListener(controller,
                    info.target, info.onClick));
        }
        if (info.onLongClick != null) {
            view.setOnLongClickListener(new DefinedOnLongClickListener(controller,
                    info.target, info.onLongClick));
        }
        if (info.beforeTextChanged != null || info.onTextChanged != null || info.afterTextChanged != null) {
            EditText v = (EditText) view;
            v.addTextChangedListener(new DefaultOnTextChangedListener(controller, info.target,
                    info.beforeTextChanged, info.onTextChanged, info.afterTextChanged));
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "TextView":
                view = new AppCompatTextView(context, attrs);
                break;
            case "ImageView":
                view = new ImageView(context, attrs);
                break;
            case "Button":
                view = new AppCompatButton(context, attrs);
                break;
            case "EditText":
                view = new AppCompatEditText(context, attrs);
                break;
            case "RatingBar":
                view = new AppCompatRatingBar(context, attrs);
                break;
            case "SeekBar":
                view = new AppCompatSeekBar(context, attrs);
                break;
        }

        if(view == null) {
            view = createViewFromTag(context, name, attrs);
        }

        return view;
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        View view = null;
        try {
            constructorArgs[0] = context;
            constructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                for (int i = 0; i < preparedClass.length; i++) {
                    view = createView(context, name, preparedClass[i]);
                    if (view != null) {
                        break;
                    }
                }
            } else {
                view = createView(context, name, null);
            }

        } catch (Exception e) {
            view = null;
        } finally {
            constructorArgs[0] = null;
            constructorArgs[1] = null;
        }

        return view;
    }

    private View createView(Context context, String name, String preparedClas) {
        Constructor<? extends View> constructor = constructors.get(name);
        View view;
        try {
            if (constructor == null) {
                Class<? extends View> cls = context.getClassLoader().loadClass(
                        preparedClas != null ? (preparedClas + name) : name).asSubclass(View.class);
                constructor = cls.getConstructor(signature);
                constructors.put(name, constructor);
            }
            constructor.setAccessible(true);
            view = constructor.newInstance(constructorArgs);
            constructor.setAccessible(false);
        } catch (Exception e) {
            view = null;
        }

        return view;
    }

    private void execute(Object scene, List<Method> ms) {
        int count = filters.size();
        for (int i = 0; i < count; i++) {
            Filter filter = filters.get(i);
            filter.execute(scene, target, ms);
        }
    }
}
