package com.cyl.fgtdemo.model.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.util.Log;

import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.fgtdemo.model.bean.RecordInfo;

public class SocketClient {
	
	public final static byte MSG_STATUS=0x04;
	public final static byte MSG_MATCH=0x13;
	public final static byte MSG_REVICE=0x14;
	public final static byte MSG_REVICEUSERID=0x15;
	
	public final static byte NET_LINK=0x01;
	public final static byte NET_ERROR=0x02;
	public final static byte NET_UNLINK=0x03;
	
	public String ServerIP;
	public int ServerPort;

	private Handler msgHandler=null;
	private Handler handler=new Handler();
	private Socket socket=null;	
	private boolean mIsLink=false; 
	private DataOutputStream dataout; 
	private DataInputStream datain;
	private byte inpbuf[]=new byte[1024];	//接收缓存
	private byte oupbuf[]=new byte[26];	//发送缓存
	private int inpsize=0;
	private int oupsize=0;
	private byte ui[]=new byte[48];
	private byte ul[]=new byte[520];

	public SocketClient(String serverIP,int ServerPort){
		this.ServerIP=serverIP;
		this.ServerPort=ServerPort;
	}
	
	public void SetMsgHandler(Handler handler){
		msgHandler=handler;
	}
	
	private void SendMessage(int cmd,int state,int size,byte[] buffer){
		if(msgHandler!=null)
			msgHandler.obtainMessage(cmd,state,size,buffer).sendToTarget();
    }

	public boolean Start(){
		if(!mIsLink){
			if(socket!=null){
				mIsLink=false;
				try{
					socket.close();
				}catch(IOException e){
					
				}
				socket=null;
			}
			Thread clientThread=new Thread(new ClientThread());
			clientThread.start();
		}
		return true;
	}
	
	public boolean Stop(){
		if(mIsLink)
		{
			mIsLink=false;
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket=null;
		}
		return true;
	}
	
	public boolean SendData(byte[] buffer,int size){
		if(mIsLink){
			System.arraycopy(buffer,0, oupbuf, 0, size);			
			if (socket != null && socket.isConnected())
			{
				handler.post(new Runnable() {
					public void run() {
						try
						{  
							dataout.write(oupbuf,0,oupsize);
						}catch (UnknownHostException e)	{
						}catch (IOException e){
						} 
					}
				});
			}
			return true;
		}
		return false;
	}
	
