package com.iskanbil.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iskanbil.database.FPDatabaseUtils.FurgoPerfectos;
import com.iskanbil.furgoperfectos.R;

public class FPDatabaseApi {

	private static final String TAG = "FPDatabaseApi";
	private ContentResolver m_cr;
	private Context m_context;

	public FPDatabaseApi(Context context) {
		m_cr = context.getContentResolver();
		m_context = context;
	}

	/**
	 * Obtiene los FPs dentro de un bound determinado
	 * 
	 * @param latitude_min
	 * @param longitude_min
	 * @param latitude_max
	 * @param longitude_max
	 * @return
	 */
	public Cursor getFurgoPerfectos(float latitude_min, float latitude_max,
			float longitude_min, float longitude_max, int type) {

		String WHERE = FurgoPerfectos._LATITUDE + ">=? AND "
				+ FurgoPerfectos._LATITUDE + "<=? AND "
				+ FurgoPerfectos._LONGITUDE + ">=? AND "
				+ FurgoPerfectos._LONGITUDE + "<=? AND " + FurgoPerfectos._TIPO
				+ "=?";
		String[] VALUES = new String[] { String.valueOf(latitude_min),
				String.valueOf(latitude_max), String.valueOf(longitude_min),
				String.valueOf(longitude_max), String.valueOf(type) };
		Log.d(TAG, WHERE);
		Cursor c = m_cr.query(FurgoPerfectos.CONTENT_URI,
				FurgoPerfectos.PROJECTION, WHERE, VALUES, null);
		return c;
	}

	public int deleteFurgoPerfectos() {

		return m_cr.delete(FurgoPerfectos.CONTENT_URI, null, null);

	}

	public Cursor getFurgoPerfectos() {

		Cursor c = m_cr.query(FurgoPerfectos.CONTENT_URI,
				FurgoPerfectos.PROJECTION, null, null, null);
		return c;
	}

	public Cursor getFurgoPerfectos(int type) {
		String WHERE = FurgoPerfectos._TIPO + "=?";
		String[] VALUES = new String[] { String.valueOf(type) };
		Cursor c = m_cr.query(FurgoPerfectos.CONTENT_URI,
				FurgoPerfectos.PROJECTION, WHERE, VALUES, null);
		return c;
	}

