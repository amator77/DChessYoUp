package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cyp.game.IChallenge;
import com.cyp.transport.Util;
import com.dcyp.task.Task;
import com.dcyp.task.TaskManager;

public class ChallengesUI extends JComponent implements ActionListener {

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 1L;

	private JList<IChallenge> challanges;

	private ChallangesAdapter adapter;

	private JPopupMenu challangesMenu;

	private JMenuItem mItemCancel;

	private JMenuItem mItemReject;

	private JMenuItem mItemAccept;

	private Task acceptChallangeTask;

	private Task rejectChallangeTask;

	private Task abortChallangeTask;

	public ChallengesUI() {
		this.adapter = new ChallangesAdapter();
		this.initUI();
		this.installListeners();
	}
	

	public void actionPerformed(ActionEvent e) {
		
		if( this.getSelectedChallenge().isReceived() ){
			this.mItemAccept.setVisible(true);
			this.mItemReject.setEnabled(true);
			this.mItemCancel.setVisible(false);
		}
		else{
			this.mItemAccept.setVisible(false);
			this.mItemReject.setEnabled(false);
			this.mItemCancel.setVisible(true);
		}
		
		if (e.getSource() == this.mItemCancel) {
			if (this.abortChallangeTask != null) {
				TaskManager.getManager().runTask(abortChallangeTask);
			}
		} else if (e.getSource() == this.mItemReject) {
			if (this.rejectChallangeTask != null) {
				TaskManager.getManager().runTask(rejectChallangeTask);				
			}
		} else if (e.getSource() == this.mItemAccept) {
			if (this.acceptChallangeTask != null) {
				TaskManager.getManager().runTask(acceptChallangeTask);
			}
		}
	}
	

	public Task getAcceptChallangeTask() {
		return acceptChallangeTask;
	}


	public void setAcceptChallangeTask(Task acceptChallangeTask) {
		this.acceptChallangeTask = acceptChallangeTask;
	}


	public Task getRejectChallangeTask() {
		return rejectChallangeTask;
	}


	public void setRejectChallangeTask(Task rejectChallangeTask) {
		this.rejectChallangeTask = rejectChallangeTask;
	}


	public Task getAbortChallangeTask() {
		return abortChallangeTask;
	}


	public void setAbortChallangeTask(Task abortChallangeTask) {
		this.abortChallangeTask = abortChallangeTask;
	}


	private void initUI() {
		this.challanges = new JList<IChallenge>();
		this.challanges.setModel(this.adapter);
		this.challanges.setCellRenderer(this.adapter);
		JScrollPane sc = new JScrollPane(this.challanges);
		this.setLayout(new BorderLayout());
		this.add(sc, BorderLayout.CENTER);
		this.challangesMenu = new JPopupMenu();
		this.mItemCancel = new JMenuItem("Cancel chalange");
		this.mItemReject = new JMenuItem("Reject chalange");
		this.mItemAccept = new JMenuItem("Accept chalange");
		this.challangesMenu.add(this.mItemAccept);
		this.challangesMenu.add(this.mItemReject);
		this.challangesMenu.add(this.mItemCancel);
	}

	private void installListeners() {
		this.mItemAccept.addActionListener(this);
		this.mItemReject.addActionListener(this);
		this.mItemAccept.addActionListener(this);

		this.challanges.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				IChallenge selected = challanges.getSelectedValue();

				if (selected != null) {
					Rectangle r = challanges.getCellBounds(
							challanges.getSelectedIndex(),
							challanges.getSelectedIndex());
					challangesMenu.show(challanges, r.x, r.y);
				}
			}
		});

		this.challanges.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					IChallenge selected = challanges.getSelectedValue();

					if (selected != null) {

						challangesMenu.show(challanges, e.getX(), e.getY());
					}
				}
			}
		});
	}

	public void addChallenge(IChallenge challenge) {
		this.adapter.addElement(challenge);

	}

	public void removeChallenge(IChallenge challenge) {
		this.adapter.removeElement(challenge);
	}

	private class ChallangesAdapter extends DefaultListModel<IChallenge>
			implements ListCellRenderer<IChallenge> {

		/**
		 * serial ID
		 */
		private static final long serialVersionUID = 1L;

		private JPanel ui;

		private JLabel challangeLabel;

		private JLabel avatarLabel;

		public ChallangesAdapter() {
			this.ui = new JPanel(new BorderLayout(2, 2));
			this.ui.setOpaque(false);
			this.challangeLabel = new JLabel();
			this.avatarLabel = new JLabel();
			this.ui.add(this.challangeLabel, BorderLayout.CENTER);
			this.ui.add(this.avatarLabel, BorderLayout.EAST);
		}

		public Component getListCellRendererComponent(
				JList<? extends IChallenge> list, IChallenge value, int index,
				boolean isSelected, boolean cellHasFocus) {

			this.challangeLabel.setIcon(value.isReceived() ? ResourceManager
					.getManager().loadImageAsIcon("receive.png")
					: ResourceManager.getManager().loadImageAsIcon("send"));
			this.challangeLabel.setText(Util.getContactFromId(value
					.getRemoteContact().getId()));
			this.avatarLabel.setIcon(ResourceManager.getManager()
					.createAvatarIcon(value.getRemoteContact().getAvatar()));
			return this.ui;
		}
	}

	public IChallenge getSelectedChallenge() {
		return this.challanges.getSelectedValue();
	}
}
