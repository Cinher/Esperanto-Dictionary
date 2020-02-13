package com.cinher.github.esperantodict;
import android.content.res.*;
import java.io.*;
import org.xmlpull.v1.*;
import android.content.*;

public class GetLocalResultModule
{
    public static int SEARCH_IN_CHINESE = 0;
    public static int SEARCH_IN_ENGLISH = 1;

	//输入世界文，返回中文和英文
	public static String[] GetLocalTranslationResult(Context context, String word){
        String [] resultStrings ={"",""};//搜索的结果，中文和英文
        //搜索中文翻译
		XmlResourceParser parserLocal = context.getResources().getXml(R.xml.eo_zh);
		try
		{
			while (parserLocal.getEventType() != XmlResourceParser.END_DOCUMENT)
			{
				if (parserLocal.getEventType() == XmlResourceParser.START_TAG)
				{
					if (parserLocal.getName().equals("word"))
					{
						String en = parserLocal.getAttributeValue(null, "eo");
						if (en.equals(word))
						{
							resultStrings [0] = parserLocal.getAttributeValue(null, "zh");
						}
					}
				}
				parserLocal.next();
			}
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

        //搜索英文翻译
        parserLocal = context.getResources().getXml(R.xml.eo_en);
        try
        {
            while (parserLocal.getEventType() != XmlResourceParser.END_DOCUMENT)
            {
                if (parserLocal.getEventType() == XmlResourceParser.START_TAG)
                {
                    if (parserLocal.getName().equals("word"))
                    {
                        String en = parserLocal.getAttributeValue(null, "eo");
                        if (en.equals(word))
                        {
                            resultStrings [1] = parserLocal.getAttributeValue(null, "en");
                        }
                    }
                }
                parserLocal.next();
            }
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //返回搜索结果
		return resultStrings;
	}

	//输入中文或英文，返回世界文及其释义
//	public static String GetLocalEsperantoResult(Context context, String word, int searchType){
//        XmlResourceParser parserLocal;
//		String result = "";
//        String tagType = "";
//
//        //判断源语言
//        if (searchType == SEARCH_IN_CHINESE){
//            parserLocal = context.getResources().getXml(R.xml.eo_zh);
//            //create a xml file "zh_eo"
//            //replace "eo_zh" with "zh_eo"
//            tagType = "zh";
//        }else{
//            parserLocal = context.getResources().getXml(R.xml.eo_en);
//            tagType = "en";
//        }
//
//        try
//        {
//            while (parserLocal.getEventType() != XmlResourceParser.END_DOCUMENT)
//            {
//                if (parserLocal.getEventType() == XmlResourceParser.START_TAG)
//                {
//                    if (parserLocal.getName().equals("word"))
//                    {
//                        String targetText = parserLocal.getAttributeValue(null, tagType);
//                        if (targetText.contains(word))//包含要找的字段
//                        {
//                            result = result
//                                    + parserLocal.getAttributeValue(null, "eo")
//                                    + "\n\n"
//                                    + targetText;
//                        }
//                    }
//                }
//                parserLocal.next();
//            }
//        }
//        catch (XmlPullParserException e)
//        {
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//		return result;
//	}
}
