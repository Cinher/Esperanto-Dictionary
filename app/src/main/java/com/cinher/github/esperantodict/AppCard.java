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
	public static final int TYPE_DICTIONARY = 0;
	public static final int TYPE_DEMONSTRATE = 1;
	
    private ImageView typeImageView;
    private TextView typeTextView;
    private TextView centralTextView;

    public AppCard(Context context, int type) {
        super(context);
		LayoutInflater inflater;
		if(type ==  TYPE_DICTIONARY){
			Log.d("Debug", "AppCard type: dict");
            inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.app_card, this);
		}else{
			Log.d("Debug", "AppCard type: demo");
			inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.app_card_demonstrate, this);
		}

        typeImageView = (ImageView) findViewById(R.id.card_imageView);
        typeTextView = (TextView) findViewById(R.id.card_typeTextView);
		if (type == TYPE_DICTIONARY){
        	centralTextView = (TextView) findViewById(R.id.card_centralTextView);
		}
    }

    public void setImage(int id){
        typeImageView.setImageResource(id);
    }

    public void setTitle(String name){
        typeTextView.setText(name);
    }

    public void setText(String text){
        centralTextView.setText(text);
    }

}

