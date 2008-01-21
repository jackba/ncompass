package info.nymble.ncompass.view;

import info.nymble.measure.T;
import info.nymble.ncompass.R;

import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Presents a [target] compass display to the user. This view displays target
 * oriented information including 
 * 
 * 1. a NWSE directional bearing
 * 		! depends on location.getBearing only 
 * 2. A pointer indicating the direction to the target 
 * 		! depends on location.getBearing, .getLat, .getLon and target.getLat, .getLon
 * 
 * 
 * @author Andrew Evenson
 * 
 */
public class TargetCompass extends View {
	private final BitmapDrawable nwse = (BitmapDrawable) getResources().getDrawable(R.drawable.compass_nwse);
	private final BitmapDrawable needle = (BitmapDrawable) getResources().getDrawable(R.drawable.compass_needle);
	private final String no_bearing_message = "Current Bearing Unknown";
	private final String no_target_message = "No Target Set";
	private final Paint error_message_paint = buildErrorPaint();
	
	// variables for containing and rotating the compass images
	private Rect bounds = new Rect(); 	// the square bounding rectangle into which we draw the compass
	private float cx; 					// center of the screen on the horizontal axis
	private float cy; 					// center of the screen on the vertical axis


	private Location location = null;
	private Location target = null;
	private ProgressiveDisplay nwse_display = new ProgressiveDisplay();
	private ProgressiveDisplay needle_display = new ProgressiveDisplay();
	
	
	
	
	public TargetCompass(Context c) 
	{
		super(c);
	}

	@SuppressWarnings("unchecked")
	public TargetCompass(Context context, AttributeSet attrs,
			Map inflateParams, int defStyle) {
		super(context, attrs, inflateParams, defStyle);
	}

	@SuppressWarnings("unchecked")
	public TargetCompass(Context context, AttributeSet attrs, Map inflateParams) {
		super(context, attrs, inflateParams);
	}







	public TargetCompass(Context c, Location location) 
	{
		this(c);
		this.location = location;
	}
	
	public TargetCompass(Context c, Location location, Location target) 
	{
		this(c);
		this.location = location;
		this.target = target;
	}

	/**
	 * Alters the location that is being pointed at. The target must not be null
	 * and should include a latitude and longitude.
	 * 
	 * @param t the location to use as 'point to there'
	 */
	public void setTarget(Location t) 
	{
		target = t;
		updateView();
	}

	/**
	 * @see setTarget(Location t)
	 * 
	 * convenience method to construct a Location for the caller
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setTarget(double latitude, double longitude) {
		Location t = new Location();

		t.setLatitude(latitude);
		t.setLongitude(longitude);

		setTarget(t);
	}

	/**
	 * @return the target this compass is pointing at. Null if no target has
	 *         been set
	 */
	public Location getTarget() {
		return target;
	}

	/**
	 * Alters the location that is being displayed as the reference point for
	 * this compass. If the supplied parameter is no different from the current
	 * location, the view will not be affected (no work will be done). If the
	 * new location is different, the location will be set and the view will be
	 * invalidated and drawn on the UI thread's next pass.
	 * 
	 * @param l the location to represent as 'from here'
	 */
	public void setLocation(Location l) {
		location = l;
		updateView();
	}

	/**
	 * @see setLocation(Location l)
	 * 
	 * convenience method to construct a Location for the caller
	 * 
	 * @param latitude
	 * @param longitude
	 * @param bearing
	 */
	public void setLocation(double latitude, double longitude, float bearing) {
		Location l = new Location();

		l.setLatitude(latitude);
		l.setLongitude(longitude);
		l.setBearing(bearing);

		setLocation(l);
	}

	/**
	 * @return the location representing the current location from which the
	 *         compass points toward the target. Null if no location has been
	 *         set.
	 */
	public Location getLocation() {
		return location;
	}

	
	

	
	
