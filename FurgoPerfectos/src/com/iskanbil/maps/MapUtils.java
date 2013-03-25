package com.iskanbil.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import com.iskanbil.database.FPDatabaseApi;
import com.iskanbil.database.FPDatabaseUtils.FurgoPerfectos;
import com.iskanbil.furgoperfectos.R;

public class MapUtils {
	
	
	public static class LoadFurgoPerfectos extends AsyncTask<Void,Void,Boolean>{
		
		private float m_lat_min;
		private float m_lat_max;
		private float m_lon_min;
		private float m_lon_max;
		private Context m_context;
		private FPDatabaseApi m_databaseApi;
		private FPInfoItemizedOverlay<OverlayItem> m_fp;
		private FPInfoItemizedOverlay<OverlayItem> m_ac;
		//private ProgressDialog m_pd;
		private LoadFurgoPerfectosListener m_lfpl;
		private boolean m_load_all;
		private MapView m_mapView;
		private Cursor m_cursor_fp;
		private Cursor m_cursor_ac;
		
		public LoadFurgoPerfectos(Context context, float lat_min,float lat_max,float lon_min, float lon_max,MapView mapView,LoadFurgoPerfectosListener lfpl){
			m_lat_min=lat_min;
			m_lat_max=lat_max;
			m_lon_min=lon_min;
			m_lon_max=lon_max;
			m_context=context;
			m_load_all=false;
			//m_pd=new ProgressDialog(m_context);
			//m_pd.setIndeterminate(true);
			//m_pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//m_pd.setMessage("Cargando FurgoPerfectos de la zona...");
			m_lfpl=lfpl;
			m_mapView=mapView;
		}
		
		public LoadFurgoPerfectos(Context context, MapView mapView, LoadFurgoPerfectosListener lfpl){
		
			m_context=context;
			m_load_all=true;
			//m_pd=new ProgressDialog(m_context);
			//m_pd.setIndeterminate(true);
			//m_pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//m_pd.setMessage("Cargando FurgoPerfectos de la zona...");
			m_lfpl=lfpl;
			m_mapView=mapView;
			
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_databaseApi=new FPDatabaseApi(m_context);
			//m_pd.show();
			
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			 
			if(m_load_all) {
				m_cursor_fp= m_databaseApi.getFurgoPerfectos(FurgoPerfectos.TIPO_FP);
				m_cursor_ac= m_databaseApi.getFurgoPerfectos(FurgoPerfectos.TIPO_AC);
			}else{
				m_cursor_fp= m_databaseApi.getFurgoPerfectos(m_lat_min, m_lat_max, m_lon_min, m_lon_max,FurgoPerfectos.TIPO_FP);
				m_cursor_ac= m_databaseApi.getFurgoPerfectos(m_lat_min, m_lat_max, m_lon_min, m_lon_max,FurgoPerfectos.TIPO_AC);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//m_pd.dismiss();
			
			m_ac = new FPInfoItemizedOverlay<OverlayItem>(m_context.getResources().getDrawable(R.drawable.furgovw_areasac), m_mapView);
			m_fp = new FPInfoItemizedOverlay<OverlayItem>(m_context.getResources().getDrawable(R.drawable.furgovw_furgoperfectos), m_mapView);
			//m_io=new ItemizedFPOverlay(m_context.getResources().getDrawable(R.drawable.furgovw_furgoperfectos),m_context,false,ItemizedPointOverlay.PUNTO_CENTRAL);
			while(m_cursor_ac.moveToNext()){
				float lat=m_cursor_ac.getFloat(FurgoPerfectos.COLUMN_INDEX_LATITUDE);
				float lon=m_cursor_ac.getFloat(FurgoPerfectos.COLUMN_INDEX_LONGITUDE);
				
				CustomOverlayItem overlayItem = new CustomOverlayItem(new GeoPoint((int)(lat*1e6),(int)(lon*1e6)), m_cursor_ac.getString(FurgoPerfectos.COLUMN_INDEX_NAME), m_cursor_ac.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION),FurgoPerfectos.TIPO_AC);
				
				//OverlayItem ov = new OverlayItem(new GeoPoint((int)(lat*1e6),(int)(lon*1e6)), c.getString(FurgoPerfectos.COLUMN_INDEX_NAME), c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));
				m_ac.addOverlay(overlayItem);
			}
			m_cursor_ac.close();
			
			while(m_cursor_fp.moveToNext()){
				float lat=m_cursor_fp.getFloat(FurgoPerfectos.COLUMN_INDEX_LATITUDE);
				float lon=m_cursor_fp.getFloat(FurgoPerfectos.COLUMN_INDEX_LONGITUDE);
				
				CustomOverlayItem overlayItem = new CustomOverlayItem(new GeoPoint((int)(lat*1e6),(int)(lon*1e6)), m_cursor_fp.getString(FurgoPerfectos.COLUMN_INDEX_NAME), m_cursor_fp.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION),FurgoPerfectos.TIPO_FP);
				
				//OverlayItem ov = new OverlayItem(new GeoPoint((int)(lat*1e6),(int)(lon*1e6)), c.getString(FurgoPerfectos.COLUMN_INDEX_NAME), c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));
				m_fp.addOverlay(overlayItem);
			}
			m_cursor_fp.close();
			
			if (m_lfpl!=null)
				m_lfpl.LoadFurgoPerfectosFinished((m_fp!=null && m_fp.size()>0?m_fp:null),(m_ac!=null && m_ac.size()>0?m_ac:null));
			
		}
		
		public interface LoadFurgoPerfectosListener{
			public void LoadFurgoPerfectosFinished(FPInfoItemizedOverlay<OverlayItem> ifpo_ac,FPInfoItemizedOverlay<OverlayItem> ifpo_fp);
		}
	}

}
