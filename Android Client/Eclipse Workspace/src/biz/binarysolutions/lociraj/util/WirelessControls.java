package biz.binarysolutions.lociraj.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.provider.Settings;
import biz.binarysolutions.lociraj.R;

/**
 * 
 *
 */
public class WirelessControls {

	/**
	 * 
	 * @param activity
	 * @param finishActivity
	 */
	public static void showDialog
		(
				final Activity activity, 
				final boolean  finishActivity
		) {

		OnClickListener goToSettingsListener = new OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				activity.startActivity(
					new Intent(Settings.ACTION_WIRELESS_SETTINGS)
				);
				
				if (finishActivity) {
					activity.finish();
				}
			}
		};
		
		OnClickListener cancelListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (finishActivity) {
					activity.finish();
				}
			}
		};
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.Error)
			.setMessage(R.string.ErrorConnecting)
			.setPositiveButton(R.string.Yes, goToSettingsListener)
			.setNegativeButton(R.string.No,  cancelListener)
			.show();		
	}

	/**
	 * 
	 * @param activity
	 */
	public static void showDialog(Activity activity) {
		showDialog(activity, false);
	}
}
