
/**
 * This is the main program for Lab 3. It goes through an input text file and simulates Optimistic and Banker's Resource Allocation Managers.
 * An optimistic resource manager grants a request when a resource is available, while the banker checks to see if the state is safe
 * before granting a resource. The program prints results of termination time, waiting time, and percentage of time spent waiting for 
 * both resource managers. 
 * 
 * @author Alexander Lim
 * Operating Systems, Fall 2017
 *
 */

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;



public class Lab3 {

	public static ArrayList<Task> taskList;
	public static ArrayList<Resource> resourceList;
	public static ArrayList<Instruction> sortedInstructionList;
	public static void main(String[] args) {
		
		/* declare new scanner to go through text files */
		Scanner input = null;
		try{
			input = new Scanner(new File(args[0]));
		}
		catch (Exception e){
			System.err.println("Cannot find the file inputted.");
		}
		
		int numOfTasks = 0;
		int numOfResources = 0;
		taskList = new ArrayList<Task>();
		resourceList = new ArrayList<Resource>();
		sortedInstructionList = new ArrayList<Instruction>();
		/* collect information of first input line */
		numOfTasks = input.nextInt();
		for (int i = 0; i < numOfTasks; i++){
			Task newTask = new Task(i+1);
			taskList.add(newTask);
		}
		numOfResources = input.nextInt();
		for (int j = 0; j < numOfResources; j++){
			int units = input.nextInt();
			Resource newResource = new Resource(j+1,units);
			resourceList.add(newResource);
		}
		/* add to each task's borrowed resource list */ 
		for (int k = 0; k < taskList.size(); k++){
			Task currentTask = taskList.get(k);
			for (int l = 0; l < resourceList.size(); l++){
				Resource currentResource = new Resource(k+1,0);
				currentTask.borrowedResources.add(currentResource);
			}
		}
		
		/* go through data */
		int index = 0;
		while (input.hasNext() == true){
			String action = input.next();
			int taskID = input.nextInt();
			int resourceNum = input.nextInt();
			int units = input.nextInt();
			Instruction newInstruction = new Instruction(index, action,taskID,resourceNum,units);
			/* -1 because offset */
			taskList.get(taskID-1).instructionList.add(newInstruction);
			sortedInstructionList.add(newInstruction);
			index++;			
		}
		
		/* deep copy of data for each manager */
		ArrayList<Task> optimisticTasks = new ArrayList<Task>();
		for (Task t: taskList) {
			  optimisticTasks.add(t.clone());
		}
		optimisticRM(optimisticTasks,resourceList);
		
		System.out.println();
		System.out.println();
		
		ArrayList<Task> bankerTasks = new ArrayList<Task>();
		for (Task t: taskList) {
			  bankerTasks.add(t.clone());
		}
		bankerRM(bankerTasks,resourceList);
		
	}
	
	/**
	 * This is a method that prints out the results for the Optimistic Resource Manager. It prints out termination time, waiting time,
	 * and percentage of time spent waiting.
	 * @param taskList : print results from this list 
	 */
	public static void optimisticRMResult(ArrayList<Task> taskList){
		/* make sure it displays in order */ 
		Collections.sort(taskList);
		DecimalFormat df = new DecimalFormat("#0.00"); 
		int totalFinish = 0;
		int totalWait = 0;
		System.out.println("FIFO:" +  "   " + "Run Time" + "  " + "Wait Time" + "     " + "Percentage");
		for (int i = 0; i < taskList.size(); i++){
			Task currentTask = taskList.get(i);
			System.out.print(currentTask.toString());
			if (currentTask.aborted == true){
				System.out.print("	aborted");
				System.out.println();
			}
			else {
				totalFinish += currentTask.terminatedTime;
				totalWait += currentTask.waitingTime;
				double percentage = ((double)currentTask.waitingTime/(double)currentTask.terminatedTime)*100;
				System.out.print("   " + currentTask.terminatedTime + "          " + currentTask.waitingTime + "          " + df.format(percentage) + "%" );
				System.out.println();
			}
		}
		double totalPercentage = ( ( (double)totalWait / (double)totalFinish ) )*100; 
		System.out.println("Total:" + "   " + totalFinish + "          " + totalWait + "          " + df.format(totalPercentage)+ "%");
	}
			
