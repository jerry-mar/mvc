package com.jerry_mar.mvc.utils;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import java.lang.reflect.Field;

public class ScrollingUtils {
    public static boolean isViewToTop(View view, int touchSlop){
        if (view instanceof AbsListView) {
            return isAbsListViewToTop((AbsListView) view);
        }
        if (view instanceof RecyclerView) {
            return isRecyclerViewToTop((RecyclerView) view);
        }

        return  (view != null && Math.abs(view.getScrollY()) <= touchSlop);
    }

    public static boolean isViewToBottom(View view,int touchSlop){
        if (view instanceof AbsListView) return isAbsListViewToBottom((AbsListView) view);
        if (view instanceof RecyclerView) return isRecyclerViewToBottom((RecyclerView) view);
        if (view instanceof WebView) return isWebViewToBottom((WebView) view,touchSlop);
        if (view instanceof ViewGroup) return isViewGroupToBottom((ViewGroup) view, touchSlop);
        return false;
    }

    public static boolean isAbsListViewToTop(AbsListView absListView) {
        if (absListView != null) {
            int firstChildTop = 0;
            if (absListView.getChildCount() > 0) {
                // 如果AdapterView的子控件数量不为0，获取第一个子控件的top
                firstChildTop = absListView.getChildAt(0).getTop() - absListView.getPaddingTop();
            }
            if (absListView.getFirstVisiblePosition() == 0 && firstChildTop == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRecyclerViewToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null) {
                return true;
            }
            if (manager.getItemCount() == 0) {
                return true;
            }

            if (manager instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;

                int firstChildTop = 0;
                if (recyclerView.getChildCount() > 0) {
                    // 处理item高度超过一屏幕时的情况
                    View firstVisibleChild = recyclerView.getChildAt(0);
                    if (firstVisibleChild != null && firstVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                        if (android.os.Build.VERSION.SDK_INT < 14) {
                            return !(ViewCompat.canScrollVertically(recyclerView, -1) || recyclerView.getScrollY() > 0);
                        } else {
                            return !ViewCompat.canScrollVertically(recyclerView, -1);
                        }
                    }

                    // 如果RecyclerView的子控件数量不为0，获取第一个子控件的top

                    // 解决item的topMargin不为0时不能触发下拉刷新
                    View firstChild = recyclerView.getChildAt(0);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) firstChild.getLayoutParams();
                    firstChildTop = firstChild.getTop() - layoutParams.topMargin - getRecyclerViewItemTopInset(layoutParams) - recyclerView.getPaddingTop();
                }

                return layoutManager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0;
            }

            if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

                int[] out = layoutManager.findFirstCompletelyVisibleItemPositions(null);
                return (out[0] < 1);
            }
        }
        return false;
    }

    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            // 开发者自定义的滚动监听器
            Rect decorInsets = (Rect) field.get(layoutParams);
            return decorInsets.top;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isAbsListViewToBottom(AbsListView absListView) {
        if (absListView != null && absListView.getAdapter() != null&& absListView.getChildCount()
                > 0 && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
            View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
            return lastChild.getBottom() <= absListView.getMeasuredHeight();
        }
        return false;
    }

    public static boolean isRecyclerViewToBottom(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getItemCount() == 0) {
                return false;
            }

            if (manager instanceof LinearLayoutManager) {
                View lastVisibleChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                if (lastVisibleChild != null && lastVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                    if (android.os.Build.VERSION.SDK_INT < 14) {
                        return !(ViewCompat.canScrollVertically(recyclerView, 1) || recyclerView.getScrollY() < 0);
                    } else {
                        return !ViewCompat.canScrollVertically(recyclerView, 1);
                    }
                }

                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
                return layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;
            }

            if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

                int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
                int lastPosition = layoutManager.getItemCount() - 1;
                for (int position : out) {
                    if (position == lastPosition) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWebViewToBottom(WebView webview,int touchSlop) {
        return webview != null && ((webview.getContentHeight() * webview.getScale()
                - (webview.getHeight() + webview.getScrollY())) <= touchSlop);
    }

    public static boolean isViewGroupToBottom(ViewGroup viewGroup, int touchSlop){
        View subChildView = viewGroup.getChildAt(0);
        return (subChildView != null && subChildView.getMeasuredHeight() - touchSlop <=
                viewGroup.getScrollY() + viewGroup.getHeight());
    }

    public static void scrollToBottom(View view) {
        if (view instanceof AbsListView) {}
        if (view instanceof RecyclerView) {
            RecyclerView recycler = (RecyclerView) view;
            recycler.scrollToPosition(recycler.getAdapter().getItemCount() - 1);
            return;
        }
        if (view instanceof WebView) {}
        if (view instanceof ViewGroup) {
            View v = ((ViewGroup) view).getChildAt(0);
            int offset = v.getMeasuredHeight() - view.getHeight();
            if (offset < 0) {
                offset = 0;
            }

            view.scrollTo(0, offset);
        }
    }
}
