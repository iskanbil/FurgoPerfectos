package com.iskanbil.database;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.iskanbil.database.FPDatabaseUtils.FurgoPerfectos;


public class FPProvider extends ContentProvider{
	private static final String TAG = "FPProvider";

	private static final String DATABASE_NAME = "furgoperfectos.db";
	private static final int DATABASE_VERSION = 2;
	
	private static HashMap<String, String> sFurgoPerfectosProjectionMap;
	
	private static final int FURGOPERFECTOS = 1;
	private static final int FURGOPERFECTO_ID = 2;
	
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(FPDatabaseUtils.AUTHORITY, "furgoperfectos", FURGOPERFECTOS);
		sUriMatcher.addURI(FPDatabaseUtils.AUTHORITY, "furgoperfectos/#", FURGOPERFECTO_ID);
		
		sFurgoPerfectosProjectionMap = new HashMap<String, String>();
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._ID, FurgoPerfectos._ID);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._NAME, FurgoPerfectos._NAME);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._DESCRIPCION, FurgoPerfectos._DESCRIPCION);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._LATITUDE, FurgoPerfectos._LATITUDE);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._LONGITUDE, FurgoPerfectos._LONGITUDE);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos._TIPO, FurgoPerfectos._TIPO);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos.AUX2, FurgoPerfectos.AUX2);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos.AUX3, FurgoPerfectos.AUX3);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos.AUX4, FurgoPerfectos.AUX4);
		sFurgoPerfectosProjectionMap.put(FurgoPerfectos.AUX5, FurgoPerfectos.AUX5);
		
		
	}
	
	public class FPDatabaseHelper extends SQLiteOpenHelper {

		public FPDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + FurgoPerfectos.TABLE_NAME + " ("
					+ FurgoPerfectos._ID + " INTEGER PRIMARY KEY," +
						FurgoPerfectos._NAME + " TEXT," +
						FurgoPerfectos._DESCRIPCION + " TEXT," + 
						FurgoPerfectos._LATITUDE + " NUMERIC," + 
						FurgoPerfectos._LONGITUDE + " NUMERIC," + 
						FurgoPerfectos._TIPO + " NUMERIC," +
						FurgoPerfectos.AUX2 + " TEXT," +
						FurgoPerfectos.AUX3 + " TEXT," +
						FurgoPerfectos.AUX4 + " TEXT," +
						FurgoPerfectos.AUX5 + " TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE " + FurgoPerfectos.TABLE_NAME + ";");

		}

	}
	
	private FPDatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new FPDatabaseHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
		case FURGOPERFECTOS:
			qb.setTables(FurgoPerfectos.TABLE_NAME);
			qb.setProjectionMap(sFurgoPerfectosProjectionMap);
			break;
		case FURGOPERFECTO_ID:
			qb.setTables(FurgoPerfectos.TABLE_NAME);
			qb.setProjectionMap(sFurgoPerfectosProjectionMap);
			qb.appendWhere(FurgoPerfectos._ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy = null;
		if (TextUtils.isEmpty(sortOrder)) {
			if (sUriMatcher.match(uri) == FURGOPERFECTOS)
				orderBy = FurgoPerfectos.DEFAULT_SORT_ORDER;
		} else 
			orderBy =sortOrder;
		
		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case FURGOPERFECTOS:
			count = db.delete(FurgoPerfectos.TABLE_NAME, selection, selectionArgs);
			break;
		case FURGOPERFECTO_ID:
			String objeto_prId = uri.getPathSegments().get(1);
			count = db.delete(FurgoPerfectos.TABLE_NAME, FurgoPerfectos._ID
					+ "="
					+ objeto_prId, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
		
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case FURGOPERFECTOS:
			return FurgoPerfectos.CONTENT_TYPE;
		case FURGOPERFECTO_ID:
			return FurgoPerfectos.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		if (sUriMatcher.match(uri) == FURGOPERFECTOS) {
			ContentValues values;
	        if (initialValues != null) {
	            values = new ContentValues(initialValues);
	        } else {
	            values = new ContentValues();
	        }
	        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	        long rowId = db.insert(FurgoPerfectos.TABLE_NAME,null, values);
	        if (rowId > 0) {
	            Uri fpUri = ContentUris.withAppendedId(FurgoPerfectos.CONTENT_URI, rowId);
	            getContext().getContentResolver().notifyChange(fpUri, null);
	            return fpUri;
	        }
	        throw new SQLException("Failed to insert row into " + uri);
		}else{
	        throw new IllegalArgumentException("Unknown URI " + uri);
        }
		
	}

	

	

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case FURGOPERFECTOS:
			count = db.update(FurgoPerfectos.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case FURGOPERFECTO_ID:
			String objeto_prId = uri.getPathSegments().get(1);
			count = db.update(FurgoPerfectos.TABLE_NAME, values, FurgoPerfectos._ID
					+ "="
					+ objeto_prId, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
