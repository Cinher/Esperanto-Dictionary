package com.cinher.github.esperantodict;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;
import java.net.URLEncoder;


public class ResultActivity extends AppCompatActivity {

    String word; //将要搜索的单词
    LinearLayout resultView;
    private Handler messageHandler;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        context = this;

        word = getIntent().getStringExtra("word");
        Toolbar toolbar = (Toolbar) findViewById(R.id.result_toolbar);
        toolbar.setTitle(word);
        setSupportActionBar(toolbar);

        resultView = (LinearLayout) findViewById(R.id.result_central_linear_layout);

        //设置Overview界面图标
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(word, bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();
        }

        //设置导航栏颜色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }
		
        //fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.result_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				
				//收藏
				Db db = new Db(getApplicationContext());
				SQLiteDatabase dbWrite = db.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put("word", word);
				SQLiteDatabase dbRead = db.getReadableDatabase();
				boolean isWordExistInFavorites = false;
				Cursor c = dbRead.query("favorites", null, null, null, null, null, null);
				while (c.moveToNext()) {
					if (word.equals(c.getString(c.getColumnIndex("word")))) {
						isWordExistInFavorites = true; //收藏夹中有该词汇
						break;
					}
				}
				
				//如收藏夹中有该词汇则删除，否则添加
				if (isWordExistInFavorites){
					dbWrite.delete("favorites", "word = \'" + word + "\'", null);
					Snackbar.make(view, getResources().getString(R.string.result_removed_from_favorites), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
				}else {
					dbWrite.insert("favorites", null, cv);
					Snackbar.make(view, getResources().getString(R.string.result_added_to_favorites), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
				}
				dbWrite.close();
				c.close();
            }
        });

        //本地结果
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("settings_local_interpretation", true)) {//设置中是否开启了本地结果
            AppCard localCard = new AppCard(this, AppCard.TYPE_DICTIONARY);
            localCard.setTitle(getResources().getString(R.string.result_local_interpretation));
            String[] arr = GetLocalResultModule.GetLocalTranslationResult(this, addHat(word));
            //数组第一个元素为中文释义，第二个为英文释义
            //addHat() 将 x 形式转换为戴帽子的字母形式
            switch (PreferenceManager.getDefaultSharedPreferences(this).getString("settings_language_local", "-1")) {
                //查看设置中的本地词典显示释义的语言
                case "0":
                    localCard.setText(
                            getResources().getString(R.string.result_definition_en) + "\n"
                                    + (arr[1]) + "\n"
                    );
                    if (!((arr[1]).isEmpty())) {
                        resultView.addView(localCard);
                    }
                    break;
                case "1":
                    localCard.setText(
                            getResources().getString(R.string.result_definition_zh) + "\n"
                                    + (arr[0]) + "\n\n"
                    );
                    if (!((arr[0]).isEmpty())) {
                        resultView.addView(localCard);
                    }
                    break;
                default:
                    localCard.setText(
                            getResources().getString(R.string.result_definition_zh) + "\n"
                                    + (arr[0]) + "\n\n"
                                    + getResources().getString(R.string.result_definition_en) + "\n"
                                    + (arr[1]) + "\n"
                    );
                    if (!((arr[0]).isEmpty()
                            && (arr[1]).isEmpty())) {
                        resultView.addView(localCard);
                    }
                    break;
            }
        }

        //La Simpla Vortaro 结果
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("settings_simpla_vortaro", true)) {//设置中是否开启
            new Thread(runnable).start();
            Looper looper = Looper.myLooper();
            messageHandler = new MessageHandler(looper);
        }

		//本地词典结果
		DictionaryOpenHelper helper = new DictionaryOpenHelper();
		String [] dictList = helper.listExistDictionaries(this);
		if (dictList != null)
		{
            for (String s : dictList) { //遍历词典
                //第 i 个词典的搜索结果
                String result = helper.search(this, DictionaryOpenHelper.DEFAULT_DIRECTORY + "/" + s, word);
                if (result != null) {
                    //创建卡片用于显示结果
                    AppCard localDictCard = new AppCard(this, AppCard.TYPE_DICTIONARY);
                    localDictCard.setTitle(s);
                    localDictCard.setText(result);
                    resultView.addView(localDictCard);
                }
            }
            for (String s : dictList) {
                if ((s).endsWith(".output")
                        || (s).endsWith(".inflated")
                        || (s).endsWith(".idx")
                        || (s).endsWith(".words")
                        || (s).endsWith(".ld2.xml")
                ) {
                    File file = new File(DictionaryOpenHelper.DEFAULT_DIRECTORY + "/" + s);
                    file.delete();
                }
            }
		}
    }

    Runnable runnable = new Runnable(){//获取La Simpla Vortaro的数据
        @Override
        public void run()
        {
            String url="http://www.simplavortaro.org/api/v1/vorto/" + addHat(word);
            try {
                url = "http://www.simplavortaro.org/api/v1/vorto/" + URLEncoder.encode(addHat(word), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            boolean isWordExists = true;
            String SimplaVortaroResult = "";
            StringBuffer SimplaVortaroResultBuffer = new StringBuffer();
            BufferedReader in = null;

            String definition = "";
            String example = "";
            String english = "";
            try
            {
                URL realUrl = new URL(url);
                URLConnection connection = realUrl.openConnection();
                connection.connect();
                //BufferedReader读取URL响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null)
                {
                    SimplaVortaroResultBuffer.append(line);
                }
                SimplaVortaroResult = SimplaVortaroResultBuffer.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                isWordExists = false;
            }
            //关闭输入流
            finally
            {
                try
                {
                    if (in != null)
                    {
                        in.close();
                    }
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
            //json数据
            if (isWordExists)
            {
                //搜索释意（defino）
                int pointer = SimplaVortaroResult.indexOf("{\"difino\":");
                do
                {
                    pointer = SimplaVortaroResult.indexOf("{\"difino\":", pointer - 1);
                    String s = "";
                    if (pointer != -1)
                    {
                        for (pointer = pointer + 12;(Character.toString(SimplaVortaroResult.charAt(pointer))).compareTo("\"") != 0;pointer ++)
                        {
                            s = s + SimplaVortaroResult.charAt(pointer);
                        }}
                    System.out.println(s);//definition
                    definition = definition + s + "\n";
                }while(pointer != -1);

                //搜索例句（ekzemplo）
                pointer = SimplaVortaroResult.indexOf("{\"ekzemplo\":");
                do
                {
                    pointer = SimplaVortaroResult.indexOf("{\"ekzemplo\":", pointer - 1);
                    String s0 = "";
                    if (pointer != -1)
                    {
                        for (pointer = pointer + 14;(Character.toString(SimplaVortaroResult.charAt(pointer))).compareTo("\"") != 0;pointer ++)
                        {
                            s0 = s0 + SimplaVortaroResult.charAt(pointer);
                        }}
                    System.out.println(s0);//example
                    example = example + s0 + "\n";
                }while(pointer != -1);

                pointer = SimplaVortaroResult.indexOf("{\"kodo\": \"en\", \"traduko\": \"");
                do
                {
                    pointer = SimplaVortaroResult.indexOf("{\"kodo\": \"en\", \"traduko\": \"", pointer - 1);
                    String s1 = "";
                    if (pointer != -1)
                    {
                        for (pointer = pointer + 27;(Character.toString(SimplaVortaroResult.charAt(pointer))).compareTo("\"") != 0;pointer ++)
                        {
                            s1 = s1 + SimplaVortaroResult.charAt(pointer);
                        }}
                    System.out.println(s1);//example
                    english = english + s1 + "\n";
                }while(pointer != -1);

                //添加
                String total = "—" + addHat(word) + "\n\n" + getResources().getString(R.string.result_definition_en) + "\n"
                        + english + "\n"
                        + getResources().getString(R.string.result_definition_eo) + "\n" + definition + "\n"
                        + getResources().getString(R.string.result_examples) + "\n" + example + "\n";
                total = EsperantoUnicodeToCharacter(total);
                AppCard card = new AppCard(context, AppCard.TYPE_DICTIONARY);//如第一个参数传 getApplicationContext() 会导致低版本下 AppCard 上覆一层灰色
                card.setTitle(getResources().getString(R.string.result_la_simpla_vortaro));
                card.setText(total);
                Message message = Message.obtain();
                message.obj = card;
                messageHandler.sendMessage(message);
				
            }else{
				//在 isWordExists 为 false 的情况下尝试模糊搜索 
				SimplaVortaroResult = "";//清空 SimplaVortaroResult 准备把 trovi 的结果赋值给它 
				String contentWords = "";//存储 malpreciza 后面 [] 之间的内容
				
				try
				{
					URL urlSearch = new URL("http://www.simplavortaro.org/api/v1/trovi/" + addHat(word));
					URLConnection connection = urlSearch.openConnection();
					connection.connect();
					in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while ((line = in.readLine()) != null)
					{
						SimplaVortaroResult += line;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				if((SimplaVortaroResult.contains("\"malpreciza\""))
				   && (!SimplaVortaroResult.contains("\"malpreciza\": []"))){
					   //trovi 的结果中有模糊搜索的词汇
					int pointer = SimplaVortaroResult.indexOf("{\"malpreciza\":");
					do
					{
						pointer = SimplaVortaroResult.indexOf("{\"malpreciza\":", pointer - 1);
						String s = "";
						if (pointer != -1)
						{
							for (pointer = pointer + 17;(Character.toString(SimplaVortaroResult.charAt(pointer))).compareTo("\"") != 0;pointer ++)
							{
								s = s + SimplaVortaroResult.charAt(pointer);
							}
						}
						contentWords = contentWords + s + "\n";
					}while(pointer != -1);
					//contentWords 已取得 malpreciza 后 [] 内的内容
					//将模糊搜索结果显示出来
					AppCard card = new AppCard(context, AppCard.TYPE_DICTIONARY);
					card.setTitle(getResources().getString(R.string.result_la_simpla_vortaro));
					card.setText(getResources().getString(R.string.result_do_you_mean) + " \n\n" + EsperantoUnicodeToCharacter(contentWords));
					Message message = Message.obtain();
					message.obj = card;
					messageHandler.sendMessage(message);
				}
			}
        }
    };

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            this.startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setClass(ResultActivity.this, MainActivity.class));
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    class MessageHandler extends Handler
    {
        public MessageHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            resultView.addView((AppCard)msg.obj);
        }
    }

    public String EsperantoUnicodeToCharacter(String s){
        s = s.replace("\\u0108","Ĉ");
        s = s.replace("\\u0109","ĉ");
        s = s.replace("\\u011c","Ĝ");
        s = s.replace("\\u011d","ĝ");
        s = s.replace("\\u0124","Ĥ");
        s = s.replace("\\u0125","ĥ");
        s = s.replace("\\u0134","Ĵ");
        s = s.replace("\\u0135","ĵ");
        s = s.replace("\\u015c","Ŝ");
        s = s.replace("\\u015d","ŝ");
        s = s.replace("\\u016c","Ŭ");
        s = s.replace("\\u016d","ŭ");
        return s;
    }

    public String ToUpperCase(String s){
        s = s.toUpperCase();
        s = s.replace("ĉ","Ĉ");
        s = s.replace("ĝ","Ĝ");
        s = s.replace("ĥ","Ĥ");
        s = s.replace("ĵ","Ĵ");
        s = s.replace("ŝ","Ŝ");
        s = s.replace("ŭ","Ŭ");
        return  s;
    }

    public String ToLowerCase(String s){
        s = s.toLowerCase();
        s = s.replace("Ĉ","ĉ");
        s = s.replace("Ĝ","ĝ");
        s = s.replace("Ĥ","ĥ");
        s = s.replace("Ĵ","ĵ");
        s = s.replace("Ŝ","ŝ");
        s = s.replace("Ŭ","ŭ");
        return  s;
    }

    public String addHat(String s){
        s = s.replace("Cx","Ĉ");
        s = s.replace("cx","ĉ");
        s = s.replace("Gx","Ĝ");
        s = s.replace("gx","ĝ");
        s = s.replace("Hx","Ĥ");
        s = s.replace("hx","ĥ");
        s = s.replace("Jx","Ĵ");
        s = s.replace("jx","ĵ");
        s = s.replace("Sx","Ŝ");
        s = s.replace("sx","ŝ");
        s = s.replace("Ux","Ŭ");
        s = s.replace("ux","ŭ");
        return s;
    }

}
