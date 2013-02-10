package com.dcyp.task;

public class TaskManager {
	
	private static final TaskManager manager = new TaskManager(); 
	
	public static TaskManager getManager(){
		return TaskManager.manager;
	}
	
	public void runTask(Task task){
		Thread th = new Thread(task,"Task Managr");
		th.start();
	}
}
