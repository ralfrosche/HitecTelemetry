package de.rosche.spectraTelemetry;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Profile extends Activity {
	ArrayList<ArrayList<String>> DataToDB;
	String editID = "";
	boolean editMode = false;
	String query;
	Integer profileId = 0;
	DatabaseHelper myDbHelper = new DatabaseHelper(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		getProfiles();

		Button cancelButton = (Button) findViewById(R.id.cancelbutton);
		Button newButton = (Button) findViewById(R.id.newbutton);
		Button delButton = (Button) findViewById(R.id.delbutton);
		Button editflButton = (Button) findViewById(R.id.editflbutton);

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		delButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteProfile();
				getProfiles();
			}

		});

		newButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				launchEdit(false);
			}

		});
		editflButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				launchEdit(true);
			}

		});

	}

	private void deleteProfile() {
		try {
			if (profileId != 0) {
				myDbHelper.createDataBase();
				myDbHelper.deleteProfileFromDB(profileId);
				getProfiles();
			} else {
				Log.e("MLISTE", "+++ flightID missing++++");
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void getProfiles() {
		try {
			myDbHelper.createDataBase();
			DataToDB = myDbHelper.getProfiles();
			int length = DataToDB.size();
			TableLayout tbl = (TableLayout) findViewById(R.id.tableLayout1);
			tbl.removeAllViews();
			TableRow.LayoutParams params1 = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);

			TableRow.LayoutParams params2 = new TableRow.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			TableRow.LayoutParams params3 = new TableRow.LayoutParams(
					LayoutParams.MATCH_PARENT, 1);
			View vH = new View(this);
			vH.setLayoutParams(params3);
			vH.setBackgroundColor(getResources().getColor(R.color.errorColor));
			tbl.addView(vH);

			for (int i = 0; i < length; i++) {

				final TableRow row = new TableRow(this);
				row.setPadding(10, 2, 0, 0);
				TextView txt1 = new TextView(this);
				TextView txt2 = new TextView(this);
				ArrayList<String> profile_array = new ArrayList<String>();

				profile_array = DataToDB.get(i);
				Log.e("MLISTE", "+++ logliste++++" + profile_array);
				txt1.setText(profile_array.get(0));

				if (profile_array.get(1).length() > 15) {
					txt2.setText(profile_array.get(1).substring(0, 15) + "...");
				} else {
					txt2.setText(profile_array.get(1));
				}
				final int fid = Integer.parseInt(profile_array.get(0));

				if (i == 0) {
					profileId = fid;
				}

				txt1.setLayoutParams(params1);
				txt2.setLayoutParams(params1);
				txt1.setTextSize(24);
				txt2.setTextSize(24);
				View v = new View(this);
				v.setLayoutParams(params3);
				v.setBackgroundColor(getResources()
						.getColor(R.color.errorColor));

				row.addView(txt1);
				row.addView(txt2);
				row.setLayoutParams(params2);
				row.setFocusableInTouchMode(true);
				row.setClickable(true);
				row.setFocusable(true);
				// android:drawable="@drawable/semitransparent_white" />
				row.setBackgroundResource(R.drawable.selector);

				row.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						final int action = event.getAction();

						switch (action & MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
						case MotionEvent.ACTION_OUTSIDE:
							profileId = fid;
							Log.e("MLISTE", "flightID:" + profileId);

							break;
						default:

						}
						return false;
					}

				});
				// doubleclick on xperia
				row.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {

						profileId = fid;
						Log.e("MLISTE", "flightID:" + profileId);
					}
				});

				tbl.addView(row);
				tbl.addView(v);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("MListe", "- onresume flights-");
		getProfiles();

	}

	private void launchEdit(boolean edit) {
		Intent intent = new Intent(this, Newprofile.class);
		if (edit == true) {
			intent.putExtra("id", String.valueOf(profileId));
		} else {
			intent.putExtra("id", "");
		}
		startActivity(intent);
	}

}
