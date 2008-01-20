package info.nymble.ncompass.view;

import info.nymble.measure.Stopwatch;
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
import android.view.MotionEvent;
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
	private final BitmapDrawable cover = (BitmapDrawable) getResources().getDrawable(R.drawable.compass_cover);
	private final String no_bearing_message = "Current Bearing Unknown";
	private final String no_target_message = "No Target Set";
	private final Paint error_message_paint = buildErrorPaint();
	private final float degree_error_tolerance = 5.0F;
	
	// variables for containing and rotating the compass images
	private Rect bounds = new Rect(); 	// the square bounding rectangle into which we draw the compass
	private float cx; 					// center of the screen on the horizontal axis
	private float cy; 					// center of the screen on the vertical axis

	private Location displayLocation = null;
	private Location displayTarget = null;
	private Location location = null;
	private Location target = null;
	
	private Spinner spinner = new Spinner();
	
	
	
	
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
	public void setTarget(Location t) {
		if (t != null) {
			if (target == null || t.getLatitude() != target.getLatitude()
					|| t.getLongitude() != target.getLongitude()) {
				target = t;
				invalidateIfNecessary();
			}
		}
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
		if (l != null) {
			if (location == null || l.getLatitude() != location.getLatitude()
					|| l.getLongitude() != location.getLongitude()
					|| l.getBearing() != location.getBearing()) {
				location = l;
				invalidateIfNecessary();
			}
		}
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
	 */
	protected void onDraw(Canvas canvas) {
		Stopwatch.start();

		this.onDrawBackground(canvas);
		if (location != null && location.hasBearing()) 
		{
			canvas.save();
			canvas.rotate(spinner.getNWSE(), cx, cy);
			nwse.draw(canvas);
			

			if (target != null) 
			{
				canvas.rotate(spinner.getTarget(), cx, cy);
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

		if (!spinner.isStable()) postInvalidate();
		Stopwatch.stop("redrawing compass");
	}

	
	

	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i(null, "onSizeChanged w=" + w + " h=" + h);
		super.onSizeChanged(w, h, oldw, oldh);
		setDimensions(w, h);
	}

	@Override
	public boolean onMotionEvent(MotionEvent event) {
		// mContext.getContentResolver().insert(Recent.CONTENT_URI, null);
		// mContext.startActivity(new Intent(Intent.VIEW_ACTION,
		// Recent.CONTENT_URI));

		if (event.getAction() == MotionEvent.ACTION_UP) {
			Location target = new Location();
			target.setLatitude(37.447524150941874);
			target.setLongitude(-122.11882744124402);

//			setLocation(tracker.getCurrentLocation());
			setTarget(target);
		}

		return true;
	}
	
	
	
	
	
	
	/**
	 * We should only redraw the window if the location or
	 * target that is being displayed is inaccurate by more
	 * than degree_error_tolerance.
	 * 
	 * TODO assumes Location.bearingTo(null) does not throw an error
	 */
	private void invalidateIfNecessary()
	{
		boolean redraw = (displayLocation == null ^ location == null) ||
						(displayTarget == null ^ target == null) ||
						(location != null && 
								( exceedsDifference (location.getBearing(), displayLocation.getBearing()) ||
								  exceedsDifference (location.bearingTo(target), displayLocation.bearingTo(displayTarget)))
						);
		
		Log.i(null, "invalidation step");
		Log.i(null, "dl=" + displayLocation);
		Log.i(null, "dt=" + displayTarget);
		Log.i(null, "l=" + location);
		Log.i(null, "t=" + target);
		if (location != null)
		{
			Log.i(null, "bearing=" + location.getBearing());
			if (target != null) Log.i(null, "bearingTo=" + location.bearingTo(target));
		}
		if (displayLocation != null)
		{
			Log.i(null, "display bearing=" + displayLocation.getBearing());
			if (displayTarget != null) Log.i(null, "displayBearingTo=" + displayLocation.bearingTo(displayTarget));
		}
		
		
		
		
		
		if (redraw)
		{
			Log.i(null, "redrawing compass");
			spinner.setUnstable(location, target);
			displayLocation = location;
			displayTarget = target;
			postInvalidate();
		}
	}
	
	
	private boolean exceedsDifference(float f1, float f2)
	{
		return Math.abs(f1 - f2) > this.degree_error_tolerance;
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
	 * @author Andrew Evenson
	 *
	 */
	private class Spinner
	{
		private boolean initializedNWSE = false;
		private boolean initializedTarget = false;
		
		
		private int nwse_bearing = 0;
		private int target_bearing = 0;
		private int nwse_bearing_current = 0;
		private int target_bearing_current = 0;
		
		private int speed = 1;
		
		
		public void setUnstable(Location l, Location t)
		{
			if (l != null)
			{
				nwse_bearing = (int)-l.getBearing();
				if (!initializedNWSE)
				{
					nwse_bearing_current = nwse_bearing;
					initializedNWSE = true;
				}

				if (t!= null)
				{					
					target_bearing = (int)l.bearingTo(t);
					if (!initializedTarget)
					{
						target_bearing_current = target_bearing;
						initializedTarget = true;
					}
				}
				int d = (int)(target_bearing - target_bearing_current) % 360;
				if (d > 180) d -= 360;
				if (d < -180) d += 360;
				Log.i(null,"retargeting tb"+target_bearing + " tbc=" + target_bearing_current + " d=" + d);
			}
		}
		
		public float getNWSE()
		{
			nwse_bearing_current = rotate(nwse_bearing_current, nwse_bearing);
			return nwse_bearing_current;
		}
		
		public float getTarget()
		{
			target_bearing_current = rotate(target_bearing_current, target_bearing);
			return target_bearing_current;
		}
		
		public boolean isStable()
		{
			boolean isStable = nwse_bearing == nwse_bearing_current && target_bearing == target_bearing_current;
			
			if (isStable) Log.i(null, "compass stabilized");
			return isStable;
		}
		
		
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
	}
}


