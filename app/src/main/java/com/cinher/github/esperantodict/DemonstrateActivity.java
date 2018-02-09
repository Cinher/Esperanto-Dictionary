package com.cinher.github.esperantodict;
import android.support.v7.app.*;
import android.os.*;
import android.util.*;
import android.content.res.*;
import android.graphics.*;
import android.app.*;
import android.widget.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.LinearLayout.*;

public class DemonstrateActivity extends AppCompatActivity {
	
	public final int TYPE_IMPORT = 0;
	public final int TYPE_TRANSLATE = 1;
	public final int TYPE_DONATE = 2;
	public final int TYPE_OPENSOURCE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demonstrate);
		
		final int TYPE = getIntent().getIntExtra("type",5);
		Log.d("debug", TYPE + " ");
		String title = "";
		
        //设置Overview界面图标
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
			
			switch(TYPE){
				case 0:
					title = getResources().getString(R.string.drawer_import);
					break;
				case 1:
					title = getResources().getString(R.string.drawer_translate);
					break;
				case 2:
					title = getResources().getString(R.string.drawer_donate);
					break;
				case 3:
					title = getResources().getString(R.string.drawer_open_source);
					break;
				default:
					break;
			}
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(title, bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();
        }

        //设置导航栏颜色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }
		
		//设置标题
		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.demonstrate_toolbar);
		toolbar.setTitle(title);
		
		//Temporary
		//ImageButton btn = new ImageButton(getApplicationContext());
		//btn.setAdjustViewBounds(true);
		//int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		//int e = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		//btn.setMaxHeight(h);
		//btn.setMaxWidth(h);
		//btn.setElevation(e);
		//LayoutParams params = new LayoutParams(h, h);
		//btn.setBackground(getDrawable(R.drawable.circle));
		//Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ic_google_translate);
		//btn.setImageBitmap(bm);
		//((LinearLayout) findViewById(R.id.content_demonstrate)).addView(btn, params);
		
		//((LinearLayout) findViewById(R.id.content_demonstrate)).addView(new Button(this));
		
		//添加内容
		int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
		int e = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)getResources().getDimension(R.dimen.widget_margin), getResources().getDisplayMetrics());
		LayoutParams paramsPic = new LayoutParams(h, h);
		LayoutParams paramsLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsFull = new LayoutParams(LayoutParams.MATCH_PARENT, h);
		switch(TYPE){
			case 0:
				//Import
				break;
			case 1:
				//Translate
				ImageView view1 = new ImageView(getApplicationContext());
				ImageView view2 = new ImageView(getApplicationContext());
				view1.setMaxWidth(h);
				view2.setMaxWidth(h);
				view1.setMaxHeight(h);
				view2.setMaxHeight(h);
				view1.setAdjustViewBounds(true);
				view2.setAdjustViewBounds(true);
				view1.setImageDrawable(getDrawable(R.drawable.ic_launcher_small));
				view2.setImageDrawable(getDrawable(R.drawable.ic_google_translate));
				
				Button btn1 = new Button(getApplicationContext());
				btn1.setGravity(Gravity.CENTER);
				btn1.setText(getResources().getString(R.string.translate_esperanto));
				Button btn2 = new Button(getApplicationContext());
				btn2.setGravity(Gravity.CENTER);
				btn2.setText(getResources().getString(R.string.translate_via_google));
				
				LinearLayout layoutMain = new LinearLayout(this);
				layoutMain.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout layoutPic = new LinearLayout(this);
				layoutPic.setOrientation(LinearLayout.VERTICAL);
				LinearLayout layoutBtn = new LinearLayout(this);
				layoutBtn.setOrientation(LinearLayout.VERTICAL);
				LinearLayout layout1 = new LinearLayout(this);
				layout1.setGravity(Gravity.CENTER);
				LinearLayout layout2 = new LinearLayout(this);
				layout2.setGravity(Gravity.CENTER);
				layout2.setPadding(0, e, 0, 0);
				
				layout1.addView(btn1);
				layout2.addView(btn2);
				
				paramsFull.setMargins(e,2 * e, 0, 0);
				paramsPic.setMargins(e,2 * e, 0, 0);
				layoutBtn.addView(layout1, paramsFull);
				layoutBtn.addView(layout2, paramsFull);
				layoutPic.addView(view1, paramsPic);
				layoutPic.addView(view2, paramsPic);
				layoutMain.addView(layoutPic);
				layoutMain.addView(layoutBtn, paramsLayout);
				
				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(layoutMain,paramsLayout);
				break;
			case 2:
				//Donate
				break;
			case 3:
				//Open Source
				break;
			default:
				break;
			}
		
	}
	
}
