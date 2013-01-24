package com.chessyoup.context;

import java.util.Date;

import com.gamelib.application.Logger;

public class DesktopLogger implements Logger {

	@Override
	public void debug(String component, String message) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
		
	}

	@Override
	public void info(String component, String message) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
	}

	@Override
	public void error(String component, String message, Throwable ex) {
		System.out.println( new Date().toString() +" "+ component+" -> "+message);
		ex.printStackTrace();		
	}
}
