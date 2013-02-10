package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.cyp.game.IChallenge;
import com.cyp.game.IGameControllerListener;
import com.dcyp.game.ui.roster.RosterUI;

public class MainUI extends JComponent implements IGameControllerListener {

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;
	
	private RosterUI rosterUI;
	
	private ChallengesUI challengesUI;
	
	private JSplitPane splitPane;
	
	public MainUI(){
		this.initUI();
	}

	private void initUI() {
		this.rosterUI = new RosterUI();
		this.challengesUI = new ChallengesUI();
		this.splitPane = new JSplitPane();
		this.setLayout(new BorderLayout());
		this.add(this.splitPane,BorderLayout.CENTER);
		this.splitPane.setLeftComponent(this.rosterUI);
		this.splitPane.setRightComponent(this.challengesUI);
		this.setPreferredSize(new Dimension(600,600));
		
	}
	
	public void setBounds(int x , int y , int w , int h){
		super.setBounds(x,y,w,h);
		this.splitPane.setDividerLocation(0.3d);
	}

	public RosterUI getRosterUI() {
		return rosterUI;
	}

	public ChallengesUI getChallengesUI() {
		return challengesUI;
	}

	public void challengeReceived(final IChallenge challenge) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				challengesUI.addChallenge(challenge);	
			}
		});
	}

	public void challengeCanceled(final IChallenge challenge) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				challengesUI.removeChallenge(challenge);	
			}
		});
		
	}

	public void challengeAccepted(final IChallenge challenge) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				challengesUI.removeChallenge(challenge);	
			}
		});
		
	}

	public void challengeRejected(final IChallenge challenge) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				challengesUI.removeChallenge(challenge);	
			}
		});
		
	}
}
