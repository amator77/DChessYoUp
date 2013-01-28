package com.chessyoup;

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

import com.chess.account.GoogleChessAccount;
import com.chessyoup.online.OnlineGame;
import com.gamelib.application.Application;
import com.gamelib.game.IChallenge;
import com.gamelib.game.IGameControllerListener;
import com.gamelib.transport.Contact;
import com.gamelib.transport.Presence;
import com.gamelib.transport.RosterListener;
import com.gamelib.transport.impl.ChatMessage;

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
			
			@Override
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
			
			@Override
			public Component getListCellRendererComponent(
					JList<? extends IChallenge> list, IChallenge value, int index,
					boolean isSelected, boolean cellHasFocus) {
					
				lab.setOpaque(true);
				lab.setText(value.getRemoteId()+","+value.getTime());
				
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
	
	static GoogleChessAccount googleAccount;
	
	public static void main(String[] args) throws Exception {
		final TestConsole tcUI = new TestConsole();
		
		Application.configure("com.chessyoup.context.DesktopContext", null);
//		googleAccount = new GoogleChessAccount("amator77@gmail.com","leo@1977");
		googleAccount = new GoogleChessAccount("florea.leonard@gmail.com","mirela76");		
		googleAccount.login(null);
		googleAccount.getGameController().addGameControllerListener(tcUI);
		
//		Thread.currentThread().sleep(1000);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				for(Contact contact : googleAccount.getRoster().getContacts() ){
					System.out.println("aici"+contact.toString());
					((DefaultListModel<Contact>)tcUI.contactsList.getModel()).addElement(contact);
				}				
			}
		});
		
		
		
		tcUI.mItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Contact selectedContact = tcUI.contactsList.getSelectedValue();
				
				if( selectedContact != null ){
					try {
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).addElement( googleAccount.getGameController().sendChallenge(selectedContact.getId(), null) );						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});		
		
		tcUI.mItemAccept.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IChallenge selectedChalange = tcUI.challangesList.getSelectedValue();
				
				if( selectedChalange != null ){
					try {
						googleAccount.getGameController().acceptChallenge(selectedChalange);
						((DefaultListModel<IChallenge>)tcUI.challangesList.getModel()).removeElement(selectedChalange);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}								
			}
		});		
		
		tcUI.mItemCancel.addActionListener(new ActionListener() {
			
			@Override
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
			
			@Override
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
			
			@Override
			public void presenceChanged(Presence presence) {
								
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						DefaultListModel<Contact> model = (DefaultListModel<Contact>)tcUI.contactsList.getModel();
						model.clear();
						
						for(Contact contact : googleAccount.getRoster().getContacts() ){
							((DefaultListModel<Contact>)tcUI.contactsList.getModel()).addElement(contact);
						}				
					}
				});
			}
		});
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandLine = null;

		while (true) {
			commandLine = br.readLine();
			
			if (commandLine == null || commandLine.equals("exit")) {
				Application.getContext().listAccounts().get(0).logout();
				System.exit(0);
			} else {
				String parts[] = commandLine.split(",");

				switch (parts[0]) {
				case "chat": {
					String to = parts[1];
					String body = parts[2];
					ChatMessage chat = new ChatMessage(to,body);
					googleAccount.getConnection().sendMessage(chat);
				}
				break;
				case "challenge": {
					String to = parts[1];										
					googleAccount.getGameController().sendChallenge(to, null);
				}
				break;
				
				default:
					break;
				}
			}
		}
	}

	@Override
	public void challengeReceived(IChallenge challenge) {		
		System.out.println("challange received :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).addElement(challenge);
	}

	@Override
	public void challengeCanceled(IChallenge challenge) {
		System.out.println("challange canceled :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);
	}

	@Override
	public void challengeAccepted(IChallenge challenge) {
		System.out.println("challange accepted :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);		
	}

	@Override
	public void challengeRejected(IChallenge challenge) {
		System.out.println("challange rejected :"+challenge.toString());
		((DefaultListModel<IChallenge>)this.challangesList.getModel()).removeElement(challenge);
	}
}
