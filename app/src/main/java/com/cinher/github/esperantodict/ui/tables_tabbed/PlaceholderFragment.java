package com.cinher.github.esperantodict.ui.tables_tabbed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.cinher.github.esperantodict.R;

public class PlaceholderFragment extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static com.cinher.github.esperantodict.ui.tables_tabbed.PlaceholderFragment newInstance(int index) {
        com.cinher.github.esperantodict.ui.tables_tabbed.PlaceholderFragment fragment = new com.cinher.github.esperantodict.ui.tables_tabbed.PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tables_tabbed, container, false);
        WebView webView = (WebView) root.findViewById(R.id.tables_tabbed_webview);
        String url = "";
        switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                url = "file:///android_asset/tables/eo_prefix.html";
                break;
            case 2:
                url = "file:///android_asset/tables/eo_suffix.html";
                break;
            case 3:
                url = "file:///android_asset/tables/eo_correlatives.html";
                break;
        }
        webView.loadUrl(url);
        return root;
    }

}
