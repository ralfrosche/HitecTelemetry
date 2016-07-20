/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.rosche.spectraTelemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class BluetoothSerialService {
	private static final UUID SerialPortServiceClass_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private SpectraData mData;
	private boolean mlog;
	private boolean mpxData;
	public static final int STATE_NONE = 0; 
	public static final int STATE_LISTEN = 1; 
	public static final int STATE_CONNECTING = 2; 
	public static final int STATE_CONNECTED = 3; 
	private FileOutputStream f = null;

	public BluetoothSerialService(Context context, Handler handler,
			SpectraData spData, boolean log_file, boolean mpxDataFlag) { 
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
		mlog = log_file;
		mpxData = mpxDataFlag;
		mData = spData;
		File sdCard = Environment.getExternalStorageDirectory();
		String dirname = "/hitec_log/raw";
		if (mpxData == true){
			dirname = "/mlink_log/raw";
		}
		File dir = new File(sdCard.getAbsolutePath() + dirname);
		dir.mkdirs();
		if (mlog == true) {
			try {
				String filename = "spectra_raw.log";
				if (mpxData == true){
					filename = "mlink_raw.log";
				}
				File mSampleFile = new File(dir, "/" + filename);
				mSampleFile.createNewFile();
				f = new FileOutputStream(mSampleFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
			}

		}
	}

	private synchronized void setState(int state) {
		mState = state;
		mHandler.obtainMessage(SpectraTelemetry.MESSAGE_STATE_CHANGE, state, -1)
				.sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}

	public synchronized void connect(BluetoothDevice device) {
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
		Message msg = mHandler
				.obtainMessage(SpectraTelemetry.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(SpectraTelemetry.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}

	private void connectionFailed() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(SpectraTelemetry.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(SpectraTelemetry.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(STATE_NONE);
		try {
			Message msg = mHandler.obtainMessage(SpectraTelemetry.MESSAGE_TOAST);
			Bundle bundle = new Bundle();
			bundle.putString(SpectraTelemetry.TOAST, "Device connection was lost");
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
		catch(Exception e){
			
		}
	}
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device
						.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		@Override
		public void run() {
			setName("ConnectThread");
			mAdapter.cancelDiscovery();
			try {
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
				}
				return;
			}
			synchronized (BluetoothSerialService.this) {
				mConnectThread = null;
			}
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			try {
				tmpIn = socket.getInputStream();

			} catch (IOException e) {
			}
			mmInStream = tmpIn;
		}

		@Override
		public void run() {
			int[] buffer;
			if (mpxData == true)
			{
				buffer = new int[20];
			} else {
				buffer = new int[22];
			}
			
			int data;
			byte bufferbyte;
			boolean data_ready = false;
		
			while (true) {
				try {

					data_ready = false;
					data = mmInStream.read();

					while (data != -1 && data_ready == false) {
						if (mpxData == true)
						{ 
							if (data == 0x2) {
								int n = 0;
								buffer[n] = 0x02;
								for (int i = n+1; i <= 37; i++) {
									data = mmInStream.read();
									if (data == 0x3) {
										n++;
										buffer[n] =  0x03;
										break;
									}
									if (data == 0x1B) {
										data = mmInStream.read();
										n++;
										data = data & 0x1B ;
										buffer[n] = data;
									} else {
										n++;
										buffer[n] = data;
									}
									if (n >= 19)
										break;
								}
								data_ready = true;
							} else {
								data = mmInStream.read();
							}
						} else {
							if (data == 0xAF) {
								buffer[0] = 0x00;
								buffer[1] = 0xAF;
								for (int i = 2; i <= 21; i++) {
									data = mmInStream.read();
									buffer[i] = data;
								}
								data_ready = true;
							} else {
								data = mmInStream.read();
							}
							
						}
					}
					if (data_ready == true) {
						mData.UpdateData(buffer, mpxData);
						int n = 21;
						if (mlog == true) {
							n = 19;
						}
							if (mpxData == true)
							{ 
							for (int i = 0; i <= n; i++) {
								try {
									bufferbyte = (byte) buffer[i];
									f.write(bufferbyte);
								} catch (IOException e) {
								}
							}
							try {
								f.write('\r');
							} catch (IOException e) {
							}
						}
					}

				} catch (IOException e) {
					connectionLost();
					break;
				}
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
				f.close();
			} catch (IOException e) {
			}
		}
	}
}
