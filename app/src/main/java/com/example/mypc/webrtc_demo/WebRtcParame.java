package com.example.mypc.webrtc_demo;

import android.content.Context;

import org.webrtc.VideoCapturer;

/**
 * Created by MyPC on 2018/7/11.
 */

public class WebRtcParame {
    public final ClientCallback callback;
    public final Context context;
    public final VideoCapturer capturer;

    public final int videoWidth;
    public final int videoHeight;
    public final int videoFps;

    public WebRtcParame(ClientCallback callback, Context context, VideoCapturer capturer, int videoWidth, int videoHeight, int videoFps) {
        this.callback = callback;
        this.context = context;
        this.capturer = capturer;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
    }

    public interface ClientCallback{
        void OnCall (Object applicant);
    }
}
