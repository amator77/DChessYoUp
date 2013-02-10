package com.dcyp.game.ui.roster;

import java.awt.Image;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cyp.transport.Presence.MODE;

public class RosterGroup extends DefaultMutableTreeNode implements Comparable<RosterGroup>{
	
	/**
	 * seriadl ID
	 */
	private static final long serialVersionUID = 1L;

	private String groupName;
	
	private Image image;

	public RosterGroup(String groupName,Image image){
		this.groupName = groupName;
		this.image = image;	
	}							
	
	public int compareTo(RosterGroup another) {			
		
		if( this.countOnline() > another.countOnline() ){
			return 1;
		}
		else{
			return groupName.compareTo(another.groupName);
		}
		
	}
	
	public int countOnline(){
		int count = 0;
		
		for( int i = 0; i < this.getChildCount(); i++ ){
			RosterContact rContact = (RosterContact)this.getChildAt(i);
			
			if( rContact.getContact().getPresence().getMode() == MODE.ONLINE ){
				count++;
			}
		}
		
		return count;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "RosterGroup [groupName=" + groupName + ", image=" + image + "]";
	}
}
