package biz.binarysolutions.lociraj.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import biz.binarysolutions.lociraj.Main;
import biz.binarysolutions.lociraj.R;
import biz.binarysolutions.lociraj.dataupdates.JSONParser;
import biz.binarysolutions.lociraj.dataupdates.JSONParserHandler;
import biz.binarysolutions.lociraj.dataupdates.JSONParserListener;
import biz.binarysolutions.lociraj.dataupdates.JSONSaver;
import biz.binarysolutions.lociraj.dataupdates.KMLParser;
import biz.binarysolutions.lociraj.dataupdates.KMLParserHandler;
import biz.binarysolutions.lociraj.dataupdates.KMLParserListener;
import biz.binarysolutions.lociraj.dataupdates.KMLSaver;
import biz.binarysolutions.lociraj.grid.GridAdapter;
import biz.binarysolutions.lociraj.map.CustomMapActivity;
import biz.binarysolutions.lociraj.map.Placemark;
import biz.binarysolutions.lociraj.util.FileUtil;

/**
 * 
 *
 */
public class CustomListActivity extends Activity 
	implements KMLParserListener, JSONParserListener, OnClickListener {
	
	private int placemarkIndex = 0;

	/**
	 * 
	 */
	private void setBackButton() {
		
		findViewById(R.id.BackButton).setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View view) {
				startActivity(new Intent(CustomListActivity.this, Main.class));
			}
	    });
	}

	/**
	 * 
	 */
	private void setMapButton() {
		
		ImageButton mapButton = (ImageButton) findViewById(R.id.MapButton);
	    mapButton.setEnabled(true);
	    mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
	    });
	}
	
	/**
	 * 
	 * @param index
	 */
	private void returnSelectedMarkerIndex(int index) {
		
		Bundle bundle = new Bundle();
		bundle.putInt(CustomMapActivity.SELECTED_MARKER_INDEX, index);
		
		Intent intent = new Intent();
		intent.putExtras(bundle);
		
		setResult(Activity.RESULT_OK, intent);
		finish();		
	}

	/**
	 * 
	 */
	private void setListButton() {
		
		findViewById(R.id.ListButton).setEnabled(false);
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
	 * @param data
	 */
	private void onDataAvailable(String data) {
		
		Handler handler = new KMLParserHandler(this);
		new KMLParser(handler, data).start();
	}
	
	/**
	 * 
	 * @param data
	 */
	private void onThirdPartyDataAvailable(String data) {
		
		Handler handler = new JSONParserHandler(this);
		new JSONParser(handler, data).start();
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
	 * @param categoryID
	 * @return
	 */
	private LinearLayout getDataContainer(int categoryID) {
		
		LinearLayout dataContainer = null;
		
		switch (categoryID) {
		
		case 1: 
			dataContainer = (LinearLayout) findViewById(R.id.ATMDataLayout);
			findViewById(R.id.ATMLayout).setVisibility(View.VISIBLE);
			break;
			
		case 2: 
			dataContainer = (LinearLayout) findViewById(R.id.BankDataLayout);
			findViewById(R.id.BankLayout).setVisibility(View.VISIBLE);
			break;
			
		case 3: 
			dataContainer = (LinearLayout) findViewById(R.id.GasStationDataLayout);
			findViewById(R.id.GasStationLayout).setVisibility(View.VISIBLE);
			break;
			
		case 4: 
			dataContainer = (LinearLayout) findViewById(R.id.PharmacyDataLayout);
			findViewById(R.id.PharmacyLayout).setVisibility(View.VISIBLE);
			break;
			
		case 5: 
			dataContainer = (LinearLayout) findViewById(R.id.NotaryDataLayout);
			findViewById(R.id.NotaryLayout).setVisibility(View.VISIBLE);
			break;
			
		case 6: 
			dataContainer = (LinearLayout) findViewById(R.id.PostOfficeDataLayout);
			findViewById(R.id.PostOfficeLayout).setVisibility(View.VISIBLE);
			break;
			
		case 7: 
			dataContainer = (LinearLayout) findViewById(R.id.HospitalDataLayout);
			findViewById(R.id.HospitalLayout).setVisibility(View.VISIBLE);
			break;
			
		case 8: 
			dataContainer = (LinearLayout) findViewById(R.id.FitnessDataLayout);
			findViewById(R.id.FitnessLayout).setVisibility(View.VISIBLE);
			break;
			
		case GridAdapter.IDEMVAN:
			dataContainer = (LinearLayout) findViewById(R.id.IdemVanDataLayout);
			findViewById(R.id.IdemVanLayout).setVisibility(View.VISIBLE);
			break; 

		default: 
			break;
		}
		
		return dataContainer;
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	private LinearLayout getDataItem(String name, String description) {
		
		LinearLayout item = 
			(LinearLayout) getLayoutInflater().
				inflate(R.layout.list_data_item, null);
		
		((TextView) item.findViewById(R.id.Name)).
			setText(Html.fromHtml(name));
		((TextView) item.findViewById(R.id.Description)).
			setText(Html.fromHtml(description));
		
		item.setOnClickListener(this);
		
		return item;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list);
		
		setActivitiesButtons();
		loadDataFromFile();
	}
	
	@Override
	public void onClick(View v) {
		
		if (v instanceof LinearLayout) {
			
			int id = ((LinearLayout) v).getId();
			returnSelectedMarkerIndex(id);
		}
	}

	@Override
	public void onPlacemarkAvailable(Bundle data) {

		Placemark placemark = new Placemark(data);
		
		int    categoryID  = placemark.getCategoryID();
		String name        = placemark.getName();
		String description = placemark.getDescription();
		
		LinearLayout container = getDataContainer(categoryID);
		if (container != null) {
			
			LinearLayout item = getDataItem(name, description);
			item.setId(placemarkIndex);
			container.addView(item);
		}
		
		placemarkIndex++;
	}

	@Override
	public void onKMLParsed() {

		String data = FileUtil.read(this, JSONSaver.FILE_NAME);
		if (data != null) {
			onThirdPartyDataAvailable(data);
		}
	}

	@Override
	public void onJSONParsed() {
		// do nothing
	}
}
