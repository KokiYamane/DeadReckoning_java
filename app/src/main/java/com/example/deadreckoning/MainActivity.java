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
    private SensorManager m_sensorManager;

    private TextView m_accel_val_x_TextView, m_accel_val_y_TextView, m_accel_val_z_TextView;
    private TextView m_rot_val_x_TextView, m_rot_val_y_TextView, m_rot_val_z_TextView;
    private TextView m_vel_val_x_TextView, m_vel_val_y_TextView, m_vel_val_z_TextView;
    private TextView m_pos_val_x_TextView, m_pos_val_y_TextView, m_pos_val_z_TextView;

    private float m_sensor_accel_val_x, m_sensor_accel_val_y, m_sensor_accel_val_z;
    private float m_sensor_rot_val_x, m_sensor_rot_val_y, m_sensor_rot_val_z;

    private Runnable m_runnable;
    private final Handler m_handler = new Handler();

    double filterCoefficient = 0.9;
    double[] lowpassValue = {0,0,0};

    final double timeSpan = 0.02;      // 時間差分[s]
    double time = 0;

    double[] speed = {0,0,0};
    double[] diff = {0,0,0};
    double[] oldacc = {0,0,0};
    double[] oldspeed = {0,0,0};

    double[] rotdiff = {0,0,0};
    double[] oldrotacc = {0,0,0};
    double[] oldrotspeed = {0,0,0};

    String fileName = "pos.txt";

    private static final int MATRIX_SIZE = 16;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteFile(fileName);
        sampleFileOutput("time,acc_x,acc_y,acc_z,vel_x,vel_y,vel_z,pos_x,pos_y,pos_z," +
                "rot_x,rot_y,rot_z,rotvel_x,rotvel_y,rotvel_z,rotpos_x,rotpos_y,rotpos_z\n", fileName);

        m_sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        m_accel_val_x_TextView = findViewById(R.id.accel_val_x);
        m_accel_val_y_TextView = findViewById(R.id.accel_val_y);
        m_accel_val_z_TextView = findViewById(R.id.accel_val_z);
        m_sensor_accel_val_x = m_sensor_accel_val_y = m_sensor_accel_val_z = 0;

        m_rot_val_x_TextView = findViewById(R.id.rot_val_x);
        m_rot_val_y_TextView = findViewById(R.id.rot_val_y);
        m_rot_val_z_TextView = findViewById(R.id.rot_val_z);
        m_sensor_rot_val_x = m_sensor_rot_val_y = m_sensor_rot_val_z = 0;

        m_vel_val_x_TextView = findViewById(R.id.vel_val_x);
        m_vel_val_y_TextView = findViewById(R.id.vel_val_y);
        m_vel_val_z_TextView = findViewById(R.id.vel_val_z);

        m_pos_val_x_TextView = findViewById(R.id.pos_val_x);
        m_pos_val_y_TextView = findViewById(R.id.pos_val_y);
        m_pos_val_z_TextView = findViewById(R.id.pos_val_z);

        if(isExternalStorageWritable() == true)StartCyclicHandler();       // 周期ハンドラ開始
    }

    @Override protected void onResume() {
        super.onResume();

        // Event Listener登録
        Sensor accel = m_sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        m_sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        Sensor rot = m_sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        m_sensorManager.registerListener(this, rot, SensorManager.SENSOR_DELAY_GAME);
        Sensor gyro = m_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        m_sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override protected void onPause() {
        super.onPause();
        m_sensorManager.unregisterListener(this);   // Event Listener登録解除
        StoptCyclicHandler();                       // 周期ハンドラ停止
    }

    // センサーの値更新
    @Override public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            m_sensor_accel_val_x = event.values[0];
            m_sensor_accel_val_y = event.values[1];
            m_sensor_accel_val_z = event.values[2];

            if(time < 2) return;
            for(int i = 0; i < 3; i++) {
                // ローパスフィルター(現在の値 = 係数 * ひとつ前の値 ＋ (1 - 係数) * センサの値)
                lowpassValue[i] = lowpassValue[i] * filterCoefficient + event.values[i] * (1 - filterCoefficient);

                speed[i] += (oldacc[i] + lowpassValue[i])/2.0 * timeSpan;
                diff[i] += (oldspeed[i] + speed[i])/2.0 * timeSpan;

                oldacc[i] = lowpassValue[i];
                oldspeed[i] = speed[i];
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            m_sensor_rot_val_x = event.values[0];
            m_sensor_rot_val_y = event.values[1];
            m_sensor_rot_val_z = event.values[2];

            if(time < 2) return;
            for(int i = 0; i < 3; i++) {
                // ローパスフィルター(現在の値 = 係数 * ひとつ前の値 ＋ (1 - 係数) * センサの値)
                lowpassValue[i] = lowpassValue[i] * filterCoefficient + event.values[i] * (1 - filterCoefficient);
                rotdiff[i] += (oldrotacc[i] + lowpassValue[i])/2.0 * timeSpan;
                oldrotspeed[i] = lowpassValue[i];
            }
        }
    }

    // 値表示
    protected void StartCyclicHandler(){
        m_runnable = new Runnable() {
            @Override public void run() {
            m_accel_val_x_TextView.setText(String.format("%.3f", m_sensor_accel_val_x));
            m_accel_val_y_TextView.setText(String.format("%.3f", m_sensor_accel_val_y));
            m_accel_val_z_TextView.setText(String.format("%.3f", m_sensor_accel_val_z));

            m_rot_val_x_TextView.setText(String.format("%.3f", m_sensor_rot_val_x));
            m_rot_val_y_TextView.setText(String.format("%.3f", m_sensor_rot_val_y));
            m_rot_val_z_TextView.setText(String.format("%.3f", m_sensor_rot_val_z));

            m_vel_val_x_TextView.setText(String.format("%.3f", speed[0]));
            m_vel_val_y_TextView.setText(String.format("%.3f", speed[1]));
            m_vel_val_z_TextView.setText(String.format("%.3f", speed[2]));

            m_pos_val_x_TextView.setText(String.format("%.3f", diff[0]));
            m_pos_val_y_TextView.setText(String.format("%.3f", diff[1]));
            m_pos_val_z_TextView.setText(String.format("%.3f", diff[2]));

            time += timeSpan;

            String text = new String();
            text += String.format("%.1f",time);
            text += String.format(",%.3f",m_sensor_accel_val_x);
            text += String.format(",%.3f",m_sensor_accel_val_y);
            text += String.format(",%.3f",m_sensor_accel_val_z);
            text += String.format(",%.3f",speed[0]);
            text += String.format(",%.3f",speed[1]);
            text += String.format(",%.3f",speed[2]);
            text += String.format(",%.3f",diff[0]);
            text += String.format(",%.3f",diff[1]);
            text += String.format(",%.3f",diff[2]);
            text += String.format(",%.3f",m_sensor_rot_val_x);
            text += String.format(",%.3f",m_sensor_rot_val_y);
            text += String.format(",%.3f",m_sensor_rot_val_z);
            text += String.format(",%.3f",rotdiff[0]);
            text += String.format(",%.3f",rotdiff[1]);
            text += String.format(",%.3f",rotdiff[2]);
            text += String.format("\n");

            sampleFileOutput(text, fileName);

            m_handler.postDelayed(this, 20);    // 200msスリープ
            }
        };
        m_handler.post(m_runnable);     // スレッド起動
    }

    protected void StoptCyclicHandler() {
        m_handler.removeCallbacks(m_runnable);
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