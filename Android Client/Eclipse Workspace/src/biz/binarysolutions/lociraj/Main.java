package biz.binarysolutions.lociraj;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import biz.binarysolutions.lociraj.applicationupdates.CheckForUpdatesHandler;
import biz.binarysolutions.lociraj.applicationupdates.CheckForUpdatesThread;
import biz.binarysolutions.lociraj.dataupdates.JSONSaver;
import biz.binarysolutions.lociraj.dataupdates.RequestURLBuilder;
import biz.binarysolutions.lociraj.grid.GridAdapter;
import biz.binarysolutions.lociraj.map.CustomMapActivity;
import biz.binarysolutions.lociraj.util.ApplicationUtil;

/**
 * 
 *
 */
public class Main extends Activity {
	
	public  static final int THIRD_PARTY_CATEGORIES_START_INDEX = 1000;
	
	private static final String DEFAULT_FILTER         = "";
	private static final String DEFAULT_RESULTS_NUMBER = "3";
	
	private static final String SELECTED_CATEGORIES = "SelectedCategories";
	
	/**
	 * 
	 */
	private void checkForNewVersion() {
		
		String         title  = getString(R.string.CheckForNewVersion); 
		ProgressDialog dialog = ProgressDialog.show(this, title, "");
		
		String packageName = this.getPackageName();
		String versionCode = String.valueOf(
				ApplicationUtil.getVersionCode(this)
			);
		
		Handler handler = new CheckForUpdatesHandler(this, dialog);
		new CheckForUpdatesThread(handler, packageName, versionCode).start();
	}
	
	/**
	 * 
	 */
	private void startSearch() {
		
		new JSONSaver(this, "").start();
			
		String requestURL = RequestURLBuilder.getNonBusinessURL(this); 
		
		//TODO: recikliraj stari activity
		Intent mapActivity = new Intent(this, CustomMapActivity.class);
		String packageName = getPackageName();
		
		String key = packageName + getString(R.string.app_extras_requestURL);
		mapActivity.putExtra(key, requestURL);
		
		key = packageName + getString(R.string.app_extras_resultsNumber);
		mapActivity.putExtra(key, getResultsNumber());
		
		key = packageName + getString(R.string.app_extras_3rdPartyCategories);
		mapActivity.putExtra(key, getSelectedThirdPartyCategories());
		
    	startActivity(mapActivity);
	}
	
	/**
	 * 
	 */
	private void setGridView() {
		
		GridView    gridView    = (GridView) findViewById(R.id.GridView);
	    GridAdapter gridAdapter = new GridAdapter(this);
	    
	    gridView.setAdapter(gridAdapter);
	    gridView.setOnItemClickListener(gridAdapter);
	}

	/**
	 * 
	 */
	private void setEditTextListener() {

		findViewById(R.id.EditTextFilter).setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View view, int keycode, KeyEvent event) {
				
				if (keycode == KeyEvent.KEYCODE_ENTER) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setSearchButtonListener() {
		
		findViewById(R.id.Search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startSearch();
			}
		});
	}
	
	/**
	 * 
	 */
	private void clearEditTextFocus() {
		
		findViewById(R.id.EditTextFilter).clearFocus();
	}
	
	/**
	 * @return
	 */
	private GridAdapter getGridAdapter() {
		
		GridView gridView  = (GridView) findViewById(R.id.GridView);
		return (GridAdapter) gridView.getAdapter();
	}

	/**
	 * 
	 * @return
	 */
	private int[] getSelectedThirdPartyCategories() {
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		GridAdapter        gridAdapter = getGridAdapter();
		SparseBooleanArray array       = gridAdapter.getSelectedCategories();
		
		for (int i = 0, length = array.size(); i < length; i++) {
			
			int key = array.keyAt(i);
			if (key > THIRD_PARTY_CATEGORIES_START_INDEX) {
				if (array.get(key)) {
					list.add(new Integer(key));
				}
			}
		}		
		
		int[] selected = new int[list.size()];
		for (int i = 0; i < selected.length; i++) {
			selected[i] = list.get(i);
		}
		
		return selected;
	}
	
	/**
	 * 
	 * @param includeThirdParty
	 * @return
	 */
	private String getSelectedCategories(boolean includeThirdParty) {
		
		StringBuffer sb = new StringBuffer();
		
		GridAdapter        gridAdapter = getGridAdapter();
		SparseBooleanArray array       = gridAdapter.getSelectedCategories();
		
		for (int i = 0, length = array.size(); i < length; i++) {
	
			int key = array.keyAt(i);
			if (includeThirdParty || 
				(!includeThirdParty && key < THIRD_PARTY_CATEGORIES_START_INDEX)) {
				if (array.get(key)) {
					sb.append(key);
					sb.append(",");
				}
			}
		}
		
		String string = sb.toString();
		if (string.endsWith(",")) {
			string = string.substring(0, string.length() - 1);
		}
		
		return string;
	}

	/**
	 * 
	 * @param selectedCategories
	 */
	private void setSelectedCategories(String selectedCategories) {
		
		SparseBooleanArray array = new SparseBooleanArray();
		
		String[] items = selectedCategories.split(",");
		for (int i = 0; i < items.length; i++) {
			
			try {
				array.append(Integer.valueOf(items[i]).intValue(), true);
			} catch (Exception e) {
				// do nothing
			}
		}
		
		GridAdapter gridAdapter = getGridAdapter();
		gridAdapter.setSelectedCategories(array);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setGridView();
        setEditTextListener();
        setSearchButtonListener();
    }

    @Override 
    public void onResume() {
    	
    	clearEditTextFocus();
        super.onResume();
    }

	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		
		String selectedCategories = bundle.getString(SELECTED_CATEGORIES);
		setSelectedCategories(selectedCategories);
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		
		String selectedCategories = getSelectedCategories(true);
		bundle.putString(SELECTED_CATEGORIES, selectedCategories);
		
		super.onSaveInstanceState(bundle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	 	switch (item.getItemId()) {
	 	
	    case R.id.menuItemCheck:
	    	checkForNewVersion();
	        return true;
	        
	    case R.id.menuItemSettings:
	    	Intent settings = new Intent(this, ApplicationSettings.class);
        	startActivity(settings);
        	return true;	        
	 	
	    case R.id.menuItemAbout:
	    	ApplicationUtil.displayAboutDialog(this);
	    	return true;
	    	
	    default:
	    	break;
	    }
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * @return
	 */
	public String getSelectedCategories() {
		return getSelectedCategories(false);
	}

	/**
	 * 
	 * @return
	 */
	public String getFilter() {
	
		EditText editText = (EditText) findViewById(R.id.EditTextFilter);
		String   text     = editText.getText().toString();
	
		return (text != null) ? text : DEFAULT_FILTER;
	}

	/**
	 * 
	 * @return
	 */
	public String getResultsNumber() {
	
		RadioGroup group = (RadioGroup) findViewById(R.id.ResultsNumber);
		
		int checked = group.getCheckedRadioButtonId();
		switch (checked) {
		
		case R.id.RadioButton3:
			return "3";
			
		case R.id.RadioButton5:
			return "5";
			
		case R.id.RadioButton10:
			return "10";
	
		default:
			return DEFAULT_RESULTS_NUMBER;
		}
	}	
}