	/**
	 * This the method that simulates the Optimistic Resource Manager. It uses ArrayLists to store the task and resources. When a task
	 * is blocked, the task is put into a blocked ArrayList, which is constantly updated at the end of every cycle so that tasks go
	 * in the correct order. Once a task is finished, it is placed into a finished ArrayList, which will be used for printing results. When
	 * a deadlock occurs, the manager aborts the lowest number deadlock task.
	 * 
	 * @param taskList : list of tasks to be completed
	 * @param resourceList : resources available
	 */
	public static void optimisticRM(ArrayList<Task> taskList, ArrayList<Resource> resourceList){
		
		boolean done = false;
		int cycle = 0;
		int numTerminated = 0;
		int requestNotGranted; 
		int originalTaskListSize = taskList.size();
		ArrayList<Task> blockedTasks = new ArrayList<Task>();
		ArrayList<Task> finishedTasks = new ArrayList<Task>();
		boolean useBlockedList = false;
		while(done == false){
			requestNotGranted = 0; 
			/* determine which task to do, prioritizes blocked tasks */
			if (blockedTasks.isEmpty() == false){
				useBlockedList = true;
			}
			else {
				useBlockedList = false;
			}
			for (int i = 0; i < taskList.size(); i++){
				Task currentTask;
				if (blockedTasks.isEmpty() == false && useBlockedList == true){
					currentTask = blockedTasks.get(i);
				}
				else{
					currentTask = taskList.get(i);
				}
				/* keep track of which instruction we are working on */
				Instruction currentInstruction = currentTask.instructionList.get(0);
				/* initiate */
				if (currentInstruction.action.equals("initiate")){
					currentTask.instructionList.remove(currentInstruction);
				}
				/* request */
				else if (currentInstruction.action.equals("request")){
					Resource currentResource = resourceList.get(currentInstruction.resourceNum-1);
					/* make sure there is enough resources to give to task */
					if (currentResource.units >= currentInstruction.units){
						currentTask.blocked = false;
						/* add units to task's borrowedResource list */ 
						currentTask.borrowedResources.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
						/* take away units from original resource */ 
						currentResource.units = currentResource.units - currentInstruction.units;
						currentTask.instructionList.remove(currentInstruction);
					}
					/* cannot grant request */
					else{
						currentTask.blocked = true;
						currentTask.waitingTime++;
						if (blockedTasks.contains(currentTask) == false){
							blockedTasks.add(currentTask);
						}
						requestNotGranted++;
					}
				}
				/* release */
				else if (currentInstruction.action.equals("release")){
					/* resources released won't be available until next cycle */
					currentTask.released = true;
				}
				/* compute */
				else if (currentInstruction.action.equals("compute")){
					currentInstruction.resourceNum--;
					if (currentInstruction.resourceNum == 0){
						currentTask.instructionList.remove(currentInstruction);
					}
				}
				/* terminate */
				else{
					/* keep track of termination time */
					currentTask.terminatedTime = cycle;
					currentTask.terminated = true;
					numTerminated++;
					currentTask.instructionList.remove(currentInstruction);
					/* add task to finished list */
					finishedTasks.add(currentTask);
				}
			}
	
			if (blockedTasks.isEmpty() == false){
				/* for loop to get rid of tasks whose requests were granted in the cycle */
				for (int i = 0; i < taskList.size(); i++){
					Task currentTask = taskList.get(i);
					if (currentTask.blocked == false && blockedTasks.contains(currentTask)){
						blockedTasks.remove(currentTask);
					}
				}
				/* for loop to form a new order for the blocked list for the next cycle */
				for (int i = 0; i < taskList.size();i++){
					Task currentTask = taskList.get(i);
					if (currentTask.blocked == false && blockedTasks.contains(currentTask) == false){
						blockedTasks.add(currentTask);
					}
				}
			}
			
			/* for loop to remove any tasks that were terminated this cycle */
			for (int i = 0; i < taskList.size(); i++){
				Task currentTask = taskList.get(i);
				if (currentTask.terminated == true){
					taskList.remove(currentTask);
					blockedTasks.remove(currentTask);
				}
			}
			
			/* deals with deadlock */
			if (requestNotGranted == taskList.size()){
				boolean deadlocked = true;
				int counter = 0;
				while (deadlocked == true && counter < taskList.size()){
					Task currentTask = taskList.get(0);
					/* resource that current instruction is referring to */ 
					Resource currentResource = resourceList.get((currentTask.instructionList.get(0).resourceNum)-1);
					/* abort lowest ranked task */ 
					if ( currentResource.units < currentTask.instructionList.get(0).units){
						for (int i = 0; i < currentTask.borrowedResources.size(); i++){
							Resource originalResource = resourceList.get(i);
							/* giving back units to original resource */ 
							originalResource.units += currentTask.borrowedResources.get(i).units;
							currentTask.borrowedResources.get(i).units = 0;
						}
						currentTask.aborted = true;
						finishedTasks.add(currentTask);
						/* keep track of termination time */
						currentTask.terminatedTime = cycle;
						/* incremented because task was removed */
						currentTask.terminated = true;
						numTerminated++;
						taskList.remove(currentTask);
						blockedTasks.remove(currentTask);
					}
					else {
						deadlocked = false;
					}
					counter++;
				}
			}
			
			/* check if all tasks are finished */
			if (numTerminated == originalTaskListSize){
				done = true;
			}
			
			/* give back resources that were released in cycle, so they are available next cycle */
			for (int i = 0; i < taskList.size();i++){
				Task currentTask = taskList.get(i);
				if (currentTask.released == true){
					Resource currentResource = resourceList.get(currentTask.instructionList.get(0).resourceNum-1);
					Instruction currentInstruction = currentTask.instructionList.get(0);
					/* give units back to original resource */
					currentResource.units += currentTask.borrowedResources.get(currentInstruction.resourceNum-1).units;
					/* subtract units from task's borrowedResource list */ 
					currentTask.borrowedResources.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
					currentTask.instructionList.remove(currentInstruction);
					currentTask.released = false;
				}
			}
			cycle++;
		}
		
		/* display result */
		optimisticRMResult(finishedTasks);
		

	}
	
