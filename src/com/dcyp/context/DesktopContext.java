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
	
	@Override
	public void initialize(Object contextData) {		
		this.accounts = new ArrayList<Account>();		
	}

	@Override
	public InputStream getResourceAsInputStream(String resource) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	@Override
	public List<Account> listAccounts() {
		return this.accounts;
	}

	@Override
	public String getApplicationName() {		
		return "cyp";
	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public PLATFORM getPlatform() {
		return PLATFORM.DESKTOP_JAVA;
	}

	@Override
	public List<String> getApplicationFutures() {
		List<String> futures = new ArrayList<String>();
		futures.add("http://jabber.org/protocol/games/chess/v1");		
		return futures;
	}
}
