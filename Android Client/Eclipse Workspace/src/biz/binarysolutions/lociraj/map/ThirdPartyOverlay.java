package biz.binarysolutions.lociraj.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 
 *
 */
public class ThirdPartyOverlay extends CustomOverlay {
	
	private Activity activity;
	private List<Placemark> placemarks = new ArrayList<Placemark>();
	
	@Override
	protected boolean onBalloonTap(int index) {
		
		hideBalloon();
		
		Placemark placemark = placemarks.get(index);
		if (placemark.hasWebSite()) {
			try {
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String web    = placemark.getWebSite();
				
				intent.setData(Uri.parse(web));
				activity.startActivity(intent);						
				
			} catch (Exception e) {
				// do nothing
			}
		}
		
		return true;
	}	

	/**
	 * 
	 * @param defaultMarker
	 * @param mapView
	 * @param activity
	 */
	public ThirdPartyOverlay
		(
				Drawable defaultMarker, 
				MapView  mapView, 
				Activity activity
		) {
		super(defaultMarker, mapView);
		
		this.activity = activity;
	}
	
	/**
	 * 
	 * @param overlayItem
	 * @param marker
	 * @param placemark
	 */
	public void addOverlay
		(
				OverlayItem overlayItem, 
				Drawable    marker,
				Placemark   placemark
		) {
		
		addOverlay(overlayItem, marker);
		placemarks.add(placemark);
	}	

}
