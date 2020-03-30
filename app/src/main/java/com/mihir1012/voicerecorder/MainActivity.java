package com.mihir1012.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button play, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private SharedPreferences preferences;

    public static final int request_code = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.PlatBtn);
        record = findViewById(R.id.RecordStop);

        preferences = getSharedPreferences("pref",MODE_PRIVATE);
        if(preferences.getBoolean("FirstTime",true)){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("RecordStopToggle",0);
            editor.putBoolean("FirstTime",false);
            editor.commit();
        }
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        if (checkPermissionFromDevice()) {
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (preferences.getInt("RecordStopToggle", 0) == 0 || preferences.getInt("RecordStopToggle", 0) == 2) {
                            record.setText("STOP");
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("RecordStopToggle", 1);
                            editor.commit();
                        } else {
                            record.setText("RECORD");
                            play.setEnabled(true);
                            preferences.edit().putInt("RecordStopToggle", 2).commit();
                        }

                    } catch (IllegalStateException ise) {
                        // make something ...
                    } catch (IOException ioe) {
                        // make something
                    }
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        if (preferences.getInt("RecordStopToggle", 0) == 2) {
                            mediaPlayer.setDataSource(outputFile);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "HERE HERE", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("EXCEPTION PLAYING E", e.getMessage() + e.getCause());
                        Toast.makeText(getApplicationContext(), "there There", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            requestPermissionFromDevice();
        }
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case request_code:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    Toast.makeText(getApplicationContext(),"permission granted...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int storage_permission= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recorder_permssion=ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return storage_permission == PackageManager.PERMISSION_GRANTED && recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }


}
