package com.andreacioni.remotemusiccontroller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

public class Fragment_Ctrl extends Fragment 
{
	//private static final String TITLE = "RemoteMusicSwither: Fragment_Ctrl";
	private OnHeadlineSelectedListener mCallback;
	
	private Button nextButton;
	private Button prevButton;
	private Button playButton;
	private Button pauseButton;
	private Button volUpButton;
	private Button volDownButton;
	private Button muteButton;
	private TextView infoBox;
	
	private TimerThread h;
	//private long lastUpdate;
	
	private Queue<Byte> queue= new LinkedList<Byte>();
	
	private boolean oneRefresh = false;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_main, container, false);
        
		nextButton = (Button) rootView.findViewById(R.id.buttonNext);
        prevButton = (Button) rootView.findViewById(R.id.buttonPrev);
        playButton = (Button) rootView.findViewById(R.id.buttonPlay);
        pauseButton = (Button) rootView.findViewById(R.id.buttonPause);      
        volDownButton = (Button) rootView.findViewById(R.id.buttonVolDown);
        volUpButton = (Button) rootView.findViewById(R.id.buttonVolUp);
        muteButton = (Button) rootView.findViewById(R.id.buttonMute);
        infoBox = (TextView) rootView.findViewById(R.id.textInfo);
        
        
        EnableGraphics(true);
        
        playButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	
					if(queue.size() < 5)
						queue.add(Globals.PLAYER_PLAY);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
		 });
        
        pauseButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.PLAYER_PAUSE);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
			
		 });
        
        nextButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.PLAYER_NEXT);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
		 });
        
        prevButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.PLAYER_PREVIOUS);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
			
		 });
        
        volDownButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.VOLUME_DOWN);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
			
		 });
        
        volUpButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.VOLUME_UP);
					
					oneRefresh=true;
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}
			
		 });
        
        muteButton.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(h==null)
					h = new TimerThread();
				if(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
				{	if(queue.size() < 5)
						queue.add(Globals.VOLUME_MUTE);
					
					oneRefresh=true;
				
				}
				else
				{
					Toast info = Toast.makeText(getActivity().getApplicationContext(), "You must be the remote controller if you want to send any any comand!", Toast.LENGTH_SHORT);
					info.show();
				}
			}	
			
		 });

        return rootView;
    }
	
	
	private void EnableGraphics(boolean enable)
	{
		nextButton.setEnabled(enable);
		prevButton.setEnabled(enable);
		playButton.setEnabled(enable);
		pauseButton.setEnabled(enable);
		volDownButton.setEnabled(enable);
		volUpButton.setEnabled(enable);
		muteButton.setEnabled(enable);
	}	
	
	public void setTextInfo(final String info)
	{
		//Log.d(TITLE,"rec:" + info);
		infoBox.post(new Runnable() {
		    public void run() {
		        infoBox.setText(info);
		    } 
		});
	}
	
	// Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void sendThroughWiFiMng(byte action);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }
    
    /*public class TimerThread extends Thread
    {
    	public TimerThread()
    	{
    		start();
    	}
    	
    	@Override
    	public void run() {
    		Thread.currentThread().setName("TimerThread");
 
			while((System.currentTimeMillis()) < (lastUpdate + 10000))
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			infoBox.post(new Runnable() {
			    public void run() {
			        setTextInfo();
			    } 
			});
			
			
			h = null;
    	}
    }*/
    
    public class TimerThread extends Thread
    {
    	SharedPreferences shared = getActivity().getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
    	
    	public TimerThread()
    	{
    		start();
    		if(WiFiMng.isOn3g(getActivity()) && (!shared.getBoolean("3gMode", false)))
    		{
    			//Mostra toast per avvertire di abilitare il 3g Mode
    			Toast.makeText(getActivity(), "You're using mobile data connection, i suggest to enable 3g Mode on menu", Toast.LENGTH_LONG).show();
    		}
    		
    	}
    	
    	@Override
    	public void run() {
    		Thread.currentThread().setName("TimerThread");
    		Looper.prepare();
    		while(Fragment_Setup.getState() == Globals.REMOTE_CONTROLLER)
    		{
    			if(!queue.isEmpty())
    				mCallback.sendThroughWiFiMng(queue.poll());
    			else
    			{	
    				if(shared.getBoolean("3gMode", false))
    				{
    					if(oneRefresh)
    					{
    						mCallback.sendThroughWiFiMng(Globals.REFRESH);
    						oneRefresh=false;
    						
    						infoBox.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									infoBox.setText("3g Mode Enabled! Title auto-update stopped.");
									
								}
							}, 10000);
    					}
    				}
    				else
    					mCallback.sendThroughWiFiMng(Globals.REFRESH);
    			}
    			
    			try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    			
    			//Looper.loop();
    		}
    		
    		h = null;
    	}
			
    }
    
}
    