	@Override
	/**
	 * The main routine of the class, draws the compass images to the screen.
	 * Uses the spinner to perform animation (transition images between
	 * the current canvas and the canvas that represents accurately the 
	 * current state).
	 */
	protected void onDraw(Canvas canvas) {
		this.onDrawBackground(canvas);
		if (nwse_display.isVisible()) 
		{
			canvas.save();
			canvas.rotate(nwse_display.getBearing(), cx, cy);
			nwse.draw(canvas);
			

			if (needle_display.isVisible()) 
			{
				canvas.rotate(needle_display.getBearing(), cx, cy);
				needle.draw(canvas);
				canvas.restore();
			} else 
			{
				canvas.restore();
				canvas.drawText(no_target_message, cx, cy, error_message_paint);
			}
		} 
		else 
		{
			canvas.drawText(no_bearing_message, cx, cy, error_message_paint);
		}
		if (!nwse_display.isStable() | !needle_display.isStable()) postInvalidate();
	}

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i(null, "onSizeChanged w=" + w + " h=" + h);
		super.onSizeChanged(w, h, oldw, oldh);
		setDimensions(w, h);
	}
	
	
	
	
	
		
	/**
	 * Updates the display controls with the new target and location.
	 * If the displays become unstable, will postInvalidate to ensure 
	 * the window is redrawn.
	 */
	private void updateView()
	{
		boolean showNWSE = (location != null && location.hasBearing());
		boolean showTarget = (showNWSE && target != null);
		
		if (showNWSE)
		{			
			nwse_display.setBearing( (int)-location.getBearing() );
		}
		else
		{
			nwse_display.clearBearing();
		}
			
		if (showTarget)
		{
			needle_display.setBearing( (int)location.bearingTo(target) );
		}
		else
		{
			needle_display.clearBearing();
		}

		if (!nwse_display.isStable() | !needle_display.isStable()) postInvalidate();
	}
	
	/**
	 * Manufactures a paint object that is suited to drawing error messages on
	 * the compass window.
	 * 
	 * @return the constructed paint object
	 */
	private Paint buildErrorPaint() {
		Paint p = new Paint();
		p.setARGB(255, 255, 0, 0);
		p.setTextAlign(Paint.Align.CENTER);
		p.setTextSize(14);
		p.setTypeface(Typeface.create("Georgia", Typeface.BOLD));
		p.setAntiAlias(true);

		return p;
	}

	/**
	 * Makes sure that the drawing parameters for the class are correctly set to
	 * the current screen dimensions. The height and width of the screen are
	 * used to set these values. They are only corrected if the mMeasuredHeight
	 * or mMeasuredWidth of the view are changed.
	 */
	private void setDimensions(int w, int h) 
	{
		w = w - getPaddingLeft() - getPaddingRight();
		h = h - getPaddingTop() - getPaddingBottom();
		cx = w / 2 + getPaddingLeft();
		cy = h / 2 + getPaddingTop();
		setSquareBounds(h, w, (int)cx, (int)cy, bounds);

		nwse.setBounds(bounds);
		needle.setBounds(bounds);
	}

	/**
	 * calculates the largest square that is smaller than h X w
	 * 
	 * @param h max height of the square
	 * @param w max width of the square
	 * 
	 * @return a square that fits the dimensions
	 */
	private void setSquareBounds(int h, int w, int cx, int cy, Rect r) {
		int radius = Math.min(w, h)/2;

		r.left = cx - radius;
		r.right = cx + radius;
		r.top = cy  - radius;
		r.bottom = cy + radius;
	}
	
	
	
	
	
	/**
	 * Internal class to handle the animation of the compass
	 * face for intermediate positions while turning the graphics
	 * to their destination rotations. Operates by moving the position
	 * toward the destination each time it is retrieved. The 
	 * isStabilized function answers whether the device has reached
	 * a stable state where the represented position and the 
	 * destination are equivalent. 
	 * 
	 * Whenever the target or nwse are not displayed, setting the
	 * target or nwse will cause them to appear immediately in their 
	 * correct positions without intermediate animation. 
	 * 
	 * 
	 * @author Andrew Evenson
	 *
	 */
	private static class ProgressiveDisplay
	{
		private final T watch = new T("stabilization interval");
		private final int degree_error_tolerance = 10;
		
		private boolean wasDestablized = false;
		private boolean visible = false;
		private boolean visibleReported = false;
		private boolean exceededTolerance = false;
		private int bearing = 0;
		private int bearing_current = 0;

		private int speed = 1;
		
		
		public void setBearing(int bearing)
		{
			visible = true;
			this.bearing = bearing;
			exceededTolerance = exceedsDifference(this.bearing, bearing_current);
		}
		
		public void clearBearing()
		{
			visible = false;
		}
	
		
		public boolean isStable()
		{
			boolean isStable = visible == visibleReported &&
								!exceededTolerance;
			
			
			if (isStable && wasDestablized)
			{
				wasDestablized = false;
				watch.stop();
			}
			else if (!isStable && !wasDestablized)
			{
				wasDestablized = true;
				watch.start();
			}
			
			return isStable;
		}
		
		
		
		public boolean isVisible()
		{
			visibleReported = visible;
			
			return visibleReported;
		}
		
		public float getBearing()
		{
			bearing_current = rotate(bearing_current, bearing);
			exceededTolerance = (bearing_current != bearing);
			
			return bearing_current;
		}


		
		
		
		
		/**
		 * Calculates the rotation angle that should next be
		 * used when traveling between from and to given the 
		 * current speed
		 * 
		 * @param from the current angle
		 * @param to the destination angle
		 * @return a value between from and to that represents
		 * the next best angle to represent given the current
		 * speed.
		 */
		private int rotate(int from, int to)
		{
			int d = (int)(to - from) % 360;		
			if (d > 180) d -= 360;
			if (d < -180)d += 360;
			int v = (d < 0 ? -1 : 1);
			
			if (d == 0)
			{
				speed = 1;
				return to;
			}
			else if (d*v > 12)
			{
				speed = Math.max(speed++, 8);
				return speed*v + from;
			}
			else
			{
				speed = Math.min(speed--, 1);
				return speed*v + from;
			}
		}
		
		
		
		private boolean exceedsDifference(int i1, int i2)
		{
			return Math.abs(i1 - i2) > degree_error_tolerance;
		}
	}
}



