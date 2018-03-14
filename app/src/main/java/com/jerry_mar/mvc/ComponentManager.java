package com.jerry_mar.mvc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class ComponentManager {
    private FragmentManager manager;
    private int content;
    private Factory factory;
    private String curTag;

    public ComponentManager(FragmentManager manager) {
        this.manager = manager;
    }

    public void setContentId(int content) {
        this.content = content;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public String getCurTag() {
        return curTag;
    }

    public FragmentManager getFragmentManager() {
        return manager;
    }

    public <T extends Fragment> T getFragment(String tag) {
        return (T) manager.findFragmentByTag(tag);
    }

    public void remove(Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        if (fragment.getTag().equals(curTag)) {
            curTag = null;
        }
        transaction.commitAllowingStateLoss();
    }

    public void show(String tag) {
        Fragment fragment = getFragment(tag);
        if(fragment == null) {
            fragment = factory.createFragment(tag);
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if(fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(content, fragment, tag);
        }
        curTag = tag;
        transaction.commitAllowingStateLoss();
    }

    public void hide(String tag, boolean replace) {
        Fragment fragment = getFragment(tag);
        if(fragment == null) {
            fragment = factory.createFragment(tag);
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if(replace) {
            transaction.remove(fragment);
        } else {
            transaction.hide(fragment);
        }
        if (tag.equals(curTag)) {
            curTag = null;
        }
        transaction.commitAllowingStateLoss();
    }

    public void hide(String tag) {
        hide(tag,false);
    }

    public void toggle(String furTag) {
        Fragment curFragment = null;
        Fragment furFragment;

        if(curTag != null) {
            curFragment = getFragment(curTag);
        }
        furFragment = getFragment(furTag);
        if(furFragment == null) {
            furFragment = factory.createFragment(furTag);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        if(curFragment != null)
            transaction.hide(curFragment);

        if(furFragment.isAdded()) {
            transaction.show(furFragment);
        } else {
            transaction.add(content, furFragment, furTag);
        }
        curTag = furTag;
        transaction.commitAllowingStateLoss();
    }

    public interface Factory {
        Fragment createFragment(String tag);
    }
}
