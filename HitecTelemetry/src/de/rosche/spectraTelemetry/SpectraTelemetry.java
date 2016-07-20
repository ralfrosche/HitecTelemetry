package de.rosche.spectraTelemetry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import de.rosche.spectraTelemetry.R;
import android.media.AudioManager;
import android.media.MediaPlayer;

public final class SpectraTelemetry extends Activity implements TextToSpeech.OnInitListener {
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private PendingIntent pendingIntent;
	private static final int REQUEST_PREFS = 3;
	private static final int REQUEST_ENABLE_BT = 2;
	public static boolean initapp = true;
	public static boolean initialized = false;
	public static boolean changed_prefs = false;
	private Timer myTimer;
	private String mConnectedDeviceName = null;
	public static final boolean DEBUG = true;
	public static final boolean LOG_CHARACTERS_FLAG = DEBUG && false;
	public static final boolean LOG_UNKNOWN_ESCAPE_SEQUENCES = DEBUG && false;
	public static final String LOG_TAG = "Hitec2Android";
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final Callback Runnable = null;
	public static boolean updatePref = false;
	private BluetoothAdapter mBluetoothAdapter = null;
	private SpectraView worker;
	private Thread workthread;
	public static SpectraData spData;
	private double LastValidLatitude = 0.0;
	private int LastValidDistance = 0;
	private double LastValidCourse = 0.0;
	private double LastValidLongitude = 0.0;
	private double voltageMin = 0.0;
	private double voltageMax = 0.0;
	private double heightMin = 0.0;
	private double heightMax = 0.0;
	private double currentMin = 0.0;
	private double currentMax = 0.0;
	private double powerMin = 0.0;
	private double powerMax = 0.0;
	private static BluetoothSerialService mSerialService = null;
	private static DeviceListActivity mDeviceListActivity = null;
	private static int profile = 0;
	private static final int MY_DATA_CHECK_CODE = 5;
	private static String alert_tone = "";
	private static String bluetooth_device = "HITECAURORA";
	private static String current_sensor_type = "C200";
	private static String vario_delay = "300";
	private static String current_speech_label = "Strom";
	private static int current_show_type = 1;
	private static int power_show_type = 1;
	private static int height_show_type = 1;
	private static int voltage_show_type = 1;
	private static String current_speech_unit = "Ampär";
	private static String height_speech_label = "Höhe";
	private static String height_speech_unit = "Meter";
	private static String label_rpm1 = "Rpm1:";
	private static String label_rpm2 = "Rpm2:";
	private static String label_temp1 = "Temp1:";
	private static String label_temp2 = "Temp2:";
	private static boolean metric = true;
	private static String log_file = "spectra_live.log";
	private static String rpm_speech_label = "Drehzahl";
	private static String rpm_speech_unit = "Umdrehungen";
	private static String speech_intervall = "10000";
	private static String speech_alert_intervall = "10000";
	private static String speed_speech_label = "Geschwindigkeit";
	private static String speed_speech_unit = "Kilometer";
	private static String voltage_speech_alert = "Achtung Spannung zu niedrig";
	private static String height_speech_alert = "Achtung höhe";
	private static String current_speech_alert = "Achtung Strom zu hoch";
	private static String used_speech_alert = "Achtung Verbrauch überschritten";
	private static String temp_speech_alert = "Achtung Temperatur zu hoch";
	private static String rpm_speech_alert = "Achtung Drehzahl zu hoch";
	private static String speed_speech_alert = "tung Geschwindigkeit";
	private static String temp_speech_label = "Temperatur";
	private static String temp_speech_unit = "Grad";
	private static String unit_rpm1 = "U/min";
	private static String unit_rpm2 = "U/min";
	private static String unit_temp1 = "°C";
	private static String unit_temp2 = "°C";
	private static String used_speech_label = "Verbrauch";
	private static String used_speech_unit = "Milli Ampär";
	private static String voltage_speech_label = "Spannung";
	private static String voltage_speech_unit = "Wolt";
	private static TextToSpeech tts;
	private static Uri uri;
	private static boolean auto_connect = false;
	private static boolean mpxData = false;
	private static boolean speak_altitude = false;
	private static boolean speak_current = false;
	private static boolean speak_fuel = false;
	private static boolean speak_power = false;
	private static boolean speak_rpm = false;
	private static boolean speak_speed = false;
	private static boolean speak_temperature = false;
	private static boolean speak_used = false;
	private static boolean speak_voltage = false;
	private static boolean alarm_altitude = true;
	private static boolean displayAlwaysOn = false;
	private static boolean alarm_current = true;
	private static boolean alarm_fuel = true;
	private static boolean alarm_power = true;
	private static boolean alarm_rpm = true;
	private static boolean alarm_speed = true;
	private static boolean alarm_temperature = true;
	private static boolean alarm_used = true;
	private static boolean alarm_voltage = true;
	private static boolean current_speech_ack = false;
	private static boolean height_speech_ack = false;
	private static boolean mAlarm_on = true;
	private static boolean mSpeak_on = false;
	private static boolean mEnablingBT;
	private static boolean mSound_on = false;
	private static boolean mlog_file = false;
	private static boolean rpm_speech_ack = false;
	private static boolean speech_on = false;
	private static boolean speed_speech_ack = false;
	private static boolean temp_speech_ack = false;
	private static boolean used_speech_ack = false;
	private static boolean vibrate_on = false;
	private static boolean voltage_speech_ack = false;
	private static boolean writelog = false;
	private static double mCurrentAlarm = 100;
	private static double mPowerAlarm = 1000;
	private static int current_hysteresis = 10;
	private static int mAltitudeAlarm = 300;
	private static int mFuelAlarm = 0;
	private static int mRpmAlarm = 10000;
	private static int mSpeedAlarm = 300;
	private static int mTemperatureAlarm = 100;
	private static int power_hysteresis = 10;
	private static int rpm_hysteresis = 10;
	private static int speech_volume = 100;
	private static int temperature_hysteresis = 10;
	private static int voltage_hysteresis = 10;
	public static boolean aurora_ready = false;
	public static double mUsedCap = 3000;
	public static double mVoltageAlarm = 0;
	public static final int WHITE = 0xffffffff;
	public static final int BLACK = 0xff000000;
	public static final int BLUE = 0xff344ebd;
	public SharedPreferences mPrefs;
	private MenuItem mMenuItemConnect;
	private MenuItem mMenuItemLog;
	private static final int STOPSPLASH = 0;
	private static final long SPLASHTIME = 2500;
	private DatabaseHelper myDbHelper = new DatabaseHelper(this);
	private static TextView voltage;
	private static TextView  receiverVoltage;
	private static TextView current;
	private static TextView speed;
	private static TextView alt;
	private static TextView temp1;
	private static TextView temp2;
	private static TextView temp5;
	private static TextView speedpressure;
	private static TextView power;
	private static TextView fuel;
	private static TextView rpm1;
	private static TextView rpm2;
	private static TextView tempLabel;
	private static TextView temp2Label;
	private static TextView rpm1Label;
	private static TextView rpm2Label;
	private static TextView temp1_unit;
	private static TextView temp2_unit;
	private static TextView rpm1_unit;
	private static TextView rpm2_unit;
	private static TextView servo1;
	private static TextView servo2;
	private static TextView servo3;
	private static TextView servo4;
	private static TextView used;
	private static TextView air1;
	private static TextView air2;
	private static TextView lqi;
	private static TextView txstate;
	private static TextView gpsCourse;
	private static TextView distance;
	private static TextView currentType;
	private static TextView voltageType;
	private static TextView heightType;
	private static TextView powerType;
	private static String message;
	private static double ampC2002 = 0.0f;
	private static double ampC502 = 0.0f;
	private static MediaPlayer mp;
	private Handler handler;
	private static ArrayList<ArrayList<String>> profiles = new ArrayList<ArrayList<String>>();
	@SuppressLint("HandlerLeak")
	Handler splashHandler = new Handler() {
		private TableRow tbrow;
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				setContentView(R.layout.main);
			    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				InitAPPState();
				spData = new SpectraData();
				mSerialService = new BluetoothSerialService(getBaseContext(),
						mHandlerBT, spData, mlog_file, mpxData);
				handler = new Handler();
				if (mp != null) {
					mp.start();
					mp.release();
					mp = null;
				}
				if (uri != null && !uri.toString().equals("res/raw/alarm.mp3")) {
					mp = MediaPlayer.create(getApplicationContext(), uri);
				} else {
					mp = MediaPlayer.create(getApplicationContext(),
							R.raw.alarm);
				}
				int sb2value;
				AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				sb2value = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				sb2value = sb2value * speech_volume / 100;
				am.setStreamVolume(AudioManager.STREAM_MUSIC, sb2value, 0);
				mDeviceListActivity = new DeviceListActivity();
				
				mDeviceListActivity.test(bluetooth_device);
				message = DeviceListActivity.DEFAULT_DEVICE_ADDRESS;
				Log.e("bluetooth_device_main:",""+bluetooth_device);
				if (auto_connect == true) {
					if (!message.equals("")
							&& !message.equals("device_address")) {
						BluetoothDevice device = mBluetoothAdapter
								.getRemoteDevice(message);
						mSerialService.connect(device);
						if (getConnectionState() == BluetoothSerialService.STATE_CONNECTED) {
							aurora_ready = true;
						}
					}
				}
				ampC2002 = 0.0f;
				ampC502 = 0.0f;
				myTimer = new Timer();
				myTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						TimerMethod();
					}

				}, 0, 1000);
				voltage = (TextView) findViewById(R.id.voltsValue);
				receiverVoltage = (TextView) findViewById(R.id.receiverVoltageValue);
				temp1 = (TextView) findViewById(R.id.tempValue);
				temp2 = (TextView) findViewById(R.id.temp2Value);
				temp5 = (TextView) findViewById(R.id.temp5value);
				speedpressure = (TextView) findViewById(R.id.speedpressurevalue);
				current = (TextView) findViewById(R.id.currentValue);
				speed = (TextView) findViewById(R.id.speedValue);
				alt = (TextView) findViewById(R.id.altValue);
				power = (TextView) findViewById(R.id.powerValue);
				powerType = (TextView) findViewById(R.id.powerType);
				currentType = (TextView) findViewById(R.id.currentType);
				voltageType = (TextView) findViewById(R.id.voltageType);
				heightType = (TextView) findViewById(R.id.heightType);
				fuel = (TextView) findViewById(R.id.fuelValue);
				rpm1 = (TextView) findViewById(R.id.rpm1Value);
				rpm2 = (TextView) findViewById(R.id.rpm2Value);
				servo1 = (TextView) findViewById(R.id.servo1value);
				servo2 = (TextView) findViewById(R.id.servo2value);
				servo3 = (TextView) findViewById(R.id.servo3value);
				servo4 = (TextView) findViewById(R.id.servo4value);
				distance = (TextView) findViewById(R.id.distanceValue);
				gpsCourse = (TextView) findViewById(R.id.gpsCourseValue);
				used = (TextView) findViewById(R.id.usedvalue);
				air1 = (TextView) findViewById(R.id.airpressure1value);
				air2 = (TextView) findViewById(R.id.airpressure2value);
				rpm1Label = (TextView) findViewById(R.id.rpm1Label);
				rpm2Label = (TextView) findViewById(R.id.rpm2Label);
				tempLabel = (TextView) findViewById(R.id.tempLabel);
				temp2Label = (TextView) findViewById(R.id.temp2Label);
				rpm1Label.setText(label_rpm1);
				rpm2Label.setText(label_rpm2);
				tempLabel.setText(label_temp1);
				temp2Label.setText(label_temp2);
				lqi = (TextView) findViewById(R.id.lqiValue);
				txstate = (TextView) findViewById(R.id.txStateValue);
				rpm1_unit = (TextView) findViewById(R.id.rpm1unit);
				rpm2_unit = (TextView) findViewById(R.id.rpm2unit);
				temp1_unit = (TextView) findViewById(R.id.tempunit);
				temp2_unit = (TextView) findViewById(R.id.temp2unit);
				rpm1_unit.setText(unit_rpm1);
				rpm2_unit.setText(unit_rpm2);
				temp1_unit.setText(unit_temp1);
				temp2_unit.setText(unit_temp2);
				
				ArrayList<ArrayList<String>> sensorData= new ArrayList<ArrayList<String>>();
				if (profile == 0) {
					/*Toast.makeText(getBaseContext(), "Sensorsettings default",
							Toast.LENGTH_LONG).show();*/
				} else {
					sensorData = myDbHelper.getSensorData(profile);
				}
				
				if (profile != 0) {
					int length = sensorData.size();
					Map<String, String> SensorArray = new HashMap<String, String>();
					for (int i = 0; i < length; i++) {
						ArrayList<String> data = new ArrayList<String>();
						data = sensorData.get(i);
						String name = data.get(0).toString();
						String visible = data.get(1).toString();
						SensorArray.put(name, visible);
					}
					if (SensorArray.size() == 0 ){
						String visible = "1";
						SensorArray.put("volts",visible);
						SensorArray.put("amps",visible);
						SensorArray.put("Used",visible);
						SensorArray.put("receiverVoltage",visible);
						SensorArray.put("power",visible);
						SensorArray.put("speed",visible);
						SensorArray.put("altitude",visible);
						SensorArray.put("rpm1",visible);
						SensorArray.put("temp1",visible);
						SensorArray.put("temp2",visible);
						SensorArray.put("rpm2",visible);
						SensorArray.put("fuel",visible);
						SensorArray.put("Servo01",visible);
						SensorArray.put("Servo02",visible);
						SensorArray.put("Servo03",visible);
						SensorArray.put("Servo04",visible);
						SensorArray.put("temp5",visible);
						SensorArray.put("airpressure1",visible);
						SensorArray.put("airpressure2",visible);
						SensorArray.put("speedpressure",visible);
						SensorArray.put("lqi",visible);
						SensorArray.put("txState",visible);
						SensorArray.put("distance",visible);
						SensorArray.put("gpsCourse",visible);
							
					}

					TableLayout table = (TableLayout) findViewById(R.id.sensorTable);  
					if (Integer.parseInt(SensorArray.get("amps"))== 0) {
						tbrow = (TableRow) findViewById(R.id.amps);
						table.removeView(tbrow);
						current = null;
					}
					if (Integer.parseInt(SensorArray.get("volts"))== 0) {
						tbrow = (TableRow) findViewById(R.id.volts);
						table.removeView(tbrow);
						voltage = null;
					}
					if (Integer.parseInt(SensorArray.get("Used"))== 0) {
						tbrow = (TableRow) findViewById(R.id.Used);
						table.removeView(tbrow);
						used = null;
					}
					
					if (Integer.parseInt(SensorArray.get("lqi"))== 0) {
						tbrow = (TableRow) findViewById(R.id.lqi);
						table.removeView(tbrow);
						lqi = null;
					}
				
					if (Integer.parseInt(SensorArray.get("txState"))== 0) {
						tbrow = (TableRow) findViewById(R.id.txState);
						table.removeView(tbrow);
						txstate = null;
					}
					
					if (Integer.parseInt(SensorArray.get("gpsCourse"))== 0) {
						tbrow = (TableRow) findViewById(R.id.gpsCourse);
						table.removeView(tbrow);
						gpsCourse = null;
						
					}
					
					if (Integer.parseInt(SensorArray.get("distance"))== 0) {
						tbrow = (TableRow) findViewById(R.id.distance);
						table.removeView(tbrow);
						distance = null;
					}
					
					if (Integer.parseInt(SensorArray.get("receiverVoltage"))== 0) {
						tbrow = (TableRow) findViewById(R.id.receiverVoltage);
						table.removeView(tbrow);
						receiverVoltage = null;
					}
					
					
					if (Integer.parseInt(SensorArray.get("power"))== 0) {
						tbrow = (TableRow) findViewById(R.id.power);
						table.removeView(tbrow);
						power = null;
				
					}
					
					if (Integer.parseInt(SensorArray.get("speed"))== 0) {
						tbrow = (TableRow) findViewById(R.id.speed);
						table.removeView(tbrow);
						speed = null;
					}
					
					if (Integer.parseInt(SensorArray.get("altitude"))== 0) {
						tbrow = (TableRow) findViewById(R.id.altitude);
						table.removeView(tbrow);
						alt = null;
					}
				
					if (Integer.parseInt(SensorArray.get("rpm1"))== 0) {
						tbrow = (TableRow) findViewById(R.id.rpm1);
						table.removeView(tbrow);
						rpm1 = null;
					}
				
					if (Integer.parseInt(SensorArray.get("rpm2"))== 0) {
						tbrow = (TableRow) findViewById(R.id.rpm2);
						table.removeView(tbrow);
						rpm2 = null;
					}
					if (Integer.parseInt(SensorArray.get("temp1"))== 0) {
						tbrow = (TableRow) findViewById(R.id.temp1);
						table.removeView(tbrow);
						temp1 = null;
					}
					if (Integer.parseInt(SensorArray.get("temp2"))== 0) {
						tbrow = (TableRow) findViewById(R.id.temp2);
						table.removeView(tbrow);
						temp2 = null;
					}
					if (Integer.parseInt(SensorArray.get("temp5"))== 0) {
						tbrow = (TableRow) findViewById(R.id.temp5);
						table.removeView(tbrow);
						temp5 = null;
					}
					if (Integer.parseInt(SensorArray.get("Servo01"))== 0) {
						tbrow = (TableRow) findViewById(R.id.Servo01);
						table.removeView(tbrow);
						servo1 = null;
					}
					
					if (Integer.parseInt(SensorArray.get("Servo02"))== 0) {
						tbrow = (TableRow) findViewById(R.id.Servo02);
						table.removeView(tbrow);
						servo2 = null;
					}
					if (Integer.parseInt(SensorArray.get("Servo03"))== 0) {
						tbrow = (TableRow) findViewById(R.id.Servo03);
						table.removeView(tbrow);
						servo3 = null;
					}
				
					if (Integer.parseInt(SensorArray.get("Servo04"))== 0) {
						tbrow = (TableRow) findViewById(R.id.Servo04);
						table.removeView(tbrow);
						servo4 = null;
					}
				
					if (Integer.parseInt(SensorArray.get("fuel"))== 0) {
						tbrow = (TableRow) findViewById(R.id.fuel);
						table.removeView(tbrow);
						fuel = null;
					}
					
					if (Integer.parseInt(SensorArray.get("airpressure1"))== 0) {
						tbrow = (TableRow) findViewById(R.id.airpressure1);
						table.removeView(tbrow);
						air1 = null;
					}
					
					if (Integer.parseInt(SensorArray.get("airpressure2"))== 0) {
						tbrow = (TableRow) findViewById(R.id.airpressure2);
						table.removeView(tbrow);
						air2 = null;
					}
					
					if (Integer.parseInt(SensorArray.get("speedpressure"))== 0) {
						tbrow = (TableRow) findViewById(R.id.speedpressure);
						table.removeView(tbrow);
						speedpressure = null;
					}
					SharedPreferences settings = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					SharedPreferences.Editor editor = settings.edit();
					for (int i = 0; i < length; i++) {
						ArrayList<String> data = new ArrayList<String>();
						data = sensorData.get(i);
						String name = data.get(0).toString();
						String visible = data.get(1).toString();
						if (name =="speed" || name =="altitude" || name =="power" || name =="fuel"){
							name +="+";
						}
						if (Integer.parseInt(visible) == 1) {
								editor.putBoolean(name, true);
						} else {
							
							editor.putBoolean(name, false);
						}
					}
					editor.commit();
				
				}
				TableLayout table = (TableLayout) findViewById(R.id.sensorTable);  
				for(int i = 0; i < table.getChildCount(); i++)
				    {
						if (i%4 == 0){
					        TableRow row = (TableRow) table.getChildAt(i); 
					        row.setBackgroundResource(R.drawable.row_border_top);
						}
				    }
				
				worker = new SpectraView();
				workthread = new Thread(worker);
				worker.setPause(true);
				worker.getFile(false);
				workthread.start();
				
				if (voltage != null) {
					attachOnTouchVoltage(voltage);
					attachOnLongTouchVoltage(voltage);
				}
				if (used != null) {
					attachOnTouchResetUsed(used);
				}
				if (air1 != null) {
					attachOnTouchResetClimbrate(air1);
					attachOnLongTouchHeight(air1);
				}
				if (air2 != null) {
					attachOnTouchResetHeight(air2);
					attachOnLongTouchHeight(air2);
				}

				if (power != null) {
					attachOnLongTouchPower(power);
					attachOnTouchResetPower(power);
				}

				if (current != null) {
					attachOnTouchResetCurrent(current);
					attachOnLongTouchCurrent(current);
				}
				
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void attachOnTouchResetUsed(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					used.setText("0");
					if (worker != null) {
						worker.resetUsedData();
					}
				}
				return false;
			}
		});

	}

	public void attachOnTouchResetCurrent(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					String typeText = "";

					current_show_type += 1;
					if (current_show_type > 3) {
						current_show_type = 1;
					}
					switch (current_show_type) {
					case 1:
						typeText = "live";
						break;
					case 2:
						typeText = "min";
						break;
					case 3:
						typeText = "max";
						break;
					}
					currentType.setText(typeText);
				}
				return false;
			}
		});
	}
	
	public void attachOnTouchResetPower(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					String typeText = "";

					power_show_type += 1;
					if (power_show_type > 3) {
						power_show_type = 1;
					}
					switch (power_show_type) {
					case 1:
						typeText = "live";
						break;
					case 2:
						typeText = "min";
						break;
					case 3:
						typeText = "max";
						break;
					}
					powerType.setText(typeText);
				}
				return false;
			}
		});
	}

	public void attachOnTouchResetClimbrate(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					view.setText("0.0");
					if (worker != null) {
						worker.resetClimbRateData();
					}
				}
				return false;
			}
		});
	}

	public void attachOnTouchResetHeight(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					String typeText = "";
					height_show_type += 1;
					if (height_show_type > 3) {
						height_show_type = 1;
					}
					switch (height_show_type) {
					case 1:
						typeText = "live";
						break;
					case 2:
						typeText = "min";
						break;
					case 3:
						typeText = "max";
						break;
					}
					heightType.setText(typeText);
				}
				return false;
			}
		});
	}

	public void attachOnTouchVoltage(final TextView view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					String typeText = "";
					voltage_show_type += 1;
					if (voltage_show_type > 3) {
						voltage_show_type = 1;
					}
					switch (voltage_show_type) {
					case 1:
						typeText = "live";
						break;
					case 2:
						typeText = "min";
						break;
					case 3:
						typeText = "max";
						break;
					}
					voltageType.setText(typeText);
				}
				return false;
			}
		});
	}

	public void attachOnLongTouchVoltage(final TextView view) {
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				view.setText("0.0");
				voltageMin = 0.0;
				voltageMax = 0.0;
				voltage_show_type = 1;
				voltageType.setText("live");
				if (tts != null) {
					tts.stop();

				}
				if (mp != null) {
					if (mp.isPlaying() == true) {
						mp.pause();
					}
				}
				mSound_on = !mSound_on;
				if (vibrate_on == true)
					vibrate_on = false;
				if (mSound_on != false) {
					worker.resetHysteresis();
				}
				if (mSound_on) {
					Toast.makeText(getBaseContext(), "sound on",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "sound off",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}

	public void attachOnLongTouchHeight(final TextView view) {
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				heightMin = 0.0;
				heightMax = 0.0;
				air1.setText("0.0");
				air2.setText("0.0");
				height_show_type = 1;
				heightType.setText("live");
				if (worker != null) {
					worker.resetClimbRateData();
				}
				return false;
			}
		});
	}

	public void attachOnLongTouchPower(final TextView view) {
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				view.setText("0.0");
				powerMin = 0.0;
				powerMax = 0.0;
				power_show_type = 1;
				powerType.setText("live");
				return false;
			}
		});
	}

	public void attachOnLongTouchCurrent(final TextView view) {
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				view.setText("0.0");
				currentMin = 0.0;
				currentMax = 0.0;
				current_show_type = 1;
				currentType.setText("live");
				return false;
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initapp = true;
		//sandbox();
		initialized = false;
		if (android.os.Build.VERSION.SDK_INT > 10) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
			//getOverflowMenu();
		}
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			finishDialogNoBluetooth();
			return;
		}
		setContentView(R.layout.term_activity);
		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}
	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void InitAPPState() {
		String FILENAME = "HitecTelemetry_sav";
		byte[] bytes = new byte[] { 32, 32, 32, 32, 32, 32, 32 };
		String str = "";
		FileInputStream fis = null;
		try {
			fis = openFileInput(FILENAME);
			try {
				fis.read(bytes, 0, 7);
				str = new String(bytes);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!str.equals("") && profile == 0) {
			profile = Integer.parseInt(str.trim());
		}
		try {
			myDbHelper.createDataBase();
			boolean upgrade = myDbHelper.checkDatabaseVersion();
			if (upgrade == true) {
				myDbHelper.upgradeDatabaseVersion(getBaseContext());
				Toast.makeText(
						getBaseContext(),
						"Database  patched to version: "
								+ DatabaseHelper.PROGRAMM_VERSION,
						Toast.LENGTH_LONG).show();
			} else {
				/*Toast.makeText(getBaseContext(),
						"Database has current version", Toast.LENGTH_LONG)
						.show();*/
			}
			if (profile == 0) {
				Toast.makeText(getBaseContext(), "Profile is default",
						Toast.LENGTH_LONG).show();
			} else {
				Log.e("profile","profile:"+profile);
				String profile_txt = myDbHelper.getProfileById(profile);
				Toast.makeText(getBaseContext(), "Profile is " + profile_txt,
						Toast.LENGTH_LONG).show();
			}
			initialized = true;
			profiles = myDbHelper.getProfiles();

		} catch (IOException e) {
			e.printStackTrace();
		}
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		readMinPrefs();
		if (displayAlwaysOn == true) {
			
		}
		if (metric == false) {
			TextView tv = (TextView) findViewById(R.id.speedunit);
			tv.setText("mph");
			tv = (TextView) findViewById(R.id.tempunit);
			tv.setText("°F");
			tv = (TextView) findViewById(R.id.temp2unit);
			tv.setText("°F");
			tv = (TextView) findViewById(R.id.temp5unit);
			tv.setText("°F");
			tv = (TextView) findViewById(R.id.airpressure2unit);
			tv.setText("feet");
			tv = (TextView) findViewById(R.id.airpressure1unit);
			tv.setText("f/s");
			tv = (TextView) findViewById(R.id.altunit);
			tv.setText("feet");
			tv = (TextView) findViewById(R.id.speedpressureunit);
			tv.setText("mph");
		}
		if (profile == 0) {
			readPrefs();
		} else {
			loadDefaultProfile(profile);
		}
	}

	private void TimerMethod() {
		this.runOnUiThread(CalculateUsed);
	}

	private Runnable CalculateUsed = new Runnable() {
		@Override
		public void run() {
			double amp200 = 0.0f;
			double amp50 = 0.0f;
			double ampDiff = 0.0f;

			if (mSerialService.getState() == 3) {
				amp200 = spData.getAmpC200() * 1000;
				amp50 = spData.getAmpC50() * 1000;
				ampDiff = (amp200 + ampC2002) / 2;
				ampDiff = ampDiff / 3600;
				spData.addUsedC200(ampDiff);
				ampC2002 = amp200;
				ampDiff = (amp50 + ampC502) / 2;
				ampDiff = ampDiff / 3600;
				spData.addUsedC50(ampDiff);
				ampC502 = amp50;
			}

		}
	};

	@Override
	public void onStart() {
		super.onStart();
		Intent checkIntent = new Intent();
		if (tts == null) {
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		}
			profiles = myDbHelper.getProfiles();
		if (worker != null) {
			worker.setPause(false);
		}
		mEnablingBT = false;
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (!mEnablingBT) {
			if ((mBluetoothAdapter != null) && (!mBluetoothAdapter.isEnabled())) {
				mEnablingBT = true;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.alert_dialog_turn_on_bt)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.alert_dialog_warning_title)
						.setCancelable(false)
						.setPositiveButton(R.string.alert_dialog_yes,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										mEnablingBT = true;
										dialog.dismiss();
										Intent enableIntent = new Intent(
												BluetoothAdapter.ACTION_REQUEST_ENABLE);
										startActivityForResult(enableIntent,
												REQUEST_ENABLE_BT);
									}
								})
						.setNegativeButton(R.string.alert_dialog_no,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										finishDialogNoBluetooth();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}

			if (mSerialService != null) {
				if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
					mSerialService.start();
				}
			}
			String tmp_logfile = log_file;
			boolean append_state = false;
			if (mBluetoothAdapter != null) {
				if (initapp == false) {
					if (profile == 0) {
						if (initialized == true) {
							readPrefs();
						}
					} else {
						if (initialized == true) {
							readPrefs();
						}
					}
				}
			}
			if (log_file.equals(tmp_logfile)) {
				append_state = true;
			}
			if (worker != null && writelog == true) {
				worker.getFile(append_state);
				worker.setPause(false);
			}
			if (changed_prefs == true) {
				changed_prefs = false;
				readPrefs();
			}
		}
	}
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
  	    super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onStop() {
		super.onStop();
		Log.e("Hitec telemetry", "onStop");
		if (worker != null) {
			worker.setPause(true);
		}
	}

	@Override
	public void onDestroy() {
		stopService();
		Log.e("Hitec telemetry", "onDestroy");
		super.onDestroy();
	
		if (workthread != null) {
			workthread.interrupt();
			workthread = null;
		}
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
			worker.copyFile();
			worker = null;
		}
		if (handler != null) {

			handler = null;
		}
		if (mp != null) {
			if (mp.isPlaying() == true) {
				mp.stop();
				mp.release();
				mp = null;
			}
		}
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		if (mSerialService != null)
			mSerialService.stop();
		String FILENAME = "HitecTelemetry_sav";
		String string = String.valueOf(profile);
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fos.write(string.getBytes());
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DatabaseHelper myDbHelper = new DatabaseHelper(this);
		myDbHelper.close();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private double getDouble(String value, double dafaultvalue) {
		String valuePref = "";
		valuePref = mPrefs.getString(value, String.valueOf(dafaultvalue));
		if (valuePref != "") {
			try {
				return Double.parseDouble(valuePref);
			} catch (NumberFormatException e) {
				return dafaultvalue;
			}
		} else {
			return dafaultvalue;
		}
	}

	private double getDouble(double dafaultvalue, String value) {
		if (value != "") {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return dafaultvalue;
			}
		} else {
			return dafaultvalue;
		}

	}

	private int getInteger(String value, int dafaultvalue) {
		String valuePref = "";

		valuePref = mPrefs.getString(value, Integer.toString(dafaultvalue));
		if (valuePref != "") {
			try {
				return Integer.parseInt(valuePref);
			} catch (NumberFormatException e) {
				return dafaultvalue;
			}
		} else {
			return dafaultvalue;
		}

	}

	private int getInteger(int dafaultvalue, String value) {
		if (value != "") {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return dafaultvalue;
			}
		} else {
			return dafaultvalue;
		}

	}

	private boolean getBoolean(boolean dafaultvalue, String value) {
		if (value != "") {
			try {
				return Boolean.parseBoolean(value);
			} catch (NumberFormatException e) {
				return dafaultvalue;
			}
		} else {
			return dafaultvalue;
		}
	}

	public ArrayList<ArrayList<String>> getprefs() {
		ArrayList<ArrayList<String>> prefs = new ArrayList<ArrayList<String>>();
		ArrayList<String> pref = new ArrayList<String>();
		pref.add("mVoltageAlarm");
		pref.add(String.valueOf(mVoltageAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mUsedCap");
		pref.add(String.valueOf(mUsedCap));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mCurrentAlarm");
		pref.add(String.valueOf(mCurrentAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mPowerAlarm");
		pref.add(String.valueOf(mPowerAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mSpeedAlarm");
		pref.add(String.valueOf(mSpeedAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mFuelAlarm");
		pref.add(String.valueOf(mFuelAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("voltage_hysteresis");
		pref.add(String.valueOf(voltage_hysteresis));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("current_hysteresis");
		pref.add(String.valueOf(current_hysteresis));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("rpm_hysteresis");
		pref.add(String.valueOf(rpm_hysteresis));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("temperature_hysteresis");
		pref.add(String.valueOf(temperature_hysteresis));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mTemperatureAlarm");
		pref.add(String.valueOf(mTemperatureAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speech_volume");
		pref.add(String.valueOf(speech_volume));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("vibrate_on");
		pref.add(String.valueOf(vibrate_on));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("voltage_speech_ack");
		pref.add(String.valueOf(voltage_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("current_speech_ack");
		pref.add(String.valueOf(current_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("height_speech_ack");
		pref.add(String.valueOf(height_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("used_speech_ack");
		pref.add(String.valueOf(used_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speed_speech_ack");
		pref.add(String.valueOf(speed_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("rpm_speech_ack");
		pref.add(String.valueOf(rpm_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("temp_speech_ack");
		pref.add(String.valueOf(temp_speech_ack));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("voltage_speech_label");
		pref.add(voltage_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("voltage_speech_unit");
		pref.add(voltage_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("height_speech_label");
		pref.add(height_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("height_speech_unit");
		pref.add(height_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("current_speech_label");
		pref.add(current_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("current_speech_unit");
		pref.add(current_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("used_speech_label");
		pref.add(used_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("used_speech_unit");
		pref.add(used_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speed_speech_label");
		pref.add(speed_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speed_speech_unit");
		pref.add(speed_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("rpm_speech_label");
		pref.add(rpm_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("rpm_speech_unit");
		pref.add(rpm_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("temp_speech_label");
		pref.add(temp_speech_label);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("temp_speech_unit");
		pref.add(temp_speech_unit);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mAltitudeAlarm");
		pref.add(String.valueOf(mAltitudeAlarm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mSound_on");
		pref.add(String.valueOf(mSound_on));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speech_on");
		pref.add(String.valueOf(speech_on));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mlog_file");
		pref.add(String.valueOf(mlog_file));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("mAlarm_on");
		pref.add(String.valueOf(mAlarm_on));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_voltage");
		pref.add(String.valueOf(alarm_voltage));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_used");
		pref.add(String.valueOf(alarm_used));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_current");
		pref.add(String.valueOf(alarm_current));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_speed");
		pref.add(String.valueOf(alarm_speed));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_fuel");
		pref.add(String.valueOf(alarm_fuel));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_temperature");
		pref.add(String.valueOf(alarm_temperature));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_altitude");
		pref.add(String.valueOf(alarm_altitude));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_rpm");
		pref.add(String.valueOf(alarm_rpm));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alarm_power");
		pref.add(String.valueOf(alarm_power));
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("log_file");
		pref.add(log_file);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("bluetooth_device");
		pref.add(bluetooth_device);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("speech_intervall");
		pref.add(speech_intervall);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("alert_tone");
		pref.add(alert_tone);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("label_rpm1");
		pref.add(label_rpm1);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("label_rpm2");
		pref.add(label_rpm2);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("label_temp1");
		pref.add(label_temp1);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("label_temp2");
		pref.add(label_temp2);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("unit_rpm1");
		pref.add(unit_rpm1);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("unit_rpm2");
		pref.add(unit_rpm2);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("unit_temp1");
		pref.add(unit_temp1);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("unit_temp2");
		pref.add(unit_temp2);
		prefs.add(pref);
		pref = new ArrayList<String>();
		pref.add("current_sensor_type");
		pref.add(current_sensor_type);
		prefs.add(pref);
		return prefs;
	}

	public void readMinPrefs() {
		try {
			auto_connect = mPrefs.getBoolean("auto_connect", auto_connect);
			mpxData = mPrefs.getBoolean("mpx_data", mpxData);
			metric = mPrefs.getBoolean("metric", metric);
			vario_delay = mPrefs.getString("vario_delay",
					vario_delay);
			displayAlwaysOn = mPrefs.getBoolean("displayAlwaysOn",
					displayAlwaysOn);
			
		} catch (Exception e) {
		}
	}

	public void readPrefs() {
		String valueTmp = "";
		try {
			voltage_speech_alert = mPrefs.getString("voltage_speech_alert",
					voltage_speech_alert);
			height_speech_alert = mPrefs.getString("height_speech_alert",
					height_speech_alert);
			current_speech_alert = mPrefs.getString("current_speech_alert",
					current_speech_alert);
			used_speech_alert = mPrefs.getString("used_speech_alert",
					used_speech_alert);
			temp_speech_alert = mPrefs.getString("temp_speech_alert",
					temp_speech_alert);
			rpm_speech_alert = mPrefs.getString("rpm_speech_alert",
					rpm_speech_alert);
			speed_speech_alert = mPrefs.getString("speed_speech_alert",
					speed_speech_alert);
			mVoltageAlarm = getDouble("voltage", mVoltageAlarm);
			mUsedCap = getDouble("used_cap", mUsedCap);
			mCurrentAlarm = getDouble("current", mCurrentAlarm);
			mPowerAlarm = getDouble("power", mPowerAlarm);
			mSpeedAlarm = tools.getInteger("speed", mSpeedAlarm, mPrefs);
			// mSpeedAlarm = getInteger("speed", mSpeedAlarm);
			mRpmAlarm = getInteger("rpm", mRpmAlarm);
			mAltitudeAlarm = getInteger("altitude", mAltitudeAlarm);
			mFuelAlarm = getInteger("fuel", mFuelAlarm);
			voltage_hysteresis = getInteger("voltage_hysteresis",
					voltage_hysteresis);
			current_hysteresis = getInteger("current_hysteresis",
					current_hysteresis);
			rpm_hysteresis = getInteger("rpm_hysteresis", rpm_hysteresis);
			temperature_hysteresis = getInteger("temperature_hysteresis",
					temperature_hysteresis);

			power_hysteresis = getInteger("power_hysteresis", power_hysteresis);
			mTemperatureAlarm = getInteger("temperature", mTemperatureAlarm);
			speech_volume = getInteger("speech_volume", speech_volume);
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int sb2value = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			sb2value = sb2value * speech_volume / 100;
			am.setStreamVolume(AudioManager.STREAM_MUSIC, sb2value, 0);
			vibrate_on = mPrefs.getBoolean("vibrate_on", vibrate_on);
			metric = mPrefs.getBoolean("metric", metric);
			mpxData = mPrefs.getBoolean("mpx_data", mpxData);
			displayAlwaysOn = mPrefs.getBoolean("displayAlwaysOn",
					displayAlwaysOn);
			voltage_speech_ack = mPrefs.getBoolean("voltage_speech_ack",
					voltage_speech_ack);
			valueTmp = voltage_speech_label + ";" + voltage_speech_unit;
			valueTmp = mPrefs.getString("voltage_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				voltage_speech_label = tokens.nextToken().trim();
				voltage_speech_unit = tokens.nextToken().trim();
			}

			height_speech_ack = mPrefs.getBoolean("height_speech_ack",
					height_speech_ack);
			auto_connect = mPrefs.getBoolean("auto_connect", auto_connect);
			mpxData = mPrefs.getBoolean("mpx_data", mpxData);
			valueTmp = height_speech_label + ";" + height_speech_unit;
			valueTmp = mPrefs.getString("height_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				height_speech_label = tokens.nextToken().trim();
				height_speech_unit = tokens.nextToken().trim();
			}
			current_speech_ack = mPrefs.getBoolean("current_speech_ack",
					current_speech_ack);
			valueTmp = current_speech_label + ";" + current_speech_unit;
			valueTmp = mPrefs.getString("current_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				current_speech_label = tokens.nextToken().trim();
				current_speech_unit = tokens.nextToken().trim();
			}
			used_speech_ack = mPrefs.getBoolean("used_speech_ack",
					used_speech_ack);
			valueTmp = used_speech_label + ";" + used_speech_unit;
			valueTmp = mPrefs.getString("used_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				used_speech_label = tokens.nextToken().trim();
				used_speech_unit = tokens.nextToken().trim();
			}
			speed_speech_ack = mPrefs.getBoolean("speed_speech_ack",
					speed_speech_ack);
			valueTmp = speed_speech_label + ";" + speed_speech_unit;
			valueTmp = mPrefs.getString("speed_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				speed_speech_label = tokens.nextToken().trim();
				speed_speech_unit = tokens.nextToken().trim();
			}
			vario_delay = mPrefs.getString("vario_delay",
					vario_delay);
			rpm_speech_ack = mPrefs
					.getBoolean("rpm_speech_ack", rpm_speech_ack);
			valueTmp = rpm_speech_label + ";" + rpm_speech_unit;
			valueTmp = mPrefs.getString("rpm_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				rpm_speech_label = tokens.nextToken().trim();
				rpm_speech_unit = tokens.nextToken().trim();
			}

			temp_speech_ack = mPrefs.getBoolean("temp_speech_ack",
					temp_speech_ack);
			valueTmp = temp_speech_label + ";" + temp_speech_unit;
			valueTmp = mPrefs.getString("temp_speech_text", valueTmp);
			if (!valueTmp.equals("") && valueTmp.contains(";")) {
				StringTokenizer tokens = new StringTokenizer(valueTmp, ";");
				temp_speech_label = tokens.nextToken().trim();
				temp_speech_unit = tokens.nextToken().trim();
			}

		} catch (Exception e) {
		}
		valueTmp = mPrefs.getString("log_file", log_file);
		if (valueTmp != "") {
			log_file = valueTmp;
		}
		valueTmp = mPrefs.getString("bluetooth_device", bluetooth_device);
		if (valueTmp != "") {
			bluetooth_device = valueTmp;
		}
		mSound_on = mPrefs.getBoolean("sound_on", mSound_on);
		speech_on = mPrefs.getBoolean("speech_on", speech_on);
		speech_intervall = mPrefs.getString("speech_intervall",
				speech_intervall);

		speech_alert_intervall = mPrefs.getString("speech_alert_intervall",
				speech_alert_intervall);
		mAlarm_on = mPrefs.getBoolean("alarm_on", mAlarm_on);
		mSpeak_on = mPrefs.getBoolean("speak_on", mSpeak_on);
		mlog_file = mPrefs.getBoolean("logging_on", mlog_file);
		alert_tone = mPrefs.getString("ringtone", alert_tone);
		label_rpm1 = mPrefs.getString("label_rpm1", label_rpm1);
		label_rpm2 = mPrefs.getString("label_rpm2", label_rpm2);
		label_temp1 = mPrefs.getString("label_temp1", label_temp1);
		label_temp2 = mPrefs.getString("label_temp2", label_temp2);
		unit_rpm1 = mPrefs.getString("unit_rpm1", unit_rpm1);
		unit_rpm2 = mPrefs.getString("unit_rpm2", unit_rpm2);
		unit_temp1 = mPrefs.getString("unit_temp1", unit_temp1);
		unit_temp2 = mPrefs.getString("unit_temp2", unit_temp2);
		current_sensor_type = mPrefs.getString("current_sensor_type",
				current_sensor_type);
		vario_delay= mPrefs.getString("vario_delay",
				vario_delay);
		
		
		alarm_voltage = mPrefs.getBoolean("alarm_voltage", alarm_voltage);
		alarm_used = mPrefs.getBoolean("alarm_used", alarm_used);
		alarm_current = mPrefs.getBoolean("alarm_current", alarm_current);
		alarm_speed = mPrefs.getBoolean("alarm_speed", alarm_speed);
		alarm_fuel = mPrefs.getBoolean("alarm_fuel", alarm_fuel);
		alarm_temperature = mPrefs.getBoolean("alarm_temperature",
				alarm_temperature);
		alarm_altitude = mPrefs.getBoolean("alarm_altitude", alarm_altitude);
		alarm_rpm = mPrefs.getBoolean("alarm_rpm", alarm_rpm);
		alarm_power = mPrefs.getBoolean("alarm_power", alarm_power);
		speak_voltage = mPrefs.getBoolean("speak_voltage", speak_voltage);
		speak_used = mPrefs.getBoolean("speak_used", speak_used);
		speak_current = mPrefs.getBoolean("speak_current", speak_current);
		speak_speed = mPrefs.getBoolean("speak_speed", speak_speed);
		speak_fuel = mPrefs.getBoolean("speak_fuel", speak_fuel);
		speak_temperature = mPrefs.getBoolean("speak_temperature",
				speak_temperature);
		speak_altitude = mPrefs.getBoolean("speak_altitude", speak_altitude);
		speak_rpm = mPrefs.getBoolean("speak_rpm", speak_rpm);
		speak_power = mPrefs.getBoolean("speak_power", speak_power);
		if (rpm1Label != null)
			rpm1Label.setText(label_rpm1);
		if (rpm2Label != null)
			rpm2Label.setText(label_rpm2);
		if (tempLabel != null)
			tempLabel.setText(label_temp1);
		if (temp2Label != null)
			temp2Label.setText(label_temp2);
		if (rpm1_unit != null)
			rpm1_unit.setText(unit_rpm1);
		if (rpm2_unit != null)
			rpm2_unit.setText(unit_rpm2);
		if (temp1_unit != null)
			temp1_unit.setText(unit_temp1);
		if (temp1_unit != null)
			temp1_unit.setText(unit_temp2);
		if (alert_tone.equals("")) {
			uri = null;
		} else {
			uri = Uri.parse(alert_tone);
		}
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
			if (uri != null && !uri.toString().equals("res/raw/alarm.mp3")) {
				
				mp = MediaPlayer.create(getApplicationContext(), uri);
			} else {
				mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
			}
		}
	}

	public int getConnectionState() {
		return mSerialService.getState();
	}
	private final Handler mHandlerBT = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:

				switch (msg.arg1) {
				case BluetoothSerialService.STATE_CONNECTED:
					if (mMenuItemConnect != null) {
						mMenuItemConnect
								.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
						mMenuItemConnect.setTitle(R.string.disconnect);
					}
					aurora_ready = true;
					break;
				case BluetoothSerialService.STATE_CONNECTING:
					break;
				case BluetoothSerialService.STATE_LISTEN:
				case BluetoothSerialService.STATE_NONE:
					try {
						if (mMenuItemConnect != null) {
							mMenuItemConnect.setIcon(R.drawable.bluetooth);
							mMenuItemConnect.setTitle(R.string.connect);
						}
					} catch (Exception e) {
					}
					aurora_ready = false;
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void finishDialogNoBluetooth() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alert_dialog_no_bt)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				mSerialService.connect(device);
				mEnablingBT = true;
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				mEnablingBT = true;
			} else {
				finishDialogNoBluetooth();
			}
			break;
		case MY_DATA_CHECK_CODE:
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				if (tts == null) {
					tts = new TextToSpeech(this, this);
				}
				
			} else {
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
			break;
		case REQUEST_PREFS:
			if (resultCode == Activity.RESULT_OK) {
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu,(android.view.Menu) menu);
		mMenuItemConnect = menu.getItem(0);
		mMenuItemLog = menu.getItem(2);
		if (mMenuItemConnect != null && aurora_ready == true) {
			mMenuItemConnect
					.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			mMenuItemConnect.setTitle(R.string.disconnect);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.connect) {
			if (getConnectionState() == BluetoothSerialService.STATE_NONE) {
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				serverIntent.putExtra("bluetooth_device", bluetooth_device);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				Log.e("bluetooth_device_conect:",""+bluetooth_device);
				
			} else if (getConnectionState() == BluetoothSerialService.STATE_CONNECTED) {
				mSerialService.stop();
				mSerialService.start();
			}
		} else if (itemId == R.id.preferences) {
			doPreferences();
		} else if (itemId == R.id.menu_special_keys) {
			doDocumentKeys();
		} else if (itemId == R.id.menu_graph) {
			launchGraphDialog();
		} else if (itemId == R.id.id_menu_vario) {
			launchVario();
		} else if (itemId == R.id.id_menu_instrument) {
			launchInstrument();
		} else if (itemId == R.id.menu_help) {
			launchHelp();
		} else if (itemId == R.id.id_menu_vario_test) {
			launchVarioTest();
		} else if (itemId == R.id.menu_log) {
			toggle_log();
		} else if (itemId == R.id.menu_data_explorer) {
			launchDataExplorer();
		} else if (itemId == R.id.menu_save_profile) {
			saveProfile();
		} else if (itemId == R.id.menu_launch_profile) {
			launchProfile();
		} else if (itemId == R.id.menu_backup) {
			doBackup();
		} else if (itemId == R.id.menu_profile_editor) {
			doProfileEdit();
		} else if (itemId == R.id.menu_maps) {
			doMaps();
		} else if (itemId == R.id.menu_speech_test) {
			doSpeak();
		} else if (itemId == R.id.speakService) {
			doService();
		}
		return false;
	}

	private void doSpeakAlarm(String alert_text, boolean speak_alert) {
		if (initialized == true && mSpeak_on == true && speak_alert == true) {
			tts.speak(alert_text, TextToSpeech.QUEUE_ADD, null);
			tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
		}
	}
	private void doService() {
		
		
		 NotificationCompat.Builder builder =
		            new NotificationCompat.Builder(this)
		                    .setSmallIcon(R.drawable.aurorasmall)
		                    .setContentTitle("Hitec Telemetry Service")
		                    .setContentText("started...");
		    int NOTIFICATION_ID = 12346;
		
		    Intent targetIntent = new Intent(this, SpectraTelemetry.class);
		    targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
		    builder.setContentIntent(contentIntent);
		    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		    nManager.notify(NOTIFICATION_ID, builder.build());
		
		
		
		
        Intent intent = new Intent(this, SpeakService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        Calendar cal = Calendar.getInstance();
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pendingIntent); 
	}
	private void stopService() {
		Log.e("Hitec telemetry", "Stopt service");
		  AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		  alarmManager.cancel(pendingIntent);
	}
	public static void doSpeak() {
		if (initialized == true) {
			String text = "";
			if (voltage != null) {
				text = voltage.getText().toString();
				if (text != null && text.length() > 0 && voltage_speech_ack == true) {
					text = voltage_speech_label + " " + text + " "
							+ voltage_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
			if (alt != null) {
				text = alt.getText().toString();
				if (text != null && text.length() > 0 && height_speech_ack == true) {
					text = height_speech_label + " " + text + " "
							+ height_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				} 
			}
			if (current != null) {
				text = current.getText().toString();
				if (text != null && text.length() > 0 && current_speech_ack == true) {
					text = current_speech_label + " " + text + " "
							+ current_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
			if (used != null) {
				text = used.getText().toString();
				if (text != null && text.length() > 0 && used_speech_ack == true) {
					text = used_speech_label + " " + text + " " + used_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
			if (temp1 !=null) {
				text = temp1.getText().toString();
				if (text != null && text.length() > 0 && temp_speech_ack == true) {
					text = temp_speech_label + " " + text + " " + temp_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
			if (rpm1 != null) {
				text = rpm1.getText().toString();
				if (text != null && text.length() > 0 && rpm_speech_ack == true) {
					text = rpm_speech_label + " " + text + " " + rpm_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
			if (speed != null) {
				text = speed.getText().toString();
				if (text != null && text.length() > 0 && speed_speech_ack == true) {
					text = speed_speech_label + " " + text + " "
							+ speed_speech_unit;
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
				}
			}
		}

	}

	private void doProfileEdit() {
		Intent intent = new Intent(this, Profile.class);
		startActivity(intent);
	}

	private void launchVario() {
		Intent intent = new Intent(this, Vario.class);
		intent.putExtra("metric",
				String.valueOf(metric));
		startActivity(intent);
	}
	private void launchInstrument() {
		Intent intent = new Intent(this, instrument.class);
		startActivity(intent);
	}
	
	private void launchHelp() {
		Intent intent = new Intent(this, help.class);
		startActivity(intent);
	}

	private void launchVarioTest() {
		Intent intent = new Intent(this, VarioTest.class);
		intent.putExtra("delay", Integer.parseInt(vario_delay));
		startActivity(intent);
	
	}

	private void doMaps() {

		try {
			if (android.os.Build.VERSION.SDK_INT < 8) {

				new AlertDialog.Builder(this)
						.setTitle("Hitec Telemetry")
						.setInverseBackgroundForced(true)
						.setCancelable(true)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										}
								})
						.setMessage(
								"Sorry, you have to use Android\nVersion greater than 2.1\nto use this feature!")
						.show();

			} else {
				Intent mapsIntent = new Intent(this, MapView.class);
				mapsIntent.putExtra("longitude",
						String.valueOf(LastValidLongitude));
				mapsIntent.putExtra("latitude",
						String.valueOf(LastValidLatitude));
				mapsIntent.putExtra("mpxdata", mpxData);
				mapsIntent.putExtra("distance", LastValidDistance);
				mapsIntent.putExtra("alpha", LastValidCourse);
				this.startActivityForResult(mapsIntent, REQUEST_PREFS);
			}
		} catch (Exception e) {

		}
	}

	private void doPreferences() {
		this.startActivityForResult(new Intent(this, TermPreferences.class), REQUEST_PREFS);
	}

	private void toggle_log() {
		if (writelog == true) {
			if (worker != null) {
				worker.setPause(true);
				worker.storeData();
				worker.copyFile();
				worker.setPause(false);
			}
			mMenuItemLog.setTitle(R.string.log_on);
			writelog = false;
		} else {
			writelog = true;
			mMenuItemLog.setTitle(R.string.log_off);
			if (worker != null) {
				worker.getFile(false);
				worker.setPause(false);
			}
		}
	}

	private void launchDataExplorer() {
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
		}
		Intent intent = new Intent(this, DataExplorer.class);
		intent.putExtra("log_file", log_file);
		intent.putExtra("mlog_file", true);
		startActivityForResult(intent, REQUEST_PREFS);
	}

	private void launchGraphAV() {
		// AdvancedGraph
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
		}
		Intent intent = new Intent(this, AVGraph.class);
		intent.putExtra("type", "line");
		intent.putExtra("log_file", log_file);
		intent.putExtra("mlog_file", true);
		intent.putExtra("rpm1_label", label_rpm1);
		startActivityForResult(intent, REQUEST_PREFS);
	}

	private void launchGraphRP() {
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
		}
		Intent intent = new Intent(this, RPGraph.class);
		intent.putExtra("type", "line");
		intent.putExtra("log_file", log_file);
		intent.putExtra("mlog_file", true);
		intent.putExtra("rpm1_label", label_rpm1);
		startActivityForResult(intent, REQUEST_PREFS);
	}

	public void doBackup() {
		boolean backupSuccess = false;
		try {
			myDbHelper.createDataBase();
			backupSuccess = myDbHelper.backupDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (backupSuccess) {
			Toast.makeText(getBaseContext(), "Backup successful!",
					Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(getBaseContext(), "ERROR:Backup not successful!",
					Toast.LENGTH_SHORT).show();

		}

	}

	private void launchGraphSA() {
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
		}
		Intent intent = new Intent(this, ASGraph.class);
		intent.putExtra("type", "line");
		intent.putExtra("log_file", log_file);
		intent.putExtra("mlog_file", true);
		intent.putExtra("rpm1_label", label_rpm1);
		startActivityForResult(intent, REQUEST_PREFS);
	}

	private void launchGraphT() {
		if (worker != null) {
			worker.setPause(true);
			worker.storeData();
		}
		Intent intent = new Intent(this, TGraph.class);
		intent.putExtra("type", "line");
		intent.putExtra("log_file", log_file);
		intent.putExtra("mlog_file", true);
		intent.putExtra("rpm1_label", label_rpm1);
		startActivityForResult(intent, REQUEST_PREFS);
	}

	private void doDocumentKeys() {
		new AlertDialog.Builder(this)
				.setTitle("")
				.setInverseBackgroundForced(false)
				.setMessage(
						" Hitec Telemetry\n"
								+ " Interface V1.09B20140106\n"
								+ " (c) 2013, 2014 Ralf Rosche\n"
								+ " Licensed under the GNU Lesser General Public License (LGPL)"
								+ " http://www.gnu.org/licenses/lgpl.html\n")
				.show();
	}

	private void launchGraphDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("select graph type")
				.setItems(R.array.graphTypes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									launchGraphAV();
									break;
								case 1:
									launchGraphRP();
									break;
								case 2:
									launchGraphSA();
									break;
								case 3:
									launchGraphT();
									break;
								}
							}
						}).show();
	}

	private int getprofileId(String item) {
		int id = 0;
		int length = profiles.size();
		for (int i = 0; i < length; i++) {
			ArrayList<String> profile_array = new ArrayList<String>();
			profile_array = profiles.get(i);
			String itemList = profile_array.get(1).toString();
			if (itemList.equals(item)) {
				id = Integer.parseInt(profile_array.get(0));
			}
		}
		return id;
	}

	private Map<String, String> setPrefs(ArrayList<ArrayList<String>> prefs) {
		Map<String, String> pref_array = new HashMap<String, String>();
		int length = prefs.size();
		for (int i = 0; i < length; i++) {
			ArrayList<String> pref = new ArrayList<String>();
			pref = prefs.get(i);
			String var_name = pref.get(0).toString();
			String value = pref.get(1).toString();
			pref_array.put(var_name, value);
		}
		return pref_array;
	}

	private void loadDefaultProfile(int profile) {
		ArrayList<ArrayList<String>> prefs = new ArrayList<ArrayList<String>>();
		try {
			prefs = myDbHelper.loadProfile(profile);

		} catch (SQLiteException e) {

			e.printStackTrace();
		}
		if (prefs.size() > 0) {
			Map<String, String> set_Prefs = setPrefs(prefs);
			setDefaultPrefs(set_Prefs);
			overwritePrefs(set_Prefs);
		} else {
			readPrefs();
		}
	}

	private void overwritePrefs(Map<String, String> DbPrefs) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("mVoltageAlarm", "voltage");
		map.put("mUsedCap", "used_cap");
		map.put("mCurrentAlarm", "current");
		map.put("mPowerAlarm", "power");
		map.put("mSpeedAlarm", "speed");
		map.put("mRpmAlarm", "rpm");
		map.put("mAltitudeAlarm", "altitude");
		map.put("mFuelAlarm", "fuel");
		map.put("mTemperatureAlarm", "temperature");
		map.put("mSpeak_on", "speak_on");
		map.put("mlog_file", "logging_on");
		map.put("alert_tone", "ringtone");
		map.put("mSound_on", "sound_on");
		map.put("mAlarm_on", "alarm_on");
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		Iterator it = DbPrefs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (!pairs.getKey().toString().equals("id")
					&& !pairs.getKey().toString().equals("ref_profile")) {
				SharedPreferences.Editor editor = settings.edit();
				if (pairs.getValue().toString().equals("true")
						|| pairs.getValue().toString().equals("false")) {

					String nameBool = map.get(pairs.getKey().toString());
					if (nameBool == null)
						nameBool = pairs.getKey().toString();
					editor.putBoolean(nameBool,
							Boolean.parseBoolean(pairs.getValue().toString()));
				} else {

					String nameName = map.get(pairs.getKey().toString());
					if (nameName == null)
						nameName = pairs.getKey().toString();

					if (nameName.equals("voltage_speech_unit")) {
						voltage_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("voltage_speech_label")) {
						voltage_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("height_speech_unit")) {
						height_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("height_speech_label")) {
						height_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("used_speech_unit")) {
						used_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("used_speech_label")) {
						used_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("current_speech_unit")) {
						current_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("current_speech_label")) {
						current_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("used_speech_unit")) {
						used_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("used_speech_label")) {
						used_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("rpm_speech_unit")) {
						rpm_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("rpm_speech_label")) {
						rpm_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("speed_speech_unit")) {
						speed_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("speed_speech_label")) {
						speed_speech_label = pairs.getValue().toString();
						continue;
					}

					if (nameName.equals("temp_speech_unit")) {
						temp_speech_unit = pairs.getValue().toString();
						continue;
					}
					if (nameName.equals("temp_speech_label")) {
						temp_speech_label = pairs.getValue().toString();
						continue;
					}
					editor.putString(nameName, pairs.getValue().toString());
				}
				editor.commit();
			}
			it.remove();

		}

		SharedPreferences.Editor editor = settings.edit();
		String voltage_speech_text = voltage_speech_label + ";"
				+ voltage_speech_unit;
		editor.putString("voltage_speech_text", voltage_speech_text);
		editor.commit();

		String height_speech_text = height_speech_label + ";"
				+ height_speech_unit;
		editor.putString("height_speech_text", height_speech_text);
		editor.commit();

		String current_speech_text = current_speech_label + ";"
				+ current_speech_unit;
		editor.putString("current_speech_text", current_speech_text);
		editor.commit();

		String used_speech_text = used_speech_label + ";" + used_speech_unit;
		editor.putString("used_speech_text", used_speech_text);
		editor.commit();

		String rpm_speech_text = rpm_speech_label + ";" + rpm_speech_unit;
		editor.putString("rpm_speech_text", rpm_speech_text);
		editor.commit();

		String speed_speech_text = speed_speech_label + ";" + speed_speech_unit;
		editor.putString("speed_speech_text", speed_speech_text);
		editor.commit();

		String temp_speech_text = temp_speech_label + ";" + temp_speech_unit;
		editor.putString("temp_speech_text", temp_speech_text);
		editor.commit();

	}

	private void setDefaultPrefs(Map<String, String> DbPrefs) {
		String valueTmp = "";
		mVoltageAlarm = getDouble(mVoltageAlarm, DbPrefs.get("mVoltageAlarm"));
		mUsedCap = getDouble(mUsedCap, DbPrefs.get("mUsedCap"));
		mCurrentAlarm = getDouble(mCurrentAlarm, DbPrefs.get("mCurrentAlarm"));
		mPowerAlarm = getDouble(mPowerAlarm, DbPrefs.get("mPowerAlarm"));

		mSpeedAlarm = getInteger(mSpeedAlarm, DbPrefs.get("mSpeedAlarm"));
		mRpmAlarm = getInteger(mRpmAlarm, DbPrefs.get("mRpmAlarm"));
		mAltitudeAlarm = getInteger(mAltitudeAlarm,
				DbPrefs.get("mAltitudeAlarm"));
		mFuelAlarm = getInteger(mFuelAlarm, DbPrefs.get("mFuelAlarm"));
		voltage_hysteresis = getInteger(voltage_hysteresis,
				DbPrefs.get("voltage_hysteresis"));
		current_hysteresis = getInteger(current_hysteresis,
				DbPrefs.get("current_hysteresis"));
		rpm_hysteresis = getInteger(rpm_hysteresis,
				DbPrefs.get("rpm_hysteresis"));
		temperature_hysteresis = getInteger(temperature_hysteresis,
				DbPrefs.get("temperature_hysteresis"));
		power_hysteresis = getInteger(power_hysteresis, DbPrefs.get(""));
		mTemperatureAlarm = getInteger(mTemperatureAlarm,
				DbPrefs.get("power_hysteresis"));
		speech_volume = getInteger(speech_volume, DbPrefs.get("speech_volume"));
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int sb2value = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		sb2value = sb2value * speech_volume / 100;
		am.setStreamVolume(AudioManager.STREAM_MUSIC, sb2value, 0);
		vibrate_on = getBoolean(vibrate_on, DbPrefs.get("vibrate_on"));
		voltage_speech_ack = getBoolean(voltage_speech_ack,
				DbPrefs.get("voltage_speech_ack"));
		current_speech_ack = getBoolean(current_speech_ack,
				DbPrefs.get("current_speech_ack"));
		height_speech_ack = getBoolean(height_speech_ack,
				DbPrefs.get("height_speech_ack"));
		used_speech_ack = getBoolean(used_speech_ack,
				DbPrefs.get("used_speech_ack"));
		speed_speech_ack = getBoolean(speed_speech_ack,
				DbPrefs.get("speed_speech_ack"));
		rpm_speech_ack = getBoolean(rpm_speech_ack,
				DbPrefs.get("rpm_speech_ack"));
		temp_speech_ack = getBoolean(temp_speech_ack,
				DbPrefs.get("temp_speech_ack"));

		voltage_speech_label = DbPrefs.get("voltage_speech_label");
		voltage_speech_unit = DbPrefs.get("voltage_speech_unit");

		height_speech_label = DbPrefs.get("height_speech_label");
		height_speech_unit = DbPrefs.get("height_speech_unit");

		current_speech_label = DbPrefs.get("current_speech_label");
		current_speech_unit = DbPrefs.get("current_speech_unit");

		used_speech_label = DbPrefs.get("used_speech_label");
		used_speech_unit = DbPrefs.get("used_speech_unit");

		speed_speech_label = DbPrefs.get("speed_speech_label");
		speed_speech_unit = DbPrefs.get("speed_speech_unit");

		rpm_speech_label = DbPrefs.get("rpm_speech_label");
		rpm_speech_unit = DbPrefs.get("rpm_speech_unit");

		temp_speech_label = DbPrefs.get("temp_speech_label");
		temp_speech_unit = DbPrefs.get("temp_speech_unit");

		valueTmp = DbPrefs.get("log_file");
		if (valueTmp != "") {
			log_file = valueTmp;
		}

		valueTmp = DbPrefs.get("bluetooth_device");
		if (valueTmp != "") {
			bluetooth_device = valueTmp;
		}
		mSound_on = getBoolean(mSound_on, DbPrefs.get("mSound_on"));
		speech_on = getBoolean(speech_on, DbPrefs.get("speech_on"));
		speech_intervall = DbPrefs.get("speech_intervall");
		mAlarm_on = getBoolean(mAlarm_on, DbPrefs.get("mAlarm_on"));
		mlog_file = getBoolean(mlog_file, DbPrefs.get("mlog_file"));
		alert_tone = DbPrefs.get("alert_tone");
		label_rpm1 = DbPrefs.get("label_rpm1");
		label_rpm2 = DbPrefs.get("label_rpm2");
		label_temp1 = DbPrefs.get("label_temp1");
		label_temp2 = DbPrefs.get("label_temp2");
		unit_rpm1 = DbPrefs.get("unit_rpm1");
		unit_rpm2 = DbPrefs.get("unit_rpm2");
		unit_temp1 = DbPrefs.get("unit_temp1");
		unit_temp2 = DbPrefs.get("unit_temp2");
		current_sensor_type = DbPrefs.get("current_sensor_type");
		alarm_voltage = getBoolean(alarm_voltage, DbPrefs.get("alarm_voltage"));
		alarm_used = getBoolean(alarm_used, DbPrefs.get("alarm_used"));
		alarm_current = getBoolean(alarm_current, DbPrefs.get("alarm_current"));
		alarm_speed = getBoolean(alarm_speed, DbPrefs.get("alarm_speed"));
		alarm_fuel = getBoolean(alarm_fuel, DbPrefs.get("alarm_fuel"));
		alarm_temperature = getBoolean(alarm_temperature,
				DbPrefs.get("alarm_temperature"));
		alarm_altitude = getBoolean(alarm_altitude,
				DbPrefs.get("alarm_altitude"));
		alarm_rpm = getBoolean(alarm_rpm, DbPrefs.get("alarm_rpm"));
		alarm_power = getBoolean(alarm_power, DbPrefs.get("alarm_power"));

		if (rpm1Label != null)
			rpm1Label.setText(label_rpm1);
		if (rpm2Label != null)
			rpm2Label.setText(label_rpm2);
		if (tempLabel != null)
			tempLabel.setText(label_temp1);
		if (temp2Label != null)
			temp2Label.setText(label_temp2);

		if (rpm1_unit != null)
			rpm1_unit.setText(unit_rpm1);
		if (rpm2_unit != null)
			rpm2_unit.setText(unit_rpm2);
		if (temp1_unit != null)
			temp1_unit.setText(unit_temp1);
		if (temp1_unit != null)
			temp1_unit.setText(unit_temp2);

		if (alert_tone.equals("")) {
			uri = null;
		} else {
			uri = Uri.parse(alert_tone);
		}

		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
			if (uri != null && !uri.toString().equals("res/raw/alarm.mp3")) {
				mp = MediaPlayer.create(getApplicationContext(), uri);
			} else {
				mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
			}
		}
	}

	private void launchProfile() {
		int length = profiles.size();
		final CharSequence[] items = new CharSequence[length];
		int selectedId = 0;
		for (int i = 0; i < length; i++) {
			ArrayList<String> profile_array = new ArrayList<String>();

			profile_array = profiles.get(i);
			items[i] = profile_array.get(1).toString();
			int id = Integer.parseInt(profile_array.get(0).toString());
			if (profile == id) {
				selectedId = i;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("select profile")
				.setSingleChoiceItems(items, selectedId,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Toast.makeText(getApplicationContext(),
										"load profile " + items[which],
										Toast.LENGTH_LONG).show();
								profile = getprofileId(items[which].toString());
								loadDefaultProfile(profile);
								dialog.dismiss();

							}
						}).show();

	}

	private void saveProfile() {
		int length = profiles.size();
		int selectedId = 0;
		final CharSequence[] items = new CharSequence[length];
		for (int i = 0; i < length; i++) {
			ArrayList<String> profile_array = new ArrayList<String>();

			profile_array = profiles.get(i);
			items[i] = profile_array.get(1).toString();
			int id = Integer.parseInt(profile_array.get(0).toString());
			if (profile == id) {
				selectedId = i;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("select profile")
				.setSingleChoiceItems(items, selectedId,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(getApplicationContext(),
										"save profile " + items[which],
										Toast.LENGTH_LONG).show();
								profile = getprofileId(items[which].toString());
								ArrayList<ArrayList<String>> prefs = new ArrayList<ArrayList<String>>();
								prefs = getprefs();
								
								try {
									boolean result = myDbHelper.saveProfile(
											profile, prefs);
								} catch (SQLiteException e) {

									e.printStackTrace();
								}
								ArrayList<String> pref_liste = new ArrayList<String>();
								int length = prefs.size();
								for (int i = 0; i < length; i++) {
									ArrayList<String> pref = new ArrayList<String>();
									pref = prefs.get(i);
									pref_liste.add(pref.get(0));

								}
								saveSensorView();
								dialog.dismiss();
							}
						}).show();

	}
	public void saveSensorView() {
		ArrayList<ArrayList<String>> sensorArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> data = new ArrayList<String>();
		boolean VisibleState = false;
		boolean visible = false;
		VisibleState = mPrefs.getBoolean("volts",visible);
		data.add("volts");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("amps",visible);
		data.add("amps");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("Used",visible);
		data.add("Used");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("receiverVoltage",visible);
		data.add("receiverVoltage");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("powers",visible);
		data.add("power");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("speeds",visible);
		data.add("speed");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("altitudes",visible);
		data.add("altitude");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("rpm1",visible);
		data.add("rpm1");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("temp1",visible);
		data.add("temp1");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("temp2",visible);
		data.add("temp2");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("rpm2",visible);
		data.add("rpm2");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("fuels",visible);
		data.add("fuel");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("Servo01",visible);
		data.add("Servo01");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("Servo02",visible);
		data.add("Servo02");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("Servo03",visible);
		data.add("Servo03");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("Servo04",visible);
		data.add("Servo04");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("temp5",visible);
		data.add("temp5");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("airpressure1",visible);
		data.add("airpressure1");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("airpressure2",visible);
		data.add("airpressure2");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("speedpressure",visible);
		data.add("speedpressure");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("lqi",visible);
		data.add("lqi");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("txState",visible);
		data.add("txState");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("distance",visible);
		data.add("distance");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		VisibleState = mPrefs.getBoolean("gpsCourse",visible);
		data.add("gpsCourse");
		if (VisibleState == false) {
			data.add("0");
		} else {
			data.add("1");
		}
		sensorArray.add(data);
		data = new ArrayList<String>();
		try {
			boolean result = myDbHelper.InsertSensorData(
					profile, sensorArray);
		} catch (SQLiteException e) {

			e.printStackTrace();
		}
	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

	private class SpectraView implements Runnable {
		private int vspeed, valt, vtemp1, vtemp2, vtemp5, vfuel, vrpm1, vrpm2,
				vused,vlqi,vtxstate, vdistance;
		private double vvoltage, vvoltage2, vpower, vcurrent, vservo1, vservo2,
				vservo3, vservo4, vair1, vair2, vsP, vcourse;
		private DecimalFormat number_format = new DecimalFormat("0.#");
		public boolean alarm_flag, mustWrite = false;
		private boolean pause = false;
		private String vfirmware = "";
		private FileWriter writer = null;
		private long speech_int = 10000;
		private long speech_alert_int = 10000;
		private String liveData, hashLiveData = "";
		private long millis;
		private long millis_for_speech;
		private long millis_for_speech_alert;
		private long millis_for_vibrate;
		private int lvoltage_hysteresis = 0;
		private int lcurrent_hysteresis = 0;
		private int lrpm1_hysteresis = 0;
		private int ltemperature1_hysteresis = 0;
		private int lrpm2_hysteresis = 0;
		private int ltemperature2_hysteresis = 0;
		private int lpower_hysteresis = 0;
		private double Latitude = 0.0;
		private double Longitude = 0.0;
		boolean speak_alert = false;

		private String join(String[] strings) {
			StringBuilder mutable = new StringBuilder();
			boolean first = true;

			for (int i = 0; i < strings.length; i++) {
				if (!first)
					first = false;
				mutable.append(strings[i]);
			}

			return mutable.toString();
		}

		private final Runnable sRunnable = new Runnable() {
			@Override
			public void run() {
				if (aurora_ready == false)
					return;
				final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				try {

					speech_int = Long.parseLong(speech_intervall.trim());
					speech_alert_int = Long.parseLong(speech_alert_intervall
							.trim());

				} catch (NumberFormatException e) {

				}

				if (speech_on == true && aurora_ready == true) {
					if ((SystemClock.uptimeMillis() - millis_for_speech) > speech_int) {
						millis_for_speech = SystemClock.uptimeMillis();
						doSpeak();
					}
				}

				mustWrite = false;

				String[] tmp_string = new String[2];
				tmp_string[0] = "";
				tmp_string[1] = String.valueOf(vtemp1);
				if (temp1 !=null) {
					temp1.setText(join(tmp_string));
				}

				tmp_string[1] = String.valueOf(vtemp2);
				if (temp2 !=null) {
					temp2.setText(join(tmp_string));
				}

				tmp_string[1] = String.valueOf(vtemp5);
				if (temp5 !=null) {
					temp5.setText(join(tmp_string));
				}

				if (receiverVoltage != null) {
					receiverVoltage.setText(number_format.format(vvoltage2));
				}
				
				if (voltage != null) {
					switch (voltage_show_type) {
					case 1:
						voltage.setText(number_format.format(vvoltage));
						break;
					case 2:
						voltage.setText(number_format.format(voltageMin));
						break;
					case 3:
						voltage.setText(number_format.format(voltageMax));
						break;
					}
				}
				if (current != null) {
					switch (current_show_type) {
					case 1:
						current.setText(number_format.format(vcurrent));
						break;
					case 2:
						current.setText(number_format.format(currentMin));
						break;
					case 3:
						current.setText(number_format.format(currentMax));
						break;
					}
				}
				if (power != null) {
					switch (power_show_type) {
					case 1:
						power.setText(number_format.format(vpower));
						break;
					case 2:
						power.setText(number_format.format(powerMin));
						break;
					case 3:
						power.setText(number_format.format(powerMax));
						break;
					}
				}
				tmp_string[1] = String.valueOf(vspeed);
				if (speed != null) {
					speed.setText(join(tmp_string));
				}
			
				tmp_string[1] = String.valueOf(valt);
				if (alt != null) {
					alt.setText(join(tmp_string));
				}

				tmp_string[1] = String.valueOf(vfuel);
				if (fuel != null) {
					fuel.setText(join(tmp_string));
				}

				tmp_string[1] = String.valueOf(vrpm1);
				if (rpm1 != null) {
					rpm1.setText(join(tmp_string));
				}
				tmp_string[1] = String.valueOf(vrpm2);
				if (rpm2 != null) {
					rpm2.setText(join(tmp_string));
				}
				tmp_string[1] = String.valueOf(vlqi);
				if (lqi != null){
					lqi.setText(join(tmp_string));
				}
			
				if (txstate != null){
					switch (vtxstate) {
					case 4:
						txstate.setText("ok");
						break;
					case 20:
						txstate.setText("lost");
						break;
					default:
						txstate.setText("na");
						break;
					}
				}
				if (servo1 != null) {
					servo1.setText(number_format.format(vservo1));
				}
				if (servo2 != null) {
					servo2.setText(number_format.format(vservo2));
				}
				if (servo3 != null) {
					servo3.setText(number_format.format(vservo3));
				}
				if (servo4 != null) {
					servo4.setText(number_format.format(vservo4));
				}
				tmp_string[1] = String.valueOf(vused);
				if (used != null) {
					used.setText(join(tmp_string));
				}
				
				tmp_string[1] = String.valueOf(vsP);
				if (speedpressure != null) {
					speedpressure.setText(join(tmp_string));
				}

				Date GPsdate = spData.getGPSDate();
				String datestring = "0000/00/00 00:00:00";
				if (GPsdate != null) {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy/MM/dd kk:mm:ss", Locale.GERMANY);
					datestring = formatter.format(GPsdate);
				}
				Latitude = spData.getLatitude();
				Longitude = spData.getLongitude();
				vdistance = spData.getDistance();
				vcourse = spData.getGPSCourse();
				tmp_string[1] = String.valueOf(vdistance);
				if (distance != null) {
					distance.setText(join(tmp_string));
				}
				if (gpsCourse != null) {
					gpsCourse.setText(number_format.format(vcourse));
				}
				
				LastValidCourse = vcourse;
				LastValidDistance = vdistance;
				
				if (Latitude != 0.0) {
					LastValidLatitude = Latitude;
				}

				if (Longitude != 0.0) {
					LastValidLongitude = Longitude;
				}
				liveData = "$1;1;;";
				
						if (voltage != null) {
							liveData += (String) voltage.getText() + ";";
						}
				
						if (current != null) {
							liveData += (String) current.getText() + ";";
						}
						if (rpm1 != null) {
							liveData += (String) rpm1.getText() + ";";
						}
						if (rpm2 != null) {
							liveData += (String) rpm2.getText() + ";";
						}

						if (power != null) {
							liveData += (String) power.getText() + ";";
						}
						if (alt != null) {
							liveData += (String) alt.getText() + ";";
						}
						if (temp1 != null) {
							liveData += (String) temp1.getText() + ";";
						}
						if (temp2 != null) {
							liveData += (String) temp2.getText() + ";";
						}
						if (temp5 != null) {
							liveData += (String) temp5.getText() + ";";
						}
						if (speedpressure != null) {
							liveData += (String) speedpressure.getText() + ";";
						}
						if (used != null) {
							liveData += (String) used.getText() + ";";
						}
						if (speed != null) {
							liveData += (String) speed.getText() + ";";
						}
						liveData += number_format.format(vvoltage2) + ";"
						+ spData.getLatitudeDMS() + ";"
						+ spData.getLongitudeDMS() + ";"
						+ String.valueOf(Latitude) + ";"
						+ String.valueOf(Longitude) + ";" + datestring + ";";
						
						if (servo1 != null) {
							liveData += (String) servo1.getText() + ";";
						}
						if (servo2 != null) {
							liveData += (String) servo1.getText() + ";";
						}
						if (servo3 != null) {
							liveData += (String) servo1.getText() + ";";
						}
						if (servo4 != null) {
							liveData += (String) servo1.getText() + ";";
						}
						if (air1 != null) {
							liveData += (String) air1.getText() + ";";
						}
						if (air2 != null) {
							liveData += (String) air2.getText() + ";";
						}
						if (fuel != null) {
							liveData += (String) fuel.getText() + ";";
						}
						if (lqi != null){
							liveData += (String) lqi.getText() + ";";
						}
						if (txstate != null){
							liveData += (String) txstate.getText() + ";";
						}
						
						if (gpsCourse != null){
							liveData += (String) gpsCourse.getText() + ";";
						}
						if (receiverVoltage != null) {
							liveData += (String) receiverVoltage.getText() + ";";
						}
						if (distance != null){
							liveData += (String) distance.getText() + ";";
						}
						liveData +=  "0\r\n";

				if (hashLiveData.equals(liveData.toString())) {
					mustWrite = false;
				} else {
					mustWrite = true;
				}
				mustWrite = true;
				if ((SystemClock.uptimeMillis() - millis) > 1000) {
					if (air1 != null) {
						air1.setText(number_format.format(vair1));
					}
					tmp_string[1] = String.valueOf((int) vair2);
					if (air2 != null) {
						air2.setText(join(tmp_string));
					}
					millis = SystemClock.uptimeMillis();
					if (mustWrite == true) {
						writeData();
						hashLiveData = liveData.toString();
					}
				}

				alarm_flag = false;
				if (used != null) {
					if ((vused > mUsedCap) && mAlarm_on == true
							&& alarm_used == true) {
	
						used.setTextColor(getResources().getColor(
								R.color.errorColor));
						alarm_flag = true;
						if (speak_used == true) {
							doSpeakAlarm(used_speech_alert, speak_alert);
						}
	
					} else {
						used.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (voltage != null) {
					if ((vvoltage < mVoltageAlarm) && mAlarm_on == true
							&& alarm_voltage == true) {
	
						lvoltage_hysteresis++;
						if (lvoltage_hysteresis > voltage_hysteresis) {
							voltage.setTextColor(getResources().getColor(
									R.color.errorColor));
							alarm_flag = true;
							if (speak_voltage == true) {
								doSpeakAlarm(voltage_speech_alert, speak_alert);
							}
	
						} else {
							voltage.setTextColor(getResources().getColor(
									R.color.White));
	
						}
	
					} else {
						voltage.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (current != null) {
				if ((vcurrent > mCurrentAlarm) && mAlarm_on == true
						&& alarm_current == true) {

					lcurrent_hysteresis++;
					if (lcurrent_hysteresis > current_hysteresis) {
						current.setTextColor(getResources().getColor(
								R.color.errorColor));
						alarm_flag = true;
						if (speak_current == true) {
							doSpeakAlarm(current_speech_alert, speak_alert);
						}
					} else {
						current.setTextColor(getResources().getColor(
								R.color.White));
					}

				} else {
					current.setTextColor(getResources().getColor(R.color.White));
				}
				}
				if (speed != null) {
					if ((vspeed > mSpeedAlarm) && mAlarm_on == true
							&& alarm_speed == true) {
						alarm_flag = true;
						if (speak_speed == true) {
							doSpeakAlarm(speed_speech_alert, speak_alert);
						}
						speed.setTextColor(getResources().getColor(
								R.color.errorColor));
					} else {
						speed.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (power != null) {
					if ((vpower > mPowerAlarm) && mAlarm_on == true) {
	
						lpower_hysteresis++;
						if (lpower_hysteresis > power_hysteresis) {
							alarm_flag = true;
	
							power.setTextColor(getResources().getColor(
									R.color.errorColor));
						} else {
							power.setTextColor(getResources().getColor(
									R.color.White));
						}
	
					} else {
						power.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (fuel != null) {
					if ((vfuel < mFuelAlarm) && mAlarm_on == true
							&& alarm_fuel == true) {
						alarm_flag = true;
						fuel.setTextColor(getResources().getColor(
								R.color.errorColor));
					} else {
						fuel.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (alt != null) {
					if ((valt > mAltitudeAlarm) && mAlarm_on == true
							&& alarm_altitude == true) {
						alarm_flag = true;
						if (speak_altitude == true) {
							doSpeakAlarm(height_speech_alert, speak_alert);
						}
						alt.setTextColor(getResources()
								.getColor(R.color.errorColor));
					} else {
						alt.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (rpm1 != null) {
					if ((vrpm1 > mRpmAlarm) && mAlarm_on == true
							&& alarm_rpm == true) {
	
						lrpm1_hysteresis++;
						if (lrpm1_hysteresis > rpm_hysteresis) {
							alarm_flag = true;
							if (speak_rpm == true) {
								doSpeakAlarm(rpm_speech_alert, speak_alert);
							}
							rpm1.setTextColor(getResources().getColor(
									R.color.errorColor));
						} else {
							rpm1.setTextColor(getResources()
									.getColor(R.color.White));
						}
	
					} else {
						rpm1.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (rpm2 != null) {
					if ((vrpm2 > mRpmAlarm) && mAlarm_on == true
							&& alarm_rpm == true) {
	
						lrpm2_hysteresis++;
						if (lrpm2_hysteresis > rpm_hysteresis) {
							alarm_flag = true;
							if (speak_rpm == true) {
								doSpeakAlarm(rpm_speech_alert, speak_alert);
							}
							rpm2.setTextColor(getResources().getColor(
									R.color.errorColor));
						} else {
							rpm2.setTextColor(getResources()
									.getColor(R.color.White));
						}
					} else {
						rpm2.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (temp1 !=null) {
					if ((vtemp1 > mTemperatureAlarm) && mAlarm_on == true
							&& alarm_temperature == true) {
	
						ltemperature1_hysteresis++;
						if (ltemperature1_hysteresis > temperature_hysteresis) {
							alarm_flag = true;
							if (speak_temperature == true) {
								doSpeakAlarm(temp_speech_alert, speak_alert);
							}
							temp1.setTextColor(getResources().getColor(
									R.color.errorColor));
						} else {
							temp1.setTextColor(getResources().getColor(
									R.color.White));
						}
	
					} else {
						temp1.setTextColor(getResources().getColor(R.color.White));
					}
				}
				if (temp2 !=null) {
					if ((vtemp2 > mTemperatureAlarm) && mAlarm_on == true
							&& alarm_temperature == true) {
	
						ltemperature2_hysteresis++;
						if (ltemperature2_hysteresis > temperature_hysteresis) {
							alarm_flag = true;
							if (speak_temperature == true) {
								doSpeakAlarm(temp_speech_alert, speak_alert);
							}
							temp2.setTextColor(getResources().getColor(
									R.color.errorColor));
						} else {
							temp2.setTextColor(getResources().getColor(
									R.color.White));
						}
	
					} else {
						temp2.setTextColor(getResources().getColor(R.color.White));
					}
				}
				try {
					if ((mSound_on == true && alarm_flag == true)
							&& mSpeak_on == false) {
						if (mp.isPlaying() == false) {
							mp.start();
						}
					}
				} catch (Exception e) {
				}

				speak_alert = false;
				if ((SystemClock.uptimeMillis() - millis_for_vibrate) > 1200) {

					millis_for_vibrate = SystemClock.uptimeMillis();
					if (vibrate_on == true && alarm_flag == true) {
						long pattern[] = { 0, 200, 100 };
						v.vibrate(pattern, -1);
					}
				}

				if ((SystemClock.uptimeMillis() - millis_for_speech_alert) > speech_alert_int) {
					millis_for_speech_alert = SystemClock.uptimeMillis();
					speak_alert = true;
				}
			}

		};

		@Override
		public void run() {

			pause = false;
			millis = SystemClock.uptimeMillis();
			millis_for_vibrate = SystemClock.uptimeMillis();
			millis_for_speech = SystemClock.uptimeMillis();
			millis_for_speech_alert = SystemClock.uptimeMillis();
			while (true) {

				if (pause) {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}

					continue;
				}
				vlqi= spData.getLQI();
				vfirmware = spData.getFirmware();
				vtxstate = spData.getTxState();
				vtemp1 = spData.getTemp1();
				vtemp2 = spData.getTemp2();
				vtemp5 = spData.getTemp5();

				vvoltage = spData.getVolt1();
				vvoltage2 = spData.getVolt2();

				if (current_sensor_type.equals("C200")
						|| current_sensor_type.equals("UNILOG")
						|| current_sensor_type.equals("UNISENS")) {
					vcurrent = spData.getAmpC200();
					vused = (int) spData.getUsedC200();
				} else {
					vcurrent = spData.getAmpC50();
					vused = (int) spData.getUsedC50();
				}
				vpower = vvoltage * vcurrent;
				vair2 = spData.getHeight();
				if (metric == false) {
					vair2 = vair2 * 3.2808399;
				}
				switch (current_show_type) {
				case 2:
					if (currentMin == 0.0) {
						currentMin = vcurrent;
					}
					if (currentMin > vcurrent) {
						currentMin = vcurrent;
					}

					break;
				case 3:
					if (currentMax < vcurrent) {
						currentMax = vcurrent;
					}
					break;
				default:
					break;
				}
				switch (height_show_type) {
				case 2:
					if (heightMin == 0.0) {
						heightMin = vair2;
					}
					if (heightMin > vair2) {
						heightMin = vair2;
					}

					break;
				case 3:
					if (heightMax < vair2) {
						heightMax = vair2;
					}
					break;
				default:
					break;
				}
				switch (voltage_show_type) {
				case 2:
					if (voltageMin == 0.0) {
						voltageMin = vvoltage;
					}
					if (voltageMin > vvoltage) {
						voltageMin = vvoltage;
					}

					break;
				case 3:
					if (voltageMax < vvoltage) {
						voltageMax = vvoltage;
					}
					break;
				default:
					break;
				}
				switch (power_show_type) {
				case 2:
					if (powerMin == 0.0) {
						powerMin = vpower;
					}
					if (powerMin > vpower) {
						powerMin = vpower;
					}
					break;
				case 3:
					if (powerMax < vpower) {
						powerMax = vpower;
					}
					break;
				default:
					break;
				}
				vspeed = spData.getSpeed();
				valt = spData.getAltitude();
				vfuel = spData.getFuel();
				
				if (current_sensor_type.equals("UNILOG") || current_sensor_type.equals("UNISENS")) {
					vrpm1 = spData.getRPM1() * 10;
					vrpm2 = spData.getRPM2() * 10;
				} else {
					vrpm1 = spData.getRPM1();
					vrpm2 = spData.getRPM2();
				}
			
				vservo1 = spData.getServo1();
				vservo2 = spData.getServo2();
				vservo3 = spData.getServo3();
				vservo4 = spData.getServo4();
				vsP = spData.getsP();

				vair1 = spData.getCLimbRate();

				if (metric == false) {
					vspeed = (int) Math.round(vspeed * 0.621371192);
					valt = (int) Math.round(valt * 3.2808399);
					vair1 = vair1 * 3.2808399;
					vtemp1 = (int) Math.round(vtemp1 * 1.8 + 32);
					vtemp2 = (int) Math.round(vtemp2 * 1.8 + 32);
					vtemp5 = (int) Math.round(vtemp5 * 1.8 + 32);
					vsP = vsP * 0.621371192;
				}
				handler.post(sRunnable);

				try {

					Thread.sleep(100);

				} catch (InterruptedException e) {
				}
				spData.setUpdateState(false);
			}
		}

		public void resetUsedData() {
			spData.resetUsed();
		}

		public void resetClimbRateData() {
			spData.resetClimbRate();
		}

		public void resetHysteresis() {
			lvoltage_hysteresis = 0;
			lcurrent_hysteresis = 0;
			lrpm1_hysteresis = 0;
			lrpm2_hysteresis = 0;
			ltemperature1_hysteresis = 0;
			ltemperature2_hysteresis = 0;
			lpower_hysteresis = 0;
		}

		public void setPause(boolean pause) {

			this.pause = pause;

		}

		public void storeData() {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					writer = null;
				} catch (IOException e) {
				}
			}
		}

		public void getFile(boolean appendMode) {
			String strDir = "/hitec_log/live";
			if (mpxData == true) {
				//strDir = "/mlink_log/live";
			}
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + strDir);
			dir.mkdir();
			try {

				File f = new File(dir, "/" + log_file);
				writer = new FileWriter(f, appendMode);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}

		}

		@SuppressLint("SimpleDateFormat")
		public void copyFile() {
			String strDir = "/hitec_log/live";
			String mpxTrue = "";
			if (mpxData == true) {
				mpxTrue = "_ml_";
				//strDir = "/mlink_log/live";
			}
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + strDir);
			dir.mkdir();
			File f = new File(dir, "/" + log_file);
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
			String dateString = dateFormat.format(new Date());

			File b = new File(dir, "/" + dateString +mpxTrue+ "_" + log_file);

			if (f.exists()) {
				FileChannel src = null;
				try {
					src = new FileInputStream(f).getChannel();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				FileChannel dst = null;
				try {
					dst = new FileOutputStream(b).getChannel();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					dst.transferFrom(src, 0, src.size());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					src.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					dst.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void writeData() {
			if (writer != null) {
				try {
					writer.append(liveData);
				} catch (IOException e) {
				}
			}

		}

	}
}
