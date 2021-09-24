package biz.binarysolutions.lociraj.dataupdates;

import android.os.Bundle;

/**
 * 
 *
 */
public interface KMLParserListener {

	void onPlacemarkAvailable(Bundle data);
	void onKMLParsed();
}
