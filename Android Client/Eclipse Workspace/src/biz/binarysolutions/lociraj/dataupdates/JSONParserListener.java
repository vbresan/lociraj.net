package biz.binarysolutions.lociraj.dataupdates;

import android.os.Bundle;

/**
 * 
 *
 */
public interface JSONParserListener {

	void onPlacemarkAvailable(Bundle data);
	void onJSONParsed();
}
