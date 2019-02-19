package com.cinher.github.esperantodict;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.app.*;
import android.widget.*;
import android.view.inputmethod.*;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;

public class WidgetPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_popup);

        //设置Overview界面图标
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();
        }

        //设置导航栏颜色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }
		
		((EditText) findViewById(R.id.widget_popup_search_edittext))
			.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						String word = ((EditText) findViewById(R.id.widget_popup_search_edittext)).getText().toString();
						if (!word.trim().equals("")) {

							Db db = new Db(getApplicationContext());
							SQLiteDatabase dbWrite = db.getWritableDatabase();
							ContentValues values = new ContentValues();
							values.put("word", word);
							SQLiteDatabase dbRead = db.getReadableDatabase();
							Cursor cursor = dbRead.query("history", null, null, null, null, null, null);
							while (cursor.moveToNext()) {
								if (word.equals(cursor.getString(cursor.getColumnIndex("word")))) {
									dbWrite.delete("history", "word = \'" + word + "\'", null);
									break;
								}
							}
							dbWrite.insert("history", null, values);
							dbWrite.close();

							startActivity(new Intent().setClass(WidgetPopupActivity.this, ResultActivity.class).putExtra("word", word));
							WidgetPopupActivity.this.finish();
						} else {
							Snackbar.make(findViewById(R.id.searchEditText), getResources().getString(R.string.input_is_empty), Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
						}
						return true;
					}
					return false;
				}
			});
		
	}
	
}
