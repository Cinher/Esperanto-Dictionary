package com.cinher.github.esperantodict;
import android.appwidget.*;
import android.content.*;
import android.widget.*;
import android.app.*;

public class DictWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
        final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
            int appId = appWidgetIds[i];
            Intent intent = new Intent(context, WidgetPopupActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			
            PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_text_view, pendIntent);
            appWidgetManager.updateAppWidget(appId,views);
    	}

	}
}
