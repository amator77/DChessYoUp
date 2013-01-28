package com.chessyoup.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.chess.ChessController;
import com.chess.GUIInterface;
import com.chess.GameMode;
import com.chess.gamelogic.Move;
import com.chess.gamelogic.Position;
import com.chess.pgn.HTMLPGNText;
import com.chess.pgn.PGNOptions;

public class GameUI extends JPanel implements GUIInterface {

	private static final long serialVersionUID = 1L;

	private ChessBoardPainter cbp;

	private ChessController ctrl;

	private PGNOptions pgnOption = new PGNOptions();

	private HTMLPGNText pgnTextView;

	private JButton abortButton;

	private JButton exitButton;

	private JButton drawButton;

	private JButton resignButton;

	private JButton rematchButton;

	private JButton sendChatButton;

	private JTextField chatMessageField;

	private JScrollPane moveListScroll;

	private JScrollPane chatListScroll;

	private JTextPane moveListArea;

	private JTextPane chatArea;

	private JTabbedPane tp;

	private String whitePlayer;

	private String blackPlayer;

	public GameUI() {
		this.cbp = new ChessBoardPainter();
		this.pgnOption = new PGNOptions();
		this.pgnTextView = new HTMLPGNText(pgnOption);
		this.ctrl = new ChessController(this, pgnTextView, pgnOption);
		this.initUI();
		this.installListeners();
		ctrl.newGame(new GameMode(GameMode.TWO_PLAYERS));		
	}

	public ChessController getController() {
		return this.ctrl;
	}

	@Override
	public void setPosition(Position pos, String variantInfo,
			ArrayList<Move> variantMoves) {
		cbp.setPosition(pos);
	}

	@Override
	public void setSelection(int sq) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(GameStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveListUpdated() {
		this.moveListArea.setText(this.pgnTextView.getHTMLData());

	}

	@Override
	public void requestPromotePiece() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Object[] options = { "Queen", "Rook", "Bishop", "Knight" };
				int choice = JOptionPane
						.showOptionDialog(cbp, "Promote pawn to?",
								"Pawn Promotion", 0,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				ctrl.reportPromotePiece(choice);
			}
		});
	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public void reportInvalidMove(Move m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemainingTime(long wTime, long bTime, long nextUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAnimMove(Position sourcePos, Move move, boolean forward) {
		// TODO Auto-generated method stub

	}

	public void setWhitePlayer(String whitePlayer) {
		this.whitePlayer = whitePlayer;
	}

	public void setBlackPlayer(String blackPlayer) {
		this.blackPlayer = blackPlayer;
	}

	@Override
	public String whitePlayerName() {
		return this.whitePlayer;
	}

	@Override
	public String blackPlayerName() {
		return this.blackPlayer;
	}

	@Override
	public boolean discardVariations() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remoteMoveMade() {
		// TODO Auto-generated method stub

	}

	private void initUI() {
		this.resignButton = new JButton("Resign");
		this.exitButton = new JButton("Exit");
		this.drawButton = new JButton("Draw");
		this.abortButton = new JButton("Abort");
		this.rematchButton = new JButton("Rematch");
		this.moveListArea = new JTextPane();
		this.moveListScroll = new JScrollPane(this.moveListArea);
		this.chatArea = new JTextPane();
		this.chatListScroll = new JScrollPane(this.chatArea);
		this.chatMessageField = new JTextField();
		this.sendChatButton = new JButton("Send");
		this.setLayout(new BorderLayout());
		JPanel eastPanel = new JPanel(new BorderLayout());
		this.add(this.cbp, BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST);
		JPanel eastNorthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eastNorthPanel.add(this.abortButton);
		eastNorthPanel.add(this.resignButton);
		eastNorthPanel.add(this.drawButton);
		eastNorthPanel.add(this.rematchButton);
		eastNorthPanel.add(this.exitButton);
		eastPanel.add(eastNorthPanel, BorderLayout.NORTH);
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.add(this.chatListScroll, BorderLayout.CENTER);
		JPanel chatSountPanel = new JPanel(new BorderLayout());
		chatSountPanel.add(this.chatMessageField, BorderLayout.CENTER);
		chatSountPanel.add(this.sendChatButton, BorderLayout.EAST);
		chatPanel.add(chatSountPanel, BorderLayout.SOUTH);
		this.tp = new JTabbedPane();
		this.tp.add("Moves", this.moveListScroll);
		this.tp.add("Chat", chatPanel);

		eastPanel.add(this.tp, BorderLayout.CENTER);
	}

	private void installListeners() {
		cbp.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (ctrl.humansTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mousePressed(sq);
					if (m != null) {
						ctrl.makeHumanMove(m);
					}
				}
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if (ctrl.humansTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mouseReleased(sq);
					if (m != null) {
						ctrl.makeHumanMove(m);
					}
				}
			}
		});
		cbp.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				if (ctrl.humansTurn()) {
					cbp.mouseDragged(evt);
				}
			}
		});
	}

	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		JFrame fr = new JFrame();
		fr.setContentPane(new GameUI());
		fr.pack();
		fr.setVisible(true);
		fr.setDefaultCloseOperation(3);
	}
}
