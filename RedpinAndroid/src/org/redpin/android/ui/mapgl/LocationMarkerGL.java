package org.redpin.android.ui.mapgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import org.redpin.android.core.Location;
import org.redpin.android.net.home.LocationRemoteHome;
import org.redpin.android.ui.mapview.LocationMarker;
import org.redpin.android.ui.mapview.LocationMarkerAnnotation;

import android.util.Log;

  
public class LocationMarkerGL{
   private FloatBuffer vertexBuffer;  // Buffer for vertex-array
   private FloatBuffer colorBuffer;   // Buffer for color-array
   private ByteBuffer indexBuffer;    // Buffer for index-array
   
   private float xCenter = 0f;
   private float yCenter = 0f;
   private float zCenter = 0;
   private float size = 0.05f;
   
   private Location location;
   private boolean enabled = false;
   private boolean createdThisSession = false;
    
   private float[] vertices = { // 5 vertices of the pyramid in (x,y,z)
      -0.5f, -0.5f, 1.0f,  // 0. left-bottom-back
      0.5f, -0.5f, 1.0f,  // 1. right-bottom-back
      0.5f, 0.5f,  1.0f,  // 2. right-bottom-front
      -0.5f, 0.5f,  1.0f,  // 3. left-bottom-front
       0.0f,  0.0f,  0.0f   // 4. top
   };
          
   private float[] colors = {  // Colors of the 5 vertices in RGBA
		   	  0.0f, 0.0f, 1.0f, 1.0f,
		      0.0f, 0.0f, 1.0f, 1.0f,
		      0.0f, 0.0f, 1.0f, 1.0f,
		      0.0f, 0.0f, 1.0f, 1.0f,
		      1.0f, 0.0f, 0.0f, 1.0f,   // top of the pyramid
};
  
   private byte[] indices = { // Vertex indices of the 4 Triangles
      2, 4, 3,   // front face (CCW)
      1, 4, 2,   // right face
      0, 4, 1,   // back face
      4, 0, 3    // left face
   };

  
   // Constructor - Set up the buffers
   public LocationMarkerGL(Location mLocation) {
	   this();
	   setLocation(mLocation);
	   updateLocation();
   }
   
   public LocationMarkerGL() {
	   // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
      ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
      vertexBuffer.put(vertices);         // Copy data into buffer
      vertexBuffer.position(0);           // Rewind
  
      // Setup color-array buffer. Colors in float. An float has 4 bytes
      ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
      cbb.order(ByteOrder.nativeOrder());
      colorBuffer = cbb.asFloatBuffer();
      colorBuffer.put(colors);
      colorBuffer.position(0);
  
      // Setup index-array buffer. Indices in byte.
      indexBuffer = ByteBuffer.allocateDirect(indices.length);
      indexBuffer.put(indices);
      indexBuffer.position(0);

   }
   
  
   // Draw the shape
   public void draw(GL10 gl) {
      gl.glFrontFace(GL10.GL_CCW);  // Front face in counter-clockwise orientation
  
      // Enable arrays and define their buffers
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
      gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
      gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
      
      
      
      gl.glTranslatef(xCenter, yCenter, zCenter);
      gl.glScalef(size, size, size);
      
      gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE,
            indexBuffer);
     
      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
      
      
   }
	
	public float getxCenter() {
		return xCenter;
	}

	public void setxCenter(float xCenter) {
		this.xCenter = xCenter;
	}

	public float getyCenter() {
		return yCenter;
	}

	public void setyCenter(float yCenter) {
		this.yCenter = yCenter;
	}

	public float getzCenter() {
		return zCenter;
	}

	public void setzCenter(float zCenter) {
		this.zCenter = zCenter;
	}

	public void setSize(float size) {
		this.size = size;
	}
	
	
	
	
	
	/**
	 * Sets the markers location
	 * 
	 * @param l
	 *            {@link Location}
	 */
	private void setLocation(Location l) {
		this.location = l;
		xCenter = location.getMapXcord();
		yCenter = location.getMapYcord();
		zCenter = location.getMapZcord();
	}

	/**
	 * 
	 * @return {@link Location} represented by the {@link LocationMarker}
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Updates the coordinates of the locations and saves the change on the
	 * server
	 */
	public void updateLocation() {
		updateLocationCords();
		LocationRemoteHome.updateLocation(location);
	}

	/**
	 * Updates the coordinates
	 */
	private void updateLocationCords() {
		location.setMapXcord(xCenter);
		location.setMapYcord(yCenter);
		location.setMapZcord(zCenter);
	}
	
	
	public void setEnabled(boolean b) {
			enabled = b;
	}
	public boolean getEnabled() {
		return this.enabled;
	}
	
	public boolean isCreatedThisSession() {
		return createdThisSession;
	}

	public void setCreatedThisSession(boolean createdThisSession) {
		this.createdThisSession = createdThisSession;
	}
	
	public void setColors(float red, float green, float blue, float pointRed, float pointGreen, float pointBlue)
	{
		for (int i = 0; i < 16; i = i + 4)
		   {
			   this.colors[i] = red;
			   this.colors[i + 1] = green;
			   this.colors[i + 2] = blue;
			   this.colors[i + 3] = 1.0f;
		   }
		   for (int i = 16; i < 20; i = i + 4)
		   {
			   this.colors[i] = pointRed;
			   this.colors[i + 1] = pointGreen;
			   this.colors[i + 2] = pointBlue;
			   this.colors[i + 3] = 1.0f;
		   }
		   
		   // Setup color-array buffer. Colors in float. An float has 4 bytes
		      ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		      cbb.order(ByteOrder.nativeOrder());
		      colorBuffer = cbb.asFloatBuffer();
		      colorBuffer.put(colors);
		      colorBuffer.position(0);
	}
}