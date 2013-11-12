/**
 *  Filename: MapGLViewActivity.java (in org.repin.android.ui)
 *  This file is part of the Redpin project.
 * 
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright Polytech Paris-UPMC Benjamin Mesre, Davy Ushaka Ishimwe, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.redpin.android.ApplicationContext;
import org.redpin.android.R;
import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.core.SpinnerData;
import org.redpin.android.net.InternetConnectionManager;
import org.redpin.android.net.Response;
import org.redpin.android.net.SynchronizationManager;
import org.redpin.android.net.home.FingerprintRemoteHome;
import org.redpin.android.net.home.LocationRemoteHome;
import org.redpin.android.net.home.MapRemoteHome;
import org.redpin.android.net.home.RemoteEntityHomeCallback;
import org.redpin.android.net.home.SpinnerDataRemoteHome;
import org.redpin.android.net.wifi.WifiSniffer;
import org.redpin.android.ui.list.LocationListActivity;
import org.redpin.android.ui.list.SearchListActivity;
import org.redpin.android.ui.mapgl.MapGLRenderer;
import org.redpin.android.ui.mapgl.MapGLSurfaceView;
import org.redpin.android.ui.mapgl.Position_Name;
import org.redpin.android.ui.mapview.MapView;
import org.redpin.android.util.ExceptionReporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Main activity of the client that displays maps and locations
 * 
 * @author Benjamin Mesre, Davy Ushaka Ishimwe
 * 
 */
public class MapGLViewActivity extends Activity {
	private static final String TAG = MapGLViewActivity.class.getSimpleName();
	// MapView mapView;
	public static Map mapUPMC;
	static {
		mapUPMC = new Map();
		mapUPMC.setRemoteId(1);
		mapUPMC.setMapName("UPMC");
		mapUPMC.setMapURL("URL");
		MapRemoteHome.setMap(mapUPMC);
	}
	
	public static SpinnerData dataspin;
	
//	static {
//		dataspin = new SpinnerData();
//		dataspin.setRemoteId(1);
//		dataspin.setBuildingName("frefgvyj");
//		SpinnerDataRemoteHome.setSpinnerData(dataspin);
//	}

	Context mContext = this;
	MapGLSurfaceView glSurfaceView;
	ImageButton locateButton;
	ImageButton addLocationButton;
	ImageButton etageSupButton;
	ImageButton etageInfButton;
	ProgressDialog progressDialog;

	WifiSniffer mWifiService;
	Location mLocation;
	SpinnerData mdataspin;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.init(getApplicationContext());
		ExceptionReporter.register(this);

		registerReceiver(connectionChangeReceiver, new IntentFilter(
				InternetConnectionManager.CONNECTIVITY_ACTION));
		startService(new Intent(MapGLViewActivity.this,
				SynchronizationManager.class));
		bindService(new Intent(this, InternetConnectionManager.class),
				mICMConnection, Context.BIND_AUTO_CREATE);
		/*
		 * startService(new Intent(MapViewActivity.this,
		 * InternetConnectionManager.class));
		 */

		startWifiSniffer();
		/*
		 * bindService(new Intent(this, WifiSniffer.class), mWifiConnection,
		 * Context.BIND_AUTO_CREATE); registerReceiver(wifiReceiver, new
		 * IntentFilter( WifiSniffer.WIFI_ACTION));
		 */

		setContentView(R.layout.map_view);
		glSurfaceView = (MapGLSurfaceView) findViewById(R.id.carteopengl);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		float screenDensity = getApplicationContext().getResources()
				.getDisplayMetrics().density;
		glSurfaceView.setscreenDensity(screenDensity);
		glSurfaceView.getMyRenderer().setFloorTextview((TextView) findViewById(R.id.txt_etage));

