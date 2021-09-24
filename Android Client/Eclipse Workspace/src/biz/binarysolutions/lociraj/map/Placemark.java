package biz.binarysolutions.lociraj.map;

import org.json.JSONException;
import org.json.JSONObject;

import biz.binarysolutions.lociraj.Main;
import biz.binarysolutions.lociraj.grid.GridAdapter;

import android.os.Bundle;

/**
 * 
 *
 */
public class Placemark {
	
	private int categoryID;

	private String category;
	private String name;
	private String description;
	private String icon;
	
	private String phoneNumber;
	private String webSite;
	
	private double latitude;
	private double longitude;
	
	/**
	 * 
	 * @param event 
	 * @param name
	 * @return
	 */
	private String getJSONString(JSONObject event, String name) {
		
		String string = null;
		
		try {
			string = event.getString(name);
		} catch (JSONException e) {
			// do nothing
		}
		
		return string;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getExtras() {
		
		String extras = null;
		
		boolean hasPhone = hasPhone();
		boolean hasWeb   = hasWebSite(); 
		
		if (hasPhone || hasWeb) {
			
			extras = "";
			
			if (hasPhone) {
				extras += "<b>Tel: </b><a href=\"\">" + 
					phoneNumber + "</a><br />";
			}
			if (hasWeb) {
				extras += "<b>Web: </b><a href=\"\">" + 
					webSite + "</a><br />";
			}		
		}
		
		return extras;
	}

	/**
	 * @param event
	 */
	private void extractLocationData(JSONObject event) {
		
		try {
			JSONObject location = event.getJSONObject("location");
			
			String venueName    = getJSONString(location, "name");
			String venueAddress = getJSONString(location, "address");
			
			description += venueName + "<br />" + venueAddress + "<br />";
			
			String lat = getJSONString(location, "lat");
			String lon = getJSONString(location, "lng");
			
			if (lat != null) {
				latitude = Double.parseDouble(lat);
			}
			if (lon != null) {
				longitude = Double.parseDouble(lon);
			}
			
		} catch (JSONException e) {
			// do nothing
		}
	}

	/**
	 * 
	 * @param category
	 */
	public Placemark(String category) {
		this.category = category;
	}

	/**
	 * 
	 * @param data
	 */
	public Placemark(Bundle data) {
		
		categoryID = data.getInt("categoryID");

		category    = data.getString("category");
		name        = data.getString("name");
		description = data.getString("description");
		icon        = data.getString("icon");
		
		phoneNumber = data.getString("phoneNumber");
		webSite     = data.getString("webSite");
		
		latitude  = data.getDouble("latitude");
		longitude = data.getDouble("longitude");
	}

	/**
	 * 
	 * @param event
	 */
	public Placemark(JSONObject event) {
		
		categoryID = GridAdapter.IDEMVAN;
		
		category = getJSONString(event, "category");
		name     = getJSONString(event, "name");
		
		description = getJSONString(event, "description");
		if (description != null && description.length() > 0) {
			description +="<br /><br />";
		} else {
			description = "";
		}
		
		icon = "icon_map_idemvan.png";

		webSite = getJSONString(event, "info_url");
		
		extractLocationData(event);
	}

	/**
	 * 
	 * @param categoryID
	 */
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @param icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	/**
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * 
	 * @param webSite
	 */
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	/**
	 * 
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCategoryID() {
		return categoryID;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		
		String extras = getExtras();
		
		if (extras != null) {
			return description + "<br />" + extras;
		} else {
			return description;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		
		String title = "";
		
		if (category != null && category.length() > 0) {
			title = category + ": ";
		}
		
		title += name;
		
		return title;
	}

	/**
	 * 
	 * @return
	 */
	public String getIcon() {
		return icon;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getWebSite() {
		return webSite;
	}

	/**
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return
	 */
	public Bundle toBundle() {
		
		Bundle bundle = new Bundle();
		
		bundle.putInt("categoryID", categoryID);
		bundle.putString("category", category);
		bundle.putString("name", name);
		bundle.putString("description", description);
		bundle.putString("icon", icon);
		bundle.putDouble("latitude", latitude);
		bundle.putDouble("longitude", longitude);
		bundle.putString("phoneNumber", phoneNumber);
		bundle.putString("webSite", webSite);
	
		return bundle;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBusinessLocation() {
		return categoryID == 0 && !icon.endsWith("icon_map_your_location.png");
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isThirdPartyLocation() {
		return categoryID > Main.THIRD_PARTY_CATEGORIES_START_INDEX;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasPhone() {
		return phoneNumber != null && phoneNumber.length() > 0;
	}

	/**
	 * @return
	 */
	public boolean hasWebSite() {
		return webSite != null && webSite.length() > 0;
	}
	
}
