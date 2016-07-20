package de.rosche.spectraTelemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import android.content.Context;
import android.content.ContextWrapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME_VERSION = "version";
	public static final String PROGRAMM_VERSION = "1";
	private static String DB_PATH = "/data/data/de.rosche.spectraTelemetry/databases/";

	public static String DB_NAME = "hitecTelemetry20140225";
	//spublic static String DB_NAME = "hitecTelemetry20140231";
	private SQLiteDatabase myDataBase;

	public static String separation = ",";

	private final Context myContext;
	public int actualVersion = 0;

	public ArrayList<String> upgrades = new ArrayList<String>();

	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
		upgrades.clear();
		/*upgrades.add("create table if not exists profiles (id integer primary key autoincrement, profile text, description text)");
		upgrades.add("insert into profiles (id,profile,description) Values('1','default','default')");
		upgrades.add("insert into profiles (id,profile,description) Values('2','glider','glider')");
		upgrades.add("create table if not exists profile_data (id integer primary key autoincrement, ref_profile integer, mVoltageAlarm text, mUsedCap text, mCurrentAlarm text, mPowerAlarm text, mSpeedAlarm text, mFuelAlarm text, voltage_hysteresis text, current_hysteresis text, rpm_hysteresis text, temperature_hysteresis text, mTemperatureAlarm text, speech_volume text, vibrate_on text, voltage_speech_ack text, current_speech_ack text, height_speech_ack text, used_speech_ack text, speed_speech_ack text, rpm_speech_ack text, temp_speech_ack text, voltage_speech_label text, voltage_speech_unit text, height_speech_label text, height_speech_unit text, current_speech_label text, current_speech_unit text, used_speech_label text, used_speech_unit text, speed_speech_label text, speed_speech_unit text, rpm_speech_label text, rpm_speech_unit text, temp_speech_label text, temp_speech_unit text, mAltitudeAlarm text, mSound_on text, speech_on text, mlog_file text, mAlarm_on text, alarm_voltage text, alarm_used text, alarm_current text, alarm_speed text, alarm_fuel text, alarm_temperature text, alarm_altitude text, alarm_rpm text, alarm_power text, log_file text, bluetooth_device text, speech_intervall text, alert_tone text, label_rpm1 text, label_rpm2 text, label_temp1 text, label_temp2 text, unit_rpm1 text, unit_rpm2 text, unit_temp1 text, unit_temp2 text, current_sensor_type text)");
		upgrades.add("ALTER TABLE profile_data ADD COLUMN voltage_speed_unit");
		upgrades.add("ALTER TABLE profile_data DROP COLUMN voltage_speed_unit");
		upgrades.add("ALTER TABLE profile_data ADD COLUMN voltage_speech_unit text");
		upgrades.add("DROP table profile_data");
		upgrades.add("create table if not exists profile_data (id integer primary key autoincrement, ref_profile integer, mVoltageAlarm text, mUsedCap text, mCurrentAlarm text, mPowerAlarm text, mSpeedAlarm text, mFuelAlarm text, voltage_hysteresis text, current_hysteresis text, rpm_hysteresis text, temperature_hysteresis text, mTemperatureAlarm text, speech_volume text, vibrate_on text, voltage_speech_ack text, current_speech_ack text, height_speech_ack text, used_speech_ack text, speed_speech_ack text, rpm_speech_ack text, temp_speech_ack text, voltage_speech_label text, voltage_speech_unit text, height_speech_label text, height_speech_unit text, current_speech_label text, current_speech_unit text, used_speech_label text, used_speech_unit text, speed_speech_label text, speed_speech_unit text, rpm_speech_label text, rpm_speech_unit text, temp_speech_label text, temp_speech_unit text, mAltitudeAlarm text, mSound_on text, speech_on text, mlog_file text, mAlarm_on text, alarm_voltage text, alarm_used text, alarm_current text, alarm_speed text, alarm_fuel text, alarm_temperature text, alarm_altitude text, alarm_rpm text, alarm_power text, log_file text, bluetooth_device text, speech_intervall text, alert_tone text, label_rpm1 text, label_rpm2 text, label_temp1 text, label_temp2 text, unit_rpm1 text, unit_rpm2 text, unit_temp1 text, unit_temp2 text, current_sensor_type text)");
		upgrades.add("ALTER TABLE profile_data ADD COLUMN voltage_speech_unit text");
		upgrades.add("DROP table profile_data");
		upgrades.add("create table if not exists profile_data (id integer primary key autoincrement, ref_profile integer, mVoltageAlarm text, mUsedCap text, mCurrentAlarm text, mPowerAlarm text, mSpeedAlarm text, mFuelAlarm text, voltage_hysteresis text, current_hysteresis text, rpm_hysteresis text, temperature_hysteresis text, mTemperatureAlarm text, speech_volume text, vibrate_on text, voltage_speech_ack text, current_speech_ack text, height_speech_ack text, used_speech_ack text, speed_speech_ack text, rpm_speech_ack text, temp_speech_ack text, voltage_speech_label text, voltage_speech_unit text, height_speech_label text, height_speech_unit text, current_speech_label text, current_speech_unit text, used_speech_label text, used_speech_unit text, speed_speech_label text, speed_speech_unit text, rpm_speech_label text, rpm_speech_unit text, temp_speech_label text, temp_speech_unit text, mAltitudeAlarm text, mSound_on text, speech_on text, mlog_file text, mAlarm_on text, alarm_voltage text, alarm_used text, alarm_current text, alarm_speed text, alarm_fuel text, alarm_temperature text, alarm_altitude text, alarm_rpm text, alarm_power text, log_file text, bluetooth_device text, speech_intervall text, alert_tone text, label_rpm1 text, label_rpm2 text, label_temp1 text, label_temp2 text, unit_rpm1 text, unit_rpm2 text, unit_temp1 text, unit_temp2 text, current_sensor_type text)");
		upgrades.add("create table if not exists sensor_data (id integer primary key autoincrement, ref_profile integer, name text,label text, value text, unit text, threshold text, hysteresis text, type text, visible text)");
		upgrades.add("ALTER TABLE profile_data DROP COLUMN voltage_speed_unit");
		upgrades.add("DROP table profile_data");
		upgrades.add("create table if not exists sensor_data (id integer primary key autoincrement, ref_profile integer, name text,label text, value text, unit text, threshold text, hysteresis text, type text, visible text)");
		upgrades.add("ALTER TABLE profile_data ADD COLUMN voltage_speech_unit text");
		upgrades.add("create table if not exists profile_data (id integer primary key autoincrement, ref_profile integer, mVoltageAlarm text, mUsedCap text, mCurrentAlarm text, mPowerAlarm text, mSpeedAlarm text, mFuelAlarm text, voltage_hysteresis text, current_hysteresis text, rpm_hysteresis text, temperature_hysteresis text, mTemperatureAlarm text, speech_volume text, vibrate_on text, voltage_speech_ack text, current_speech_ack text, height_speech_ack text, used_speech_ack text, speed_speech_ack text, rpm_speech_ack text, temp_speech_ack text, voltage_speech_label text, voltage_speech_unit text, height_speech_label text, height_speech_unit text, current_speech_label text, current_speech_unit text, used_speech_label text, used_speech_unit text, speed_speech_label text, speed_speech_unit text, rpm_speech_label text, rpm_speech_unit text, temp_speech_label text, temp_speech_unit text, mAltitudeAlarm text, mSound_on text, speech_on text, mlog_file text, mAlarm_on text, alarm_voltage text, alarm_used text, alarm_current text, alarm_speed text, alarm_fuel text, alarm_temperature text, alarm_altitude text, alarm_rpm text, alarm_power text, log_file text, bluetooth_device text, speech_intervall text, alert_tone text, label_rpm1 text, label_rpm2 text, label_temp1 text, label_temp2 text, unit_rpm1 text, unit_rpm2 text, unit_temp1 text, unit_temp2 text, current_sensor_type text)");
		upgrades.add("ALTER TABLE profile_data ADD COLUMN voltage_speech_unit text");
		*/
	}

	public boolean upgradeDatabaseVersion(Context context) {
		for (int i = actualVersion; i < upgrades.size(); i++) {
			String sqlQuery = upgrades.get(i);
			try {
				SQLiteDatabase db = this.getWritableDatabase();

				db.execSQL(sqlQuery);

				db.close();

			} catch (SQLiteException e) {
				return false;
			}

		}
		return true;

	}

	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist

		} else {

			this.getReadableDatabase();

			try {
					copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	public boolean checkDatabaseVersion() {
		String sqlDataStore = "create table if not exists "
				+ TABLE_NAME_VERSION
				+ " (id integer primary key autoincrement,"
				+ " version text not null default '0')";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlDataStore);

			db.close();

		} catch (SQLiteException e) {
			Log.e("dbnonexistent","dbmonexistent");
			return false;
		}

		String sqlQuery = "SELECT version from version WHERE 1 ORDER BY ID DESC LIMIT 1";
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			Cursor c = db.rawQuery(sqlQuery, null);
			String version = "0";
			if (c.moveToFirst()) {
				do {

					version = c.getString(c.getColumnIndex("version"));

				} while (c.moveToNext());
			}
			c.close();
			db.close();
			actualVersion = Integer.parseInt(version);

			if (!version.equals(PROGRAMM_VERSION)) {
				try {
					db = this.getWritableDatabase();
					String sqlQueryDelete = "DELETE from version WHERE 1";
					sqlQuery = "INSERT INTO version  (version) VALUES ('"
							+ PROGRAMM_VERSION + "')";
					db.execSQL(sqlQueryDelete);
					db.execSQL(sqlQuery);
					db.close();
					Log.e("dbnonexistent","dberror selects"+sqlQueryDelete);
					return true;

				} catch (SQLiteException e) {
					Log.e("dbnonexistent","dberror selects");
					return false;
				}

			} else {

				return false;
			}

		} catch (SQLiteException e) {

			return false;
		}

	}

	public String getProfileDescriptionById(int filter) {

		String profile = "";

		String constraint = "";
		if (filter != 0) {
			constraint = " WHERE id='" + filter + "'";

		} else {

			return profile;

		}
		String sqlQuery = "SELECT description FROM profiles " + constraint
				+ " ORDER BY profile";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			Cursor c = db.rawQuery(sqlQuery, null);

			if (c.moveToFirst()) {
				do {
					profile = c.getString(c.getColumnIndex("description"));

				} while (c.moveToNext());
			}

			c.close();
			db.close();
		} catch (SQLiteException e) {

		}

		return profile;

	}

	public String getProfileById(int filter) {

		String profile = "";

		String constraint = "";
		if (filter != 0) {
			constraint = " WHERE id='" + filter + "'";

		} else {

			return profile;

		}
		String sqlQuery = "SELECT profile FROM profiles " + constraint
				+ " ORDER BY profile";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			Cursor c = db.rawQuery(sqlQuery, null);

			if (c.moveToFirst()) {
				do {
					profile = c.getString(c.getColumnIndex("profile"));

				} while (c.moveToNext());
			}

			c.close();
			db.close();
		} catch (SQLiteException e) {

		}

		return profile;

	}

	public Integer updateProfile(String[] params, String id) {

		String sqlQuery = "UPDATE profiles SET ";

		sqlQuery += "description='" + params[1] + "',";
		sqlQuery += "profile='" + params[0] + "'";
		sqlQuery += " WHERE id ='" + id + "'";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

		return Integer.parseInt(id);
	}

	public ArrayList<ArrayList<String>> loadProfile(int profile) {
		ArrayList<ArrayList<String>> prefs = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		String sqlQuery = "PRAGMA table_info('profile_data')";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);
			header.clear();

			if (c.moveToFirst()) {

				do {

					header.add(c.getString(c.getColumnIndex("name")));

				} while (c.moveToNext());
			}

			c.close();

			sqlQuery = "SELECT * FROM profile_data where ref_profile = '"
					+ String.valueOf(profile) + "'";
			c = db.rawQuery(sqlQuery, null);
			prefs.clear();
			if (c.moveToFirst()) {

				do {
					int length = header.size();

					for (int i = 0; i < length; i++) {
						String field = header.get(i);
						ArrayList<String> pref = new ArrayList<String>();
						String pref_name = field.toString();
						String pref_value = c
								.getString(c.getColumnIndex(field));
						pref.add(pref_name);
						pref.add(pref_value);
						prefs.add(pref);
					}
				} while (c.moveToNext());
			}

			c.close();

		} catch (SQLiteException e) {

		}

		return prefs;

	}

	public boolean saveProfile(int profile, ArrayList<ArrayList<String>> prefs) {

		ArrayList<String> header = new ArrayList<String>();
		String sqlQuery = "PRAGMA table_info('profile_data')";

		try {
			Integer nextval = getId("profile_data");
			String sqlQueryDelete = "DELETE FROM profile_data WHERE ref_profile ='"
					+ profile + "'";

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);
			header.clear();

			if (c.moveToFirst()) {

				do {

					header.add(c.getString(c.getColumnIndex("name")));

				} while (c.moveToNext());
			}

			c.close();

			db.execSQL(sqlQueryDelete);
			String prefix = "";
			StringBuilder columns = new StringBuilder();
			for (String column : header) {
				columns.append(prefix);
				prefix = ",";
				columns.append(column);
			}

			sqlQuery = "INSERT INTO profile_data (" + columns.toString()
					+ ") values (";

			StringBuilder sb = new StringBuilder(sqlQuery);
			sb.append("\"" + String.valueOf(nextval) + "\",");
			sb.append("\"" + String.valueOf(profile) + "\",");

			int length = prefs.size();
			prefix = "";
			for (int i = 0; i < length; i++) {
				ArrayList<String> pref = new ArrayList<String>();
				pref = prefs.get(i);
				String pref_name = pref.get(0).toString();
				String pref_value = pref.get(1).toString();
				if (header.contains(pref_name)) {
					sb.append(prefix);
					prefix = ",";
					sb.append("\"" + pref_value + "\"");
				} else {

				}
			}

			sb.append(")");

			db.execSQL(sb.toString());

			db.close();

		} catch (SQLiteException e) {

		}

		return true;

	}
	public  boolean InsertSensorData(int profileId, ArrayList<ArrayList<String>> sensorData) {
		String sqlQueryDelete = "DELETE FROM sensor_data where ref_profile="+String.valueOf(profileId);
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL(sqlQueryDelete);
			int length = sensorData.size();
			for (int i = 0; i < length; i++) {
				String sqlQuery = "INSERT INTO sensor_data (ref_profile,name,visible) values ";
				StringBuilder sb = new StringBuilder(sqlQuery);
				ArrayList<String> data = new ArrayList<String>();
				data = sensorData.get(i);
				String name = data.get(0).toString();
				String visible = data.get(1).toString();
				sb.append("(");
				sb.append("\"" + String.valueOf(profileId) + "\"");
				sb.append(",");
				sb.append("\"" + name + "\"");
				sb.append(",");
				sb.append("\"" + visible + "\"");
				sb.append(")");
				db.execSQL(sb.toString());
			}
			db.close();
		} catch (SQLiteException e) {
		}
		return true;
	}
	public ArrayList<ArrayList<String>> getSensorData(int profileId) {
		ArrayList<ArrayList<String>> sensorArray = new ArrayList<ArrayList<String>>();
		String sqlQuery = "SELECT name,label, visible FROM sensor_data where ref_profile="+String.valueOf(profileId);
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);
			sensorArray.clear();
			if (c.moveToFirst()) {
				do {
					ArrayList<String> data = new ArrayList<String>();
					data.add(c.getString(c.getColumnIndex("name")));
					data.add(c.getString(c.getColumnIndex("visible")));
					sensorArray.add(data);
				} while (c.moveToNext());
			}
			c.close();
			db.close();
		} catch (SQLiteException e) {
		}
		return sensorArray;
	}
	public ArrayList<ArrayList<String>> getProfiles() {
		ArrayList<ArrayList<String>> profile_array = new ArrayList<ArrayList<String>>();
		String sqlQuery = "SELECT id, profile FROM profiles where 1 ORDER BY profile";

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);
			profile_array.clear();

			if (c.moveToFirst()) {

				do {
					ArrayList<String> profile = new ArrayList<String>();
					profile.add(c.getString(c.getColumnIndex("id")));
					profile.add(c.getString(c.getColumnIndex("profile")));
					profile_array.add(profile);

				} while (c.moveToNext());
			}

			c.close();

			db.close();

		} catch (SQLiteException e) {

		}
		return profile_array;
	}
	
	private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
	    File dbFile=context.getDatabasePath(dbName);
	    Log.e("MLISTE", "+++ test"+dbFile);
	    return dbFile.exists();
	}
	
	public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			checkDB.execSQL("SELECT * FROM profile_data WHERE id=1");

		} catch (SQLiteException e) {
			checkDB.close();
			return false;
		}
		checkDB.close();

		return true;
		
	}

	private void copyDataBase() throws IOException {

		try {
			InputStream myInput = myContext.getAssets().open(DB_NAME);

			String outFileName = DB_PATH + DB_NAME;

			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
			int length;

			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (Exception e) {
			Log.e("exception","exception:"+e);
			;
		}

	}

	public boolean importDataBaseComplete(Context context) throws IOException {

		try {

			File sdCard = Environment.getExternalStorageDirectory();
			String dir = sdCard.getAbsolutePath() + "/flight_log/import/";
			String inFilename = "model_data.csv";

			// testing read from asset
			// InputStream myInput = myContext.getAssets().open(inFilename);
			// InputStreamReader r = new InputStreamReader(myInput);
			// BufferedReader br = new BufferedReader(r);
			// testing

			FileReader fr = new FileReader(dir + inFilename);
			BufferedReader br = new BufferedReader(fr);

			String data = "";
			String tableName = "flightlog";
			String columns = "id,name,typ,beschreibung,datum,spannweite,länge,gewicht,rcdata,ausstattung,status";
			String InsertString1 = "INSERT INTO " + tableName + " (" + columns
					+ ") values (";
			String InsertString2 = ")";

			while ((data = br.readLine()) != null) {

				Integer nextval = getId("flightlog");

				String[] sarray = data.split(separation);
				String sqlQuery = "";
				if (sarray.length == 14) {
					StringBuilder sb = new StringBuilder(InsertString1);
					String nameModel = sarray[1].replaceAll("\"", "").trim()
							.toLowerCase(Locale.GERMANY);
					if (!nameModel.equals("name")) {
						sb.append("\"" + String.valueOf(nextval) + "\",");
						sb.append("\"" + sarray[1].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[2].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[3].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[4].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[5].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[6].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[7].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[8].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[9].replaceAll("\"", "") + "\",");
						sb.append("\"" + sarray[10].replaceAll("\"", "") + "\"");
						sb.append("\"" + sarray[11].replaceAll("\"", "") + "\"");
						sb.append("\"" + sarray[12].replaceAll("\"", "") + "\"");
						sb.append(InsertString2);

						sqlQuery = sb.toString();

						try {
							SQLiteDatabase db = this.getWritableDatabase();
							db.execSQL(sqlQuery);
							db.close();

						} catch (SQLiteException e) {

						}

						Toast.makeText(
								context,
								"Modell " + sarray[1].replaceAll("\"", "")
										+ " importiert.", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(
							context,
							"Error: Datensatzlänge nicht korrekt! L:"
									+ String.valueOf(sarray.length),
							Toast.LENGTH_SHORT).show();
				}

			}

			br.close();
			fr.close();
			File file = new File(dir + inFilename);
			file.delete();

			return true;

		} catch (IOException e) {

			Toast.makeText(context,
					"ERROR:Import Model der Datenbank fehlgeschlagen!" + e,
					Toast.LENGTH_LONG).show();

			return false;
		}

	}

	public boolean exportDataBaseComplete() throws IOException {

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"ddMMyyyyHHmmss", Locale.GERMANY);
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/flight_log/export");
			dir.mkdir();

			String outFilename = dateFormat.format(new Date()) + "_"
					+ "model_data_complete.csv";

			File f = new File(dir, outFilename);
			f.createNewFile();

			OutputStream myOutput = new FileOutputStream(f);
			PrintStream printStream = new PrintStream(myOutput);

			String sqlQuery = "SELECT f.*,i.image_path FROM flightlog f join images i on i.model_id=f.id ORDER BY f.id ASC";
			String record = "";
			String header = "";

			header = '"' + "MODELL_ID" + '"' + separation;
			header += '"' + "NAME" + '"' + separation;
			header += '"' + "TYP" + '"' + separation;
			header += '"' + "ART" + '"' + separation;
			header += '"' + "BESCHREIBUNG" + '"' + separation;
			header += '"' + "HERSTELLER" + '"' + separation;
			header += '"' + "DATUM" + '"' + separation;
			header += '"' + "SPANNWEITE" + '"' + separation;
			header += '"' + "LAENGE" + '"' + separation;
			header += '"' + "GEWICHT" + '"' + separation;
			header += '"' + "RCEINSTELLUNG" + '"' + separation;
			header += '"' + "AUSSTATTUNG" + '"' + separation;
			header += '"' + "STATUS" + '"' + separation;
			header += '"' + "BILD_PFAD" + '"' + "\n";

			printStream.print(header);

			try {
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor c = db.rawQuery(sqlQuery, null);

				if (c.moveToFirst()) {
					do {

						record = c.getString(c.getColumnIndex("id"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("name")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("typ")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("art")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("beschreibung"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("hersteller"))
								+ '"';
						record += separation
								+ c.getString(c.getColumnIndex("datum"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("spannweite"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("länge")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("gewicht"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("rcdata")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("ausstattung"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("status")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("image_path"))
								+ '"';
						record += '\n';
						printStream.print(record);
						Integer model_id = Integer.parseInt(c.getString(c
								.getColumnIndex("id")));
						ArrayList<String> flights = new ArrayList<String>();

						// *********************
						// insert flights
						flights = exportDataBaseFlightsById(model_id);
						int length = flights.size();
						if (length > 2) {
							String prefix = "\"\"" + separation + "\"\""
									+ separation;
							String postfix = separation + prefix + prefix
									+ prefix + "\n";
							for (int i = 0; i < length; i++) {
								String flight = "";
								if (i == 0 || i == 1) {
									flight = flights.get(i);
									flight = prefix + flight + postfix;

								} else {
									flight = flights.get(i);
									flight = prefix + String.valueOf(i)
											+ separation + flight + postfix;
								}
								printStream.print(flight);
							}
						}

						// ********************

					} while (c.moveToNext());
				}

				c.close();

				db.close();

			} catch (SQLiteException e) {

			}

			printStream.flush();
			printStream.close();
			myOutput.flush();
			myOutput.close();

			return true;
		} catch (IOException e) {

			return false;
		}

	}

	public boolean exportDataBase() throws IOException {

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"ddMMyyyyHHmmss", Locale.GERMANY);
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/flight_log/export");
			dir.mkdir();

			String outFilename = dateFormat.format(new Date()) + "_"
					+ "model_data.csv";

			File f = new File(dir, outFilename);
			f.createNewFile();

			OutputStream myOutput = new FileOutputStream(f);
			PrintStream printStream = new PrintStream(myOutput);

			String sqlQuery = "SELECT f.*,i.image_path FROM flightlog f join images i on i.model_id=f.id";
			String record = "";
			String header = "";

			header = '"' + "MODELL_ID" + '"' + separation;
			header += '"' + "NAME" + '"' + separation;
			header += '"' + "TYP" + '"' + separation;
			header += '"' + "ART" + '"' + separation;
			header += '"' + "BESCHREIBUNG" + '"' + separation;
			header += '"' + "HERSTELLER" + '"' + separation;
			header += '"' + "DATUM" + '"' + separation;
			header += '"' + "SPANNWEITE" + '"' + separation;
			header += '"' + "LAENGE" + '"' + separation;
			header += '"' + "GEWICHT" + '"' + separation;
			header += '"' + "RCEINSTELLUNG" + '"' + separation;
			header += '"' + "AUSSTATTUNG" + '"' + separation;
			header += '"' + "STATUS" + '"' + separation;
			header += '"' + "BILD_PFAD" + '"' + "\n";

			printStream.print(header);

			try {
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor c = db.rawQuery(sqlQuery, null);

				if (c.moveToFirst()) {
					do {

						record = c.getString(c.getColumnIndex("id"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("name")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("typ")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("art")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("beschreibung"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("hersteller"))
								+ '"';
						record += separation
								+ c.getString(c.getColumnIndex("datum"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("spannweite"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("länge")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("gewicht"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("rcdata")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("ausstattung"))
								+ '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("status")) + '"';
						record += separation + '"'
								+ c.getString(c.getColumnIndex("image_path"))
								+ '"';
						record += "\n";

						printStream.print(record);

					} while (c.moveToNext());
				}

				c.close();

				db.close();

			} catch (SQLiteException e) {

			}

			printStream.flush();
			printStream.close();
			myOutput.flush();
			myOutput.close();

			return true;
		} catch (IOException e) {

			return false;
		}

	}

	public ArrayList<String> exportDataBaseFlightsById(Integer model_id) {

		ArrayList<String> flights = new ArrayList<String>();
		String constraint = "";
		if (model_id > 0) {
			constraint = " AND f.model_id=" + String.valueOf(model_id)
					+ " ORDER BY f.id ASC";
		} else {
			constraint = " ORDER BY f.id ASC";
		}
		String sqlQuery = "SELECT f.model_id,m.name,f.date,f.description FROM flights f join flightlog m on f.model_id=m.id "
				+ constraint;
		String record = "";
		String header = "";

		header = "\"Fluege\"" + separation;
		header += "\"\"" + separation;
		header += "\"\"";
		flights.add(header);

		header = "\"Nr.\"" + separation;
		header += "\"Datum\"" + separation;
		header += "\"Bericht\"";

		flights.add(header);

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);
			if (c.moveToFirst()) {
				do {
					record = c.getString(c.getColumnIndex("date"));
					record += separation + "\""
							+ c.getString(c.getColumnIndex("description"))
							+ "\"";

					flights.add(record);

				} while (c.moveToNext());
			}

			c.close();

			db.close();

		} catch (SQLiteException e) {

		}
		return flights;

	}

	public boolean exportDataBaseFlights(Integer model_id) throws IOException {

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"ddMMyyyyHHmmss", Locale.GERMANY);
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/flight_log/export");
			dir.mkdir();

			String outFilename = dateFormat.format(new Date()) + "_"
					+ "flight_data.csv";

			File f = new File(dir, outFilename);
			f.createNewFile();

			OutputStream myOutput = new FileOutputStream(f);
			PrintStream printStream = new PrintStream(myOutput);
			String constraint = "";
			if (model_id > 0) {
				constraint = " AND f.model_id=" + String.valueOf(model_id);
			} else {
				constraint = "";
			}
			String sqlQuery = "SELECT f.model_id,m.name,f.date,f.description FROM flights f join flightlog m on f.model_id=m.id "
					+ constraint;
			String record = "";
			String header = "";

			header = '"' + "MODELL_ID" + '"' + separation;
			header += '"' + "NAME" + '"' + separation;
			header += '"' + "DATUM" + '"' + separation;
			header += '"' + "EINTRAG" + '"' + "\n";

			printStream.print(header);

			try {
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor c = db.rawQuery(sqlQuery, null);

				if (c.moveToFirst()) {
					do {

						record = c.getString(c.getColumnIndex("model_id"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("name")) + '"';
						record += separation
								+ c.getString(c.getColumnIndex("date"));
						record += separation + '"'
								+ c.getString(c.getColumnIndex("description"))
								+ '"';
						record += "\n";

						printStream.print(record);

					} while (c.moveToNext());
				}

				c.close();

				db.close();

			} catch (SQLiteException e) {

			}

			printStream.flush();
			printStream.close();
			myOutput.flush();
			myOutput.close();

			return true;
		} catch (IOException e) {

			return false;
		}

	}

	public boolean backupDataBase() throws IOException {

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"ddMMyyyyHHmmss", Locale.GERMANY);
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/hitec_log/backup");
			dir.mkdir();
			String inFileName = DB_PATH + DB_NAME;
			String outFilename = dateFormat.format(new Date()) + "_" + DB_NAME;

			File f = new File(dir, outFilename);
			f.createNewFile();
			InputStream myInput = new FileInputStream(inFileName);
			OutputStream myOutput = new FileOutputStream(f);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
			return true;
		} catch (IOException e) {

			return false;
		}

	}

	public Integer insertProfile(String[] params) {
		Integer nextval = getId("profiles");
		String sqlQuery = "INSERT INTO profiles (id,profile,description) VALUES ('"
				+ nextval + "'";
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {

				sqlQuery += ",'" + params[i] + "'";
			} else {
				sqlQuery += ",''";
			}

		}
		sqlQuery += ")";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

		return nextval;
	}

	public Integer insert(String[] params) {
		Integer nextval = getId("flightlog");
		String sqlQuery = "INSERT INTO flightlog (id,name,typ,beschreibung,"
				+ "datum,spannweite,länge,gewicht,rcdata,ausstattung,status,art,hersteller) VALUES ('"
				+ nextval + "'";
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {

				sqlQuery += ",'" + params[i] + "'";
			} else {
				sqlQuery += ",''";
			}

		}
		sqlQuery += ")";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

		return nextval;
	}

	public void deleteProfileFromDB(Integer profileId) {

		String sqlQuery = "DELETE FROM profiles WHERE id='"
				+ String.valueOf(profileId) + "'";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

	}

	public Integer insertImage(Integer id, String image_path) {
		Integer nextval = getId("images");
		String sqlQueryDelete = "DELETE FROM images WHERE model_id ='" + id
				+ "'";

		String sqlQuery = "INSERT INTO images (id,model_id,image_path) VALUES ('"
				+ nextval
				+ "'"
				+ ",'"
				+ String.valueOf(id)
				+ "',"
				+ "'"
				+ image_path + "'" + ")";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQueryDelete);
			db.execSQL(sqlQuery);

			db.close();
			return nextval;

		} catch (SQLiteException e) {

			return null;

		}

	}

	public String getImage(Integer model_id) {
		String sqlQuery = "SELECT image_path FROM images WHERE model_id ='"
				+ model_id + "' ORDER BY id DESC";
		String image_path = "";

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);

			if (c.moveToFirst()) {
				do {

					image_path = c.getString(c.getColumnIndex("image_path"));

				} while (c.moveToNext());
			}

			c.close();

			db.close();

		} catch (SQLiteException e) {

		}
		return image_path;
	}

	public void setSeparation(String character) {
		separation = character;

	}

	public void resetImageDB() {

		String sqlQueryDelete = "DELETE FROM images WHERE 1";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQueryDelete);

			db.close();

		} catch (SQLiteException e) {

		}
	}

	public Integer updateImage(Integer model_id, String image_path) {

		Integer nextval = getId("images");
		String sqlQueryDelete = "DELETE FROM images WHERE model_id ='"
				+ model_id + "'";

		String sqlQuery = "INSERT INTO images (id,model_id,image_path) VALUES ('"
				+ nextval
				+ "'"
				+ ",'"
				+ String.valueOf(model_id)
				+ "',"
				+ "'"
				+ image_path + "'" + ")";

		String sqlSearchImage = "SELECT * FROM images WHERE model_id ='"
				+ model_id + "'";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			Cursor c = db.rawQuery(sqlSearchImage, null);
			File sdCard = Environment.getExternalStorageDirectory();

			if (c.moveToFirst()) {
				do {
					String oldImagePath = c.getString(c
							.getColumnIndex("image_path"));
					;
					String f = sdCard.getAbsolutePath() + "/" + oldImagePath;

					File file = new File(f);
					if (file.exists()) {
						if (!oldImagePath.equals(image_path))
							file.delete();
					}

				} while (c.moveToNext());
			}

			c.close();

			db.execSQL(sqlQueryDelete);
			db.execSQL(sqlQuery);

			db.close();
			return model_id;

		} catch (SQLiteException e) {

			return null;
		}
	}

	public String delete(String id) {
		String[] result_array;
		result_array = id.split(":");
		id = result_array[0].trim();

		String sqlQuery = "DELETE FROM flightlog WHERE id='" + id + "'";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

		return id;
	}

	public Integer update(String[] params, String id) {

		String sqlQuery = "UPDATE flightlog SET ";

		sqlQuery += "name='" + params[0] + "',";
		sqlQuery += "typ='" + params[1] + "',";
		sqlQuery += "beschreibung='" + params[2] + "',";
		sqlQuery += "datum='" + params[3] + "',";
		sqlQuery += "spannweite='" + params[4] + "',";
		sqlQuery += "länge='" + params[5] + "',";
		sqlQuery += "gewicht='" + params[6] + "',";
		sqlQuery += "rcdata='" + params[7] + "',";
		sqlQuery += "ausstattung='" + params[8] + "',";
		sqlQuery += "status='" + params[9] + "',";
		sqlQuery += "art='" + params[10] + "',";
		sqlQuery += "hersteller='" + params[11] + "'";
		sqlQuery += " WHERE id ='" + id + "'";

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL(sqlQuery);

			db.close();

		} catch (SQLiteException e) {

		}

		return Integer.parseInt(id);
	}

	private int getId(String table) {
		String sqlQuery = "SELECT max(id) as nextval, count(*) as counts FROM "
				+ table;
		Integer id = 0;
		Integer counts = 0;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor c = db.rawQuery(sqlQuery, null);

			if (c.moveToFirst()) {
				do {
					counts = Integer.parseInt(c.getString(c
							.getColumnIndex("counts")));
					if (counts > 0) {

						id = Integer.parseInt(c.getString(c
								.getColumnIndex("nextval")));
					} else {
						id = 0;
					}

				} while (c.moveToNext());
			}

			c.close();

			db.close();

		} catch (SQLiteException e) {

		}

		return id + 1;

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}