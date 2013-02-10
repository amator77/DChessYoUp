package com.dcyp;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.cyp.application.Application;
import com.cyp.chess.account.GTalkAccount;
import com.cyp.game.IChallenge;
import com.cyp.game.IGameControllerListener;
import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.RosterListener;
import com.cyp.transport.impl.ChatMessage;
import com.dcyp.game.ui.GameUI;

public class TestConsole implements IGameControllerListener {
	
	
	JList<Contact> contactsList = new JList<Contact>();
	JList<IChallenge> challangesList = new JList<IChallenge>();
	JFrame fr = new JFrame();
	JTabbedPane tp = new JTabbedPane();
	JPopupMenu contactsMenu = new JPopupMenu();
	JPopupMenu challangesMenu = new JPopupMenu();
	JMenuItem mItem = new JMenuItem("Send challenge");
	JMenuItem mItemCancel = new JMenuItem("Cancel chalange");
	JMenuItem mItemReject = new JMenuItem("Reject chalange");
	JMenuItem mItemAccept = new JMenuItem("Accept chalange");
	
	TestConsole(){		
		
		challangesList.setModel(new DefaultListModel<IChallenge>());
		
		contactsList.setModel(new DefaultListModel<Contact>());
		
		contactsList.setCellRenderer(new ListCellRenderer<Contact>() {
			JLabel lab = new JLabel();
						
			public Component getListCellRendererComponent(
					JList<? extends Contact> list, Contact value, int index,
					boolean isSelected, boolean cellHasFocus) {
					
				lab.setOpaque(true);
				lab.setText(value.getId()+" "+ (value.getPresence() !=null  ? value.getPresence().toString() : "unaivable"));
				
				if( isSelected ){
					lab.setBackground(Color.CYAN);
				}
				else{
					lab.setBackground(Color.WHITE);
				}
				
				return lab;
			}			
		});
		
		challangesList.setCellRenderer(new ListCellRenderer<IChallenge>() {
			JLabel lab = new JLabel();
			
			
			public Component getListCellRendererComponent(
					JList<? extends IChallenge> list, IChallenge value, int index,
					boolean isSelected, boolean cellHasFocus) {
					
				lab.setOpaque(true);
				lab.setText(value.getRemoteContact()+","+value.getTime());
				
				if( isSelected ){
					lab.setBackground(Color.CYAN);
				}
				else{
					lab.setBackground(Color.WHITE);
				}
				
				return lab;
			}			
		});
		
		contactsList.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if( e.getButton() == MouseEvent.BUTTON3){
					contactsMenu.show(contactsList, e.getX(), e.getY());
				}
			}
		});
		
		
		contactsMenu.add(mItem);
		challangesMenu.add(mItemAccept);
		challangesMenu.add(mItemCancel);
		challangesMenu.add(mItemReject);
		
		this.challangesList.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if( e.getButton() == MouseEvent.BUTTON3){
					IChallenge selectedChalange = challangesList.getSelectedValue();
					
					if( selectedChalange != null ){
						
						if(selectedChalange.isReceived()){
							mItemAccept.setEnabled(true);
							mItemCancel.setEnabled(false);
							mItemReject.setEnabled(true);
						}
						else{
							mItemAccept.setEnabled(false);
							mItemCancel.setEnabled(true);
							mItemReject.setEnabled(false);
						}
						
						challangesMenu.show(challangesList, e.getX(), e.getY());
					}
				}
			}
		});
		
		JScrollPane sc1 = new JScrollPane(contactsList);
		JScrollPane sc2 = new JScrollPane(challangesList);
		tp.add("Contacts", sc1);
		tp.add("Challenges", sc2);		
		fr.setContentPane(tp);
		fr.pack();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
	}
	
	static GTalkAccount googleAccount;
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		final TestConsole tcUI = new TestConsole();
		
		Application.configure("com.dcyp.context.DesktopContext", null);
		googleAccount = new GTalkAccount("amator77@gmail.com","leo@1977");
