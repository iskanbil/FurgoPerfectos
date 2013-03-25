package com.iskanbil.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.iskanbil.furgoperfectos.R;

/**
 * Overlay para mostrar el resultado de una búsqueda de dirección 
 * @author izabala
 *
 */
public class FPInfoOverlay extends Overlay {

	private GeoPoint mPosicion;
	private Context context;

	public FPInfoOverlay(Context context,String text, GeoPoint gp) {
		super();
		this.context=context;
		mPosicion = gp;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		Point screenPts = new Point();
		mapView.getProjection().toPixels(mPosicion, screenPts);

		// ---add the marker---
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.furgovw_furgoperfectos);

		canvas.drawBitmap(bmp, screenPts.x - (bmp.getWidth() / 2),
				screenPts.y - (bmp.getHeight()), null);
	}
}