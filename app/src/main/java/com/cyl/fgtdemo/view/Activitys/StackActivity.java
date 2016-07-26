package com.cyl.fgtdemo.view.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.model.bean.WebSockecmd;
import com.cyl.fgtdemo.model.http.ExampleClient;
import com.cyl.fgtdemo.model.http.SocketClient;
import com.cyl.libraryview.ButtonRectangle;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class StackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);
    }
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, StackActivity.class);
        return intent;
    }

}
