package com.wise.extend;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
/**
 * 当前位置
 * @author 唐飞
 *
 */
public class MeOverlay extends Overlay{
	GeoPoint geoPoint;
	Bitmap bitmap;
	float Accuracy;
	public MeOverlay(GeoPoint geoPoint,Bitmap bitmap,float Accuracy){
		this.geoPoint = geoPoint;
		this.bitmap = bitmap;
		this.Accuracy = Accuracy;
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
        Point myScreenCoords = new Point();
        mapView.getProjection().toPixels(geoPoint, myScreenCoords);
        canvas.drawBitmap(bitmap, myScreenCoords.x, myScreenCoords.y, paint);
        
        Paint innerPaint = new Paint();  
        Paint borderPaint = new Paint();  
        innerPaint.setARGB(75, 167, 222, 224); // darkgray  
        borderPaint.setARGB(75, 4, 110, 114); // white  
        borderPaint.setAntiAlias(true); // 抗锯齿  
        borderPaint.setStyle(Style.STROKE); //描边，和Style.Fill相对  
        borderPaint.setStrokeWidth(2);  
        
        Projection projection = mapView.getProjection();
		Point p1 = new Point();
		projection.toPixels(geoPoint, p1);
		
		canvas.drawCircle(p1.x, p1.y, projection.metersToEquatorPixels(Accuracy), innerPaint);
		canvas.drawCircle(p1.x, p1.y, projection.metersToEquatorPixels(Accuracy), borderPaint);
		
		return true;
	}
}
