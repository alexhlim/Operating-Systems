
/**
 * This class represents a Page in Lab 4. Pages have characteristics, such as ID, Frame number, and memory references. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

public class Page {

	
	protected int ID;
	protected int frameNum;
	protected Frame frame;
	protected double memoryReferences = 0;
	protected double timeLoaded;
	protected double timeEvicted;

	/**
	 * Constructor for Page.
	 * @param ID
	 */
	public Page(int ID){
		this.ID = ID; 
	}
	
	/**
	 * Equals method so that contains method for arraylist will work. 
	 */
	@Override
	public boolean equals(Object o){
		Page page = (Page) o;
		if (this.ID == page.ID){
			return true;
		}
		return false;
		
	}
	
	/**
	 * To String Method
	 */
	public String toString(){
		return "Page " + this.ID; 
	}
}
