package de.rosche.spectraTelemetry;

import java.io.IOException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ImageView;

public class Newprofile extends Activity {
	String[] DataToDB;
	String editID = "";
	boolean editMode = false;

	int column_index;

	DatabaseHelper myDbHelper = new DatabaseHelper(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newprofle);
		final String id = getIntent().getStringExtra("id");
		String name = "";
		String description = "";
		if (!id.equals("")) {
			editMode = true;
			EditText sbeschreibung = (EditText) findViewById(R.id.beschreibungedit);
			EditText sname = (EditText) findViewById(R.id.nameedit);

			try {
				myDbHelper.createDataBase();
				name = myDbHelper.getProfileById(Integer.parseInt(id));
				description = myDbHelper.getProfileDescriptionById(Integer
						.parseInt(id));
			} catch (IOException e) {

				e.printStackTrace();
			}

			sname.setText(name);
			sbeschreibung.setText(description);

		}

		Button cancelButton = (Button) findViewById(R.id.cancelbutton);
		Button saveButton = (Button) findViewById(R.id.savebutton);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});
		saveButton.setOnClickListener(new View.OnClickListener() {
			String[] params = new String[2];
			Integer insertedID = 0;

			@Override
			public void onClick(View arg0) {
				EditText sbeschreibung = (EditText) findViewById(R.id.beschreibungedit);
				EditText sname = (EditText) findViewById(R.id.nameedit);

				params[1] = sbeschreibung.getText().toString();
				params[0] = sname.getText().toString().trim();

				if (!params[0].equals("")) {

					try {

						myDbHelper.createDataBase();
						if (editMode == true) {
							insertedID = myDbHelper.updateProfile(params, id);
							Log.e("MListe", "- updated profile ID:"
									+ insertedID);

						} else {
							insertedID = myDbHelper.insertProfile(params);

							Log.e("MListe", "- inserted profile ID:"
									+ insertedID);

						}

						myDbHelper.close();
						finish();

					} catch (IOException ioe) {

						throw new Error("Unable to open database");

					}
				} else {
					// show error
					Log.e("MListe", "- empty id");
				}

			}

		});

	}

}