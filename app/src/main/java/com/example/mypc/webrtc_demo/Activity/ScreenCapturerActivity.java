package com.example.mypc.webrtc_demo.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mypc.webrtc_demo.R;
import com.example.mypc.webrtc_demo.WebRTCClient;
import com.example.mypc.webrtc_demo.WebRtcParame;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;

public class ScreenCapturerActivity extends AppCompatActivity implements MainActivity.SocketOn,WebRtcParame.ClientCallback{
    private static final String TAG = "ScreenCapturerActivity";
    WebRtcParame parame;
    WebRTCClient client;
    String mSocketAddress = "http://192.168.2.107:3000/";
    Socket socket = null;
    MessageHandler messageHandler = new MessageHandler(this);
    ScreenCapturerAndroid capturer;
    final int INTENT_RECODE = 10010;
    Intent mMediaProjectionPermissionResultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_capturer);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),INTENT_RECODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_RECODE){
            mMediaProjectionPermissionResultData = data;
        }

        createWebRTCClient();
    }

    private void createWebRTCClient() {
        capturer = new ScreenCapturerAndroid(mMediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                Log.e(TAG, "onStop: 获取screenCapturer失败");
            }
        });

        parame = new WebRtcParame(this, this, capturer,  720/2, 1280/2, 30);
        client = new WebRTCClient(parame);
        Toast.makeText(this,"初始化成功，可以开始socket连接了",Toast.LENGTH_LONG).show();
    }

    // onclick
    public void StartSocket(View view) {
        try {
            socket = IO.socket(mSocketAddress);
            socket.on("id",messageHandler.clientIdListener);
            socket.on("message",messageHandler.messageListener);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnGetID(String sockeID  ) {
        try {
            client.initStream();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name","android_device_Hzq");
            jsonObject.put("from",sockeID);
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
            String from = data.getString("from");
            String type = data.getString("type");

            Log.e(TAG, "from server--  from  " + from );
            Log.e(TAG, "from server--  type  " + type );



            switch (type){
                case "pleaseSendOffer":
                    Log.e(TAG, "to server " + " send offer");
                    WebRTCClient.Peer peer = client.addPeer(from);
                    peer.pc.createOffer(peer,client.mPeerConnConstraints);
                    break;
                case "disposeAnsewer":
                    Log.e(TAG, "to server " + " connect ");
                    if (client.peers.containsKey(from)){
                        JSONObject payload = data.optJSONObject("payload");
                        WebRTCClient.Peer peer1 = client.peers.get(from);
                        SessionDescription sdp = new SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(payload.optString("type")),
                                payload.optString("sdp")
                        );
                        peer1.pc.setRemoteDescription(peer1,sdp);
                        Log.e(TAG, "OnMessage:  执行" );
                    }


                    break;
                case "candidate":
                    if (client.peers.containsKey(from)){
                        if (client.peers.containsKey(from)) {
                            JSONObject payload = data.optJSONObject("payload");
                            PeerConnection pc = client.peers.get(from).pc;
                            if (pc.getRemoteDescription() != null) {
                                IceCandidate candidate = new IceCandidate(
                                        payload.optString("id"),
                                        payload.optInt("label"),
                                        payload.optString("candidate")
                                );
                                pc.addIceCandidate(candidate);
                            }
                        }
                    }
                    break;
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void OnCall(Object applicant) {
        socket.emit("message",applicant);
    }
}
