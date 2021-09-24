package biz.binarysolutions.lociraj.grid;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import biz.binarysolutions.lociraj.R;

/**
 * 
 *
 */
public class SoundManager {
	
	private SoundPool soundPool;
	private int       soundID;
	
	private Context context;


	/**
	 * @param context 
	 * 
	 */
	public SoundManager(Context context) {
		
		this.context = context;
		
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundID   = soundPool.load(context, R.raw.pop, 1);
	}

	/**
	 * 
	 */
	public void play() {

		AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		float currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);  
		float maxVolume     = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		float volume = currentVolume / maxVolume;  

		soundPool.play(soundID, volume, volume, 1, 0, 1f); 
	}

}