	/**
	 * This method is used for the Banker's Algorithm by determining if a state is safe or not. It does this by simulating what would happen
	 * if the banker grants the task's request. It first starts by creating deep copies of the task list, resource list, blocked list, total list,
	 * and the available list, so that the original lists are not modified. The data structure used is ArrayList, and keeps iterating through
	 * tasks stored in this ArrayList until either the task is completed, or the state is deemed unsafe. Even though this algorithm prevents deadlocks,
	 * if a task's request exceeds its initial claim, then that task is aborted. 
	 * 
	 * @param task : evaluating whether granting this task's request would lead to a safe state
	 * @param taskList : list of task used by banker
	 * @param resourceList : list of resources used by banker
	 * @param blockedList : blocked list used by banker
	 * @param requestNotGranted : how many requests were not granted in the cycle (if task evaluated is not the first task in the cycle) 
	 * @param numberTerminated : how many requests were terminated 
	 * @param total : how many of each resources is allocated the tasks
	 * @param available : how many of each resource is available to the tasks
	 * @param usedBlockedList : if the blocked list is being used 
	 * @return : true if the state is safe, and false if it is not safe 
	 */
	public static boolean safe(Task task, ArrayList<Task> taskList, ArrayList<Resource> resourceList, ArrayList<Task> blockedList, int requestNotGranted, int numberTerminated, ArrayList<Resource> total, ArrayList<Resource> available, boolean usedBlockedList){
		
		/* deep copies of blocked list, task list, available list, and total lists, so I do not modify original task list */
		ArrayList<Task> safeTasks = new ArrayList<Task>();
		for (Task t: taskList) {
			  safeTasks.add(t.clone());
		}
		
		ArrayList<Task> safeBlockedList = new ArrayList<Task>();
		for (int w = 0; w < blockedList.size(); w++){
			int num = blockedList.get(w).ID;
			boolean done = false;
			int index = 0;
			while (done == false){
				if (safeTasks.get(index).ID == num){
					done = true;
				}
				else{
					index++;
				}
			}
			safeBlockedList.add(safeTasks.get(index));
		}
		
		ArrayList<Resource> safeTotal = new ArrayList<Resource>();
		for (Resource t: total) {
			  safeTotal.add(t.clone());
		}
		
		ArrayList<Resource> safeAvailable = new ArrayList<Resource>();
		for (Resource a: available) {
			  safeAvailable.add(a.clone());
		}
		
	
		
		/* different copies of variables so I do not modify banker's information */ 
		int reqNotGranted = new Integer(requestNotGranted);
		int numTerminated = new Integer(numberTerminated);
		boolean newUsedBlockedList = new Boolean(usedBlockedList);
		
		boolean safe = true;
		boolean done = false; 
		int originalTaskListSize = safeTasks.size();
		boolean firstCycle = true;
		int bound = safeTasks.size();
		while (done == false && safe == true){
			if (safeBlockedList.isEmpty() == false && safeBlockedList.contains(task)){
				newUsedBlockedList = true;
			}
			else {
				newUsedBlockedList = false;
			}
			
			/* find location of task - especially in the case of blocked list, because tasks aren't in order */
			int location = 0;
			if (newUsedBlockedList == true && firstCycle == true){
				boolean found = false;
				while (found == false){
					if (safeBlockedList.get(location).ID == task.ID){
						found = true;
					}
					else{
						location++;
					}
				}
			}
			else if (newUsedBlockedList == false && firstCycle == true) {
				boolean found = false;
				while (found == false){
					if (safeTasks.get(location).ID == task.ID){
						found = true;
					}
					else{
						location++;
					}
				}
			}
			
			/* determine how many tasks go in the remaining cycle */ 
			if (firstCycle == true){
				bound = safeTasks.size() - location;
			}
			else{
				bound = safeTasks.size();
				reqNotGranted = 0; 
			}
			
			int nextIndex = 0;
			for (int i = 0; i < bound; i++){
				Task currentTask;
				if (firstCycle == true && i == 0){
					/* find the task, because we don't know the exact location in blocked List + keep track of next task to perform */
					if (safeBlockedList.isEmpty() == false && newUsedBlockedList == true){
						boolean found = false;
						int index = 0;
						while (found == false){
							if (safeBlockedList.get(index).ID == task.ID){
								found = true;
							}
							else{
								index++;
							}
						}
						currentTask = safeBlockedList.get(index);
						if (index + 1 > safeBlockedList.size()){
							nextIndex = 0;
						}
						else{
							nextIndex = index+1;
						}
					}
					else {
						currentTask = safeTasks.get(safeTasks.indexOf(task));
						if ( (task.ID-1) + 1 > safeBlockedList.size()){
							nextIndex = 0;
						}
						else{
							nextIndex = task.ID;
						}
					}
				}
				else{
					if (safeBlockedList.isEmpty() == false && newUsedBlockedList == true){
						boolean found = false;
						int index = 0;
						while (found == false){
							if (index == nextIndex ){
								found = true;
							}
							else{
								index++;
							}
						}
						currentTask = safeBlockedList.get(index);
						/* if the task is the end of the list, then the next task is at the beginning of the list */ 
						if ( index + 1 > safeBlockedList.size()){
							nextIndex = 0;
						}
						else{
							nextIndex = index+1;
						}
				
					}
					else{
						currentTask = safeTasks.get(i);
						/* if the task is the end of the list, then the next task is at the beginning of the list */ 
						if ( (i) + 1 > safeBlockedList.size()){
							nextIndex = 0;
						}
						else{
							nextIndex = i+1;
						}
					}
				}

				if (currentTask.instructionList.isEmpty() == false){
					/* keep track of which instruction we are working on */
					Instruction currentInstruction = currentTask.instructionList.get(0);
					/* initiate */
					if (currentInstruction.action.equals("initiate")){
						/* check to see if a task's claims exceeds what is available, if so, abort the task */
						for (int j = 0; j < currentTask.claimsList.size(); j++){
							if (currentTask.claimsList.get(j).units > safeAvailable.get(j).units){
								currentTask.aborted = true;
								reqNotGranted++;
							}
						}
						if (currentTask.aborted == false){
							currentTask.instructionList.remove(currentInstruction);
						}
					}
					/* request */
					else if (currentInstruction.action.equals("request")){
						/* check if current task's claims exceeds requests, if so abort it */
						for (int j = 0; j < currentTask.claimsList.size(); j++){
							if (currentInstruction.resourceNum == currentTask.currentAlloc.get(j).ID){
								if (currentTask.currentAlloc.get(j).units + currentInstruction.units > currentTask.claimsList.get(j).units){
									currentTask.aborted = true;
								}
							}
								
						}
						if (currentTask.aborted == false){
							int index = currentInstruction.resourceNum-1;
							/* check if current task's max additional is less than or equal to what is available for each resource */
							int resourcesOk = 0;
							for (int j = 0; j < safeAvailable.size(); j++){
								if (currentTask.maxAdditional.get(j).units <= safeAvailable.get(j).units){
									resourcesOk++;
								}
							}
							/* check if available resources exceeds task max additional */
							if (resourcesOk == safeAvailable.size()){
								if (currentTask.maxAdditional.get(index).units >= currentInstruction.units){
									currentTask.blocked = false;
									currentTask.safe = true;
									currentTask.currentAlloc.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
									currentTask.maxAdditional.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
									safeTotal.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
									safeAvailable.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
									currentTask.instructionList.remove(currentInstruction);
								}
								else{
									if (firstCycle == true && i == 0){
										return false;
									}
									reqNotGranted++;
									currentTask.safe = false;
									currentTask.blocked = true;
									System.out.println("helloa re youuuu there");
									}
							}
							else {
									/* if first task could not be requested, the state is not safe */ 
									if (firstCycle == true && i == 0){
										return false;
									}
									currentTask.safe = false;
									currentTask.blocked = true;
									if (safeBlockedList.contains(currentTask) == false){
										safeBlockedList.add(currentTask);
									}
									reqNotGranted++;
								}
						}
						
					}
					/* release */
					else if (currentInstruction.action.equals("release")){
						/* resources released won't be available until next cycle */
						currentTask.released = true;
					}
					/* compute */
					else if (currentInstruction.action.equals("compute")){
						currentInstruction.resourceNum--;
						if (currentInstruction.resourceNum == 0){
							currentTask.instructionList.remove(currentInstruction);
						}
					}
					/* terminate */
					else{
						/* keep track of termination time */
						currentTask.terminated = true;
						numTerminated++;
						currentTask.instructionList.remove(currentInstruction);
					}
					
				}
				/* if no task could complete its request in a cycle, the state is not safe */
				if (reqNotGranted == originalTaskListSize){
					return false;
				}
		}
			/* end of first cycle */
			firstCycle = false;
			/* reset the number of requests granted */
			reqNotGranted = 0;
			
			/* remove any tasks that were terminated or aborted */
			for (int i = 0; i < safeTasks.size();i++){
				Task currentTask = safeTasks.get(i);
				if (currentTask.aborted == true || currentTask.terminated == true){
					if (currentTask.aborted == true){
						for (int j = 0; j < currentTask.currentAlloc.size(); j++){
							Resource currentResource = currentTask.currentAlloc.get(j);
							safeTotal.get(currentResource.ID-1).units -= currentResource.units;
							safeAvailable.get(currentResource.ID-1).units += currentResource.units;
						}
					}
					safeTasks.remove(currentTask);
					safeBlockedList.remove(currentTask);
					numTerminated++;
				}
			}

			if (safeBlockedList.isEmpty() == false){
				/* remove any non-blocked tasks, mainly for ordering of the next cycle */
				for (int i = 0; i < safeTasks.size(); i++){
					Task currentTask = safeTasks.get(i);
					if (currentTask.blocked == false && safeBlockedList.contains(currentTask) == true){
						safeBlockedList.remove(currentTask);
					}
				}
				/* for loop to form a new order for the blocked list for the next cycle */
				for (int i = 0; i < safeTasks.size();i++){
					Task currentTask = safeTasks.get(i);
					if (currentTask.blocked == false && safeBlockedList.contains(currentTask) == false){
						safeBlockedList.add(currentTask);
					}
				}
			}
			
			/* check if all tasks are finished */
			if (numTerminated >= originalTaskListSize){
				done = true;
			}
			
			/* give back resources that were released in cycle, so they are available next cycle */
			int size = 0;
			if (newUsedBlockedList == false){
				size = safeTasks.size();
			}
			else{
				size = safeBlockedList.size();
			}
			for (int i = 0; i < size;i++){
				Task currentTask;
				/* decide which list to use */ 
				if (newUsedBlockedList == false){
					currentTask = safeTasks.get(i);
					
				}
				else {
					currentTask = safeBlockedList.get(i);
				}
				if (currentTask.released == true){
					Instruction currentInstruction = currentTask.instructionList.get(0);
					currentTask.currentAlloc.get(currentInstruction.resourceNum-1).units -= currentInstruction.units; 
					currentTask.maxAdditional.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
					safeTotal.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
					safeAvailable.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
					currentTask.instructionList.remove(currentInstruction);
					currentTask.released = false;
				}
			}
		}
		
		/* last evaluation if state is safe */ 
		int numOfSafeTasks = 0;
		for (int i = 0; i < safeTasks.size(); i++){
			Task currentTask = safeTasks.get(i);
			if (currentTask.safe == true){
				numOfSafeTasks++;
			}
		}
		
		if (numOfSafeTasks == 0){
			return false;
		}
		else if (safe == false){
			return false;
		}
		else {
			return true;
		}	
	}
	
