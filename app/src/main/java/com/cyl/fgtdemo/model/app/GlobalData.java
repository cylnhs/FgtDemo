package com.cyl.fgtdemo.model.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

public class GlobalData {
	
	private static GlobalData instance;
	private Context pcontext=null;

	private String	sDir=Environment.getExternalStorageDirectory() + "/OnePass";
	private List<Uri> fsList;

	public boolean	isonline=false;
	public String 	DefaultUser;
	public String 	DefaultPassword;

	public String   IPAddr;
	public int   IPPort;
	public String   comm_psw;
	public int   deviceNum;
	public String	AdminFingerprint="";
	public String	AdminPassword="1010";
	
	public int rT ;
	
	public static GlobalData getInstance() {
    	if(null == instance) {
    		instance = new GlobalData();
    	}
    	return instance;
    }
	
	public void SetContext(Context pc){
		pcontext=pc;
	}
	
	public boolean IsHaveSdCard() {
    	String status = Environment.getExternalStorageState();
    	if (status.equals(Environment.MEDIA_MOUNTED)) 	{
    	   return true;
    	}else{
    	   return false;
    	}
    }
    
	public String GetDir(){
		return sDir;
	}
	
    public void CreateDir() {
    	if(IsHaveSdCard()){    		
    		File destDir = new File(sDir);
    		if (!destDir.exists()) {
    			destDir.mkdirs();
    		}
    	}else{
    	}
    }
    
    public void LoadFileList() {
    	File file = new File(sDir);

    	fsList = new ArrayList<Uri>();
        if(file.isDirectory()) {
        	File[] all_file = file.listFiles();
            if (all_file != null) {
            	for(int i=0;i<all_file.length;i++){
            		fsList.add(Uri.parse(all_file[i].toString()));
            	}
             }
         } 
    }
    
    public boolean FileIsExists(Uri fsname) {
    	for(int i=0;i<fsList.size();i++)
    	{
    		if(fsList.get(i).equals(fsname))
    			return true;
    	}
    	return false;
    }
    
    public boolean IsFileExists(String filename){
		File f=new File(filename);
		if(f.exists()){
			return true;
		}
		return false;
	}
	
	public void DeleteFile(String filename){
		File f=new File(filename);
		if(f.exists()){
			f.delete();
		} 
	}
	
  //��������
  	public void SaveConfig(){
      	SharedPreferences sp;
  		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
  		Editor edit=sp.edit();
  		
  		edit.putString("IPAddr",IPAddr);
		edit.putInt("IPPort", IPPort);
  		edit.putString("comm_psw",comm_psw);
		edit.putInt("deviceNum",deviceNum);
  		edit.putString("DefaultUser",DefaultUser);
  		edit.putString("DefaultPassword",DefaultPassword);
  		edit.putBoolean("IsOnline", isonline);

  		
  		edit.commit();

      }
      
  	//��ȡ����
      public void LoadConfig(){
      	SharedPreferences sp;
  		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);

  		IPAddr=sp.getString("IPAddr","192.168.1.10");
		  IPPort=sp.getInt("IPPort", 8000);
  		DefaultUser=sp.getString("DefaultUser","admin");
  		DefaultPassword=sp.getString("DefaultPassword","1010");
  		isonline=sp.getBoolean("IsOnline", false);
		  comm_psw=sp.getString("comm_psw","FFFFFFFF");
		  deviceNum=sp.getInt("deviceNum",1);
      }


	public  byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	private  byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public  boolean checkNum (String aNumber) {

		String regString = "[a-f0-9A-F]{8}";
		if (Pattern.matches(regString, aNumber)){
			return true;
		}
		return false;
	}

	public  boolean test(String s){
		for(int i=0;i<s.length();i++){
			char c=s.charAt(i);
			if(((c>='a')&&(c<='f'))||((c>='A')&&(c<='F'))||((c>='0')&&(c<='9')))
				continue;
			else
				return false;
		}
		return true;
	}
/*    public boolean IsHaveUserItem(String id){
    	for(int i=0;i<userList.size();i++){
    		if(userList.get(i).id.equals(id))
    			return true;
    	}
    	return false;
    }*/

    public void ClearRecordsList(){
    //	recordList.clear();
    //	RecordFile.ReCreate(sDir+"/recordslist.dat");
    }

	public byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		byte[] bytes2=new byte[4];
		bytes2[0]=(byte)(bytes[0]|0x00);
		bytes2[1]=(byte)(bytes[1]|0x00);
		bytes2[2]=(byte)(bytes[2]|0xff);
		bytes2[3]=(byte)(bytes[3]|0xff);
		return bytes2;
	}
}
