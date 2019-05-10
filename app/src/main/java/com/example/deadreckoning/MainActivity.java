package com.example.deadreckoning;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    // 画面表示
    private TextView acc_x_text, acc_y_text, acc_z_text;
    private TextView vel_x_text, vel_y_text, vel_z_text;
    private TextView pos_x_text, pos_y_text, pos_z_text;
    private TextView rot_x_text, rot_y_text, rot_z_text;

    // 周期ハンドラ
    private Runnable runnable;
    private final Handler handler = new Handler();
    
    // 時間
    final double timeSpan = 0.02;
    double time = 0;
    double start;

    // 加速度センサー
    float[] acc_device = new float[3];
    float[] acc_world = new float[3];
    float[] speed = new float[3];
    float[] diff = new float[3];
    float[] oldacc = new float[3];
    float[] oldspeed = new float[3];

    // ジャイロセンサー
    float[] angularVelocity = new float[3];
    float[] angulardiff = new float[3];
    float[] oldAngularVelocity = new float[3];

    // 地磁気・重力センサー
    float[] geomagnetic = new float[3];
    float[] gravity = new float[3];
    float[] rotationMatrix = new float[9];
    float[] attitude = new float[3];

    // 出力ファイル名
    String fileName = "pos.txt";

    protected final static float RAD2DEG = 180/(float)Math.PI;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteFile(fileName);
        sampleFileOutput("time," +
                "acc_x,acc_y,acc_z," +
                "vel_x,vel_y,vel_z," +
                "pos_x,pos_y,pos_z," +
                "rot_x,rot_y,rot_z," +
                "rotvel_x,rotvel_y,rotvel_z," +
                "rotpos_x,rotpos_y,rotpos_z\n", fileName);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        acc_x_text = findViewById(R.id.accel_val_x);
        acc_y_text = findViewById(R.id.accel_val_y);
        acc_z_text = findViewById(R.id.accel_val_z);

        rot_x_text = findViewById(R.id.rot_val_x);
        rot_y_text = findViewById(R.id.rot_val_y);
        rot_z_text = findViewById(R.id.rot_val_z);

        vel_x_text = findViewById(R.id.vel_val_x);
        vel_y_text = findViewById(R.id.vel_val_y);
        vel_z_text = findViewById(R.id.vel_val_z);

        pos_x_text = findViewById(R.id.pos_val_x);
        pos_y_text = findViewById(R.id.pos_val_y);
        pos_z_text = findViewById(R.id.pos_val_z);

        if(isExternalStorageWritable() == true)StartCyclicHandler();       // 周期ハンドラ開始
        start = System.currentTimeMillis()/1000.0;
    }

    @Override protected void onResume() {
        super.onResume();

        // Event Listener登録
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);   // Event Listener登録解除
        StoptCyclicHandler();                     // 周期ハンドラ停止
    }

    public float LPF(float value, float oldvalue, double Coefficient) {
        return oldvalue * (float)Coefficient + value * (1 - (float)Coefficient);
    }

    // センサーの値更新
    @Override public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            acc_device = event.values.clone();

            if(time < 2) return;
            for(int i = 0; i < 3; i++) {
                float lowpassValue = LPF(acc_device[i],oldacc[i], 0.9);
                
                speed[i] += (oldacc[i] + lowpassValue)/2.0 * timeSpan;
                diff[i]  += (oldspeed[i] + speed[i])/2.0 * timeSpan;

                oldacc[i]   = lowpassValue;
                oldspeed[i] = speed[i];
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            angularVelocity = event.values.clone();

            if(time < 2) return;
            for(int i = 0; i < 3; i++) {
                float lowpassValue = LPF(angularVelocity[i],oldAngularVelocity[i], 0.9);
                angulardiff[i] += (oldAngularVelocity[i] + lowpassValue)/2.0 * timeSpan;
                oldAngularVelocity[i] = lowpassValue;
            }
        }

        switch(event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_GYROSCOPE:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_GRAVITY:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
        }

        if(geomagnetic != null && gravity != null){
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, attitude);
        }
    }

    // 周期ハンドラ　50[Hz]
    protected void StartCyclicHandler(){
        runnable = new Runnable() {
            @Override public void run() {
                time = System.currentTimeMillis()/1000.0 - start;

                // 画面出力
                acc_x_text.setText(String.format("%.3f", acc_device[0]));
                acc_y_text.setText(String.format("%.3f", acc_device[1]));
                acc_z_text.setText(String.format("%.3f", acc_device[2]));

//                rot_x_text.setText(String.format("%.3f", angulardiff[0] * RAD2DEG));
//                rot_y_text.setText(String.format("%.3f", angulardiff[1] * RAD2DEG));
//                rot_z_text.setText(String.format("%.3f", angulardiff[2] * RAD2DEG));

                rot_x_text.setText(Integer.toString( (int)(attitude[0] * RAD2DEG) ));
                rot_y_text.setText(Integer.toString( (int)(attitude[1] * RAD2DEG) ));
                rot_z_text.setText(Integer.toString( (int)(attitude[2] * RAD2DEG) ));

                vel_x_text.setText(String.format("%.3f", speed[0]));
                vel_y_text.setText(String.format("%.3f", speed[1]));
                vel_z_text.setText(String.format("%.3f", speed[2]));

                pos_x_text.setText(String.format("%.3f", diff[0]));
                pos_y_text.setText(String.format("%.3f", diff[1]));
                pos_z_text.setText(String.format("%.3f", diff[2]));

                // ファイル出力
                String text = new String();
                text += String.format("%.3f",time);
                text += String.format(",%.3f", acc_world[0]);
                text += String.format(",%.3f", acc_world[1]);
                text += String.format(",%.3f", acc_world[2]);
                text += String.format(",%.3f", speed[0]);
                text += String.format(",%.3f", speed[1]);
                text += String.format(",%.3f", speed[2]);
                text += String.format(",%.3f", diff[0]);
                text += String.format(",%.3f", diff[1]);
                text += String.format(",%.3f", diff[2]);
                text += String.format(",%.3f", angularVelocity[0]);
                text += String.format(",%.3f", angularVelocity[1]);
                text += String.format(",%.3f", angularVelocity[2]);
                text += String.format(",%.3f", angulardiff[0]);
                text += String.format(",%.3f", angulardiff[1]);
                text += String.format(",%.3f", angulardiff[2]);
                text += String.format(",%.3f", attitude[0]);
                text += String.format(",%.3f", attitude[1]);
                text += String.format(",%.3f", attitude[2]);
                text += String.format("\n");
                sampleFileOutput(text, fileName);

                handler.postDelayed(this, 20);    // 20msスリープ
            }
        };
        handler.post(runnable);     // スレッド起動
    }

    protected void StoptCyclicHandler() {
        handler.removeCallbacks(runnable);
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sampleFileOutput(String text, String fileName) {

        try {
            FileOutputStream out = openFileOutput(fileName,MODE_APPEND);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            System.out.println(text);
            pw.write(text);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}