package biz.binarysolutions.lociraj.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

/**
 * 
 *
 */
public class MapBoundaries {
	
	private int minLatitude;
	private int maxLatitude;
	
	private int minLongitude;
	private int maxLongitude;
	
	/**
	 * 
	 */
	public MapBoundaries() {
		reset();
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setMapBoundaries(int latitude, int longitude) {
		
        minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
		maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;

		minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
		maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	}

	/**
	 * 
	 * @param controller
	 */
	public void centerMap(MapController controller) {
		
		int deltaLatitude  = maxLatitude - minLatitude;
		int deltaLongitude = maxLongitude - minLongitude; 
		controller.zoomToSpan(deltaLatitude, deltaLongitude);
		
		int midLatitude  = (maxLatitude + minLatitude) / 2;
		int midLongitude = (maxLongitude + minLongitude) / 2;
		controller.animateTo(new GeoPoint(midLatitude, midLongitude));
	}

	/**
	 * 
	 */
	public void reset() {

		minLatitude = (int)(+81 * 1E6);
		maxLatitude = (int)(-81 * 1E6);
		
		minLongitude  = (int)(+181 * 1E6);
		maxLongitude  = (int)(-181 * 1E6);
	}	
}
