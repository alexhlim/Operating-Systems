
/**
 * This is the class that represents a Resource. A resource has two characteristics, its ID (task number), and how many units it has. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

import java.util.ArrayList;

public class Resource {
	
	protected int ID;
	protected int units;
	
	/**
	 * Main constructor for Resources. Assigns its ID and how many units it has 
	 * @param ID : task number
	 * @param units : how many units available
	 */
	public Resource(int ID, int units){
		this.ID = ID;
		this.units = units;
	}
	
	/**
	 * Alternate constructor for Resource, assigns a resource units 
	 * @param units : how many units available 
	 */
	public Resource(int units){
		this.units = units;
	}
	
	/**
	 * This is a cloning method for the banker's algorithm, so that new copies of data would be made without modifying the original.
	 */
	public Resource clone(){
		Resource r = new Resource(this.ID, this.units);
		return r;
	}
	
	/**
	 * Display the characteristics of the resource. 
	 */
	public String toString(){
		return this.ID + " " + this.units;
	}

}
