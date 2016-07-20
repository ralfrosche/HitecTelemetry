package de.rosche.spectraTelemetry;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class instrument extends Activity implements SwipeInterface {
	   private static RPM 	   mRPM;
	   private static Altimeter	   mAltimeter;
	   private static Volts 	   mVOLTS;
	   private static Amps	   mAMPS;
	   private static FuelGauge mFUEL;
	   private static EngineGauge	mENGINE;
	   Thread myCommsThread = null;
	   private static int showLayout = 1;
	   private static int mRpm = 0;
	   private static int mAlt = 0;
	   private static int mFuel = 0;
	   private static int mEngine = 0;
	   private static double mVolt = 0.0;
	   private static double mAmps = 0.0;
	   protected static final int MSG_ID = 0x1337;
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   requestWindowFeature(Window.FEATURE_NO_TITLE);
		   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 		   
		   setContentView(R.layout.instrument);
		   mRPM		 = (RPM) findViewById(R.id.rpm);
		   mAltimeter		 = (Altimeter) findViewById(R.id.altimeter);
		   
		   myCommsThread = new Thread(new CommsThread());
		   myCommsThread.start();
		   
		   ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
		   LinearLayout swipe_layout = (LinearLayout) findViewById(R.id.swipeLayout);
		   swipe_layout.setOnTouchListener(swipe);
		   


	   }

	   @Override
	   protected void onStop() {
		   super.onStop();
		   if (myCommsThread != null) {
			   myCommsThread.interrupt();
			   myCommsThread = null;
		   }
		   //
		   
	   }
	   private static Handler myUpdateHandler = new Handler() {
		   public void handleMessage(Message msg) {
			   switch (msg.what) {
			   case MSG_ID:			   
				   try {
					   
					   
					   switch (showLayout){
					   case 1:
						   mRPM.setRPM((float)mRpm/100f);
							
						   float hunderter = (float) Math.floor(mAlt/100f);
						   float zehner = (float)Math.floor( (mAlt- (hunderter*100))/10) ;
						   float einer = mAlt -(hunderter*100) - (zehner*10);
											  mAltimeter.setAltimeter(hunderter, 
								   zehner, 
								  einer);
						   break;
					   case 2:
						   mVOLTS.setValue((float)mVolt);
						   mAMPS.setValue((float)mAmps);
						   break;
					   case 3:
						   mENGINE.setValues((float)mEngine,0,0);
						   mFUEL.setValue((float)mFuel);
						   break;
						  
							   
					   }
										  			   
				   } catch (Exception e) {
					   // TODO Auto-generated catch block
					   e.printStackTrace();
				   }
				   break;
			   default:
				   break;
			   }
			   super.handleMessage(msg);
		   }
	   };
	   
	   class CommsThread implements Runnable {
			   public void run() {
			
			   while (!Thread.currentThread().isInterrupted()) {
				   Message m = new Message();
				   m.what = MSG_ID;
				   boolean mustUpdate = false;
				 if (mRpm != SpectraTelemetry.spData.getRPM1()) {
						mRpm = SpectraTelemetry.spData.getRPM1();
						mustUpdate = true;
						
					}
				 if (mAlt != SpectraTelemetry.spData.getAltitude()) {
					 mAlt = SpectraTelemetry.spData.getAltitude();
						mustUpdate = true;
						
					}
			
						 
				 if (mVolt != SpectraTelemetry.spData.getVolt1()) {
					 mVolt = SpectraTelemetry.spData.getVolt1();
						mustUpdate = true;
						
					}
				 if (mAmps != SpectraTelemetry.spData.getAmpC200()) {
					 mAmps = SpectraTelemetry.spData.getAmpC200();
						mustUpdate = true;
						
					}
				 
				 if (mFuel != SpectraTelemetry.spData.getFuel()) {
					 mFuel = SpectraTelemetry.spData.getFuel();
						mustUpdate = true;
						
					}
				 if (mEngine != SpectraTelemetry.spData.getTemp1()) {
					 mEngine = SpectraTelemetry.spData.getTemp1();
						mustUpdate = true;
						
					}
				   if (mustUpdate == true) {
					   myUpdateHandler.sendMessage(m);
				   }
		 
			   }
		   }
	    }
	public void switchView(int switchLayout) {
		if (myCommsThread != null) {
			myCommsThread.interrupt();
			myCommsThread = null;
		}
		
		switch (switchLayout) {
		case 2:
			 setContentView(R.layout.instrument2);
			   mVOLTS		 = (Volts) findViewById(R.id.ivolt);
			   mAMPS		 = (Amps) findViewById(R.id.iamps);
			   ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
			   LinearLayout swipe_layout = (LinearLayout) findViewById(R.id.swipeLayout2);
			   swipe_layout.setOnTouchListener(swipe);
			   break;
		case 1:
			  setContentView(R.layout.instrument);
			   mRPM		 = (RPM) findViewById(R.id.rpm);
			   mAltimeter		 = (Altimeter) findViewById(R.id.altimeter);
		   	   swipe = new ActivitySwipeDetector(this);
			   swipe_layout = (LinearLayout) findViewById(R.id.swipeLayout);
			   swipe_layout.setOnTouchListener(swipe);
			   break;
		case 3:
			  setContentView(R.layout.instrument3);
			  mENGINE	 = (EngineGauge) findViewById(R.id.iengine);
			   mFUEL		 = (FuelGauge) findViewById(R.id.ifuel);
		   	   swipe = new ActivitySwipeDetector(this);
			   swipe_layout = (LinearLayout) findViewById(R.id.swipeLayout3);
			   swipe_layout.setOnTouchListener(swipe);
			   break;
			}
		
		if (myCommsThread == null) {
			
			  myCommsThread = new Thread(new CommsThread());
			  myCommsThread.start();
		   		
		}
	}

	@Override
	public void bottom2top(View v) {
		
	}

	@Override
	public void left2right(View v) {

		if (showLayout <= 3){
			if (showLayout == 3) {
				showLayout = 1;
			} else {
				showLayout++;
			}
			
			switchView(showLayout);
		}
		
	}

	@Override
	public void right2left(View v) {
	
	}

	@Override
	public void top2bottom(View v) {
		
	}
}
