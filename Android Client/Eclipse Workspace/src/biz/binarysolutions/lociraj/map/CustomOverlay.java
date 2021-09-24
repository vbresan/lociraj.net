package biz.binarysolutions.lociraj.map;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

/**
 * 
 *
 */
public class CustomOverlay extends BalloonItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	/**
	 * 
	 * @param defaultMarker
	 * @param context
	 * @param mapView
	 */
	public CustomOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	/**
	 * 
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}

	/**
	 * 
	 * @param overlay
	 * @param drawable
	 */
	public void addOverlay(OverlayItem overlay, Drawable drawable) {
		overlay.setMarker(boundCenter(drawable));
		addOverlay(overlay);
	}

	/**
	 * 
	 * @param index
	 */
	public void simulateOnTap(int index) {
		
		if (index < overlays.size()) {
			onTap(index);
		}
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
	}
	
}
