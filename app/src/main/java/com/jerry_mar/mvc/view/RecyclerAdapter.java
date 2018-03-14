package com.jerry_mar.mvc.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

public abstract class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int TYPE_ITEM_HEADER = 0x100000;
    private static final int TYPE_ITEM_TAILER = 0x200000;

    private SparseArray<View> header = new SparseArray<>();
    private SparseArray<View> tailer = new SparseArray<>();
    private List<View> headerHidden = new LinkedList<>();
    private List<View> tailerHidden = new LinkedList<>();

    protected LayoutInflater inflater;

    public RecyclerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    private boolean isHeaderView(int position) {
        return position < getHeaderCount();
    }

    private boolean isItemView(int position) {
        return !(isHeaderView(position) | isTailerView(position));
    }

    private boolean isTailerView(int position) {
        return position >= getHeaderCount() + getCount();
    }

    public void addHeaderView(View view) {
        header.put(header.size() + TYPE_ITEM_HEADER, view);
    }

    public void addHeaderView(int resid, ViewGroup parent) {
        View view = inflater.inflate(resid, parent, false);
        addHeaderView(view);
    }

    public void addTailerView(View view) {
        tailer.put(tailer.size() + TYPE_ITEM_TAILER, view);
    }

    public void addTailerView(int resid, ViewGroup parent) {
        View view = inflater.inflate(resid, parent, false);
        addTailerView(view);
    }

    public void hideHeader(int index) {
        if (header.size() > index) {
            int key = header.keyAt(index);
            View view = header.get(key);
            if (!headerHidden.contains(view))
                headerHidden.add(view);
        }
    }

    public void showHeader(int index) {
        if (header.size() > index) {
            int key = header.keyAt(index);
            View view = header.get(key);
            headerHidden.remove(view);
        }
    }

    public void hideTailer(int index) {
        if (tailer.size() > index) {
            int key = tailer.keyAt(index);
            View view = tailer.get(key);
            if (!tailerHidden.contains(view))
                tailerHidden.add(view);
        }
    }

    public void showTailer(int index) {
        if (tailer.size() > index) {
            int key = tailer.keyAt(index);
            View view = tailer.get(key);
            tailerHidden.remove(view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = header.get(viewType);
        if(view != null) {
            return new ViewHolder(view);
        }
        view = tailer.get(viewType);
        if(view != null) {
            return new ViewHolder(view);
        }
        view = inflater.inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public final int getItemViewType(int position) {
        int key = -1;
        if (isHeaderView(position)) {
            while (getHeaderCount() - position >= 1) {
                key = header.keyAt(position);
                View view = header.get(key);
                if (!headerHidden.contains(view)) {
                    break;
                }
                position++;
            }
            return key;
        }
        if (isTailerView(position)) {
            while (getTailerCount() - (position - getHeaderCount() - getCount()) >= 1) {
                key = tailer.keyAt(position - getHeaderCount() - getCount());
                View view = tailer.get(key);
                if (!tailerHidden.contains(view)) {
                    break;
                }
                position++;
            }
            return key;
        }

        return getViewType(position - getHeaderCount());
    }

    public int getViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if (isItemView(position)) {
            onAttachViewHolder(holder, position - getHeaderCount());
        }
    }

    @Override
    public final int getItemCount() {
        return getCount() + getHeaderCount() + getTailerCount();
    }

    public int getHeaderCount() {
        return header.size() - headerHidden.size();
    }

    public int getTailerCount() {
        return tailer.size() - tailerHidden.size();
    }

    public abstract void onAttachViewHolder(ViewHolder holder, int position);

    public abstract int getCount();

    @Override
    public void onAttachedToRecyclerView(android.support.v7.widget.RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager _GridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup _SpanSizeLookup = _GridLayoutManager.getSpanSizeLookup();

            _GridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(!isItemView(position)) {
                        return _GridLayoutManager.getSpanCount();
                    }

                    if (_SpanSizeLookup != null) {
                        return _SpanSizeLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
            _GridLayoutManager.setSpanCount(_GridLayoutManager.getSpanCount());
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        if(!isItemView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;
                params.setFullSpan(true);
            }
        }
        super.onViewAttachedToWindow(holder);
    }

    public <T extends View> T getView(int resid) {
        T target = null;
        int count = getHeaderCount();
        if(count > 0) {
            for(int i = 0; i < count; i++) {
                int key = header.keyAt(i);
                View view = header.get(key);
                target = (T) view.findViewById(resid);
                if(target != null) {
                    return target;
                }
            }
        }

        count = getTailerCount();
        if(count > 0) {
            for(int i = 0; i < count; i++) {
                int key = tailer.keyAt(i);
                View view = tailer.get(key);
                target = (T) view.findViewById(resid);
                if(target != null) {
                    return target;
                }
            }
        }

        return target;
    }
}
