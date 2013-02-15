package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.UIManager;

import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.cyp.chess.account.GTalkAccount;
import com.cyp.transport.Room;
import com.cyp.transport.Util;
import com.dcyp.task.Task;

public class AppletUI extends JApplet {

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;

	private MainUI mainUI;
	
	public AppletUI(){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			Application.configure("com.dcyp.context.DesktopContext", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.initUI();
	}
	
	private void initUI() {
		this.mainUI = new MainUI();
		this.setLayout(new BorderLayout());
		this.add(this.mainUI,BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(800,600));
	}

	public void init() {
		final GTalkAccount googleAccount = new GTalkAccount("florea.leonard@gmail.com","mirela76");
		
		googleAccount.login(new Account.LoginCallback() {
			
			public void onLogginSuccess() {
				mainUI.getRosterUI().getRosterAdapter().addAccount(googleAccount);					
				googleAccount.getRoster().addListener(mainUI.getRosterUI());
				googleAccount.getGameController().addGameControllerListener(mainUI);
				mainUI.getChallengesUI().setAbortChallangeTask(new Task() {
					
					public void run() {
						try {
							googleAccount.getGameController().abortChallenge(mainUI.getChallengesUI().getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(mainUI.getChallengesUI().getSelectedChallenge());
						} catch (IOException e) {
						
							e.printStackTrace();
						}							
					}
				});
				mainUI.getChallengesUI().setAcceptChallangeTask(new Task() {
					
					public void run() {
						try {
							googleAccount.getGameController().acceptChallenge(mainUI.getChallengesUI().getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(mainUI.getChallengesUI().getSelectedChallenge());
						} catch (IOException e) {
						
							e.printStackTrace();
						}							
					}
				});
				mainUI.getChallengesUI().setRejectChallangeTask(new Task() {
					
					public void run() {
						try {
							googleAccount.getGameController().rejectChallenge(mainUI.getChallengesUI().getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(mainUI.getChallengesUI().getSelectedChallenge());
						} catch (IOException e) {
						
							e.printStackTrace();
						}							
					}
				});
				
				googleAccount.getConnection().getRoomsManager().initialize();
				List<? extends Room> rooms = googleAccount.getConnection().getRoomsManager().listRooms();
				
				for( Room room : rooms ){
					room.join(Util.getUsernameFromId(googleAccount.getId()));
					mainUI.getRoomUI().setRoom(room);
				}
			}
			
			public void onLogginError(String errorMessage) {								
			}
		});
	}

	public void start() {
	}

	public void stop() {
	}

	public void destroy() {
	}
}
