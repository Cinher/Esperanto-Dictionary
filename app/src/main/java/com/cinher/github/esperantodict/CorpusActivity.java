package com.cinher.github.esperantodict;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;

public class CorpusActivity extends AppCompatActivity {

    public static String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //初始化
        super.onCreate(savedInstanceState);
        word = getIntent().getStringExtra("word");
        setContentView(R.layout.activity_corpus);
        String word = getIntent().getStringExtra("word");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("Corpus", bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();

            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.corpus_toolbar);
            toolbar.setTitle(word);
        }
    }
}
