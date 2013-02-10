package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

import com.cyp.chess.chessboard.ChessboardController;
import com.cyp.chess.chessboard.ChessboardMode;
import com.cyp.chess.chessboard.ChessboardStatus;
import com.cyp.chess.chessboard.ChessboardUIInterface;
import com.cyp.chess.game.ChessGame;
import com.cyp.chess.game.ChessGameListener;
import com.cyp.chess.model.Game.GameState;
import com.cyp.chess.model.Move;
import com.cyp.chess.model.Position;
import com.cyp.chess.model.pgn.HTMLPGNText;
import com.cyp.chess.model.pgn.PGNOptions;
import com.cyp.game.IGameCommand;
import com.dcyp.chessboard.ChessBoardPainter;

public class GameUI extends JPanel implements ChessboardUIInterface,
		ChessGameListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private ChessGame game;

	private ChessBoardPainter cbp;

	private ChessboardController ctrl;

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

	private boolean abortRequested;

	private boolean drawRequested;

	public GameUI(ChessGame game) {
		this.game = game;
		this.cbp = new ChessBoardPainter();
		this.pgnOption = new PGNOptions();
		this.pgnTextView = new HTMLPGNText(pgnOption);
		this.ctrl = new ChessboardController(this, pgnTextView, pgnOption);
		this.initUI();
		this.installListeners();

		try {
			game.sendReady();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public ChessboardController getController() {
		return this.ctrl;
	}

	public void setPosition(Position pos, String variantInfo,
			ArrayList<Move> variantMoves) {
		cbp.setPosition(pos);
	}

	public void actionPerformed(ActionEvent e) {
		if (ctrl.getGame().getGameState() == GameState.ALIVE) {
			if (e.getSource() == this.resignButton) {
				ctrl.resignGame();

				try {
					game.resign();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (e.getSource() == this.abortButton) {
				if (abortRequested) {
					ctrl.abortGame();
					moveListArea.setText(moveListArea.getText() + " aborted");
					abortRequested = false;
					try {
						game.acceptAbortRequest();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						game.sendAbortRequest();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else if (e.getSource() == this.drawButton) {
				if (drawRequested) {
					ctrl.drawGame();
					drawRequested = false;
					try {
						game.acceptDrawRequest();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						ctrl.offerDraw();
						game.sendDrawRequest();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}			
			} else if (e.getSource() == this.exitButton) {
				
				if (JOptionPane.showConfirmDialog(null, "Exit and Resign?") == JOptionPane.OK_OPTION) {
					try {
						game.resign();
						game.sendGameClosed();
						ctrl.resignGame();
						game.getAccount().getGameController()
								.closeGame(this.game);
						SwingUtilities.getWindowAncestor(this.cbp).dispose();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
			if (e.getSource() == this.exitButton) {
				try {
					game.sendGameClosed();
					ctrl.abortGame();
					game.getAccount().getGameController().closeGame(this.game);
					SwingUtilities.getWindowAncestor(this.cbp).dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if (e.getSource() == this.rematchButton) {
				try {
					game.sendRematchRequest();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	
	public void moveListUpdated() {
		this.moveListArea.setText(this.pgnTextView.getHTMLData());

	}

	
	public void requestPromotePiece() {
		SwingUtilities.invokeLater(new Runnable() {

			
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

	
	public void runOnUIThread(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	
	public String whitePlayerName() {
		return this.game.getWhitePlayer();
	}

	
	public String blackPlayerName() {
		return this.game.getBlackPlayer();
	}

	
	public boolean discardVariations() {
		return false;
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
		this.game.addGameListener(this);
		this.resignButton.addActionListener(this);
		this.drawButton.addActionListener(this);
		this.abortButton.addActionListener(this);
		this.exitButton.addActionListener(this);

		cbp.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mousePressed(sq);
					if (m != null) {
						ctrl.makeLocalMove(m);
					}
				}
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mouseReleased(sq);
					if (m != null) {
						ctrl.makeLocalMove(m);
					}
				}
			}
		});

		cbp.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					cbp.mouseDragged(evt);
				}
			}
		});
	}

	
	public void setStatus(ChessboardStatus status) {

	}

	
	public void localMoveMade(Move m) {
		try {
			game.sendMove(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void readyReceived() {
		ctrl.newGame(new ChessboardMode(
				game.getChallenge().isReceived() ? ChessboardMode.TWO_PLAYERS_WHITE_REMOTE
						: ChessboardMode.TWO_PLAYERS_BLACK_REMOTE));
		cbp.setFlipped(game.getChallenge().isReceived());
		ctrl.startGame();
	}

	
	public void resignReceived() {
		ctrl.makeRemoteMove("resign");
	}

	
	public void chatReceived(String text) {
		this.chatArea.setText(this.chatArea.getText() + "\n" + text);
	}

	
	public void moveReceived(final String move) {

		runOnUIThread(new Runnable() {

			
			public void run() {
				ctrl.makeRemoteMove(move);
			}
		});
	}

	
	public void drawRequestReceived() {
		this.drawRequested = true;
		ctrl.offerDraw();
	}

	
	public void drawAcceptedReceived() {
		ctrl.drawGame();
	}

	
	public void abortRequestReceived() {
		this.abortRequested = true;
		moveListArea.setText(moveListArea.getText() + " abort requested!");
	}

	
	public void abortAcceptedReceived() {
		ctrl.abortGame();
		moveListArea.setText(moveListArea.getText() + " abort accepted");
	}

	
	public void rematchRequestReceived() {

		if (JOptionPane.showConfirmDialog(null, "Rematch?") == JOptionPane.OK_OPTION) {
			try {
				this.readyReceived();
				game.sendReady();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void gameClosedReceived() {
		moveListArea.setText(moveListArea.getText() + " opponent leaved!");
	}

	
	public void reportInvalidMove(Move m) {

	}

	
	public void setRemainingTime(long wTime, long bTime, long nextUpdate) {

	}

	
	public void setAnimMove(Position sourcePos, Move move, boolean forward) {

	}

	
	public void commandReceived(IGameCommand command) {
	}

	
	public void setSelection(int sq) {

	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		JFrame fr = new JFrame();
		fr.setContentPane(new GameUI(null));
		fr.pack();
		fr.setVisible(true);
		fr.setDefaultCloseOperation(3);
	}
}
