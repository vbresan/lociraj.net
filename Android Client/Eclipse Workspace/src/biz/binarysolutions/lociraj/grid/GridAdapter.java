package biz.binarysolutions.lociraj.grid;

import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import biz.binarysolutions.lociraj.Main;
import biz.binarysolutions.lociraj.R;

/**
 * 
 *
 */
public class GridAdapter extends BaseAdapter implements OnItemClickListener {

	//TODO: is this class the right place for this constant?
	public static final int IDEMVAN = 1001;
	
	private Main main;
	private CategoryIcon[] icons;

	// categoryID, array index
	private SparseIntArray mappings = new SparseIntArray(); 
	
	private SoundManager soundManager;
	
	/**
	 * 
	 */
	private void initializeMappings() {
		
		// categoryID, array index
		mappings.append(1, 0);
		mappings.append(2, 1);
		mappings.append(3, 2);
		mappings.append(4, 4);
		mappings.append(5, 6);
		mappings.append(6, 7);
		mappings.append(7, 3);
		mappings.append(8, 5);
		
		mappings.append(IDEMVAN, 8);
	}
	
	/**
	 * 
	 */
	private void loadIcons() {
		
		icons = new CategoryIcon[] {
			new CategoryIcon(R.drawable.icon_grid_atm, 1),
			new CategoryIcon(R.drawable.icon_grid_bank, 2),
			new CategoryIcon(R.drawable.icon_grid_gas_station, 3),
			new CategoryIcon(R.drawable.icon_grid_hospital, 7),
			new CategoryIcon(R.drawable.icon_grid_pharmacy, 4),
			new CategoryIcon(R.drawable.icon_grid_fitness, 8),
			new CategoryIcon(R.drawable.icon_grid_notary, 5),
			new CategoryIcon(R.drawable.icon_grid_post_office, 6),
			new CategoryIcon(R.drawable.icon_grid_idemvan, IDEMVAN),
		};
	}

	/**
	 * 
	 * @return
	 */
	private LinearLayout inflateLinearLayout() {
		return (LinearLayout) LinearLayout.inflate(main, R.layout.grid_icon, null);
	}
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	private boolean isPositionValid(int position) {
		return position >= 0 && position <= icons.length;
	}

	/**
	 * 
	 * @param b
	 */
	private void setCheckMarkVisible(View parent, boolean visible) {
	
		ImageView checkView = (ImageView) parent.findViewById(R.id.Check);
	
		int visibility = visible ? View.VISIBLE : View.INVISIBLE;
		checkView.setVisibility(visibility);
	}

	/**
	 * 
	 * @param position
	 */
	private void displayCategoryName(int position) {
		
		String[] names = main.getResources().getStringArray(R.array.Categories);
		String   name  = names[position];
			
		TextView textView = (TextView) main.findViewById(R.id.CategoryName);
		textView.setText(name);
		
		Animation animation = AnimationUtils.loadAnimation(main, R.anim.fade_out);
		textView.startAnimation(animation);
	}

	/**
	 * 
	 */
	private void playSound() {
		
		new Thread() {
			
			@Override
			public void run() {
				soundManager.play();
			}
		}.start();
	}
	
	/**
	 * 
	 * @param main
	 * @param apps 
	 */
	public GridAdapter(Main main) {
		
		this.main = main;
		this.soundManager = new SoundManager(main);
		
		initializeMappings();
		loadIcons();
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout linearLayout;
		if (convertView == null) {
			linearLayout = inflateLinearLayout();
		} else {
			linearLayout = (LinearLayout) convertView;
		}
		
		ImageView iconView = (ImageView) linearLayout.findViewById(R.id.Icon);
		iconView.setImageResource(icons[position].getIcon());
		
		if (icons[position].isSelected()) {
			setCheckMarkVisible(linearLayout, true);
		} else {
			setCheckMarkVisible(linearLayout, false);
		}
		
        return linearLayout;
    }

	@Override
    public final int getCount() {
        return icons.length;
    }

	@Override
    public final Object getItem(int position) {
        return icons[position];
    }

	@Override
    public final long getItemId(int position) {
        return position;
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		if ((v instanceof LinearLayout) && isPositionValid(position)) {
			
			playSound();
				
			LinearLayout linearLayout = (LinearLayout) v;
			CategoryIcon categoryIcon = icons[position];
			
			if (categoryIcon.isSelected()) {
				
				setCheckMarkVisible(linearLayout, false);
				categoryIcon.isSelected(false);
				
			} else {

				displayCategoryName(position);
				setCheckMarkVisible(linearLayout, true);
				categoryIcon.isSelected(true);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public SparseBooleanArray getSelectedCategories() {

		SparseBooleanArray array = new SparseBooleanArray();
		
		for (int i = 0; i < icons.length; i++) {
			if (icons[i].isSelected()) {
				array.append(icons[i].getID(), true);
			}
		}
		
		return array;
	}

	/**
	 * 
	 * @param array
	 */
	public void setSelectedCategories(SparseBooleanArray array) {

		for (int i = 0, length = array.size(); i < length; i++) {

			int key = array.keyAt(i);
			if (array.get(key)) {
				icons[mappings.get(key)].isSelected(true);
			}
		}
	}
}