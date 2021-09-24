package biz.binarysolutions.lociraj.dataupdates;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.util.StringUtil;

/**
 * 
 *
 */
public class FetchLocations extends Thread {

	private Handler handler;
	private String  requestURL;
	
	/**
	 * 
	 * @param requestURL
	 * @return
	 */
	private HttpResponse getHttpResponse(String requestURL) {
		
		HttpResponse httpResponse = null;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet    httpGet    = new HttpGet(requestURL);
		
		try {
			
			httpResponse = httpClient.execute(httpGet);
			
		} catch (Exception e) {
			// do nothing
		} 
		
		return httpResponse;
	}

	/**
	 * 
	 * @param httpResponse
	 * @return
	 */
	private String getOutput(HttpResponse httpResponse) {
		
		String output = "";
		
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			
			try {
			
				InputStream is = httpEntity.getContent();
	            output = StringUtil.getString(is);
	            
	            is.close();
	            
			} catch (Exception e) {
				// do nothing
			}
		}
		
		return output;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private Bundle getBundle(String data) {
		
		Bundle bundle = new Bundle();
		bundle.putString("data", data);
		
		return bundle;
	}

	/**
	 * 
	 * @param baseURL 
	 */
	public FetchLocations(Handler handler, String requestURL) {
		
		this.handler    = handler;
		this.requestURL = requestURL;
	}

	@Override
	public void run() {
		
		HttpResponse httpResponse = getHttpResponse(requestURL);
		if (httpResponse != null) {
		
			String output = getOutput(httpResponse);
			if (output.length() > 0) {
				
				Message message = Message.obtain();
				message.what = MessageStatus.DONE;
				message.setData(getBundle(output));
				
				handler.sendMessage(message);
			} else {
				handler.sendEmptyMessage(MessageStatus.DATA_ERROR);
			}
		} else {
			handler.sendEmptyMessage(MessageStatus.ERROR_CONNECTING_TO_SERVER);
		}
	}
}
