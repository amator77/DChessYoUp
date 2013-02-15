package com.dcyp.game.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.cyp.chess.account.GTalkAccount;
import com.cyp.transport.Room;
import com.cyp.transport.Util;
import com.dcyp.task.Task;

public class FrameUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MainUI mainUI;
	
	public FrameUI() {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			Application.configure("com.dcyp.context.DesktopContext", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.mainUI = new MainUI();
		this.setContentPane(this.mainUI);
		this.pack();
		
		final GTalkAccount googleAccount = new GTalkAccount(
				"amator77@gmail.com", "leo@1977");

		googleAccount.login(new Account.LoginCallback() {

			public void onLogginSuccess() {
				mainUI.getRosterUI().getRosterAdapter()
						.addAccount(googleAccount);
				googleAccount.getRoster().addListener(mainUI.getRosterUI());
				googleAccount.getGameController().addGameControllerListener(
						mainUI);
				mainUI.getChallengesUI().setAbortChallangeTask(new Task() {

					public void run() {
						try {
							googleAccount.getGameController().abortChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				});
				mainUI.getChallengesUI().setAcceptChallangeTask(new Task() {

					public void run() {
						try {
							googleAccount.getGameController().acceptChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				});
				mainUI.getChallengesUI().setRejectChallangeTask(new Task() {

					public void run() {
						try {
							googleAccount.getGameController().rejectChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
							mainUI.getChallengesUI().removeChallenge(
									mainUI.getChallengesUI()
											.getSelectedChallenge());
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				});
								
				googleAccount.getConnection().getRoomsManager().initialize();
				List<? extends Room> rooms = googleAccount.getConnection()
						.getRoomsManager().listRooms();

				for (Room room : rooms) {
					room.join(Util.getUsernameFromId(googleAccount.getId()));
					mainUI.getRoomUI().setRoom(room);
				}												
			}

			public void onLogginError(String errorMessage) {
			}
		});
	}

	public static void main(String[] args) {
		new FrameUI().setVisible(true);
	}
}
