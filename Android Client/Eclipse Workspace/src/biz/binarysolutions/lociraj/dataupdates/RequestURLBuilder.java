package biz.binarysolutions.lociraj.dataupdates;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContextWrapper;
import android.location.Location;
import biz.binarysolutions.lociraj.Main;
import biz.binarysolutions.lociraj.R;
import biz.binarysolutions.lociraj.grid.GridAdapter;
import biz.binarysolutions.lociraj.map.CustomMapActivity;
import biz.binarysolutions.lociraj.util.ApplicationUtil;
import biz.binarysolutions.lociraj.util.Crypto;

/**
 * 
 *
 */
public class RequestURLBuilder {
	
	private static final int SHA1_LEN = 40;	
	
	/**
	 * 
	 * @param androidID
	 * @return
	 */
	private static String getUnencryptedUserID(String androidID) {
		
		String userID = "";
		
		while (userID.length() < SHA1_LEN) {
			userID += androidID;
		}
		
		if (userID.length() > SHA1_LEN) {
			userID = userID.substring(0, SHA1_LEN);
		}
		
		return userID;
	}	
	
	/**
	 * 
	 * @param activity 
	 * @return
	 */
	private static String getUserID(Main activity) {
		
		String androidID = ApplicationUtil.getAndroidID(activity);
		String userID    = "";
		
		try {
			
			userID = Crypto.getSHA1(androidID);
			
		} catch (NoSuchAlgorithmException e) {
			
			userID = getUnencryptedUserID(androidID);
		}
		
		return userID;
	}	

	/**
	 * 
	 * @param location 
	 * @param activity 
	 * @return
	 */
	private static List<NameValuePair> getRequestParameters(Main activity) {
		
		String platform = activity.getString(R.string.app_platform);
		String id       = getUserID(activity); 
		String version  = ApplicationUtil.getVersionName(activity);
		
		String lang = activity.getString(R.string.app_lang);
		String key  = activity.getString(R.string.lociraj_api_key);
		
		String results    = activity.getResultsNumber();
		String filter     = activity.getFilter().replace(" ", "%20");
		String categories = activity.getSelectedCategories();		
	
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("platform", platform));
		parameters.add(new BasicNameValuePair("id", id));
		parameters.add(new BasicNameValuePair("version", version));
		parameters.add(new BasicNameValuePair("categories", categories));
		parameters.add(new BasicNameValuePair("results", results));
		parameters.add(new BasicNameValuePair("filter", filter));
		parameters.add(new BasicNameValuePair("lang", lang));
		parameters.add(new BasicNameValuePair("key", key));
		
		return parameters;
	}

	/**
	 * 
	 * @param activity
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private static List<NameValuePair> getRequestParameters(Location location) {
		
		double latitude  = location.getLatitude();
		double longitude = location.getLongitude();
		
		String lat = String.valueOf(latitude);
		String lon = String.valueOf(longitude);
	
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("lat", lat));
		parameters.add(new BasicNameValuePair("lon", lon));
		
		return parameters;
	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static String getNonBusinessURL(Main activity) {
		
		String url = activity.getString(R.string.lociraj_non_business_URL);
		List<NameValuePair> parameters = getRequestParameters(activity);		
		
		String formattedParameters = URLEncodedUtils.format(parameters, "UTF-8"); 
		String requestURL = url + "?" + formattedParameters;

		return requestURL;
	}

	/**
	 * 
	 * @param activity
	 * @param location
	 * @return
	 */
	public static String getBusinessURL
		(
				CustomMapActivity activity, 
				Location 		  location
		) {
		
		String url = activity.getString(R.string.lociraj_business_URL);
		List<NameValuePair> parameters = getRequestParameters(location);		
		
		String formattedParameters = URLEncodedUtils.format(parameters, "UTF-8"); 
		String requestURL = url + "?" + formattedParameters;

		return requestURL;
	}

	/**
	 * 
	 * @param context 
	 * @param categoryID
	 * @param resultsNumber
	 * @param location
	 * @return
	 */
	public static String getThirdPartyURL
		(
				ContextWrapper context, 
				int            categoryID, 
				String         resultsNumber,
				Location       location
		) {
		
		String[] urls = 
			context.getResources().getStringArray(R.array.ThirdPartyBaseURLs);
		
		StringBuffer sb = new StringBuffer();
		sb.append(urls[categoryID - Main.THIRD_PARTY_CATEGORIES_START_INDEX - 1]);
		
		switch (categoryID) {
		case GridAdapter.IDEMVAN:
			
			sb.append(location.getLatitude());
			sb.append("/");
			sb.append(location.getLongitude());
			sb.append("/");
			sb.append(resultsNumber);
			sb.append("/");
			break;

		default:
			break;
		}
		
		return sb.toString();
	}

}
