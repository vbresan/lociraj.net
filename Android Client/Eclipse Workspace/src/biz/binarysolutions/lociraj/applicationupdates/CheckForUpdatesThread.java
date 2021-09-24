package biz.binarysolutions.lociraj.applicationupdates;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.util.StringUtil;

/**
 * 
 *
 */
public class CheckForUpdatesThread extends Thread {
	
	private static final String URI = "";
	
	private String  versionCode = "";
	private String  packageName = "";
	private Handler handler     = null;

	/**
	 * 
	 */
	private HttpResponse getHttpResponse() {
		
		HttpResponse httpResponse = null;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost   httpPost   = new HttpPost(URI);
		
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("versionCode", versionCode));
			nameValuePairs.add(new BasicNameValuePair("packageName", packageName));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			httpResponse = httpClient.execute(httpPost);
			
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
	private LatestVersion getLatestVersion(HttpResponse httpResponse) {
		
		LatestVersion latestVersion = new LatestVersion();
		
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			
			try {
			
				InputStream is     = httpEntity.getContent();
	            String      result = StringUtil.getString(is);
	            JSONObject  json   = new JSONObject(result);
	            
	            latestVersion = new LatestVersion(json);
	            is.close();
	            
			} catch (Exception e) {
				// do nothing
			}
		}
		
		return latestVersion;
	}

	/**
	 * 
	 * @param handler
	 * @param packageName
	 * @param versionCode
	 */
	public CheckForUpdatesThread
		(
				Handler handler,
				String  packageName, 
				String  versionCode
		) {
		super();
		
		this.handler     = handler;
		this.packageName = packageName;
		this.versionCode = versionCode;
	}
	
	/**
	 * 
	 */
	public void run() {
		
		handler.sendEmptyMessage(MessageStatus.CONNECTING_TO_SERVER);
		HttpResponse httpResponse = getHttpResponse();
		
		if (httpResponse != null) {
			
			handler.sendEmptyMessage(MessageStatus.GETTING_LAST_VERSION_NUMBER);
			LatestVersion latestVersion = getLatestVersion(httpResponse);
			
			Message message = Message.obtain();
			message.what = MessageStatus.DONE;
			message.setData(latestVersion.toBundle());
			
			handler.sendMessage(message);
			
		} else {
			handler.sendEmptyMessage(MessageStatus.ERROR_CONNECTING_TO_SERVER);
		}
	}	
}
