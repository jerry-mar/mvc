package com.jerry_mar.mvc.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jerry_mar.mvc.ComponentManager;

import java.util.ArrayList;

public abstract class FragmentStateAdapter extends PagerAdapter {
    protected final FragmentManager manager;
    private FragmentTransaction curTransaction = null;

    private ArrayList<Fragment.SavedState> stateArray = new ArrayList<>();
    private ArrayList<Fragment> fragmentArray = new ArrayList<>();
    private Fragment currentPrimaryItem = null;

    public FragmentStateAdapter(ComponentManager cm) {
        this(cm.getFragmentManager());
    }

    public FragmentStateAdapter(FragmentManager sm) {
        manager = sm;
    }

    public Fragment getFragment(int index) {
        return fragmentArray.size() > index ? fragmentArray.get(index) : null;
    }

    public Fragment getFragment(String tag) {
        Fragment fragment = null;
        for (int i = 0; i < fragmentArray.size(); i++) {
            Fragment temp = fragmentArray.get(i);
            if (temp.toString().equals(tag)) {
                fragment = temp;
                break;
            }
        }
        return fragment;
    }

    public void remove(int index) {
        if (fragmentArray.size() > index) {
            fragmentArray.remove(index);
        }
        if (stateArray.size() > index) {
            stateArray.remove(index);
        }
    }

    public void remove(String tag) {
        int index = -1;
        for (int i = 0; i < fragmentArray.size(); i++) {
            Fragment temp = fragmentArray.get(i);
            if (temp.toString().equals(tag)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            remove(index);
        }
    }

    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (fragmentArray.size() > position) {
            Fragment fragment = fragmentArray.get(position);
            if (fragment != null) {
                return fragment;
            }
        }

        if (curTransaction == null) {
            curTransaction = manager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (stateArray.size() > position) {
            Fragment.SavedState fss = stateArray.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (fragmentArray.size() <= position) {
            fragmentArray.add(null);
        }
        fragment.setMenuVisibility(false);
        setUserVisibleHint(fragment, false);
        fragmentArray.set(position, fragment);
        curTransaction.add(container.getId(), fragment, fragment.toString());

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment scenarios = (Fragment) object;

        if (curTransaction == null) {
            curTransaction = manager.beginTransaction();
        }
        while (stateArray.size() <= position) {
            stateArray.add(null);
        }
        stateArray.set(position, scenarios.isAdded()
                ? manager.saveFragmentInstanceState(scenarios) : null);
        fragmentArray.set(position, null);

        curTransaction.remove(scenarios);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                setUserVisibleHint(fragment, false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                setUserVisibleHint(fragment, true);
            }
            currentPrimaryItem = fragment;
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
        Bundle state = null;
        if (stateArray.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[stateArray.size()];
            stateArray.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i=0; i < fragmentArray.size(); i++) {
            Fragment f = fragmentArray.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                manager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            stateArray.clear();
            fragmentArray.clear();
            if (fss != null) {
                for (int i=0; i<fss.length; i++) {
                    stateArray.add((Fragment.SavedState)fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key: keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = (Fragment) manager.getFragment(bundle, key);
                    if (f != null) {
                        while (fragmentArray.size() <= index) {
                            fragmentArray.add(null);
                        }
                        f.setMenuVisibility(false);
                        fragmentArray.set(index, f);
                    }
                }
            }
        }
    }

    private void setUserVisibleHint(Fragment f, boolean isVisibleToUser) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            f.setUserVisibleHint(isVisibleToUser);
        }
    }
}
