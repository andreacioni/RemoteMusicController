package com.andreacioni.remotemusiccontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity 
{
	private static final String TITLE = "SplashScreen";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//switch to full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash_screen);
		
		
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //Start another activity
                Intent i = new Intent();
                i.setClass(SplashScreen.this,RemoteSwitcherMain.class); 
        		startActivity(i);
        		
                Log.d(TITLE,"Setup activity launched!");
            }
        }, Globals.SPLASH_TIME);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
