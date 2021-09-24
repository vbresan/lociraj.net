package biz.binarysolutions.lociraj;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * 
 *
 */
public class ApplicationSettings extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener {
	
	private String timeKey;
	private String distanceKey;
	
	private ListPreference timePreference;
	private ListPreference distancePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        timeKey     = getString(R.string.preferences_time_key);
        distanceKey = getString(R.string.preferences_distance_key);
        
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        timePreference = 
        	(ListPreference) preferenceScreen.findPreference(timeKey);
        distancePreference = 
        	(ListPreference) preferenceScreen.findPreference(distanceKey);        

        String timeSummary     = timePreference.getEntry().toString();
        String distanceSummary = distancePreference.getEntry().toString();
        
        timePreference.setSummary(timeSummary);
        distancePreference.setSummary(distanceSummary); 

        getPreferenceScreen().
        	getSharedPreferences().
        		registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().
        	getSharedPreferences().
        		unregisterOnSharedPreferenceChangeListener(this);    
    }
    
    /**
     * 
     */
    public void onSharedPreferenceChanged
    	(
    			SharedPreferences sharedPreferences, 
    			String            key
    	) {

    	if (key.equals(timeKey)) {
    		String summary = timePreference.getEntry().toString();
            timePreference.setSummary(summary);
        } else if (key.equals(distanceKey)) {
        	String summary = distancePreference.getEntry().toString();
            distancePreference.setSummary(summary); 
        }
    }

}
