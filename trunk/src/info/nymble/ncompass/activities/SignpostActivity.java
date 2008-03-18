package info.nymble.ncompass.activities;

/* 
 * Copyright (C) 2007 Google Inc.
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


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceView;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * Example of how to use OpenGL|ES in a custom view
 *
 */

public class SignpostActivity extends Activity {
    
    @Override
	protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);     
        setContentView(new GLView( getApplication() ));
    }
    
    @Override
	protected void onResume()
    {
        super.onResume();
    	//android.os.Debug.startMethodTracing("/tmp/trace/GLView1.dmtrace",
        //  8 * 1024 * 1024);
    }
    
    @Override
	protected void onStop()
    {
        super.onStop();
        //android.os.Debug.stopMethodTracing();
    }
}

class GLView extends SurfaceView
{
	Sign sign = new Sign(120000, 40000, 10000);
	
    /**
     * The View constructor is a good place to allocate our OpenGL context
     */
    public GLView(Context context)
    {
        super(context);
        
        /* 
         * Create an OpenGL|ES context. This must be done only once, an
         * OpenGL contex is a somewhat heavy object.
         */
        mGLContext = new OpenGLContext(0);
        mCube = new Cube();
        mAnimate = false;
    }
    
    /*
     * Start the animation only once we're attached to a window
     * @see android.view.View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        mAnimate = false;
        mAngle = 200.0F;
        Message msg = mHandler.obtainMessage(INVALIDATE);
        mNextTime = SystemClock.uptimeMillis();
        mHandler.sendMessageAtTime(msg, mNextTime);
        super.onAttachedToWindow();
    }
    
    /*
     * Make sure to stop the animation when we're no longer on screen,
     * failing to do so will cause most of the view hierarchy to be
     * leaked until the current process dies.
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        mAnimate = false;
        super.onDetachedFromWindow();
    }

    /**
     * Draw the view content
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
    	if (true) {
        /*
         * First, we need to get to the appropriate GL interface.
         * This is simply done by casting the GL context to either
         * GL10 or GL11.
         */
    		
    	canvas.drawColor(0x66FFFFFF);
        GL10 gl = (GL10)(mGLContext.getGL());
        
        /*
         * Before we can issue GL commands, we need to make sure all
         * native drawing commands are completed. Simply call
         * waitNative() to accomplish this. Once this is done, no native
         * calls should be issued.
         */
        mGLContext.waitNative(); //canvas, this);
        
            int w = getWidth();
            int h = getHeight();

            /*
             * Set the viewport. This doesn't have to be done each time
             * draw() is called. Typically this is called when the view
             * is resized.
             */


            gl.glViewport(0, 0, w, h);
        
            /*
             * Set our projection matrix. This doesn't have to be done
             * each time we draw, but usualy a new projection needs to be set
             * when the viewport is resized.
             */
             
            float ratio = (float)w / h;
            gl.glMatrixMode(gl.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 2, 12);

            /*
             * dithering is enabled by default in OpenGL, unfortunattely
             * it has a significant impact on performace in software
             * implementation. Often, it's better to just turn it off.
             */
             gl.glDisable(gl.GL_DITHER);

            /*
             * Usually, the first thing one might want to do is to clear
             * the screen. The most efficient way of doing this is to use
             * glClear(). However we must make sure to set the scissor
             * correctly first. The scissor is always specified in window
             * coordinates:
             */

            gl.glClearColor(1,1,1,1);
            gl.glEnable(gl.GL_SCISSOR_TEST);
            gl.glScissor(0, 0, w, h);
            gl.glClear(gl.GL_COLOR_BUFFER_BIT);


            
            
            
            gl.glEnable(gl.GL_LIGHTING);
            gl.glEnable(gl.GL_LIGHT0);
            gl.glLightf(gl.GL_LIGHT0, gl.GL_AMBIENT_AND_DIFFUSE, 0.6F);
            gl.glMaterialf(gl.GL_FRONT_AND_BACK, gl.GL_AMBIENT_AND_DIFFUSE, 0.7F);
            
            
            
            /*
             * Now we're ready to draw some 3D object
             */

            gl.glMatrixMode(gl.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -3.0f);
            gl.glScalef(0.5f, 0.5f, 0.5f);
            gl.glRotatef(mAngle,        0, 1, 0);
            gl.glRotatef(mAngle*0.25f,  1, 0, 0);

            gl.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
            gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
            gl.glEnableClientState(gl.GL_COLOR_ARRAY);
            gl.glEnable(gl.GL_CULL_FACE);

            sign.draw(gl);
            
            
 
            
            mAngle += 1.2f;

        /*
         * Once we're done with GL, we need to flush all GL commands and
         * make sure they complete before we can issue more native
         * drawing commands. This is done by calling waitGL().
         */
        mGLContext.waitGL();
    	}
    }
    

    // ------------------------------------------------------------------------

    private static final int INVALIDATE = 1;

    private final Handler mHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
            if (mAnimate && msg.what == INVALIDATE) {
                invalidate();
                msg = obtainMessage(INVALIDATE);
                long current = SystemClock.uptimeMillis();
                if (mNextTime < current) {
                    mNextTime = current + 20;
                }
                sendMessageAtTime(msg, mNextTime);
                mNextTime += 20;
            }
        }
    };

    private OpenGLContext   mGLContext;
    private Cube            mCube;
    private float           mAngle;
    private long            mNextTime;
    private boolean         mAnimate;
}


