package com.cinher.github.esperantodict;
import android.database.sqlite.*;
import android.content.*;

public class Db extends SQLiteOpenHelper
{
	public Db(Context context){
		super(context, "db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("CREATE TABLE history(word TEXT)");
		p1.execSQL("CREATE TABLE dictionaries(path TEXT)");//用户导入的词典路径存储
		p1.execSQL("CREATE TABLE favorites(word TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{

	}
}
