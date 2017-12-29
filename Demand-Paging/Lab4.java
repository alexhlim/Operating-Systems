
/**
 * This is the main file for Lab 4. It simulates demand paging and shows how number of page faults depends on page size, program size, replacement algorithm and job mix.
 * Given an argument in the command line, this program will calculate the number of faults and the average residency for each process. This program simulates three
 * different types of page replacement algorithms: Last In First Out (LIFO), Random, and Least Recently Used (LRU). 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Lab4 {
	
	/* scanner to get random numbers from random number file */
	public static Scanner inputProbability = null;
	
	public static void main(String[] args){
		
	
	Scanner input = null;
	try{
		inputProbability = new Scanner(new File(args[0]));
		input = new Scanner(args[1]);
	}
	catch (Exception e){
		System.err.println("Cannot find argument inputed");
	}
	
	int M = input.nextInt();
	int P = input.nextInt();
	double S = input.nextInt();
	int J = input.nextInt();
	int N = input.nextInt();
	String R = input.next();
	int debugOutput = input.nextInt();
	
	/* display input */
	System.out.println("The machine size is " + M + ".");
	System.out.println("The page size is " + P + ".");
	System.out.println("The process size is " + S + ".");
	System.out.println("The job mix number is " + J + ".");
	System.out.println("The number of references per process is " + N + ".");
	System.out.println("The replacement algorithm is " + R + ".");
	System.out.println("The level of debugging output is " + debugOutput + "." );
	
	/* depending on J, create ArrayList of Processes */
	ArrayList<Process> processList = new ArrayList<Process>();
	if (J == 1){
		Process p1 = new Process(1,1,0,0);
		processList.add(p1);
	}
	else if (J == 2){
		Process p1 = new Process(1,1,0,0);
		Process p2 = new Process(2,1,0,0);
		Process p3 = new Process(3,1,0,0);
		Process p4 = new Process(4,1,0,0);
		processList.add(p1);
		processList.add(p2);
		processList.add(p3);
		processList.add(p4);
		
	}
	else if (J == 3){
		Process p1 = new Process(1,0,0,0);
		Process p2 = new Process(2,0,0,0);
		Process p3 = new Process(3,0,0,0);
		Process p4 = new Process(4,0,0,0);
		processList.add(p1);
		processList.add(p2);
		processList.add(p3);
		processList.add(p4);
	}
	else {
		Process p1 = new Process(1,0.75,0.25,0);
		Process p2 = new Process(2,0.75,0,0.25);
		Process p3 = new Process(3,0.75,0.125,0.125);
		Process p4 = new Process(4,0.5,0.125,0.125);
		processList.add(p1);
		processList.add(p2);
		processList.add(p3);
		processList.add(p4);
		
	}
	System.out.println();
	
	/* create frame list */
	ArrayList<Frame> frameList = new ArrayList<Frame>();
	int numberOfFrames = M / P;
	for (int i = 0; i < numberOfFrames; i++){
		Frame newFrame = new Frame(i);
		frameList.add(newFrame);
		
	}
	
	
	/* execute replacement algorithm specified in command line */ 
	if (R.equalsIgnoreCase("LRU")){
		LRU(M, P, S, J, N, processList, frameList);
	}
	else if (R.equalsIgnoreCase("LIFO")){
		LIFO(M, P, S, J, N, processList, frameList);
	}
	else{
		Random(M, P, S, J, N, processList, frameList);
	}
	
	
	
	}
	
	/**
	 * This method simulates LIFO. It determines the page to evict by choosing the page that was last inserted into a frame.
	 * @param M : machine size
	 * @param P : page size
	 * @param S : size of each process
	 * @param J : job mix
	 * @param N : number of references for each process
	 * @param processList : list of processes
	 * @param frameList : list of frames
	 */
	public static void LIFO(int M, int P, double S, int J, int N, ArrayList<Process> processList, ArrayList<Frame> frameList){
		
		/* keep track of time */
		int time = 0;
		boolean done = false;
		Frame lastFrameIn = null;
		while (done == false){
			for(int i = 0; i < processList.size(); i++){
				Process currentProcess = processList.get(i);
				/* quantam = 3 */
				for (int j = 0; j < 3; j++){
					time++;
					int r = inputProbability.nextInt();
					double y = (double) r / (Integer.MAX_VALUE + 1d);
					
					/* calculate first word */
					if (currentProcess.firstWord == true){
						currentProcess.currentWord = (111*currentProcess.ID)%(int)S;
					}
					
					/* case 1 */
					if (y < currentProcess.A){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+1+(int)S)%(int)S;
					/* case 2 */	
					}
					else if (y < currentProcess.A + currentProcess.B){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord-5+(int)S)%(int)S;
					}
					/* case 3 */
					else if (y < currentProcess.A + currentProcess.B + currentProcess.C){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+4+(int)S)%(int)S;
					}
					/* case 4 */
					else{
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						if (J == 3){
							int r2 = inputProbability.nextInt();
							currentProcess.nextWord = ((r2+(int)S)%(int)S);
						}
						/* J == 4 */
						else{
							/* A = 0.5, B = 0.125, C = 0.125 */
							if (currentProcess.ID == 4){
								int r2 = inputProbability.nextInt();
								currentProcess.nextWord = ((r2+(int)S)%(int)S);
							}
							
						}
						
					}
					
					if (currentProcess.firstWord == true){
						currentProcess.firstWord = false;
					}
					

					currentProcess.processSize++;
					
					
					/* find the frame that contains the current process' page, if it exists */
					boolean contains = false;
					for (int k = frameList.size()-1; k >= 0; k--){
						Frame currentFrame = frameList.get(k);
						
						Page temp = new Page(currentProcess.currentWord/P);
						if (currentProcess.pageTable.contains(temp) == true && currentFrame.process != null){
							if ( (currentFrame.page.ID == temp.ID) && (currentFrame.process.ID == currentProcess.ID) ){
								currentProcess.frame = currentFrame;
								contains = true;
							}
						}
					}
					
					
					/* hit */
					if (contains == true){
						currentProcess.frame.timestamp = time;
					}
					/* find available frame or evict */		
					else{
						Frame firstAvailable = null;
						boolean found = false;
						for (int k = frameList.size()-1; k >= 0; k--){
							Frame currentFrame = frameList.get(k);
							if (currentFrame.occupied == false){
								firstAvailable = currentFrame;
								found = true;
								break;
							}
						}
						
						/* found an available frame */
						if (found == true){
							currentProcess.frame = firstAvailable;
							currentProcess.frame.occupied = true;
							
							if (currentProcess.pageTable.contains(currentProcess.currentWord/P) == false){
								Page newPage = new Page(currentProcess.currentWord/P);
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								currentProcess.frame.page = newPage;
							}
							
							currentProcess.frame.process = currentProcess;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							currentProcess.frame.page.timeLoaded = time;
							
							lastFrameIn = currentProcess.frame;

						}
						/* evict */
						else{
							
							/* for calculating residency */
							if (lastFrameIn.page != null){
								lastFrameIn.page.timeEvicted = time;
								lastFrameIn.process.averageResidency += (lastFrameIn.page.timeEvicted - lastFrameIn.page.timeLoaded);
							}
							
							/* evicted frame is the last frame in */ 
							currentProcess.frame = lastFrameIn;
							Page newPage = new Page(currentProcess.currentWord/P);
							if (currentProcess.pageTable.contains(currentProcess.currentWord/P) == false){
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								lastFrameIn.page = newPage;
								
							}
							currentProcess.frame.occupied = true;
							currentProcess.frame.page.timeLoaded = time;
							lastFrameIn.process.numOfEvictions++;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							lastFrameIn.process = currentProcess;
							lastFrameIn = currentProcess.frame;
							
						}
						
					
					}
					
					
					if (currentProcess.processSize == N){
						currentProcess.done = true;
						break;
					}

				}
			
			}
			int counter = 0;
			for (int k = 0; k < processList.size(); k++){
				Process currentProcess = processList.get(k);
				if (currentProcess.done == true){
					counter++;
				}
				if (counter == processList.size()){
					done = true;
				}
			}
	
			
		}
		
		/* print results */
		int totalFaults = 0;
		double overallResidency = 0;
		double totalEvictions = 0;
		for (int i = 0; i < processList.size(); i++){
			Process currentProcess = processList.get(i);
			totalFaults += currentProcess.numOfFaults;
			totalEvictions += currentProcess.numOfEvictions;
			overallResidency += currentProcess.averageResidency;
			currentProcess.averageResidency = (currentProcess.averageResidency) / (currentProcess.numOfEvictions);
			if (currentProcess.numOfEvictions == 0){
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults. With no evictions, the average residency is undefined." );
			}
			else{
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults with an average residency of " + currentProcess.averageResidency);
			}
		}
		
		overallResidency = (overallResidency / totalEvictions);
		System.out.println();
		if (totalEvictions == 0){
			System.out.println("The total number of faults is " + totalFaults + ". With no evictions, the overall average residency is undefined.");
		}
		else{
			System.out.println("The total number of faults is " + totalFaults + " and the overall average residency is " + overallResidency);
		}
		
		
	
	}
		
		
	
	/**
	 * This method simulates the random page replacement algorithm. It uses random numbers from a random number file and evicts a random page based on that random number.
	 * @param M : machine size
	 * @param P : page size
	 * @param S : size of each process
	 * @param J : job mix
	 * @param N : number of references for each process
	 * @param processList : list of processes
	 * @param frameList : list of frames
	 */
	public static void Random(int M, int P, double S, int J, int N, ArrayList<Process> processList, ArrayList<Frame> frameList){
			
		/* keep track of time */
		int time = 0;
		boolean done = false;
		Frame randomFrame = null;
		int r = 0;
		double y = 0;
		while (done == false){
			for(int i = 0; i < processList.size(); i++){
				Process currentProcess = processList.get(i);
				/* quantam = 3 */
				for (int j = 0; j < 3; j++){
					time++;
					r = inputProbability.nextInt();
					
					/* calculate first word */
					if (currentProcess.firstWord == true){
						currentProcess.currentWord = (111*currentProcess.ID)%(int)S;
					}
					
					/* choose word based on whether its the first word or next word.
					 * doing this because I update nextWord at the end of the loop.
					 */
					Page temp;
					if (currentProcess.firstWord == true){
						temp = new Page(currentProcess.currentWord/P);
					}
					else{
						temp = new Page(currentProcess.nextWord/P);
					}
					
					/* see if frame contains page and process */
					boolean contains = false;
					for (int k = frameList.size()-1; k >= 0; k--){
						Frame currentFrame = frameList.get(k);
					
						if (currentProcess.pageTable.contains(temp) == true && currentFrame.process != null){
							if ( (currentFrame.page.ID == temp.ID) && (currentFrame.process.ID == currentProcess.ID) ){
								currentProcess.frame = currentFrame;
								contains = true;
							}
						}
					}
					
					/* hit */
					if (contains == true){
						currentProcess.frame.timestamp = time;
					}
					/* find available frame or evict */		
					else{
						Frame firstAvailable = null;
						boolean found = false;
						for (int k = frameList.size()-1; k >= 0; k--){
							Frame currentFrame = frameList.get(k);
							if (currentFrame.occupied == false){
								firstAvailable = currentFrame;
								found = true;
								break;
							}
						}
						
						/* found an available frame */
						if (found == true){
							currentProcess.frame = firstAvailable;
							currentProcess.frame.occupied = true;
							
							if (currentProcess.pageTable.contains(temp) == false){
								Page newPage = new Page(temp.ID);
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								currentProcess.frame.page = newPage;
							}
							
							currentProcess.frame.process = currentProcess;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							currentProcess.frame.page.timeLoaded = time;
							
						}
						/* evict */
						else{
							
							/* select random frame using random number */
							int randomFrameIndex = (r%frameList.size());
							randomFrame = frameList.get(randomFrameIndex);
							
							r = inputProbability.nextInt();
							
							/* for calculating residency */
							if (randomFrame.page != null){
								randomFrame.page.timeEvicted = time;
								randomFrame.process.averageResidency += (randomFrame.page.timeEvicted - randomFrame.page.timeLoaded);
							}
							
							currentProcess.frame = randomFrame;
							
							Page newPage = new Page(temp.ID);
							if (currentProcess.pageTable.contains(temp.ID) == false){
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								randomFrame.page = newPage;
								
							}
							currentProcess.frame.occupied = true;
							currentProcess.frame.page.timeLoaded = time;
							randomFrame.process.numOfEvictions++;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							randomFrame.process = currentProcess;
							
						}
						
					
					}
					
					
					/* now calculate nextWord. doing this because I need to grab the random numbers from the file in the correct order */
					y = ( (double) r ) / (Integer.MAX_VALUE + 1d);
					
					/* case 1 */
					if (y < currentProcess.A){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+1+(int)S)%(int)S;
					/* case 2 */	
					}
					else if (y < currentProcess.A + currentProcess.B){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord-5+(int)S)%(int)S;
					}
					/* case 3 */
					else if (y < currentProcess.A + currentProcess.B + currentProcess.C){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+4+(int)S)%(int)S;
					}
					/* case 4 */
					else{
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						if (J == 3){
							int r2 = inputProbability.nextInt();
							currentProcess.nextWord = ((r2+(int)S)%(int)S);
						}
						/* J == 4 */
						else{
							/* A = 0.5, B = 0.125, C = 0.125 */
							if (currentProcess.ID == 4){
								int r2 = inputProbability.nextInt();
								currentProcess.nextWord = ((r2+(int)S)%(int)S);
							}
							
						}
						
					}
					
					if (currentProcess.firstWord == true){
						currentProcess.firstWord = false;
					}
					
					currentProcess.processSize++;
					
					if (currentProcess.processSize == N){
						currentProcess.done = true;
						break;
					}
					
				}
				
			}
			
			int counter = 0;
			for (int k = 0; k < processList.size(); k++){
				Process currentProcess = processList.get(k);
				if (currentProcess.done == true){
					counter++;
				}
				if (counter == processList.size()){
					done = true;
				}
			}
	
			
		}
		
		/* print results */
		int totalFaults = 0;
		double overallResidency = 0;
		double totalEvictions = 0;
		for (int i = 0; i < processList.size(); i++){
			Process currentProcess = processList.get(i);
			totalFaults += currentProcess.numOfFaults;
			totalEvictions += currentProcess.numOfEvictions;
			overallResidency += currentProcess.averageResidency;
			currentProcess.averageResidency = (currentProcess.averageResidency) / (currentProcess.numOfEvictions);
			if (currentProcess.numOfEvictions == 0){
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults. With no evictions, the average residency is undefined." );
			}
			else{
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults with an average residency of " + currentProcess.averageResidency);
			}
		}
		
		overallResidency = (overallResidency / totalEvictions);
		System.out.println();
		if (totalEvictions == 0){
			System.out.println("The total number of faults is " + totalFaults + ". With no evictions, the overall average residency is undefined.");
		}
		else{
			System.out.println("The total number of faults is " + totalFaults + " and the overall average residency is " + overallResidency);
		}
		
		
	
	}
	
	/**
	 * This is the method that simulates LRU. It chooses the frame that was used the at the shortest time stamp. 
	 * @param M : machine size
	 * @param P : page size
	 * @param S : size of each process
	 * @param J : job mix
	 * @param N : number of references for each process
	 * @param processList : list of processes
	 * @param frameList : list of frames
	 */
	public static void LRU(int M, int P, double S, int J, int N, ArrayList<Process> processList, ArrayList<Frame> frameList){
		
		/* keep track of time */
		int time = 0;
		boolean done = false;
		while (done == false){
			for(int i = 0; i < processList.size(); i++){
				Process currentProcess = processList.get(i);
				/* quantam = 3 */
				for (int j = 0; j < 3; j++){
					time++;
					int r = inputProbability.nextInt();
					double y = (double) r / (Integer.MAX_VALUE + 1d);
					
					/* calculate first word */
					if (currentProcess.firstWord == true){
						currentProcess.currentWord = (111*currentProcess.ID)%(int)S;
					}
					
					/* case 1 */
					if (y < currentProcess.A){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+1+(int)S)%(int)S;
					/* case 2 */	
					}
					else if (y < currentProcess.A + currentProcess.B){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord-5+(int)S)%(int)S;
					}
					/* case 3 */
					else if (y < currentProcess.A + currentProcess.B + currentProcess.C){
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						currentProcess.nextWord = (currentProcess.currentWord+4+(int)S)%(int)S;
					}
					/* case 4 */
					else{
						if (currentProcess.firstWord == false){
							currentProcess.currentWord = currentProcess.nextWord;
						}
						if (J == 3){
							int r2 = inputProbability.nextInt();
							currentProcess.nextWord = ((r2+(int)S)%(int)S);
						}
						/* J == 4 */
						else{
							/* A = 0.5, B = 0.125, C = 0.125 */
							if (currentProcess.ID == 4){
								int r2 = inputProbability.nextInt();
								currentProcess.nextWord = ((r2+(int)S)%(int)S);
							}
							
						}
						
					}
					
					if (currentProcess.firstWord == true){
						currentProcess.firstWord = false;
					}
					

					currentProcess.processSize++;
					
					/* find the frame that contains the current process' page, if it exists */
					boolean contains = false;
					for (int k = frameList.size()-1; k >= 0; k--){
						Frame currentFrame = frameList.get(k);
						
						Page temp = new Page(currentProcess.currentWord/P);
						if (currentProcess.pageTable.contains(temp) == true && currentFrame.process != null){
							if ( (currentFrame.page.ID == temp.ID) && (currentFrame.process.ID == currentProcess.ID) ){
								currentProcess.frame = currentFrame;
								contains = true;
							}
						}
					}
					/* hit */
					if (contains == true){
						currentProcess.frame.timestamp = time;
					}
					/* find available frame or evict */		
					else{
						Frame firstAvailable = null;
						boolean found = false;
						for (int k = frameList.size()-1; k >= 0; k--){
							Frame currentFrame = frameList.get(k);
							if (currentFrame.occupied == false){
								firstAvailable = currentFrame;
								found = true;
								break;
							}
						}
						
						/* found an available frame */
						if (found == true){
							currentProcess.frame = firstAvailable;
							currentProcess.frame.occupied = true;
							
							if (currentProcess.pageTable.contains(currentProcess.currentWord/P) == false){
								Page newPage = new Page(currentProcess.currentWord/P);
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								currentProcess.frame.page = newPage;
							}
							
							currentProcess.frame.process = currentProcess;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							currentProcess.frame.page.timeLoaded = time;

						}
						/* evict */
						else{
							Frame lowestFrame = null;
							Frame previousLowest = null;
							int lowest = Integer.MAX_VALUE;
							for (int k = frameList.size()-1; k >= 0; k--){
								Frame currentFrame = frameList.get(k);
								if ( (currentFrame.timestamp <= lowest) ){
									if (k == frameList.size()-1){
										lowest = currentFrame.timestamp;
										lowestFrame = currentFrame;
										previousLowest = currentFrame;
									}
									else{
										if (currentFrame.timestamp < previousLowest.timestamp){
											lowest = currentFrame.timestamp;
											previousLowest = lowestFrame;
											lowestFrame = currentFrame;
										}
									}
								}
							}
							
							/* for calculating residency */
							if (lowestFrame.page != null){
								lowestFrame.page.timeEvicted = time;
								lowestFrame.process.averageResidency += (lowestFrame.page.timeEvicted - lowestFrame.page.timeLoaded);
							}
							
							currentProcess.frame = lowestFrame;
							Page newPage = new Page(currentProcess.currentWord/P);
							if (currentProcess.pageTable.contains(currentProcess.currentWord/P) == false){
								newPage.frame = currentProcess.frame;
								currentProcess.pageTable.add(newPage);
								lowestFrame.page = newPage;
								
							}
							currentProcess.frame.occupied = true;
							currentProcess.frame.page.timeLoaded = time;
							lowestFrame.process.numOfEvictions++;
							currentProcess.frame.timestamp = time;
							currentProcess.numOfFaults++;
							lowestFrame.process = currentProcess;
							
						}
						
					
					}
					
					if (currentProcess.processSize == N){
						currentProcess.done = true;
						break;
					}

				}

			}
			int counter = 0;
			for (int k = 0; k < processList.size(); k++){
				Process currentProcess = processList.get(k);
				if (currentProcess.done == true){
					counter++;
				}
				if (counter == processList.size()){
					done = true;
				}
			}
	
			
		}
		
		/* print results */
		int totalFaults = 0;
		double overallResidency = 0;
		double totalEvictions = 0;
		for (int i = 0; i < processList.size(); i++){
			Process currentProcess = processList.get(i);
			totalFaults += currentProcess.numOfFaults;
			totalEvictions += currentProcess.numOfEvictions;
			overallResidency += currentProcess.averageResidency;
			currentProcess.averageResidency = (currentProcess.averageResidency) / (currentProcess.numOfEvictions);
			if (currentProcess.numOfEvictions == 0){
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults. With no evictions, the average residency is undefined." );
			}
			else{
				System.out.println("Process " + currentProcess.ID + " had " + currentProcess.numOfFaults + " faults with an average residency of " + currentProcess.averageResidency);
			}
		}
		
		overallResidency = (overallResidency / totalEvictions);
		System.out.println();
		if (totalEvictions == 0){
			System.out.println("The total number of faults is " + totalFaults + ". With no evictions, the overall average residency is undefined.");
		}
		else{
			System.out.println("The total number of faults is " + totalFaults + " and the overall average residency is " + overallResidency);
		}
		
		
	
	}
}
	
	
	



