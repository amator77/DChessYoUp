package com.chessyoup;

import java.util.Calendar;
import java.util.TimeZone;

public class Start {
	public static void main(String[] args) throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 
		
		System.out.println(cal.get(Calendar.YEAR));
		System.out.println(cal.get(Calendar.HOUR));
		System.out.println(cal.get(Calendar.HOUR_OF_DAY));
		System.out.println(cal.get(Calendar.MINUTE));
		
		System.out.println(cal.getTimeInMillis());
	}
}