class Cube
{
    public Cube()
    {
        int one = 0x10000;

        int vertices[] = {
               -one, -one, -one/2,
                one, -one, -one/2,
                one,  one, -one/2,
               -one,  one, -one/2,
               -one, -one,  one/2,
                one, -one,  one/2,
                one,  one,  one/2,
               -one,  one,  one/2,
                2*one, 	0, -one/2,
                2*one, 	0, 	one/2
            };
            
        int colors[] = {
                  0,    0,    0,  one,
                one,    0,    0,  one,
                one,  one,    0,  one,
                  0,  one,    0,  one,
                  0,    0,  one,  one,
                one,    0,  one,  one,
                one,  one,  one,  one,
                  0,  one,  one,  one,
                  0,  one,  one,  one,
                  0,  one,  one,  one
            };
        
        for (int i = 0; i < 10; i++) 
        {
			colors[4*i] = 0;
			colors[4*i+1] = 0;
			colors[4*i+2] = 0;
			colors[4*i+3] = 0;
		}
        

        byte indices[] = {
                0, 4, 5,    0, 5, 1,
                2, 6, 7,    2, 7, 3,
                3, 7, 4,    3, 4, 0,
                4, 7, 6,    4, 6, 5,
                3, 0, 1,    1, 2, 3, 
                6, 9, 5,	2, 1, 8,
                2, 9, 6,	2, 8, 9,
                1, 5, 9,	1, 9, 8
        };

	// Buffers to be passed to gl*Pointer() functions
	// must be direct, i.e., they must be placed on the
	// native heap where the garbage collector cannot
	// move them.
    //
    // Buffers with multi-byte datatypes (e.g., short, int, float)
    // must have their byte order set to native order

    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
    vbb.order(ByteOrder.nativeOrder());
    mVertexBuffer = vbb.asIntBuffer();
	mVertexBuffer.put(vertices);
	mVertexBuffer.position(0);

    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
    cbb.order(ByteOrder.nativeOrder());
	mColorBuffer = cbb.asIntBuffer();
	mColorBuffer.put(colors);
	mColorBuffer.position(0);

	mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
	mIndexBuffer.put(indices);
	mIndexBuffer.position(0);
    }
    
    public void draw(GL10 gl)
    {
        gl.glFrontFace(gl.GL_CW);
        gl.glVertexPointer(3, gl.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, gl.GL_FIXED, 0, mColorBuffer);
        gl.glDrawElements(gl.GL_TRIANGLES, 48, gl.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
    
    private IntBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}






class Sign
{
    private IntBuffer vertices;
    private IntBuffer colors;
    private IntBuffer normals;
    private ByteBuffer shape;
	private int size=0; 
	
	
	
    public Sign(int w, int h, int d)
    {
    	int px = (w >> 3) + w;
    	int py = h >> 1;
    	
    	int[] v = new int[]
        {
    			0, 0, 0,
    			w, 0, 0,
    			0, h, 0,
    			w, h, 0,
    			px, py, 0,
    			0, 0, d,
    			w, 0, d,
    			0, h, d,
    			w, h, d,
    			px, py, d
        };
    	
    	
    	int[] color = new int[]{0, 0x6E, 0x5B, 0x3E};
    	int[] c = new int[40];
    	
    	for (int i = 0; i < v.length; i += 4) 
    	{
    		System.arraycopy(color, 0, c, i, 4);
		}
    	
    	int[] n = new int[]{
    			0, 0, -1,
    			0, 0, -1,
    			0, 0, -1,
    			
    			0, 1, 0,
    			0, 1, 0,
    			0, 1, 0,
    			0, 1, 0,
    			
    			0, -1, 0,
    			0, -1, 0,
    			0, -1, 0,
    			0, -1, 0,
    			
    			-1, 0, 0, 
    			-1, 0, 0, 
    			
    			0, 0, 1,
    			0, 0, 1,
    			0, 0, 1
    	};
    	
    	
    	

    	byte[] s = new byte[]{
    			0, 2, 1,
    			2, 3, 1,
    			3, 4, 1,
    			
    			2, 7, 8, 
    			2, 8, 3,
    			3, 8, 9,
    			3, 9, 4,
    			
    			5, 0, 1,
    			5, 1, 6,
    			6, 1, 4,
    			6, 4, 9,
    			
    			2, 0, 5,
    			2, 5, 7,
    			
    			5, 6, 7,
    			7, 6, 8,
    			8, 6, 9
    	};
    	
    	

    	vertices = buildBuffer(v);
    	colors = buildBuffer(c);
    	normals = buildBuffer(n);
    	shape = buildBuffer(s);
    	size = s.length;
    }
    
    
    
    
    public void draw(GL10 gl)
    {
    	gl.glFrontFace(gl.GL_CCW);
    	gl.glVertexPointer(3, gl.GL_FIXED, 0, vertices);
    	gl.glColorPointer(4, gl.GL_FIXED, 0, colors);
    	gl.glNormalPointer(gl.GL_FIXED, 0, normals);
    	
    	gl.glDrawElements(gl.GL_TRIANGLES, size, gl.GL_UNSIGNED_BYTE, shape);
    }
    
    
    
    
    static IntBuffer buildBuffer(int[] ints)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ints.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
    	IntBuffer intBuffer = byteBuffer.asIntBuffer();
    	intBuffer.put(ints);
    	intBuffer.position(0);
    	
    	return intBuffer;
    }
    
    
    static FloatBuffer buildBuffer(float[] floats)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(floats.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(floats);
        floatBuffer.position(0);
    	
    	return floatBuffer;
    }
    
    
    
    static ByteBuffer buildBuffer(byte[] bytes)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.position(0);
    	
    	return byteBuffer;
    }
}


