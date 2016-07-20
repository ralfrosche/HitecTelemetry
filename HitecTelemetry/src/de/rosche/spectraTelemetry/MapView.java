package de.rosche.spectraTelemetry;

import java.util.List;
import android.app.Dialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MapView extends FragmentActivity implements
		OnMyLocationChangeListener {
	GoogleMap googleMap;
	MarkerOptions markerOptions;
	String LongitudeString = "";
	String LatitudeString = "";

	double Longitude = 0.0;
	double Latitude = 0.0;
	boolean mpxLocationSet = false;
	double alpha = 0.0;
	double distance = 0.0;
	boolean mpxdata = false;
	boolean haszoomed = false;
	int eventCounter = 0;

	double LocationLongitude = 0.0;
	double LocationLatitude = 0.0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		LongitudeString = getIntent().getStringExtra("longitude");
		mpxdata = getIntent().getBooleanExtra("mpxdata", mpxdata);
		LatitudeString = getIntent().getStringExtra("latitude");
		alpha= getIntent().getDoubleExtra("alpha", 0);
		distance = getIntent().getDoubleExtra("distance",0);
		Longitude = Double.parseDouble(LongitudeString);
		Longitude = Math.round(Longitude * 100000) / 100000.0;
		Latitude = Double.parseDouble(LatitudeString);
		Latitude = Math.round(Latitude * 100000) / 100000.0;
		
		
		if (((int)Math.round(Longitude) == 0 || (int)Math.round(Latitude) == 0 ) && mpxdata == false) {
			
			Toast.makeText(
					this,
					"GPS-Position: no actual modell gps data avaiable" , Toast.LENGTH_LONG).show();

			int status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());

			if (status != ConnectionResult.SUCCESS) {
				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
						requestCode);
				dialog.show();
			} else {
				if (googleMap == null) {
					googleMap = ((SupportMapFragment) getSupportFragmentManager()
							.findFragmentById(R.id.map)).getMap();

					if (googleMap != null) {
						googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						googleMap.setMyLocationEnabled(true);
						googleMap.getUiSettings().setCompassEnabled(true);
						googleMap.getUiSettings().setZoomControlsEnabled(true);
						googleMap.setOnMyLocationChangeListener(this);
					}
				}
			}
			
		}
		else {
	
			int status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());

			if (status != ConnectionResult.SUCCESS) {
				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
						requestCode);
				dialog.show();
			} else {
				if (googleMap == null) {
					googleMap = ((SupportMapFragment) getSupportFragmentManager()
							.findFragmentById(R.id.map)).getMap();

					if (googleMap != null) {
						googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						googleMap.setMyLocationEnabled(true);
						googleMap.getUiSettings().setZoomControlsEnabled(true);
						googleMap.getUiSettings().setCompassEnabled(true);
						googleMap.setOnMyLocationChangeListener(this);
					}
				} 
				if (mpxdata == false) {
					SetLastLocation();
					Toast.makeText(
							this,
							"GPS-Position: " + String.valueOf(Math.round(Latitude * 100000) / 100000.0) + ", "
									+ String.valueOf(Math.round(Longitude * 100000) / 100000.0), Toast.LENGTH_LONG).show();
					
				}
			}
		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		LatLng latLng = new LatLng(latitude, longitude);
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		if (haszoomed == false) {
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
			haszoomed = true;
		}
		
		if ((LocationLongitude == 0.0|| eventCounter < 5) && longitude > 0.0) {
			LocationLongitude = longitude;
		}
		if ((LocationLatitude == 0.0 || eventCounter < 5) && latitude > 0.0) {
			LocationLatitude = latitude;
		}
		eventCounter++;
	    tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
		if ((LocationLongitude != 0.0 && LocationLatitude != 0.0) && mpxLocationSet == false) {
			if (mpxdata == true && eventCounter > 5) {
				MPXLocation(LocationLatitude, LocationLongitude, alpha, distance);
				mpxLocationSet = true;
			}
			
		}
    
	}

	private void SetLastLocation() {
		double lat = Latitude;
		double lng = Longitude;

		googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title("Estimated model position")
				.snippet(
						"GPS-Position: " + String.valueOf(Math.round(Latitude * 100000) / 100000.0) + ", "
								+ String.valueOf(Math.round(Longitude * 100000) / 100000.0)));

		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				lat, lng), 17));

	}

	public double[] getlocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		double[] gps = new double[2];

		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	private void MPXLocation(double lat1,double lng1, double alpha, double distance){
		double lat2= 0.0;
		double lng2 = 0.0;
		lat1= Math.toRadians(lat1);
		lng1= Math.toRadians(lng1);
		double c = distance/6371000;
		double a = Math.acos(Math.sin(lat1) * Math.cos(c) +Math.cos(lat1)*Math.sin(c)*Math.cos(Math.toRadians(alpha)));
		double gamma = Math.acos(((Math.cos(c) -Math.cos(a)*Math.sin(lat1)) / Math.sin(a)/Math.cos(lat1)));
		lat2 = Math.PI / 2;
		lat2 = lat2 - a;
		if (Math.toRadians(alpha) < Math.PI){
			 lng2 = lng1 + gamma;
			
		} else {
			lng2 = lng1 - gamma;
		}
		Latitude = Math.toDegrees(lat2);
		Longitude = Math.toDegrees(lng2);
		SetLastLocation();
		Toast.makeText(
				this,
				"GPS-Position: " + String.valueOf(Math.round(Latitude * 100000) / 100000.0) + ", "
						+ String.valueOf(Math.round(Longitude * 100000) / 100000.0), Toast.LENGTH_LONG).show();

	}
}
