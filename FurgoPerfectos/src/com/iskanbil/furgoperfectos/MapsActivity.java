package com.iskanbil.furgoperfectos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.iskanbil.maps.EnhancedMapView;
import com.iskanbil.maps.FPInfoItemizedOverlay;
import com.iskanbil.maps.MapUtils;

public class MapsActivity extends SherlockMapActivity{
	private static final String MY_AD_UNIT_ID = "a1505b1550914d0";
    private EnhancedMapView m_mapView;
	private MyLocationOverlay myLocationOverlay;
	
	protected FPInfoItemizedOverlay<OverlayItem> m_ifpo_fp;
	protected FPInfoItemizedOverlay<OverlayItem> m_ifpo_ac;
	private AdView adView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSherlock().setProgressBarIndeterminate(true);
        getSherlock().setProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.activity_maps);

       	getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       	getSupportActionBar().setTitle(R.string.app_name);
       	
        m_mapView=(EnhancedMapView)findViewById(R.id.mapView);
        
        m_mapView.setBuiltInZoomControls(true);

        myLocationOverlay = new MyLocationOverlay(this, m_mapView);
        myLocationOverlay.disableCompass();
        m_mapView.getOverlays().add(myLocationOverlay);

        myLocationOverlay.runOnFirstFix(new Runnable() {
          public void run() {
        	  m_mapView.getController().animateTo(myLocationOverlay.getMyLocation());
             }
        }); 
        
        adView = (AdView)findViewById(R.id.adView);
        
        
        
    }
	
    @Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation(); 
	}
    @Override
	protected void onDestroy() {
    	if(adView!=null)
    		adView.destroy();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		getSherlock().setProgressBarIndeterminateVisibility(false);
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_maps, menu);
        menu.findItem(R.id.refresh)
        	.setIcon(R.drawable.ic_refresh)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.findItem(R.id.list)
    	.setIcon(R.drawable.ic_action_list)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.refresh:
            	GeoPoint max=m_mapView.getProjection().fromPixels(0, 0);
            	GeoPoint min=m_mapView.getProjection().fromPixels(m_mapView.getWidth(), m_mapView.getHeight());
            	
            	getSherlock().setProgressBarIndeterminateVisibility(true);
            	
            	(new MapUtils.LoadFurgoPerfectos(this,(float)(min.getLatitudeE6()/1e6),(float)(max.getLatitudeE6()/1e6),(float)(max.getLongitudeE6()/1e6),(float)(min.getLongitudeE6()/1e6), m_mapView,new MapUtils.LoadFurgoPerfectos.LoadFurgoPerfectosListener(){

					@Override
					public void LoadFurgoPerfectosFinished(
							FPInfoItemizedOverlay<OverlayItem> ifpo_fp,FPInfoItemizedOverlay<OverlayItem> ifpo_ac) {
						if (ifpo_fp!=null){
							if(m_ifpo_fp!=null){
								m_ifpo_fp.hideAllBalloons();
								m_mapView.getOverlays().remove(m_ifpo_fp);
							}
							m_ifpo_fp=ifpo_fp;
							m_ifpo_fp.do_populate();
							m_mapView.getOverlays().add(m_ifpo_fp);
						}
						if (ifpo_ac!=null){
							if(m_ifpo_ac!=null){
								m_ifpo_ac.hideAllBalloons();
								m_mapView.getOverlays().remove(m_ifpo_ac);
							}
							m_ifpo_ac=ifpo_ac;
							m_ifpo_ac.do_populate();
							m_mapView.getOverlays().add(m_ifpo_ac);
						}
						m_mapView.postInvalidate();
						getSherlock().setProgressBarIndeterminateVisibility(false);
					}
            		
            	})).execute();
            	
            	return true;
            case R.id.list:
            	startActivity(new Intent(this,ListaActivity.class));
            	return true;
            	
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
