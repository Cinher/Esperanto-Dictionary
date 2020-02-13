package com.cinher.github.esperantodict;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.*;
import android.os.*;
import android.support.v7.widget.Toolbar;
import android.util.*;
import android.content.res.*;
import android.graphics.*;
import android.app.*;
import android.widget.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.LinearLayout.*;
import android.content.*;
import android.support.v4.app.*;
import android.*;
import android.content.pm.*;
import android.webkit.*;

import java.lang.reflect.Array;
import java.util.*;

public class DemonstrateActivity extends AppCompatActivity {
	
	public static final int TYPE_IMPORT = 0;
    public static final int TYPE_TRANSLATE = 1;
	public static final int TYPE_DONATE = 2;
	public static final int TYPE_OPENSOURCE = 3;
	public static final int TYPE_FAVORITES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demonstrate);
		
		final int TYPE = getIntent().getIntExtra("type",5);
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
					title = getResources().getString(R.string.nav_import);
					break;
				case 1:
					title = getResources().getString(R.string.nav_tools);
					break;
				case 2:
					title = getResources().getString(R.string.drawer_donate);
					break;
				case 3:
					title = getResources().getString(R.string.nav_opensource);
					break;
				case 4:
					title = getResources().getString(R.string.nav_favorites);
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
		android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.demonstrate_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//添加内容
		LinearLayout layout = (LinearLayout) findViewById(R.id.content_demonstrate);
		switch(TYPE){
			case 0:
				//Import

				//添加运行时权限，用于词典导入
				//无以下代码时在 AIDE 编译运行时 DictionaryOpenHelper.copyFile() 报 IOException: Permission denied
				if (ActivityCompat.checkSelfPermission(this
						, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					new AlertDialog.Builder(this)
							.setTitle(getResources().getString(R.string.import_alert_dialog_request_for_permissions))
							.setMessage(getResources().getString(R.string.import_alert_dialog_message))
							.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									ActivityCompat.requestPermissions(DemonstrateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
											,1);
								}
							})
							.show();
				}
				
                //导入词典 Button
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21, Android 5.0
					FloatingActionButton fab = new FloatingActionButton(this);
					AppBarLayout.LayoutParams fabParams = new AppBarLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fabParams.gravity = Gravity.END;
					fabParams.setMargins(768, 72, 72, 72);
					fab.setLayoutParams(fabParams);
					fab.setImageResource(R.drawable.ic_add_white_48dp);
					fab.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							chooseFile();//系统展开文件选择页面
						}
					});
					((android.support.design.widget.CoordinatorLayout) findViewById(R.id.activity_demonstrate_main)).addView(fab);
				} else {
					Button btn = new Button(this);
					btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							chooseFile();//系统展开文件选择页面
						}
					});
					LayoutParams btnParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					btnParams.setMargins(10, 10, 10, 10);
					btnParams.gravity = Gravity.CENTER_HORIZONTAL;
					btn.setLayoutParams(btnParams);
					btn.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0,
							(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0);
					btn.setText("+");
					btn.setTextColor(Color.WHITE);
					GradientDrawable gradientDrawable = new GradientDrawable();
					gradientDrawable.setColor(getResources().getColor(R.color.colorAccent));
					gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
					btn.setBackground(gradientDrawable);
					((LinearLayout) findViewById(R.id.content_demonstrate)).addView(btn);
				}

				
				//列出所有词典
				DictionaryOpenHelper helper = new DictionaryOpenHelper();
				String [] list = helper.listExistDictionaries(this);
				if (list != null)
				{
					if (list.length != 0) {
						for (String s : list) {
							if ((s).endsWith(".ld2") || (s).endsWith(".ldx")) {
								AppCard card = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
								card.setTitle(s);
								card.setImage(R.drawable.ic_insert_drive_file_black_48dp);
								layout.addView(card);
							}
						}
					}else {//显示：没有导入的词典文件
						LinearLayout messageLayout = new LinearLayout(this);
						messageLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
						messageLayout.setOrientation(LinearLayout.VERTICAL);
						messageLayout.setGravity(Gravity.CENTER);

						ImageView imageView = new ImageView(this);
						LayoutParams imageViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								(int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics())));
						int marginHorizontal = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
						int marginVertical = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
						imageViewParams.setMargins(marginHorizontal, marginVertical * 5, marginHorizontal, marginVertical);
						imageView.setLayoutParams(imageViewParams);
						imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found_1));

						TextView textView = new TextView(this);
						textView.setGravity(Gravity.CENTER);
						textView.setText(getResources().getString(R.string.import_no_dictionary));
						textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

						messageLayout.addView(imageView);
						messageLayout.addView(textView);
						layout.addView(messageLayout);
					}
				}else{//显示：没有导入的词典文件
					LinearLayout messageLayout = new LinearLayout(this);
					messageLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					messageLayout.setOrientation(LinearLayout.VERTICAL);
					messageLayout.setGravity(Gravity.CENTER);

					ImageView imageView = new ImageView(this);
					LayoutParams imageViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							(int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics())));
					int marginHorizontal = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
					int marginVertical = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
					imageViewParams.setMargins(marginHorizontal, marginVertical * 5, marginHorizontal, marginVertical);
					imageView.setLayoutParams(imageViewParams);
					imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found_1));

					TextView textView = new TextView(this);
					textView.setGravity(Gravity.CENTER);
					textView.setText(getResources().getString(R.string.import_no_dictionary));
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

					messageLayout.addView(imageView);
					messageLayout.addView(textView);
					layout.addView(messageLayout);
				}

				break;
			case 1:
				//Tools
				System.gc();

				AppCard card1 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
				card1.setTitle(getResources().getString(R.string.tools_prefixes_suffixes));
				card1.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View p){
							startActivity(new Intent().setClass(DemonstrateActivity.this, TablesTabbedActivity.class));
						}
					});
				card1.setImage(R.drawable.ic_presuf);
				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card1);
				
