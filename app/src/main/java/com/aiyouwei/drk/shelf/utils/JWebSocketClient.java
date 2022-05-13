package com.aiyouwei.drk.shelf.utils;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        Log.i("JWebSocketClient", "onOpen() = "+handShakeData);
    }

    @Override
    public void onMessage(String message) {
        Log.i("JWebSocketClient", "onMessage() = "+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("JWebSocketClient", "onClose() =  code : "+code+" , reason : "+ reason+" , remote : "+remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.i("JWebSocketClient", "onError() = "+ex.getMessage());
    }
}