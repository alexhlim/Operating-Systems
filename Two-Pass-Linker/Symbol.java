

/**
 * This class acts a symbol for the main linker class. A symbol can either be
 * a definition or a use in the given module. This class also takes into account the 
 * locations of given symbols as well as various booleans that are used for error checking.
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 */

package package1;

import java.util.ArrayList;

public class Symbol {

	protected String symbol;
	protected Integer relLocation;
	protected Integer absLocation;
	protected Integer modNumber;
	protected boolean defined = false;
	protected boolean called = false;
	protected boolean noAdressChange;
	protected boolean done = false;
	protected boolean multiple = false;
	protected ArrayList<Integer> useLocCalls = new ArrayList<Integer>();
	

	public Symbol(String symbol, Integer absLocation){
		this.symbol = symbol;
		this.absLocation = absLocation;
	}
	
	public Symbol(String symbol){
		this.symbol = symbol;
	}

	
}
