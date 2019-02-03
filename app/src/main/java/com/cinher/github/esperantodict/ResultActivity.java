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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import android.widget.*;
import android.util.*;
import java.io.*;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;

public class ResultActivity extends AppCompatActivity {

    String word; //将要搜索的单词
    LinearLayout resultView;
    private Handler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        word = getIntent().getStringExtra("word");
        Toolbar toolbar = (Toolbar) findViewById(R.id.resultToolbar);
        toolbar.setTitle(word);
        setSupportActionBar(toolbar);

        resultView = (LinearLayout) findViewById(R.id.resultCentralView);

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
					Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
				}else {
					dbWrite.insert("favorites", null, cv);
					Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
				}
				dbWrite.close();
            }
        });

        //本地结果
        AppCard localCard = new AppCard(this, AppCard.TYPE_DICTIONARY);
        localCard.setTitle(getResources().getString(R.string.local_interpretation));
        localCard.setText(
                getResources().getString(R.string.chinese)+"\n"
                +(GetLocalResultModule.GetLocalTranslationResult(this,word)[0])+"\n\n"
                + getResources().getString(R.string.english)+"\n"
                +(GetLocalResultModule.GetLocalTranslationResult(this,word)[1])+"\n"
        );
        if(!((GetLocalResultModule.GetLocalTranslationResult(this,word)[0]).isEmpty()
        && (GetLocalResultModule.GetLocalTranslationResult(this,word)[1]).isEmpty() ))
        {
            resultView.addView(localCard);
        }else{

        }

        //La Simpla Vortaro 结果
        new Thread(runnable).start();
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
		
		//本地词典结果
		DictionaryOpenHelper helper = new DictionaryOpenHelper();
		String [] dictList = helper.listExistDictionaries(this);
		if (dictList != null)
		{
			for (int i = 0; i < dictList.length; i++)
			{ //遍历词典
				Log.d("ResultActivity", "" + dictList[i]);
				//第 i 个词典的搜索结果
				String result = helper.search(this, DictionaryOpenHelper.DEFAULT_DIRECTORY + "/" + dictList[i], word);
				if (result != null)
				{
					Log.d("ResultActivity", result);
					//创建卡片用于显示结果
					AppCard localDictCard = new AppCard(this, AppCard.TYPE_DICTIONARY);
					localDictCard.setTitle(dictList[i]);
					localDictCard.setText(result);
					resultView.addView(localDictCard);
				}
			}
			for (int i = 0; i < dictList.length; i++) //删除临时文件
			{
				if ((dictList[i]).endsWith(".output")
					|| (dictList[i]).endsWith(".inflated")
					|| (dictList[i]).endsWith(".idx")
					|| (dictList[i]).endsWith(".words")
					|| (dictList[i]).endsWith(".ld2.xml")
					)
				{
					File file = new File(DictionaryOpenHelper.DEFAULT_DIRECTORY + "/" + dictList[i]);
					file.delete();
				}
			}
		}
    }

    Runnable runnable = new Runnable(){//获取La Simpla Vortaro的数据
        @Override
        public void run()
        {
            String url="http://www.simplavortaro.org/api/v1/vorto/" + word;
            boolean isWordExists = true;
            String SimplaVortaroResult = "";
            BufferedReader in = null;

            String definition = "";
            String example = "";
            String english = "";
            try
            {
                String urlNameString = url;
                URL realUrl = new URL(urlNameString);
                URLConnection connection = realUrl.openConnection();
                connection.connect();
                // 获取响应头字段
                Map<String, List<String>> map = connection.getHeaderFields();
                //BufferedReader读取URL响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null)
                {
                    SimplaVortaroResult += line;
                }
            }
            catch (Exception e)
            {
                //Log.d("Debug", "Exception Caught: " + e);
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
                String total = "—" + word + "\n\n" + getResources().getString(R.string.english) + "\n" + english + "\n" + getResources().getString(R.string.definition) + "\n" + definition + "\n" + getResources().getString(R.string.examples) + "\n" + example + "\n";
                total = EsperantoUnicodeToCharacter(total);
                AppCard card = new AppCard(getApplicationContext(), AppCard.TYPE_DICTIONARY);
                card.setTitle("La Simpla Vortaro");
                card.setText(total);
                Message message = Message.obtain();
                message.obj = card;
                messageHandler.sendMessage(message);
				
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setClass(ResultActivity.this, MainActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

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

}
