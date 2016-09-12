package com.cyl.fgtdemo.view.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.Utils.ExtApi;
import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.fgtdemo.model.bean.RecordInfo;
import com.cyl.fgtdemo.model.http.SocketClient;
import com.cyl.libraryview.ButtonRectangle;

import org.xml.sax.DTDHandler;

import java.util.Timer;
import java.util.TimerTask;


public class UploadFragment extends Fragment {
    private Handler handler;

    public  SocketClient socketClient;

    private byte[] bytes;
    private byte[] devsn;

    private ButtonRectangle btn1,btn2;

    private TextView txCount;

    byte bz=0x02;
    private int st=0;
    private int dt=63;
    byte[] readBuf;
    int sb;

    private Timer startTimer;
    private TimerTask startTask;
    private Handler startHandler;

    private ProgressDialog prgDialog;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      final View view1 = inflater.inflate(R.layout.fragment_upload, container, false);
        btn1=(ButtonRectangle) view1.findViewById(R.id.button);
        btn2=(ButtonRectangle) view1.findViewById(R.id.button1);
        txCount=(TextView)view1.findViewById(R.id.textView2);
        bytes = GlobalData.getInstance().hexStringToBytes( GlobalData.getInstance().comm_psw);
        devsn= GlobalData.getInstance().changeByte( GlobalData.getInstance().deviceNum);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SocketClient.NET_LINK:
                        //  SendStatus(msg.arg1);
                        break;
                    case SocketClient.NET_ERROR:
                        //byte[] writeBuf = (byte[]) msg.obj;
                        //AddStatusListHex(writeBuf,writeBuf.length);
                        break;
                    case SocketClient.MSG_MATCH:
                         readBuf = (byte[]) msg.obj;
                        byte[] rb ={readBuf[0],readBuf[1],readBuf[2],readBuf[3]};
                        sb=GlobalData.getInstance().byteToInt2(rb);
                        Log.i("quwjfsfsddfsdjj",sb+"");
                        if (sb>0) {
                        prgDialog = new ProgressDialog(getActivity());
                        //	prgDialog.setIcon(R.drawable.progress);
                        prgDialog.setTitle("下载");
                        prgDialog.setMessage("正在下载考勤数据...");
                        prgDialog.setCancelable(false);
                        prgDialog.setIndeterminate(false);
                        prgDialog.show();
                        TimerStart();
                        }else {
                            Toast.makeText(getActivity(), "当前无考勤记录",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case SocketClient.MSG_REVICE:
                       byte[] readBuf2 = (byte[]) msg.obj;
                        byte[] qn={readBuf2[4],readBuf2[5],0x00,0x00};
                        int sum = GlobalData.getInstance().byteToInt2(qn);
                        byte[] readBuf1=new byte[sum-2];
                        System.arraycopy(readBuf2, 8, readBuf1, 0, readBuf1.length);
                        int start = 0;
                        for (int i = 0; i < readBuf1.length / 8; i++) {
                            byte[] dest = new byte[8];
                            System.arraycopy(readBuf1, start, dest, 0, dest.length);
                            start += dest.length;
                            RecordInfo recordInfo = new RecordInfo();
                            byte[] no = {dest[0],dest[1],0x00,0x00};
                            recordInfo.UserID = String.format("%05d", GlobalData.getInstance().byteToInt2(no));
                            byte dn=(byte) (dest[3]&0x0f);
                            switch (dn) {
                                case (byte) 0x01:
                                    recordInfo.cLogType = "指纹操作";
                                    break;
                                case (byte) 0x02:
                                    recordInfo.cLogType = "个人密码操作";
                                    break;
                                case (byte) 0x03:
                                    recordInfo.cLogType = "卡操作";
                                    break;
                                case (byte) 0x04:
                                    recordInfo.cLogType = "指纹+密码操作";
                                    break;
                                case (byte) 0x05:
                                    recordInfo.cLogType = "指纹+卡操作";
                                    break;
                                case (byte) 0x06:
                                    recordInfo.cLogType = "密码+卡操作";
                                    break;
                                case (byte) 0x07:
                                    recordInfo.cLogType = "指纹+密码+卡操作";
                                    break;
                            }
                            if ((int) dest[2] > 127) {
                                recordInfo.cLogState = "下班";
                            } else if ((int) dest[2] < 128) {
                                recordInfo.cLogState = "上班";
                            } else {
                                recordInfo.cLogState = "无效";
                            }
                            byte[] sn = { dest[4], dest[5],dest[6], dest[7]};
                            int sb1 = GlobalData.getInstance().byteToInt2(sn);
                            int year = ((sb1) >> 26);
                            int month = ((sb1) >> 22) & 0x000f;
                            int date = ((sb1) >> 17) & 0x001f;
                            int hour = ((sb1) >> 12) & 0x001f;
                            int min = ((sb1) >> 6) & 0x003f;
                            int sec = (sb1) & 0x003f;
                            recordInfo.sRecordTime = String.format("%04d-%02d-%02d %02d:%02d:%02d", year + 2000, month, date, hour, min, sec);
                       //     GlobalData.getInstance().AppendRecord(recordInfo);
                            GlobalData.getInstance().recordInfoList.add(recordInfo);
                        }
                        break;
                    case SocketClient.NET_UNLINK:
                        // save the connected device's name
                        //    mConnectedDeviceName = msg.getData().getString(BluetoothReaderService.DEVICE_NAME);
                        //Toast.makeText(getApplicationContext(), "Connected to "  + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case SocketClient.MSG_STATUS:
                        //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        if(ExtApi.IsWifi(getActivity())){
        socketClient=new SocketClient(GlobalData.getInstance().IPAddr,Integer.valueOf(GlobalData.getInstance().IPPort));
        socketClient.SetMsgHandler(handler);
        socketClient.SendCommand((byte) 0xa0,bytes,devsn);
        }else{
            Toast.makeText(getActivity(), "当前无WIFI，请联网操作",Toast.LENGTH_SHORT).show();
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    GlobalData.getInstance().LoadRecordsList();
            //    socketClient.SendData((byte)0xa0,bytes,devsn,bz,st,dt);
                addWegit(view1);
            }
        });
        return view1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void addWegit(View view) {
        TableLayout table = (TableLayout)view.findViewById(R.id.tablelayout);
        table.setStretchAllColumns(true);
        table.removeAllViews();
   //     GlobalData.getInstance().LoadRecordsList();
        TableRow tablecap = new TableRow(getActivity());
        tablecap.setBackgroundColor(Color.rgb(180, 180, 180));
        for (int j = 0; j < 4; j++) {
            TextView tv = new TextView(getActivity());
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.rgb(255, 255,255));
            switch(j){
                case 0:
                    tv.setText("编号");
                    break;
                case 1:
                    tv.setText("日期时间");
                    break;
                case 2:
                    tv.setText("状态");
                    break;
                case 3:
                    tv.setText("操作");
                    break;
/*                case 4:
                    tv.setText(R.string.txt_lng);
                    break;*/
            }
            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(1, 1, 1, 1);
            tablecap.addView(tv,lp);
        }
        table.addView(tablecap, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        int count=0;
        if(GlobalData.getInstance().recordInfoList!=null){
            for (int i = 0; i <GlobalData.getInstance().recordInfoList.size(); i++) {
/*                if(querytype==1){
                    if(GlobalData.getInstance().recordList.get(i).id.indexOf(qyText.getText().toString())>=0){
                        count++;
                    }else{
                        continue;
                    }
                }else if(querytype==2){
                    if(GlobalData.getInstance().recordList.get(i).name.indexOf(qyText.getText().toString())>=0){
                        count++;
                    }else{
                        continue;
                    }
                }else{*/
                    count++;
              //  }

                TableRow tablerow = new TableRow(getActivity());
                tablerow.setBackgroundColor(Color.rgb(222, 220, 210));
                for (int j = 0; j < 4; j++) {
                    TextView tv = new TextView(getActivity());
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackgroundColor(Color.rgb(255, 255,255));
                    switch(j){
                        case 0:
                            tv.setText(GlobalData.getInstance().recordInfoList.get(i).UserID+"");
                            break;
                        case 1:
                            tv.setText(GlobalData.getInstance().recordInfoList.get(i).sRecordTime);
                            break;
                        case 2:
                            tv.setText(GlobalData.getInstance().recordInfoList.get(i).cLogState);
                            break;
                        case 3:
                            tv.setText(GlobalData.getInstance().recordInfoList.get(i).cLogType);
                            break;
/*                        case 4:
                            tv.setText(GlobalData.getInstance().recordInfoList.get(i).lng);
                            break;*/
                    }
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(1, 1, 1, 1);
                    tablerow.addView(tv,lp);
                }
                table.addView(tablerow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }

       txCount.setText("Records:"+String.valueOf(count));
    }
    public void TimerStart(){
        if(startTimer==null){
            startTimer = new Timer();
            startHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (sb==0) {
                        TimeStop();
                        prgDialog.dismiss();
                  //      socketClient.Stop();
                        Toast.makeText(getActivity(),"数据加载完成",Toast.LENGTH_SHORT).show();
                    }


                }
            };
            startTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                        if (sb<64){
                            int st1=st;
                            int dt=st1+sb-1;
                            socketClient.SendData((byte)0xa0,bytes,devsn,(byte)0x02,st1,dt);
                            sb=0;
                        }else{
                            socketClient.SendData((byte)0xa0,bytes,devsn,bz,st,dt);
                            st=++dt;
                            sb-=64;
                            dt=st+63;
                        }
                    startHandler.sendMessage(message);
                }
            };
            startTimer.schedule(startTask, 0, 800);
        }
    }

    public void TimeStop(){
        if (startTimer!=null)
        {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask=null;
        }
    }

}
