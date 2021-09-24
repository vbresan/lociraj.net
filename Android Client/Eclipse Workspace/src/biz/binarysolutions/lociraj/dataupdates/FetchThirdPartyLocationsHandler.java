package biz.binarysolutions.lociraj.dataupdates;

import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.map.CustomMapActivity;

/**
 * 
 *
 */
public class FetchThirdPartyLocationsHandler extends Handler {

	private CustomMapActivity activity = null;
	
	/**
	 * 
	 * @param activity 
	 * @param dialog
	 */
	public FetchThirdPartyLocationsHandler(CustomMapActivity activity) {
		super();
		
		this.activity = activity;
	}

	/**
	 * 
	 */
	public void handleMessage(Message message) {
		
		switch (message.what) {
		
		case MessageStatus.ERROR_CONNECTING_TO_SERVER:
			break;
			
		case MessageStatus.DATA_ERROR:
			break;			
			
		case MessageStatus.DONE:
			
			String data = message.getData().getString("data");
			activity.onThirdPartyDataAvailable(data);
			break;
	
		default:
			break;
		}
	}
}
