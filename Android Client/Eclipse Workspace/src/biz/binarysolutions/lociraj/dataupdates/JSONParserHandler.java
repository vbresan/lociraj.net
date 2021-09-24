package biz.binarysolutions.lociraj.dataupdates;

import android.os.Handler;
import android.os.Message;

/**
 * 
 *
 */
public class JSONParserHandler extends Handler {

	private JSONParserListener listener = null;
	
	/**
	 * 
	 * @param listener 
	 */
	public JSONParserHandler(JSONParserListener listener) {
		super();
		
		this.listener = listener;
	}

	/**
	 * 
	 */
	public void handleMessage(Message message) {
		
		switch (message.what) {
		
		case MessageStatus.NEW_PLACEMARK:
			
			listener.onPlacemarkAvailable(message.getData());
			break;
			
		case MessageStatus.DONE:
			
			listener.onJSONParsed();
			break;
	
		default:
			break;
		}
	}
}
