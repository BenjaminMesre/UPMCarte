/**
 * 
 */
package org.redpin.android.ui.mapgl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.redpin.android.R;
import org.redpin.android.core.Location;

import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.FloatMath;
import android.util.Log;
import android.widget.TextView;

/**
 * Renderer displaying the map by floors, the markers, and controlling the camera, perspective, etc.
 * 
 * @author Benjamin Mesre
 *
 */
public class MapGLRenderer implements Renderer {

	private static final float DistBtwnFloors = 1.0f;
	private Context mContext;
	private float initialFloor = 0f;
	
	ArrayList<FloorMap> listFloorMap = new ArrayList<FloorMap>();
	ArrayList<LocationMarkerGL> listMarkers = new ArrayList<LocationMarkerGL>();
	List<Location> listeLocation;
	private FloorMap map0;
	private FloorMap mapJ;
	private FloorMap map1;
	private FloorMap map2;
	private FloorMap map3;
	private FloorMap map4;
	private FloorMap map5;
	private HashMap<Integer, String> floorsNumbersNames = new HashMap<Integer, String>();
	
	private LocationMarkerGL markerCurrentLocation;
	
    
    private float widthMap = getDistBtwnFloors()*3/4;
    private float lengthMap = getDistBtwnFloors()*3/4;
    
    public float mCamX = lengthMap / 2;
    public float mCamY = widthMap / 2;
    public float mCamZ = getDistBtwnFloors() - 0.01f;
    private int currentFloor;
    private TextView floorTextview;


