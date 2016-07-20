package de.rosche.spectraTelemetry;

import android.content.SharedPreferences;

public class tools {
	
	public static int getInteger(String value, int dafaultvalue, SharedPreferences mPrefs) {
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


}
