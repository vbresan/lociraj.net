package biz.binarysolutions.lociraj.applicationupdates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.R;
import biz.binarysolutions.lociraj.util.ApplicationUtil;
import biz.binarysolutions.lociraj.util.WirelessControls;

/**
 * 
 *
 */
public class CheckForUpdatesHandler extends Handler {

	private Activity       activity = null;
	private ProgressDialog dialog   = null;
	
	private LatestVersion latestVersion = null;
	
	/**
	 * 
	 */
	private OnClickListener updateButtonListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			if (latestVersion != null) {

				Uri    uri    = Uri.parse(latestVersion.getURI());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				
				activity.startActivity(intent);
			}
		}
	};

	/**
	 * 
	 */
	private void showUpToDateDialog() {
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.Update)
			.setMessage(R.string.UpToDate)
			.setPositiveButton(R.string.OK, null)
			.show();
	}

	/**
	 * 
	 */
	private void showUpdateDialog() {
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.Update)
			.setMessage(R.string.UpdateMessage)
			.setPositiveButton(R.string.UpdateAction, updateButtonListener)
			.setNegativeButton(R.string.Cancel, null)
			.show();
	}

	/**
	 * 
	 * @param activity 
	 * @param dialog
	 */
	public CheckForUpdatesHandler(Activity activity, ProgressDialog dialog) {
		super();
		
		this.activity = activity;
		this.dialog   = dialog;
	}

	/**
	 * 
	 */
	public void handleMessage(Message message) {
		
		String text = "";
		
		try {
		
		switch (message.what) {
		
		case MessageStatus.ERROR_CONNECTING_TO_SERVER:
			
			dialog.dismiss();
			WirelessControls.showDialog(activity);				
			break;
		
		case MessageStatus.CONNECTING_TO_SERVER:
			
			text = activity.getString(R.string.ConnectingToServer);
			dialog.setMessage(text);
			break;
			
		case MessageStatus.GETTING_LAST_VERSION_NUMBER:
			
			text = activity.getString(R.string.GettingLastVersionNumber);
			dialog.setMessage(text);
			break;
			
		case MessageStatus.DONE:
			
			dialog.dismiss();
			
			latestVersion = new LatestVersion(message.getData());
			if (latestVersion.getVersionCode() <= 
				ApplicationUtil.getVersionCode(activity)) {
				
				showUpToDateDialog();
			} else {
				showUpdateDialog();
			}
			
			break;
	
		default:
			break;
		}
		
		} catch (Exception e) {
			// do nothing
		}
	}
}