	/** 
	 * Constructor to set the handed over context 
	 */
	public MapGLRenderer(Context context) {
		this.mContext = context;
		this.map0 = new FloorMap(lengthMap, widthMap, initialFloor, R.drawable.plan_rc);
		this.mapJ = new FloorMap(lengthMap, widthMap, initialFloor + getDistBtwnFloors(), R.drawable.plan_j);
		this.map1 = new FloorMap(lengthMap, widthMap, initialFloor + 2 * getDistBtwnFloors(), R.drawable.plan_1);
		this.map2 = new FloorMap(lengthMap, widthMap, initialFloor + 3 * getDistBtwnFloors(), R.drawable.plan_2);
		this.map3 = new FloorMap(lengthMap, widthMap, initialFloor + 4 * getDistBtwnFloors(), R.drawable.plan_3);
		this.map4 = new FloorMap(lengthMap, widthMap, initialFloor + 5 * getDistBtwnFloors(), R.drawable.plan_4);
		this.map5 = new FloorMap(lengthMap, widthMap, initialFloor + 6 * getDistBtwnFloors(), R.drawable.plan_5);
		listFloorMap.add(this.map0);
		listFloorMap.add(this.mapJ);
		listFloorMap.add(this.map1);
		listFloorMap.add(this.map2);
		listFloorMap.add(this.map3);
		listFloorMap.add(this.map4);
		listFloorMap.add(this.map5);
		floorsNumbersNames.put(0, "RC");
		floorsNumbersNames.put(1, "J");
		floorsNumbersNames.put(2, "1");
		floorsNumbersNames.put(3, "2");
		floorsNumbersNames.put(4, "3");
		floorsNumbersNames.put(5, "4");
		floorsNumbersNames.put(6, "5");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		
		//Log.d("testAffichage","(" + this.mCamX + ", "+ this.mCamY + ", "+ this.mCamZ + ")");
		
		// clear Screen and Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, mCamX,mCamY,mCamZ, mCamX,mCamY,0, 0,1,0);
		
		//Draws the current floor map
		for(int i = 0; i < listFloorMap.size(); i++)
        {
			FloorMap carteEnCours = listFloorMap.get(i);
			if(carteEnCours.getFloor() <= this.currentFloor)
			{
				carteEnCours.draw(gl);
			}	
        }
		//Draws the current floor markers
		displayMarkers(gl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
		GLU.gluLookAt(gl, mCamX,mCamY,mCamZ, mCamX,mCamY,0, 0,1,0);
		
		displayMarkers(gl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Load the texture for the square
		for(int i = 0; i < listFloorMap.size(); i++)
        {
			FloorMap carteEnCours = listFloorMap.get(i);
			carteEnCours.loadGLTexture(gl, this.mContext);	
        }
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f); 	//White Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
	
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}

	/**
	 * Translates the camera position on the X-Axis if it stays within the boundaries of the map
	 * 
	 * @param factor The valor that will be added to the camera X value
	 * 
	 */
	public void changeHPosition(float factor) {
		if ( (mCamX + factor <= this.lengthMap) && (mCamX + factor >= 0) )
		{
			mCamX += factor;
		}
	}
	
	/**
	 * Translates the camera position on the Y-Axis if it stays within the boundaries of the map
	 * 
	 * @param factor The valor that will be added to the camera Y value
	 * 
	 */
	public void changeVPosition(float factor) {
		if ( (mCamY + factor <= this.lengthMap) && (mCamY + factor >= 0) )
		{
			mCamY += factor;
		}
	}
	
	/**
	 * Translates the camera position on the Z-Axis if it stays within two existing floors
	 * 
	 * @param factor The valor that will be added to the camera Z value
	 * 
	 */
	public void changeZoom(float factor) {
		if (mCamZ + factor > currentFloor * DistBtwnFloors + 0.1f)
		{
			if(mCamZ + factor < (currentFloor + 1) * DistBtwnFloors )
			{
				mCamZ += factor;
			}
		}
	}

	/**
	 * Adds a new {@link LocationMarkerGL} on the center of the screen and sets
	 * the {@link Location}s coordinates accordingly.
	 * 
	 * @param location
	 *            {@link Location} for the {@link LocationMarkerGL}
	 */
	public void addMarkerToList(Location location) {
		location.setMapXcord(mCamX);
		location.setMapYcord(mCamY);
		location.setMapZcord((float)currentFloor * DistBtwnFloors);
		LocationMarkerGL newMarker = new LocationMarkerGL(location);
		newMarker.setEnabled(true);
		newMarker.setCreatedThisSession(true);
		this.listMarkers.add(newMarker);
		
	}

	/**
	 * Display all the selected {@link LocationMarkerGL}
	 * 
	 * @param gl {@link GL10} configuration
	 * 
	 */
	void displayMarkers(GL10 gl)
	{
		for(int j = 0; j < listMarkers.size(); j++)
        {
			LocationMarkerGL marker = listMarkers.get(j);
			if(marker.getLocation() == null)
			{
				this.listMarkers.remove(marker);
			}
			if(marker.getzCenter() == currentFloor * DistBtwnFloors)
			{
				gl.glPushMatrix();
				marker.setSize( (mCamZ - currentFloor * DistBtwnFloors) * 0.05f);
				marker.draw(gl);
				gl.glPopMatrix();
			}
        }
	}
	
	/**
	 * Displays correctly a {@link LocationMarkerGL} for a given {@link Location}
	 * 
	 * @param location {@link Location} of the {@link LocationMarkerGL} to show
	 * 
	 */	
	public void showMarker(Location location) {
		boolean inList = false;
		int j = 0;
		LocationMarkerGL marker = null;
		
		// Search if the current location is already displayed
		while( (j < listMarkers.size() ) && (!inList) )
		{
			marker = listMarkers.get(j);
			if (marker.getLocation().equals(location))
			{
				
				marker.setColors(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f); // green + red point
				inList = true;
			}
			j++;
        }
		
		// if not already displayed: creates it
		if (!inList)
		{
			marker = new LocationMarkerGL(location);
			marker.setColors(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f);
			this.listMarkers.add(marker);
		}
		
		// Change the colors or deletes the last marker of a localization
		if (this.markerCurrentLocation != null )
		{
			// Do nothing if located at the same spot as before
			if (!marker.equals(this.markerCurrentLocation))
			{
			// Created this time by the user, hence in blue and not removed
				if(this.markerCurrentLocation.isCreatedThisSession())
				{
					this.markerCurrentLocation.setColors(0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f); // blue + red point
				}
				else
				{
					this.listMarkers.remove(this.markerCurrentLocation);
				}
			}
		}
		
		// updates the current location
		this.markerCurrentLocation = marker;
		
		// Centers the camera on the location with the same zoom
		centerOnLocation(location);
	}
	
	/**
	 * Centers the camera correctly on the {@link LocationMarkerGL} with the given {@link Location}
	 * 
	 * @param location {@link Location} of the {@link LocationMarkerGL} to center on
	 * 
	 */	
	public void centerOnLocation(Location location)
	{
		this.mCamX=location.getMapXcord();
		this.mCamY=location.getMapYcord();
		changeFloor((this.mCamZ - currentFloor * DistBtwnFloors) + location.getMapZcord());
	}
	
	/**
	 * Updates the text indicating the current floor
	 * 
	 * @param zNewPosition floating value of the current floor
	 * 
	 */	
	public void changeFloor(float zNewPosition) {
		this.mCamZ = zNewPosition;
		this.currentFloor = (int) (FloatMath.floor(zNewPosition) / DistBtwnFloors);
		floorTextview.setText(this.floorsNumbersNames.get(currentFloor));
	}
	
	
	
	/**
	 * Returns the floating distance between each floor
	 * 
	 */
	public static float getDistBtwnFloors() {
		return DistBtwnFloors;
	}

	/**
	 * Returns the number of floor maps
	 * 
	 */
	public int getNbFloors(){
		return this.listFloorMap.size();
	}
	
	/**
	 * Sets the textview indicating the current floor
	 * 
	 * @param etage {@link TextView} that will display the number of the floor
	 * 
	 */	
	public void setFloorTextview(TextView floorTextview) {
		this.floorTextview = floorTextview;
	}

	/**
	 * Gets the {@link HashMap} mapping floors numbers (key) and floors names (value)
	 */	
	public HashMap<Integer, String> getFloorsNumbersNames() {
		return floorsNumbersNames;
	}

	/**
	 * Returns the number of the current floor 
	 * 
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

}
