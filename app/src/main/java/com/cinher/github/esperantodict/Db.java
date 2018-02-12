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
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
}
