package com.cyl.fgtdemo.model.http;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by cylnhs on 2016/6/23.
 */
public class ExampleClient extends WebSocketClient {

    public ExampleClient(URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    public ExampleClient( URI serverURI ) {
        super( serverURI );
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
