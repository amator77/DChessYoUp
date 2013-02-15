package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.cyp.transport.Contact;
import com.cyp.transport.Room;
import com.cyp.transport.RoomListener;

public class RoomUI extends JComponent implements ActionListener, RoomListener {

	/**
	 * serial version ID
	 */
	private static final long serialVersionUID = 1L;

	private JTextPane consolePane;

	private JTextField sendMessageField;

	private JButton sendMessageButton;

	private JList<Contact> users;

	private Room room;
	
	private HTMLEditorKit kit;
	
	private HTMLDocument doc;
	
	public RoomUI(Room room) {
		this.setRoom(room);
		this.initUI();
		this.installListeners();
	}

	private void installListeners() {		
		this.sendMessageButton.addActionListener(this);
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
		
		if( this.room != null ){
			this.room.addRoomListener(this);
		}
	}

	private void initUI() {
		this.setLayout(new BorderLayout(2, 2));
		this.consolePane = new JTextPane();
		this.sendMessageField = new JTextField();
		this.sendMessageButton = new JButton("Send");
		this.users = new JList<Contact>();
		JScrollPane consoleSc = new JScrollPane(this.consolePane);
		JScrollPane usersSc = new JScrollPane(this.users);
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(usersSc);
		this.add(consoleSc, BorderLayout.CENTER);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(consoleSc, BorderLayout.CENTER);
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(this.sendMessageField, BorderLayout.CENTER);
		southPanel.add(this.sendMessageButton, BorderLayout.EAST);
		centerPanel.add(southPanel, BorderLayout.SOUTH);
		this.add(eastPanel, BorderLayout.EAST);
		this.add(centerPanel, BorderLayout.CENTER);
		consolePane.setContentType("text/html");
		consolePane.setEditable(false);		
		kit = new HTMLEditorKit();
	    doc = new HTMLDocument();
	    consolePane.setEditorKit(kit);
	    consolePane.setDocument(doc);	    
	}

	public void setSendMessageAction(ActionListener listener) {
		this.sendMessageButton.addActionListener(listener);
	}

	public void appendMessage(String from, String message)
			throws BadLocationException {
		consolePane.getDocument().insertString(
				consolePane.getDocument().getLength(), message, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.sendMessageButton) {
			this.room.sendChatMessage(this.sendMessageField.getText());
		}
	}

	@Override
	public void contactList(List<? extends Contact> list) {
		

	}

	@Override
	public void contactJoined(Contact contact) {
		((DefaultListModel<Contact>)this.users.getModel()).addElement(contact);
	}

	@Override
	public void contactLeaved(Contact contact) {
		((DefaultListModel<Contact>)this.users.getModel()).removeElement(contact);
	}

	@Override
	public void chatMessageReceived(String jid, String message) {
		
		try {			
			kit.insertHTML(doc, doc.getLength(), "<font color='blue'>"+jid+"</font><br/>", 0, 0, null);
			kit.insertHTML(doc, doc.getLength(), "<font color='dark'>"+message+"</font><br>", 0, 0, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    				
	}
}
