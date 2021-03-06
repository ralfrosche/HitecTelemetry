package de.rosche.spectraTelemetry;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;


public class TermPreferences extends PreferenceActivity {
	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;

	/**
	 * Checks to see if using new v11+ way of handling PrefFragments.
	 * 
	 * @return Returns false pre-v11, else checks to see if using headers.
	 */
	public boolean isNewV11Prefs() {
		if (mHasHeaders != null && mLoadHeaders != null) {
			try {
				return (Boolean) mHasHeaders.invoke(this);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return false;
	}

	@Override
	public void onCreate(Bundle aSavedState) {
		// onBuildHeaders() will be called during super.onCreate()
		try {
			mLoadHeaders = getClass().getMethod("loadHeadersFromResource",
					int.class, List.class);
			mHasHeaders = getClass().getMethod("hasHeaders");
		} catch (NoSuchMethodException e) {
		}
		super.onCreate(aSavedState);
		if (!isNewV11Prefs()) {
			addPreferencesFromResource(R.xml.preferences);
			//addPreferencesFromResource(R.xml.preferences_spinner);
		}
	}

	@Override
	public void onBuildHeaders(List<Header> aTarget) {
		try {
			mLoadHeaders.invoke(this, new Object[] { R.xml.preference_headers,
					aTarget });
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}
	@Override
	public synchronized void onResume() {
		super.onResume();
		SpectraTelemetry.changed_prefs = true;
		Log.e("HITEC TELEMETRY", "+ ON RESUME in prefs+");
	}
	@Override
	public synchronized void onDestroy() {
		super.onDestroy();
		
		Log.e("HITEC TELEMETRY", "+ ON Destoy in prefs+");
	}
	@Override
	public synchronized void onStop() {
		super.onStop();
		
		Log.e("HITEC TELEMETRY", "+ ON stop in prefs+");
	}


	static public class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle aSavedState) {
			super.onCreate(aSavedState);
			Context anAct = getActivity().getApplicationContext();
			int thePrefRes = anAct.getResources().getIdentifier(
					getArguments().getString("pref-resource"), "xml",
					anAct.getPackageName());
			addPreferencesFromResource(thePrefRes);
		}
	}
}
