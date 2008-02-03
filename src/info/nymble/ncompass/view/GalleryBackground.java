package info.nymble.ncompass.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

public class GalleryBackground extends Drawable
{
	private Bitmap bitmap = null;
	private Paint fillPaint = new Paint();
	private Paint borderPaint = new Paint();
	private int alpha = 0x99;

	private int centerWidth = 100;
	private float wingHeightFactor = 0.8F;

	


	
	
	public GalleryBackground(int centerWidth, float wingHeightFactor)
	{
		this.wingHeightFactor = wingHeightFactor;
		this.centerWidth = centerWidth;	

		fillPaint.setAntiAlias(true);
		fillPaint.setColor(0x000000);
		fillPaint.setAlpha(alpha);
		fillPaint.setStyle(Style.FILL);
		
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(0xFFFFFF);
		borderPaint.setAlpha(255);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);	
	}

	
	
	@Override
	protected void onBoundsChange(Rect bounds) 
	{
		super.onBoundsChange(bounds);
		bitmap = null;
	}

	@Override
	public int getOpacity() 
	{
		return alpha;
	}

	@Override
	public void setAlpha(int alpha) 
	{
		this.alpha = alpha;
		bitmap = null;
	}
	
	
	public void setFillColor(int r, int g, int b)
	{
		fillPaint.setARGB(alpha, r, g, b);
	}
	
	public void setFillColor(int color)
	{
		fillPaint.setColor(color);
	}
	
	public void setBorderColor(int r, int g, int b)
	{
		borderPaint.setARGB(alpha, r, g, b);
	}
	
	public void setBorderColor(int color)
	{
		borderPaint.setColor(color);
	}
	
	
	
	

	@Override
	public void clearColorFilter() 
	{
		super.clearColorFilter();
		fillPaint.setColorFilter(null);
		borderPaint.setColorFilter(null);
	}

	@Override
	public void setColorFilter(int color, Mode mode) 
	{
		ColorFilter filter = PorterDuffColorFilter.create(color, mode);
		fillPaint.setColorFilter(filter);
		borderPaint.setColorFilter(filter);
	}
	
	
	@Override
	public void draw(Canvas canvas) 
	{
		if (this.bitmap == null) bitmap = buildImage();
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	
	
	
	private Bitmap buildImage()
	{
		Rect bounds = getBounds();
		int boundsHeight = bounds.bottom - bounds.top;
		int boundsWidth = bounds.right - bounds.left;

		int edgeHeight = (int)(this.wingHeightFactor*boundsHeight);
		RectF edge = new RectF(0, boundsHeight - edgeHeight, boundsWidth, boundsHeight + 10);
		
		int centerWidth = Math.min(this.centerWidth, boundsWidth);
		float centerLeft = (boundsWidth-centerWidth)/2;
		RectF center = new RectF(centerLeft, 2, centerLeft + centerWidth, boundsHeight + 10);
		
		Bitmap bitmap = Bitmap.createBitmap(boundsWidth, boundsHeight, true);
		Canvas canvas = new Canvas(bitmap);
		
		fillPaint.setAlpha(alpha);
		canvas.save();
		canvas.clipRect(0, 0, centerLeft, boundsHeight);
		canvas.drawRoundRect(edge, 10, 10, fillPaint);
		canvas.drawRoundRect(edge, 10, 10, borderPaint);
		canvas.restore();
		
		canvas.save();
		canvas.clipRect(centerLeft + centerWidth, 0, boundsWidth, boundsHeight);
		canvas.drawRoundRect(edge, 10, 10, fillPaint);
		canvas.drawRoundRect(edge, 10, 10, borderPaint);
		canvas.restore();
		
		fillPaint.setAlpha(alpha + 30);
		canvas.clipRect(centerLeft, 0, centerLeft + centerWidth, boundsHeight);
		canvas.drawRoundRect(center, 10, 10, fillPaint);
		canvas.drawRoundRect(center, 10, 10, borderPaint);

		return bitmap;
	}
}