//				AppCard card2 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
//				card2.setTitle(getResources().getString(R.string.translate_via_google));
//				card2.setOnClickListener(new View.OnClickListener(){
//						@Override
//						public void onClick(View p){
//							Uri url = Uri.parse("http://translate.google.cn/?hl=en#eo|en|");
//							Intent intent = new Intent(Intent.ACTION_VIEW,url);
//							startActivity(intent);
//						}
//					});
//				card2.setImage(R.drawable.ic_google_translate);
//				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card2);

				AppCard card2 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
				card2.setTitle(getResources().getString(R.string.tools_text_converter));
				card2.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p){
						startActivity(new Intent().setClass(DemonstrateActivity.this, ConverterActivity.class));
					}
				});
				card2.setImage(R.drawable.ic_converter);
				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card2);

				break;
			case 2:
				//Donate
				break;
			case 3:
				//Open Source
				WebView webview = new WebView(this);
				webview.loadUrl("file:///android_asset/opensource.html");
				webview.setWebViewClient(new WebViewClient(){
						@Override
						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							view.loadUrl(url);
							return true;
						}
					});
				layout.addView(webview);
				break;
			case 4:
				//Favorites
				//用 ListView 显示收藏夹，点击元素时跳转
				mListView listView = new mListView(this);
				listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				List<String> data = new ArrayList<String>();
				
				Db db = new Db(getApplicationContext());
				SQLiteDatabase dbRead = db.getReadableDatabase();
				Cursor c = dbRead.query("favorites", null, null, null, null, null, null);
				while (c.moveToNext()) {
					data.add(c.getString(c.getColumnIndex("word")));
				}
				
				listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, data));
				
				final List<String> dataArray = data;
				listView.setOnItemClickListener(new ListView.OnItemClickListener(){
						@Override                                                
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							//点击元素时跳转到相应词汇的搜索结果
							startActivity(new Intent().setClass(DemonstrateActivity.this, ResultActivity.class).putExtra("word", dataArray.get(position)));
							DemonstrateActivity.this.finish();
						}
				});
				if (dataArray.size() != 0) {
					layout.addView(listView);
				} else {//显示没有收藏
					LinearLayout messageLayout = new LinearLayout(this);
					messageLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					messageLayout.setOrientation(LinearLayout.VERTICAL);
					messageLayout.setGravity(Gravity.CENTER);

					ImageView imageView = new ImageView(this);
					LayoutParams imageViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							(int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics())));
					int marginHorizontal = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
					int marginVertical = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
					imageViewParams.setMargins(marginHorizontal, marginVertical * 5, marginHorizontal, marginVertical);
					imageView.setLayoutParams(imageViewParams);
					imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found_2));

					TextView textView = new TextView(this);
					textView.setGravity(Gravity.CENTER);
					textView.setText(getResources().getString(R.string.favorites_no_favorites));
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

					messageLayout.addView(imageView);
					messageLayout.addView(textView);
					layout.addView(messageLayout);
				}
				c.close();//关闭 Cursor 释放资源
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
			startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.import_select_a_file)), 0);
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
                String [] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getApplicationContext().getContentResolver()
                        .query(uri, projection, null, null, null);
                int column = 0;
                if (cursor != null) {
                    column = cursor.getColumnIndexOrThrow(projection [0]);
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column);
                        cursor.close();
                    }
                }else{
					Toast.makeText(this, getResources().getString(R.string.import_error_1), Toast.LENGTH_LONG).show();
				}

            } else if ((uri.getScheme()).equalsIgnoreCase("file")) {//开头为 file
                Log.d("onActivityResult", uri.getPath());
                path = uri.getPath();
            }

            if(path != null && !(path.equals(""))){
				DictionaryOpenHelper helper = new DictionaryOpenHelper();
				helper.importDictionary(this, path, 0);//将词典复制到指定位置
				DemonstrateActivity.this.finish();
            }else{
            	Toast.makeText(this, getResources().getString(R.string.import_error_2), Toast.LENGTH_LONG).show();
			}
        }
    }
	
	//修改 ListView，防止在 ScrollView 中只显示一行
	public class mListView extends ListView {
		public mListView(Context context) {
			super(context);
		}

		public mListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, heightSpec);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
