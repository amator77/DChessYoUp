package com.dcyp.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.cyp.accounts.Account;
import com.cyp.application.Context;
import com.cyp.application.Logger;

public class DesktopContext implements Context {
		
	private DesktopLogger logger;
	
	private List<Account> accounts;
	
	public DesktopContext(){
		this.logger = new DesktopLogger();		
	}
	
	
	public void initialize(Object contextData) {		
		this.accounts = new ArrayList<Account>();		
	}

	
	public InputStream getResourceAsInputStream(String resource) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
	}

	
	public Logger getLogger() {
		return this.logger;
	}

	
	public List<Account> listAccounts() {
		return this.accounts;
	}

	
	public String getApplicationName() {		
		return "cyp";
	}

	
	public String getVersion() {
		return "1";
	}

	
	public PLATFORM getPlatform() {
		return PLATFORM.DESKTOP_JAVA;
	}

	
	public List<String> getApplicationFutures() {
		List<String> futures = new ArrayList<String>();
		futures.add("http://jabber.org/protocol/games/chess/v1");		
		return futures;
	}

	
	public void registerAccount(Account account) { 
	}

	
	public void removeAccount(Account account) {
	}
	
	public String getGameBaseURL() {
		return "http://api.chessyoup.com";
	}
}
