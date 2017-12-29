
/**
 * This class represents a Process in Lab 4. A process has an ID, page table, and each page in the page table have an associated frame. Also, this class
 * keeps track of various pieces of information such as number of faults, in order display results. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

import java.util.ArrayList;

public class Process {
	
	protected int ID;
	protected double A;
	protected double B;
	protected double C;
	protected int currentWord;
	protected int nextWord;
	protected boolean firstWord = true;
	protected double processSize = 0;
	protected boolean done = false;
	protected Frame frame;
	protected ArrayList<Page> pageTable = new ArrayList<Page>();
	protected double averageResidency;
	protected double numOfEvictions;
	protected int numOfFaults;
	
	
	
	/**
	 * Constructor method for Process
	 * @param ID : Process ID
	 * @param A : case 1 probability
	 * @param B : case 2 probability
	 * @param C : case 3 probability
	 */
	public Process (int ID, double A, double B, double C){
		this.ID = ID;
		this.A = A;
		this.B = B;
		this.C = C;
	}

}
