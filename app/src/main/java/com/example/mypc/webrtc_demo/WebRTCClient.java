package com.example.mypc.webrtc_demo;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by MyPC on 2018/7/10.
 */

public class WebRTCClient {
    private static final String TAG = "WebRTCClient";
    private final WebRtcParame parame;
    private final VideoCapturer capturer;
    PeerConnectionFactory factory;
    Context context;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    public HashMap<String, Peer> peers = new HashMap<>();
    public MediaConstraints mPeerConnConstraints = new MediaConstraints();
    private VideoSource mVideoSource;
    private MediaStream mLocalMediaStream;
    private MediaConstraints videoConstraints;


    public WebRTCClient(WebRtcParame parame) {
        this.parame = parame;
        this.context = parame.context;
        this.capturer =  parame.capturer;


        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions
                .builder(context)
                .createInitializationOptions());

//        PeerConnectionFactory.Builder builder = PeerConnectionFactory.builder();
//        factory = builder.createPeerConnectionFactory();
        factory = new PeerConnectionFactory();

        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mPeerConnConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(parame.videoFps)));
        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(parame.videoFps)));
        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(parame.videoHeight)));
        mPeerConnConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(parame.videoWidth)));

        Log.e(TAG, "WebRTCClient: client  初始化" );
    }

    public void initStream() {
        try {
            mLocalMediaStream = factory.createLocalMediaStream("ARDAMS");
            videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(parame.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(parame.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(parame.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(parame.videoFps)));


            //将需要的  信道添加到  MediaStream 中
            mVideoSource = factory.createVideoSource(capturer);
            capturer.startCapture(parame.videoWidth, parame.videoHeight, parame.videoFps);
            VideoTrack localVideoTrack = factory.createVideoTrack("ARDAMSv1", mVideoSource);
            if (localVideoTrack == null){
                Log.e(TAG, "initStream: ");
            }
            localVideoTrack.setEnabled(true);
            mLocalMediaStream = factory.createLocalMediaStream("ARDAMS");
            mLocalMediaStream.addTrack(localVideoTrack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Peer implements SdpObserver, PeerConnection.Observer {
        public PeerConnection pc;
        public String id;

        public Peer(String id) {
            this.id = id;

            this.pc = factory.createPeerConnection(iceServers,videoConstraints,this);

            pc.addStream(mLocalMediaStream);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.e(TAG, "Peer onSignalingChange: ");
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.e(TAG, "onIceConnectionCha nge: "  + iceConnectionState.toString() + "  end");
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.e(TAG, "Peer onIceConnectionReceivingChange: ");
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.e(TAG, "Peer onIceGatheringChange: ");
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.e(TAG, "Peer onIceCandidate: ");
            try {
                JSONObject payload = new JSONObject();
                payload.put("label", iceCandidate.sdpMLineIndex);
                payload.put("id", iceCandidate.sdpMid);
                payload.put("candidate", iceCandidate.sdp);
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.e(TAG, "Peer onIceCandidatesRemoved: ");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.e(TAG, "Peer onAddStream: ");
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.e(TAG, "Peer onRemoveStream: ");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.e(TAG, "Peer onDataChannel: ");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.e(TAG, "Peer onRenegotiationNeeded: ");
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            Log.e(TAG, "Peer onAddTrack: ");
        }

        @Override
        public void onCreateSuccess(SessionDescription sdp) {
            Log.e(TAG, "Peer onCreateSuccess: ");

            // 当创建offer 成功的时候调用
            try {
                JSONObject payload = new JSONObject();
                payload.put("type",sdp.type.canonicalForm());
                payload.put("sdp",sdp.description);
                sendMessage(id,sdp.type.canonicalForm(),payload);
                pc.setLocalDescription(Peer.this,sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSetSuccess() {
            Log.e(TAG, "Peer onSetSuccess: ");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.e(TAG, "Peer onCreateFailure: ");
        }

        @Override
        public void onSetFailure(String s) {
            Log.e(TAG, "Peer onSetFailure: ");
        }
    }


    private void sendMessage(String id, String candidate, JSONObject payload) {
        try {
            JSONObject message = new JSONObject();
            message.put("to", id);
            message.put("type", candidate);
            message.put("payload", payload);
            parame.callback.OnCall(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Peer addPeer(String id){
        Peer peer = new Peer(id);
        peers.put(id, peer);
        return peer;
    }

    public void onDestroy(){
        for (Peer peer:peers.values()) {
            peer.pc.dispose();
        }
        if (factory != null){
            factory.dispose();
        }
        if (mVideoSource != null){
            mVideoSource.dispose();
        }
        Log.e(TAG, "onDestroy:   client 清空完毕"  );
    }

}
