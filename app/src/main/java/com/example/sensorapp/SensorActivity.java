package com.example.sensorapp;

import static com.example.sensorapp.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private static final String SENSOR_APP_TAG = "SENSOR_APP";
    private final List<Integer> activeSensors = Arrays.asList(Sensor.TYPE_LIGHT, Sensor.TYPE_AMBIENT_TEMPERATURE);
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_sensor_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String string = getString(R.string.sensors_count, sensorList.size());
        getSupportActionBar().setSubtitle(string);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorList.forEach(sensor -> {
            Log.d(SENSOR_APP_TAG, "Sensor name:" + sensor.getName());
            Log.d(SENSOR_APP_TAG, "Sensor vendor:" + sensor.getVendor());
            Log.d(SENSOR_APP_TAG, "Sensor max range:" + sensor.getMaximumRange());
        });

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
    public class SensorHolder extends RecyclerView.ViewHolder {
        private final TextView sensorNameTextView;
        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));
            sensorNameTextView = itemView.findViewById(R.id.sensor_name);
        }
        public void bind(Sensor sensor) {
            View itemContainer = itemView.findViewById(R.id.list_item_sensor);
            if (activeSensors.contains(sensor.getType())) {
                sensorNameTextView.setText(sensor.getName());
                itemContainer.setBackgroundColor(getResources().getColor(R.color.green));
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivity(intent);
                });
            }else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                sensorNameTextView.setText(sensor.getName());
                itemContainer.setBackgroundColor(getResources().getColor(R.color.teal_700));
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivity(intent);
                });
            }
            else{
                itemContainer.setBackgroundColor(getResources().getColor(R.color.red));
                sensorNameTextView.setText(R.string.missing_sensor);
            }
        }
    }
    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private final List<Sensor> mValues;
        public SensorAdapter(List<Sensor> items) {
            mValues = items;
        }
        @Override
        public SensorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflate = LayoutInflater.from(parent.getContext());
            return new SensorHolder(inflate, parent);
        }
        @Override
        public void onBindViewHolder(final SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }
        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}