	public ArrayList<FurgoPerfecto> readKMLFile(InputStream file, int type) {

		// File f=new File(file);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<FurgoPerfecto> aFPs = new ArrayList<FurgoPerfecto>();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(file); // Pfileara la lectura
														// desde la sdcard
			NodeList rootNodes = document.getChildNodes();
			NodeList placemark_list = null;
			for (int i = 0; i < rootNodes.getLength(); i++) {
				Node n = rootNodes.item(i);
				if (n.getNodeName().equals("kml")) {
					NodeList nodes = n.getChildNodes();
					for (int j = 0; j < nodes.getLength(); j++) {
						if (nodes.item(j).getNodeName().equals("Document"))
							placemark_list = nodes.item(j).getChildNodes();
					}

				}
			}

			if (placemark_list == null)
				return null;

			int num = placemark_list.getLength();
			for (int i = 0; i < num; i++) {
				Node nodo = placemark_list.item(i);

				if (nodo.getNodeName().equals("Placemark")) {
					String name = null;
					String description = null;
					double latitude = 0;
					double longitude = 0;
					NodeList placemark_data = nodo.getChildNodes();
					int num_data = placemark_data.getLength();
					for (int j = 0; j < num_data; j++) {
						Node nodo2 = placemark_data.item(j);
						if (nodo2.getNodeName().equals("name")) {
							String[] vals = nodo2.getFirstChild()
									.getNodeValue().split("  ");
							name = nodo2.getFirstChild().getNodeValue()
									.split("  ")[0];
							if (vals.length > 1)
								description = nodo2.getFirstChild()
										.getNodeValue().split("  ")[1];
						} else if (nodo2.getNodeName().equals("Point")) {
							NodeList coordinates = nodo2.getChildNodes();
							int num_coord = coordinates.getLength();
							for (int k = 0; k < num_coord; k++) {
								Node nodo3 = coordinates.item(k);
								if (nodo3.getNodeName().equals("coordinates")) {
									longitude = Float.valueOf(nodo3
											.getFirstChild().getNodeValue()
											.split(",")[0]);
									latitude = Float.valueOf(nodo3
											.getFirstChild().getNodeValue()
											.split(",")[1]);
								}
							}
						}
					}
					FurgoPerfecto fp = new FurgoPerfecto(name, description,
							latitude, longitude, type);
					// Log.d(this.getClass().getSimpleName(), fp.toString());
					aFPs.add(fp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return aFPs;

	}

	public boolean ApplyBatch(ArrayList<ContentProviderOperation> aOperations) {
		try {
			m_cr.applyBatch(FPDatabaseUtils.AUTHORITY, aOperations);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static class FurgoPerfecto {
		private String m_name;
		private String m_description;
		private double m_latitude;
		private double m_longitude;
		private int m_type;

		public FurgoPerfecto(String name, String description, double latitude,
				double longitude, int type) {
			m_name = name;
			m_description = description;
			m_latitude = latitude;
			m_longitude = longitude;
			m_type = type;
		}

		public String getName() {
			return m_name;
		}

		public String getDescription() {
			return m_description;
		}

		public double getLatitude() {
			return m_latitude;
		}

		public double getLongitude() {
			return m_longitude;
		}

		public int getType() {
			return m_type;
		}

		public String toString() {
			return "FP:(Name: " + m_name + " Description: " + m_description
					+ " Latitude: " + String.valueOf(m_latitude)
					+ " Longitude: " + String.valueOf(m_longitude) + ")";
		}

	}

	public void LoadInitialData(LoadFPResult lfpr) {
		(new LoadFPFiles(m_context, this, lfpr)).execute();

	}

	public class LoadFPFiles extends AsyncTask<Void, Integer, Boolean> {

		private FPDatabaseApi m_databaseApi;
		private ArrayList<FurgoPerfecto> m_fps;
		private ProgressDialog m_pd;
		private Context m_context;
		private boolean m_indeterminate_progress = true;
		private static final int LOADING_PROGRESS = 0;
		private static final int PROCESSING = 1;
		private LoadFPResult m_lfpr;

		public LoadFPFiles(Context context, FPDatabaseApi databaseApi,
				LoadFPResult lfpr) {
			m_context = context;
			m_databaseApi = databaseApi;
			m_pd = new ProgressDialog(context);

			m_pd.setMessage(context.getString(R.string.loading_fps));
			m_pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			m_pd.setIndeterminate(true);
			m_pd.setCancelable(false);
			m_lfpr = lfpr;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			m_pd.show();
			m_databaseApi.deleteFurgoPerfectos();
			InputStream is_ac = null;
			InputStream is_fp = null;
			try {
				is_ac = m_context.getAssets().open("ac.kml");
				is_fp = m_context.getAssets().open("fp.kml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: Implementar lectura de diferentes formatos

			if (is_ac != null)
				m_fps = m_databaseApi.readKMLFile(is_ac,
						FPDatabaseUtils.FurgoPerfectos.TIPO_AC);
			if (is_fp != null)
				m_fps.addAll(m_databaseApi.readKMLFile(is_fp,
						FPDatabaseUtils.FurgoPerfectos.TIPO_FP));
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();
			// Borramos la tabla
			list.add(ContentProviderOperation.newDelete(
					FurgoPerfectos.CONTENT_URI).build());
			// Añadimos las inserciones
			if (m_fps == null)
				return false;

			int size = m_fps.size();
			for (int i = 0; i < m_fps.size(); i++) {
				publishProgress(LOADING_PROGRESS, size, i);
				FurgoPerfecto fp = m_fps.get(i);
				ContentValues cv = new ContentValues();
				cv.put(FurgoPerfectos._NAME, fp.getName());
				cv.put(FurgoPerfectos._DESCRIPCION, fp.getDescription());
				cv.put(FurgoPerfectos._LATITUDE, fp.getLatitude());
				cv.put(FurgoPerfectos._LONGITUDE, fp.getLongitude());
				cv.put(FurgoPerfectos._TIPO, fp.getType());
				list.add(ContentProviderOperation
						.newInsert(FurgoPerfectos.CONTENT_URI).withValues(cv)
						.build());
			}
			publishProgress(PROCESSING);
			return m_databaseApi.ApplyBatch(list);

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (values[0] == LOADING_PROGRESS) {
				if (m_indeterminate_progress) {
					m_pd.setIndeterminate(false);
					m_pd.setMax(values[1]);
					m_indeterminate_progress = false;
				}
				m_pd.setProgress(values[2]);
			} else if (values[0] == PROCESSING) {
				m_pd.dismiss();
				m_pd = new ProgressDialog(m_context);
				m_pd.setCancelable(false);
				m_pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pd.setIndeterminate(true);
				m_pd.setMessage(m_context.getString(R.string.processing));
				m_pd.show();
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			m_pd.dismiss();
			if (m_lfpr != null)
				m_lfpr.result(result);
			if (result)
				Toast.makeText(m_context, "OK", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(m_context, "NOT OK", Toast.LENGTH_SHORT).show();
		}
		

	}
	public interface LoadFPResult {
		public void result(boolean result);
	}
	

}
