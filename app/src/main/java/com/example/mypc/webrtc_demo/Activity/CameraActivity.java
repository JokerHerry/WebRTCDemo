package com.example.mypc.webrtc_demo.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mypc.webrtc_demo.R;
import com.example.mypc.webrtc_demo.WebRTCClient;
import com.example.mypc.webrtc_demo.WebRtcParame;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.ScreenCapturerAndroid;

import java.net.URISyntaxException;

public class CameraActivity extends AppCompatActivity implements MainActivity.SocketOn {
    private static final String TAG = "CameraActivity";
    WebRtcParame parame;
    WebRTCClient client;
    String mSocketAddress ;
    Socket socket = null;
    MessageHandler messageHandler = new MessageHandler(this);
    ScreenCapturerAndroid capturer;
    final int INTENT_RECODE = 10010;
    Intent mMediaProjectionPermissionResultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSocketAddress = getResources().getString(R.string.host);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


    }



    @Override
    public void OnGetID(String socketID) {
        try {
            client.initStream();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name","android_device_Hzq");
            jsonObject.put("from",socketID);
            jsonObject.put("type","readyToStream");

            socket.emit("readyToStream",jsonObject);

            Log.e(TAG, "OnGetID: 我这边已经准备完成了" );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnMessage(JSONObject data) {
        try {
            socket = IO.socket(mSocketAddress);
            socket.on("id",messageHandler.clientIdListener);
            socket.on("message",messageHandler.messageListener);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    //Onclick
    public void StartSocket(View view) {


    }
    public void StopSocket(View view) {

    }
}
