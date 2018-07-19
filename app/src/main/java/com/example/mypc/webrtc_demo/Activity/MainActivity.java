package com.example.mypc.webrtc_demo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mypc.webrtc_demo.R;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int PERMISSION_REQUEST_CODE = 8080;
    private static final String[] PERMISSIONS =
            {
                    "android.permission.MODIFY_AUDIO_SETTINGS",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.INTERNET",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE"
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void GetPermission(View view) {
        for (String permission:PERMISSIONS){
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                Log.e(TAG, "GetPermission: 没有" + permission +"  的权限" );
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){

                }else{
                    ActivityCompat.requestPermissions(this,new String[]{permission},PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    // onclick
    public void GoScreenCapturerDemo(View view) {
        Intent intent = new Intent(this, ScreenCapturerActivity.class);
        startActivity(intent);
    }


    interface SocketOn {
        void OnGetID(String socketID);
        void OnMessage(JSONObject data);
    }

}
