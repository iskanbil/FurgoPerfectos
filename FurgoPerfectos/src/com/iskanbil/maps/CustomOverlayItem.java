package com.iskanbil.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomOverlayItem extends OverlayItem {

	private int m_type;

	public CustomOverlayItem(GeoPoint point) {
		super(point, "", "");
	}

	public CustomOverlayItem(GeoPoint point, String title, String sni, int type) {
		super(point, title, sni);
		m_type = type;
	}

	public int getType() {
		return m_type;
	}

}
