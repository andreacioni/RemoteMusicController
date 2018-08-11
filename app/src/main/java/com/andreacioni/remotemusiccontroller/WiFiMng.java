package com.andreacioni.remotemusiccontroller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class WiFiMng 
{
	
	//private DatagramSocket dataSocket;
	//private DatagramPacket packet;
	//private byte[] b_packet = new byte[1];
	private static InetAddress ipRemotePlayer = null;
	private static String str_ipRemotePlayer = null;
	
	private MainThread mThread;
	private SendResponseThread sendResponseThread;
	
	private DataOutputStream outToServer = null;
	private BufferedReader inFromServer = null;
	private Socket send = null;
	
	private static boolean STOP_TAG;
	
	private static final String TITLE = "WiFiMng";
	
	public WiFiMng(String ip) throws SocketException, UnknownHostException
	{
		//Client side
		//137.4
		STOP_TAG = false;
		ipRemotePlayer = InetAddress.getByName(ip);
		str_ipRemotePlayer = ip;

		//dataSocket = new DatagramSocket();
		//dataSocket.connect(ipRemotePlayer, Globals.PORT);
		
	}
	
	public WiFiMng()
	{
		//Server side
		
		STOP_TAG = true;
		
		try {
			mThread = new MainThread();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TITLE, "Problem when open port: " + Globals.PORT);
		}
		
		try {
			sendResponseThread = new SendResponseThread();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TITLE, "Problem when open port: " + Globals.SERVICE_DISCOVERING);
		}
		
		
	}
	
	public void sendAction(byte playerAction,Context c)
	{
		if(STOP_TAG == true)
			return;
		
		if(!isNetworkAvailable(c))
		{
			Toast.makeText(c, "No connection available!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		switch(playerAction)
		{
			case Globals.PLAYER_NEXT:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Request: next song!");
				
				break;
			}	
			case Globals.WIDGET_NEXT:
			{
				send(playerAction,c);
				Log.i(TITLE, "Request: next song!");
				
				break;
			}
			case Globals.PLAYER_PREVIOUS:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Request: previous song!");
				
				break;
			}	
			case Globals.WIDGET_PREV:
			{
				send(playerAction,c);
				Log.i(TITLE, "Request: previous song!");
				
				break;
			}
			case Globals.PLAYER_PAUSE:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Request: pause!");
				
				break;
			}
			case Globals.PLAYER_PLAY:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Request: play!");
				
				break;
			}	
			case Globals.WIDGET_PLAY_PAUSE:
			{
				send(playerAction,c);
				Log.i(TITLE, "Request: play!");
				
				break;
			}
			case Globals.VOLUME_UP:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		

				Log.i(TITLE, "Volume: UP");
				
				break;
			}
			case Globals.VOLUME_DOWN:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Volume: DOWN");
				
				break;
			}
			case Globals.VOLUME_MUTE:
			{
				send(playerAction,c);
				send(Globals.PLAYER_NOTHING,c);		
				Log.i(TITLE, "Volume: MUTE");
				break;
			}
			case Globals.REFRESH:
			{
				send(Globals.REFRESH,c);
				break;
			}				
			default:
				Log.e(TITLE, "Player action unrecognized!");
				break;
		}
		
	}
	
	public String getRemoteAddress()
	{
		if(str_ipRemotePlayer == null)
			return "null";
		
		return str_ipRemotePlayer;
	}

	public static String printHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (byte b : bytes) {
			sb.append(String.format("0x%02X ", b));
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static WiFiMng sendServiceRequest(Context c)
	{
		DatagramSocket discover;
		DatagramPacket packet;
		byte address[];
		String MyIp;
		
		STOP_TAG = false;
		
		MyIp = getIPAddress(true);
		address = MyIp.getBytes();

		Log.d(TITLE, "Sending IP: " + MyIp + " to broadcast (bytes: " + printHex(address) + ")");
		
		try {
			discover = new DatagramSocket();
		} catch (SocketException e) {
			Log.e(TITLE, "Error when setting up the service request", e);
			return null;
		}
		
		packet = new DatagramPacket(address,address.length);
		
		try {
			discover.connect(getBroadcastAddress(c),Globals.SERVICE_DISCOVERING);
			discover.send(packet);
			discover.disconnect();
			discover.close();
		} catch (IOException e) {
			//e.printStackTrace();
			
			Log.e(TITLE, "Error while sending discover packet");
			
			return null;
		}
		
		try {
			discover = new DatagramSocket(Globals.SERVICE_DISCOVERING);
			discover.setSoTimeout(3000);
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}
		
		packet = new DatagramPacket(address, address.length);
		
		try {
			discover.receive(packet);		
			discover.disconnect();
			discover.close();
		} catch (IOException e) {
			//e.printStackTrace();
			discover.disconnect();
			discover.close();
			return null;
		}
		
		address = null;
		address = packet.getData();
		
		String remoteIp = null;
		
		try {
			remoteIp = new String(address);
			ipRemotePlayer = InetAddress.getByName(remoteIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
			return null;
		}
		
		/*if(remoteIp==null)
			return null;*/
		
		WiFiMng WifiManagerObj;
		try {
			WifiManagerObj = new WiFiMng(remoteIp);
			
			ipRemotePlayer = InetAddress.getByName(remoteIp);
		} catch (SocketException e) {
			e.printStackTrace();
			
			return null;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return WifiManagerObj;
		
		
		
	}
	
	public static boolean isNetworkAvailable(Context c) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static boolean isOn3g(Context c)
	{
		 ConnectivityManager connectivityManager 
         = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		 
		 if(activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
			 return true;
		 else
			 return false;
	}
	
	public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        Log.d(TITLE, "Got IP address: " + sAddr);
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
								String toIPv4Format = delim<0 ? sAddr : sAddr.substring(0, delim);
								Log.d(TITLE, "IPV4 format: " + toIPv4Format);
                                return toIPv4Format;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

	private static InetAddress getBroadcastAddress(Context c) throws IOException
	{
	    WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);

	    return InetAddress.getByAddress(quads);
	}


	
	private synchronized void send(byte action,Context c)
	{
		if((ipRemotePlayer == null)||(str_ipRemotePlayer == null))
			return;
		
		/*b_packet[0] = action;
		
		packet = new DatagramPacket(b_packet, b_packet.length);
		
		try 
		{
			dataSocket.send(packet);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Log.e(TITLE, "IO send Exception");
			return;
		}*/
		
		try {
			
			
			send = new Socket();
			send.connect(new InetSocketAddress(ipRemotePlayer, Globals.PORT) ,Globals.SEND_TIMEOUT);
			
			outToServer = new DataOutputStream(send.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(send.getInputStream()));

			outToServer.writeBytes(action + "\n");
			outToServer.flush();
			
			if((action != Globals.WIDGET_NEXT) || (action != Globals.WIDGET_PLAY_PAUSE) || (action != Globals.WIDGET_PREV))
			{
				String received = inFromServer.readLine();
				Log.d(TITLE, "Received this title: " + received);
				RemoteSwitcherMain.titleOnPlaying = received;
			}			
			
			outToServer.close();
			inFromServer.close();
			send.close();		
			
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TITLE, "Can't send bytes over TCP caused by: " + e.getMessage());
			Toast.makeText(c, "Connection timeout or failed to connect to the host, probably incorrect ip or the host is unreachable", Toast.LENGTH_LONG).show();
		}
	}
	
    /*private static InetAddress getBroadcast(InetAddress inetAddr){

        NetworkInterface temp;
        InetAddress iAddr=null;
     try {
         temp = NetworkInterface.getByInetAddress(inetAddr);
         List<InterfaceAddress> addresses = temp.getInterfaceAddresses();

         for(InterfaceAddress inetAddress:addresses)

         iAddr=inetAddress.getBroadcast();
         Log.d(TITLE,"iAddr="+iAddr);
         return iAddr;  

     } catch (SocketException e) {

         e.printStackTrace();
         Log.d(TITLE,"getBroadcast"+e.getMessage());
     }
      return null; 
 }*/
	
	private class SendResponseThread extends Thread
	{
		DatagramSocket dataServerSocket;

		public SendResponseThread() throws Exception
		{
			dataServerSocket = new DatagramSocket(Globals.SERVICE_DISCOVERING);		
			this.start();
		}
		
		public void stopAll()
		{
			dataServerSocket.close();
			dataServerSocket.disconnect();

			STOP_TAG = false;
		}
		
		public void run()
		{
			Thread.currentThread().setName("SendResponse");
			
			while(STOP_TAG == true)
			{
				byte[] buff = new byte[64];
				byte remoteAddress[] = null;
				byte currentAddress[] = getIPAddress(true).getBytes();
				DatagramPacket discoveringPacket = new DatagramPacket(buff, buff.length);


				try 
				{
					dataServerSocket.receive(discoveringPacket);

					int ipLength = discoveringPacket.getLength();

					Log.d(TITLE, "Packet data length: " + ipLength);

					remoteAddress = Arrays.copyOfRange(discoveringPacket.getData(), 0,  ipLength);

					Log.d(TITLE, "Remote address IP bytes: " + printHex(remoteAddress));
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				String str_RemoteIp = new String(remoteAddress);
				
				Log.i(TITLE,"Incoming connction from: " + str_RemoteIp);	

				try {
					DatagramSocket send = new DatagramSocket();
					
					//String a = new String(currentAddress);
					
					discoveringPacket = new DatagramPacket(currentAddress, currentAddress.length);
					send.connect(InetAddress.getByName(str_RemoteIp),Globals.SERVICE_DISCOVERING);
					send.send(discoveringPacket);
					send.disconnect();
					send.close();
				} catch (SocketException e1) {
				
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
		
					e1.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
				
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}

			
		}
		
		
		}
	
	
	private  class MainThread extends Thread //Thread that run the main server service
	{
		ServerSocket dataServerSocket = null;
		Socket s = null;
		BufferedReader inFromServer = null;
		DataOutputStream outToServer = null;

		public MainThread() throws Exception
		{
			dataServerSocket = new ServerSocket(Globals.PORT);
			this.start();
		}
		
		public void stopAll()
		{
			STOP_TAG = false;
			try {
				dataServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//dataServerSocket.disconnect();
		}
		
		public void run()
		{			
			Thread.currentThread().setName("Main Thread");
			
			while(STOP_TAG == true)
			{
				//packet = new DatagramPacket(b_packet,b_packet.length);
				String incoming = "";
				try 
				{
					s = dataServerSocket.accept();
					inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
					outToServer = new DataOutputStream(s.getOutputStream());
					
					incoming = inFromServer.readLine();
					
					if(incoming != null)
					{
						byte b = (byte) Byte.parseByte(incoming);
						
						Log.d(TITLE,"Received: " + b);
						
						sendToMainActivity(b);
							
						//sleep(100);
							
						if((b != Globals.WIDGET_NEXT) && (b != Globals.WIDGET_PLAY_PAUSE) && (b != Globals.WIDGET_PREV))
						{
							outToServer.writeBytes(RemoteMusicSwitcherService.currentPlayed + "\n");
							outToServer.flush();
						}
					}					
					
					outToServer.close();
					inFromServer.close();
					s.close();					
					
				}
				catch (IOException e) {
					Log.e(TITLE, "Error in Main Thread!", e);
					//e.printStackTrace();
				}
				catch(NumberFormatException e)
				{
					Log.e(TITLE, "Number format exception", e);
				}
			
				
				
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		  }
		}

		private void sendToMainActivity(byte action) 
		{
			RemoteMusicSwitcherService.receiveFromWiFiMng(action);
		}
		
		public void release(){		
			STOP_TAG = false;
			
			mThread.stopAll();
			sendResponseThread.stopAll();
			
			mThread	= null;
			sendResponseThread = null;
		}
	}



