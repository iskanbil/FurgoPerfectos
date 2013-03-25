package com.iskanbil.furgoperfectos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iskanbil.database.FPDatabaseApi;
import com.iskanbil.database.FPDatabaseApi.LoadFPResult;


public class MainActivity extends SherlockActivity implements OnClickListener{

    private static final String MY_AD_UNIT_ID = "a1505b1550914d0";
	private Button m_btnShowMap;
	private boolean m_showed;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getPreferences(MODE_PRIVATE).contains("loaded")){
			Intent i = new Intent(MainActivity.this,MapsActivity.class);
			startActivity(i);
			finish();
			return;
						
        }
        setContentView(R.layout.activity_main);
        
        m_btnShowMap=(Button)findViewById(R.id.btnShowMap);
        m_btnShowMap.setOnClickListener(this);
        
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnShowMap:
			Intent i = new Intent(this,MapsActivity.class);
			startActivity(i);
			finish();
			break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=Activity.RESULT_OK)
			return;
		
		
		Uri uri=data.getData();
		
		if (uri==null){
			Toast.makeText(this, getString(R.string.error_selecting_file), Toast.LENGTH_SHORT).show();
			return;
		}
		
		String file_path=(uri.getEncodedPath());
		if (file_path==null){
			Toast.makeText(this, getString(R.string.error_selecting_file), Toast.LENGTH_SHORT).show();
			return;
		}
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(!m_showed)
			if(!getPreferences(MODE_PRIVATE).contains("loaded")){
				AlertDialog.Builder builder=null; 
//				if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
//					builder= new AlertDialog.Builder(this,R.style.Theme_Sherlock_Dialog);
//				else
				builder= new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.text_info_start))
				       .setCancelable(false)
				       
				       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               dialog.cancel();
				               
				               FPDatabaseApi api=new FPDatabaseApi(MainActivity.this);
				   				api.LoadInitialData(new LoadFPResult() {
									
									@Override
									public void result(boolean result) {
										if(result)
											getPreferences(MODE_PRIVATE).edit().putBoolean("loaded", true).commit();
									}
								});
		
				           }
				       });
				       
				AlertDialog alert = builder.create();
				alert.show();
				m_showed=true;
	   		}
			
				
			
		
	}
	
	
	
	

}
