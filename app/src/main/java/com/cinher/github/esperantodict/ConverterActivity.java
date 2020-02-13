package com.cinher.github.esperantodict;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConverterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        //初始化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(0xff00aa8d, typedValue, true);
            int color = getResources().getColor(R.color.colorPrimary);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_small);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), bm, color);

            setTaskDescription(taskDescription);
            bm.recycle();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }

        final EditText editText = (EditText) findViewById(R.id.converter_edit_text);
        ((Button) findViewById(R.id.converter_button_1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                text = removeHat(text);
                editText.setText(text);
            }
        });
        ((Button) findViewById(R.id.converter_button_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                text = addHat(text);
                editText.setText(text);
            }
        });
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
