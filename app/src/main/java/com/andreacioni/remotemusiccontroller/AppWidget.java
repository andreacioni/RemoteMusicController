package com.andreacioni.remotemusiccontroller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AppWidget extends AppWidgetProvider 
{	
	private static final String WIDGET_BUTTON_PLAY = "bPlay";
	private static final String WIDGET_BUTTON_NEXT = "bNext";
	private static final String WIDGET_BUTTON_PREV = "bPrev";
	
	private RemoteViews remoteViews;
	private int[] appWidgetIds;   
	
    private WiFiMng wMng;
    
	private static final String TAG ="RemoteMusicController: Widget";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(TAG,"onUpdate");
		
		//this.appWidgetIds = appWidgetIds;

        /*remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        watchWidget = new ComponentName(context, AppWidget.class);
        
        remoteViews.setOnClickPendingIntent(R.id.widgetPlay, getPendingSelfIntent(context, WIDGET_BUTTON_PLAY));
        remoteViews.setOnClickPendingIntent(R.id.widgetNext, getPendingSelfIntent(context, WIDGET_BUTTON_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.buttonPrev, getPendingSelfIntent(context, WIDGET_BUTTON_PREV));*/
		
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);

	    Intent active = new Intent(context, AppWidget.class);
	    active.setAction(WIDGET_BUTTON_PLAY);
	    PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
	    remoteViews.setOnClickPendingIntent(R.id.widgetPlay, actionPendingIntent);

	    active = new Intent(context, AppWidget.class);
	    active.setAction(WIDGET_BUTTON_NEXT);
	    actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
	    remoteViews.setOnClickPendingIntent(R.id.widgetNext, actionPendingIntent);

	    active = new Intent(context, AppWidget.class);
	    active.setAction(WIDGET_BUTTON_PREV);
	    actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
	    remoteViews.setOnClickPendingIntent(R.id.widgetPrev, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        
        this.appWidgetIds = appWidgetIds;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		super.onReceive(context, intent);
		Log.d(TAG, "onReceive");
		
		wMng = RemoteSwitcherMain.getWiFiMng();
			
		
		if (WIDGET_BUTTON_PLAY.equals(intent.getAction())) 
		{
			if(wMng==null)
			{
				Toast.makeText(context, "Keep the application in background!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			wMng.sendAction(Globals.WIDGET_PLAY_PAUSE,context);
			//Toast.makeText(context, "PLAYER_PAUSE", Toast.LENGTH_LONG).show();
			
		}
		
		if(WIDGET_BUTTON_NEXT.equals(intent.getAction()))
		{
			if(wMng==null)
			{
				Toast.makeText(context, "Keep the application in background!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			wMng.sendAction(Globals.WIDGET_NEXT,context);
			//Toast.makeText(context, "PLAYER_NEXT", Toast.LENGTH_SHORT).show();
		}
		
		if(WIDGET_BUTTON_PREV.equals(intent.getAction()))
		{
			if(wMng==null)
			{
				Toast.makeText(context, "Keep the application in background!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			wMng.sendAction(Globals.WIDGET_PREV,context);
			//Toast.makeText(context, "PLAYER_PREVIOUS", Toast.LENGTH_SHORT).show();
		}
		
		/*if(remoteViews!=null)
		{
			remoteViews.setTextViewText(R.id.widgetText, "Ciao");
			AppWidgetManager app = AppWidgetManager.getInstance(context);
			app.updateAppWidget(appWidgetIds, remoteViews);
		}*/
		
		
		return;
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
		Toast t = Toast.makeText(context, "Remember to run the application in Remote Controller mode! ", Toast.LENGTH_LONG);
		t.show();
	}
	
	protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
	
}
