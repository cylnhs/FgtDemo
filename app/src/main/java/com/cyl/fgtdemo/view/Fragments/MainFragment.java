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
import com.cyl.fgtdemo.model.http.SocketClient;
import com.cyl.libraryview.ButtonRectangle;

public class MainFragment extends Fragment {



    private EditText IPaddress;
    private EditText IPport;
    private ButtonRectangle btn1;

    private Handler handler;

    private String ipport;

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
        IPaddress=(EditText) view.findViewById(R.id.IP);
        IPport=(EditText) view.findViewById(R.id.port);
        btn1=(ButtonRectangle) view.findViewById(R.id.button);
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
                ipport=IPport.getText().toString();
                if(!ipport.isEmpty()&&!IPaddress.getText().toString().isEmpty()){
                    SocketClient socketClient=new SocketClient(IPaddress.getText().toString(),Integer.valueOf(ipport));
                    socketClient.SetMsgHandler(handler);
                    //     socketClient.SendCommand();
                    //     Toast.makeText(getContext(),IPaddress.getText(),Toast.LENGTH_SHORT).show();
                }

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
