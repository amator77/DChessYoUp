package com.dcyp.game.ui.roster;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.cyp.transport.Contact;
import com.dcyp.game.ui.ResourceManager;

public class RosterContact extends DefaultMutableTreeNode implements
		Comparable<RosterContact> {

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;

	private Contact contact;

	private Icon avatar;

	public RosterContact(Contact contact) {
		this.contact = contact;
		this.setContact(contact);
	}

	public int compareTo(RosterContact another) {
		return contact.getPresence().getMode()
				.compareTo(another.contact.getPresence().getMode());
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
		this.avatar = ResourceManager.getManager().createAvatarIcon(
				contact.getAvatar());
	}

	public Icon getAvatar() {
		return avatar;
	}
}
