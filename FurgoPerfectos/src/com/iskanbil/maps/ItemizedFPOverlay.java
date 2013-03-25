package com.iskanbil.maps;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ItemizedFPOverlay extends ItemizedPointOverlay {

	public ItemizedFPOverlay(Drawable defaultMarker, Context context,
			boolean shadow, int posicion_icono) {
		super(defaultMarker, context, shadow, posicion_icono);
		
	}

	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
