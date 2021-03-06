package com.cinher.github.esperantodict;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CorpusView extends LinearLayout {

    Context context;
    String word;
    View view;
    LinearLayout linearLayout;//显示语料库中条目的界面，在其中添加书名或语料库单项
    private Handler messageHandler;

    int loopTimes;
    int characterNumber;

    public CorpusView(Context c) {
        this(c, null);
    }

    public CorpusView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public CorpusView(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        init(c);
    }

    //初始化
    private void init(@NonNull Context c) {
        context = c;
        word = CorpusActivity.word;
        word = addHat(word);//将 x 形式字母转换为帽子字母
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.app_corpus_view, this, true);
        }
        linearLayout = (LinearLayout) view.findViewById(R.id.app_corpus_view_linear_layout);

        //读取设置
        switch (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("settings_search_maximum", "10")) {
            case "5":
                loopTimes = 5;
                break;
            case "10":
                loopTimes = 10;
                break;
            default:
                loopTimes = 20;
                break;
        }
        switch (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("settings_number_of_characters", "100")) {
            case "50":
                characterNumber = 50;
                break;
            case "100":
                characterNumber = 100;
                break;
            default:
                characterNumber = 200;
                break;
        }

        //启动新线程。新线程会在语料库中搜索要找的词汇，并把结果显示出来
        new Thread(runnable).start();
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
    }

    //该线程会在语料库中搜索，并将搜索结果动态添加到 CorpusActivity 上
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            searchWordInCorpus(word);
        }
    };


    //输入要搜索的词汇，在语料库中进行搜索，并自动将结果动态显示在屏幕上
    private void searchWordInCorpus(String word){
        /*
        * 添加新书方法：
        * 将新书以 txt 格式保存，编码为 UTF-8（Windows 记事本），添加到 assets/e_books/ 下
        * 在下面的 bookNames 中添加书名 + ".txt"，即可正常搜索
        */
        String [] bookNames = {"Dua Libro de l' Lingvo Internacia.txt",
                "Fundamenta Krestomatio.txt",
                "Hamleto, Reĝido de Danujo.txt",
                "Ifigenio en Taŭrido.txt",
                "La Batalo de l' Vivo.txt",
                "El la Biblio.txt"};
        for (String bookName : bookNames) {
            addRow(((bookName).replace(".txt", "")));//显示该书的书名
            if (bookName.equals("Fundamenta Krestomatio.txt")) {
                searchWordInBook(getBookFromAssets(bookName), removeHat(word));
            } else {
                searchWordInBook(getBookFromAssets(bookName), word);
            }
        }

    }

    //传入 String 形式的书的内容和要搜索的单词，搜索几个包含该词汇的示例，通过 addRow 把结果输出到界面上
    private void searchWordInBook(@NonNull String book, @NonNull String word){
        int loopCount;
        int index = 0;
        int bookLength = book.length();
        int wordLength = word.length();
        //遍历全书搜索单词
        for(loopCount = 0; loopCount < loopTimes/*一本书中最多搜索出结果的个数*/; loopCount ++){
            index = book.indexOf(word, (index + 1));
            if(index == -1) {
                break;
            }
            if(index < characterNumber){//找到的单词位置过于靠前
                addRow(book.substring(0,index),word,book.substring((index + wordLength),(index + wordLength + characterNumber)));
            }else if ((bookLength - index) <= characterNumber){//单词位置过于靠后
                addRow(book.substring((index - characterNumber),index),word,book.substring((index + wordLength),bookLength));
            }else{
                addRow(book.substring((index - characterNumber),index),word,book.substring((index + wordLength),(index + wordLength + characterNumber)));
            }
        }
    }

    //输入书名（无需路径），返回 String 形式的在 assets/e_books/ 目录下的电子书的内容
    @NonNull
    private String getBookFromAssets(String name){
        name = "e_books/" + name;//修改为 assets 下的完整路径
        String line;
        StringBuilder stringBuilder = new StringBuilder();//临时存储电子书的内容
        try {
            InputStreamReader reader = new InputStreamReader(getResources().getAssets().open(name));
            BufferedReader bufferedReader = new BufferedReader(reader);
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);//将新读取的 line 的内容添加到 stringBuilder 中存储
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return(stringBuilder.toString());//将 StringBuilder 转换为 String 并返回
    }

    //添加书名
    public void addRow(String title){
        TextView tv = new TextView(context);//显示书名的 TextView
        tv.setText("\n " + title);
        tv.setTypeface(Typeface.MONOSPACE);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);//加粗标题
        tv.setTextColor(getResources().getColor(R.color.colorAccent));//设置特殊的颜色

        //用 MessageHandler 将表示书名的 TextView 添加到 linearLayout 上
        Message message = Message.obtain();
        message.obj = tv;
        messageHandler.sendMessage(message);
    }

    //添加语料库单项
    public void addRow(String pre, String word, String suf){
        TextView tvPre = new TextView(context);//单词左边部分的文字
        tvPre.setText(pre + "");
        tvPre.setTypeface(Typeface.MONOSPACE);

        TextView tvWord = new TextView(context);//显示单词的 TextView
        tvWord.setText(word + "");
        tvWord.setTypeface(Typeface.MONOSPACE);
        TextPaint tp = tvWord.getPaint();
        tp.setFakeBoldText(true);//加粗单词
        tvWord.setTextColor(getResources().getColor(R.color.colorAccent));//为单词设置颜色

        TextView tvSuf = new TextView(context);//单词右边部分的文字
        tvSuf.setText(suf + "");
        tvSuf.setTypeface(Typeface.MONOSPACE);

        LinearLayout innerLinerLayout = new LinearLayout(context);
        innerLinerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLinerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        innerLinerLayout.addView(tvPre);
        innerLinerLayout.addView(tvWord);
        innerLinerLayout.addView(tvSuf);

        //用 MessageHandler 将搜索结果 innerLinearLayout 添加到界面上
        Message message = Message.obtain();
        message.obj = innerLinerLayout;
        messageHandler.sendMessage(message);
    }

    class MessageHandler extends Handler
    {
        MessageHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            linearLayout.addView((View) msg.obj);
        }
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

    public String removeHat(String s){
        s = s.replace("Ĉ","Cx");
        s = s.replace("ĉ","cx");
        s = s.replace("Ĝ","Gx");
        s = s.replace("ĝ","gx");
        s = s.replace("Ĥ","Hx");
        s = s.replace("ĥ","hx");
        s = s.replace("Ĵ","Jx");
        s = s.replace("ĵ","jx");
        s = s.replace("Ŝ","Sx");
        s = s.replace("ŝ","sx");
        s = s.replace("Ŭ","Ux");
        s = s.replace("ŭ","ux");
        return s;
    }

}
