package com.iskanbil.maps;


import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;


/**
 * Clase extendida de MapView de Google con funcionalidades como:
 * - Saber cuando se deja de arrastrar el mapa
 * - Saber cuando se hace pulsación larga
 * - Centrar el mapa en un bounds
 * @author izabala
 *
 */
public class EnhancedMapView extends MapView {
	Context mContext;
	MapLongPressInterface m_Mlpi;
	
	/**
	 * Nº de pixeles de movimiento para que no salte el evento LongPress
	 */
	public static final int N_PIXELS_TO_MOVE=15;
	private static final int LONG_PRESS_EVENT_TIME=1000;
	private long m_startDownEventTime;
	private boolean m_mapMoved;
	private boolean m_isLongPress;
	public boolean m_cancelWait=false;
	float x_down;
	float y_down;
	private OnSingleTapListener singleTapListener;
	private GestureDetector gd;
	
	private class waitForLongPress extends AsyncTask<Float,Void,Void>{

		float m_X;
		float m_Y;
		
		@Override
		protected Void doInBackground(Float... params) {
			m_X=params[0];
			m_Y=params[1];
			boolean bSalir=false;
			do{
				try {
					//Utils.LogDebug(this, "doInBackground", "waiting...");
					Thread.sleep(10);
					if (System.currentTimeMillis()-m_startDownEventTime>LONG_PRESS_EVENT_TIME && !m_mapMoved){
						m_isLongPress=true;
						getHandler().post(new Runnable(){
							@Override
							public void run() {
								
								if(m_Mlpi!=null){
									//Utils.LogDebug(this, "doInBackground", "Notificando");
									m_Mlpi.LongPressMapEvent(m_X,m_Y);
								}
								
							}
							
						});
						bSalir=true;
					}else if(System.currentTimeMillis()-m_startDownEventTime>LONG_PRESS_EVENT_TIME){
						//Utils.LogDebug(this, "doInBackground", "Pasa el tiempo");
						bSalir=true;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while(!bSalir &&!m_cancelWait);
			//Utils.LogDebug(this, "doInBackground", "Sale del bucle");
			return null;
		}
		
	}
	public EnhancedMapView(Context context, String apiKey) {
		super(context, apiKey);
		mContext = context;
		
		
	}

	public EnhancedMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	
		
	}

	public EnhancedMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		
	}

	public void setOnLongClickEventListener(MapLongPressInterface mlpi){
		m_Mlpi=mlpi;
		
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if (ev.getAction()==MotionEvent.ACTION_DOWN){
			m_startDownEventTime=System.currentTimeMillis();
			m_mapMoved=false;
			m_isLongPress=false;
			m_cancelWait=false;
			x_down=ev.getX();
			y_down=ev.getY();
			(new waitForLongPress()).execute(ev.getX(),ev.getY());
		}else if(ev.getAction()==MotionEvent.ACTION_MOVE){
			m_mapMoved=Math.sqrt(Math.pow(ev.getX()-x_down, 2)+Math.pow(ev.getY()-y_down, 2))>N_PIXELS_TO_MOVE;
			
		}else if (ev.getAction() == MotionEvent.ACTION_UP) {
			if(!m_mapMoved &&(System.currentTimeMillis()-m_startDownEventTime)<m_startDownEventTime)
				if(singleTapListener!=null && !m_isLongPress)
					singleTapListener.onSingleTap(ev);
			m_cancelWait=true;
		}
		
		return super.onTouchEvent(ev);
	}
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		if (this.gd.onTouchEvent(ev)) {
//			return true;
//		} else {
//			return super.onTouchEvent(ev);
//		}
//		//return false;
//	}

	
	
	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}
	
	

	@Override
	protected void onAnimationEnd() {
		// TODO Auto-generated method stub
		super.onAnimationEnd();
		Log.i("ANIMATION","Acaba animacion");
	}

	@Override
	public void scheduleLayoutAnimation() {
		// TODO Auto-generated method stub
		super.scheduleLayoutAnimation();
	}

	public void setMapBoundsToPois(List<GeoPoint> items, double hpadding,
			double vpadding) {

		MapController mapController = this.getController();
		// If there is only on one result
		// directly animate to that location

		if (items.size() == 1) { // animate to the location
			mapController.animateTo(items.get(0));
		} else {
			// find the lat, lon span
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;

			// Find the boundaries of the item set
			for (GeoPoint item : items) {
				int lat = item.getLatitudeE6();
				int lon = item.getLongitudeE6();

				maxLatitude = Math.max(lat, maxLatitude);
				minLatitude = Math.min(lat, minLatitude);
				maxLongitude = Math.max(lon, maxLongitude);
				minLongitude = Math.min(lon, minLongitude);
			}

			// leave some padding from corners
			// such as 0.1 for hpadding and 0.2 for vpadding
			maxLatitude = maxLatitude
					+ (int) ((maxLatitude - minLatitude) * hpadding);
			minLatitude = minLatitude
					- (int) ((maxLatitude - minLatitude) * hpadding);

			maxLongitude = maxLongitude
					+ (int) ((maxLongitude - minLongitude) * vpadding);
			minLongitude = minLongitude
					- (int) ((maxLongitude - minLongitude) * vpadding);

			// Calculate the lat, lon spans from the given pois and zoom
			mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math
					.abs(maxLongitude - minLongitude));

			// Animate to the center of the cluster of points
			mapController.animateTo(new GeoPoint(
					(maxLatitude + minLatitude) / 2,
					(maxLongitude + minLongitude) / 2));
		}
	} // end of the method´
	public void setOnSingleTapListener(OnSingleTapListener singleTapListener) {
		this.singleTapListener = singleTapListener;
	}
	
	public interface MapLongPressInterface {
		
		void LongPressMapEvent(float x,float y);

	}
	public interface MapMovementInterface {

		public void mapMoved();
	}
	public interface OnSingleTapListener {
		public boolean onSingleTap(MotionEvent e);
	}

}
