package de.rosche.spectraTelemetry;



import android.app.Service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class SpeakService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("Hitec Telemetry", "Service:doSpeak();");
		//SpectraTelemetry.doSpeak();
		return Service.START_NOT_STICKY;
	}


	@Override
	public void onCreate() {
		super.onCreate();

		
		


	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}