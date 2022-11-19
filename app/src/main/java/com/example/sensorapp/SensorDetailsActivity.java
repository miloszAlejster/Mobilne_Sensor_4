package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_SENSOR_TYPE_PARAMETER = "EXTRA_SENSOR_TYPE";

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorNameTextView;
    private TextView sensorValueTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        // views
        sensorNameTextView = findViewById(R.id.details_sensor_name);
        sensorValueTextView = findViewById(R.id.details_sensor_value);
        // sensor type setup
        int sensorType = getIntent().getIntExtra(EXTRA_SENSOR_TYPE_PARAMETER, Sensor.TYPE_ALL);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        // set init values
        sensorNameTextView.setText(sensor.getName());
        sensorValueTextView.setText(String.valueOf("Default"));
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        sensorValueTextView.setText(String.valueOf(value));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}