package com.cinher.github.esperantodict.ui.tables_tabbed;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return com.cinher.github.esperantodict.ui.tables_tabbed.PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "PREFIX";
                break;
            case 1:
                title = "SUFFIX";
                break;
            case 2:
                title = "CORRELATIVES";
                break;
        }
        return title;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}