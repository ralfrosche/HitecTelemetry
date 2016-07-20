package de.rosche.spectraTelemetry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DataExplorer extends Activity {

	private List<String> fileList = new ArrayList<String>();
	private List<String> filelength = new ArrayList<String>();
	File dir = null;
	String filename = "";
	String selectedFilename = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataexplorer);

		File sdCard = Environment.getExternalStorageDirectory();
		dir = new File(sdCard.getAbsolutePath() + "/hitec_log/live");
		ListDir(dir);
		Button deleteButton = (Button) findViewById(R.id.deletebutton);
		Button saveButton = (Button) findViewById(R.id.savebutton);
		Button closeButton = (Button) findViewById(R.id.closebutton);
		Button activateButton = (Button) findViewById(R.id.activatebutton);

		// TODO: send file to other device
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_SEND);
		// intent.setType("text/txt");
		// intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
		// startActivity(intent);
		//

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!selectedFilename.equals("")) {
					File file = new File(dir + "/" + selectedFilename);
					boolean deleted = file.delete();
					if (deleted) {
						Toast.makeText(getApplicationContext(),
								"file successfully deleted!",
								Toast.LENGTH_SHORT).show();
						selectedFilename = "";
						ListDir(dir);
					} else {
						Toast.makeText(getApplicationContext(),
								selectedFilename + " not deleted!",
								Toast.LENGTH_SHORT).show();
					}
				}

			}

		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!selectedFilename.equals("")) {
					File file = new File(dir + "/" + selectedFilename);
					boolean saved = saveFile(file);
					if (saved) {
						Toast.makeText(getApplicationContext(),
								"file successfully saved!", Toast.LENGTH_SHORT)
								.show();
						selectedFilename = "";
						ListDir(dir);
					}

				}

			}

		});

		activateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!selectedFilename.equals("")) {
					File file = new File(dir + "/" + selectedFilename);
					boolean activated = activateFile(file);
					if (activated) {
						Toast.makeText(getApplicationContext(),
								"file successfully activated!",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}

			}

		});
	}

	@SuppressLint("SimpleDateFormat")
	public boolean saveFile(File f) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
		String dateString = dateFormat.format(new Date());

		String log_file = "spectra_live.log";
		File b = new File(dir, "/" + dateString + "_" + log_file);

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
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public boolean activateFile(File f) {
		String log_file = "spectra_live.log";
		File b = new File(dir, "/" + log_file);

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
		return true;
	}

	void ListDir(File f) {
		File[] filelist = f.listFiles();
		fileList.clear();
		filelength.clear();

		for (File file : filelist) {

			String len = humanReadableByteCount(file.length(), true);

			fileList.add(file.getName() + " | (" + len + ")");

		}

		int length = filelist.length;
		RadioGroup rg = (RadioGroup) findViewById(R.id.files);
		rg.removeAllViews();
		final RadioButton[] rb = new RadioButton[length + 1];
		rg.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < length; i++) {

			filename = fileList.get(i);
			if (!filename.equals("spectra_live.log")) {

				rb[i] = new RadioButton(this);
				rg.addView(rb[i]);
				rb[i].setText(" " + fileList.get(i));

			}

		}

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
				RadioButton checkedRadioButton = (RadioButton) rGroup
						.findViewById(checkedId);
				boolean isChecked = checkedRadioButton.isChecked();
				if (isChecked) {
					selectedFilename = (String) checkedRadioButton.getText();
					selectedFilename = selectedFilename.replaceAll(" ", "");
					selectedFilename = selectedFilename.substring(0,
							selectedFilename.indexOf("|"));
					// Toast.makeText(getApplicationContext(), filename,
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
