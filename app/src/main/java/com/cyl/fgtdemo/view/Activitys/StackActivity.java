package com.cyl.fgtdemo.view.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private Toolbar toolbar;
    private ImageView avatar;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);
        avatar=(ImageView)findViewById(R.id.id_toolbar_image);
        textView=(TextView)findViewById(R.id.id_about);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setTitle(R.string.menu_title_about);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        avatar.setImageResource(R.drawable.plasmid);
        textView.setText("深圳菲格特智能科技有限公司门禁产品手机APP,实现远程开关门，查看全部登记用户，查看考勤记录");
    }
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, StackActivity.class);
        return intent;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
