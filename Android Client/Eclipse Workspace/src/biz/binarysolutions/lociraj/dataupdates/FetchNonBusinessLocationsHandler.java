package biz.binarysolutions.lociraj.dataupdates;

import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.map.CustomMapActivity;

/**
 * 
 *
 */
public class FetchNonBusinessLocationsHandler extends Handler {

	private CustomMapActivity activity = null;
	
	/**
	 * 
	 * @param activity 
	 * @param dialog
	 */
	public FetchNonBusinessLocationsHandler(CustomMapActivity activity) {
		super();
		
		this.activity = activity;
	}

	/**
	 * 
	 */
	public void handleMessage(Message message) {
		
		switch (message.what) {
		
		case MessageStatus.ERROR_CONNECTING_TO_SERVER:

			activity.onConnectionError();
			break;
			
		case MessageStatus.DATA_ERROR:

			activity.onDataUnavailable();
			break;			
			
		case MessageStatus.DONE:
			
			activity.onDataAvailable(message.getData().getString("data"));
			break;
	
		default:
			break;
		}
	}
}
