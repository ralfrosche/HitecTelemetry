package de.rosche.spectraTelemetry;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VarioTest extends Activity {
	private static Thread t;
	private int sr = 44100;
	private boolean isRunning = true;
	private boolean writeToTrack = true;
	private SeekBar fSlider;
	private double sliderval = 0.5;
	private TextView climbrate = null;
	private TextView height = null;
	private static Timer myTimer = null;
	private static long delay = 0;
	private double climbrateVal = 0.0;
	private short amp = Short.MAX_VALUE;
	private double climbrateValSpan = 14.0;
	private static AudioTrack audioTrack = null;
	public void playNote(){
		double notes[] = { 400, 450, 500, 600, 700, 800, 850, 900, 950,
				1000, 1050};
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
		setContentView(R.layout.vario_test);
		fSlider = (SeekBar) findViewById(R.id.frequency);
		fSlider.setMax(100);
		fSlider.setProgress(50);
		climbrate = (TextView) findViewById(R.id.airpressure1value);
		height = (TextView) findViewById(R.id.airpressure2value);
		climbrateVal = 	-1*(climbrateValSpan/2) + climbrateValSpan * sliderval;
		climbrate.setText("" + String.format("%.1f", climbrateVal));
		height.setText("" + String.format("%.1f", 0.0));
		delay = getIntent().getIntExtra("delay", 300);

		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser)
					sliderval = progress / (double) seekBar.getMax();
				double delayValue = 100 - sliderval*200;
				setupTimer(delay + (long) delayValue);
				climbrateVal = 	-1*(climbrateValSpan/2) + climbrateValSpan * sliderval;
				climbrate.setText("" + String.format("%.1f", climbrateVal));
				
			}
		};
		
		
		int buffsize = AudioTrack.getMinBufferSize(sr,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, sr,
				AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, buffsize,
				AudioTrack.MODE_STREAM);
		audioTrack.setStereoVolume(0.0f, 0.0f);
		RadioGroup rg = (RadioGroup) findViewById(R.id.volume);
		rg.check(R.id.muteV);
		audioTrack.play();
		myTimer = new Timer();
		while (delay == 0) {
			// nop;
		}
		myTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, delay);

		fSlider.setOnSeekBarChangeListener(listener);
	
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
					TimerMethod();
				}

			}, 0, duration);
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
	private void TimerMethod() {
		playNote();
		this.runOnUiThread(Timer_Tick);
	}
	
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			if (isRunning) {
				double heigthVal = 0;
				double climbrate = -1*(climbrateValSpan/2) + climbrateValSpan * sliderval;
				if (height.getText().length() > 0) {
					try {
						heigthVal = Double.parseDouble(height.getText().toString()
								.replace(',', '.'));
					} catch (Exception e) {
					}
				}
				heigthVal = heigthVal + 1 * climbrate/(1000/300);
				height.setText("" + String.format("%.1f", heigthVal));
			}
		}
	};
	
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		writeToTrack = false;
		isRunning = false;
		audioTrack.release();
		myTimer = null;
		audioTrack = null;
	}

}
