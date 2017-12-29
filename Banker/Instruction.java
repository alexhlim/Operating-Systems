/**
 * This is the class that represents an Instruction. An instruction could be: request, initiate, release, compute, or terminate. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */
public class Instruction {

	protected int index;
	protected String action;
	protected int taskID;
	protected int resourceNum;
	protected int units;
	
	/**
	 * Main constructor for Instruction. It assigns an index (because instructions are stored in ArrayLists), action, task ID, resource number,
	 * and units. 
	 * @param index : index of instruction in ArrayList
	 * @param action : initiate, request, release, compute, or terminate
	 * @param taskID : which task's instruction it is
	 * @param resourceNum : corresponds to a resource 
	 * @param units : how many units will be used or released
	 */
	public Instruction(int index, String action, int taskID, int resourceNum, int units){
		this.index = index;
		this.action = action;
		this.taskID = taskID;
		this.resourceNum = resourceNum;
		this.units = units;
	}
	
	/**
	 * Clone class for the banker's algorithm, so that original data is not modified. 
	 */
	public Instruction clone(){
		Instruction i = new Instruction(this.index,this.action,this.taskID, this.resourceNum,this.units);
		return i;
	}
	
	/**
	 * Display characteristics of Instruction.
	 */
	public String toString(){
		return this.action;
	}
	
}
