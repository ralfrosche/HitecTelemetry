package de.rosche.spectraTelemetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpectraData {
	private static int countAlt = 0;
	private static final int cPACKETTYPEINDEX = 3;
	private static final int cVOLT1H = 4;
	private static final int cVOLT1L = 5;
	private static final int cLATSECH = 6;
	private static final int cLATSECL = 7;
	private static final int cLATMINH = 8;
	private static final int cLATMINL = 9;
	private static final int cSECOND = 10;
	private static final int cLONSECH = 11;
	private static final int cLONSECL = 12;
	private static final int cLONMINH = 13;
	private static final int cLONMINL = 14;
	private static final int cTEMP2 = 15;
	private static final int cSPEEDH = 16;
	private static final int cSPEEDL = 17;
	private static final int cALTH = 18;
	private static final int cALTL = 19;
	private static final int cTEMP1 = 20;
	private static final int cFUEL = 21;
	private static final int cRPM1L = 4;
	private static final int cRPM1H = 5;
	private static final int cRPM2L = 6;
	private static final int cRPM2H = 7;
	private static final int cYEAR = 8;
	private static final int cMONTH = 9;
	private static final int cDAY = 10;
	private static final int cHOUR = 11;
	private static final int cMINUTE = 12;
	private static final int cCOURSEH = 13;
	private static final int cCOURSEL = 14;
	private static final int cGPSCOUNT = 15;
	private static final int cTEMP3 = 16;
	private static final int cTEMP4 = 17;
	private static final int cVOLT2L = 18;
	private static final int cVOLT2H = 19;
	private static final int cAMP1L = 20;
	private static final int cAMP1H = 21;
	private static final int cSERVO1 = 4;
	private static final int cSERVO2 = 5;
	private static final int cSERVO3 = 6;
	private static final int cSERVO4 = 7;
	private static final int cTEMP5 = 19;
	private static final int cP1H = 11;
	private static final int cP1L = 12;
	private static final int cair1L = 15;
	private static final int cair1H = 14;
	private static final int cair2L = 17;
	private static final int cair2H = 16;
	private static final float cVOLTCONVERSION = 28.5714f;
	private int Seconds = 0;
	private boolean UpdateFired = false;
	private int RPM1 = 0;
	private int RPM2 = 0;
	private int LQI = 0;
	private String FIRMWARE = "";
	private int switchStates = 0; 
	private int TXSTATE = 0;
	private Date GPSDate = null;
	private double GPSCourse = 0;
	private int GPSCount = 0;
	private int Temp3 = -40;
	private int Temp4 = -40;
	private int SERVO1 = 0;
	private int SERVO2 = 0;
	private int SERVO3 = 0;
	private int SERVO4 = 0;
	private float P1 = 0.0f;
	private float air1 = 0.0f;
	private float air2 = 0.0f;
	private float airBase = 0.0f;
	private float airClimbRateBase = 0.0f;
	private float Volt1 = 0.0f;
	private float Volt2 = 0.0f;
	private float AmpC50 = 0.0f;
	private float AmpC200 = 0.0f;
	private float UsedAmpC200;
	private float UsedAmpC50;
	private double Latitude = 0.0;
	private double Longitude = 0.0;
	private String LatitudeDMS = "";
	private String LongitudeDMS = "";
	private int Temp2 = -40;
	private int Speed = 0;
	private int Altitude = 0;
	private int distance = 0;
	private boolean hasGps = false;
	private int AltitudeOffset = 0;
	private int Temp1 = -40;
	private int Temp5 = -40;
	private int Fuel = 0;
	private float Power = 0.0f;
	private boolean mpxData = false;
	private int[] data2D = new int[22];
	private int[] data2B = new int[22];
	private int[] data50 = new int[22];

	public SpectraData() {
	}

	public void UpdateData(int[] data, boolean mpx_data) {
		mpxData = mpx_data; 
		if (data != null) {
			if (mpxData == false) {
				if (data.length >= 22) {
					switch (data[cPACKETTYPEINDEX]) {
					case 0x2D:
						Decode2DPacket(data);
						setUpdateState(true);
						break;
					case 0x2B:
						Decode2BPacket(data);
						setUpdateState(true);
						break;
					case 0x08:
						Decode08Packet(data);
						setUpdateState(true);
						break;
					default:
						break;
					}
				}
			} else {
				if (data.length == 20) {
					DecodeMPXPacket(data);
					setUpdateState(true);
				}
			}
		}
	}
	private void setMPXData(int sensorValue, int sensorClass, int sensorAdr) {
		switch (sensorClass){
		case 0:
			break;
		case 1:
			if (sensorAdr == 0) {
				Volt2 = (float) (sensorValue / 10f);
			} else {
				Volt1 = (float) (sensorValue / 10f);
			}
			break;
		case 2:
			AmpC50 = (float) (sensorValue / 10f);
			AmpC200 = (float) (sensorValue / 10f);
			break;
		case 3:
			airClimbRateBase = (float) (sensorValue / -10f);
			break;
		case 4:
			Speed = (int) (sensorValue / 10f);
			break;
		case 5:
			RPM1 = sensorValue * 100;
			break;
		case 6:
			Temp1 = (int) (sensorValue / 10f);
			break;
		case 7:
			GPSCourse = (double) (sensorValue / 10.0);
			break;
		case 8:
			if (sensorAdr >= 9) {
				hasGps = true;
				distance = sensorValue;
			} else {
				airBase = (float) (sensorValue * -10f);
				Altitude = sensorValue;
			}
			break;
		case 9:
			Fuel = sensorValue;
			break;
		case 10:
			LQI = sensorValue;
			break;
		case 11:
			// verbrauch
			break;
		case 12:
			// flüssigkeit
			break;
		case 13:
			// long distance
			break;
		default:
			break;
		}
	}
	private void DecodeMPXPacket(int[] data) {
		/*Map<Integer, String> SensorClasses = new HashMap<Integer, String>(); 
		String[] Units = {"","V","A","m/s","km/h","1/min","°C","Grad","m","%","%","mA","ml","km","",""};
		for (int i = 0; i<=15; i++) {
			SensorClasses.put(i ,Units[i]);
		}
		*/
		/*FIRMWARE = String.valueOf(Character.toChars(data[2])) +String.valueOf(Character.toChars(data[3]))+String.valueOf(Character.toChars(data[4]));
		int H = (data[5] & 0xff) * 256;
		int L = (data[6] & 0xff);
		switchStates = H + L;
		*/
		int H = 0;
		int L = 0;
		TXSTATE = data[7];
		int sensorAdr = (data[8] >> 4) & 0xF;
		int sensorClass = data[8] & 0xF;
		
		H = (data[10] & 0xff) * 256;
		L = (data[9] & 0xff);
		int sensorValue = H + L;
		sensorValue = sensorValue >> 1;
		if (data[10] >> 7 == 1) {
			sensorValue ^=  0x7fff;
			sensorValue += 1;
			sensorValue *=-1;
		} 
		setMPXData(sensorValue, sensorClass, sensorAdr);

		sensorAdr = (data[11] >> 4) & 0xF;
		sensorClass = data[11] & 0xF;
		
		H = (data[13] & 0xff) * 256;
		L = (data[12] & 0xff);
		sensorValue = H + L;
		sensorValue = sensorValue >> 1;
		if (data[13] >> 7 == 1) {
			sensorValue ^=  0x7fff;
			sensorValue += 1;
			sensorValue *=-1;
		} 
		setMPXData(sensorValue, sensorClass, sensorAdr);
				
	}
	private boolean calcChecksum(int[] data) {
		byte checksum = 0x02;
		for (int i = 1; i < 18;i++) {
			checksum -= data[i]; 
		}
		checksum -= 0x03;
		if (checksum == (byte)data[18]) {
			return true;
		} else {
			return false;
		}
		
		
	}
	private void Decode08Packet(int[] data) {
		SERVO1 = data[cSERVO1];
		SERVO2 = data[cSERVO2];
		SERVO3 = data[cSERVO3];
		SERVO4 = data[cSERVO4];
		Temp5 = data[cTEMP5] - 40;

		int P1H = (data[cP1H] & 0xff) * 256;
		int P1L = (data[cP1L] & 0xff);

		P1 = ((float) P1H + (float) P1L) * 1f;

		int airH = (data[cair1H] & 0xff) * 256;
		int airL = (data[cair1L] & 0xff);

		air1 = ((float) airH + (float) airL) * 1f;

		if (air1 > 0.0f && airBase == 0.0f)
			airBase = air1;

		airH = (data[cair2H] & 0xff) * 256;
		airL = (data[cair2L] & 0xff);

		air2 = ((float) airH + (float) airL) * 1f;

		if (air2 > 0.0f && airClimbRateBase == 0.0f) {
			airClimbRateBase = air1 / 10 - air2 / 10;
		}

	}

	private void Decode2BPacket(int[] data) {

		int convH = (data[cRPM1H] & 0xff) * 256;
		int convL = (data[cRPM1L] & 0xff);
		RPM1 = convH + convL;

		convH = (data[cRPM2H] & 0xff) * 256;
		convL = (data[cRPM2L] & 0xff);
		RPM2 = convH + convL;

		String sdate = "20" + data[cYEAR] + "/" + data[cMONTH] + "/"
				+ data[cDAY] + " " + data[cHOUR] + ":" + data[cMINUTE] + ":"
				+ Seconds;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy/MM/dd kk:mm:ss", Locale.GERMANY);
			GPSDate = formatter.parse(sdate);
		} catch (Exception ex) {
			GPSDate = null;
		}

		convH = (data[cCOURSEH] & 0xff) * 256;
		convL = (data[cCOURSEL] & 0xff);
		GPSCourse = convH + convL;

		GPSCount = data[cGPSCOUNT];
		Temp3 = data[cTEMP3] - 40;
		Temp4 = data[cTEMP4] - 40;

		convH = (data[cVOLT2H] & 0xff) * 256;
		convL = (data[cVOLT2L] & 0xff);
		Volt2 = ((float) convH + (float) convL) / cVOLTCONVERSION;

		convH = (data[cAMP1H] & 0xff) * 256;
		convL = (data[cAMP1L] & 0xff);
		int conv = convH + convL;

		AmpC50 = (conv - 180f) / 14f;
		AmpC200 = (conv - 150f) / 4f;
		if (AmpC50 < 0) {
			AmpC50 = 0;
		}
		if (AmpC200 < 0) {
			AmpC200 = 0;
		}
		Power = conv;

	}

	private void Decode2DPacket(int[] data) {
		int convH = (data[cVOLT1H] & 0xff) * 256;
		int convL = (data[cVOLT1L] & 0xff);
		int conv = convH + convL;
		Volt1 = conv / cVOLTCONVERSION;
		try {
			short isecondsH = (short) ((data[cLATSECH] & 0xff) * 256);
			short isecondsL = (short) (data[cLATSECL] & 0xff);
			short iseconds = (short) (isecondsH + isecondsL);
			float seconds = iseconds / 166.7f;
			seconds = (float) Math.round(seconds * 1000) / 1000;
			short degminH = (short) ((data[cLATMINH] & 0xff) * 256);
			short degminL = (short) (data[cLATMINL] & 0xff);
			short degmin = (short) (degminH+ degminL);
			String degminstring = Short.toString(degmin);
			String degstring = degminstring.substring(0,
					degminstring.length() - 2);
			int degrees = Integer.parseInt(degstring);
			String minstring = degminstring
					.substring(degminstring.length() - 2);
			int minutes = Integer.parseInt(minstring);
			if (degmin > 0) {
				LatitudeDMS = degstring + ":" + minstring + ":"
						+ Float.toString(seconds) + "N";
				Latitude = (((minutes * 60f) + seconds) / 3600f) + degrees;
			} else {
				LatitudeDMS = Integer.toString(degrees * -1) + ":" + minstring
						+ ":" + Float.toString(seconds) + "S";
				Latitude = (((minutes * 60f) + seconds) / (3600f * -1))
						+ degrees;
			}
			iseconds = (short) ((data[cLONSECH] << 8) | (data[cLONSECL] & 0xff));
			seconds = iseconds / 166.7f;
			seconds = (float) Math.round(seconds * 1000) / 1000;
			degmin = (short) ((data[cLONMINH] << 8) | (data[cLONMINL] & 0xff));
			degminstring = Short.toString(degmin);
			degstring = degminstring.substring(0, degminstring.length() - 2);
			degrees = Integer.parseInt(degstring);
			minstring = degminstring.substring(degminstring.length() - 2);
			minutes = Integer.parseInt(minstring);
			if (degmin > 0) {
				LongitudeDMS = degstring + ":" + minstring + ":"
						+ Float.toString(seconds) + "E";
				Longitude = (((minutes * 60f) + seconds) / 3600f) + degrees;
			} else {
				LongitudeDMS = Integer.toString(degrees * -1) + ":" + minstring
						+ ":" + Float.toString(seconds) + "W";
				Longitude = (((minutes * 60f) + seconds) / (3600f * -1))
						+ degrees;
			}
			Seconds = data[cSECOND];

		} catch (Exception ex) {
			LongitudeDMS = "0:0:0.0E";
			Longitude = 0.0f;
			Latitude = 0.0f;
			LatitudeDMS = "0:0.0.0N";
		}
		Temp2 = data[cTEMP2] - 40;
		convH = (data[cSPEEDH] & 0xff) * 256;
		convL = (data[cSPEEDL] & 0xff);
		Speed = convH + convL;
		convH = (data[cALTH] & 0xff) * 255;
		convL = (data[cALTL] & 0xff);
		Altitude = convH + convL;
		if (Altitude > 0 && countAlt <10) {
			AltitudeOffset += Altitude;
			countAlt++;
		}
		if (countAlt == 10) {
			AltitudeOffset /=10;
		}
		Temp1 = data[cTEMP1] - 40;
		Fuel = (25 * data[cFUEL]);
	}

	public void ShiftLeftEight(int value) {

	}

	public int getRPM1() {
		return RPM1;
	}

	public int getRPM2() {
		return RPM2;
	}

	public Date getGPSDate() {
		return GPSDate;
	}

	public double getGPSCourse() {
		return GPSCourse;
	}

	public int getGPSCount() {
		return GPSCount;
	}

	public int getTemp1() {
		return Temp1;
	}

	public int getTemp5() {
		return Temp5;
	}

	public int getTemp2() {
		return Temp2;
	}

	public double getServo1() {
		return SERVO1 / 10.0;
	}

	public double getServo2() {
		return SERVO2 / 10.0;
	}

	public double getServo3() {
		return SERVO3 / 10.0;
	}

	public double getServo4() {
		return SERVO4 / 10.0;
	}

	public boolean getUpdateState() {
		return UpdateFired;
	}

	public void setUpdateState(final boolean state) {
		UpdateFired = state;
	}

	public int getTemp3() {
		return Temp3;
	}

	public int getTemp4() {
		return Temp4;
	}

	public double getVolt1() {
		return Volt1;
	}

	public double getVolt2() {
		return Volt2;
	}

	public double getsP() {
		return P1;
	}

	public double getair1() {
		return air1 / 10.0;
	}

	public double getair2() {
		return air2 / 10.0;
	}

	public double getCLimbRate() {
		return (air1 / 10.0 - air2 / 10.0) - airClimbRateBase;
	}

	public double getHeight() {
		return air1 / 10.0 - airBase / 10.0;
	}
	public double getLatitude() {
		return Latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public int getSpeed() {
		return Speed;
	}
	public int getAltitude() {
		Altitude = Altitude - AltitudeOffset;
		if (Altitude < 0)
			Altitude = 0;
		return Altitude;
	}
	public int getDistance() {
		return distance;
	}
	public boolean hasGps() {
		return hasGps;
	}
	public int getFuel() {
		return Fuel;
	}
	public double getAmpC200() {
		return AmpC200;
	}
	public double getAmpC50() {
		return AmpC50;
	}
	public double getUsedC200() {
		return UsedAmpC200;
	}
	public double getUsedC50() {
		return UsedAmpC50;
	}
	public int getTxState() {
		return TXSTATE;
	}
	public int getLQI() {
		return LQI;
	}
	public String getFirmware() {
		return FIRMWARE;
	}
	public int getswitchStates() {
		return switchStates;
	}
	public void addUsedC200(double value) {
		UsedAmpC200 += value;
	}
	public void addUsedC50(double value) {
		UsedAmpC50 += value;
	}
	public double getPower() {
		return Power;
	}
	public String getLatitudeDMS() {
		return LatitudeDMS;
	}
	public String getLongitudeDMS() {
		return LongitudeDMS;
	}
	public void resetUsed() {
		UsedAmpC200 = 0.0f;
		UsedAmpC50 = 0.0f;
	}
	public void resetClimbRate() {
		airBase = 0.0f;
		airClimbRateBase = 0.0f;
		air1 = 0.0f;
		air2 = 0.0f;
	}
	public int[] GetData2D() {
		return data2D;
	}
	public int[] GetData2B() {
		return data2B;
	}
	public int[] GetData50() {
		return data50;
	}
}