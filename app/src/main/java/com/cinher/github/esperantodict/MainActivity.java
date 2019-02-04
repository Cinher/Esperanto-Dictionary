package com.cinher.github.esperantodict;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nex3z.flowlayout.*;
import android.widget.FrameLayout.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                               @Override
                                               public boolean onMenuItemClick(MenuItem item) {
                                                   startActivity(new Intent().setClass(MainActivity.this, SettingsActivity.class));
                                                   return true;
                                               }
                                           }
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //搜索历史
                final Db db = new Db(getApplicationContext());
                final SQLiteDatabase dbRead = db.getReadableDatabase();
                Cursor c = dbRead.query("history", null, null, null, null, null, null);

                FlowLayout layout = new FlowLayout(getApplicationContext());
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7, getResources().getDisplayMetrics());
                int margin = (int) getResources().getDimension(R.dimen.widget_margin);

                View.OnClickListener ocl = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView) v;
                        startActivity(new Intent().setClass(MainActivity.this, ResultActivity.class).putExtra("word", tv.getText().toString()));
                        MainActivity.this.finish();
                    }
                };

                while (c.moveToNext()) {//添加TextView到Flow Layout
                    String word = c.getString(c.getColumnIndex("word"));
                    System.out.println("Database history: " + word);
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextSize(sp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textView.setBackgroundDrawable(getDrawable(R.drawable.label_bg));
                    }//Fix it!
                    textView.setText(word);
                    textView.setOnClickListener(ocl);
                    textView.setPadding(2 * margin, margin, 2 * margin, margin);
                    layout.addView(textView, params);
                }

                layout.setChildSpacing(FlowLayout.SPACING_AUTO);
                layout.setChildSpacingForLastRow(FlowLayout.SPACING_ALIGN);
                layout.setRowSpacing((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                layout.setPadding(3 * margin, margin, 3 * margin, margin);

                //按钮按下时弹出，显示历史记录
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("SEARCH HISTORY")
                        .setView(layout)
                        .setPositiveButton("CLEAR ALL", new DialogInterface.OnClickListener(){
                            //点击时，删除所有记录
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.getWritableDatabase().execSQL("DELETE FROM history");
                            }
                        })
                        .setNeutralButton("CANCEL", null)
                        .show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //设置Overview界面图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = /*typedValue.data*/getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();
        }

        //启动服务
        Intent serviceIntent = new Intent(this, DictService.class);
        startService(serviceIntent);

        //搜索
        ((EditText) findViewById(R.id.searchEditText))
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            String word = ((EditText) findViewById(R.id.searchEditText)).getText().toString();
                            if (!word.trim().equals("")) {

                                //Save to database
                                Db db = new Db(getApplicationContext());
                                SQLiteDatabase dbWrite = db.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put("word", word);
                                SQLiteDatabase dbRead = db.getReadableDatabase();
                                Cursor c = dbRead.query("history", null, null, null, null, null, null);
                                while (c.moveToNext()) {
                                    if (word.equals(c.getString(c.getColumnIndex("word")))) {
                                        dbWrite.delete("history", "word = \'" + word + "\'", null);
                                        break;
                                    }
                                }
                                dbWrite.insert("history", null, cv);
                                dbWrite.close();

                                //Go to the result activity
                                startActivity(new Intent().setClass(MainActivity.this, ResultActivity.class).putExtra("word", word));
                                MainActivity.this.finish();
                            } else {
                                //Log.w("Debug","No Word Found");
                                Snackbar.make(findViewById(R.id.searchEditText), getResources().getString(R.string.input_is_empty), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            return true;
                        }
                        return false;

                    }
                });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_translate) {
            startActivity(new Intent().setClass(MainActivity.this, DemonstrateActivity.class).putExtra("type", DemonstrateActivity.TYPE_TRANSLATE));
        } else if (id == R.id.nav_import) {
            startActivity(new Intent().setClass(MainActivity.this, DemonstrateActivity.class).putExtra("type", DemonstrateActivity.TYPE_IMPORT));
        /*} else if (id == R.id.nav_settings) {
            startActivity(new Intent().setClass(MainActivity.this, SettingsActivity.class));
        */} else if (id == R.id.nav_opensource) {
            startActivity(new Intent().setClass(MainActivity.this, DemonstrateActivity.class).putExtra("type", DemonstrateActivity.TYPE_OPENSOURCE));
        /*} else if (id == R.id.nav_donate) {
            startActivity(new Intent().setClass(MainActivity.this, DemonstrateActivity.class).putExtra("type", DemonstrateActivity.TYPE_DONATE));
        */} else if (id == R.id.nav_favorits) { 
			startActivity(new Intent().setClass(MainActivity.this, DemonstrateActivity.class).putExtra("type", DemonstrateActivity.TYPE_FAVORITES));
		}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean isFocus) {
        super.onWindowFocusChanged(isFocus);
        if (isFocus) {
            /*//隐藏Navigation Bar
            View decorView = getWindow().getDecorView();
            int uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOption);*/
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
