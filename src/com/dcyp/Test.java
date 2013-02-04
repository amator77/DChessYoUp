package com.dcyp;

import java.io.IOException;

import com.cyp.application.Application;
import com.cyp.transport.exceptions.LoginException;
import com.cyp.transport.xmpp.google.GTalkConnection;
import com.cyp.transport.xmpp.google.GTalkConfigurationManager;

public class Test {
	

	public static void main(String[] args) throws Exception {
		
		try {
			Application.configure("com.dcyp.context.DesktopContext", null);
			new GTalkConnection(GTalkConfigurationManager.createOauth2Configuration()).login("a", "ya29.AHES6ZRjIBnUc0bD_zAH1sRqnG2InwzXZTW34Dl7G99piZISW7EUbA");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