//		googleAccount = new GTalkAccount("florea.leonard@gmail.com","mirela76");		
		googleAccount.login(null);
		googleAccount.getGameController().addGameControllerListener(tcUI);
		
//		Thread.currentThread().sleep(1000);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			
			public void run() {
				for(Contact contact : googleAccount.getRoster().getContacts() ){
					System.out.println("aici"+contact.toString());
					((DefaultListModel<Contact>)tcUI.contactsList.getModel()).addElement(contact);
				}				
			}
		});				
		
		tcUI.mItem.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				Contact selectedContact = tcUI.contactsList.getSelectedValue();
				
				if( selectedContact != null ){
					try {
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).addElement( googleAccount.getGameController().sendChallenge(selectedContact, null) );						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});		
		
		tcUI.mItemAccept.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				IChallenge selectedChalange = tcUI.challangesList.getSelectedValue();
				
				if( selectedChalange != null ){
					try {
						googleAccount.getGameController().acceptChallenge(selectedChalange);
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).removeElement(selectedChalange);
						
						GameUI gameUI = new GameUI(googleAccount.getGameController().startGame(selectedChalange));		
						JFrame fr = new JFrame();
						fr.setTitle("Started");
						fr.setContentPane(gameUI);
						fr.pack();
						fr.setVisible(true);		
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}								
			}
		});		
		
		tcUI.mItemCancel.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				IChallenge selectedChalange = tcUI.challangesList.getSelectedValue();
				
				if( selectedChalange != null ){
					try {
						googleAccount.getGameController().abortChallenge(selectedChalange);
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).removeElement(selectedChalange);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}								
			}
		});		
		
		tcUI.mItemReject.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				IChallenge selectedChalange = tcUI.challangesList.getSelectedValue();
				
				if( selectedChalange != null ){
					try {
						googleAccount.getGameController().rejectChallenge(selectedChalange);
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).removeElement(selectedChalange);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}								
			}
		});		
		
		googleAccount.getRoster().addListener(new RosterListener() {
			
			
			public void presenceChanged(Presence presence) {
								
				SwingUtilities.invokeLater(new Runnable() {
					
					
					public void run() {
						DefaultListModel<Contact> model = (DefaultListModel<Contact>)tcUI.contactsList.getModel();
						model.clear();
						
						for(Contact contact : googleAccount.getRoster().getContacts() ){
							((DefaultListModel<Contact>)tcUI.contactsList.getModel()).addElement(contact);
						}				
					}
				});
			}

			
			public void contactUpdated(Contact contact) {
				SwingUtilities.invokeLater(new Runnable() {
					
					
					public void run() {
						DefaultListModel<Contact> model = (DefaultListModel<Contact>)tcUI.contactsList.getModel();
						model.clear();
						
						for(Contact contact : googleAccount.getRoster().getContacts() ){
							((DefaultListModel<Contact>)tcUI.contactsList.getModel()).addElement(contact);
						}				
					}
				});
				
			}

			
			public void contactDisconected(Contact contact) {
				// TODO Auto-generated method stub
				
			}
		});
		

	}

	
	public void challengeReceived(IChallenge challenge) {		
		System.out.println("challange received :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).addElement(challenge);
	}

	
	public void challengeCanceled(IChallenge challenge) {
		System.out.println("challange canceled :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);
	}

	
	public void challengeAccepted(IChallenge challenge) {
		System.out.println("challange accepted :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);
		GameUI gameUI = new GameUI(googleAccount.getGameController().startGame(challenge));		
		JFrame fr = new JFrame();
		fr.setTitle("Accepted");
		fr.setContentPane(gameUI);
		fr.pack();
		fr.setVisible(true);		
	}

	
	public void challengeRejected(IChallenge challenge) {
		System.out.println("challange rejected :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);
	}
}
