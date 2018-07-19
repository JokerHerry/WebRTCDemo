package com.example.mypc.webrtc_demo.Activity;

import android.util.Log;

import com.example.mypc.webrtc_demo.WebRTCClient;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

/**
 * Created by MyPC on 2018/7/10.
 */

class MessageHandler {
    public static final String TAG = "MessageHandler";
    private final MainActivity.SocketOn socketOn;


    public MessageHandler(MainActivity.SocketOn socketOn) {
        this.socketOn = socketOn;
    }

    public Emitter.Listener clientIdListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String id = (String) args[0];
            Log.e(TAG, "拿到自己的 socket ID"  + id);
            socketOn.OnGetID(id);
        }
    };

    public Emitter.Listener messageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject)args[0];
            Log.e(TAG, "messageListener call data : " + data);

            socketOn.OnMessage(data);

        }
    };

    public Emitter.Listener connect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "clientIdListener call connect : ");
        }
    };

    public Emitter.Listener disconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "clientIdListener call disconnect : ");
        }
    };

    public Emitter.Listener error = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "clientIdListener call error : ");
        }
    };


}
