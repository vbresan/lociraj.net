package biz.binarysolutions.lociraj.grid;

/**
 * 
 *
 */
public class CategoryIcon {

	private int icon;
	private int id;
	
	private boolean isSelected = false;

	/**
	 * 
	 * @param icon
	 * @param id 
	 * @param bigIcon 
	 */
	public CategoryIcon(int icon, int id) {
		
		this.icon = icon;
		this.id   = id;
	}

	/**
	 * 
	 * @return
	 */
	public int getIcon() {
		return icon;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * 
	 * @param isSelected
	 */
	public void isSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
