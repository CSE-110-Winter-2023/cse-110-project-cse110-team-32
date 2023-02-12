package com.example.team_32;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService implements SensorEventListener {
    private static OrientationService instance;

    private final SensorManager sensorManager;
    private float[] accMeterReading;
    private float[] magMeterReading;

    //Angle between the app and the north
    // values between (-pi, pi), when 0 -> points to north pole
    private MutableLiveData<Float> azimuth;

    /**
     * Constructor
     * @param activity Context is needed to initiate the sensor manager
     */
    protected OrientationService(Activity activity) {
        this.azimuth = new MutableLiveData<>();
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        this.regSensorListeners();
    }

    public void regSensorListeners() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

    }

    public static OrientationService singleton (Activity activity){
        if (instance == null)
            instance = new OrientationService(activity);
        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accMeterReading = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magMeterReading = event.values;
        }
        if (magMeterReading != null && accMeterReading !=null){
            sensorDataOnBoth();
        }
    }

    private void sensorDataOnBoth() {
        if (accMeterReading == null || magMeterReading == null){
            throw new IllegalStateException("Data for both sensors must be available ");
        }
        float[] rotMatrix =  new float[9];
        float[] incMatrix =  new float[9];

        boolean success = SensorManager.getRotationMatrix(rotMatrix, incMatrix, accMeterReading, magMeterReading);

        if (success){
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotMatrix, orientation);

            this.azimuth.postValue(orientation[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    public void unregSensors(){
        sensorManager.unregisterListener(this);
    }

    public LiveData<Float> getOrientation(){return this.azimuth;}

    public void setMockOrientation(MutableLiveData<Float> mockSource){
        unregSensors();
        this.azimuth = mockSource;
    }
}