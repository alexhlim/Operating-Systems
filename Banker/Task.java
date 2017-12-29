
/**
 * This class represents a Task used in the Resource Allocation Algorithms. It has many characteristics, some of which are used in the
 * Optimistic Manager, and others that are used in the Banker's Manager. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

import java.util.ArrayList;


public class Task implements Comparable {
	
	protected int ID;
	protected int claim;
	protected int terminatedTime;
	protected int waitingTime;
	protected ArrayList<Resource> borrowedResources;
	protected boolean terminated = false;
	protected boolean released = false;
	protected boolean blocked = false;
	protected boolean aborted = false;
	/* for informative message */ 
	protected boolean abortedInitiate = false;
	protected boolean abortedRequest = false;
	protected int cycleAborted;
	protected boolean safe = true;
	protected ArrayList<Instruction> instructionList;
	/* used for banker's algorithm */
	protected ArrayList<Resource> claimsList;
	protected ArrayList<Resource> currentAlloc;
	protected ArrayList<Resource> maxAdditional;
	
	/** 
	 * Main constructor for Task.
	 * @param ID : task number
	 */
	public Task(int ID){
		this.ID = ID;
		this.instructionList = new ArrayList<Instruction>();
		this.borrowedResources = new ArrayList<Resource>();
		/* bankers' task characteristics */
		this.claimsList = new ArrayList<Resource>();
		this.currentAlloc = new ArrayList<Resource>();
		this.maxAdditional = new ArrayList<Resource>();
	}
	
	/**
	 * Alternate constructor for Task. Used mostly for banker's algorithm to make deep copies of data. 
	 * @param ID : task number
	 * @param instructionList : instruction list of the task
	 * @param borrowedResources : borrowed resources of task (used in optimistic manager)
	 * @param claimsList : claims list for task (used in banker manager)
	 * @param currentAlloc : current allocation for task (used in banker manager)
	 * @param maxAdditional : current max additional resources list for task (used in banker manager) 
	 */
	public Task(int ID, ArrayList<Instruction> instructionList, ArrayList<Resource> borrowedResources, ArrayList<Resource> claimsList, ArrayList<Resource> currentAlloc, ArrayList<Resource> maxAdditional ){
		this.ID = new Integer(ID);
		this.instructionList = new ArrayList<Instruction>(instructionList);
		this.borrowedResources = new ArrayList<Resource>(borrowedResources);
		this.claimsList = new ArrayList<Resource>(claimsList);
		this.currentAlloc = new ArrayList<Resource>(currentAlloc);
		this.maxAdditional = new ArrayList<Resource>(maxAdditional);
	}
	
	/**
	 * Display characteristic of Task.
	 */
	public String toString(){
		return "Task " + Integer.toString(this.ID);
	}
	
	/**
	 * Used for sorting the tasks.
	 */
	@Override
	public int compareTo(Object otherTask) {
		return this.ID - ((Task) otherTask).ID;
	}
	
	/**
	 * Used so that ArrayList.contains() works correctly.
	 */
	@Override
	public boolean equals(Object o){
        Task task = (Task) o;
        if (task.ID == this.ID){
        	return true;
        }
        return false;
	}
	
	/**
	 * Cloning for deep copies in the banker's algorithm, so that information is not modified when evulating whether a state is safe or not.
	 */
	public Task clone(){
		Task t = new Task(this.ID);
		for (int i = 0; i < this.instructionList.size(); i++){
			t.instructionList.add(this.instructionList.get(i).clone());
		}
		for (int i = 0; i < this.borrowedResources.size(); i++){
			t.borrowedResources.add(this.borrowedResources.get(i).clone());
		}
		for (int i = 0; i < this.claimsList.size(); i++){
			t.claimsList.add(this.claimsList.get(i).clone());
		}
		for (int i = 0; i < this.currentAlloc.size(); i++){
			t.currentAlloc.add(this.currentAlloc.get(i).clone());
		}
		for (int i = 0; i < this.maxAdditional.size(); i++){
			t.maxAdditional.add(this.maxAdditional.get(i).clone());
		}
		
		return t;
	}

}