		addLocationButton = (ImageButton) findViewById(R.id.add_location_button);
		addLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocation = new Location();
				mLocation.setMap(mapUPMC);
				formNewLocationShow();
			}
		});

		etageSupButton = (ImageButton) findViewById(R.id.etage_sup_button);
		etageSupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etageUp();
			}
		});

		etageInfButton = (ImageButton) findViewById(R.id.etage_inf_button);
		etageInfButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etageDown();
			}
		});

		locateButton = (ImageButton) findViewById(R.id.locate_button);
		locateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				locate();
			}
		});

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getText(R.string.taking_measurement));

		setOnlineMode(false);
		restoreState();
	}

	
	
	  public Integer getFloorNumberFromName(HashMap<Integer, String> hm, Object value)
	  {
		    List <Integer>list = new ArrayList<Integer>();
		    for(Integer o:hm.keySet())
		    {
		        if(hm.get(o).equals(value))
		        {
		            list.add(o);
		        }
		    }
		    if (list.size() != 1)
		    {
		    	return -1;
		    }
		    else
		    {
		    	return list.get(0);
		    }
	  }
	
		/**
		 * Set in database all 
		 * 
		 * @param formalizedDataReceived
		 *            
		 */
	public void onEditorActionGL(String formalizedDataReceived) {
		Log.d("onEditoActionGL", "newSId = " + formalizedDataReceived);
		String buildingName = "", floorName = "", roomName = "", roomType = "", openingTime = "", closingTime = "";
		String symbolicIdReceived = "";
		Integer floorNumber = -1;
		
		String fields[] = formalizedDataReceived.split("\\" + Position_Name.getSeparator(), -2);
		if (fields.length == Position_Name.getNumberOfFields())
		{
			// Extracting each field of information
			roomName = fields[Position_Name.getPosRoomName()];
			buildingName = fields[Position_Name.getPosBuildingName()];
			floorName = fields[Position_Name.getPosFloorName()];
			roomType = fields[Position_Name.getPosRoomType()];
			openingTime = fields[Position_Name.getPosTimeOpening()];
			closingTime = fields[Position_Name.getPosTimeClosing()];
			
			// Retrieving the floor name using the floor number
			floorNumber = getFloorNumberFromName(this.glSurfaceView.getMyRenderer().getFloorsNumbersNames(), floorName);
			
			// Retrieved symbolic Id
			symbolicIdReceived = roomName + Position_Name.getSeparator() 
					+ buildingName + Position_Name.getSeparator() 
					+ floorName;
		}
		
		Log.d(this.getClass().getName(), "(floorName, floorNumber) = (" + floorName + 
										", " + floorNumber + ")");
		
		Log.d(this.getClass().getName(), "(roomName, roomType, buildingName) = " + roomName 
											+ ", " + roomType
											+ ", " + buildingName + ")");
		
		Log.d(this.getClass().getName(), "(openingTime, closingTime) = (" + openingTime
				+ ", " + closingTime + ")");
		
		// To create a marker, there must at least be a room name and a correct floor number
		if ( !(roomName.equals("")) && (floorNumber != -1) )
		{
			// Modify symbolic Id if different
			if (!mLocation.getSymbolicID().equals(symbolicIdReceived))
			{	
				mLocation.setSymbolicID(symbolicIdReceived);
				
				// Modify buildingId if different
				if (!mLocation.getBatimentID().equals(buildingName))
				{	
					mLocation.setBatimentID(buildingName);
				}
				
				// Modify roomId if different
				if (!mLocation.getSalleID().equals(roomName))
				{	
					mLocation.setSalleID(roomName);
				}
				
				// Modify floorName if different
			}

			// Modify roomType if different
			if (!mLocation.getTypeSalleID().equals(roomType))
			{	
				mLocation.setTypeSalleID(roomType);
			}
			// Modify openingTime if different
			if (!mLocation.getOpeningTIME().equals(openingTime))
			{	
				mLocation.setOpeningTIME(openingTime);
			}
			// Modify closingTime if different
			if (!mLocation.getClosingTIME().equals(closingTime))
			{	
				mLocation.setClosingTIME(closingTime);
			}
			
			// update the modifications in the server
			LocationRemoteHome.updateLocation(mLocation);			
			
			MapGLRenderer renderer = glSurfaceView.getMyRenderer();
			renderer.changeFloor(renderer.mCamZ - renderer.getCurrentFloor() + floorNumber);
			addNewLocation();
			
		}
	}

	/**
	 * Shows the {@link Position_Name} clicked
	 */
	public void formNewLocationShow()
	{	
       	//creation d'une instance de ma classe
		Position_Name boite_alert_dialog = new Position_Name(mContext, this, this.glSurfaceView.getMyRenderer().getCurrentFloor(),dataspin);
		
    	//titre de ma boite de dialogue
        boite_alert_dialog.setTitle("Enregistrement Position");
        
        //ouverture de ma boite de dialogue
    	boite_alert_dialog.show();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
		glSurfaceView.onResume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Handling of orientation change (here nothing)
	}

	/**
	 * Starts the setting screen
	 * 
	 * @param target
	 *            {@link View} that called this method
	 */
	public void button_Settings(View target) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private static final String pref_url = "url";

	/**
	 * Saves some of the {@link MapView} state
	 */
	private void saveState() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.commit();
	}

	/**
	 * Restores the {@link MapView} to show the last shown map
	 */
	private void restoreState() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		String mapUrl = preferences.getString(pref_url, null);

		if (getIntent().getData() == null && mapUrl != null) {
			getIntent().setData(Uri.parse(mapUrl));
			preferences.edit().clear().commit();
		}

		preferences.edit().clear().commit();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		saveState();

		unregisterReceiver(connectionChangeReceiver);

		stopWifiSniffer();

		stopService(new Intent(MapGLViewActivity.this,
				SynchronizationManager.class));
		/*
		 * stopService(new Intent(MapViewActivity.this,
		 * InternetConnectionManager.class));
		 */
		unbindService(mICMConnection);

		super.onDestroy();
	}

	/**
	 * Shows the {@link LocationMarkerGL} clicked
	 */
	protected void show() {
		getIntent().resolveType(this);
		this.glSurfaceView.showLocationClickedFromList(getIntent().getData());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		show();
	}

	/**
	 * Initiates a scan for a new measurement, creates a location and afterwards
	 * displays it on the map
	 */
	private void addNewLocation() {

		progressDialog.show();
		firstMeasurement = true;
		mWifiService.forceMeasurement();

	}

	/**
	 * Displays the map of the map above the current one, if there is one
	 */
	private void etageUp() {
		MapGLRenderer renderer = glSurfaceView.getMyRenderer();
		int oldEtage = (int) FloatMath.floor(renderer.mCamZ);
		if (oldEtage < renderer.getNbFloors() - 1) {
			renderer.changeFloor(renderer.mCamZ + MapGLRenderer.getDistBtwnFloors());
		}
	}

	/**
	 * Displays the map of the map below the current one, if there is one
	 */
	private void etageDown() {
		MapGLRenderer renderer = glSurfaceView.getMyRenderer();
		int oldEtage = (int) FloatMath.floor(renderer.mCamZ);
		if (oldEtage > 0) {
			renderer.changeFloor(renderer.mCamZ - MapGLRenderer.getDistBtwnFloors());
		}
	}

	/**
	 * Locates the client
	 */
	private void locate() {
		progressDialog.show();

		mLocation = null;
		mWifiService.forceMeasurement();

	}

	/**
	 * Sets the connectivity mode of the view
	 * 
	 * @param isOnline
	 *            <code>True</code> if the client can connect to the server,
	 *            <code>false</code> otherwise
	 */
	private void setOnlineMode(boolean isOnline) {
		locateButton.setEnabled(isOnline);
		addLocationButton.setEnabled(isOnline);

	}

	/**
	 * Starts the sniffer and registers the receiver
	 */
	private void startWifiSniffer() {
		bindService(new Intent(this, WifiSniffer.class), mWifiConnection,
				Context.BIND_AUTO_CREATE);
		registerReceiver(wifiReceiver,
				new IntentFilter(WifiSniffer.WIFI_ACTION));
		Log.i(TAG, "Started WifiSniffer");
	}

	/**
	 * Stops the sniffer and unregisters the receiver
	 */
	private void stopWifiSniffer() {
		if (mWifiService != null) {
			mWifiService.stopMeasuring();
		}
		unbindService(mWifiConnection);
		unregisterReceiver(wifiReceiver);
		Log.i(TAG, "Stopped WifiSniffer");
	}

	/**
	 * {@link InternetConnectionManager} {@link ServiceConnection} to check
	 * current online state
	 */
	private ServiceConnection mICMConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			InternetConnectionManager mManager = ((InternetConnectionManager.LocalBinder) service)
					.getService();
			setOnlineMode(mManager.isOnline());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	};

	/**
	 * Receives notifications about connectivity changes
	 */
	private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			setOnlineMode(intent.getFlags() == InternetConnectionManager.ONLINE_FLAG);
		}

	};

	private boolean firstMeasurement = false;
	String iD = "dada";
	/**
	 * Receives notifications about new available measurements
	 */
	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Measurement m = mWifiService.retrieveLastMeasurement();
			if (m == null) {
				return;
			}

			if (mLocation != null) {
				// Interval Scanning
				Fingerprint fp = new Fingerprint(mLocation, m);
				FingerprintRemoteHome.setFingerprint(fp,
						new RemoteEntityHomeCallback() {

							@Override
							public void onResponse(Response<?> response) {
								if (firstMeasurement) {
									progressDialog.hide();
									glSurfaceView.addNewLocation(mLocation,
											mContext);
									firstMeasurement = false;
								}

								Log.i(TAG,
										"addNewLocation: setFingerprint successfull");
							}

							@Override
							public void onFailure(Response<?> response) {
								progressDialog.hide();
								Log.i(TAG,
										"addNewLocation: setFingerprint failed: "
												+ response.getStatus() + ", "
												+ response.getMessage());
							}
						});

			} else {
				// Localization
				LocationRemoteHome.getLocation(m,
						new RemoteEntityHomeCallback() {

							@Override
							public void onFailure(Response<?> response) {
								progressDialog.hide();

								new AlertDialog.Builder(MapGLViewActivity.this)
										.setMessage(response.getMessage())
										.setPositiveButton(android.R.string.ok,
												null).create().show();

							}

							@Override
							public void onResponse(Response<?> response) {
								progressDialog.hide();
								Location l = (Location) response.getData();
								if(l != null)
								{
									glSurfaceView.getMyRenderer().showMarker(l);
								}
							}

						});
				mWifiService.stopMeasuring();
			}

		}
	};
	/**
	 * {@link ServiceConnection} for the {@link WifiSniffer}
	 */
	private ServiceConnection mWifiConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mWifiService = ((WifiSniffer.LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mWifiService = null;
		}

	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, R.id.options_menu_search, 0,
				R.string.options_menu_search_text).setIcon(
				R.drawable.menu_search);
		menu.add(0, R.id.options_menu_listview, 0,
				R.string.options_menu_listview_text).setIcon(
				R.drawable.menu_list_black);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_listview:
			Intent mainlist = new Intent(MapGLViewActivity.this, LocationListActivity.class);
			startActivity(mainlist);
			return true;
		case R.id.options_menu_search:
			Intent search = new Intent(this, SearchListActivity.class);
			startActivity(search);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onSearchRequested() {
		Intent search = new Intent(this, SearchListActivity.class);
		startActivity(search);
		return false;
	}

}
