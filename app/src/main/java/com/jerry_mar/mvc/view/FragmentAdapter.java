package com.jerry_mar.mvc.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jerry_mar.mvc.ComponentManager;

import java.util.ArrayList;
import java.util.List;

public abstract class FragmentAdapter extends PagerAdapter {
    protected final FragmentManager manager;
    private FragmentTransaction curTransaction = null;
    private Fragment fragment = null;
    private List<String> nameList;

    public FragmentAdapter(ComponentManager cm) {
        this(cm.getFragmentManager());
    }

    public FragmentAdapter(FragmentManager sm) {
        manager = sm;
        nameList = new ArrayList<>();
    }

    protected abstract Fragment getItem(int position);

    public Fragment getFragment(int position) {
        return manager.findFragmentByTag(nameList.get(position));
    }

    public Fragment getFragment(String tag) {
        return manager.findFragmentByTag(tag);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (curTransaction == null) {
            curTransaction = manager.beginTransaction();
        }

        Fragment temp = null;
        if (nameList.size() > position) {
            temp = manager.findFragmentByTag(nameList.get(position));
        }
        if (temp != null) {
            curTransaction.attach(temp);
        } else {
            temp = getItem(position);
            if (!temp.isAdded()) {
                curTransaction.add(container.getId(), temp, temp.toString());
            }
            String tag = temp.toString();
            if (!nameList.contains(tag)) {
                nameList.add(tag);
            }
        }
        if (temp != fragment) {
            temp.setMenuVisibility(false);
            setUserVisibleHint(temp, false);
        }

        return temp;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (curTransaction == null) {
            curTransaction = manager.beginTransaction();
        }
        curTransaction.detach((Fragment) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment temp = (Fragment) object;
        if (temp != fragment) {
            if (fragment != null) {
                fragment.setMenuVisibility(false);
                setUserVisibleHint(temp, false);
            }
            if (temp != null) {
                temp.setMenuVisibility(true);
                setUserVisibleHint(temp, true);
            }
            this.fragment = temp;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (curTransaction != null) {
            curTransaction.commitAllowingStateLoss();
            curTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    private void setUserVisibleHint(Fragment f, boolean isVisibleToUser) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            f.setUserVisibleHint(isVisibleToUser);
        }
    }
}
