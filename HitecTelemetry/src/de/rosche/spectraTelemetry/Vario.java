package de.rosche.spectraTelemetry;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Vario extends Activity {
	Thread t;
	Thread d;
	int sr = 44100;
	boolean isRunning = true;
	private TextView climbrate = null;
	private double sliderval = 0.5;
	private TextView height = null;
	private TextView speed = null;
	private boolean metric = true;
	private static Timer myTimer = null;
	private double vair1, vair2, vsP;
	private double climbrateValSpan = 14.0;
	private static long delay = 0;
	private boolean writeToTrack = true;
	private static AudioTrack audioTrack = null;
	private static short amp = Short.MAX_VALUE;
	private DecimalFormat number_format = new DecimalFormat("0.#");

	public void playNote() {
		double notes[] = { 400, 450, 500, 600, 700, 800, 850, 900, 950, 1000,
				1050 };
		int buffsize = 9000;
		short samples[] = new short[buffsize];
		double twopi = 8. * Math.atan(1.);
		double ph = 0.0;
		int note_selector = (int) Math.ceil(sliderval * 10) - 1;
		if (note_selector < 0 ) {
			note_selector = 0;
		}
		if (note_selector > 10 ) {
			note_selector = 10;
		}
		double fr = notes[note_selector];
		int pause = (int) (buffsize * (1 - sliderval));
		for (int i = 0; i < buffsize; i++) {
			if (i > pause && note_selector > 2) {
				samples[i] = 0;
			} else {
				samples[i] = (short) (amp * Math.sin(ph));
				ph += twopi * fr / sr;
			}
		}
		if (writeToTrack) {
			audioTrack.write(samples, 0, buffsize);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vario);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		metric = getIntent().getBooleanExtra("metric", true);
		delay = getIntent().getIntExtra("delay", 300);
		climbrate = (TextView) findViewById(R.id.airpressure1value);
		height = (TextView) findViewById(R.id.airpressure2value);
		speed = (TextView) findViewById(R.id.speedpressurevalue);
		climbrate.setText("" + String.format("%.1f", 0.00));
		height.setText("" + String.format("%.1f", 0.00));
		speed.setText("" + String.format("%.1f", 0.00));
		
		if (metric == false) {
			TextView tv = (TextView) findViewById(R.id.airpressure2unit);
			tv.setText("feet");
			tv = (TextView) findViewById(R.id.airpressure1unit);
			tv.setText("f/s");
			tv = (TextView) findViewById(R.id.speedpressureunit);
			tv.setText("mph");
		}
		
	
		int buffsize = AudioTrack.getMinBufferSize(sr,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, sr,
				AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, buffsize,
				AudioTrack.MODE_STREAM);
		RadioGroup rg = (RadioGroup) findViewById(R.id.volume);
		rg.check(R.id.lowV);
		rg = (RadioGroup) findViewById(R.id.sensitivity);
		rg.check(R.id.medium);
		audioTrack.play();
		myTimer = new Timer();
		while (delay == 0) {
			// nop;
		}
		myTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (SpectraTelemetry.aurora_ready == true && isRunning == true)
					ViewMethod();
			}

		}, 0, delay);
	}
	synchronized void setupTimer(long duration){
		  if(myTimer != null) {
			  myTimer.cancel();
			  myTimer = null;
		  }
		  audioTrack.pause();
		  audioTrack.flush();
		  audioTrack.play();
		  myTimer = new Timer();
			myTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (SpectraTelemetry.aurora_ready == true && isRunning == true)
						ViewMethod();
				}

			}, 0, duration);
		}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			writeToTrack = false;
			isRunning = false;
			audioTrack.pause();
			audioTrack.flush();
			myTimer.cancel();
		}
		return super.onKeyDown(keyCode, event);

	}

	private void ViewMethod() {
		this.runOnUiThread(UpdateView);
		playNote();
	}
	public void onRadioButtonClicked(View view) {
	    boolean checked = ((RadioButton) view).isChecked();
	    switch(view.getId()) {
	        case R.id.low:
	            if (checked)
	            	climbrateValSpan = 14.0;
	            break;
	        case R.id.medium:
	            if (checked)
	            	climbrateValSpan = 10.0;
	            break;
	        case R.id.high:
	            if (checked)
	            	climbrateValSpan = 6.0;
	            break;
	    }
	    Log.e("sens:",""+climbrateValSpan);
	}
	public void onRadioButtonVolumeClicked(View view) {
	    boolean checked = ((RadioButton) view).isChecked();
	    switch(view.getId()) {
	        case R.id.highV:
	            if (checked)
	            	audioTrack.setStereoVolume(1.0f,1.0f);
	            amp = Short.MAX_VALUE;
	            break;
	        case R.id.mediumV:
	            if (checked)
	            	audioTrack.setStereoVolume(0.5f,0.5f);
	            amp = 1000;
	            break;
	        case R.id.lowV:
	            if (checked)
	            	audioTrack.setStereoVolume(0.2f,0.2f);
	            amp = 100;
	            break;
	        case R.id.muteV:
	            if (checked)
	            	audioTrack.setStereoVolume(0.0f,0.0f);
	            break;
	    }
	}
	private Runnable UpdateView = new Runnable() {
		public void run() {
				if (vsP != SpectraTelemetry.spData.getsP()) {
					vsP = SpectraTelemetry.spData.getsP();
					double vsPTemp = vsP;
					if (metric == false) {
						vsPTemp = vsPTemp * 0.621371192;
					}
					speed.setText(number_format.format(vsPTemp));
				}
				if (vair1 != SpectraTelemetry.spData.getCLimbRate()) {
					vair1 = SpectraTelemetry.spData.getCLimbRate();
					double climbrateVal = vair1;
					double vair1Temp = vair1;
					if (metric == false) {
						vair1Temp = vair1Temp * 3.2808399;
					}

					climbrate.setText(number_format.format(vair1Temp));
					if (climbrateVal < -1*(climbrateValSpan/2) ) {
						climbrateVal = -1*climbrateValSpan/2;
					}
					if (climbrateVal > climbrateValSpan/2) {
						climbrateVal = climbrateValSpan/2;
					}
					sliderval = climbrateVal / climbrateValSpan + 0.5;
					double delayValue = 100 - sliderval*200;
					setupTimer(delay+ (long) delayValue);
				}
				if (vair2 != SpectraTelemetry.spData.getHeight()) {
					vair2 = SpectraTelemetry.spData.getHeight();
					double vair2Temp = vair2;
					if (metric == false) {
						vair2Temp = vair2Temp * 3.2808399;
					}
					height.setText(number_format.format(vair2Temp));
				}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		writeToTrack = false;
		isRunning = false;
		audioTrack.release();
		myTimer = null;
		audioTrack = null;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

}