	/**
	 * This is the method that prints out the Banker's algorithm's results. It displays the termination time, waiting time, and percentage
	 * of time spent waiting. 
	 * @param taskList : task list whose results need to be displayed
	 */
	public static void bankerRMResult(ArrayList<Task> taskList){
			Collections.sort(taskList);
			DecimalFormat df = new DecimalFormat("#0.00"); 
			int totalFinish = 0;
			int totalWait = 0;
			System.out.println("Banker:" +  "  " + "Run Time" + "  " + "Wait Time" + "   " + "Percentage");
			for (int i = 0; i < taskList.size(); i++){
				Task currentTask = taskList.get(i);
				System.out.print(currentTask.toString());
				if (currentTask.aborted == true){
					System.out.print("	aborted");
					System.out.println();
					if (currentTask.abortedInitiate == true){
						System.out.println("Message: Task " + currentTask.ID + " aborted at cycle " + currentTask.cycleAborted + " because its initiate exceeded amount of resources available.");
					}
					else if (currentTask.abortedRequest == true){
						System.out.println("Message: Task " + currentTask.ID + " aborted at cycle " + currentTask.cycleAborted + " because its requests exceed its initial claim.");
					}
				}
				else {
					totalFinish += currentTask.terminatedTime;
					totalWait += currentTask.waitingTime;
					double percentage = ((double)currentTask.waitingTime/(double)currentTask.terminatedTime)*100;
					System.out.print("   " + currentTask.terminatedTime + "          " + currentTask.waitingTime + "          " + df.format(percentage) + "%" );
					System.out.println();
				}
			}
			double totalPercentage = ( ( (double)totalWait / (double)totalFinish ) )*100; 
			System.out.println("Total:" + "   " + totalFinish + "	    " + totalWait + "          " + df.format(totalPercentage)+ "%");
	}
	
