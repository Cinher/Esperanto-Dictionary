package com.cinher.github.esperantodict;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class TablesTabbedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_tabbed);
        com.cinher.github.esperantodict.ui.tables_tabbed.SectionsPagerAdapter sectionsPagerAdapter = new com.cinher.github.esperantodict.ui.tables_tabbed.SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.tables_tabbed_view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = (TabLayout) findViewById(R.id.tables_tabbed_tabs);
        tabs.setupWithViewPager(viewPager);
    }
}