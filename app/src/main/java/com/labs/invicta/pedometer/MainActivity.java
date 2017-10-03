package com.labs.invicta.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener{

    private TextView stepsview,seekbartext;
    private Button register,unregister,reset;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps=0;

    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stepsview=(TextView)findViewById(R.id.stepstextview);
        register=(Button)findViewById(R.id.register);
        unregister=(Button)findViewById(R.id.unregister);
        reset=(Button)findViewById(R.id.reset);

        sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.STEP_THRESHOLD = 12;
        simpleStepDetector.registerListener(this);

        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekbartext=(TextView)findViewById(R.id.seekbartext);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                simpleStepDetector.STEP_THRESHOLD=seekBar.getProgress();
                seekbartext.setText(String.valueOf(simpleStepDetector.STEP_THRESHOLD));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        unregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregister();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void register(){
        if(sensorManager == null) {
            sensorManager = (SensorManager)
                    getSystemService(Context.SENSOR_SERVICE);
        }
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregister(){
        if (sensorManager != null) {
            sensorManager.unregisterListener(MainActivity.this);
        }
    }

    private void reset(){
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        numSteps=0;
        sensorManager =null;
        stepsview.setText("0");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        stepsview.setText(String.valueOf(numSteps));
    }
}
