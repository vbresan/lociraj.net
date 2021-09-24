package biz.binarysolutions.lociraj.dataupdates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.map.Placemark;

/**
 * 
 *
 */
public class JSONParser extends Thread {

	private Handler handler;
	private String  data;

	/**
	 * 
	 */
	private void sendPlacemark(Placemark placemark) {
		
		Message message = Message.obtain();
		message.what = MessageStatus.NEW_PLACEMARK;
		message.setData(placemark.toBundle());
		
		handler.sendMessage(message);
	}

	/**
	 * 
	 * @param handler
	 * @param data
	 */
	public JSONParser(Handler handler, String data) {
		this.handler = handler;
		this.data    = data;
	}

	@Override
	public void run() {
		
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray  events     = jsonObject.getJSONArray("events");
			
			for (int i = 0, length = events.length(); i < length; i++) {
				
				JSONObject event     = events.getJSONObject(i);
				Placemark  placemark = new Placemark(event);
				
				sendPlacemark(placemark);
			}
		} catch (JSONException e) {
			// do nothing
		}
		
		handler.sendEmptyMessage(MessageStatus.DONE);
	}

}
