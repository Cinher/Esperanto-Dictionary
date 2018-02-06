package com.cinher.github.esperantodict;
import android.support.v7.app.*;
import android.os.*;
import android.util.*;
import android.content.res.*;
import android.graphics.*;
import android.app.*;
import android.widget.*;

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
					title = "Import";
					break;
				case 1:
					title = "Translate";
					break;
				case 2:
					title = "Donate";
					break;
				case 3:
					title = "Open Source";
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
		
		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.demonstratetoolbar);
		toolbar.setTitle(title);
		
		
	}
	
}
