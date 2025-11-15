package ethanhom.cse_162.sensorprogramming;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gravity;
    private Sensor light;
    private Sensor magnetic;


    @Override
    public final void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);

//        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,gravity,SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public final void onResume() {
//        register a listener for the sensor
        super.onResume();
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onPause() {
//        be sure to unregister the sensor when the activity pauses
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//        do something here if sensor accuracy changes
    }


    @Override
    public final void onSensorChanged(SensorEvent sensorEvent){
        final DecimalFormat df = new DecimalFormat("0.00");
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float xaccel = sensorEvent.values[0];
            float yaccel = sensorEvent.values[1];
            float zaccel = sensorEvent.values[2];

            EditText grav_x = findViewById(R.id.gravValue_x);
            grav_x.setText(df.format(xaccel) + "m/s^2");

            EditText grav_y = findViewById(R.id.gravValue_y);
            grav_y.setText(df.format(yaccel) + "m/s^2");

            EditText grav_z = findViewById(R.id.gravValue_z);
            grav_z.setText(df.format(zaccel) + "m/s^2");
        }

//        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
//            float light = sensorEvent.values[0];
//
//            EditText grav_x = findViewById(R.id.lightvalue);
//            grav_x.setText(df.format(light)+ "lux");
//        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float xmag = sensorEvent.values[0];
            float ymag = sensorEvent.values[1];
            float zmag = sensorEvent.values[2];

            EditText mag_x = findViewById(R.id.magValue_x);
            mag_x.setText(df.format(xmag));

            EditText mag_y = findViewById(R.id.magValue_y);
            mag_y.setText(df.format(ymag));

            EditText mag_z = findViewById(R.id.magValue_z);
            mag_z.setText(df.format(zmag));
        }
    }
}