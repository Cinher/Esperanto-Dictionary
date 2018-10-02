package com.cinher.github.esperantodict;

import java.io.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import android.util.*;

public class DictionaryOpenHelper
{
	public static final int DICT_TYPE_LD2 = 0;
	public static final int DICT_TYPE_MDX = 1;
	
	public static final String DEFAULT_DIRECTORY = "/storage/emulated/0/EsperantoDict";
	
	public DictionaryOpenHelper(){
		
	}
	
	public boolean importDictionary(Context context ,String path, int type){
		//格式： /storage/emulated/0/.../abc.xyz
		File fromFile = new File(path);
		File toFile = new File(DEFAULT_DIRECTORY + "/" + fromFile.getName());
		copyFile(context, fromFile, toFile);
		return true;
	}
	
	public String search(Context context,String path, String word){//输入词汇，返回该词的解释，否则返回 null
		if (path.endsWith(".ld2"))
		{
			try
			{
				return LingoesLd2Reader.main(null, path, word.trim());
			}
			catch (IOException e)
			{
				Toast.makeText(context, "Error: Dictionary not found. e.Message =  " + e.toString(), Toast.LENGTH_LONG).show();
			}
		}
		return null;
	}
	
	public String [] listExistDictionaries(Context context) throws IOException{
		File defaultDir = new File(DEFAULT_DIRECTORY);
		if(!defaultDir.exists()){
			Log.d("listExistDictionary","Dir not exists");
			return null;
		}
		if(!defaultDir.isDirectory()){
			Toast.makeText(context, "Error: Not a directory", Toast.LENGTH_LONG).show();
			return null;
		}
		String [] dictionaryNameList = defaultDir.list();
		return dictionaryNameList;
	}
	
	public boolean isDictionaryExistsInDefaultDirectory(Context context, String name){
		try
		{
			if((Arrays.binarySearch(listExistDictionaries(context), name)) >= 0){
				return true;
			}
		}
		catch (IOException e)
		{
			
		}
		return false;
	}
	
	private static void copyFile(Context context, File fromFile, File toFile) 
	{
		if ((!fromFile.exists()) || (!fromFile.isFile()) || (!fromFile.canRead()))
		{
			Toast.makeText(context, "Error: File not found", Toast.LENGTH_LONG).show();
			return;
		}
		if (!toFile.getParentFile().exists())
		{
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists())
		{
			toFile.delete();
		}
		try
		{
			java.io.FileInputStream fromInputStream = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream toOutputStream = new java.io.FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fromInputStream.read(bt)) > 0)
			{
				toOutputStream.write(bt, 0, c);
			}
			fromInputStream.close();
			toOutputStream.close();
		}
		catch (Exception e)
		{
			Toast.makeText(context, "Error: File not found. e.Message =  " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
