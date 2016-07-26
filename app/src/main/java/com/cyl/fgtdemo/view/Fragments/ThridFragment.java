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


public class ThridFragment extends Fragment {


    private EditText IPaddress;
    private EditText port;
    private EditText commpsw;
    private EditText deviceNum;
    private Button btn;

    public ThridFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_thrid, container, false);
        IPaddress=(EditText) view.findViewById(R.id.IP);
        port=(EditText) view.findViewById(R.id.port);
        commpsw=(EditText) view.findViewById(R.id.comm_psw);
        deviceNum=(EditText) view.findViewById(R.id.deviceNum);
        btn=(Button) view.findViewById(R.id.button);
        IPaddress.setText(GlobalData.getInstance().IPAddr);
        port.setText(String.valueOf(GlobalData.getInstance().IPPort));
        commpsw.setText(GlobalData.getInstance().comm_psw);
        deviceNum.setText(String.valueOf(GlobalData.getInstance().deviceNum));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if (checkout()){
                 GlobalData.getInstance().IPAddr=IPaddress.getText().toString();
                 GlobalData.getInstance().IPPort=Integer.valueOf(port.getText().toString());
                 GlobalData.getInstance().comm_psw=commpsw.getText().toString();
                 GlobalData.getInstance().deviceNum=Integer.valueOf(deviceNum.getText().toString());
                 GlobalData.getInstance().SaveConfig();
                 Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private boolean checkout(){
        int len=IPaddress.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入IP地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!ipCheck(IPaddress.getText().toString())){
            Toast.makeText(getActivity(), "当前不是一个合法的IP地址！", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=Integer.valueOf(port.getText().toString());
        if (len<=0||!(len>=0 && len<=65535)) {
            Toast.makeText(getActivity(), "请输入正确端口号", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=commpsw.getText().toString().length();
        if(len<=0){
            Toast.makeText(getActivity(), "请输入通信密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(len==8)){
            Toast.makeText(getActivity(), "通信密码必须为8位", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!GlobalData.getInstance().checkNum(commpsw.getText().toString())){
            Toast.makeText(getActivity(), "通信密码必须为不带任何标点符号的8位十六进制数", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=deviceNum.getText().toString().length();
        if(len<=0||Integer.valueOf(deviceNum.getText().toString())>=65535){
            Toast.makeText(getActivity(), "请输入正确设备编号(0-65535)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean ipCheck(String text) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
    }

