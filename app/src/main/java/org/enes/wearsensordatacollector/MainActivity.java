package org.enes.wearsensordatacollector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.enes.wearsensordatacollector.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {

    private ActivityMainBinding binding;

    private FloatingActionButton fab;

    private TextView text;

//    private ImageView iv_icon;

    private boolean is_working_now = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View cardView = binding.viewToSettings;
        cardView.setOnClickListener(this);
//        iv_icon = binding.ivIcon;
        text = binding.text;
        fab = binding.fab;
        fab.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        Log.e("test","onBackPressed");

        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        Log.e("test","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startAndStopHeartRateRecord();
                break;





        }
    }

    private SensorManager sensorManager;

    private Sensor heartRateSensor;


    private void startAndStopHeartRateRecord() {
        if(checkPermissions()) {
            Log.e("test", "start wwwwwww");
            if(sensorManager == null) {
                sensorManager =
                        ((SensorManager) getSystemService(SENSOR_SERVICE));
                heartRateSensor =
                        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
                sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            if(!is_working_now) {
                sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
                fab.setImageResource(R.drawable.ic_baseline_stop);
                is_working_now = true;
            }else {
                sensorManager.unregisterListener(this);
                fab.setImageResource(R.drawable.ic_baseline_play_arrow);
                text.setText("");
                is_working_now = false;
            }

        }
    }

    private final int permission_request_code = 1111111;

    private final String permission_str = Manifest.permission.BODY_SENSORS;

    private boolean checkPermissions() {
        int permission_state =
                ContextCompat.checkSelfPermission(this, permission_str);
        if(permission_state == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else if (permission_state == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this , new String[]{permission_str}, permission_request_code);
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == permission_request_code) {
            for(int request_result : grantResults) {
//                Log.e("test", request_result +"" );
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission_str)){
                    //denied
                    Log.e("test", "denied");
                    Toast.makeText(this, "wwwww", Toast.LENGTH_SHORT ).show();
                }else{
                    if(ActivityCompat.checkSelfPermission(this, permission_str) == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        Log.e("test", "allowed");
                        startAndStopHeartRateRecord();
                    } else{
                        //set to never ask again
                        Log.e("test", "set to never ask again");
                        //do something here.
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, permission_request_code);
                    }
                }
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == permission_request_code) {
            startAndStopHeartRateRecord();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("test", "onSensorChanged:" + event.sensor.getName());


        String msg = (int)event.values[0] + " BPM";
        text.setText(msg);

        Log.e("test", "test:" + msg);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e("test", "onAccuracyChanged:" + sensor.getName() +", " + accuracy);

    }
}