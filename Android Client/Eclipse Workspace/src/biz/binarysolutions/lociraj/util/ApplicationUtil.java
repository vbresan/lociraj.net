package biz.binarysolutions.lociraj.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import biz.binarysolutions.lociraj.R;

/**
 * 
 *
 */
public class ApplicationUtil {
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private static PackageInfo getPackageInfo(Context context) {
		
		PackageInfo packageInfo = null;
		
		String         packageName = context.getPackageName();
		PackageManager manager     = context.getPackageManager(); 
		
		try {
			packageInfo = manager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// do nothing
		}
		
		return packageInfo;
	}	
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private static String getApplicationText(Context context) {
		
		StringBuffer sb = new StringBuffer()
			.append(context.getString(R.string.app_name))
			.append(" v")
			.append(getVersionName(context));
	
		return sb.toString();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {

		int versionCode = 0;
		
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo != null) {
			versionCode = packageInfo.versionCode;
		}
		
		return versionCode;
	}	

	/**
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		
		String versionName = "";
		
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo != null) {
			versionName = packageInfo.versionName;
		}
		
		return versionName;
	}	

	/**
	 * 
	 * @param activity
	 */
	public static void displayAboutDialog(Activity activity) {
		
		View     view     = View.inflate(activity, R.layout.dialog_about, null);
		TextView textView = (TextView) view.findViewById(R.id.Application);
		
		String text = getApplicationText(activity);
		textView.setText(text);
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.About)
			.setView(view)
			.setPositiveButton(R.string.OK, null)
			.show();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getAndroidID(ContextWrapper context) {
		
		ContentResolver resolver  = context.getContentResolver();
		String          id        = Settings.Secure.ANDROID_ID;
		String          androidID = Settings.Secure.getString(resolver, id); 
		
		return (androidID != null) ? androidID : "unknown";
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isDebuggable(Context context) {
		
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo != null) {
			
			int flags = packageInfo.applicationInfo.flags; 
			return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		}
		
		return false;
	}
}
