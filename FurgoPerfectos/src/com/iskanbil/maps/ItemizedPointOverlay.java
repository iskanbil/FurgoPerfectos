package com.iskanbil.maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


/**
 * Overlay para mostrar los objetos de red puntuales (un PointOverlay=todos los objetos de un mismo tipo)
 * @author izabala
 *
 */
public abstract class ItemizedPointOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private boolean mShadow;
	private Context mContext;
	public static final int PUNTO_CENTRAL = 0;
	public static final int PUNTO_CENTRAL_INFERIOR = 1;

	public ItemizedPointOverlay(Drawable defaultMarker, Context context,
			boolean shadow, int posicion_icono) {
		super(posicion_icono == PUNTO_CENTRAL ? boundCenter(defaultMarker)
				: boundCenterBottom(defaultMarker));
		this.mContext = context;
		
		mShadow = shadow;
		
	}
	protected Context getContext(){
		return mContext;
	}
	
	protected ArrayList<OverlayItem> getOverlays(){
		return mOverlays;
	}
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	
	public void addOverlay(OverlayItem overlay) {

		mOverlays.add(overlay);

	}


	public void removeOverlay(OverlayItem oi) {
		mOverlays.remove(oi);
		populate();
		setLastFocusedIndex(-1);
	}

	/**
	 * Necesarío llamar a este método tras añadir todos los pointOverlay para que se refresque
	 * Posteriormente añadir al mapa.
	 */
	public void do_populate() {
		populate();
	}

	@Override
	protected abstract boolean onTap(int index);
	
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		if (!mShadow)
			shadow = false;
		super.draw(canvas, mapView, shadow);
	}

	
	
	

}