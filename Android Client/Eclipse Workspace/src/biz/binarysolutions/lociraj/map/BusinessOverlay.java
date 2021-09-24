package biz.binarysolutions.lociraj.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import biz.binarysolutions.lociraj.R;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 
 *
 */
public class BusinessOverlay extends CustomOverlay {
	
	private Activity activity;
	private List<Placemark> placemarks = new ArrayList<Placemark>();

	/**
	 * @param view
	 * @param dialog
	 * @param placemark
	 */
	private void setCallButtonListener
		(
				final View        view,
				final AlertDialog dialog, 
				final Placemark   placemark
		) {
		
		ImageButton call = (ImageButton) view.findViewById(R.id.Call);
		call.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				
				if (placemark.hasPhone()) {
					try {
						
						Intent intent = new Intent(Intent.ACTION_DIAL);
						String phone  = placemark.getPhoneNumber();
						
						intent.setData(Uri.parse("tel:" + phone));
						activity.startActivity(intent);
						
					} catch (Exception e) {
						// do nothing
					}
				}
			}
		});
	}

	/**
	 * @param view
	 * @param dialog
	 * @param placemark
	 */
	private void setVisitWebButtonListener
		(
				final View        view,
				final AlertDialog dialog, 
				final Placemark   placemark
		) {
		
		ImageButton visitWeb = (ImageButton) view.findViewById(R.id.VisitWeb);
		visitWeb.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
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
			}
		});
	}

	/**
	 * 
	 * @param view
	 * @param dialog
	 * @param placemark 
	 */
	private void setButtonListeners
		(
				final View        view, 
				final AlertDialog dialog, 
				final Placemark   placemark
		) {

		setCallButtonListener(view, dialog, placemark);
		setVisitWebButtonListener(view, dialog, placemark);
	}

	@Override
	protected boolean onBalloonTap(int index) {
		
		hideBalloon();
		
		View view = View.inflate(activity, R.layout.dialog_contact, null);
		AlertDialog dialog = 
			new AlertDialog.Builder(activity).setView(view).show();
		Placemark placemark = placemarks.get(index);
		
		setButtonListeners(view, dialog, placemark);
		
		return true;
	}

	/**
	 * 
	 * @param defaultMarker
	 * @param mapView
	 * @param activity
	 */
	public BusinessOverlay
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
