package com.dcyp.game.ui.roster;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.RosterListener;

public class RosterUI extends JComponent implements RosterListener {

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;

	private JTree contactsTree;
	
	private RosterAdapter rosterAdapter;
	
	public RosterUI() {
		this.initUI();
		this.installListeners();
	}

	private void initUI() {
		this.contactsTree = new JTree();
		this.rosterAdapter = new RosterAdapter(contactsTree);
		this.setLayout(new BorderLayout());
		JScrollPane sc = new JScrollPane(contactsTree);
		this.add(sc, BorderLayout.CENTER);
	}

	private void installListeners() {

	}

	public RosterAdapter getRosterAdapter() {
		return rosterAdapter;
	}

	public void presenceChanged(Presence presence) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				rosterAdapter.refresh();									
			}
		});					
	}

	public void contactUpdated(Contact contact) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				rosterAdapter.refresh();									
			}
		});		
		
	}

	public void contactDisconected(Contact contact) {
		// TODO Auto-generated method stub
		
	}
}
