package com.cinher.github.esperantodict;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.content.*;

public class DemonstrateActivity extends AppCompatActivity {
	
	/*public final int TYPE_IMPORT = 0;
    public final int TYPE_TRANSLATE = 1;
	public final int TYPE_DONATE = 2;
	public final int TYPE_OPENSOURCE = 3;
	*/

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
					title = getResources().getString(R.string.drawer_tools);
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
		
		//添加内容
		int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
		int e = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)getResources().getDimension(R.dimen.widget_margin), getResources().getDisplayMetrics());
		LayoutParams paramsPic = new LayoutParams(h, h);
		LayoutParams paramsLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsFull = new LayoutParams(LayoutParams.MATCH_PARENT, h);
		switch(TYPE){
			case 0:
				//Import
                //导入词典 Button
                FloatingActionButton fab = new FloatingActionButton(this);
                AppBarLayout.LayoutParams fabParams = new AppBarLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fabParams.gravity = Gravity.END;
                fabParams.setMargins(32, 32, 32, 32);
                fab.setLayoutParams(fabParams);
                fab.setImageResource(R.drawable.ic_note_white);
                fab.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        chooseFile();//系统展开文件选择页面
                    }
                });

                ((LinearLayout) findViewById(R.id.content_demonstrate)).addView(fab);

				break;
			case 1:
				//Translate
				/*ImageView view1 = new ImageView(getApplicationContext());
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
				btn1.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p){
						startActivity(new Intent().setClass(DemonstrateActivity.this, TranslateActivity.class));
					}
				});
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
				*/
				AppCard card1 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
				card1.setTitle(getResources().getString(R.string.translate_esperanto));
				card1.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View p){
							startActivity(new Intent().setClass(DemonstrateActivity.this, TranslateActivity.class));
						}
					});
				card1.setImage(R.drawable.ic_launcher_small);
				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card1);
				
				AppCard card2 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
				card2.setTitle(getResources().getString(R.string.translate_via_google));
				card2.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View p){
							Uri url = Uri.parse("http://translate.google.cn/");
							Intent intent = new Intent(Intent.ACTION_VIEW,url);
							startActivity(intent);
						}
					});
				card2.setImage(R.drawable.ic_google_translate);
				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card2);
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

	protected void chooseFile(){
        //选择词典导入
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try{
            //请求系统选择文件
			startActivityForResult(Intent.createChooser(intent, "Select a File"), 0);
		}catch (ActivityNotFoundException e){
			e.printStackTrace();
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //选择词典 chooseFile() 的回调接收， 并转换 uri 的格式
        //格式： /storage/emulated/0/.../abc.xyz
        super.onActivityResult(requestCode, resultCode, data);
        if(0 == requestCode && resultCode == RESULT_OK){
            Uri uri = data.getData();
			String path = null;
            //判断格式
            if ((uri.getScheme()).equalsIgnoreCase("content")) {//(忽略大小写)判断开头为 content
                String[] projection = {"_data"};
                Cursor cursor = getApplicationContext().getContentResolver()
                        .query(uri, projection, null, null, null);
                int column = 0;
                if (cursor != null) {
                    column = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        Log.d("onActivityResult", cursor.getString(column));
                        path = cursor.getString(column);
                        cursor.close();
                    }
                }else{
                    Toast.makeText(this, "Error: cursor == null", Toast.LENGTH_SHORT).show();
                }

            } else if ((uri.getScheme()).equalsIgnoreCase("file")) {//开头为 file
                Log.d("onActivityResult", uri.getPath());
                path = uri.getPath();
            }

            if(path != null && !(path.equals(""))){
                final Db db = new Db(getApplicationContext());
                final SQLiteDatabase dbWrite = db.getWritableDatabase();
                Log.d("onActivityResult", "path != null: " + path);
                // TODO: insert path to database "dictionaries".
            }
        }
    }
}
