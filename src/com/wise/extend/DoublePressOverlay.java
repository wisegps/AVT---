package com.wise.extend;

import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DoublePressOverlay extends Overlay implements OnDoubleTapListener,OnGestureListener{

	private MapController mMapCtrl;
	private View mPopView;
	private GestureDetector gestureScanner = new GestureDetector(this);
	
	public DoublePressOverlay(MapController mapCtrl,View popView){
		mMapCtrl = mapCtrl;
		mPopView = popView;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return gestureScanner.onTouchEvent(event);
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		mMapCtrl.zoomIn();
		return false;
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		//¹Ø±ÕÐü¸¡´°  mPopView
		if (mPopView != null) {
			mPopView.setVisibility(View.GONE);
		}
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		return false;
	}

}
