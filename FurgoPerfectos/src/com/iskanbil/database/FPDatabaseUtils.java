package com.iskanbil.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FPDatabaseUtils {
	
	public static final String AUTHORITY = "com.iskanbil.database.FurgoVW";
	public static class FurgoPerfectos implements BaseColumns{
		
		public static String TABLE_NAME="furgoperfectos";
		
		
		//Campos
		public static final String _NAME="c1";
		public static final String _DESCRIPCION="c2";
		public static final String _LATITUDE="l1";
		public static final String _LONGITUDE="l2";
		public static final String _TIPO="c3";
		public static final int TIPO_AC=0;
		public static final int TIPO_FP=1;
		
		//Auxiliares
		public static final String AUX2="c4";
		public static final String AUX3="c5";
		public static final String AUX4="c6";
		public static final String AUX5="c7";

		//Indices
		public static final int COLUMN_INDEX_NAME=1;
		public static final int COLUMN_INDEX_DESCRIPCION=2;
		public static final int COLUMN_INDEX_LATITUDE=3;
		public static final int COLUMN_INDEX_LONGITUDE=4;
		
		public static final String[] PROJECTION = new String[] {
			FurgoPerfectos._ID, // 0
			FurgoPerfectos._NAME, // 1
			FurgoPerfectos._DESCRIPCION, // 2
			FurgoPerfectos._LATITUDE, //3
			FurgoPerfectos._LONGITUDE, //4
			FurgoPerfectos._TIPO //4
	    };
		
		public static final Uri CONTENT_URI
        = Uri.parse("content://" + AUTHORITY + "/furgoperfectos");
		
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.iskanbil.furgoperfecto";
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.iskanbil.furgoperfecto";
        
		
        public static final String DEFAULT_SORT_ORDER = _NAME + " ASC";
	}

}
