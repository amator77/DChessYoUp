package com.dcyp.context;

import java.util.Date;

import com.cyp.application.Logger;

public class DesktopLogger implements Logger {

	
	public void debug(String component, String message) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
		
	}

	
	public void info(String component, String message) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
	}

	
	public void error(String component, String message, Throwable ex) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
		ex.printStackTrace();		
	}
}
