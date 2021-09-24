package biz.binarysolutions.lociraj.map;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import biz.binarysolutions.lociraj.R;
import biz.binarysolutions.lociraj.dataupdates.FetchBusinessLocationsHandler;
import biz.binarysolutions.lociraj.dataupdates.FetchLocations;
import biz.binarysolutions.lociraj.dataupdates.FetchNonBusinessLocationsHandler;
import biz.binarysolutions.lociraj.dataupdates.FetchThirdPartyLocationsHandler;
import biz.binarysolutions.lociraj.dataupdates.JSONParser;
import biz.binarysolutions.lociraj.dataupdates.JSONParserHandler;
import biz.binarysolutions.lociraj.dataupdates.JSONParserListener;
import biz.binarysolutions.lociraj.dataupdates.JSONSaver;
import biz.binarysolutions.lociraj.dataupdates.KMLParser;
import biz.binarysolutions.lociraj.dataupdates.KMLParserHandler;
import biz.binarysolutions.lociraj.dataupdates.KMLParserListener;
import biz.binarysolutions.lociraj.dataupdates.KMLSaver;
import biz.binarysolutions.lociraj.dataupdates.RequestURLBuilder;
import biz.binarysolutions.lociraj.list.CustomListActivity;
import biz.binarysolutions.lociraj.location.LocationHandler;
import biz.binarysolutions.lociraj.util.ApplicationUtil;
import biz.binarysolutions.lociraj.util.FileUtil;
import biz.binarysolutions.lociraj.util.WirelessControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 *
 */
