package com.andreacioni.remotemusiccontroller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RemoteMusicSwitcherService extends Service
{
	private static final String TITLE = "NotificationService";
	private static byte actionToDo = Globals.PLAYER_NOTHING;
	private static final int notificationId = 1;
	
	private WiFiMng wMng = new WiFiMng();
	private MusicMng mMng = new MusicMng();
	
	private BroadcastReceiver mReceiver;
	
	public static String currentPlayed = "";

	@Override
	public void onCreate() {
		super.onCreate();	
		
		Log.i(TITLE,"onCreate service");
		
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged2");
		//iF.addAction(Intent.ACTION_MEDIA_BUTTON);
		
		new DoActionThread();
		
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				Log.d(TITLE,"Receiving broadcast intent");
				
				String cmd = intent.getStringExtra("command");
				Log.d(TITLE,"mIntentReceiver.onReceive: " + action + " / " + cmd);
				String artist = intent.getStringExtra("artist");
				String album = intent.getStringExtra("album");
				String track = intent.getStringExtra("track");
				Log.d(TITLE,"Music info"+artist+":"+album+":"+track);
				
				currentPlayed = track;
				
			}
		};
		
		registerReceiver(mReceiver, iF);
		
		//Costruzione notifica
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.iconotify_mdpi)
		        .setContentTitle("Remote Music Switcher")
		        .setContentText("Media Player running...");
		
		//Creazione intent per avvio applicazione quando si clicca sulla notifica
		Intent i = new Intent(this,RemoteSwitcherMain.class);
		PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    this,
			    0,
			    i,
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
		
		mBuilder.setContentIntent(resultPendingIntent);
		
		//Richiedo un istanza del NotificationManager
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(notificationId, mBuilder.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i(TITLE, "onStartCommand service");
		
		return START_STICKY;
	}
	  
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TITLE, "onDestroy service");
		
		unregisterReceiver(mReceiver);
		
		actionToDo = Globals.KILLTHREAD;
		
		wMng.release();
		
		//Richiedo un istanza del NotificationManager
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.cancel(notificationId);
		
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TITLE, "onBind service");
		return null;
	}
	
	public static void receiveFromWiFiMng(byte action)
	{
		actionToDo = action;
	}
	
	private void sendActionToBroadcast(byte action)
	{
		
		switch(action)
		{
			case Globals.PLAYER_NEXT:
			case Globals.WIDGET_NEXT:
			{
				sendBroadcast(mMng.nextTrack());
				sendBroadcast(mMng.nextTrack());
				
				break;
			}
			case Globals.PLAYER_PAUSE:
			{
				sendBroadcast(mMng.pauseTrack());
				sendBroadcast(mMng.pauseTrack());
				
				break;
			}
			case Globals.PLAYER_PLAY:
			case Globals.WIDGET_PLAY_PAUSE:
			{
				sendBroadcast(mMng.playTrack());
				sendBroadcast(mMng.playTrack());
				
				break;
			}
			case Globals.PLAYER_PREVIOUS:
			case Globals.WIDGET_PREV:
			{
				sendBroadcast(mMng.previousTrack());
				sendBroadcast(mMng.previousTrack());
				
				break;
			}
			case Globals.VOLUME_UP:
			{
				mMng.MusicVolumeUp((AudioManager)getSystemService(Context.AUDIO_SERVICE));		
				
				break;
			}
			case Globals.VOLUME_DOWN:
			{
				mMng.MusicVolumeDown((AudioManager)getSystemService(Context.AUDIO_SERVICE));
				
				break;
			}
			case Globals.VOLUME_MUTE:
			{
				mMng.MusicVolumeMute((AudioManager)getSystemService(Context.AUDIO_SERVICE));
				
				break;
			}
			case Globals.REFRESH:
			{
				break;
			}
			default:
			{
				Log.e(TITLE, "Incorrect action!");
				
				break;
			}
			
		}
		
		actionToDo = Globals.PLAYER_NOTHING;
	}
	
	private class DoActionThread extends Thread
	{
		
		private DoActionThread()
		{
			actionToDo = Globals.PLAYER_NOTHING;
			start();
		}
		
		public void run()
		{
			Thread.currentThread().setName("DoAction");
			while(true)
			{
				if(actionToDo!=Globals.PLAYER_NOTHING)
				{
					if(actionToDo == Globals.KILLTHREAD)
						return;
					
					sendActionToBroadcast(actionToDo);		
					
					actionToDo = Globals.PLAYER_NOTHING;
				}			
				
			}
		}
		
	
	}
	

}
