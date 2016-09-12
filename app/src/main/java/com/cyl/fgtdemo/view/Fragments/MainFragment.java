package com.cyl.fgtdemo.view.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.fgtdemo.model.bean.WebSockecmd;
import com.cyl.fgtdemo.model.http.ExampleClient;
import com.cyl.fgtdemo.model.http.SocketClient;
import com.cyl.libraryview.ButtonRectangle;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainFragment extends Fragment {




    private ButtonRectangle btn1,btn2;

    private Handler handler;

    public  SocketClient socketClient;

    private byte[] bytes;
    private byte[] devsn;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, null);
        btn1=(ButtonRectangle) view.findViewById(R.id.button);
        btn2=(ButtonRectangle) view.findViewById(R.id.button1);
        bytes = GlobalData.getInstance().hexStringToBytes( GlobalData.getInstance().comm_psw);
        devsn= GlobalData.getInstance().changeByte( GlobalData.getInstance().deviceNum);
        socketClient=new SocketClient(GlobalData.getInstance().IPAddr,Integer.valueOf(GlobalData.getInstance().IPPort));
        socketClient.SetMsgHandler(handler);
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
                        //检查长度和校验后在处理
                    //    ReceiveCommand(readBuf);
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


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    socketClient.SendCommand((byte) 0x80,bytes,devsn);

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    socketClient.SendCommand((byte) 0x81,bytes,devsn);
            }
        });
        return view;
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


}
