package biz.binarysolutions.lociraj.location;

import biz.binarysolutions.lociraj.map.CustomMapActivity;
import biz.binarysolutions.lociraj.util.DefaultLocationListener;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * 
 *
 */
public class LocationHandler {
	
	private CustomMapActivity activity;
	
	private LocationManager locationManager;
	private String          provider;
	private Location        lastKnownLocation;
	
	private long  updateTime;
	private float updateDistance;
	
	/**
	 * 
	 */
	private LocationListener locationListener = new DefaultLocationListener() {
		
		/**
		 * 
		 * @return
		 */
		private boolean isDistanceChanged(Location location) {
			return location.distanceTo(lastKnownLocation) > updateDistance;
		}
		
		/**
		 * 
		 * @param location
		 * @return
		 */
		private boolean isTimeouted(Location location) {
			
			long currentTime   = location.getTime();
			long lastKnownTime = lastKnownLocation.getTime();
			
			return (currentTime - lastKnownTime) > updateTime;
		}
		
		/**
		 * 
		 * @param location
		 * @return
		 */
		private boolean isLocationChanged(Location location) {
			
			if (location != null) {
				if (lastKnownLocation != null) {
					
					boolean isDistanceChanged = isDistanceChanged(location);
					boolean isTimeouted       = isTimeouted(location);
					
					return isDistanceChanged || isTimeouted;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			
			if (isLocationChanged(location)) {
				
				lastKnownLocation = location;
				activity.fetchData(location);
			}
		}		
	};	
	
	/**
	 * 
	 * @param activity 
	 * @return
	 */
	private LocationManager getLocationManager(Activity activity) {
		return (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * 
	 * @return
	 */
	private String getProvider() {
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		return locationManager.getBestProvider(criteria, true);		
	}

	/**
	 * 
	 * @param lm
	 */
	public LocationHandler(CustomMapActivity activity) {
		
		this.activity = activity;
		
		locationManager = getLocationManager(activity);
		provider        = getProvider();
		
		if (provider != null) {
			lastKnownLocation = locationManager.getLastKnownLocation(provider);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasProvider() {
		return provider != null;
	}

	/**
	 * 
	 */
	public void requestUpdates() {
		
		locationManager.requestLocationUpdates(
			provider, updateTime, updateDistance, locationListener
		);
	}

	/**
	 * 
	 */
	public void removeUpdates() {
		locationManager.removeUpdates(locationListener);
	}
	
	/**
	 * 
	 * @return
	 */
	public Location getLastKnownLocation() {
		return lastKnownLocation;
	}

	/**
	 * 
	 * @param updateTime 
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 
	 * @param updateDistance 
	 */
	public void setUpdateDistance(float updateDistance) {
		this.updateDistance = updateDistance;
	}
}
