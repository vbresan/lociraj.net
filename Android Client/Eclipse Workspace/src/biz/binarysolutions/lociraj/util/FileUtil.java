package biz.binarysolutions.lociraj.util;

import java.io.FileInputStream;
import java.io.IOException;

import android.content.ContextWrapper;

/**
 * 
 *
 */
public class FileUtil {

	/**
	 * 
	 * @param wrapper
	 * @return
	 */
	public static String read(ContextWrapper wrapper, String fileName) {
		
		String data = null;
		
		try {
			
			FileInputStream in = wrapper.openFileInput(fileName);
			data = StringUtil.getString(in);
			in.close();
			
		} catch (IOException e) {
			// do nothing
		}
		
		return data;
	}
}
