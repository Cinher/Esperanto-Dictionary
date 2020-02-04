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
import android.support.v4.app.*;
import android.*;
import android.content.pm.*;
import java.util.*;
import android.webkit.*;

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
		
		//添加运行时权限，用于词典导入
		//无以下代码时在 AIDE 编译运行时 DictionaryOpenHelper.copyFile() 报 IOException: Permission denied
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions((DemonstrateActivity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
		}
		
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
				case 4:
					title = "Favorites";
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
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.content_demonstrate);
		switch(TYPE){
			case 0:
				//Import
				
                //导入词典 Button
                FloatingActionButton fab = new FloatingActionButton(this);
                AppBarLayout.LayoutParams fabParams = new AppBarLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fabParams.gravity = Gravity.END;
                fabParams.setMargins(768, 72, 72, 72);
                fab.setLayoutParams(fabParams);
                fab.setImageResource(R.drawable.ic_add_white_48dp);
                fab.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        chooseFile();//系统展开文件选择页面
                    }
                });
				((android.support.design.widget.CoordinatorLayout) findViewById(R.id.activity_demonstrate_main)).addView(fab);
				
				//列出所有词典
				DictionaryOpenHelper helper = new DictionaryOpenHelper();
				String [] list = helper.listExistDictionaries(this);
				if (list != null)
				{
					for (int i = 0; i < list.length; i++)
					{
						if ((list[i]).endsWith(".ld2") || (list[i]).endsWith(".ldx"))
						{
							AppCard card = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
							card.setTitle(list[i]);
							card.setImage(R.drawable.ic_insert_drive_file_black_48dp);
							layout.addView(card);
						}
					}
				}

				break;
			case 1:
				//Translate
//				AppCard card1 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
//				card1.setTitle(getResources().getString(R.string.translate_esperanto));
//				card1.setOnClickListener(new View.OnClickListener(){
//						@Override
//						public void onClick(View p){
//							startActivity(new Intent().setClass(DemonstrateActivity.this, TranslateActivity.class));
//						}
//					});
//				card1.setImage(R.drawable.ic_launcher_small);
//				((LinearLayout) findViewById(R.id.content_demonstrate)).addView(card1);
				
				AppCard card2 = new AppCard(this, AppCard.TYPE_DEMONSTRATE);
				card2.setTitle(getResources().getString(R.string.translate_via_google));
				card2.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View p){
							Uri url = Uri.parse("http://translate.google.cn/?hl=en#eo|en|");
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
				
				layout.addView(listView);
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
			startActivityForResult(Intent.createChooser(intent, "Select a File (*.ld2)"), 0);
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
                // TODO
				DictionaryOpenHelper helper = new DictionaryOpenHelper();
				helper.importDictionary(this, path, 0);//将词典复制到指定位置
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
}
