package com.cinher.github.esperantodict.ui.tables_tabbed;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cinher.github.esperantodict.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
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
                title = mContext.getResources().getString(R.string.tools_psc_title_prefix);
                break;
            case 1:
                title = mContext.getResources().getString(R.string.tools_psc_title_suffix);
                break;
            case 2:
                title = mContext.getResources().getString(R.string.tools_psc_title_correlatives);
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