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
import com.cyl.fgtdemo.model.bean.UserInfo;
import com.cyl.fgtdemo.model.http.SocketClient;
import com.cyl.libraryview.ButtonRectangle;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class SecondFragment extends Fragment {
    private Handler handler;

    public  SocketClient socketClient;

    private byte[] bytes;
    private byte[] devsn;

    private ButtonRectangle btn1,btn2;

    private TextView txCount;

    private int mm=1;
    private int sb=0;

    private Timer startTimer;
    private TimerTask startTask;
    private Handler startHandler;

    private ProgressDialog prgDialog;
    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view1 = inflater.inflate(R.layout.fragment_second, container, false);
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
                        byte[] readBuf = (byte[]) msg.obj;
                        byte[] rb ={readBuf[0],readBuf[1],0x00,0x00};
                        sb=GlobalData.getInstance().byteToInt2(rb);
                        Log.i("quwjfdfsdjj",sb+"");
                        if (sb>0){
                                int dt=sb;
                                socketClient.SendData1((byte)0x64,bytes,devsn,(byte)0x02,100);
                        }else {
                            Toast.makeText(getActivity(), "当前无登记用户",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case SocketClient.MSG_REVICEUSERID:
                        byte[] readIDBuf = (byte[]) msg.obj;
                        int start1 = 0;
                        for(int i=0;i<readIDBuf.length/2;i++){
                            byte[] dest = new byte[2];
                            System.arraycopy(readIDBuf, start1, dest, 0, dest.length);
                            start1 += dest.length;
                            byte[] rb1 ={dest[0],dest[1],0x00,0x00};
                            int sb1=GlobalData.getInstance().byteToInt2(rb1);
                            GlobalData.getInstance().userInfoID.add(sb1);
                            Log.i("hhhhgdhdx",GlobalData.getInstance().userInfoID.size()+"");
                        }
                        if (GlobalData.getInstance().userInfoID!=null&GlobalData.getInstance().userInfoID.size()>0){
                        prgDialog = new ProgressDialog(getActivity());
                        //	prgDialog.setIcon(R.drawable.progress);
                        prgDialog.setTitle("下载");
                        prgDialog.setMessage("正在下载用户......");
                        prgDialog.setCancelable(false);
                        prgDialog.setIndeterminate(false);
                        prgDialog.show();
                        socketClient.SendData2((byte) 0x79, bytes, devsn, (byte) 0x01, GlobalData.getInstance().userInfoID.get(0));
                            //    TimerStart();
                        }
                        break;
                    case SocketClient.MSG_REVICE:
                        byte[] readBuf2 = (byte[]) msg.obj;
                        byte[] qn={readBuf2[0],readBuf2[1],0x00,0x00};
                        UserInfo userInfo=new UserInfo();
                        userInfo.wUserID = String.format("%05d", GlobalData.getInstance().byteToInt2(qn));
                        byte dn=(byte) (readBuf2[2]&0x0f);
                        switch (dn) {
                            case (byte) 0x00:
                                userInfo.sUserType = "普通用户";
                                break;
                            case (byte) 0x01:
                                userInfo.sUserType = "管理员";
                                break;
                            case (byte) 0x02:
                                userInfo.sUserType = "登记员";
                                break;
                            case (byte) 0x03:
                                userInfo.sUserType = "高级管理员";
                                break;
                            case (byte) 0x04:
                                userInfo.sUserType = "移动存储管理员";
                                break;
                        }
                        byte zn=(byte) (readBuf2[3]&0x0f);
                        userInfo.sEnrollStatus=String.valueOf((int)zn);
                        byte[] sm={readBuf2[14],readBuf2[15],readBuf2[16],readBuf2[17],readBuf2[18],readBuf2[19],readBuf2[20],readBuf2[21],readBuf2[22],readBuf2[23],readBuf2[24],readBuf2[25],readBuf2[26],readBuf2[27],readBuf2[28],readBuf2[29]};
                        String str = null;
                        try {
                            str = new String(sm,"GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        userInfo.wName=str;
                        GlobalData.getInstance().userInfoList.add(userInfo);
                        if (mm<GlobalData.getInstance().userInfoID.size()-1){
                            socketClient.SendData2((byte) 0x79, bytes, devsn, (byte) 0x01, GlobalData.getInstance().userInfoID.get(mm));
                            mm++;
                        }else{
                            prgDialog.dismiss();
                            Toast.makeText(getActivity(),"用户加载完成",Toast.LENGTH_SHORT).show();
                        //    socketClient.Stop();
                        }
                    //    TimerStart();
                        break;
                    case SocketClient.NET_UNLINK:
                        // save the connected device's name
                        //    mConnectedDeviceName = msg.getData().getString(BluetoothReaderService.DEVICE_NAME);
                        //Toast.makeText(getApplicationContext(), "Connected to "  + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case SocketClient.MSG_STATUS:
                    //    Toast.makeText(getActivity(), "当前无网络连接",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        if(ExtApi.IsWifi(getActivity())){
        socketClient=new SocketClient(GlobalData.getInstance().IPAddr,Integer.valueOf(GlobalData.getInstance().IPPort));
        socketClient.SetMsgHandler(handler);
        if (GlobalData.getInstance().userInfoList.size()==0) {
            socketClient.SendCommand((byte) 0x64, bytes, devsn);
        }
        }else{
            Toast.makeText(getActivity(), "当前无WIFI，请联网操作",Toast.LENGTH_SHORT).show();
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   TimerStart();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWegit(view1);
            }
        });

        return view1;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
                    tv.setText("姓名");
                    break;
                case 2:
                    tv.setText("用户类型");
                    break;
                case 3:
                    tv.setText("指纹登记数");
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
        if(GlobalData.getInstance().userInfoList!=null){
            for (int i = 0; i <GlobalData.getInstance().userInfoList.size(); i++) {
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
                            tv.setText(GlobalData.getInstance().userInfoList.get(i).wUserID);
                            break;
                        case 1:
                            tv.setText(GlobalData.getInstance().userInfoList.get(i).wName);
                            break;
                        case 2:
                            tv.setText(GlobalData.getInstance().userInfoList.get(i).sUserType);
                            break;
                        case 3:
                            tv.setText(GlobalData.getInstance().userInfoList.get(i).sEnrollStatus);
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

        txCount.setText("Users:"+String.valueOf(count));
    }
    public void TimerStart(){
        if(startTimer==null){
            startTimer = new Timer();
            startHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (mm > GlobalData.getInstance().userInfoID.size()-1) {
                        prgDialog.dismiss();
                        TimeStop();
                        Toast.makeText(getActivity(),"用户加载完成",Toast.LENGTH_SHORT).show();
                    }


                }
            };
            startTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    socketClient.SendData2((byte) 0x79, bytes, devsn, (byte) 0x01, GlobalData.getInstance().userInfoID.get(mm));
                    mm++;
                    startHandler.sendMessage(message);
                }
            };
            startTimer.schedule(startTask, 0, 500);
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
