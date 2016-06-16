package com.cyl.fgtdemo.model.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;

public class SocketClient {
	
	public final static byte MSG_STATUS=0x04;
	public final static byte MSG_MATCH=0x13;
	
	public final static byte NET_LINK=0x01;
	public final static byte NET_ERROR=0x02;
	public final static byte NET_UNLINK=0x03;
	
	public String ServerIP;
	public int ServerPort;

	public byte devsn[]=new byte[4];
	
	private Handler msgHandler=null;

	private Handler handler=new Handler();
	private Socket socket=null;	
	private boolean mIsLink=false; 
	private DataOutputStream dataout; 
	private DataInputStream datain;
	private byte inpbuf[]=new byte[1024];	//接收缓存
	private byte oupbuf[]=new byte[1024];	//发送缓存
	private int inpsize=0;
	private int oupsize=0;
	private byte ui[]=new byte[38];
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
	
	private int calcCheckSum(byte[] buffer,int offset,int size) {
		int sum=0;
		for(int i=0;i<size;i++) {
			sum=sum+buffer[offset+i];
		}
		sum = (((~sum)+1) & 0x0000FFFF);
		return (sum & 0x0000ffff);
	}
	
	public void SendCommand(byte cmdid,byte val,byte[] buffer,int size,byte[] extbuf,int extsize){
		int tsize=12+size+extsize;
		oupbuf[0]=0x24; //TAG
		oupbuf[1]=0x23;
		oupbuf[2]=0x00; //CRC
		oupbuf[3]=0x00;
		oupbuf[4]=(byte) (((tsize) << 24) >> 24); //SIZE
		oupbuf[5]=(byte) (((tsize) << 16) >> 24);
		oupbuf[6]=cmdid;//CMD
		oupbuf[7]=0x00;	//RET
		if(cmdid==0x13){
			tsize=5+size+extsize;
			oupbuf[4]=(byte) (((tsize) << 24) >> 24); //SIZE
			oupbuf[5]=(byte) (((tsize) << 16) >> 24);
			System.arraycopy(devsn,0, oupbuf, 8, 4);
			oupbuf[12]=val;
			if(size>0)
				System.arraycopy(buffer,0, oupbuf, 13, size);
			if(extsize>0)
				System.arraycopy(extbuf,0, oupbuf, 13+size, extsize);			
		}else{
			tsize=4+size+extsize;
			oupbuf[4]=(byte) (((tsize) << 24) >> 24); //SIZE
			oupbuf[5]=(byte) (((tsize) << 16) >> 24);
			System.arraycopy(devsn,0, oupbuf, 8, 4);
			if(size>0)
				System.arraycopy(buffer,0, oupbuf, 12, size);
			if(extsize>0)
				System.arraycopy(extbuf,0, oupbuf, 12+size, extsize);
		}
		int sum=calcCheckSum(oupbuf,4,tsize+4);
		oupbuf[2]=(byte) (((sum) << 24) >> 24); //CRC
		oupbuf[3]=(byte) (((sum) << 16) >> 24);
		oupsize=8+tsize;
		/*
		if(!mIsLink){
			Stop();
			Start();
			try {
				Thread.currentThread();
				Thread.sleep(1000);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		*/
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
		if(size>=8)
		if(buffer[0]==0x24&&buffer[1]==0x23){
			switch(buffer[6]){
			case 0x13:{
				if(buffer[7]==0x01){					
					System.arraycopy(buffer,8, ui, 0, 38);
					SendMessage(MSG_MATCH,1,38,ui);
				}else{
					SendMessage(MSG_MATCH,0,0,null);
				}
				}
				break;
			}
		}
		Stop();
	}
	
	public class ClientThread implements Runnable {
		public void run() {
			try  {
				InetAddress serverAddr = InetAddress.getByName(ServerIP);
				handler.post(new Runnable() {
					public void run() {
						//textStatus.setText("���ӷ�����");
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
								SendMessage(MSG_STATUS,NET_UNLINK,1,null);
								mIsLink=false;
							}
						});

					} catch (Exception e) {
						final String error = e.getLocalizedMessage();
						handler.post(new Runnable() {
							public void run() {
								//textStatus.setText("�������3:" + error);
								SendMessage(MSG_STATUS,NET_ERROR,3,null);
								mIsLink=false;
							}
						});
					}

				} catch (Exception e) {
					final String error = e.getLocalizedMessage();
					handler.post(new Runnable() {
						public void run() {
							//textStatus.setText("�������2:" + error);
							SendMessage(MSG_STATUS,NET_ERROR,2,null);
							mIsLink=false;
						}
					});
				}

				handler.post(new Runnable() {
					public void run() {
						//textStatus.setText("���ӹر�");
						SendMessage(MSG_STATUS,NET_UNLINK,2,null);
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
						SendMessage(MSG_STATUS,NET_ERROR,1,null);
						mIsLink=false;
					}
				});
			}
		}
	}
}
