package org.redpin.android.ui.mapgl;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

/**
 * Rectangular structure with a given 3D position (Z-Axis perpendicular to the screen) and a map picture used as a texture
 * 
 * @author Benjamin Mesre
 *
 */
public class FloorMap {
	// Our vertices.
	private float vertices[] = new float[12];
	
	// The order we like to connect them.
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
	
	// Our vertex buffer.
	private FloatBuffer vertexBuffer;

	// Our index buffer.
	private ShortBuffer indexBuffer;
	
	// The id of the picture stored in the 'drawable' folder that will be used as a texture
	private int idImg;
	
	
	// Our buffer holding the texture coordinates
	private FloatBuffer textureBuffer;
	
	private float texture[] = new float[8];
	
	// The texture pointer
	private int[] textures = new int[1];

	private float mFloor;
	
	
	/** 
	 * Constructor
	 * 
	 * @param length Length that will have the displayed structure
	 * @param width Width that will have the displayed structure
	 * @param numFloor Floating value (floor) where the rectangular structure will be displayed
	 * @param idImg id of the map picture that will be used as a texture on the rectangular structure
	 */
	public FloorMap(float length, float width, float numFloor, int idImg) {
		
		//Determination of the map corners
		//Upper-left
		this.vertices[0] = 0;
		this.vertices[1] = width;
		this.vertices[2] = numFloor;
		
		//Lower-left
		this.vertices[3] = 0;
		this.vertices[4] = 0;
		this.vertices[5] = numFloor;
		
		//Lower-right
		this.vertices[6] = length;
		this.vertices[7] = 0;
		this.vertices[8] = numFloor;
		
		//Upper-right
		this.vertices[9] = length;
		this.vertices[10] = width;
		this.vertices[11] = numFloor;
		
		//Determination of the texture corners
		
		//Lower-left
		this.texture[0] = 0;
		this.texture[1] = 0;
		
		//Upper-left
		this.texture[2] = 0;
		this.texture[3] = 1.0f;
		
		//Upper-right
		this.texture[4] = 1.0f;
		this.texture[5] = 1.0f;
		
		//Lower-right
		this.texture[6] = 1.0f;
		this.texture[7] = 0;
		
		// a float is 4 bytes, therefore we multiply the number of vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// short is 2 bytes, therefore we multiply the number of vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texture.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
		this.idImg = idImg;
		this.mFloor = numFloor;
	}
	
	
	/**
	 * Load the texture for the {@link FloorMap}
	 * @param gl
	 * @param context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		// loading texture
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				idImg);

		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		// Clean up
		bitmap.recycle();
	}
	
	
	/**
	 * Draws the rectangular structure on the screen.
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
				
		//Activates transparency
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// Handle color and transparency of the object (RGB+Alpha)
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		// Draws the triangles
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, 
				GL10.GL_UNSIGNED_SHORT, indexBuffer);
		
		//Deactivates transparency
		gl.glDisable(GL10.GL_BLEND);
		
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	/**
	 * Returns the floor of the {@link FloorMap}
	 */
	public float getFloor() {
		return mFloor;
	}
	
}

