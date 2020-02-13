package com.cinher.github.esperantodict.ui.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cinher.github.esperantodict.MainActivity;
import com.cinher.github.esperantodict.ResultActivity;
import com.cinher.github.esperantodict.TranslateActivity;
import com.cinher.github.esperantodict.CorpusActivity;
import com.cinher.github.esperantodict.Db;
import com.cinher.github.esperantodict.R;
import com.nex3z.flowlayout.FlowLayout;

public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View root;
        System.out.println(getArguments().getInt(ARG_SECTION_NUMBER));
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            root = inflater.inflate(R.layout.content_main, container, false);

            FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.main_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //搜索历史
                    final Db db = new Db(getContext());
                    final SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor c = dbRead.query("history", null, null, null, null, null, null);

                    FlowLayout layout = new FlowLayout(getContext());
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7, getResources().getDisplayMetrics());
                    int margin = (int) getResources().getDimension(R.dimen.widget_margin);

                    View.OnClickListener ocl = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView tv = (TextView) v;
                            startActivity(new Intent().setClass(getActivity(), ResultActivity.class).putExtra("word", tv.getText().toString()));
                        }
                    };

                    while (c.moveToNext()) {//添加TextView到Flow Layout
                        String word = c.getString(c.getColumnIndex("word"));
                        System.out.println("Database history: " + word);
                        TextView textView = new TextView(getContext());
                        textView.setTextSize(sp);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textView.setBackgroundDrawable(getContext().getDrawable(R.drawable.label_bg));
                        }
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
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.main_history_search_history))
                            .setView(layout)
                            .setPositiveButton(getResources().getString(R.string.main_history_clear_all), new DialogInterface.OnClickListener(){
                                //点击时，删除所有记录
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.getWritableDatabase().execSQL("DELETE FROM history");
                                }
                            })
                            .setNeutralButton(getResources().getString(android.R.string.cancel), null)
                            .show();
                }
            });

            /*
             * 在主界面上有三个前往不同搜索结果的方式
             * 1. 直接在搜索框输入时点击输入法中的搜索按钮
             * 2. 点击按钮搜索词典
             * 3. 点击按钮搜索语料库
             */

            //1. 直接在输入法回车 默认搜索词典
            ((EditText) root.findViewById(R.id.main_search_edit_text))
                    .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                                String word = ((EditText) root.findViewById(R.id.main_search_edit_text)).getText().toString();
                                if (!word.trim().equals("")) {
                                    searchInDictionary(word);
                                } else {
                                    Snackbar.make(root.findViewById(R.id.main_search_edit_text), getResources().getString(R.string.main_empty), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                return true;
                            }
                            return false;

                        }
                    });

            //2&3. 点击按钮搜索词典或语料库
            ((Button) root.findViewById(R.id.main_dictionary_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String word = ((EditText) root.findViewById(R.id.main_search_edit_text)).getText().toString();
                    if (!word.trim().equals("")) {
                        searchInDictionary(word);
                    } else {
                        Snackbar.make(root.findViewById(R.id.main_search_edit_text), getResources().getString(R.string.main_empty), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
            ((Button) root.findViewById(R.id.main_corpus_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String word = ((EditText) root.findViewById(R.id.main_search_edit_text)).getText().toString();
                    if (!word.trim().equals("")) {
                        addWordToHistoryDatabase(word);
                        startActivity(new Intent().setClass(getActivity(), CorpusActivity.class).putExtra("word", word));
                    } else {
                        Snackbar.make(root.findViewById(R.id.main_search_edit_text), getResources().getString(R.string.main_empty), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        } else {
            root = inflater.inflate(R.layout.content_translate, container, false);
            ((FloatingActionButton) root.findViewById(R.id.translate_fab)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageTranslateEsperanto(root);
                }
            });
        }
        return root;
    }

    @Override
    public void onClick(View view) {

    }

    //这个方法用来搜索词典，直接开启一个新的 Activity
    private void searchInDictionary(String word){
        //将搜索记录添加到数据库
        addWordToHistoryDatabase(word);
        //进入 ResultActivity
        startActivity(new Intent().setClass(getActivity(), ResultActivity.class).putExtra("word", word));
        //MainActivity.this.finish();
    }

    //将一个单词添加到搜索记录数据库
    private void addWordToHistoryDatabase(String word){
        Db db = new Db(getContext());
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
    }

    public void sendMessageTranslateEsperanto(View view) {
        EditText editText = (EditText) view.findViewById(R.id.translate_edit_message);
        EditText editText2 = (EditText) view.findViewById(R.id.translate_edit_message2);
        String message = editText.getText().toString();
        if (message.compareTo(TranslateActivity.donate) == 0) {
            editText.setText("");
            editText2.setText("");
        } else {
            try {
                editText2.setText(TranslateActivity.translateEsperanto(message));
            } catch (RuntimeException ee) {
                System.out.println(ee.toString());
                editText2.setText("[Untranslatable]");
            }
            if (editText2.getText().toString().equals("No text. Neniu teksto.")) editText.setText("");
        }
    }
}
