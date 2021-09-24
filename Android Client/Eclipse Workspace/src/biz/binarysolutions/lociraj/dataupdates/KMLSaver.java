package biz.binarysolutions.lociraj.dataupdates;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContextWrapper;

/**
 * 
 *
 */
public class KMLSaver extends Thread {

	public static final String FILE_NAME = "latest.kml";
	
	private Activity activity;
	private String   data;

	/**
	 * 
	 * @param activity 
	 * @param data
	 */
	public KMLSaver(Activity activity, String data) {
		
		this.activity = activity;
		this.data     = data;
	}

	@Override
	public void run() {
		
		try {
			
			FileOutputStream out = 
				activity.openFileOutput(FILE_NAME, ContextWrapper.MODE_PRIVATE);
			
			out.write(data.getBytes());
			out.close();
			
		} catch (IOException e) {
			// do nothing
		}		
	}
}
