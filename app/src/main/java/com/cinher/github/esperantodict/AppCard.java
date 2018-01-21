package com.cinher.github.esperantodict;

/**
 * Created by Junchen on 2017/8/17.
 */

import android.support.v7.widget.CardView;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class AppCard extends CardView
{
    private ImageView typeImageView;
    private TextView typeTextView;
    private TextView centralTextView;

    public AppCard(Context context) {
        super(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.app_card, this);

        typeImageView = (ImageView) findViewById(R.id.card_typeImageView);
        typeTextView = (TextView) findViewById(R.id.card_typeTextView);
        centralTextView = (TextView) findViewById(R.id.card_centralTextView);
    }
    public AppCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.app_card, this);

        typeImageView = (ImageView) findViewById(R.id.card_typeImageView);
        typeTextView = (TextView) findViewById(R.id.card_typeTextView);
        centralTextView = (TextView) findViewById(R.id.card_centralTextView);
    }

    public void setTypeImage(int id){
        typeImageView.setImageResource(id);
    }

    public void setTitle(String name){
        typeTextView.setText(name);
    }

    public void setText(String text){
        centralTextView.setText(text);
    }

}

