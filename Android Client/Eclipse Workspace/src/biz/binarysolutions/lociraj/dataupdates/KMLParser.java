package biz.binarysolutions.lociraj.dataupdates;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;
import android.os.Message;
import biz.binarysolutions.lociraj.map.Placemark;
import biz.binarysolutions.lociraj.util.StringUtil;

/**
 * 
 *
 */
public class KMLParser extends Thread {
	
	private Handler handler;
	private String  data;
	
	private boolean inFolder    = false;
	private boolean inPlacemark = false;
	private boolean inIcon      = false;
	
	private String    currentCategory  = "";
	private Placemark currentPlacemark = null;
	
	/**
	 * 
	 */
	private void sendPlacemark() {
		
		Message message = Message.obtain();
		message.what = MessageStatus.NEW_PLACEMARK;
		message.setData(currentPlacemark.toBundle());
		
		handler.sendMessage(message);
	}
	
	/**
	 * 
	 * @param parser
	 */
	private void parseStartTag(XmlPullParser parser) {

		String tagName = parser.getName();
		
		if (tagName.equals("Folder")) {
			inFolder = true;
		} else if (tagName.equals("Icon")) {
			inIcon = true;
		} else if (tagName.equals("Placemark")) {
			inPlacemark = true;
			currentPlacemark = new Placemark(currentCategory);
		}
	}

	/**
	 * 
	 * @param parser
	 * @param trim
	 */
	private void parseEndTag(XmlPullParser parser, String currentText) {

		String tagName = parser.getName();
		
		if (tagName.equals("name")) {
		
			if (inFolder && !inPlacemark) {
				currentCategory = currentText;
			} else if (inPlacemark) {
				currentPlacemark.setName(currentText);
			}
		} else if (tagName.equals("description")) {
			currentPlacemark.setDescription(currentText);
		} else if (tagName.equals("href") && inIcon && inPlacemark) {
			currentPlacemark.setIcon(currentText);
		} else if (tagName.equals("coordinates")) {
			
			String[] parts = currentText.split(",");
			if (parts.length >= 2) {
				
				double longitude = Double.valueOf(parts[0]).doubleValue();
				double latitude  = Double.valueOf(parts[1]).doubleValue();
				
				currentPlacemark.setLatitude(latitude);
				currentPlacemark.setLongitude(longitude);
			}
		}
		
		else if (tagName.equals("l:categoryID")) {
			
			try {
				int categoryID = Integer.valueOf(currentText).intValue();
				currentPlacemark.setCategoryID(categoryID);
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
		
		else if (tagName.equals("l:phoneNumber")) {
			currentPlacemark.setPhoneNumber(currentText);
		} else if (tagName.equals("l:webSite")) {
			currentPlacemark.setWebSite(currentText);
		}
		
		else if (tagName.equals("Folder")) {
			inFolder = false;
			currentCategory = "";
		} else if (tagName.equals("Icon")) {
			inIcon = false;
		} else if (tagName.equals("Placemark")) {
			inPlacemark = false;
			sendPlacemark();
		}		
	}

	/**
	 * @param parser 
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * 
	 */
	private void parse(XmlPullParser parser) 
		throws XmlPullParserException, IOException {
		
		String currentText = "";
		int    eventType   = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			
			if (eventType == XmlPullParser.START_TAG) {
				parseStartTag(parser);
			} else if (eventType == XmlPullParser.END_TAG) {
				parseEndTag(parser, currentText.trim());
				currentText = "";
			} else if (eventType == XmlPullParser.TEXT) {
				currentText += parser.getText();
			}
			
			eventType = parser.next();
		}
		
		handler.sendEmptyMessage(MessageStatus.DONE);
	}	
	
	/**
	 * 
	 * @param handler 
	 * @param data
	 */
	public KMLParser(Handler handler, String data) {
		this.handler = handler;
		this.data    = data;
	}

	@Override
	public void run() {
		
		try {
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			XmlPullParser        parser  = factory.newPullParser();
			
			parser.setInput(StringUtil.getInputStream(data), "UTF-8");
			parse(parser);
			
		} catch (Exception e) {
			// TODO inform user about the error?
		} 		
	}
}
