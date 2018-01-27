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
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_text_view, pendIntent);
            appWidgetManager.updateAppWidget(appId,views);
    	}

	}
}
