package com.cyl.fgtdemo.model.app;

import com.cyl.fgtdemo.model.bean.RecordInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RecordFile {
	
	public static void CreateFile(String fileName){
		new File(fileName);
	}
	
	public static void AppendToFile(String fileName, RecordInfo rs) {
		try {
			
			// ��һ����������ļ���������д��ʽ
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// �ļ����ȣ��ֽ���
			long fileLength = randomFile.length();
			// 	��д�ļ�ָ���Ƶ��ļ�β��
			randomFile.seek(fileLength);
			
			byte[] content=new byte[68];
			System.arraycopy(rs.UserID.getBytes(), 0, content, 0, rs.UserID.getBytes().length);
			System.arraycopy(rs.cLogState.getBytes(), 0, content, 16, rs.cLogState.getBytes().length);
			System.arraycopy(rs.cLogType.getBytes(), 0, content, 32, rs.cLogType.getBytes().length);
			System.arraycopy(rs.sRecordTime.getBytes(), 0, content, 48, rs.sRecordTime.getBytes().length);
			
			randomFile.write(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<RecordInfo> ReadFromFile(String fileName){
		ArrayList<RecordInfo> list=new ArrayList<RecordInfo>();
		try {
			// ��һ����������ļ���������д��ʽ
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// �ļ����ȣ��ֽ���
			long fileLength = randomFile.length();
			long count=fileLength/68;
			for(long i=0;i<count;i++){
				byte[] content=new byte[68];
				randomFile.read(content);
				RecordInfo rc=new RecordInfo();
				rc.UserID=new String(content, 0, 16);
				rc.UserID=rc.UserID.replaceAll("\\s","");
				rc.cLogState=new String(content, 16, 16);
				rc.cLogState=rc.cLogState.replaceAll("\\s","");
				rc.cLogType=new String(content, 32, 16);
				rc.cLogType=rc.cLogType.replaceAll("\\s","");
				rc.sRecordTime=new String(content, 48, 20);
				rc.sRecordTime=rc.sRecordTime.replaceAll("\\s","");
				
				list.add(rc);
			}
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean IsFileExists(String filename){
		File f=new File(filename);
		if(f.exists()){
			return true;
		}
		return false;
	}
	
	public static void DeleteFile(String filename){
		File f=new File(filename);
		if(f.exists()){
			f.delete();
		} 
	}
	
	public static void ReCreate(String filename){
		DeleteFile(filename);
		CreateFile(filename);
	}
}
