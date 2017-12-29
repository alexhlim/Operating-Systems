
/**
 * This class acts a module object for the main linker class. A module contains
 * a set of definitions, a set of uses, and program instructions. This class replicates that 
 * and stores data inside its many lists for easy access.
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 */

package package1;

import java.util.ArrayList;

public class Module {
	
	protected Integer modNum;
	protected Integer startLoc;
	protected Integer endLoc;
	protected Integer length;
	protected ArrayList<Symbol> definitions;
	protected ArrayList<Symbol> uses;
	private ArrayList<String> text;
	
	public Module(Integer modNum, Integer startLoc) {
		this.modNum = modNum;
		this.startLoc = startLoc;
		this.endLoc = startLoc;
		this.length = 0;
		this.definitions = new ArrayList<Symbol>(); 
		this.uses = new ArrayList<Symbol>(); 
		this.text = new ArrayList<String>();
	}
	
	public Module(Integer modNum){
		this.modNum = modNum;
		this.startLoc = 0;
		this.endLoc = 0;
		this.length = 0;
		this.definitions = new ArrayList<Symbol>(); 
		this.uses = new ArrayList<Symbol>(); 
		this.text = new ArrayList<String>();
	}
	
	
	
	public void addDefinition(Symbol definition){
		this.definitions.add(definition);
	}
	
	public void addUse(Symbol use){
		this.uses.add(use);
	}
	
	public void addText(String text){
		this.text.add(text);
	}
	
	public void addLength(Integer newLength){
		this.length = this.length + newLength;
	}
	
	public String toString(){
		return this.definitions.toString();
	}
	
}
