package biz.binarysolutions.lociraj.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

import com.google.android.maps.MapView;

/**
 * 
 *
 */
public class CustomMapView extends MapView implements AnimationListener {

	private LinearLayout activitiesButtons;
	private Animation    animation;
	
	private boolean isAnimationActive = false;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param apiKey
	 */
	public CustomMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (! isAnimationActive) {

			if (activitiesButtons != null && animation != null) {
				isAnimationActive = true;
				activitiesButtons.startAnimation(animation);
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	/**
	 * 
	 * @param linearLayout
	 */
	public void setActivitiesButtons(View view) {
		activitiesButtons = (LinearLayout) view;
	}
	
	/**
	 * 
	 */
	public void setAnimation(Animation animation) {
		
		this.animation = animation; 
		animation.setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		isAnimationActive = false;
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// do nothing
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// do nothing
	}

}
