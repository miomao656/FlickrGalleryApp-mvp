package com.misotest.flickrgalleryapp.presentation.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import timber.log.Timber;

/**
 * Fragment operations
 */
public class FragmentHelper {

    /**
     * Replace current Fragment with new one.
     *
     * @param newFragmentLayoutId
     * @param newFragment
     */
    public static void prepareAndShowFragment(Activity activity, int newFragmentLayoutId,
                                              Fragment newFragment, boolean addToBackStack, String tag) {
        // Add the fragment to the activity, pushing this transaction on to the back stack.
        Timber.i("FRAGMENT BACKSTACK", "Before count: " +
                activity.getFragmentManager().getBackStackEntryCount() + " TAG: " + tag);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN & FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(newFragmentLayoutId, newFragment, tag);
        ft.commit();
        Timber.i("FRAGMENT BACKSTACK", "After count: " +
                activity.getFragmentManager().getBackStackEntryCount() + " TAG: " + tag);
    }

    /**
     * Returns to previous fragment in hierarchy if there is previous fragment available.
     *
     * @param activity
     */
    public static boolean popBackFromStack(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        return fm.popBackStackImmediate();
    }

    /**
     * Removes all fragments from backstack
     *
     * @param activity
     */
    public static void popAllFromBackStack(Activity activity) {
        activity.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Remove fragment from activity
     *
     * @param activity
     * @param tag
     */
    public static void removeFragment(Activity activity, String tag) {
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * Clears fragment stack in activity
     *
     * @param activity
     */
    public static void clearFragmentStack(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}
