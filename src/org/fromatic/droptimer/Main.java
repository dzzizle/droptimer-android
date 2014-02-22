package org.fromatic.droptimer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener {

    private final int start = 0, stop = 1, refresh = 2;
   
    private float lastZ = 0;
    private Timer timer = new Timer();
    private TextView timerView, scrambleView;

    private SensorManager sensorManager;

    private static float SENSITIVITY = 0.22;

    private SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
          float z = se.values[2];
          if (lastZ != 0) {
        	  if (lastZ - z > SENSITIVITY && timer.isRunning()) {
        		  timerHandler.sendEmptyMessage(stop);
        	  }
          }
          lastZ = z;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
        }
    };

    @Override
    protected void onResume() {
    	super.onResume();
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
    	sensorManager.unregisterListener(sensorListener);
        super.onPause();
   	}
    
    Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case start:
            	if (timer.isRunning())
            		break;
          
            	 try {
         			Thread.sleep(120);
         		} catch (InterruptedException e) 
         		{
         		}
            	 
                timer.start();
                timerHandler.sendEmptyMessage(refresh);
                break;

            case refresh:
                timerView.setText(""+ format(timer.getElapsedTime()));
                timerHandler.sendEmptyMessageDelayed(refresh, 1); 
                break;                                  
            case stop:
                timerHandler.removeMessages(refresh);                
                if (!timer.isRunning())
                	break;
                
                timer.stop();
                scrambleView.setText(Scrambler.scramble());
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerView = (TextView) findViewById(R.id.Time_label);
        scrambleView = (TextView) findViewById(R.id.Scramble);
        scrambleView.setText(Scrambler.scramble());
        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onClick(View view) {
    	
    }
    
    private boolean justStopped = false, isFingerDown = false;
    private long readyTime;
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	isFingerDown = true;
        	if (timer.isRunning()) {
        		timerHandler.sendEmptyMessage(stop);
        		justStopped = true;
        		break;
        	}
        	readyTime = System.currentTimeMillis();
        	timerView.setTextColor(Color.RED);
        	
        	timerHandler.postDelayed(new Runnable()
            {
                public void run() {
                	if (isFingerDown) {
                		timerView.setTextColor(Color.GREEN);
                	}
                }
            }, 1050);
        	
        	justStopped = false;
       		break;
       	case MotionEvent.ACTION_UP:
       		isFingerDown = false;
       		timerView.setTextColor(Color.WHITE);
       		if (!justStopped && System.currentTimeMillis() - readyTime > 1000) {
       			timerHandler.sendEmptyMessage(start);
       			break;
       		}
    	}

        return super.onTouchEvent(event);
    }
    
    private String format(long milliseconds) {
    	String time = String.format("%1$TM:%1$TS:%1$TL", milliseconds);
    	return time.substring(0, time.length() - 1);
    }
}