	/**
	 * This is the actual Banker's algorithm. It goes through a task list and grants task's requests based if their state is safe or not.
	 * It uses the safe algorithm to decide if a state is safe. This algorithm makes use of ArrayLists to store tasks and resources. Unlike
	 * the Optimistic Resource Manager, it makes use of a Available List (number of units available for each resource), and the Total List
	 * (total number of units from each resource allocated to tasks). If a task's request exceeds its initial claims, then the task is aborted.
	 * 
	 * @param taskList : list of tasks for the banker to allocate resources to 
	 * @param resourceList : list of resources for the banker to allocate
	 */
	public static void bankerRM(ArrayList<Task> taskList, ArrayList<Resource> resourceList){
		
		/* fill out the each task's max additional list and claims list */ 
		ArrayList<Resource> total = new ArrayList<Resource>();
		ArrayList<Resource> available = new ArrayList<Resource>();
		for (int i = 0; i < taskList.size();i++){
			Task currentTask = taskList.get(i);
			for (int j = 0; j < currentTask.instructionList.size(); j++ ){
				Instruction currentInstruction = currentTask.instructionList.get(j);
				if (currentInstruction.action.equals("initiate")){
					Resource newResource = new Resource(currentInstruction.resourceNum, currentInstruction.units);
					currentTask.claimsList.add(newResource);
				}
			}
			for (int k = 0; k < currentTask.claimsList.size(); k++){
				Resource newResource = new Resource (k+1, 0);
				currentTask.currentAlloc.add(newResource);
				int maxAdditional = currentTask.claimsList.get(k).units;
				Resource maxResource = new Resource(k+1,maxAdditional);
				currentTask.maxAdditional.add(maxResource);
			}
		}
		/* fill out total and available lists */ 
		for (int i = 0; i < resourceList.size(); i++){
			Resource totalResource = new Resource(resourceList.get(i).ID, 0);
			total.add(totalResource);
			Resource availableResource = resourceList.get(i);
			available.add(availableResource);
		}
		
		boolean done = false;
		int cycle = 0;
		int numTerminated = 0;
		int requestNotGranted; 
		int originalTaskListSize = taskList.size();
		ArrayList<Task> blockedTasks = new ArrayList<Task>();
		ArrayList<Task> finishedTasks = new ArrayList<Task>();
		boolean useBlockedList = false;
		while(done == false){
			requestNotGranted = 0; 
			/* determine which task to do, prioritizes blocked tasks */
			if (blockedTasks.isEmpty() == false){
				useBlockedList = true;
			}
			else {
				useBlockedList = false;
			}
	
			/* determine if we want to use blocked list or regular list */
			int nextIndex = 0;
			for (int i = 0; i < taskList.size(); i++){
				Task currentTask;
				if (blockedTasks.isEmpty() == false && useBlockedList == true){
					currentTask = blockedTasks.get(i);
				}
				else{
					currentTask = taskList.get(i);
				}
				/* figure out location of task in blocked list */
				if (blockedTasks.isEmpty() == false && useBlockedList == true){
					boolean found = false;
					int index = 0;
					while (found == false){
						if (index == nextIndex ){
							found = true;
						}
						else{
							index++;
						}
					}
					currentTask = blockedTasks.get(index);
					/* keep track of next task's location */
					if ( index + 1 >= blockedTasks.size()){
						nextIndex = 0;
					}
					else{
						nextIndex = index+1;
					}
				}
				else{
					currentTask = taskList.get(i);
					/* keep track of next task's location */
					if ( (i) + 1 >= blockedTasks.size()){
						nextIndex = 0;
					}
					else{
						nextIndex = i+1;
					}
				}

				/* keep track of which instruction we are working on */
				Instruction currentInstruction = currentTask.instructionList.get(0);
				/* initiate */
				if (currentInstruction.action.equals("initiate")){
					/* check if we need to abort any tasks if their initiate requests exceed the available resources */
					for (int j = 0; j < currentTask.claimsList.size(); j++){
						if (currentTask.claimsList.get(j).units > available.get(j).units){
							currentTask.aborted = true;
							currentTask.abortedInitiate = true;
							currentTask.cycleAborted = cycle;
						}
					}
					if (currentTask.aborted == false){
						currentTask.instructionList.remove(currentInstruction);
					}
				}
				/* request */
				else if (currentInstruction.action.equals("request")){
					/* check if we need to abort any tasks if their requests exceeds their initial claims */
					for (int j = 0; j < currentTask.claimsList.size(); j++){
						if (currentInstruction.resourceNum == currentTask.currentAlloc.get(j).ID){
							if (currentTask.currentAlloc.get(j).units + currentInstruction.units > currentTask.claimsList.get(j).units){
								currentTask.aborted = true;
								currentTask.abortedRequest = true;
								currentTask.cycleAborted = cycle;
							}
						}
					}
					if (currentTask.aborted == false){
						/* check if the task is safe */
						if (safe(currentTask,taskList,resourceList,blockedTasks,requestNotGranted,numTerminated,total,available,useBlockedList) == true){
							Resource currentResource = available.get(currentInstruction.resourceNum-1);
							/* make sure there is enough resources to give to task */
							if (currentResource.units >= currentInstruction.units){
								currentTask.blocked = false;
								currentTask.currentAlloc.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
								currentTask.maxAdditional.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
								total.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
								available.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
								currentTask.instructionList.remove(currentInstruction);
							}
						}
						else{
							/* cannot grant request */
							currentTask.blocked = true;
							currentTask.waitingTime++;
							if (blockedTasks.contains(currentTask) == false){
								blockedTasks.add(currentTask);
							}
							requestNotGranted++;
							}
				}
					
				}
				/* release */
				else if (currentInstruction.action.equals("release")){
					/* resources released won't be available until next cycle */
					currentTask.released = true;
				}
				/* compute */
				else if (currentInstruction.action.equals("compute")){
					currentInstruction.resourceNum--;
					if (currentInstruction.resourceNum == 0){
						currentTask.instructionList.remove(currentInstruction);
					}
				}
				
				/* terminate */
				else{
					/* keep track of termination time */
					currentTask.terminatedTime = cycle;
					currentTask.terminated = true;
					numTerminated++;
					currentTask.instructionList.remove(currentInstruction);
					/* add task to finished list */
					finishedTasks.add(currentTask);
				}
			}
			
			/* remove any tasks if they were aborted, and add them to finished list */ 
			for (int i = 0; i < taskList.size();i++){
				Task currentTask = taskList.get(i);
				if (currentTask.aborted == true){
					for (int j = 0; j < currentTask.currentAlloc.size(); j++){
						Resource currentResource = currentTask.currentAlloc.get(j);
						total.get(currentResource.ID-1).units -= currentResource.units;
						available.get(currentResource.ID-1).units += currentResource.units;
					}
					finishedTasks.add(currentTask);
					taskList.remove(currentTask);
					blockedTasks.remove(currentTask);
					numTerminated++;
				}
			}
				
			
			if (blockedTasks.isEmpty() == false){
				/* for loop to get rid of tasks whose requests were granted in the cycle */
				for (int i = 0; i < taskList.size(); i++){
					Task currentTask = taskList.get(i);
					if (currentTask.blocked == false && blockedTasks.contains(currentTask) == true){
						blockedTasks.remove(currentTask);
					}
				}
				/* for loop to form a new order for the blocked list for the next cycle */
				for (int i = 0; i < taskList.size();i++){
					Task currentTask = taskList.get(i);
					if (currentTask.blocked == false && blockedTasks.contains(currentTask) == false){
						blockedTasks.add(currentTask);
					}
				}
			}
			
			
			/* for loop to remove any tasks that were terminated this cycle */
			for (int i = 0; i < taskList.size(); i++){
				Task currentTask = taskList.get(i);
				if (currentTask.terminated == true){
					taskList.remove(currentTask);
					blockedTasks.remove(currentTask);
				}
			}
			
			
			/* check if all tasks are finished */
			if (numTerminated == originalTaskListSize){
				done = true;
			}
			
			/* give back resources that were released in cycle, so they are available next cycle */
			for (int i = 0; i < taskList.size();i++){
				Task currentTask = taskList.get(i);
				if (currentTask.released == true){
					Instruction currentInstruction = currentTask.instructionList.get(0);
					currentTask.currentAlloc.get(currentInstruction.resourceNum-1).units -= currentInstruction.units; 
					currentTask.maxAdditional.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
					total.get(currentInstruction.resourceNum-1).units -= currentInstruction.units;
					available.get(currentInstruction.resourceNum-1).units += currentInstruction.units;
					currentTask.instructionList.remove(currentInstruction);
					currentTask.released = false;
				}
			}
			cycle++;
		}
		bankerRMResult(finishedTasks);
		
	}
	
	
}
