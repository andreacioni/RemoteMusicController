package com.andreacioni.remotemusiccontroller;

public class Globals 
{
	public static final byte PLAYER_PLAY = 0x01;
	public static final byte PLAYER_PAUSE = 0x02;
	public static final byte PLAYER_NEXT = 0x03;
	public static final byte PLAYER_PREVIOUS = 0x04;
	public static final byte VOLUME_UP = 0x05;
	public static final byte VOLUME_DOWN = 0x06;
	public static final byte VOLUME_MUTE = 0x07;
	public static final byte KILLTHREAD = 0x08;	
	public static final byte WIDGET_PLAY_PAUSE = 0x09;
	public static final byte WIDGET_NEXT = 0x0A;
	public static final byte WIDGET_PREV = 0x0B;
	public static final byte REFRESH = 0x0C;
	
	public static final int DEBUG_CLIENT = 0;
	public static final int DEBUG_SERVER = 12500;
	
	public static final int PORT = 9091;
	public static final int SERVICE_DISCOVERING = 9092;
	
	
	public static final byte PLAYER_NOTHING = (byte) 0xFF;
	
	public static final int SPLASH_TIME = 2000;
	
	public static final String MusicPlayer_Description = "By clicking on GO button the application start the automatic research of Remote Controller ready in the broadcast";
	public static final String RemoteController_Description = "By clicking on GO button the application start the service and listen for incoming connection of Media Player";
	
	public static final int MUSIC_PLAYER = 0;
	public static final int REMOTE_CONTROLLER = 1;
	public static final int UNCONFIGURED = 2;
	
	public static final int SEND_TIMEOUT = 2000;  //Usato per evitare di bloccare l'app se il server non dovesse rispondere
	
	
}
