
/**
 * This class represents a Frame in Lab 4. Frames contain characteristics, such as an ID, page, and time stamps.
 * Frames are particularly important because in Lab 4 all evictions were made based on the frame table.
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

public class Frame {
	
	protected int ID;
	protected Process process;
	protected Page page;
	protected boolean occupied = false;
	protected int timestamp = 0;
	
	/**
	 * Constructor for Frame.
	 * @param ID : Frame ID
	 */
	public Frame(int ID){
		this.ID = ID;
	}
	
	/**
	 * To String method. 
	 */
	public String toString(){
		return "ID: " + this.ID + " Process: " + this.process.ID + " Page: " + this.page.ID;
	}

}
