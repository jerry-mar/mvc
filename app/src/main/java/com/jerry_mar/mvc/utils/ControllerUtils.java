package com.jerry_mar.mvc.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Process;

import java.util.LinkedList;
import java.util.List;

public class ControllerUtils {
    private static LinkedList<Activity> stack = new LinkedList<>();

    public static List<Activity> getStack() {
        return stack;
    }

    /**
     * @since 1.0
     * @return 获取堆栈中Activity的个数
     */
    public static int size() {
        return stack.size();
    }

    /**
     * @since 1.0
     * @param activity 想要查找的Activity
     * @return 相应Activity实例的序列数(没有相应的实例返回-1)
     */
    public static int index(Activity activity) {
        int index = stack.indexOf(activity);
        if (index != -1) {
            index = size() - stack.indexOf(activity) - 1;
        }
        return index;
    }

    /**
     * @since 1.0
     * @param activity 将要添加进堆栈中的Activity
     */
    public static void add(Activity activity) {
        if (stack.contains(activity)) {
            stack.remove(activity);
        }
        stack.add(activity);
    }

    /**
     * @since 1.0
     * @param index 堆栈中相应的Activity的序列数(栈顶为0)
     * @return 查找到指定序列的Activity实例(如有没有返回null)
     */
    public static Activity get(int index) {
        return stack.size() > index ? stack.get(size() - index - 1) : null;
    }

    /**
     * @since 1.0
     * @param cls 想要获取的Activity类
     * @return 查找到的Activity实例(如有多个返回最后压入堆栈的实例)
     */
    public static Activity get(Class cls) {
        return get(cls.getName());
    }

    /**
     * @since 1.0
     * @param cls 想要获取的Activity类
     * @param val 想要获取的Activity.toString的返回值
     * @return 查找到的Activity实例(如有多个返回最后压入堆栈的实例)
     */
    public static Activity get(Class cls, String val) {
        return get(cls.getName(), val);
    }

    /**
     * @since 1.0
     * @param className 想要获取的Activity完整类名
     * @return 查找到的Activity实例(如有多个返回最后压入堆栈的实例)
     */
    public static Activity get(String className) {
        return get(className, null);
    }

    /**
     * @since 1.0
     * @param className 想要获取的Activity完整类名
     * @param val 想要获取的Activity.toString的返回值
     * @return 查找到的Activity实例(如有多个返回最后压入堆栈的实例)
     */
    public static Activity get(String className, String val) {
        Activity result = null;
        int count = stack.size() - 1;
        for (int i = count; i >= 0; i--) {
            result = stack.get(i);
            String targetName = result.getClass().getName();
            if (targetName.equals(className) && (val == null || val.equals(result))) {
                break;
            }
            result = null;
        }
        return result;
    }

    /**
     * @since 1.0
     * @param activity 将要在堆栈中删除的Activity
     * @return 是否删除成功(相应的Activity可能已经不存在)
     */
    public static boolean remove(Activity activity) {
        return stack.remove(activity);
    }

    /**
     * @since 1.0
     * @param activity 将要结束的Activity
     */
    public static void release(Activity activity) {
        if (remove(activity)) {
            activity.finish();
        }
    }

    /**
     * @since 1.0
     * @param index 将要开始结束的Activity的序列数
     * @param count 将要结束的Activity的数量
     */
    public static void release(int index, int count) {
        int size = size();
        if (size >= index + count) {
            index = size - index;
            for (int i = 1; i <= count; i++) {
                release(stack.get(index - i));
            }
        }
    }

    /**
     * @since 1.0
     * @param index 将要开始结束的Activity的序列数
     */
    public static void release(int index) {
        release(index, stack.size() - index);
    }

    /**
     * @since 1.0
     * @param context 应用实例(退出进程时回调onTerminate)
     */
    public void exit(Application context) {
        release(0);
        if (context != null){
            context.onTerminate();
        }
        Process.killProcess(Process.myPid());
    }
}
