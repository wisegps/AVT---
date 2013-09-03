package com.wise.extend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
/**
 * 划线
 * @author 唐飞
 *
 */
public class LineOverlay extends Overlay {
	GeoPoint geopoint1;
	GeoPoint geopoint2;
	/**
	 * 2点之间划线
	 * @param geoPoint1
	 * @param geoPoint2
	 */
	public LineOverlay(GeoPoint geoPoint1, GeoPoint geoPoint2) {
		this.geopoint1 = geoPoint1;
		this.geopoint2 = geoPoint2;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(5);
		Projection projection = mapView.getProjection();
		Point p1 = new Point();
		Point p2 = new Point();
		projection.toPixels(geopoint1, p1);
		projection.toPixels(geopoint2, p2);
		Path path = new Path();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		canvas.drawPath(path, paint);
	}
}
