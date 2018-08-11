package com.andreacioni.remotemusiccontroller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Fragment_Setup extends Fragment 
{
	//private static final String TAG = "RemoteMusicSwither: Fragment_Setup";
	private OnHeadlineSelectedListener mCallback;
	
	private RadioButton radioMusicPlayer;
	private RadioButton radioController;
	private TextView textInfo;
	private ProgressBar progressCircle;
	
	private static int whoAmI = Globals.UNCONFIGURED;
	
	public static WiFiMng wMng;
	
	//private MusicMng mMng = new MusicMng();

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_start, container, false);

        //RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioSex);
        
        radioMusicPlayer = (RadioButton) rootView.findViewById(R.id.radioButton1);
        radioController = (RadioButton) rootView.findViewById(R.id.radioButton2); 
        textInfo = (TextView) rootView.findViewById(R.id.textLoad);
        progressCircle = (ProgressBar) rootView.findViewById(R.id.loadCircle);

        if(isMyServiceRunning(getActivity()))
        	radioMusicPlayer.setChecked(true);
        
        
        radioMusicPlayer.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{			
				if(!WiFiMng.isNetworkAvailable(getActivity().getApplicationContext()))
				{
					textInfo.setTextColor(Color.RED);
					textInfo.setText("You are OFFLINE!");
					
					radioController.setEnabled(false);
					radioMusicPlayer.setEnabled(false);
					
					textInfo.setVisibility(View.VISIBLE);
					
					return;
				}
				
				if(radioMusicPlayer.isChecked())
				{
					/*WiFiMng w = new WiFiMng();
					
					mCallback.updateWiFiMng(w);*/
					
					//TODO Controllare se gia esiste, se esiste eliminarlo!
					
					String ip = null;
					
					ip = WiFiMng.getIPAddress(true);
				
					if(ip!=null)
					{
						textInfo.setText("Your address:\n" + ip);
						textInfo.setVisibility(View.VISIBLE);
					}
					
					if(isMyServiceRunning(getActivity()))
					{
						//Elimina il servizio
						Intent stopService = new Intent(getActivity(), RemoteMusicSwitcherService.class);
						getActivity().stopService(stopService);
						
						Toast info = Toast.makeText(getActivity().getApplicationContext(), "Service ended correctly!", Toast.LENGTH_SHORT);
						info.show();
						
						whoAmI = Globals.UNCONFIGURED;
						
						return;
					}
					
					Intent startService = new Intent(getActivity(), RemoteMusicSwitcherService.class);
					getActivity().startService(startService);
					
					
					if(isMyServiceRunning(getActivity()))
					{
						Toast info = Toast.makeText(getActivity().getApplicationContext(), "Service started, click here to stop it!", Toast.LENGTH_SHORT);
						info.show();
						
						whoAmI = Globals.MUSIC_PLAYER;
					}
					else
					{
						Toast info = Toast.makeText(getActivity().getApplicationContext(), "Service is not started...report it! (CODE: 1)", Toast.LENGTH_SHORT);
						info.show();
					}
					
					return;
				}
				
				if(radioController.isChecked())
				{
					return;
				}
				
				Toast info = Toast.makeText(getActivity().getApplicationContext(), "Select an option!", Toast.LENGTH_SHORT);
				info.show();
				
									
				
			}
		 });
        
        radioController.setOnClickListener(new View.OnClickListener() 
		{
				
			@Override
			public void onClick(View v) 
			{
				if(radioController.isChecked())
				{
					if(!WiFiMng.isNetworkAvailable(getActivity().getApplicationContext()))
					{
						textInfo.setTextColor(Color.RED);
						textInfo.setText("You are OFFLINE!");
						
						radioController.setEnabled(false);
						radioMusicPlayer.setEnabled(false);
						
						textInfo.setVisibility(View.VISIBLE);
						
						return;
					}
					
					WiFiMng w = WiFiMng.sendServiceRequest(getActivity());
					
					if(w==null)
					{
						ManualIpRequest();
					}
					else
					{
						//Salva l'ip nelle sharedpreferences per poterlo utilizzare dopo
						SharedPreferences shared = getActivity().getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = shared.edit();
						editor.putString("ip", w.getRemoteAddress());
						editor.commit();
						
						Toast info = Toast.makeText(getActivity(), "Found: " + w.getRemoteAddress(), Toast.LENGTH_SHORT);
						info.show();
						mCallback.updateWiFiMng(w);
						
						whoAmI = Globals.REMOTE_CONTROLLER;
						
						mCallback.goToCtrl();						
						//textInfo.setTextColor(Color.GREEN);
						//textInfo.setText("READY!");
					}
					
					setLoadingVisible(false);
					
					return;
				}
				
				if(radioMusicPlayer.isChecked())
				{
					return;
				}
				
				
				
									
				
			}
		 });
        
        return rootView;
    }
	
	private void ManualIpRequest()
	{		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Insert IP Manually");
		alert.setMessage("Insert here the IP of the device where the service is ready");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		
		//Cerca se � gia stato inserito un ip di recente
		SharedPreferences shared = getActivity().getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
		input.setText(shared.getString("ip", null));
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String ip = null;
		  Editable value = input.getText();
		  ip = value.toString();
		  
		  if(!ip.isEmpty())
		  {
			 try
			 {
				 
				 //Salva l'ip nelle sharedpreferences per poterlo utilizzare dopo
				 SharedPreferences shared = getActivity().getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
				 SharedPreferences.Editor editor = shared.edit();
				 editor.putString("ip", ip);
				 editor.commit();
				 
				 
				 WiFiMng w = new WiFiMng(ip);
				 
				 if(w!=null)
					 mCallback.updateWiFiMng(w);
				 
				 whoAmI = Globals.REMOTE_CONTROLLER;
				 mCallback.goToCtrl();
				 
				 //textInfo.setTextColor(Color.GREEN);
				 //textInfo.setText("READY!");
				 
			 }
			 catch(SocketException e)
			 {
				 e.printStackTrace();
				 //Log.e(TITLE,"Error while initilizing the internet connection");
				 
				 Toast info = Toast.makeText(getActivity(), "Error! you can't proceed", Toast.LENGTH_SHORT);
				 info.show();
				 
				 whoAmI = Globals.UNCONFIGURED;
			 }
			 catch(UnknownHostException e)
			 {
				 Toast info = Toast.makeText(getActivity(), "Error! you can't proceed", Toast.LENGTH_SHORT);
				 info.show();
				 
				 whoAmI = Globals.UNCONFIGURED;
				 
				 e.printStackTrace();
			 }
		  }
		  else
		  {
			  //Log.e(TITLE,"Error while initilizing the internet connection");
			  
			  Toast info = Toast.makeText(getActivity(), "Error: insert something...", Toast.LENGTH_SHORT);
			  info.show();
			  
			  whoAmI = Globals.UNCONFIGURED;
		  }
		  setLoadingVisible(false);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			  
			  /*Toast info = Toast.makeText(getActivity(), "Try another time with the automatic search by clicking Go!", Toast.LENGTH_SHORT);
			  info.show();*/
			  
			  setLoadingVisible(false);
		  }
		});

		alert.show();
		
		return;
	}

	public static int getState()
	{
		return whoAmI;
	}
	
	// Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void updateWiFiMng(WiFiMng w);
        public void goToCtrl();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	private static boolean isMyServiceRunning(Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RemoteMusicSwitcherService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	
	private void setLoadingVisible(boolean state)
	{
		if(state == false)
		{
			textInfo.setVisibility(View.INVISIBLE);
			progressCircle.setVisibility(View.INVISIBLE);
		}
		else
		{
			textInfo.setText("Loading...");
			textInfo.setVisibility(View.VISIBLE);
			progressCircle.setVisibility(View.VISIBLE);
		}
		
	}
}
