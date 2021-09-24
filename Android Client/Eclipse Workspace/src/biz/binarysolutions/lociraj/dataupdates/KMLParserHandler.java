package biz.binarysolutions.lociraj.dataupdates;

import android.os.Handler;
import android.os.Message;

/**
 * 
 *
 */
public class KMLParserHandler extends Handler {

	private KMLParserListener listener = null;
	
	/**
	 * 
	 * @param listener 
	 */
	public KMLParserHandler(KMLParserListener listener) {
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
			
			listener.onKMLParsed();
			break;
	
		default:
			break;
		}
	}
}
