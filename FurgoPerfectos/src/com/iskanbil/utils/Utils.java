package com.iskanbil.utils;

import java.io.File;

public class Utils {
	public static String getFileExtension(String filePath)
    {
        File f = new File(filePath);
        String name = f.getName();
        int k = name.lastIndexOf(".");
        String ext = "";
        if(k != -1)
            ext = name.substring(k + 1, name.length());
        return ext;
    }

	public static boolean supportedFPData(String file_path) {
		String ext=Utils.getFileExtension(file_path).toUpperCase();
		
		return ext.equals("KML"); //|| ext.equals("***");
		
		
	}
}
