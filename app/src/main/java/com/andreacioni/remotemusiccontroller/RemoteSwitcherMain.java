package com.andreacioni.remotemusiccontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


	public class RemoteSwitcherMain extends AppCompatActivity implements Fragment_Setup.OnHeadlineSelectedListener, Fragment_Ctrl.OnHeadlineSelectedListener
{	
	//private static final String TITLE = "RemoteMusicSwither: Main";
	
	//private RemoteMusicSwitcherService myService;
	
	public static String titleOnPlaying = "";
	
	private static WiFiMng wMng;
	
	private Fragment_Ctrl fControl;
	private Fragment_Setup fSetup;	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.viewpager); 
		
		// Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = (ViewPager) findViewById(R.id.vpager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
	    MenuItem a;
	    final MenuItem check;
	    
	    check = menu.add("3g Mode");
	    a = menu.add("Credits");
	    
	    check.setCheckable(true);
	    
	    SharedPreferences shared = getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
		if(shared.getBoolean("3gMode", false))
			check.setChecked(true);
	    
	    check.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(check.isChecked())
				{
					check.setChecked(false);
					
					SharedPreferences shared = getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = shared.edit();
					editor.putBoolean("3gMode", false);
					editor.commit();
				}
				else
				{
					SharedPreferences shared = getSharedPreferences("RemoteMusicControllerPref", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = shared.edit();
					editor.putBoolean("3gMode", true);
					editor.commit();
					
					check.setChecked(true);
				}
					
				return false;
			}
		});
	    
	    a.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(RemoteSwitcherMain.this,Credits.class));
				return false;
			}
		});
	    //inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) 
        {
        	switch (position)
        	{
        		case 0:
        		{
        			fSetup = new Fragment_Setup();
        			return fSetup;
        		}
        		case 1:
        		{
        			fControl = new Fragment_Ctrl();
        			return fControl;
        		}
        		default:
        		{
        			                  
                    fControl = new Fragment_Ctrl();
        			return fControl;
        		}
        	}
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
	

	
	public void sendThroughWiFiMng(final byte action)
	{
		if(wMng!=null)
		{
			wMng.sendAction(action,getApplicationContext());
		}
		else
		{
			Toast info = Toast.makeText(getApplicationContext(), "Unconfigured WiFi", Toast.LENGTH_SHORT);
			info.show();
		}
		
		fControl.setTextInfo(titleOnPlaying);
	}
	
	
	public void updateWiFiMng(WiFiMng w) 
	{				
		wMng = w;		
	}	
	
	public static WiFiMng getWiFiMng()
	{
		return wMng;
	}

	@Override
	public void goToCtrl() {
		
		ViewPager mPager = (ViewPager) findViewById(R.id.vpager);
		mPager.setCurrentItem(1);	
	}
	
}