	private byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		return bytes;
	}

	public static int CRC_XModem(byte[] bytes){//CRC16校验
		int crc = 0x00;          // initial value
		int polynomial = 0x1021;
		for (int index = 0 ; index< 16; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b   >> (7-i) & 1) == 1);
				boolean c15 = ((crc >> 15    & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit) crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}

	public static int CRC_XModem1(byte[] bytes){//CRC16校验
		int crc = 0x00;          // initial value
		int polynomial = 0x1021;
		for (int index = 0 ; index< 24; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b   >> (7-i) & 1) == 1);
				boolean c15 = ((crc >> 15    & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit) crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}

	public static int CRC_XModem2(byte[] bytes){//CRC16校验
		int crc = 0x00;          // initial value
		int polynomial = 0x1021;
		for (int index = 0 ; index<20; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b   >> (7-i) & 1) == 1);
				boolean c15 = ((crc >> 15    & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit) crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}

	public static int CRC_XModem3(byte[] bytes){//CRC16校验
		int crc = 0x00;          // initial value
		int polynomial = 0x1021;
		for (int index = 0 ; index<18; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b   >> (7-i) & 1) == 1);
				boolean c15 = ((crc >> 15    & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit) crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}
    //命令包发送
	public void SendCommand(byte cmdid,byte[] buffer,byte[] devsn)
	{
		oupbuf[0]=(byte)0xef; //包头
		oupbuf[1]=0x01;
		oupbuf[2]=0x00; //设备地址
		oupbuf[3]=0x00;
		oupbuf[4]=0x00;
		oupbuf[5]=0x00;
		oupbuf[6]=0x00;//通信密码
		oupbuf[7]=0x00;
		oupbuf[8]=0x00;
		oupbuf[9]=0x00;
		oupbuf[10]=0x01;//包标号
		oupbuf[11]=0x00;
		System.arraycopy(devsn,0, oupbuf, 2, 4);
		System.arraycopy(buffer,0, oupbuf, 6, 4);
		oupbuf[12]=cmdid;//发送指令
		oupbuf[13]=0x01; //包标识
		oupbuf[14]=0x00;  //数据长度
		oupbuf[15]=0x00;
		int sum=CRC_XModem(oupbuf);
		oupbuf[16]=(byte)(((sum) << 24) >> 24);//CRC16校验
		oupbuf[17]=(byte)(((sum) << 16) >> 24);
		oupsize=18;
		if(mIsLink){
			if (socket != null && socket.isConnected())
			{
				handler.post(new Runnable() {
					public void run() {
						try
						{  
							dataout.write(oupbuf,0,oupsize);
						}catch (UnknownHostException e)	{
						}catch (IOException e){
						} 
					}
				});
			}
		}else{
			Start();
		}
	}

	public void SendData(byte cmdid,byte[] buffer,byte[] devsn,byte bs,int st,int dt)
	{
		oupbuf[0]=(byte)0xef; //包头
		oupbuf[1]=0x01;
		oupbuf[2]=0x00; //设备地址
		oupbuf[3]=0x00;
		oupbuf[4]=0x00;
		oupbuf[5]=0x00;
		oupbuf[6]=0x00;//通信密码
		oupbuf[7]=0x00;
		oupbuf[8]=0x00;
		oupbuf[9]=0x00;
		oupbuf[10]=bs;//包标号
		oupbuf[11]=0x00;
		System.arraycopy(devsn,0, oupbuf, 2, 4);
		System.arraycopy(buffer,0, oupbuf, 6, 4);
		oupbuf[12]=cmdid;//发送指令
		oupbuf[13]=0x01; //包标识
		oupbuf[14]=0x08;  //数据长度
		oupbuf[15]=0x00;
		oupbuf[16]=0x00;  //数据起始点
		oupbuf[17]=0x00;
		oupbuf[18]=0x00;
		oupbuf[19]=0x00;
		oupbuf[20]=0x00;  //数据终点
		oupbuf[21]=0x00;
		oupbuf[22]=0x00;
		oupbuf[23]=0x00;
		System.arraycopy(changeByte(st),0, oupbuf, 16, 4);
		System.arraycopy(changeByte(dt),0, oupbuf, 20, 4);
		int sum=CRC_XModem1(oupbuf);
		oupbuf[24]=(byte)(((sum) << 24) >> 24);//CRC16校验
		oupbuf[25]=(byte)(((sum) << 16) >> 24);
		oupsize=26;
		if(mIsLink){
			if (socket != null && socket.isConnected())
			{
				handler.post(new Runnable() {
					public void run() {
						try
						{
							dataout.write(oupbuf,0,oupsize);
						}catch (UnknownHostException e)	{
						}catch (IOException e){
						}
					}
				});
			}
		}else{
			Start();
		}
	}


	public void SendData1(byte cmdid,byte[] buffer,byte[] devsn,byte bs,int st)
	{
		oupbuf[0]=(byte)0xef; //包头
		oupbuf[1]=0x01;
		oupbuf[2]=0x00; //设备地址
		oupbuf[3]=0x00;
		oupbuf[4]=0x00;
		oupbuf[5]=0x00;
		oupbuf[6]=0x00;//通信密码
		oupbuf[7]=0x00;
		oupbuf[8]=0x00;
		oupbuf[9]=0x00;
		oupbuf[10]=bs;//包标号
		oupbuf[11]=0x00;
		System.arraycopy(devsn,0, oupbuf, 2, 4);
		System.arraycopy(buffer,0, oupbuf, 6, 4);
		oupbuf[12]=cmdid;//发送指令
		oupbuf[13]=0x01; //包标识
		oupbuf[14]=0x04;  //数据长度
		oupbuf[15]=0x00;
		oupbuf[16]=0x00;  //数据起始点
		oupbuf[17]=0x00;
		oupbuf[18]=0x00;
		oupbuf[19]=0x00;
		System.arraycopy(changeByte(st),0, oupbuf, 18, 2);
		int sum=CRC_XModem2(oupbuf);
		oupbuf[20]=(byte)(((sum) << 24) >> 24);//CRC16校验
		oupbuf[21]=(byte)(((sum) << 16) >> 24);
		oupsize=22;
		if(mIsLink){
			if (socket != null && socket.isConnected())
			{
				handler.post(new Runnable() {
					public void run() {
						try
						{
							dataout.write(oupbuf,0,oupsize);
						}catch (UnknownHostException e)	{
						}catch (IOException e){
						}
					}
				});
			}
		}else{
			Start();
		}
	}

	public void SendData2(byte cmdid,byte[] buffer,byte[] devsn,byte bs,int st)
	{
		oupbuf[0]=(byte)0xef; //包头
		oupbuf[1]=0x01;
		oupbuf[2]=0x00; //设备地址
		oupbuf[3]=0x00;
		oupbuf[4]=0x00;
		oupbuf[5]=0x00;
		oupbuf[6]=0x00;//通信密码
		oupbuf[7]=0x00;
		oupbuf[8]=0x00;
		oupbuf[9]=0x00;
		oupbuf[10]=bs;//包标号
		oupbuf[11]=0x00;
		System.arraycopy(devsn,0, oupbuf, 2, 4);
		System.arraycopy(buffer,0, oupbuf, 6, 4);
		oupbuf[12]=cmdid;//发送指令
		oupbuf[13]=0x01; //包标识
		oupbuf[14]=0x02;  //数据长度
		oupbuf[15]=0x00;
		oupbuf[16]=0x00;  //数据起始点
		oupbuf[17]=0x00;
		System.arraycopy(changeByte(st),0, oupbuf, 16, 2);
		int sum=CRC_XModem3(oupbuf);
		oupbuf[18]=(byte)(((sum) << 24) >> 24);//CRC16校验
		oupbuf[19]=(byte)(((sum) << 16) >> 24);
		oupsize=20;
		if(mIsLink){
			if (socket != null && socket.isConnected())
			{
				handler.post(new Runnable() {
					public void run() {
						try
						{
							dataout.write(oupbuf,0,oupsize);
						}catch (UnknownHostException e)	{
						}catch (IOException e){
						}
					}
				});
			}
		}else{
			Start();
		}
	}
	public void ReceiveData(byte[] buffer,int size){
		if(size>=18)
		if(buffer[13]==0x07){
			switch(buffer[12]){
				case (byte) 0x64:{
					if(buffer[16]==0x00&buffer[17]==0x00){
						if(buffer[10]==(byte)0x01){
							System.arraycopy(buffer, 18, ui, 0, 2);
							SendMessage(MSG_MATCH, 1, 2, ui);
						}else{
							byte[] st={buffer[14],buffer[15],0x00,0x00};
							int sb=GlobalData.getInstance().byteToInt2(st);
						    byte ud[]=new byte[sb-2];
							System.arraycopy(buffer, 18, ud, 0, sb-2);
							SendMessage(MSG_REVICEUSERID, 1, sb-2, ud);
							Log.i("gjkfdgksdg",sb-2+"");
						}
					}else{
							SendMessage(MSG_REVICE,0,0,null);
					}
			}
					break;
			case (byte) 0xa0:{
				if(buffer[16]==0x00&buffer[17]==0x00){
					if(buffer[10]==(byte)0x01){
							System.arraycopy(buffer, 18, ui, 0, 12);
							SendMessage(MSG_MATCH, 1, 12, ui);
					}else{
						System.arraycopy(buffer, 10, ul, 0, 520);
							SendMessage(MSG_REVICE, 1, 520, ul);
					}
				}else{
				//	SendMessage(MSG_REVICE,0,0,null);
				}
				}
				break;
				case (byte) 0x79:{
					if(buffer[16]==0x00&buffer[17]==0x00){
						if(buffer[10]==(byte)0x01){
							System.arraycopy(buffer, 18, ui, 0, 48);
							SendMessage(MSG_REVICE, 1, 48, ui);
						}else{
							SendMessage(MSG_REVICE,0,0,null);
						}
					}else{
							SendMessage(MSG_REVICE,0,0,null);
					}
				}
				break;
			}
		}
		//是否长连接
		Stop();
	}
	
	public class ClientThread implements Runnable {
		public void run() {
			try  {
				InetAddress serverAddr = InetAddress.getByName(ServerIP);
				handler.post(new Runnable() {
					public void run() {
						SendMessage(MSG_STATUS,NET_LINK,0,null);
					}
				});
				socket = new Socket(serverAddr, ServerPort);				
				try{
					mIsLink=true;
					dataout = new DataOutputStream(socket.getOutputStream());  
					datain = new DataInputStream(socket.getInputStream());
					
					if (socket != null && socket.isConnected())
					{
						handler.post(new Runnable() {
							public void run() {
								try
								{  
									dataout.write(oupbuf,0,oupsize);
								}catch (UnknownHostException e)	{
								}catch (IOException e){
								} 
							}
						});
					}
					
					try	{
						int len=datain.read(inpbuf);
						inpsize=len;
						//while (len != 0) {
						//while (len >= 0) 
						{
							if(len>0){
								handler.post(new Runnable() {
									public void run() {
										ReceiveData(inpbuf,inpsize);
									}
								});
							}
						}

						//---disconnected from the server---
						handler.post(new Runnable() {
							public void run() {
								//textStatus.setText("�������Ͽ�");
								SendMessage(NET_UNLINK,NET_UNLINK,1,null);
								mIsLink=false;
							}
						});

					} catch (Exception e) {
						final String error = e.getLocalizedMessage();
						handler.post(new Runnable() {
							public void run() {
								//textStatus.setText("�������3:" + error);
								SendMessage(NET_ERROR,NET_ERROR,3,null);
								mIsLink=false;
							}
						});
					}

				} catch (Exception e) {
					final String error = e.getLocalizedMessage();
					handler.post(new Runnable() {
						public void run() {
							//textStatus.setText("�������2:" + error);
							SendMessage(NET_ERROR,NET_ERROR,2,null);
							mIsLink=false;
						}
					});
				}

				handler.post(new Runnable() {
					public void run() {
						//textStatus.setText("���ӹر�");
						SendMessage(NET_UNLINK,NET_UNLINK,2,null);
						mIsLink=false;
					}
				});

			} 
			catch (Exception e)
			{
				final String error = e.getLocalizedMessage();
				handler.post(new Runnable()
				{
					public void run() {
						//textStatus.setText("�������1:" + error);
						SendMessage(NET_ERROR,NET_ERROR,1,null);
						mIsLink=false;
					}
				});
			}
		}
	}
}
