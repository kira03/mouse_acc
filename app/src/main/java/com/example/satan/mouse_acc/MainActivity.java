package com.example.satan.mouse_acc;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity implements SensorEventListener,View.OnClickListener{
    Activity context;
    String de="debug";
    Button button;
    TextView tv;
    boolean start=true;
    int done=0;
    Socket s;
    
    OutputStream o;
    DataOutputStream dos;
    float xLast, yLast;
    SensorManager sensorManager;
    Sensor sensor;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.txtview);
        button =(Button)findViewById(R.id.button);
        button.setOnClickListener( this);
        context=this;
        // Get SensorManager instance
        new Thread() {
            @Override
            public void run() {

                try

                {
                    Log.i(de, "attempting to connect");
                    tv.setText("attempting to connect");
                    s = new Socket("192.168.1.194", 6969);
                    Log.i(de, "connected");
                    tv.setText("connected...");
                    done=1;

                    o = s.getOutputStream();
                    dos = new DataOutputStream(o);
                    if(done==1)
                    {


                    }

                } catch (
                        Exception e
                        )

                {
                    Log.e(de, e.getMessage());
                }
            }
        }.start();


        sensorManager = (SensorManager) MainActivity.this.getSystemService(SENSOR_SERVICE);
        // Get ACCELEROMETER sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tv.setText("acc");

    }

    protected void onResume(){
        super.onResume();
        // Register sensor

        sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause(){
        super.onPause();
        // Unregister sensor

        sensorManager.unregisterListener(this, sensor);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {

        //Log.e("Main", "GX="+String.valueOf(event.values[0])+"\nGY="+String.valueOf(event.values[1])+"\nGZ="+String.valueOf(event.values[2]));
        float xCurrent=event.values[0]; // Get current x
        float yCurrent=event.values[1]; // Get current y

        if(start){
            // Initialize last x and y
            xLast=xCurrent;
            yLast=yCurrent;
            start=false;
        }
        else{
            // Calculate variation between last x and current x, last y and current y
            float xDelta=xLast-xCurrent;
            float yDelta=yLast-yCurrent;
            try {
                int x,y;

                dos.writeUTF(xCurrent+" "+yDelta);
                tv.setText(""+xDelta+" "+yCurrent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Math.sqrt(xDelta*xDelta/2)>1) {
                //tv.setText("The device is moved horizontally.");

            }

            if(Math.sqrt(yDelta*yDelta/2)>1) //tv.setText("The device is moved vertically.");

            // Update last x and y
            xLast=xCurrent;
            yLast=yCurrent;
            tv.setText(xLast+"   "+yCurrent);
        }


    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button) {

        }
    }
}
