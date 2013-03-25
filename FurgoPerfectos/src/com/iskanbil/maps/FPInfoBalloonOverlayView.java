package com.iskanbil.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.iskanbil.database.FPDatabaseUtils.FurgoPerfectos;
import com.iskanbil.furgoperfectos.R;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class FPInfoBalloonOverlayView<FPInfoOverlayItem extends CustomOverlayItem> extends BalloonOverlayView<FPInfoOverlayItem> {

	private TextView title;
	private TextView snippet;
	private GeoPoint oGp;
	private ImageView image;
	private Context m_context;
	
	public FPInfoBalloonOverlayView(Context context, int balloonBottomOffset) {
		super(context, balloonBottomOffset);
		m_context=context;
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fp_balloon, parent);
		
		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image=(ImageView)v.findViewById(R.id.balloon_item_image);
		
//		btnDir = (ImageButton)v.findViewById(R.id.botonDireccion);
//		btnDir.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+oGp.getLatitudeE6()/1E6+","+oGp.getLongitudeE6()/1E6));  
//				getContext().startActivity(i);
//			}
//		});
	}

	@Override
	protected void setBalloonData(FPInfoOverlayItem item, ViewGroup parent) {
		
		
		// map our custom item data to fields
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		if(item.getType()==FurgoPerfectos.TIPO_AC)
			image.setImageDrawable(m_context.getResources().getDrawable(R.drawable.furgovw_areasac));
		else
			image.setImageDrawable(m_context.getResources().getDrawable(R.drawable.furgovw_furgoperfectos));
		oGp=item.getPoint();
	}

	
}
