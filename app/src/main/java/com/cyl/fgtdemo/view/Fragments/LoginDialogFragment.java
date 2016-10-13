package com.cyl.fgtdemo.view.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.libraryview.ButtonRectangle;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginDialogFragment extends DialogFragment {

    TextInputLayout userNameWrapper;
    TextInputLayout emailWrapper;

    private ButtonRectangle btn1;

    private static SelectPicIndexInterface mSelectInterface;//声明一个回调接口变量，将用于把Activity强转


    public LoginDialogFragment() {
        // Required empty public constructor
    }




    public interface SelectPicIndexInterface{
        public void onSelectPicIndex();
    }
    // TODO: Rename and change types and number of parameters
    public static LoginDialogFragment newInstance(SelectPicIndexInterface selectPicIndexInterface) {

        LoginDialogFragment fragment = new LoginDialogFragment();
        mSelectInterface= selectPicIndexInterface;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login_dialog, container);
        userNameWrapper = (TextInputLayout) view.findViewById(R.id.userName);
        emailWrapper = (TextInputLayout) view.findViewById(R.id.email);
        btn1=(ButtonRectangle) view.findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkUserName()) {

                    userNameWrapper.setError("用户名不正确！");

                } else {
                    userNameWrapper.setError("");
                    if (!checkEmail()) {
                        emailWrapper.setError("密码不正确！");

                    } else {
                        GlobalData.getInstance().isonline=true;
                        GlobalData.getInstance().DefaultUser=userNameWrapper.getEditText().getText().toString();
                        emailWrapper.setError("");
                        mSelectInterface.onSelectPicIndex();//发送事件
                        dismiss();
                    }
                }
            }
        });
        return view;
    }

/*    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login_dialog, null);
        userNameWrapper = (TextInputLayout) view.findViewById(R.id.userName);
        emailWrapper = (TextInputLayout) view.findViewById(R.id.email);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("登录",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                if (!checkUserName()) {

                                    userNameWrapper.setError("用户名不正确！");

                                } else {
                                    userNameWrapper.setError("");
                                    if (!checkEmail()) {
                                        emailWrapper.setError("密码不正确！");

                                    } else {
                                        emailWrapper.setError("");
                                    }
                                }

                            }
                        }).setNegativeButton("取消", null);
        return builder.create();
    }*/
    private boolean checkUserName() {
        String userName = userNameWrapper.getEditText().getText().toString();
        if (userName.trim().equals("")||!userName.trim().equals(GlobalData.getInstance().DefaultUser))
            return false;
        else
            return true;
    }

    private boolean checkEmail() {
        String email = emailWrapper.getEditText().getText().toString();
        if (email.trim().equals("")||!email.trim().equals(GlobalData.getInstance().DefaultPassword))
            return false;
        else
            return true;
    }
}
