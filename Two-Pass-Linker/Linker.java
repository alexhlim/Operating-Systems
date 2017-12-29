
/**.
 * This class is the main class for Lab 1. It acts as a Two Pass Linker, which basically takes a file and goes through it,
 * relocating relative addresses as well as resolving external references.
 * In addition, this class checks for many errors that might occur in a file.
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 */

package package1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Linker {

	public static void main(String[] args) {
		
	/* declare new scanner to go through text files */
	Scanner input = null;
	try{
		input = new Scanner(new File(args[0]));
	}
	catch (Exception e){
		System.err.println("Cannot find the file inputted.");
	}

	   	/* first input in text file is always total modules */
		int totalMods = input.nextInt();
	    /* used to calculate absolute addresses */
		int offset = 0;
	    ArrayList<Integer> textArray = new ArrayList<Integer>();
	    ArrayList<Module> moduleArray = new ArrayList<Module>();
	    /* Hash maps used for symbol table and for error checking */
	    Map<String, Symbol> symbolTable = new HashMap<String,Symbol>();
	    /* booleans used for error checking later on */
	    boolean useNoExternal = false; 
	    boolean immediateOnUseList = false;
	    /* ArrayLists used for error checking later on */
	    ArrayList<Integer> errorCheckImmediate = new ArrayList<Integer>();
	    ArrayList<Integer> errorCheckExternal = new ArrayList<Integer>();
	    
	    
	    /* first pass: go through file and form symbol table */
	    for (int i = 0; i < totalMods; i++){
	    	Module newMod = new Module(i);
	    	/* process definitions and place them into modules */
	    	int numOfDef = input.nextInt();
	    	for (int j = 0; j < numOfDef; j++){
	    		String defName = input.next();
	    		int defLoc = input.nextInt() + offset;
	    		Symbol newDef = new Symbol(defName,defLoc);
	    		/* error check #2: check if symbol is defined multiple times */
	    		if (symbolTable.containsKey(newDef.symbol)){
	    			symbolTable.get(defName).multiple = true;
	    		}
	    		else{
	    			newMod.definitions.add(newDef);
	    			newMod.definitions.get(j).defined = true;
		    		newMod.definitions.get(j).relLocation = defLoc - offset;
		    		newMod.definitions.get(j).modNumber = newMod.modNum;
		    		symbolTable.put(defName, newDef);
	    		}
	    	}
	    	/* process uses and place them into modules */
	    	int numOfUses = input.nextInt();
	    	for (int k = 0; k < numOfUses; k++){
	    		String useName = input.next();
	    		Integer useLoc = input.nextInt() + offset;
	    		Symbol newUse = new Symbol(useName,useLoc);
	    		newMod.addUse(newUse);
	    		newMod.uses.get(k).called = true;
	    		newMod.uses.get(k).relLocation = useLoc - offset;
	    		newMod.uses.get(k).modNumber = newMod.modNum;
	    	}
	    	/* process through program text and store instructions in array for later use */
	    	int numOfText = input.nextInt();
	    	newMod.startLoc = offset;
	    	newMod.length = newMod.length + (numOfText-1);
	    	newMod.endLoc = offset + (numOfText-1);
	    	offset = newMod.endLoc + 1;
	    	for (int l = 0; l < numOfText; l++){
	    		Integer text = input.nextInt();
	    		textArray.add(text);
	    	}
	    	
	    	moduleArray.add(newMod);
	    }
	    
	    /* error 4 fix: address appearing in definition exceeds size of module */
	    for (String symbol: symbolTable.keySet()){
			Integer keyAbsLoc = symbolTable.get(symbol).absLocation;
			Integer keyRelLoc = symbolTable.get(symbol).relLocation;
			Integer symbolModLen = moduleArray.get(symbolTable.get(symbol).modNumber).length;
			if (keyRelLoc > symbolModLen){
				keyAbsLoc = moduleArray.get(symbolTable.get(symbol).modNumber).startLoc;
				symbolTable.get(symbol).absLocation = keyAbsLoc;
			}
		}
		
	    /* need this ArrayList for error checking later on */
	    ArrayList<Integer> originalTextArray = (ArrayList<Integer>)textArray.clone();
		for (int i = 0; i < totalMods; i++){
	    	Module currentMod = moduleArray.get(i);
	    	int numOfDefs = currentMod.definitions.size();
	    	int numOfUses = currentMod.uses.size();
	    	/* go through current module and relocate relative addresses */
	    	for (int j = currentMod.startLoc; j < currentMod.endLoc+1; j++){
	    		int lastDigit = (textArray.get(j))%10;
	    		int currentAddress = (textArray.get(j))/10;
	    		/* relative address */
	    		if (lastDigit == 3){
	    			textArray.set(j, currentMod.startLoc + currentAddress);	
	    		}
	    		/* absolute address */
	    		else if (lastDigit == 2) {
	    			textArray.set(j, currentAddress);
	    		}	
	    	}
	    	
	    	/* go through uses and resolve all external references */
	    	for (int m = 0; m < numOfUses; m++){
	    		Symbol currentUse = currentMod.uses.get(m);
	    		Integer currentInstructionLoc = currentMod.startLoc + currentUse.relLocation;
				Integer currentInstruction = textArray.get(currentInstructionLoc);
				/* in case there is a use with no definition */
				if (symbolTable.containsKey(currentUse.symbol) == false){
	    			symbolTable.put(currentUse.symbol, currentUse);
	    		}
				/* loop through module's uses */
				while (currentUse.done == false){
					currentUse.useLocCalls.add(currentInstructionLoc);
					/* location for next instruction */
					Integer nextLoc = Integer.parseInt(Integer.toString(currentInstruction).substring(1, 4));
					if (nextLoc == 777){
    					currentUse.done = true;
    					symbolTable.get(currentUse.symbol).called = true;
    					/* error 3: if symbol is used, but not defined (case for end of list 777) */
    					if ( (symbolTable.get(currentUse.symbol).called == true) && (symbolTable.get(currentUse.symbol).defined == false)){
    						textArray.set(currentInstructionLoc,(currentInstruction/10000)*(1000));
    					}
    					else {
    						textArray.set(currentInstructionLoc,(currentInstruction/10000)*(1000)+ symbolTable.get(currentUse.symbol).absLocation);
    					}
    					break;
	    			}
					/* error 3: if symbol is used, but not defined (normal case) */
					else if( (symbolTable.get(currentUse.symbol).called == true) && (symbolTable.get(currentUse.symbol).defined == false) ){
						textArray.set(currentInstructionLoc,(currentInstruction/10000)*(1000));
					}
					
	    			else{
	    				/* error 5: immediate address appears on use list */
	    				if (currentInstruction%10 == 1){
	    					immediateOnUseList = true;
	    					Integer val = (currentInstruction/10000)*(1000) + symbolTable.get(currentUse.symbol).absLocation;
	    					errorCheckImmediate.add(currentInstructionLoc);
	    					textArray.set(currentInstructionLoc,val);
	    				}
	    				else{
	    				textArray.set(currentInstructionLoc,(currentInstruction/10000)*(1000) + symbolTable.get(currentUse.symbol).absLocation);
	    				}
	    			}
					currentInstruction = textArray.get(currentMod.startLoc + nextLoc);
    				currentInstructionLoc = currentMod.startLoc + nextLoc;
	    			
				}
	    	} 
	    
	    	for (int k = currentMod.startLoc; k < currentMod.endLoc+1; k++){
	    		Integer currentAddress = textArray.get(k);
	    		Integer addressLength = String.valueOf(currentAddress).length();
	    		/* checks for any instructions that were not modified */
	    		if (addressLength > 4){
	    			int lastDigit = (textArray.get(k))%10;
	    			/* immediate operand check */
	    			if (lastDigit == 1){
	    				textArray.set(k, currentAddress/10);
	    			}
	    			/* error 6: external address is not on use list */
	    			else if (lastDigit == 4){
	    				useNoExternal = true;
		    			errorCheckExternal.add(k);
	    				textArray.set(k, currentAddress/10);
	    			}
	    		}
	    	}
		}
		
		/* printing our the symbol table using hashmap */ 
		System.out.println("Symbol Table");
		for (String symbol: symbolTable.keySet()){
			String key = symbol.toString();
			Integer keyAbsLoc = symbolTable.get(symbol).absLocation;
			Integer keyRelLoc = symbolTable.get(symbol).relLocation;
			Integer symbolModLen = moduleArray.get(symbolTable.get(symbol).modNumber).length;
			/* error 2: print out error if symbol is multiply defined */
			if (symbolTable.get(symbol).multiple == true){
				System.out.println(key + "= " + keyAbsLoc + " Error: This variable is multiply defined; first value used");
			}
			/* error 4: address appearing in definition exceeds size of module */
			else if (keyRelLoc > symbolModLen){
				keyAbsLoc = moduleArray.get(symbolTable.get(symbol).modNumber).startLoc;
				System.out.println(key + "= " + keyAbsLoc + " Error: The definition of " + key + " is outside module " + symbolTable.get(key).modNumber + "; zero (relative) used");
			}
			/* all other cases, make sure symbol is defined */
			else if (symbolTable.get(key).defined == true){
				System.out.println(key + "= " + keyAbsLoc);
			}
		}	
		
			System.out.println("Memory Map");
			for (int i = 0; i < textArray.size(); i++){ 
				Integer currentInstruction = textArray.get(i);
				Integer currentLocation = i;
				System.out.println(currentLocation + ": " + currentInstruction);
			}
		
		
		System.out.println();
		for (int i = 0; i < totalMods; i++){
			Module currentMod = moduleArray.get(i);
			int numOfDefs = currentMod.definitions.size();
	    	int numOfUses = currentMod.uses.size();
			/* error 1: symbol is defined but not used */
	    	for (int j = 0; j < numOfDefs; j++){
				Symbol def = currentMod.definitions.get(j);
				if (symbolTable.get(def.symbol).called == false && symbolTable.get(def.symbol).defined == true) {
					System.out.println("Warning: " + def.symbol + " was defined in module " + def.modNumber + " but never used"  );
				}
			}
			/* error 4: print if definition exceeds module size */
			for (int k = 0; k < numOfUses; k++){
				Symbol use = currentMod.uses.get(k);
				for (int l = 0; l < use.useLocCalls.size(); l++){
					if (symbolTable.get(use.symbol).called == true && symbolTable.get(use.symbol).defined == false){
						System.out.println("Error: " + use.symbol + " is not defined; zero used in location " + use.useLocCalls.get(l) );
					}
				}
			}
		}	
		/* error 6:external address is not on a use list */
		if (useNoExternal == true) {
			for (int l = 0; l < errorCheckExternal.size(); l++){
				System.out.println("In location " + errorCheckExternal.get(l) + ", Error: E type address not on use chain; treated as I type");
			}
		}
		/* error 5: immediate address on use list */
		if (immediateOnUseList == true){
			for (int m = 0; m < errorCheckImmediate.size(); m++ ){
				System.out.println("In location " + errorCheckImmediate.get(m) + ", Error: Immediate address on use list; treated as External");
			}
		}
	}
}

	   
	    	  
	    
	    	
	    		
	    		
	    	
	   
	    		
	    	
	    
	    

	

		

