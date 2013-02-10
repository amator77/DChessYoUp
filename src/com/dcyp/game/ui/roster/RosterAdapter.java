package com.dcyp.game.ui.roster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import com.cyp.accounts.Account;
import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.Util;
import com.dcyp.game.ui.ResourceManager;

public class RosterAdapter extends DefaultTreeModel implements TreeCellRenderer {
	
	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;

	private List<Account> accounts;
	
	private RosterGroup chessyoupRoot;
	
	private RosterGroupItemUI groupItemUI;
	
	private RosterContactItemUI contactItemUI;
	
	private JTree tree;
	
	public RosterAdapter(JTree tree) {		
		super(new DefaultMutableTreeNode("Accounts"));
		this.tree = tree;
		this.tree.setModel(this);
		this.tree.setCellRenderer(this);
		this.accounts = new ArrayList<Account>();		
		this.root = new DefaultMutableTreeNode("Accounts");
		this.groupItemUI = new RosterGroupItemUI();
		this.contactItemUI = new RosterContactItemUI();
		this.chessyoupRoot = new RosterGroup("ChessYoUp",ResourceManager.getManager().loadImage("chessyoup.png"));
		this.tree.setRootVisible(false);
	}
	
	public DefaultMutableTreeNode getRoot(){
		return (DefaultMutableTreeNode)this.root;
	}
	
	public void refresh(){
		getRoot().removeAllChildren();		
		chessyoupRoot.removeAllChildren();
		List<RosterContact> chessyoupContats = new ArrayList<RosterContact>();
		List<RosterGroup> rosterGroups = new ArrayList<RosterGroup>();
		
		for(Account account : this.accounts ){
			RosterGroup group = new RosterGroup( Util.getContactFromId(account.getConnection().getAccountId()), ResourceManager.getManager().loadImage("gtalk.png"));
			List<RosterContact> groupContats = new ArrayList<RosterContact>();
			
			for(Contact contact : account.getRoster().getContacts() ){
				
				if( !contact.isCompatible() ){					
					groupContats.add(new RosterContact(contact));					
				}
				else{					
					chessyoupContats.add(new RosterContact(contact));
				}
			}
			
			Collections.sort(groupContats);
			
			for(RosterContact rContact : groupContats ){
				group.add(rContact);
			}
			
			rosterGroups.add(group);			
		}
		
		Collections.sort(chessyoupContats);	
		
		for(RosterContact rContact : chessyoupContats ){
			chessyoupRoot.add(rContact);
		}
		
		
		getRoot().add(chessyoupRoot);
		
		Collections.sort(rosterGroups);		
		System.out.println("groups :"+rosterGroups);
		for(RosterGroup rGroup : rosterGroups){
			getRoot().add(rGroup);
		}
		
		this.reload();
		
		
		for( int i = 0 ; i < this.tree.getRowCount();i++){
			this.tree.expandRow(i);
		}
	}
	
	public void addAccount(Account account) {
		this.accounts.add(account);
		this.refresh();
	}

	
	private Image getStatusIcon(Presence presence) {
		switch (presence.getMode()) {
		case ONLINE:
			return ResourceManager.getManager().loadImage("general_status_online.png");

		case AWAY:
			return ResourceManager.getManager().loadImage("general_status_away.png");

		case BUSY:
			return ResourceManager.getManager().loadImage("general_status_busy.png");

		case OFFLINE:
			return ResourceManager.getManager().loadImage("general_status_offline.png");

		default:
			return ResourceManager.getManager().loadImage("general_status_offline.png");
		}
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		if( value instanceof RosterContact ){
			RosterContact contact = (RosterContact)value;
			this.contactItemUI.getStatusIconLabel().setIcon(new ImageIcon(getStatusIcon(contact.getContact().getPresence())));
			this.contactItemUI.getContactNameLabel().setText(contact.getContact().getName() != null ? contact.getContact().getName() : Util.getContactFromId(contact.getContact().getId()));
			this.contactItemUI.getContactStatusLabel().setText(contact.getContact().getPresence() != null ? contact.getContact().getPresence().getStatus() : "available");			
			this.contactItemUI.getContactAvatarLabel().setIcon(contact.getAvatar());	
			this.contactItemUI.setPreferredSize(new Dimension(200,this.contactItemUI.getPreferredSize().height));
			return this.contactItemUI;
		}
		else if( value instanceof RosterGroup ){
			RosterGroup group = (RosterGroup)value;
			this.groupItemUI.getGroupNameLabel().setText(group.getGroupName());
			this.groupItemUI.getGroupIconLabel().setIcon(new ImageIcon(group.getImage()));
			
			return this.groupItemUI;
		}
		else{
			return new JLabel(value.toString());
		}
	}
	
	private class RosterGroupItemUI extends JComponent{
		
		/**
		 *serial ID 
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel groupIconLabel;
		
		private JLabel groupNameLabel;
		
		RosterGroupItemUI(){
			this.groupIconLabel = new JLabel();
			this.groupNameLabel = new JLabel();
			this.setLayout(new BorderLayout());
			this.add(this.groupIconLabel,BorderLayout.WEST);
			this.add(this.groupNameLabel,BorderLayout.CENTER);
			this.groupIconLabel.setForeground(Color.RED);
		}

		public JLabel getGroupIconLabel() {
			return groupIconLabel;
		}

		public JLabel getGroupNameLabel() {
			return groupNameLabel;
		}
		
	}
	
	class RosterContactItemUI extends JComponent{
		/**
		 *serial ID 
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel statusIconLabel;
		private JLabel contactNameLabel;
		private JLabel contactStatusLabel;
		private JLabel contactAvatarLabel;
		
		RosterContactItemUI(){
			this.statusIconLabel = new JLabel();
			this.contactNameLabel = new JLabel();
			this.contactStatusLabel = new JLabel();
			this.contactAvatarLabel = new JLabel();
			this.setLayout(new BorderLayout(2,2));
			this.add(this.statusIconLabel,BorderLayout.WEST);
			JPanel pan = new JPanel(new BorderLayout(2,2));
			pan.setOpaque(false);
			pan.add(this.contactNameLabel,BorderLayout.NORTH);
			pan.add(this.contactStatusLabel,BorderLayout.CENTER);
			this.add(pan,BorderLayout.CENTER);
			this.add(this.contactAvatarLabel,BorderLayout.EAST);
			this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		public JLabel getStatusIconLabel() {
			return statusIconLabel;
		}

		public void setStatusIconLabel(JLabel statusIconLabel) {
			this.statusIconLabel = statusIconLabel;
		}

		public JLabel getContactNameLabel() {
			return contactNameLabel;
		}


		public JLabel getContactStatusLabel() {
			return contactStatusLabel;
		}

		public JLabel getContactAvatarLabel() {
			return contactAvatarLabel;
		}
	}
}
