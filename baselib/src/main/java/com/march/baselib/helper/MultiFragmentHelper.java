package com.march.baselib.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.march.baselib.inter.MultiFragmentOperator;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib.helper
 * CreateAt : 16/9/30
 * Describe :
 *
 * @author chendong
 */
public class MultiFragmentHelper {
    private final String FRAGMENT_TAG = "FRAGMENT";
    private final String ITEM_HIDE = "mHideItem";
    private final String ITEM_SHOW = "mShowItem";
    private MultiFragmentOperator mOperator;
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private int mShowItem, mHideItem;
    private int mExactlyItem = 0;

    public MultiFragmentHelper(AppCompatActivity activity, MultiFragmentOperator mOperator, Bundle save) {
        this.mOperator = mOperator;
        mFragmentManager = activity.getSupportFragmentManager();
        // 从savedInstanceState获取到保存的mCurrentItem
        if (save != null) {
            mHideItem = save.getInt(ITEM_HIDE, 0);
            mShowItem = save.getInt(ITEM_SHOW, 0);
        }
        performSelectItem(mHideItem, mShowItem, true);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ITEM_HIDE, mHideItem);
        outState.putInt(ITEM_SHOW, mShowItem);
    }

    /**
     * 隐藏当前显示的fragment,显示将要显示的fragment
     *
     * @param hideItem   需要隐藏的fragment
     * @param showItem   需要显示的fragment
     * @param isOnCreate 是否是第一次从OnCreate中启动,点击都是false
     */
    private void performSelectItem(int hideItem, int showItem, boolean isOnCreate) {
        // 获得将要显示页的tag
        String currentTag = "fragment" + hideItem;
        // 隐藏当前的的fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 如果被杀后再进来，全部的fragment都会被呈现显示状态，所以都隐藏一边
        if (isOnCreate && mFragmentManager.getFragments() != null) {
            for (Fragment fragment : mFragmentManager.getFragments()) {
                transaction.hide(fragment);
            }
        } else {
            // 正常按钮点击进入
            Fragment lastFragment = mFragmentManager.findFragmentByTag(currentTag);
            if (lastFragment != null) {
                transaction.hide(lastFragment);
            }
        }
        // 获得将要显示页的tag
        String toTag = FRAGMENT_TAG + showItem;
        // find要显示的Fragment
        mCurrentFragment = mFragmentManager.findFragmentByTag(toTag);
        if (mCurrentFragment != null) {
            // 已经存在则显示
            transaction.show(mCurrentFragment);
        } else {
            // 不存在则添加新的fragment
            mCurrentFragment = mOperator.makeFragment(showItem);
            if (mCurrentFragment != null) {
                transaction.add(mOperator.getFragmentContainerId(), mCurrentFragment, toTag);
            }
        }
        // 同步状态
        mOperator.syncSelectState(showItem);
        // 保存当前显示fragment的item
        mHideItem = hideItem;
        mShowItem = showItem;
        transaction.commitAllowingStateLoss();
    }

    /**
     * 选中某一个fragment
     *
     * @param showItem   显示的item
     * @param isOnCreate 是否是第一次创
     */
    void showFragment(int showItem, boolean isOnCreate) {
        if (showItem == mShowItem) {
            if (mOperator.whenShowSameFragment(showItem)) {
                performSelectItem(mExactlyItem, showItem, isOnCreate);
                mExactlyItem = showItem;
            }
        } else {
            performSelectItem(mExactlyItem, showItem, isOnCreate);
            mExactlyItem = showItem;
        }
    }

    /**
     * 显示某个fragment
     *
     * @param showItem 显示的item
     */
    public void showFragment(int showItem) {
        showFragment(showItem, false);
    }

    /**
     * 获取当前处于活动状态的fragment'
     *
     * @return fragment
     */
    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
