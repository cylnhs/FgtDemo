package com.cyl.fgtdemo.view.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.libraryview.Button;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;



public class RecordFragment extends Fragment {

    private EditText User_name;
    private EditText old_psw;
    private EditText new_psw;
    private EditText qt_psw;
    private Button btn,btn1;

    public RecordFragment() {
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
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        User_name=(EditText) view.findViewById(R.id.IP);
        old_psw=(EditText) view.findViewById(R.id.port);
        new_psw=(EditText) view.findViewById(R.id.comm_psw);
        qt_psw=(EditText) view.findViewById(R.id.deviceNum);
        btn=(Button) view.findViewById(R.id.button);
        btn1=(Button) view.findViewById(R.id.button1);
        User_name.setText(GlobalData.getInstance().DefaultUser);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkout()){
                 if(new_psw.getText().toString().equals(qt_psw.getText().toString())){
                  if(old_psw.getText().toString().equals(GlobalData.getInstance().DefaultPassword)){
                       GlobalData.getInstance().DefaultUser=User_name.getText().toString();
                       GlobalData.getInstance().DefaultPassword=new_psw.getText().toString();
                       GlobalData.getInstance().SaveConfig();
                      User_name.setText("");
                      old_psw.setText("");
                      new_psw.setText("");
                      qt_psw.setText("");
                       Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "当前密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_name.setText("");
                old_psw.setText("");
                new_psw.setText("");
                qt_psw.setText("");
            }
        });
        return view;
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

    private boolean checkout(){
        int len=User_name.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入IP地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=old_psw.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入当前密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=new_psw.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入新密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=qt_psw.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入确认密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
