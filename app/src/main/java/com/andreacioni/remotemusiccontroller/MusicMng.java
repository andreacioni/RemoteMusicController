package com.andreacioni.remotemusiccontroller;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.view.KeyEvent;

public class MusicMng 
{
	boolean upDown = false;
	boolean state = false;
	
	public Intent nextTrack()
	{
		//long time = SystemClock.uptimeMillis();
		
		if(!upDown)
		{
			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT); 
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent); 
			
			
			
			upDown = true;
			
			return downIntent;
		}
		else
		{
			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
			KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT); 
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
		
			upDown = false;
			
			return upIntent;
		}
	}
	
	public Intent previousTrack()
	{
		//long time = SystemClock.uptimeMillis();
		
		if(!upDown)
		{
			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS); 
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent); 
			
			upDown = true;
			
			return downIntent;
		}
		else
		{
			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
			KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS); 
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
		
			upDown = false;
			
			return upIntent;
		}		

	}
	
	public Intent playTrack()
	{
		//long time = SystemClock.uptimeMillis();
		
		if(!upDown)
		{
			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE); 
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent); 
			
			upDown = true;
			
			return downIntent;
		}
		else
		{
			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
			KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE); 
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
			
		
			upDown = false;
			
			return upIntent;
		}		
	}
	
	public Intent pauseTrack()
	{
		//long time = SystemClock.uptimeMillis();
		
		if(!upDown)
		{
			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE); 
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent); 
			
			upDown = true;
			
			return downIntent;
		}
		else
		{
			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
			KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE); 
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
		
			upDown = false;
			
			return upIntent;
		}		
	}
	
	public void MusicVolumeUp(AudioManager audio)
	{
		audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
	}
	
	public void MusicVolumeDown(AudioManager audio)
	{
		audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
	}
	public void MusicVolumeMute(AudioManager audio)
	{
		if(audio.getStreamVolume(AudioManager.STREAM_MUSIC) > 0)
			audio.setStreamMute(AudioManager.STREAM_MUSIC, true);
		else
			audio.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}
	
	 /**
     * Send the passed media key event to the AudioManager by any means necessary
     * 
     * @param keyEvent event to send to AudioManager
     */
    public Intent handleMediaKeyEvent(KeyEvent keyEvent) {
            boolean dispatchMediaKeyEvent = false;
            
            // Added to support Samsung devices running JellyBean 4.1.0
            // All messages being intercepted by Google Music player instead of the registered receiver 
            
            /*
             * Attempt to execute the following with reflection. Methods are not part of standard jars
             * 
             * [Code]
             * IAudioService audioService = IAudioService.Stub.asInterface(b);
             * audioService.dispatchMediaKeyEvent(keyEvent);
             */
            try {
                    
                    // Get binder from ServiceManager.checkService(String)
                    IBinder iBinder  = (IBinder) Class.forName("android.os.ServiceManager")
                        .getDeclaredMethod("checkService",String.class)
                        .invoke(null, Context.AUDIO_SERVICE);
                        
                    // get audioService from IAudioService.Stub.asInterface(IBinder)
                    Object audioService  = Class.forName("android.media.IAudioService$Stub")
                                    .getDeclaredMethod("asInterface",IBinder.class)
                                    .invoke(null,iBinder);
                    
                    // Dispatch keyEvent using IAudioService.dispatchMediaKeyEvent(KeyEvent)
                    Class.forName("android.media.IAudioService")
                    .getDeclaredMethod("dispatchMediaKeyEvent",KeyEvent.class)
                    .invoke(audioService, keyEvent);

                    dispatchMediaKeyEvent = true;
                    
                    
            }  catch (Exception e1) {
                    e1.printStackTrace();
            }
            
            // If dispatchMediaKeyEvent failed then try using broadcast
            if(!dispatchMediaKeyEvent){
                    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                    // Note that sendOrderedBroadcast is needed since there is only
                    // one official receiver of the media button intents at a time
                    // (controlled via AudioManager) so the system needs to figure
                    // out who will handle it rather than just send it to everyone.
                   return intent;
            }
			return null;
       
    }
	
}