public class CustomMapActivity extends MapActivity 
	implements KMLParserListener, JSONParserListener {
	
	public  static final String SELECTED_MARKER_INDEX = "selectedMarkerIndex";
	private static final String LAST_REQUEST_URL      = "lastRequestURL";
	
	private LocationHandler locationHandler;
	
	private CustomMapView mapView;
	private List<Overlay> mapOverlays;
	
	private CustomOverlay     nonBusinessOverlay;
	private BusinessOverlay   businessOverlay;
	private ThirdPartyOverlay thirdPartyOverlay;
	
	private String requestURL;
	private String resultsNumber = "0";
	private int[]  thirdPartyCategories;
	
	private int kmlsParsed = 0;
	
	private MapBoundaries mapBoundaries;
	
	/**
	 * 
	 * @param preferences
	 * @return
	 */
	private long getLocationUpdateTime(SharedPreferences preferences) {
		
		String key          = getString(R.string.preferences_time_key);
		String defaultValue = getString(R.string.preferences_time_default_value);
		String index        = preferences.getString(key, defaultValue);
		
		long updateTime = 0;
		if (index.equals("1")) {
			updateTime = 5;
		} else if (index.equals("2")) {
			updateTime = 10;
		} else {
			updateTime = 1;
		}
		
		return updateTime * 60 * 1000;
	}

	/**
	 * 
	 * @param preferences
	 * @return
	 */
	private float getLocationUpdateDistance(SharedPreferences preferences) {
		
		String key          = getString(R.string.preferences_distance_key);
		String defaultValue = getString(R.string.preferences_distance_default_value);
		String index        = preferences.getString(key, defaultValue);
		
		float updateDistance = 0;
		if (index.equals("0")) {
			updateDistance = 0.5f;
		} else if (index.equals("2")) {
			updateDistance = 5;
		} else if (index.equals("3")) {
			updateDistance = 10;
		} else {
			updateDistance = 1;
		}
		
		return updateDistance * 1000;
	}
	
	/**
	 * 
	 */
	private void requestLocationUpdates() {
		
		SharedPreferences preferences = 
			PreferenceManager.getDefaultSharedPreferences(this);
		
		long  updateTime     = getLocationUpdateTime(preferences);
		float updateDistance = getLocationUpdateDistance(preferences);
		
		locationHandler.setUpdateTime(updateTime);
		locationHandler.setUpdateDistance(updateDistance);
		locationHandler.requestUpdates();
	}
	
	/**
	 * 
	 */
	private void determineLocationDialog() {
		
		DialogInterface.OnClickListener goToSettingsListener = 
			new DialogInterface.OnClickListener() {
		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
				}
		};
		
		new AlertDialog.Builder(this)
			.setMessage(R.string.LocationChoice)
			.setPositiveButton(R.string.GoToSettings, goToSettingsListener)
			.show();			
	}	

	/**
	 * 
	 */
	private void loadDataFromFile() {
		
		String data = FileUtil.read(this, KMLSaver.FILE_NAME);
		if (data != null) {
			onDataAvailable(data);
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	private String getLastRequestURL() {
		
		SharedPreferences preferences = 
			PreferenceManager.getDefaultSharedPreferences(this);
		
		return preferences.getString(LAST_REQUEST_URL, "");
	}
	
	/**
	 * 
	 * @param requestURL
	 */
	private void saveLastRequestURL(String requestURL) {
	
		SharedPreferences preferences = 
			PreferenceManager.getDefaultSharedPreferences(this);
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LAST_REQUEST_URL, requestURL);
		editor.commit();
	}

	/**
	 * 
	 * @param location 
	 * @param requestURL
	 */
	private void fetchNonBusinessLocations(Location location) {
		
		if (requestURL == null) {
			return;
		}
		
		double latitude  = location.getLatitude();
		double longitude = location.getLongitude();
		String url = requestURL + "&lat=" + latitude + "&lon=" + longitude;

		setProgressBarIndeterminateVisibility(true);
		
		if (url.equals(getLastRequestURL())) {
			loadDataFromFile();
		} else {
			
			Handler handler = new FetchNonBusinessLocationsHandler(this);
			new FetchLocations(handler, url).start();
			
			saveLastRequestURL(url);
		}			
	}

	/**
	 * 
	 * @param location
	 */
	private void fetchBusinessLocations(Location location) {
		
		Handler handler    = new FetchBusinessLocationsHandler(this);
		String  requestURL = RequestURLBuilder.getBusinessURL(this, location);
		
		new FetchLocations(handler, requestURL).start();
	}
	
	/**
	 * 
	 * @param location
	 */
	private void fetchThirdPartyCategories(Location location) {
		
		for (int i = 0; i < thirdPartyCategories.length; i++) {
			
			Handler handler    = new FetchThirdPartyLocationsHandler(this);
			String  requestURL = RequestURLBuilder.getThirdPartyURL(
					this, thirdPartyCategories[i], resultsNumber, location);
			
			new FetchLocations(handler, requestURL).start();
		}
	}

	/**
	 * 
	 * @return
	 */
	private Drawable getDefaultMarker() {
		return getResources().getDrawable(R.drawable.icon_map_your_location);
	}
	
	/**
	 * 
	 * @return
	 */
	private Drawable getBusinessMarker() {
		return getResources().getDrawable(R.drawable.icon_map_business_location);
	}	
	
	/**
	 * 
	 * @param icon
	 * @return
	 */
	private Drawable getMarker(String icon) {
		
		Resources resources = getResources();
		
		if (icon.endsWith("icon_map_your_location.png")) {
			return resources.getDrawable(R.drawable.icon_map_your_location);
		} else if (icon.endsWith("icon_map_atm.png")) {
			return resources.getDrawable(R.drawable.icon_map_atm);
		} else if (icon.endsWith("icon_map_bank.png")) {
			return resources.getDrawable(R.drawable.icon_map_bank);
		} else if (icon.endsWith("icon_map_gas_station.png")) {
			return resources.getDrawable(R.drawable.icon_map_gas_station);
		} else if (icon.endsWith("icon_map_pharmacy.png")) {
			return resources.getDrawable(R.drawable.icon_map_pharmacy);
		} else if (icon.endsWith("icon_map_notary.png")) {
			return resources.getDrawable(R.drawable.icon_map_notary);
		} else if (icon.endsWith("icon_map_post_office.png")) {
			return resources.getDrawable(R.drawable.icon_map_post_office);
		} else if (icon.endsWith("icon_map_hospital.png")) {
			return resources.getDrawable(R.drawable.icon_map_hospital);
		} else if (icon.endsWith("icon_map_fitness.png")) {
			return resources.getDrawable(R.drawable.icon_map_fitness);
		} else if (icon.endsWith("icon_map_business_location.png")) {
			return resources.getDrawable(R.drawable.icon_map_business_location);
		} else if (icon.endsWith("icon_map_idemvan.png")) {
			return resources.getDrawable(R.drawable.icon_map_idemvan);
		}

		return null;
	}
	
	/**
	 * 
	 */
	private void openList() {
		
		Intent listActivity = 
			new Intent(CustomMapActivity.this, CustomListActivity.class);
		
		startActivityForResult(listActivity, 0);
	}
	
    /**
	 * 
	 */
	private void setBackButton() {
		
		findViewById(R.id.BackButton).setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View view) {
				finish();
			}
	    });
	}

	/**
	 * 
	 */
	private void setMapButton() {
		
		findViewById(R.id.MapButton).setEnabled(false);
	}

	/**
	 * 
	 */
	private void setListButton() {
		
		ImageButton listButton = (ImageButton) findViewById(R.id.ListButton);
	    listButton.setEnabled(false);
	    listButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				openList();
			}
	    });
	}

	/**
	 * 
	 */
	private void setActivitiesButtons() {
		
		setBackButton();
	    setMapButton();
	    setListButton();
	}

	/**
	 * 
	 */
	private void setMapView() {
	    
	    mapView = (CustomMapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    mapView.setActivitiesButtons(findViewById(R.id.ActivitiesButtons));
	    mapView.setAnimation(
	    		AnimationUtils.loadAnimation(this, R.anim.fade_in)
	    );	    
	    
	    mapOverlays = mapView.getOverlays();
	}

	/**
	 * 
	 */
	private void clearMap() {
		
		if (nonBusinessOverlay != null) {
			nonBusinessOverlay.hideBalloon();
		}
		if (businessOverlay != null) {
			businessOverlay.hideBalloon();
		}
		if (thirdPartyOverlay != null) {
			thirdPartyOverlay.hideBalloon();
		}
		
		mapBoundaries.reset();
		mapOverlays.clear();
		
		mapView.postInvalidate();
		mapOverlays = mapView.getOverlays();

		nonBusinessOverlay = new CustomOverlay(getDefaultMarker(), mapView);
	    businessOverlay = new BusinessOverlay(getBusinessMarker(), mapView, this);
	    thirdPartyOverlay = new ThirdPartyOverlay(getBusinessMarker(), mapView, this);
	    
	    kmlsParsed = 0;		
	}

	/**
	 * 
	 */
	private void getExtras() {
		
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	String packageName = getPackageName();
	    	
	    	String key = packageName + getString(R.string.app_extras_requestURL);
	    	requestURL = extras.getString(key);
	    	
	    	key = packageName + getString(R.string.app_extras_resultsNumber);
	    	resultsNumber = extras.getString(key);
	    	
	    	key = packageName + getString(R.string.app_extras_3rdPartyCategories);
	    	thirdPartyCategories = extras.getIntArray(key);
	    }		
	}
	
	/**
	 * 
	 */
	private void setContentView() {
		
		if (ApplicationUtil.isDebuggable(this)) {
			setContentView(R.layout.map_debug);
		} else {
			setContentView(R.layout.map);
		}
	}

	/**
	 * 
	 */
	private void onInputParsed() {
		
		mapOverlays.add(nonBusinessOverlay);
		
		/*
		 * Workaround: 
		 * 
		 * Maps API crashes with NullPointerException on tap if
		 * overlay is empty.
		 */
		if (businessOverlay.size() > 0) {	
			mapOverlays.add(businessOverlay);
		}
		if (thirdPartyOverlay.size() > 0) {	
			mapOverlays.add(thirdPartyOverlay);
		}
		
		mapBoundaries.centerMap(mapView.getController());
		
		ImageButton listButton = (ImageButton) findViewById(R.id.ListButton);
		if (nonBusinessOverlay.size() <= 1) {
			if (thirdPartyOverlay.size() > 0) {
				listButton.setEnabled(true);
			} else {
				listButton.setEnabled(false);
			}
		} else {
			listButton.setEnabled(true);
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView();
        
        setProgressBarIndeterminateVisibility(false);

        setActivitiesButtons();
        setMapView();
        getExtras();
        
        locationHandler = new LocationHandler(this);
        mapBoundaries   = new MapBoundaries();
        
        fetchData(locationHandler.getLastKnownLocation());
    }
	
	@Override 
    public void onResume() {
		
        if (locationHandler.hasProvider()) {
        	requestLocationUpdates();
		} else {
			determineLocationDialog();
		}
        
        super.onResume();
	}
	
	@Override
    public void onPause() {
    	
    	locationHandler.removeUpdates();
        super.onPause();
    }

	@Override
	protected void onActivityResult
		(
				int    requestCode, 
				int    resultCode, 
				Intent data
		) {
		
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			
			Bundle bundle = data.getExtras();
			int    index  = bundle.getInt(SELECTED_MARKER_INDEX);
			int    size   = nonBusinessOverlay.size(); 
			
			if (index < size) {
				nonBusinessOverlay.simulateOnTap(index);
			} else {
				thirdPartyOverlay.simulateOnTap(index - size);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_map, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	 	switch (item.getItemId()) {
	 	
	    case R.id.menuItemAdd:
			try {
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String url    = getString(R.string.lociraj_add_location_URL);
				
				intent.setData(Uri.parse(url));
				startActivity(intent);						
				
			} catch (Exception e) {
				// do nothing
			}
	        return true;	 	
	 	
	    default:
	    	break;
	    }
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * 
	 * @param string
	 */
	public void onDataAvailable(String data) {
		
		setProgressBarIndeterminateVisibility(false);
		
		Handler handler = new KMLParserHandler(this);
		new KMLParser(handler, data).start();
		
		new KMLSaver(this, data).start();
	}

	/**
	 * 
	 * @param data
	 */
	public void onBusinessDataAvailable(String data) {

		Handler handler = new KMLParserHandler(this);
		new KMLParser(handler, data).start();
	}

	/**
	 * 
	 * @param data
	 */
	public void onThirdPartyDataAvailable(String data) {
		
		Handler handler = new JSONParserHandler(this);
		new JSONParser(handler, data).start();
		
		new JSONSaver(this, data).start();
	}

	/**
	 * 
	 */
	public void onDataUnavailable() {

		setProgressBarIndeterminateVisibility(false);
		saveLastRequestURL("");
		
		DialogInterface.OnClickListener listener =
			new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		};
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.Error)
			.setMessage(R.string.DataError)
			.setPositiveButton(R.string.OK, listener)
			.show();		
	}

	/**
	 * 
	 */
	public void onConnectionError() {

		setProgressBarIndeterminateVisibility(false);
		saveLastRequestURL("");
		
		WirelessControls.showDialog(this, true); 
	}

	/**
	 * 
	 * @param data
	 */
	public void onPlacemarkAvailable(Bundle data) {
		
		Placemark placemark = new Placemark(data);
		
		int latitude  = (int) (placemark.getLatitude()  * 1E6);
		int longitude = (int) (placemark.getLongitude() * 1E6);
		mapBoundaries.setMapBoundaries(latitude, longitude);
		
		GeoPoint point   = new GeoPoint(latitude, longitude);
		String   title   = placemark.getTitle();
		String   snippet = placemark.getDescription();
		OverlayItem overlayItem = new OverlayItem(point, title, snippet);

		String   icon   = placemark.getIcon();
		Drawable marker = getMarker(icon);
		
		if (placemark.isBusinessLocation()) {
			businessOverlay.addOverlay(overlayItem, marker, placemark);
		} else if (placemark.isThirdPartyLocation()) {
			thirdPartyOverlay.addOverlay(overlayItem, marker, placemark);
		} else {
			nonBusinessOverlay.addOverlay(overlayItem, marker);
		}
	}

	/**
	 * 
	 */
	public void onKMLParsed() {
		
		//TODO: what if there is an error in parsing?
		if (++kmlsParsed < 2) {
			return;
		}
		
		onInputParsed();
	}

	@Override
	public void onJSONParsed() {
		onInputParsed();
	}

	/**
	 * @param location 
	 * 
	 */
	public void fetchData(Location location) {
		
		if (location != null) {
			
			clearMap();
			
			fetchNonBusinessLocations(location);
			fetchBusinessLocations(location);
			fetchThirdPartyCategories(location);
		}
	}
}
