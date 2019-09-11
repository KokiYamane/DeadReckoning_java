package com.example.deadreckoning;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    // 画面表示
    private TextView time_text;
    private TextView acc_x_text, acc_y_text, acc_z_text;
    private TextView vel_x_text, vel_y_text, vel_z_text;
    private TextView pos_x_text, pos_y_text, pos_z_text;
    private TextView dig_mag_x_text, dig_mag_y_text, dig_mag_z_text;
    private TextView dig_gyro_x_text, dig_gyro_y_text, dig_gyro_z_text;
    private TextView step_text, pos_step_x_text, pos_step_y_text;

    // 周期ハンドラ
    private Runnable runnable;
    private final Handler handler = new Handler();
    
    // 時間
    double time = 0;
    double start;
    double accTimeSpan = 0.02;
    double accNowTime = 0;
    double accOldTime = 0;
    double gyroTimeSpan = 0.02;
    double gyroNowTime = 0;
    double gyroOldTime = 0;
    double lastInitTime = 0;

    // 加速度センサー
    float[] acc_device = new float[3];
    float[] acc_world = new float[3];
    float[] speed = new float[3];
    float[] diff = new float[3];
    float[] oldAcc = new float[3];
    float[] oldSpeed = new float[3];

    // ジャイロセンサー
    float[] angularVelocity = new float[3];
    float[] angularVelocity_LPF = new float[3];
    float[] rad_gyro = new float[3];
    float[] oldAngularVelocity = new float[3];

    // 地磁気・重力センサー
    float[] geomagnetic = new float[3];
    float[] gravity = new float[3];
    float[] rotationMatrix = new float[9];
    float[] rad_mag = new float[3];
    float[] rad_mag_tmp = new float[3];
    float[] rad_mag_init = new float[3];
    protected final static int FILTER_DATA_NUM = 1;
    float[][] angularVelocity_old = new float[3][FILTER_DATA_NUM];

    // 歩数センサー
    float step;
    float initStep;
    float[] pos_step = new float[2];
    float[] pos_step_gyro = new float[2];
    float[] pos_step_mag = new float[2];
    protected final static double STRIDE = 0.5;

    // ジャイロ・地磁気の値を組み合わせた角度
    float[] rad = new float[3];

    // ファイル出力用配列
    List<Float> timeList = new ArrayList<>();
    List<Float> accList_x = new ArrayList<>();
    List<Float> accList_y = new ArrayList<>();
    List<Float> accList_z = new ArrayList<>();

    // 出力ファイル名
    String fileName = "data.csv";

    protected final static float RAD2DEG = 180/(float)Math.PI;
    protected final static int X = 0;
    protected final static int Y = 1;
    protected final static int Z = 2;

    // アプリ起動時に実行
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // センサーマネージャー起動
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // イベントリスナー登録（センサー有効化）
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
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_GAME);

        // 画面表示初期化
        time_text = findViewById(R.id.time_val);

        acc_x_text = findViewById(R.id.accel_val_x);
        acc_y_text = findViewById(R.id.accel_val_y);
        acc_z_text = findViewById(R.id.accel_val_z);

        vel_x_text = findViewById(R.id.vel_val_x);
        vel_y_text = findViewById(R.id.vel_val_y);
        vel_z_text = findViewById(R.id.vel_val_z);

        pos_x_text = findViewById(R.id.pos_val_x);
        pos_y_text = findViewById(R.id.pos_val_y);
        pos_z_text = findViewById(R.id.pos_val_z);

        dig_gyro_x_text = findViewById(R.id.dig_gyro_val_x);
        dig_gyro_y_text = findViewById(R.id.dig_gyro_val_y);
        dig_gyro_z_text = findViewById(R.id.dig_gyro_val_z);

        dig_mag_x_text = findViewById(R.id.dig_mag_val_x);
        dig_mag_y_text = findViewById(R.id.dig_mag_val_y);
        dig_mag_z_text = findViewById(R.id.dig_mag_val_z);

        step_text       = findViewById(R.id.step_val);
        pos_step_x_text = findViewById(R.id.pos_step_val_x);
        pos_step_y_text = findViewById(R.id.pos_step_val_y);

        // ファイル出力
        deleteFile(fileName);
        sampleFileOutput(
                "time, " +
                "acc_x, acc_y, acc_z, " +
//                "vel_x, vel_y, vel_z, " +
//                "pos_x, pos_y, pos_z, " +
//                "rad_vel_x, rad_vel_y, rad_vel_z, " +
//                "rad_vel_LPF_x, rad_vel_LPF_y, rad_vel_LPF_z, " +
//                "rad_gyro_x, rad_gyro_y, rad_gyro_z, " +
//                "rad_mag_x, rad_mag_y, rad_mag_z, " +
//                "pos_step_gyro_x, pos_step_gyro_y, " +
//                "pos_step_mag_x, pos_step_mag_y, " +
//                "pos_step_x, pos_step_y" +
                "\n",
                fileName);

        // 実行開始時刻
        start = System.currentTimeMillis()/1000.0;
    }

    // アプリが前面に来たときに実行
    @Override protected void onResume() {
        super.onResume();

        // 周期ハンドラ開始
        if(isExternalStorageWritable() == true)StartCyclicHandler();
    }

    // アプリがバックグラウンドに移行したとき実行
    @Override protected void onPause() {
        super.onPause();

        // 周期ハンドラ停止
        StoptCyclicHandler();

        // ファイル書き込み
        for(int i = 0; i < accList_x.size(); i ++) {
            String text = new String();
            text += String.format("%7.4f",timeList.get(i));
            text += String.format(",%7.4f", accList_x.get(i));
            text += String.format(",%7.4f", accList_y.get(i));
            text += String.format(",%7.4f", accList_z.get(i));
            text += String.format("\n");
            sampleFileOutput(text, fileName);
        }
    }

    // アプリ終了時に実行
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // イベントリスナー登録解除
        sensorManager.unregisterListener(this);
    }

    // ローパスフィルタ(移動平均)
    public float LPF(float value, float oldValue, double Coefficient) {
        return oldValue * (float)Coefficient + value * (1 - (float)Coefficient);
    }

    // センサーの値更新
    @Override public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){

            // 加速度センサー
            case Sensor.TYPE_LINEAR_ACCELERATION:
                acc_device = event.values.clone();

                double accTime = System.currentTimeMillis()/1000.0 - start;
                timeList.add((float)accTime);
                accList_x.add(acc_device[X]);
                accList_y.add(acc_device[Y]);
                accList_z.add(acc_device[Z]);

                // 時間差計算
                accNowTime = System.currentTimeMillis()/1000.0;
                accTimeSpan = accNowTime - accOldTime;
                accOldTime = accNowTime;

                if(time < 2) return;
                for(int i = 0; i < 3; i++) {
                    // ローパスフィルタ
                    float lowPassValue = LPF(acc_device[i],oldAcc[i], 0.9);

                    // 台形積分
                    speed[i] += (oldAcc[i] + lowPassValue)/2.0 * accTimeSpan;
                    diff[i]  += (oldSpeed[i] + speed[i])/2.0 * accTimeSpan;

                    // 現在の値保存
                    oldAcc[i]   = lowPassValue;
                    oldSpeed[i] = speed[i];
                }
                break;

            // ジャイロセンサー
            case Sensor.TYPE_GYROSCOPE:
                angularVelocity = event.values.clone();

                // 時間差計算
                gyroNowTime = System.currentTimeMillis()/1000.0;
                gyroTimeSpan = gyroNowTime - gyroOldTime;
                gyroOldTime = gyroNowTime;

                if(time < 2) return;
                for(int i = 0; i < 3; i++) {
                    // データ更新
                    for(int j = 0; j < FILTER_DATA_NUM-1; j++) {
                        angularVelocity_old[i][j] = angularVelocity_old[i][j+1];
                    }
                    angularVelocity_old[i][FILTER_DATA_NUM-1] = angularVelocity[i];

                    // 移動平均計算
                    float sum = 0;
                    for(int j = 0; j < FILTER_DATA_NUM; j++) {
                        sum += angularVelocity_old[i][j];
                    }
                    angularVelocity_LPF[i] = sum / (float)FILTER_DATA_NUM;

                    // 台形積分
                    rad_gyro[i] += (oldAngularVelocity[i] + angularVelocity_LPF[i])/2.0 * gyroTimeSpan;
                    if(rad_gyro[i] >  Math.PI) rad_gyro[i] -= 2*Math.PI;
                    if(rad_gyro[i] < -Math.PI) rad_gyro[i] += 2*Math.PI;

                    // 台形積分
                    rad[i] += (oldAngularVelocity[i] + angularVelocity_LPF[i])/2.0 * gyroTimeSpan;
                    if(rad[i] >  Math.PI) rad[i] -= 2*Math.PI;
                    if(rad[i] < -Math.PI) rad[i] += 2*Math.PI;

                    // 現在の値保存
                    oldAngularVelocity[i] = angularVelocity_LPF[i];
                }
                break;

            // 重力センサー
            case Sensor.TYPE_GRAVITY:
                gravity = event.values.clone();
                break;

            // 地磁気センサー
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;

            // 歩数センサー
            case Sensor.TYPE_STEP_COUNTER:
                if(initStep == 0) {
                    initStep = event.values[0];
                    break;
                }

                step = event.values[0] - initStep;

                // 歩数と角度での自己位置推定
                pos_step_gyro[X] += STRIDE * Math.cos(rad_gyro[Z]);
                pos_step_gyro[Y] += STRIDE * Math.sin(rad_gyro[Z]);
                pos_step_mag[X]  += STRIDE * Math.cos(rad_mag[Z]);
                pos_step_mag[Y]  += STRIDE * Math.sin(rad_mag[Z]);
                pos_step[X]      += STRIDE * Math.cos(rad[Z]);
                pos_step[Y]      += STRIDE * Math.sin(rad[Z]);
                break;
        }

        // 重力・地磁気から角度を算出
        if(geomagnetic != null && gravity != null){
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, rad_mag_tmp);

            // 地磁気角度初期値を0とする
            if(time < 2) {
                rad_mag_init[X] =  rad_mag_tmp[1];
                rad_mag_init[Y] =  rad_mag_tmp[2];
                rad_mag_init[Z] = -rad_mag_tmp[0];
            }

            // ロール・ヨー・ピッチからXYZに変換
            else {
                rad_mag[X] =  rad_mag_tmp[1] - rad_mag_init[X];
                rad_mag[Y] =  rad_mag_tmp[2] - rad_mag_init[Y];
                rad_mag[Z] = -rad_mag_tmp[0] - rad_mag_init[Z];
                for(int i = 0; i < 3; i++) {
                    if(rad_mag[i] >  Math.PI) rad_mag[i] -= 2*Math.PI;
                    if(rad_mag[i] < -Math.PI) rad_mag[i] += 2*Math.PI;
                }
            }

            // ジャイロ角度を地磁気角度で定期的に補正
            if(time - lastInitTime > 60) {
                rad[X] = rad_mag[X];
                rad[Y] = rad_mag[Y];
                rad[Z] = rad_mag[Z];
                lastInitTime = time;
            }
        }
    }

    // 周期ハンドラ　50[Hz]
    protected void StartCyclicHandler(){
        runnable = new Runnable() {
            @Override public void run() {
            time = System.currentTimeMillis()/1000.0 - start;

            // 画面出力
            time_text.setText(String.format("%.3f", time));

            acc_x_text.setText(String.format("%.3f", acc_device[X]));
            acc_y_text.setText(String.format("%.3f", acc_device[Y]));
            acc_z_text.setText(String.format("%.3f", acc_device[Z]));

            vel_x_text.setText(String.format("%.3f", speed[X]));
            vel_y_text.setText(String.format("%.3f", speed[Y]));
            vel_z_text.setText(String.format("%.3f", speed[Z]));

            pos_x_text.setText(String.format("%.3f", diff[X]));
            pos_y_text.setText(String.format("%.3f", diff[Y]));
            pos_z_text.setText(String.format("%.3f", diff[Z]));

            dig_gyro_x_text.setText(Integer.toString( (int)(rad_gyro[X] * RAD2DEG) ));
            dig_gyro_y_text.setText(Integer.toString( (int)(rad_gyro[Y] * RAD2DEG) ));
            dig_gyro_z_text.setText(Integer.toString( (int)(rad_gyro[Z] * RAD2DEG) ));

            dig_mag_x_text.setText(Integer.toString( (int)(rad_mag[X] * RAD2DEG) ));
            dig_mag_y_text.setText(Integer.toString( (int)(rad_mag[Y] * RAD2DEG) ));
            dig_mag_z_text.setText(Integer.toString( (int)(rad_mag[Z] * RAD2DEG) ));

            step_text.setText(String.format("%.3f", step));
            pos_step_x_text.setText(String.format("%.3f", pos_step[X]));
            pos_step_y_text.setText(String.format("%.3f", pos_step[Y]));

            // ファイル出力
//            String text = new String();
//            text += String.format("%7.3f",time);
//            text += String.format(",%7.3f", acc_device[X]);
//            text += String.format(",%7.3f", acc_device[Y]);
//            text += String.format(",%7.3f", acc_device[Z]);
//            text += String.format(",%7.3f", speed[X]);
//            text += String.format(",%7.3f", speed[Y]);
//            text += String.format(",%7.3f", speed[Z]);
//            text += String.format(",%7.3f", diff[X]);
//            text += String.format(",%7.3f", diff[Y]);
//            text += String.format(",%7.3f", diff[Z]);
//            text += String.format(",%7.3f", angularVelocity[X]);
//            text += String.format(",%7.3f", angularVelocity[Y]);
//            text += String.format(",%7.3f", angularVelocity[Z]);
//            text += String.format(",%7.3f", angularVelocity_LPF[X]);
//            text += String.format(",%7.3f", angularVelocity_LPF[Y]);
//            text += String.format(",%7.3f", angularVelocity_LPF[Z]);
//            text += String.format(",%7.3f", rad_gyro[X]);
//            text += String.format(",%7.3f", rad_gyro[Y]);
//            text += String.format(",%7.3f", rad_gyro[Z]);
//            text += String.format(",%7.3f", rad_mag[X]);
//            text += String.format(",%7.3f", rad_mag[Y]);
//            text += String.format(",%7.3f", rad_mag[Z]);
//            text += String.format(",%7.3f", pos_step_gyro[X]);
//            text += String.format(",%7.3f", pos_step_gyro[Y]);
//            text += String.format(",%7.3f", pos_step_mag[X]);
//            text += String.format(",%7.3f", pos_step_mag[Y]);
//            text += String.format(",%7.3f", pos_step[X]);
//            text += String.format(",%7.3f", pos_step[Y]);
//            text += String.format("\n");
//            sampleFileOutput(text, fileName);

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

    // ファイル出力関数
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
