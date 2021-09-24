package biz.binarysolutions.lociraj.applicationupdates;

import org.json.JSONObject;

import android.os.Bundle;

/**
 * 
 *
 */
public class LatestVersion {
	
	private int    versionCode = 1;
	private String uri         = "";	

	/**
	 * 
	 */
	public LatestVersion() {
		// do nothing
	}

	/**
	 * 
	 * @param json
	 */
	public LatestVersion(JSONObject json) {
		
		try {
			
			versionCode = json.getInt("versionCode");
			uri         = json.getString("uri");
			
		} catch (Exception e) {
			// do nothing
		}
	}
	
	/**
	 * 
	 * @param bundle
	 */
	public LatestVersion(Bundle bundle) {

		int    bundleVersionCode = bundle.getInt("versionCode");
		String bundleURI         = bundle.getString("uri");
		
		if (bundleVersionCode != 0) {
			versionCode = bundleVersionCode;
		}
		
		if (bundleURI != null) {
			uri = bundleURI;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Bundle toBundle() {
		
		Bundle bundle = new Bundle();
		
		bundle.putInt("versionCode", versionCode);
		bundle.putString("uri", uri);
		
		return bundle;
	}

	/**
	 * 
	 * @return
	 */
	public int getVersionCode() {
		return versionCode;
	}

	/**
	 * 
	 * @return
	 */
	public String getURI() {
		return uri;
	}
}
