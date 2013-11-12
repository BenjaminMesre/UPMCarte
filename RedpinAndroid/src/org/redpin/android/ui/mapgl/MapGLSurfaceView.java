package org.redpin.android.ui.mapgl;


import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.provider.RedpinContract;
import org.redpin.android.ui.MapGLViewActivity;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * {@link GLSurfaceView} handling touch events and containing the renderer displaying the map and the locations
 * 
 * @author Benjamin Mesre
 *
 */
public class MapGLSurfaceView extends GLSurfaceView {
	
	private MapGLRenderer MyRenderer;
	
    private float mPreviousXScreen;
    private float mPreviousYScreen;
    boolean isMarkerSelected = false;
    LocationMarkerGL markerSelected = null;
    private float screenDensity = 1.0f;
    
    



	public MapGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		MyRenderer = new MapGLRenderer(context);
		setRenderer(MyRenderer);
	}
	
   
	/**
	 * Returns the distance between the first add the second touched points on the screen
	 * 
	 * @param event {@link MotionEvent} of the {@link MapGLSurfaceView}
	 * 
	 */	
    private float spacing(MotionEvent event) {
    	   float x = event.getX(0) - event.getX(1);
    	   float y = event.getY(0) - event.getY(1);
    	   return FloatMath.sqrt(x * x + y * y);
    	}
    
    float oldDist = 100.0f;
	float newDist;
	
	/**
	 * {@inheritDoc}
	 */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	
    	float TOUCH_SCALE_FACTOR = 1.0f;
    	float inFloorDist = MyRenderer.mCamZ % MapGLRenderer.getDistBtwnFloors();
    	if ( this.getWidth() > this.getHeight())
    	{
    		TOUCH_SCALE_FACTOR = inFloorDist / (350 * screenDensity) ;
    	}
    	else
    	{
    		TOUCH_SCALE_FACTOR = inFloorDist / (550 * screenDensity) ;
    	}
    	float eps =  0.01f + 0.05f * inFloorDist;
    	int xCenterScreen = this.getWidth() / 2;
    	int yCenterScreen = this.getHeight() / 2;
    	
		float scaleToGL =  0.0019935f / screenDensity * inFloorDist;
        float xScreen = e.getX(0);
        float yScreen = e.getY(0);
        
        float diffX = (xScreen - xCenterScreen);
		float diffY = - (yScreen - yCenterScreen); // OpenGL and screen Y-axis not in the same direction
		float xVise = MyRenderer.mCamX + (diffX * scaleToGL);
		float yVise = MyRenderer.mCamY + (diffY * scaleToGL);

        switch (e.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		Log.d(this.getClass().getName(),"(xVise, yVise, zVise = (" 
        				+ xVise + ", " 
        				+ yVise + ", "
        				+ (this.MyRenderer.getCurrentFloor() * MapGLRenderer.getDistBtwnFloors()) +")" );
        		Log.d(this.getClass().getName(),"eps = " + eps);
        		Log.d(this.getClass().getName(),"xMin < xVise < xMax" + (xVise - eps) + " < " + xVise + " < " + (xVise + eps) );
        		Log.d(this.getClass().getName(),"yMin < yVise < yMax" + (yVise - eps) + " < " + yVise + " < " + (yVise + eps) );
        		
        		// If there is an object were the finger touched the screen, drags the object
         		for(int i = 0; i < MyRenderer.listMarkers.size(); i++)
                {
        			LocationMarkerGL marker = MyRenderer.listMarkers.get(i);
 					float xMarker = marker.getxCenter();
        			float yMarker = marker.getyCenter();
        			float zMarker  = marker.getzCenter();	
        			Log.d(this.getClass().getName(), "0");
        			if (xMarker >= xVise - eps) 
        			{	
        				Log.d(this.getClass().getName(), "1");
        				if (xMarker <= xVise + eps)
		         		{
        					Log.d(this.getClass().getName(), "2");
	        				if (yMarker >= yVise - eps)
	        				{
	        					Log.d(this.getClass().getName(), "3");
	        					if (yMarker <= yVise + eps)
				         		{						
	        						Log.d(this.getClass().getName(), "4");
		        					if (zMarker == this.MyRenderer.getCurrentFloor() * MapGLRenderer.getDistBtwnFloors())
					         		{
		        						Log.d(this.getClass().getName(), "succes");
		        						markerSelected = marker;
			        					isMarkerSelected = true;
			        					
			        					//Displays the info of the selected marker in a Toast
			                			Location selectedLocation = markerSelected.getLocation();
			                			String markerData = "";
			                			
			                			// Creating the displayed message
			                			markerData += "Salle : " + selectedLocation.getSalleID();
			                			
			                			if(selectedLocation.getBatimentID() != null)
			                			{
			                				if(selectedLocation.getBatimentID() != "")
			                    			{
			                					if(selectedLocation.getBatimentID() != " ")
			                        			{
			                						markerData +=  "\n" + "Bâtiment : " + selectedLocation.getBatimentID();
			                        			}
			                    			}
			                			}
			                			
			                			if(selectedLocation.getTypeSalleID() != "")
			                			{
			                				markerData +=  "\n" + "Type : " + selectedLocation.getTypeSalleID();
			                			}
			                			
			                			if( (selectedLocation.getOpeningTIME() != "") || (selectedLocation.getClosingTIME() != "") )
			                			{
			                				markerData +=  "\n" + "Horaires : ";
			                				markerData += ((selectedLocation.getOpeningTIME() == "") ? "?":selectedLocation.getOpeningTIME());
			                				markerData += "-";
			                				markerData += ((selectedLocation.getClosingTIME() == "") ? "?":selectedLocation.getClosingTIME());
			                			}
			                			
			                			
			                			Toast toast = Toast.makeText(getContext(), markerData, 500);
			                			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			                			toast.show();
	
					         		}
				         		}
	        				}
		         		}
        			}

                }
				break;
			
        	case MotionEvent.ACTION_POINTER_UP:
        		isMarkerSelected = false;
        		break;
        	
        	case MotionEvent.ACTION_UP:
        		//Log.d(this.getClass().getName(), "(!wasDragged , isMarkerSelected = " + !wasDragged + ", " + isMarkerSelected);
        		isMarkerSelected = false;
        		break;

				
	        case MotionEvent.ACTION_MOVE:
	        	Log.d(this.getClass().getName(), "MOVE");
	        	// More than one finger <=> Zoom
	        	if (e.getPointerCount() > 1) {
	        		newDist = spacing(e);
					if (newDist > 10.0f) {
						float scale = newDist/oldDist;
						
						//Zoom
						if (scale > 1.0f)
						{
							MyRenderer.changeZoom(-0.025f * MapGLRenderer.getDistBtwnFloors() / 2);
							oldDist = newDist;
						}
						
						//De-zoom
						else if (scale < 1.0f)
						{
							MyRenderer.changeZoom(0.025f * MapGLRenderer.getDistBtwnFloors() / 2);
							oldDist = newDist;
						}
					} 
	        	}
	        	
	        	// Only one finger <=> Drag
	         	else{
	         		
	         		
	         		float dx = (-xScreen - mPreviousXScreen);
	         		float dy = (yScreen - mPreviousYScreen);
	         		
					if(isMarkerSelected && markerSelected.getEnabled())
	         		{
						markerSelected.setxCenter(markerSelected.getxCenter() - dx * TOUCH_SCALE_FACTOR);
    					markerSelected.setyCenter(markerSelected.getyCenter() - dy * TOUCH_SCALE_FACTOR);
    					markerSelected.updateLocation();
    					
	         		}
	         		else
	         			// Moves the camera
         			{
        				MyRenderer.changeHPosition(dx * TOUCH_SCALE_FACTOR);
		         		MyRenderer.changeVPosition(dy * TOUCH_SCALE_FACTOR);
		         		requestRender();
         			}
	           }
	           break;
	        
        }
        mPreviousXScreen = -xScreen;
        mPreviousYScreen = yScreen;
        return true;
    }
	


	/**
	 * Adds a new {@link LocationMarkerGL} on the center of the screen and sets
	 * the {@link Location}s coordinates accordingly.
	 * 
	 * @param newLocation
	 *            {@link Location} for the {@link LocationMarkerGL}
	 */

    
	public void addNewLocation(Location newLocation, Context mContext) {
		if (newLocation == null)
			return;
		MyRenderer.addMarkerToList(newLocation);

	}
	
	
	/**
	 * Shows a {@link Location} represented by an
	 * {@link Uri}
	 * 
	 * @param uri
	 *            {@link Uri} of the {@link Location}
	 */  
	public void showLocationClickedFromList(Uri uri) {
			if (uri == null)
				return;

			long id = ContentUris.parseId(uri);
			String type = getContext().getContentResolver().getType(uri);

			if (RedpinContract.Location.ITEM_TYPE.equals(type)) {
				Location l = EntityHomeFactory.getLocationHome().getById(id);
				if (l == null) {
					return;
				}
				MyRenderer.showMarker(l);
				return;
			}

		}

	
	/**
	 * Sets the screen density
	 * 
	 * @param density New floating value of the density
	 */
	public void setscreenDensity(float density) {
		this.screenDensity  = density;	
	}

	/**
	 * Returns the {@link MapGLRenderer} contained in the {@link MapGLSurfaceView}
	 */
	public MapGLRenderer getMyRenderer() {
		return MyRenderer;
	}

	



}
