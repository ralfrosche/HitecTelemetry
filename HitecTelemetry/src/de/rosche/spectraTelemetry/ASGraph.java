package de.rosche.spectraTelemetry;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;

import java.io.IOException;

import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class ASGraph extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphs);

		// draw sin curve
		int num = 150;
		int i;
		GraphViewData[] data = new GraphViewData[num];
		double v = 0;
		for (i = 0; i < num; i++) {
			v += 0.2;
			data[i] = new GraphViewData(i, Math.sin(v));
		}
		// graph with dynamically genereated horizontal and vertical labels
		GraphView graphView;

		graphView = new LineGraphView(this, "Altitude");
		// ((LineGraphView) graphView).setDrawBackground(true);

		String[] inputData;
		double alt[] = new double[1024];
		double speed[] = new double[1024];
		boolean mlog_file = false;
		String log_file;

		log_file = getIntent().getStringExtra("log_file");
		mlog_file = getIntent().getBooleanExtra("mlog_file", false);

		i = 0;
		if (mlog_file == true) {
			try {

				// open the file for reading
				File sdCard = Environment.getExternalStorageDirectory();
				File file = new File(sdCard.getAbsolutePath()
						+ "/hitec_log/live/" + log_file);
				FileReader reader = new FileReader(file);
				BufferedReader buffreader = new BufferedReader(reader);

				// if file the available for reading
				if (reader != null) {
					// prepare the file for reading

					String line = "";
					i = 0;
					v = 0;
					// read every line of the file into the line-variable, on
					// line at the time
					try {

						while ((line = buffreader.readLine()) != null) {

							line = line.replace("$1;1;;", "");
							inputData = line.split(";");
							if (inputData.length > 0) { // FIXME: exact data
														// record length
								// volts[i] = Double.parseDouble(
								// inputData[0].replaceAll(",", "."));
								// amps[i] = Double.parseDouble(
								// inputData[1].replaceAll(",", "."));
								alt[i] = Double.parseDouble(inputData[6]
										.replaceAll(",", "."));
								speed[i] = Double.parseDouble(inputData[5]
										.replaceAll(",", "."));
								i++;
							}
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// close the file again
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (java.io.FileNotFoundException e) {
				// do something if the myfilename.txt does not exits
			}
		}
		num = i;
		data = new GraphViewData[num];
		for (i = 0; i < num; i++) {

			data[i] = new GraphViewData(i, alt[i]); // Math.sin(Math.random()*v)
		}
		// add data
		graphView.addSeries(new GraphViewSeries("Alt",
				new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), data));

		// set view port, start=2, size=40
		graphView.setViewPort(2, 40);
		// graphView.setLegendAlign(LegendAlign.BOTTOM);
		// graphView.setLegendWidth(200);
		graphView.setScrollable(true);
		// graphView.setShowLegend(true);
		graphView.setScalable(true);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

		graphView = new LineGraphView(this, "Speed");
		data = new GraphViewData[num];
		for (i = 0; i < num; i++) {
			data[i] = new GraphViewData(i, speed[i]);
		}
		graphView.addSeries(new GraphViewSeries("Speed",
				new GraphViewSeriesStyle(Color.rgb(90, 250, 00), 3), data));
		graphView.setViewPort(2, 40);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);

	}

}
